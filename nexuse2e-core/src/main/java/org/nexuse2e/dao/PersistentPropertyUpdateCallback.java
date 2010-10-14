package org.nexuse2e.dao;

import org.nexuse2e.pojo.PersistentPropertyPojo;

/**
 * Interface to be implemented for transactional operations on PersistenProperties.
 * Used for Hollywood priciple.
 *
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public interface PersistentPropertyUpdateCallback {

    /**
     * Callback method that will be invoked by Hollywood.
     * @param property The property. If the requested property was not found, a newly created
     * <code>PersistenPropertyPojo</code> object will be passed here.
     * @return <code>true</code> if property changes be committed afterwards, <code>false</code> if a
     * rollback shall be performed.
     */
    public abstract boolean update(PersistentPropertyPojo property); 
}
