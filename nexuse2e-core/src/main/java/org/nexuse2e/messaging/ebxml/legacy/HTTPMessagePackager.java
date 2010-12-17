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
package org.nexuse2e.messaging.ebxml.legacy;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.mail.internet.ParseException;
import javax.xml.soap.SOAPException;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.nexuse2e.Engine;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.logging.LogMessage;
import org.nexuse2e.messaging.AbstractPipelet;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.pojo.MessagePayloadPojo;
import org.nexuse2e.pojo.MessagePojo;

/**
 * @author gesch
 *
 */
public class HTTPMessagePackager extends AbstractPipelet {

    private static Logger       LOG = Logger.getLogger( HTTPMessagePackager.class );

    private Map<String, Object> parameters;

    /**
     * Default constructor.
     */
    public HTTPMessagePackager() {

        parameters = new HashMap<String, Object>();
        frontendPipelet = true;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.MessagePipelet#processMessage(org.nexuse2e.messaging.MessageContext)
     */
    public MessageContext processMessage( MessageContext messageContext ) {

        String serializedSOAPMessage = null;
        MessagePojo messagePojo = messageContext.getMessagePojo();
        LOG.debug( new LogMessage("Entering HTTPMessagePackager.processMessage...",messagePojo) );

        try {
            if ( messagePojo.getType() == org.nexuse2e.messaging.Constants.INT_MESSAGE_TYPE_ACK ) {
                serializedSOAPMessage = getJAXMSOAPAck( messagePojo );
            } else {
                serializedSOAPMessage = getJAXMSOAPMsg( messagePojo );
            }
        } catch ( Exception ex ) {
            ex.printStackTrace();
            throw new IllegalArgumentException( ex.toString() );
        }

        if ( serializedSOAPMessage != null ) {
            messageContext.setData( serializedSOAPMessage.getBytes() );
            //LOG.trace( new String(messageContext.getData()) );
        } else {
            throw new IllegalArgumentException( "unable to create SOAPMessage" );
        }

        return messageContext;
    }

    // TODO move to header (for ack and normal msg)
    //    method.setRequestHeader( "SOAPAction", "\"ebXML\"" );
    //    method.setRequestHeader( "Content-Type", contentType.toString() );

    /**
     *
     */
    private static String getJAXMSOAPMsg( MessagePojo messagePojo ) throws SOAPException, ParseException {

        String soapId = getSOAPId( messagePojo );
        String hdrContentId = "Content-ID: " + soapId;
        // TODO (encoding) which encoding is used for headers ?
        String ebXMLHeader = new String( messagePojo.getHeaderData() );

        StringBuffer msgBuffer = new StringBuffer();
        msgBuffer.append( Constants.MIMEPARTBOUNDARY + "\n" );
        // Add ebXML Header
        msgBuffer.append( hdrContentId + "\n" );
        msgBuffer.append( Constants.HDRCONTENTTYPE + "\n\n" );
        msgBuffer.append( ebXMLHeader + "\n\n" );

        if ( messagePojo.getMessagePayloads() != null && messagePojo.getMessagePayloads().size() > 0 ) {

            for (MessagePayloadPojo payloadPojo : messagePojo.getMessagePayloads()) {
                // add bodyparts
                msgBuffer.append( Constants.MIMEPARTBOUNDARY + "\n" );

                // String payloadContentID = "Content-ID: " + "<" + getContentId( messagePojo.getMessageId(), payloadPojo.getSequenceNumber() ) + ">";
                // MBE: Changed 20100215 due to interop problem
                String payloadContentID = "Content-ID: " + "<"
                + payloadPojo.getContentId() + ">";

                msgBuffer.append( payloadContentID + "\n" );
                msgBuffer.append( "Content-Type: " + payloadPojo.getMimeType() + "\n\n" );

                // TODO is Binary !!!
                //               if ( bodyPartPojo.get.isBinaryType() ) {
                //                   msgBuffer.append( Base64.encode( newPayload.getContent() ) + "\n" );
                //               } else {
                //                   // Get the payload as a string, from the database.
                msgBuffer.append( new String( payloadPojo.getPayloadData() ) + "\n" );
                //               }
            }
        }

        msgBuffer.append( Constants.MIMEPACKBOUNDARY );

        return msgBuffer.toString();
    }

    /**
     *
     */
    private static String getJAXMSOAPAck( MessagePojo messagePojo ) throws ParseException {

        String soapId = getSOAPId( messagePojo );
        String hdrContentId = "Content-ID: " + soapId;
        // TODO (encoding) which encoding is used for headers ?
        String ackHeader = new String( messagePojo.getHeaderData() );

        StringBuffer ackBuffer = new StringBuffer();
        ackBuffer.append( Constants.MIMEPARTBOUNDARY + "\n" );
        ackBuffer.append( hdrContentId + "\n" );
        ackBuffer.append( Constants.HDRCONTENTTYPE + "\n\n" );
        ackBuffer.append( ackHeader + "\n\n" );
        ackBuffer.append( Constants.MIMEPACKBOUNDARY );

        return ackBuffer.toString();
    }

    /**
     * Retrieve a soap/mime contentId.
     */
    private static String getSOAPId( MessagePojo nexusMsg ) {

        String soapID = "<" + nexusMsg.getMessageId() + nexusMsg.getTRP().getProtocol() + "-Header" + ">";
        return soapID;
    }

    /**
     * Retrieve standard BodyPart content-Id based on it's position in the Enumeration (message ID + "-body" + position).
     * @param position Position of the BodyPart relative to 1.
     * @return conentId based on the BodyPart position parameter.
     */
    public static String getContentId( String id, int position ) {

        String contentId = null;
        contentId = id + "-body" + ( position );
        return contentId;
    }

    public void afterPropertiesSet() throws Exception {

        // TODO Auto-generated method stub

    }

    @SuppressWarnings("unchecked")
    public <T> T getParameter( String name ) {

        return (T) parameters.get( name );
    }

    @SuppressWarnings("unchecked")
    public Map<String, ParameterDescriptor> getParameterMap() {

        return Collections.EMPTY_MAP;
    }

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
