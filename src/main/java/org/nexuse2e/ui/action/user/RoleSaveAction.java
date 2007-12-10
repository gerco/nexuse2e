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
package org.nexuse2e.ui.action.user;

import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.nexuse2e.Engine;
import org.nexuse2e.configuration.ConfigurationAccessService;
import org.nexuse2e.pojo.GrantPojo;
import org.nexuse2e.pojo.RolePojo;
import org.nexuse2e.pojo.UserPojo;
import org.nexuse2e.ui.action.NexusE2EAction;
import org.nexuse2e.ui.form.RoleForm;


/**
 * @author Sebastian Schulze
 * @date 29.01.2007
 */
public class RoleSaveAction extends NexusE2EAction {

    private static final String PARAMETER_NAME_GRANT_PREFIX = "__grant:";
    
    /* (non-Javadoc)
     * @see org.nexuse2e.ui.action.NexusE2EAction#executeNexusE2EAction(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.apache.struts.action.ActionMessages, org.apache.struts.action.ActionMessages)
     */
    @SuppressWarnings("unchecked")
    @Override
    public ActionForward executeNexusE2EAction( ActionMapping actionMapping, ActionForm actionForm,
            HttpServletRequest request, HttpServletResponse response, ActionMessages errors, ActionMessages messages )
            throws Exception {

        RoleForm roleForm = (RoleForm) actionForm;
        ConfigurationAccessService cas = Engine.getInstance().getActiveConfigurationAccessService();
        // does role exist already?
        RolePojo role = cas.getRoleByNxRoleId( roleForm.getNxRoleId() );
        if ( role == null ) {
            // create new role
            role = new RolePojo();
        }
        // name must be unique
        RolePojo roleWithName = cas.getRoleByName( roleForm.getName() );
        if( roleWithName != null && roleWithName.getNxRoleId() != role.getNxRoleId() ) {
            errors.add( ActionMessages.GLOBAL_MESSAGE, new ActionMessage( "nexususer.error.role.name.unique" ) );
        }
        
        if ( errors.isEmpty() ) {
            // current date
            Date now = new Date();
            int modifier = ( (UserPojo) request.getSession().getAttribute( ATTRIBUTE_USER ) ).getNxUserId();
            
            role.setName( roleForm.getName() );
            role.setDescription( roleForm.getDescription() );
            // update grants
            Map<String,GrantPojo> oldGrants = new HashMap<String,GrantPojo>( role.getGrants() );
            role.getGrants().clear();
            Enumeration<String> paramEnum = request.getParameterNames();
            while ( paramEnum.hasMoreElements() ) {
                String paramName = (String) paramEnum.nextElement();
                if ( paramName != null && paramName.startsWith( PARAMETER_NAME_GRANT_PREFIX ) ) {
                    String target = paramName.substring( PARAMETER_NAME_GRANT_PREFIX.length() );
                    // check if grant already exists; grants are identified by the action string only
                    if ( oldGrants.containsKey( target ) ) {
                        role.getGrants().put( target, oldGrants.get( target ) );
                    } else {
                        GrantPojo newGrant = new GrantPojo( target, now, now, modifier );
                        role.getGrants().put( target, newGrant );
                    }
                }
            }
            //role.setGrants( newGrants );
            
            if ( role.getCreatedDate() == null ) {
                role.setCreatedDate( now );
            }
            role.setModifiedDate( now );
            role.setModifiedNxUserId( modifier );
            
            // make changes persistent
            cas.updateRole( role );
            // update form
            roleForm.init( role );
        }
        
        return actionMapping.findForward( ACTION_FORWARD_SUCCESS );
    }

}
