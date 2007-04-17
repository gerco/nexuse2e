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

import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.Constants.ParameterType;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.pojo.MessagePayloadPojo;

/**
 * @author gesch
 *
 */
public class FileSystemLoadPipelet extends AbstractOutboundBackendPipelet {

    
    /**
     * Default constructor.
     */
    public FileSystemLoadPipelet() {

        
        parameterMap.put( "directory", new ParameterDescriptor( ParameterType.STRING, "Directory", "Target directory",
                "/nexus/dump" ) );
        parameterMap.put( "password", new ParameterDescriptor( ParameterType.PASSWORD, "Password", "Secure password",
                "" ) );
        parameterMap
                .put( "username", new ParameterDescriptor( ParameterType.STRING, "Username", "Login Username", "" ) );
        //        DropdownParameter dropdown = new DropdownParameter();
        //        dropdown.addElement( "Testing 1", "a" );
        //        dropdown.addElement( "Testing 2", "b" );
        //        dropdown.addElement( "Testing 3", "c" );
        //        dropdown.setSelectedIndex( 0 );
        //        parameterMap.put( "dummyDropDown", new ParameterDescriptor( ParameterType.DROPDOWN, "Test Dropdown",
        //                "Please make your choice", dropdown ) );
        //        EnumerationParameter enumeration = new EnumerationParameter();
        //        enumeration.putElement( "firstKey", "First Value" );
        //        enumeration.putElement( "secondKey", "Second Value" );
        //        parameterMap.put( "enum", new ParameterDescriptor( ParameterType.ENUMERATION, "Magic Enum",
        //                "Extesible key/value pairs", enumeration ) );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.backend.pipelets.AbstractOutboundBackendPipelet#processPayloadAvailable(org.nexuse2e.messaging.MessageContext)
     */
    @Override
    public MessageContext processPayloadAvailable( MessageContext backendPipeletParameter )
            throws NexusException {

        return backendPipeletParameter;
    } // processPayloadAvailable

    /* (non-Javadoc)
     * @see org.nexuse2e.backend.pipelets.AbstractOutboundBackendPipelet#processPrimaryKeyAvailable(org.nexuse2e.messaging.MessageContext)
     */
    @Override
    public MessageContext processPrimaryKeyAvailable( MessageContext messageContext )
            throws NexusException {

        byte[] documentBuffer = null; // The binary data buffer that will hold the document
        String newPrimaryKey = (String) messageContext.getData(); // Cast primary key to correct type
        String fileName = null;

        if ( ( messageContext == null ) || ( messageContext.getMessagePojo() == null ) ) {
            throw new NexusException( "MessageContext not properly initialized, missing MessagePojo!" );
        }

        StringTokenizer tokens = new StringTokenizer( newPrimaryKey, "," );
        List<MessagePayloadPojo> messagePayloads = new ArrayList<MessagePayloadPojo>();
        int count = tokens.countTokens();

        for ( int i = 0; i < count; i++ ) {
            fileName = ( (String) tokens.nextElement() ).trim();

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
                    //System.err.println( "FileConnector - composeDocument: IOException: " + ioEx );
                    throw new NexusException( ioEx.getMessage() ); // Pass exception to NEXUSe2e engine using correct exception type
                } // try/catch

            } else { // if
                throw new NexusException( "FileConnector - No primary key specified." );
            } // if

            // Determine the MIME type of the document
            MimetypesFileTypeMap mimetypesFileTypeMap = (MimetypesFileTypeMap) FileTypeMap.getDefaultFileTypeMap();
            String mimeType = mimetypesFileTypeMap.getContentType( fileName );

            // Prepare the Payload and set the MIME content type
            MessagePayloadPojo messagePayloadPojo = new MessagePayloadPojo( messageContext.getMessagePojo(),
                    i, mimeType, Engine.getInstance().getIdGenerator(
                            org.nexuse2e.Constants.ID_GENERATOR_MESSAGE_PAYLOAD ).getId(), documentBuffer, new Date(),
                    new Date(), 1 );
            messagePayloads.add( messagePayloadPojo );

        } // for
        messageContext.getMessagePojo().setMessagePayloads( messagePayloads );

        return messageContext;
    } // processPrimaryKeyAvailable

    /* (non-Javadoc)
     * @see org.nexuse2e.Configurable#getParameterMap()
     */
    public Map<String, ParameterDescriptor> getParameterMap() {

        return Collections.unmodifiableMap( parameterMap );
    }

    public void activate() {

        // TODO Auto-generated method stub
        
    }

    public void deactivate() {

        // TODO Auto-generated method stub
        
    }

    public void initialize() {

        // TODO Auto-generated method stub
        
    }

    public void teardown() {

        // TODO Auto-generated method stub
        
    }
} // FileSystemLoadPipelet
