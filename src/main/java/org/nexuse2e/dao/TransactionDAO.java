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
package org.nexuse2e.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.nexuse2e.NexusException;
import org.nexuse2e.controller.StateTransitionException;
import org.nexuse2e.pojo.ActionPojo;
import org.nexuse2e.pojo.ChoreographyPojo;
import org.nexuse2e.pojo.ConversationPojo;
import org.nexuse2e.pojo.LogPojo;
import org.nexuse2e.pojo.MessagePayloadPojo;
import org.nexuse2e.pojo.MessagePojo;
import org.nexuse2e.pojo.PartnerPojo;

public interface TransactionDAO {

    public static final int SORT_NONE     = 0;
    public static final int SORT_CREATED  = 1;
    public static final int SORT_MODIFIED = 2;
    public static final int SORT_STATUS   = 3;
    public static final int SORT_CPAID    = 4;
    public static final int SORT_ACTION   = 5;

    /**
     * Find a conversation by its identifier
     * @param conversationId The converstaion identifier
     * @return
     */
    public abstract ConversationPojo getConversationByConversationId( String conversationId ); // getConversationByConversationId

    /**
     * Gets a <code>ConversationPojo</code> by it's primary key.
     * @param nxConversationId The NEXUS conversation ID.
     * @return The conversation, or <code>null</code> if none with the given ID exists.
     */
    public abstract ConversationPojo getConversationByConversationId( int nxConversationId ); // getConversationByConversationId

    public abstract MessagePojo getMessageByMessageId( String messageId ) throws NexusException;

    public abstract MessagePojo getMessageByReferencedMessageId( String messageId ) throws NexusException;

    /**
     * @param status
     * @param nxChoreographyId
     * @param nxPartnerId
     * @param nxConversationId
     * @param messageId
     * @param startDate
     * @param endDate
     * @return
     * @throws NexusException
     */
    public abstract int getMessagesCount( String status, int nxChoreographyId, int nxPartnerId, String conversationId,
            String messageId, Date startDate, Date endDate ) throws NexusException;

    /**
     * @return
     * @throws NexusException
     */
    public abstract List<MessagePojo> getActiveMessages() throws NexusException; // getActiveMessages

    /**
     * @param status
     * @param nxChoreographyId
     * @param nxPartnerId
     * @param nxConversationId
     * @param messageId
     * @param type
     * @param start
     * @param end
     * @param itemsPerPage
     * @param page
     * @param field
     * @param ascending
     * @return
     * @throws NexusException
     */
    public abstract List<MessagePojo> getMessagesForReport( String status, int nxChoreographyId, int nxPartnerId,
            String conversationId, String messageId, String type, Date start, Date end, int itemsPerPage, int page,
            int field, boolean ascending ) throws NexusException;

    /**
     * @param status
     * @param nxChoreographyId
     * @param nxPartnerId
     * @param conversationId
     * @param start
     * @param end
     * @param itemsPerPage
     * @param page
     * @param field
     * @param ascending
     * @return
     * @throws NexusException
     */
    public abstract List<ConversationPojo> getConversationsForReport( String status, int nxChoreographyId,
            int nxPartnerId, String conversationId, Date start, Date end, int itemsPerPage, int page, int field,
            boolean ascending ) throws NexusException;

    /**
     * Gets the number of conversations created between the given start and end dates.
     * @param start The start date. May be <code>null</code> for stone age.
     * @param end The end date. May be <code>null</code> for Star Wars age.
     * @return The number of conversations that have been created between <code>start</code> and <code>end</code>.
     * @throws NexusException If something went wrong.
     */
    public abstract long getConversationsCount( Date start, Date end ) throws NexusException;

    /**
     * Gets the number of messages associated with a conversation that has been created
     * between the given start and end dates.
     * @param start The start date. May be <code>null</code> for stone age.
     * @param end The end date. May be <code>null</code> for Star Wars age.
     * @return The number of messages in conversations that have been created between
     * <code>start</code> and <code>end</code>.
     * @throws NexusException If something went wrong.
     */
    public abstract long getMessagesCount( Date start, Date end ) throws NexusException;

    /**
     * Gets the number of log entries created between the given start and end dates.
     * @param start The start date. May be <code>null</code> for stone age.
     * @param end The end date. May be <code>null</code> for Star Wars age.
     * @return The number of log entries that have been created between <code>start</code> and <code>end</code>.
     * @throws NexusException If something went wrong.
     */
    public abstract long getLogCount( Date start, Date end ) throws NexusException;

    /**
     * @param start
     * @param end
     * @param session
     * @param transaction
     * @return
     * @throws NexusException
     */
    public abstract long removeLogEntries( Date start, Date end ) throws NexusException;

    /**
     * Removes all conversations that have been created between the given start and end dates.
     * @param start The start date. May be <code>null</code> for stone age.
     * @param end The end date. May be <code>null</code> for Star Wars age.
     * @return The number of conversations that have been deleted.
     * @throws NexusException If something went wrong.
     */
    public abstract long removeConversations( Date start, Date end ) throws NexusException;

    /**
     * @param status
     * @param choreographyId
     * @param participantId
     * @param conversationId
     * @param start
     * @param end
     * @param field
     * @param ascending
     * @return
     * @throws PersistenceException
     */
    public abstract int getConversationsCount( String status, int nxChoreographyId, int nxPartnerId,
            String conversationId, Date start, Date end, int field, boolean ascending ) throws NexusException;

    public abstract void storeTransaction( ConversationPojo conversationPojo, MessagePojo messagePojo )
            throws NexusException; // storeTransaction

    /**
     * @param partner
     * @param session
     * @param transaction
     * @return
     * @throws NexusException
     */
    public abstract List<ConversationPojo> getConversationsByPartner( PartnerPojo partner );

    /**
     * @param partner
     * @param choreography
     * @param session
     * @param transaction
     * @return
     * @throws NexusException
     */
    public abstract List<ConversationPojo> getConversationsByPartnerAndChoreography( PartnerPojo partner,
            ChoreographyPojo choreography ) throws NexusException;

    /**
     * @param choreography
     * @return
     */
    public abstract List<ConversationPojo> getConversationsByChoreography( ChoreographyPojo choreography );

    /**
     * @param partner
     * @param session
     * @param transaction
     * @return
     * @throws NexusException
     */
    public abstract List<MessagePojo> getMessagesByPartner( PartnerPojo partner, int field, boolean ascending )
            throws NexusException;

    /**
     * @param messagePojo
     * @param session
     * @param transaction
     * @throws NexusException
     */
    public abstract void deleteMessage( MessagePojo messagePojo ) throws NexusException; // updateMessage

    /**
     * @param conversationPojo
     * @param session
     * @param transaction
     * @throws NexusException
     */
    public abstract void deleteConversation( ConversationPojo conversationPojo ) throws NexusException; // updateMessage

    /**
     * @param partner
     * @param outbound
     * @param field
     * @param ascending
     * @param session
     * @param transaction
     * @return
     * @throws NexusException
     */
    public abstract List<MessagePojo> getMessagesByPartnerAndDirection( PartnerPojo partner, boolean outbound,
            int field, boolean ascending ) throws NexusException;

    public abstract List<MessagePojo> getMessagesByActionPartnerDirectionAndStatus( ActionPojo action,
            PartnerPojo partner, boolean outbound, int status, int field, boolean ascending );

    /**
     * @param choreography
     * @param partner
     * @param field
     * @param ascending
     * @return
     */
    public abstract List<MessagePojo> getMessagesByChoreographyAndPartner( ChoreographyPojo choreography,
            PartnerPojo partner, int field, boolean ascending );

    /**
     * @param choreography
     * @param partner
     * @param conversation
     * @param field
     * @param ascending
     * @return
     */
    public abstract List<MessagePojo> getMessagesByChoreographyPartnerAndConversation( ChoreographyPojo choreography,
            PartnerPojo partner, ConversationPojo conversation, int field, boolean ascending );

    /**
     * @param logEntry
     * @param session
     * @param transaction
     */
    public abstract void deleteLogEntry( LogPojo logEntry ) throws NexusException;

    public abstract List<MessagePayloadPojo> fetchLazyPayloads( MessagePojo message );

    public abstract List<MessagePojo> fetchLazyMessages( ConversationPojo conversation );

    public abstract void updateTransaction( MessagePojo message, boolean force ) throws NexusException,
            StateTransitionException; // updateTransaction

    /**
     * Checks if the transition to the given status is allowed and returns it if so.
     * @param message The original message.
     * @param conversationStatus The target conversation status.
     * @return <code>conversationStatus</code> if transition is allowed, or the original
     * conversation status if not.
     */
    public abstract int getAllowedTransitionStatus( ConversationPojo conversation, int conversationStatus );

    /**
     * Checks if the transition to the given status is allowed and returns it if so.
     * @param message The original message.
     * @param messageStatus The target message status.
     * @return <code>messageStatus</code> if transition is allowed, or the original
     * message status if not.
     */
    public abstract int getAllowedTransitionStatus( MessagePojo message, int messageStatus );

    /**
     * Gets a count of messages that have been created since the given time. 
     * @param since The earliest creation date of messages that shall be counted.
     * @return A count.
     * @throws NexusException if something went wrong.
     */
    public abstract int getCreatedMessagesSinceCount( Date since ) throws NexusException;
    
    /**
     * Convenience method for direct hibernate session access. This method shall only
     * be called if advanced features are required that are not directly supported by
     * <code>TransactionDAO</code>.
     * @return A DB session.
     */
    public Session getDBSession();

    /**
     * Release the given session.
     * @param session The session to be released.
     */
    public void releaseDBSession( Session session );
}