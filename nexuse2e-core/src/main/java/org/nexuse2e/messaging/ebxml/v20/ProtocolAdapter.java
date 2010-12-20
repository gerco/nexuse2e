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
package org.nexuse2e.messaging.ebxml.v20;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.ProtocolSpecificKey;
import org.nexuse2e.logging.LogMessage;
import org.nexuse2e.messaging.Constants.ErrorMessageReasonCode;
import org.nexuse2e.messaging.ErrorDescriptor;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.pojo.ChoreographyPojo;
import org.nexuse2e.pojo.ConversationPojo;
import org.nexuse2e.pojo.MessagePojo;
import org.nexuse2e.pojo.ParticipantPojo;
import org.nexuse2e.pojo.PartnerPojo;

/**
 * @author gesch, sschulze
 *
 */
public class ProtocolAdapter implements org.nexuse2e.messaging.ProtocolAdapter {

    private static Logger       LOG = Logger.getLogger( ProtocolAdapter.class );

    private ProtocolSpecificKey key = null;

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.ProtocolAdapter#createAcknowledge(org.nexuse2e.messaging.messageContext)
     */
    public MessageContext createAcknowledgement( ChoreographyPojo choreography, MessageContext messageContext )
            throws NexusException {
        
        MessageContext ackMessageContext = null;
        try {
            ackMessageContext = (MessageContext) messageContext.clone();
        } catch ( CloneNotSupportedException e ) {
            throw new NexusException( new LogMessage( "Cannot clone message context to create an acknowledgment: " + e.getMessage(), messageContext ), e );
        }
        
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
            ackMessageContext.setMessagePojo( null );
            ackMessageContext.setOriginalMessagePojo( null );
            return ackMessageContext;
        }
        acknowledgment.setMessageId( messageId );

        acknowledgment.setReferencedMessage( messageContext.getMessagePojo() );

        PartnerPojo partner = Engine.getInstance().getActiveConfigurationAccessService().getPartnerByPartnerId(
                currentPartnerId );

        if ( partner == null ) {
            ackMessageContext.setMessagePojo( null );
            LOG.error( "No partner entry found for partnerId: " + currentPartnerId );
            return ackMessageContext;
        }
        ParticipantPojo participant = Engine.getInstance().getActiveConfigurationAccessService()
                .getParticipantFromChoreographyByPartner(
                        messageContext.getMessagePojo().getConversation().getChoreography(), partner );

        acknowledgment.getCustomParameters().put(
                Constants.PARAMETER_PREFIX_EBXML20 + Constants.PROTOCOLSPECIFIC_TOIDTYPE, partner.getPartnerIdType() );
        acknowledgment.getCustomParameters().put( Constants.PARAMETER_PREFIX_EBXML20 + Constants.PROTOCOLSPECIFIC_FROM,
                participant.getLocalPartner().getPartnerId() );
        acknowledgment.getCustomParameters().put(
                Constants.PARAMETER_PREFIX_EBXML20 + Constants.PROTOCOLSPECIFIC_FROMIDTYPE,
                participant.getLocalPartner().getPartnerIdType() );
        acknowledgment.getCustomParameters().put(
                Constants.PARAMETER_PREFIX_EBXML20 + Constants.PROTOCOLSPECIFIC_SERVICE, "uri:Acknowledgement" );

        if ( LOG.isTraceEnabled() ) {
        	LOG.trace( new LogMessage( "-----conversation:" + messageContext.getMessagePojo().getConversation(),messageContext.getMessagePojo()) );
        }
        acknowledgment.setConversation( messageContext.getMessagePojo().getConversation() );
        acknowledgment.setOutbound( true );

        ackMessageContext.setMessagePojo( acknowledgment );

        return ackMessageContext;
    } // createAcknowledgement

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.ProtocolAdapter#addProtcolSpecificParameters(org.nexuse2e.messaging.MessageContext)
     */
    public void addProtcolSpecificParameters( MessageContext messageContext ) {

        String localPartnerId = messageContext.getParticipant().getLocalPartner().getPartnerId();
        String localPartnerType = messageContext.getParticipant().getLocalPartner().getPartnerIdType();
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put( Constants.PARAMETER_PREFIX_EBXML20 + Constants.PROTOCOLSPECIFIC_FROM, localPartnerId );
        parameters.put( Constants.PARAMETER_PREFIX_EBXML20 + Constants.PROTOCOLSPECIFIC_FROMIDTYPE, localPartnerType );
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

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.ProtocolAdapter#createErrorAcknowledgement(org.nexuse2e.messaging.Constants.ErrorMessageReasonCode, org.nexuse2e.pojo.ChoreographyPojo, org.nexuse2e.messaging.MessageContext, java.util.List)
     */
    public MessageContext createErrorAcknowledgement(
            ErrorMessageReasonCode reasonCode, ChoreographyPojo choreography,
            MessageContext messageContext, List<ErrorDescriptor> errorMessages) {
    	if ( LOG.isDebugEnabled() ) {
        	LOG.debug( new LogMessage( "Creating error acknowledgement", messageContext ) );
        }

        String currentMessageId = messageContext.getMessagePojo().getMessageId();

        MessagePojo errorNotification = new MessagePojo();

        errorNotification.setCreatedDate( new Date() );
        errorNotification.setModifiedDate( new Date() );

        errorNotification.setTRP( messageContext.getMessagePojo().getTRP() );

        errorNotification.setAction( messageContext.getMessagePojo().getAction() );
        errorNotification.setType( org.nexuse2e.messaging.Constants.INT_MESSAGE_TYPE_ERROR );
        errorNotification.setOutbound( true );

        String messageId = null;
        try {
            messageId = Engine.getInstance().getIdGenerator( "messageId" ).getId();
        } catch ( NexusException e ) {
            LOG.fatal( "unable to create ErrorMessageId for message:" + currentMessageId );
            e.printStackTrace();
            messageContext.setMessagePojo( null );
            return messageContext;
        }

        errorNotification.setMessageId( messageId );
        errorNotification.setReferencedMessage( messageContext.getMessagePojo() );

        String from = null;
        String fromIdType = null;

        ConversationPojo conv = messageContext.getMessagePojo().getConversation();
        errorNotification.getCustomParameters().put(
                Constants.PARAMETER_PREFIX_EBXML20 + Constants.PROTOCOLSPECIFIC_TOIDTYPE,
                (conv != null ? conv.getPartner().getPartnerIdType() : null) );
        errorNotification.getCustomParameters().put(
                Constants.PARAMETER_PREFIX_EBXML20 + Constants.PROTOCOLSPECIFIC_FROM,
                messageContext.getMessagePojo().getParticipant().getLocalPartner().getPartnerId() );
        errorNotification.getCustomParameters().put(
                Constants.PARAMETER_PREFIX_EBXML20 + Constants.PROTOCOLSPECIFIC_FROMIDTYPE,
                messageContext.getMessagePojo().getParticipant().getLocalPartner().getPartnerIdType() );

        errorNotification.getCustomParameters().put( Constants.PROTOCOLSPECIFIC_FROM, from );
        errorNotification.getCustomParameters().put( Constants.PROTOCOLSPECIFIC_FROMIDTYPE, fromIdType );
        errorNotification.getCustomParameters().put( Constants.PROTOCOLSPECIFIC_SERVICE,
                "uri:" + messageContext.getMessagePojo().getAction() );
        messageContext.setMessagePojo( errorNotification );

        errorNotification.setConversation( messageContext.getConversation() );
        errorNotification.setOutbound( true );
        errorNotification.setErrors( messageContext.getMessagePojo().getErrors() );
        messageContext.setMessagePojo( errorNotification );
        
        
        return messageContext;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.ProtocolAdapter#createResponse(org.nexuse2e.messaging.MessageContext)
     */
    public MessageContext createResponse( MessageContext messageContext )
            throws NexusException {

        // request-response semantics not part of ebXML
        return null;
    }

} // ProtocolAdapter
