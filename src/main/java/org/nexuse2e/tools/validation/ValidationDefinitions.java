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

import java.util.ArrayList;
import java.util.List;

/**
 * @author gesch
 *
 */
public class ValidationDefinitions {

    String                             outputEncoding        = null;

    private List<ValidationDefinition> validationDefinitions = new ArrayList<ValidationDefinition>();

    /**
     * @param validationDefinition
     */
    public void addValidationDefinition( ValidationDefinition validationDefinition ) {

        validationDefinitions.add( validationDefinition );
    }

    /**
     * @return the validationDefinitions
     */
    public List<ValidationDefinition> getValidationDefinitions() {

        return validationDefinitions;
    }

    /**
     * @param validationDefinitions the validationDefinitions to set
     */
    public void setValidationDefinitions( List<ValidationDefinition> validationDefinitions ) {

        this.validationDefinitions = validationDefinitions;
    }

    
    public String getOutputEncoding() {
    
        return outputEncoding;
    }

    
    public void setOutputEncoding( String outputEncoding ) {
    
        this.outputEncoding = outputEncoding;
    }
}
