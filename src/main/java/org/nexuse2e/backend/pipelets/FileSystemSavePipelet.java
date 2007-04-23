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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;

import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.Constants.ParameterType;
import org.nexuse2e.messaging.AbstractPipelet;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.pojo.MessagePayloadPojo;

public class FileSystemSavePipelet extends AbstractPipelet {

    public static final String               DIRECTORY_PARAM_NAME = "directory";

    
    /**
     * Default constructor.
     */
    public FileSystemSavePipelet() {
    
        parameterMap.put( DIRECTORY_PARAM_NAME, new ParameterDescriptor( ParameterType.STRING, "Save directory",
                "Path to directory where to store files", "" ) );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.Pipelet#processMessage(org.nexuse2e.messaging.MessageContext)
     */
    public MessageContext processMessage( MessageContext messageContext )
            throws NexusException {

        try {
            writePayloadToUniqueFile( (String) getParameter( DIRECTORY_PARAM_NAME ), messageContext );
        } catch ( FileNotFoundException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new NexusException( "Could not create output file!", e );
        } catch ( IOException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new NexusException( "Could not write output file!", e );
        }

        // TODO Auto-generated method stub
        return messageContext;
    }

    /**
     * Generate a unique file name for a payload, finding it's file extension
     * and write data to that file.
     * @param destinationDirectory where the file gets written
     * @param msgInfo about message.
     * @param payload the contents of the message
     * returns the full name of the file written.
     */
    public String writePayloadToUniqueFile( String destinationDirectory, MessageContext messageContext )
            throws FileNotFoundException, IOException {

        return writePayloadToUniqueFile( destinationDirectory, messageContext, false );
    }

    /**
     * Generate a unique file name for a payload, finding it's file extension
     * and write data to that file.
     * @param destinationDirectory where the file gets written
     * @param msgInfo Information about message.
     * @param includeSender Flag whether to include the sender in the file name.
     * @param payload the contents of the message
     * returns the full name of the file written.
     */
    public String writePayloadToUniqueFile( String destinationDirectory,
            MessageContext messageContext, boolean includeSender ) throws FileNotFoundException,
            IOException {

        String retVal = null;
        boolean success = false;

        // Workaround, sometimes filesystem are 'sleeping' and need two tries to
        // get things rolling. Try once and ignore failure.
        try {
            retVal = writePayload( destinationDirectory, messageContext, includeSender );
            success = true;
        } catch ( Exception ex ) {
            // ignore
            success = false;
        }

        // If 1st time failed, try again
        if ( success == false ) {
            retVal = writePayload( destinationDirectory, messageContext, includeSender );
        }

        return retVal;

    }

    /**
     * @param destinationDirectory
     * @param messageContext
     * @param includeSender
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    private String writePayload( String destinationDirectory, MessageContext messageContext,
            boolean includeSender ) throws FileNotFoundException, IOException {

        File destDirFile = new File( destinationDirectory );
        StringBuffer fileName = new StringBuffer();

        List<MessagePayloadPojo> messagePayloadPojos = messageContext.getMessagePojo().getMessagePayloads();
        Iterator<MessagePayloadPojo> iterator = messagePayloadPojos.iterator();
        if ( iterator.hasNext() ) {
            DateFormat df = new SimpleDateFormat( "yyyyMMddHHmmss" );
            MessagePayloadPojo payload = iterator.next();
            if ( includeSender ) {
                fileName.append( messageContext.getPartner().getPartnerId() + "_"
                        + df.format( messageContext.getMessagePojo().getCreatedDate() ) + "_"
                        + payload.getSequenceNumber() );
            } else {
                fileName.append( df.format( messageContext.getMessagePojo().getCreatedDate() ) + "_"
                        + payload.getSequenceNumber() );
            }
            fileName.append( "_" ); // separator for java random part of filename

            String fileExtension = ".xml"; // getFileExtension( payload.getContentType() );

            // null fileExtension is supported
            File tmpFile = File.createTempFile( fileName.toString(), fileExtension, destDirFile );
            fileName = new StringBuffer( tmpFile.getPath() );

            BufferedOutputStream fileOutputStream = new BufferedOutputStream(
                    new FileOutputStream( fileName.toString() ) );

            fileOutputStream.write( payload.getPayloadData() );
            fileOutputStream.flush();
            fileOutputStream.close();
        }

        return fileName.toString();
    }
    
} // FileSystemSavePipelet
