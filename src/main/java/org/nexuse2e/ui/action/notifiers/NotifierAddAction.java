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
package org.nexuse2e.ui.action.notifiers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.nexuse2e.configuration.ConfigurationUtil;
import org.nexuse2e.configuration.Constants;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.configuration.Constants.ComponentType;
import org.nexuse2e.logging.LogAppender;
import org.nexuse2e.pojo.ComponentPojo;
import org.nexuse2e.pojo.LoggerPojo;
import org.nexuse2e.ui.action.NexusE2EAction;
import org.nexuse2e.ui.form.LoggerForm;

/**
 * @author gesch
 *
 */
public class NotifierAddAction extends NexusE2EAction {

    private static String URL     = "notifier.error.url";
    private static String TIMEOUT = "notifier.error.timeout";

    /* (non-Javadoc)
     * @see com.tamgroup.nexus.e2e.ui.action.NexusE2EAction#executeNexusE2EAction(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.apache.struts.action.ActionMessages)
     */
    @Override
    public ActionForward executeNexusE2EAction( ActionMapping actionMapping, ActionForm actionForm,
            HttpServletRequest request, HttpServletResponse response, EngineConfiguration engineConfiguration, ActionMessages errors, ActionMessages messages )
            throws Exception {

        ActionForward success = actionMapping.findForward( ACTION_FORWARD_SUCCESS );
        ActionForward error = actionMapping.findForward( ACTION_FORWARD_FAILURE );

        // List<LoggerPojo> loggers = null;
        LoggerForm loggerForm = (LoggerForm) actionForm;
        if ( loggerForm.getName() != null && loggerForm.getName().trim().length() > 0 ) {
            LogAppender logger = engineConfiguration.getLogger(
                    loggerForm.getName() );
            if ( logger != null ) {
                loggerForm.setName( "" );
            }
        } else {
            loggerForm.setName( "" );
        }

        List<ComponentPojo> components = engineConfiguration.getComponents(
                ComponentType.LOGGER, Constants.COMPONENTCOMPARATOR );

        if ( components == null || components.size() == 0 ) {
            LOG.trace( "no logger components found" );
            ActionMessage errorMessage = new ActionMessage( "generic.error", "No logger components configured" );
            errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
            addRedirect( request, URL, TIMEOUT );
            return error;
        }

        ComponentPojo componentPojo = null;
        if ( loggerForm.getNxComponentId() == 0 ) {
            componentPojo = components.iterator().next();
        } else {
            componentPojo = engineConfiguration.getComponentByNxComponentId(
                    loggerForm.getNxComponentId() );
        }

        if ( componentPojo != null ) {
            LogAppender logger = null;
            String className = componentPojo.getClassName();
            Object obj = Class.forName( className ).newInstance();
            if ( obj instanceof LogAppender ) {
                logger = (LogAppender) obj;
                loggerForm.setLoggerInstance( logger );
                loggerForm.setParameters( ConfigurationUtil.getConfiguration( logger, new LoggerPojo() ) );
                loggerForm.createParameterMapFromPojos();
            }
        }

        request.setAttribute( ATTRIBUTE_COLLECTION, components );
        request.setAttribute( ATTRIBUTE_SERVICE_COLLECTION, engineConfiguration.getServices( ) );

        return success;
    } // executeNexusE2EAction

} // NotifierDeleteAction
