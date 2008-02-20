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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.nexuse2e.configuration.ConfigurationAccessService;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.pojo.RolePojo;
import org.nexuse2e.pojo.UserPojo;
import org.nexuse2e.ui.action.NexusE2EAction;
import org.nexuse2e.ui.form.UserForm;
import org.nexuse2e.util.PasswordUtil;

/**
 * Saves a User.
 * @author Sebastian Schulze
 * @date 26.01.2007
 */
public class UserSaveAction extends NexusE2EAction {

    /* (non-Javadoc)
     * @see org.nexuse2e.ui.action.NexusE2EAction#executeNexusE2EAction(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.apache.struts.action.ActionMessages, org.apache.struts.action.ActionMessages)
     */
    @Override
    public ActionForward executeNexusE2EAction( ActionMapping actionMapping, ActionForm actionForm,
            HttpServletRequest request, HttpServletResponse response, EngineConfiguration engineConfiguration, ActionMessages errors, ActionMessages messages )
            throws Exception {

        UserForm userForm = (UserForm) actionForm;
        ConfigurationAccessService cas = engineConfiguration;
        // does user exist already?
        UserPojo user = cas.getUserByNxUserId( userForm.getNxUserId() );
        if ( user == null ) {
            // create new user
            user = new UserPojo();
        }
        // login name must be unique
        UserPojo userWithLogin = cas.getUserByLoginName( userForm.getLoginName() );
        if ( userWithLogin != null && userWithLogin.getNxUserId() != user.getNxUserId() ) {
            errors.add( ActionMessages.GLOBAL_MESSAGE, new ActionMessage( "nexususer.error.loginname.unique" ) );
        }

        RolePojo role = cas.getRoleByNxRoleId( userForm.getNxRoleId() );
        if ( role == null ) {
            errors.add( ActionMessages.GLOBAL_MESSAGE, new ActionMessage( "nexususer.error.role.unknown" ) );
        }

        if ( errors.isEmpty() ) {
            user.setRole( role );
            if ( userForm.getNewPassword() != null && !userForm.getNewPassword().equals( userForm.getPassword() ) ) {
                // the from validation ensures that newPassword is set for new users
                // encrypt password
                user.setPassword( PasswordUtil.hashPassword( userForm.getNewPassword() ) );
            }

            user.setFirstName( userForm.getFirstName() );
            user.setLastName( userForm.getLastName() );
            user.setMiddleName( userForm.getMiddleName() );
            user.setLoginName( userForm.getLoginName() );
            //user.setPassword( userForm.getNewPassword() );
            user.setActive( userForm.getActive() );
            // only system users are invisible
            user.setVisible( true );
            Date now = new Date();
            if ( user.getCreatedDate() == null ) {
                user.setCreatedDate( now );
            }
            user.setModifiedDate( now );
            user.setModifiedNxUserId( ( (UserPojo) request.getSession().getAttribute( ATTRIBUTE_USER ) ).getNxUserId() );

            // make changes persistent
            cas.updateUser( user );
            // update form
            userForm.init( user );
        }

        return actionMapping.findForward( ACTION_FORWARD_SUCCESS );
    }


}
