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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.nexuse2e.Engine;
import org.nexuse2e.configuration.ConfigurationAccessService;
import org.nexuse2e.pojo.UserPojo;
import org.nexuse2e.ui.action.NexusE2EAction;
import org.nexuse2e.ui.form.LoginForm;
import org.nexuse2e.util.PasswordUtil;

/**
 * Handles the user login.
 * @author Sebastian Schulze
 * @date 29.12.2006
 */
public class LoginAction extends Action {

    private static Logger LOG = Logger.getLogger( LoginAction.class );

    /* (non-Javadoc)
     * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward execute( ActionMapping actionMapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response ) throws Exception {

        ActionForward forward = actionMapping.findForward( NexusE2EAction.ACTION_FORWARD_FAILURE );
        ActionMessages errors = new ActionErrors();

        if ( form != null ) {
            LoginForm loginForm = (LoginForm) form;
            String user = loginForm.getUser();
            String pass = PasswordUtil.hashPassword( loginForm.getPass() );
            if ( user != null && user.length() > 0 ) {
                ConfigurationAccessService accessService = Engine.getInstance().getActiveConfigurationAccessService();
                UserPojo userInstance = accessService.getUserByLoginName( user );
                if ( userInstance != null && userInstance.getPassword().equals( pass ) ) { // nx_user.password has a "not null" constraint
                    HttpSession session = request.getSession();
                    session.setAttribute( NexusE2EAction.ATTRIBUTE_USER, userInstance );
                    forward = actionMapping.findForward( NexusE2EAction.ACTION_FORWARD_SUCCESS );
                    LOG.info( "Login for \"" + user + "\" successful." );
                } else {
                    ActionMessage errorMessage = new ActionMessage( "login.credentials.wrong" );
                    errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
                    LOG.info( "Login for \"" + user + "\" failed." );
                }
            }

            form.reset( actionMapping, request );
        }

        if ( !errors.isEmpty() ) {
            saveErrors( request, errors );
        }

        return forward;
    }

}
