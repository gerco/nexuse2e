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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.nexuse2e.tools.mapping.csv.RecordEntry.Align;
import org.nexuse2e.tools.mapping.csv.RecordEntry.Trim;

/**
 * @author guido.esch
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CSVLine {

    /**
     * Comment for <code>children</code>
     */
    private List<CSVLine> children = new ArrayList<CSVLine>();
    /**
     * Comment for <code>ref</code>
     */
    public Object         ref;
    /**
     * Comment for <code>columns</code>
     */
    public List<String>   columns  = new ArrayList<String>();
    private String        id;
    private String        source;
    private String        separator;
    private Record        desc;
    private int           siblingSequence;

    public CSVLine() {

        splitStringRelative( null, "," );
    }

    /**
     * @param line
     * @param separator
     */
    public CSVLine( String line, String separator ) {

        splitStringRelative( line, separator );
    }

    /**
     * @param line
     * @param container
     */
    public CSVLine( String line, RecordContainer container ) {

        boolean lineDescFound = false;
        Record record = null;
        for ( Iterator<Record> iter = container.getRecords().values().iterator(); iter.hasNext(); ) {
            record = iter.next();
            if ( record.getRecordValue().length() <= line.length() ) {
                if ( line.startsWith( record.getRecordValue() ) ) {
                    lineDescFound = true;
                    break;
                }
            }
        }
        if ( !lineDescFound || ( record != null && record.getEntries().size() > 0 && !container.getSeparator().equals( "FIXED" ) ) ) {
            splitStringRelative( line, container.getSeparator() );
        } else {
            splitStringAbsolute( line, record );
        }
    }

    /**
     * @param line
     * @param desc
     */
    private void splitStringAbsolute( String line, Record desc ) {

        System.out.println( "desc:" + desc.getRecordID() );
        this.desc = desc;
        separator = null;
        boolean first = true;
        for ( RecordEntry er : desc.getEntries() ) {
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
        desc = null;
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

    /**
     * @param column
     */
    public void addColumn( String column ) {

        if ( columns == null ) {
            columns = new ArrayList<String>();
        }
        columns.add( column );
    }

    /**
     * @param column
     * @param pos
     */
    public void addColumn( String column, int pos ) {

        if ( columns == null ) {
            columns = new ArrayList<String>();
        }
        if ( columns.size() - 1 < pos ) {
            while ( columns.size() - 1 < pos ) {
                columns.add( new String( "" ) ); //$NON-NLS-1$
            }
        }
        columns.set( pos, column );
    }

    /**
     * @param colNum
     * @return col
     */
    public String getColumn( int colNum ) {

        if ( columns == null ) {
            columns = new ArrayList<String>();
            return null;
        }
        if ( colNum > -1 && columns.size() > colNum ) {
            return (String) columns.get( colNum );
        }
        return null;
    }

    /**
     * @return id
     */
    public String getId() {

        return id;
    }

    /**
     * @param id
     */
    public void setId( String id ) {

        this.id = id;
    }

    /**
     * @return columns
     */
    public List<String> getColumns() {

        return columns;
    }

    /**
     * @param columns
     */
    public void setColumns( List<String> columns ) {

        this.columns = columns;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {

        StringBuffer buffer = new StringBuffer();
        
        if ( getSeparator() != null  ) { //&& getDesc() == null
            for ( int i = 0; i < columns.size(); i++ ) {
                String value = (String) columns.get( i );
                RecordEntry entry = (RecordEntry) desc.getEntries().get( i );
                value = callExternalModifier( value, entry );
                
                if ( entry.getTrim() != Trim.FALSE ) {
                    value = value.trim();
                }
                else {
                    value = value.replace( '\n', ' ' );
                    value = value.replace( '\r', ' ' );
                    value = value.replace( '\t', ' ' );
                }
                
                buffer.append( value );
                buffer.append( getSeparator() );
            }
            if ( children != null ) {
                Iterator<CSVLine> i = children.iterator();
                while ( i.hasNext() ) {
                    CSVLine temp = i.next();
                    buffer.append( "\n" ); //$NON-NLS-1$
                    buffer.append( temp );
                }
            }
        }
        if ( getSeparator() == null  ) { //&& getDesc() != null
            for ( int i = 0; i < columns.size(); i++ ) {
                System.out.println( "columns.size()" + columns.size() );
                String colVal = (String) columns.get( i );
                System.out.println( "desc.getEntries().size()" + desc.getEntries().size() );

                RecordEntry entry = (RecordEntry) desc.getEntries().get( i );

                colVal = callExternalModifier( colVal, entry );

                if ( entry.getTrim() != Trim.FALSE ) {
                    colVal = colVal.trim();
                } else {
                    colVal = colVal.replace( '\n', entry.getFiller().charAt( 0 ) );
                    colVal = colVal.replace( '\r', entry.getFiller().charAt( 0 ) );
                    colVal = colVal.replace( '\t', entry.getFiller().charAt( 0 ) );
                }
                if ( colVal.length() > entry.getLength() ) {
                    colVal = colVal.substring( 0, entry.getLength() );
                } else {
                    StringBuffer dump = new StringBuffer();
                    if ( entry.getAlign() == Align.LEFT ) {
                        dump.append( colVal );
                    }
                    for ( int ii = 0; ii < entry.getLength() - colVal.length(); ii++ ) {
                        dump.append( entry.getFiller() );
                    }
                    if ( entry.getAlign() == Align.RIGHT ) {
                        dump.append( colVal );
                    }
                    colVal = dump.toString();
                }

                buffer.append( colVal );
            }
            if ( children != null ) {
                for (CSVLine temp : children) {
                    buffer.append( "\n" ); //$NON-NLS-1$
                    buffer.append( temp );
                }
            }
        }
        return buffer.toString();
    }

    /**
     * @param colVal
     * @param entry
     * @return
     */
    private String callExternalModifier( String colVal, RecordEntry entry ) {

        if ( !StringUtils.isEmpty( entry.getMethod() ) ) {
            if ( !StringUtils.isEmpty( desc.getConversionClass() ) ) {
                try {
                    Class<?> c = Class.forName( desc.getConversionClass() );
                    Object o = c.newInstance();
                    Class<?>[] args = new Class[2];
                    args[0] = String.class;
                    args[1] = RecordEntry.class;
                    Method m;
                    try {
                        m = c.getMethod( entry.getMethod(), args );
                        Object[] argsObjects = new Object[2];
                        argsObjects[0] = colVal;
                        argsObjects[1] = entry;
                        colVal = (String)m.invoke( o, argsObjects );
                    } catch ( NoSuchMethodException e ) {
                        args = new Class[1];
                        args[0] = String.class;
                        try {
                            m = c.getMethod( entry.getMethod(), args );
                            Object[] argsObjects = new Object[1];
                            argsObjects[0] = colVal;
                            colVal = (String)m.invoke( o, argsObjects );
                        } catch ( NoSuchMethodException e1 ) {
                            e1.printStackTrace();
                        }    
                    }
                    
                } catch ( InstantiationException e ) {
                    e.printStackTrace();
                } catch ( IllegalAccessException e ) {
                    e.printStackTrace();
                } catch ( ClassNotFoundException e ) {
                    e.printStackTrace();
                } catch ( SecurityException e ) {
                    e.printStackTrace();
                } catch ( IllegalArgumentException e ) {
                    e.printStackTrace();
                } catch ( InvocationTargetException e ) {
                    e.printStackTrace();
                }

            }

        }
        return colVal;
    }

    /**
     * @return source
     */
    public String getSource() {

        return source;
    }

    /**
     * @param source
     */
    public void setSource( String source ) {

        this.source = source;
    }

    /**
     * @return separator
     */
    public String getSeparator() {

        return separator;
    }

    /**
     * @param separator
     */
    public void setSeparator( String separator ) {

        this.separator = separator;
        if ( source != null ) {
            columns = new ArrayList<String>();
            StringTokenizer st = new StringTokenizer( source, separator );
            if ( st.hasMoreElements() ) {
                String id = (String) st.nextElement();
                setId( id.trim() );
                columns.add( id.trim() );
            } else {
                return;
            }
            while ( st.hasMoreElements() ) {
                String value = (String) st.nextElement();
                columns.add( value.trim() );
            }
        }
    }

    /**
     * @return Returns the desc.
     */
    public Record getDesc() {

        return desc;
    }

    /**
     * @param desc The desc to set.
     */
    public void setDesc( Record desc ) {

        this.desc = desc;
    }

    public int getSiblingSequence() {

        return siblingSequence;
    }

    public void setSiblingSequence( int siblingSequence ) {

        this.siblingSequence = siblingSequence;
    }

    public List<CSVLine> getChildren() {

        return children;
    }

    public void setChildren( List<CSVLine> children ) {

        this.children = children;
    }

    public Object getRef() {

        return ref;
    }

    public void setRef( Object ref ) {

        this.ref = ref;
    }
}