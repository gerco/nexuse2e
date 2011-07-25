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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;
import org.nexuse2e.ActionSpecificKey;
import org.nexuse2e.Engine;
import org.nexuse2e.Manageable;
import org.nexuse2e.NexusException;
import org.nexuse2e.Constants.BeanStatus;
import org.nexuse2e.Constants.Layer;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.logging.LogMessage;
import org.nexuse2e.pojo.ActionPojo;
import org.nexuse2e.pojo.ChoreographyPojo;
import org.nexuse2e.pojo.ConversationPojo;
import org.nexuse2e.pojo.MessagePojo;

/**
 * Component in the NEXUSe2e frontend that serializes the processing of inbound messages per Choreography. 
 * Messages are placed in a queue and are processed by a single thread in fifo order.
 *
 * @author mbreilmann
 */
public class StatusUpdateSerializer implements Manageable {

    private static Logger                           LOG                       = Logger
                                                                                      .getLogger( StatusUpdateSerializer.class );

    private String                                  choreographyId            = null;

    private BackendInboundDispatcher                backendInboundDispatcher  = null;

    private ChoreographyValidator                    stateMachineExecutor      = null;

    private BlockingQueue<MessageContext>           queue                     = new LinkedBlockingQueue<MessageContext>();

    private String                                  queueName                 = "queue_name_not_set";

    private BeanStatus                              status                    = BeanStatus.UNDEFINED;

    private StatusUpdateQueueListener               statusUpdateQueueListener = null;

    private Thread                                  queueListenerThread       = null;

    private Map<ActionSpecificKey, BackendPipeline> statusUpdatePipelines     = new HashMap<ActionSpecificKey, BackendPipeline>();

    /**
     * @param choreographyId
     */
    public StatusUpdateSerializer( String choreographyId ) {

        this.choreographyId = choreographyId;
        queueName = choreographyId + Constants.POSTFIX_STATUS_UPDATE_SERIALIZER;

    } // constructor

    /**
     * @param messageContext
     * @return
     */
    public MessageContext processMessage( MessageContext messageContext ) throws NexusException {

        LOG.debug(new LogMessage( "StatusUpdateSerializer.processMessage - " + choreographyId,messageContext.getMessagePojo()) );

        if ( status == BeanStatus.STARTED ) {

            queue.add( messageContext );
        } else {
            LOG.error( new LogMessage( "Received message for StatusUpdateSerializer (" + choreographyId
                    + ") which hasn't been properly started!", messageContext.getMessagePojo() ) );
        }

        return messageContext;
    } // processMessage

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
            statusUpdateQueueListener.setStopRequested( true );
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

        LOG.trace( "StatusUpdateSerializer.activate - " + choreographyId );

        if ( status == BeanStatus.INITIALIZED ) {
            queueListenerThread = new Thread( statusUpdateQueueListener, queueName );
            queueListenerThread.start();
            status = BeanStatus.STARTED;
        } else {
            LOG.error( "Trying to start uninitialized StatusUpdateSerializer (" + choreographyId + ")!" );
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

        LOG.trace( "Initializing StatusUpdateSerializer " + choreographyId );

        backendInboundDispatcher = config.getStaticBeanContainer().getBackendInboundDispatcher();

        stateMachineExecutor = config.getStaticBeanContainer().getFrontendInboundDispatcher();

        statusUpdateQueueListener = new StatusUpdateQueueListener();

        statusUpdatePipelines = config.getStatusUpdatePipelines();

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
                && ( statusUpdateQueueListener != null ) ) {
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
    private class StatusUpdateQueueListener implements Runnable {

        private boolean stopRequested = false;

        protected void setStopRequested( boolean stopRequested ) {

            this.stopRequested = stopRequested;
        }

        public void run() {

            MessageContext messageContext = null;

            while ( !stopRequested ) {
                try {
                    messageContext = queue.take();

                    LOG.debug( new LogMessage("###############  Status Update #################",messageContext.getMessagePojo()) );

                    if ( statusUpdatePipelines != null ) {
                        ActionPojo action = messageContext.getMessagePojo().getAction();
                        ConversationPojo conversation = messageContext.getMessagePojo().getConversation();
                        ChoreographyPojo choreography = ( conversation == null ? null : conversation.getChoreography() );
                        ActionSpecificKey actionSpecificKey = new ActionSpecificKey( ( action == null ? null : action
                                .getName() ), ( choreography == null ? null : choreography.getName() ) );
                        BackendPipeline statusUpdatePipeline = statusUpdatePipelines.get( actionSpecificKey );
                        if ( statusUpdatePipeline != null ) {
                            LOG.debug( new LogMessage( "Found pipeline: " + statusUpdatePipeline + " - "
                                    + actionSpecificKey, messageContext.getMessagePojo() ) );

                            // Clone MessagePojo so that Pipelets in the Pipeline can modify the message/payloads
                            try {
                                messageContext.setMessagePojo( (MessagePojo) messageContext.getMessagePojo().clone() );
                            } catch ( CloneNotSupportedException e ) {
                                LOG.error(new LogMessage( "Error cloning original MessagePojo!", messageContext.getMessagePojo()) );
                            }

                            try {
                                statusUpdatePipeline.processMessage( messageContext );
                            } catch ( NexusException e ) {
                                LOG.error( "Error processing status update message!" );
                            }
                        } else {
                            LOG.debug( new LogMessage("No status pipeline found for message: "
                                    + messageContext.getMessagePojo().getMessageId() + " ("
                                    + messageContext.getMessagePojo().getConversation().getChoreography().getName()
                                    + " - " + messageContext.getMessagePojo().getAction() + ")",messageContext.getMessagePojo()) );
                        }
                    } else {
                        LOG.debug( new LogMessage("No status update pipelines configured!",messageContext.getMessagePojo()) );
                    }

                } catch ( InterruptedException ex ) {
                    LOG.debug( new LogMessage( "Interrupted while listening on queue ", ( messageContext == null ? null
                            : messageContext.getMessagePojo() ) ) );
                }
            } // while
            LOG.info( new LogMessage( "Stopped InboundQueueListener (StatusUpdateSerializer) "
                    + StatusUpdateSerializer.this.choreographyId, ( messageContext == null ? null : messageContext
                    .getMessagePojo() ) ) );
            stopRequested = false;
        } // run

    } // InboundQueueListener

} // StatusUpdateSerializer
