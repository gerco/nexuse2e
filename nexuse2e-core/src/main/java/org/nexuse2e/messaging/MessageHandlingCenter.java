/**
 * 
 */
package org.nexuse2e.messaging;

import org.apache.log4j.Logger;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.controller.StateTransitionException;
import org.nexuse2e.logging.LogMessage;
import org.nexuse2e.pojo.ConversationPojo;

/**
 * Central entity that organizes serialized processing of inbound, and outbound
 * messages that belong to the same conversation. All conversations will be
 * processed in parallel.
 * 
 * @author sschulze
 */
public class MessageHandlingCenter implements MessageProcessor {

    private static final Logger LOG = Logger.getLogger( MessageHandlingCenter.class );

    private static MessageHandlingCenter instance;

    private MessageHandlingCenter() {
        super();
    }

    /**
     * Singleton instance.
     * 
     * @return singleton instance.
     */
    public static synchronized MessageHandlingCenter getInstance() {
        if ( instance == null ) {
            instance = new MessageHandlingCenter();
        }
        return instance;
    }

    /**
     * Announces the queuing of a message without appending it to the queue.
     * I.e. only the state machine transition is performed, and the
     * {@link MessageContext} gets persisted. See also
     * {@link MessageHandlingCenter#processMessage(MessageContext)}.
     * 
     * @param messageContext
     * @return The given reference from the input parameter. It's state will
     *         most likely be modified during state machine transition.
     * @throws IllegalArgumentException
     * @throws IllegalStateException
     * @throws NexusException
     */
    public MessageContext announceQueuing( MessageContext messageContext ) throws IllegalArgumentException,
                                                                          IllegalStateException, NexusException {
        // validate business transition
        ConversationPojo conversationPojo = null;
        try {
            conversationPojo = new StateMachineExecutor().validateTransition( messageContext );
        } catch ( NexusException e ) {
            LOG.error( new LogMessage( "Not a valid action: " + messageContext.getMessagePojo().getAction(),
                    messageContext.getMessagePojo() ), e );
            throw e;
        }

        if ( conversationPojo == null ) {
            throw new NexusException( "Choreography (business) transition not allowed at this time - message ID: "
                    + messageContext.getStateMachine() );
        }

        // persist and indicate processing state (technical transition)
        performQueuedTransition( messageContext, false );

        return messageContext;
    }

    /**
     * Queues the given message for inbound, or outbound processing. I.e. the
     * state machine transition is performed, the {@link MessageContext} gets
     * persisted, and the message gets appended to the queue. If a message is
     * already in the QUEUED state (most likely if
     * {@link MessageContext#announceQueuing(MessageContext)} was called
     * before), the state machine transition will not be triggered again.
     * 
     * @param messageContext
     * @return The given reference from the input parameter. It's state will
     *         most likely be modified during state machine transition.
     * @throws IllegalArgumentException
     * @throws IllegalStateException
     * @throws NexusException
     */
    public MessageContext processMessage( MessageContext messageContext ) throws IllegalArgumentException,
                                                                         IllegalStateException, NexusException {

        if ( messageContext.getMessagePojo().getStatus() != Constants.MESSAGE_STATUS_QUEUED ) {
            // validate business transition
            ConversationPojo conversationPojo = null;
            try {
                conversationPojo = new StateMachineExecutor().validateTransition( messageContext );
            } catch ( NexusException e ) {
                LOG.error( new LogMessage( "Not a valid action: " + messageContext.getMessagePojo().getAction(),
                        messageContext.getMessagePojo() ), e );
                throw e;
            }

            if ( conversationPojo == null ) {
                throw new NexusException( "Choreography (business) transition not allowed at this time - message ID: "
                        + messageContext.getStateMachine() );
            }

            // persist and indicate processing state (technical transition)
            performQueuedTransition( messageContext, false );
        }

        queue( messageContext );

        return messageContext;
    }

    /**
     * Performs the transition of the (technical) conversation state machine to
     * queued state.
     * 
     * @param messageContext
     * @param force
     * @throws NexusException
     */
    protected void performQueuedTransition( MessageContext messageContext, boolean force ) throws NexusException {
        try {
            messageContext.getStateMachine().queueMessage( false );
        } catch ( StateTransitionException e ) {
            LOG.warn( new LogMessage( e.getMessage(), messageContext ) );
        }
    }

    /**
     * Append the message to the queue, if the participant's connection is not marked as hold.
     * "Hold" connections indicate that the participant polls outbound messages.
     * So we do not queue them for sending. They just remain in the database as QUEUED until the participant issues a request.
     * @param messageContext
     */
    protected void queue( MessageContext messageContext ) {
        if ( !( messageContext.getMessagePojo().isOutbound() && messageContext.getParticipant().getConnection()
                .isHold() ) ) {
            ConversationWorker.queue( messageContext );
        }
    }

    /**
     * Gathers a {@link MessageContext} from the persistent store, and
     * initializes a re-queue.
     * 
     * @param choreographyId
     * @param participantId
     * @param conversationId
     * @param messageId
     * @throws NexusException
     */
    public void requeueMessage( String choreographyId, String participantId, String conversationId, String messageId )
                                                                                                                      throws NexusException {

        MessageContext messageContext = Engine.getInstance().getTransactionService().getMessageContext( messageId );
        requeueMessage( messageContext );
    }

    /**
     * Re-queues a message.
     * 
     * @param messageContext
     * @throws NexusException
     */
    public void requeueMessage( MessageContext messageContext ) throws NexusException {

        if ( messageContext != null ) {
            // if message is processing, cancel it
            String messageId = messageContext.getMessagePojo().getMessageId();
            if ( Engine.getInstance().getTransactionService().isProcessingMessage( messageId ) ) {
                Engine.getInstance().getTransactionService().deregisterProcessingMessage( messageId );
            }
            // set message end date to null, otherwise it's processing may be
            // cancelled
            if ( messageContext.getMessagePojo() != null ) {
                messageContext.getMessagePojo().setEndDate( null );
            }

            if ( LOG.isDebugEnabled() ) {
                LOG.debug( new LogMessage( "Re-queuing message " + messageContext.toString() ) );
            }

            // queue message
            performQueuedTransition( messageContext, true );
            queue( messageContext );
        } else {
            LOG.error( "Cannot requeue message with messageContext = null" );
        }
    }
}