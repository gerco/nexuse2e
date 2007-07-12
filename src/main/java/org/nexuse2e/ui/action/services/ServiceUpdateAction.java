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
package org.nexuse2e.ui.action.services;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.nexuse2e.Engine;
import org.nexuse2e.configuration.Constants;
import org.nexuse2e.configuration.Constants.ComponentType;
import org.nexuse2e.pojo.ComponentPojo;
import org.nexuse2e.pojo.ServiceParamPojo;
import org.nexuse2e.pojo.ServicePojo;
import org.nexuse2e.ui.action.NexusE2EAction;
import org.nexuse2e.ui.form.ServiceForm;

/**
 * Updates a service.
 * @author jonas.reese
 */
public class ServiceUpdateAction extends NexusE2EAction {

    private static String URL     = "service.error.url";
    private static String TIMEOUT = "service.error.timeout";

    @Override
    public ActionForward executeNexusE2EAction( ActionMapping actionMapping, ActionForm actionForm,
            HttpServletRequest request, HttpServletResponse response, ActionMessages errors, ActionMessages messages )
            throws Exception {

        ActionForward refresh = actionMapping.findForward( "refresh" );
        ActionForward update = actionMapping.findForward( "update" );
        ActionForward error = actionMapping.findForward( "error" );

        ServiceForm form = (ServiceForm) actionForm;

        List<ComponentPojo> components = Engine.getInstance().getActiveConfigurationAccessService().getComponents(
                ComponentType.SERVICE, Constants.COMPONENTCOMPARATOR );
        if ( components == null || components.size() == 0 ) {
            LOG.trace( "no service components found" );
            ActionMessage errorMessage = new ActionMessage( "generic.error", "No service components configured" );
            errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
            addRedirect( request, URL, TIMEOUT );
            return error;
        }
        request.setAttribute( ATTRIBUTE_COLLECTION, components );
        request.setAttribute( ATTRIBUTE_SERVICE_COLLECTION, Engine.getInstance().getActiveConfigurationAccessService().getServices( ) );

        ServicePojo originalService = Engine.getInstance().getActiveConfigurationAccessService().getServicePojoByNxServiceId(
                form.getNxServiceId() );

        ComponentPojo component = null;

        if ( originalService == null ) {
            if ( form.getNxServiceId() != 0 ) {
                ActionMessage errorMessage = new ActionMessage( "generic.error",
                        "Service does not exist any more. Maybe it has been deleted in the meantime." );
                errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
                addRedirect( request, URL, TIMEOUT );
                return error;
            }
        } else {
            if ( form.getNxComponentId() == 0 ) {
                if ( originalService == null ) {
                    component = components.iterator().next();
                } else {
                    component = originalService.getComponent();
                }
            }
        }
        if ( component == null ) {
            component = Engine.getInstance().getActiveConfigurationAccessService().getComponentByNxComponentId(
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

            if ( originalService == null ) {
                originalService = new ServicePojo();
                originalService.setName( form.getName() );
                originalService.setComponent( component );
                request.setAttribute( REFRESH_TREE, "true" );
            }

            originalService.setAutostart( form.isAutostart() );
            
            form.fillPojosFromParameterMap();
            List<ServiceParamPojo> list = form.getParameters();
            for ( ServiceParamPojo param : list ) {
                param.setService( originalService );
            }

            if ( !originalService.getName().equals( form.getName() ) ) {
                Engine.getInstance().getActiveConfigurationAccessService().renameService( originalService.getName(), form.getName() );
                originalService.setName( form.getName() );
                request.setAttribute( REFRESH_TREE, "true" );
            }
            originalService.setServiceParams( list );

            Engine.getInstance().getActiveConfigurationAccessService().updateService( originalService );
            form.setServiceInstance( Engine.getInstance().getActiveConfigurationAccessService().getService(
                    originalService.getName() ) );

            return update;
        }

        return refresh;
    }

}
