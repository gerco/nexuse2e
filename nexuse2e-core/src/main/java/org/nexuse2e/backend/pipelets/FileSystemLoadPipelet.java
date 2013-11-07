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
package org.nexuse2e.backend.pipelets;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.activation.FileTypeMap;
import javax.activation.MimetypesFileTypeMap;

import org.apache.log4j.Logger;
import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.ParameterType;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.pojo.MessagePayloadPojo;

/**
 * @author gesch
 *
 */
public class FileSystemLoadPipelet extends AbstractOutboundBackendPipelet {

    private static Logger      LOG                  = Logger.getLogger( FileSystemLoadPipelet.class );

    public static final String DIRECTORY_PARAM_NAME = "directory";
    public static final String PASSWORD_PARAM_NAME  = "password";
    public static final String USER_NAME_PARAM_NAME = "username";

    /**
     * Default constructor.
     */
    public FileSystemLoadPipelet() {

        parameterMap.put( DIRECTORY_PARAM_NAME, new ParameterDescriptor( ParameterType.STRING, "Directory",
                "Target directory", "/nexus/dump" ) );
        parameterMap.put( PASSWORD_PARAM_NAME, new ParameterDescriptor( ParameterType.PASSWORD, "Password",
                "Secure password", "" ) );
        parameterMap.put( USER_NAME_PARAM_NAME, new ParameterDescriptor( ParameterType.STRING, "Username",
                "Login Username", "" ) );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.backend.pipelets.AbstractOutboundBackendPipelet#processPayloadAvailable(org.nexuse2e.messaging.MessageContext)
     */
    @Override
    public MessageContext processPayloadAvailable( MessageContext messageContext ) throws NexusException {

        return messageContext;
    } // processPayloadAvailable

    /* (non-Javadoc)
     * @see org.nexuse2e.backend.pipelets.AbstractOutboundBackendPipelet#processPrimaryKeyAvailable(org.nexuse2e.messaging.MessageContext)
     */
    @Override
    public MessageContext processPrimaryKeyAvailable( MessageContext messageContext ) throws NexusException {

        byte[] documentBuffer = null; // The binary data buffer that will hold the document
        String newPrimaryKey = (String) messageContext.getData(); // Cast primary key to correct type
        String fileName = null;

        if ( ( messageContext == null ) || ( messageContext.getMessagePojo() == null ) ) {
            throw new NexusException( "MessageContext not properly initialized, missing MessagePojo!" );
        }

        if ( newPrimaryKey != null ) {
            StringTokenizer tokens = new StringTokenizer( newPrimaryKey, "," );
            List<MessagePayloadPojo> messagePayloads = new ArrayList<MessagePayloadPojo>();
            int count = tokens.countTokens();

            for ( int i = 0; i < count; i++ ) {
                fileName = ( (String) tokens.nextElement() ).trim();

                LOG.debug( "File to send: '" + fileName + "'" );

                // Only execute if a file name was specified
                if ( ( fileName != null ) && ( fileName.length() != 0 ) ) {
                    // Execute within a try/catch block to handle any exceptions that might occur
                    try {

                        // Open the file to read one line at a time
                        FileInputStream fis = null;

                        // Workaround: Some filesystem need two tries to successfully
                        // get a file, expecially remote (network) file shares.
                        try {
                            fis = new FileInputStream( fileName );
                        } catch ( Exception ex ) {
                        }

                        if ( fis == null ) {
                            fis = new FileInputStream( fileName );
                        }
                        BufferedInputStream bufferedInputStream = new BufferedInputStream( fis );

                        // Determine the size of the file
                        int fileSize = bufferedInputStream.available();

                        long memory = Runtime.getRuntime().freeMemory();
                        if ( fileSize >= memory ) {
                            String msg = "Not Enough memory to transfer data of " + fileSize / 1024
                                    + " Kbytes. Available memory is " + memory / 1024 + " Kbytes";
                            throw new NexusException( msg );
                        }

                        documentBuffer = new byte[fileSize]; // Create a buffer that will hold the data from the file

                        bufferedInputStream.read( documentBuffer, 0, fileSize ); // Read the file content into the buffer
                        bufferedInputStream.close();

                    } catch ( IOException ioEx ) { // Handle exceptions related to the file I/O
                        ioEx.printStackTrace();
                        LOG.error( "IOException: " + ioEx );
                        throw new NexusException( ioEx.getMessage() ); // Pass exception to NEXUSe2e engine using correct exception type
                    } // try/catch

                } else { // if
                    throw new NexusException( "FileConnector - No primary key specified." );
                } // if

                // Determine the MIME type of the document
                MimetypesFileTypeMap mimetypesFileTypeMap = (MimetypesFileTypeMap) FileTypeMap.getDefaultFileTypeMap();
                String mimeType = mimetypesFileTypeMap.getContentType( fileName );

                // Prepare the Payload and set the MIME content type
                /* Old version without file name...
                MessagePayloadPojo messagePayloadPojo = new MessagePayloadPojo( messageContext.getMessagePojo(), i,
                        mimeType, Engine.getInstance().getIdGenerator(
                                org.nexuse2e.Constants.ID_GENERATOR_MESSAGE_PAYLOAD ).getId(), documentBuffer,
                        new Date(), new Date(), 1 );
                */
                MessagePayloadPojo messagePayloadPojo = new MessagePayloadPojo( messageContext.getMessagePojo(), i,
                        mimeType, fileName + "__body_" + (i+1), documentBuffer,
                        new Date(), new Date(), 1 );
                messagePayloads.add( messagePayloadPojo );

            } // for
            messageContext.getMessagePojo().setMessagePayloads( messagePayloads );

        }

        return messageContext;
    } // processPrimaryKeyAvailable

    /* (non-Javadoc)
     * @see org.nexuse2e.Configurable#getParameterMap()
     */
    public Map<String, ParameterDescriptor> getParameterMap() {

        return Collections.unmodifiableMap( parameterMap );
    }

} // FileSystemLoadPipelet
