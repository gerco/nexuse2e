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

package org.nexuse2e;

import java.net.UnknownHostException;

import org.apache.log4j.Logger;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.messaging.Pipelet;
import org.nexuse2e.service.Service;
import org.nexuse2e.transport.TransportReceiver;

/**
 * @author mbreilmann
 *
 */
public class DefaultEngineControllerStub implements EngineControllerStub {

    private static Logger LOG = Logger.getLogger( DefaultEngineControllerStub.class );

    /**
     * 
     */
    public DefaultEngineControllerStub() {

        LOG.debug( "In constructor..." );
    } // default constructor

    /* (non-Javadoc)
     * @see org.nexuse2e.EngineControllerStub#getTransportReceiver(java.lang.String, org.nexuse2e.messaging.Pipelet)
     */
    public Pipelet getTransportReceiver( String controllerId, String className ) {

        TransportReceiver transportReceiver = null;
        try {
            transportReceiver = (TransportReceiver) Class.forName( className ).newInstance();
            LOG.trace( "Returning transportReceiver: " + transportReceiver );
        } catch ( Exception e ) {
            LOG.error( e.getClass().getName() + ": unable to create Instance of " + className );
            e.printStackTrace();
        }

        return transportReceiver;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.EngineControllerStub#initialize()
     */
    public void initialize() {

    }

    /* (non-Javadoc)
     * @see org.nexuse2e.EngineControllerStub#getMachineId()
     */
    public String getMachineId() {

        try {
            return java.net.InetAddress.getLocalHost().getHostName();
        } catch ( UnknownHostException e ) {
            LOG.error( "Unable to determine MachineId: " + e );
        }
        return "*unknown";
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.EngineControllerStub#getServiceWrapper(org.nexuse2e.service.Service)
     */
    public Service getServiceWrapper( Service service ) {

        return service;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.EngineControllerStub#broadcastAck(org.nexuse2e.messaging.MessageContext)
     */
    public void broadcastAck( MessageContext message ) {
        
    }
}