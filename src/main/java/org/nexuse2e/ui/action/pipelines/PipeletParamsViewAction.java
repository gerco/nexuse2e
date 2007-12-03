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
package org.nexuse2e.ui.action.pipelines;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.nexuse2e.Configurable;
import org.nexuse2e.Engine;
import org.nexuse2e.configuration.ConfigurationUtil;
import org.nexuse2e.pojo.PipeletPojo;
import org.nexuse2e.ui.action.NexusE2EAction;
import org.nexuse2e.ui.form.PipelineForm;

/**
 * @author guido.esch
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PipeletParamsViewAction extends NexusE2EAction {

    /* (non-Javadoc)
     * @see com.tamgroup.nexus.e2e.ui.action.NexusE2EAction#executeNexusE2EAction(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.apache.struts.action.ActionMessages)
     */
    @Override
    public ActionForward executeNexusE2EAction( ActionMapping actionMapping, ActionForm actionForm,
            HttpServletRequest request, HttpServletResponse response, ActionMessages errors, ActionMessages messages )
            throws Exception {

        ActionForward success = actionMapping.findForward( ACTION_FORWARD_SUCCESS );
        ActionForward error = actionMapping.findForward( ACTION_FORWARD_FAILURE );

        PipelineForm form = (PipelineForm) actionForm;

        int position = form.getSortaction() - 1;

        PipeletPojo pipeletPojo = null;
        List<PipeletPojo> pipelets = form.getPipelets();
        if (position >= 0 && position < pipelets.size()) {
            pipeletPojo = pipelets.get( position );
        }

        if ( pipeletPojo == null ) {
            return error;
        }

        form.setCurrentPipelet( pipeletPojo );

        Object componentInst = Class.forName( pipeletPojo.getComponent().getClassName() ).newInstance();
        if ( componentInst == null || !( componentInst instanceof Configurable ) ) {
            return error;
        }
        Configurable configurable = (Configurable) componentInst;
        ConfigurationUtil.configurePipelet( configurable, pipeletPojo.getPipeletParams() );
        form.setConfigurable( configurable );
        form.setParameters( ConfigurationUtil.getConfiguration( configurable, pipeletPojo ) );
        form.createParameterMapFromPojos();

        request.setAttribute( ATTRIBUTE_COLLECTION, Engine.getInstance().getActiveConfigurationAccessService()
                .getServices() );

        return success;
    }

}
