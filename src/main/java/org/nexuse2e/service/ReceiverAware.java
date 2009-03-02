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
package org.nexuse2e.service;

import org.nexuse2e.transport.TransportReceiver;

/**
 * This interface shall be implemented by <code>Service</code>s that can serve
 * as a peer for a <code>TransportReceiver</code>.
 * @author jonas.reese
 */
public interface ReceiverAware {

    /**
     * Gets the <code>TransportReceiver</code>.
     * @return The transport receiver.
     */
    public TransportReceiver getTransportReceiver();

    /**
     * Sets the <code>TransportReceiver</code>.
     * @param transportReceiver the Transport Receiver to set
     */
    public void setTransportReceiver( TransportReceiver transportReceiver );

}
