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
 */
public class Magic {

    private Map<String,MagicEntry> entries;
    private String    blockID;
    private String    recordID;

    /**
     * @param entry
     */
    public void addEntry( MagicEntry entry ) {

        if ( entries == null ) {
            entries = new HashMap<String, MagicEntry>();
        }
        if ( entry != null && entry.getXPathId() != null ) {
            entries.put( entry.getXPathId(), entry );
        }
    }

    /**
     * @param xpathID
     * @return entry
     */
    public MagicEntry getEntryByXpathID( String xpathID ) {

        if ( entries == null ) {
            entries = new HashMap<String, MagicEntry>();
            return null;
        }
        return (MagicEntry) entries.get( xpathID );
    }

    /**
     * @return entries
     */
    public Map<String, MagicEntry> getEntries() {

        return entries;
    }

    /**
     * @param entries
     */
    public void setEntries( Map<String, MagicEntry> entries ) {

        this.entries = entries;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {

        StringBuffer buffer = new StringBuffer();
        buffer.append( "RecordID:" ); //$NON-NLS-1$
        buffer.append( recordID );
        buffer.append( " BlockID:" ); //$NON-NLS-1$
        buffer.append( blockID + "\n" ); //$NON-NLS-1$
        if ( entries != null ) {
            for (MagicEntry entry : entries.values()) {
                buffer.append( "  " + entry ); //$NON-NLS-1$
                buffer.append( "\n" ); //$NON-NLS-1$
            }
        }

        return buffer.toString();
    }

    /**
     * @return blockid
     */
    public String getBlockID() {

        return blockID;
    }

    /**
     * @param blockID
     */
    public void setBlockID( String blockID ) {

        this.blockID = blockID;
    }

    /**
     * @return recordid
     */
    public String getRecordID() {

        return recordID;
    }

    /**
     * @param recordID
     */
    public void setRecordID( String recordID ) {

        this.recordID = recordID;
    }
}