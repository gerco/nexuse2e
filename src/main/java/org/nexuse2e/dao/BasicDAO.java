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
import java.util.Collection;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.criterion.DetachedCriteria;
import org.nexuse2e.NexusException;

public interface BasicDAO {

    public static final String ENGINE_ID  = "#ENGINE#";
    public static final String CACERTS_ID = "#CACERTS#";

    /**
     * Convenience method for loading a single instance.
     * @param returnClass The class of the record to retrieve
     * @param id The unique identifier of the record to retrieve
     * @return The retrieved record
     * @throws HibernateException
     */
    public abstract Object getRecordById( Class<?> returnClass, Serializable id ); // getRecordById

    /**
     * Convenience method for deleting a single record.
     * @param record Instance of the record to delete.
     * @throws CannotDeleteException
     * @throws HibernateException
     */
    public abstract void deleteRecord( Object record ); // deleteRecordById

    /**
     * Convenience method for deleting a single record.
     * @param record Instance of the record to delete.
     * @throws CannotDeleteException
     * @throws HibernateException
     */
    public abstract void deleteRecords( List<? extends Object> records ); // deleteRecordById

    /**
     * Convenience method for saving a new instance.
     * @param record The instance to be persisted
     * @param key The key of the instance to be persisted
     * @throws HibernateException
     */
    public abstract void saveRecord( Object record ); // saveRecord

    /**
     * Convenience method for saving a new instance.
     * @param record The instance to be persisted
     * @param key The key of the instance to be persisted
     * @throws HibernateException
     */
    public abstract void saveOrUpdateRecord( Object record ); // saveOrUpdateRecord

    /**
     * Convenience method for saving or updating a single instance.
     * @param record The instance to be persisted
     * @throws HibernateException
     */
    public abstract void updateRecord( Object record ); // updateRecord

    /**
     * Convenience method for merging a single record to the persistent object.
     * @param record The instance to be persisted
     * @param session The session. If <code>null</code>, a new session is created.
     * @param transaction The transaction. If <code>null</code>, a new transaction is created.
     * @throws NexusException
     */
    public abstract void mergeRecord( Object record ); // updateRecord

    /**
     * Convenience method for saving or updating a single instance.
     * @param record The instance to be persisted
     * @throws HibernateException
     */
    public abstract void reattachRecord( Object record ); // reattachRecord

    /**
     * Convenience method for saving or updating a <code>Collection</code> of data records.
     * @param records The collection of records to be persisted
     * @throws HibernateException
     */
    public abstract void updateRecords( Collection<?> records ); // saveOrUpdateRecord

    /**
     * Convenience method for running a Hibernate find query.
     * @param criteria the criteria to search for
     * @return List with the retrieved entries.
     */
    public abstract List<?> getListThroughSessionFind( DetachedCriteria criteria, int firstResult, int maxResult ); // getListThroughSessionFind

    /**
     * Convenience method that gets the row count for the given criteria.
     * @param criteria the criteria
     * @return The row count
    
     */
    public abstract int getCountThroughSessionFind( DetachedCriteria criteria );
    
    /**
     * @return Returns the iSeriesServer.
     */
    public abstract boolean isISeriesServer() ;

    /**
     * @param seriesTimestamp The iSeriesServer to set.
     */
    public abstract void setISeriesServer( boolean seriesTimestamp );

    public abstract boolean isMsSqlServer();

    public abstract void setMsSqlServer( boolean msSqlServer );


}