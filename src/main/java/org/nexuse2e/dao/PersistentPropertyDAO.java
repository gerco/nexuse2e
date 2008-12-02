package org.nexuse2e.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.nexuse2e.NexusException;
import org.nexuse2e.pojo.PersistentPropertyPojo;

/**
 * DAO for accessing persistent properties. Since persistent properties are not
 * part of the NEXUSe2e configuration, they have their own DAO.
 *
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public class PersistentPropertyDAO extends BasicDAO {

    /**
     * Delete the given persistent property.
     * @param property The persistent property that shall be deleted.
     * @throws NexusException If the property could not be deleted.
     */
    public void deletePersistentProperty( PersistentPropertyPojo property ) throws NexusException {
        deleteRecord( property, null, null );
    }
    
    /**
     * Saves the given persistent property.
     * @param property The persistent property that shall be saved.
     * @throws NexusException If the property could not be deleted.
     */
    public void savePersistentProperty( PersistentPropertyPojo property ) throws NexusException {
        saveRecord( property, null, null );
    }
    
    /**
     * Gets a persistent property by it's primary key (ID).
     * @param nxPersistentPropertyId The primary key.
     * @return A <code>PersistentPropertyPojo</code> object if one was found, <code>null</code> otherwise.
     * @throws NexusException If an error occurred while accessing the persistence layer.
     */
    public PersistentPropertyPojo getPersistentPropertyById(
            int nxPersistentPropertyId ) throws NexusException {
        return getPersistentPropertyById( nxPersistentPropertyId, null, null );
    }
    
    /**
     * Gets a persistent property by it's primary key (ID).
     * @param nxPersistentPropertyId The primary key.
     * @param session The session. May be <code>null</code>.
     * @param transaction The transaction. May be <code>null</code>.
     * @return A <code>PersistentPropertyPojo</code> object if one was found, <code>null</code> otherwise.
     * @throws NexusException If an error occurred while accessing the persistence layer.
     */
    public PersistentPropertyPojo getPersistentPropertyById(
            int nxPersistentPropertyId, Session session, Transaction transaction ) throws NexusException {
        return (PersistentPropertyPojo) getRecordById(PersistentPropertyPojo.class, nxPersistentPropertyId, session, transaction );
    }
    
    private String getQuery( String namespace, String version ) {
        StringBuilder query = new StringBuilder( "from PersistentPropertyPojo p " );
        if (namespace != null || version != null) {
            query.append( "where " );
        }
        if (namespace != null) {
            query.append( "p.namespace='" + namespace + "'" );
            if (version != null) {
                query.append( " and p.version='" + version + "'" );
            }
        } else {
            if (version != null) {
                query.append( "p.version='" + version + "'" );
            }
        }

        return query.toString();
    }
    
    /**
     * Gets all persistent properties in the given namespace and version.
     * @param namespace The namespace. If <code>null</code>, properties for all namespaces
     * are returned.
     * @param version The version. If <code>null</code>, properties for all versions are returned.
     * @throws NexusException if an error occurred accessing the persistent layer.
     */
    public List<PersistentPropertyPojo> getPersistentProperties( String namespace, String version ) throws NexusException {
        return getPersistentProperties( namespace, version, null, null );
    }
    
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
    @SuppressWarnings("unchecked")
    public List<PersistentPropertyPojo> getPersistentProperties(
            String namespace, String version, Session session, Transaction transaction ) throws NexusException {
        String query = getQuery( namespace, version );
        return (List<PersistentPropertyPojo>) getListThroughSessionFind( query, session, transaction );
    }
    
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
    @SuppressWarnings("unchecked")
    public List<PersistentPropertyPojo> getPersistentProperties(
            String namespace,
            String version,
            int itemsPerPage,
            int pageNo,
            Session session,
            Transaction transaction ) throws NexusException {
        String query = getQuery( namespace, version );
        return (List<PersistentPropertyPojo>) getListThroughSessionFindByPageNo( query, itemsPerPage, pageNo, session, transaction );
    }
    
    /**
     * Gets the persistent property count for the given namespace and version.
     * @param namespace The namespace. If <code>null</code>, properties for all namespaces
     * are returned.
     * @param version The version. If <code>null</code>, properties for all versions are returned.
     * @throws NexusException if an error occurred accessing the persistent layer.
     */
    public int getPersistentPropertyCount( String namespace, String version ) throws NexusException {
        return getPersistentPropertyCount( namespace, version, null, null );
    }
    
    /**
     * Gets the persistent property count for the given namespace and version.
     * @param namespace The namespace. If <code>null</code>, properties for all namespaces
     * are returned.
     * @param version The version. If <code>null</code>, properties for all versions are returned.
     * @param session A session if an existing session shall be used, or <code>null</code> otherwise.
     * @param transaction A transaction if an existing transaction shall be used, or <code>null</code>
     * otherwise.
     * @throws NexusException if an error occurred accessing the persitent layer.
     */
    public int getPersistentPropertyCount(
            String namespace, String version, Session session, Transaction transaction ) throws NexusException {
        String query = getQuery( namespace, version );
        return getCountThroughSessionFind( query, session, transaction );
    }
    
    /**
     * Gets a specific persistent property.
     * @param namespace The namespace. Must not be <code>null</code>.
     * @param version The version. Must not be <code>null</code>.
     * @param name The name. Must not be <code>null</code>.
     * @return A <code>PersistentPropertyPojo</code> if one was found, or <code>null</code> otherwise.
     * @throws NexusException if an error occurred accessing the persistent layer.
     * @throws IllegalArgumentException if a parameter is <code>null</code>.
     */
    public PersistentPropertyPojo getPersistentProperty(
            String namespace, String version, String name ) throws NexusException {
        return getPersistentProperty( namespace, version, name );
    }
    
    /**
     * Gets a specific persistent property.
     * @param namespace The namespace. Must not be <code>null</code>.
     * @param version The version. Must not be <code>null</code>.
     * @param name The name. Must not be <code>null</code>.
     * @param session A session if an existing session shall be used, or <code>null</code> otherwise.
     * @param transaction A transaction if an existing transaction shall be used, or <code>null</code>
     * @return A <code>PersistentPropertyPojo</code> if one was found, or <code>null</code> otherwise.
     * @throws NexusException if an error occurred accessing the persistent layer.
     * @throws IllegalArgumentException if a parameter (except for <code>session</code> and
     * <code>transaction</code>) is <code>null</code>.
     */
    public PersistentPropertyPojo getPersistentProperty(
            String namespace, String version, String name, Session session, Transaction transaction ) throws NexusException {
        if (namespace == null || version == null || name == null) {
            throw new IllegalArgumentException( "namespace, version and name may not be null" );
        }
        List<?> l = getListThroughSessionFind( getQuery( namespace, version ) + " and p.name='" + name + "'", session, transaction );
        if (l == null || l.isEmpty()) {
            return null;
        }
        return (PersistentPropertyPojo) l.get( 0 );
    }
}
