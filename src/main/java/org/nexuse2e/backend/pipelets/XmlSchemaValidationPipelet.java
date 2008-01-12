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

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.Constants.ParameterType;
import org.nexuse2e.messaging.AbstractPipelet;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.pojo.MessagePayloadPojo;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * @author Sebastian Schulze
 * @date 19.12.2007
 */
public class XmlSchemaValidationPipelet extends AbstractPipelet {

    private static Logger LOG = Logger.getLogger( XmlSchemaValidationPipelet.class );
    
    protected static final String SCHEMA_FILE_PATH = "schemaFilePath";
    
    protected String schemaFilePath;
    protected Validator validator = null;
    
    public XmlSchemaValidationPipelet() {
        parameterMap.put( SCHEMA_FILE_PATH, new ParameterDescriptor( ParameterType.STRING, "Schema file",
                "Path to the schema file for XML validation", "" ) );
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.AbstractPipelet#initialize(org.nexuse2e.configuration.EngineConfiguration)
     */
    @Override
    public void initialize( EngineConfiguration config ) throws InstantiationException {

        LOG.trace( "initializing" );
        schemaFilePath = getParameter( SCHEMA_FILE_PATH );
        
        if ( !StringUtils.isEmpty( schemaFilePath ) ) {
            SchemaFactory factory = SchemaFactory.newInstance( XMLConstants.W3C_XML_SCHEMA_NS_URI );
            Schema schema;
            try {
                schema = factory.newSchema( new File( schemaFilePath ) );
                validator = schema.newValidator();
                LOG.trace( "Using XML Schema: " + schemaFilePath );
            } catch ( Exception e ) {
//                LOG.error( "Error reading XML Schema file: " + schemaFilePath );
                throw new InstantiationException( "Error reading XML Schema file: " + schemaFilePath
                        + ". Cause: " + e.getMessage() );
            }
        } else {
//            LOG.error( "No XML Schema file provided" );
            throw new InstantiationException( "No XML Schema file provided" );
        }
        super.initialize( config );
    }
    
    
    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.AbstractPipelet#processMessage(org.nexuse2e.messaging.MessageContext)
     */
    @Override
    public MessageContext processMessage( MessageContext messageContext ) throws IllegalArgumentException,
            IllegalStateException, NexusException {

        if ( validator != null ) {
            // get message payloads
            List<MessagePayloadPojo> payloads = messageContext.getMessagePojo().getMessagePayloads();
            
            // iterate over message payloads
            for ( MessagePayloadPojo pojo : payloads ) {
                
                try {
                    InputSource inputSource = new InputSource( new ByteArrayInputStream( pojo.getPayloadData() ) );
                    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                    documentBuilderFactory.setNamespaceAware( true );
                    DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
                    Document document = builder.parse( inputSource );
        
                    validator.validate( new DOMSource( document ) );
        
                    LOG.info( "Validated XML Schema compliance of payload - OK!" );
                } catch ( Exception e ) {
                    throw new NexusException( "XML validation error (Conversation Id: "
                            + messageContext.getMessagePojo().getConversation().getConversationId()
                            + ", Message Id: " + messageContext.getMessagePojo().getMessageId() + "): "
                            + e.getMessage(), e );
                }
            }
        } else {
            throw new NexusException( "Validator cannot not be initialized" );
        }

        return messageContext;
    }

}
