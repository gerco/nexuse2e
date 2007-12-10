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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.nexuse2e.pojo.RolePojo;
import org.nexuse2e.ui.security.AccessController.ParsedRequest;


/**
 * Form for role data.
 * @author Sebastian Schulze
 * @date 28.01.2007
 */
public class RoleForm extends ActionForm {
    
    private static final long serialVersionUID = -8893742272682115403L;

    //  the user instance
    private int    nxRoleId;
    private String name;
    private String description;
    private Map<String,Set<ParsedRequest>> allowedRequests;

    /**
     * @param role the role to set
     */
    public void init( RolePojo role ) {
        
        nxRoleId = role.getNxRoleId();
        name = role.getName();
        description = role.getDescription();
        allowedRequests = role.getAllowedRequests();
    }
    
    public void reset() {

        nxRoleId = 0;
        name = null;
        description = null;
        allowedRequests = new HashMap<String,Set<ParsedRequest>>();
    }
    
    /* (non-Javadoc)
     * @see org.apache.struts.action.ActionForm#validate(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest)
     */
    @Override
    public ActionErrors validate( ActionMapping actionMapping, HttpServletRequest request ) {

        ActionErrors errors = new ActionErrors();
        // name must be not null and not empty
        if ( name == null || name.length() == 0 ) {
            errors.add( ActionMessages.GLOBAL_MESSAGE, new ActionMessage( "nexususer.error.role.name.required" ) );
        }
        
        return errors;
    }

    public String getName() {
        return name;
    }
    
    public void setName( String name ) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription( String description ) {
        this.description = description;
    }

    
    /**
     * @return the nxRoleId
     */
    public int getNxRoleId() {
    
        return nxRoleId;
    }
    
    
    /**
     * @param nxRoleId the nxRoleId to set
     */
    public void setNxRoleId( int nxRoleId ) {
    
        this.nxRoleId = nxRoleId;
    }

    
    /**
     * @return the allowed requests
     */
    public Map<String, Set<ParsedRequest>> getAllowedRequests() {
    
        return allowedRequests;
    }
    
    /**
     * @param allowed requests
     */
    public void setAllowedRequests( Map<String, Set<ParsedRequest>> allowedRequests ) {
    
        this.allowedRequests = allowedRequests;
    }
}
