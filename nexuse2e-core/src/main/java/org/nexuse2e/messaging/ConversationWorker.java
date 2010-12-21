/**
 * 
 */
package org.nexuse2e.messaging;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.controller.StateTransitionException;
import org.nexuse2e.logging.LogMessage;
import org.nexuse2e.messaging.sync.ConversationLockManager;

/**
 * 
 * @author sschulze
 *
 */
public class ConversationWorker implements Runnable {
    
    private static final Logger LOG = Logger.getLogger( ConversationWorker.class );
    
    private static Map<String,ConversationWorker> workers = new HashMap<String,ConversationWorker>();
    private static ExecutorService threadPool = Executors.newFixedThreadPool( 100 ); // TODO: Make this configurable

    private Queue<MessageContext> queue;
    private boolean stop;
    private String conversationId;
    
    private ConversationWorker( String conversationId ) {
        queue = new LinkedList<MessageContext>();
        stop = false;
        this.conversationId = conversationId;
    }
    
    public static void queue( MessageContext messageContext ) {
        synchronized ( workers ) {
            String conversationId = messageContext.getConversation().getConversationId(); 
            ConversationWorker worker = workers.get( conversationId );
            if ( worker == null ) {
                worker = new ConversationWorker( conversationId );
                workers.put( conversationId, worker );
                if ( LOG.isDebugEnabled() ) {
                    LOG.debug( new LogMessage( "Created new conversation worker", messageContext ) );
                }
                // TODO: review
                synchronized ( worker.queue ) {
                    // queue msg
                    if ( LOG.isDebugEnabled() ) {
                        LOG.debug( new LogMessage( "Queuing message", messageContext ) );
                    }
                    worker.queue.add( messageContext );
                    // init execution of worker
                    threadPool.submit( worker );
                }
            } else {
                synchronized ( worker.queue ) {
                    // queue only; worker is already running
                    if ( LOG.isDebugEnabled() ) {
                        LOG.debug( new LogMessage( "Queuing message", messageContext ) );
                    }
                    worker.queue.add( messageContext );
                }
            }
        }
    }

    public void run() {
        if ( LOG.isDebugEnabled() ) {
            LOG.debug( new LogMessage( "Started worker for conversation " + conversationId ) );
        }
        int counter = 0;
        while ( !stop ) {
            if ( LOG.isDebugEnabled() ) {
                LOG.debug( new LogMessage( "ConversationWorker (" + conversationId + ") iteration no. " + ( ++counter ) ) );
            }
            MessageContext messageContext = null;
            synchronized ( queue ) {
                messageContext = queue.poll();
            }
            if ( messageContext != null ) {
                // process
                if ( messageContext.getMessagePojo().isOutbound() ) {
                    // process outbound
                    processOutbound( messageContext );
                } else {
                    // process inbound
                    processInbound( messageContext );
                }
                // if conversation is completed, we don't need the worker anymore
                if ( messageContext.getConversation().getStatus() == Constants.CONVERSATION_STATUS_COMPLETED ) {
                    resign();
                }
            } else {
                // if something happens in the close future, we can keep this resource
                // the check for completed conversation was done after the previous message was processed
                try {
                    Thread.sleep( 1000 ); // TODO: Make this configurable in the GUI. Could that be the connection timeout?
                    resign();
                } catch ( InterruptedException e ) {
                    resign();
                }
                resign();
            }
        }
    }

    protected void resign() {
        synchronized ( workers ) {
            synchronized ( queue ) {
                if ( queue.isEmpty() ) {
                    stop = true;
                    workers.remove( conversationId );
                    if ( LOG.isDebugEnabled() ) {
                        LOG.debug( new LogMessage( "Worker for conversation " + conversationId + " resigned") );
                    }
                }
            }
        }
    }
    
    protected void processInbound( MessageContext messageContext ) {
        if ( LOG.isDebugEnabled() ) {
            LOG.debug( new LogMessage( "Processing inbound message: " + messageContext.getStateMachine().toString(), messageContext ) );
        }
        Object syncObj = Engine.getInstance().getTransactionService().getSyncObjectForConversation( messageContext.getConversation() );
        synchronized (syncObj) {
            // Initiate the backend process
            // We Synchronize the conversation so that -- with fast back-end systems -- response
            // messages don't get processed earlier than the state machine transition.
            // TODO: remove ConversationLockManager globally?
            ConversationLockManager conversationLockManager = Engine.getInstance().getTransactionService().getConversationLockManager();
            try {
                // lock conversation
                conversationLockManager.lock( messageContext.getConversation() );
                // do it
                
//                Engine.getInstance().getCurrentConfiguration().getStaticBeanContainer().getFrontendInboundDispatcher().processMessage( messageContext );
                
                Engine.getInstance().getCurrentConfiguration().getStaticBeanContainer().getBackendInboundDispatcher().processMessage( messageContext );
                messageContext.getStateMachine().processedBackend();
                
            } catch ( NexusException nex ) {
                LOG.error( "InboundQueueListener.run detected an exception: ", nex );
                try {
                    messageContext.getStateMachine().processingFailed();
                } catch (StateTransitionException e) {
                    LOG.warn( new LogMessage( e.getMessage(), messageContext ) );
                } catch (NexusException e) {
                    LOG.error( new LogMessage( "Error while setting conversation status to ERROR: "
                            + e.getMessage(), messageContext), e );
                }
            } catch (StateTransitionException stex) {
                LOG.warn( new LogMessage( stex.getMessage(), messageContext ) );
            } finally {
                // release conversation lock
                conversationLockManager.unlock( messageContext.getConversation() );
            }
        }
    }
    
    protected void processOutbound( MessageContext messageContext ) {
        if ( LOG.isDebugEnabled() ) {
            LOG.debug( new LogMessage( "Processing outbound message: " + messageContext.getStateMachine().toString(), messageContext ) );
        }
        try {
            
//            Engine.getInstance().getCurrentConfiguration().getStaticBeanContainer().getBackendOutboundDispatcher().processMessage( messageContext );
            
            if ( Engine.getInstance().getTransactionService().isSynchronousReply(
                    messageContext.getMessagePojo().getMessageId() ) ) {
                Engine.getInstance().getCurrentConfiguration().getStaticBeanContainer().getFrontendInboundDispatcher()
                        .processSynchronousReplyMessage( messageContext );
                Engine.getInstance().getTransactionService().removeSynchronousRequest(
                        messageContext.getMessagePojo().getMessageId() );
            } else {
                Engine.getInstance().getCurrentConfiguration().getStaticBeanContainer().getFrontendOutboundDispatcher().processMessage( messageContext );
            }
        } catch ( NexusException e ) {
            LOG.error( new LogMessage( "OutboundQueueListener.run() detected an exception: " + e.getMessage(), messageContext ), e );
        }
    }
}
