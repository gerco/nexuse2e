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
import org.nexuse2e.Constants.Severity;
import org.nexuse2e.messaging.AbstractPipelet;
import org.nexuse2e.messaging.ErrorDescriptor;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.messaging.TimestampFormatter;
import org.nexuse2e.pojo.ConnectionPojo;
import org.nexuse2e.pojo.MessagePayloadPojo;
import org.nexuse2e.pojo.MessagePojo;

public class HeaderSerializer extends AbstractPipelet {

    private static Logger LOG = Logger.getLogger( HeaderDeserializer.class );

    //    private static SOAPFactory    soapFactory    = null;
    //    private static MessageFactory messageFactory = null;
    //
    //    static {
    //        String saveSOAPFactory = System.getProperty( "javax.xml.soap.SOAPFactory" );
    //        String saveMessageFactory = System.getProperty( "javax.xml.soap.MessageFactory" );
    //
    //        // Grab soap factories explicitly to make sure we get the ones we ship with
    //        System.setProperty( "javax.xml.soap.SOAPFactory", "com.sun.xml.messaging.saaj.soap.ver1_1.SOAPFactory1_1Impl" );
    //        System.setProperty( "javax.xml.soap.MessageFactory",
    //                "com.sun.xml.messaging.saaj.soap.ver1_1.SOAPMessageFactory1_1Impl" );
    //        try {
    //            soapFactory = SOAPFactory.newInstance();
    //        } catch ( SOAPException e ) {
    //            LOG.error( "Could not instantiate SOAPFactory! " + e );
    //        }
    //        try {
    //            messageFactory = MessageFactory.newInstance();
    //        } catch ( SOAPException e ) {
    //            LOG.error( "Could not instantiate MessageFactory! " + e );
    //        }
    //        
    //        if ( saveSOAPFactory != null ) {
    //            System.setProperty( "javax.xml.soap.SOAPFactory", saveSOAPFactory );
    //        }
    //        if ( saveMessageFactory != null ) {
    //            System.setProperty( "javax.xml.soap.MessageFactory", saveMessageFactory );
    //        }
    //    }

    /**
     * Default constructor.
     */
    public HeaderSerializer() {

        frontendPipelet = true;
    }

    /**
     * 
     */
    @SuppressWarnings("unchecked")
    public MessageContext processMessage( MessageContext messageContext ) throws NexusException {

        try {
            MessagePojo messagePojo = messageContext.getMessagePojo();

            // Test for re-send of ack
            if ( ( messagePojo.getType() == Constants.INT_MESSAGE_TYPE_ACK ) && ( messagePojo.getHeaderData() != null )
                    && ( messagePojo.getHeaderData().length != 0 ) ) {
                LOG.debug( "Re-send of acknowledgment - using existing header." );
            } else {
                SOAPFactory soapFactory = SOAPFactory.newInstance();
                MessageFactory messageFactory = MessageFactory.newInstance();

                //messagePojo.setCreatedDate( "2006-09-15T17:50:24Z" );

                TimestampFormatter formatter = Engine.getInstance().getTimestampFormatter( "ebxml" );
                String createdDate;
                try {
                    Date createdDateObject = messagePojo.getCreatedDate();
                    createdDate = formatter.getTimestamp( createdDateObject );
                } catch ( Exception e ) {
                    throw new NexusException( "error while processing createdDate field:"
                            + messagePojo.getCreatedDate(), e );
                }

                SOAPMessage soapMessage = messageFactory.createMessage();
                soapMessage.setProperty( SOAPMessage.WRITE_XML_DECLARATION, "true" );
                SOAPPart soapPart = soapMessage.getSOAPPart();
                SOAPEnvelope soapEnvelope = soapPart.getEnvelope();
                SOAPBody soapBody = soapEnvelope.getBody();
                SOAPHeader soapHeader = soapEnvelope.getHeader();
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
                soapEnvelope.addNamespaceDeclaration( "eb", "http://www.ebxml.org/namespaces/messageHeader" );
                soapEnvelope.addNamespaceDeclaration( "xlink", "http://www.w3.org/1999/xlink" );
                soapEnvelope.addNamespaceDeclaration( "xsi", "http://www.w3.org/2001/XMLSchema-instance" );

                // HEADER ATTRS --------------------------------------------------------
                //soapHeader.addAttribute( soapFactory.createName( "xmlns:eb" ),
                //        "http://www.ebxml.org/namespaces/messageHeader" );
                // BODY ATTRS ----------------------------------------------------------
                // soapBody.addAttribute( soapFactory.createName( "xsi:schemaLocation" ), "http://www.ebxml.org/namespaces/messageHeader http://www.ebxml.org/namespaces/messageHeader" );
                //soapBody.addAttribute( soapFactory.createName( "xmlns:eb" ),
                //        "http://www.ebxml.org/namespaces/messageHeader" );

                // MESSAGE HEADER ------------------------------------------------------
                name = soapFactory.createName( "MessageHeader", Constants.EBXML_NAMESPACE_PREFIX,
                        Constants.EBXML_NAMESPACE );
                SOAPHeaderElement msgHeader = soapHeader.addHeaderElement( name );
                msgHeader.setMustUnderstand( true );
                msgHeader.addAttribute( soapFactory.createName( Constants.VERSION, Constants.EBXML_NAMESPACE_PREFIX,
                        Constants.EBXML_NAMESPACE ), Constants.EBXML_VERSION );

                // TO & FROM -----------------------------------------------------------
                String from = messagePojo.getParticipant().getLocalPartner().getPartnerId();
                if ( from == null ) {
                    //TODO: for testing..
                    from = "dummyfrom";
                }
                String fromIdType = messagePojo.getParticipant().getLocalPartner().getPartnerIdType();
                ;
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
                    toIDType = "dummyToType";
                }
                msgHeader.addChildElement( createPartyElement( soapFactory, "From", from, fromIdType, null ) );
                msgHeader.addChildElement( createPartyElement( soapFactory, "To", to, toIDType, null ) );

                // CPA & ConversationId ------------------------------------------------
                createSOAPElement( soapFactory, msgHeader, "CPAId", messagePojo.getConversation().getChoreography()
                        .getName() );
                createSOAPElement( soapFactory, msgHeader, "ConversationId", messagePojo.getConversation()
                        .getConversationId() );

                // SERVICE -------------------------------------------------------------
                //  service is hard coded to  meet spec.  Services are not used.
                String service = messagePojo.getCustomParameters().get( Constants.PROTOCOLSPECIFIC_SERVICE );
                if ( service == null ) {
                    service = messagePojo.getConversation().getChoreography().getName();
                }
                
                String serviceVal = "";
                if ( ack ) {
                    serviceVal = Constants.ACK_SERVICE;
                } else {

                    if ( !( service.startsWith( "uri:" ) || service.startsWith( "urn:" ) ) ) {
                        serviceVal += "uri:";
                    }

                    serviceVal += service;
                }
                createSOAPElement( soapFactory, msgHeader, "Service", serviceVal );

                // ACTION --------------------------------------------------------------

                String actionName = null;
                if ( messagePojo.getType() == org.nexuse2e.messaging.Constants.INT_MESSAGE_TYPE_ACK ) {
                    actionName = "Acknowledgment";
                } else {
                    actionName = messagePojo.getAction().getName();
                }

                createSOAPElement( soapFactory, msgHeader, "Action", actionName );

                // MESSAGE DATA --------------------------------------------------------
                SOAPElement msgDataEl = soapFactory.createElement(
                        "MessageData", Constants.EBXML_NAMESPACE_PREFIX, Constants.EBXML_NAMESPACE );
                msgDataEl.removeNamespaceDeclaration( Constants.EBXML_NAMESPACE_PREFIX );

                // MESSAGEDATA MESSAGE ID ----------------------------------------------
                createSOAPElement( soapFactory, msgDataEl, "MessageId", messagePojo.getMessageId() );

                // MESSAGEDATA TIMESTAMP -----------------------------------------------
                createSOAPElement( soapFactory, msgDataEl, Constants.TIMESTAMP_ID, createdDate );

                if ( ack || error ) {
                    // MESSAGEDATA REFTOMESSAGE ID ----------------------------------------------
                    createSOAPElement( soapFactory, msgDataEl, "RefToMessageId", messagePojo.getReferencedMessage().getMessageId() );
                }

                msgHeader.addChildElement( msgDataEl );

                if ( ack ) { // ack
                    // ACKNOWLEDGEMENT--------------------------------------------------
                    if ( messagePojo.getParticipant().getConnection().isReliable() ) {
                        SOAPElement soapElement = msgHeader.addChildElement(
                                "QualityOfServiceInfo", Constants.EBXML_NAMESPACE_PREFIX, Constants.EBXML_NAMESPACE );
                        soapElement.addAttribute( soapFactory.createName( Constants.EBXML_NAMESPACE_PREFIX
                                + ":deliverySemantics" ), "OnceAndOnlyOnce" );
                        soapElement.removeNamespaceDeclaration( Constants.EBXML_NAMESPACE_PREFIX );
                    }
                    createAck( soapFactory, soapHeader, createdDate, messagePojo.getReferencedMessage().getMessageId(),
                            from, fromIdType );
                } else if ( error ) { // error
                    createErrorList( soapFactory, soapHeader, messagePojo.getReferencedMessage().getMessageId(),
                            (Vector<ErrorDescriptor>) messageContext.getData() );
                } else { // regular message
                    // QUALITY OF SERVICE---------------------------------------------------
                    if ( messagePojo.getParticipant().getConnection().isReliable() ) {
                        SOAPElement soapElement = msgHeader.addChildElement(
                                "QualityOfServiceInfo", Constants.EBXML_NAMESPACE_PREFIX, Constants.EBXML_NAMESPACE );
                        soapElement.addAttribute( soapFactory.createName( Constants.EBXML_NAMESPACE_PREFIX
                                + ":deliverySemantics" ), "OnceAndOnlyOnce" );
                        soapElement.removeNamespaceDeclaration( Constants.EBXML_NAMESPACE_PREFIX );
                    }

                    name = soapFactory.createName( "TraceHeaderList", Constants.EBXML_NAMESPACE_PREFIX,
                            Constants.EBXML_NAMESPACE );
                    SOAPHeaderElement traceHeaderList = soapHeader.addHeaderElement( name );
                    traceHeaderList.setMustUnderstand( true );
                    traceHeaderList.setActor( Constants.SOAPACTOR );
                    traceHeaderList.addAttribute( soapFactory.createName( Constants.VERSION,
                            Constants.EBXML_NAMESPACE_PREFIX, Constants.EBXML_NAMESPACE ), Constants.EBXML_VERSION );

                    SOAPElement traceHeader = traceHeaderList.addChildElement(
                            "TraceHeader", Constants.EBXML_NAMESPACE_PREFIX, Constants.EBXML_NAMESPACE );
                    SOAPElement sender = createPartyElement( soapFactory, "Sender", from, fromIdType, null );
                    Set<ConnectionPojo> localConnections = messagePojo.getParticipant().getLocalPartner()
                            .getConnections();
                    String localURI = null;
                    if ( !localConnections.isEmpty() ) {
                        // Try to find connection that is names ebxml10
                        for ( Iterator iterator = localConnections.iterator(); iterator.hasNext(); ) {
                            ConnectionPojo connectionPojo = (ConnectionPojo) iterator.next();
                            if ( connectionPojo.getName().equalsIgnoreCase( "ebxml10" )
                                    || connectionPojo.getName().equalsIgnoreCase( "ebms10" )
                                    || connectionPojo.getUri().contains( "ebxml10" ) ) {
                                localURI = connectionPojo.getUri();
                                break;
                            }
                        }
                        // If nothing found use first URL
                        if ( localURI == null ) {
                            localURI = localConnections.iterator().next().getUri();
                        }
                    } else {
                        localURI = "http://localhost:8080/NEXUSe2e/handler/ebxml10";
                    }
                    createSOAPElement( soapFactory, sender, "Location", localURI );
                    traceHeader.addChildElement( sender );

                    SOAPElement receiver = createPartyElement( soapFactory, "Receiver", to, toIDType, null );
                    createSOAPElement( soapFactory, receiver, "Location", messagePojo.getParticipant().getConnection().getUri() );
                    traceHeader.addChildElement( receiver );

                    createSOAPElement( soapFactory, traceHeader, Constants.TIMESTAMP_ID, createdDate );

                    traceHeaderList.addChildElement( traceHeader );

                    name = soapFactory.createName( "Via", Constants.EBXML_NAMESPACE_PREFIX, Constants.EBXML_NAMESPACE );
                    SOAPHeaderElement via = soapHeader.addHeaderElement( name );
                    via.setMustUnderstand( true );
                    via.setActor( Constants.SOAPACTOR );
                    via.addAttribute( soapFactory.createName( Constants.VERSION, Constants.EBXML_NAMESPACE_PREFIX,
                            Constants.EBXML_NAMESPACE ), Constants.EBXML_VERSION );
                    via.addAttribute( soapFactory.createName( Constants.ACK_REQUESTED,
                            Constants.EBXML_NAMESPACE_PREFIX, Constants.EBXML_NAMESPACE ),
                            Constants.ACK_REQUESTED_VALUE );
                    via.addAttribute( soapFactory.createName( Constants.RELIABLE_MESSAGING_METHOD,
                            Constants.EBXML_NAMESPACE_PREFIX, Constants.EBXML_NAMESPACE ),
                            Constants.RELIABLE_MESSAGING_METHOD_VALUE );

                    SOAPElement soapManifest = null;

                    // MANIFEST --------------------------------------------------------
                    name = soapFactory.createName( "Manifest", Constants.EBXML_NAMESPACE_PREFIX,
                            Constants.EBXML_NAMESPACE );
                    soapManifest = soapBody.addBodyElement( name );

                    soapManifest.addAttribute( soapFactory.createName( Constants.MUSTUNDERSTAND,
                            Constants.EBXML_NAMESPACE_PREFIX, Constants.EBXML_NAMESPACE ),
                            Constants.MUSTUNDERSTAND_VALUE );
                    soapManifest.addAttribute( soapFactory.createName( Constants.VERSION,
                            Constants.EBXML_NAMESPACE_PREFIX, Constants.EBXML_NAMESPACE ), Constants.EBXML_VERSION );
                    //soapManifest.addAttribute( soapFactory.createName( "xmlns:eb" ),
                    //        "http://www.ebxml.org/namespaces/messageHeader" );

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
            } // Test for re-send of ack
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
     * @param fromIDType
     * @return
     * @throws SOAPException
     */
    private SOAPElement createAck( SOAPFactory soapFactory, SOAPHeader soapHeader, String timestamp,
            String refMessageId, String from, String fromIDType ) throws SOAPException {

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
        ackEl.addChildElement( createPartyElement( soapFactory, "From", from, fromIDType, null ) );

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
        Severity severity = Severity.INFO;
        String highestSeverity = "nothing";
        if ( errors != null ) {
            Iterator<ErrorDescriptor> i = errors.iterator();
            while ( i.hasNext() ) {
                ErrorDescriptor ed = i.next();
                if ( ed.getSeverity().ordinal() > severity.ordinal() ) {
                    severity = ed.getSeverity();
                    highestSeverity = ed.getSeverity().name();

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
                        Constants.EBXML_NAMESPACE ), "" + ed.getErrorCode() );
                severity = ed.getSeverity();

                soapEl.addAttribute( soapFactory.createName( "severity", Constants.EBXML_NAMESPACE_PREFIX,
                        Constants.EBXML_NAMESPACE ), severity.name() );
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

        SOAPElement soapEl = parent.addChildElement( "Reference", Constants.EBXML_NAMESPACE_PREFIX, Constants.EBXML_NAMESPACE );
        soapEl.addAttribute( soapFactory.createName( "xmlns:xlink" ), "http://www.w3.org/1999/xlink" );
        //soapEl.addAttribute( soapFactory.createName( "xmlns:" + Constants.EBXML_NAMESPACE_PREFIX ), Constants.EBXML_NAMESPACE );

        soapEl.addAttribute( soapFactory.createName( Constants.EBXML_NAMESPACE_PREFIX + ":id" ), id );
        soapEl.addAttribute( soapFactory.createName( "xlink:href" ), href );

        if ( role != null && role.length() != 0 ) {
            soapEl.addAttribute( soapFactory.createName( "xlink:role" ), role );
        }

        if ( type != null && type.length() != 0 ) {
            soapEl.addAttribute( soapFactory.createName( "xlink:type" ), "simple" ); // hard coded according to spec, p. 35, 8.11.3
        }
    }

    /**
     * convenience routine for adding text nodes to soap elements.
     *
     */
    private void createSOAPElement( SOAPFactory soapFactory, SOAPElement parent, String childName, String childText )
            throws SOAPException {

        LOG.trace( "createSOAPElement: " + childName );
        SOAPElement soapEl = parent.addChildElement( childName, Constants.EBXML_NAMESPACE_PREFIX, Constants.EBXML_NAMESPACE );
        soapEl.removeNamespaceDeclaration( Constants.EBXML_NAMESPACE_PREFIX );
        soapEl.addTextNode( childText );
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
        SOAPElement soapEl = soapFactory.createElement( id, Constants.EBXML_NAMESPACE_PREFIX, Constants.EBXML_NAMESPACE );
        soapEl.removeNamespaceDeclaration( Constants.EBXML_NAMESPACE_PREFIX );
        SOAPElement partyId = soapEl.addChildElement( Constants.PARTY_ID, Constants.EBXML_NAMESPACE_PREFIX, Constants.EBXML_NAMESPACE );
        partyId.removeNamespaceDeclaration( Constants.EBXML_NAMESPACE_PREFIX );

        if ( type != null && !type.equals( "" ) ) {
            // partyId.addAttribute( soapFactory.createName( "type" ), type );
            partyId.addAttribute( soapFactory.createName( "eb:type" ), type );
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
            createSOAPElement( soapFactory, soapEl, Constants.LOCATION_ID, location );
        }

        return soapEl;
    } // createPartyElement

    public void afterPropertiesSet() throws Exception {

        // TODO Auto-generated method stub

    }

}
