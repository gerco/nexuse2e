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
package org.nexuse2e.transport;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.ProtocolSpecific;
import org.nexuse2e.ProtocolSpecificKey;
import org.nexuse2e.Constants.BeanStatus;
import org.nexuse2e.Constants.Layer;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.Constants.ParameterType;
import org.nexuse2e.logging.LogMessage;
import org.nexuse2e.messaging.FrontendPipeline;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.messaging.Pipelet;
import org.nexuse2e.messaging.Pipeline;
import org.nexuse2e.service.ReceiverAware;
import org.nexuse2e.service.Service;
import org.nexuse2e.service.http.HttpReceiverService;

/**
 * A <code>TransportReceiver</code> is the starting point of a pipeline. It uses
 * a <code>Service</code> referenced by it's unique name in order to perform the
 * "real" receiving action. It can be configured with Spring or the default
 * mechanism for <code>Configurable</code>s (this not supported yet).
 * 
 * @author jonas.reese
 */
public class TransportReceiver implements Pipelet, ProtocolSpecific {

    private static final String                SERVICE_PARAM_NAME                        = "service";

    public static final String                 COMMUNICATION_PROTOCOL_ID_PARAM_NAME      = "communicationProtocolId";
    public static final String                 COMMUNICATION_PROTOCOL_VERSION_PARAM_NAME = "communicationProtocolVersion";
    public static final String                 TRANSPORT_PROTOCOL_ID_PARAM_NAME          = "transportProtocolId";

    private static Logger                      LOG                                       = Logger
                                                                                                 .getLogger( TransportReceiver.class );

    protected Map<String, ParameterDescriptor> parameterDescriptors;
    protected Map<String, Object>              parameters;
    private FrontendPipeline                   frontendPipeline                          = null;
    private BeanStatus                         status                                    = BeanStatus.UNDEFINED;
    private ProtocolSpecificKey                key;

    /**
     * Default constructor. This should only be called by the engine
     * (or Spring).
     */
    public TransportReceiver() {

        parameters = new HashMap<String, Object>();
        parameterDescriptors = new LinkedHashMap<String, ParameterDescriptor>();
        parameterDescriptors.put( SERVICE_PARAM_NAME, new ParameterDescriptor( ParameterType.SERVICE, "Service",
                "The name of the service that shall be used by the receiver", ReceiverAware.class ) );
        /*
        ListParameter communicationProtocolIdDropdown = new ListParameter();
        communicationProtocolIdDropdown.addElement( "EBXML", "ebxml" );
        parameterDescriptors.put( COMMUNICATION_PROTOCOL_ID_PARAM_NAME, new ParameterDescriptor( ParameterType.LIST,
                "Communication protocol", "The communication protocol", communicationProtocolIdDropdown ) );
        ListParameter communicationProtocolVersionDropdown = new ListParameter();
        communicationProtocolVersionDropdown.addElement( "1.0", "1.0" );
        communicationProtocolVersionDropdown.addElement( "2.0", "2.0" );
        communicationProtocolVersionDropdown.setSelectedIndex( 1 ); // default ist 2.0
        parameterDescriptors.put( COMMUNICATION_PROTOCOL_VERSION_PARAM_NAME, new ParameterDescriptor(
                ParameterType.LIST, "Communication protocol version", "The communication protocol version number",
                communicationProtocolVersionDropdown ) );
        ListParameter transportProtocolIdDropdown = new ListParameter();
        transportProtocolIdDropdown.addElement( "HTTP", "http" );
        parameterDescriptors.put( TRANSPORT_PROTOCOL_ID_PARAM_NAME, new ParameterDescriptor( ParameterType.LIST,
                "Transport protocol", "The transport protocol", transportProtocolIdDropdown ) );
                */
        status = BeanStatus.INSTANTIATED;
    }

    public void afterPropertiesSet() throws Exception {

        if ( frontendPipeline == null ) {
            throw new NexusException( "No frontend pipeline configured for TransportReceiver" );
        }
    }

    public MessageContext processMessage( MessageContext data ) throws NexusException {

        if ( LOG.isTraceEnabled() ) {
            LOG.trace( new LogMessage( "TransportReceiver processing message..",data.getMessagePojo()) );
        }
        data.setProtocolSpecificKey( getKey() );

        return frontendPipeline.processMessage( data );
    }

    /**
     * @return the frontendPipeline
     */
    public FrontendPipeline getFrontendPipeline() {

        return frontendPipeline;
    }

    /**
     * @param frontendPipeline the frontendPipeline to set
     */
    public void setFrontendPipeline( FrontendPipeline frontendPipeline ) {

        this.frontendPipeline = frontendPipeline;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#activate()
     */
    public void activate() {

        status = BeanStatus.ACTIVATED;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#deactivate()
     */
    public void deactivate() {

        status = BeanStatus.INITIALIZED;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#getActivationTime()
     */
    public Layer getActivationLayer() {

        return Layer.INTERFACES;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#getStatus()
     */
    public BeanStatus getStatus() {

        return status;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#initialize(org.nexuse2e.configuration.EngineConfiguration)
     */
    public void initialize( EngineConfiguration config ) {

        Service service = null;
        String s = getParameter( SERVICE_PARAM_NAME );
        LOG.trace( "TransportReceiver - service: " + s );
        if ( s != null && s.trim().length() > 0 ) {
            service = config.getStaticBeanContainer().getService( s );
            if ( service == null ) {
                LOG.error( "TransportReceiver.initialize(): Service \"" + s
                        + "\" not found. Please check your configuration" );
            }
        }
        if ( !( service instanceof ReceiverAware ) ) {
            if ( service == null ) {
                LOG.warn( "No service configured for TransportReceiver. " + "Using default (HTTP with EBXML 2.0)" );
            } else {
                LOG.warn( "Invalid service configured for TransportReceiver. " + "Using default (HTTP with EBXML 2.0)" );
            }
            service = new HttpReceiverService();
        }
        LOG.trace( "TransportReceiverService: " + service );
        ( (ReceiverAware) service ).setTransportReceiver( this );
        status = BeanStatus.INITIALIZED;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#teardown()
     */
    public void teardown() {

        LOG.trace( "Freeing resources..." );

        // For properly restarting the engine the following refrences MUST NOT be cleared/reset!!!
        // parameterDescriptors
        // parameters
        // frontendpipeline
        // key

        status = BeanStatus.INSTANTIATED;
    } // teardown

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
    public Map<String, ParameterDescriptor> getParameterMap() {

        return parameterDescriptors;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Configurable#getParameters()
     */
    public Map<String, Object> getParameters() {

        return Collections.unmodifiableMap( parameters );
    }

    public void setParameter( String name, Object value ) {

        parameters.put( name, value );
        // re-initialize when service is changed
        if ( SERVICE_PARAM_NAME.equals( name ) ) {
            Object v = getParameter( name );
            if ( v == null && value != null || value == null && v != null || !( v.equals( value ) ) ) {
                initialize( Engine.getInstance().getCurrentConfiguration() );
            }
        }
    }

    public void setServiceName( String serviceName ) {

        setParameter( SERVICE_PARAM_NAME, serviceName );
    }

    public String getServiceName() {

        return getParameter( SERVICE_PARAM_NAME );
    }

    public void setKey( ProtocolSpecificKey key ) {

        this.key = key;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.ProtocolSpecific#getKey()
     */
    public ProtocolSpecificKey getKey() {

        return key;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.Pipelet#isForwardPipelet()
     */
    public boolean isForwardPipelet() {

        return true;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.Pipelet#isFrontendPipelet()
     */
    public boolean isFrontendPipelet() {

        return true;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.Pipelet#setForwardPipelet(boolean)
     */
    public void setForwardPipelet( boolean isForwardPipelet ) {

    }

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.Pipelet#setFrontendPipelet(boolean)
     */
    public void setFrontendPipelet( boolean isFrontendPipelet ) {

    }
    
    public void setPipeline( Pipeline pipeline ) {
        if (!(pipeline instanceof FrontendPipeline)) {
            throw new IllegalArgumentException( "Parent pipeline for TransportReceiver must be a FrontendPipeline" );
        }
        this.frontendPipeline = (FrontendPipeline) pipeline;
    }
    
    public Pipeline getPipeline() {
        return getFrontendPipeline();
    }
}
