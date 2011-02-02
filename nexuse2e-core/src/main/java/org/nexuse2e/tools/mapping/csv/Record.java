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
package org.nexuse2e.tools.mapping.csv;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author guido.esch
 */
public class Record {

    private List<RecordEntry> entries;
    private String            recordID;
    private String            value;
    private String            conversionClass;
    private boolean           active;

    /**
     * @param entry
     */
    public void addEntry( RecordEntry entry ) {

        if ( entries == null ) {
            entries = new ArrayList<RecordEntry>();
        }
        entries.add( entry );
    }

    public int getColumnNum( RecordEntry entry ) {

        if ( !entries.contains( entry ) ) {
            return -1;
        }
        for ( int i = 0; i < entries.size(); i++ ) {
            if ( entry == entries.get( i ) ) {
                return i;
            }
        }
        return -1;
    }

    /**
     * @param entryID
     * @return entry
     */
    public RecordEntry getEntry( String entryID ) {

        Iterator<RecordEntry> i = entries.iterator();
        while ( i.hasNext() ) {
            RecordEntry tempEntry = i.next();
            if ( tempEntry.getEntryID().equals( entryID ) ) {
                return tempEntry;
            }
        }
        return null;
    }

    /**
     * @return entries
     */
    public List<RecordEntry> getEntries() {

        if ( entries == null ) {
            entries = new ArrayList<RecordEntry>();
        }
        return entries;
    }

    /**
     * @param entries
     */
    public void setEntries( List<RecordEntry> entries ) {

        this.entries = entries;
    }

    /**
     * @return id
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

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {

        StringBuffer buffer = new StringBuffer();
        buffer.append( "Record:" ); //$NON-NLS-1$
        buffer.append( recordID + "\n" ); //$NON-NLS-1$
        if ( entries != null ) {
            Iterator<RecordEntry> i = entries.iterator();
            while ( i.hasNext() ) {
                RecordEntry entry = i.next();
                buffer.append( "  " + entry ); //$NON-NLS-1$
                buffer.append( "\n" ); //$NON-NLS-1$
            }
        }

        return buffer.toString();
    }

    /**
     * @param nodeValue
     */
    public void setRecordValue( String value ) {

        this.value = value;
    }

    /**
     * @return Returns the value.
     */
    public String getRecordValue() {

        return value;
    }

    /**
     * @return Returns the active.
     */
    public boolean isActive() {

        return active;
    }

    /**
     * @param active The active to set.
     */
    public void setActive( boolean active ) {

        this.active = active;
    }

    /**
     * @return the conversationClass
     */
    public String getConversionClass() {

        return conversionClass;
    }

    /**
     * @param conversationClass the conversationClass to set
     */
    public void setConversionClass( String conversationClass ) {

        this.conversionClass = conversationClass;
    }
}