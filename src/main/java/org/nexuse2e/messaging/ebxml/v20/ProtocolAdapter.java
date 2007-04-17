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
     * @see org.nexuse2e.messaging.ProtocolAdapter#createAcknowledge(org.nexuse2e.messaging.MessagePipeletParameter)
     */
    public MessageContext createAcknowledgement( ChoreographyPojo choreography,
            MessageContext messagePipeletParameter ) throws NexusException {

        String currentMessageId = messagePipeletParameter.getMessagePojo().getMessageId();
        String currentPartnerId = messagePipeletParameter.getMessagePojo().getConversation().getPartner()
                .getPartnerId();

        MessagePojo acknowledgment = new MessagePojo();
        acknowledgment.setCustomParameters( new HashMap<String, String>() );
        acknowledgment.setCreatedDate( new Date() );
        acknowledgment.setModifiedDate( new Date() );

        acknowledgment.setTRP( messagePipeletParameter.getMessagePojo().getTRP() );

        acknowledgment.setType( org.nexuse2e.messaging.Constants.INT_MESSAGE_TYPE_ACK );

        // TODO: verify that using this instance does not create problems
        acknowledgment.setAction( messagePipeletParameter.getMessagePojo().getAction() );

        String messageId;
        try {
            messageId = Engine.getInstance().getIdGenerator( org.nexuse2e.Constants.ID_GENERATOR_MESSAGE ).getId();
        } catch ( NexusException e ) {
            LOG.fatal( "Unable to create AcknowldegeMessageId for message: " + currentMessageId );
            e.printStackTrace();
            messagePipeletParameter.setMessagePojo( null );
            return messagePipeletParameter;
        }
        acknowledgment.setMessageId( messageId );

        acknowledgment.setReferencedMessage( messagePipeletParameter.getMessagePojo() );

        PartnerPojo partner = Engine.getInstance().getActiveConfigurationAccessService().getPartnerByPartnerId( currentPartnerId );

        if ( partner == null ) {
            messagePipeletParameter.setMessagePojo( null );
            LOG.error( "No partner entry found for partnerId: " + currentPartnerId );
            return messagePipeletParameter;
        }
        ParticipantPojo participant = Engine.getInstance().getActiveConfigurationAccessService()
                .getParticipantFromChoreographyByPartner(
                        messagePipeletParameter.getMessagePojo().getConversation().getChoreography(), partner );

        acknowledgment.getCustomParameters().put( Constants.PARAMETER_PREFIX_EBXML20 +Constants.PROTOCOLSPECIFIC_TOIDTYPE, partner.getPartnerIdType() );
        acknowledgment.getCustomParameters().put( Constants.PARAMETER_PREFIX_EBXML20 +Constants.PROTOCOLSPECIFIC_FROM,
                participant.getLocalPartner().getPartnerId() );
        acknowledgment.getCustomParameters().put( Constants.PARAMETER_PREFIX_EBXML20 +Constants.PROTOCOLSPECIFIC_FROMIDTYPE,
                participant.getLocalPartner().getPartnerIdType() );
        acknowledgment.getCustomParameters().put( Constants.PARAMETER_PREFIX_EBXML20 +Constants.PROTOCOLSPECIFIC_SERVICE, "uri:Acknowledgement" );

        LOG.trace( "-----conversation:" + messagePipeletParameter.getMessagePojo().getConversation() );
        acknowledgment.setConversation( messagePipeletParameter.getMessagePojo().getConversation() );
        acknowledgment.setOutbound( true );

        messagePipeletParameter.setMessagePojo( acknowledgment );

        return messagePipeletParameter;
    } // createAcknowledgement

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.ProtocolAdapter#createErrorAcknowledge(int, org.nexuse2e.messaging.MessagePipeletParameter)
     */
    public MessageContext createErrorAcknowledgement( Constants.ErrorMessageReasonCode reasonCode, ChoreographyPojo choreography,
            MessageContext messagePipeletParameter, Vector<ErrorDescriptor> errorMessages ) {

        //        String currentMessageId = messagePipeletParameter.getMessagePojo().getMessageKey().getMessageId();
        //        String currentConversationId = messagePipeletParameter.getMessagePojo().getMessageKey()
        //                .getConversationId();
        //        String currentChoreographyId = messagePipeletParameter.getMessagePojo().getMessageKey()
        //                .getChoreographyId();
        //        String currentPartnerId = messagePipeletParameter.getMessagePojo().getMessageKey().getPartnerId();
        //
        //        MessagePojo errrorNotification = new MessagePojo();
        //
        //        errrorNotification.setCreatedDate( DateWrapper.getNOWdatabaseString() );
        //        errrorNotification.setLastModifiedDate( DateWrapper.getNOWdatabaseString() );
        //
        //        errrorNotification.setCommunicationProtocolId( "ebXML" );
        //        errrorNotification.setCommunicationProtocolVersion( "2.0" );
        //
        //        errrorNotification.setAction( messagePipeletParameter.getMessagePojo().getAction() );
        //        errrorNotification.setMessageType( Constants.MESSAGE_TYPE_ERROR );
        //        errrorNotification.setOutbound( true );
        //
        //        String messageId = null;
        //        try {
        //            messageId = Engine.getInstance().getIdGenerator( "messageId" ).getId();
        //        } catch ( NexusException e ) {
        //            LOG.fatal( "unable to create ErrorMessageId for message:" + currentMessageId );
        //            e.printStackTrace();
        //            messagePipeletParameter.setMessagePojo( null );
        //            return messagePipeletParameter;
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
        //            messagePipeletParameter.setMessagePojo( null );
        //            LOG.error( "no partner entry found for partnerId:" + currentPartnerId );
        //            return messagePipeletParameter;
        //        }
        //
        //        errrorNotification.getCustomParameters().put( Constants.PROTOCOLSPECIFIC_TOIDTYPE, partner.getPartnerType() );
        //
        //        String from = null;
        //        String fromIdType = null;
        //        if ( choreography == null ) {
        //            from = messagePipeletParameter.getMessagePojo().getCustomParameters().get(
        //                    Constants.PROTOCOLSPECIFIC_TO );
        //            fromIdType = messagePipeletParameter.getMessagePojo().getCustomParameters().get(
        //                    Constants.PROTOCOLSPECIFIC_TOIDTYPE );
        //        } else {
        //            from = choreography.getLocalPartnerId();
        //            fromIdType = choreography.getLocalPartnerIdType();
        //        }
        //        errrorNotification.getCustomParameters()
        //                .put( Constants.PROTOCOLSPECIFIC_FROM, from );
        //        errrorNotification.getCustomParameters().put( Constants.PROTOCOLSPECIFIC_FROMIDTYPE, fromIdType );
        //        errrorNotification.getCustomParameters().put( Constants.PROTOCOLSPECIFIC_SERVICE,
        //                "uri:" + messagePipeletParameter.getMessagePojo().getAction() );
        //        messagePipeletParameter.setMessagePojo( errrorNotification );

        return messagePipeletParameter;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.ProtocolAdapter#addProtcolSpecificParameters(org.nexuse2e.messaging.MessagePipeletParameter)
     */
    public void addProtcolSpecificParameters( MessageContext messagePipeletParameter ) {

        String localPartnerId = messagePipeletParameter.getParticipant().getLocalPartner().getPartnerId();
        String localPartnerType = messagePipeletParameter.getParticipant().getLocalPartner().getPartnerIdType();
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put( Constants.PARAMETER_PREFIX_EBXML20 +Constants.PROTOCOLSPECIFIC_FROM, localPartnerId );
        parameters.put( Constants.PARAMETER_PREFIX_EBXML20 +Constants.PROTOCOLSPECIFIC_FROMIDTYPE, localPartnerType );
        messagePipeletParameter.getMessagePojo().setCustomParameters( parameters );

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
