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
package org.nexuse2e.ui.action.choreographies;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.nexuse2e.configuration.Constants;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.pojo.ChoreographyPojo;
import org.nexuse2e.pojo.ParticipantPojo;
import org.nexuse2e.pojo.PartnerPojo;
import org.nexuse2e.ui.action.NexusE2EAction;
import org.nexuse2e.ui.form.ParticipantForm;

/**
 * @author guido.esch
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ParticipantViewAction extends NexusE2EAction {

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
        ActionForward error = actionMapping.findForward( ACTION_FORWARD_FAILURE );

        ParticipantForm form = (ParticipantForm) actionForm;

        int nxChoreographyId = form.getNxChoreographyId();
        if ( nxChoreographyId == 0 ) {
            ActionMessage errorMessage = new ActionMessage( "generic.error", "ChoreographyId must not be null!" );
            errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
            addRedirect( request, URL, TIMEOUT );
            return error;
        }

        ChoreographyPojo choreography = engineConfiguration
                .getChoreographyByNxChoreographyId( nxChoreographyId );

        List<PartnerPojo> localPartnerList = engineConfiguration.getPartners(
                Constants.PARTNER_TYPE_LOCAL, Constants.PARTNERCOMPARATOR );
        form.setLocalPartners( localPartnerList );

        ParticipantPojo participant = engineConfiguration
                .getParticipantFromChoreographyByNxPartnerId( choreography, form.getNxPartnerId() );
        form.setProperties( participant );
        form.setPartnerDisplayName( participant.getPartner().getPartnerId() );
        form.setNxPartnerId( participant.getPartner().getNxPartnerId() );
        form.setUrl( participant.getConnection().getUri() );
        form.setConnections( participant.getPartner().getConnections() );
        form.setNxConnectionId( participant.getConnection().getNxConnectionId() );
        form.setDescription( participant.getDescription() );
        form.setNxLocalPartnerId( participant.getLocalPartner().getNxPartnerId() );

        form.setLocalCertificates( participant.getLocalPartner().getCertificates() );
        if ( participant.getLocalCertificate() != null ) {
            form.setNxLocalCertificateId( participant.getLocalCertificate().getNxCertificateId() );
        } else {
            form.setNxLocalCertificateId( 0 );
        }

        //request.getSession().setAttribute( Crumbs.CURRENT_LOCATION, Crumbs.PARTICIPANT+ "_"+choreographyId+"_"+partner.getPartnerId());

        return success;
    }

}
