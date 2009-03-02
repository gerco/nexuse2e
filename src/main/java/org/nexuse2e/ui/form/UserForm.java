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
package org.nexuse2e.ui.form;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.nexuse2e.pojo.UserPojo;

/**
 * Form for user data.
 * @author Sebastian Schulze
 * @date 04.01.2007
 */
public class UserForm extends ActionForm {

    private static final long serialVersionUID = -5228133016676845655L;
    
    // the user instance
    private int nxUserId;
    private String password;
    private String newPassword;
    private String passwordRepeat;
    private int    nxRoleId;
    private String firstName;
    private String lastName;
    private String middleName;
    private String loginName;
    private boolean active = true;

    /**
     * @param user the user to set
     */
    public void init( UserPojo user ) {
        
        nxUserId = user.getNxUserId();
        password = user.getPassword();
        newPassword = null;
        passwordRepeat = null;
        nxRoleId = user.getRole().getNxRoleId();
        firstName = user.getFirstName();
        lastName = user.getLastName();
        middleName = user.getMiddleName();
        loginName = user.getLoginName();
        active = user.isActive();
    }
    
    public void reset() {

        nxUserId = 0;
        password = null;
        newPassword = null;
        passwordRepeat = null;
        nxRoleId = 0;
        firstName = null;
        lastName = null ;
        middleName = null;
        loginName = null;
        active = true;
    }
    
    /* (non-Javadoc)
     * @see org.apache.struts.action.ActionForm#validate(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest)
     */
    @Override
    public ActionErrors validate( ActionMapping actionMapping, HttpServletRequest request ) {

        ActionErrors errors = new ActionErrors();
        // login must be not null and not empty
        if ( loginName == null || loginName.length() == 0 ) {
            errors.add( ActionMessages.GLOBAL_MESSAGE, new ActionMessage( "nexususer.error.loginname.required" ) );
        }
        // first name must be not null and not empty
        if ( firstName == null || firstName.length() == 0 ) {
            errors.add( ActionMessages.GLOBAL_MESSAGE, new ActionMessage( "nexususer.error.firstname.required" ) );
        }
        // last name must be not null and not empty
        if ( lastName == null || lastName.length() == 0 ) {
            errors.add( ActionMessages.GLOBAL_MESSAGE, new ActionMessage( "nexususer.error.lastname.required" ) );
        }
        // password must be not null and not empty
        if ( newPassword != null ) {
            // new password must be not empty and match password repeat
            if ( newPassword.length() == 0 ) {
                errors.add( ActionMessages.GLOBAL_MESSAGE, new ActionMessage( "nexususer.error.password.required" ) );
            } else {
                if ( passwordRepeat == null || !newPassword.equals( passwordRepeat ) ) {
                    errors.add( ActionMessages.GLOBAL_MESSAGE, new ActionMessage( "nexususer.error.password.confirmMismatch" ) );
                }
            }
        } else if ( password == null || password.length() == 0 ) {
            errors.add( ActionMessages.GLOBAL_MESSAGE, new ActionMessage( "nexususer.error.password.required" ) );
        }
        // role must be selected
        if( nxRoleId == 0 ) {
            errors.add( ActionMessages.GLOBAL_MESSAGE, new ActionMessage( "nexususer.error.role.required" ) );
        }
        return errors;
    }

    public String getLastName() {
        return lastName;
    }
    
    public void setLastName( String lastName ) {
        this.lastName = lastName;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName( String firstName ) {
        this.firstName = firstName;
    }
    
    public String getMiddleName() {
        return middleName;
    }
    
    public void setMiddleName( String middleName ) {
        this.middleName = middleName;
    }
    
    public String getLoginName() {
        return loginName;
    }
    
    public void setLoginName( String loginName ) {
        this.loginName = loginName;
    }
    
    public String getPassword() {
        return password;
    }
    
    public String getNewPassword() {
        return newPassword;
    }
    
    public void setPassword( String password ) {
        newPassword = password;
    }
    
    public String getPasswordRepeat() {
        return password;
    }
    
    public void setPasswordRepeat( String password ) {
        passwordRepeat = password;
    }
    
    public boolean getActive() {
        return active;
    }
    
    public void setActive( boolean active ) {
        this.active = active;
    }
    
    public int getNxRoleId() {
        return nxRoleId;
    }
    
    public void setNxRoleId( int roleId ) {
        this.nxRoleId = roleId;
    }
    
    public void setNxUserId( int nxUserId ) {
        this.nxUserId = nxUserId;
    }
    
    public int getNxUserId() {
        return nxUserId;
    }
}
