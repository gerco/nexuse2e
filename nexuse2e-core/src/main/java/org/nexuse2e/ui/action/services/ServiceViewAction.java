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
package org.nexuse2e.ui.action.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.nexuse2e.configuration.ConfigurationUtil;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.configuration.GenericComparator;
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
            HttpServletRequest request, HttpServletResponse response, EngineConfiguration engineConfiguration, ActionMessages errors, ActionMessages messages )
            throws Exception {


        ServiceForm serviceForm = (ServiceForm) actionForm;

        ServicePojo servicePojo = engineConfiguration.getServicePojoByNxServiceId(
                serviceForm.getNxServiceId() );

        serviceForm.setProperties( servicePojo );
        Service service = engineConfiguration.getServiceInstanceFromPojo( servicePojo );
        for ( ServiceParamPojo serviceParam : servicePojo.getServiceParams() ) {
            serviceParam.setParameterDescriptor( service.getParameterMap().get( serviceParam.getParamName() ) );
        }
        serviceForm.setParameters( ConfigurationUtil.getConfiguration( service, servicePojo ) );
        serviceForm.createParameterMapFromPojos();
        serviceForm.setServiceInstance( service );
        
        List<ServicePojo> services = engineConfiguration.getServices();
        List<ServicePojo> sortedServices = new ArrayList<ServicePojo>( services.size() );
        sortedServices.addAll( engineConfiguration.getServices() );
        Collections.sort( sortedServices, new GenericComparator<ServicePojo>( "name", true ) );
        request.setAttribute( ATTRIBUTE_SERVICE_COLLECTION, sortedServices );

        return actionMapping.findForward( ACTION_FORWARD_SUCCESS );
        
    }

}
