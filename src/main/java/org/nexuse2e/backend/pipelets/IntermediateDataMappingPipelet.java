/**
 * NEXUSe2e Business Messaging Open Source  
 * Copyright 2007, Tamgroup and X-ioma GmbH   
 *  
 * This is free software; you can redistribute it and/or modify it  
 * under the terms of the GNU Lesser General Public License as  
 * published by the Free Software Foundation version 2.1 of  
 * the License.  
 *  
 * This software is distributed in the hope that it will be useful,  
 * but WITHOUT ANY WARRANTY; without even the implied warranty of  
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU  
 * Lesser General Public License for more details.  
 *  
 * You should have received a copy of the GNU Lesser General Public  
 * License along with this software; if not, write to the Free  
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.nexuse2e.backend.pipelets;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.digester.Digester;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.Constants.BeanStatus;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.Constants.ParameterType;
import org.nexuse2e.messaging.AbstractPipelet;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.service.DataConversionService;
import org.nexuse2e.service.Service;
import org.nexuse2e.tools.mapping.FlatFileRecord;
import org.nexuse2e.tools.mapping.xmldata.MappingDefinition;
import org.nexuse2e.tools.mapping.xmldata.MappingDefinitions;
import org.xml.sax.SAXException;

/**
 * @author mbreilmann
 *
 */
public class IntermediateDataMappingPipelet extends AbstractPipelet {

    private static Logger         LOG                = Logger.getLogger( IntermediateDataMappingPipelet.class );

    public static final String    CONFIG_FILE        = "config_file";
    public static final String    MAPPING_SERVICE    = "mapping_service";

    public static final String    COMMAND_MAP_LEFT   = "$map_left";
    public static final String    COMMAND_MAP_RIGHT  = "$map_right";

    private String                configFileName     = null;
    private DataConversionService mappingService     = null;
    private MappingDefinitions    mappingDefinitions = null;

    /**
     * 
     */
    public IntermediateDataMappingPipelet() {

        parameterMap.put( CONFIG_FILE, new ParameterDescriptor( ParameterType.STRING, "Configuration Path",
                "Path to configuration file", "" ) );
        parameterMap.put( MAPPING_SERVICE, new ParameterDescriptor( ParameterType.SERVICE, "Data Mapping Service",
                "The Data Mapping and Conversion Service", null ) );
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
                this.mappingService = (DataConversionService) service;
            }
        }

        String configFileNameValue = getParameter( CONFIG_FILE );
        if ( ( configFileNameValue != null ) && ( configFileNameValue.length() != 0 ) ) {
            configFileName = configFileNameValue;
            testFile = new File( configFileName );
            if ( !testFile.exists() ) {
                status = BeanStatus.ERROR;
                LOG.error( "Configuration file does not exist!" );
                throw new InstantiationException( "Configuration file does not exist!" );
            }

            mappingDefinitions = readConfiguration( configFileName );

        } else {
            status = BeanStatus.ERROR;
            LOG.error( "No value for setting 'conversion definition file' provided!" );
            throw new InstantiationException( "No value for setting 'conversion definition file' provided!" );
        }

        LOG.trace( "configFileName  : " + configFileName );

        super.initialize( config );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.AbstractPipelet#processMessage(org.nexuse2e.messaging.MessageContext)
     */
    @SuppressWarnings("unchecked")
    @Override
    public MessageContext processMessage( MessageContext messageContext ) throws IllegalArgumentException,
            IllegalStateException, NexusException {

        if ( configFileName != null ) {
            Object records = messageContext.getData();
            if ( ( records != null ) && ( records instanceof List ) ) {
                try {
                    processMappings( (List<FlatFileRecord>) records );

                    LOG.debug( "Modified records: " + records );
                } catch ( Exception e ) {
                    throw new NexusException( "Error converting records: " + e );
                }
            } else {
                throw new IllegalArgumentException( "No data found to store to database!" );
            }
        } else {
            LOG.error( "No configuration file specified - no mapping possible." );
            throw new NexusException( "No configuration file specified - no mapping possible." );
        }// if

        return messageContext;
    }

    /**
     * @param document
     * @return
     */
    private List<FlatFileRecord> processMappings( List<FlatFileRecord> records ) {

        for ( MappingDefinition mappingDefinition : mappingDefinitions.getMappingDefinitions() ) {
            LOG.debug( "def.Category: " + mappingDefinition.getCategory() );
            LOG.debug( "def.Command: " + mappingDefinition.getCommand() );
            LOG.debug( "def.XPath: " + mappingDefinition.getXpath() );

            for ( FlatFileRecord flatFileRecord : records ) {
                String value = flatFileRecord.getColumn( mappingDefinition.getXpath(), null );
                if ( value != null ) {
                    flatFileRecord.setColumn( mappingDefinition.getXpath(), mapData( value, mappingDefinition ) );
                }
            }
        }

        return records;
    }

    /**
     * @param sourceValue
     * @param mappingDefinition
     * @return
     */
    private String mapData( String sourceValue, MappingDefinition mappingDefinition ) {

        String result = null;

        if ( mappingService != null ) {
            // LOG.debug( "calling mappingservice" );
            String targetValue = mappingService.processConversion( sourceValue, mappingDefinition );
            if ( !StringUtils.isEmpty( targetValue ) ) {
                return targetValue;
            } else {
                return "";
            }
        } else {
            LOG.error( "Data Mapping Service must be configured!" );
        }

        return result;
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

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.AbstractPipelet#teardown()
     */
    @Override
    public void teardown() {

        super.teardown();
    }

    public MappingDefinitions getMappingDefinitions() {

        return mappingDefinitions;
    }

    public void setMappingDefinitions( MappingDefinitions mappingDefinitions ) {

        this.mappingDefinitions = mappingDefinitions;
    }
} // IntermediateDataMappingPipelet
