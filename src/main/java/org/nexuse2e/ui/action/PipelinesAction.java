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
package org.nexuse2e.ui.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.nexuse2e.configuration.Constants;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.pojo.PipelinePojo;
import org.nexuse2e.ui.form.PipelineForm;

/**
 * @author gesch
 *
 */
public class PipelinesAction extends NexusE2EAction {

    /* (non-Javadoc)
     * @see com.tamgroup.nexus.e2e.ui.action.NexusE2EAction#executeNexusE2EAction(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.apache.struts.action.ActionMessages)
     */
    @Override
    public ActionForward executeNexusE2EAction( ActionMapping actionMapping, ActionForm actionForm,
            HttpServletRequest request, HttpServletResponse response, EngineConfiguration engineConfiguration, ActionMessages errors, ActionMessages messages )
            throws Exception {

        ActionForward success = actionMapping.findForward( ACTION_FORWARD_SUCCESS );

        List<PipelineForm> pipelines = new ArrayList<PipelineForm>();
        List<PipelinePojo> pipelinePojos = null;

        String parameter = actionMapping.getParameter();

        if ( "frontend".equalsIgnoreCase( parameter ) ) {
            pipelinePojos = engineConfiguration.getFrontendPipelinePojos(
                    Constants.PIPELINE_TYPE_ALL, Constants.PIPELINECOMPARATOR );
        } else {
            pipelinePojos = engineConfiguration.getBackendPipelinePojos(
                    Constants.PIPELINE_TYPE_ALL, Constants.PIPELINECOMPARATOR );
        }
        if ( pipelinePojos != null && pipelinePojos.size() > 0 ) {

            Iterator<PipelinePojo> pipelineI = pipelinePojos.iterator();
            while ( pipelineI.hasNext() ) {
                PipelinePojo pipeline = pipelineI.next();
                PipelineForm pipelineForm = new PipelineForm();
                pipelineForm.setProperties( pipeline );
                pipelines.add( pipelineForm );
            }
        }
        request.setAttribute( ATTRIBUTE_COLLECTION, pipelines );

        //request.getSession().setAttribute( Crumbs.CURRENT_LOCATION, Crumbs.PIPELINES );

        return success;
    }

}
