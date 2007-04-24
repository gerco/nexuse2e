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
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import org.apache.log4j.Logger;
import org.nexuse2e.NexusException;
import org.springframework.beans.factory.InitializingBean;

import com.tamgroup.nexus.e2e.connector.ConnectorException;
import com.tamgroup.nexus.e2e.connector.NexusE2EServerInterface;

/**
 * The NexusE2EServer main class. This class is used for registering the RMI interface of the server with the default
 * JavaSoft name service (shipped with the JDK).
 * @author $Author: markus.breilmann $
 * @version $Revision: 1024 $ $Date: 2006-02-14 12:27:45 +0100 (Tue, 14 Feb 2006) $
 */

public class NEXUSe2eRMILegacyServer implements NexusE2EServerInterface, InitializingBean {

    private static Logger         LOG                   = Logger.getLogger( NEXUSe2eRMILegacyServer.class );

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

    /**
     * Create a new conversation
     * @param choreographyId The logical identifier of the document choreography
     * @param businessPartner The logical name of the receiver
     * @return The identifier (conversation ID) of the newly created conversation context
     */
    public String createConversation( String choreographyId, String businessPartnerId ) throws ConnectorException,
            RemoteException {

        try {
            return nexuse2eInterfaceImpl.createConversation( choreographyId, businessPartnerId );
        } catch ( NexusException nexEx ) {
            LOG.error( "Error creating new conversation: " + nexEx );
            throw new ConnectorException( nexEx.toString() );
        }
    }

    /**
     * Create a new conversation
     * @param choreographyId The logical identifier of the document choreography
     * @param businessPartner The logical name of the receiver
     * @param conversationID The ID that shall be used for this conversation
     * @return The identifier (conversation ID) of the newly created conversation context
     */
    public String createConversation( String choreographyId, String businessPartnerId, String conversationId )
            throws ConnectorException, RemoteException {

        try {
            return nexuse2eInterfaceImpl.createConversation( choreographyId, businessPartnerId, conversationId );
        } catch ( NexusException nexEx ) {
            LOG.error( "Error creating new conversation: " + nexEx );
            throw new ConnectorException( nexEx.toString() );
        }
    }

    /**
     * Create a new conversation
     * @param choreographyId The logical identifier of the document choreography
     * @param businessPartner The logical name of the receiver
     * @param conversationID The ID that shall be used for this conversation
     * @return The identifier (conversation ID) of the newly created conversation context
     */
    public String createOrRetrieveConversation( String choreographyId, String businessPartnerId, String conversationId )
            throws ConnectorException, RemoteException {

        return conversationId;
    } // createOrRetrieveConversation

    /**
     * Verify that there is an active Nexus .
     * @return boolean
     */
    public boolean failOverActive() throws RemoteException {

        return false;
    }

    /**
     * Generate a nexus Notification.
     * @param origin 
     * @param className
     * @param methodName
     * @param eventID
     * @param severity
     */
    public void generateNotification( String origin, String className, String methodName, int eventID, int severity )
            throws RemoteException {

        LOG.debug( "generateNotification " + origin + " - " + className + " - " + methodName + " - " + eventID + " - "
                + eventID );
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

            remoteObj = UnicastRemoteObject.exportObject( this, portNo );

            try {
                registry = LocateRegistry.createRegistry( portNo );
            } catch ( Exception rEx ) {
                LOG.error( "RMI: Registry already exists." );
                registry = null;
            } // try

            if ( registry == null ) {
                registry = LocateRegistry.getRegistry( portNo );
            }

            String bindTo = "//" + host + "/" + interfaceName;
            LOG.debug( "RMI: Trying to bind to: " + bindTo );

            registry.rebind( bindTo, remoteObj );

            LOG.info( "RMI: " + interfaceName + " bound in registry" );
        } catch ( Exception e ) {
            LOG.error( "Error initializing RMI interface.  Exception " + e.getMessage() );
        } // try
    } // registerListener

    /**
     * Send a message within a specific conversation context.
     * @param conversationId The identifier of the conversation context for this message
     * @param action The action requested by this message (e.g. sending a purchase order)
     * @param primaryKey The primary key information uniquely identifying the data to be retrieved by the backend connector
     */
    public void sendMessage( String choreographyId, String businessPartnerId, String conversationId, String actionId,
            Object primaryKey ) throws ConnectorException, RemoteException {

        try {
            nexuse2eInterfaceImpl.triggerSendingNewMessage( choreographyId, businessPartnerId, actionId,
                    conversationId, primaryKey );
        } catch ( NexusException nexEx ) {
            throw new ConnectorException( nexEx.toString() );
        }
    }

    /**
     * Send a message, and generate a new conversation.
     * @param conversationId The identifier of the conversation context for this message
     * @param action The action requested by this message (e.g. sending a purchase order)
     * @param primaryKey The primary key information uniquely identifying the data to be retrieved by the backend connector
     */
    public String sendNewMessage( String choreographyId, String businessPartnerId, String actionId, Object primaryKey )
            throws ConnectorException, RemoteException {

        String conversationId = null;

        try {
            conversationId = nexuse2eInterfaceImpl.triggerSendingNewMessage( choreographyId, businessPartnerId,
                    actionId, primaryKey );
        } catch ( NexusException nexEx ) {
            throw new ConnectorException( nexEx.toString() );
        }

        return conversationId;
    }

    public void setHostName( String hostName ) {

        this.hostName = hostName;
    }

    public void setInterfaceName( String interfaceName ) {

        this.interfaceName = interfaceName;
    }

    public void setRmiPort( int rmiPort ) {

        this.rmiPort = rmiPort;
    }
} // NEXUSe2eRMILegacyServer