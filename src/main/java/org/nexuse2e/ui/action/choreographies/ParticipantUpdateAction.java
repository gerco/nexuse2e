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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.Constants;
import org.nexuse2e.pojo.CertificatePojo;
import org.nexuse2e.pojo.ChoreographyPojo;
import org.nexuse2e.pojo.ConnectionPojo;
import org.nexuse2e.pojo.ParticipantPojo;
import org.nexuse2e.pojo.PartnerPojo;
import org.nexuse2e.ui.action.NexusE2EAction;
import org.nexuse2e.ui.form.ParticipantForm;

/**
 * @author gesch
 *
 */
public class ParticipantUpdateAction extends NexusE2EAction {

    private static String URL     = "choreographies.error.url";
    private static String TIMEOUT = "choreographies.error.timeout";

    /* (non-Javadoc)
     * @see com.tamgroup.nexus.e2e.ui.action.NexusE2EAction#executeNexusE2EAction(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.apache.struts.action.ActionMessages, org.apache.struts.action.ActionMessages)
     */
    @Override
    public ActionForward executeNexusE2EAction( ActionMapping actionMapping, ActionForm actionForm,
            HttpServletRequest request, HttpServletResponse response, ActionMessages errors, ActionMessages messages )
            throws Exception {

        ActionForward update = actionMapping.findForward( "update" );
        ActionForward reload = actionMapping.findForward( "reload" );
        ActionForward error = actionMapping.findForward( ACTION_FORWARD_FAILURE );

        ParticipantForm form = (ParticipantForm) actionForm;

        int nxChoreographyId = form.getNxChoreographyId();
        int nxPartnerId = form.getNxPartnerId();
        int localPartnerId = form.getNxLocalPartnerId();
        String description = form.getDescription();

        if ( nxChoreographyId == 0 ) {
            ActionMessage errorMessage = new ActionMessage( "generic.error", "ChoreographyId must not be null!" );
            errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
            addRedirect( request, URL, TIMEOUT );
            return error;
        }

        ParticipantPojo participant;
        if ( !form.isSubmitted() ) {

            form.cleanSetting();
            form.setNxChoreographyId( nxChoreographyId );
            form.setNxPartnerId( nxPartnerId );
            form.setNxLocalPartnerId( localPartnerId );
            form.setDescription( description );

            form.setPartners( Engine.getInstance().getActiveConfigurationAccessService().getPartners(
                    Constants.PARTNER_TYPE_PARTNER, Constants.PARTNERCOMPARATOR ) );
            form.setLocalPartners( Engine.getInstance().getActiveConfigurationAccessService().getPartners(
                    Constants.PARTNER_TYPE_LOCAL, Constants.PARTNERCOMPARATOR ) );

            if ( form.getPartners() == null || form.getPartners().size() == 0 ) {
                ActionMessage errorMessage = new ActionMessage( "generic.error", "No partners found!" );
                errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
                addRedirect( request, URL, TIMEOUT );
                return error;
            }

            if ( form.getLocalPartners() == null || form.getLocalPartners().size() == 0 ) {
                ActionMessage errorMessage = new ActionMessage( "generic.error", "No Server Identities found!" );
                errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
                addRedirect( request, URL, TIMEOUT );
                return error;
            }

            if ( form.getNxPartnerId() == 0 ) {
                form.setNxPartnerId( ( form.getPartners().get( 0 ) ).getNxPartnerId() );
            }

            try {
                PartnerPojo partner = Engine.getInstance().getActiveConfigurationAccessService().getPartnerByNxPartnerId(
                        form.getNxPartnerId() );
                form.setPartnerDisplayName( partner.getPartnerId() );
                LOG.debug( "connections.size: " + partner.getConnections().size() );
                form.setConnections( partner.getConnections() );

            } catch ( NexusException e ) {
                ActionMessage errorMessage = new ActionMessage( "generic.error", e.getMessage() );
                errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
                addRedirect( request, URL, TIMEOUT );
                return error;
            }
            if ( form.getNxLocalPartnerId() == 0 ) {
                form.setLocalCertificates( form.getLocalPartners().get( 0 ).getCertificates() );
            } else {
                PartnerPojo localPartner = Engine.getInstance().getActiveConfigurationAccessService().getPartnerByNxPartnerId(
                        form.getNxLocalPartnerId() );
                form.setLocalCertificates( localPartner.getCertificates() );

            }

            return reload;

        } else {
            LOG.debug( "Updating Particpant pojo..." );

            try {
                ChoreographyPojo choreography = Engine.getInstance().getActiveConfigurationAccessService()
                        .getChoreographyByNxChoreographyId( nxChoreographyId );
                PartnerPojo partner = Engine.getInstance().getActiveConfigurationAccessService().getPartnerByNxPartnerId(
                        form.getNxPartnerId() );
                PartnerPojo localPartner = Engine.getInstance().getActiveConfigurationAccessService().getPartnerByNxPartnerId(
                        form.getNxLocalPartnerId() );
                participant = Engine.getInstance().getActiveConfigurationAccessService()
                        .getParticipantFromChoreographyByNxPartnerId( choreography, partner.getNxPartnerId() );
                if ( participant == null ) {
                    ActionMessage errorMessage = new ActionMessage( "generic.error",
                            "Unable to find matching Participant for NxChoreographyId:" + nxChoreographyId
                                    + " and NxPartnerId:" + form.getNxPartnerId() + "!" );
                    errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
                    addRedirect( request, URL, TIMEOUT );
                    return error;
                }

                if ( form.getNxLocalCertificateId() != 0 ) {
                    LOG.debug( "Setting local certificate" );
                    CertificatePojo localCertificate = Engine.getInstance().getActiveConfigurationAccessService()
                            .getCertificateFromPartnerByNxCertificateId( localPartner, form.getNxLocalCertificateId() );
                    participant.setLocalCertificate( localCertificate );
                } else {
                    participant.setLocalCertificate( null );
                }

                if ( form.getNxConnectionId() != 0 ) {
                    LOG.debug( "Setting connection" );
                    ConnectionPojo connectionPojo = Engine.getInstance().getActiveConfigurationAccessService()
                            .getConnectionFromPartnerByNxConnectionId( partner, form.getNxConnectionId() );
                    participant.setConnection( connectionPojo );
                } else {
                    LOG.error( "No Connection provided for participant!" );
                    ActionMessage errorMessage = new ActionMessage( "generic.error",
                            "No Connection provided for participant!" );
                    errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
                    return error;
                }

                participant.setDescription( form.getDescription() );
                participant.setLocalPartner( localPartner );

                LOG.debug( "updating choreography" );
                Engine.getInstance().getActiveConfigurationAccessService().updateChoreography( choreography );
            } catch ( NexusException e ) {
                ActionMessage errorMessage = new ActionMessage( "generic.error", e.getMessage() );
                errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
                addRedirect( request, URL, TIMEOUT );
                return error;
            }
        }

        return update;
    }

}
