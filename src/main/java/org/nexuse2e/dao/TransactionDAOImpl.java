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
package org.nexuse2e.dao;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.nexuse2e.Constants;
import org.nexuse2e.NexusException;
import org.nexuse2e.controller.StateTransitionException;
import org.nexuse2e.pojo.ActionPojo;
import org.nexuse2e.pojo.ChoreographyPojo;
import org.nexuse2e.pojo.ConversationPojo;
import org.nexuse2e.pojo.LogPojo;
import org.nexuse2e.pojo.MessagePayloadPojo;
import org.nexuse2e.pojo.MessagePojo;
import org.nexuse2e.pojo.PartnerPojo;
import org.springframework.orm.hibernate3.HibernateCallback;

/**
 * Data access object (DAO) to provide persistence services for transaction related entities.
 *
 * @author gesch
 */
public class TransactionDAOImpl extends BasicDAOImpl implements TransactionDAO {

    private static Logger              LOG           = Logger.getLogger( TransactionDAOImpl.class );

    private static Map<Integer, int[]> followUpConversationStates;
    private static Map<Integer, int[]> followUpMessageStates;

    static {
        followUpConversationStates = new HashMap<Integer, int[]>();
        followUpConversationStates.put( Constants.CONVERSATION_STATUS_ERROR, new int[] {
            Constants.CONVERSATION_STATUS_IDLE, Constants.CONVERSATION_STATUS_COMPLETED,
            Constants.CONVERSATION_STATUS_PROCESSING} );
        followUpConversationStates.put( Constants.CONVERSATION_STATUS_CREATED,
                new int[] { Constants.CONVERSATION_STATUS_PROCESSING} );
        followUpConversationStates.put( Constants.CONVERSATION_STATUS_PROCESSING, new int[] {
            Constants.CONVERSATION_STATUS_AWAITING_ACK, Constants.CONVERSATION_STATUS_AWAITING_BACKEND,
            Constants.CONVERSATION_STATUS_SENDING_ACK, Constants.CONVERSATION_STATUS_ACK_SENT_AWAITING_BACKEND,
            Constants.CONVERSATION_STATUS_BACKEND_SENT_SENDING_ACK, Constants.CONVERSATION_STATUS_IDLE,
            Constants.CONVERSATION_STATUS_ERROR, Constants.CONVERSATION_STATUS_COMPLETED} );
        followUpConversationStates.put( Constants.CONVERSATION_STATUS_AWAITING_ACK, new int[] {
            Constants.CONVERSATION_STATUS_COMPLETED, Constants.CONVERSATION_STATUS_ERROR,
            Constants.CONVERSATION_STATUS_IDLE} );
        followUpConversationStates.put( Constants.CONVERSATION_STATUS_IDLE,
                new int[] { Constants.CONVERSATION_STATUS_PROCESSING} );
        followUpConversationStates.put( Constants.CONVERSATION_STATUS_SENDING_ACK,
                new int[] { Constants.CONVERSATION_STATUS_ACK_SENT_AWAITING_BACKEND} );
        followUpConversationStates.put( Constants.CONVERSATION_STATUS_ACK_SENT_AWAITING_BACKEND, new int[] {
            Constants.CONVERSATION_STATUS_COMPLETED, Constants.CONVERSATION_STATUS_ERROR,
            Constants.CONVERSATION_STATUS_IDLE} );
        followUpConversationStates.put( Constants.CONVERSATION_STATUS_AWAITING_BACKEND,
                new int[] { Constants.CONVERSATION_STATUS_BACKEND_SENT_SENDING_ACK} );
        followUpConversationStates.put( Constants.CONVERSATION_STATUS_BACKEND_SENT_SENDING_ACK, new int[] {
            Constants.CONVERSATION_STATUS_COMPLETED, Constants.CONVERSATION_STATUS_ERROR,
            Constants.CONVERSATION_STATUS_IDLE} );
        followUpConversationStates.put( Constants.CONVERSATION_STATUS_COMPLETED,
                new int[] { Constants.CONVERSATION_STATUS_ERROR} );

        followUpMessageStates = new HashMap<Integer, int[]>();
        followUpMessageStates.put( Constants.MESSAGE_STATUS_FAILED, new int[] { Constants.MESSAGE_STATUS_QUEUED,
            Constants.MESSAGE_STATUS_SENT} );
        followUpMessageStates.put( Constants.MESSAGE_STATUS_RETRYING, new int[] { Constants.MESSAGE_STATUS_FAILED,
            Constants.MESSAGE_STATUS_SENT} );
        followUpMessageStates.put( Constants.MESSAGE_STATUS_QUEUED, new int[] { Constants.MESSAGE_STATUS_RETRYING,
            Constants.MESSAGE_STATUS_FAILED, Constants.MESSAGE_STATUS_SENT} );
        followUpMessageStates.put( Constants.MESSAGE_STATUS_SENT, new int[] { Constants.MESSAGE_STATUS_QUEUED} );
    }

    private String getType( int messageType ) {
        switch (messageType) {
        case org.nexuse2e.messaging.Constants.INT_MESSAGE_TYPE_NORMAL:
            return "normal";
        case org.nexuse2e.messaging.Constants.INT_MESSAGE_TYPE_ACK:
            return "acknowledgement";
        case org.nexuse2e.messaging.Constants.INT_MESSAGE_TYPE_ERROR:
            return "error";
        }
        return "unknown";
    }
    /**
     * Helper method for trace log level output
     */
    private void printConversationInfo( String prefix, ConversationPojo pojo, MessagePojo lastMessage ) {
        if (pojo != null) {
            
            // DEBUG
            int id = pojo.getNxConversationId();
            pojo.setNxConversationId( 0 );
            String s = pojo.toString();
            pojo.setNxConversationId( id );
            // END OF DEBUG
            
            
            List<MessagePojo> messages = pojo.getMessages();
            if (lastMessage == null) {
                lastMessage = (messages == null || messages.isEmpty() ? null : messages.get( messages.size() - 1 ));
            }
            org.nexuse2e.configuration.EngineConfiguration cfg = org.nexuse2e.Engine.getInstance().getCurrentConfiguration();
            ActionPojo action = null;
            if (lastMessage != null) {
                try {
                    action = cfg.getActionFromChoreographyByNxActionId(
                            cfg.getChoreographyByNxChoreographyId( lastMessage.getConversation().getChoreography().getNxChoreographyId() ),
                            lastMessage.getAction().getNxActionId() );
                } catch (NexusException ex) {
                    ex.printStackTrace();
                }
            }
            LOG.trace( prefix + s + " conversationId: " + pojo.getConversationId() + " " +
                    (messages == null ? 0 : messages.size()) + " messages" +
                    (lastMessage == null ? "" : ", last message type is " + getType( lastMessage.getType() ) +
                    ", messageId is " + lastMessage.getMessageId()) + (action == null ? "" : " (" + action.getName() + ")") +
                    ", thread " + Thread.currentThread().getName() );
        } else if (lastMessage != null) {
            LOG.trace( prefix + " message type is " + getType( lastMessage.getType() ) + ", messageId is " + lastMessage.getMessageId() );
        }
    }
    //    public static final String TYPE_ACK      = "Acknowledgment";
    //    public static final String TYPE_DEFAULT  = "Normal";

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.TransactionDAO#getConversationByConversationId(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public ConversationPojo getConversationByConversationId( String conversationId ) {

        DetachedCriteria dc = DetachedCriteria.forClass( ConversationPojo.class );
        dc.add( Restrictions.eq( "conversationId", conversationId ) );
        List<ConversationPojo> result = (List<ConversationPojo>) getListThroughSessionFind( dc, 0, 0 );

        if ( result != null && result.size() > 0 ) {
            ConversationPojo pojo = result.get( 0 );
            if (LOG.isTraceEnabled()) {
                printConversationInfo( "loaded conversation:", pojo, null );
            }
            return pojo;
        }

        return null;
    } // getConversationByConversationId

    public ConversationPojo getConversationByConversationId( int nxConversationId ) {

        return (ConversationPojo) getHibernateTemplate().get( ConversationPojo.class, nxConversationId );
    } // getConversationByConversationId

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.TransactionDAO#getMessageByMessageId(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public MessagePojo getMessageByMessageId( String messageId ) throws NexusException {

        DetachedCriteria dc = DetachedCriteria.forClass( MessagePojo.class );
        dc.add( Restrictions.eq( "messageId", messageId ) );
        List<MessagePojo> result = (List<MessagePojo>) getListThroughSessionFind( dc, 0, 0 );

        if ( result != null && result.size() > 0 ) {
            return result.get( 0 );
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.TransactionDAO#getMessageByReferencedMessageId(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public MessagePojo getMessageByReferencedMessageId( String messageId ) throws NexusException {

        DetachedCriteria dc = DetachedCriteria.forClass( MessagePojo.class );
        dc.createCriteria( "referencedMessage" ).add( Restrictions.eq( "messageId", messageId ) );
        //StringBuffer query = new StringBuffer( "from MessagePojo where referencedMessage.messageId='" + messageId + "'" );

        List<MessagePojo> result = (List<MessagePojo>) getListThroughSessionFind( dc, 0, 0 );

        if ( result != null && result.size() > 0 ) {
            return result.get( 0 );
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.TransactionDAO#getMessagesCount(java.lang.String, int, int, java.lang.String, java.lang.String, java.util.Date, java.util.Date)
     */
    public int getMessagesCount( String status, int nxChoreographyId, int nxPartnerId, String conversationId,
            String messageId, Date startDate, Date endDate ) throws NexusException {

        return getCountThroughSessionFind( getMessagesForReportHQL( status, nxChoreographyId, nxPartnerId,
                conversationId, messageId, null, startDate, endDate, 0, false ) );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.TransactionDAO#getActiveMessages()
     */
    @SuppressWarnings("unchecked")
    public List<MessagePojo> getActiveMessages() throws NexusException {

        DetachedCriteria dc = DetachedCriteria.forClass( MessagePojo.class );
        dc.add( Restrictions.in( "status", new Object[] { Constants.MESSAGE_STATUS_RETRYING,
                Constants.MESSAGE_STATUS_QUEUED} ) );
        dc.add( Restrictions.eq( "outbound", true ) );

        return (List<MessagePojo>) getListThroughSessionFind( dc, 0, 0 );
    } // getActiveMessages

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.TransactionDAO#getMessagesForReport(java.lang.String, int, int, java.lang.String, java.lang.String, java.lang.String, java.util.Date, java.util.Date, int, int, int, boolean)
     */
    @SuppressWarnings("unchecked")
    public List<MessagePojo> getMessagesForReport( String status, int nxChoreographyId, int nxPartnerId,
            String conversationId, String messageId, String type, Date start, Date end, int itemsPerPage, int page,
            int field, boolean ascending ) throws NexusException {

        DetachedCriteria dc = getMessagesForReportHQL( status, nxChoreographyId, nxPartnerId, conversationId,
                messageId, type, start, end, field, ascending );

        return (List<MessagePojo>) getListThroughSessionFind( dc, itemsPerPage * page, itemsPerPage );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.TransactionDAO#getConversationsForReport(java.lang.String, int, int, java.lang.String, java.util.Date, java.util.Date, int, int, int, boolean)
     */
    @SuppressWarnings("unchecked")
    public List<ConversationPojo> getConversationsForReport( String status, int nxChoreographyId, int nxPartnerId,
            String conversationId, Date start, Date end, int itemsPerPage, int page, int field, boolean ascending )
            throws NexusException {

        return (List<ConversationPojo>) getListThroughSessionFind( getConversationsForReportCriteria( status,
                nxChoreographyId, nxPartnerId, conversationId, start, end, field, ascending ), itemsPerPage * page,
                itemsPerPage );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.TransactionDAO#getConversationsCount(java.util.Date, java.util.Date)
     */
    public long getConversationsCount( Date start, Date end ) throws NexusException {

        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        StringBuilder query = new StringBuilder( "select count(nx_conversation_id) from nx_conversation conv " );
        Map<String, Date> map = appendQueryDate( query, "conv", start, end );
        Query sqlquery = session.createSQLQuery( query.toString() );
        for (String name : map.keySet()) {
            sqlquery.setDate( name, map.get( name ) );
        }
        return ( (Number) sqlquery.uniqueResult() ).longValue();

    }

    public long getMessagesCount( Date start, Date end ) throws NexusException {

        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        StringBuilder query = new StringBuilder( "select count(nx_message_id) from nx_message msg inner join nx_conversation conv on (msg.nx_conversation_id = conv.nx_conversation_id) " );
        Map<String, Date> map = appendQueryDate( query, "conv", start, end );
        Query sqlquery = session.createSQLQuery( query.toString() );
        for (String name : map.keySet()) {
            sqlquery.setDate( name, map.get( name ) );
        }
        return ( (Number) sqlquery.uniqueResult() ).longValue();

    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.TransactionDAO#getLogCount(java.util.Date, java.util.Date)
     */
    public long getLogCount( Date start, Date end ) throws NexusException {

        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        StringBuilder query = new StringBuilder( "select count(nx_log_id) from nx_log log " );
        Map<String, Date> map = appendQueryDate( query, "log", start, end );
        Query sqlquery = session.createSQLQuery( query.toString() );
        for (String name : map.keySet()) {
            sqlquery.setDate( name, map.get( name ) );
        }
        return ( (Number) sqlquery.uniqueResult() ).longValue();

    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.TransactionDAO#removeLogEntries(java.util.Date, java.util.Date)
     */
    public long removeLogEntries( Date start, Date end ) throws NexusException {

        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();

        StringBuilder query = new StringBuilder( "delete from nx_log " );
        Map<String, Date> map = appendQueryDate( query, "", start, end );
        Query sqlquery = session.createSQLQuery( query.toString() );
        for (String name : map.keySet()) {
            sqlquery.setDate( name, map.get( name ) );
        }
        return sqlquery.executeUpdate();

    }

    /**
     * @param start
     * @param end
     * @return
     */
    private Map<String, Date> appendQueryDate( StringBuilder queryString, String prefix, Date start, Date end ) {

        if (StringUtils.isEmpty( prefix )) {
            prefix = "";
        } else {
            prefix += ".";
        }
        boolean first = queryString.indexOf( "where" ) < 0;
        Map<String, Date> map = new HashMap<String, Date>( 2 );
        if ( start != null ) {
            if ( !first ) {
                queryString.append( " and " );
            } else {
                queryString.append( " where " );
            }
            queryString.append( prefix + "created_date >= :startDate" );
            map.put( "startDate", start );
            first = false;
        }
        if ( end != null ) {
            if ( !first ) {
                queryString.append( " and " );
            } else {
                queryString.append( " where " );
            }
            queryString.append( prefix + "created_date <= :endDate" );
            map.put( "endDate", end );
            first = false;
        }
        return map;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.TransactionDAO#removeConversations(java.util.Date, java.util.Date)
     */
    public long removeConversations( Date start, Date end ) throws NexusException {

        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();

        StringBuilder query = new StringBuilder(
                "delete label from nx_message_label label, nx_message message, nx_conversation conv where " +
                "label.nx_message_id = message.nx_message_id and message.nx_conversation_id = conv.nx_conversation_id" );

        Map<String, Date> map = appendQueryDate( query, "conv", start, end );
        LOG.debug( "sql1: " + query );
        Query sqlquery1 = session.createSQLQuery( query.toString() );
        for (String name : map.keySet()) {
            sqlquery1.setDate( name, map.get( name ) );
        }

        query = new StringBuilder(
                "delete payload from nx_message_payload payload, nx_message message, nx_conversation conv where " +
                "payload.nx_message_id = message.nx_message_id and message.nx_conversation_id = conv.nx_conversation_id" );

        map = appendQueryDate( query, "conv", start, end );
        LOG.debug( "sql2: " + query );
        Query sqlquery2 = session.createSQLQuery( query.toString() );
        for (String name : map.keySet()) {
            sqlquery2.setDate( name, map.get( name ) );
        }
        
        query = new StringBuilder(
                "delete message from nx_message message, nx_conversation conv where message.nx_conversation_id = conv.nx_conversation_id " );

        map = appendQueryDate( query, "conv", start, end );
        LOG.debug( "sql3: " + query );
        Query sqlquery3 = session.createSQLQuery( query.toString() );
        for (String name : map.keySet()) {
            sqlquery3.setDate( name, map.get( name ) );
        }

        query = new StringBuilder( "delete conv from nx_conversation conv" );

        map = appendQueryDate( query, "conv", start, end );
        LOG.debug( "sql4: " + query );
        Query sqlquery4 = session.createSQLQuery( query.toString() );
        for (String name : map.keySet()) {
            sqlquery4.setDate( name, map.get( name ) );
        }

        sqlquery1.executeUpdate();
        int result = sqlquery2.executeUpdate();
        sqlquery3.executeUpdate();
        sqlquery4.executeUpdate();

        return result;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.TransactionDAO#getConversationsCount(java.lang.String, int, int, java.lang.String, java.util.Date, java.util.Date, int, boolean)
     */
    public int getConversationsCount( String status, int nxChoreographyId, int nxPartnerId, String conversationId,
            Date start, Date end, int field, boolean ascending ) throws NexusException {

        return getCountThroughSessionFind( getConversationsForReportCriteria( status, nxChoreographyId, nxPartnerId,
                conversationId, start, end, SORT_NONE, ascending ) );
    }

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
     */
    private DetachedCriteria getConversationsForReportCriteria( String status, int nxChoreographyId, int nxPartnerId,
            String conversationId, Date start, Date end, int field, boolean ascending ) {

        DetachedCriteria dc = DetachedCriteria.forClass( ConversationPojo.class );
        
        if ( status != null ) {

            if ( status.indexOf( ',' ) == -1 ) {
                dc.add( Restrictions.eq( "status", status ) );

            } else {
                String[] statusValues = status.split( "," );
                dc.add( Restrictions.in( "status", statusValues ) );
            }
        }
        if ( nxChoreographyId != 0 ) {
            dc.createCriteria( "choreography" ).add( Restrictions.eq( "nxChoreographyId", nxChoreographyId ) );
        }
        if ( nxPartnerId != 0 ) {
            dc.createCriteria( "partner" ).add( Restrictions.eq( "nxPartnerId", nxPartnerId ) );
        }
        if ( conversationId != null ) {
            dc.add( Restrictions.like( "conversationId", "%" + conversationId.trim() + "%" ) );
        }
        if ( start != null ) {
            dc.add( Restrictions.ge( "createdDate", start ) );
        }
        if ( end != null ) {
            dc.add( Restrictions.le( "createdDate", end ) );
        }

        Order order = getSortOrder( field, ascending );
        if ( order != null ) {
            dc.addOrder( order );
        }

        return dc;
    }

    /**
     * @param status
     * @param nxChoreographyId
     * @param nxPartnerId
     * @param nxConversationId
     * @param messageId
     * @param type
     * @param start
     * @param end
     * @param field
     * @param ascending
     * @return
     */
    private DetachedCriteria getMessagesForReportHQL( String status, int nxChoreographyId, int nxPartnerId,
            String conversationId, String messageId, String type, Date start, Date end, int field, boolean ascending ) {

        DetachedCriteria dc = DetachedCriteria.forClass( MessagePojo.class );

        if ( status != null ) {
            if ( status.indexOf( ',' ) == -1 ) {
                dc.add( Restrictions.eq( "status", status ) );

            } else {
                String[] statusList = status.split( "," );
                dc.add( Restrictions.in( "status", statusList ) );
            }
        }
        if ( nxChoreographyId != 0 ) {
            dc.createCriteria( "conversation" ).createCriteria( "choreographyId" ).add(
                    Restrictions.eq( "nxChoreographyId", nxChoreographyId ) );
        }
        if ( nxPartnerId != 0 ) {
            dc.createCriteria( "conversation" ).createCriteria( "partner" ).add(
                    Restrictions.eq( "nxPartnerId", nxPartnerId ) );
        }
        if ( conversationId != null ) {
            dc.createCriteria( "conversation" ).add(
                    Restrictions.like( "conversationId", "%" + conversationId.trim() + "%" ) );
        }
        if ( messageId != null ) {
            dc.add( Restrictions.like( "messageId", "%" + messageId.trim() + "%" ) );
        }
        if ( type != null ) {
            dc.add( Restrictions.eq( "type", type ) );
        }
        if ( start != null ) {
            dc.add( Restrictions.ge( "createdDate", start ) );
        }
        if ( end != null ) {
            dc.add( Restrictions.le( "createdDate", end ) );
        }

        Order order = getSortOrder( field, ascending );
        if ( order != null ) {
            dc.addOrder( order );
        }
        return dc;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.TransactionDAO#storeTransaction(org.nexuse2e.pojo.ConversationPojo, org.nexuse2e.pojo.MessagePojo)
     */
    public void storeTransaction( ConversationPojo conversationPojo, MessagePojo messagePojo ) throws NexusException {

        LOG.debug( "storeTransaction: " + conversationPojo + " - " + messagePojo );

        saveOrUpdateRecord( conversationPojo );
        if (LOG.isTraceEnabled()) {
            printConversationInfo( "stored conversation:", conversationPojo, null );
        }

    } // storeTransaction

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.TransactionDAO#reattachConversation(org.nexuse2e.pojo.ConversationPojo)
     */
    public void reattachConversation( ConversationPojo conversationPojo ) throws NexusException {

        LOG.debug( "updateTransaction: " + conversationPojo );

        reattachRecord( conversationPojo );
        if (LOG.isTraceEnabled()) {
            printConversationInfo( "reattached conversation:", conversationPojo, null );
        }
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.TransactionDAO#updateMessage(org.nexuse2e.pojo.MessagePojo)
     */
    public void updateMessage( MessagePojo messagePojo ) throws NexusException {

        LOG.debug( "updateMessage: " + messagePojo );

        mergeRecord( messagePojo );
        if (LOG.isTraceEnabled()) {
            printConversationInfo( "updated message:", null, messagePojo );
        }
    } // updateMessage

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.TransactionDAO#updateConversation(org.nexuse2e.pojo.ConversationPojo)
     */
    public void updateConversation( ConversationPojo conversationPojo ) throws NexusException {

        LOG.debug( "updateConversation: " + conversationPojo );

        reattachRecord( conversationPojo );
        mergeRecord( conversationPojo );
        if (LOG.isTraceEnabled()) {
            printConversationInfo( "merged conversation:", conversationPojo, null );
        }
    } // updateMessage

    /**
     * @param objectName
     * @param field
     * @param ascending
     * @return
     */
    private Order getSortOrder( int field, boolean ascending ) {

        Order order = null;

        switch ( field ) {
            case SORT_NONE:
                break;
            case SORT_CREATED:
                if ( ascending ) {
                    order = Order.asc( "createdDate" );
                } else {
                    order = Order.desc( "createdDate" );
                }
                break;
            case SORT_MODIFIED:
                if ( ascending ) {
                    order = Order.asc( "lastModifiedDate" );
                } else {
                    order = Order.desc( "lastModifiedDate" );
                }
                break;
            case SORT_STATUS:
                if ( ascending ) {
                    order = Order.asc( "status" );
                } else {
                    order = Order.desc( "status" );
                }
                break;
            case SORT_CPAID:
                if ( ascending ) {
                    order = Order.asc( "choreographyId" );
                } else {
                    order = Order.desc( "choreographyId" );
                }
                break;
            case SORT_ACTION:
                if ( ascending ) {
                    order = Order.asc( "action" );
                } else {
                    order = Order.desc( "action" );
                }
                break;
        }

        return order;
    } // getSortString

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.TransactionDAO#getConversationsByPartner(org.nexuse2e.pojo.PartnerPojo)
     */
    @SuppressWarnings("unchecked")
    public List<ConversationPojo> getConversationsByPartner( PartnerPojo partner ) {

        //String query = "from ConversationPojo conv where conv.partner.nxPartnerId=" + partner.getNxPartnerId();

        DetachedCriteria dc = DetachedCriteria.forClass( ConversationPojo.class );
        dc.createCriteria( "partner" ).add( Restrictions.eq( "nxPartnerId", partner.getNxPartnerId() ) );

        return (List<ConversationPojo>) getListThroughSessionFind( dc, 0, 0 );

    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.TransactionDAO#getConversationsByPartnerAndChoreography(org.nexuse2e.pojo.PartnerPojo, org.nexuse2e.pojo.ChoreographyPojo)
     */
    @SuppressWarnings("unchecked")
    public List<ConversationPojo> getConversationsByPartnerAndChoreography( PartnerPojo partner,
            ChoreographyPojo choreography ) throws NexusException {

        DetachedCriteria dc = DetachedCriteria.forClass( ConversationPojo.class );
        dc.createCriteria( "partner" ).add( Restrictions.eq( "nxPartnerId", partner.getNxPartnerId() ) )
                .createCriteria( "choreography" ).add(
                        Restrictions.eq( "nxChoreographyId", choreography.getNxChoreographyId() ) );

        return (List<ConversationPojo>) getListThroughSessionFind( dc, 0, 0 );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.TransactionDAO#getConversationsByChoreography(org.nexuse2e.pojo.ChoreographyPojo)
     */
    @SuppressWarnings("unchecked")
    public List<ConversationPojo> getConversationsByChoreography( ChoreographyPojo choreography ) {

        DetachedCriteria dc = DetachedCriteria.forClass( ConversationPojo.class );
        dc.createCriteria( "choreography" ).add(
                Restrictions.eq( "nxChoreographyId", choreography.getNxChoreographyId() ) );

        return (List<ConversationPojo>) getListThroughSessionFind( dc, 0, 0 );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.TransactionDAO#getMessagesByPartner(org.nexuse2e.pojo.PartnerPojo, int, boolean)
     */
    @SuppressWarnings("unchecked")
    public List<MessagePojo> getMessagesByPartner( PartnerPojo partner, int field, boolean ascending )
            throws NexusException {

        DetachedCriteria dc = DetachedCriteria.forClass( MessagePojo.class );
        dc.createCriteria( "covnersation" ).createCriteria( "partner" ).add(
                Restrictions.eq( "nxPartnerId", partner.getNxPartnerId() ) );
        Order order = getSortOrder( field, ascending );
        if ( order != null ) {
            dc.addOrder( order );
        }

        return (List<MessagePojo>) getListThroughSessionFind( dc, 0, 0 );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.TransactionDAO#deleteMessage(org.nexuse2e.pojo.MessagePojo)
     */
    public void deleteMessage( MessagePojo messagePojo ) throws NexusException {

        LOG.debug( "deleteMessage: " + messagePojo );
        deleteRecord( messagePojo );
    } // updateMessage

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.TransactionDAO#deleteConversation(org.nexuse2e.pojo.ConversationPojo)
     */
    public void deleteConversation( ConversationPojo conversationPojo ) throws NexusException {

        LOG.debug( "deleteMessage: " + conversationPojo );
        deleteRecord( conversationPojo );
    } // updateMessage

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.TransactionDAO#getMessagesByPartnerAndDirection(org.nexuse2e.pojo.PartnerPojo, boolean, int, boolean)
     */
    @SuppressWarnings("unchecked")
    public List<MessagePojo> getMessagesByPartnerAndDirection( PartnerPojo partner, boolean outbound, int field,
            boolean ascending ) throws NexusException {

        DetachedCriteria dc = DetachedCriteria.forClass( MessagePojo.class );
        dc.createCriteria( "conversation" ).createCriteria( "partner" ).add(
                Restrictions.eq( "nxPartnerId", partner.getNxPartnerId() ) );
        dc.add( Restrictions.eq( "outbound", ( outbound ? 1 : 0 ) ) );

        Order order = getSortOrder( field, ascending );
        if ( order != null ) {
            dc.addOrder( order );
        }

        return (List<MessagePojo>) getListThroughSessionFind( dc, 0, 0 );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.TransactionDAO#getMessagesByActionPartnerDirectionAndStatus(org.nexuse2e.pojo.ActionPojo, org.nexuse2e.pojo.PartnerPojo, boolean, int, int, boolean)
     */
    @SuppressWarnings("unchecked")
    public List<MessagePojo> getMessagesByActionPartnerDirectionAndStatus( ActionPojo action, PartnerPojo partner,
            boolean outbound, int status, int field, boolean ascending ) {

        DetachedCriteria dc = DetachedCriteria.forClass( MessagePojo.class );

        dc.createCriteria( "conversation" ).createCriteria( "partner" ).add(
                Restrictions.eq( "nxPartnerId", partner.getNxPartnerId() ) );
        dc.createCriteria( "action" ).add( Restrictions.eq( "name", action.getName() ) );
        dc.createCriteria( "action" ).createCriteria( "choreography" ).add(
                Restrictions.eq( "name", action.getChoreography().getName() ) );
        dc.add( Restrictions.eq( "outbound", ( outbound ? 1 : 0 ) ) );
        dc.createCriteria( "conversation" ).createCriteria( "partner" ).add(
                Restrictions.eq( "partnerId", partner.getPartnerId() ) );
        dc.add( Restrictions.eq( "status", status ) );

        Order order = getSortOrder( field, ascending );
        if ( order != null ) {
            dc.addOrder( order );
        }

        return (List<MessagePojo>) getListThroughSessionFind( dc, 0, 0 );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.TransactionDAO#getMessagesByChoreographyAndPartner(org.nexuse2e.pojo.ChoreographyPojo, org.nexuse2e.pojo.PartnerPojo, int, boolean)
     */
    @SuppressWarnings("unchecked")
    public List<MessagePojo> getMessagesByChoreographyAndPartner( ChoreographyPojo choreography, PartnerPojo partner,
            int field, boolean ascending ) {

        DetachedCriteria dc = DetachedCriteria.forClass( MessagePojo.class );
        dc.createCriteria( "conversation" ).createCriteria( "choreography" ).add(
                Restrictions.eq( "nxChoreographyId", choreography.getNxChoreographyId() ) );
        dc.createCriteria( "conversation" ).createCriteria( "partner" ).add(
                Restrictions.eq( "nxPartnerId", partner.getNxPartnerId() ) );
        Order order = getSortOrder( field, ascending );
        if ( order != null ) {
            dc.addOrder( order );
        }

        return (List<MessagePojo>) getListThroughSessionFind( dc, 0, 0 );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.TransactionDAO#getMessagesByChoreographyPartnerAndConversation(org.nexuse2e.pojo.ChoreographyPojo, org.nexuse2e.pojo.PartnerPojo, org.nexuse2e.pojo.ConversationPojo, int, boolean)
     */
    @SuppressWarnings("unchecked")
    public List<MessagePojo> getMessagesByChoreographyPartnerAndConversation( ChoreographyPojo choreography,
            PartnerPojo partner, ConversationPojo conversation, int field, boolean ascending ) {

        DetachedCriteria dc = DetachedCriteria.forClass( MessagePojo.class );

        dc.createCriteria( "conversation" ).add(
                Restrictions.eq( "nxConversationId", conversation.getNxConversationId() ) );
        dc.createCriteria( "conversation" ).createCriteria( "choreography" ).add(
                Restrictions.eq( "nxChoreographyId", choreography.getNxChoreographyId() ) );
        dc.createCriteria( "conversation" ).createCriteria( "partner" ).add(
                Restrictions.eq( "nxPartnerId", partner.getNxPartnerId() ) );

        Order order = getSortOrder( field, ascending );
        if ( order != null ) {
            dc.addOrder( order );
        }

        return (List<MessagePojo>) getListThroughSessionFind( dc, 0, 0 );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.TransactionDAO#deleteLogEntry(org.nexuse2e.pojo.LogPojo)
     */
    public void deleteLogEntry( LogPojo logEntry ) throws NexusException {

        deleteRecord( logEntry );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.TransactionDAO#fetchLazyPayloads(org.nexuse2e.pojo.MessagePojo)
     */
    public List<MessagePayloadPojo> fetchLazyPayloads( MessagePojo message ) {

        lockRecord( message );
        List<MessagePayloadPojo> payloads = message.getMessagePayloads();
        // Force db access 
        payloads.size();
        return payloads;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.TransactionDAO#fetchLazyMessages(org.nexuse2e.pojo.ConversationPojo)
     */
    public List<MessagePojo> fetchLazyMessages( ConversationPojo conversation ) {

        lockRecord( conversation );
        List<MessagePojo> messages = conversation.getMessages();
        // Force db access 
        messages.size();
        return messages;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.TransactionDAO#updateTransaction(org.nexuse2e.pojo.MessagePojo, boolean)
     */
    public void updateTransaction( MessagePojo message, boolean force ) throws NexusException, StateTransitionException {

        
        int messageStatus = message.getStatus();
        int conversationStatus = message.getConversation().getStatus();
        
        if (messageStatus < Constants.MESSAGE_STATUS_FAILED
                || messageStatus > Constants.MESSAGE_STATUS_STOPPED) {
            throw new IllegalArgumentException( "Illegal message status: " + messageStatus
                    + ", only values >= " + Constants.MESSAGE_STATUS_FAILED
                    + " and <= " + Constants.MESSAGE_STATUS_STOPPED + " allowed" );
        }
        
        if (conversationStatus < Constants.CONVERSATION_STATUS_ERROR
                || conversationStatus > Constants.CONVERSATION_STATUS_COMPLETED) {
            throw new IllegalArgumentException( "Illegal conversation status: " + conversationStatus
                    + ", only values >= " + Constants.CONVERSATION_STATUS_ERROR
                    + " and <= " + Constants.CONVERSATION_STATUS_COMPLETED + " allowed" );
        }
        
        int allowedMessageStatus = messageStatus;
        int allowedConversationStatus = conversationStatus;
        
        MessagePojo persistentMessage;
        if ( message.getNxMessageId() > 0 ) {
            persistentMessage = (MessagePojo) getRecordById( MessagePojo.class, message.getNxMessageId() );
        } else {
            persistentMessage = message;
        }
        if (persistentMessage != null) {
            if (!force) {
                allowedMessageStatus = getAllowedTransitionStatus( persistentMessage, messageStatus );
                allowedConversationStatus = getAllowedTransitionStatus(
                        persistentMessage.getConversation(), conversationStatus );
            }
            message.setStatus( allowedMessageStatus );
            message.getConversation().setStatus( allowedConversationStatus );
            
            if (messageStatus == allowedMessageStatus && conversationStatus == allowedConversationStatus) {
                boolean updateMessage = message.getNxMessageId() > 0;
                
                // persist unsaved messages first
                List<MessagePojo> messages = message.getConversation().getMessages();
                for (MessagePojo m : messages) {
                    if (m.getNxMessageId() <= 0) {
                        getHibernateTemplate().save( m );
                    }
                }

                // we need to merge the message into the persistent message a persistent version exists
                if (updateMessage) {
                    getHibernateTemplate().merge( message );
                }

                // now, update the conversation status
                final int status = message.getConversation().getStatus();
                final int id = message.getConversation().getNxConversationId();
                getHibernateTemplate().execute( new HibernateCallback() {
                    public Object doInHibernate( Session session )
                            throws HibernateException, SQLException {
                        Query q = session.createQuery( "update ConversationPojo set status=" + status + " where nxConversationId=" + id );
                        return q.executeUpdate();
                    }
                } );
            }
        }
            
          
        
        String errMsg = null;
        
        if (allowedMessageStatus != messageStatus) {
            errMsg = "Illegal transition: Cannot set message status from " +
            MessagePojo.getStatusName( allowedMessageStatus ) + " to " + MessagePojo.getStatusName( messageStatus );
        }
        if (allowedConversationStatus != conversationStatus) {
            if (errMsg != null) {
                errMsg += ", cannot set conversation status from " + ConversationPojo.getStatusName( allowedConversationStatus ) +
                    " to " + ConversationPojo.getStatusName( conversationStatus );
            } else {
                errMsg = "Illegal transition: Cannot set conversation status from "
                    + ConversationPojo.getStatusName( allowedConversationStatus ) + " to " + ConversationPojo.getStatusName( conversationStatus );
            }
        }
        if (errMsg != null) {
            throw new StateTransitionException( errMsg );
        }
        

    } // updateTransaction

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.TransactionDAO#getAllowedTransitionStatus(org.nexuse2e.pojo.ConversationPojo, int)
     */
    public int getAllowedTransitionStatus( ConversationPojo conversation, int conversationStatus ) {

        if ( conversation.getStatus() == conversationStatus ) {
            return conversationStatus;
        }
        int[] validStates = followUpConversationStates.get( conversation.getStatus() );
        if ( validStates != null ) {
            for ( int status : validStates ) {
                if ( status == conversationStatus ) {
                    return conversationStatus;
                }
            }
        }

        return conversation.getStatus();
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.TransactionDAO#getAllowedTransitionStatus(org.nexuse2e.pojo.MessagePojo, int)
     */
    public int getAllowedTransitionStatus( MessagePojo message, int messageStatus ) {

        if ( message.getStatus() == messageStatus ) {
            return messageStatus;
        }
        int[] validStates = followUpMessageStates.get( message.getStatus() );
        if ( validStates != null ) {
            for ( int status : validStates ) {
                if ( status == messageStatus ) {
                    return messageStatus;
                }
            }
        }
        return message.getStatus();
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.TransactionDAO#getCreatedMessagesSinceCount(java.util.Date)
     */
    public int getCreatedMessagesSinceCount( Date since ) throws NexusException {

        DetachedCriteria dc = DetachedCriteria.forClass( MessagePojo.class );
        dc.add( Restrictions.ge( "createdDate", since ) );
        return getCountThroughSessionFind( dc );
    }
    
    public Session getDBSession() {
        return super.getSession();
    }
    
    public void releaseDBSession( Session session ) {
        super.releaseSession( session );
    }
}
