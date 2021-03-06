/**
 * NEXUSe2e Business Messaging Open Source
 * Copyright 2000-2009, Tamgroup and X-ioma GmbH
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
package org.nexuse2e.ui.action.choreographies;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.Constants;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.configuration.PipelineType;
import org.nexuse2e.pojo.ActionPojo;
import org.nexuse2e.pojo.ChoreographyPojo;
import org.nexuse2e.pojo.FollowUpActionPojo;
import org.nexuse2e.ui.action.NexusE2EAction;
import org.nexuse2e.ui.form.ChoreographyActionForm;

/**
 * @author guido.esch
 */
public class ActionSettingsViewAction extends NexusE2EAction {

    private static String URL     = "choreographies.error.url";
    private static String TIMEOUT = "choreographies.error.timeout";

    /*
     * (non-Javadoc)
     * 
     * @see com.tamgroup.nexus.e2e.ui.action.NexusE2EAction#executeNexusE2EAction(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm,
     * javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.apache.struts.action.ActionMessages,
     * org.apache.struts.action.ActionMessages)
     */
    @Override
    public ActionForward executeNexusE2EAction(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest request, HttpServletResponse response,
            EngineConfiguration engineConfiguration, ActionMessages errors, ActionMessages messages) throws Exception {

        ActionForward success = actionMapping.findForward(ACTION_FORWARD_SUCCESS);
        ActionForward error = actionMapping.findForward(ACTION_FORWARD_FAILURE);

        ChoreographyActionForm form = (ChoreographyActionForm) actionForm;

        int nxChoreographyId = form.getNxChoreographyId();
        if (nxChoreographyId == 0) {
            ActionMessage errorMessage = new ActionMessage("generic.error", "ChoreographyId must not be null!");
            errors.add(ActionMessages.GLOBAL_MESSAGE, errorMessage);
            addRedirect(request, URL, TIMEOUT);
            return error;
        }
        int nxActionId = form.getNxActionId();
        if (nxActionId == 0) {
            ActionMessage errorMessage = new ActionMessage("generic.error", "ActionId must not be null!");
            errors.add(ActionMessages.GLOBAL_MESSAGE, errorMessage);
            addRedirect(request, URL, TIMEOUT);
            return error;
        }
        ActionPojo action = null;
        ChoreographyPojo choreography = null;
        try {
            choreography = engineConfiguration.getChoreographyByNxChoreographyId(nxChoreographyId);
            action = engineConfiguration.getActionFromChoreographyByNxActionId(choreography, nxActionId);
        } catch (NexusException e) {
            ActionMessage errorMessage = new ActionMessage("generic.error", e.getMessage());
            errors.add(ActionMessages.GLOBAL_MESSAGE, errorMessage);
            addRedirect(request, URL, TIMEOUT);
            return error;
        }
        form.setProperties(action);

        Set<ActionPojo> followUps = null;

        followUps = choreography.getActions();
        if (followUps == null) {
            followUps = new HashSet<ActionPojo>();
        }
        Iterator<ActionPojo> i = followUps.iterator();

        List<String> followUpList = new ArrayList<String>();
        List<String> followUpSelectedList = new ArrayList<String>();
        while (i.hasNext()) {
            ActionPojo follow = i.next();
            followUpList.add(follow.getName());
            Iterator<FollowUpActionPojo> followI = action.getFollowUpActions().iterator();
            while (followI.hasNext()) {
                if (follow.getName().equals(followI.next().getFollowUpAction().getName())) {
                    followUpSelectedList.add(follow.getName());
                }
            }
        }
        String[] followUpArray = new String[0];
        if (followUpSelectedList.size() > 0) {
            followUpArray = new String[followUpSelectedList.size()];
            followUpSelectedList.toArray(followUpArray);
        }
        form.setFollowups(followUpArray);
        form.setFollowupActions(followUpList);

        form.setBackendInboundPipelines(engineConfiguration.getBackendPipelinePojos(PipelineType.INBOUND.getOrdinal(), Constants.PIPELINECOMPARATOR));
        form.setStatusUpdatePipelines(engineConfiguration.getBackendPipelinePojos(PipelineType.INBOUND.getOrdinal(), Constants.PIPELINECOMPARATOR));
        form.setBackendOutboundPipelines(engineConfiguration.getBackendPipelinePojos(PipelineType.OUTBOUND.getOrdinal(), Constants.PIPELINECOMPARATOR));

        // request.getSession().setAttribute( Crumbs.CURRENT_LOCATION, Crumbs.ACTION+"_"+choreographyId+"_"+actionId );

        return success;
    }

}