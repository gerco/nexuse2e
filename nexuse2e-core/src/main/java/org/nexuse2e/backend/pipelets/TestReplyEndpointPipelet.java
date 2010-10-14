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
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.nexuse2e.NexusException;
import org.nexuse2e.Constants.BeanStatus;
import org.nexuse2e.backend.pipelets.helper.RequestResponseData;
import org.nexuse2e.backend.pipelets.helper.ResponseSender;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.Constants.ParameterType;
import org.nexuse2e.logging.LogMessage;
import org.nexuse2e.messaging.AbstractPipelet;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.pojo.MessagePayloadPojo;

public class TestReplyEndpointPipelet extends AbstractPipelet {

    private static Logger      LOG                             = Logger.getLogger( TestReplyEndpointPipelet.class );

    public static final String FILE_NAME_PARAM_NAME            = "fileName";
    public static final String DIRECTORY_PARAM_NAME            = "directory";
    public static final String ACTION_PARAM_NAME               = "action";
    public static final String DELAY_PARAM_NAME                = "delay";
    public static final String USE_ORIGINAL_MESSAGE_PARAM_NAME = "useOriginalMessage";

    private int                delay                           = 1000;
    private String             action                          = null;
    private String             fileName                        = null;
    private String             directory                       = null;
    private String             fileContent                     = null;
    private boolean            useOriginalMessage              = true;

    /**
     * Default constructor.
     */
    public TestReplyEndpointPipelet() {

        super();
        parameterMap.put( FILE_NAME_PARAM_NAME, new ParameterDescriptor( ParameterType.STRING, "File Name",
                "File to return as a response (optional).", "" ) );
        parameterMap.put( DIRECTORY_PARAM_NAME, new ParameterDescriptor( ParameterType.STRING, "Directory",
                "Inbound directory to stare payloads.", "" ) );
        parameterMap.put( ACTION_PARAM_NAME, new ParameterDescriptor( ParameterType.STRING, "Action",
                "Action to trigger for outbound message.", "" ) );
        parameterMap.put( DELAY_PARAM_NAME, new ParameterDescriptor( ParameterType.STRING, "Delay",
                "Delay in milliseconds before outbound message is sent.", "1000" ) );
        parameterMap.put( USE_ORIGINAL_MESSAGE_PARAM_NAME, new ParameterDescriptor( ParameterType.BOOLEAN,
                "Use Original Message", "Use Original Message to retrieve XML.", Boolean.TRUE ) );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.AbstractPipelet#initialize(org.nexuse2e.configuration.EngineConfiguration)
     */
    @Override
    public void initialize( EngineConfiguration config ) throws InstantiationException {

        String actionValue = getParameter( ACTION_PARAM_NAME );
        if ( actionValue == null ) {
            LOG.error( "No value for setting 'action' provided!" );
            return;
        } else {
            action = actionValue;
        }

        File testFile = null;

        String fileNameValue = getParameter( FILE_NAME_PARAM_NAME );
        if ( ( fileNameValue != null ) && ( fileNameValue.length() != 0 ) ) {
            fileName = fileNameValue;
            testFile = new File( fileName );
            if ( !testFile.exists() ) {
                status = BeanStatus.ERROR;
                LOG.error( "Response file does not exist!" );
                return;
            }

            fileContent = loadFile( testFile );

        } else {
            status = BeanStatus.ERROR;
            LOG.info( "No value for setting 'fileName' provided!" );
        }

        String directoyValue = getParameter( DIRECTORY_PARAM_NAME );
        if ( ( directoyValue != null ) && ( directoyValue.length() != 0 ) ) {
            directory = directoyValue;
            testFile = new File( directory );
            if ( !testFile.exists() || !testFile.isDirectory() ) {
                status = BeanStatus.ERROR;
                LOG.error( "Directory does not exist: " + testFile.getAbsolutePath() );
                return;
            }

        } else {
            status = BeanStatus.ERROR;
            LOG.error( "No value for setting 'directory' provided!" );
            return;
        }

        String delayString = getParameter( DELAY_PARAM_NAME );
        delay = 1000;
        if ( ( delayString != null ) && ( delayString.length() != 0 ) ) {
            delay = Integer.parseInt( delayString );
        }

        Boolean useOriginalMessageValue = getParameter( USE_ORIGINAL_MESSAGE_PARAM_NAME );
        if ( useOriginalMessageValue != null ) {
            useOriginalMessage = useOriginalMessageValue.booleanValue();
        }
        super.initialize( config );
    }

    public MessageContext processMessage( MessageContext messageContext ) throws NexusException {

        LOG.debug( "Entered TestReplyEndpointPipelet.processMessage..." );
        RequestResponseData requestResponseData = null;
        String requestString = null;

        for (MessagePayloadPojo messagePayloadPojo : messageContext.getMessagePojo().getMessagePayloads()) {

            if ( ( directory != null ) && ( directory.length() != 0 ) ) {
                try {
                    writePayloadToUniqueFile( directory, messageContext, true );
                } catch ( Exception e ) {
                    e.printStackTrace();
                    throw new NexusException( e );
                }
            }

            if ( ( messagePayloadPojo != null ) && ( messagePayloadPojo.getPayloadData() != null ) ) {
                if ( useOriginalMessage ) {
                    List<MessagePayloadPojo> messagePayloadPojos = messageContext.getOriginalMessagePojo()
                            .getMessagePayloads();
                    Iterator<MessagePayloadPojo> iterator = messagePayloadPojos.iterator();
                    if ( iterator.hasNext() ) {
                        MessagePayloadPojo payload = iterator.next();
                        if ( ( payload != null ) && ( payload.getPayloadData() != null ) ) {
                            requestString = new String( payload.getPayloadData() );
                        }
                    }
                } else {
                    requestString = new String( messagePayloadPojo.getPayloadData() );
                }

                if ( ( fileContent != null ) && ( fileContent.length() != 0 ) ) {
                    requestResponseData = new RequestResponseData( 0, fileContent, requestString );
                } else {
                    requestResponseData = new RequestResponseData( 0,
                            new String( messagePayloadPojo.getPayloadData() ), requestString );
                }

                // Trigger new response message
                new Thread( new ResponseSender( messageContext.getChoreography().getName(), messageContext.getPartner()
                        .getPartnerId(), messageContext.getConversation().getConversationId(), action,
                        requestResponseData, delay ) ).start();
            }
        }
        LOG.debug( "Done!" );

        return messageContext;
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
    private String writePayloadToUniqueFile( String destinationDirectory, MessageContext messageContext,
            boolean includeSender ) throws FileNotFoundException, IOException {

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
    private String writePayload( String destinationDirectory, MessageContext messageContext, boolean includeSender )
            throws FileNotFoundException, IOException {

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

            LOG.trace(new LogMessage(  "Wrote output file: " + fileName.toString(),messageContext.getMessagePojo()) );
        }

        return fileName.toString();
    }

    private String loadFile( File file ) {

        String result = null;

        try {

            // Open the file to read one line at a time
            FileInputStream fis = null;

            // Workaround: Some filesystem need two tries to successfully
            // get a file, expecially remote (network) file shares.
            try {
                fis = new FileInputStream( file );
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
                LOG.error( "Not Enough memory to transfer data of " + fileSize / 1024 + " Kbytes. Available memory is "
                        + memory / 1024 + " Kbytes" );
            }

            byte[] documentBuffer = new byte[fileSize]; // Create a buffer that will hold the data from the file

            bufferedInputStream.read( documentBuffer, 0, fileSize ); // Read the file content into the buffer
            bufferedInputStream.close();

            result = new String( documentBuffer );

        } catch ( IOException ioEx ) { // Handle exceptions related to the file I/O
            ioEx.printStackTrace();
            LOG.error( "IOException: " + ioEx );
        } // try/catch

        return result;
    }

}
