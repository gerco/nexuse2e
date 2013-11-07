/**
 * NEXUSe2e Business Messaging Open Source  
 * Copyright 2009, X-ioma GmbH   
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
package org.nexuse2e.service;

import java.util.Map;

import org.nexuse2e.Layer;
import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.messaging.MessageProcessor;
import org.nexuse2e.messaging.generic.StaticRoutingPipelet;
import org.nexuse2e.transport.TransportReceiver;

/**
 * This class implements the {@link ReceiverAware} interface in order to enable entities to
 * directly (programmatically) pass a {@link MessageContext} to an inbound frontend pipeline.
 * With an instance of {@link GenericReceiverService} one could e.g. pass an inbound message
 * generated from a synchronous response of an outbound frontend message back to a different
 * inbound frontend pipeline. That would simulate a multi-step choreogrophy with synchronous
 * frontend protocols.
 * 
 * <code>Note:</code> This service contains no paramters by purpose.
 * Message routing etc. can be configured by subsequent pipelets (e.g. {@link StaticRoutingPipelet}).
 * 
 * @author Sebastian Schulze
 * @date 15.09.2010
 */
public class GenericReceiverService extends AbstractService implements ReceiverAware, MessageProcessor {

    private TransportReceiver transportReceiver;
    
    /* (non-Javadoc)
     * @see org.nexuse2e.service.AbstractService#fillParameterMap(java.util.Map)
     */
    @Override
    public void fillParameterMap( Map<String, ParameterDescriptor> parameterMap ) {

    }

    /* (non-Javadoc)
     * @see org.nexuse2e.service.AbstractService#getActivationLayer()
     */
    @Override
    public Layer getActivationLayer() {
        return Layer.INBOUND_PIPELINES;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.service.ReceiverAware#getTransportReceiver()
     */
    public TransportReceiver getTransportReceiver() {
        return transportReceiver;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.service.ReceiverAware#setTransportReceiver(org.nexuse2e.transport.TransportReceiver)
     */
    public void setTransportReceiver( TransportReceiver transportReceiver ) {
        this.transportReceiver = transportReceiver;
    }

   public MessageContext processMessage( MessageContext messageContext ) throws IllegalArgumentException,
                                                                         IllegalStateException, NexusException {
        return transportReceiver.processMessage( messageContext );
    }

}
