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
package org.nexuse2e.configuration;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.Date;
import java.util.StringTokenizer;

import org.apache.commons.beanutils.PropertyUtils;

/**
 * @author gesch
 *
 */
public class GenericComparator<T> implements Comparator<T>, Serializable {

    private static final long serialVersionUID = 1L;

    private boolean  ascending;
    private String[] fieldname;

    /**
     * @deprecated Use other constructor.
     * @param clazz
     * @param fieldname
     * @param ascending
     */
    public GenericComparator( Class<T> clazz, String fieldname, boolean ascending ) {
        this( fieldname, ascending );
    }

    /**
     * Constructs a <code>GenericComparator</code>
     * @param fieldname The bean-style property name.
     * @param ascending Ascending/descending flag.
     */
    public GenericComparator( String fieldname, boolean ascending ) {

        this.ascending = ascending;
        StringTokenizer st = new StringTokenizer( fieldname, ";" );
        ;
        this.fieldname = new String[st.countTokens()];
        int counter = 0;
        while ( st.hasMoreTokens() ) {
            this.fieldname[counter] = st.nextToken();
            counter++;
        }
    }

    /* (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare( T o1, T o2 ) {

        int result = 0;

        try {
            Object result1 = null;
            Object result2 = null;

            for ( int i = 0; i < fieldname.length; i++ ) {

                result1 = PropertyUtils.getProperty( o1, fieldname[i] );
                result2 = PropertyUtils.getProperty( o2, fieldname[i] );
                
                if(result1 == null) {
                    result1 = "";
                }
                if(result2 == null) {
                    result2 = "";
                }
                
                if ( result1 instanceof String && result2 instanceof String ) {
                    result = compareStrings( result1, result2 );

                } else if ( result1 instanceof Integer && result2 instanceof Integer ) {
                    result = compareInts( result1, result2 );

                } else if ( result1 instanceof Date && result2 instanceof Date ) {
                    result = compareDates( result1, result2 );

                } else {
                    throw new ClassCastException( result1.getClass().getName() + " is not a valid field type!" );
                }
                if ( result != 0 ) {
                    break;
                }
            }

        } catch ( NoSuchMethodException e ) {
            throw new ClassCastException( e.getMessage() );
        } catch ( IllegalArgumentException e ) {
            throw new ClassCastException( e.getMessage() );
        } catch ( IllegalAccessException e ) {
            throw new ClassCastException( e.getMessage() );
        } catch ( InvocationTargetException e ) {
            throw new ClassCastException( e.getMessage() );
        }
        if ( !ascending ) {
            result = result * -1;
        }
        return result;
    }

    /**
     * @param result1
     * @param result2
     * @return
     */
    private int compareDates( Object result1, Object result2 ) {

        if ( ( (Date) result1 ).getTime() == ( (Date) result2 ).getTime() ) {
            return 0;
        } else if ( ( (Date) result1 ).getTime() < ( (Date) result2 ).getTime() ) {
            return -1;
        } else {
            return 1;
        }
    }

    /**
     * @param result1
     * @param result2
     * @return
     */
    private int compareInts( Object result1, Object result2 ) {

        if ( ( (Integer) result1 ).intValue() == ( (Integer) result2 ).intValue() ) {
            return 0;
        } else if ( ( (Integer) result1 ).intValue() < ( (Integer) result2 ).intValue() ) {
            return -1;
        } else {
            return 1;
        }
    }

    /**
     * @param result1
     * @param result2
     * @return
     */
    private int compareStrings( Object result1, Object result2 ) {

        int result;
        result = ( (String) result1 ).toLowerCase().compareTo( ( (String) result2 ).toLowerCase() );
        if ( result == 0 ) {
            result = -( (String) result1 ).compareTo( (String) result2 );
        }
        return result;
    }

}
