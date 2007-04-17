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
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import org.apache.log4j.Logger;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.Constants.BeanStatus;
import org.nexuse2e.Constants.Runlevel;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.configuration.IdGenerator;
import org.nexuse2e.dao.LogDAO;
import org.nexuse2e.dao.TransactionDAO;
import org.nexuse2e.messaging.Constants;
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

    private HashMap<String, ScheduledFuture>          processingMessages = new HashMap<String, ScheduledFuture>();
    private HashMap<String, ScheduledExecutorService> schedulers         = new HashMap<String, ScheduledExecutorService>();
    private Hashtable<String, String>                 synchronousReplies = new Hashtable<String, String>();

    private Constants.BeanStatus                      status             = Constants.BeanStatus.UNDEFINED;

    public ConversationPojo createConversation( String choreographyId, String partnerId, String conversationId )
            throws NexusException {

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
        }

        ConversationPojo conversationPojo = new ConversationPojo();
        conversationPojo.setChoreography( choreography );
        conversationPojo.setConversationId( conversationId );
        conversationPojo.setPartner( partner );

        return conversationPojo;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.controller.TransactionService#getConversation(java.lang.String)
     */
    public ConversationPojo getConversation( String conversationId ) throws NexusException {

        LOG.trace( "Entering TransactionDataService.getConversation..." );
        TransactionDAO transactionDao;
        try {
            transactionDao = (TransactionDAO) Engine.getInstance().getDao( "transactionDao" );
        } catch ( Exception e ) {
            throw new NexusException( e );
        }

        return transactionDao.getConversationByConversationId( conversationId, null, null );

    }

    /* (non-Javadoc)
     * @see org.nexuse2e.controller.TransactionService#getConversation(java.lang.String, java.lang.String, java.lang.String)
     */
    public ConversationPojo getConversation( String choreographyId, String conversationId, String partnerId )
            throws NexusException {

        LOG.trace( "Entering TransactionDataService.getConversation..." );
        TransactionDAO transactionDao;
        try {
            transactionDao = (TransactionDAO) Engine.getInstance().getDao( "transactionDao" );
        } catch ( Exception e ) {
            throw new NexusException( e );
        }

        PartnerPojo partner = Engine.getInstance().getActiveConfigurationAccessService().getPartnerByPartnerId(
                partnerId );

        return transactionDao.getConversationByConversationId( choreographyId, conversationId,
                partner.getNxPartnerId(), null, null );

    }

    /* (non-Javadoc)
     * @see org.nexuse2e.controller.TransactionService#getConversationsForReport(java.lang.String, int, int, java.lang.String, java.util.Date, java.util.Date, int, int, int, boolean)
     */
    public List getConversationsForReport( String status, int nxChoreographyId, int nxPartnerId, String conversationId,
            Date start, Date end, int itemsPerPage, int page, int field, boolean ascending ) throws NexusException {

        TransactionDAO transactionDao;
        try {
            transactionDao = (TransactionDAO) Engine.getInstance().getDao( "transactionDao" );
        } catch ( Exception e ) {
            throw new NexusException( e );
        }
        return transactionDao.getConversationsForReport( status, nxChoreographyId, nxPartnerId, conversationId, start,
                end, itemsPerPage, page, field, ascending );

    }

    /* (non-Javadoc)
     * @see org.nexuse2e.controller.TransactionService#getConversationsCount(java.lang.String, int, int, java.lang.String, java.util.Date, java.util.Date, int, boolean)
     */
    public int getConversationsCount( String status, int nxChoreographyId, int nxPartnerId, String conversationId,
            Date start, Date end, int field, boolean ascending ) throws NexusException {

        TransactionDAO transactionDao;
        try {
            transactionDao = (TransactionDAO) Engine.getInstance().getDao( "transactionDao" );
        } catch ( Exception e ) {
            throw new NexusException( e );
        }
        return transactionDao.getConversationsCount( status, nxChoreographyId, nxPartnerId, conversationId, start, end,
                field, ascending );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.controller.TransactionService#getMessage(java.lang.String)
     */
    public MessagePojo getMessage( String messageId ) throws NexusException {

        TransactionDAO transactionDao;
        try {
            transactionDao = (TransactionDAO) Engine.getInstance().getDao( "transactionDao" );
        } catch ( Exception e ) {
            throw new NexusException( e );
        }
        return transactionDao.getMessageByMessageId( messageId, null, null );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.controller.TransactionService#getMessagesForReport(java.lang.String, int, int, java.lang.String, java.lang.String, java.lang.String, java.util.Date, java.util.Date, int, int, int, boolean)
     */
    public List getMessagesForReport( String status, int nxChoreographyId, int nxPartnerId, String conversationId,
            String messageId, String type, Date start, Date end, int itemsPerPage, int page, int field,
            boolean ascending ) throws NexusException {

        TransactionDAO transactionDao;
        try {
            transactionDao = (TransactionDAO) Engine.getInstance().getDao( "transactionDao" );
        } catch ( Exception e ) {
            throw new NexusException( e );
        }
        return transactionDao.getMessagesForReport( status, nxChoreographyId, nxPartnerId, conversationId, messageId,
                type, start, end, itemsPerPage, page, field, ascending );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.controller.TransactionService#getMessagesCount(java.lang.String, int, int, java.lang.String, java.lang.String, java.util.Date, java.util.Date)
     */
    public int getMessagesCount( String status, int nxChoreographyId, int nxPartnerId, String conversationId,
            String messageId, Date startDate, Date endDate ) throws NexusException {

        TransactionDAO transactionDao;
        try {
            transactionDao = (TransactionDAO) Engine.getInstance().getDao( "transactionDao" );
        } catch ( Exception e ) {
            throw new NexusException( e );
        }

        return transactionDao.getMessagesCount( status, nxChoreographyId, nxPartnerId, conversationId, messageId,
                startDate, endDate );

    }

    /* (non-Javadoc)
     * @see org.nexuse2e.controller.TransactionService#getMessagesFromConversation(org.nexuse2e.pojo.ConversationPojo)
     */
    public List<MessagePojo> getMessagesFromConversation( ConversationPojo conversation ) throws NexusException {

        TransactionDAO transactionDao;
        try {
            transactionDao = (TransactionDAO) Engine.getInstance().getDao( "transactionDao" );
        } catch ( Exception e ) {
            throw new NexusException( e );
        }

        Session session = transactionDao.getDBSession();
        session.lock( conversation, LockMode.NONE );
        List<MessagePojo> messages = conversation.getMessages();
        // Force db access 
        messages.size();
        transactionDao.releaseDBSession( session );

        return messages;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.controller.TransactionService#getMessagePayloadsFromMessage(org.nexuse2e.pojo.MessagePojo)
     */
    public List<MessagePayloadPojo> getMessagePayloadsFromMessage( MessagePojo message ) throws NexusException {

        TransactionDAO transactionDao;
        try {
            transactionDao = (TransactionDAO) Engine.getInstance().getDao( "transactionDao" );
        } catch ( Exception e ) {
            throw new NexusException( e );
        }

        Session session = transactionDao.getDBSession();
        session.lock( message, LockMode.NONE );
        List<MessagePayloadPojo> payloads = message.getMessagePayloads();
        // Force db access 
        payloads.size();
        transactionDao.releaseDBSession( session );

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

    public List<MessagePojo> getActiveMessages() throws NexusException {

        TransactionDAO transactionDao;
        try {
            transactionDao = (TransactionDAO) Engine.getInstance().getDao( "transactionDao" );

            return transactionDao.getActiveMessages();
        } catch ( Exception e ) {
            throw new NexusException( e );
        }
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
        message.setCreatedDate( date );
        message.setModifiedDate( date );
        TransactionDAO transactionDao;
        try {
            transactionDao = (TransactionDAO) Engine.getInstance().getDao( "transactionDao" );
        } catch ( Exception e ) {
            e.printStackTrace();
            throw new NexusException( e );
        } catch ( Error e ) {
            e.printStackTrace();
            throw e;
        }
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
        message.getCustomParameters().put(org.nexuse2e.messaging.ebxml.v20.Constants.PARAMETER_PREFIX_EBXML20 + "from", senderId );
        message.getCustomParameters().put(org.nexuse2e.messaging.ebxml.v20.Constants.PARAMETER_PREFIX_EBXML20 + "fromIdType", senderIdType );

        message.setTRP( participant.getConnection().getTrp() );
        ActionPojo action = null;
        if ( message.getType() != Constants.INT_MESSAGE_TYPE_ACK ) {
            action = Engine.getInstance().getActiveConfigurationAccessService().getActionFromChoreographyByActionId(
                    choreography, actionId );
            if ( action == null ) {
                throw new NexusException( "No action found for actionId:" + actionId + " in choreography:"
                        + choreography.getName() );
            }

        } else {
            action = new ActionPojo( choreography, new Date(), new Date(), 0, false, false, null, null, actionId );
        }

        ConversationPojo conversation = transactionDao.getConversationByConversationId( choreographyId, conversationId,
                partner.getNxPartnerId(), null, null );

        if ( conversation == null ) {
            conversation = new ConversationPojo();
            conversation.setPartner( partner );
            conversation.setChoreography( choreography );
            if ( action != null && !action.isStart() && ( message.getType() != Constants.INT_MESSAGE_TYPE_ACK ) ) {
                throw new NexusException( "action:" + action.getName() + " is not a valid starting action!" );
            }
            //conversation.setCurrentAction( action );
            conversation.setConversationId( conversationId );
            conversation.setStatus( org.nexuse2e.Constants.CONVERSATION_STATUS_CREATED );
        }
        message.setConversation( conversation );
        message.setAction( action );

        return message;
    } // initializeMessage

    /* (non-Javadoc)
     * @see org.nexuse2e.controller.TransactionService#storeTransaction(org.nexuse2e.pojo.ConversationPojo, org.nexuse2e.pojo.MessagePojo)
     */
    public void storeTransaction( ConversationPojo conversationPojo, MessagePojo messagePojo ) throws NexusException {

        LOG.debug( "storeTransaction: " + conversationPojo + " - " + messagePojo );

        TransactionDAO transactionDao;
        try {
            transactionDao = (TransactionDAO) Engine.getInstance().getDao( "transactionDao" );
        } catch ( Exception e ) {
            e.printStackTrace();
            throw new NexusException( e );
        } catch ( Error e ) {
            e.printStackTrace();
            throw e;
        }

        transactionDao.storeTransaction( conversationPojo, messagePojo );
    } // storeTransaction

    /* (non-Javadoc)
     * @see org.nexuse2e.controller.TransactionService#updateTransaction(org.nexuse2e.pojo.ConversationPojo)
     */
    public void updateTransaction( ConversationPojo conversationPojo ) throws NexusException {

        LOG.debug( "updateTransaction: " + conversationPojo );

        TransactionDAO transactionDao;
        try {
            transactionDao = (TransactionDAO) Engine.getInstance().getDao( "transactionDao" );
        } catch ( Exception e ) {
            e.printStackTrace();
            throw new NexusException( e );
        } catch ( Error e ) {
            e.printStackTrace();
            throw e;
        }

        transactionDao.updateTransaction( conversationPojo );
    } // updateTransaction

    /* (non-Javadoc)
     * @see org.nexuse2e.controller.TransactionService#updateMessage(org.nexuse2e.pojo.MessagePojo)
     */
    public void updateMessage( MessagePojo messagePojo ) throws NexusException {

        LOG.debug( "updateMessage: " + messagePojo );

        TransactionDAO transactionDao;
        try {
            transactionDao = (TransactionDAO) Engine.getInstance().getDao( "transactionDao" );
        } catch ( Exception e ) {
            e.printStackTrace();
            throw new NexusException( e );
        } catch ( Error e ) {
            e.printStackTrace();
            throw e;
        }

        transactionDao.updateMessage( messagePojo );
    } // updateMessage

    /* (non-Javadoc)
     * @see org.nexuse2e.controller.TransactionService#updateConversation(org.nexuse2e.pojo.ConversationPojo)
     */
    public void updateConversation( ConversationPojo conversationPojo ) throws NexusException {

        LOG.debug( "updateMessage: " + conversationPojo );

        TransactionDAO transactionDao;
        try {
            transactionDao = (TransactionDAO) Engine.getInstance().getDao( "transactionDao" );
        } catch ( Exception e ) {
            e.printStackTrace();
            throw new NexusException( e );
        } catch ( Error e ) {
            e.printStackTrace();
            throw e;
        }

        transactionDao.updateConversation( conversationPojo );
    } // updateMessage

    /* (non-Javadoc)
     * @see org.nexuse2e.controller.TransactionService#registerProcessingMessage(java.lang.String, java.util.concurrent.ScheduledFuture)
     */
    public void registerProcessingMessage( String id, ScheduledFuture handle, ScheduledExecutorService scheduler ) {

        LOG.debug( "registerProcessingMessage: " + id );

        processingMessages.put( id, handle );
        schedulers.put( id, scheduler );
    } // registerProcessingMessage

    /* (non-Javadoc)
     * @see org.nexuse2e.controller.TransactionService#deregisterProcessingMessage(java.lang.String)
     */
    public void deregisterProcessingMessage( String id ) {

        LOG.debug( "deregisterProcessingMessage: " + id );

        ScheduledFuture handle = processingMessages.get( id );
        if ( handle != null ) {
            handle.cancel( false );
            LOG.debug( "deregisterProcessingMessage - processing cancelled!" );
            try {
                ScheduledExecutorService scheduler = schedulers.remove( id );
                if ( scheduler != null ) {
                    LOG.debug( "Shutting down scheduler..." );
                    scheduler.shutdownNow();
                }
            } catch ( Exception e ) {
                e.printStackTrace();
            }
            processingMessages.remove( id );
        }
    } // deregisterProcessingMessage

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
    public void deleteMessage( MessagePojo message, Session session, Transaction transaction ) throws NexusException {

        TransactionDAO transactionDao;
        try {
            transactionDao = (TransactionDAO) Engine.getInstance().getDao( "transactionDao" );
        } catch ( Exception e ) {
            e.printStackTrace();
            throw new NexusException( e );
        } catch ( Error e ) {
            e.printStackTrace();
            throw e;
        }
        transactionDao.deleteMessage( message, session, transaction );

    }
    
    /**
     * @param conversation
     * @param session
     * @param transaction
     * @throws NexusException
     */
    public void deleteConversation( ConversationPojo conversation, Session session, Transaction transaction ) throws NexusException {

        TransactionDAO transactionDao;
        try {
            transactionDao = (TransactionDAO) Engine.getInstance().getDao( "transactionDao" );
        } catch ( Exception e ) {
            e.printStackTrace();
            throw new NexusException( e );
        } catch ( Error e ) {
            e.printStackTrace();
            throw e;
        }
        transactionDao.deleteConversation( conversation, session, transaction );

    }

    /* (non-Javadoc)
     * @see org.nexuse2e.controller.TransactionService#getMessagesByPartnerAndDirection(org.nexuse2e.pojo.PartnerPojo, boolean, int, boolean, org.hibernate.Session, org.hibernate.Transaction)
     */
    public List<MessagePojo> getMessagesByPartnerAndDirection( PartnerPojo partner, boolean outbound, int sort,
            boolean ascending, Session session, Transaction transaction ) throws NexusException {

        TransactionDAO transactionDao;
        try {
            transactionDao = (TransactionDAO) Engine.getInstance().getDao( "transactionDao" );
        } catch ( Exception e ) {
            e.printStackTrace();
            throw new NexusException( e );
        } catch ( Error e ) {
            e.printStackTrace();
            throw e;
        }
        return transactionDao.getMessagesByPartnerAndDirection( partner, outbound, sort, ascending, session,
                transaction );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.controller.TransactionService#getConversationsByPartner(org.nexuse2e.pojo.PartnerPojo, org.hibernate.Session, org.hibernate.Transaction)
     */
    public List<ConversationPojo> getConversationsByPartner( PartnerPojo partner, Session session,
            Transaction transaction ) throws NexusException {

        TransactionDAO transactionDao;
        try {
            transactionDao = (TransactionDAO) Engine.getInstance().getDao( "transactionDao" );
        } catch ( Exception e ) {
            e.printStackTrace();
            throw new NexusException( e );
        } catch ( Error e ) {
            e.printStackTrace();
            throw e;
        }
        return transactionDao.getConversationsByPartner( partner, session, transaction );
    }

    
    /* (non-Javadoc)
     * @see org.nexuse2e.controller.TransactionService#getConversationsByChoreography(org.nexuse2e.pojo.ChoreographyPojo, org.hibernate.Session, org.hibernate.Transaction)
     */
    public List<ConversationPojo> getConversationsByChoreography( ChoreographyPojo choreography, 
            Session session, Transaction transaction ) throws NexusException {

        TransactionDAO transactionDao;
        try {
            transactionDao = (TransactionDAO) Engine.getInstance().getDao( "transactionDao" );
        } catch ( Exception e ) {
            e.printStackTrace();
            throw new NexusException( e );
        } catch ( Error e ) {
            e.printStackTrace();
            throw e;
        }
        return transactionDao.getConversationsByChoreography( choreography, session, transaction );
    }
    
    
    /* (non-Javadoc)
     * @see org.nexuse2e.controller.TransactionService#getConversationsByPartnerAndChoreography(org.nexuse2e.pojo.PartnerPojo, org.nexuse2e.pojo.ChoreographyPojo, org.hibernate.Session, org.hibernate.Transaction)
     */
    public List<ConversationPojo> getConversationsByPartnerAndChoreography( PartnerPojo partner,
            ChoreographyPojo choreography, Session session, Transaction transaction ) throws NexusException {

        TransactionDAO transactionDao;
        try {
            transactionDao = (TransactionDAO) Engine.getInstance().getDao( "transactionDao" );
        } catch ( Exception e ) {
            e.printStackTrace();
            throw new NexusException( e );
        } catch ( Error e ) {
            e.printStackTrace();
            throw e;
        }
        return transactionDao.getConversationsByPartnerAndChoreography( partner, choreography, session, transaction );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.controller.TransactionService#getMessagessByPartner(org.nexuse2e.pojo.PartnerPojo, org.hibernate.Session, org.hibernate.Transaction)
     */
    public List<MessagePojo> getMessagesByPartner( PartnerPojo partner, int field, boolean ascending, Session session,
            Transaction transaction ) throws NexusException {

        TransactionDAO transactionDao;
        try {
            transactionDao = (TransactionDAO) Engine.getInstance().getDao( "transactionDao" );
        } catch ( Exception e ) {
            e.printStackTrace();
            throw new NexusException( e );
        } catch ( Error e ) {
            e.printStackTrace();
            throw e;
        }
        return transactionDao.getMessagesByPartner( partner, field, ascending, session, transaction );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.controller.TransactionService#getMessagesByChoreographyAndPartner(org.nexuse2e.pojo.ChoreographyPojo, org.nexuse2e.pojo.PartnerPojo, int, boolean, org.hibernate.Session, org.hibernate.Transaction)
     */
    public List<MessagePojo> getMessagesByChoreographyAndPartner( ChoreographyPojo choreography, PartnerPojo partner,
            int field, boolean ascending, Session session, Transaction transaction ) throws NexusException {

        TransactionDAO transactionDao;
        try {
            transactionDao = (TransactionDAO) Engine.getInstance().getDao( "transactionDao" );
        } catch ( Exception e ) {
            e.printStackTrace();
            throw new NexusException( e );
        } catch ( Error e ) {
            e.printStackTrace();
            throw e;
        }
        return transactionDao.getMessagesByChoreographyAndPartner( choreography, partner, field, ascending, session,
                transaction );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.controller.TransactionService#getMessagesByChoreographyPartnerAndConversation(org.nexuse2e.pojo.ChoreographyPojo, org.nexuse2e.pojo.PartnerPojo, org.nexuse2e.pojo.ConversationPojo, int, boolean, org.hibernate.Session, org.hibernate.Transaction)
     */
    public List<MessagePojo> getMessagesByChoreographyPartnerAndConversation( ChoreographyPojo choreography,
            PartnerPojo partner, ConversationPojo conversation, int field, boolean ascending, Session session,
            Transaction transaction ) throws NexusException {

        TransactionDAO transactionDao;
        try {
            transactionDao = (TransactionDAO) Engine.getInstance().getDao( "transactionDao" );
        } catch ( Exception e ) {
            e.printStackTrace();
            throw new NexusException( e );
        } catch ( Error e ) {
            e.printStackTrace();
            throw e;
        }
        return transactionDao.getMessagesByChoreographyPartnerAndConversation( choreography, partner, conversation,
                field, ascending, session, transaction );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.controller.TransactionService#getLogEntriesForReportCount(java.lang.String, java.lang.String, java.util.Date, java.util.Date, int, boolean)
     */
    public int getLogEntriesForReportCount( String severity, String messageText, Date start, Date end, int field,
            boolean ascending ) throws NexusException {

        LogDAO logDao;
        try {
            logDao = (LogDAO) Engine.getInstance().getDao( "logDao" );
        } catch ( Exception e ) {
            e.printStackTrace();
            throw new NexusException( e );
        } catch ( Error e ) {
            e.printStackTrace();
            throw e;
        }
        return logDao.getLogEntriesForReportCount( severity, messageText, start, end, field, ascending );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.controller.TransactionService#getLogEntriesForReport(java.lang.String, java.lang.String, java.util.Date, java.util.Date, int, int, int, boolean)
     */
    public List<LogPojo> getLogEntriesForReport( String severity, String messageText, Date start, Date end,
            int itemsPerPage, int page, int field, boolean ascending ) throws NexusException {

        LogDAO logDao;
        try {
            logDao = (LogDAO) Engine.getInstance().getDao( "logDao" );
        } catch ( Exception e ) {
            e.printStackTrace();
            throw new NexusException( e );
        } catch ( Error e ) {
            e.printStackTrace();
            throw e;
        }
        return logDao.getLogEntriesForReport( severity, messageText, start, end, itemsPerPage, page, field, ascending );
    }

    public void activate() {

        LOG.debug( "Activating..." );
        status = Constants.BeanStatus.ACTIVATED;
    }

    public void deactivate() {

        LOG.debug( "Deactivating..." );
        status = Constants.BeanStatus.INITIALIZED;
    }

    public Runlevel getActivationRunlevel() {

        return Runlevel.CORE;
    }

    public BeanStatus getStatus() {

        return status;
    }

    public void initialize( EngineConfiguration config ) {

        LOG.debug( "Initializing..." );
        status = Constants.BeanStatus.INITIALIZED;
    }

    public void teardown() {

        LOG.debug( "Tearing down..." );

        for ( String key : processingMessages.keySet() ) {
            deregisterProcessingMessage( key );
        }
        status = Constants.BeanStatus.INSTANTIATED;
    }

} // TransactionService
