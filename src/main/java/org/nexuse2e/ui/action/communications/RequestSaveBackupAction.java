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
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.jce.PKCS10CertificationRequest;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.Constants;
import org.nexuse2e.pojo.CertificatePojo;
import org.nexuse2e.ui.action.NexusE2EAction;
import org.nexuse2e.ui.form.ProtectedFileAccessForm;
import org.nexuse2e.util.CertificateUtil;
import org.nexuse2e.util.EncryptionUtil;

/**
 * @author guido.esch
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class RequestSaveBackupAction extends NexusE2EAction {

    private static String URL             = "request.error.url";
    private static String TIMEOUT         = "request.error.timeout";

    private static int    PEM_LINE_LENGTH = 64;

    /* (non-Javadoc)
     * @see com.tamgroup.nexus.e2e.ui.action.NexusE2EAction#executeNexusE2EAction(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.apache.struts.action.ActionMessages)
     */
    @Override
    public ActionForward executeNexusE2EAction( ActionMapping actionMapping, ActionForm actionForm,
            HttpServletRequest request, HttpServletResponse response, ActionMessages errors, ActionMessages messages )
            throws Exception {

        ActionForward success = actionMapping.findForward( ACTION_FORWARD_SUCCESS );
        ActionForward error = actionMapping.findForward( ACTION_FORWARD_FAILURE );

        try {
            ProtectedFileAccessForm form = (ProtectedFileAccessForm) actionForm;
            // String path = form.getCertficatePath();
            String pwd = form.getPassword();

            KeyStore pkcs12 = KeyStore.getInstance( CertificateUtil.DEFAULT_KEY_STORE,
                    CertificateUtil.DEFAULT_JCE_PROVIDER );

            if ( ( form.getCertficate() == null ) || ( form.getCertficate().getFileData() == null ) ) {
                ActionMessage errormessage = new ActionMessage( "cacerts.certfilenotfound",
                        "No data for certificate file submitted!" );
                errors.add( ActionMessages.GLOBAL_MESSAGE, errormessage );
                addRedirect( request, URL, TIMEOUT );
                return error;
            }
            byte[] data = form.getCertficate().getFileData();

            try {
                ByteArrayInputStream bais = new ByteArrayInputStream( data );
                pkcs12.load( bais, pwd.toCharArray() );
            } catch ( IOException e1 ) {
                ActionMessage errorMessage = new ActionMessage( "generic.error", "Unable to read the selected File ("
                        + e1.getMessage() + ")" );
                errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
                addRedirect( request, URL, TIMEOUT );
                return error;
            } catch ( NoSuchAlgorithmException e1 ) {
                ActionMessage errorMessage = new ActionMessage( "generic.error",
                        "The reqired Algorithm for this Certificate is not supported" );
                errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
                addRedirect( request, URL, TIMEOUT );
                return error;
            } catch ( CertificateException e1 ) {
                ActionMessage errorMessage = new ActionMessage( "generic.error", "Error while processing Certificate: "
                        + e1.getMessage() );
                errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
                addRedirect( request, URL, TIMEOUT );
                return error;
            } catch ( ClassCastException e1 ) {
                ActionMessage errorMessage = new ActionMessage( "generic.error",
                        "Unable to read the keystore file. Selected File is not a PKCS12 Keystore" );
                errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
                addRedirect( request, URL, TIMEOUT );
                return error;
            } catch ( Exception e1 ) {
                ActionMessage errorMessage = new ActionMessage( "generic.error", "Unable to read the selceted File ("
                        + e1.getMessage() + ")" );
                errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
                addRedirect( request, URL, TIMEOUT );
                return error;
            }
            Enumeration<String> aliases;
            try {
                aliases = pkcs12.aliases();
            } catch ( KeyStoreException e1 ) {
                ActionMessage errorMessage = new ActionMessage( "generic.error",
                        "Error while extracting Aliases from Keystore: " + e1.getMessage() );
                errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
                addRedirect( request, URL, TIMEOUT );
                return error;
            }
            String alias = aliases.nextElement();
            // LOG.trace( "Using alias:" + alias );
            Certificate[] certs;
            try {
                certs = pkcs12.getCertificateChain( alias );
            } catch ( KeyStoreException e1 ) {
                ActionMessage errorMessage = new ActionMessage( "generic.error",
                        "Error while processing Chain (Aliases:" + alias + "): " + e1.getMessage() );
                errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
                addRedirect( request, URL, TIMEOUT );
                return error;
            }
            while ( aliases.hasMoreElements() ) {
                String alias2 = aliases.nextElement();
                // LOG.trace( "other aliases:" + alias2 );
            }

            X509Certificate cert = (X509Certificate) certs[0];
            String commonName = CertificateUtil.getCertificateCN( cert, true );
            if ( commonName == null ) {
                commonName = "";
            } else {
                if ( commonName.startsWith( "SELFSIGNED-TEMPORARY:" ) ) {
                    commonName = commonName.substring( 21 );
                    // LOG.trace( "commonName:" + commonName );
                }
            }
            String country = CertificateUtil.getCertificateC( cert, true );
            if ( country == null ) {
                country = "";
            }
            String organization = CertificateUtil.getCertificateO( cert, true );
            if ( organization == null ) {
                organization = "";
            }
            String organizationUnit = CertificateUtil.getCertificateOU( cert, true );
            if ( organizationUnit == null ) {
                organizationUnit = "";
            }
            String location = CertificateUtil.getCertificateL( cert, true );
            if ( location == null ) {
                location = "";
            }
            String state = CertificateUtil.getCertificateST( cert, true );
            if ( state == null ) {
                state = "";
            }
            String email = CertificateUtil.getCertificateE( cert, true );
            if ( email == null ) {
                email = "";
            }
            Hashtable<DERObjectIdentifier, String> attrs = new Hashtable<DERObjectIdentifier, String>();
            attrs.put( X509Name.CN, commonName );
            attrs.put( X509Name.C, country );
            attrs.put( X509Name.O, organization );
            attrs.put( X509Name.OU, organizationUnit );
            attrs.put( X509Name.L, location );
            attrs.put( X509Name.ST, state );
            attrs.put( X509Name.E, email );
            attrs.put( X509Name.EmailAddress, email );

            X509Name subject = new X509Name( attrs );

            PKCS10CertificationRequest certificationRequest;
            try {
                certificationRequest = new PKCS10CertificationRequest(
                        CertificateUtil.DEFAULT_DIGITAL_SIGNATURE_ALGORITHM, subject, cert.getPublicKey(), null,
                        (PrivateKey) pkcs12.getKey( alias, pwd.toCharArray() ) );
            } catch ( InvalidKeyException e2 ) {
                ActionMessage errorMessage = new ActionMessage( "generic.error",
                        "Error while generating PKCS10 Request. Key is invalid: " + e2.getMessage() );
                errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
                addRedirect( request, URL, TIMEOUT );
                return error;
            } catch ( NoSuchAlgorithmException e2 ) {
                ActionMessage errorMessage = new ActionMessage( "generic.error",
                        "The reqired Algorithm for this Certificate is not supported: " + e2.getMessage() );
                errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
                addRedirect( request, URL, TIMEOUT );
                return error;
            } catch ( NoSuchProviderException e2 ) {
                ActionMessage errorMessage = new ActionMessage( "generic.error",
                        "The specified security Providor is not supported:" + e2.getMessage() );
                errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
                addRedirect( request, URL, TIMEOUT );
                return error;
            } catch ( SignatureException e2 ) {
                ActionMessage errorMessage = new ActionMessage( "generic.error", "Error while applying signature: "
                        + e2.getMessage() );
                errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
                addRedirect( request, URL, TIMEOUT );
                return error;
            } catch ( KeyStoreException e2 ) {
                ActionMessage errorMessage = new ActionMessage( "generic.error",
                        "Error while extracting Private Key from Keystore: " + e2.getMessage() );
                errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
                addRedirect( request, URL, TIMEOUT );
                return error;
            } catch ( UnrecoverableKeyException e2 ) {
                ActionMessage errorMessage = new ActionMessage( "generic.error",
                        "Unable to recover Private Key from Backup Keystore: " + e2.getMessage() );
                errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
                addRedirect( request, URL, TIMEOUT );
                return error;
            }

            CertificatePojo certificatePojo;
            try {
                certificatePojo = Engine.getInstance().getActiveConfigurationAccessService().getFirstCertificateByType(
                        Constants.CERTIFICATE_TYPE_REQUEST, true );
            } catch ( NexusException e2 ) {
                ActionMessage errorMessage = new ActionMessage( "generic.error",
                        "Error while reading Certificate from Database: " + e2.getMessage() );
                errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
                addRedirect( request, URL, TIMEOUT );
                return error;
            }
            if ( certificatePojo != null ) {
                try {
                    Engine.getInstance().getActiveConfigurationAccessService().deleteCertificate( Constants.CERTIFICATE_TYPE_ALL,
                            certificatePojo );
                } catch ( NexusException e2 ) {
                    ActionMessage errorMessage = new ActionMessage( "generic.error",
                            "Error while deleting old request data: " + e2.getMessage() );
                    errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
                    addRedirect( request, URL, TIMEOUT );
                    return error;
                }
            }

            certificatePojo = new CertificatePojo();
            certificatePojo.setName( certificationRequest.getCertificationRequestInfo().getSubject().toString() );
            certificatePojo.setType( Constants.CERTIFICATE_TYPE_REQUEST );
            certificatePojo.setBinaryData( certificationRequest.getEncoded() );
            certificatePojo.setPassword( EncryptionUtil.encryptString( pwd ) );
            try {
                Engine.getInstance().getActiveConfigurationAccessService().updateCertificate( certificatePojo );
            } catch ( NexusException e2 ) {
                ActionMessage errorMessage = new ActionMessage( "generic.error", "Unable to save Request Data: "
                        + e2.getMessage() );
                errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
                addRedirect( request, URL, TIMEOUT );
                return error;
            }

            CertificatePojo pkcs12Pojo;
            try {
                pkcs12Pojo = Engine.getInstance().getActiveConfigurationAccessService().getCertificateByName(
                        Constants.CERTIFICATE_TYPE_PRIVATE_KEY, certificatePojo.getName() );
            } catch ( NexusException e2 ) {
                ActionMessage errorMessage = new ActionMessage( "generic.error", "Unable to save Certificate Data: "
                        + e2.getMessage() );
                errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
                addRedirect( request, URL, TIMEOUT );
                return error;
            }
            if ( pkcs12Pojo != null ) {
                try {
                    Engine.getInstance().getActiveConfigurationAccessService().deleteCertificate( Constants.CERTIFICATE_TYPE_ALL,
                            pkcs12Pojo );
                } catch ( NexusException e2 ) {
                    ActionMessage errorMessage = new ActionMessage( "generic.error",
                            "Error while deleting old certificate keystore: " + e2.getMessage() );
                    errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
                    addRedirect( request, URL, TIMEOUT );
                    return error;
                }
            }

            pkcs12Pojo = Engine.getInstance().getActiveConfigurationAccessService().getCertificateByName(
                    Constants.CERTIFICATE_TYPE_PRIVATE_KEY, certificatePojo.getName() );

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                pkcs12.store( baos, pwd.toCharArray() );
            } catch ( KeyStoreException e3 ) {
                ActionMessage errorMessage = new ActionMessage( "generic.error", "Error while saving Keystore: "
                        + e3.getMessage() );
                errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
                addRedirect( request, URL, TIMEOUT );
                return error;
            } catch ( NoSuchAlgorithmException e3 ) {
                ActionMessage errorMessage = new ActionMessage( "generic.error",
                        "Requested Algorithm is not supported: " + e3.getMessage() );
                errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
                addRedirect( request, URL, TIMEOUT );
                return error;
            } catch ( CertificateException e3 ) {
                ActionMessage errorMessage = new ActionMessage( "generic.error", "Error while Processing certificate: "
                        + e3.getMessage() );
                errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
                addRedirect( request, URL, TIMEOUT );
                return error;
            } catch ( IOException e3 ) {
                ActionMessage errorMessage = new ActionMessage( "generic.error", "Error while saving Keystore: "
                        + e3.getMessage() );
                errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
                addRedirect( request, URL, TIMEOUT );
                return error;
            }
            pkcs12Pojo.setBinaryData( baos.toByteArray() );
            pkcs12Pojo.setName( "PKCS12" );
            pkcs12Pojo.setPassword( EncryptionUtil.encryptString( pwd ) );
            try {
                Engine.getInstance().getActiveConfigurationAccessService().updateCertificate( pkcs12Pojo );
            } catch ( NexusException e4 ) {
                ActionMessage errorMessage = new ActionMessage( "generic.error", "Unable to save Keystore Data: "
                        + e4.getMessage() );
                errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
                addRedirect( request, URL, TIMEOUT );
                return error;
            }

        } catch ( Exception e ) {
            ActionMessage errorMessage = new ActionMessage( "generic.error", "Exception while importing backup: "
                    + e.getMessage() );
            errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
            addRedirect( request, URL, TIMEOUT );
            e.printStackTrace();
            return error;
        } catch ( Error e ) {
            ActionMessage errorMessage = new ActionMessage( "generic.error", "Error while importing backup: "
                    + e.getMessage() );
            errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
            addRedirect( request, URL, TIMEOUT );
            e.printStackTrace();
            return error;
        }
        return success;
    }
}
