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
package org.nexuse2e.messaging.cidx;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.log4j.Logger;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.IdGenerator;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.messaging.AbstractPipelet;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.messaging.Pipelet;
import org.nexuse2e.pojo.MessagePayloadPojo;
import org.nexuse2e.pojo.MessagePojo;
import org.nexuse2e.transport.WebServiceGenericXMLDocumentReceiver;
import org.springframework.beans.factory.InitializingBean;

public class CIDXDummyHeaderBuilder extends AbstractPipelet {

    private static Logger       LOG = Logger.getLogger( WebServiceGenericXMLDocumentReceiver.class );

    
    /**
     * Default constructor.
     */
    public CIDXDummyHeaderBuilder() {

    }

    public MessageContext processMessage( MessageContext frontendPipeletParameter )
            throws NexusException {

        String partnerId = null;
        String messageId = null;
        String conversationId = null;
        String actionId = null;
        String choreographyId = null;

        MessagePojo messagePojo = frontendPipeletParameter.getMessagePojo();
        messagePojo.setType( org.nexuse2e.messaging.Constants.INT_MESSAGE_TYPE_NORMAL );
        messagePojo.setCreatedDate( new Date() );
        messagePojo.setModifiedDate( new Date() );

        IdGenerator messageIdGenerator = Engine.getInstance().getIdGenerator(
                org.nexuse2e.Constants.ID_GENERATOR_MESSAGE );
        messageId = messageIdGenerator.getId();

        IdGenerator conversationIdGenerator = Engine.getInstance().getIdGenerator(
                org.nexuse2e.Constants.ID_GENERATOR_CONVERSATION );
        conversationId = conversationIdGenerator.getId();

        partnerId = "not set";
        choreographyId = "not set";
        actionId = "not set";

        ByteArrayInputStream bais = new ByteArrayInputStream( frontendPipeletParameter.getData() );
        XMLInputFactory factory = XMLInputFactory.newInstance();
        try {
            XMLStreamReader parser = factory.createXMLStreamReader( bais );
            while ( !parser.isStartElement() ) {
                parser.next();
            }
            choreographyId = parser.getLocalName().trim();
            actionId = choreographyId;
            System.out.println( "Root: " + parser.getLocalName() );
            while ( !( parser.isStartElement() && parser.getLocalName().equals( "DocumentIdentifier" ) ) ) {
                parser.next();
            }
            System.out.println( "Doc ID: " + parser.getElementText() );
            while ( !( parser.isStartElement() && parser.getLocalName().equals( "From" ) ) ) {
                parser.next();
            }
            while ( !( parser.isStartElement() && parser.getLocalName().equals( "PartnerIdentifier" ) ) ) {
                parser.next();
            }
            partnerId = parser.getElementText().trim();
            System.out.println( "Partner ID: '" + partnerId + "'" );

            LOG.debug( "CIDXDummyHeaderBuilder.processMessage triggered... " );

            String payload = new String( frontendPipeletParameter.getData() );

            LOG.debug( "CIDXDummyHeaderBuilder.processMessage - payload:\n" + payload );
            MessagePayloadPojo messagePayloadPojo = new MessagePayloadPojo();
            messagePayloadPojo.setMessage( messagePojo );
            messagePayloadPojo.setPayloadData( frontendPipeletParameter.getData() );
            messagePayloadPojo.setMimeType( "text/xml" );
            messagePayloadPojo.setContentId( messagePojo.getMessageId() + "-body1" );
            messagePayloadPojo.setSequenceNumber( new Integer( 1 ) );
            messagePayloadPojo.setCreatedDate( messagePojo.getCreatedDate() );
            messagePayloadPojo.setModifiedDate( messagePojo.getCreatedDate() );
            List<MessagePayloadPojo> payloads = new ArrayList<MessagePayloadPojo>();
            payloads.add( messagePayloadPojo );

            messagePojo.setMessagePayloads( payloads );
        } catch ( XMLStreamException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new NexusException( "Error processing inbound CIDX message: " + e, e );
        }

        return frontendPipeletParameter;
    }
   
}
