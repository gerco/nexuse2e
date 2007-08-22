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
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.nexuse2e.NexusException;
import org.nexuse2e.messaging.AbstractPipelet;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.pojo.MessagePayloadPojo;
import org.nexuse2e.pojo.MessagePojo;

/**
 * @author mbreilmann
 *
 */
public class UnZipPipelet extends AbstractPipelet {

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.Pipelet#processMessage(org.nexuse2e.messaging.MessageContext)
     */
    public MessageContext processMessage( MessageContext messageContext ) throws NexusException {

        byte[] newContent = null;
        String mimeType = null;

        MessagePojo messagePojo = messageContext.getMessagePojo();
        for ( MessagePayloadPojo messagePayloadPojo : messagePojo.getMessagePayloads() ) {
            try {
                ZipInputStream zis = new ZipInputStream( new ByteArrayInputStream( messagePayloadPojo.getPayloadData() ) );
                ZipEntry zipEntry = zis.getNextEntry();
                zis.closeEntry();

                long zipSize = zipEntry.getSize();
                mimeType = new String( zipEntry.getExtra() );

                zis = new ZipInputStream( new ByteArrayInputStream( messagePayloadPojo.getPayloadData() ) );
                zipEntry = zis.getNextEntry();

                newContent = new byte[(int) zipSize];
                new BufferedInputStream( zis ).read( newContent, 0, (int) zipSize );

                zis.close();
            } catch ( IOException ioEx ) {
                throw new NexusException( "Error decompressing message payload.  Exception:  " + ioEx );
            }

            if ( newContent != null && mimeType != null ) {
                messagePayloadPojo.setPayloadData( newContent );
                messagePayloadPojo.setMimeType( mimeType );
            } else {
                throw new NexusException( "No content or mime type found after decompressing payload data." );
            }
        }

        return messageContext;
    }

}
