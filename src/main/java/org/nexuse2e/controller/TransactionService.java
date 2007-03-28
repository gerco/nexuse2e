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

import java.util.Date;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.nexuse2e.Manageable;
import org.nexuse2e.NexusException;
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
     * TODO ConversationId, unique for CPA (choreography) context, not partner
     * @param MessageId
     * @return
     */
    public abstract ConversationPojo getConversation( String choreographyId, String conversationId, String partnerId )
            throws NexusException;

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
    public abstract List getConversationsForReport( String status, int nxChoreographyId, int nxPartnerId,
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
     * @param messageId
     * @return
     * 
     * EbXML 2.0 Spec
     * 878 The REQUIRED element MessageId is a globally unique identifier for each message conforming to   
     * 879 MessageId [RFC2822].
     
     */
    public abstract MessagePojo getMessage( String messageId ) throws NexusException;

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
    public abstract List getMessagesForReport( String status, int nxChoreographyId, int nxPartnerId,
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
     * @param conversationPojo
     * @param messagePojo
     * @throws NexusException
     */
    public abstract void updateTransaction( ConversationPojo conversationPojo ) throws NexusException; // updateTransaction

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
     * Register a <code>ScheduledFuture</code> for a message that is being processed so that it can be 
     * stopped in case an acknowledgment is received.
     * @param id The message ID
     * @param handle The <code>ScheduledFuture</code> handle
     */
    public abstract void registerProcessingMessage( String id, ScheduledFuture handle,
            ScheduledExecutorService scheduler ); // registerProcessingMessage

    /**
     * Unregister a <code>ScheduledFuture</code> for a message that no longer needs processing 
     * and stop the scheduler for it.
     * @param id The message ID
     */
    public abstract void deregisterProcessingMessage( String id ); // deregisterProcessingMessage

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
     * @param session
     * @param transaction
     * @return
     * @throws NexusException
     */
    public abstract List<ConversationPojo> getConversationsByPartner( PartnerPojo partner, Session session,
            Transaction transaction ) throws NexusException;

    /**
     * @param partner
     * @param choreography
     * @param session
     * @param transaction
     * @return
     * @throws NexusException
     */
    public abstract List<ConversationPojo> getConversationsByPartnerAndChoreography( PartnerPojo partner,
            ChoreographyPojo choreography, Session session, Transaction transaction ) throws NexusException;

    /**
     * @param choreography
     * @param session
     * @param transaction
     * @return
     * @throws NexusException
     */
    public abstract List<ConversationPojo> getConversationsByChoreography( ChoreographyPojo choreography, 
            Session session, Transaction transaction ) throws NexusException;

    /**
     * @param partner
     * @param field
     * @param ascending
     * @param session
     * @param transaction
     * @return
     * @throws NexusException
     */
    public abstract List<MessagePojo> getMessagesByPartner( PartnerPojo partner, int field, boolean ascending,
            Session session, Transaction transaction ) throws NexusException;

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
            int field, boolean ascending, Session session, Transaction transaction ) throws NexusException;

    /**
     * @param message
     * @param session
     * @param transaction
     * @throws NexusException
     */
    public abstract void deleteMessage( MessagePojo message, Session session, Transaction transaction )
            throws NexusException;

    
    /**
     * @param conversation
     * @param session
     * @param transaction
     * @throws NexusException
     */
    public abstract void deleteConversation( ConversationPojo conversation, Session session, Transaction transaction )
    throws NexusException;

    /**
     * @param choreography
     * @param partner
     * @param field
     * @param ascending
     * @param session
     * @param transaction
     * @return
     * @throws NexusException
     */
    public abstract List<MessagePojo> getMessagesByChoreographyAndPartner( ChoreographyPojo choreography,
            PartnerPojo partner, int field, boolean ascending, Session session, Transaction transaction )
            throws NexusException;

    /**
     * @param choreography
     * @param partner
     * @param conversation
     * @param field
     * @param ascending
     * @param session
     * @param transcaction
     * @return
     * @throws NexusException
     */
    public abstract List<MessagePojo> getMessagesByChoreographyPartnerAndConversation( ChoreographyPojo choreography,
            PartnerPojo partner, ConversationPojo conversation, int field, boolean ascending, Session session,
            Transaction transcaction ) throws NexusException;

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
} // TransactionService