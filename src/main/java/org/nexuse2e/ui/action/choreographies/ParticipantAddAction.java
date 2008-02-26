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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.ConfigurationAccessService;
import org.nexuse2e.configuration.Constants;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.configuration.GenericComparator;
import org.nexuse2e.pojo.CertificatePojo;
import org.nexuse2e.pojo.ChoreographyPojo;
import org.nexuse2e.pojo.ConnectionPojo;
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
public class ParticipantAddAction extends NexusE2EAction {

    private static String URL     = "choreographies.error.url";
    private static String TIMEOUT = "choreographies.error.timeout";

    /* (non-Javadoc)
     * @see com.tamgroup.nexus.e2e.ui.action.NexusE2EAction#executeNexusE2EAction(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.apache.struts.action.ActionMessages, org.apache.struts.action.ActionMessages)
     */
    @Override
    public ActionForward executeNexusE2EAction( ActionMapping actionMapping, ActionForm actionForm,
            HttpServletRequest request, HttpServletResponse response, EngineConfiguration engineConfiguration, ActionMessages errors, ActionMessages messages )
            throws Exception {

        ActionForward reload = actionMapping.findForward( "reload" );
        ActionForward created = actionMapping.findForward( "created" );
        ActionForward error = actionMapping.findForward( ACTION_FORWARD_FAILURE );

        ParticipantForm form = (ParticipantForm) actionForm;

        int nxChoreographyId = form.getNxChoreographyId();
        int partnerId = form.getNxPartnerId();
        int localPartnerId = form.getNxLocalPartnerId();
        String description = form.getDescription();

        LOG.trace( "from.issubmitted: " + form.isSubmitted() );
        if ( !form.isSubmitted() ) {

            form.cleanSetting();
            form.setNxChoreographyId( nxChoreographyId );
            form.setNxPartnerId( partnerId );
            form.setNxLocalPartnerId( localPartnerId );
            form.setDescription( description );

            ConfigurationAccessService cas = engineConfiguration;
            ChoreographyPojo choreography = engineConfiguration
                                                    .getChoreographyByNxChoreographyId( nxChoreographyId );
            if ( choreography == null ) {
                ActionMessage errorMessage = new ActionMessage( "generic.error", "ChoreographyId must not be null!" );
                errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
                addRedirect( request, URL, TIMEOUT );
                return error;
            }
            
            List<PartnerPojo> allPartners = cas.getPartners(
                    Constants.PARTNER_TYPE_PARTNER, new GenericComparator<PartnerPojo>( "partnerId", false ) );
            List<PartnerPojo> unboundPartners = new ArrayList<PartnerPojo>();
            for ( PartnerPojo currPartner : allPartners ) {
                // return only partners that are not participants for this choreography already
                if ( cas.getParticipantFromChoreographyByNxPartnerId( choreography, currPartner.getNxPartnerId() ) == null ) {
                    unboundPartners.add( currPartner );
                }
            }
            form.setPartners( unboundPartners );
            form.setLocalPartners( engineConfiguration.getPartners(
                    Constants.PARTNER_TYPE_LOCAL, Constants.PARTNERCOMPARATOR ) );

            LOG.trace( "Partners: " + form.getPartners().size() );
            LOG.trace( "localPartners: " + form.getLocalPartners().size() );

            if ( form.getPartners() == null || form.getPartners().size() == 0 ) {
                if ( allPartners == null || allPartners.size() == 0 ) {
                    // there are no partners
                    errors.add( ActionMessages.GLOBAL_MESSAGE,
                            new ActionMessage( "generic.error", "No partners found!" ) );
                } else {
                    // all partners are already bound
                    errors.add( ActionMessages.GLOBAL_MESSAGE,
                            new ActionMessage( "generic.error", "All partners are participants already!" ) );
                }
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
                PartnerPojo partner = engineConfiguration.getPartnerByNxPartnerId(
                        form.getNxPartnerId() );
                form.setPartnerDisplayName( partner.getPartnerId() );
                LOG.trace( "connections.size: " + partner.getConnections().size() );
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
                PartnerPojo localPartner = engineConfiguration.getPartnerByNxPartnerId(
                        form.getNxLocalPartnerId() );
                form.setLocalCertificates( localPartner.getCertificates() );

            }

            //request.getSession().setAttribute( Crumbs.CURRENT_LOCATION, Crumbs.ADD_PARTICIPANT+ "_"+choreographyId);

            return reload;
        } else {
            if ( nxChoreographyId == 0 ) {
                ActionMessage errorMessage = new ActionMessage( "generic.error", "ChoreographyId must not be null!" );
                errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
                addRedirect( request, URL, TIMEOUT );
                return error;
            }
            
            if ( partnerId == 0 ) {
                ActionMessage errorMessage = new ActionMessage( "generic.error", "PartnerId must not be null!" );
                errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
                addRedirect( request, URL, TIMEOUT );
                return error;
            }

            ParticipantPojo participant = new ParticipantPojo();
            participant.setDescription( form.getDescription() );
            try {
                ChoreographyPojo choreography = engineConfiguration
                        .getChoreographyByNxChoreographyId( nxChoreographyId );
                participant.setChoreography( choreography );
                PartnerPojo partner = engineConfiguration.getPartnerByNxPartnerId(
                        form.getNxPartnerId() );
                PartnerPojo localPartner = engineConfiguration.getPartnerByNxPartnerId(
                        form.getNxLocalPartnerId() );
                CertificatePojo localCertificate = engineConfiguration
                        .getCertificateFromPartnerByNxCertificateId( partner, form.getNxLocalCertificateId() );
                participant.setPartner( partner );
                
                // Make sure we have a default description set
                if ( StringUtils.isEmpty( participant.getDescription() ) ) {
                    participant.setDescription( partner.getPartnerId() );
                }
                
                participant.setLocalPartner( localPartner );
                participant.setCreatedDate( new Date() );
                participant.setModifiedDate( new Date() );
                participant.setLocalCertificate( localCertificate );

                for (ConnectionPojo connection : partner.getConnections()) {
                    if ( connection.getNxConnectionId() == form.getNxConnectionId() ) {
                        participant.setConnection( connection );
                        break;
                    }
                }

                if (participant.getConnection() == null) {
                    ActionMessage errorMessage = new ActionMessage( "participant.error.noconnection" );
                    errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
                    addRedirect( request, URL, TIMEOUT );
                    return error;
                } else {
                    choreography.getParticipants().add( participant );
                    engineConfiguration.updateChoreography( choreography );
                }

            } catch ( NexusException e ) {
                ActionMessage errorMessage = new ActionMessage( "generic.error", e.getMessage() );
                errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
                addRedirect( request, URL, TIMEOUT );
                return error;
            }

            //request.getSession().setAttribute( Crumbs.CURRENT_LOCATION, Crumbs.ADD_PARTICIPANT+ "_"+choreographyId);

            request.setAttribute( REFRESH_TREE, "true" );

            return created;
        }
    }
}
