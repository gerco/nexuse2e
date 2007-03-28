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
import org.nexuse2e.configuration.ConfigurationUtil;
import org.nexuse2e.configuration.Constants;
import org.nexuse2e.configuration.Constants.ComponentType;
import org.nexuse2e.pojo.ComponentPojo;
import org.nexuse2e.pojo.ServicePojo;
import org.nexuse2e.service.Service;
import org.nexuse2e.ui.action.NexusE2EAction;
import org.nexuse2e.ui.form.ServiceForm;

/**
 * Adds a <code>Service</code>.
 * 
 * @author jonas.reese
 */
public class ServiceAddAction extends NexusE2EAction {

    private static String URL     = "service.error.url";
    private static String TIMEOUT = "service.error.timeout";

    @Override
    public ActionForward executeNexusE2EAction( ActionMapping actionMapping, ActionForm actionForm,
            HttpServletRequest request, HttpServletResponse response, ActionMessages errors, ActionMessages messages )
            throws Exception {

        ActionForward success = actionMapping.findForward( ACTION_FORWARD_SUCCESS );
        ActionForward error = actionMapping.findForward( ACTION_FORWARD_FAILURE );

        ServiceForm serviceForm = (ServiceForm) actionForm;
        if ( serviceForm.getName() != null && serviceForm.getName().trim().length() > 0 ) {
            Service service = Engine.getInstance().getActiveConfigurationAccessService().getService( serviceForm.getName() );
            if ( service != null ) {
                serviceForm.setName( "" );
            }
        } else {
            serviceForm.setName( "" );
        }

        List<ComponentPojo> components = Engine.getInstance().getActiveConfigurationAccessService().getComponents(
                ComponentType.SERVICE, Constants.COMPONENTCOMPARATOR );

        if ( components == null || components.size() == 0 ) {
            LOG.trace( "no service components found" );
            ActionMessage errorMessage = new ActionMessage( "generic.error", "No service components configured" );
            errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
            addRedirect( request, URL, TIMEOUT );
            return error;
        }

        ComponentPojo componentPojo = null;
        if ( serviceForm.getNxComponentId() == 0 ) {
            componentPojo = components.iterator().next();
        } else {
            componentPojo = Engine.getInstance().getActiveConfigurationAccessService().getComponentByNxComponentId(
                    serviceForm.getNxComponentId() );
        }

        if ( componentPojo != null ) {
            Service service = null;
            String className = componentPojo.getClassName();
            Object obj = Class.forName( className ).newInstance();
            if ( obj instanceof Service ) {
                service = (Service) obj;
                serviceForm.setServiceInstance( service );
                serviceForm.setParameters( ConfigurationUtil.getConfiguration( service, new ServicePojo() ) );
                serviceForm.createParameterMapFromPojos();
            }
        }
        request.setAttribute( REFRESH_TREE, "true" );
        request.setAttribute( ATTRIBUTE_COLLECTION, components );

        return success;
    }

}
