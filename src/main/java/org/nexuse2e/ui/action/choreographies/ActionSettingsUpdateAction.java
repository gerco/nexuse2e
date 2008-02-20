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
package org.nexuse2e.ui.action.choreographies;

import java.util.Date;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.pojo.ActionPojo;
import org.nexuse2e.pojo.ChoreographyPojo;
import org.nexuse2e.pojo.FollowUpActionPojo;
import org.nexuse2e.ui.action.NexusE2EAction;
import org.nexuse2e.ui.form.ChoreographyActionForm;

/**
 * @author guido.esch
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ActionSettingsUpdateAction extends NexusE2EAction {

    private static String URL     = "choreographies.error.url";
    private static String TIMEOUT = "choreographies.error.timeout";

    /* (non-Javadoc)
     * @see com.tamgroup.nexus.e2e.ui.action.NexusE2EAction#executeNexusE2EAction(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.apache.struts.action.ActionMessages, org.apache.struts.action.ActionMessages)
     */
    @Override
    public ActionForward executeNexusE2EAction( ActionMapping actionMapping, ActionForm actionForm,
            HttpServletRequest request, HttpServletResponse response, EngineConfiguration engineConfiguration, ActionMessages errors, ActionMessages messages )
            throws Exception {

        ActionForward success = actionMapping.findForward( ACTION_FORWARD_SUCCESS );
        ActionForward error = actionMapping.findForward( ACTION_FORWARD_SUCCESS );

        ChoreographyActionForm form = (ChoreographyActionForm) actionForm;
        if (form.getBackendInboundPipelineId() == 0) {
            errors.add( ActionMessages.GLOBAL_MESSAGE,
                    new ActionMessage( "participant.error.noinboundpipeline" ) );
        }
        if (form.getBackendOutboundPipelineId() == 0) {
            errors.add( ActionMessages.GLOBAL_MESSAGE,
                    new ActionMessage( "participant.error.nooutboundpipeline" ) );
        }

        if (errors.isEmpty()) {
            String[] selectedFollowUps = form.getFollowups();
            int nxActionId = form.getNxActionId();
            int nxChoreographyId = form.getNxChoreographyId();
    
            ActionPojo actionPojo;
            ChoreographyPojo choreography;
            try {
                choreography = engineConfiguration.getChoreographyByNxChoreographyId(
                        nxChoreographyId );
                actionPojo = engineConfiguration.getActionFromChoreographyByNxActionId(
                        choreography, nxActionId );
                form.getProperties( actionPojo );
                for ( int i = 0; i < selectedFollowUps.length; i++ ) {
                    LOG.trace( "selected: " + selectedFollowUps[i] );
                    Iterator<FollowUpActionPojo> followI = actionPojo.getFollowUpActions().iterator();
                    boolean exists = false;
                    while ( followI.hasNext() ) {
                        FollowUpActionPojo followUp = followI.next();
                        if ( followUp.getFollowUpAction().getName().equals( selectedFollowUps[i] ) ) {
                            exists = true;
                            break;
                        }
                    }
                    if ( !exists ) {
                        ActionPojo followAction = engineConfiguration
                                .getActionFromChoreographyByActionId( choreography, selectedFollowUps[i] );
                        followAction.setModifiedDate( new Date() );
                        FollowUpActionPojo newFollowUp = new FollowUpActionPojo();
                        newFollowUp.setAction( actionPojo );
                        newFollowUp.setCreatedDate( new Date() );
                        newFollowUp.setModifiedDate( new Date() );
                        newFollowUp.setModifiedNxUserId( 0 );
                        newFollowUp.setFollowUpAction( followAction );
                        followAction.getFollowedActions().add( newFollowUp );
                        actionPojo.getFollowUpActions().add( newFollowUp );
                    }
    
                }
                Iterator<FollowUpActionPojo> followI = actionPojo.getFollowUpActions().iterator();
                while ( followI.hasNext() ) {
                    FollowUpActionPojo followUp = followI.next();
                    boolean exists = false;
                    for ( int i = 0; i < selectedFollowUps.length; i++ ) {
                        if ( followUp.getFollowUpAction().getName().equals( selectedFollowUps[i] ) ) {
                            exists = true;
                            break;
                        }
                    }
                    if ( !exists ) {
                        followUp.getFollowUpAction().getFollowedActions().remove( followUp );
                        // followUp.setAction( null );
                        followI.remove();
                    }
                }
                engineConfiguration.updateChoreography( choreography );
    
            } catch ( NexusException e ) {
                e.printStackTrace();
                ActionMessage errorMessage = new ActionMessage( "generic.error", e.getMessage() );
                errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
                addRedirect( request, URL, TIMEOUT );
                return error;
            } catch ( Exception e ) {
                e.printStackTrace();
            } catch ( Error e ) {
                e.printStackTrace();
            }
        } else {
            return error;
        }

        return success;
    }

}
