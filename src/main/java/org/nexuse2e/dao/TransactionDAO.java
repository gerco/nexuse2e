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

import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.nexuse2e.Constants;
import org.nexuse2e.NexusException;
import org.nexuse2e.pojo.ActionPojo;
import org.nexuse2e.pojo.ChoreographyPojo;
import org.nexuse2e.pojo.ConversationPojo;
import org.nexuse2e.pojo.LogPojo;
import org.nexuse2e.pojo.MessagePojo;
import org.nexuse2e.pojo.PartnerPojo;

/**
 * Data access object (DAO) to provide persistence services for transaction related entities.
 *
 * @author gesch
 */
public class TransactionDAO extends BasicDAO {

    private static Logger   LOG           = Logger.getLogger( TransactionDAO.class );

    public static final int SORT_NONE     = 0;
    public static final int SORT_CREATED  = 1;
    public static final int SORT_MODIFIED = 2;
    public static final int SORT_STATUS   = 3;
    public static final int SORT_CPAID    = 4;
    public static final int SORT_ACTION   = 5;

    //    public static final String TYPE_ACK      = "Acknowledgment";
    //    public static final String TYPE_DEFAULT  = "Normal";

    /**
     * Find a conversation by its identifier
     * @param conversationId The converstaion identifier
     * @param session
     * @param transaction
     * @return
     * @throws NexusException
     */
    @SuppressWarnings("unchecked")
    public ConversationPojo getConversationByConversationId( String conversationId, Session session,
            Transaction transaction ) throws NexusException {

        StringBuffer query = new StringBuffer( "from ConversationPojo where conversationId='" + conversationId + "'" );

        List<ConversationPojo> result = getListThroughSessionFind( query.toString(), session, transaction );

        if ( result != null && result.size() > 0 ) {
            return result.get( 0 );
        }

        return null;
    } // getConversationByConversationId

    /**
     * @param conversationId
     * @param session
     * @param transaction
     * @return
     * @throws NexusException
     */
    @SuppressWarnings("unchecked")
    public ConversationPojo getConversationByConversationId( String choreographyId, String conversationId,
            int nxPartnerId, Session session, Transaction transaction ) throws NexusException {

        StringBuffer query = new StringBuffer( "from ConversationPojo where conversationId='" + conversationId
                + "' and partner.nxPartnerId=" + nxPartnerId + " and choreography.name='" + choreographyId + "'" );

        List<ConversationPojo> result = getListThroughSessionFind( query.toString(), session, transaction );

        if ( result != null && result.size() > 0 ) {
            return result.get( 0 );
        }

        return null;
    } // getConversationByConversationId

    @SuppressWarnings("unchecked")
    public MessagePojo getMessageByMessageId( String messageId, Session session, Transaction transaction )
            throws NexusException {

        StringBuffer query = new StringBuffer( "from MessagePojo where messageId='" + messageId + "'" );

        List<MessagePojo> result = getListThroughSessionFind( query.toString(), session, transaction );

        if ( result != null && result.size() > 0 ) {
            return result.get( 0 );
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public MessagePojo getMessageByReferencedMessageId( String messageId, Session session, Transaction transaction )
            throws NexusException {

        StringBuffer query = new StringBuffer( "from MessagePojo where referencedMessage.messageId='" + messageId + "'" );

        List<MessagePojo> result = getListThroughSessionFind( query.toString(), session, transaction );

        if ( result != null && result.size() > 0 ) {
            return result.get( 0 );
        }
        return null;
    }

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
    public int getMessagesCount( String status, int nxChoreographyId, int nxPartnerId, String conversationId,
            String messageId, Date startDate, Date endDate ) throws NexusException {

        return getCountThroughSessionFind( getMessagesForReportHQL( status, nxChoreographyId, nxPartnerId,
                conversationId, messageId, null, startDate, endDate, 0, false ), null, null );
    }

    /**
     * @return
     * @throws NexusException
     */
    @SuppressWarnings("unchecked")
    public List<MessagePojo> getActiveMessages() throws NexusException {

        return getListThroughSessionFind( "select message from MessagePojo as message where (message.status = "
                + Constants.MESSAGE_STATUS_RETRYING + " or message.status = " + Constants.MESSAGE_STATUS_QUEUED
                + ") and message.outbound=true", null, null );
    } // getActiveMessages

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
    @SuppressWarnings("unchecked")
    public List<MessagePojo> getMessagesForReport( String status, int nxChoreographyId, int nxPartnerId, String conversationId,
            String messageId, String type, Date start, Date end, int itemsPerPage, int page, int field,
            boolean ascending ) throws NexusException {

        return (List<MessagePojo>) getListThroughSessionFindByPageNo( "select message "
                + getMessagesForReportHQL( status, nxChoreographyId, nxPartnerId, conversationId, messageId, type,
                        start, end, field, ascending ), itemsPerPage, page );
    }

    /**
     * Convenience method for running a Hibernate find query.
     * @param queryString The query String
     * @return List with the retrieved entries.
     * @throws HibernateException
     */
    public List<?> getListThroughSessionFindByPageNo( String queryString, int itemsPerPage, int pageNo )
            throws NexusException {

        return getListThroughSessionFind( queryString, itemsPerPage * pageNo, itemsPerPage, null, null );
    } // getListThroughSessionFind

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
    @SuppressWarnings("unchecked")
    public List<ConversationPojo> getConversationsForReport( String status, int nxChoreographyId, int nxPartnerId, String conversationId,
            Date start, Date end, int itemsPerPage, int page, int field, boolean ascending, Session session, Transaction transaction ) throws NexusException {

        return getListThroughSessionFindByPageNo( getConversationsForReportHQL( status, nxChoreographyId, nxPartnerId,
                conversationId, start, end, field, ascending, false ), itemsPerPage, page, session, transaction );
    }

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
    public int getConversationsCount( String status, int nxChoreographyId, int nxPartnerId, String conversationId,
            Date start, Date end, int field, boolean ascending ) throws NexusException {

        return getCountThroughSessionFind( getConversationsForReportHQL( status, nxChoreographyId, nxPartnerId,
                conversationId, start, end, field, ascending, true ), null, null );
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
    private String getConversationsForReportHQL( String status, int nxChoreographyId, int nxPartnerId,
            String conversationId, Date start, Date end, int field, boolean ascending, boolean countOnly ) {

        StringBuffer query = new StringBuffer( ( countOnly ? "" : "select conversation" )
                + " from ConversationPojo as conversation" );
        boolean first = true;
        if ( status != null ) {

            query.append( " where " );
            if ( status.indexOf( ',' ) == -1 ) {
                query.append( "conversation.status = " + status + " " );
            } else {

                StringTokenizer st = new StringTokenizer( status, "," );
                StringBuffer sb = new StringBuffer();
                sb.append( "(" );
                while ( st.hasMoreElements() ) {
                    sb.append( (String) st.nextElement() );
                    if ( !st.hasMoreElements() ) {
                        sb.append( ")" );
                    } else {
                        sb.append( "," );
                    }
                }
                query.append( "conversation.status in " + sb.toString() );
                // LOG.trace("query:"+query.toString());
            }

            first = false;
        }
        if ( nxChoreographyId != 0 ) {
            if ( !first ) {
                query.append( " and " );
            } else {
                query.append( " where " );
            }
            query.append( "conversation.choreography.nxChoreographyId = " + nxChoreographyId + " " );
            first = false;
        }
        if ( nxPartnerId != 0 ) {
            if ( !first ) {
                query.append( " and " );
            } else {
                query.append( " where " );
            }
            LOG.trace( "nxpartnerid: " + nxPartnerId );
            query.append( "conversation.partner.nxPartnerId = " + nxPartnerId + " " );
            first = false;
        }
        if ( conversationId != null ) {
            if ( !first ) {
                query.append( " and " );
            } else {
                query.append( " where " );
            }
            query.append( "conversation.conversationId like '%" + conversationId.trim() + "%' " );
            first = false;
        }
        if ( start != null ) {
            if ( !first ) {
                query.append( " and " );
            } else {
                query.append( " where " );
            }
            query.append( "conversation.createdDate >= " + getTimestampString( start ) );
            first = false;
        }
        if ( end != null ) {
            if ( !first ) {
                query.append( " and " );
            } else {
                query.append( " where " );
            }

            query.append( "conversation.createdDate <= " + getTimestampString( end ) );
            first = false;
        }

        if ( !countOnly ) {
            query.append( getSortString( "conversation", field, ascending ) );
        }

        LOG.trace( "SQL: " + query );

        return query.toString();
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
    private String getMessagesForReportHQL( String status, int nxChoreographyId, int nxPartnerId,
            String conversationId, String messageId, String type, Date start, Date end, int field, boolean ascending ) {

        StringBuffer query = new StringBuffer( "from MessagePojo as message" );
        boolean first = true;
        if ( status != null ) {

            query.append( " where " );
            if ( status.indexOf( ',' ) == -1 ) {
                query.append( "message.status = " + status + " " );
            } else {
                StringTokenizer st = new StringTokenizer( status, "," );
                StringBuffer sb = new StringBuffer();
                sb.append( "(" );
                while ( st.hasMoreElements() ) {
                    sb.append( (String) st.nextElement() );
                    if ( !st.hasMoreElements() ) {
                        sb.append( ")" );
                    } else {
                        sb.append( "," );
                    }
                }
                query.append( "message.status in " + sb.toString() );
                // LOG.trace( "query:" + query.toString() );
            }
            first = false;
        }
        if ( nxChoreographyId != 0 ) {
            if ( !first ) {
                query.append( " and " );
            } else {
                query.append( " where " );
            }
            query.append( "message.conversation.choreography.nxChoreographyId = " + nxChoreographyId + " " );
            first = false;
        }
        if ( nxPartnerId != 0 ) {
            if ( !first ) {
                query.append( " and " );
            } else {
                query.append( " where " );
            }
            query.append( "message.conversation.partner.nxPartnerId = " + nxPartnerId + " " );
            first = false;
        }
        if ( conversationId != null ) {
            if ( !first ) {
                query.append( " and " );
            } else {
                query.append( " where " );
            }
            query.append( "message.conversation.conversationId like '%" + conversationId.trim() + "%' " );
            first = false;
        }
        if ( messageId != null ) {
            if ( !first ) {
                query.append( " and " );
            } else {
                query.append( " where " );
            }
            query.append( "message.messageId like '%" + messageId.trim() + "%' " );
            first = false;
        }
        if ( type != null ) {
            if ( !first ) {
                query.append( " and " );
            } else {
                query.append( " where " );
            }
            query.append( "message.type = '" + type + "' " );
            first = false;
        }
        if ( start != null ) {
            if ( !first ) {
                query.append( " and " );
            } else {
                query.append( " where " );
            }
            query.append( "message.createdDate >= " + getTimestampString( start ) );
            first = false;
        }
        if ( end != null ) {
            if ( !first ) {
                query.append( " and " );
            } else {
                query.append( " where " );
            }
            query.append( "message.createdDate <= " + getTimestampString( end ) );
            first = false;
        }

        query.append( getSortString( field, ascending ) );
        return query.toString();
    }

    public void storeTransaction( ConversationPojo conversationPojo, MessagePojo messagePojo ) throws NexusException {

        LOG.debug( "storeTransaction: " + conversationPojo + " - " + messagePojo );

        Session session = null;
        Transaction transaction = null;

        saveOrUpdateRecord( conversationPojo, session, transaction );

    } // storeTransaction

    public void reattachConversation( ConversationPojo conversationPojo ) throws NexusException {

        LOG.debug( "updateTransaction: " + conversationPojo );

        reattachRecord( conversationPojo );
    }

    public void updateMessage( MessagePojo messagePojo ) throws NexusException {

        LOG.debug( "updateMessage: " + messagePojo );

        Session session = null;
        Transaction transaction = null;

        mergeRecord( messagePojo, session, transaction );
    } // updateMessage

    public void updateConversation( ConversationPojo conversationPojo ) throws NexusException {

        LOG.debug( "updateConversation: " + conversationPojo );

        Session session = null;
        Transaction transaction = null;

        reattachRecord( conversationPojo );
        mergeRecord( conversationPojo, session, transaction );
    } // updateMessage
    
    /**
     * 
     * @param field
     * @param ascending
     * @return
     */
    private String getSortString( int field, boolean ascending ) {

        return getSortString( null, field, ascending );
    }

    /**
     * @param objectName
     * @param field
     * @param ascending
     * @return
     */
    private String getSortString( String objectName, int field, boolean ascending ) {

        String sortString = "";

        if ( objectName == null || objectName.trim().length() == 0 ) {
            objectName = "";
        } else {
            if ( !objectName.endsWith( "." ) ) {
                objectName += ".";
            }
        }

        switch ( field ) {
            case SORT_NONE:
                sortString = "";
                break;
            case SORT_CREATED:
                sortString = " order by " + objectName + "createdDate";
                break;
            case SORT_MODIFIED:
                sortString = " order by " + objectName + "lastModifiedDate";
                break;
            case SORT_STATUS:
                sortString = " order by " + objectName + "status";
                break;
            case SORT_CPAID:
                sortString = " order by " + objectName + "choreographyId";
                break;
            case SORT_ACTION:
                sortString = " order by " + objectName + "action";
                break;
        }

        if ( field != SORT_NONE ) {
            if ( ascending ) {
                sortString += " asc";
            } else {
                sortString += " desc";
            }
        }

        return sortString;
    } // getSortString

    /**
     * @param partner
     * @param session
     * @param transaction
     * @return
     * @throws NexusException
     */
    @SuppressWarnings("unchecked")
    public List<ConversationPojo> getConversationsByPartner( PartnerPojo partner, Session session,
            Transaction transaction ) throws NexusException {

        String query = "from ConversationPojo conv where conv.partner.nxPartnerId=" + partner.getNxPartnerId();

        return getListThroughSessionFind( query, session, transaction );

    }

    /**
     * @param partner
     * @param choreography
     * @param session
     * @param transaction
     * @return
     * @throws NexusException
     */
    @SuppressWarnings("unchecked")
    public List<ConversationPojo> getConversationsByPartnerAndChoreography( PartnerPojo partner,
            ChoreographyPojo choreography, Session session, Transaction transaction ) throws NexusException {

        String query = "from ConversationPojo conv where conv.partner.nxPartnerId=" + partner.getNxPartnerId()
                + " and conv.choreography.nxChoreographyId=" + choreography.getNxChoreographyId();

        return getListThroughSessionFind( query, session, transaction );
    }
    
    /**
     * @param choreography
     * @param session
     * @param transaction
     * @return
     * @throws NexusException
     */
    @SuppressWarnings("unchecked")
    public List<ConversationPojo> getConversationsByChoreography( ChoreographyPojo choreography, 
            Session session, Transaction transaction ) throws NexusException {

        String query = "from ConversationPojo conv where conv.choreography.nxChoreographyId=" + choreography.getNxChoreographyId();

        return getListThroughSessionFind( query, session, transaction );
    }

    /**
     * @param partner
     * @param session
     * @param transaction
     * @return
     * @throws NexusException
     */
    @SuppressWarnings("unchecked")
    public List<MessagePojo> getMessagesByPartner( PartnerPojo partner, int field, boolean ascending, Session session,
            Transaction transaction ) throws NexusException {

        String query = "from MessagePojo message where message.conversation.partner.nxPartnerId="
                + partner.getNxPartnerId() + getSortString( "message", field, ascending );

        return getListThroughSessionFind( query.toString(), session, transaction );
    }

    /**
     * @param messagePojo
     * @param session
     * @param transaction
     * @throws NexusException
     */
    public void deleteMessage( MessagePojo messagePojo, Session session, Transaction transaction )
            throws NexusException {

        LOG.debug( "deleteMessage: " + messagePojo );
        deleteRecord( messagePojo, session, transaction );
    } // updateMessage
    
    /**
     * @param conversationPojo
     * @param session
     * @param transaction
     * @throws NexusException
     */
    public void deleteConversation( ConversationPojo conversationPojo, Session session, Transaction transaction )
    throws NexusException {

        LOG.debug( "deleteMessage: " + conversationPojo );
        deleteRecord( conversationPojo, session, transaction );
    } // updateMessage

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
    @SuppressWarnings("unchecked")
    public List<MessagePojo> getMessagesByPartnerAndDirection( PartnerPojo partner, boolean outbound, int field,
            boolean ascending, Session session, Transaction transaction ) throws NexusException {

        String query = "from MessagePojo message where message.conversation.partner.nxPartnerId="
                + partner.getNxPartnerId() + " and message.outbound=" + ( outbound ? 1 : 0 )
                + getSortString( "message", field, ascending );
        return getListThroughSessionFind( query.toString(), session, transaction );
    }

    @SuppressWarnings("unchecked")
    public List<MessagePojo> getMessagesByActionPartnerDirectionAndStatus(
            ActionPojo action,
            PartnerPojo partner,
            boolean outbound,
            int status,
            int field,
            boolean ascending, 
            Session session,
            Transaction transaction ) throws NexusException {

        String query = "from MessagePojo message where message.conversation.partner.nxPartnerId="
                + partner.getNxPartnerId() + " and message.action.name='" + action.getName()
                + "' and message.action.choreography.name='" +  action.getChoreography().getName()
                + "' and message.outbound=" + ( outbound ? 1 : 0 )
                + " and message.conversation.partner.partnerId='" + partner.getPartnerId()
                + "' and message.status=" + status
                + getSortString( "message", field, ascending );
        return getListThroughSessionFind( query.toString(), session, transaction );
    }

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
    @SuppressWarnings("unchecked")
    public List<MessagePojo> getMessagesByChoreographyAndPartner( ChoreographyPojo choreography, PartnerPojo partner,
            int field, boolean ascending, Session session, Transaction transaction ) throws NexusException {

        String query = "from MessagePojo message where message.conversation.choreography.nxChoreographyId="
                + choreography.getNxChoreographyId() + " and message.conversation.partner.nxPartnerId="
                + partner.getNxPartnerId() + getSortString( "message", field, ascending );
        return getListThroughSessionFind( query.toString(), session, transaction );
    }

    /**
     * @param choreography
     * @param partner
     * @param conversation
     * @param field
     * @param ascending
     * @param session
     * @param transaction
     * @return
     * @throws NexusException
     */
    @SuppressWarnings("unchecked")
    public List<MessagePojo> getMessagesByChoreographyPartnerAndConversation( ChoreographyPojo choreography,
            PartnerPojo partner, ConversationPojo conversation, int field, boolean ascending, Session session,
            Transaction transaction ) throws NexusException {

        String query = "from MessagePojo message where message.conversation.nxConversationId="
                + conversation.getNxConversationId() + " and message.conversation.choreography.nxChoreographyId="
                + choreography.getNxChoreographyId() + " and message.conversation.partner.nxPartnerId="
                + partner.getNxPartnerId() + getSortString( "message", field, ascending );
        return getListThroughSessionFind( query.toString(), session, transaction );
    }

    /**
     * @param logEntry
     * @param session
     * @param transaction
     */
    public void deleteLogEntry( LogPojo logEntry, Session session, Transaction transaction ) throws NexusException {

        deleteRecord( logEntry, session, transaction );
    }
}
