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

package org.nexuse2e.tools.mapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.nexuse2e.NexusException;
import org.nexuse2e.tools.mapping.csv.Record;
import org.nexuse2e.tools.mapping.csv.RecordContainer;
import org.nexuse2e.tools.mapping.csv.RecordEntry;

/**
 * Class to represent a record/line of data in a flat file, broken out into single fields based on
 * a record description.
 *
 * @author mbreilmann
 */
public class FlatFileRecord {

    private List<String>             columns     = new ArrayList<String>();
    private String                   id;
    private String                   source;
    private String                   separator;
    private Record                   recordFormat;
    private HashMap<String, Integer> columnNames = new HashMap<String, Integer>();

    public FlatFileRecord() {

        splitStringRelative( null, "," );
    }

    /**
     * @param line
     * @param separator
     */
    public FlatFileRecord( String line, String separator ) {

        splitStringRelative( line, separator );
    }

    /**
     * @param line
     * @param container
     */
    public FlatFileRecord( String line, RecordContainer container ) throws NexusException {

        boolean lineDescFound = false;
        Record record = null;
        if ( container.isSingleFormat() ) {
            record = container.getSingleFormatRecord();
        } else {
            for ( Iterator<Record> iter = container.getRecords().values().iterator(); iter.hasNext(); ) {
                record = iter.next();
                if ( record.getRecordValue().length() <= line.length() ) {
                    if ( line.startsWith( record.getRecordValue() ) ) {
                        lineDescFound = true;
                        break;
                    }
                }
            }
        }
        if ( record != null ) {
            int i = 0;
            for ( RecordEntry recordEntry : record.getEntries() ) {
                columnNames.put( recordEntry.getEntryID(), new Integer( i++ ) );
            }
        }
        if ( !lineDescFound
                || ( record != null && record.getEntries().size() > 0 && !container.getSeparator().equals( "FIXED" ) ) ) {
            splitStringRelative( line, container.getSeparator() );
        } else {
            splitStringAbsolute( line, record );
        }
    }

    public String getColumn( String columnId, String defaultValue ) {

        Integer index = columnNames.get( columnId );
        if ( index != null && ( index.intValue() < columns.size() ) ) {
            return columns.get( index.intValue() );
        }
        return defaultValue;
    }
    
    public void setColumn( String columnId, String value ) {
        Integer index = columnNames.get( columnId );
        if ( index != null && ( index.intValue() < columns.size() ) ) {
            columns.set( index.intValue(), value );
        }
    }

    /**
     * @param line
     * @param desc
     */
    private void splitStringAbsolute( String line, Record recordFormat ) {

        // System.out.println( "desc:" + desc.getRecordID() );
        this.recordFormat = recordFormat;
        separator = null;
        boolean first = true;
        for ( RecordEntry er : recordFormat.getEntries() ) {
            if ( line.length() >= er.getPosition() + er.getLength() ) {
                String value = line.substring( er.getPosition(), er.getPosition() + er.getLength() );
                columns.add( value.trim() );
                if ( first ) {
                    setId( value );
                }
            }
            first = false;
        }
    }

    /**
     * @param line
     * @param separator
     */
    private void splitStringRelative( String line, String separator ) {

        this.separator = separator;
        recordFormat = null;
        if ( separator == null ) {
            separator = ","; //$NON-NLS-1$
        }
        if ( separator.equals( "[tab]" ) ) {
            separator = "\t";
        }

        setSeparator( separator );

        if ( separator.equals( "|" ) ) {
            separator = "\\0174";
        }
        source = line;
        if ( line != null ) {

            String[] result = line.split( separator );

            for ( int x = 0; x < result.length; x++ ) {
                if ( x == 0 ) {
                    String id = result[x];
                    setId( id.trim() );
                    columns.add( id.trim() );
                } else {

                    String value = result[x];
                    columns.add( value.trim() );
                }
            }
        }
    }

    public List<String> getColumns() {

        return columns;
    }

    public void setColumns( List<String> columns ) {

        this.columns = columns;
    }

    public String getId() {

        return id;
    }

    public void setId( String id ) {

        this.id = id;
    }

    public String getSource() {

        return source;
    }

    public void setSource( String source ) {

        this.source = source;
    }

    public String getSeparator() {

        return separator;
    }

    public void setSeparator( String separator ) {

        this.separator = separator;
    }

    public Record getRecordFormat() {

        return recordFormat;
    }

    public void setRecordFormat( Record desc ) {

        this.recordFormat = desc;
    }

    public String toString() {

        StringBuffer result = new StringBuffer();
        
        for ( String value : columns ) {
            result.append( "'" + value + "' " );
        }

        return result.toString();
    }

} // FlatFileRecord