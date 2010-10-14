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
package org.nexuse2e;

import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.messaging.Pipelet;
import org.nexuse2e.service.Service;

public interface EngineControllerStub {

    /**
     * Return a Transport Receiver for the specified controller ID.
     * @param receiverId The ID of the receiver to wrap.
     * @return The wrapper for the specified controller.
     */
    public Pipelet getTransportReceiver( String receiverId, String className );

    /**
     * @param service
     * @return
     */
    public Service getServiceWrapper(Service service);
    /**
     * is called before using the underlying stub implementation
     */
    public void initialize();
    
    /**
     * @param message
     */
    public void broadcastAck(MessageContext message);
    
    
    public String getMachineId();
    
    /**
     * @return
     */
    public boolean isPrimaryNode();
    
} // EngineControllerStub
