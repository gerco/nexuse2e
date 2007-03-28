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
package org.nexuse2e.backend.pipelets;

import org.nexuse2e.NexusException;
import org.nexuse2e.messaging.AbstractPipelet;
import org.nexuse2e.messaging.MessageContext;

/**
 * Abstract class to facilitate the easier development of backend Pipelets for outbound messages.
 * If the system has received the actual payload for the message the normal <code>processMessage</code>
 * method will be dispatched to the <code>processPayloadAvailable</code>. If only the identifying information
 * is provided to retrieve the actual payload then the method <code>processPrimaryKeyAvailable</code> will
 * be triggered. Both methods are abstract and sub classes need to implement these methods.
 * @author gesch
 */
public abstract class AbstractOutboundBackendPipelet extends AbstractPipelet {
    
    /**
     * 
     */
    public AbstractOutboundBackendPipelet() {

    }

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.Pipelet#processMessage(org.nexuse2e.messaging.MessagePipeletParameter)
     */
    public MessageContext processMessage( MessageContext backendPipeletParameter )
            throws NexusException {

        if ( backendPipeletParameter != null && backendPipeletParameter.getMessagePojo() != null
                && backendPipeletParameter.getMessagePojo().getMessagePayloads() != null
                && backendPipeletParameter.getMessagePojo().getMessagePayloads().size() > 0 ) {

            return processPayloadAvailable( backendPipeletParameter );
        }
        return processPrimaryKeyAvailable( backendPipeletParameter );

    }

    /**
     * Method intended for processing outbound messages for which the payload has already been provided. 
     * @param backendPipeletParameter The <code>MessagePipeletParameter</code> parameter that contains
     * all information in order to process the outbound message.
     * @return The modified <code>MessagePipeletParameter</code> parameter that was operated on.
     */
    public abstract MessageContext processPayloadAvailable( MessageContext backendPipeletParameter )
            throws NexusException;

    /**
     * Method intended for processing outbound messages for which only identifying information been provided
     * required to retrieve the payload of the message through other means. 
     * @param backendPipeletParameter The <code>MessagePipeletParameter</code> parameter that contains
     * all information in order to process the outbound message.
     * @return The modified <code>MessagePipeletParameter</code> parameter that was operated on.
     */
    public abstract MessageContext processPrimaryKeyAvailable( MessageContext backendPipeletParameter )
            throws NexusException;
    
} // AbstractOutboundBackendPipelet
