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
package org.nexuse2e.messaging.httpplain;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.nexuse2e.NexusException;
import org.nexuse2e.messaging.AbstractPipelet;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.pojo.MessagePayloadPojo;

public class HTTPPlainMessageUnpacker extends AbstractPipelet {

    private static Logger       LOG = Logger.getLogger( HTTPPlainMessageUnpacker.class );

    
    /**
     * Default constructor.
     */
    public HTTPPlainMessageUnpacker() {

    }

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.MessageUnpackager#processMessage(com.tamgroup.nexus.e2e.persistence.pojo.MessagePojo, byte[])
     */
    public MessageContext processMessage( MessageContext messagePipeletParameter )
            throws IllegalArgumentException, IllegalStateException, NexusException {

        byte[] payloadData = null;
        MessagePayloadPojo messagePayloadPojo = new MessagePayloadPojo();

        Object object = messagePipeletParameter.getGenericData();
        if ( !( object instanceof HttpServletRequest ) ) {
            LOG.error( "Unable to process message: raw data not of type HttpServletRequest!" );
            throw new IllegalArgumentException( "Unable to process message: raw data not of type HttpServletRequest!" );
        }

        HttpServletRequest request = (HttpServletRequest) object;

        try {
            payloadData = getContentFromRequest( request );

            messagePayloadPojo.setMessage( messagePipeletParameter.getMessagePojo() );
            messagePayloadPojo.setPayloadData( payloadData );
            if ( request.getContentType() != null ) {
                messagePayloadPojo.setMimeType( request.getContentType() );
            } else {
                messagePayloadPojo.setMimeType( "text/xml" );
            }
            messagePayloadPojo.setCreatedDate( new Date() );
            messagePayloadPojo.setModifiedDate( messagePayloadPojo.getCreatedDate() );
            messagePayloadPojo.setModifiedNxUserId( org.nexuse2e.Constants.SYSTEM_USER_ID );
            messagePayloadPojo.setSequenceNumber( 1 );
            messagePayloadPojo.setContentId( "HTTPPlain_contentId" );

            messagePipeletParameter.getMessagePojo().getMessagePayloads().add( messagePayloadPojo );
        } catch ( IOException e ) {
            LOG.error( "Error retrieving payload from HTTP POST: " + e );
            throw new NexusException( "Error retrieving payload from HTTP POST: " + e );
        }

        return messagePipeletParameter;
    }

    /**
     * @param request
     * @param preBuffer
     * @param preBufferLen
     * @return
     * @throws IOException
     */
    public byte[] getContentFromRequest( ServletRequest request ) throws IOException {

        int contentLength = request.getContentLength();
        byte bufferArray[] = new byte[contentLength];
        ServletInputStream inputStream = request.getInputStream();
        int offset = 0;
        int restBytes = contentLength;

        for ( int bytesRead = inputStream.readLine( bufferArray, offset, contentLength ); bytesRead != -1
                && restBytes != 0; bytesRead = inputStream.readLine( bufferArray, offset, restBytes ) ) {
            offset += bytesRead;
            restBytes -= bytesRead;
        }

        return bufferArray;
    }

} // HTTPPlainMessageUnpacker
