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
package org.nexuse2e.messaging.ebxml.v10;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import org.apache.log4j.Logger;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.messaging.AbstractPipelet;
import org.nexuse2e.messaging.ErrorDescriptor;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.messaging.TimestampFormatter;
import org.nexuse2e.pojo.ConnectionPojo;
import org.nexuse2e.pojo.MessagePayloadPojo;
import org.nexuse2e.pojo.MessagePojo;

public class HeaderSerializer extends AbstractPipelet {

    private static Logger LOG = Logger.getLogger( HeaderDeserializer.class );

    /**
     * Default constructor.
     */
    public HeaderSerializer() {

    }

    /**
     * 
     */
    @SuppressWarnings("unchecked")
    public MessageContext processMessage( MessageContext messageContext ) throws NexusException {

        try {
            MessagePojo messagePojo = messageContext.getMessagePojo();

            //messagePojo.setCreatedDate( "2006-09-15T17:50:24Z" );

            TimestampFormatter formatter = Engine.getInstance().getTimestampFormatter( "ebxml" );
            String createdDate;
            try {
                Date createdDateObject = messagePojo.getCreatedDate();
                createdDate = formatter.getTimestamp( createdDateObject );
            } catch ( Exception e ) {
                throw new NexusException( "error while processing createdDate field:" + messagePojo.getCreatedDate(), e );
            }

            SOAPFactory soapFactory = SOAPFactory.newInstance();
            MessageFactory messageFactory = MessageFactory.newInstance();
            SOAPMessage soapMessage = messageFactory.createMessage();
            SOAPPart soapPart = soapMessage.getSOAPPart();
            SOAPEnvelope soapEnvelope = soapPart.getEnvelope();
            SOAPBody soapBody = soapEnvelope.getBody();
            SOAPHeader soapHeader = soapEnvelope.getHeader();
            SOAPElement soapElement = null;
            Name name = null;
            boolean ack = false;
            boolean error = false;
            if ( messagePojo.getType() == org.nexuse2e.messaging.Constants.INT_MESSAGE_TYPE_ACK ) {
                ack = true;
            }
            if ( messagePojo.getType() == org.nexuse2e.messaging.Constants.INT_MESSAGE_TYPE_ERROR ) {
                error = false;
            }

            // ENVELOPE ATTRS ------------------------------------------------------
            //  namespace attributes, 'soap-env' namespace handled by JAXM
            soapEnvelope.addNamespaceDeclaration( "eb",
                    "http://www.oasis-open.org/committees/ebxml-msg/schema/msg-header-2_0.xsd" );
            soapEnvelope.addNamespaceDeclaration( "xsi",
                    "http://www.oasis-open.org/committees/ebxml-msg/schema/envelope.xsd" );
            /*
             soapEnvelope.addAttribute( soapFactory.createName( "xmlns:eb" ),
             "http://www.oasis-open.org/committees/ebxml-msg/schema/msg-header-2_0.xsd" );
             soapEnvelope
             .addAttribute( soapFactory.createName( "schemaLocation", "xsi", "http://www.oasis-open.org/committees/ebxml-msg/schema/envelope.xsd" ),
             "http://schemas.xmlsoap.org/soap/envelope/ http://www.oasis-open.org/committees/ebxml-msg/schema/envelope.xsd" );
             */

            // HEADER ATTRS --------------------------------------------------------
            soapHeader
                    .addAttribute(
                            soapFactory.createName( "xsi:schemaLocation" ),
                            "http://www.oasis-open.org/committees/ebxml-msg/schema/msg-header-2_0.xsd http://www.oasis-open.org/committees/ebxml-msg/schema/msg-header-2_0.xsd" );
            soapHeader.addAttribute( soapFactory.createName( "xmlns:eb" ),
                    "http://www.oasis-open.org/committees/ebxml-msg/schema/msg-header-2_0.xsd" );

            // BODY ATTRS ----------------------------------------------------------
            soapBody
                    .addAttribute(
                            soapFactory.createName( "xsi:schemaLocation" ),
                            "http://www.oasis-open.org/committees/ebxml-msg/schema/msg-header-2_0.xsd http://www.oasis-open.org/committees/ebxml-msg/schema/msg-header-2_0.xsd" );
            soapBody.addAttribute( soapFactory.createName( "xmlns:eb" ),
                    "http://www.oasis-open.org/committees/ebxml-msg/schema/msg-header-2_0.xsd" );

            // MESSAGE HEADER ------------------------------------------------------
            name = soapFactory
                    .createName( "MessageHeader", Constants.EBXML_NAMESPACE_PREFIX, Constants.EBXML_NAMESPACE );
            SOAPHeaderElement msgHeader = soapHeader.addHeaderElement( name );
            msgHeader.setMustUnderstand( true );
            msgHeader.addAttribute( soapFactory.createName( Constants.VERSION, Constants.EBXML_NAMESPACE_PREFIX,
                    Constants.EBXML_NAMESPACE ), Constants.EBXML_VERSION );

            // TO & FROM -----------------------------------------------------------
            String from = messagePojo.getCustomParameters().get( Constants.PROTOCOLSPECIFIC_FROM );
            if ( from == null ) {
                //TODO: for testing..
                from = "dummyfrom";
            }
            String fromIdType = messagePojo.getCustomParameters().get( Constants.PROTOCOLSPECIFIC_FROMIDTYPE );
            if ( fromIdType == null ) {
                //              TODO: for testing..
                fromIdType = "dummyFromType";
            }
            String to = messagePojo.getConversation().getPartner().getPartnerId();
            if ( to == null ) {
                //              TODO: for testing..
                to = "dummyto";
            }
            String toIDType = messagePojo.getConversation().getPartner().getPartnerIdType();
            if ( toIDType == null ) {
                //              TODO: for testing..
                toIDType = "dummytoType";
            }
            msgHeader.addChildElement( createPartyElement( soapFactory, "From", from, fromIdType, null ) );
            msgHeader.addChildElement( createPartyElement( soapFactory, "To", to, toIDType, null ) );

            // CPA & ConversationId ------------------------------------------------
            crateSOAPElement( soapFactory, msgHeader, "CPAId", messagePojo.getConversation().getChoreography()
                    .getName() );
            crateSOAPElement( soapFactory, msgHeader, "ConversationId", messagePojo.getConversation()
                    .getConversationId() );

            // SERVICE -------------------------------------------------------------
            //  service is hard coded to  meet spec.  Services are not used.
            String service = messagePojo.getCustomParameters().get( Constants.PROTOCOLSPECIFIC_SERVICE );
            if ( service == null ) {
                service = messagePojo.getConversation().getChoreography().getName();
            }
            soapElement = soapFactory.createElement( "Service", Constants.EBXML_NAMESPACE_PREFIX,
                    Constants.EBXML_NAMESPACE );
            if ( service != null && service.length() > 0 ) {
                soapElement.addAttribute( soapFactory.createName( "type", Constants.EBXML_NAMESPACE_PREFIX,
                        Constants.EBXML_NAMESPACE ), service );
            }

            if ( ack ) {
                soapElement.addTextNode( Constants.ACK_SERVICE );
            } else {

                String serviceVal = new String();
                if ( !( service.startsWith( "uri:" ) || service.startsWith( "urn:" ) ) ) {
                    serviceVal += "uri:";
                }

                soapElement.addTextNode( serviceVal + service );
            }
            msgHeader.addChildElement( soapElement );

            // ACTION --------------------------------------------------------------

            String actionName = null;
            if ( messagePojo.getType() == org.nexuse2e.messaging.Constants.INT_MESSAGE_TYPE_ACK ) {
                actionName = "Acknowledgement";
            } else {
                actionName = messagePojo.getAction().getName();
            }

            crateSOAPElement( soapFactory, msgHeader, "Action", actionName );

            // MESSAGE DATA --------------------------------------------------------
            SOAPElement msgDataEl = soapFactory.createElement( "MessageData", Constants.EBXML_NAMESPACE_PREFIX,
                    Constants.EBXML_NAMESPACE );

            // MESSAGEDATA MESSAGE ID ----------------------------------------------
            soapElement = soapFactory.createElement( "MessageId", Constants.EBXML_NAMESPACE_PREFIX,
                    Constants.EBXML_NAMESPACE );
            soapElement.addTextNode( messagePojo.getMessageId() );
            msgDataEl.addChildElement( soapElement );

            // MESSAGEDATA TIMESTAMP -----------------------------------------------
            soapElement = soapFactory.createElement( Constants.TIMESTAMP_ID, Constants.EBXML_NAMESPACE_PREFIX,
                    Constants.EBXML_NAMESPACE );

            soapElement.addTextNode( createdDate );
            msgDataEl.addChildElement( soapElement );

            if ( ack || error ) {
                // MESSAGEDATA REFTOMESSAGE ID ----------------------------------------------
                soapElement = soapFactory.createElement( "RefToMessageId", Constants.EBXML_NAMESPACE_PREFIX,
                        Constants.EBXML_NAMESPACE );
                soapElement.addTextNode( messagePojo.getReferencedMessage().getMessageId() );
                msgDataEl.addChildElement( soapElement );
            }

            msgHeader.addChildElement( msgDataEl );

            if ( ack ) { // ack
                // ACKNOWLEDGEMENT--------------------------------------------------
                if ( messagePojo.getParticipant().getConnection().isReliable() ) {
                    soapElement = soapFactory.createElement( "QualityOfServiceInfo", Constants.EBXML_NAMESPACE_PREFIX,
                            Constants.EBXML_NAMESPACE );
                    soapElement.addAttribute( soapFactory.createName( "deliverySemantics",
                            Constants.EBXML_NAMESPACE_PREFIX, Constants.EBXML_NAMESPACE ), "OnceAndOnlyOnce" );
                    msgHeader.addChildElement( soapElement );
                }
                createAck( soapFactory, soapHeader, createdDate, messagePojo.getReferencedMessage().getMessageId(),
                        from, fromIdType );
            } else if ( error ) { // error
                createErrorList( soapFactory, soapHeader, messagePojo.getReferencedMessage().getMessageId(),
                        (Vector<ErrorDescriptor>) messageContext.getData() );
            } else { // regular message
                // QUALITY OF SERVICE---------------------------------------------------
                if ( messagePojo.getParticipant().getConnection().isReliable() ) {
                    soapElement = soapFactory.createElement( "QualityOfServiceInfo", Constants.EBXML_NAMESPACE_PREFIX,
                            Constants.EBXML_NAMESPACE );
                    soapElement.addAttribute( soapFactory.createName( "deliverySemantics",
                            Constants.EBXML_NAMESPACE_PREFIX, Constants.EBXML_NAMESPACE ), "OnceAndOnlyOnce" );
                    msgHeader.addChildElement( soapElement );
                }

                name = soapFactory.createName( "TraceHeaderList", Constants.EBXML_NAMESPACE_PREFIX,
                        Constants.EBXML_NAMESPACE );
                SOAPHeaderElement traceHeaderList = soapHeader.addHeaderElement( name );
                traceHeaderList.setMustUnderstand( true );
                traceHeaderList.setActor( Constants.SOAPACTOR );
                traceHeaderList.addAttribute( soapFactory.createName( Constants.VERSION,
                        Constants.EBXML_NAMESPACE_PREFIX, Constants.EBXML_NAMESPACE ), Constants.EBXML_VERSION );

                SOAPElement traceHeader = soapFactory.createElement( "TraceHeader", Constants.EBXML_NAMESPACE_PREFIX,
                        Constants.EBXML_NAMESPACE );
                SOAPElement sender = createPartyElement( soapFactory, "Sender", from, fromIdType, null );
                SOAPElement senderLocation = soapFactory.createElement( "Location", Constants.EBXML_NAMESPACE_PREFIX,
                        Constants.EBXML_NAMESPACE );
                Set<ConnectionPojo> localConnections = messagePojo.getParticipant().getLocalPartner().getConnections();
                String localURI = "http://localhost:8080/NEXUSe2e/handler/ebxml10";
                if ( !localConnections.isEmpty() ) {
                    localURI = localConnections.iterator().next().getUri();
                }
                senderLocation.addTextNode( localURI );
                sender.addChildElement( senderLocation );
                traceHeader.addChildElement( sender );

                SOAPElement receiver = createPartyElement( soapFactory, "Receiver", to, toIDType, null );
                SOAPElement receiverLocation = soapFactory.createElement( "Location", Constants.EBXML_NAMESPACE_PREFIX,
                        Constants.EBXML_NAMESPACE );
                receiverLocation.addTextNode( messagePojo.getParticipant().getConnection().getUri() );
                receiver.addChildElement( receiverLocation );
                traceHeader.addChildElement( receiver );

                soapElement = soapFactory.createElement( Constants.TIMESTAMP_ID, Constants.EBXML_NAMESPACE_PREFIX,
                        Constants.EBXML_NAMESPACE );
                soapElement.addTextNode( createdDate );
                traceHeader.addChildElement( soapElement );

                traceHeaderList.addChildElement( traceHeader );

                name = soapFactory.createName( "Via", Constants.EBXML_NAMESPACE_PREFIX, Constants.EBXML_NAMESPACE );
                SOAPHeaderElement via = soapHeader.addHeaderElement( name );
                via.setMustUnderstand( true );
                via.setActor( Constants.SOAPACTOR );
                via.addAttribute( soapFactory.createName( Constants.VERSION, Constants.EBXML_NAMESPACE_PREFIX,
                        Constants.EBXML_NAMESPACE ), Constants.EBXML_VERSION );
                via.addAttribute( soapFactory.createName( Constants.ACK_REQUESTED, Constants.EBXML_NAMESPACE_PREFIX,
                        Constants.EBXML_NAMESPACE ), Constants.ACK_REQUESTED_VALUE );
                via.addAttribute( soapFactory.createName( Constants.RELIABLE_MESSAGING_METHOD,
                        Constants.EBXML_NAMESPACE_PREFIX, Constants.EBXML_NAMESPACE ),
                        Constants.RELIABLE_MESSAGING_METHOD_VALUE );

                SOAPElement soapManifest = null;

                // MANIFEST --------------------------------------------------------
                name = soapFactory.createName( "Manifest", Constants.EBXML_NAMESPACE_PREFIX, Constants.EBXML_NAMESPACE );
                soapManifest = soapBody.addBodyElement( name );

                soapManifest.addAttribute( soapFactory.createName( Constants.MUSTUNDERSTAND,
                        Constants.EBXML_NAMESPACE_PREFIX, Constants.EBXML_NAMESPACE ), Constants.MUSTUNDERSTAND_VALUE );
                soapManifest.addAttribute( soapFactory.createName( Constants.VERSION, Constants.EBXML_NAMESPACE_PREFIX,
                        Constants.EBXML_NAMESPACE ), Constants.EBXML_VERSION );

                // REFERENCES ------------------------------------------------------

                // newMsg.addManifestEntry( newMsg.getMessageID() + "-body" + ( i + 1 ) );

                Iterator bodyParts = messagePojo.getMessagePayloads().iterator();
                while ( bodyParts.hasNext() ) {
                    MessagePayloadPojo bodyPart = (MessagePayloadPojo) bodyParts.next();
                    LOG.trace( "ContentID:" + bodyPart.getContentId() );
                    createManifestReference( soapFactory, soapManifest, bodyPart.getContentId(), "Payload-"
                            + bodyPart.getSequenceNumber(), bodyPart.getMimeType(), null );
                }

            }
            soapMessage.saveChanges();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            soapMessage.writeTo( baos );
            messagePojo.setHeaderData( baos.toByteArray() );
            LOG.trace( "Message:" + new String( messagePojo.getHeaderData() ) );
        } catch ( NexusException e ) {
            throw e;
        } catch ( Exception e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return messageContext;
    }

    /**
     * Creates acknowledgement elements.
     * 
     * @param soapFactory
     * @param soapHeader
     * @param timestamp
     * @param refMessageId
     * @param from
     * @param FromIDType
     * @return
     * @throws SOAPException
     */
    private SOAPElement createAck( SOAPFactory soapFactory, SOAPHeader soapHeader, String timestamp,
            String refMessageId, String from, String FromIDType ) throws SOAPException {

        Name name = soapFactory.createName( "Acknowledgment", Constants.EBXML_NAMESPACE_PREFIX,
                Constants.EBXML_NAMESPACE );
        SOAPHeaderElement ackEl = soapHeader.addHeaderElement( name );
        ackEl.setActor( Constants.SOAPACTOR );
        ackEl.setMustUnderstand( true );
        ackEl.addAttribute( soapFactory.createName( Constants.VERSION, Constants.EBXML_NAMESPACE_PREFIX,
                Constants.EBXML_NAMESPACE ), Constants.EBXML_VERSION );

        // TIMESTAMP------------------------------------------------------------
        SOAPElement soapEl = soapFactory.createElement( Constants.TIMESTAMP_ID, Constants.EBXML_NAMESPACE_PREFIX,
                Constants.EBXML_NAMESPACE );
        soapEl.addTextNode( timestamp );
        ackEl.addChildElement( soapEl );

        // RefToMessageId-------------------------------------------------------
        /*
         soapEl = soapFactory.createElement( Constants.REFTOMESSAGE_ID, Constants.EBXML_NAMESPACE_PREFIX,
         Constants.EBXML_NAMESPACE );
         soapEl.addTextNode( refMessageId );
         ackEl.addChildElement( soapEl );
         */

        // FROM-----------------------------------------------------------------
        ackEl.addChildElement( createPartyElement( soapFactory, "From", from, FromIDType, null ) );

        return ackEl;
    }

    /**
     * Creates errorList element
     * 
     * @param soapFactory
     * @param soapHeader
     * @param timestamp
     * @param refMessageId
     * @param from
     * @param FromIDType
     * @return
     * @throws SOAPException
     */
    /*  
     <eb:ErrorList eb:id="3490sdo", eb:highestSeverity="error" eb:version="2.0" SOAP:mustUnderstand="1">    
     <eb:Error eb:errorCode="SecurityFailure" eb:severity="Error" eb:location="URI_of_ds:Signature"> 
     <eb:Description xml:lang="en-US">Validation of signature failed<eb:Description> 
     </eb:Error> 
     <eb:Error ...> ... </eb:Error> 
     </eb:ErrorList> 
     */
    private SOAPElement createErrorList( SOAPFactory soapFactory, SOAPHeader soapHeader, String refMessageId,
            Vector<ErrorDescriptor> errors ) throws SOAPException {

        Name name = soapFactory.createName( "ErrorList", Constants.EBXML_NAMESPACE_PREFIX, Constants.EBXML_NAMESPACE );
        SOAPHeaderElement errorListEl = soapHeader.addHeaderElement( name );
        //errorListEl.setActor( Constants.SOAPACTOR );
        errorListEl.setMustUnderstand( true );
        errorListEl.addAttribute( soapFactory.createName( Constants.VERSION, Constants.EBXML_NAMESPACE_PREFIX,
                Constants.EBXML_NAMESPACE ), Constants.EBXML_VERSION );
        errorListEl.addAttribute( soapFactory.createName( "id", Constants.EBXML_NAMESPACE_PREFIX,
                Constants.EBXML_NAMESPACE ), "unknown" );
        int severityCode = -1;
        String highestSeverity = "nothing";
        if ( errors != null ) {
            Iterator<ErrorDescriptor> i = errors.iterator();
            while ( i.hasNext() ) {
                ErrorDescriptor ed = i.next();
                if ( ed.getSeverityCode() > severityCode ) {
                    severityCode = ed.getSeverityCode();
                    try {
                        highestSeverity = ed.getSeverity();
                    } catch ( NexusException e ) {
                        // TODO whats now?
                        e.printStackTrace();
                        highestSeverity = "Error";
                    }
                }
            }
        }

        errorListEl.addAttribute( soapFactory.createName( "highestSeverity", Constants.EBXML_NAMESPACE_PREFIX,
                Constants.EBXML_NAMESPACE ), highestSeverity );

        if ( errors != null ) {
            Iterator<ErrorDescriptor> i = errors.iterator();
            while ( i.hasNext() ) {
                ErrorDescriptor ed = i.next();
                SOAPElement soapEl = soapFactory.createElement( "Error", Constants.EBXML_NAMESPACE_PREFIX,
                        Constants.EBXML_NAMESPACE );
                soapEl.addAttribute( soapFactory.createName( "errorCode", Constants.EBXML_NAMESPACE_PREFIX,
                        Constants.EBXML_NAMESPACE ), ed.getErrorCode() );
                String severity = "error";
                try {
                    severity = ed.getSeverity();
                } catch ( NexusException e ) {
                    LOG.error( "no severity string found for Code:" + ed.getSeverityCode() );
                }
                soapEl.addAttribute( soapFactory.createName( "severity", Constants.EBXML_NAMESPACE_PREFIX,
                        Constants.EBXML_NAMESPACE ), severity );
                soapEl.addAttribute( soapFactory.createName( "location", Constants.EBXML_NAMESPACE_PREFIX,
                        Constants.EBXML_NAMESPACE ), ed.getLocation() );

                errorListEl.addChildElement( soapEl );
            }
        }

        /*
         // TIMESTAMP------------------------------------------------------------
         SOAPElement soapEl = soapFactory.createElement( Constants.TIMESTAMP_ID );
         soapEl.addTextNode( timestamp );
         errorListEl.addChildElement( soapEl );

         // RefToMessageId-------------------------------------------------------
         soapEl = soapFactory.createElement( Constants.REFTOMESSAGE_ID );
         soapEl.addTextNode( refMessageId );
         errorListEl.addChildElement( soapEl );
         // FROM-----------------------------------------------------------------
         errorListEl.addChildElement( createPartyElement( soapFactory, "eb:From", from, FromIDType, null ) );
         */

        return errorListEl;
    }

    /**
     *  Takes a reference object and populates a SOAPelement.
     *
     */
    private void createManifestReference( SOAPFactory soapFactory, SOAPElement parent, String href, String id,
            String type, String role ) throws SOAPException {

        SOAPElement soapEl = soapFactory.createElement( "Reference", Constants.EBXML_NAMESPACE_PREFIX,
                Constants.EBXML_NAMESPACE );
        soapEl.addAttribute( soapFactory.createName( "xmlns:xlink" ), "http://www.w3.org/1999/xlink" );

        soapEl.addAttribute(
                soapFactory.createName( "id", Constants.EBXML_NAMESPACE_PREFIX, Constants.EBXML_NAMESPACE ), id );
        soapEl.addAttribute( soapFactory.createName( "xlink:href" ), href );

        if ( role != null && role.length() != 0 ) {
            soapEl.addAttribute( soapFactory.createName( "xlink:role" ), role );
        }

        if ( type != null && type.length() != 0 ) {
            soapEl.addAttribute( soapFactory.createName( "xlink:type" ), type );
        }

        parent.addChildElement( soapEl );
    }

    /**
     * convenience routine for adding text nodes to soap elements.
     *
     */
    private void crateSOAPElement( SOAPFactory soapFactory, SOAPElement parent, String childName, String childText )
            throws SOAPException {

        LOG.trace( "crateSOAPElement: " + childName );
        SOAPElement soapEl = soapFactory.createElement( childName, Constants.EBXML_NAMESPACE_PREFIX,
                Constants.EBXML_NAMESPACE );
        soapEl.addTextNode( childText );
        parent.addChildElement( soapEl );
    }

    /**
     * Create an ebXML Party element
     * @param id
     * @param value
     * @param type
     * @param location
     * @return
     * @throws SOAPException
     */
    private SOAPElement createPartyElement( SOAPFactory soapFactory, String id, String value, String type,
            String location ) throws SOAPException {

        String party = null;
        SOAPElement soapEl = soapFactory
                .createElement( id, Constants.EBXML_NAMESPACE_PREFIX, Constants.EBXML_NAMESPACE );
        SOAPElement partyId = soapEl.addChildElement( Constants.PARTY_ID, Constants.EBXML_NAMESPACE_PREFIX,
                Constants.EBXML_NAMESPACE );

        if ( type != null && !type.equals( "" ) ) {
            partyId.addAttribute( soapFactory.createName( "type" ), type );
            // partyId.addAttribute( soapFactory.createName( "eb:type" ), type );
            // partyId.addAttribute( soapFactory.createName( "type", Constants.EBXML_NAMESPACE_PREFIX, Constants.EBXML_NAMESPACE ), type );
            party = value;
        } else { // as per ebXML 1.0 spec, if no type attr, value is a uri
            if ( ( value.startsWith( Constants.URI_ID ) == false ) && ( value.indexOf( ":" ) == -1 ) ) {
                party = "uri:" + value;
            } else {
                party = value;
            }
        }

        partyId.addTextNode( party );
        // soapEl.addChildElement( partyId );

        if ( location != null ) {
            crateSOAPElement( soapFactory, soapEl, Constants.LOCATION_ID, location );
        }

        return soapEl;
    } // createPartyElement

    public void afterPropertiesSet() throws Exception {

        // TODO Auto-generated method stub

    }

}
