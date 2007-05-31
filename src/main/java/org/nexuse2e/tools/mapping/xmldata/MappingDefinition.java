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

package org.nexuse2e.tools.mapping.xmldata;


/**
 * @author mbreilmann
 *
 */
public class MappingDefinition {
    String  xpath        = null;
    String  staticValue  = null;
    boolean useLeftValue = false;

    public String getStaticValue() {

        return staticValue;
    }

    public void setStaticValue( String staticValue ) {

        this.staticValue = staticValue;
    }

    public boolean isUseLeftValue() {

        return useLeftValue;
    }

    public void setUseLeftValue( boolean useLeftValue ) {

        this.useLeftValue = useLeftValue;
    }

    public String getXpath() {

        return xpath;
    }

    public void setXpath( String xpathValue ) {

        this.xpath = xpathValue;
    }

}
