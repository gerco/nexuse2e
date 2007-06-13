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
import org.nexuse2e.Constants.Runlevel;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.configuration.ParameterDescriptor;
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

    private HashMap<String, FrontendActionSerializer> frontendActionSerializers = new HashMap<String, FrontendActionSerializer>();

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
    public MessageContext processMessage( MessageContext messageContext ) {

        boolean errorFlag = false;
        ChoreographyPojo choreography = null;
        ParticipantPojo participant = null;
        MessageContext clonedMessageContext = null;
        MessageContext responseMessageContext = null;
        Vector<ErrorDescriptor> errorMessages = new Vector<ErrorDescriptor>();

        MessagePojo messagePojo = messageContext.getMessagePojo();

        LOG.trace( "Entering FrontendInboundDispatcher.processMessage..." );

        if ( messagePojo.getConversation().getStatus() == org.nexuse2e.Constants.CONVERSATION_STATUS_CREATED ) {
            messagePojo.getConversation().setStatus( org.nexuse2e.Constants.CONVERSATION_STATUS_PROCESSING );
        }

        LOG.debug( "MessageType:" + messagePojo.getType() );

        ProtocolAdapter protocolAdapter = getProtocolAdapterByKey( messageContext.getProtocolSpecificKey() );
        if ( protocolAdapter == null ) {
            LOG.error( "No protocol implementation found for key: " + messageContext.getProtocolSpecificKey() );
            return null;
        }
        LOG.trace( "ProtocolAdapter found for incoming message" );

        try {
            choreography = validateChoreography( messageContext, Constants.INBOUND );
            LOG.trace( "matching choreography found" );
        } catch ( NexusException e ) {
            e.printStackTrace();
            LOG.error( "No matching choreography found: " + messagePojo.getConversation().getChoreography().getName() );
            responseMessageContext = protocolAdapter.createErrorAcknowledgement(
                    Constants.ErrorMessageReasonCode.CHOREOGRAPHY_NOT_FOUND, null, messageContext, null );
            errorFlag = true;
            errorMessages.add( new ErrorDescriptor( "No matching choreography found in configuration: "
                    + messagePojo.getConversation().getChoreography().getName() ) );
        }

        String actionId = messagePojo.getAction().getName();
        ActionPojo action = Engine.getInstance().getActiveConfigurationAccessService()
                .getActionFromChoreographyByActionId( choreography, actionId );
        if ( action == null ) {
            errorFlag = true;
            errorMessages.add( new ErrorDescriptor( "No matching action found in configuration: "
                    + messagePojo.getAction().getName() ) );
        }

        if ( !errorFlag ) {

            try {
                participant = validateParticipant( messageContext, Constants.INBOUND );
                LOG.trace( "matching participant found" );
            } catch ( NexusException e ) {
                e.printStackTrace();
                LOG.error( "No matching participant found: "
                        + messagePojo.getConversation().getPartner().getPartnerId() );
                responseMessageContext = protocolAdapter.createErrorAcknowledgement(
                        Constants.ErrorMessageReasonCode.PARTICIPANT_NOT_FOUND, choreography, messageContext, null );
                errorFlag = true;
                errorMessages.add( new ErrorDescriptor( "No matching participant found in configuration: "
                        + messagePojo.getConversation().getPartner().getPartnerId() ) );
            }
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

        LOG.info( "Received  " + msgType + " message (" + messagePojo.getMessageId() + ") from " + participant.getPartner().getPartnerId() + " for "
                + choreography.getName() + "/" + action.getName() );

        if ( messagePojo.getType() == Constants.INT_MESSAGE_TYPE_NORMAL ) {

            if ( !errorFlag ) {
                // Forward the message to check the transistion, persist it and pass to backend
                FrontendActionSerializer frontendActionSerializer = frontendActionSerializers.get( messagePojo
                        .getConversation().getChoreography().getName() );
                if ( frontendActionSerializer != null ) {
                    try {
                        clonedMessageContext = (MessageContext) messageContext.clone();

                        // Forward message to MrontendActionSerializer for further processing/queueing
                        frontendActionSerializer.processMessage( messageContext );

                        // Block for synchronous processing
                        if ( participant.getConnection().isSynchronous() ) {
                            LOG.debug( "Found synchronous connection setting." );
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
                        LOG.error( "Error processing message: " + messagePojo.getMessageId() + " ("
                                + messagePojo.getConversation().getChoreography().getName() + "/"
                                + messagePojo.getConversation().getPartner().getPartnerId() + ") - " + e );
                        errorFlag = true;
                    }
                } else {
                    LOG.error( "No FrontendActionSerializer found: "
                            + messagePojo.getConversation().getChoreography().getName() );
                    errorFlag = true;
                }
            }

            if ( !participant.getConnection().isSynchronous() ) {
                if ( !errorFlag ) {
                    LOG.trace( "no error response message found, creating ack" );
                    responseMessageContext = null;
                    try {
                        responseMessageContext = protocolAdapter.createAcknowledgement( choreography,
                                clonedMessageContext );
                    } catch ( NexusException e ) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {
                    LOG.trace( "error response message found" );
                    responseMessageContext = protocolAdapter.createErrorAcknowledgement(
                            Constants.ErrorMessageReasonCode.UNKNOWN, choreography, messageContext, errorMessages );
                }
            }

            if ( responseMessageContext == null ) {
                LOG.error( "No dispatcheable return message created" );
                return null;
            }

            // Send acknowledgment/error message back asynchronously
            if ( participant.getConnection().isSynchronous() ) {
                return responseMessageContext;
            } else {
                try {
                    backendOutboundDispatcher.processMessage( responseMessageContext );
                    return null;
                } catch ( NexusException e ) {
                    LOG.error( "unable to process Acknowledgement:" + e.getMessage() );
                }
            }
        } else if ( messagePojo.getType() == Constants.INT_MESSAGE_TYPE_ACK ) {
            LOG.trace( "Ack detected" );

            MessagePojo referencedMessagePojo = messagePojo.getReferencedMessage();
            if ( referencedMessagePojo != null ) {
                synchronized ( referencedMessagePojo.getConversation() ) {
                    if ( referencedMessagePojo.getConversation().getStatus() == org.nexuse2e.Constants.CONVERSATION_STATUS_AWAITING_ACK ) {
                        if ( referencedMessagePojo.getConversation().getCurrentAction().isEnd() ) {
                            referencedMessagePojo.getConversation().setStatus(
                                    org.nexuse2e.Constants.CONVERSATION_STATUS_COMPLETED );
                        } else {
                            referencedMessagePojo.getConversation().setStatus(
                                    org.nexuse2e.Constants.CONVERSATION_STATUS_IDLE );
                        }
                        referencedMessagePojo.setStatus( org.nexuse2e.Constants.MESSAGE_STATUS_SENT );
                        try {
                            // Complete ack message and add to conversation
                            messagePojo.setAction( referencedMessagePojo.getAction() );
                            messagePojo.setStatus( org.nexuse2e.Constants.MESSAGE_STATUS_SENT );
                            referencedMessagePojo.getConversation().getMessages().add( messagePojo );

                            Engine.getInstance().getTransactionService().updateTransaction(
                                    referencedMessagePojo.getConversation() );
                            Engine.getInstance().getTransactionService().deregisterProcessingMessage(
                                    referencedMessagePojo.getMessageId() );
                        } catch ( NexusException e ) {
                            LOG.error( "Error updating status for acknowleged message (message ID: "
                                    + referencedMessagePojo.getMessageId() + ")" );
                            e.printStackTrace();
                        }
                    } else {
                        LOG.warn( "Received ACK when it was not expected - ID of acknowleged message: "
                                + referencedMessagePojo.getMessageId() );
                    }
                } // synchronized
            } else {
                LOG.error( "Error using referenced message on acknowledgment (ack message ID: "
                        + messagePojo.getMessageId() + ")" );
            }
        } else if ( messagePojo.getType() == Constants.INT_MESSAGE_TYPE_ERROR ) {
            LOG.trace( "Error detected" );
        } else {
            LOG.error( "Message of unknown type received: " + messagePojo.getType() );
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

        LOG.debug( "Initializing..." );

        backendOutboundDispatcher = config.getStaticBeanContainer().getBackendOutboundDispatcher();

        frontendActionSerializers = config.getFrontendActionSerializers();

        status = BeanStatus.INITIALIZED;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#teardown()
     */
    public void teardown() {

        LOG.debug( "Freeing resources..." );

        status = BeanStatus.INSTANTIATED;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#getRunLevel()
     */
    public Runlevel getActivationRunlevel() {

        return Runlevel.INBOUND_PIPELINES;
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
