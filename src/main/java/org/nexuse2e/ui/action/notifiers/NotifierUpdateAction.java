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
import org.nexuse2e.configuration.Constants;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.configuration.Constants.ComponentType;
import org.nexuse2e.pojo.ComponentPojo;
import org.nexuse2e.pojo.LoggerParamPojo;
import org.nexuse2e.pojo.LoggerPojo;
import org.nexuse2e.ui.action.NexusE2EAction;
import org.nexuse2e.ui.form.LoggerForm;

/**
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 *
 * @author markus.breilmann
 */
public class NotifierUpdateAction extends NexusE2EAction {

    private static String URL     = "notifier.error.url";
    private static String TIMEOUT = "notifier.error.timeout";

    /* (non-Javadoc)
     * @see com.tamgroup.nexus.e2e.ui.action.NexusE2EAction#executeNexusE2EAction(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.apache.struts.action.ActionMessages)
     */
    @Override
    public ActionForward executeNexusE2EAction( ActionMapping actionMapping, ActionForm actionForm,
            HttpServletRequest request, HttpServletResponse response, EngineConfiguration engineConfiguration, ActionMessages errors, ActionMessages messages )
            throws Exception {

        ActionForward refresh = actionMapping.findForward( "refresh" );
        ActionForward update = actionMapping.findForward( "update" );
        ActionForward error = actionMapping.findForward( "error" );

        LoggerForm form = (LoggerForm) actionForm;

        List<ComponentPojo> components = engineConfiguration.getComponents(
                ComponentType.LOGGER, Constants.COMPONENTCOMPARATOR );
        if ( components == null || components.size() == 0 ) {
            LOG.trace( "no service components found" );
            ActionMessage errorMessage = new ActionMessage( "generic.error", "No service components configured" );
            errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
            addRedirect( request, URL, TIMEOUT );
            return error;
        }
        request.setAttribute( ATTRIBUTE_COLLECTION, components );
        request.setAttribute( ATTRIBUTE_SERVICE_COLLECTION, engineConfiguration.getServices( ) );

        LoggerPojo originalLogger = engineConfiguration.getLoggerByNxLoggerId(
                form.getNxLoggerId() );

        ComponentPojo component = null;

        if ( originalLogger == null ) {
            if ( form.getNxLoggerId() != 0 ) {
                ActionMessage errorMessage = new ActionMessage( "generic.error",
                        "Logger does not exist any more. Maybe it has been deleted in the meantime." );
                errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
                addRedirect( request, URL, TIMEOUT );
                return error;
            }
        } else {
            if ( form.getNxComponentId() == 0 ) {
                if ( originalLogger == null ) {
                    component = components.iterator().next();
                } else {
                    component = originalLogger.getComponent();
                }
            }
        }
        if ( component == null ) {
            component = engineConfiguration.getComponentByNxComponentId(
                    form.getNxComponentId() );
        }

        if ( component == null ) {
            System.err.println( "Error: could not find ComponentPojo with nxComponentId " + form.getNxComponentId() );
            return error;
        }

        if ( form.getSubmitted() != null && form.getSubmitted().equals( "true" ) ) {
            LOG.trace( "update" );
            form.setSubmitted( "false" );

            if ( form.getName() == null ) {
                return error;
            }

            if ( originalLogger == null ) {
                originalLogger = new LoggerPojo();
                originalLogger.setName( form.getName() );
                originalLogger.setComponent( component );
            }
            StringBuffer filter = new StringBuffer();
            for ( String group : form.getLogFilterValues().keySet() ) {
                LOG.trace( "group:" + group + "  -  " + form.getLogFilterValue( group ) );
                filter.append( group );
                filter.append( "," );
            }
            filter.append( form.getFilterJavaPackagePattern() );
            if ( filter.length() == 0 ) {
                filter.append( "core" );
            }
            originalLogger.setFilter( filter.toString() );

            form.fillPojosFromParameterMap();
            List<LoggerParamPojo> list = form.getParameters();
            for ( LoggerParamPojo param : list ) {
                param.setLogger( originalLogger );
            }

            if ( !originalLogger.getName().equals( form.getName() ) ) {
                engineConfiguration.renameLogger( originalLogger.getName(),
                        form.getName() );
                originalLogger.setName( form.getName() );
            }
            originalLogger.setLoggerParams( list );

            originalLogger.setThreshold( form.getThreshold() );

            engineConfiguration.updateLogger( originalLogger );
            form.setLoggerInstance( engineConfiguration.getLogger(
                    originalLogger.getName() ) );

            return update;
        }

        return refresh;
    } // executeNexusE2EAction

} // NotifierViewAction
