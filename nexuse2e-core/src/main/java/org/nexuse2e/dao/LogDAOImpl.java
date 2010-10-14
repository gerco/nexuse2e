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

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.nexuse2e.NexusException;
import org.nexuse2e.pojo.LogPojo;

/**
 * @author mbreilmann
 *
 */
public class LogDAOImpl extends BasicDAOImpl implements LogDAO {

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.LogDAO#saveLog(org.nexuse2e.pojo.LogPojo)
     */
    public void saveLog( LogPojo log ) throws NexusException {

        log.setCreatedDate( new Date() );
        saveRecord( log );

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
    private DetachedCriteria getLogEntriesForReportCriteria( String severity, String messageText, Date start, Date end, int field,
            boolean ascending ) {

        DetachedCriteria dc = DetachedCriteria.forClass( LogPojo.class );
        
        
        if ( severity != null ) {
            dc.add( Restrictions.eq( "severity", Integer.parseInt( severity ) ) );
        }

        if ( messageText != null ) {
            dc.add( Restrictions.like( "description", "%" + messageText + "%" ) );
        }
        if ( start != null ) {
            dc.add( Restrictions.ge( "createdDate",  start  ) );
        }
        if ( end != null ) {
            dc.add( Restrictions.le( "createdDate",  end ) );
        }
//        if ( start != null ) {
//            dc.add( Restrictions.ge( "createdDate", getTimestampString( start ) ) );
//        }
//        if ( end != null ) {
//            dc.add( Restrictions.le( "createdDate", getTimestampString( end ) ) );
//        }
        
        Order order = getSortOrder( field, ascending );
        if(order != null) {
            dc.addOrder( order );
        }
        
        return dc;

    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.LogDAO#getLogEntriesForReport(java.lang.String, java.lang.String, java.util.Date, java.util.Date, int, int, int, boolean)
     */
    @SuppressWarnings("unchecked")
    public List<LogPojo> getLogEntriesForReport( String severity, String messageText, Date start, Date end,
            int itemsPerPage, int page, int field, boolean ascending ) throws NexusException {

        // LOG.trace( "page:" + page );
        // LOG.trace( "pagesize:" + itemsPerPage );
        return (List<LogPojo>) getListThroughSessionFind( getLogEntriesForReportCriteria( severity, messageText, start, end,
                field, ascending ), itemsPerPage * page, itemsPerPage );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.LogDAO#getLogEntriesForReportCount(java.lang.String, java.lang.String, java.util.Date, java.util.Date, int, boolean)
     */
    public int getLogEntriesForReportCount( String severity, String messageText, Date start, Date end, int field,
            boolean ascending ) throws NexusException {

        List<?> items = getListThroughSessionFind( getLogEntriesForReportCriteria( severity, messageText, start, end, field,
                ascending ),0,0 );
        if ( items == null ) {
            return 0;
        }
        return items.size();
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.LogDAO#getLog()
     */
    @SuppressWarnings("unchecked")
    public List<LogPojo> getLog() throws NexusException {
        
        DetachedCriteria dc = DetachedCriteria.forClass( LogPojo.class );
        
        return (List<LogPojo>) getListThroughSessionFind( dc,0,0 );
    } // getLog

    /**
     * 
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
            case SORT_SEVERITY:
                if ( ascending ) {
                    order = Order.asc( "severity" );
                } else {
                    order = Order.desc( "severity" );
                }
                break;
            case SORT_DESCRIPTION:
                if ( ascending ) {
                    order = Order.asc( "description" );
                } else {
                    order = Order.desc( "description" );
                }
                break;
            case SORT_METHODNAME:
                if ( ascending ) {
                    order = Order.asc( "methodName" );
                } else {
                    order = Order.desc( "methodName" );
                }
                break;
            case SORT_CLASSNAME:
                if ( ascending ) {
                    order = Order.asc( "className" );
                } else {
                    order = Order.desc( "className" );
                }
                break;
        }
        return order;

    } // getSortString

} // LogDAO
