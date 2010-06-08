/**
 *  NEXUSe2e Business Messaging Open Source
 *  Copyright 2000-2009, Tamgroup and X-ioma GmbH
 *
 *  This is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation version 2.1 of
 *  the License.
 *
 *  This software is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this software; if not, write to the Free
 *  Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *  02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.nexuse2e.messaging.generic;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.digester.Digester;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.nexuse2e.Constants;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.Constants.BeanStatus;
import org.nexuse2e.backend.pipelets.helper.PartnerSpecificConfiguration;
import org.nexuse2e.backend.pipelets.helper.PartnerSpecificConfigurations;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.configuration.IdGenerator;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.Constants.ParameterType;
import org.nexuse2e.logging.LogMessage;
import org.nexuse2e.messaging.AbstractPipelet;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.service.DataConversionService;
import org.nexuse2e.service.Service;
import org.nexuse2e.tools.mapping.xmldata.MappingDefinition;
import org.nexuse2e.tools.mapping.xmldata.MappingDefinitions;
import org.xml.sax.SAXException;

/**
 * Pipelet that uses data found in the custom parameters of the message to route message
 * to a specific choreography/action/partner etc.
 *
 * @author mbreilmann
 */
public class ParameterBasedRoutingPipelet extends AbstractPipelet {

    private static Logger                       LOG                           = Logger
                                                                                      .getLogger( ParameterBasedRoutingPipelet.class );

    public static final String                  ROUTING_DEFINITION            = "routingDefinition";
    public static final String                  PARTNER_SPECIFIC              = "partnerSpecific";
    public static final String                  MAPPING_SERVICE               = "mappingService";
    public static final String                  PARTNER_ID                    = "partnerId";
    public static final String                  CHOREOGRAPHY_ID               = "choreographyId";
    public static final String                  ACTION_ID                     = "actionId";

    private String                              routingDefinition             = null;
    private DataConversionService               mappingService                = null;
    private MappingDefinitions                  mappingDefinitions            = null;
    private HashMap<String, MappingDefinitions> partnerMappingDefinitions     = null;
    private PartnerSpecificConfigurations       partnerSpecificConfigurations = null;
    private boolean                             partnerSpecific               = false;

    public ParameterBasedRoutingPipelet() {

        parameterMap.put( MAPPING_SERVICE, new ParameterDescriptor( ParameterType.SERVICE, "Data Mapping Service",
                "The Data Mapping and Conversion Service", DataConversionService.class ) );
        parameterMap.put( PARTNER_SPECIFIC, new ParameterDescriptor( ParameterType.BOOLEAN,
                "Partner Specific Configuration", "Partner Specific Configuration", Boolean.FALSE ) );
        parameterMap.put( ROUTING_DEFINITION, new ParameterDescriptor( ParameterType.STRING,
                "Flat File Definition File", "Path to flat file definition file.", "" ) );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.AbstractPipelet#initialize(org.nexuse2e.configuration.EngineConfiguration)
     */
    @Override
    public void initialize( EngineConfiguration config ) throws InstantiationException {

        File testFile = null;

        String mappingServiceName = getParameter( MAPPING_SERVICE );
        if ( !StringUtils.isEmpty( mappingServiceName ) ) {

            Service service = Engine.getInstance().getActiveConfigurationAccessService()
                    .getService( mappingServiceName );
            if ( service != null && service instanceof DataConversionService ) {
                mappingService = (DataConversionService) service;
            }
        }

        if ( mappingService == null ) {
            throw new InstantiationException( "Mapping Service does not exist!" );
        }

        Boolean partnerSpecificValue = getParameter( PARTNER_SPECIFIC );
        if ( partnerSpecificValue != null ) {
            partnerSpecific = partnerSpecificValue.booleanValue();
        }

        String flatFileDefinitionValue = getParameter( ROUTING_DEFINITION );
        if ( ( flatFileDefinitionValue != null ) && ( flatFileDefinitionValue.length() != 0 ) ) {
            routingDefinition = flatFileDefinitionValue;
            testFile = new File( routingDefinition );
            if ( !testFile.exists() ) {
                status = BeanStatus.ERROR;
                LOG.error( "Flat file block file does not exist!" );
                throw new InstantiationException( "Flat file block file does not exist!" );
            }
        } else {
            status = BeanStatus.ERROR;
            LOG.error( "No value for setting 'routing definition file' provided!" );
            throw new InstantiationException( "No value for setting 'routing definition file' provided!" );
        }

        LOG.trace( "routingDefinition: " + routingDefinition );
        if ( partnerSpecific ) {
            partnerSpecificConfigurations = readPartnerSpecificConfigurations( routingDefinition );
            partnerMappingDefinitions = new HashMap<String, MappingDefinitions>();
            MappingDefinitions theMappingDefinitions = null;

            for ( PartnerSpecificConfiguration partnerSpecificConfiguration : partnerSpecificConfigurations
                    .getPartnerSpecificConfigurations() ) {
                theMappingDefinitions = readConfiguration( partnerSpecificConfiguration.getConfigurationFile() );
                partnerMappingDefinitions.put( partnerSpecificConfiguration.getPartnerId(), theMappingDefinitions );
            }
        } else {
            mappingDefinitions = readConfiguration( routingDefinition );
        }

        super.initialize( config );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.AbstractPipelet#processMessage(org.nexuse2e.messaging.MessageContext)
     */
    @Override
    public MessageContext processMessage( MessageContext messageContext ) throws IllegalArgumentException,
            IllegalStateException, NexusException {

        MappingDefinitions theMappingDefinitions = null;

        String actionId = null;
        String choreographyId = null;
        String partnerId = null;

        if ( partnerSpecific ) {
            partnerId = messageContext.getMessagePojo().getCustomParameters().get( PARTNER_ID );
            if ( partnerId == null ) {
                throw new NexusException( "No partner ID found in custom parameters!"
                        + messageContext.getMessagePojo().getCustomParameters() );
            }

            theMappingDefinitions = partnerMappingDefinitions.get( partnerId );
        } else {
            theMappingDefinitions = mappingDefinitions;
        }

        if ( theMappingDefinitions == null ) {
            throw new NexusException( "No routing configuration found!" );
        }

        for ( MappingDefinition mappingDefinition : theMappingDefinitions.getMappingDefinitions() ) {
            String parameter = messageContext.getMessagePojo().getCustomParameters().get( mappingDefinition.getXpath() );
            String routingInfo = mappingDefinition.getCategory();
            if ( parameter != null ) {
                String result = mappingService.processConversion( null, null, parameter, mappingDefinition );
                if ( CHOREOGRAPHY_ID.equalsIgnoreCase( routingInfo ) ) {
                    choreographyId = result;
                } else if ( ACTION_ID.equalsIgnoreCase( routingInfo ) ) {
                    actionId = result;
                }
            } else {
                LOG.error( new LogMessage( "No customer parameter found with name: " + mappingDefinition.getXpath(),messageContext.getMessagePojo()) );
            }
        }

        IdGenerator messageIdGenerator = Engine.getInstance().getIdGenerator( Constants.ID_GENERATOR_MESSAGE );
        IdGenerator conversationIdGenerator = Engine.getInstance().getIdGenerator( Constants.ID_GENERATOR_CONVERSATION );
        messageContext.getMessagePojo().setOutbound( false );
        messageContext.getMessagePojo().setType( org.nexuse2e.messaging.Constants.INT_MESSAGE_TYPE_NORMAL );
        Engine.getInstance().getTransactionService().initializeMessage( messageContext.getMessagePojo(),
                messageIdGenerator.getId(), conversationIdGenerator.getId(), actionId, partnerId, choreographyId );

        return messageContext;
    }

    /**
     * @param configFileName
     * @return
     */
    private MappingDefinitions readConfiguration( String configFileName ) {

        MappingDefinitions mappingDefinitions = null;
        Digester digester = new Digester();
        digester.setValidating( false );
        digester.addObjectCreate( "MappingDefinitions", "org.nexuse2e.tools.mapping.xmldata.MappingDefinitions" );
        digester.addSetProperties( "MappingDefinitions" );
        digester.addObjectCreate( "MappingDefinitions/MappingDefinition",
                "org.nexuse2e.tools.mapping.xmldata.MappingDefinition" );
        digester.addSetProperties( "MappingDefinitions/MappingDefinition" );
        digester.addSetNext( "MappingDefinitions/MappingDefinition", "addMappingDefinition",
                "org.nexuse2e.tools.mapping.xmldata.MappingDefinition" );

        try {
            mappingDefinitions = (MappingDefinitions) digester.parse( configFileName );
        } catch ( IOException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch ( SAXException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return mappingDefinitions;
    }

    /**
     * @param configFileName
     * @return
     */
    private PartnerSpecificConfigurations readPartnerSpecificConfigurations( String configFileName ) {

        PartnerSpecificConfigurations partnerSpecificConfigurations = null;
        Digester digester = new Digester();
        digester.setValidating( false );
        digester.addObjectCreate( "PartnerSpecificConfigurations",
                "org.nexuse2e.backend.pipelets.helper.PartnerSpecificConfigurations" );
        digester.addSetProperties( "PartnerSpecificConfigurations" );
        digester.addObjectCreate( "PartnerSpecificConfigurations/PartnerSpecificConfiguration",
                "org.nexuse2e.backend.pipelets.helper.PartnerSpecificConfiguration" );
        digester.addSetProperties( "PartnerSpecificConfigurations/PartnerSpecificConfiguration" );
        digester.addSetNext( "PartnerSpecificConfigurations/PartnerSpecificConfiguration",
                "addPartnerSpecificConfiguration", "org.nexuse2e.backend.pipelets.helper.PartnerSpecificConfiguration" );

        try {
            partnerSpecificConfigurations = (PartnerSpecificConfigurations) digester.parse( configFileName );
        } catch ( IOException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch ( SAXException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return partnerSpecificConfigurations;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.AbstractPipelet#teardown()
     */
    @Override
    public void teardown() {

        super.teardown();
    }

} // ParameterBasedRoutingPipelet
