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

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;
import org.nexuse2e.Engine;
import org.nexuse2e.Manageable;
import org.nexuse2e.NexusException;
import org.nexuse2e.Constants.BeanStatus;
import org.nexuse2e.Constants.Layer;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.logging.LogMessage;
import org.nexuse2e.pojo.ConversationPojo;
import org.nexuse2e.pojo.MessagePojo;

/**
 * Component in the NEXUSe2e frontend that serializes the processing of inbound messages per Choreography. 
 * Messages are placed in a queue and are processed by a single thread in fifo order.
 *
 * @author mbreilmann
 */
public class FrontendActionSerializer implements Manageable {

    private static Logger                 LOG                      = Logger.getLogger( FrontendActionSerializer.class );

    private String                        choreographyId           = null;

    private BackendInboundDispatcher      backendInboundDispatcher = null;

    private StateMachineExecutor          stateMachineExecutor     = null;

    private BlockingQueue<MessageContext> queue                    = new LinkedBlockingQueue<MessageContext>();

    private String                        queueName                = "queue_name_not_set";

    private BeanStatus                    status                   = BeanStatus.UNDEFINED;

    private InboundQueueListener          inboundQueueListener     = null;

    private Thread                        queueListenerThread      = null;

    /**
     * @param choreographyId
     */
    public FrontendActionSerializer( String choreographyId ) {

        this.choreographyId = choreographyId;
        queueName = choreographyId + org.nexuse2e.Constants.POSTFIX_INBOUND_QUEUE;

    } // constructor

    /**
     * @param messageContext
     * @return
     */
    public MessageContext processMessage( MessageContext messageContext ) throws NexusException {

        LOG.debug( "FrontendActionSerializer.processMessage - " + choreographyId );

        if ( status == BeanStatus.STARTED ) {

            // Test whether the action is allowed at this time
            ConversationPojo conversationPojo = null;
            try {
                conversationPojo = stateMachineExecutor.validateTransition( messageContext );
            } catch ( NexusException e ) {
                e.printStackTrace();
                LOG.error( new LogMessage( "Not a valid action: " + messageContext.getMessagePojo().getAction(),
                        messageContext.getMessagePojo() ) );
                throw e;
            }

            if ( conversationPojo == null ) {
                throw new NexusException( "Choreography transition not allowed at this time - message ID: "
                        + messageContext.getMessagePojo().getMessageId() );
            }

            // Persist the message
            try {
                queueMessage( messageContext, conversationPojo, true );
            } catch ( Exception e ) {
                throw new NexusException( "Error storing new conversation/message state: " + e );
            }
        } else {
            LOG.error( new LogMessage( "Received message for FrontendActionSerializer (" + choreographyId
                    + ") which hasn't been properly started!", messageContext.getMessagePojo() ) );
        }

        return messageContext;
    } // processMessage

    /**
     * @param messageContext
     * @param conversationPojo
     * @param newMessage
     * @throws NexusException
     */
    private void queueMessage( MessageContext messageContext, ConversationPojo conversationPojo, boolean newMessage )
            throws NexusException {

        synchronized ( conversationPojo ) {
            messageContext.getMessagePojo().setStatus( org.nexuse2e.Constants.MESSAGE_STATUS_QUEUED );
            if ( newMessage ) {
                List<MessagePojo> messages = conversationPojo.getMessages();
                /*
                 for ( Iterator iter = messages.iterator(); iter.hasNext(); ) {
                 MessagePojo tempMessagePojo = (MessagePojo) iter.next();
                 LOG.debug( "tempMessagePojo: " + tempMessagePojo );
                 }
                 */
                messages.add( messageContext.getMessagePojo() );
                Engine.getInstance().getTransactionService().storeTransaction( conversationPojo,
                        messageContext.getMessagePojo() );
            } else {
                Engine.getInstance().getTransactionService().updateTransaction( conversationPojo );
            }

            // Submit the message to the queue/backend
            queue.add( messageContext );
        }
    } // queueMessage

    /**
     * @param choreographyId
     * @param participantId
     * @param conversationId
     * @param messageId
     * @throws NexusException
     */
    public void requeueMessage( String choreographyId, String participantId, String conversationId, String messageId )
            throws NexusException {

        LOG.debug( new LogMessage( "Requeueing message " + messageId + " for choreography " + choreographyId
                + ", participant " + participantId + ", conversation " + conversationId, conversationId, messageId ) );

        MessageContext messageContext = Engine.getInstance().getTransactionService().getMessageContext( messageId );

        if ( messageContext != null ) {
            queueMessage( messageContext, messageContext.getConversation(), false );
        } else {
            LOG.error( "Message: " + messageId + " could not be found in database, cancelled requeueing!" );
        }
    }

    /**
     * @return the choreographyId
     */
    public String getChoreographyId() {

        return choreographyId;
    } // getChoreographyId

    /**
     * @param choreographyId the choreographyId to set
     */
    public void setChoreographyId( String choreographyId ) {

        this.choreographyId = choreographyId;
    } // setChoreographyId

    /**
     * 
     */
    public void deactivate() {

        LOG.trace( "deactivate" );

        if ( status == BeanStatus.STARTED ) {
            inboundQueueListener.setStopRequested( true );
            queueListenerThread.interrupt();
            status = BeanStatus.INITIALIZED;
        }
    } // stop

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#getRunLevel()
     */
    public Layer getActivationLayer() {

        return Layer.CORE;
    }

    /**
     * 
     */
    public void activate() {

        LOG.trace( "FrontendActionSerializer.activate - " + choreographyId );

        if ( status == BeanStatus.INITIALIZED ) {
            queueListenerThread = new Thread( inboundQueueListener, queueName );
            queueListenerThread.start();
            status = BeanStatus.STARTED;
        } else {
            LOG.error( "Trying to start uninitialized FrontendActionSerializer (" + choreographyId + ")!" );
        }
    } // start

    /**
     * force configuration update
     */
    public void initialize() {

        initialize( Engine.getInstance().getCurrentConfiguration() );
    } // initialize

    /**
     * force configuration update
     */
    public void initialize( EngineConfiguration config ) {

        LOG.trace( "Initializing FrontendActionSerializer " + choreographyId );

        backendInboundDispatcher = config.getStaticBeanContainer().getBackendInboundDispatcher();

        stateMachineExecutor = config.getStaticBeanContainer().getFrontendInboundDispatcher();

        inboundQueueListener = new InboundQueueListener();

        if ( validate() ) {
            status = BeanStatus.INITIALIZED;
        } else {
            status = BeanStatus.ERROR;
        }
    } // initialize

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#teardown()
     */
    public void teardown() {

        LOG.trace( "Freeing resources..." );

        status = BeanStatus.INSTANTIATED;
    } // teardown

    /**
     * @return
     */
    public boolean validate() {

        if ( ( backendInboundDispatcher != null ) && ( stateMachineExecutor != null ) && ( queue != null )
                && ( inboundQueueListener != null ) ) {
            return true;
        }

        return false;
    } // validate

    /**
     * @return bean status
     */
    public BeanStatus getStatus() {

        return status;
    } // getStatus

    /**
     * @author mbreilmann
     *
     */
    private class InboundQueueListener implements Runnable {

        private boolean stopRequested = false;

        protected void setStopRequested( boolean stopRequested ) {

            this.stopRequested = stopRequested;
        }

        public void run() {

            MessageContext messageContext = null;
            MessagePojo messagePojo = null;
            ConversationPojo conversationPojo = null;

            while ( !stopRequested ) {
                try {
                    messageContext = queue.take();
                    messagePojo = messageContext.getMessagePojo();
                    conversationPojo = messagePojo.getConversation();

                    synchronized ( conversationPojo ) {

                        if ( conversationPojo.getStatus() == org.nexuse2e.Constants.CONVERSATION_STATUS_PROCESSING ) {
                            conversationPojo.setStatus( org.nexuse2e.Constants.CONVERSATION_STATUS_AWAITING_BACKEND );
                        } else if ( conversationPojo.getStatus() == org.nexuse2e.Constants.CONVERSATION_STATUS_ERROR ) {
                            // Fixing state for requeued message
                            conversationPojo
                                    .setStatus( org.nexuse2e.Constants.CONVERSATION_STATUS_ACK_SENT_AWAITING_BACKEND );

                        }

                        // Initiate the backend process
                        FrontendActionSerializer.this.backendInboundDispatcher.processMessage( messageContext );

                        messagePojo.setStatus( org.nexuse2e.Constants.MESSAGE_STATUS_SENT );
                        if ( ( conversationPojo.getStatus() == org.nexuse2e.Constants.CONVERSATION_STATUS_ACK_SENT_AWAITING_BACKEND )
                                || ( conversationPojo.getStatus() == org.nexuse2e.Constants.CONVERSATION_STATUS_IDLE ) ) {
                            if ( conversationPojo.getCurrentAction().isEnd() ) {
                                conversationPojo.setStatus( org.nexuse2e.Constants.CONVERSATION_STATUS_COMPLETED );
                            } else {
                                conversationPojo.setStatus( org.nexuse2e.Constants.CONVERSATION_STATUS_IDLE );
                            }
                        } else if ( conversationPojo.getStatus() == org.nexuse2e.Constants.CONVERSATION_STATUS_AWAITING_BACKEND ) {
                            conversationPojo
                                    .setStatus( org.nexuse2e.Constants.CONVERSATION_STATUS_BACKEND_SENT_SENDING_ACK );
                        } else {
                            LOG.error( new LogMessage( "Unexpected conversation state detected: "
                                    + conversationPojo.getStatus(), messagePojo ) );
                        }

                        // Persist the message
                        try {
                            Engine.getInstance().getTransactionService().updateTransaction( conversationPojo );
                        } catch ( Exception e ) {
                            e.printStackTrace();
                            LOG.error( new LogMessage(
                                    "InboundQueueListener.run detected an exception when storing message ack status: "
                                            + e, messagePojo ) );
                        }
                    } // synchronized
                } catch ( NexusException nex ) {
                    LOG.error( "InboundQueueListener.run detected an exception: " + nex );
                    nex.printStackTrace();
                    synchronized ( messagePojo.getConversation() ) {

                        messagePojo.setStatus( org.nexuse2e.Constants.MESSAGE_STATUS_FAILED );
                        conversationPojo.setStatus( org.nexuse2e.Constants.CONVERSATION_STATUS_ERROR );
                        // Persist the message
                        try {
                            Engine.getInstance().getTransactionService().updateTransaction(
                                    messagePojo.getConversation() );
                        } catch ( Exception e ) {
                            e.printStackTrace();
                            LOG.error( new LogMessage(
                                    "InboundQueueListener.run detected an exception when storing message error status: "
                                            + e, messagePojo ) );
                        }
                    }
                } catch ( InterruptedException ex ) {
                    LOG.debug( new LogMessage( "Interrupted while listening on queue ", messagePojo ) );
                }
            } // while
            LOG.info( new LogMessage( "Stopped InboundQueueListener " + FrontendActionSerializer.this.choreographyId,
                    messagePojo ) );
            stopRequested = false;
        } // run

    } // InboundQueueListener

} // FrontendActionSerializer
