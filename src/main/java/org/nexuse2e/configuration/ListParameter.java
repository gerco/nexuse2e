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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class represents a <code>List</code> type configuration
 * parameter.
 * 
 * @author jonas.reese
 */
public class ListParameter implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<ListElement>     list;
    private int                   selectedIndex;

    /**
     * Constructs a new <code>DropdownParameter</code> that is initially
     * empty.
     */
    public ListParameter() {

        list = new ArrayList<ListElement>();
    }

    /**
     * Adds a name/value pair to this <code>EnumerationParameter</code>.
     * There is no check being performed if the name already exists.
     * @param label The (human-readable) short label.
     * @param value The value.
     */
    public void addElement( String label, String value ) {

        ListElement element = new ListElement();
        element.label = label;
        element.value = value;
        list.add( element );
    }
    
    /**
     * Gets the element for the given value.
     * @param value The value to get the element for.
     * @return The according <code>ListElement</code>, or <code>null</code>
     * if none exists for the given value.
     */
    public ListElement getElement( String value ) {
        for (ListElement element : list) {
            if (element.value != null && element.value.equals( value )) {
                return element;
            }
        }
        return null;
    }

    /**
     * Gets an unmodifiable <code>List</code> of all dropdown values. 
     * @return An unmodifiable <code>List</code>.
     */
    public List<ListElement> getElements() {

        return Collections.unmodifiableList( list );
    }

    /**
     * Gets the selected index.
     * @return The selectedIndex.
     */
    public int getSelectedIndex() {

        return selectedIndex;
    }

    /**
     * Sets the selected index.
     * @param selectedIndex The index to select.
     */
    public void setSelectedIndex( int selectedIndex ) {

        this.selectedIndex = selectedIndex;
    }

    /**
     * Sets the selected value.
     * @param value The value to be selected. If it does not exist,
     * this method has no effect.
     */
    public boolean setSelectedValue( String value ) {

        if ( value == null ) {
            return false;
        }
        int index = 0;
        for ( ListElement element : list ) {
            if ( value.equals( element.getValue() ) ) {
                setSelectedIndex( index );
                return true;
            }
            index++;
        }
        return false;
    }

    /**
     * Gets the value that is currently selected.
     * @return The selected value, or <code>null</code> if no value is selected.
     */
    public String getSelectedValue() {

        if ( selectedIndex >= list.size() || selectedIndex < 0 ) {
            return null;
        }
        return list.get( selectedIndex ).value;
    }

    @Override
    public String toString() {

        return super.toString() + " selectedValue=" + getSelectedValue();
    }

    /**
     * Represents a single dropdown element.
     * @author jonas.reese
     */
    public static class ListElement implements Serializable {

        private static final long serialVersionUID = 1L;
        
        private String label;
        private String value;

        /**
         * Gets this <code>EnumerationElement</code>'s label.
         * @return The (human-readable) short label.
         */
        public String getLabel() {

            return label;
        }

        /**
         * Gets this <code>EnumerationElement</code>'s value
         * as a <code>String</code>.
         * @return the value
         */
        public String getValue() {

            return value;
        }
    }
}
