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
package org.nexuse2e.controller;

import java.lang.ref.WeakReference;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.nexuse2e.ActionSpecificKey;
import org.nexuse2e.BeanStatus;
import org.nexuse2e.Engine;
import org.nexuse2e.Layer;
import org.nexuse2e.NexusException;
import org.nexuse2e.ProtocolSpecificKey;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.configuration.IdGenerator;
import org.nexuse2e.dao.LogDAO;
import org.nexuse2e.dao.TransactionDAO;
import org.nexuse2e.dao.UpdateTransactionOperation;
import org.nexuse2e.logging.LogMessage;
import org.nexuse2e.messaging.Constants;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.pojo.ActionPojo;
import org.nexuse2e.pojo.ChoreographyPojo;
import org.nexuse2e.pojo.ConversationPojo;
import org.nexuse2e.pojo.LogPojo;
import org.nexuse2e.pojo.MessagePayloadPojo;
import org.nexuse2e.pojo.MessagePojo;
import org.nexuse2e.pojo.ParticipantPojo;
import org.nexuse2e.pojo.PartnerPojo;

/**
 * @author gesch
 *
 */
public class TransactionServiceImpl implements TransactionService {

    private static Logger                             LOG                = Logger
                                                                                 .getLogger( TransactionServiceImpl.class );

    private HashMap<String, ScheduledFuture<?>>       processingMessages = new HashMap<String, ScheduledFuture<?>>();
    private Hashtable<String, String>                 synchronousReplies = new Hashtable<String, String>();

    private Map<String, WeakReference<Object>>        syncObjects        = new HashMap<String, WeakReference<Object>>();

    private BeanStatus                      status             = BeanStatus.UNDEFINED;

    private TransactionDAO                            transactionDao     = null;
    private LogDAO                                    logDao             = null;

    
    /*
     * Completes the given <code>ConversationPojo</code> list by using the current engine configuration.
     * @param conversations The conversations to be completed.
     * @param completeMessages If <code>true</code>, indicates that message associations shall be
     * completed, too.
     * @return A conversation pojo list filled with the fields that were initially set up with proxies
     * for lazy loading. This is a reference to the object passed as argument.
     * @throws NexusException If something went wrong
     */
    protected List<ConversationPojo> completeConversations(
            List<ConversationPojo> conversations, boolean completeMessages ) throws NexusException {
        if (conversations == null) {
            return null;
        }
        for (ConversationPojo c : conversations) {
            if (c != null) {
                complete( c, completeMessages );
            }
        }
        return conversations;
    }
    
    /**
     * Completes the given <code>ConversationPojo</code> by using the current engine configuration.
     * @param conversation The conversation to be completed.
     * @return A conversation pojo filled with the fields that were initially set up with proxies
     * for lazy loading. This is a reference to the object passed as argument.
     * @throws NexusException If something went wrong
     */
    protected ConversationPojo complete( ConversationPojo conversation ) throws NexusException {
        return complete( conversation, true );
    }
    
    /**
     * Completes the given <code>ConversationPojo</code> by using the current engine configuration.
     * @param message The conversation to be completed.
     * @param completeMessages If <code>true</code>, child messages will be completed as well.
     * Pass <code>true</code> if in doubt.
     * @return A conversation pojo filled with the fields that were initially set up with proxies
     * for lazy loading. This is a reference to the object passed as argument.
     * @throws NexusException If something went wrong
     */
    protected ConversationPojo complete( ConversationPojo conversation, boolean completeMessages ) throws NexusException {
        if (conversation == null) {
            return null;
        }
        
        EngineConfiguration c = Engine.getInstance().getCurrentConfiguration();
        if (conversation.getChoreography() != null) {
            conversation.setChoreography( c.getChoreographyByNxChoreographyId( conversation.getChoreography().getNxChoreographyId() ) );
        }
        if (conversation.getPartner() != null) {
            conversation.setPartner( c.getPartnerByNxPartnerId( conversation.getPartner().getNxPartnerId() ) );
        }
        if (conversation.getCurrentAction() != null && conversation.getChoreography() != null) {
            ActionPojo currentAction = c.getActionFromChoreographyByNxActionId(conversation.getChoreography(), conversation.getCurrentAction().getNxActionId());
            conversation.setCurrentAction(currentAction);
        }
        
        if (completeMessages) {
            for (MessagePojo m : conversation.getMessages()) {
                complete( m, false );
            }
        }
        
        return conversation;
    }
    
    /**
     * Completes the given <code>MessagePojo</code> list by using the current engine configuration.
     * @param messages The messages to be completed.
     * @return A message pojo list filled with the fields that were initially set up with proxies
     * for lazy loading. This is a reference to the object passed as argument.
     * @throws NexusException If something went wrong
     */
    protected List<MessagePojo> completeMessages( List<MessagePojo> messages ) throws NexusException {
        if (messages == null) {
            return null;
        }
        for (MessagePojo c : messages) {
            if (c != null) {
                complete( c );
            }
        }
        return messages;
    }
    
    /**
     * Completes the given <code>MessagePojo</code> by using the current engine configuration.
     * @param message The message to be completed.
     * @return A message pojo filled with the fields that were initially set up with proxies
     * for lazy loading. This is a reference to the object passed as argument.
     * @throws NexusException if something went wrong.
     */
    protected MessagePojo complete( MessagePojo message ) throws NexusException {
        return complete( message, true );
    }
    
    /**
     * Completes the given <code>MessagePojo</code> by using the current engine configuration.
     * @param message The message to be completed.
     * @param completeConversation If <code>true</code>, parent conversation will be completed as well.
     * Pass <code>true</code> if in doubt.
     * @return A message pojo filled with the fields that were initially set up with proxies
     * for lazy loading. This is a reference to the object passed as argument.
     * @throws NexusException if something went wrong.
     */
    protected MessagePojo complete( MessagePojo message, boolean completeConversation ) throws NexusException {
        if (message == null || message.getAction() == null || message.getConversation() == null || message.getConversation().getChoreography() == null) {
            return message;
        }
        
        EngineConfiguration c = Engine.getInstance().getCurrentConfiguration();
        if (completeConversation) {
            complete( message.getConversation(), false );
        }
        if (message.getConversation() != null) {
            ActionPojo action = c.getActionFromChoreographyByNxActionId( message.getConversation().getChoreography(), message.getAction().getNxActionId() );
            message.setAction( action );
        }

        return message;
    }
    

    public ConversationPojo createConversation( String choreographyId, String partnerId, String conversationId )
            throws NexusException {

        ConversationPojo conversationPojo = null;

        ChoreographyPojo choreography = Engine.getInstance().getActiveConfigurationAccessService()
                .getChoreographyByChoreographyId( choreographyId );
        if ( choreography == null ) {
            throw new NexusException( "No choreography found for choreography ID: " + choreographyId );
        }

        PartnerPojo partner = Engine.getInstance().getActiveConfigurationAccessService().getPartnerByPartnerId(
                partnerId );
        if ( partner == null ) {
            throw new NexusException( "No partner found for partner ID: " + partnerId );
        }

        if ( ( conversationId == null ) || ( conversationId.trim().length() == 0 ) ) {
            IdGenerator idGenerator = Engine.getInstance().getIdGenerator( Constants.ID_GENERATOR_CONVERSATION );
            conversationId = idGenerator.getId();

            conversationPojo = new ConversationPojo();
            conversationPojo.setChoreography( choreography );
            conversationPojo.setConversationId( conversationId );
            conversationPojo.setPartner( partner );

            storeTransaction( conversationPojo, null );
        } else {
            conversationPojo = getConversation( conversationId );
            if ( conversationPojo == null ) {
                conversationPojo = new ConversationPojo();
                conversationPojo.setChoreography( choreography );
                conversationPojo.setConversationId( conversationId );
                conversationPojo.setPartner( partner );

                storeTransaction( conversationPojo, null );
            }
        }

        return conversationPojo;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.controller.TransactionService#getConversation(java.lang.String)
     */
    public ConversationPojo getConversation( String conversationId ) throws NexusException {

        LOG.trace(new LogMessage(  "Entering TransactionDataService.getConversation...",conversationId,"") );
        return complete( getTransactionDao().getConversationByConversationId( conversationId ) );

    }

    public ConversationPojo getConversation( int nxConversationId )
            throws NexusException {

        LOG.trace( "Entering TransactionDataService.getConversation..." );
        return complete( getTransactionDao().getConversationByConversationId( nxConversationId ) );

        

    }

    /* (non-Javadoc)
     * @see org.nexuse2e.controller.TransactionService#getConversationsForReport(java.lang.String, int, int, java.lang.String, java.util.Date, java.util.Date, int, int, int, boolean)
     */
    public List<ConversationPojo> getConversationsForReport( String status, int nxChoreographyId, int nxPartnerId,
            String conversationId, Date start, Date end, int itemsPerPage, int page, int field, boolean ascending ) throws NexusException {

        return completeConversations( getTransactionDao().getConversationsForReport(
                status, nxChoreographyId, nxPartnerId, conversationId, start,
                end, itemsPerPage, page, field, ascending ), false );

    }
    

	
    /* (non-Javadoc)
     * @see org.nexuse2e.controller.TransactionService#getConversationsCount(java.lang.String, int, int, java.lang.String, java.util.Date, java.util.Date, int, boolean)
     */
    public int getConversationsCount( String status, int nxChoreographyId, int nxPartnerId, String conversationId,
            Date start, Date end, int field, boolean ascending ) throws NexusException {

        return getTransactionDao().getConversationsCount(
                status, nxChoreographyId, nxPartnerId, conversationId, start, end, field, ascending );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.controller.TransactionService#getMessage(java.lang.String)
     */
    public MessagePojo getMessage( String messageId ) throws NexusException {

        return getMessage( messageId, false );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.controller.TransactionService#getMessage(java.lang.String)
     */
    public MessagePojo getMessage( String messageId, boolean isReferencedMessageId ) throws NexusException {

        if ( isReferencedMessageId ) {
            return complete( getTransactionDao().getMessageByReferencedMessageId( messageId ) );
        } else {
            return complete( getTransactionDao().getMessageByMessageId( messageId ) );
        }
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.controller.TransactionService#getMessagesForReport(java.lang.String, int, int, java.lang.String, java.lang.String, java.lang.String, java.util.Date, java.util.Date, int, int, int, boolean)
     */
    public List<MessagePojo> getMessagesForReport( String status, int nxChoreographyId, int nxPartnerId,
            String conversationId, String messageId, String type, Date start, Date end, int itemsPerPage, int page,
            int field, boolean ascending ) throws NexusException {

        return completeMessages( getTransactionDao().getMessagesForReport(
                status, nxChoreographyId, nxPartnerId, conversationId, messageId,
                type, start, end, itemsPerPage, page, field, ascending ) );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.controller.TransactionService#getMessagesCount(java.lang.String, int, int, java.lang.String, java.lang.String, java.util.Date, java.util.Date)
     */
    public int getMessagesCount( String status, int nxChoreographyId, int nxPartnerId, String conversationId,
            String messageId, Date startDate, Date endDate ) throws NexusException {

        return getTransactionDao().getMessagesCount(
                status, nxChoreographyId, nxPartnerId, conversationId, messageId, startDate, endDate );

    }

    /* (non-Javadoc)
     * @see org.nexuse2e.controller.TransactionService#getMessagesFromConversation(org.nexuse2e.pojo.ConversationPojo)
     */
    public List<MessagePojo> getMessagesFromConversation( ConversationPojo conversation ) throws NexusException {

        List<MessagePojo> messages = getTransactionDao().fetchLazyMessages( conversation );
        return completeMessages( messages );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.controller.TransactionService#getMessagePayloadsFromMessage(org.nexuse2e.pojo.MessagePojo)
     */
    public List<MessagePayloadPojo> getMessagePayloadsFromMessage( MessagePojo message ) throws NexusException {

        List<MessagePayloadPojo> payloads = getTransactionDao().fetchLazyPayloads( message );
        return payloads;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.controller.TransactionService#getNewMessage(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, int)
     */
    public MessagePojo createMessage( String messageId, String conversationId, String actionId, String partnerId,
            String choreographyId, int messageType ) throws NexusException {

        MessagePojo messagePojo = new MessagePojo();
        messagePojo.setType( messageType );
        return initializeMessage( messagePojo, messageId, conversationId, actionId, partnerId, choreographyId );
    } // getNewMessage

    /* (non-Javadoc)
     * @see org.nexuse2e.controller.TransactionService#getActiveMessages()
     */
    public List<MessagePojo> getActiveMessages() throws NexusException {

        return completeMessages( getTransactionDao().getActiveMessages() );
    } // getActiveMessages

    /* (non-Javadoc)
     * @see org.nexuse2e.controller.TransactionService#initializeMessage(org.nexuse2e.pojo.MessagePojo, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public MessagePojo initializeMessage( MessagePojo message, String messageId, String conversationId,
            String actionId, String partnerId, String choreographyId ) throws NexusException {

        String senderId = null;
        String senderIdType = null;

        if ( message == null ) {
            message = new MessagePojo();
        }
        if ( message.getCustomParameters() == null ) {
            HashMap<String, String> customParameters = new HashMap<String, String>();
            message.setCustomParameters( customParameters );
        }

        //TODO refactor and using correct Constants.

        message.setMessageId( messageId );
        Date date = new Date();
        if ( message.getCreatedDate() == null ) {
            message.setCreatedDate( date );
        }
        if ( message.getModifiedDate() == null ) {
            message.setModifiedDate( date );
        }
        TransactionDAO transactionDao = getTransactionDao();
        PartnerPojo partner = Engine.getInstance().getActiveConfigurationAccessService().getPartnerByPartnerId(
                partnerId );
        if ( partner == null ) {
            throw new NexusException( "No partner found for PartnerId: '" + partnerId + "'" );
        }
        ChoreographyPojo choreography = Engine.getInstance().getActiveConfigurationAccessService()
                .getChoreographyByChoreographyId( choreographyId );
        if ( choreography == null ) {
            throw new NexusException( "No choreography found for ChoreographyId: " + choreographyId );
        }

        ParticipantPojo participant = Engine.getInstance().getActiveConfigurationAccessService()
                .getParticipantFromChoreographyByPartner( choreography, partner );
        if ( participant == null ) {
            throw new NexusException( "No participant " + partnerId + " found for ChoreographyId: " + choreographyId );
        }

        senderId = participant.getLocalPartner().getPartnerId();
        senderIdType = participant.getLocalPartner().getPartnerIdType();

        //TODO refactor and standard constants
        message.getCustomParameters().put(
                org.nexuse2e.messaging.ebxml.v20.Constants.PARAMETER_PREFIX_EBXML20 + "from", senderId );
        message.getCustomParameters().put(
                org.nexuse2e.messaging.ebxml.v20.Constants.PARAMETER_PREFIX_EBXML20 + "fromIdType", senderIdType );

        message.setTRP( participant.getConnection().getTrp() );
        ActionPojo action = null;
        if ( message.isNormal() ) {
            action = Engine.getInstance().getActiveConfigurationAccessService().getActionFromChoreographyByActionId(
                    choreography, actionId );
            if ( action == null ) {
                throw new NexusException( "No action found for actionId:" + actionId + " in choreography:"
                        + choreography.getName() );
            }

        } else if ( message.isAck() ) {
            action = new ActionPojo( choreography, new Date(), new Date(), 0, false, false, null, null, actionId );
        } else { // error message
            action = new ActionPojo( choreography, new Date(), new Date(), 0, false, false, null, null, actionId );
        }

        ConversationPojo conversation = null;
        conversation = complete( transactionDao.getConversationByConversationId( conversationId ), false );

        if ( conversation == null ) {
            conversation = new ConversationPojo();
            conversation.setPartner( partner );
            conversation.setChoreography( choreography );
            if ( action != null && !action.isStart() && ( message.isNormal() ) ) {
                throw new NexusException( "action:" + action.getName() + " is not a valid starting action!" );
            }
            //conversation.setCurrentAction( action );
            conversation.setConversationId( conversationId );
            conversation.setStatus( org.nexuse2e.Constants.CONVERSATION_STATUS_CREATED );
        }

        message.setConversation( complete( conversation ) );
        message.setAction( action );

        return message;
    } // initializeMessage
    
    public MessageContext createMessageContext( MessagePojo messagePojo ) {

        MessageContext messageContext = null;

        if ( messagePojo != null ) {
            messageContext = new MessageContext();
            messageContext.setActionSpecificKey( new ActionSpecificKey( messagePojo.getAction().getName(), messagePojo
                    .getConversation().getChoreography().getName() ) );
            messageContext.setChoreography( messagePojo.getConversation().getChoreography() );
            messageContext.setCommunicationPartner( messagePojo.getConversation().getPartner() );
            messageContext.setParticipant( messagePojo.getParticipant() );
            messageContext.setConversation( messagePojo.getConversation() );
            messageContext.setProtocolSpecificKey( new ProtocolSpecificKey( messagePojo.getTRP().getProtocol(),
                    messagePojo.getTRP().getVersion(), messagePojo.getTRP().getTransport() ) );
            messageContext.setMessagePojo( messagePojo );
            messageContext.setOriginalMessagePojo( messagePojo );
            String senderId = messagePojo.getParticipant().getLocalPartner().getPartnerId();
            String senderIdType = messagePojo.getParticipant().getLocalPartner().getPartnerIdType();

            //TODO refactor and standard constants
            messagePojo.getCustomParameters().put(
                    org.nexuse2e.messaging.ebxml.v20.Constants.PARAMETER_PREFIX_EBXML20 + "from", senderId );
            messagePojo.getCustomParameters().put(
                    org.nexuse2e.messaging.ebxml.v20.Constants.PARAMETER_PREFIX_EBXML20 + "fromIdType", senderIdType );
        }

        return messageContext;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.controller.TransactionService#getMessageContext(java.lang.String)
     */
    public MessageContext getMessageContext( String messageId ) throws NexusException {

        return getMessageContext( messageId, false );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.controller.TransactionService#getMessageContext(java.lang.String)
     */
    public MessageContext getMessageContext( String messageId, boolean isReferencedMessageId ) throws NexusException {

        return createMessageContext( getMessage( messageId, isReferencedMessageId ) );
    } // getMessageContext

    /* (non-Javadoc)
     * @see org.nexuse2e.controller.TransactionService#storeTransaction(org.nexuse2e.pojo.ConversationPojo, org.nexuse2e.pojo.MessagePojo)
     */
    public void storeTransaction( ConversationPojo conversationPojo, MessagePojo messagePojo ) throws NexusException {

        if ( ( conversationPojo != null ) && ( messagePojo != null ) ) {
            LOG.debug(new LogMessage(  "storeTransaction: " + conversationPojo.getConversationId() + " - "
                            + messagePojo.getMessageId(),messagePojo) );
        } else if ( conversationPojo != null ) {
            LOG.debug(new LogMessage(  "storeTransaction: " + conversationPojo.getConversationId(),messagePojo) );
        }
        getTransactionDao().storeTransaction( conversationPojo, messagePojo );
    } // storeTransaction


    
        
    

    public void updateTransaction( MessagePojo message ) throws NexusException, StateTransitionException {
        updateTransaction( message, false );
    }
    
    public void updateTransaction( MessagePojo message, boolean force ) throws NexusException, StateTransitionException {
        getTransactionDao().updateTransaction( message, force );
    } // updateTransaction

    public void updateTransaction( MessagePojo message, UpdateTransactionOperation operation ) throws NexusException, StateTransitionException {
        updateTransaction(message, operation, false);
    } // updateTransaction

    public void updateTransaction( MessagePojo message, UpdateTransactionOperation operation, boolean force ) throws NexusException, StateTransitionException {
        getTransactionDao().updateTransaction( message, operation, force );
    } // updateTransaction


    public void updateRetryCount( MessagePojo message ) throws NexusException {
        getTransactionDao().updateRetryCount( message );
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.controller.TransactionService#isProcessingMessage(java.lang.String)
     */
    public boolean isProcessingMessage( String id ) {

        boolean result = false;

        synchronized ( processingMessages ) {
            result = processingMessages.containsKey( id );
        }

        return result;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.controller.TransactionService#registerProcessingMessage(java.lang.String, java.util.concurrent.ScheduledFuture)
     */
    public void registerProcessingMessage( MessagePojo message, ScheduledFuture<?> handle ) {

        LOG.debug( new LogMessage ("registerProcessingMessage: " + message.getMessageId(),"unknown",message.getMessageId()) );

        synchronized ( processingMessages ) {
            if ( !processingMessages.containsKey( message.getMessageId() ) ) {
                processingMessages.put( message.getMessageId(), handle );
            } else {
                handle.cancel( false );
                LOG.warn( new LogMessage( "Request to process message that was already being processed: " + message.getMessageId(),message) );
                new Exception().printStackTrace();
            }
        }
    } // registerProcessingMessage

    /* (non-Javadoc)
     * @see org.nexuse2e.controller.TransactionService#deregisterProcessingMessage(java.lang.String)
     */
    public void deregisterProcessingMessage( String id ) {

        String conversation = "unknown";
        MessagePojo message = null;
        try {
            message = getMessage( id );
        } catch ( NexusException e1 ) {
            e1.printStackTrace();
        }
        if(message != null && message.getConversation() != null) {
            conversation = message.getConversation().getConversationId();
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug( new LogMessage("deregisterProcessingMessage: " + id,conversation,id) );
        }

        synchronized ( processingMessages ) {
            ScheduledFuture<?> handle = processingMessages.get( id );
            if ( handle != null ) {
                handle.cancel( false );
                if (LOG.isDebugEnabled()) {
                    LOG.debug( new LogMessage(
                            "deregisterProcessingMessage - processing cancelled with message status " +
                            MessagePojo.getStatusName(message.getStatus()) + "!", conversation, id) );
                }
                processingMessages.remove( id );
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug( new LogMessage("No handle found when trying to deregister processing message: " + id, conversation, id) );
                }
            }
        }
    } // deregisterProcessingMessage

    /* (non-Javadoc)
     * @see org.nexuse2e.controller.TransactionService#stopProcessingMessage(java.lang.String)
     */
    public void stopProcessingMessage( String id ) throws NexusException {

        MessagePojo messagePojo = getMessage( id );
        messagePojo.setStatus( org.nexuse2e.MessageStatus.STOPPED.getOrdinal() );
        messagePojo.setModifiedDate( new Date() );
        messagePojo.getConversation().setStatus( org.nexuse2e.Constants.CONVERSATION_STATUS_IDLE );
        try {
            updateTransaction( messagePojo, true );
        } catch (StateTransitionException stex) {
            LOG.error( new LogMessage( "Program error: Unexpected " + stex + " was thrown", messagePojo) );
            stex.printStackTrace();
        }
        deregisterProcessingMessage( id );
    } // stopProcessingMessage

    /* (non-Javadoc)
     * @see org.nexuse2e.controller.TransactionService#addSynchronousRequest(java.lang.String)
     */
    public void addSynchronousRequest( String messageId ) {

        synchronousReplies.put( messageId, messageId );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.controller.TransactionService#isSynchronousReply(java.lang.String)
     */
    public boolean isSynchronousReply( String messageId ) {

        return synchronousReplies.get( messageId ) != null;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.controller.TransactionService#removeSynchronousRequest(java.lang.String)
     */
    public void removeSynchronousRequest( String messageId ) {

        synchronousReplies.remove( messageId );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.controller.TransactionService#deleteMessage(org.nexuse2e.pojo.MessagePojo, org.hibernate.Session, org.hibernate.Transaction)
     */
    public void deleteMessage( MessagePojo message ) throws NexusException {

        getTransactionDao().deleteMessage( message );

    }

    /**
     * @param conversation
     * @param session
     * @param transaction
     * @throws NexusException
     */
    public void deleteConversation( ConversationPojo conversation )
            throws NexusException {

        if (conversation != null) {
            getTransactionDao().deleteConversation( conversation );
        }

    }

    /* (non-Javadoc)
     * @see org.nexuse2e.controller.TransactionService#getMessagesByPartnerAndDirection(org.nexuse2e.pojo.PartnerPojo, boolean, int, boolean, org.hibernate.Session, org.hibernate.Transaction)
     */
    public List<MessagePojo> getMessagesByPartnerAndDirection( PartnerPojo partner, boolean outbound, int sort,
            boolean ascending ) throws NexusException {

        return completeMessages( getTransactionDao().getMessagesByPartnerAndDirection(
                partner, outbound, sort, ascending ) );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.controller.TransactionService#getConversationsByPartner(org.nexuse2e.pojo.PartnerPojo, org.hibernate.Session, org.hibernate.Transaction)
     */
    public List<ConversationPojo> getConversationsByPartner( PartnerPojo partner ) throws NexusException {

        return completeConversations( getTransactionDao().getConversationsByPartner( partner ), true );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.controller.TransactionService#getConversationsByChoreography(org.nexuse2e.pojo.ChoreographyPojo, org.hibernate.Session, org.hibernate.Transaction)
     */
    public List<ConversationPojo> getConversationsByChoreography( ChoreographyPojo choreography ) throws NexusException {
       
        return completeConversations( getTransactionDao().getConversationsByChoreography(
                choreography ), true );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.controller.TransactionService#getConversationsByPartnerAndChoreography(org.nexuse2e.pojo.PartnerPojo, org.nexuse2e.pojo.ChoreographyPojo, org.hibernate.Session, org.hibernate.Transaction)
     */
    public List<ConversationPojo> getConversationsByPartnerAndChoreography( PartnerPojo partner,
            ChoreographyPojo choreography ) throws NexusException {

        return completeConversations( getTransactionDao().getConversationsByPartnerAndChoreography(
                partner, choreography ), true );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.controller.TransactionService#getMessagessByPartner(org.nexuse2e.pojo.PartnerPojo, org.hibernate.Session, org.hibernate.Transaction)
     */
    public List<MessagePojo> getMessagesByPartner( PartnerPojo partner, int field, boolean ascending ) throws NexusException {

        return completeMessages( getTransactionDao().getMessagesByPartner(
                partner, field, ascending ) );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.controller.TransactionService#getMessagesByChoreographyAndPartner(org.nexuse2e.pojo.ChoreographyPojo, org.nexuse2e.pojo.PartnerPojo, int, boolean, org.hibernate.Session, org.hibernate.Transaction)
     */
    public List<MessagePojo> getMessagesByChoreographyAndPartner( ChoreographyPojo choreography, PartnerPojo partner,
            int field, boolean ascending ) throws NexusException {

        return completeMessages( getTransactionDao().getMessagesByChoreographyAndPartner(
                choreography, partner, field, ascending ) );
    }

    public List<MessagePojo> getMessagesByActionPartnerDirectionAndStatus(
            ActionPojo action,
            PartnerPojo partner,
            boolean outbound,
            int status,
            int field,
            boolean ascending ) throws NexusException{
        
        return completeMessages( getTransactionDao().getMessagesByActionPartnerDirectionAndStatus(
                action, partner, outbound, status, field, ascending ) );
    }


    /* (non-Javadoc)
     * @see org.nexuse2e.controller.TransactionService#getMessagesByChoreographyPartnerAndConversation(org.nexuse2e.pojo.ChoreographyPojo, org.nexuse2e.pojo.PartnerPojo, org.nexuse2e.pojo.ConversationPojo, int, boolean, org.hibernate.Session, org.hibernate.Transaction)
     */
    public List<MessagePojo> getMessagesByChoreographyPartnerAndConversation( ChoreographyPojo choreography,
            PartnerPojo partner, ConversationPojo conversation, int field, boolean ascending ) throws NexusException {

        return completeMessages( getTransactionDao().getMessagesByChoreographyPartnerAndConversation(
                choreography, partner, conversation, field, ascending ) );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.controller.TransactionService#getLogEntriesForReportCount(java.lang.String, java.lang.String, java.util.Date, java.util.Date, int, boolean)
     */
    public int getLogEntriesForReportCount( String severity, String messageText, Date start, Date end, int field,
            boolean ascending ) throws NexusException {

        return getLogDao().getLogEntriesForReportCount(
                severity, messageText, start, end, field, ascending );
    }

    /* (non-Javadoc)
	 * @see org.nexuse2e.controller.TransactionService#getLogEntriesForReport(java.lang.String, java.lang.String, java.lang.String, boolean)
	 */
	public List<LogPojo> getLogEntriesForReport(String severity, String conversationId, String messageId, boolean ascending)
			throws NexusException {
		
		return getLogDao().getLogEntriesForReport(severity, conversationId, messageId, ascending );
	}


    public List<LogPojo> getLogEntriesForReport(String severity, String conversationId, String messageId, int itemsPerPage, int page, boolean ascending)
            throws NexusException {
        return getLogDao().getLogEntriesForReport(severity, conversationId, messageId, itemsPerPage, page, ascending);
    }

    public int getLogEntriesForReportCount(String severity, String conversationId, String messageId) throws NexusException {
        return getLogDao().getLogEntriesForReportCount(severity, conversationId, messageId);
    }

	
	
    /* (non-Javadoc)
     * @see org.nexuse2e.controller.TransactionService#getLogEntriesForReport(java.lang.String, java.lang.String, java.util.Date, java.util.Date, int, int, int, boolean)
     */
    public List<LogPojo> getLogEntriesForReport( String severity, String messageText, Date start, Date end,
            int itemsPerPage, int page, int field, boolean ascending )
            throws NexusException {

        return getLogDao().getLogEntriesForReport(
                severity, messageText, start, end, itemsPerPage, page, field, ascending );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#activate()
     */
    public void activate() {

        LOG.debug( "Activating..." );
        status = BeanStatus.ACTIVATED;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#deactivate()
     */
    public void deactivate() {

        LOG.debug( "Deactivating..." );
        status = BeanStatus.INITIALIZED;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#getActivationRunlevel()
     */
    public Layer getActivationLayer() {

        return Layer.CORE;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#getStatus()
     */
    public BeanStatus getStatus() {

        return status;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#initialize(org.nexuse2e.configuration.EngineConfiguration)
     */
    public void initialize( EngineConfiguration config ) {

        LOG.trace( "Initializing..." );
        status = BeanStatus.INITIALIZED;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#teardown()
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void teardown() {

        LOG.trace( "Tearing down..." );

        Set<String> keys = ( (HashMap<String, ScheduledFuture>) processingMessages.clone() ).keySet();
        for ( String key : keys ) {
            deregisterProcessingMessage( key );
        }

        status = BeanStatus.INSTANTIATED;
    }

    
    public void deleteLogEntry( LogPojo logEntry ) throws NexusException {

        getTransactionDao().deleteLogEntry( logEntry );

    }

    public Object getSyncObjectForConversation( ConversationPojo conversation ) {

        synchronized (syncObjects) {
            WeakReference<Object> ref = syncObjects.get( conversation.getConversationId() );
            Object obj = null;
            if (ref != null) {
                obj = ref.get();
            }
            
            // clear out removed weak references
            List<String> keys = new ArrayList<String>();
            for (String key : syncObjects.keySet()) {
                WeakReference<Object> weakRef = syncObjects.get( key );
                if (weakRef.get() == null) {
                    keys.add( key );
                }
            }
            
            for (String key : keys) {
                syncObjects.remove( key );
            }
            
            if (obj != null) {
                return obj;
            }
            obj = new Object();
            syncObjects.put( conversation.getConversationId(), new WeakReference<Object>( obj ) );
            return obj;
        }
    }

	/**
     * @return the dao
     */
    public TransactionDAO getTransactionDao() {
    
        return transactionDao;
    }

    
    /**
     * @param dao the dao to set
     */
    public void setTransactionDao( TransactionDAO dao ) {
    
        this.transactionDao = dao;
    }

    
    /**
     * @return the logDao
     */
    public LogDAO getLogDao() {
    
        return logDao;
    }

    
    /**
     * @param logDao the logDao to set
     */
    public void setLogDao( LogDAO logDao ) {
    
        this.logDao = logDao;
    }

    public int getCreatedMessagesSinceCount( Timestamp timestamp ) throws NexusException {

        return getTransactionDao().getCreatedMessagesSinceCount( timestamp );
        
    }

    
    /**
     * @param status the status to set
     */
    public void setStatus( BeanStatus status ) {
    
        this.status = status;
    }

    public long getConversationsCount( Date start, Date end ) throws NexusException {

        return getTransactionDao().getConversationsCount( start, end );
    }

    public long getLogCount( Date start, Date end ) throws NexusException {

        return getTransactionDao().getLogCount( start, end );
    }

    public Map<Level, Long> getLogCount( Date start, Date end, Level minLevel, Level maxLevel ) throws NexusException {

        return getTransactionDao().getLogCount( start, end, minLevel, maxLevel );
    }

    public long getMessagesCount( Date start, Date end ) throws NexusException {

        return getTransactionDao().getMessagesCount( start, end );
    }

    public long removeConversations( Date start, Date end ) throws NexusException {

        return getTransactionDao().removeConversations( start, end );
    }
    
    public List<int[]> getConversationStatesSince( Date since ) {
        
        return getTransactionDao().getConversationStatesSince( since );
    }

    public List<int[]> getMessageStatesSince( Date since ) {
        
        return getTransactionDao().getMessageStatesSince( since );
    }

    public List<String[]> getMessagesPerConversationSince( Date since ) {
        
        return getTransactionDao().getMessagesPerConversationSince( since );
    }
    
    public List<int[]> getMessagesPerHourLast24Hours() {
        
        return getTransactionDao().getMessagesPerHourLast24Hours();
    }

    public long removeLogEntries( Date start, Date end ) throws NexusException {

        return getTransactionDao().removeLogEntries( start, end );
    }


} // TransactionService
