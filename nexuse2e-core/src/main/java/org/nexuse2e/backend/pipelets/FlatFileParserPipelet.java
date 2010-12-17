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
package org.nexuse2e.backend.pipelets;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.digester.Digester;
import org.apache.log4j.Logger;
import org.nexuse2e.NexusException;
import org.nexuse2e.Constants.BeanStatus;
import org.nexuse2e.backend.pipelets.helper.PartnerSpecificConfiguration;
import org.nexuse2e.backend.pipelets.helper.PartnerSpecificConfigurations;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.Constants.ParameterType;
import org.nexuse2e.logging.LogMessage;
import org.nexuse2e.messaging.AbstractPipelet;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.pojo.MessagePayloadPojo;
import org.nexuse2e.tools.mapping.FlatFileParser;
import org.nexuse2e.tools.mapping.FlatFileRecord;
import org.xml.sax.SAXException;

/**
 * @author mbreilmann
 *
 */
public class FlatFileParserPipelet extends AbstractPipelet {

    private static Logger                   LOG                           = Logger
                                                                                  .getLogger( FlatFileParserPipelet.class );

    public static final String              FLAT_FILE_DEFINITION          = "flatFileDefinition";
    public static final String              PARTNER_SPECIFIC              = "partnerSpecific";

    private String                          flatFileDefinition            = null;

    private FlatFileParser                  flatFileParser                = null;

    private PartnerSpecificConfigurations   partnerSpecificConfigurations = null;
    private boolean                         partnerSpecific               = false;
    private HashMap<String, FlatFileParser> partnerFlatFileParsers        = null;

    public FlatFileParserPipelet() {
        
        parameterMap.put( FLAT_FILE_DEFINITION, new ParameterDescriptor( ParameterType.STRING,
                "Flat File Definition File", "Path to flat file definition file.", "" ) );
        parameterMap.put( PARTNER_SPECIFIC, new ParameterDescriptor( ParameterType.BOOLEAN,
                "Partner Specific Configuration", "Partner Specific Configuration", Boolean.FALSE ) );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.AbstractPipelet#initialize(org.nexuse2e.configuration.EngineConfiguration)
     */
    @Override
    public void initialize( EngineConfiguration config ) throws InstantiationException {

        File testFile = null;

        Boolean partnerSpecificValue = getParameter( PARTNER_SPECIFIC );
        if ( partnerSpecificValue != null ) {
            partnerSpecific = partnerSpecificValue.booleanValue();
        }
        LOG.debug( "Partner specific configuration: " + partnerSpecific + "( " + this + " )" );

        String flatFileDefinitionValue = getParameter( FLAT_FILE_DEFINITION );
        if ( ( flatFileDefinitionValue != null ) && ( flatFileDefinitionValue.length() != 0 ) ) {
            flatFileDefinition = flatFileDefinitionValue;
            testFile = new File( flatFileDefinition );
            if ( !testFile.exists() ) {
                status = BeanStatus.ERROR;
                LOG.error( "Flat file block file does not exist!" );
                throw new InstantiationException( "Flat file block file does not exist!" );
            }
        } else {
            status = BeanStatus.ERROR;
            LOG.error( "No value for setting 'flat file definition file' provided!" );
            throw new InstantiationException( "No value for setting 'flat file definition file' provided!" );
        }
        LOG.trace( "flatFileDefinition: " + flatFileDefinition );

        if ( partnerSpecific ) {
            FlatFileParser partnerFlatFileParser = null;
            partnerSpecificConfigurations = readPartnerSpecificConfigurations( flatFileDefinition );
            partnerFlatFileParsers = new HashMap<String, FlatFileParser>();

            for ( PartnerSpecificConfiguration partnerSpecificConfiguration : partnerSpecificConfigurations
                    .getPartnerSpecificConfigurations() ) {
                partnerFlatFileParser = new FlatFileParser();
                try {
                    partnerFlatFileParser.init( partnerSpecificConfiguration.getConfigurationFile() );
                    partnerFlatFileParsers.put( partnerSpecificConfiguration.getPartnerId(), partnerFlatFileParser );
                } catch ( NexusException e ) {
                    throw new InstantiationException( e.getMessage() );
                }
            }
            for ( String partnerId : partnerFlatFileParsers.keySet() ) {
                LOG
                        .debug( "Configuration fpr partner: '" + partnerId + "' - "
                                + partnerFlatFileParsers.get( partnerId ) );
            }
        } else {
            flatFileParser = new FlatFileParser();
            try {
                flatFileParser.init( flatFileDefinition );
            } catch ( NexusException e ) {
                throw new InstantiationException( e.getMessage() );
            }
        }

        super.initialize( config );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.AbstractPipelet#processMessage(org.nexuse2e.messaging.MessageContext)
     */
    @Override
    public MessageContext processMessage( MessageContext messageContext ) throws IllegalArgumentException,
            IllegalStateException, NexusException {

        List<FlatFileRecord> result = null;
        FlatFileParser theFlatFileParser = null;

        try {
            for ( MessagePayloadPojo messagePayloadPojo : messageContext.getMessagePojo().getMessagePayloads() ) {
                ByteArrayInputStream bias = new ByteArrayInputStream( messagePayloadPojo.getPayloadData() );

                LOG.debug( "Partner specific configuration: " + partnerSpecific + "( " + this + " )" );
                if ( partnerSpecific ) {
                    LOG.debug( "Trying to retrieve configuration for partner: '"
                            + messageContext.getMessagePojo().getParticipant().getPartner().getPartnerId() + "'" );
                    theFlatFileParser = partnerFlatFileParsers.get( messageContext.getMessagePojo().getParticipant()
                            .getPartner().getPartnerId() );
                } else {
                    theFlatFileParser = flatFileParser;
                }

                if ( theFlatFileParser == null ) {
                    throw new NexusException( "No mappings found for partner: "
                            + messageContext.getMessagePojo().getParticipant().getPartner().getPartnerId() );
                }

                result = theFlatFileParser.process( bias );

                if ( LOG.isTraceEnabled() ) {
                    LOG.trace(new LogMessage(  "....................",messageContext ) );
                    LOG.trace( new LogMessage( result.toString(), messageContext ) );
                    LOG.trace( new LogMessage( "....................",messageContext) );
                }

                messageContext.setData( result );
            }
        } catch ( Exception e ) {
            throw new NexusException( new LogMessage( "Error mapping payload: " + e.getMessage(), messageContext ), e );
        }
        return messageContext;
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

} // FlatFileParserPipelet
