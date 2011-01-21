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
package org.nexuse2e.messaging;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.logging.LogMessage;
import org.nexuse2e.pojo.ChoreographyPojo;
import org.nexuse2e.pojo.MessagePojo;
import org.nexuse2e.pojo.ParticipantPojo;
import org.nexuse2e.pojo.PartnerPojo;

/**
 * This class is an abstract superclass for classes that need to validate choreography entities.
 * Choreography entities include the choreography itself and the participant.
 *
 * @author gesch, sschulze
 */
public abstract class ChoreographyValidator {

    private static Logger LOG = Logger.getLogger( ChoreographyValidator.class );

    private ChoreographyPojo getMatchingChoreography( List<ChoreographyPojo> choreographies, MessagePojo messagePojo )
            throws NexusException {

        Iterator<ChoreographyPojo> i = choreographies.iterator();
        while ( i.hasNext() ) {
            ChoreographyPojo choreography = i.next();
            if ( choreography.getName() == null || choreography.getName().equals( "" ) ) {
                LOG.error( "invalid choreography found in EngineConfiguration (ChoreographyId is missing)" );
                continue;
            }
            if ( messagePojo.getConversation().getChoreography().getName() == null
                    || messagePojo.getConversation().getChoreography().getName().equals( "" ) ) {
                LOG.error( "invalid message, no choreographyId found in message header (messageId:"
                        + messagePojo.getMessageId() + ")" );
                throw new NexusException( "Invalid message, no choreographyId found in message header" );
            }
            if ( choreography.getName().equals( messagePojo.getConversation().getChoreography().getName() ) ) {
                return choreography;
            }
        }
        return null;
    } // getMatchingChoreography
    
    /**
     * Validates the choreography from the given <code>MessageContext</code> and applies it to the message
     * context. If the choreography could not be validated, an exception is thrown.
     * @param messageContext The message context to validate the choreography for.
     * @return The validated choreography. Never <code>null</code>.
     * @throws NexusException If the choreography validation failed.
     */
    public ChoreographyPojo validateChoreography( MessageContext messageContext ) throws NexusException {

        MessagePojo messagePojo = null;
        String choreographyId = null;

        messagePojo = messageContext.getMessagePojo();

        if ( messagePojo.getConversation() != null ) {
            if ( LOG.isTraceEnabled() ) {
                LOG.trace( new LogMessage("Conversation:" + messagePojo.getConversation(),messagePojo) );
                LOG.trace( new LogMessage("Choreography:" + messagePojo.getConversation().getChoreography(),messagePojo) );
            }
            if ( LOG.isDebugEnabled() ) {
                LOG.debug( new LogMessage("ChoreographyID:" + messagePojo.getConversation().getChoreography().getName(),messagePojo) );
            }
            choreographyId = messagePojo.getConversation().getChoreography().getName();
        }

        if ( choreographyId == null || choreographyId.equals( "" ) ) {
            throw new NexusException( "No valid choreography ID found in message!" );
        }
        List<ChoreographyPojo> choreographies = Engine.getInstance().getCurrentConfiguration().getChoreographies();
        if ( choreographies == null || choreographies.size() == 0 ) {
            throw new NexusException( "No choreographies found in EngineConfiguration!" );
        }

        ChoreographyPojo choreography = getMatchingChoreography( choreographies, messagePojo );
        if ( choreography == null ) {
            throw new NexusException( "No matching choreography found for choreographyId: "
                    + messagePojo.getConversation().getChoreography().getName() );
        }
        messageContext.setChoreography( choreography );

        return choreography;
    } // validateChoreography

    /**
     * Validates the participant from the given <code>MessageContext</code> and applies it to the message
     * context. If the participant could not be validated, an exception is thrown.
     * @param messageContext The message context to validate the participant for.
     * @return The validated participant. Never <code>null</code>.
     * @throws NexusException If the participant validation failed.
     */
    private PartnerPojo getMatchingPartner( List<PartnerPojo> partners, MessagePojo messagePojo ) throws NexusException {

        Iterator<PartnerPojo> i = partners.iterator();
        while ( i.hasNext() ) {
            PartnerPojo partner = i.next();
            if ( partner.getPartnerId() == null || partner.getPartnerId().equals( "" ) ) {
                LOG.error( "invalid partner found in EngineConfiguration (partnerId is missing)" );
                continue;
            }
            if ( messagePojo.getConversation().getPartner().getPartnerId() == null
                    || messagePojo.getConversation().getPartner().getPartnerId().equals( "" ) ) {
                LOG.error( "invalid message, no partnerId found in message header (messageId:"
                        + messagePojo.getMessageId() + ")" );
                throw new NexusException( "Invalid message, no partnerId found in message header" );
            }
            if ( partner.getPartnerId().equals( messagePojo.getConversation().getPartner().getPartnerId() ) ) {
                return partner;
            }
        }
        return null;
    } // getMatchingChoreography

    public ParticipantPojo validateParticipant( MessageContext messagePipelineParameter ) throws NexusException {

        MessagePojo messagePojo = null;

        messagePojo = messagePipelineParameter.getMessagePojo();

        List<PartnerPojo> partners = Engine.getInstance().getCurrentConfiguration().getPartners();
        PartnerPojo partner = getMatchingPartner( partners, messagePojo );
        if ( partner == null ) {
            throw new NexusException( "No partner found for messageid: " + messagePojo.getMessageId() );
        }
        messagePipelineParameter.setCommunicationPartner( partner );

        ChoreographyPojo choreography = messagePipelineParameter.getChoreography();

        if ( choreography == null ) {
            throw new NexusException( "Choreography must not be null" );
        }
        Collection<ParticipantPojo> participants = choreography.getParticipants();
        if ( participants == null || participants.size() == 0 ) {
            throw new NexusException( "No particpants found for choreography: " + choreography.getName() );
        }
        Iterator<ParticipantPojo> i = participants.iterator();
        while ( i.hasNext() ) {
            ParticipantPojo participant = i.next();
            if ( participant.getPartner().getPartnerId() == null || participant.getPartner().getPartnerId().equals( "" ) ) {
                LOG.error( "Invalid participant configuration, partnerId must not be null" );
                continue;
            }
            if ( messagePojo.getConversation().getPartner().getPartnerId() == null
                    || messagePojo.getConversation().getPartner().getPartnerId().equals( "" ) ) {
                throw new NexusException( "No partnerId found in message header" );

            }
            if ( participant.getPartner().getPartnerId().equals(
                    messagePojo.getConversation().getPartner().getPartnerId() ) ) {
                messagePipelineParameter.setParticipant( participant );
                return participant;
            }
        }

        throw new NexusException( "No matching participant found for partner: "
                + messagePojo.getConversation().getPartner().getPartnerId() );
    } // validateParticipant
} // StateMachineExecutor