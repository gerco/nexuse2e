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

import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.ProtocolSpecificKey;
import org.nexuse2e.Constants.BeanStatus;
import org.nexuse2e.Constants.Layer;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.controller.StateTransitionException;
import org.nexuse2e.logging.LogMessage;
import org.nexuse2e.messaging.ebxml.v20.HeaderDeserializer;
import org.nexuse2e.pojo.ActionPojo;
import org.nexuse2e.pojo.ChoreographyPojo;
import org.nexuse2e.pojo.MessagePojo;
import org.nexuse2e.pojo.ParticipantPojo;
import org.springframework.beans.factory.InitializingBean;

/**
 * Component dispatching inbound messages to the correct queue based on their choreography (business process). 
 * Additionally the message is checked for consistency with the messaging protocol it belongs to and acknowledgements
 * or error messages are created accordingly if the underlying messaging protocol requires it.
 *
 * @author gesch
 */
public class FrontendInboundDispatcher extends StateMachineExecutor implements Dispatcher, Pipelet, InitializingBean {

    private static Logger                             LOG                       = Logger
                                                                                        .getLogger( HeaderDeserializer.class );

    private ProtocolAdapter[]                         protocolAdapters;

    private Map<String, FrontendActionSerializer>     frontendActionSerializers = new HashMap<String, FrontendActionSerializer>();

    private BeanStatus                                status                    = BeanStatus.UNDEFINED;

    private Hashtable<String, MessageContext>         synchronousReplies        = new Hashtable<String, MessageContext>();

    private Map<String, Object>                       parameters                = new HashMap<String, Object>();

    private boolean                                   frontendPipelet;
    private boolean                                   forwardPipelet;

    /**
     * temporary !!
     */
    private BackendOutboundDispatcher                 backendOutboundDispatcher = null;

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.FrontendDispatcher#processMessage(org.nexuse2e.messaging.MessageContext)
     */
    public MessageContext processMessage( MessageContext messageContext ) throws NexusException {

        boolean headerAccessible = false;
        boolean headerInvalid = false;
        ChoreographyPojo choreography = null;
        ParticipantPojo participant = null;
        MessageContext clonedMessageContext = null;
        MessageContext responseMessageContext = null;
        Vector<ErrorDescriptor> errorMessages = new Vector<ErrorDescriptor>();

        MessagePojo messagePojo = messageContext.getMessagePojo();

        LOG.trace( "Entering FrontendInboundDispatcher.processMessage..." );

        // extract header data

        if ( messagePojo.getConversation() != null
                && messagePojo.getConversation().getStatus() == org.nexuse2e.Constants.CONVERSATION_STATUS_CREATED ) {
            messagePojo.getConversation().setStatus( org.nexuse2e.Constants.CONVERSATION_STATUS_PROCESSING );
            LOG.debug( new LogMessage( "MessageType:" + messagePojo.getType(), messagePojo.getConversation()
                    .getConversationId(), messagePojo.getMessageId() ) );

        }

        ProtocolAdapter protocolAdapter = getProtocolAdapterByKey( messageContext.getProtocolSpecificKey() );
        if ( protocolAdapter == null ) {
            String msg = "No protocol implementation found for key: " + messageContext.getProtocolSpecificKey();
            LOG.error( new LogMessage( msg, messagePojo ) );
            throw new NexusException( msg );
        }
        LOG.trace( "ProtocolAdapter found for incoming message" );

        try {
            choreography = validateChoreography( messageContext, Constants.INBOUND );
            LOG.trace( "matching choreography found" );
        } catch ( NexusException e ) {
            e.printStackTrace();
            LOG.error( new LogMessage( "No matching choreography found: "
                    + messagePojo.getConversation().getChoreography().getName(), messagePojo ) );
            responseMessageContext = protocolAdapter.createErrorAcknowledgement(
                    Constants.ErrorMessageReasonCode.CHOREOGRAPHY_NOT_FOUND, null, messageContext, null );
            headerAccessible = true;
            errorMessages.add( new ErrorDescriptor( "No matching choreography found in configuration: "
                    + messagePojo.getConversation().getChoreography().getName() ) );
        }

        String actionId = messagePojo.getAction().getName();
        ActionPojo action = Engine.getInstance().getActiveConfigurationAccessService()
                .getActionFromChoreographyByActionId( choreography, actionId );
        if ( ( action == null ) && ( messagePojo.getType() == Constants.INT_MESSAGE_TYPE_NORMAL ) ) {
            headerAccessible = true;
            errorMessages.add( new ErrorDescriptor( "No matching action found in configuration: "
                    + messagePojo.getAction().getName() ) );
            responseMessageContext = protocolAdapter.createErrorAcknowledgement(
                    Constants.ErrorMessageReasonCode.ACTION_NOT_PERMITTED, null, messageContext, null );
        }

        if ( !headerAccessible ) {

            try {
                participant = validateParticipant( messageContext, Constants.INBOUND );
                LOG.trace( "matching participant found" );
            } catch ( NexusException e ) {
                e.printStackTrace();
                LOG.error( new LogMessage( "No matching participant found: "
                        + messagePojo.getConversation().getPartner().getPartnerId(), messagePojo ) );
                responseMessageContext = protocolAdapter.createErrorAcknowledgement(
                        Constants.ErrorMessageReasonCode.PARTICIPANT_NOT_FOUND, choreography, messageContext, null );
                headerAccessible = true;
                errorMessages.add( new ErrorDescriptor( "No matching participant found in configuration: "
                        + messagePojo.getConversation().getPartner().getPartnerId() ) );
            }
        }

        if ( headerAccessible ) {
            LOG.error( new LogMessage( "Error processing inbound message.", messagePojo ) );
            for (ErrorDescriptor errorDescriptor : errorMessages) {
                LOG.error( new LogMessage( "Error - " + errorDescriptor.getDescription(), messagePojo ) );
            }
            return null;
        }

        // header data are accessible, but may not be valid for the current conversation

        if ( messageContext.getErrors() != null && messageContext.getErrors().size() > 0 ) {
            //headerInvalid = true;

            messageContext.getMessagePojo().setStatus( Constants.MESSAGE_STATUS_FAILED );
            messageContext.getConversation().setStatus( Constants.CONVERSATION_STATUS_ERROR );
        }

        String msgType = null;
        switch ( messagePojo.getType() ) {
            case Constants.INT_MESSAGE_TYPE_ACK:
                msgType = "ack";
                break;
            case Constants.INT_MESSAGE_TYPE_ERROR:
                msgType = "error";
                break;
            default:
                msgType = "normal";
        }

        if ( messagePojo.getType() == Constants.INT_MESSAGE_TYPE_NORMAL ) {
            LOG.info( new LogMessage( "Received  " + msgType + " message (" + messagePojo.getMessageId() + ") from "
                    + participant.getPartner().getPartnerId() + " for " + choreography.getName() + "/"
                    + action.getName(), messagePojo ) );
        } else {
            LOG.info( new LogMessage( "Received  " + msgType + " message (" + messagePojo.getMessageId() + ") from "
                    + messagePojo.getParticipant().getPartner().getPartnerId() + " for " + choreography.getName() + "/"
                    + messagePojo.getConversation().getCurrentAction().getName(), messagePojo ) );
        }

        if ( messagePojo.getType() == Constants.INT_MESSAGE_TYPE_NORMAL ) {

            // Try to identify duplicate
            MessageContext duplicateMessageContext = Engine.getInstance().getTransactionService().getMessageContext(
                    messagePojo.getMessageId() );
            if ( duplicateMessageContext != null ) {
                LOG.info( "Received duplicate message: " + messagePojo.getMessageId() );
                responseMessageContext = Engine.getInstance().getTransactionService().getMessageContext(
                        messagePojo.getMessageId(), true );
            } else { // duplicate

                // Forward the message to check the transition, persist it and pass to backend
                FrontendActionSerializer frontendActionSerializer = frontendActionSerializers.get( messagePojo
                        .getConversation().getChoreography().getName() );
                if ( frontendActionSerializer != null ) {
                    try {
                        clonedMessageContext = (MessageContext) messageContext.clone();

                        // Forward message to FrontendActionSerializer for further processing/queueing
                        frontendActionSerializer.processMessage( messageContext );

                        // Block for synchronous processing
                        if ( participant.getConnection().isSynchronous() ) {
                            LOG.debug( new LogMessage( "Found synchronous connection setting.", messagePojo ) );
                            Engine.getInstance().getTransactionService().addSynchronousRequest(
                                    messagePojo.getMessageId() );
                            while ( responseMessageContext == null ) {
                                synchronized ( synchronousReplies ) {
                                    try {
                                        LOG.debug( "Waiting for reply..." );
                                        synchronousReplies.wait();
                                    } catch ( InterruptedException e ) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                }

                                responseMessageContext = synchronousReplies.get( messageContext.getMessagePojo()
                                        .getMessageId() );
                            }
                            LOG.debug( "Found reply: " + responseMessageContext );
                        }
                    } catch ( CloneNotSupportedException e ) {
                        e.printStackTrace();
                    } catch ( NexusException e ) {
                        LOG
                                .error( new LogMessage( "Error processing message: " + messagePojo.getMessageId()
                                        + " (" + messagePojo.getConversation().getChoreography().getName() + "/"
                                        + messagePojo.getConversation().getPartner().getPartnerId() + ") - " + e,
                                        messagePojo ) );
                        headerInvalid = true;
                    }
                } else {
                    LOG.error( new LogMessage( "No FrontendActionSerializer found: "
                            + messagePojo.getConversation().getChoreography().getName(), messagePojo ) );
                    headerInvalid = true;
                }

                // Asynchronous processing
                if ( !participant.getConnection().isSynchronous() ) {
                    if ( !headerInvalid && messageContext.getMessagePojo().getStatus() != Constants.MESSAGE_STATUS_FAILED) {
                        LOG.trace( "No error response message found, creating ack" );
                        responseMessageContext = null;
                        if ( messageContext.getParticipant().getConnection().isReliable() ) {
                            try {
                                responseMessageContext = protocolAdapter.createAcknowledgement( choreography,
                                        clonedMessageContext );
                            } catch ( NexusException e ) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        } else {
                            LOG.debug( "Not reliable, not creating ack - message ID: " + messagePojo.getMessageId() );
                            try {
                                messageContext.getStateMachine().receivedNonReliableMessage();
                            } catch (StateTransitionException e) {
                                LOG.warn( e.getMessage() );
                            }
                        }
                    } else {
                        LOG.trace( "error response message found" );
                        responseMessageContext = protocolAdapter.createErrorAcknowledgement(
                                Constants.ErrorMessageReasonCode.UNKNOWN, choreography, messageContext, errorMessages );
                    }
                }
            } // no duplicate

            if ( responseMessageContext == null ) {
                if ( messageContext.getParticipant().getConnection().isReliable() ) {
                    LOG.error( new LogMessage( "No dispatchable return message created", messagePojo ) );
                }
                return null;
            }

            // Send acknowledgment/error message back asynchronously
            if ( participant.getConnection().isSynchronous() ) {
                return responseMessageContext;
            } else if ( ( responseMessageContext != null ) && responseMessageContext.getMessagePojo() != null ) {
                try {
                    backendOutboundDispatcher.processMessage( responseMessageContext );
                    return null;
                } catch ( NexusException e ) {
                    LOG.error( new LogMessage( "Unable to process Acknowledgement:" + e.getMessage(), messagePojo ) );
                }
            } else {
                LOG.warn( "No message to send as reply." );
            }
        } else if ( messagePojo.getType() == Constants.INT_MESSAGE_TYPE_ACK ) {
            LOG.trace( "Ack detected" );

            // Try to identify duplicate
            MessageContext duplicateMessageContext = Engine.getInstance().getTransactionService().getMessageContext(
                    messagePojo.getMessageId() );
            if ( duplicateMessageContext != null ) {
                LOG.info( "Received duplicate acknowledgment: " + messagePojo.getMessageId() + " for normal message: "
                        + ( messagePojo.getReferencedMessage() != null ? messagePojo.getReferencedMessage() : "n/a" ) );
            } else {
                MessagePojo referencedMessagePojo = messagePojo.getReferencedMessage();
                if ( referencedMessagePojo != null ) {
                    try {
                        messageContext.getStateMachine().receivedAckMessage();
                    } catch (StateTransitionException stex) {
                        LOG.warn( stex.getMessage() );
                    }

                    // deregistering 
                    Engine.getInstance().getEngineController().getEngineControllerStub().broadcastAck( messageContext );
                    Engine.getInstance().getTransactionService().deregisterProcessingMessage(
                            referencedMessagePojo.getMessageId() );
                } else {
                    LOG.error( new LogMessage( "Error using referenced message on acknowledgment (ack message ID: "
                            + messagePojo.getMessageId() + ")", messagePojo ) );
                }
            }
        } else if ( messagePojo.getType() == Constants.INT_MESSAGE_TYPE_ERROR ) {
            LOG.trace( "Error detected" );
            
         // Try to identify duplicate
            MessageContext duplicateMessageContext = Engine.getInstance().getTransactionService().getMessageContext(
                    messagePojo.getMessageId() );
            if ( duplicateMessageContext != null ) {
                LOG.info( "Received duplicate error: " + messagePojo.getMessageId() + " for normal message: "
                        + ( messagePojo.getReferencedMessage() != null ? messagePojo.getReferencedMessage() : "n/a" ) );
            } else {

                MessagePojo referencedMessagePojo = messagePojo.getReferencedMessage();
                if ( referencedMessagePojo != null ) {
                    try {
                        messageContext.getStateMachine().receivedErrorMessage();
                    } catch (StateTransitionException stex) {
                        LOG.warn( stex.getMessage() );
                    }

                    Engine.getInstance().getEngineController().getEngineControllerStub().broadcastAck( messageContext );
                    Engine.getInstance().getTransactionService().deregisterProcessingMessage(
                            referencedMessagePojo.getMessageId() );
                } else {
                    LOG.error( new LogMessage( "Error using referenced message on acknowledgment (ack message ID: "
                            + messagePojo.getMessageId() + ")", messagePojo ) );
                }
            }
            
            
        } else {
            LOG.error( new LogMessage( "Message of unknown type received: " + messagePojo.getType(), messagePojo ) );
        }

        return responseMessageContext;
    } // processMessage

    /**
     * @param messageContext
     */
    protected void processSynchronousReplyMessage( MessageContext messageContext ) {

        synchronousReplies.put( messageContext.getMessagePojo().getMessageId(), messageContext );
        synchronized ( synchronousReplies ) {
            synchronousReplies.notifyAll();
        }
    } // processSynchronousReplyMessage

    /**
     * @param key
     * @return
     */
    public ProtocolAdapter getProtocolAdapterByKey( ProtocolSpecificKey key ) {

        if ( getProtocolAdapters() != null ) {
            for ( int i = 0; i < getProtocolAdapters().length; i++ ) {
                if ( getProtocolAdapters()[i].getKey().equalsIgnoreTransport( key ) ) {
                    return getProtocolAdapters()[i];
                }
            }
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.Dispatcher#getProtocolAdapters()
     */
    public ProtocolAdapter[] getProtocolAdapters() {

        return protocolAdapters;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.Dispatcher#setProtocolAdapters(org.nexuse2e.messaging.ProtocolAdapter[])
     */
    public void setProtocolAdapters( ProtocolAdapter[] protocolAdapter ) {

        this.protocolAdapters = protocolAdapter;
    }

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet() throws Exception {

        // check configuration
        if ( protocolAdapters == null || protocolAdapters.length == 0 ) {
            // no protcoladpter set.
            status = BeanStatus.ERROR;
        }
        status = BeanStatus.INSTANTIATED;
    }

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

        LOG.trace( "Initializing..." );

        backendOutboundDispatcher = config.getStaticBeanContainer().getBackendOutboundDispatcher();

        frontendActionSerializers = config.getFrontendActionSerializers();

        status = BeanStatus.INITIALIZED;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#teardown()
     */
    public void teardown() {

        LOG.trace( "Freeing resources..." );

        status = BeanStatus.INSTANTIATED;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#getRunLevel()
     */
    public Layer getActivationLayer() {

        return Layer.INBOUND_PIPELINES;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#start()
     */
    public void activate() {

        // TODO Auto-generated method stub
        LOG.trace( "activate" );

    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#stop()
     */
    public void deactivate() {

        // TODO Auto-generated method stub
        LOG.trace( "deactivate" );

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
    public BeanStatus getStatus() {

        // TODO Auto-generated method stub
        return status;
    }

    @SuppressWarnings("unchecked")
    public <T> T getParameter( String name ) {

        return (T) parameters.get( name );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Configurable#getParameterMap()
     */
    @SuppressWarnings("unchecked")
    public Map<String, ParameterDescriptor> getParameterMap() {

        return Collections.EMPTY_MAP;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Configurable#getParameters()
     */
    public Map<String, Object> getParameters() {

        return Collections.unmodifiableMap( parameters );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Configurable#setParameter(java.lang.String, java.lang.Object)
     */
    public void setParameter( String name, Object value ) {

        parameters.put( name, value );
    }

    public boolean isForwardPipelet() {

        return forwardPipelet;
    }

    public boolean isFrontendPipelet() {

        return frontendPipelet;
    }

    public void setForwardPipelet( boolean isForwardPipelet ) {

        forwardPipelet = isForwardPipelet;

    }

    public void setFrontendPipelet( boolean isFrontendPipelet ) {

        frontendPipelet = isFrontendPipelet;

    }
} // FrontendInboundDispatcher
