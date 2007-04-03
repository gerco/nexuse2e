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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.nexuse2e.Engine;
import org.nexuse2e.configuration.ConfigurationUtil;
import org.nexuse2e.pojo.ServiceParamPojo;
import org.nexuse2e.pojo.ServicePojo;
import org.nexuse2e.service.Service;
import org.nexuse2e.ui.action.NexusE2EAction;
import org.nexuse2e.ui.form.ServiceForm;

/**
 * Fills a ServiceForm with data for a requested service
 * @author jonas.reese
 */
public class ServiceViewAction extends NexusE2EAction {

    @Override
    public ActionForward executeNexusE2EAction( ActionMapping actionMapping, ActionForm actionForm,
            HttpServletRequest request, HttpServletResponse response, ActionMessages errors, ActionMessages messages )
            throws Exception {

        ActionForward success = actionMapping.findForward( ACTION_FORWARD_SUCCESS );

        ServiceForm serviceForm = (ServiceForm) actionForm;

        ServicePojo servicePojo = Engine.getInstance().getActiveConfigurationAccessService().getServicePojoByNxServiceId(
                serviceForm.getNxServiceId() );

        serviceForm.setProperties( servicePojo );
        Service service = Engine.getInstance().getActiveConfigurationAccessService().getService( servicePojo.getName() );
        for ( ServiceParamPojo serviceParam : servicePojo.getServiceParams() ) {
            serviceParam.setParameterDescriptor( service.getParameterMap().get( serviceParam.getParamName() ) );
        }
        serviceForm.setParameters( ConfigurationUtil.getConfiguration( service, servicePojo ) );
        serviceForm.createParameterMapFromPojos();
        serviceForm.setServiceInstance( service );

        request.setAttribute( ATTRIBUTE_COLLECTION, Engine.getInstance().getActiveConfigurationAccessService().getServices( ) );

        return success;
    }

}
