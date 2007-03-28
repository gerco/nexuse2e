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
package org.nexuse2e.messaging;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.ProtocolSpecificKey;
import org.nexuse2e.Constants.BeanStatus;
import org.nexuse2e.Constants.Runlevel;
import org.nexuse2e.pojo.ConversationPojo;
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

    private static Logger       LOG        = Logger.getLogger( FrontendOutboundDispatcher.class );
    private FrontendPipeline[]  frontendOutboundPipelines;
    private BeanStatus          status     = BeanStatus.UNDEFINED;
    
    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.Pipelet#processMessage(org.nexuse2e.messaging.MessagePipeletParameter)
     */
    public MessageContext processMessage( MessageContext messagePipeletParameter )
            throws NexusException {

        int interval = Constants.DEFAULT_MESSAGE_INTERVAL;

        FrontendPipeline pipeline = getProtocolSpecificPipeline( messagePipeletParameter.getProtocolSpecificKey() );
        if ( pipeline == null ) {
            throw new NexusException( "No valid pipeline found for " + messagePipeletParameter.getProtocolSpecificKey() );
        }

        final Runnable messageSender = new MessageSender( pipeline, messagePipeletParameter, messagePipeletParameter
                .getParticipant().getConnection().getRetries() );
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool( 1 );

        ParticipantPojo participantPojo = messagePipeletParameter.getMessagePojo().getParticipant();
        if ( participantPojo != null ) {
            interval = participantPojo.getConnection().getMessageInterval();
        }

        LOG.debug( "Waiting " + interval + " seconds until message resend." );

        ScheduledFuture<?> handle = scheduler.scheduleAtFixedRate( messageSender, 0, interval, TimeUnit.SECONDS );
        Engine.getInstance().getTransactionService().registerProcessingMessage(
                messagePipeletParameter.getMessagePojo().getMessageId(), handle, scheduler );

        return messagePipeletParameter;
    } // processMessage


    /**
     * @param protocolSpecificKey
     * @return
     */
    private FrontendPipeline getProtocolSpecificPipeline( ProtocolSpecificKey protocolSpecificKey ) {

        for ( int i = 0; i < frontendOutboundPipelines.length; i++ ) {
            LOG.debug( "comparing keys:" + protocolSpecificKey + " - " + frontendOutboundPipelines[i].getKey() );
            if ( frontendOutboundPipelines[i].getKey().equals( protocolSpecificKey ) ) {
                return frontendOutboundPipelines[i];
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
    public void initialize() {

        initialize( Engine.getInstance().getCurrentConfiguration() );

    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#getRunLevel()
     */
    public Runlevel getActivationRunlevel() {

        return Runlevel.OUTBOUND_PIPELINES;
    }

    

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#validate()
     */
    public boolean validate() {

        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @author mbreilmann
     *
     */
    private class MessageSender implements Runnable {

        MessageContext messagePipeletParameter = null;
        FrontendPipeline        pipeline                = null;
        int                     retries                 = 0;
        int                     retryCount              = 0;

        /**
         * @param pipeline
         * @param messagePipeletParameter
         * @param retries
         */
        MessageSender( FrontendPipeline pipeline, MessageContext messagePipeletParameter, int retries ) {

            this.messagePipeletParameter = messagePipeletParameter;
            this.pipeline = pipeline;
            this.retries = retries;
            LOG.debug( "Retries: " + retries );
        }

        public void run() {

            MessagePojo messagePojo = messagePipeletParameter.getMessagePojo();
            ;
            ConversationPojo conversationPojo = messagePojo.getConversation();

            if ( retryCount <= retries ) {
                LOG.debug( "Sending message..." );
                try {
                    retryCount++;
                    synchronized ( conversationPojo ) {
                        if ( ( messagePojo.getType() != Constants.INT_MESSAGE_TYPE_NORMAL )
                                && ( conversationPojo.getStatus() == org.nexuse2e.Constants.CONVERSATION_STATUS_PROCESSING ) ) {
                            conversationPojo.setStatus( org.nexuse2e.Constants.CONVERSATION_STATUS_SENDING_ACK );
                        }

                        // Send message
                        messagePipeletParameter = pipeline.processMessage( messagePipeletParameter );

                        if ( messagePojo.getType() == Constants.INT_MESSAGE_TYPE_NORMAL ) {
                            if ( conversationPojo.getStatus() == org.nexuse2e.Constants.CONVERSATION_STATUS_PROCESSING ) {
                                conversationPojo.setStatus( org.nexuse2e.Constants.CONVERSATION_STATUS_AWAITING_ACK );
                            } else {
                                LOG.error( "Unexpected conversation state after sending normal message: "
                                        + conversationPojo.getStatus() );
                            }
                        } else {
                            Engine.getInstance().getTransactionService().deregisterProcessingMessage(
                                    messagePojo.getMessageId() );
                            messagePojo.setStatus( org.nexuse2e.Constants.MESSAGE_STATUS_SENT );
                            if ( conversationPojo.getStatus() == org.nexuse2e.Constants.CONVERSATION_STATUS_SENDING_ACK ) {
                                conversationPojo
                                        .setStatus( org.nexuse2e.Constants.CONVERSATION_STATUS_ACK_SENT_AWAITING_BACKEND );
                            } else if ( conversationPojo.getStatus() == org.nexuse2e.Constants.CONVERSATION_STATUS_BACKEND_SENT_SENDING_ACK ) {
                                if ( conversationPojo.getCurrentAction().isEnd() ) {
                                    conversationPojo.setStatus( org.nexuse2e.Constants.CONVERSATION_STATUS_COMPLETED );
                                } else {
                                    conversationPojo.setStatus( org.nexuse2e.Constants.CONVERSATION_STATUS_IDLE );
                                }
                            } else {
                                LOG.error( "Unexpected conversation state after sending ack message: "
                                        + conversationPojo.getStatus() );
                            }
                        }

                        // Persist status changes
                        Engine.getInstance().getTransactionService().updateTransaction( conversationPojo );
                    } // synchronized

                    LOG.debug( "Message sent." );
                } catch ( Throwable e ) {
                    e.printStackTrace();
                    LOG.error( "Error sending message: " + e );
                }
            } else {
                LOG.debug( "Max number of retries reached!" );
                cancelRetrying();
            }
        } // run

        /**
         * 
         */
        private void cancelRetrying() {

            MessagePojo messagePojo = messagePipeletParameter.getMessagePojo();
            synchronized ( messagePojo.getConversation() ) {

                messagePojo.setStatus( org.nexuse2e.Constants.MESSAGE_STATUS_FAILED );
                messagePojo.getConversation().setStatus( org.nexuse2e.Constants.CONVERSATION_STATUS_ERROR );
                try {
                    Engine.getInstance().getTransactionService().updateTransaction( messagePojo.getConversation() );
                } catch ( NexusException e ) {
                    LOG.error( "Error updating conversation/message on failed atempt message!" );
                    e.printStackTrace();
                }
                Engine.getInstance().getTransactionService().deregisterProcessingMessage(
                        messagePipeletParameter.getMessagePojo().getMessageId() );
            } // synchronized
        }

    } // inner class MessageSender
    
} // FrontendOutboundDispatcher
