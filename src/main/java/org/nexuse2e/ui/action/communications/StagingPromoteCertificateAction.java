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
package org.nexuse2e.ui.action.communications;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.nexuse2e.Engine;
import org.nexuse2e.configuration.Constants;
import org.nexuse2e.pojo.CertificatePojo;
import org.nexuse2e.pojo.PartnerPojo;
import org.nexuse2e.ui.action.NexusE2EAction;
import org.nexuse2e.ui.form.CertificatePromotionForm;

/**
 * @author guido.esch
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class StagingPromoteCertificateAction extends NexusE2EAction {

    private static String URL     = "staging.error.url";
    private static String TIMEOUT = "staging.error.timeout";

    /* (non-Javadoc)
     * @see com.tamgroup.nexus.e2e.ui.action.NexusE2EAction#executeNexusE2EAction(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.apache.struts.action.ActionMessages)
     */
    @Override
    public ActionForward executeNexusE2EAction( ActionMapping actionMapping, ActionForm actionForm,
            HttpServletRequest request, HttpServletResponse response, ActionMessages errors, ActionMessages messages )
            throws Exception {


        CertificatePromotionForm form = (CertificatePromotionForm) actionForm;

        if ("changeServerIdentity".equals( form.getActionName() )) {
            form.setActionName( "promote" );
            return actionMapping.findForward( "changeServerIdentity" );
        } else {
            int certificateId = form.getNxCertificateId();
            if (certificateId <= 0) {
                ActionMessage errorMessage = new ActionMessage( "generic.error", "invalid certificate ID (" + certificateId
                        + "). No Certificate found to promote" );
                errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
                addRedirect( request, URL, TIMEOUT );
                return actionMapping.findForward( ACTION_FORWARD_FAILURE );
            }
    
            PartnerPojo localPartner = Engine.getInstance().getActiveConfigurationAccessService().getPartnerByNxPartnerId(
                    form.getLocalNxPartnerId() );
    
            if ( localPartner == null ) {
                ActionMessage errorMessage = new ActionMessage( "generic.error", "invalid localNxpartnerId("
                        + form.getLocalNxPartnerId() + "), no partner found to promote" );
                errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
                addRedirect( request, URL, TIMEOUT );
                return actionMapping.findForward( ACTION_FORWARD_FAILURE );
            }
    
            CertificatePojo stagedCert = Engine.getInstance().getActiveConfigurationAccessService()
                    .getCertificateByNxCertificateId( Constants.CERTIFICATE_TYPE_STAGING, certificateId );
    
            if ( stagedCert == null ) {
                ActionMessage errorMessage = new ActionMessage( "generic.error",
                        "invalid SeqNo. No Certificate found to promote" );
                errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
                addRedirect( request, URL, TIMEOUT );
                return actionMapping.findForward( ACTION_FORWARD_FAILURE );
            }
    
            CertificatePojo certificate;
            if (form.getReplaceNxCertificateId() <= 0) {
                certificate = new CertificatePojo();
            } else {
                certificate = Engine.getInstance().getActiveConfigurationAccessService().getCertificateByNxCertificateId(
                        Constants.CERTIFICATE_TYPE_ALL, form.getReplaceNxCertificateId() );
                if (certificate == null) {
                    ActionMessage errorMessage = new ActionMessage( "generic.error",
                        "invalid certificate ID. Replaced certificate not found." );
                    errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
                    addRedirect( request, URL, TIMEOUT );
                    return actionMapping.findForward( ACTION_FORWARD_FAILURE );
                }
            }
            
            certificate.setType( Constants.CERTIFICATE_TYPE_LOCAL );
            certificate.setName( stagedCert.getName() );
            certificate.setModifiedDate( new Date() );
            certificate.setCreatedDate( new Date() );
            certificate.setBinaryData( stagedCert.getBinaryData() );
            certificate.setPassword( stagedCert.getPassword() );
            certificate.setPartner( localPartner );
            localPartner.getCertificates().add( certificate );
            Engine.getInstance().getActiveConfigurationAccessService().updateCertificate( certificate );
        }

        return actionMapping.findForward( ACTION_FORWARD_SUCCESS );
    }

}
