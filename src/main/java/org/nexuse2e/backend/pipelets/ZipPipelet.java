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

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.Constants.ParameterType;
import org.nexuse2e.messaging.AbstractPipelet;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.pojo.ConversationPojo;
import org.nexuse2e.pojo.MessagePayloadPojo;
import org.nexuse2e.pojo.MessagePojo;
import org.nexuse2e.pojo.PartnerPojo;

/**
 * @author mbreilmann, s_schulze
 *
 */
public class ZipPipelet extends AbstractPipelet {

    private static Logger      LOG               = Logger.getLogger( ZipPipelet.class );

    public final static String FILE_NAME_PATTERN = "fileNamePattern";
    public final static String TIMESTAMP_PATTERN = "timestampPattern";
    public final static String FILE_EXTENSION    = "fileExtension";
    public final static String COMBINE_PAYLOADS  = "combinePayloads";
    public final static String SEQUENCE_DIGITS   = "sequenceDigits";
    public static final String USE_CONTENT_ID    = "useContentId";
    

    private String             fileNamePattern   = null;
    private String             timestampPattern  = null;
    private String             fileExtension     = null;
    private boolean            combinePayloads   = false;
    private int                sequenceDigits    = 2;
    private boolean            useTimestamp      = true;
    private boolean            useContentId      = false;

    public ZipPipelet() {

        parameterMap.put( FILE_NAME_PATTERN, new ParameterDescriptor( ParameterType.STRING, "File name pattern",
                "The file name pattern to use for payloads", "Payload_${sequence}" ) );
        parameterMap.put( FILE_EXTENSION, new ParameterDescriptor( ParameterType.STRING, "File extension",
                "The file extension to append", "Payloads" ) );
        parameterMap.put( TIMESTAMP_PATTERN, new ParameterDescriptor( ParameterType.STRING, "Timestamp pattern",
                "The timestamp pattern to use", "yyyy-MM-dd HH.mm.ssSSS" ) );
        parameterMap.put( SEQUENCE_DIGITS, new ParameterDescriptor( ParameterType.STRING, "Sequence digits",
                "Number of digits in sequence counter (prepended zeroes)", "2" ) );
        parameterMap.put( COMBINE_PAYLOADS, new ParameterDescriptor( ParameterType.BOOLEAN, "Combine Payloads",
                "Combine Payloads", Boolean.FALSE ) );
        parameterMap.put( USE_CONTENT_ID, new ParameterDescriptor( ParameterType.BOOLEAN, "Use Content ID",
            "Flag whether to use the content ID as the file name. This overrides the other file name related settings.", Boolean.FALSE ) );

    }

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.AbstractPipelet#initialize(org.nexuse2e.configuration.EngineConfiguration)
     */
    @Override
    public void initialize( EngineConfiguration config ) throws InstantiationException {

        LOG.trace( "initializing" );

        String tempParam = getParameter( FILE_NAME_PATTERN );
        if ( !StringUtils.isEmpty( tempParam ) ) {
            fileNamePattern = tempParam;
        } else {
            LOG.error( "No file name pattern provided!" );
            fileNamePattern = "Payload_${sequence}";
        }

        tempParam = getParameter( FILE_EXTENSION );
        if ( !StringUtils.isEmpty( tempParam ) ) {
            fileExtension = tempParam;
            if ( !fileExtension.startsWith( "." ) ) {
                fileExtension = "." + fileExtension;
            }
        } else {
            LOG.error( "No file extension provided!" );
            fileExtension = ".dat";
        }

        tempParam = getParameter( TIMESTAMP_PATTERN );
        if ( !StringUtils.isEmpty( tempParam ) ) {
            timestampPattern = tempParam;
        } else {
            LOG.warn( "No timestamp pattern provided!" );
            timestampPattern = "yyyy-MM-dd HH.mm.ssSSS";
            useTimestamp = false;
        }
        tempParam = getParameter( SEQUENCE_DIGITS );
        if ( !StringUtils.isEmpty( tempParam ) ) {
            try {
                sequenceDigits = Integer.parseInt( tempParam );
            } catch ( NumberFormatException nfe ) {
                LOG.error( "Parameter sequence digits nota number: " + tempParam );
                sequenceDigits = 2;
            }
        } else {
            LOG.warn( "No sequence digits parameter provided!" );
            sequenceDigits = 2;
        }

        Boolean tempBoolean = getParameter( COMBINE_PAYLOADS );
        if ( ( tempBoolean != null ) ) {
            combinePayloads = tempBoolean.booleanValue();
        }
        
        tempBoolean = getParameter( USE_CONTENT_ID );
        if ( ( tempBoolean != null ) ) {
            useContentId = tempBoolean.booleanValue();
        }
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.Pipelet#processMessage(org.nexuse2e.messaging.MessageContext)
     */
    public MessageContext processMessage( MessageContext messageContext ) throws NexusException {

        if ( messageContext != null && messageContext.getMessagePojo().getMessagePayloads() != null
                && messageContext.getMessagePojo().getMessagePayloads().size() > 0 ) {

            byte[] newContent = null;
            String mimeType = "application/x-zip-compressed";

            int sequence = 1;

            SimpleDateFormat sdt = new SimpleDateFormat( timestampPattern );

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ZipOutputStream zos = new ZipOutputStream( baos );
            zos.setMethod( ZipOutputStream.DEFLATED );

            MessagePojo messagePojo = messageContext.getMessagePojo();
            String timestamp = sdt.format( new Date() );
            for ( MessagePayloadPojo messagePayloadPojo : messagePojo.getMessagePayloads() ) {
                try {
                    if ( !combinePayloads ) {
                        baos = new ByteArrayOutputStream();
                        zos = new ZipOutputStream( baos );
                        zos.setMethod( ZipOutputStream.DEFLATED );
                    }

                    String filename = null;
                    // use contentId or generate file name?
                    if ( useContentId ) {
                        filename = messagePayloadPojo.getContentId();
                    } else {
                        // Create sequence with fixed number of digits (leading zeroes)
                        String sequenceTemp = "" + sequence;
                        String sequenceFix = "";
                        for ( int i = 0; i < ( sequenceDigits - sequenceTemp.length() ); i++ ) {
                            sequenceFix += "0";
                        }
                        sequenceFix += sequenceTemp;
    
                        if ( useTimestamp ) {
                            filename = StringUtils.replace( fileNamePattern, "${timestamp}", timestamp );
                        }
                        filename = StringUtils.replace( filename, "${sequence}", "" + sequenceFix );
    
                        sequence++;
    
                        filename += fileExtension;
                    }

                    ZipEntry zipEntry = new ZipEntry( filename );
                    zipEntry.setSize( messagePayloadPojo.getPayloadData().length );
                    if ( messagePayloadPojo.getMimeType() != null ) {
                        zipEntry.setComment( messagePayloadPojo.getMimeType() );
                        zipEntry.setExtra( messagePayloadPojo.getMimeType().getBytes() );
                    }
                    zos.putNextEntry( zipEntry );
                    zos.write( messagePayloadPojo.getPayloadData() );
                    zos.closeEntry();
                    if ( !combinePayloads ) {
                        zos.finish();
                        newContent = baos.toByteArray();
                        if ( newContent != null ) {
                            messagePayloadPojo.setPayloadData( newContent );
                            messagePayloadPojo.setMimeType( mimeType );
                            messagePayloadPojo.setContentId( messagePayloadPojo.getContentId() );
                        } else {
                            throw new NexusException( "No content found after compression of payload." );
                        }
                    }
                } catch ( IOException ioEx ) {
                    throw new NexusException( "Error compressing payload.  Exception: " + ioEx.getMessage() );
                }

            } // for

            if ( combinePayloads ) {
                try {
                    zos.finish();
                    List<MessagePayloadPojo> payloads = messagePojo.getMessagePayloads();
                    if ( !payloads.isEmpty() ) {
                        MessagePayloadPojo messagePayloadPojo = payloads.get( 0 );
                        newContent = baos.toByteArray();
                        if ( newContent != null ) {
                            messagePayloadPojo.setPayloadData( newContent );
                            messagePayloadPojo.setMimeType( mimeType );
                            messagePayloadPojo.setContentId( "COMBINED_PAYLOADS" );
                            payloads.clear();
                            payloads.add( messagePayloadPojo );
                        } else {
                            throw new NexusException( "No content found after compression of payload." );
                        }
                    } else {
                        LOG.warn( "No payloads to compress!" );
                    }
                } catch ( IOException ioEx ) {
                    throw new NexusException( "Error compressing payload.  Exception: " + ioEx.getMessage() );
                }
            }

        }

        return messageContext;
    }

    /**
     * @param args
     */
    public static void main( String[] args ) {

        if ( args.length != 1 ) {
            System.err.println( "No file specified!" );
            return;
        }

        FileInputStream fis;
        try {
            fis = new FileInputStream( args[0] );

            byte[] payloadData = new byte[fis.available()];
            fis.read( payloadData );
            fis.close();

            MessageContext messageContext = new MessageContext();
            MessagePojo messagePojo = new MessagePojo();
            ConversationPojo conversationPojo = new ConversationPojo();
            messagePojo.setConversation( conversationPojo );
            PartnerPojo partnerPojo = new PartnerPojo();
            partnerPojo.setPartnerId( "TestPartner" );
            conversationPojo.setPartner( partnerPojo );
            MessagePayloadPojo messagePayloadPojo = new MessagePayloadPojo();
            messagePayloadPojo.setPayloadData( payloadData );
            messagePayloadPojo.setMimeType( "text/plain" );
            messagePayloadPojo.setContentId( "test" );

            messageContext.setMessagePojo( messagePojo );
            messagePayloadPojo.setMessage( messagePojo );
            messagePojo.getMessagePayloads().add( messagePayloadPojo );

            ZipPipelet zipPipelet = new ZipPipelet();

            zipPipelet.setParameter( FILE_NAME_PATTERN, "Payload_${sequence}_${timestamp}" );
            zipPipelet.setParameter( TIMESTAMP_PATTERN, "yyyyMMddHHmmssSSS" );
            zipPipelet.setParameter( FILE_EXTENSION, "xml" );

            zipPipelet.initialize( null );

            zipPipelet.processMessage( messageContext );

            for ( Iterator<MessagePayloadPojo> payloads = messageContext.getMessagePojo().getMessagePayloads()
                    .iterator(); payloads.hasNext(); ) {
                MessagePayloadPojo tempMessagePayloadPojo = payloads.next();
                /* */
                FileOutputStream fos = new FileOutputStream( args[0] + ".zip" );
                fos.write( tempMessagePayloadPojo.getPayloadData() );
                fos.flush();
                fos.close();
                /* */

                System.out.println( "Mime type: " + tempMessagePayloadPojo.getMimeType() );
                System.out.println( "Payload (" + tempMessagePayloadPojo.getSequenceNumber() + "):\n"
                        + new String( tempMessagePayloadPojo.getPayloadData() ) );
            }

        } catch ( Exception e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println( "Done!" );

    }

}
