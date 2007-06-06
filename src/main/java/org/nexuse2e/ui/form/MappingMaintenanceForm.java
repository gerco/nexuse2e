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

import java.util.List;

import org.apache.struts.action.ActionForm;


/**
 * @author gesch
 *
 */
public class MappingMaintenanceForm extends ActionForm {
    /**
     * 
     */
    private static final long serialVersionUID = -4659566397866558364L;
    private String submitaction;
    private String nxMappingId;
    private String leftValue;
    private String rightValue;
    private String category;
    private int leftType;
    private int rightType;
    private List<String> typenames;
    
    /**
     * @return the category
     */
    public String getCategory() {
    
        return category;
    }
    
    /**
     * @param category the category to set
     */
    public void setCategory( String category ) {
    
        this.category = category;
    }
    
    /**
     * @return the leftType
     */
    public int getLeftType() {
    
        return leftType;
    }
    
    /**
     * @param leftType the leftType to set
     */
    public void setLeftType( int leftType ) {
    
        this.leftType = leftType;
    }
    
    /**
     * @return the leftValue
     */
    public String getLeftValue() {
    
        return leftValue;
    }
    
    /**
     * @param leftValue the leftValue to set
     */
    public void setLeftValue( String leftValue ) {
    
        this.leftValue = leftValue;
    }
    
    /**
     * @return the rightType
     */
    public int getRightType() {
    
        return rightType;
    }
    
    /**
     * @param rightType the rightType to set
     */
    public void setRightType( int rightType ) {
    
        this.rightType = rightType;
    }
    
    /**
     * @return the rightValue
     */
    public String getRightValue() {
    
        return rightValue;
    }
    
    /**
     * @param rightValue the rightValue to set
     */
    public void setRightValue( String rightValue ) {
    
        this.rightValue = rightValue;
    }

        
    /**
     * @return the typenames
     */
    public List<String> getTypenames() {
    
        return typenames;
    }

    
    /**
     * @param typenames the typenames to set
     */
    public void setTypenames( List<String> typenames ) {
    
        this.typenames = typenames;
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
    public String getNxMappingId() {
    
        return nxMappingId;
    }

    
    /**
     * @param nxMappingId the nxMappingId to set
     */
    public void setNxMappingId( String nxMappingId ) {
    
        this.nxMappingId = nxMappingId;
    }
    
    
}
