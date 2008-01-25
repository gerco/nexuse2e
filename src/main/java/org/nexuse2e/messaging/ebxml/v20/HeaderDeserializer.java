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
package org.nexuse2e.messaging.ebxml.v20;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import javax.mail.MessagingException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.Node;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.soap.Text;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.logging.LogMessage;
import org.nexuse2e.messaging.AbstractPipelet;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.messaging.TimestampFormatter;
import org.nexuse2e.pojo.MessagePojo;
import org.w3c.dom.NodeList;

/**
 * @author mbreilmann
 *
 */
public class HeaderDeserializer extends AbstractPipelet {

    private static Logger LOG = Logger.getLogger( HeaderDeserializer.class );

    /**
     * Default constructor.
     */
    public HeaderDeserializer() {

        frontendPipelet = true;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.HeaderDeserializer#processMessage(com.tamgroup.nexus.e2e.persistence.pojo.MessagePojo)
     */
    public MessageContext processMessage( MessageContext messageContext ) throws IllegalArgumentException,
            IllegalStateException, NexusException {

        LOG.trace( "enter EbXMLV20HeaderDeserializer.processMessageImpl" );

        MessagePojo messagePojo = messageContext.getMessagePojo();

        if ( messagePojo.getCustomParameters() == null ) {
            messagePojo.setCustomParameters( new HashMap<String, String>() );
        }

        messagePojo.setType( org.nexuse2e.messaging.Constants.INT_MESSAGE_TYPE_NORMAL );

        try {
            MessageFactory messageFactory = MessageFactory.newInstance();

            LOG.trace( "Header:" + new String( messagePojo.getHeaderData() ) );

            SOAPMessage soapMessage;
            try {
                soapMessage = messageFactory.createMessage( null,
                        new ByteArrayInputStream( messagePojo.getHeaderData() ) );
            } catch ( SOAPException soapException ) {
                LOG.info( "Got SOAPException (" + soapException.getMessage() + "), trying to fix SOAP header" );
                // jre: fix invalid SOAP header sent by some Nexus versions (missing namespace decl.)
                String token = ":Envelope";
                String s = new String( messagePojo.getHeaderData() );
                int index = s.indexOf( token );
                if ( index >= 0 ) {
                    s = s.substring( 0, index + token.length() ) + " xmlns:xlink=\"http://www.w3.org/1999/xlink\""
                            + s.substring( index + token.length() );
                }
                soapMessage = messageFactory.createMessage( null, new ByteArrayInputStream( s.getBytes() ) );
            }
            SOAPPart part = soapMessage.getSOAPPart();
            SOAPEnvelope soapEnvelope = null;
            try {
                soapEnvelope = part.getEnvelope();
            } catch ( Exception e ) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                throw new IllegalArgumentException( "Error processing ebXML Header: " + e );
            } catch ( Error e ) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                throw new IllegalArgumentException( "Error processing ebXML Header: " + e );
            }

            SOAPHeader soapHeader = soapEnvelope.getHeader();

            // Determine message type first
            Iterator headerElements = soapHeader.getChildElements();
            while ( headerElements.hasNext() ) {
                Node node = (Node) headerElements.next();
                while ( node instanceof Text && headerElements.hasNext() ) {
                    node = (Node) headerElements.next();
                }
                if ( node instanceof SOAPElement ) {
                    SOAPElement element = (SOAPElement) node;
                    String localName = element.getElementName().getLocalName();
                    LOG.trace( "LocalName=" + localName );
                    if ( localName.equals( "Acknowledgment" ) ) {
                        messagePojo.setType( org.nexuse2e.messaging.Constants.INT_MESSAGE_TYPE_ACK );
                        break;
                    } else if ( localName.equals( "ErrorList" ) ) {
                        messagePojo.setType( org.nexuse2e.messaging.Constants.INT_MESSAGE_TYPE_ERROR );
                        break;
                    }
                }
            }

            headerElements = soapHeader.getChildElements();
            while ( headerElements.hasNext() ) {
                Node node = (Node) headerElements.next();
                while ( node instanceof Text && headerElements.hasNext() ) {
                    node = (Node) headerElements.next();
                }
                if ( node instanceof SOAPElement ) {
                    SOAPElement element = (SOAPElement) node;
                    String localName = element.getElementName().getLocalName();
                    LOG.trace( "LocalName=" + localName );
                    if ( localName.equals( "MessageHeader" ) ) {
                        unmarshallMessageHeader( soapEnvelope, element, messagePojo );
                    } else if ( localName.equals( "AckRequested" ) ) {
                        unmarshallAckRequested( soapEnvelope, element, messagePojo );
                    } else if ( localName.equals( "Acknowledgment" ) ) {
                        unmarshallAcknowledgment( soapEnvelope, element, messagePojo );
                    } else if ( localName.equals( "ErrorList" ) ) {
                        unmarshallErrorList( soapEnvelope, element, messagePojo );
                    }
                }
            }

            if ( StringUtils.equalsIgnoreCase( messagePojo.getAction().getName(), "MessageError" ) ) {
                messagePojo.setType( org.nexuse2e.messaging.Constants.INT_MESSAGE_TYPE_ERROR );
            }

            LOG.trace( "unmarshall done" );
        } catch ( NexusException e ) {
            // e.printStackTrace();
            throw e;
        } catch ( SOAPException e ) {
            e.printStackTrace();
            throw new NexusException( e );
        } catch ( IOException e ) {
            e.printStackTrace();
            throw new NexusException( e );
        }
        // 
        //        Iterator bodyElements = soapBody.getChildElements();
        //        while ( bodyElements.hasNext() ) {
        //            SOAPElement element = (SOAPElement) bodyElements.next();
        //            if ( element.getElementName().getLocalName().equals( "Manifest" ) ) {
        //
        //            }
        //        }

        LOG.trace( "leave EbXMLV20HeaderDeserializer.processMessageImpl" );

        return messageContext;
    }

    /**
     * Unmarshall the ebXML MessageHeader element
     * @param soapEnvelope
     * @param messageHeader
     * @throws MessagingException
     */
    private void unmarshallMessageHeader( SOAPEnvelope soapEnvelope, SOAPElement messageHeader, MessagePojo messagePojo )
            throws NexusException {

        String fromId = null;
        String messageId = null;
        String conversationId = null;
        String actionId = null;
        String choreographyId = null;

        LOG.trace( "enter EbXMLV20HeaderDeserializer.unmarshallMessageHeader" );
        try {
            SOAPElement element = null;
            SOAPElement innerElement = null;
            Node node = null;
            Iterator innerElements = null;
            Iterator headerElements = messageHeader.getChildElements();
            while ( headerElements.hasNext() ) {
                node = (Node) headerElements.next();
                while ( node instanceof Text && headerElements.hasNext() ) {
                    node = (Node) headerElements.next();
                }
                if ( node instanceof SOAPElement ) {
                    element = (SOAPElement) node;
                    String localName = element.getElementName().getLocalName();
                    if ( localName.equals( "From" ) ) {
                        innerElements = element.getChildElements();
                        if ( innerElements.hasNext() ) {
                            node = (Node) innerElements.next();
                            while ( node instanceof Text && innerElements.hasNext() ) {
                                node = (Node) innerElements.next();
                            }
                            if ( node instanceof SOAPElement ) {
                                innerElement = (SOAPElement) node;
                                String fromIdType = innerElement.getAttributeValue( soapEnvelope.createName( "type",
                                        Constants.EBXML_NAMESPACE_PREFIX, Constants.EBXML_NAMESPACE ) );
                                LOG.trace( "FromIDType:" + fromIdType );
                                messagePojo.getCustomParameters().put(
                                        Constants.PARAMETER_PREFIX_EBXML20 + Constants.PROTOCOLSPECIFIC_FROMIDTYPE,
                                        fromIdType );
                                fromId = innerElement.getValue();
                                // Cut off 'uri:'in case there was no type information
                                int lastColon = fromId.lastIndexOf( ":" );
                                if ( lastColon > -1 && lastColon < fromId.length() ) {
                                    fromId = fromId.substring( lastColon + 1 );
                                }
                                LOG.trace( "From:" + fromId );
                            }
                        } else {
                            throw new NexusException( "No from party found in ebXML 2.0 message!" );
                        }
                    } else if ( localName.equals( "To" ) ) {
                        innerElements = element.getChildElements();
                        if ( innerElements.hasNext() ) {
                            node = (Node) innerElements.next();
                            while ( node instanceof Text && innerElements.hasNext() ) {
                                node = (Node) innerElements.next();
                            }
                            if ( node instanceof SOAPElement ) {
                                innerElement = (SOAPElement) node;
                                String toIdType = innerElement.getAttributeValue( soapEnvelope.createName( "type",
                                        Constants.EBXML_NAMESPACE_PREFIX, Constants.EBXML_NAMESPACE ) );
                                LOG.trace( "ToIDType:" + toIdType );
                                messagePojo.getCustomParameters().put(
                                        Constants.PARAMETER_PREFIX_EBXML20 + Constants.PROTOCOLSPECIFIC_TOIDTYPE,
                                        toIdType );

                                String to = innerElement.getValue();
                                // Cut off 'uri:'in case there was no type information
                                int lastColon = to.lastIndexOf( ":" );
                                if ( lastColon > -1 && lastColon < to.length() ) {
                                    to = to.substring( lastColon + 1 );
                                }
                                LOG.trace( "To:" + to );
                                messagePojo.getCustomParameters().put(
                                        Constants.PARAMETER_PREFIX_EBXML20 + Constants.PROTOCOLSPECIFIC_TO, to );
                            }
                        } else {
                            throw new NexusException( "No from party found in ebXML 2.0 message!" );
                        }
                    } else if ( localName.equals( "CPAId" ) ) {
                        choreographyId = element.getValue();
                        LOG.trace( "Choreography:" + choreographyId );

                    } else if ( localName.equals( "ConversationId" ) ) {
                        conversationId = element.getValue();
                        LOG.trace( "Conversation:" + conversationId );
                    } else if ( localName.equals( "Service" ) ) {
                        String service = element.getValue();
                        LOG.trace( "Service(? dummy, uri: required, but not saved in database):" + service );
                        messagePojo.getCustomParameters().put(
                                Constants.PARAMETER_PREFIX_EBXML20 + Constants.PROTOCOLSPECIFIC_SERVICE, service );
                    } else if ( localName.equals( "Action" ) ) {
                        actionId = element.getValue();
                        LOG.trace( "Action:" + actionId );
                    } else if ( localName.equals( "MessageData" ) ) {
                        innerElements = element.getChildElements();
                        while ( innerElements.hasNext() ) {
                            node = (Node) innerElements.next();
                            while ( node instanceof Text && innerElements.hasNext() ) {
                                node = (Node) innerElements.next();
                            }
                            if ( node instanceof SOAPElement ) {
                                innerElement = (SOAPElement) node;
                                if ( innerElement.getElementName().getLocalName().equals( "MessageId" ) ) {
                                    messageId = innerElement.getValue();
                                    LOG.trace( "MessageId:" + messageId );
                                } else if ( innerElement.getElementName().getLocalName().equals( "Timestamp" ) ) {
                                    String timestamp = innerElement.getValue();
                                    LOG.trace( "Timestamp:" + timestamp );
                                    TimestampFormatter formatter = Engine.getInstance().getTimestampFormatter( "ebxml" );
                                    Date createdDate = formatter.getTimestamp( timestamp );
                                    if ( createdDate == null ) {
                                        createdDate = new Date();
                                        LOG.error( "Could not parse ebXML timestamp: '" + timestamp + "'" );
                                    }
                                    messagePojo.setCreatedDate( createdDate );
                                    messagePojo.setModifiedDate( createdDate );
                                } else if ( innerElement.getElementName().getLocalName().equals( "RefToMessageId" ) ) {
                                    String refToMessageId = innerElement.getValue();
                                    LOG.trace( "RefToMessageId:" + refToMessageId );
                                    MessagePojo refMessage = Engine.getInstance().getTransactionService().getMessage(
                                            refToMessageId );
                                    messagePojo.setReferencedMessage( refMessage );
                                }
                            }
                        }
                    } else if ( localName.equals( "DuplicateElimination" ) ) {
                        LOG.trace( "duplicationElimination flag found!" );
                    }
                }
            }
        } catch ( SOAPException ex ) {
            throw new NexusException( ex.getMessage() );
        }
        // initialize message with instances of referenced entities
        try {
            messagePojo = Engine.getInstance().getTransactionService().initializeMessage( messagePojo, messageId,
                    conversationId, actionId, fromId, choreographyId );
        } catch ( NexusException ex ) {
            LOG.error( "Error creating message: " + ex );
            LOG.info( "Header received:\n" + new String( messagePojo.getHeaderData() ) );
            throw ex;
        }

    } // unmarshallMessageHeader

    /**
     * @param soapEnvelope
     * @param ackRequested
     * @param messagePojo
     * @throws MessagingException
     */
    private void unmarshallAckRequested( SOAPEnvelope soapEnvelope, SOAPElement ackRequested, MessagePojo messagePojo )
            throws NexusException {

        LOG.trace( "enter EbXMLV20HeaderDeserializer.unmarshallAckRequested" );
        //setReliableMessaging( true );
        /* NYI
         try {
         setSignedAck( ackRequested.getAttributeValue( soapEnvelope.createName( "signed",
         EBXML_NAMESPACE_PREFIX, EBXML_NAMESPACE ) ) );
         } catch ( SOAPException ex ) {
         throw new MessagingException( ex.getMessage() );
         }
         */
        LOG.trace( "leave EbXMLV20HeaderDeserializer.unmarshallAckRequested" );
    } // unmarshallAckRequested

    /**
     * Unmarshall the ebXML Acknowledgment element
     * @param soapEnvelope
     * @param acknowledgement
     * @throws MessagingException
     */
    private void unmarshallAcknowledgment( SOAPEnvelope soapEnvelope, SOAPElement acknowledgement,
            MessagePojo messagePojo ) throws NexusException {

        LOG.trace( "enter EbXMLV20HeaderDeserializer.unmarshallAcknowledgment" );
        //setMessageType( MESSAGE_TYPE_ACK );
        messagePojo.setType( org.nexuse2e.messaging.Constants.INT_MESSAGE_TYPE_ACK );
        try {
            SOAPElement element = null;
            Node node = null;
            // SOAPElement innerElement = null;
            // Iterator innerElements = null;
            Iterator ackElements = acknowledgement.getChildElements();
            while ( ackElements.hasNext() ) {
                node = (Node) ackElements.next();
                while ( node instanceof Text && ackElements.hasNext() ) {
                    node = (Node) ackElements.next();
                }
                if ( node instanceof SOAPElement ) {
                    element = (SOAPElement) node;
                    String localName = element.getElementName().getLocalName();
                    if ( localName.equals( "Timestamp" ) ) {
                    } else if ( localName.equals( "RefToMessageId" ) ) {
                        String refToMessageId = element.getValue();
                        LOG.trace( "RefToMessageId:" + refToMessageId );
                        MessagePojo refMessage = Engine.getInstance().getTransactionService().getMessage(
                                refToMessageId );
                        messagePojo.setReferencedMessage( refMessage );
                    }
                    /*
                     } else if ( localName.equals( "From" ) ) {
                     innerElements = element.getChildElements();
                     if ( innerElements.hasNext() ) {
                     node = (Node) innerElements.next();
                     if ( node instanceof SOAPElement ) {
                     // node.next();
                     // innerElement = (SOAPElement) innerElements.next();
                     // TODO: Fill from party in message pojo 
                     }
                     } else {
                     throw new NexusException( "No from party found in ebXML 2.0 ack message!" );
                     }
                     }
                     */
                }
            }
        } catch ( Exception ex ) {
            LOG.error( "Error processing acknowledgment: " + ex );
            ex.printStackTrace();
            throw new NexusException( ex.getMessage() );
        }
        LOG.trace( "leave EbXMLV20HeaderDeserializer.unmarshallAcknowledgment" );
    } // unmarshallAcknowledgment    

    /**
     * Unmarshall the ebXML ErrorList element
     * @param soapEnvelope
     * @param errorList
     * @throws MessagingException
     */
    private void unmarshallErrorList( SOAPEnvelope soapEnvelope, SOAPElement errorList, MessagePojo messagePojo )
            throws NexusException {

        LOG.trace( "enter EbXMLV20HeaderDeserializer.unmarshallErrorList" );
        messagePojo.setType( org.nexuse2e.messaging.Constants.INT_MESSAGE_TYPE_ERROR );

        SOAPElement element = null;
        Node node = null;
        // SOAPElement innerElement = null;
        // Iterator innerElements = null;
        Iterator errorElements = errorList.getChildElements();
        while ( errorElements.hasNext() ) {
            node = (Node) errorElements.next();
            
            if ( node instanceof SOAPElement ) {
                NodeList descNodes = node.getChildNodes();
                
                
                for ( int i = 0; i < descNodes.getLength(); i++ ) {
                    org.w3c.dom.Node desc = descNodes.item( i );
                    LOG.error( new LogMessage( "RefId: "+messagePojo.getReferencedMessage().getMessageId()+" - "+desc.getFirstChild(), messagePojo ));
                }
                
                
            }
            
        }
        
        
        LOG.trace( "leave EbXMLV20HeaderDeserializer.unmarshallErrorList" );
    } // unmarshallErrorList

}
