package org.nexuse2e.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.nexuse2e.NexusException;
import org.nexuse2e.pojo.PersistentPropertyPojo;

/**
 * DAO for accessing persistent properties. Since persistent properties are not
 * part of the NEXUSe2e configuration, they have their own DAO.
 *
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public class PersistentPropertyDAOImpl extends BasicDAOImpl implements PersistentPropertyDAO {

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.PersistentPropertyDAO#deletePersistentProperty(org.nexuse2e.pojo.PersistentPropertyPojo)
     */
    public void deletePersistentProperty( PersistentPropertyPojo property ) throws NexusException {

        deleteRecord( property );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.PersistentPropertyDAO#savePersistentProperty(org.nexuse2e.pojo.PersistentPropertyPojo)
     */
    public void savePersistentProperty( PersistentPropertyPojo property ) throws NexusException {

        saveOrUpdateRecord( property );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.PersistentPropertyDAO#getPersistentPropertyById(int)
     */
    public PersistentPropertyPojo getPersistentPropertyById( int nxPersistentPropertyId ) throws NexusException {

        return (PersistentPropertyPojo) getRecordById( PersistentPropertyPojo.class, new Integer( nxPersistentPropertyId ) );
    }

    private DetachedCriteria getQuery( String namespace, String version, boolean order ) {

        DetachedCriteria dc = DetachedCriteria.forClass( PersistentPropertyPojo.class );

        if ( namespace != null ) {

            dc.add( Restrictions.eq( "namespace", namespace ) );

        }
        if ( version != null ) {
            dc.add( Restrictions.eq( "version", version ) );
        }
        if ( order ) {
            dc.addOrder( Order.asc( "namespace" ) );
            dc.addOrder( Order.asc( "version" ) );
            dc.addOrder( Order.asc( "name" ) );
        }

        return dc;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.PersistentPropertyDAO#getPersistentProperties(java.lang.String, java.lang.String)
     */
    public List<PersistentPropertyPojo> getPersistentProperties( String namespace, String version )
            throws NexusException {

        return getPersistentProperties( namespace, version, null, null );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.PersistentPropertyDAO#getPersistentProperties(java.lang.String, java.lang.String, org.hibernate.Session, org.hibernate.Transaction)
     */
    @SuppressWarnings("unchecked")
    public List<PersistentPropertyPojo> getPersistentProperties( String namespace, String version, Session session,
            Transaction transaction ) throws NexusException {

        DetachedCriteria dc = getQuery( namespace, version, true );
        return (List<PersistentPropertyPojo>) getListThroughSessionFind( dc, 0, 0 );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.PersistentPropertyDAO#getPersistentProperties(java.lang.String, java.lang.String, int, int)
     */
    @SuppressWarnings("unchecked")
    public List<PersistentPropertyPojo> getPersistentProperties( String namespace, String version, int itemsPerPage,
            int pageNo ) throws NexusException {

        DetachedCriteria dc = getQuery( namespace, version, true );

        return (List<PersistentPropertyPojo>) getListThroughSessionFind( dc, itemsPerPage * pageNo, itemsPerPage );
    }

   

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.PersistentPropertyDAO#getPersistentPropertyCount(java.lang.String, java.lang.String)
     */
    public int getPersistentPropertyCount( String namespace, String version ) {

        DetachedCriteria dc = getQuery( namespace, version, false );
        return getCountThroughSessionFind( dc );
    }

    

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.PersistentPropertyDAO#getPersistentProperty(java.lang.String, java.lang.String, java.lang.String)
     */
    public PersistentPropertyPojo getPersistentProperty( String namespace, String version, String name ) {

        if ( namespace == null || version == null || name == null ) {
            throw new IllegalArgumentException( "namespace, version and name may not be null" );
        }
        DetachedCriteria dc = getQuery( namespace, version, false );
        dc.add( Restrictions.eq( "name", name ) );
        
        List<?> l = getListThroughSessionFind( dc,0,0 );
        if ( l == null || l.isEmpty() ) {
            return null;
        }
        return (PersistentPropertyPojo) l.get( 0 );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.PersistentPropertyDAO#updatePersistentPropertyInTransaction(java.lang.String, java.lang.String, java.lang.String, org.nexuse2e.dao.PersistenPropertyUpdateCallback)
     */
    public void updatePersistentPropertyInTransaction(String namespace,
            String version, String name,
            PersistentPropertyUpdateCallback callback) {

        if (callback != null) {
            DetachedCriteria dc = getQuery( namespace, version, false );
            dc.add( Restrictions.eq( "name", name ) );
//            Transaction t = getSession().beginTransaction();
            try {
                List<?> l = getListThroughSessionFind( dc,0,0 );
                PersistentPropertyPojo property;
                if (l != null && !l.isEmpty()) {
                    property = (PersistentPropertyPojo) l.get(0);
                } else {
                    property = new PersistentPropertyPojo(0, namespace, version, name, null);
                }
                boolean commit = callback.update(property);
    
                if (commit) {
                    saveOrUpdateRecord(property);
//                    t.commit();
                } else {
//                    t.rollback();
                }
            } finally {
//                t.rollback();
            }
        }
    }
}
