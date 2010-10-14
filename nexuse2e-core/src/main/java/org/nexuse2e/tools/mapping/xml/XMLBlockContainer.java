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
package org.nexuse2e.tools.mapping.xml;

import java.util.HashMap;
import java.util.Map;

/**
 * @author guido.esch
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class XMLBlockContainer {

    private String    xmlContainerID;
    private Map<String,XMLBlock> blocks;

    /**
     * @param block
     */
    public void addXMLBLock( XMLBlock block ) {

        if ( blocks == null ) {
            blocks = new HashMap<String,XMLBlock>();
        }
        if ( block != null && block.getBlockID() != null ) {
            blocks.put( block.getBlockID(), block );
        }
    }

    /**
     * @param blockID
     * @return block
     */
    public XMLBlock getXMLBLockbyBlockID( String blockID ) {

        if ( blocks == null ) {
            blocks = new HashMap<String,XMLBlock>();
            return null;
        }
        return (XMLBlock) blocks.get( blockID );
    }

    /**
     * @return blocks
     */
    public Map<String,XMLBlock> getBlocks() {

        return blocks;
    }

    /**
     * @param blocks
     */
    public void setBlocks( Map<String,XMLBlock> blocks ) {

        this.blocks = blocks;
    }

    /**
     * @return id
     */
    public String getXmlContainerID() {

        return xmlContainerID;
    }

    /**
     * @param xmlContainerID
     */
    public void setXmlContainerID( String xmlContainerID ) {

        this.xmlContainerID = xmlContainerID;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {

        StringBuffer buffer = new StringBuffer();
        buffer.append( "Container:" ); //$NON-NLS-1$
        buffer.append( xmlContainerID + "\n" ); //$NON-NLS-1$
        if ( blocks != null ) {
            for (XMLBlock block : blocks.values()) {
                buffer.append( "  " + block ); //$NON-NLS-1$
                buffer.append( "\n" ); //$NON-NLS-1$
            }
        }
        return buffer.toString();
    }
}