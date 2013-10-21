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

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.LockMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.nexuse2e.Engine;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate4.support.HibernateDaoSupport;

/**
 * @author gesch
 *
 */
public class BasicDAOImpl extends HibernateDaoSupport implements BasicDAO {
    
    /**
     * This enumeration type is a coarse categorization of the
     * used DB management system, including only the most common DBMSs.
     */
    public enum DatabaseType {
        DERBY,
        MYSQL,
        MSSQL,
        ORACLE,
        UNKNOWN
    }

    //private static Logger         LOG                 = Logger.getLogger( BasicDAOImpl.class );

    protected static final String SELECT_COUNT_PREFIX = "select count(*) ";
    protected static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    private static boolean         iSeriesServer       = false;
    private static boolean         msSqlServer         = false;

    //private static int            sessionCount        = 0;

    private static String         timestampPattern    = null;

    /**
     * Default Constructor
     *
     */
    public BasicDAOImpl() {

        if ( timestampPattern == null ) {
            timestampPattern = Engine.getInstance().getTimestampPattern();
            if ( StringUtils.isEmpty( timestampPattern ) ) {
                timestampPattern = DEFAULT_DATE_FORMAT;
            }
        }

    } // BasicDAO

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.BasicDAO#getRecordById(java.lang.Class, java.io.Serializable)
     */
    public Object getRecordById( Class<?> returnClass, Serializable id ) {

        return getHibernateTemplate().get(returnClass, id);
        
    } // getRecordById

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.BasicDAO#deleteRecord(java.lang.Object)
     */
    public void deleteRecord( Object record ) {

        getHibernateTemplate().delete( record );
        
    } // deleteRecordById
    
    /* (non-Javadoc)
     * @see org.nexuse2e.dao.BasicDAO#deleteRecords(java.util.List)
     */
    public void deleteRecords(List<? extends Object> records) {
        getHibernateTemplate().deleteAll( records );
    } // deleteRecordById

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.BasicDAO#saveRecord(java.lang.Object)
     */
    public void saveRecord( Object record ) {

        getHibernateTemplate().save( record );
    } // saveRecord

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.BasicDAO#saveOrUpdateRecord(java.lang.Object)
     */
    public void saveOrUpdateRecord( Object record ) {
        getHibernateTemplate().saveOrUpdate( record );
    } // saveOrUpdateRecord

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.BasicDAO#updateRecord(java.lang.Object)
     */
    public void updateRecord( Object record ) {

        getHibernateTemplate().update( record );
    } // updateRecord

    protected void lockRecord(Object record) {
        getHibernateTemplate().lock( record, LockMode.NONE );
    }
    /* (non-Javadoc)
     * @see org.nexuse2e.dao.BasicDAO#mergeRecord(java.lang.Object)
     */
    public void mergeRecord( Object record ) {

        getHibernateTemplate().merge( record );
    } // updateRecord

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.BasicDAO#reattachRecord(java.lang.Object)
     */
    public void reattachRecord( Object record ) {

        getHibernateTemplate().lock( record, LockMode.NONE );
    } // reattachRecord

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.BasicDAO#updateRecords(java.util.Collection)
     */
    public void updateRecords( Collection<?> records ) {

        
        for ( Iterator<?> iter = records.iterator(); iter.hasNext(); ) {
            Object record = iter.next();
            getHibernateTemplate().update( record );
        }
               
    } // saveOrUpdateRecord

    
    /* (non-Javadoc)
     * @see org.nexuse2e.dao.BasicDAO#getListThroughSessionFind(org.hibernate.criterion.DetachedCriteria, int, int)
     */
    public List<?> getListThroughSessionFind( DetachedCriteria criteria,int firstResult, int maxResult ) {
        
        try {
            return getHibernateTemplate().findByCriteria( criteria,firstResult, maxResult );
        } catch ( DataAccessException e ) {
            e.printStackTrace();
            throw e;
        }
        
    } // getListThroughSessionFind

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.BasicDAO#getCountThroughSessionFind(org.hibernate.criterion.DetachedCriteria)
     */
    public int getCountThroughSessionFind( DetachedCriteria criteria ) {

        criteria.setProjection( Projections.rowCount() );
        Number n = (Number)getHibernateTemplate().findByCriteria( criteria ).get( 0 );
        return (n == null ? 0 : n.intValue());
    }

    /**
     * Create a timestamp string for created_date and lastmodified_date
     * @return The current timestamp as a String
     */
    protected String getCurrentTimestampAsString() {

        SimpleDateFormat sdf = new SimpleDateFormat( DEFAULT_DATE_FORMAT );

        return sdf.format( new Date() );
    } // getCurrentTimestampAsString

    /**
     * @return Returns the iSeriesServer.
     */
    public boolean isISeriesServer() {

        return iSeriesServer;
    }

    /**
     * @param seriesTimestamp The iSeriesServer to set.
     */
    public void setISeriesServer( boolean seriesTimestamp ) {

        iSeriesServer = seriesTimestamp;
    }

    public boolean isMsSqlServer() {

        return msSqlServer;
    }

    public void setMsSqlServer( boolean msSqlServer ) {

        BasicDAOImpl.msSqlServer = msSqlServer;
    }
    
    /**
     * Tries to find out the DB type.
     * @return The DBMS type, or <code>DatabaseType.UNKNOWN</code> if it could not be determined.
     */
    public DatabaseType getDatabaseType() {
        String dialect = Engine.getInstance().getDatabaseDialect();
        if (dialect != null) {
            dialect = dialect.toLowerCase();
            if (dialect.contains( "derby" )) {
                return DatabaseType.DERBY;
            } else if (dialect.contains( "oracle" )) {
                return DatabaseType.ORACLE;
            } else if (dialect.contains( "mysql" )) {
                return DatabaseType.MYSQL;
            } else if (dialect.contains( "sqlserver" )) {
                return DatabaseType.MSSQL;
            }
        }
        return DatabaseType.UNKNOWN;
    }

} // BasicDAO
