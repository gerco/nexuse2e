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
package org.nexuse2e.ui.form;

import org.apache.struts.action.ActionForm;


/**
 * UI form for persistent properties.
 * 
 * @author jreese
 */
public class PersistentPropertiesForm extends ActionForm {

    private static final long serialVersionUID = -4659566397866558364L;

    private String submitaction;
    private String nxPersistentPropertyId;
    private String namespace;
    private String name;
    private String version;
    private String value;
    
    /**
     * @return the category
     */
    public String getValue() {
    
        return value;
    }
    
    /**
     * @param category the category to set
     */
    public void setValue( String category ) {
    
        this.value = category;
    }
    
    /**
     * @return the leftValue
     */
    public String getNamespace() {
    
        return namespace;
    }
    
    /**
     * @param leftValue the leftValue to set
     */
    public void setNamespace( String leftValue ) {
    
        this.namespace = leftValue;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the rightValue
     */
    public String getVersion() {
    
        return version;
    }
    
    /**
     * @param rightValue the rightValue to set
     */
    public void setVersion( String rightValue ) {
    
        this.version = rightValue;
    }

        
    /**
     * @return the submitaction
     */
    public String getSubmitaction() {
    
        return submitaction;
    }

    
    /**
     * @param submitaction the submitaction to set
     */
    public void setSubmitaction( String submitaction ) {
    
        this.submitaction = submitaction;
    }

    
    /**
     * @return the nxMappingId
     */
    public String getNxPersistentPropertyId() {
    
        return nxPersistentPropertyId;
    }

    
    /**
     * @param nxMappingId the nxMappingId to set
     */
    public void setNxPersistentPropertyId( String nxMappingId ) {
    
        this.nxPersistentPropertyId = nxMappingId;
    }
    
    
}
