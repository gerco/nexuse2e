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

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.nexuse2e.NexusException;
import org.nexuse2e.pojo.LogPojo;

/**
 * @author mbreilmann
 *
 */
public class LogDAO extends BasicDAO {

    public static final int SORT_NONE        = 0;

    public static final int SORT_SEVERITY    = 1;

    public static final int SORT_DESCRIPTION = 2;

    public static final int SORT_CREATED     = 3;

    public static final int SORT_MODIFIED    = 4;

    public static final int SORT_METHODNAME  = 5;

    public static final int SORT_CLASSNAME   = 6;

    /**
     * @param log 
     * @param session
     * @param transaction
     * @throws NexusException
     */
    public void saveLog( LogPojo log, Session session, Transaction transaction ) throws NexusException {

        log.setCreatedDate( new Date() );
        saveRecord( log, session, transaction );

    }

    /**
     * @param origin
     * @param severity
     * @param messageText
     * @param start
     * @param end
     * @param field
     * @param ascending
     * @return
     */
    private String getLogEntriesForReportHQL( String severity, String messageText, Date start, Date end, int field,
            boolean ascending ) {

        StringBuffer query = new StringBuffer( "select log from LogPojo as log" );

        boolean first = true;

        if ( severity != null ) {
            if ( !first ) {
                query.append( " and " );
            } else {
                query.append( " where " );
            }

            query.append( "log.severity = " + severity + " " );
            first = false;
        }

        if ( messageText != null ) {
            if ( !first ) {
                query.append( " and " );
            } else {
                query.append( " where " );
            }
            query.append( "log.description like '%" + messageText + "%' " );
            first = false;
        }

        if ( start != null ) {

            if ( !first ) {

                query.append( " and " );

            } else {

                query.append( " where " );

            }

            query.append( "log.createdDate >= " + getTimestampString( start ) );

            first = false;

        }

        if ( end != null ) {

            if ( !first ) {

                query.append( " and " );

            } else {

                query.append( " where " );

            }

            query.append( "log.createdDate <= " + getTimestampString( end ) );

            first = false;

        }

        query.append( getSortString( field, ascending ) );

        // LOG.trace( "query:" + query );

        return query.toString();

    }

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
    @SuppressWarnings("unchecked")
    public List<LogPojo> getLogEntriesForReport( String severity, String messageText, Date start, Date end,
            int itemsPerPage, int page, int field, boolean ascending, Session session, Transaction transaction ) throws NexusException {

        // LOG.trace( "page:" + page );
        // LOG.trace( "pagesize:" + itemsPerPage );
        return (List<LogPojo>) getListThroughSessionFindByPageNo( getLogEntriesForReportHQL( severity, messageText, start, end, field,
                ascending ), itemsPerPage, page, session, transaction );
    }

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
    public int getLogEntriesForReportCount( String severity, String messageText, Date start, Date end, int field,
            boolean ascending ) throws NexusException {

        List<?> items = getListThroughSessionFind( getLogEntriesForReportHQL( severity, messageText, start, end, field,
                ascending ), null, null );
        if (items == null) {
            return 0;
        }
        return items.size();
    }

    @SuppressWarnings("unchecked")
    public List<LogPojo> getLog() throws NexusException {

        StringBuffer query = new StringBuffer( "select log from LogPojo as log" );

        return (List<LogPojo>) getListThroughSessionFind( query.toString(), null, null );
    } // getLog

    /**
     * 
     * @param field
     * @param ascending
     * @return
     */
    private String getSortString( int field, boolean ascending ) {

        String sortString = "";

        switch ( field ) {
            case SORT_NONE:
                sortString = "";
                break;
            case SORT_CREATED:
                sortString = " order by log.createdDate";
                break;
            case SORT_MODIFIED:
                sortString = " order by log.lastModifiedDate";
                break;
            case SORT_SEVERITY:
                sortString = " order by log.severity";
                break;
            case SORT_DESCRIPTION:
                sortString = " order by log.description";
                break;
            case SORT_METHODNAME:
                sortString = " order by log.methodName";
                break;
            case SORT_CLASSNAME:
                sortString = " order by log.className";
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

} // LogDAO
