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

/**
 * @author gesch
 *
 */
public class ParameterTemplate {

    private String  paramName;
    private String  label;
    private String  value;
    private int     sequenceNumber;
    private boolean required = true;
    private String  description;
    private String  defaultvalue;

    /**
     * Hint for user friendly configuration screen rendering. (sort ascending)
     */
    private int     position;
    /**
     * 0 = unknown
     * 1 = string
     * 2 = password
     * 3 = enumeration (Same paramName, but unlimited Labels, separated by sequencenumber)
     * 4 = dropdown (predefined values, used as dropdown)
     * 
     */
    private int     type     = 0;

    /**
     * @param paramName
     * @param label
     * @param value
     * @param sequenceNumber
     * @param required
     * @param description
     * @param defaultvalue
     * @param type
     */
    public ParameterTemplate( String paramName, String label, String value, int sequenceNumber, boolean required,
            String description, String defaultvalue, int type, int position ) {

        this.paramName = paramName;
        this.label = label;
        this.value = value;
        this.sequenceNumber = sequenceNumber;
        this.required = required;
        this.description = description;
        this.defaultvalue = defaultvalue;
        this.type = type;
        this.position = position;
    }

    /**
     * @return the defaultvalue
     */
    public String getDefaultvalue() {

        return defaultvalue;
    }

    /**
     * @param defaultvalue the defaultvalue to set
     */
    public void setDefaultvalue( String defaultvalue ) {

        this.defaultvalue = defaultvalue;
    }

    /**
     * @return the description
     */
    public String getDescription() {

        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription( String description ) {

        this.description = description;
    }

    /**
     * @return the label
     */
    public String getLabel() {

        return label;
    }

    /**
     * @param label the label to set
     */
    public void setLabel( String label ) {

        this.label = label;
    }

    /**
     * @return the paramName
     */
    public String getParamName() {

        return paramName;
    }

    /**
     * @param paramName the paramName to set
     */
    public void setParamName( String paramName ) {

        this.paramName = paramName;
    }

    /**
     * @return the required
     */
    public boolean isRequired() {

        return required;
    }

    /**
     * @param required the required to set
     */
    public void setRequired( boolean required ) {

        this.required = required;
    }

    /**
     * @return the sequenceNumber
     */
    public int getSequenceNumber() {

        return sequenceNumber;
    }

    /**
     * @param sequenceNumber the sequenceNumber to set
     */
    public void setSequenceNumber( int sequenceNumber ) {

        this.sequenceNumber = sequenceNumber;
    }

    /**
     * @return the value
     */
    public String getValue() {

        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue( String value ) {

        this.value = value;
    }

    /**
     * @return the type
     */
    public int getType() {

        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType( int type ) {

        this.type = type;
    }

    /**
     * @return the position
     */
    public int getPosition() {

        return position;
    }

    /**
     * @param position the position to set
     */
    public void setPosition( int position ) {

        this.position = position;
    }
}
