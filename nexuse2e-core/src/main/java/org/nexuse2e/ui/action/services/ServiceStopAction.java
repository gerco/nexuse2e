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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.nexuse2e.BeanStatus;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.pojo.ServicePojo;
import org.nexuse2e.service.Service;
import org.nexuse2e.ui.action.NexusE2EAction;
import org.nexuse2e.ui.form.ServiceForm;

public class ServiceStopAction extends NexusE2EAction {

    @Override
    public ActionForward executeNexusE2EAction( ActionMapping actionMapping, ActionForm actionForm,
            HttpServletRequest request, HttpServletResponse response, EngineConfiguration engineConfiguration, ActionMessages errors, ActionMessages messages )
            throws Exception {

        ActionForward actionForward = actionMapping.findForward( ACTION_FORWARD_SUCCESS );
        ServiceForm serviceForm = (ServiceForm) actionForm;

        ServicePojo servicePojo = engineConfiguration
                .getServicePojoByNxServiceId( serviceForm.getNxServiceId() );
        if ( servicePojo != null ) {
            Service service = engineConfiguration.getService(
                    servicePojo.getName() );
            if ( ( service != null ) && ( service.getStatus() == BeanStatus.STARTED ) ) {
                service.stop();
            } else {
                errors.add( ActionMessages.GLOBAL_MESSAGE, new ActionMessage( "generic.error",
                        "No service in matching state found: " + servicePojo.getName() ) );
            }
        }

        if ( !errors.isEmpty() ) {
            actionForward = actionMapping.findForward( ACTION_FORWARD_FAILURE );
        }

        request.setAttribute( REFRESH_TREE, "true" );

        return actionForward;
    }
} // ServiceStopAction
