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
package org.nexuse2e.integration;

import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;

/**
 * The NexusE2EServer main class. This class is used for registering the RMI interface of the server with the default
 * JavaSoft name service (shipped with the JDK).
 * @author $Author: markus.breilmann $
 * @version $Revision: 1024 $ $Date: 2006-02-14 12:27:45 +0100 (Tue, 14 Feb 2006) $
 */

public class NEXUSe2eRMIServer implements InitializingBean {

    private static Logger         LOG                   = Logger.getLogger( NEXUSe2eRMIServer.class );

    private String                hostName              = null;
    private String                interfaceName         = null;
    private int                   rmiPort               = 1099;

    private NEXUSe2eInterfaceImpl nexuse2eInterfaceImpl = null;

    public void afterPropertiesSet() throws Exception {

        nexuse2eInterfaceImpl = new NEXUSe2eInterfaceImpl();
        
        if ( (hostName == null) || (hostName.length() == 0) || (interfaceName == null) || (interfaceName.length() == 0)) {
            LOG.info( "RMI not configured, skipping initialization." );
            return;
        }
        
        if ( rmiPort == 0 ) {
            rmiPort = 1099;
        }
        
        registerListener( hostName, interfaceName, rmiPort );
    }

    public String getHostName() {

        return hostName;
    }

    public String getInterfaceName() {

        return interfaceName;
    }

    public int getRmiPort() {

        return rmiPort;
    }

    /**
     * Register the RMI listener on the host.
     * @param host
     * @param interfaceName
     */

    public void registerListener( String host, String interfaceName ) {

        registerListener( host, interfaceName, rmiPort );
    }

    public void registerListener( String host, String interfaceName, int portNo ) {

        try {
            Registry registry = null;
            Remote remoteObj = null;

            remoteObj = UnicastRemoteObject.exportObject( nexuse2eInterfaceImpl, portNo );

            try {
                registry = LocateRegistry.createRegistry( portNo );
            } catch ( Exception rEx ) {
                LOG.warn( "RMI: Registry already exists." );
                registry = null;
            } // try

            if ( registry == null ) {
                registry = LocateRegistry.getRegistry( portNo );
            }

            String bindTo = "//" + host + "/" + interfaceName;
            LOG.debug( "RMI: Trying to bind to: " + bindTo );

            registry.rebind( bindTo, remoteObj );

            LOG.info( "RMI: " + bindTo + " bound in registry" );
        } catch ( Exception e ) {
            LOG.error( "Error initializing RMI interface.  Exception " + e.getMessage() );
        } // try
    } // registerListener

    public void setHostName( String hostName ) {

        this.hostName = hostName;
    }

    public void setInterfaceName( String interfaceName ) {

        this.interfaceName = interfaceName;
    }

    public void setRmiPort( int rmiPort ) {

        this.rmiPort = rmiPort;
    }

} // NEXUSe2eRMIServer