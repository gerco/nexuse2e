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

import java.io.ByteArrayInputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.nexuse2e.configuration.ConfigurationAccessService;
import org.nexuse2e.configuration.Constants;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.pojo.CertificatePojo;
import org.nexuse2e.ui.action.NexusE2EAction;
import org.nexuse2e.ui.form.ProtectedFileAccessForm;
import org.nexuse2e.util.CertificateUtil;

/**
 * @author gesch
 *
 */
public class StagingSaveCertificateAction extends NexusE2EAction {

    private static String URL     = "staging.error.url";
    private static String TIMEOUT = "staging.error.timeout";

    /* (non-Javadoc)
     * @see com.tamgroup.nexus.e2e.ui.action.NexusE2EAction#executeNexusE2EAction(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.apache.struts.action.ActionMessages)
     */
    @Override
    public ActionForward executeNexusE2EAction( ActionMapping actionMapping, ActionForm actionForm,
            HttpServletRequest request, HttpServletResponse response, EngineConfiguration engineConfiguration, ActionMessages errors, ActionMessages messages )
            throws Exception {

        ActionForward success = actionMapping.findForward( ACTION_FORWARD_SUCCESS );
        ActionForward error = actionMapping.findForward( ACTION_FORWARD_FAILURE );

        ProtectedFileAccessForm form = (ProtectedFileAccessForm) actionForm;
        if ( ( form.getCertficate() == null ) || ( form.getCertficate().getFileData() == null ) ) {
            ActionMessage errormessage = new ActionMessage( "cacerts.certfilenotfound",
                    "No data for certificate file submitted!" );
            errors.add( ActionMessages.GLOBAL_MESSAGE, errormessage );
            addRedirect( request, URL, TIMEOUT );
            return error;
        }
        byte[] data = form.getCertficate().getFileData();

        KeyStore jks = KeyStore.getInstance( CertificateUtil.DEFAULT_KEY_STORE, CertificateUtil.DEFAULT_JCE_PROVIDER );
        try {
            jks.load( new ByteArrayInputStream( data ), form.getPassword().toCharArray() );
        } catch ( Exception e ) {
            ActionMessage errorMessage = new ActionMessage( "generic.error", e.getMessage() );
            errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
            addRedirect( request, URL, TIMEOUT );
            return error;
        }

        try {
            List<CertificatePojo> certificates = new ArrayList<CertificatePojo>();
            CertificatePojo certificate = CertificateUtil.createPojoFromPKCS12( Constants.CERTIFICATE_TYPE_STAGING,
                    jks, form.getPassword() );
            certificates.add( certificate );

            // check if root certificate is in the CA cert list
            // if not, add it to the CA list
            Certificate[] chain = CertificateUtil.getCertificateChain( jks );
            if (chain != null && chain.length > 0) {
                for (int i = 0; i < chain.length; i++) {
                    X509Certificate signer = (X509Certificate) chain[i];

                    if (i > 0 || CertificateUtil.isSelfSigned( signer )) {
                        String fingerprint = CertificateUtil.getMD5Fingerprint( signer );
                        ConfigurationAccessService cas = engineConfiguration;
                        boolean caInCaStore = false;
                        for (CertificatePojo cert : cas.getCertificates( Constants.CERTIFICATE_TYPE_CA, null )) {
                            data = cert.getBinaryData();
                            if (data != null) {
                                X509Certificate x509Certificate = CertificateUtil.getX509Certificate( data );
                                if (x509Certificate != null) {
                                    String fp = CertificateUtil.getMD5Fingerprint( x509Certificate );
                                    if (fp != null && fp.equals( fingerprint )) {
                                        caInCaStore = true;
                                        break;
                                    }
                                }
                            }
                        }
                        if (!caInCaStore) {
                            certificates.add( CertificateUtil.createPojoFromX509( signer, Constants.CERTIFICATE_TYPE_CA ) );
    
                        }
                    }
                }
            }
            engineConfiguration.updateCertificates( certificates );
        } catch ( Exception e ) {
            ActionMessage errorMessage = new ActionMessage( "generic.error", e.getMessage() );
            errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
            addRedirect( request, URL, TIMEOUT );
            return error;
        }

        return success;
    }

}
