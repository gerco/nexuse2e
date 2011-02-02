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
package org.nexuse2e.messaging.ebxml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.nexuse2e.Engine;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.logging.LogMessage;
import org.nexuse2e.messaging.AbstractPipelet;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.messaging.ebxml.v20.Constants;
import org.nexuse2e.pojo.MessagePayloadPojo;
import org.nexuse2e.pojo.MessagePojo;

/**
 * @author mbreilmann, sschulze
 *
 */
public class HTTPMessageUnpackager extends AbstractPipelet {

    private static Logger       LOG = Logger.getLogger( HTTPMessageUnpackager.class );

    private static String		CRLF = "\r\n";
    
    private Map<String, Object> parameters;

    /**
     * Default constructor.
     */
    public HTTPMessageUnpackager() {

        parameters = new HashMap<String, Object>();
        frontendPipelet = true;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.MessageUnpackager#processMessage(com.tamgroup.nexus.e2e.persistence.pojo.MessagePojo, byte[])
     */
    public MessageContext processMessage( MessageContext messageContext ) throws IllegalArgumentException,
            IllegalStateException {

        MessagePojo messagePojo = messageContext.getMessagePojo();
        Object object = messageContext.getData();
        if ( !( object instanceof byte[] ) ) {
            throw new IllegalArgumentException( "Unable to process message: raw data not of type byte[] but: "
                    + object.getClass().getName() );
        }

        // strip http header information in put in mime message for decoding
        StringBuffer sb = new StringBuffer();
        if ( messagePojo.getCustomParameters().get( Constants.PARAMETER_PREFIX_HTTP + "message-id" ) != null ) {
            sb.append( "Message-ID: "
                    + messagePojo.getCustomParameters().get( Constants.PARAMETER_PREFIX_HTTP + "message-id" ) + CRLF );
        }
        String contentType = messagePojo.getCustomParameters().get( Constants.PARAMETER_PREFIX_HTTP + "content-type" );
        sb.append( "Mime-Version: 1.0" + CRLF );
        sb.append( "Content-Type: " + contentType + CRLF + CRLF );

        String stringData = sb.toString();
        byte[] data = stringData.getBytes();
        int preBufLen = stringData.length();

        try {

            //            byte[] packedMessage = getContentFromRequest( request, data, preBufLen );

            int payloadLength = ( (byte[]) messageContext.getData() ).length;
            byte[] packedMessage = new byte[payloadLength + preBufLen];
            System.arraycopy( data, 0, packedMessage, 0, preBufLen );
            System.arraycopy( (byte[]) messageContext.getData(), 0, packedMessage, preBufLen, payloadLength );

            if ( LOG.isTraceEnabled() ) {
                LOG.trace(new LogMessage(  "--------------",messagePojo) );
                LOG.trace(new LogMessage(  new String( packedMessage ),messagePojo) );
                LOG.trace(new LogMessage(  "--------------",messagePojo) );
            }
            
            
            byte[] bodyPart = null;
            List<MessagePayloadPojo> payloads = new ArrayList<MessagePayloadPojo>();

            MimeBodyPart[] messageBodyParts = decode( packedMessage );
            MimeBodyPart headerMimeBodyPart = messageBodyParts[0];

            String msgHdr = (String) headerMimeBodyPart.getContent();
            LOG.trace( new LogMessage( "Message Header: '" + msgHdr + "'",messagePojo) );
            if ( msgHdr != null ) {
                msgHdr = msgHdr.trim();
            }

            LOG.trace( new LogMessage( "Message Header Content Type: '" + headerMimeBodyPart.getContentType() + "'" ,messagePojo));
            messagePojo.setHeaderData( msgHdr.getBytes() );
            messagePojo.setMessagePayloads( payloads );

            for ( int i = 1; i < messageBodyParts.length; i++ ) {
                MimeBodyPart mimeBodyPart = messageBodyParts[i];
                Object bodyPartContent = mimeBodyPart.getContent();
                if ( bodyPartContent instanceof ByteArrayInputStream ) {
                    ByteArrayInputStream contentIS = (ByteArrayInputStream) bodyPartContent;
                    if ( isBinaryType( mimeBodyPart.getContentType() ) ) {
                    	LOG.trace(new LogMessage("extracting binary content",messageContext));
                        bodyPart = retrieveBinaryContent( contentIS );
                    } else {
                    	LOG.trace(new LogMessage("extracting text content",messageContext));
                        bodyPart = retrieveContent( contentIS );
                    }
                } else {
                	
                	LOG.trace("BodyPart Mimetype: "+mimeBodyPart.getContentType());
                	
                    if ( isBinaryType( mimeBodyPart.getContentType() ) ) {
                    	// TODO decode( packedMessage ) seems to encrypt the content for some reason
                    	bodyPart = Base64.decodeBase64(Base64.decodeBase64((byte[]) ( bodyPartContent )));
                    } else {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        IOUtils.copy( mimeBodyPart.getInputStream(), baos );
                        bodyPart = baos.toByteArray();
                    }
                }

                MessagePayloadPojo messagePayloadPojo = new MessagePayloadPojo();
                messagePayloadPojo.setSequenceNumber( i );
                messagePayloadPojo.setPayloadData( bodyPart );
                String contentId = messageBodyParts[i].getContentID();
                if ( contentId != null ) {
                    if ( contentId.startsWith( "<" ) && contentId.endsWith( ">" ) ) {
                        contentId = contentId.substring( 1, contentId.length() - 2 );
                    }
                }
                LOG.trace( new LogMessage( "contentId:" + contentId,messagePojo) );
                messagePayloadPojo.setContentId( contentId );
                // MBR 20071213: added toLowerCase
                messagePayloadPojo.setMimeType( mimeBodyPart.getContentType().toLowerCase() );
                messagePayloadPojo.setMessage( messagePojo );
                payloads.add( messagePayloadPojo );
            }
        } catch ( IOException e ) {
            e.printStackTrace();
            throw new IllegalArgumentException( e );
        } catch ( MessagingException e ) {
            e.printStackTrace();
            throw new IllegalArgumentException( e );
        } catch ( Exception e ) {
            e.printStackTrace();
            throw new IllegalArgumentException( e );
        }

        return messageContext;

    } // processMessage

    /**
     * dcode a mime message based on a byte array.
     */
    public MimeBodyPart[] decode( byte[] data ) throws IllegalArgumentException {

        MimeBodyPart[] messageBodyParts = null;

        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream( data );
            MimeMessage mimeMessage = new MimeMessage( null, byteArrayInputStream );
            if ( mimeMessage.getContentType().startsWith( "multipart" ) ) {
                MimeMultipart mimeMultipart = (MimeMultipart) mimeMessage.getContent();
                if ( mimeMultipart.getCount() != 0 ) {
                    messageBodyParts = new MimeBodyPart[mimeMultipart.getCount()];
                    for ( int i = 0; i < mimeMultipart.getCount(); i++ ) {
                        MimeBodyPart mimeBodyPart = (MimeBodyPart) mimeMultipart.getBodyPart( i );
                        messageBodyParts[i] = mimeBodyPart;
                    } //for
                } else {
                    LOG.error( "MIME message contains no body parts!" );
                }
            } else if ( mimeMessage.getContentType().startsWith( "text" ) ) {
                // String content = (String) mimeMessage.getContent();
                messageBodyParts = new MimeBodyPart[1];
                MimeBodyPart mimeBodyPart = new MimeBodyPart();
                mimeBodyPart.setContent( mimeMessage.getContent(), mimeMessage.getContentType() );
                messageBodyParts[0] = mimeBodyPart;
            } else {
                LOG.error( "MIME message doesn't seem to be of type multipart: " + mimeMessage.getContentType() );
                throw new IllegalArgumentException( "MIME message doesn't seem to be of type multipart: "
                        + mimeMessage.getContentType() );
            } // if
        } catch ( javax.mail.MessagingException mEx ) {
            LOG.error( "Error reading message: " + mEx + ( ( data != null ) ? "\n" + new String( data ) : "" ), mEx );
            throw new IllegalArgumentException( mEx.toString() );
        } catch ( IOException ioEx ) {
            LOG.error( "Error reading message: " + ioEx + ( ( data != null ) ? "\n" + new String( data ) : "" ), ioEx );
            throw new IllegalArgumentException( ioEx.toString() );
        }

        return messageBodyParts;
    }

    /**
     * Determin if this payload is a wrapper for a binary type.
     * @returns boolean
     */
    public boolean isBinaryType( String contentType ) {

        return Engine.getInstance().isBinaryType( contentType );
    }

    /**
     * Retrieve the Mime content from an Input stream, and do Base64 decoding to the stream.
     * @param newInputStream
     */
    private final byte[] retrieveBinaryContent( InputStream newInputStream ) throws Exception {

    	return Base64.decodeBase64( retrieveContent( newInputStream ) );
    }

    /**
     * Retrieve the Mime content from an Input stream, assuming it's character data already.
     * @param newInputStream
     */
    private final byte[] retrieveContent( InputStream newInputStream ) throws Exception {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IOUtils.copy(newInputStream, baos);
        return baos.toByteArray();
    }

    
    public void afterPropertiesSet() throws Exception {
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Configurable#getParameter(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public <T> T getParameter( String name ) {

        return (T) parameters.get( name );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Configurable#getParameterMap()
     */
    @SuppressWarnings("unchecked")
    public Map<String, ParameterDescriptor> getParameterMap() {

        return Collections.EMPTY_MAP;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Configurable#getParameters()
     */
    public Map<String, Object> getParameters() {

        return Collections.unmodifiableMap( parameters );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Configurable#setParameter(java.lang.String, java.lang.Object)
     */
    public void setParameter( String name, Object value ) {

        parameters.put( name, value );
    }
}
