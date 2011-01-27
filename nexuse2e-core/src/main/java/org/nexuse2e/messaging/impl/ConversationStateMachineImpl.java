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
package org.nexuse2e.messaging.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.apache.log4j.Logger;
import org.nexuse2e.Constants;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.controller.StateTransitionException;
import org.nexuse2e.dao.UpdateTransactionOperation;
import org.nexuse2e.logging.LogMessage;
import org.nexuse2e.messaging.ConversationStateMachine;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.messaging.StateTransitionJob;
import org.nexuse2e.messaging.StatusUpdateSerializer;
import org.nexuse2e.pojo.ConversationPojo;
import org.nexuse2e.pojo.MessagePojo;

/**
 * Default implementation for {@link ConversationStateMachine}
 *
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public class ConversationStateMachineImpl implements ConversationStateMachine {

    private static Logger    LOG = Logger.getLogger( ConversationStateMachineImpl.class );

    private ConversationPojo conversation;
    private MessagePojo      message;
    private boolean          reliable;
    
    private Map<StateTransition,Queue<StateTransitionJob>> stateTransitionActions = new HashMap<StateTransition,Queue<StateTransitionJob>>();
    
    /**
     * Default constructor.
     */
    public ConversationStateMachineImpl() {
        super();
    }
    
    public void initialize(ConversationPojo conversation, MessagePojo message, boolean reliable) {
        this.conversation = conversation;
        this.message = message;
        this.reliable = reliable;
    }

    public void registerStateTransitionJob( StateTransition transition, StateTransitionJob action ) {
        synchronized ( stateTransitionActions ) {
            Queue<StateTransitionJob> queue = stateTransitionActions.get( transition );
            if ( queue == null ) {
                queue = new LinkedList<StateTransitionJob>();
                stateTransitionActions.put( transition, queue );
            }
            queue.add( action );
        } 
    }
    
    protected void executeStateTransitionJobs( StateTransition transition ) {
        synchronized ( stateTransitionActions ) {
            Queue<StateTransitionJob> queue = stateTransitionActions.get( transition );
            if ( queue != null ) {
                while( !queue.isEmpty() ) {
                    StateTransitionJob job = queue.poll();
                    if ( job != null ) {
                        job.execute();
                    }
                }
            }
        } 
    }

    public ConversationPojo getConversation() {

        return conversation;
    }
    
    public MessagePojo getAckForMessage( MessagePojo message ) {
        if (message != null || conversation != null) {
        
            for (MessagePojo ack : conversation.getMessages()) {
                if (ack.isAck() && ack.getReferencedMessage() != null &&
                        (ack.getReferencedMessage().getMessageId() == message.getMessageId() ||
                                (message.getMessageId() != null && message.getMessageId().equals( ack.getReferencedMessage().getMessageId() )))) {
                    return ack;
                }
            }
        }

        return null;
    }

    public void sentMessage() throws StateTransitionException, NexusException {

        UpdateTransactionOperation operation = new UpdateTransactionOperation() {
            public UpdateScope update(ConversationPojo conversation, MessagePojo message, MessagePojo referencedMessage) throws NexusException, StateTransitionException {
                if (message.isNormal()) {
                    LOG.trace(new LogMessage("message sent, current conversation status: " + conversation.getStatusName(), message));
                    if (conversation.getStatus() == Constants.CONVERSATION_STATUS_PROCESSING
                            || conversation.getStatus() == Constants.CONVERSATION_STATUS_AWAITING_ACK
                            || conversation.getStatus() == Constants.CONVERSATION_STATUS_IDLE /* If ack from previous choreo step was late */
                            || conversation.getStatus() == Constants.CONVERSATION_STATUS_ACK_SENT_AWAITING_BACKEND /* If ack from previous choreo step was late */
                            || conversation.getStatus() == Constants.CONVERSATION_STATUS_BACKEND_SENT_SENDING_ACK /* If ack from previous choreo step was late */
                            || conversation.getStatus() == Constants.CONVERSATION_STATUS_ERROR /* Included for re-queuing */) {
                        if (reliable) {
                            // it can happen that an outbound acknowledgement was processed faster than the inbound message
                            LOG.trace(new LogMessage("conversation status set to awaiting ack",message));
                            conversation.setStatus(Constants.CONVERSATION_STATUS_AWAITING_ACK);
                            return UpdateScope.CONVERSATION_ONLY;
                        } else {
                            Engine.getInstance().getTransactionService().deregisterProcessingMessage(message.getMessageId());
                            LOG.trace(new LogMessage("message status set to sent",message));
                            message.setStatus(Constants.MESSAGE_STATUS_SENT);
                            message.setModifiedDate(new Date());
                            message.setEndDate(message.getModifiedDate());
                            if (message.getAction().isEnd()) {
                                LOG.trace(new LogMessage("conversation status set to completed",message));
                                conversation.setStatus(Constants.CONVERSATION_STATUS_COMPLETED);
                                conversation.setEndDate(new Date());
                            } else {
                                LOG.trace(new LogMessage("conversation status set to idle",message));
                                conversation.setStatus(Constants.CONVERSATION_STATUS_IDLE);
                            }
                            return UpdateScope.CONVERSATION_AND_MESSAGE;
                        }
                    } else {
                        throw new StateTransitionException( "Unexpected conversation state after sending normal message: "
                                + ConversationPojo.getStatusName( conversation.getStatus() ) );
                    }
                } else {
                    // Engine.getInstance().getTransactionService().deregisterProcessingMessage( message.getMessageId() );
                    message.setStatus( Constants.MESSAGE_STATUS_SENT );
                    message.setModifiedDate( new Date() );
                    message.setEndDate( message.getModifiedDate() );
                    referencedMessage.setEndDate( message.getModifiedDate() );
                    if (conversation.getStatus() == Constants.CONVERSATION_STATUS_SENDING_ACK
                            || conversation.getStatus() == Constants.CONVERSATION_STATUS_PROCESSING) {
                        conversation.setStatus( Constants.CONVERSATION_STATUS_ACK_SENT_AWAITING_BACKEND );
                    } else if (conversation.getStatus() == Constants.CONVERSATION_STATUS_BACKEND_SENT_SENDING_ACK
                            || conversation.getStatus() == Constants.CONVERSATION_STATUS_IDLE
                            || conversation.getStatus() == Constants.CONVERSATION_STATUS_ACK_SENT_AWAITING_BACKEND) {
                        if (message.getAction().isEnd()) {
                            conversation.setStatus( Constants.CONVERSATION_STATUS_COMPLETED );
                            conversation.setEndDate( new Date() );
                        } else {
                            conversation.setStatus( Constants.CONVERSATION_STATUS_IDLE );
                        }
                    } else if (conversation.getStatus() == Constants.CONVERSATION_STATUS_AWAITING_BACKEND) {
                        LOG.debug(new LogMessage( "Received ack message, backend still processing - conversation ID: " + conversation.getConversationId(), message));
                    } else if (conversation.getStatus() != Constants.CONVERSATION_STATUS_COMPLETED && conversation.getStatus() != Constants.CONVERSATION_STATUS_ERROR) {
                        LOG.error(new LogMessage( "Unexpected conversation state after sending ack message: " + conversation.getStatusName(), message));
                    }
                    return UpdateScope.ALL;
                }
            }
            
        };
        
        // Persist status changes
        try {
            Engine.getInstance().getTransactionService().updateTransaction(message, operation);
        } catch (StateTransitionException stex) {
            LOG.warn(new LogMessage(stex.getMessage(), message), stex);
        }
        
        // execute state transition jobs
        executeStateTransitionJobs( StateTransition.SENT_MESSAGE );
    }

    public void receivedRequestMessage() throws StateTransitionException, NexusException {

        message.setStatus( Constants.MESSAGE_STATUS_SENT );
        message.setModifiedDate( new Date() );
        message.setEndDate( message.getModifiedDate() );
        conversation.setStatus( Constants.CONVERSATION_STATUS_PROCESSING );
        if ( message.getNxMessageId() <= 0 ) {
            conversation.addMessage( message );
        }
        if ( conversation.getNxConversationId() <= 0 ) {
            Engine.getInstance().getTransactionService().storeTransaction( conversation, message );
        } else {
            Engine.getInstance().getTransactionService().updateTransaction( message );
        }
        
        // execute state transition jobs
        executeStateTransitionJobs( StateTransition.RECEIVED_REQUEST );
    }

    public void receivedNonReliableMessage() throws StateTransitionException, NexusException {

        UpdateTransactionOperation operation = new UpdateTransactionOperation() {
            public UpdateScope update(ConversationPojo conversation, MessagePojo message, MessagePojo referencedMessage) throws NexusException, StateTransitionException {
                if (message.getAction().isEnd()) {
                    conversation.setEndDate( new Date() );
                    conversation.setStatus( Constants.CONVERSATION_STATUS_COMPLETED );
                } else {
                    conversation.setStatus( Constants.CONVERSATION_STATUS_IDLE );
                }
                
                return UpdateScope.CONVERSATION_ONLY;
            }
            
        };
        // Persist status changes
        Engine.getInstance().getTransactionService().updateTransaction(message, operation);
        
        // execute state transition jobs
        executeStateTransitionJobs( StateTransition.RECEIVED_NON_RELIABLE_MESSAGE );
    }

    public void receivedAckMessage() throws StateTransitionException, NexusException {

        UpdateTransactionOperation operation = new UpdateTransactionOperation() {
            public UpdateScope update(ConversationPojo conversation, MessagePojo message, MessagePojo referencedMessage) throws NexusException, StateTransitionException {
                if (referencedMessage == null) {
                    throw new NexusException( "Error using referenced message on acknowledgment (ack message ID: " + message.getMessageId() + ")" );
                }
                LOG.trace(new LogMessage("receiving ack, current conversation status: " + conversation.getStatusName(), referencedMessage));
                if (conversation.getStatus() == Constants.CONVERSATION_STATUS_AWAITING_ACK
                        || conversation.getStatus() == Constants.CONVERSATION_STATUS_PROCESSING
                        || conversation.getStatus() == Constants.CONVERSATION_STATUS_ACK_SENT_AWAITING_BACKEND // if OrderResponse was faster than backend return
                        || conversation.getStatus() == Constants.CONVERSATION_STATUS_BACKEND_SENT_SENDING_ACK // if OrderResponse was faster than ack status update
                        || conversation.getStatus() == Constants.CONVERSATION_STATUS_ERROR
                        || conversation.getStatus() == Constants.CONVERSATION_STATUS_IDLE) {
                    if (referencedMessage.getAction().isEnd()) {
                        LOG.trace( new LogMessage( "conversation status set to completed", referencedMessage ) );
                        conversation.setEndDate( new Date() );
                        conversation.setStatus( Constants.CONVERSATION_STATUS_COMPLETED );
                    } else {
                        LOG.trace( new LogMessage( "conversation status set to idle", referencedMessage ) );
                        conversation.setStatus( Constants.CONVERSATION_STATUS_IDLE );
                    }
                    LOG.trace( new LogMessage( "ref message status set to sent", referencedMessage ) );
                    referencedMessage.setStatus( Constants.MESSAGE_STATUS_SENT );

                    // Complete ack message and add to conversation
                    Date endDate = new Date();
                    message.setAction( referencedMessage.getAction() );
                    message.setStatus( Constants.MESSAGE_STATUS_SENT );
                    message.setModifiedDate( endDate );
                    message.setEndDate( endDate );
                    conversation.addMessage( message );
                    referencedMessage.setModifiedDate( endDate );
                    referencedMessage.setEndDate( endDate );
                } else {
                    throw new StateTransitionException(
                            "Ack message received where it was not expected: Referenced message id is "
                                    + referencedMessage.getMessageId() + ", status was "
                                    + referencedMessage.getStatusName()
                                    + ", conversation status is "
                                    + conversation.getStatusName() );
                }
                return UpdateScope.ALL;
            }
        };
        Engine.getInstance().getTransactionService().updateTransaction(message, operation);
        
        // execute state transition jobs
        executeStateTransitionJobs( StateTransition.RECEIVED_ACK );
    }

    public void receivedErrorMessage() throws StateTransitionException, NexusException {

        UpdateTransactionOperation operation = new UpdateTransactionOperation() {
            public UpdateScope update(ConversationPojo conversation, MessagePojo message, MessagePojo referencedMessage) throws NexusException, StateTransitionException {
                if (referencedMessage == null) {
                    throw new NexusException("Error using referenced message on negative acknowledgment (error message ID: " + message.getMessageId() + ")" );
                }
                
                conversation.setStatus( Constants.CONVERSATION_STATUS_ERROR );
                referencedMessage.setStatus( Constants.MESSAGE_STATUS_FAILED );

                // Complete error message and add to conversation
                Date endDate = new Date();
                message.setAction( referencedMessage.getAction() );
                message.setStatus( Constants.MESSAGE_STATUS_SENT );
                message.setModifiedDate( endDate );
                message.setEndDate( endDate );
                conversation.addMessage( message );
                referencedMessage.setModifiedDate( endDate );
                referencedMessage.setEndDate( endDate );
                
                return UpdateScope.ALL;
            }
            
        };
        try {
            Engine.getInstance().getTransactionService().updateTransaction(message, operation);
        } catch ( StateTransitionException stex ) {
            LOG.warn( stex.getMessage() );
        }

        // execute state transition jobs
        executeStateTransitionJobs( StateTransition.RECEIVED_ERROR );
    }

    public void processedBackend() throws StateTransitionException, NexusException {

        UpdateTransactionOperation operation = new UpdateTransactionOperation() {
            public UpdateScope update(ConversationPojo conversation, MessagePojo message, MessagePojo referencedMessage) throws NexusException, StateTransitionException {
                message.setStatus( Constants.MESSAGE_STATUS_SENT );
                message.setModifiedDate( new Date() );
                message.setEndDate( message.getModifiedDate() );
                
                if ( LOG.isTraceEnabled() ) {
                    LOG.trace( new LogMessage("evaluating followup, current status: " + message.getStatusName() + "/" + conversation.getStatusName(), message) );
                }
                MessagePojo ack = getAckForMessage( message );
                
                if (conversation.getStatus() == Constants.CONVERSATION_STATUS_ACK_SENT_AWAITING_BACKEND
                        || conversation.getStatus() == Constants.CONVERSATION_STATUS_ERROR // requeued message
                        || (ack != null && ack.getStatus() == Constants.MESSAGE_STATUS_SENT) // requeued message, check for completed ack added
                        || conversation.getStatus() == Constants.CONVERSATION_STATUS_IDLE) {
                    if ( message.getAction().isEnd() ) {
                        conversation.setStatus( Constants.CONVERSATION_STATUS_COMPLETED );
                        conversation.setEndDate( new Date() );
                    } else {
                        conversation.setStatus( Constants.CONVERSATION_STATUS_IDLE );
                    }
                } else if ( conversation.getStatus() == Constants.CONVERSATION_STATUS_AWAITING_BACKEND
                        || conversation.getStatus() == Constants.CONVERSATION_STATUS_PROCESSING ) {
                    conversation.setStatus( Constants.CONVERSATION_STATUS_BACKEND_SENT_SENDING_ACK );
                } else if ( conversation.getStatus() == Constants.CONVERSATION_STATUS_COMPLETED ) {
                    LOG.debug( new LogMessage( "Processing message for completed conversation.", message ) );
                    return UpdateScope.MESSAGE_ONLY;
                } else {
                    LOG.error( new LogMessage( "Unexpected conversation state detected: " + conversation.getStatusName(), message ) );
                    return UpdateScope.MESSAGE_ONLY;
                }
                return UpdateScope.CONVERSATION_AND_MESSAGE;
            }
        };

        // Persist the message
        LOG.trace(new LogMessage("Persisting status: " + message.getStatusName() + "/" + conversation.getStatusName(), message));
        Engine.getInstance().getTransactionService().updateTransaction(message, operation);
        
        // execute state transition jobs
        executeStateTransitionJobs( StateTransition.PROCESSED_BACKEND );
    }

    public void processingFailed() throws StateTransitionException, NexusException {

        UpdateTransactionOperation operation = new UpdateTransactionOperation() {
            public UpdateScope update(ConversationPojo conversation, MessagePojo message, MessagePojo referencedMessage) throws NexusException, StateTransitionException {
                if (conversation.getStatus() == Constants.CONVERSATION_STATUS_COMPLETED) {
                    throw new StateTransitionException( "Conversation " + conversation.getConversationId() + " cannot be set from status " 
                             + ConversationPojo.getStatusName( conversation.getStatus() ) + " to status "
                             + ConversationPojo.getStatusName( Constants.CONVERSATION_STATUS_ERROR ) );
                }

                message.setStatus( Constants.MESSAGE_STATUS_FAILED );
                conversation.setStatus( Constants.CONVERSATION_STATUS_ERROR );
                
                return UpdateScope.CONVERSATION_AND_MESSAGE;
            }
            
        };

        // Persist the message
        Engine.getInstance().getTransactionService().updateTransaction(message, operation);

        // Trigger error status update
        MessageContext messageContext = new MessageContext();
        messageContext.setMessagePojo( new MessagePojo() );
        messageContext.setOriginalMessagePojo( messageContext.getMessagePojo() );
        messageContext.getMessagePojo().setReferencedMessage( message );
        messageContext.getMessagePojo().setType( org.nexuse2e.messaging.Constants.INT_MESSAGE_TYPE_ERROR );
        Engine.getInstance().getTransactionService().initializeMessage( messageContext.getMessagePojo(),
                Engine.getInstance().getIdGenerator( org.nexuse2e.Constants.ID_GENERATOR_MESSAGE ).getId(),
                conversation.getConversationId(), conversation.getCurrentAction().getName(),
                conversation.getPartner().getPartnerId(), conversation.getChoreography().getName() );
        messageContext.setConversation( conversation );
        StatusUpdateSerializer statusUpdateSerializer = Engine.getInstance().getCurrentConfiguration()
                .getStatusUpdateSerializers().get(
                        messageContext.getMessagePojo().getConversation().getChoreography().getName() );
        if ( statusUpdateSerializer != null ) {
            // Forward message to StatusUpdateSerializer for further processing/queueing
            statusUpdateSerializer.processMessage( messageContext );
        }
        
        // execute state transition jobs
        executeStateTransitionJobs( StateTransition.PROCESSING_FAILED );
    }

    public void queueMessage() throws StateTransitionException, NexusException {

        queueMessage( false );
    }

    public void queueMessage( final boolean force ) throws StateTransitionException, NexusException {

        LOG.trace( new LogMessage( "current message status: " + message.getStatusName(), message ) );

        UpdateTransactionOperation operation = new UpdateTransactionOperation() {
            public UpdateScope update(ConversationPojo conversation, MessagePojo message, MessagePojo referencedMessage) throws NexusException, StateTransitionException {
                
                boolean updateConv = performChoreograhpyTransition(message, conversation, force);
                boolean updateMsg = false;
                if (message.getStatus() != Constants.MESSAGE_STATUS_SENT) {
                    updateConv = true;
                    updateMsg = true;
                    message.setStatus( Constants.MESSAGE_STATUS_QUEUED );
                    message.setModifiedDate( new Date() );

                    if ( message.isNormal() ) {
                        conversation.setStatus( Constants.CONVERSATION_STATUS_PROCESSING );
                    }
                    conversation.addMessage( message );
                }
                if (updateMsg && updateConv) {
                    return UpdateScope.CONVERSATION_AND_MESSAGE;
                } else if (updateMsg) {
                    return UpdateScope.MESSAGE_ONLY;
                } else if (updateConv) {
                    return UpdateScope.CONVERSATION_ONLY;
                } else {
                    return UpdateScope.NOTHING;
                }
            }
        };
        
        Engine.getInstance().getTransactionService().updateTransaction(message, operation, force);

        // execute state transition jobs
        executeStateTransitionJobs( StateTransition.QUEUE_MESSAGE );
    }

    protected boolean isGeneralTransitionRuleMet( int conversationStatus ) {
        return conversationStatus == Constants.CONVERSATION_STATUS_IDLE
                || conversationStatus == Constants.CONVERSATION_STATUS_COMPLETED;
    }
    
    protected boolean isSpecialOutboundTransitionRuleMet( int conversationStatus ) {
        // we allow PROCESSING for the following szenario (by example OrderCreate/OrderResponse):
        //    - inbound OrderCreate is sent to backend
        //    - backend blocks HTTP call and sends OrderResponse in the meantime
        //    - conversation is still in PROCESSING for the OrderCreate ACK message
        return conversationStatus == Constants.CONVERSATION_STATUS_ACK_SENT_AWAITING_BACKEND
                || conversationStatus == Constants.CONVERSATION_STATUS_AWAITING_BACKEND
                || conversationStatus == Constants.CONVERSATION_STATUS_BACKEND_SENT_SENDING_ACK
                || conversationStatus == Constants.CONVERSATION_STATUS_PROCESSING;
    }
    
    protected boolean isSpecialInboundTransitionRuleMet( int conversationStatus ) {
        return conversationStatus == Constants.CONVERSATION_STATUS_PROCESSING;
    }

    
    protected boolean performChoreograhpyTransition(MessagePojo message, ConversationPojo conversation, boolean force) throws NexusException {

        String currentActionId = message.getAction().getName();

        // Check for conversation lock in order to synchronize parallel processes on the same conversation

        // check business transition
        if ( message.isNormal() ) {
            // for new conversations allow all start actions
            if ( conversation.getCurrentAction() == null ) {
                if ( force || message.getAction().isStart() ) {
                    conversation.setCurrentAction( message.getAction() );
                    return true;
                }
            } else if (force
                        || isGeneralTransitionRuleMet( conversation.getStatus() )
                        || ( message.isOutbound() && isSpecialOutboundTransitionRuleMet( conversation.getStatus() ) )
                        || ( !message.isOutbound() && isSpecialInboundTransitionRuleMet( conversation.getStatus() ) )) {
                // follow-up message in conversation. Checking state machine status.
                if (force || conversation.getCurrentAction().hasFollowUpAction( currentActionId )) {
                    conversation.setCurrentAction( message.getAction() );
                    LOG.info(message);
                    return true;
                } else {
                    LOG.debug(new LogMessage("No follow-up action " + conversation.getCurrentAction().getName() + " found for " + currentActionId));
                }
                
                // It is possible that the conversation.getCurrentAction() returns the previous action.
                // This happens if the previous action's inbound message is still being processed.
                // In this case, we need to allow the transition. The worker will check for such a condition
                // and bring the message sending queue in the correct order
                if (message.isOutbound()) {
                    // check if current action has QUEUED ACK
                    for (MessagePojo mp : message.getConversation().getMessages()) {
                        if (!mp.isAck() && mp.getStatus() == Constants.MESSAGE_STATUS_QUEUED &&
                                mp.getAction().hasFollowUpAction(currentActionId)) {
                            // found queued inbound message that must be processed before this (non-ack) message
                            // we don't set the current action, since the message processing worker will bring it into correct order
                            return false;
                        }
                    }
                }
            }
        } else {
            return false;
        }

        if (!force) {
            String prevActionId = conversation.getCurrentAction() != null ? conversation.getCurrentAction().getName() : null;
            throw new NexusException(
                    new LogMessage("Choreography (business) transition from " + prevActionId + " to " +
                            currentActionId + " not allowed for " + conversation.getStatusName() + " conversation", message));
        }
        
        return false;
    } // validateTransition

    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append( "\n" );
        sb.append( "- - 8< - -" );
        sb.append( "\n" );
        sb.append( "State machine dump of conversation " + conversation.getConversationId() + " in context of message " + message.toString() );
        sb.append( "\n" );
        sb.append( "Status: " + ConversationPojo.getStatusName( conversation.getStatus() ) );
        sb.append( "\n" );
        List<MessagePojo> messages = conversation.getMessages();
        if ( messages != null ) {
            sb.append( "Number of messages: " + messages.size() );
            sb.append( "\n" );
            if ( messages.size() > 0 ) {
                sb.append( "Messages: ");
                sb.append( "\n" );
                try {
                    for ( int i = 0; i < messages.size(); i++ ) {
                        MessagePojo currMsg = messages.get( i );
                        sb.append( "\t#" + ( i + 1 ) + "\t" );
                        sb.append( currMsg.toString() );
                        sb.append( "\n" );
                    }
                } catch ( IndexOutOfBoundsException e ) {
                    sb.append( "List of messages possibly not complete, because of concurrent modification" );
                    sb.append( "\n" );
                }
            }
        } else {
            sb.append( "List of messages is null" );
            sb.append( "\n" );
        }
        sb.append( "- - >8 - -" );
        return sb.toString();
    }
}
