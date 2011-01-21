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
package org.nexuse2e.messaging;

import org.apache.log4j.Logger;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.Constants.BeanStatus;
import org.nexuse2e.Constants.Layer;
import org.nexuse2e.controller.StateTransitionException;
import org.nexuse2e.logging.LogMessage;
import org.nexuse2e.messaging.sync.ConversationLockManager;
import org.nexuse2e.pojo.MessagePojo;
import org.nexuse2e.pojo.ParticipantPojo;
import org.springframework.beans.factory.InitializingBean;

/**
 * Component dispatching outbound messages to the correct pipeline based on their messaging protocol (TRP).
 * This component also implements re-sending of messages to support reliable message if the underlying
 * protocol supports it.
 *
 * @author mbreilmann
 */
public class FrontendOutboundDispatcher extends AbstractPipelet implements InitializingBean {

    private static Logger      LOG    = Logger.getLogger( FrontendOutboundDispatcher.class );
    private FrontendPipeline[] frontendOutboundPipelines;
    private BeanStatus         status = BeanStatus.UNDEFINED;

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.Pipelet#processMessage(org.nexuse2e.messaging.MessageContext)
     */
    public MessageContext processMessage( MessageContext messageContext ) throws NexusException {

        // for  participants with "hold" connections, do not go on with processing
        if (messageContext.getParticipant().getConnection().isHold()) {
            return null;
        }
        
        FrontendPipeline pipeline = getProtocolSpecificPipeline( messageContext );
        if ( pipeline == null ) {
            throw new NexusException( "No valid pipeline found for " + messageContext.getProtocolSpecificKey() );
        }

        int retries = messageContext.getParticipant().getConnection().getRetries();

        ParticipantPojo participantPojo = messageContext.getMessagePojo().getParticipant();

        LOG.info( new LogMessage(
                "Sending " + messageContext.getMessagePojo().getTypeName()
                + " message (" + messageContext.getMessagePojo().getMessageId()
                + ") to " + participantPojo.getPartner().getPartnerId() + " for "
                + messageContext.getChoreography().getName() + "/"
                + messageContext.getMessagePojo().getAction().getName(), messageContext.getMessagePojo() ) );

        // Request an conversation lock in order to not disallow parallel i/o, and processing activity in this conversation.
        // If everything goes alright, the lock will be released in the message sender thread after the message was sent,
        // or the sending failed. In case of error while the current thread is processing, the lock will be released as well.
        ConversationLockManager conversationLockManager = Engine.getInstance().getTransactionService().getConversationLockManager();
        boolean finishedWithoutError = false;
        try {
        	// lock
	        conversationLockManager.lock( messageContext.getConversation() );
	        // do it
	        sendMessage(pipeline, messageContext, retries);
	        finishedWithoutError = true;
        } finally {
        	// In case of no error the lock will be released in the thread that will send the message (MessageSender.run()).
        	// But in case of an error within this thread, we assume that the scheduling did not take place,
        	// thus the lock will never be released, so we release the lock here.
        	// The funky finishedWithoutError stuff is used in order to avoid multiple catch statements for all types of
        	// Throwables (RuntimeExceptions, Errors, and NexusExceptions) that must be thrown up afterwards.
        	if ( !finishedWithoutError ) {
        		conversationLockManager.unlock( messageContext.getConversation() );
        	}
        }

        return messageContext;
    } // processMessage

    /**
     * @param protocolSpecificKey
     * @return
     */
    private FrontendPipeline getProtocolSpecificPipeline( MessageContext messageContext ) {
       if (frontendOutboundPipelines != null) {
            for ( int i = 0; i < frontendOutboundPipelines.length; i++ ) {
                LOG.debug( new LogMessage ("comparing keys:" + messageContext.getProtocolSpecificKey() + " - " + frontendOutboundPipelines[i].getKey(), messageContext.getMessagePojo()) );
                if ( frontendOutboundPipelines[i].getKey().equals( messageContext.getProtocolSpecificKey() ) ) {
                    return frontendOutboundPipelines[i];
                }
            }
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet() throws Exception {

        if ( frontendOutboundPipelines == null || frontendOutboundPipelines.length == 0 ) {
            status = BeanStatus.ERROR;
        }
        status = BeanStatus.INSTANTIATED;
    }

    /**
     * @return
     */
    public FrontendPipeline[] getFrontendOutboundPipelines() {

        return frontendOutboundPipelines;
    }

    /**
     * @param frontendOutboundPipelines
     */
    public void setFrontendOutboundPipelines( FrontendPipeline[] frontendOutboundPipelines ) {

        this.frontendOutboundPipelines = frontendOutboundPipelines;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#getStatus()
     */
    public BeanStatus getStatus() {

        return status;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#initialize()
     */
    public void initialize() throws InstantiationException {

        initialize( Engine.getInstance().getCurrentConfiguration() );

    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#getRunLevel()
     */
    public Layer getActivationLayer() {

        return Layer.OUTBOUND_PIPELINES;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#validate()
     */
    public boolean validate() {

        // TODO Auto-generated method stub
        return false;
    }

    public void sendMessage(FrontendPipeline pipeline, MessageContext messageContext, int retries) {

        try { // this ensures that the conversation log will be released finally (see below)
        	MessagePojo messagePojo = messageContext.getMessagePojo();

            LOG.trace( new LogMessage("Message ( " + messagePojo.getMessageId() + " ) end timestamp: " + messagePojo.getEndDate(),messagePojo) );

            Object syncObj = Engine.getInstance().getTransactionService().getSyncObjectForConversation( messageContext.getConversation() );
            synchronized (syncObj) {
                if ( messageContext.isFirstTimeInQueue() || messageContext.getMessagePojo().getRetries() < retries ) {
                    LOG.debug( new LogMessage( "Sending message...", messagePojo ) );
    
                    MessageContext returnedMessageContext = null;
    
                    try {
                        if ( messagePojo.getType() == org.nexuse2e.messaging.Constants.INT_MESSAGE_TYPE_NORMAL
                                && messagePojo.getEndDate() != null ) {
                            // If message has been ack'ed while we were waiting do nothing
                            LOG.info( new LogMessage( "Cancelled sending message (ack was just received): " + messagePojo.getMessageId(),messagePojo) );
                            cancelRetrying(messageContext, false);
                            return;
                        }
                        // Send message
                        // increment retry count AFTER message update because the retry hasn't yet been performed
                        messagePojo.setRetries(messageContext.isFirstTimeInQueue() ? 0 : messagePojo.getRetries() + 1);
                        messageContext.setFirstTimeInQueue(false);
    
                        returnedMessageContext = pipeline.processMessage( messageContext );
                        messageContext.getStateMachine().sentMessage();

                        if (!messagePojo.isAck()) {
                            Engine.getInstance().getTransactionService().updateRetryCount( messagePojo );
                        }

                        LOG.debug( new LogMessage( "Message sent.", messagePojo ) );
                    } catch ( Throwable e ) {
                        // Persist retry count changes
                        try {
                            if ( messagePojo.isAck()) {
                                //messageContext.getStateMachine().processingFailed();
                            } else {
                                Engine.getInstance().getTransactionService().updateRetryCount( messagePojo );
                            }
                        } catch ( NexusException e1 ) {
                            LOG.error( new LogMessage( "Error saving message: " + e1, messagePojo ), e1 );
                        }
    
                        LOG.error( new LogMessage( "Error sending message: " + e, messagePojo ), e );
                    }
    
                    if ( ( returnedMessageContext != null ) && !returnedMessageContext.equals( messageContext ) ) {
                        try {
                            Engine.getInstance().getCurrentConfiguration().getStaticBeanContainer()
                                    .getFrontendInboundDispatcher().processMessage( returnedMessageContext );
                        } catch ( NexusException e ) {
                            LOG.error( new LogMessage( "Error processing synchronous reply: " + e,messagePojo) );
                        }
                    }
    
                } else {
                    if ( messagePojo.getType() == Constants.INT_MESSAGE_TYPE_NORMAL ) {
                        LOG.error( new LogMessage(
                                "Maximum number of retries reached without receiving acknowledgment - choreography: "
                                        + messagePojo.getConversation().getChoreography().getName() + ", partner: " + messagePojo.getConversation().getPartner().getPartnerId(), messagePojo ) );
                    } else {
                        LOG.debug( new LogMessage( "Max number of retries reached!", messagePojo ) );
                    }
                    cancelRetrying(messageContext);
                }
            }
        } finally {
        	// The lock was acquired in FrontendOutboundDispatcher.processMessage(MessageContext)
        	// where the scheduling of this thread took place. 
        	Engine.getInstance().getTransactionService().getConversationLockManager().unlock( messageContext.getConversation() );
        }
    } // run

    /**
     * Stop the thread for resending the message based on its reliability parameters
     */
    private void cancelRetrying(MessageContext messageContext) {

        cancelRetrying(messageContext, true);
    }

    /**
     * Stop the thread for resending the message based on its reliability parameters. If updateStatus is true also
     * set the message to failed and the conversation to error.
     * @param messageContext the message context.
     * @param updateStatus update the message status?
     */
    private void cancelRetrying(MessageContext messageContext, boolean updateStatus) {

        MessagePojo messagePojo = messageContext.getMessagePojo();
        synchronized ( messagePojo.getConversation() ) {

            if ( updateStatus ) {
                try {
                    messageContext.getStateMachine().processingFailed();
                } catch ( StateTransitionException stex ) {
                    LOG.warn( stex.getMessage() );
                } catch ( NexusException e ) {
                    LOG.error( new LogMessage( "Error while setting conversation status to ERROR",messagePojo), e );
                }
            }
            Engine.getInstance().getTransactionService().deregisterProcessingMessage(
                    messageContext.getMessagePojo().getMessageId() );
        } // synchronized
    }

} // FrontendOutboundDispatcher
