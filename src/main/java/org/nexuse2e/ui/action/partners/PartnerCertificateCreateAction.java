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
package org.nexuse2e.ui.action.partners;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.nexuse2e.configuration.Constants;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.pojo.CertificatePojo;
import org.nexuse2e.pojo.PartnerPojo;
import org.nexuse2e.ui.action.NexusE2EAction;
import org.nexuse2e.ui.form.ProtectedFileAccessForm;
import org.nexuse2e.util.CertificateUtil;

/**
 * @author gesch
 *
 */
public class PartnerCertificateCreateAction extends NexusE2EAction {

    private static String URL     = "partner.error.url";
    private static String TIMEOUT = "partner.error.timeout";

    /* (non-Javadoc)
     * @see com.tamgroup.nexus.e2e.ui.action.NexusE2EAction#executeNexusE2EAction(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.apache.struts.action.ActionMessages)
     */
    @Override
    public ActionForward executeNexusE2EAction( ActionMapping actionMapping, ActionForm actionForm,
            HttpServletRequest request, HttpServletResponse response, EngineConfiguration engineConfiguration, ActionMessages errors, ActionMessages messages )
            throws Exception {

        ActionForward success = actionMapping.findForward( ACTION_FORWARD_SUCCESS );
        ActionForward error = actionMapping.findForward( ACTION_FORWARD_SUCCESS );

        ProtectedFileAccessForm form = (ProtectedFileAccessForm) actionForm;

        String partnerId = form.getId();
        String alias = form.getAlias();
        String fileName = "File name n/a";

        // LOG.trace( "partner:" + partnerId );
        // LOG.trace( "alias:" + alias );
        // LOG.trace( "path:" + path );
        if ( partnerId == null || partnerId.equals( "" ) ) {
            ActionMessage errorMessage = new ActionMessage( "generic.error", "partnerId is invalid or doesn't exist!" );
            errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
            addRedirect( request, URL, TIMEOUT );
            return error;
        }
        if ( ( form.getCertficate() == null ) || ( form.getCertficate().getFileData() == null ) ) {
            ActionMessage errormessage = new ActionMessage( "cacerts.certfilenotfound",
                    "No data for certificate file submitted!" );
            errors.add( ActionMessages.GLOBAL_MESSAGE, errormessage );
            addRedirect( request, URL, TIMEOUT );
            return error;
        }
        byte[] data = form.getCertficate().getFileData();
        if ( form.getCertficate().getFileName() != null ) {
            fileName = form.getCertficate().getFileName();
        }
        try {
            // only for validation. 
            CertificateUtil.getX509Certificate( data );
        } catch ( Exception e ) {
            ActionMessage errorMessage = new ActionMessage( "generic.error", "Unable to unpack Certificate File ("
                    + fileName + ")!" );
            errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
            addRedirect( request, URL, TIMEOUT );
            return error;
        }

        PartnerPojo partner = engineConfiguration.getPartnerByPartnerId(
                partnerId );

        CertificatePojo cPojo = new CertificatePojo();
        cPojo.setType( Constants.CERTIFICATE_TYPE_PARTNER );
        cPojo.setName( alias );
        cPojo.setBinaryData( data );
        cPojo.setPartner( partner );
        cPojo.setModifiedDate( new Date() );
        cPojo.setCreatedDate( new Date() );
        //cPojo.setType( form.get );
        cPojo.setPartner( partner );

        partner.getCertificates().add( cPojo );
        engineConfiguration.getCertificates( Constants.CERTIFICATE_TYPE_ALL,
                null ).add( cPojo );
        engineConfiguration.updatePartner( partner );

        /*
         try {
         Engine.getInstance().getConfigAccessService().updatePartner( partner );
         } catch ( NexusException e ) {
         ActionMessage errorMessage = new ActionMessage( "generic.error", "Unable to create Certificate Entry:"
         + e.getMessage() );
         errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
         addRedirect( request, URL, TIMEOUT );
         return error;
         }
         */

        return success;
    }
}
