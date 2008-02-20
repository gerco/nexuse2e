package org.nexuse2e.pojo;

import java.io.Serializable;

/**
 * Interface to be implemented by all NEXUSe2e application pojos.
 *
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public interface NEXUSe2ePojo extends Serializable {

    /**
     * Gets the unique pojo identifier.
     * @return The unique ID.
     */
    public int getNxId();

    /**
     * Sets the unique pojo identifier.
     * @param nxId The unique ID to set.
     */
    public void setNxId( int nxId );
}
