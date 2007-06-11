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

import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.ProtocolSpecificKey;
import org.nexuse2e.messaging.ebxml.v20.Constants;
import org.nexuse2e.messaging.ErrorDescriptor;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.pojo.ChoreographyPojo;
import org.nexuse2e.pojo.MessagePojo;
import org.nexuse2e.pojo.ParticipantPojo;
import org.nexuse2e.pojo.PartnerPojo;

/**
 * @author gesch
 *
 */
public class ProtocolAdapter implements org.nexuse2e.messaging.ProtocolAdapter {

    private static Logger       LOG = Logger.getLogger( ProtocolAdapter.class );

    private ProtocolSpecificKey key = null;

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.ProtocolAdapter#createAcknowledge(org.nexuse2e.messaging.MessageContext)
     */
    public MessageContext createAcknowledgement( ChoreographyPojo choreography, MessageContext messageContext )
            throws NexusException {

        String currentMessageId = messageContext.getMessagePojo().getMessageId();
        String currentPartnerId = messageContext.getMessagePojo().getConversation().getPartner().getPartnerId();

        MessagePojo acknowledgment = new MessagePojo();
        acknowledgment.setCustomParameters( new HashMap<String, String>() );
        acknowledgment.setCreatedDate( new Date() );
        acknowledgment.setModifiedDate( new Date() );

        acknowledgment.setTRP( messageContext.getMessagePojo().getTRP() );

        acknowledgment.setType( org.nexuse2e.messaging.Constants.INT_MESSAGE_TYPE_ACK );

        // TODO: verify that using this instance does not create problems
        acknowledgment.setAction( messageContext.getMessagePojo().getAction() );

        String messageId;
        try {
            messageId = Engine.getInstance().getIdGenerator( org.nexuse2e.Constants.ID_GENERATOR_MESSAGE ).getId();
        } catch ( NexusException e ) {
            LOG.fatal( "Unable to create AcknowldegeMessageId for message: " + currentMessageId );
            e.printStackTrace();
            messageContext.setMessagePojo( null );
            messageContext.setOriginalMessagePojo( null );
            return messageContext;
        }
        acknowledgment.setMessageId( messageId );

        acknowledgment.setReferencedMessage( messageContext.getMessagePojo() );

        PartnerPojo partner = Engine.getInstance().getActiveConfigurationAccessService().getPartnerByPartnerId(
                currentPartnerId );

        if ( partner == null ) {
            messageContext.setMessagePojo( null );
            LOG.error( "No partner entry found for partnerId: " + currentPartnerId );
            return messageContext;
        }
        ParticipantPojo participant = Engine.getInstance().getActiveConfigurationAccessService()
                .getParticipantFromChoreographyByPartner(
                        messageContext.getMessagePojo().getConversation().getChoreography(), partner );

        acknowledgment.getCustomParameters().put( Constants.PROTOCOLSPECIFIC_TOIDTYPE, partner.getPartnerIdType() );
        acknowledgment.getCustomParameters().put( Constants.PROTOCOLSPECIFIC_FROM,
                participant.getLocalPartner().getPartnerId() );
        acknowledgment.getCustomParameters().put( Constants.PROTOCOLSPECIFIC_FROMIDTYPE,
                participant.getLocalPartner().getPartnerIdType() );
        acknowledgment.getCustomParameters().put( Constants.PROTOCOLSPECIFIC_SERVICE, "uri:Acknowledgement" );

        LOG.trace( "-----conversation:" + messageContext.getMessagePojo().getConversation() );
        acknowledgment.setConversation( messageContext.getMessagePojo().getConversation() );
        acknowledgment.setOutbound( true );

        messageContext.setMessagePojo( acknowledgment );

        return messageContext;
    } // createAcknowledgement

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.ProtocolAdapter#createErrorAcknowledge(int, org.nexuse2e.messaging.MessageContext)
     */
    public MessageContext createErrorAcknowledgement( Constants.ErrorMessageReasonCode reasonCode,
            ChoreographyPojo choreography, MessageContext messageContext, Vector<ErrorDescriptor> errorMessages ) {

        //        String currentMessageId = messageContext.getMessagePojo().getMessageKey().getMessageId();
        //        String currentConversationId = messageContext.getMessagePojo().getMessageKey()
        //                .getConversationId();
        //        String currentChoreographyId = messageContext.getMessagePojo().getMessageKey()
        //                .getChoreographyId();
        //        String currentPartnerId = messageContext.getMessagePojo().getMessageKey().getPartnerId();
        //
        //        MessagePojo errrorNotification = new MessagePojo();
        //
        //        errrorNotification.setCreatedDate( DateWrapper.getNOWdatabaseString() );
        //        errrorNotification.setLastModifiedDate( DateWrapper.getNOWdatabaseString() );
        //
        //        errrorNotification.setCommunicationProtocolId( "ebXML" );
        //        errrorNotification.setCommunicationProtocolVersion( "2.0" );
        //
        //        errrorNotification.setAction( messageContext.getMessagePojo().getAction() );
        //        errrorNotification.setMessageType( Constants.MESSAGE_TYPE_ERROR );
        //        errrorNotification.setOutbound( true );
        //
        //        String messageId = null;
        //        try {
        //            messageId = Engine.getInstance().getIdGenerator( "messageId" ).getId();
        //        } catch ( NexusException e ) {
        //            LOG.fatal( "unable to create ErrorMessageId for message:" + currentMessageId );
        //            e.printStackTrace();
        //            messageContext.setMessagePojo( null );
        //            return messageContext;
        //        }
        //
        //        errrorNotification.setMessageKey( new MessageKey( currentChoreographyId, currentConversationId, messageId,
        //                currentPartnerId ) );
        //        errrorNotification.setReferenceMessageId( currentMessageId );
        //
        //        List<CommunicationPartnerPojo> partners = Engine.getInstance().getCurrentConfiguration().getPartners();
        //        Iterator<CommunicationPartnerPojo> i = partners.iterator();
        //        CommunicationPartnerPojo partner = null;
        //        while ( i.hasNext() ) {
        //            CommunicationPartnerPojo tempPartner = i.next();
        //            if ( tempPartner.getPartnerId().equals( currentPartnerId ) ) {
        //                partner = tempPartner;
        //            }
        //        }
        //        if ( partner == null ) {
        //            messageContext.setMessagePojo( null );
        //            LOG.error( "no partner entry found for partnerId:" + currentPartnerId );
        //            return messageContext;
        //        }
        //
        //        errrorNotification.getCustomParameters().put( Constants.PROTOCOLSPECIFIC_TOIDTYPE, partner.getPartnerType() );
        //
        //        String from = null;
        //        String fromIdType = null;
        //        if ( choreography == null ) {
        //            from = messageContext.getMessagePojo().getCustomParameters().get(
        //                    Constants.PROTOCOLSPECIFIC_TO );
        //            fromIdType = messageContext.getMessagePojo().getCustomParameters().get(
        //                    Constants.PROTOCOLSPECIFIC_TOIDTYPE );
        //        } else {
        //            from = choreography.getLocalPartnerId();
        //            fromIdType = choreography.getLocalPartnerIdType();
        //        }
        //        errrorNotification.getCustomParameters()
        //                .put( Constants.PROTOCOLSPECIFIC_FROM, from );
        //        errrorNotification.getCustomParameters().put( Constants.PROTOCOLSPECIFIC_FROMIDTYPE, fromIdType );
        //        errrorNotification.getCustomParameters().put( Constants.PROTOCOLSPECIFIC_SERVICE,
        //                "uri:" + messageContext.getMessagePojo().getAction() );
        //        messageContext.setMessagePojo( errrorNotification );

        return messageContext;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.ProtocolAdapter#addProtcolSpecificParameters(org.nexuse2e.messaging.MessageContext)
     */
    public void addProtcolSpecificParameters( MessageContext messageContext ) {

        String localPartnerId = messageContext.getParticipant().getLocalPartner().getPartnerId();
        String localPartnerType = messageContext.getParticipant().getLocalPartner().getPartnerIdType();
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put( Constants.PROTOCOLSPECIFIC_FROM, localPartnerId );
        parameters.put( Constants.PROTOCOLSPECIFIC_FROMIDTYPE, localPartnerType );
        messageContext.getMessagePojo().setCustomParameters( parameters );

    }

    /* (non-Javadoc)
     * @see org.nexuse2e.ProtocolSpecific#getKey()
     */
    public ProtocolSpecificKey getKey() {

        return key;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.ProtocolSpecific#setKey(org.nexuse2e.ProtocolSpecificKey)
     */
    public void setKey( ProtocolSpecificKey key ) {

        this.key = key;
    }

} // ProtocolAdapter
