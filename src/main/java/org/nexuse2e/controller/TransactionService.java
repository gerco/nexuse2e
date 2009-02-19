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
package org.nexuse2e.controller;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import org.nexuse2e.Manageable;
import org.nexuse2e.NexusException;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.pojo.ActionPojo;
import org.nexuse2e.pojo.ChoreographyPojo;
import org.nexuse2e.pojo.ConversationPojo;
import org.nexuse2e.pojo.LogPojo;
import org.nexuse2e.pojo.MessagePayloadPojo;
import org.nexuse2e.pojo.MessagePojo;
import org.nexuse2e.pojo.PartnerPojo;

/**
 * Controller to handle all transaction related operations.
 *
 * @author mbreilmann
 */
public interface TransactionService extends Manageable {

    /**
     * Create a new conversation
     * 
     * @param choreographyId The identifier of the choroegraphy the conversation is related to.
     * @param partnerId The idenifier of the partner to commmunicate with.
     * @param conversationId The identifier of the new conversation or NULL if one shall be created.
     * @return A new <code>ConversationPojo</code> instance
     * @throws NexusException Thrown if the converation could not be created.
     */
    public abstract ConversationPojo createConversation( String choreographyId, String partnerId, String conversationId )
            throws NexusException;

    /**
     * Get a conversation by its identifier.
     * 
     * @param conversationId
     * @return
     */
    public abstract ConversationPojo getConversation( String conversationId ) throws NexusException;

    /**
     * Gets a conversation by it's primary key.
     * @param nxConversationId The NEXUS conversation ID.
     * @return The <code>ConversationPojo</code> with the given ID, or <code>null</code> if none exists.
     */
    public abstract ConversationPojo getConversation( int nxConversationId ) throws NexusException;

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
    public abstract List<ConversationPojo> getConversationsForReport( String status, int nxChoreographyId, int nxPartnerId,
            String conversationId, Date start, Date end, int itemsPerPage, int page, int field, boolean ascending )
            throws NexusException;

    /**
     * @param status
     * @param nxChoreographyId
     * @param nxPartnerId
     * @param conversationId
     * @param start
     * @param end
     * @param field
     * @param ascending
     * @return
     * @throws NexusException
     */
    public abstract int getConversationsCount( String status, int nxChoreographyId, int nxPartnerId,
            String conversationId, Date start, Date end, int field, boolean ascending ) throws NexusException;

    /**
     * Creates a <code>MessageContext</code> from a given <code>MessagePojo</code>.
     * @param messagePojo The <code>MessagePojo</code> to be wrapped in a <code>MessageContext</code>.
     * @return A newly created <code>MessageContext</code> wrapping the given <code>MessagePojo</code>,
     * or <code>null</code> if <code>messagePojo</code> is <code>null</code>.
     */
    public abstract MessageContext createMessageContext( MessagePojo messagePojo );
    
    /**
     * @param messageId
     * @return
     * 
     * EbXML 2.0 Spec
     * 878 The REQUIRED element MessageId is a globally unique identifier for each message conforming to   
     * 879 MessageId [RFC2822].
     */
    public abstract MessagePojo getMessage( String messageId ) throws NexusException;

    /**
     * @param messageId
     * @return
     * 
     * EbXML 2.0 Spec
     * 878 The REQUIRED element MessageId is a globally unique identifier for each message conforming to   
     * 879 MessageId [RFC2822].
     */
    public abstract MessagePojo getMessage( String messageId, boolean isReferencedMessageId ) throws NexusException;

    /**
     * @param messageId
     * @return
     * @throws NexusException
     */
    public abstract MessageContext getMessageContext( String messageId ) throws NexusException;
    
    /**
     * @param messageId
     * @return
     * @throws NexusException
     */
    public abstract MessageContext getMessageContext( String messageId, boolean isReferencedMessageId ) throws NexusException;
    
    /**
     * @param status
     * @param nxChoreographyId
     * @param nxPartnerId
     * @param conversationId
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
     * @param conversation
     * @return
     * @throws NexusException
     */
    public abstract List<MessagePojo> getMessagesFromConversation( ConversationPojo conversation )
            throws NexusException;

    /**
     * @param message
     * @return
     * @throws NexusException
     */
    public abstract List<MessagePayloadPojo> getMessagePayloadsFromMessage( MessagePojo message ) throws NexusException;

    /**
     * @param messageId
     * @param conversationId
     * @param actionId
     * @param partnerId
     * @param choreographyId
     * @return
     * @throws NexusException
     */
    public abstract MessagePojo createMessage( String messageId, String conversationId, String actionId,
            String partnerId, String choreographyId, int messageType ) throws NexusException; // getNewMessage

    /**
     * @param messageId
     * @param conversationId
     * @param actionId
     * @param partnerId
     * @param choreographyId
     * @return
     * @throws NexusException
     */
    public abstract MessagePojo initializeMessage( MessagePojo message, String messageId, String conversationId,
            String actionId, String partnerId, String choreographyId ) throws NexusException; // initializeMessage

    /**
     * Return a list of all active/pending messages that need to be restored/processes on system restart
     * @return The <code>List</code> of active <code>MessagePojo</code> instances
     * @throws NexusException Exception thrown if problems occured while messages were retrieved
     */
    public abstract List<MessagePojo> getActiveMessages() throws NexusException;

    /**
     * @param conversationPojo
     * @param messagePojo
     * @throws NexusException
     */
    public abstract void storeTransaction( ConversationPojo conversationPojo, MessagePojo messagePojo )
            throws NexusException; // storeTransaction

    /**
     * Updates the given message and it's parent conversation. If the status transition cannot be
     * performed, this method updates the transaction but leaves the status unchanged unless
     * <code>force</code> is set to <code>true</code>.
     * @param message The message to update.
     * @param force If <code>true</code>, this will force the transaction to be updated exactly as
     * passed here, even if the status transition is invalid. <code>force</code> shall always be
     * <code>false</code> for automatic (state machine based) transitions.
     * @throws NexusException If a persistence layer problem occurred.
     * @throws StateTransitionException If the given message could not be updated because of an
     * illegal state transition. This will only be thrown if <code>force</code> is set to
     * <code>false</code>.
     */
    public abstract void updateTransaction( MessagePojo message, boolean force )
            throws NexusException, StateTransitionException;

    /**
     * Updates the given message and it's parent conversation. If the status transition cannot be
     * performed, this method updates the transaction but leaves the status unchanged.
     * @param message The message to update.
     * @throws NexusException If a persistence layer problem occurred.
     * @throws StateTransitionException If the given message could not be updated because of an
     * illegal state transition.
     */
    public abstract void updateTransaction( MessagePojo message )
            throws NexusException, StateTransitionException;

    /**
     * @param messagePojo
     * @throws NexusException
     */
    public abstract void updateMessage( MessagePojo messagePojo ) throws NexusException; // updateMessage

    /**
     * @param messagePojo
     * @throws NexusException
     */
    public abstract void updateConversation( ConversationPojo conversationPojo ) throws NexusException; // updateMessage

    /**
     * Determine whether a message is being processed.
     * @param id The message ID
     * @return TRUE if the message is being processed.
     */
    public abstract boolean isProcessingMessage( String id );

    /**
     * Register a <code>ScheduledFuture</code> for a message that is being processed so that it can be 
     * stopped in case an acknowledgment is received.
     * @param id The message ID
     * @param handle The <code>ScheduledFuture</code> handle
     */
    public abstract void registerProcessingMessage( String id, ScheduledFuture<?> handle,
            ScheduledExecutorService scheduler ); // registerProcessingMessage

    /**
     * Unregister a <code>ScheduledFuture</code> for a message that no longer needs processing 
     * and stop the scheduler for it.
     * @param id The message ID
     */
    public abstract void deregisterProcessingMessage( String id ); // deregisterProcessingMessage

    /**
     * Unregister a <code>ScheduledFuture</code> for a message that no longer needs processing, 
     * stop the scheduler for it and set the message to state 'STOPPED'.
     * @param id The message ID
     */
    public abstract void stopProcessingMessage( String id ) throws NexusException; // stopProcessingMessage

    /**
     * @param messageId
     */
    public abstract void addSynchronousRequest( String messageId );

    /**
     * @param messageId
     * @return
     */
    public abstract boolean isSynchronousReply( String messageId );

    /**
     * @param messageId
     */
    public abstract void removeSynchronousRequest( String messageId );

    /**
     * @param partner
     * @return
     * @throws NexusException
     */
    public abstract List<ConversationPojo> getConversationsByPartner( PartnerPojo partner ) throws NexusException;

    /**
     * @param partner
     * @param choreography
     * @return
     * @throws NexusException
     */
    public abstract List<ConversationPojo> getConversationsByPartnerAndChoreography( PartnerPojo partner,
            ChoreographyPojo choreography ) throws NexusException;

    /**
     * @param choreography
     * @return
     * @throws NexusException
     */
    public abstract List<ConversationPojo> getConversationsByChoreography( ChoreographyPojo choreography ) throws NexusException;

    /**
     * @param partner
     * @param field
     * @param ascending
     * @return
     * @throws NexusException
     */
    public abstract List<MessagePojo> getMessagesByPartner( PartnerPojo partner, int field, boolean ascending ) throws NexusException;

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

    /**
     * @param message
     * @param session
     * @param transaction
     * @throws NexusException
     */
    public abstract void deleteMessage( MessagePojo message )
            throws NexusException;

    
    /**
     * @param conversation
     * @throws NexusException
     */
    public abstract void deleteConversation( ConversationPojo conversation )
    throws NexusException;

    
    /**
     * @param logEntry
     * @throws NexusException
     */
    public abstract void deleteLogEntry( LogPojo logEntry )
    throws NexusException;

    
    
    
    /**
     * @param choreography
     * @param partner
     * @param field
     * @param ascending
     * @return
     * @throws NexusException
     */
    public abstract List<MessagePojo> getMessagesByChoreographyAndPartner( ChoreographyPojo choreography,
            PartnerPojo partner, int field, boolean ascending )
            throws NexusException;

    /**
     * Gets messages by their action, partner direction and status.
     * @param action The action. Must not be <code>null</code>.
     * @param partner The partner. Must not be <code>null</code>.
     * @param outbound <code>true</code> for outbound, <code>false</code> for inbound messages.
     * @param status The message status.
     * @param field The field to sort by.
     * @param ascending <code>true</code> for ascending, <code>false</code> for descending.
     * @return A list of messages. Empty if none were found.
     * @throws NexusException If something went wrong.
     */
    public List<MessagePojo> getMessagesByActionPartnerDirectionAndStatus(
            ActionPojo action,
            PartnerPojo partner,
            boolean outbound,
            int status,
            int field,
            boolean ascending ) throws NexusException;

    
    /**
     * @param choreography
     * @param partner
     * @param conversation
     * @param field
     * @param ascending
     * @return
     * @throws NexusException
     */
    public abstract List<MessagePojo> getMessagesByChoreographyPartnerAndConversation( ChoreographyPojo choreography,
            PartnerPojo partner, ConversationPojo conversation, int field, boolean ascending ) throws NexusException;

    /**
     * @param origin
     * @param severity
     * @param messageText
     * @param start
     * @param end
     * @param field
     * @param ascending
     * @return
     * @throws PersistenceException
     */
    public abstract int getLogEntriesForReportCount( String severity, String messageText, Date start, Date end,
            int field, boolean ascending ) throws NexusException;

    /**
     * @param severity
     * @param messageText
     * @param start
     * @param end
     * @param itemsPerPage
     * @param page
     * @param field
     * @param ascending
     * @return
     * @throws PersistenceException
     */
    public abstract List<LogPojo> getLogEntriesForReport( String severity, String messageText, Date start, Date end,
            int itemsPerPage, int page, int field, boolean ascending ) throws NexusException;
    
    
   
    
    /**
     * Gets the synchronization <code>Object</code> for the given <code>ConversationPojo</code>.
     * @param conversation The conversation to get a synchronization object for.
     * @return A non-<code>null</code> <code>Object</code>.
     */
    public abstract Object getSyncObjectForConversation( ConversationPojo conversation );
    
    public abstract int getCreatedMessagesSinceCount( Timestamp timestamp ) throws NexusException;
    
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
    
    
} // TransactionService