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
package org.nexuse2e.backend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.nexuse2e.ActionSpecificKey;
import org.nexuse2e.Constants;
import org.nexuse2e.Engine;
import org.nexuse2e.Manageable;
import org.nexuse2e.NexusException;
import org.nexuse2e.Constants.BeanStatus;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.configuration.IdGenerator;
import org.nexuse2e.messaging.BackendPipeline;
import org.nexuse2e.messaging.ErrorDescriptor;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.pojo.ConversationPojo;
import org.nexuse2e.pojo.MessagePayloadPojo;
import org.nexuse2e.pojo.MessagePojo;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author gesch
 *
 */
public class BackendPipelineDispatcher implements Manageable, InitializingBean {

    private Constants.BeanStatus                status    = Constants.BeanStatus.UNDEFINED;
    HashMap<ActionSpecificKey, BackendPipeline> pipelines = null;
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

        LOG.debug( "Entering BackendPipelineDispatcher.processMessage..." );

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
                LOG.debug( "pipelines.size: " + pipelines.size() );
                for ( ActionSpecificKey actionSpecificKey : pipelines.keySet() ) {
                    LOG.debug( "PipelineKey: " + actionSpecificKey + " - " + pipelines.get( actionSpecificKey ) );
                }
            }
            throw new NexusException( "no matching pipeline found for choreography:" + choreographyId + " and Action:"
                    + actionId );
        }

        MessageContext messageContext = new MessageContext();
        messageContext.setActionSpecificKey( key );

        //      TODO labelhandling

        if ( conversationId == null || conversationId.equals( "" ) ) {
            IdGenerator conversationIdGenerator = Engine.getInstance().getIdGenerator(
                    Constants.ID_GENERATOR_CONVERSATION );
            conversationId = conversationIdGenerator.getId();
        }

        MessagePojo messagePojo = Engine.getInstance().getTransactionService().createMessage( messageId,
                conversationId, actionId, partnerId, choreographyId,
                org.nexuse2e.messaging.Constants.INT_MESSAGE_TYPE_NORMAL );
        messagePojo.setOutbound( true );

        if ( primaryKey != null ) {
            messageContext.setData( primaryKey );
        }
        if ( payload != null && payload.length > 0 ) {
            MessagePayloadPojo messagePayloadPojo = new MessagePayloadPojo();
            messagePayloadPojo.setMessage( messagePojo );
            messagePayloadPojo.setPayloadData( payload );
            // TODO: MIME type must be determined!
            messagePayloadPojo.setMimeType( "text/xml" );
            messagePayloadPojo.setContentId( messagePojo.getMessageId() + "-body1" );
            messagePayloadPojo.setSequenceNumber( new Integer( 1 ) );
            messagePayloadPojo.setCreatedDate( messagePojo.getCreatedDate() );
            messagePayloadPojo.setModifiedDate( messagePojo.getCreatedDate() );

            List<MessagePayloadPojo> bodyParts = new ArrayList<MessagePayloadPojo>();
            bodyParts.add( messagePayloadPojo );
            messagePojo.setMessagePayloads( bodyParts );
            //messageContext.setData( payload );    
        }

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
            status = Constants.BeanStatus.ERROR;
        }
        status = Constants.BeanStatus.INITIALIZED;
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
    public Constants.Runlevel getActivationRunlevel() {

        return Constants.Runlevel.INTERFACES;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#start()
     */
    public void activate() {

        // TODO Auto-generated method stub
        LOG.trace( "start" );

        status = Constants.BeanStatus.ACTIVATED;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#stop()
     */
    public void deactivate() {

        // TODO Auto-generated method stub
        LOG.trace( "stop" );

        status = Constants.BeanStatus.INITIALIZED;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#validate()
     */
    public boolean validate() {

        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#getStatus()
     */
    public Constants.BeanStatus getStatus() {

        // TODO Auto-generated method stub
        return status;
    }

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet() throws Exception {

        status = Constants.BeanStatus.INSTANTIATED;
    }

}
