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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;
import org.nexuse2e.NexusException;
import org.nexuse2e.Constants.BeanStatus;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.Constants.ParameterType;
import org.nexuse2e.messaging.AbstractPipelet;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.pojo.MessagePayloadPojo;
import org.nexuse2e.tools.mapping.FlatFileParser;
import org.nexuse2e.tools.mapping.FlatFileRecord;

/**
 * @author mbreilmann
 *
 */
public class FlatFileParserPipelet extends AbstractPipelet {

    private static Logger      LOG                  = Logger.getLogger( FlatFileParserPipelet.class );

    public static final String FLAT_FILE_DEFINITION = "flatFileDefinition";

    private String             flatFileDefinition   = null;

    private FlatFileParser     flatFileParser       = null;

    public FlatFileParserPipelet() {

        parameterMap.put( FLAT_FILE_DEFINITION, new ParameterDescriptor( ParameterType.STRING,
                "Flat File Definition File", "Path to flat file definition file.", "" ) );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.AbstractPipelet#initialize(org.nexuse2e.configuration.EngineConfiguration)
     */
    @Override
    public void initialize( EngineConfiguration config ) throws InstantiationException {

        File testFile = null;

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

        flatFileParser = new FlatFileParser();

        try {
            flatFileParser.init( flatFileDefinition );
        } catch ( NexusException e ) {
            throw new InstantiationException( e.getMessage() );
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

        try {
            for ( MessagePayloadPojo messagePayloadPojo : messageContext.getMessagePojo().getMessagePayloads() ) {
                String contentString = new String( messagePayloadPojo.getPayloadData() );
                ByteArrayInputStream bias = new ByteArrayInputStream( contentString.getBytes() );

                result = flatFileParser.process( bias );

                if ( LOG.isTraceEnabled() ) {
                    LOG.trace( "...................." );
                    LOG.trace( result );
                    LOG.trace( "...................." );
                }

                messageContext.setData( result );
            }
        } catch ( Exception e ) {
            e.printStackTrace();
            throw new NexusException( "Error mapping payload: " + e );
        }
        return messageContext;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.AbstractPipelet#teardown()
     */
    @Override
    public void teardown() {

        super.teardown();
    }

} // FlatFileParserPipelet
