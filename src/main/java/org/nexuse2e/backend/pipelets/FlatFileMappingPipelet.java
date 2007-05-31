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
import java.util.Iterator;
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
import org.nexuse2e.tools.mapping.CSVMappingFileEntry;
import org.nexuse2e.tools.mapping.conversation.ProcessXML;

/**
 * @author mbreilmann
 *
 */
public class FlatFileMappingPipelet extends AbstractPipelet {

    private static Logger      LOG             = Logger.getLogger( FlatFileMappingPipelet.class );

    public static final String XML_BLOCK_FILE  = "xmlBlockFile";
    public static final String FLAT_BLOCK_FILE = "flatBlockFile";
    public static final String MAPPING_FILE    = "mappingFile";

    private String             xmlBlockFile    = null;
    private String             flatBlockFile   = null;
    private String             mappingFile     = null;

    public FlatFileMappingPipelet() {

        parameterMap.put( XML_BLOCK_FILE, new ParameterDescriptor( ParameterType.STRING, "XML Block Definition File",
                "Path to XML block definition file.", "" ) );
        parameterMap.put( FLAT_BLOCK_FILE, new ParameterDescriptor( ParameterType.STRING,
                "Flat File Block Definition File", "Path to flat file block definition file.", "" ) );
        parameterMap.put( MAPPING_FILE, new ParameterDescriptor( ParameterType.STRING, "Mapping File",
                "Path to mapping file.", "" ) );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.AbstractPipelet#initialize(org.nexuse2e.configuration.EngineConfiguration)
     */
    @Override
    public void initialize( EngineConfiguration config ) {

        File testFile = null;

        String xmlBlockFileValue = getParameter( XML_BLOCK_FILE );
        if ( ( xmlBlockFileValue != null ) && ( xmlBlockFileValue.length() != 0 ) ) {
            xmlBlockFile = xmlBlockFileValue;
            testFile = new File( xmlBlockFile );
            if ( !testFile.exists() ) {
                status = BeanStatus.ERROR;
                LOG.error( "XML block file does not exist!" );
                return;
            }
        } else {
            status = BeanStatus.ERROR;
            LOG.error( "No value for setting 'xml block file' provided!" );
            return;
        }

        String flatBlockFileValue = getParameter( FLAT_BLOCK_FILE );
        if ( ( flatBlockFileValue != null ) && ( flatBlockFileValue.length() != 0 ) ) {
            flatBlockFile = flatBlockFileValue;
            testFile = new File( flatBlockFile );
            if ( !testFile.exists() ) {
                status = BeanStatus.ERROR;
                LOG.error( "Flat file block file does not exist!" );
                return;
            }
        } else {
            status = BeanStatus.ERROR;
            LOG.error( "No value for setting 'flat file block file' provided!" );
            return;
        }

        String mappingFileValue = getParameter( MAPPING_FILE );
        if ( ( mappingFileValue != null ) && ( mappingFileValue.length() != 0 ) ) {
            mappingFile = mappingFileValue;
            testFile = new File( mappingFile );
            if ( !testFile.exists() ) {
                status = BeanStatus.ERROR;
                LOG.error( "Mapping file does not exist!" );
                return;
            }
        } else {
            status = BeanStatus.ERROR;
            LOG.error( "No value for setting 'mapping file' provided!" );
            return;
        }

        LOG.trace( "xmlBlockFile : " + xmlBlockFile );
        LOG.trace( "flatBlockFile: " + flatBlockFile );
        LOG.trace( "mappingFile  : " + mappingFile );

        super.initialize( config );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.AbstractPipelet#processMessage(org.nexuse2e.messaging.MessageContext)
     */
    @Override
    public MessageContext processMessage( MessageContext messageContext ) throws IllegalArgumentException,
            IllegalStateException, NexusException {

        CSVMappingFileEntry mfe = new CSVMappingFileEntry();
        mfe.setCsvmappings( flatBlockFile );
        mfe.setXmlblocks( xmlBlockFile );
        mfe.setMapping( mappingFile );
        mfe.setId( "test-mapping" );

        try {
            List<MessagePayloadPojo> payloads = messageContext.getMessagePojo().getMessagePayloads();
            for ( Iterator iter = payloads.iterator(); iter.hasNext(); ) {
                MessagePayloadPojo messagePayloadPojo = (MessagePayloadPojo) iter.next();
                String contentString = new String( messagePayloadPojo.getPayloadData() );
                ProcessXML processXML = new ProcessXML();
                String result = processXML.processXML( mfe, contentString );

                if ( LOG.isTraceEnabled() ) {
                    LOG.trace( "...................." );
                    LOG.trace( result );
                    LOG.trace( "...................." );
                }

                messagePayloadPojo.setPayloadData( result.getBytes() );
            }
        } catch ( Exception e ) {
            e.printStackTrace();
            throw new NexusException( "Error mapping payload: " + e );
        }
        return null;
    }
} // FlatFileMappingPipelet
