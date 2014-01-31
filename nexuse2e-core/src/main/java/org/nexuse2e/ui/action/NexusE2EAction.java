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
package org.nexuse2e.ui.action;

import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.nexuse2e.Engine;
import org.nexuse2e.Version;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.pojo.UserPojo;
import org.nexuse2e.ui.security.AccessController;

/**
 * Abstract Struts action that all NEXUSe2e actions need to sub class. This action redirects 
 * to the login page in case the user has not logged in.
 *
 * @author markus.breilmann
 */
public abstract class NexusE2EAction extends Action {

    protected static Logger       LOG                          = Logger.getLogger( NexusE2EAction.class );

    // Action forwards
    public final static String    ACTION_FORWARD_SUCCESS       = "success";
    public final static String    ACTION_FORWARD_FAILURE       = "error";
    public final static String    ACTION_FORWARD_LOGIN         = "login";
    public final static String    ACTION_FORWARD_ACCESS_DENIED = "accessDenied";

    // Attributes for JSPs
    public static final String    ATTRIBUTE_TREE_NODES         = "treeNodes";
    public static final String    ATTRIBUTE_WEB_APP_PATH       = "webAppPath";
    public static final String    ATTRIBUTE_USER               = "nxUser";
    public static final String    ATTRIBUTE_CONFIGURATION      = "engineConfiguration";

    public static final String    ATTRIBUTE_COLLECTION         = "collection";

    public static final String    ATTRIBUTE_SERVICE_COLLECTION = "service_collection";

    public static final String    REFRESH_TREE                 = "refreshTree";

    public static final String    MESSAGES                     = "nexus_messages";

    public static final String    NEXUSE2E_VERSION             = "NEXUSe2e_version";
    public static final String    JAVA_VERSION                 = "java_version";
    public static final String    JAVA_HOME                    = "java_home";
    public static final String    JAVA_CLASSPATH               = "java_classpath";
    public static final String    SERVICE_UPTIME               = "service_uptime";
    public static final String    ENGINE_UPTIME                = "engine_uptime";
    
    public static final String    INSTANCES                    = "instances";
    public static final String    DESCRIPTION                  = "description";
    

    protected static final String SUBMIT_BUTTON                = "Submit";

    public static final String    MSG_KEY_GENERIC_ERROR        = "generic.error";

    /* (non-Javadoc)
     * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public final ActionForward execute( ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest request,
            HttpServletResponse response ) throws Exception {

        response.setContentType( "text/html" );
        //        response.setHeader( "pragma", "no-cache" );
        //        response.setHeader( "cache-control", "no-cache" );
        //        response.setHeader( "Cache-Control", "private" );
        //        response.setDateHeader( "expires", 0 );

        ActionMessages errors = new ActionErrors();
        ActionMessages messages = new ActionMessages();

        // login
        HttpSession session = request.getSession();
        // if user can be found in the session context the user is logged in
        UserPojo user = (UserPojo) session.getAttribute( ATTRIBUTE_USER );
        ActionForward actionForward = null;

        // Set version information to it is accessible by all JSPs
        request.setAttribute( NEXUSE2E_VERSION, Version.getVersion() );
        request.setAttribute( JAVA_VERSION, System.getProperty( "java.version" ) );
        request.setAttribute( JAVA_CLASSPATH, System.getProperty( "java.class.path" ) );
        request.setAttribute( JAVA_HOME, System.getProperty( "java.home" ) );

        request.setAttribute( SERVICE_UPTIME, "n/d" );
        request.setAttribute( ENGINE_UPTIME, "n/d" );

        try {
            long serviceUptime = System.currentTimeMillis() - Engine.getInstance().getServiceStartTime();
            long engineUptime = System.currentTimeMillis() - Engine.getInstance().getEngineStartTime();
        
            request.setAttribute( SERVICE_UPTIME, formatUptime(serviceUptime) );
            request.setAttribute( ENGINE_UPTIME, formatUptime(engineUptime) );
        } catch ( Exception e ) {
            e.printStackTrace();
        }

        
        
        if(Engine.getInstance().getEngineController().getAdvancedController() != null) {
        	request.setAttribute(INSTANCES, Engine.getInstance().getEngineController().getAdvancedController().getInstances());
        	request.setAttribute(DESCRIPTION, Engine.getInstance().getEngineController().getAdvancedController().getDescription());
        }
        
        
        if ( user != null ) {
            // check access
            if ( AccessController.hasAccess( user, request ) ) {
                // execute action
                EngineConfiguration config = Engine.getInstance().getConfiguration( user.getNxUserId() );
                request.setAttribute( ATTRIBUTE_CONFIGURATION, config );
                try {
                    actionForward = executeNexusE2EAction(
                            actionMapping, actionForm, request, response, config, errors, messages );
                } catch (Exception ex) {
                    LOG.error( "Caught exception in Action", ex );
                    ex.printStackTrace();
                    String message = ex.getMessage();
                    if (message == null) {
                        message = ex.getClass().getName();
                    }
                    messages.add( ActionMessages.GLOBAL_MESSAGE, new ActionMessage( "generic.error", message ) );
                }
            } else {
                errors.add( ActionMessages.GLOBAL_MESSAGE, new ActionMessage( "access.denied" ) );
                actionForward = actionMapping.findForward( ACTION_FORWARD_ACCESS_DENIED );
            }
        } else {
            actionForward = actionMapping.findForward( ACTION_FORWARD_LOGIN );
        }

        if ( !errors.isEmpty() ) {
            saveErrors( request, errors );
        }
        if ( !messages.isEmpty() ) {
            saveMessages( request, messages );
        }

        return actionForward;
    } // execute

    private String formatUptime( long serviceUptime ) {

    	
		long dayLength = 1000*60*60*24;
		long hourlength = 1000*60*60;
		long minutelength = 1000*60;
		long secondlength = 1000;
		 
		 
		long days = serviceUptime/dayLength;
		long hours = (serviceUptime-(days*dayLength))/hourlength;
		long minutes = (serviceUptime-(days*dayLength) - (hours*hourlength))/minutelength;
		long seconds = (serviceUptime-(days*dayLength) - (hours*hourlength) - (minutes*minutelength))/secondlength;

        
        return days+ " days " +hours+" hours "+minutes+" minutes "+seconds+" seconds";
    }

    public void addRedirect( HttpServletRequest request, String urlCode, String timeCode ) {

        int time = Integer.parseInt( getResources( request ).getMessage( timeCode ) );
        String url = getResources( request ).getMessage( urlCode );
        request.setAttribute( "redirectTimeout", "" + time );
        request.setAttribute( "redirectUrl", url );
    }

    public void addRedirect( HttpServletRequest request, URL url, String timeCode ) {

        int time = Integer.parseInt( getResources( request ).getMessage( timeCode ) );
        request.setAttribute( "redirectTimeout", "" + time );
        request.setAttribute( "redirectUrl", url.toString() );
    }

    public abstract ActionForward executeNexusE2EAction(
            ActionMapping actionMapping,
            ActionForm actionForm,
            HttpServletRequest request,
            HttpServletResponse response,
            EngineConfiguration engineConfiguration,
            ActionMessages errors,
            ActionMessages messages )
            throws Exception;

} // NexusE2EAction
