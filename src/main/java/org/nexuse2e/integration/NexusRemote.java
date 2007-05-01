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

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * @author mbreilmann
 *
 */
public class NexusRemote {

    /**
     * Main method.
     * @param args
     *  Parameter 1 host name.
     *  Parameter 2 RMI object name, should ne NexusE2EServer.
     *  Parameter 3 Trading Partner
     *  Parameter 4 Action name
     *  Parameter 5 File Name ( this rmi interface can only be used for sending messages that use a backend connector
     *              that uses a file name for the key.
     */
    public static void main( String args[] ) throws Exception {

        int portNo = 1099;

        String dateString = null;
        String hostName = null;
        String objectName = null;
        String choreographyId = null;
        String participantId = null;
        String actionId = null;
        String primaryKey = null;
        String conversationId = null;
        boolean newConversation = false;

        if ( args.length < 6 ) {
            System.err.println( "Wrong number of arguments, usage: com.tamgroup.nexus.shared.NexusRemote " );
            System.err.println( "\t-host <Host Name>" );
            System.err.println( "\t-service <RMI Service Name>" );
            System.err.println( "\t-choreography <ChoreographyId>" );
            System.err.println( "\t-participant <ParticipantId>" );
            System.err.println( "\t-action <ActionId>" );
            System.err.println( "\t-key <Connector primary Key>" );
            System.err.println( "\t-conversation <ConversationId> optional" );
            System.err.println( "\t-port <RMI Server port> optional" );
            System.err.println( "\t-newConversation <ConversationId> optional" );
            return;
        }

        int maxIndex = args.length - 1;
        for ( int i = 0; i <= maxIndex; i++ ) {
            if ( args[i].equalsIgnoreCase( "-host" ) ) {
                hostName = args[i + 1];
            } else if ( args[i].equalsIgnoreCase( "-service" ) ) {
                objectName = args[i + 1];
            } else if ( args[i].equalsIgnoreCase( "-choreography" ) ) {
                choreographyId = args[i + 1];
            } else if ( args[i].equalsIgnoreCase( "-participant" ) ) {
                participantId = args[i + 1];
            } else if ( args[i].equalsIgnoreCase( "-action" ) ) {
                actionId = args[i + 1];
            } else if ( args[i].equalsIgnoreCase( "-key" ) ) {
                primaryKey = args[i + 1];
            } else if ( args[i].equalsIgnoreCase( "-port" ) ) {
                portNo = Integer.parseInt( args[i + 1] );
            } else if ( args[i].equalsIgnoreCase( "-conversation" ) ) {
                conversationId = args[i + 1];
            } else if ( args[i].equalsIgnoreCase( "-newConversation" ) ) {
                conversationId = args[i + 1];
                newConversation = true;
            }
        }

        try {

            String rmiConversationID = null;
            Registry registry = LocateRegistry.getRegistry( hostName, portNo );
            String names[] = registry.list();

            System.out.print( "Registery  Host:  " + hostName + "\t" );
            System.out.print( "Service:  " + objectName + "\t" );
            System.out.println( "Port: " + portNo + "\t" );

            System.out.println( "\nService list" );
            for ( int i = 0; i < names.length; i++ ) {
                System.out.println( "### " + names[i] );
            }

            String lookupString = "//" + hostName + "/" + objectName;
            System.out.println( "\nRMI Lookup: " + lookupString );

            NEXUSe2eInterface nexusE2EServer = (NEXUSe2eInterface) registry.lookup( lookupString );

            if ( conversationId != null ) {
                if ( newConversation ) {
                    rmiConversationID = nexusE2EServer.createConversation( choreographyId, participantId,
                            conversationId );
                } else {
                    rmiConversationID = conversationId;
                }
                nexusE2EServer.triggerSendingMessage( rmiConversationID, actionId, primaryKey );
            } else {
                nexusE2EServer.triggerSendingNewMessage( choreographyId, participantId, actionId, primaryKey );
            }


            System.out.println( dateString + "\tNew message initiated" );
            System.out.println( "\tChoreographyID:\t" + choreographyId );
            System.out.println( "\tParticipant:\t" + participantId );
            System.out.println( "\tConversationID:\t" + rmiConversationID );

        } catch ( Exception e ) {
            System.err.println( "### NexusRemote Error: Exception: " + e.toString() );
            // e.printStackTrace();
            throw e;
        }
        // Removed because customers use this from a program
        // System.exit( 0 );
    }

    /**
     * Method for building the default RMI url string.
     * @param host
     * @param port
     * @param rmiObject
     * @param useRmiPrefix
     */
    public static String buildRMIURL( String host, int port, String rmiObject, boolean useRmiPrefix ) {

        StringBuffer rmiStringBuffer = new StringBuffer( "" );
        if ( host.length() > 0 ) {
            if ( useRmiPrefix ) {
                rmiStringBuffer.append( "rmi://" );
            } else {
                rmiStringBuffer.append( "rmi://" );
            }
            rmiStringBuffer.append( host );
            if ( port > 0 ) {
                rmiStringBuffer.append( ":" );
                rmiStringBuffer.append( port );
            }
            rmiStringBuffer.append( "/" );
        }
        rmiStringBuffer.append( rmiObject );
        return rmiStringBuffer.toString();
    }

    /**
     * Method for displaying the contents of the RMI registry.
     * @param host
     * @param port
     */
    public static String[] displayRegistry( String host, int port ) {

        Registry registry = null;
        try {
            registry = LocateRegistry.getRegistry( host, port );
        } catch ( RemoteException rmEx ) {
            System.err.println( "Error in NexusRemote.displayRegistry() - Error locating registry. Host:" + host
                    + ", Port:" + port + ", exception: " + rmEx );
        }
        String[] names = null;
        if ( registry != null ) {
            try {
                System.out.println( "##### Listing registry #####" );
                names = registry.list();
                for ( int i = 0; i < names.length; i++ ) {
                    // RemoteObject r_obj;
                    System.out.println( "   Service: " + names[i] );
                }
                System.out.println( "############################" );
            } catch ( Exception ex ) {
                System.err
                        .println( "Error in NexusRemote.displayRegistry() - Error displaying registered services, exception: "
                                + ex );
            }
        }
        return names;
    }

} // NexusRemote
