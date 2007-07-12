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
package org.nexuse2e.service;

import org.nexuse2e.NexusException;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.transport.TransportSender;

/**
 * This interface shall be implemented by <code>Service</code>s that can serve
 * as a peer for a <code>TransportSender</code>.
 * @author jonas.reese
 */
public interface SenderAware {

    /**
     * Gets the <code>TransportSender</code>.
     * @return The transport sender.
     */
    public TransportSender getTransportSender();

    /**
     * Sets the <code>TransportSender</code>.
     * @param transportSender the Transport Sender to set
     */
    public void setTransportSender( TransportSender transportSender );

    /**
     * Sends a message.
     * @param message The message to be sent.
     * @throws NexusException if the message could not be sent for any reason.
     */
    public void sendMessage( MessageContext message ) throws NexusException;
}
