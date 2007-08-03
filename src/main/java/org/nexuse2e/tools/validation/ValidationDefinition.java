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
package org.nexuse2e.tools.validation;

/**
 * @author gesch
 *
 */
public class ValidationDefinition {

    String xpath        = null;
    String command      = null;
    int    fatalCode    = -1;
    int    modifiedCode = -1;
    String defaultValue = null;

    /**
     * @return the xpath
     */
    public String getXpath() {

        return xpath;
    }

    /**
     * @param xpath the xpath to set
     */
    public void setXpath( String xpath ) {

        this.xpath = xpath;
    }

    /**
     * @return the command
     */
    public String getCommand() {

        return command;
    }

    /**
     * @param command the command to set
     */
    public void setCommand( String command ) {

        this.command = command;
    }

    /**
     * @return the fatalCode
     */
    public int getFatalCode() {

        return fatalCode;
    }

    /**
     * @param fatalCode the fatalCode to set
     */
    public void setFatalCode( int fatalCode ) {

        this.fatalCode = fatalCode;
    }

    /**
     * @return the modifiedCode
     */
    public int getModifiedCode() {

        return modifiedCode;
    }

    /**
     * @param modifiedCode the modifiedCode to set
     */
    public void setModifiedCode( int modifiedCode ) {

        this.modifiedCode = modifiedCode;
    }

    
    /**
     * @return the defaultValue
     */
    public String getDefaultValue() {
    
        return defaultValue;
    }

    
    /**
     * @param defaultValue the defaultValue to set
     */
    public void setDefaultValue( String defaultValue ) {
    
        this.defaultValue = defaultValue;
    }

}
