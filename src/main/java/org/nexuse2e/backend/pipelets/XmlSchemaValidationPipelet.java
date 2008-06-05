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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.nexuse2e.util.ServerPropertiesUtil;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * @author Sebastian Schulze
 * @date 19.12.2007
 */
public class XmlSchemaValidationPipelet extends AbstractPipelet {

    private static Logger         LOG                = Logger.getLogger( XmlSchemaValidationPipelet.class );

    protected static final String SCHEMA_FILE_PATH   = "schemaFilePath";
    protected static final String VALIDATION_ENABLED = "validationEnabled";

    protected String              schemaFilePath;
    protected Map<String, Schema> schemas            = new HashMap<String, Schema>();

    public XmlSchemaValidationPipelet() {

        frontendPipelet = false;

        parameterMap.put( SCHEMA_FILE_PATH, new ParameterDescriptor( ParameterType.STRING, "Schema file",
                "Path to the schema file for XML validation", "" ) );
        parameterMap.put( VALIDATION_ENABLED, new ParameterDescriptor( ParameterType.BOOLEAN, "Enable validation",
                "Uncheck this option if you want to disable the XML schema validation", Boolean.TRUE ) );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.AbstractPipelet#initialize(org.nexuse2e.configuration.EngineConfiguration)
     */
    @Override
    public void initialize( EngineConfiguration config ) throws InstantiationException {

        LOG.trace( "initializing" );
        schemaFilePath = getParameter( SCHEMA_FILE_PATH );
        schemaFilePath = ServerPropertiesUtil.replacePathSeparators( schemaFilePath );

        super.initialize( config );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.AbstractPipelet#teardown()
     */
    @Override
    public void teardown() {

        schemas = new HashMap<String, Schema>();

        super.teardown();
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.AbstractPipelet#processMessage(org.nexuse2e.messaging.MessageContext)
     */
    @Override
    public MessageContext processMessage( MessageContext messageContext ) throws IllegalArgumentException,
            IllegalStateException, NexusException {

        Boolean enabled = getParameter( VALIDATION_ENABLED );

        if ( enabled != null && enabled.booleanValue() && !StringUtils.isEmpty( schemaFilePath ) ) {
            // get message payloads
            List<MessagePayloadPojo> payloads = messageContext.getMessagePojo().getMessagePayloads();

            // iterate over message payloads

            for ( MessagePayloadPojo pojo : payloads ) {

                String tempFilePath = ServerPropertiesUtil.replaceServerProperties( schemaFilePath, messageContext );
                tempFilePath = ServerPropertiesUtil.replacePayloadDependedValues( tempFilePath, pojo
                        .getSequenceNumber(), messageContext );

                Schema schema = schemas.get( tempFilePath );

                if ( schema == null ) {
                    SchemaFactory factory = SchemaFactory.newInstance( XMLConstants.W3C_XML_SCHEMA_NS_URI );
                    try {
                        schema = factory.newSchema( new File( tempFilePath ) );

                        schemas.put( tempFilePath, schema );
                    } catch ( Exception e ) {
                        throw new NexusException( "Error reading XML Schema file: " + tempFilePath + ". Cause: "
                                + e.getMessage() );
                    }
                }

                try {

                    Validator validator = schema.newValidator();
                    LOG.debug( "Using XML Schema: " + tempFilePath );

                    InputSource inputSource = new InputSource( new ByteArrayInputStream( pojo.getPayloadData() ) );
                    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                    documentBuilderFactory.setNamespaceAware( true );
                    DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
                    Document document = builder.parse( inputSource );

                    validator.validate( new DOMSource( document ) );

                    LOG.info( "Validated XML Schema compliance of payload - OK!" );
                } catch ( Exception e ) {
                    throw new NexusException( "XML validation error (Conversation Id: "
                            + messageContext.getMessagePojo().getConversation().getConversationId() + ", Message Id: "
                            + messageContext.getMessagePojo().getMessageId() + "): " + e.getMessage(), e );
                }
            }
        }

        return messageContext;
    }

}
