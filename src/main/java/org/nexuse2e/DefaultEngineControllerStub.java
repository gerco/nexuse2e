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
import org.nexuse2e.service.AbstractControllerService;

/**
 * @author mbreilmann
 *
 */
public class DefaultEngineControllerStub implements EngineControllerStub {

    private static Logger LOG = Logger.getLogger( DefaultEngineControllerStub.class );

    public DefaultEngineControllerStub() {

        LOG.debug( "In constructor..." );
    } // default constructor

    /* (non-Javadoc)
     * @see org.nexuse2e.EngineControllerStub#getControllerWrapper(java.lang.String)
     */
    public AbstractControllerService getControllerWrapper( String controllerId, AbstractControllerService controller ) {
        LOG.debug( "Returning controller: " + controller );

        return controller;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.EngineControllerStub#initialize()
     */
    public void initialize() {
        
    }

    public String getMachineId() {

        try {
            return java.net.InetAddress.getLocalHost().getHostName();
        } catch ( UnknownHostException e ) {
            LOG.error( "Unable to determine MachineId: "+e);
        } 
        return "*unknown";
    }
} // DefaultEngineControllerStub
