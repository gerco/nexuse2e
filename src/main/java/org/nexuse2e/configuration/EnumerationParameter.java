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
package org.nexuse2e.configuration;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class represents an <code>Enumeration</code> type configuration
 * parameter.
 * 
 * @author jonas.reese
 */
public class EnumerationParameter {

    private Map<String, String> enumMap;

    /**
     * Constructs a new <code>EnumerationParameter</code> that is initially
     * empty.
     */
    public EnumerationParameter() {

        enumMap = new LinkedHashMap<String, String>();
    }

    /**
     * Puts a name/value pair to this <code>EnumerationParameter</code>.
     * @param name The name.
     * @param value The value.
     */
    public void putElement( String name, String value ) {

        enumMap.put( name, value );
    }

    /**
     * Removes a name/value pair from this <code>EnumerationParameter</code>.
     * @param name The name of the name/value pair to be removed.
     * @return <code>true</code> if the name/value pair has been removed,
     * <code>false</code> if it does not exist and thus could not be removed.
     */
    public boolean removeElement( String name ) {

        return ( enumMap.remove( name ) != null );
    }

    /**
     * Gets an unmodifiable <code>Map</code> of all enumeration elements. 
     * @return An unmodifiable <code>Map</code>.
     */
    public Map<String, String> getElements() {

        return Collections.unmodifiableMap( enumMap );
    }
}
