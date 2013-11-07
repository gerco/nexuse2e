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
package org.nexuse2e.backend;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.nexuse2e.ActionSpecificKey;
import org.nexuse2e.BeanStatus;
import org.nexuse2e.Constants;
import org.nexuse2e.Engine;
import org.nexuse2e.Layer;
import org.nexuse2e.Manageable;
import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.configuration.IdGenerator;
import org.nexuse2e.logging.LogMessage;
import org.nexuse2e.messaging.BackendPipeline;
import org.nexuse2e.messaging.ErrorDescriptor;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.pojo.ConversationPojo;
import org.nexuse2e.pojo.MessageLabelPojo;
import org.nexuse2e.pojo.MessagePayloadPojo;
import org.nexuse2e.pojo.MessagePojo;
import org.springframework.beans.factory.InitializingBean;
import org.xml.sax.ContentHandler;

import com.ibm.icu.text.CharsetDetector;

/**
 * @author gesch, sschulze
 *
 */
public class BackendPipelineDispatcher implements Manageable, InitializingBean {

    private BeanStatus                status    = BeanStatus.UNDEFINED;
    Map<ActionSpecificKey, BackendPipeline>     pipelines = null;
    private static Logger                       LOG       = Logger.getLogger( BackendPipelineDispatcher.class );

    /**
     * @param conversationId
     * @param actionId
     * @param primaryKey
     * @param payload
     * @return
     * @throws NexusException
     */
    public MessageContext processMessage( String conversationId, String actionId, Object primaryKey, byte[] payload )
            throws NexusException {

        ConversationPojo conversationPojo = Engine.getInstance().getTransactionService().getConversation(
                conversationId );
        if ( conversationPojo == null ) {
            throw new NexusException( "No valid conversation found for ID: " + conversationId );
        }

        return processMessage( conversationPojo.getPartner().getPartnerId(), conversationPojo.getChoreography()
                .getName(), actionId, conversationPojo.getConversationId(), null, null, primaryKey, payload, null );
    } // processMessage

    /**
     * @param partnerId
     * @param choreographyId
     * @param actionId
     * @param conversationId
     * @param label
     * @param primaryKey
     * @param payload
     * @return
     * @throws NexusException
     */
    public MessageContext processMessage( String partnerId, String choreographyId, String actionId,
            String conversationId, String label, Object primaryKey, byte[] payload ) throws NexusException {

        LOG.debug( new LogMessage( "Entering BackendPipelineDispatcher.processMessage...",conversationId,"") );

        IdGenerator messageIdGenerator = Engine.getInstance().getIdGenerator( Constants.ID_GENERATOR_MESSAGE );
        String newMessageId = messageIdGenerator.getId();

        return processMessage( partnerId, choreographyId, actionId, conversationId, newMessageId, label, primaryKey,
                payload );
    } // processMessage

    /**
     * @param partnerId
     * @param choreographyId
     * @param actionId
     * @param conversationId
     * @param messageId
     * @param label
     * @param primaryKey
     * @param payload
     * @return
     * @throws NexusException
     */
    public MessageContext processMessage( String partnerId, String choreographyId, String actionId,
            String conversationId, String messageId, String label, Object primaryKey, byte[] payload )
            throws NexusException {

        return processMessage( partnerId, choreographyId, actionId, conversationId, messageId, label, primaryKey,
                payload, null );

    }

    /**
     * @param partnerId
     * @param choreographyId
     * @param actionId
     * @param conversationId
     * @param messageId
     * @param label
     * @param primaryKey
     * @param payload
     * @return
     * @throws NexusException
     */
    public MessageContext processMessage( String partnerId, String choreographyId, String actionId,
            String conversationId, String messageId, String label, Object primaryKey, byte[] payload,
            List<ErrorDescriptor> errors ) throws NexusException {

        List<byte[]> payloads = new ArrayList<byte[]>();
        payloads.add( payload );
        return processMessage( partnerId, choreographyId, actionId, conversationId, messageId, label, primaryKey,
                payloads, errors );
    }

    /**
     * @param partnerId
     * @param choreographyId
     * @param actionId
     * @param conversationId
     * @param messageId
     * @param label
     * @param primaryKey
     * @param payloads A list of Objects that can be either of type <code>byte[]</code> or {@link MessagePayloadPojo}.
     *                  The elements must not necessarily be all of the same type. 
     * @param errors
     * @return
     * @throws NexusException
     */
    public MessageContext processMessage( String partnerId, String choreographyId, String actionId,
            String conversationId, String messageId, String label, Object primaryKey, List<? extends Object> payloads,
            List<ErrorDescriptor> errors ) throws NexusException {
        
        String contentId = null;

        if ( choreographyId == null || choreographyId.trim().equals( "" ) ) {
            throw new NexusException( "No valid choreography found for ID: " + choreographyId );
        }
        if ( actionId == null || actionId.trim().equals( "" ) ) {
            throw new NexusException( "No valid action found for ID: " + actionId );
        }

        ActionSpecificKey key = new ActionSpecificKey( actionId, choreographyId );

        BackendPipeline pipeline = pipelines.get( key );

        if ( pipeline == null ) {
            if ( LOG.isDebugEnabled() ) {
                LOG.debug(new LogMessage(  "pipelines.size: " + pipelines.size(),conversationId,messageId) );
                for ( ActionSpecificKey actionSpecificKey : pipelines.keySet() ) {
                    LOG.debug(new LogMessage(  "PipelineKey: " + actionSpecificKey + " - " + pipelines.get( actionSpecificKey ),conversationId,messageId) );
                }
            }
            throw new NexusException( "no matching pipeline found for choreography:" + choreographyId + " and Action:"
                    + actionId );
        }

        MessageContext messageContext = new MessageContext();
        messageContext.setActionSpecificKey( key );

        if ( messageId == null || messageId.equals( "" ) ) {
            IdGenerator messageIdGenerator = Engine.getInstance().getIdGenerator(
                    Constants.ID_GENERATOR_MESSAGE );
            messageId = messageIdGenerator.getId();
        }

        if ( conversationId == null || conversationId.equals( "" ) ) {
            IdGenerator conversationIdGenerator = Engine.getInstance().getIdGenerator(
                    Constants.ID_GENERATOR_CONVERSATION );
            conversationId = conversationIdGenerator.getId();
        }

        MessagePojo messagePojo = Engine.getInstance().getTransactionService().createMessage( messageId,
                conversationId, actionId, partnerId, choreographyId,
                org.nexuse2e.messaging.Constants.INT_MESSAGE_TYPE_NORMAL );
        messagePojo.setOutbound( true );

        if ( !StringUtils.isEmpty( label ) && ( label.indexOf( "|" ) != -1 ) ) {
            StringTokenizer st = new StringTokenizer( label, "|" );
            String name = st.nextToken();
            String value = st.nextToken();
            contentId = value;
            
            if ( org.nexuse2e.Constants.NX_LABEL_FILE_NAME.equals( name ) ) {
                MessageLabelPojo messageLabelPojo = new MessageLabelPojo( messagePojo,
                        new Date(), new Date(), 1, name, value );
                List<MessageLabelPojo> messageLabels = messagePojo.getMessageLabels();
                if ( messageLabels == null ) {
                    messageLabels = new ArrayList<MessageLabelPojo>();
                    messageContext.getMessagePojo().setMessageLabels( messageLabels );
                }
                messageLabels.add( messageLabelPojo );
            }
        }

        messageContext.setConversation( messagePojo.getConversation() );

        // Set conversation on MessageContext
        if ( primaryKey != null ) {
            messageContext.setData( primaryKey );
        }
        
        // payload handling > detect type > only set values, if empty yet
        List<MessagePayloadPojo> payloadPojos = new ArrayList<MessagePayloadPojo>();
        if ( payloads != null ) {
            int payloadIndex = 1;
            for ( Object currPayload : payloads ) {
                MessagePayloadPojo messagePayloadPojo = null;
                if ( currPayload instanceof byte[] ) {
                    byte[] payloadData = (byte[]) currPayload;
                    if ( payloadData != null && payloadData.length > 0 ) {
                        messagePayloadPojo = new MessagePayloadPojo();
                        messagePayloadPojo.setPayloadData( payloadData );
                    }
                } else if ( currPayload instanceof MessagePayloadPojo ) {
                    messagePayloadPojo = (MessagePayloadPojo) currPayload;
                } else {
                    throw new NexusException( "Invalid payload type detected. Must be either of type byte[] or MessagePayloadPojo." );
                }
                
                payloadPojos.add( messagePayloadPojo );
                
                // init payload pojo
                messagePayloadPojo.setMessage( messagePojo );
                if ( StringUtils.isEmpty( messagePayloadPojo.getMimeType() ) ) {
					String mimetype = null;
                	try {
						ContentHandler contenthandler = new BodyContentHandler();
						Metadata metadata = new Metadata();
						Parser parser = new AutoDetectParser();
						ByteArrayInputStream bias = new ByteArrayInputStream( messagePayloadPojo.getPayloadData() );
                        parser.parse( bias, contenthandler, metadata, null );
                        // TODO: Fix this (text/xml is detected as text/html)
                        mimetype = metadata.get( Metadata.CONTENT_TYPE );
                        if ("text/html".equals(mimetype)) {
                            mimetype = "text/xml";
                        }
                        LOG.trace(new LogMessage("autodetermined mimetype: "+mimetype,messageContext));
                        if(!Engine.getInstance().isBinaryType( mimetype )) {
                            CharsetDetector detector = new CharsetDetector();
                            detector.setText( messagePayloadPojo.getPayloadData() );
                        }
                    } catch ( Exception e ) {
                        throw new NexusException( new LogMessage( "mime detection failed: "+e.getMessage(),messageContext),e);
					} 
                	messagePayloadPojo.setMimeType(mimetype );
                }
                if ( StringUtils.isEmpty( messagePayloadPojo.getContentId() ) ) {
                    if ( StringUtils.isEmpty( contentId ) ) {
                        messagePayloadPojo.setContentId( messagePojo.getMessageId() + "__body" + payloadIndex );
                    } else {
                        messagePayloadPojo.setContentId( contentId + "__body_" + payloadIndex );
                    }
                }
                messagePayloadPojo.setSequenceNumber( new Integer( payloadIndex ) );
                messagePayloadPojo.setCreatedDate( messagePojo.getCreatedDate() );
                messagePayloadPojo.setModifiedDate( messagePojo.getCreatedDate() );
                payloadIndex++;
            }
        }
        // attach payloads to message
        messagePojo.setMessagePayloads( payloadPojos );

        if ( errors != null ) {
            messagePojo.setErrors( errors );
        }

        messageContext.setMessagePojo( messagePojo );
        try {
            pipeline.processMessage( messageContext );
        } catch ( NexusException e ) {
            throw e;
        } catch ( Error e ) {
            throw new NexusException( e.toString() );
        }

        return messageContext;
    } // processMessage

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#initialize()
     */
    public void initialize() {

        initialize( Engine.getInstance().getCurrentConfiguration() );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#initialize(org.nexuse2e.configuration.EngineConfiguration)
     */
    public void initialize( EngineConfiguration config ) {

        pipelines = config.getBackendOutboundPipelines();
        if ( pipelines == null || pipelines.size() == 0 ) {
            status = BeanStatus.ERROR;
        }
        status = BeanStatus.INITIALIZED;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#teardown()
     */
    public void teardown() {

        LOG.debug( "Freeing resources..." );

        status = BeanStatus.INSTANTIATED;
    } // teardown

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#getRunLevel()
     */
    public Layer getActivationLayer() {

        return Layer.INTERFACES;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#start()
     */
    public void activate() {

        LOG.trace( "start" );

        status = BeanStatus.ACTIVATED;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#stop()
     */
    public void deactivate() {

        LOG.trace( "stop" );

        status = BeanStatus.INITIALIZED;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#validate()
     */
    public boolean validate() {

        return false;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#getStatus()
     */
    public BeanStatus getStatus() {

        return status;
    }

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet() throws Exception {

        status = BeanStatus.INSTANTIATED;
    }

}
