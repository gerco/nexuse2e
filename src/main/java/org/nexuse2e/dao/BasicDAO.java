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

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * @author gesch
 *
 */
public class BasicDAO extends HibernateDaoSupport {

    private static Logger         LOG                 = Logger.getLogger( BasicDAO.class );

    protected static final String SELECT_COUNT_PREFIX = "select count(*) ";
    protected static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String    ENGINE_ID           = "#ENGINE#";
    public static final String    CACERTS_ID          = "#CACERTS#";

    public static boolean         iSeriesServer       = false;
    public static boolean         msSqlServer         = false;

    private static int            sessionCount        = 0;

    private static String         timestampPattern    = null;

    /**
     * Default Constructor
     *
     */
    public BasicDAO() {

        if ( timestampPattern == null ) {
            timestampPattern = Engine.getInstance().getTimestampPattern();
            if ( StringUtils.isEmpty( timestampPattern ) ) {
                timestampPattern = DEFAULT_DATE_FORMAT;
            }
        }

    } // BasicDAO

    public Session getDBSession() {

        // return getSessionFactory().getCurrentSession();
        // return getSession();
        sessionCount++;
        // LOG.trace( "getDBSession - sessionCount: " + sessionCount );
        // return SessionFactoryUtils.getSession( getSessionFactory(), true );
        return getSession();
    }

    public void releaseDBSession( Session session ) {

        sessionCount--;
        // LOG.trace( "releaseDBSession - sessionCount: " + sessionCount );
        releaseSession( session );
        /*
         if ( session.isConnected() ) {
         session.close();
         }
         */
    }

    /**
     * Convenience method for loading a single instance.
     * @param returnClass The class of the record to retrieve
     * @param id The unique identifier of the record to retrieve
     * @return The retrieved record
     * @throws HibernateException
     */
    public Object getRecordById( Class returnClass, Serializable id, Session session, Transaction transaction )
            throws NexusException {

        Object record = null;
        NexusException nexusException = null;
        boolean extSessionFlag = true;
        boolean extTransactionFlag = true;

        try {
            if ( session == null ) {
                session = getDBSession();
                extSessionFlag = false;
            }

            try {
                if ( transaction == null ) {
                    transaction = session.beginTransaction();
                    extTransactionFlag = false;
                }
                record = session.get( returnClass, id );

                if ( !extTransactionFlag ) {
                    transaction.commit();
                }
            } catch ( HibernateException e ) {
                if ( transaction != null && !extTransactionFlag ) {
                    transaction.rollback();
                }
                LOG.error( "Error retrieving record by ID!" );
                e.printStackTrace();
                nexusException = new NexusException( e );
            } finally {
                if ( !extSessionFlag ) {
                    releaseDBSession( session );
                }
            }

        } catch ( HibernateException e ) {
            e.printStackTrace();
            // record does not exist
            nexusException = new NexusException( e );
        } finally {
            if ( nexusException != null ) {
                throw nexusException;
            }
        }

        return record;
    } // getRecordById

    /**
     * Convenience method for deleting a single record.
     * @param record Instance of the record to delete.
     * @throws CannotDeleteException
     * @throws HibernateException
     */
    public Object deleteRecord( Object record, Session session, Transaction transaction ) throws NexusException {

        NexusException nexusException = null;
        boolean extSessionFlag = true;
        boolean extTransactionFlag = true;
        try {

            if ( session == null ) {
                session = getDBSession();
                extSessionFlag = false;
            }
            try {
                if ( transaction == null ) {
                    transaction = session.beginTransaction();
                    extTransactionFlag = false;
                }

                session.delete( record );

                if ( !extTransactionFlag ) {
                    transaction.commit();
                }
            } catch ( HibernateException e ) {
                if ( transaction != null && !extTransactionFlag ) {
                    transaction.rollback();
                }
                e.printStackTrace();
                // record does not exist
                nexusException = new NexusException( e );
            } finally {
                if ( !extSessionFlag ) {
                    releaseDBSession( session );
                }
            }

        } catch ( HibernateException e ) {
            e.printStackTrace();
            // record does not exist
            nexusException = new NexusException( e );
        } finally {
            if ( nexusException != null ) {
                throw nexusException;
            }
        }

        return record;
    } // deleteRecordById

    /**
     * Convenience method for saving a new instance.
     * @param record The instance to be persisted
     * @param key The key of the instance to be persisted
     * @throws HibernateException
     */
    public void saveRecord( Object record, Session session, Transaction transaction ) throws NexusException {

        NexusException nexusException = null;
        boolean extSessionFlag = true;
        boolean extTransactionFlag = true;

        try {
            if ( session == null ) {
                try {
                    session = getDBSession();
                } catch ( Exception e ) {
                    throw new NexusException( e );
                }
                extSessionFlag = false;
            }

            try {

                if ( transaction == null ) {
                    try {
                        transaction = session.beginTransaction();
                    } catch ( Exception e ) {
                        if ( !extSessionFlag && session != null ) {
                            try {
                                session.close();
                            } catch ( Exception e1 ) {
                                // session cleanup failed
                            }
                        }
                        throw new NexusException( e );
                    }
                    extTransactionFlag = false;
                }
                if ( session.isConnected() ) {
                    session.save( record );
                }

                if ( !extTransactionFlag ) {
                    transaction.commit();
                }
            } catch ( HibernateException e ) {
                if ( transaction != null && !extTransactionFlag ) {
                    transaction.rollback();
                }
                LOG.error( "Error saving record: " + record );
                e.printStackTrace();
                nexusException = new NexusException( e );
            } finally {
                if ( !extSessionFlag ) {
                    releaseDBSession( session );
                    // session.close();
                }
            }

        } catch ( HibernateException e ) {
            e.printStackTrace();
            // record does not exist
            nexusException = new NexusException( e );
        } finally {
            if ( nexusException != null ) {
                throw nexusException;
            }
        }
    } // saveRecord

    /**
     * Convenience method for saving a new instance.
     * @param record The instance to be persisted
     * @param key The key of the instance to be persisted
     * @throws HibernateException
     */
    public void saveOrUpdateRecord( Object record, Session session, Transaction transaction ) throws NexusException {

        NexusException nexusException = null;
        boolean extSessionFlag = true;
        boolean extTransactionFlag = true;

        try {
            if ( session == null ) {
                session = getDBSession();
                extSessionFlag = false;
            }

            try {

                if ( transaction == null ) {
                    transaction = session.beginTransaction();
                    extTransactionFlag = false;
                }
                session.saveOrUpdate( record );

                if ( !extTransactionFlag ) {
                    transaction.commit();
                }
            } catch ( HibernateException e ) {
                if ( transaction != null && !extTransactionFlag ) {
                    transaction.rollback();
                }
                LOG.error( "Error saving record: " + record );
                e.printStackTrace();
                nexusException = new NexusException( e );
            } finally {
                if ( !extSessionFlag ) {
                    releaseDBSession( session );
                    // session.close();
                }
            }

        } catch ( HibernateException e ) {
            e.printStackTrace();
            // record does not exist
            nexusException = new NexusException( e );
        } finally {
            if ( nexusException != null ) {
                throw nexusException;
            }
        }
    } // saveOrUpdateRecord

    /**
     * Convenience method for saving or updating a single instance.
     * @param record The instance to be persisted
     * @throws HibernateException
     */
    public void updateRecord( Object record, Session session, Transaction transaction ) throws NexusException {

        NexusException persistenceException = null;
        boolean extSessionFlag = true;
        boolean extTransactionFlag = true;

        try {
            if ( session == null ) {
                session = getDBSession();
                extSessionFlag = false;
            }
            try {

                if ( transaction == null ) {
                    transaction = session.beginTransaction();
                    extTransactionFlag = false;
                }
                session.update( record );
                if ( !extTransactionFlag ) {
                    transaction.commit();
                }
            } catch ( HibernateException e ) {
                if ( transaction != null ) {
                    transaction.rollback();
                }
                LOG.error( "Error updating record: " + record );
                e.printStackTrace();
                persistenceException = new NexusException( e );
            } finally {
                if ( !extSessionFlag ) {
                    releaseDBSession( session );
                    // session.close();
                }
            }

        } catch ( HibernateException e ) {
            e.printStackTrace();
            // record does not exist
            persistenceException = new NexusException( e );
        } finally {
            if ( persistenceException != null ) {
                throw persistenceException;
            }
        }
    } // updateRecord

    /**
     * Convenience method for saving or updating a single instance.
     * @param record The instance to be persisted
     * @throws HibernateException
     */
    public void reattachRecord( Object record ) throws NexusException {

        NexusException persistenceException = null;
        boolean extSessionFlag = true;
        boolean extTransactionFlag = true;
        Session session = null;
        Transaction transaction = null;

        try {
            if ( session == null ) {
                session = getDBSession();
                extSessionFlag = false;
            }
            try {

                if ( transaction == null ) {
                    transaction = session.beginTransaction();
                    extTransactionFlag = false;
                }
                session.lock( record, LockMode.NONE );
                if ( !extTransactionFlag ) {
                    transaction.commit();
                }
            } catch ( HibernateException e ) {
                if ( transaction != null ) {
                    transaction.rollback();
                }
                LOG.error( "Error reattaching record: " + record );
                e.printStackTrace();
                persistenceException = new NexusException( e );
            } finally {
                if ( !extSessionFlag ) {
                    releaseDBSession( session );
                }
            }

        } catch ( HibernateException e ) {
            e.printStackTrace();
            // record does not exist
            persistenceException = new NexusException( e );
        } finally {
            if ( persistenceException != null ) {
                throw persistenceException;
            }
        }
    } // reattachRecord

    /**
     * Convenience method for saving or updating a <code>Collection</code> of data records.
     * @param records The collection of records to be persisted
     * @throws HibernateException
     */
    public void updateRecords( Collection records, Session session, Transaction transaction ) throws NexusException {

        NexusException nexusException = null;
        boolean extSessionFlag = true;
        boolean extTransactionFlag = true;

        try {
            if ( session == null ) {
                session = getDBSession();
                extSessionFlag = false;
            }

            Object record = null;
            try {
                if ( transaction == null ) {
                    transaction = session.beginTransaction();
                    extTransactionFlag = false;
                }

                for ( Iterator iter = records.iterator(); iter.hasNext(); ) {
                    record = iter.next();
                    session.update( record );
                }
                if ( !extTransactionFlag ) {
                    transaction.commit();
                }
            } catch ( HibernateException e ) {
                LOG.error( "Error saving record: " + record );
                if ( transaction != null && !extTransactionFlag ) {
                    transaction.rollback();
                }
                e.printStackTrace();
                nexusException = new NexusException( e );
            } finally {
                if ( !extSessionFlag ) {
                    releaseDBSession( session );
                }
            }

        } catch ( HibernateException e ) {
            e.printStackTrace();
            // record does not exist
            nexusException = new NexusException( e );
        } finally {
            if ( nexusException != null ) {
                throw nexusException;
            }
        }
    } // saveOrUpdateRecord

    /**
     * Convenience method for running a Hibernate find query.
     * @param queryString The query String
     * @return List with the retrieved entries.
     * @throws HibernateException
     */
    public List getListThroughSessionFind( String queryString, Session session, Transaction transaction )
            throws NexusException {

        List entries = null;
        NexusException nexusException = null;
        boolean extSessionFlag = true;
        boolean extTransactionFlag = true;

        try {
            if ( session == null ) {
                try {
                    session = getDBSession();
                } catch ( Exception e ) {
                    throw new NexusException( e );
                }
                extSessionFlag = false;
            }

            try {
                Query query;
                if ( transaction == null ) {
                    try {
                        transaction = session.beginTransaction();
                    } catch ( Exception e ) {
                        if ( !extSessionFlag && session != null ) {
                            try {
                                session.close();
                            } catch ( RuntimeException e1 ) {
                                // Cleanup failed
                            }
                        }
                        throw new NexusException(e);
                    }
                    extTransactionFlag = false;
                }
                if ( session.isConnected() ) {
                    query = session.createQuery( queryString );
                    entries = query.list();
                    if ( !extTransactionFlag ) {
                        transaction.commit();
                    }
                }

            } catch ( HibernateException e ) {
                if ( transaction != null && !extTransactionFlag ) {
                    transaction.rollback();
                }
                LOG.error( "Error retrieving list for query: " + queryString );
                //                e.printStackTrace();
                nexusException = new NexusException( e );
            } finally {
                if ( !extSessionFlag ) {
                    releaseDBSession( session );
                }
            }

        } catch ( HibernateException e ) {
            e.printStackTrace();
            // record does not exist
            nexusException = new NexusException( e );
        } finally {
            if ( nexusException != null ) {
                throw nexusException;
            }
        }

        return entries;
    } // getListThroughSessionFind

    /**
     * Convenience method for running a Hibernate find query.
     * @param queryString The query String
     * @return List with the retrieved entries.
     * @throws HibernateException
     */
    public List getListThroughSessionFindByPageNo( String queryString, int itemsPerPage, int pageNo, Session session,
            Transaction transaction ) throws NexusException {

        return getListThroughSessionFind( queryString, itemsPerPage * pageNo, itemsPerPage, session, transaction );
    } // getListThroughSessionFind

    /**
     * Convenience method for running a Hibernate find query.
     * @param query The query String
     * @param fromIndex The index where to start.
     * @param maximumResults The maximum number of results.
     * @return List with the retrieved entries.
     * @throws HibernateException
     */
    public List getListThroughSessionFind( String query, int fromIndex, int maximumResults, Session session,
            Transaction transaction ) throws NexusException {

        List entries = null;
        NexusException nexusException = null;
        boolean extSessionFlag = true;
        boolean extTransactionFlag = true;

        try {
            if ( ( fromIndex >= 0 ) && ( maximumResults >= 0 ) ) {

                if ( session == null ) {
                    session = getDBSession();
                    extSessionFlag = false;
                }
                try {
                    if ( transaction == null ) {
                        transaction = session.beginTransaction();
                        extTransactionFlag = false;
                    }
                    Query hqlQuery = session.createQuery( query );
                    hqlQuery.setFirstResult( fromIndex );
                    if ( maximumResults > 0 ) {
                        hqlQuery.setMaxResults( maximumResults );
                    }
                    entries = hqlQuery.list();
                    if ( !extTransactionFlag ) {
                        transaction.commit();
                    }
                } catch ( HibernateException e ) {
                    if ( transaction != null && !extTransactionFlag ) {
                        transaction.rollback();
                    }
                    LOG.error( "Error retrieving list!" );
                    e.printStackTrace();
                    nexusException = new NexusException( e );
                } finally {
                    if ( !extSessionFlag ) {
                        releaseDBSession( session );
                    }
                }
            } else {
                entries = new ArrayList();
            }
        } catch ( HibernateException e ) {
            e.printStackTrace();
            // record does not exist
            nexusException = new NexusException( e );
        } finally {
            if ( nexusException != null ) {
                throw nexusException;
            }
        }

        return entries;
    } // getListThroughSessionFind

    /**
     * Convenience method that gets the row count for the given HQL query.
     * @param queryString The HQL query (without the <code>select count(*)</code> directive).
     * @return The row count, or <code>-1</code> if not available.
     * @throws HibernateException If no hibernate session could be created.
     */
    public int getCountThroughSessionFind( String queryString, Session session, Transaction transaction )
            throws NexusException {

        int count = -1;
        NexusException nexusException = null;
        boolean extSessionFlag = true;
        boolean extTransactionFlag = true;

        try {
            if ( session == null ) {
                session = getDBSession();
                extSessionFlag = false;
            }

            try {
                if ( transaction == null ) {
                    transaction = session.beginTransaction();
                    extTransactionFlag = false;
                }

                Query query = session.createQuery( SELECT_COUNT_PREFIX + queryString );
                count = ( (Long) query.iterate().next() ).intValue();
                if ( !extTransactionFlag ) {
                    transaction.commit();
                }
            } catch ( HibernateException e ) {
                if ( transaction != null && !extTransactionFlag ) {
                    transaction.rollback();
                }
                LOG.error( "Error retrieving count!" );
                e.printStackTrace();
                nexusException = new NexusException( e );
            } finally {
                if ( !extSessionFlag ) {
                    releaseDBSession( session );
                }
            }

        } catch ( HibernateException e ) {
            e.printStackTrace();
            // record does not exist
            nexusException = new NexusException( e.getMessage() );
        } catch ( Exception e ) {
            e.printStackTrace();
            // record does not exist
            nexusException = new NexusException( e.getMessage() );
        } finally {
            if ( nexusException != null ) {
                throw nexusException;
            }
        }
        return count;
    }

    public static String getTimestampString( Date timestamp ) {

        String result = null;
        SimpleDateFormat sdf = null;
        if ( iSeriesServer ) {
            sdf = new SimpleDateFormat( "yyyy-MM-dd-HH.mm.ss.SSS" );
        } else {
            sdf = new SimpleDateFormat( timestampPattern );
        }
        result = "'" + sdf.format( timestamp ) + "'";
        // Microsoft SQL server: use timestamp escape sequence to work with all region settings
        /*
         if ( msSqlServer ) {
         result = "{ts " + result + "}";
         }
         */
        // LOG.trace( "Timestamp: " + result );
        return result;
    } // getTimestampString

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
    public static boolean isISeriesServer() {

        return iSeriesServer;
    }

    /**
     * @param seriesTimestamp The iSeriesServer to set.
     */
    public static void setISeriesServer( boolean seriesTimestamp ) {

        iSeriesServer = seriesTimestamp;
    }

    public static boolean isMsSqlServer() {

        return msSqlServer;
    }

    public static void setMsSqlServer( boolean msSqlServer ) {

        BasicDAO.msSqlServer = msSqlServer;
    }

} // BasicDAO
