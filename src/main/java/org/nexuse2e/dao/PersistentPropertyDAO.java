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

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.nexuse2e.NexusException;
import org.nexuse2e.pojo.PersistentPropertyPojo;

public interface PersistentPropertyDAO {

    /**
     * Delete the given persistent property.
     * @param property The persistent property that shall be deleted.
     * @throws NexusException If the property could not be deleted.
     */
    public abstract void deletePersistentProperty( PersistentPropertyPojo property ) throws NexusException;

    /**
     * Saves the given persistent property.
     * @param property The persistent property that shall be saved.
     * @throws NexusException If the property could not be deleted.
     */
    public abstract void savePersistentProperty( PersistentPropertyPojo property ) throws NexusException;

    /**
     * Gets a persistent property by it's primary key (ID).
     * @param nxPersistentPropertyId The primary key.
     * @return A <code>PersistentPropertyPojo</code> object if one was found, <code>null</code> otherwise.
     * @throws NexusException If an error occurred while accessing the persistence layer.
     */
    public abstract PersistentPropertyPojo getPersistentPropertyById( int nxPersistentPropertyId )
            throws NexusException;

    /**
     * Gets all persistent properties in the given namespace and version.
     * @param namespace The namespace. If <code>null</code>, properties for all namespaces
     * are returned.
     * @param version The version. If <code>null</code>, properties for all versions are returned.
     * @throws NexusException if an error occurred accessing the persistent layer.
     */
    public abstract List<PersistentPropertyPojo> getPersistentProperties( String namespace, String version )
            throws NexusException;

    /**
     * Gets all persistent properties in the given namespace and version.
     * @param namespace The namespace. If <code>null</code>, properties for all namespaces
     * are returned.
     * @param version The version. If <code>null</code>, properties for all versions are returned.
     * @param session A session if an existing session shall be used, or <code>null</code> otherwise.
     * @param transaction A transaction if an existing transaction shall be used, or <code>null</code>
     * otherwise.
     * @throws NexusException if an error occurred accessing the persitent layer.
     */
    public abstract List<PersistentPropertyPojo> getPersistentProperties( String namespace, String version,
            Session session, Transaction transaction ) throws NexusException;

    /**
     * Gets all persistent properties in the given namespace and version.
     * @param namespace The namespace. If <code>null</code>, properties for all namespaces
     * are returned.
     * @param version The version. If <code>null</code>, properties for all versions are returned.
     * @param itemsPerPage The number of items per page (maximum number of records returned).
     * @param pageNo The requested page number. 
     * @param session A session if an existing session shall be used, or <code>null</code> otherwise.
     * @param transaction A transaction if an existing transaction shall be used, or <code>null</code>
     * otherwise.
     * @throws NexusException if an error occurred accessing the persitent layer.
     */
    public abstract List<PersistentPropertyPojo> getPersistentProperties( String namespace, String version,
            int itemsPerPage, int pageNo ) throws NexusException;

    /**
     * Gets the persistent property count for the given namespace and version.
     * @param namespace The namespace. If <code>null</code>, properties for all namespaces
     * are returned.
     * @param version The version. If <code>null</code>, properties for all versions are returned.
     * otherwise.
     */
    public abstract int getPersistentPropertyCount( String namespace, String version );

    /**
     * Gets a specific persistent property.
     * @param namespace The namespace. Must not be <code>null</code>.
     * @param version The version. Must not be <code>null</code>.
     * @param name The name. Must not be <code>null</code>.
     * @return A <code>PersistentPropertyPojo</code> if one was found, or <code>null</code> otherwise.
     * @throws IllegalArgumentException if a parameter (except for <code>session</code> and
     * <code>transaction</code>) is <code>null</code>.
     */
    public abstract PersistentPropertyPojo getPersistentProperty( String namespace, String version, String name );

}