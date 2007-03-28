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

import java.io.ByteArrayOutputStream;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.cert.Certificate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.bouncycastle.jce.PKCS10CertificationRequest;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.Constants;
import org.nexuse2e.pojo.CertificatePojo;
import org.nexuse2e.ui.action.NexusE2EAction;
import org.nexuse2e.ui.form.CertificateRequestForm;
import org.nexuse2e.util.CertificateUtil;
import org.nexuse2e.util.EncryptionUtil;

/**
 * @author gesch
 *
 */
public class RequestSaveRequestAction extends NexusE2EAction {

    private static String URL     = "request.error.url";
    private static String TIMEOUT = "request.error.timeout";

    /* (non-Javadoc)
     * @see com.tamgroup.nexus.e2e.ui.action.NexusE2EAction#executeNexusE2EAction(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.apache.struts.action.ActionMessages)
     */
    @Override
    public ActionForward executeNexusE2EAction( ActionMapping actionMapping, ActionForm actionForm,
            HttpServletRequest request, HttpServletResponse response, ActionMessages errors, ActionMessages messages )
            throws Exception {

        ActionForward success = actionMapping.findForward( ACTION_FORWARD_SUCCESS );
        ActionForward error = actionMapping.findForward( ACTION_FORWARD_FAILURE );
        CertificateRequestForm form = (CertificateRequestForm) actionForm;

        String cn = form.getCommonName();
        String o = form.getOrganisation();
        String ou = form.getOrganisationUnit();
        String l = form.getLocation();
        String s = form.getState();
        String c = form.getCountryCode();
        //String e = form.getEmail();
        String pwd = form.getPassword();
        String vpwd = form.getVerifyPWD();
        if ( pwd == null || pwd.length() == 0 || !pwd.equals( vpwd ) ) {
            errors.add( ActionMessages.GLOBAL_MESSAGE, new ActionMessage( "servercert.pwdnotequal" ) );
            addRedirect( request, URL, TIMEOUT );
            return error;
        }
        Object[] csr = CertificateUtil.generatePKCS10CertificateRequest( cn, o, ou, l, c, s, null );
        if ( csr[CertificateUtil.POS_PEM] != null ) {
            //            certDao.saveLocalCertificateRequestAndPrivateKey(
            //                    (PKCS10CertificationRequest) csr[CertificateTools.POS_REQUEST],
            //                    (KeyPair) csr[CertificateTools.POS_KEYS], (Certificate) csr[CertificateTools.POS_CERT], pwd );

            boolean result = false;
            Certificate[] certs = { (Certificate) csr[CertificateUtil.POS_CERT]};
            try {

                // Request
                CertificatePojo certificate = new CertificatePojo();
                certificate.setBinaryData( ( (PKCS10CertificationRequest) csr[CertificateUtil.POS_REQUEST] )
                        .getEncoded() );
                certificate.setType( Constants.CERTIFICATE_TYPE_REQUEST );
                certificate.setName( ( (PKCS10CertificationRequest) csr[CertificateUtil.POS_REQUEST] )
                        .getCertificationRequestInfo().getSubject().toString() );
                certificate.setPassword( EncryptionUtil.encryptString( pwd ) );

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                KeyStore keyStore = KeyStore.getInstance( CertificateUtil.DEFAULT_KEY_STORE,
                        CertificateUtil.DEFAULT_JCE_PROVIDER );
                keyStore.load( null, null );
                keyStore.setKeyEntry( CertificateUtil.DEFAULT_CERT_ALIAS, ( (KeyPair) csr[CertificateUtil.POS_KEYS] )
                        .getPrivate(), pwd.toCharArray(), certs );
                keyStore.store( baos, pwd.toCharArray() );
                baos.close();
                CertificatePojo pkcs12 = new CertificatePojo();
                pkcs12.setType( Constants.CERTIFICATE_TYPE_PRIVATE_KEY );

                pkcs12.setBinaryData( baos.toByteArray() );
                pkcs12.setName( certificate.getName() );
                pkcs12.setPassword( EncryptionUtil.encryptString( pwd ) );

                Engine.getInstance().getActiveConfigurationAccessService().updateCertificate( certificate );
                Engine.getInstance().getActiveConfigurationAccessService().updateCertificate( pkcs12 );

                result = true;
            } catch ( NexusException e1 ) {
                e1.printStackTrace();
            }

        } else {
            ActionMessage errormessage = new ActionMessage( "generic.error", "invalid request information" );
            errors.add( ActionMessages.GLOBAL_MESSAGE, errormessage );
            addRedirect( request, URL, TIMEOUT );
            return error;
        }

        return success;
    }

}
