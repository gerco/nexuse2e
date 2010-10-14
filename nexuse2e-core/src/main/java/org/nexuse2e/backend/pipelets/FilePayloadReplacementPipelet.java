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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.activation.FileTypeMap;
import javax.activation.MimetypesFileTypeMap;

import org.apache.log4j.Logger;
import org.nexuse2e.NexusException;
import org.nexuse2e.Constants.BeanStatus;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.Constants.ParameterType;
import org.nexuse2e.messaging.AbstractPipelet;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.pojo.MessagePayloadPojo;

/**
 * @author gesch
 *
 */
public class FilePayloadReplacementPipelet extends AbstractPipelet {

    private static Logger      LOG       = Logger.getLogger( FilePayloadReplacementPipelet.class );

    public static final String FILE_NAME = "filename";
    public static final String PASSWORD  = "password";
    public static final String USERNAME  = "username";

    private String             fileName  = null;

    /**
     * Default constructor.
     */
    public FilePayloadReplacementPipelet() {

        parameterMap.put( FILE_NAME, new ParameterDescriptor( ParameterType.STRING, "File", "File to replace payload",
                "" ) );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.AbstractPipelet#initialize(org.nexuse2e.configuration.EngineConfiguration)
     */
    @Override
    public void initialize( EngineConfiguration config ) throws InstantiationException {

        File testFile = null;

        String fileNameValue = getParameter( FILE_NAME );
        if ( ( fileNameValue != null ) && ( fileNameValue.length() != 0 ) ) {
            fileName = fileNameValue;
            testFile = new File( fileName );
            if ( !testFile.exists() ) {
                status = BeanStatus.ERROR;
                LOG.error( "File does not exist!" );
                return;
            }
        } else {
            status = BeanStatus.ERROR;
            LOG.error( "No value for setting 'xslt file' provided!" );
            return;
        }

        LOG.trace( "fileName  : " + fileName );

        super.initialize( config );
    }

    /**
     * @param messageContext
     * @return
     * @throws IllegalArgumentException
     * @throws IllegalStateException
     * @throws NexusException
     */
    public MessageContext processMessage( MessageContext messageContext ) throws IllegalArgumentException,
            IllegalStateException, NexusException {

        byte[] documentBuffer = null; // The binary data buffer that will hold the document

        if ( ( messageContext == null ) || ( messageContext.getMessagePojo() == null ) ) {
            throw new NexusException( "MessageContext not properly initialized, missing MessagePojo!" );
        }

        List<MessagePayloadPojo> messagePayloads = messageContext.getMessagePojo().getMessagePayloads();

        for (MessagePayloadPojo messagePayloadPojo : messagePayloads) {
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

            // Replace the Payload and set the MIME content type
            messagePayloadPojo.setPayloadData( documentBuffer );
            messagePayloadPojo.setMimeType( mimeType );

        } // for

        return messageContext;
    } // processPrimaryKeyAvailable

    /* (non-Javadoc)
     * @see org.nexuse2e.Configurable#getParameterMap()
     */
    public Map<String, ParameterDescriptor> getParameterMap() {

        return Collections.unmodifiableMap( parameterMap );
    }

} // FilePayloadReplacementPipelet.
