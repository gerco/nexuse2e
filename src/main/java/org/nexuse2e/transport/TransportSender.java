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
package org.nexuse2e.transport;

import org.apache.log4j.Logger;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.Constants.BeanStatus;
import org.nexuse2e.Constants.Layer;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.Constants.ParameterType;
import org.nexuse2e.messaging.AbstractPipelet;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.service.SenderAware;
import org.nexuse2e.service.Service;
import org.nexuse2e.service.http.HttpSenderService;

/**
 * The <code>TransportSender</code> is a <code>Pipelet</code> (and thus, it
 * can be configured like other pipelets) that allows to send messages by
 * using a service that is referenced by the unique service name.
 * 
 * @author jonas.reese
 */
public class TransportSender extends AbstractPipelet {

    private static final String SERVICE_PARAM_NAME = "service";

    private static Logger       LOG                = Logger.getLogger( TransportSender.class );

    private Service             service            = null;

    /**
     * Default constructor.
     */
    public TransportSender() {

        parameterMap.put( SERVICE_PARAM_NAME, new ParameterDescriptor( ParameterType.SERVICE, "Service",
                "The name of the service that shall be used by the sender", "" ) );
        frontendPipelet = true;
        status = BeanStatus.INSTANTIATED;
    }

    /**
     * @param messagePipelineParameter
     * @return
     * @throws NexusException
     */
    public MessageContext processMessage( MessageContext messagePipelineParameter ) throws NexusException {

        ( (SenderAware) service ).sendMessage( messagePipelineParameter );
        return messagePipelineParameter;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#initialize(org.nexuse2e.configuration.EngineConfiguration)
     */
    public void initialize( EngineConfiguration config ) throws InstantiationException {

        Service service = null;
        String s = getParameter( SERVICE_PARAM_NAME );
        if ( s != null && s.trim().length() > 0 ) {
            service = config.getStaticBeanContainer().getService( s );
            if ( service == null ) {
                LOG.error( "TransportSender.initialize(): Service \"" + s
                        + "\" not found. Please check your configuration" );
            }
        }
        if ( !( service instanceof SenderAware ) ) {
            if ( service == null ) {
                LOG.warn( "No service configured for TransportSender. Using default (HTTP)" );
            } else {
                LOG.warn( "Invalid service configured for TransportSender. Using default (HTTP)" );
            }
            service = new HttpSenderService();
        } else {
            ( (SenderAware) service ).setTransportSender( this );
        }
        this.service = service;
        super.initialize( config );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#teardown()
     */
    public void teardown() {

        LOG.trace( "Freeing resources..." );
        service = null;
        super.teardown();

    } // teardown

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#getActivationRunlevel()
     */
    public Layer getActivationLayer() {

        return Layer.INTERFACES;
    }

    public void setParameter( String name, Object value ) {

        parameters.put( name, value );
        // re-initialize when service is changed
        if ( SERVICE_PARAM_NAME.equals( name ) ) {
            Object v = getParameter( name );
            if ( v == null && value != null || value == null && v != null || !( v.equals( value ) ) ) {
                try {
                    initialize( Engine.getInstance().getCurrentConfiguration() );
                } catch ( InstantiationException e ) {
                    LOG.error( "Error initializing component: " + e );
                }
            }
        }
    }

    public void setServiceName( String serviceName ) {

        setParameter( SERVICE_PARAM_NAME, serviceName );
    }

    public String getServiceName() {

        return getParameter( SERVICE_PARAM_NAME );
    }
}
