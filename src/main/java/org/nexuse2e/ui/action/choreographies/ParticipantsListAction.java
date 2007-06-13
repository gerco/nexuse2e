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

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.pojo.ChoreographyPojo;
import org.nexuse2e.pojo.ParticipantPojo;
import org.nexuse2e.ui.action.NexusE2EAction;
import org.nexuse2e.ui.form.ChoreographyForm;
import org.nexuse2e.ui.form.ParticipantForm;

/**
 * @author gesch
 *
 */
public class ParticipantsListAction extends NexusE2EAction {

    private static String URL     = "choreographies.error.url";
    private static String TIMEOUT = "choreographies.error.timeout";

    /* (non-Javadoc)
     * @see com.tamgroup.nexus.e2e.ui.action.NexusE2EAction#executeNexusE2EAction(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.apache.struts.action.ActionMessages, org.apache.struts.action.ActionMessages)
     */
    @Override
    public ActionForward executeNexusE2EAction( ActionMapping actionMapping, ActionForm actionForm,
            HttpServletRequest request, HttpServletResponse response, ActionMessages errors, ActionMessages messages )
            throws Exception {

        ActionForward success = actionMapping.findForward( ACTION_FORWARD_SUCCESS );
        ActionForward error = actionMapping.findForward( ACTION_FORWARD_FAILURE );

        ChoreographyForm form = (ChoreographyForm) actionForm;

        int nxChoreographyId = form.getNxChoreographyId();
        if ( nxChoreographyId == 0 ) {
            ActionMessage errorMessage = new ActionMessage( "generic.error", "ChoreographyId must not be null!" );
            errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
            addRedirect( request, URL, TIMEOUT );
            return error;
        }
        List<ParticipantPojo> participantPojos = null;
        Vector participants = new Vector();
        try {
            ChoreographyPojo choreography = Engine.getInstance().getActiveConfigurationAccessService()
                    .getChoreographyByNxChoreographyId( nxChoreographyId );
            participantPojos = choreography.getParticipants();
            Iterator i = participantPojos.iterator();
            while ( i.hasNext() ) {
                ParticipantPojo participant = (ParticipantPojo) i.next();
                ParticipantForm pform = new ParticipantForm();
                pform.setPartnerDisplayName( participant.getPartner().getPartnerId() );
                pform.setNxPartnerId( participant.getPartner().getNxPartnerId() );
                pform.setUrl( (participant.getConnection() != null) ? participant.getConnection().getUri() : "" );
                pform.setDescription( participant.getDescription() );
                participants.addElement( pform );
            }
        } catch ( NexusException e ) {
            ActionMessage errorMessage = new ActionMessage( "generic.error", e.getMessage() );
            errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
            addRedirect( request, URL, TIMEOUT );
            return error;
        }

        // request.getSession().setAttribute( Crumbs.CURRENT_LOCATION, Crumbs.PARTICIPANTS+ "_"+choreographyId);

        request.setAttribute( ATTRIBUTE_COLLECTION, participants );
        return success;
    } // executeNexusE2EAction

} // ParticipantsListAction
