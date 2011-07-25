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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.ProtocolSpecificKey;
import org.nexuse2e.Constants.BeanStatus;
import org.nexuse2e.Constants.Layer;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.controller.TransactionService;
import org.nexuse2e.logging.LogMessage;
import org.nexuse2e.pojo.ChoreographyPojo;
import org.nexuse2e.pojo.MessagePojo;
import org.nexuse2e.pojo.ParticipantPojo;
import org.springframework.beans.factory.InitializingBean;

/**
 * Component dispatching outbound messages to the correct queue based on their choreography (business process). 
 * Additionally the message is checked for consistency with the messaging protocol it belongs to and processing
 * fails if any inconsistencies are detected.
 *
 * @author gesch, sschulze
 */
public class BackendOutboundDispatcher extends ChoreographyValidator implements Pipelet, InitializingBean {

    private static Logger                        LOG                        = Logger
                                                                                    .getLogger( BackendOutboundDispatcher.class );

    private ProtocolAdapter[]                    protocolAdapters;

    private FrontendInboundDispatcher            frontendInboundDispatcher  = null;
    private FrontendOutboundDispatcher           frontendOutboundDispatcher = null;
    private BeanStatus                           status                     = BeanStatus.UNDEFINED;

    private boolean                              forwardPipeline;
    private boolean                              frontendPipeline;

    private Map<String, Object>                  parameters                 = new HashMap<String, Object>();

    private Pipeline                             pipeline;

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.Pipelet#processMessage(org.nexuse2e.messaging.MessageContext)
     */
    public MessageContext processMessage( MessageContext messageContext ) throws NexusException {

        ChoreographyPojo choreography = validateChoreography( messageContext );
        LOG.trace( new LogMessage( "Matching choreography found: " + choreography.getName(),messageContext.getMessagePojo()) );

        ParticipantPojo participant = validateParticipant( messageContext );
        LOG.trace( new LogMessage( "Matching participant found: " + participant.getPartner().getPartnerId(),messageContext.getMessagePojo()) );

        // create protocolspecific key

        ProtocolSpecificKey key = new ProtocolSpecificKey( messageContext.getMessagePojo().getTRP().getProtocol()
                .toLowerCase(), messageContext.getMessagePojo().getTRP().getVersion(), messageContext.getMessagePojo()
                .getTRP().getTransport().toLowerCase() );
        messageContext.setProtocolSpecificKey( key );
        LOG.debug( new LogMessage( "ProtocolKey:" + key,messageContext.getMessagePojo()) );

        ProtocolAdapter protocolAdapter = getProtocolAdapterByKey( messageContext.getProtocolSpecificKey() );
        if ( protocolAdapter == null ) {
            LOG.error( new LogMessage( "No protocol implementation found for key: " + messageContext.getProtocolSpecificKey(),messageContext.getMessagePojo()) );
            throw new NexusException( "No ProtocolAdapter found for key: " + key );
        }
        protocolAdapter.addProtcolSpecificParameters( messageContext );

        // Forward the message to check the transistion, persist it and pass to backend
        MessageHandlingCenter.getInstance().processMessage( messageContext );

        return messageContext;
    } // processMessage

    /**
     * Recover messages from persisten storage that need to be resent based on their status and messaging 
     * protocol requirements.
     */
    public void recoverMessages() {

        LOG.info( "Searching for messages to recover..." );

        List<MessagePojo> activeMessagePojos = null;

        long totalQueueingTime = 0;
        long totalGetMessageTime = 0;

        try {
            TransactionService transactionService = Engine.getInstance().getTransactionService();
            activeMessagePojos = transactionService.getActiveMessages();
            LOG.trace( "Found active messages: " + activeMessagePojos.size() );
            long startTime = System.currentTimeMillis();
            for ( MessagePojo messagePojo : activeMessagePojos ) {

                long time = System.currentTimeMillis();
                try {
                    MessageContext messageContext = transactionService.createMessageContext( messagePojo );
                    totalGetMessageTime += ( System.currentTimeMillis() - time );

                    if ( ( messageContext != null ) && messagePojo.isOutbound() ) {
                        LOG.debug(new LogMessage(  "Recovered message: " + messagePojo.getMessageId(),messagePojo) );

                        time = System.currentTimeMillis();
                        MessageHandlingCenter.getInstance().requeueMessage( messageContext );
                        totalQueueingTime += ( System.currentTimeMillis() - time );
                    }
                } catch ( Exception ex ) {
                    String messageId = "";
                    if ( messagePojo != null ) {
                        messageId = messagePojo.getMessageId();
                    }
                    LOG.error(new LogMessage( "Error recovering message " + messageId, messagePojo, ex), ex);
                }
            }
            LOG.trace( "took " + totalQueueingTime + " ms for message queueing and " + totalGetMessageTime
                    + " ms for retrieving messages, total time is " + ( System.currentTimeMillis() - startTime )
                    + " ms" );
        } catch ( NexusException e ) {
            LOG.error( e );
        }

    } // recoverMessages

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

    /**
     * @return the protocolAdapter
     */
    public ProtocolAdapter[] getProtocolAdapters() {

        return protocolAdapters;
    }

    /**
     * @param protocolAdapter the protocolAdapter to set
     */
    public void setProtocolAdapters( ProtocolAdapter[] protocolAdapter ) {

        this.protocolAdapters = protocolAdapter;
    }

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet() throws Exception {

        if ( protocolAdapters == null || protocolAdapters.length == 0 ) {
            status = BeanStatus.ERROR;
        }
        status = BeanStatus.INSTANTIATED;
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
     * @see org.nexuse2e.Manageable#initialize(org.nexuse2e.configuration.EngineConfiguration)
     */
    public void initialize( EngineConfiguration config ) {

        LOG.trace( "Initializing..." );

        frontendOutboundDispatcher = config.getStaticBeanContainer().getFrontendOutboundDispatcher();
        if ( frontendOutboundDispatcher == null ) {
            status = BeanStatus.ERROR;
        }
        frontendInboundDispatcher = config.getStaticBeanContainer().getFrontendInboundDispatcher();
        if ( frontendInboundDispatcher == null ) {
            status = BeanStatus.ERROR;
        }
        status = BeanStatus.INITIALIZED;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#teardown()
     */
    public void teardown() {

        LOG.trace( "Freeing resources..." );

        status = BeanStatus.INSTANTIATED;
    } // teardown

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#getRunLevel()
     */
    public Layer getActivationLayer() {

        return Layer.OUTBOUND_PIPELINES;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#start()
     */
    public void activate() {

        LOG.trace( "activate" );

    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#stop()
     */
    public void deactivate() {

        LOG.trace( "deactivate" );

    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#validate()
     */
    public boolean validate() {

        return false;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Configurable#getParameter(java.lang.String)
     */
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

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.Pipelet#isForwardPipelet()
     */
    public boolean isForwardPipelet() {

        return forwardPipeline;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.Pipelet#isFrontendPipelet()
     */
    public boolean isFrontendPipelet() {

        return frontendPipeline;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.Pipelet#setForwardPipelet(boolean)
     */
    public void setForwardPipelet( boolean isForwardPipelet ) {

        forwardPipeline = isForwardPipelet;

    }

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.Pipelet#setFrontendPipelet(boolean)
     */
    public void setFrontendPipelet( boolean isFrontendPipelet ) {

        frontendPipeline = isFrontendPipelet;

    }

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.Pipelet#getPipeline()
     */
    public Pipeline getPipeline() {

        return pipeline;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.Pipelet#setPipeline(org.nexuse2e.messaging.Pipeline)
     */
    public void setPipeline( Pipeline pipeline ) {

        this.pipeline = pipeline;
    }
} // BackendOutboundDispatcher
