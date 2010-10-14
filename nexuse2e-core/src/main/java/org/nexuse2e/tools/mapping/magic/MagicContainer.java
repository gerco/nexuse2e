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
package org.nexuse2e.tools.mapping.magic;

import java.util.HashMap;
import java.util.Map;

/**
 * @author guido.esch
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MagicContainer {

    private Map<String, Magic> magicContainer;
    private String    containerID;

    /**
     * @param block
     */
    public void addMagic( Magic block ) {

        if ( magicContainer == null ) {
            magicContainer = new HashMap<String, Magic>();
        }
        if ( block != null && block.getRecordID() != null ) {
            magicContainer.put( block.getRecordID(), block );
        }
    }

    /**
     * @param blockID
     * @return magic
     */
    public Magic getMappingByXMLBlockId( String blockID ) {

        if ( magicContainer == null ) {
            magicContainer = new HashMap<String, Magic>();
            return null;
        }
        
        for (Magic m : magicContainer.values()) {
            if ( m.getBlockID().equals( blockID ) ) {
                return m;
            }
        }
        return null;
    }

    /**
     * @param recordID
     * @return magic
     */
    public Magic getMappingByRecordId( String recordID ) {

        if ( magicContainer == null ) {
            magicContainer = new HashMap<String, Magic>();
            return null;
        }

        return (Magic) magicContainer.get( recordID );
    }

    /**
     * @return id
     */
    public String getContainerID() {

        return containerID;
    }

    /**
     * @param containerID
     */
    public void setContainerID( String containerID ) {

        this.containerID = containerID;
    }

    /** 
     * @return container
     */
    public Map<String, Magic> getMagicContainer() {

        return magicContainer;
    }

    /**
     * @param magicContainer
     */
    public void setMagicContainer( Map<String, Magic> magicContainer ) {

        this.magicContainer = magicContainer;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {

        StringBuffer buffer = new StringBuffer();
        buffer.append( "Container:" ); //$NON-NLS-1$
        buffer.append( containerID + "\n" ); //$NON-NLS-1$
        if ( magicContainer != null ) {
            for (Magic magic : magicContainer.values()) {
                buffer.append( "  " + magic ); //$NON-NLS-1$
                buffer.append( "\n" ); //$NON-NLS-1$
            }
        }
        return buffer.toString();
    }
}