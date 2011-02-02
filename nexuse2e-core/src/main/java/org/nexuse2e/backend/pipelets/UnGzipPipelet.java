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

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;

import org.apache.log4j.Logger;
import org.nexuse2e.NexusException;
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
public class UnGzipPipelet extends AbstractPipelet {

    private static Logger LOG = Logger.getLogger( UnGzipPipelet.class );

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.Pipelet#processMessage(org.nexuse2e.messaging.MessageContext)
     */
    public MessageContext processMessage( MessageContext messageContext ) throws NexusException {

        final int BUFFER = 4096;

        String mimeType = "unknown";
        int count = 0;
        byte data[] = new byte[BUFFER];

        MessagePojo messagePojo = messageContext.getMessagePojo();
        for ( MessagePayloadPojo messagePayloadPojo : messagePojo.getMessagePayloads() ) {
            byte payloadData[] = messagePayloadPojo.getPayloadData();

            try {
                GZIPInputStream gzis = new GZIPInputStream( new ByteArrayInputStream( payloadData ) );
                
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                BufferedOutputStream bos = new BufferedOutputStream( baos, BUFFER );
                while ( ( count = gzis.read( data, 0, BUFFER ) ) != -1 ) {
                    bos.write( data, 0, count );
                }
                bos.flush();
                bos.close();

                gzis.close();

                messagePayloadPojo.setPayloadData( baos.toByteArray() );
                
                if ( LOG.isDebugEnabled() ) {
	                LOG.debug( "Uncompressed size of file: "
	                        + messagePayloadPojo.getPayloadData().length );
                }
                
                messagePayloadPojo.setMimeType( mimeType ); // always unknown?
                
            } catch ( IOException ioEx ) {
                throw new NexusException( "Error decompressing message payload.  Exception:  " + ioEx );
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

            messageContext.setMessagePojo( messagePojo );
            messagePayloadPojo.setMessage( messagePojo );
            messagePojo.getMessagePayloads().add( messagePayloadPojo );

            new UnGzipPipelet().processMessage( messageContext );

            for ( Iterator<MessagePayloadPojo> payloads = messageContext.getMessagePojo().getMessagePayloads()
                    .iterator(); payloads.hasNext(); ) {
                MessagePayloadPojo tempMessagePayloadPojo = payloads.next();
                /*
                FileOutputStream fos = new FileOutputStream( "/Volumes/myby/temp/unzip_output.dat" );
                fos.write( tempMessagePayloadPojo.getPayloadData() );
                fos.flush();
                fos.close();
                */

                System.out.println( "Mime type: " + tempMessagePayloadPojo.getMimeType() );
                System.out.println( "Payload (" + tempMessagePayloadPojo.getSequenceNumber() + "):\n"
                        + new String( tempMessagePayloadPojo.getPayloadData() ) );
            }

        } catch ( Exception e ) {
            e.printStackTrace();
        }
        System.out.println( "Done!" );

    }

}
