/**
 * NEXUSe2e Business Messaging Open Source  
 * Copyright 2007, Tamgroup and X-ioma GmbH   
 *  
 * This is free software; you can redistribute it and/or modify it  
 * under the terms of the GNU Lesser General Public License as  
 * published by the Free Software Foundation version 2.1 of  
 * the License.  
 *  
 * This software is distributed in the hope that it will be useful,  
 * but WITHOUT ANY WARRANTY; without even the implied warranty of  
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU  
 * Lesser General Public License for more details.  
 *  
 * You should have received a copy of the GNU Lesser General Public  
 * License along with this software; if not, write to the Free  
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.nexuse2e.tools.mapping.csv;

import java.util.HashMap;
import java.util.Map;

/**
 * @author guido.esch
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class RecordContainer {

    private String    separator;
    private String    containerID;
    private Map<String,Record> records;

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
     * @param newRecord
     */
    public void addRecord( Record newRecord ) {

        if ( records == null ) {
            records = new HashMap<String, Record>();
        }
        if ( newRecord != null && newRecord.getRecordID() != null ) {
            records.put( newRecord.getRecordID(), newRecord );
        } else {
            System.out.println( "Error in:" + newRecord ); //$NON-NLS-1$
        }
    }

    /**
     * @param recordID
     * @return record
     */
    public Record getRecordByRecordID( String recordID ) {

        if ( records == null ) {
            records = new HashMap<String, Record>();
            return null;
        }
        return (Record) records.get( recordID );
    }
    
    /**
     * @param value (custom identifier)
     * @return record
     */
    public Record getRecordByValue( String value ) {

        if ( records == null ) {
            records = new HashMap<String, Record>();
            return null;
        }
        for (Record r : records.values()) {
            System.out.println("value:"+r.getRecordValue()); //$NON-NLS-1$
            System.out.println("id:"+r.getRecordID()); //$NON-NLS-1$
            if(r.getRecordValue().equals(value))
            {
                return r;
            }	
        }
        return null;
    }
    /**
     * @return records
     */
    public Map<String,Record> getRecords() {

        return records;
    }

    /**
     * @param records
     */
    public void setRecords( Map<String,Record> records ) {

        this.records = records;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {

        StringBuffer buffer = new StringBuffer();
        buffer.append( "Container:" ); //$NON-NLS-1$
        buffer.append( containerID + "\n" ); //$NON-NLS-1$
        if ( records != null ) {
            for (Record record : records.values()) {
                buffer.append( "  " + record ); //$NON-NLS-1$
                buffer.append( "\n" ); //$NON-NLS-1$
            }
        }
        return buffer.toString();
    }

    /**
     * @return separator
     */
    public String getSeparator() {

        if ( separator == null ) {
            separator = ","; //$NON-NLS-1$
        }
        return separator;
    }

    /**
     * @param separator
     */
    public void setSeparator( String separator ) {

        this.separator = separator;
    }
}