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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.bouncycastle.jce.PKCS10CertificationRequest;
import org.bouncycastle.jce.X509Principal;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.Constants;
import org.nexuse2e.pojo.CertificatePojo;
import org.nexuse2e.ui.action.NexusE2EAction;
import org.nexuse2e.ui.form.ProtectedFileAccessForm;
import org.nexuse2e.util.CertificateUtil;
import org.nexuse2e.util.EncryptionUtil;

/**
 * @author gesch
 *
 */
public class RequestSaveCertAction extends NexusE2EAction {

    private static String URL     = "request.error.url";
    private static String TIMEOUT = "request.error.timeout";

    /* (non-Javadoc)
     * @see com.tamgroup.nexus.e2e.ui.action.NexusE2EAction#executeNexusE2EAction(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.apache.struts.action.ActionMessages)
     */
    @Override
    public ActionForward executeNexusE2EAction( ActionMapping actionMapping, ActionForm actionForm,
            HttpServletRequest request, HttpServletResponse response, ActionMessages errors, ActionMessages messages )
            throws Exception {

        ActionForward complete = actionMapping.findForward( "complete" );
        ActionForward incomplete = actionMapping.findForward( "incomplete" );

        ProtectedFileAccessForm form = (ProtectedFileAccessForm) actionForm;

        try {
            if ( ( form.getCertficate() == null ) || ( form.getCertficate().getFileData() == null ) ) {
                ActionMessage errormessage = new ActionMessage( "cacerts.certfilenotfound",
                        "No data for certificate file submitted!" );
                errors.add( ActionMessages.GLOBAL_MESSAGE, errormessage );
                addRedirect( request, URL, TIMEOUT );
                return incomplete;
            }
            byte[] data = form.getCertficate().getFileData();

            CertificatePojo certificateRequest = Engine.getInstance().getActiveConfigurationAccessService()
                    .getFirstCertificateByType( Constants.CERTIFICATE_TYPE_REQUEST, true );
            CertificatePojo certificateKey = Engine.getInstance().getActiveConfigurationAccessService().getCertificateByName(
                    Constants.CERTIFICATE_TYPE_PRIVATE_KEY, certificateRequest.getName() );
            LOG.trace( "certificateKey: " + certificateKey );

            Object[] result = CertificateUtil.getLocalCertificateRequestFromPojo( certificateRequest );
            LOG.trace( "RequestInfo.subject: "
                    + ( (PKCS10CertificationRequest) result[CertificateUtil.POS_REQUEST] )
                            .getCertificationRequestInfo().getSubject().toString() );

            X509Principal certSubject = CertificateUtil.getMissingCertificateSubjectDNFromKeyStore(
                    (PKCS10CertificationRequest) result[CertificateUtil.POS_REQUEST], certificateKey );
            LOG.debug( "certSubject: " + certSubject );
            if ( CertificateUtil.isCertificateCNMatchingMissingCN( data, certSubject ) ) {
            //if ( CertificateUtil.isCertificateMatchingMissingSubjectDN( data, certSubject ) ) {
                //CertificatePojo requestPojo = Engine.getInstance().getConfigAccessService().getFirstCertificateByType( Constants.CERTIFICATE_TYPE_REQUEST, true );

                LOG.trace( "certificatePojo: " + certificateKey );

                if ( !CertificateUtil.addCertificateToTempKeyStore( data, certificateKey ) ) {
                    ActionMessage errorMessage = new ActionMessage( "generic.error",
                            "Error while adding the selected Certificate to your pending request!" );
                    errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
                    addRedirect( request, URL, TIMEOUT );
                    return incomplete;
                }
                Engine.getInstance().getActiveConfigurationAccessService().updateCertificate( certificateKey );
                if ( CertificateUtil.getMissingCertificateSubjectDNFromKeyStore(
                        (PKCS10CertificationRequest) result[CertificateUtil.POS_REQUEST], certificateKey ) == null ) {

                    KeyStore jks;
                    try {
                        jks = KeyStore.getInstance( CertificateUtil.DEFAULT_KEY_STORE,
                                CertificateUtil.DEFAULT_JCE_PROVIDER );
                    } catch ( KeyStoreException e4 ) {
                        ActionMessage errorMessage = new ActionMessage( "generic.error",
                                "Error while creating new Keystore: " + e4.getMessage() );
                        errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
                        addRedirect( request, URL, TIMEOUT );
                        return incomplete;
                    } catch ( NoSuchProviderException e4 ) {
                        ActionMessage errorMessage = new ActionMessage( "generic.error",
                                "The specified security Provider is not available: " + e4.getMessage() );
                        errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
                        addRedirect( request, URL, TIMEOUT );
                        return incomplete;
                    }
                    String pwd = EncryptionUtil.decryptString( certificateKey.getPassword() );
                    try {
                        jks.load( new ByteArrayInputStream( certificateKey.getBinaryData() ), pwd.toCharArray() );
                    } catch ( NoSuchAlgorithmException e5 ) {
                        ActionMessage errorMessage = new ActionMessage( "generic.error",
                                "The requested Algorithm is not available: " + e5.getMessage() );
                        errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
                        addRedirect( request, URL, TIMEOUT );
                        return incomplete;
                    } catch ( CertificateException e5 ) {
                        ActionMessage errorMessage = new ActionMessage( "generic.error",
                                "Error while processing certificate: " + e5.getMessage() );
                        errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
                        addRedirect( request, URL, TIMEOUT );
                        return incomplete;
                    } catch ( IOException e5 ) {
                        ActionMessage errorMessage = new ActionMessage( "generic.error",
                                "Error while reading certificate data: " + e5.getMessage() );
                        errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
                        addRedirect( request, URL, TIMEOUT );
                        return incomplete;
                    }
                    Certificate[] certs;
                    try {
                        certs = jks.getCertificateChain( CertificateUtil.DEFAULT_CERT_ALIAS );
                    } catch ( KeyStoreException e6 ) {
                        ActionMessage errorMessage = new ActionMessage( "generic.error",
                                "Error while processing Keychain (Alias:" + CertificateUtil.DEFAULT_CERT_ALIAS + "): "
                                        + e6.getMessage() );
                        errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
                        addRedirect( request, URL, TIMEOUT );
                        return incomplete;
                    }
                    if ( certs != null && certs.length > 0 ) {
                        String cn = CertificateUtil.getCertificateCN( (X509Certificate) certs[0], true );
                        String o = CertificateUtil.getCertificateO( (X509Certificate) certs[0], true );
                        String ou = CertificateUtil.getCertificateOU( (X509Certificate) certs[0], true );
                        String st = CertificateUtil.getCertificateST( (X509Certificate) certs[0], true );
                        String c = CertificateUtil.getCertificateC( (X509Certificate) certs[0], true );
                        String l = CertificateUtil.getCertificateL( (X509Certificate) certs[0], true );
                        String e = CertificateUtil.getCertificateE( (X509Certificate) certs[0], true );

                        CertificatePojo newStaging = new CertificatePojo();
                        newStaging.setName( cn );
                        newStaging.setType( Constants.CERTIFICATE_TYPE_STAGING );
                        newStaging.setBinaryData( certificateKey.getBinaryData() );
                        newStaging.setPassword( certificateKey.getPassword() );
                        newStaging.setCreatedDate( new Date() );
                        newStaging.setModifiedDate( new Date() );
                        try {
                            Engine.getInstance().getActiveConfigurationAccessService().updateCertificate( newStaging );
                        } catch ( NexusException e7 ) {
                            ActionMessage errorMessage = new ActionMessage( "generic.error",
                                    "Error while saving completed Certificate: " + e7.getMessage() );
                            errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
                            addRedirect( request, URL, TIMEOUT );
                            return incomplete;
                        }

                        PrivateKey privateKey;
                        try {
                            privateKey = (PrivateKey) jks
                                    .getKey( CertificateUtil.DEFAULT_CERT_ALIAS, pwd.toCharArray() );
                        } catch ( KeyStoreException e8 ) {
                            ActionMessage errorMessage = new ActionMessage( "generic.error",
                                    "Error while processing Private Key: " + e8.getMessage() );
                            errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
                            addRedirect( request, URL, TIMEOUT );
                            return incomplete;
                        } catch ( NoSuchAlgorithmException e8 ) {
                            ActionMessage errorMessage = new ActionMessage( "generic.error",
                                    "The specified Algorithm is not available: " + e8.getMessage() );
                            errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
                            addRedirect( request, URL, TIMEOUT );
                            return incomplete;
                        } catch ( UnrecoverableKeyException e8 ) {
                            ActionMessage errorMessage = new ActionMessage( "generic.error",
                                    "Error while recovering Private Key from Keystore: " + e8.getMessage() );
                            errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
                            addRedirect( request, URL, TIMEOUT );
                            return incomplete;
                        }
                        PublicKey publicKey = certs[0].getPublicKey();
                        Certificate selfsigned;
                        try {
                            selfsigned = CertificateUtil.createSelfSignedCert( cn, o, ou, c, st, l, e, publicKey,
                                    privateKey );
                        } catch ( Exception e9 ) {
                            ActionMessage errorMessage = new ActionMessage( "generic.error",
                                    "Error while creating selfsigned Certificate: " + e9.getMessage() );
                            errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
                            addRedirect( request, URL, TIMEOUT );
                            return incomplete;
                        }
                        Certificate[] selfArray = { selfsigned};
                        try {
                            jks.setKeyEntry( CertificateUtil.DEFAULT_CERT_ALIAS, privateKey, pwd.toCharArray(),
                                    selfArray );
                        } catch ( KeyStoreException e10 ) {
                            ActionMessage errorMessage = new ActionMessage( "generic.error",
                                    "Error while preparing certificate chain: " + e10.getMessage() );
                            errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
                            addRedirect( request, URL, TIMEOUT );
                            return incomplete;
                        }
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        try {
                            jks.store( baos, pwd.toCharArray() );
                        } catch ( KeyStoreException e11 ) {
                            ActionMessage errorMessage = new ActionMessage( "generic.error",
                                    "Error while processing request keystore: " + e11.getMessage() );
                            errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
                            addRedirect( request, URL, TIMEOUT );
                            return incomplete;
                        } catch ( NoSuchAlgorithmException e11 ) {
                            ActionMessage errorMessage = new ActionMessage( "generic.error",
                                    "The specified Alogrithm is not available: " + e11.getMessage() );
                            errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
                            addRedirect( request, URL, TIMEOUT );
                            return incomplete;
                        } catch ( CertificateException e11 ) {
                            ActionMessage errorMessage = new ActionMessage( "generic.error",
                                    "Error while processing the request data: " + e11.getMessage() );
                            errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
                            addRedirect( request, URL, TIMEOUT );
                            return incomplete;
                        } catch ( IOException e11 ) {
                            ActionMessage errorMessage = new ActionMessage( "generic.error",
                                    "Error while saving request data: " + e11.getMessage() );
                            errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
                            addRedirect( request, URL, TIMEOUT );
                            return incomplete;
                        }
                        certificateKey.setBinaryData( baos.toByteArray() );
                        try {
                            Engine.getInstance().getActiveConfigurationAccessService().updateCertificate( certificateKey );
                        } catch ( NexusException e12 ) {
                            ActionMessage errorMessage = new ActionMessage( "generic.error",
                                    "Error while updating request informations: " + e12.getMessage() );
                            errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
                            addRedirect( request, URL, TIMEOUT );
                            return incomplete;
                        }

                    }
                    return complete;
                }
            } else {
                String cn = "";

                X509Certificate x509Cert;
                try {
                    x509Cert = CertificateUtil.getX509Certificate( data );
                } catch ( NexusException e4 ) {
                    ActionMessage errorMessage = new ActionMessage( "generic.error",
                            "Error while processing certificate: " + e4.getMessage() );
                    errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
                    addRedirect( request, URL, TIMEOUT );
                    return incomplete;
                }
                String subject = x509Cert.getSubjectDN().getName();
                LOG.trace( "subject: " + subject );
                StringTokenizer st = new StringTokenizer( subject, "," );
                while ( st.hasMoreTokens() ) {
                    String temp = st.nextToken().trim();
                    if ( temp.startsWith( "CN=" ) ) {
                        cn = "(" + temp.substring( 3 ) + ")";
                    }
                }

                ActionMessage errorMessage = new ActionMessage( "generic.error", "Subject of your selected Certificate"
                        + cn + " does not match: " + certSubject );

                LOG.trace( "cn: >" + cn + "<" );
                LOG.trace( "certSubject: >" + certSubject + "<" );

                errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
                addRedirect( request, URL, TIMEOUT );
                return incomplete;
            }
        } catch ( Exception e ) {
            ActionMessage errorMessage = new ActionMessage( "generic.error", "Error while importing Certificate: "
                    + e.getMessage() );
            errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
            addRedirect( request, URL, TIMEOUT );
            e.printStackTrace();
            return incomplete;
        } catch ( Error e ) {
            ActionMessage errorMessage = new ActionMessage( "generic.error", "Error while importing Certificate: "
                    + e.getMessage() );
            errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
            addRedirect( request, URL, TIMEOUT );
            e.printStackTrace();
            return incomplete;
        }

        return incomplete;
    }
}
