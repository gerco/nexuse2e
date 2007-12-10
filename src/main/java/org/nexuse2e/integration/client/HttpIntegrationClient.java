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

package org.nexuse2e.integration.client;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpHost;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.protocol.Protocol;

/**
 * @author mbreilmann
 *
 */
public class HttpIntegrationClient {

    public static final String PARAM_CHOREOGRAPY_ID         = "choreography";
    public static final String PARAM_CONVERSATION_ID        = "conversation";
    public static final String PARAM_MESSAGE_ID             = "message";
    public static final String PARAM_MESSAGE_TIMESTAMP      = "messageTimestamp";
    public static final String PARAM_CONVERSATION_TIMESTAMP = "conversationTimestamp";
    public static final String PARAM_PARTNER_ID             = "partner";
    public static final String PARAM_ACTION_ID              = "action";
    public static final String PARAM_SENDER_ID              = "sender";
    public static final String PARAM_CONTENT                = "content";
    public static final String PARAM_PRIMARY_KEY            = "primaryKey";

    private static final int   SOCKET_TIMEOUT               = 120000;

    /**
     * @param args
     */
    public static void main( String[] args ) {

        String urlString = null;
        String choreographyId = null;
        String participantId = null;
        String actionId = null;
        String primaryKey = null;
        String conversationId = null;
        String fileName = null;
        String content = null;
        boolean newConversation = false;
        URL url = null;

        if ( args.length < 5 ) {
            System.err.println( "Wrong number of arguments, usage: com.tamgroup.nexus.shared.NexusRemote " );
            System.err.println( "\t-url <Request URL>" );
            System.err.println( "\t-choreography <ChoreographyId>" );
            System.err.println( "\t-participant <ParticipantId>" );
            System.err.println( "\t-action <ActionId>" );
            System.err.println( "\t-file <File to transmit> (this or -key must be provided)" );
            System.err.println( "\t-key <Connector primary Key>  (this or -file must be provided)" );
            System.err.println( "\t-conversation <ConversationId> optional" );
            System.err.println( "\t-newConversation <ConversationId> optional" );
            return;
        }

        int maxIndex = args.length - 1;
        try {
            for ( int i = 0; i <= maxIndex; i++ ) {
                if ( args[i].equalsIgnoreCase( "-url" ) ) {
                    urlString = args[i + 1];
                    url = new URL( urlString );
                } else if ( args[i].equalsIgnoreCase( "-choreography" ) ) {
                    choreographyId = args[i + 1];
                } else if ( args[i].equalsIgnoreCase( "-participant" ) ) {
                    participantId = args[i + 1];
                } else if ( args[i].equalsIgnoreCase( "-action" ) ) {
                    actionId = args[i + 1];
                } else if ( args[i].equalsIgnoreCase( "-file" ) ) {
                    fileName = args[i + 1];
                    FileInputStream fis = new FileInputStream( fileName );
                    BufferedInputStream bufferedInputStream = new BufferedInputStream( fis );

                    // Determine the size of the file
                    int fileSize = bufferedInputStream.available();

                    long memory = Runtime.getRuntime().freeMemory();
                    if ( fileSize >= memory ) {
                        String msg = "Not Enough memory to transfer data of " + fileSize / 1024
                                + " Kbytes. Available memory is " + memory / 1024 + " Kbytes";
                        throw new Exception( msg );
                    }

                    byte[] documentBuffer = new byte[fileSize]; // Create a buffer that will hold the data from the file

                    bufferedInputStream.read( documentBuffer, 0, fileSize ); // Read the file content into the buffer
                    bufferedInputStream.close();
                    content = new String( documentBuffer );
                } else if ( args[i].equalsIgnoreCase( "-key" ) ) {
                    primaryKey = args[i + 1];
                } else if ( args[i].equalsIgnoreCase( "-conversation" ) ) {
                    conversationId = args[i + 1];
                } else if ( args[i].equalsIgnoreCase( "-newConversation" ) ) {
                    conversationId = args[i + 1];
                    newConversation = true;
                }
            }
        } catch ( Exception e ) {
            System.err.println( "Error processing parameters: " + e );
            e.printStackTrace();
            System.exit( 0 );
        }

        System.out.println( "Creating request" );

        HostConfiguration configuration = new HostConfiguration();
        configuration.setHost( new HttpHost( url.getHost(), url.getPort(), Protocol.getProtocol( url.getProtocol() ) ) );

        HttpClient client = new HttpClient();
        client.setHostConfiguration( configuration );
        PostMethod method = new PostMethod( url.toExternalForm() );
        method.getParams().setSoTimeout( SOCKET_TIMEOUT );
        method.setParameter( PARAM_CHOREOGRAPY_ID, choreographyId );
        method.setParameter( PARAM_PARTNER_ID, participantId );
        method.setParameter( PARAM_ACTION_ID, actionId );
        if ( primaryKey != null ) {
            method.setParameter( PARAM_PRIMARY_KEY, primaryKey );
        } else {
            method.setParameter( PARAM_CONTENT, content );
        }

        try {
            int result = client.executeMethod( method );
            System.out.println( "HTTP result is " + result + "(\n" + method.getResponseBodyAsString() + "\n)" );
        } catch ( IOException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
