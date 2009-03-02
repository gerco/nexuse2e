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
package org.nexuse2e.ui.action.communications;

import java.io.ByteArrayInputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
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
import org.nexuse2e.pojo.CertificatePojo;
import org.nexuse2e.pojo.PartnerPojo;
import org.nexuse2e.ui.action.NexusE2EAction;
import org.nexuse2e.ui.form.CertificatePromotionForm;
import org.nexuse2e.ui.form.CertificatePropertiesForm;
import org.nexuse2e.util.CertificateUtil;
import org.nexuse2e.util.EncryptionUtil;

/**
 * @author gesch
 *
 */
public class StagingCertViewAction extends NexusE2EAction {

    private static String URL     = "staging.error.url";
    private static String TIMEOUT = "staging.error.timeout";

    /* (non-Javadoc)
     * @see com.tamgroup.nexus.e2e.ui.action.NexusE2EAction#executeNexusE2EAction(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.apache.struts.action.ActionMessages)
     */
    @Override
    public ActionForward executeNexusE2EAction( ActionMapping actionMapping, ActionForm actionForm,
            HttpServletRequest request, HttpServletResponse response, EngineConfiguration engineConfiguration, ActionMessages errors, ActionMessages messages )
            throws Exception {

        ActionForward succes = actionMapping.findForward( ACTION_FORWARD_SUCCESS );
        ActionForward error = actionMapping.findForward( ACTION_FORWARD_FAILURE );

        CertificatePromotionForm form = (CertificatePromotionForm) actionForm;

        try {
            int nxCertificateId = form.getNxCertificateId();
            if ( nxCertificateId == 0 ) {
                ActionMessage errorMessage = new ActionMessage( "generic.error", "CN not found." );
                errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
                addRedirect( request, URL, TIMEOUT );
                return error;
            }
            List<CertificatePropertiesForm> certificateParts = new ArrayList<CertificatePropertiesForm>();
            List<PartnerPojo> localPartners = new ArrayList<PartnerPojo>();

            CertificatePojo cPojo = engineConfiguration
                    .getCertificateByNxCertificateId( Constants.CERTIFICATE_TYPE_ALL, nxCertificateId );

            KeyStore jks = KeyStore.getInstance( CertificateUtil.DEFAULT_KEY_STORE,
                    CertificateUtil.DEFAULT_JCE_PROVIDER );
            jks.load( new ByteArrayInputStream( cPojo.getBinaryData() ), EncryptionUtil.decryptString(
                    cPojo.getPassword() ).toCharArray() );
            if ( jks != null ) {

                Enumeration<String> aliases = jks.aliases();
                if ( !aliases.hasMoreElements() ) {
                    ActionMessage errorMessage = new ActionMessage( "generic.error", "No certificate aliases found" );
                    errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
                    addRedirect( request, URL, TIMEOUT );
                    return error;
                }
                while ( aliases.hasMoreElements() ) {
                    String tempAlias = (String) aliases.nextElement();
                    if ( jks.isKeyEntry( tempAlias ) ) {
                        Certificate[] certArray = jks.getCertificateChain( tempAlias );
                        if ( certArray != null ) {

                            for ( int i = 0; i < certArray.length; i++ ) {
                                CertificatePropertiesForm certForm = new CertificatePropertiesForm();
                                X509Certificate x509 = (X509Certificate) certArray[i];
                                certForm.setCertificateProperties( x509 );

                                certificateParts.add( certForm );
                            }
                        }
                    }
                }
                localPartners = engineConfiguration.getPartners(
                        Constants.PARTNER_TYPE_LOCAL, Constants.PARTNERCOMPARATOR );

            }
            form.setCertificateParts( certificateParts );
            form.setLocalPartners( localPartners );
            //request.setAttribute( ATTRIBUTE_COLLECTION, certs );

        } catch ( Exception e ) {
            e.printStackTrace();
            ActionMessage errorMessage = new ActionMessage( "generic.error", e.getMessage() );
            errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
            addRedirect( request, URL, TIMEOUT );
            return error;
        }
        //request.getSession().setAttribute( Crumbs.CURRENT_LOCATION, Crumbs.STAGING_SHOW_CERT );

        return succes;
    }
}
