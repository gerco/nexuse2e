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
import java.io.File;
import java.io.FileOutputStream;
import java.io.StringReader;

import java.security.KeyPair;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.openssl.PasswordFinder;
import org.nexuse2e.Engine;
import org.nexuse2e.configuration.Constants;
import org.nexuse2e.pojo.CertificatePojo;
import org.nexuse2e.ui.action.NexusE2EAction;
import org.nexuse2e.ui.form.CertificateChainImportForm;
import org.nexuse2e.ui.form.CertificatePropertiesForm;
import org.nexuse2e.util.CertificateUtil;
import org.nexuse2e.util.EncryptionUtil;

public class RequestImportCertChainAction extends NexusE2EAction {

    @SuppressWarnings("unchecked")
    @Override
    public ActionForward executeNexusE2EAction( ActionMapping actionMapping, ActionForm actionForm,
            HttpServletRequest request, HttpServletResponse response, ActionMessages errors, ActionMessages messages )
            throws Exception {

        ActionForward accept = actionMapping.findForward( "accept" );
        ActionForward done = actionMapping.findForward( "done" );
        ActionForward error = actionMapping.findForward( ACTION_FORWARD_FAILURE );

        CertificateChainImportForm form = (CertificateChainImportForm) actionForm;

        Vector<CertificatePropertiesForm> caImports = (Vector<CertificatePropertiesForm>) request.getSession()
                .getAttribute( "cacertsimports" );
        Vector<CertificatePropertiesForm> certChain = (Vector<CertificatePropertiesForm>) request.getSession()
                .getAttribute( "chain" );

        if ( caImports != null ) {
            form.setCaImports( caImports );
        } else {
            form.setCaImports( new Vector<CertificatePropertiesForm>() );
        }
        if ( certChain != null ) {
            form.setCertChain( certChain );
        } else {
            form.setCertChain( new Vector<CertificatePropertiesForm>() );
        }

        if ( form != null && form.isAccept() ) {
            request.getSession().getAttribute( "" );
            System.out.println( "chain: " + form.getCertChain() );
            System.out.println( "caimports: " + form.getCaImports() );

            try {
                List<CertificatePojo> privKeys = Engine.getInstance().getActiveConfigurationAccessService()
                        .getCertificates( Constants.CERTIFICATE_TYPE_PRIVATE_KEY, null );
                if ( privKeys == null || privKeys.size() == 0 ) {
                    LOG.error( "no private key found in database!" );
                    return error;
                }
                if ( privKeys.size() > 1 ) {
                    LOG.warn( "more than one private key found in database ! using first one" );

                }

                KeyStore keyStore = KeyStore.getInstance( CertificateUtil.DEFAULT_KEY_STORE,
                        CertificateUtil.DEFAULT_JCE_PROVIDER );
                keyStore.load( null, null );
                CertificatePojo privKeyPojo = privKeys.get( 0 );

                Certificate[] certs = new Certificate[form.getCertChain().size()];
                for ( int i = 0; i < form.getCertChain().size(); i++ ) {
                    certs[i] = form.getCertChain().get( i ).getCert();
                }

                KeyPair kp = CertificateUtil.getKeyPair( privKeyPojo );
                keyStore.setKeyEntry( CertificateUtil.DEFAULT_CERT_ALIAS, kp.getPrivate(), EncryptionUtil
                        .decryptString( privKeyPojo.getPassword() ).toCharArray(), certs );
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                keyStore.store( baos, EncryptionUtil.decryptString( privKeyPojo.getPassword() ).toCharArray() );
                baos.close();

                CertificatePojo pkcs12Pojo = CertificateUtil.createPojoFromPKCS12( Constants.CERTIFICATE_TYPE_STAGING,
                        keyStore, EncryptionUtil.decryptString( privKeyPojo.getPassword() ) );

                List<CertificatePojo> updateableCertPojos = new ArrayList<CertificatePojo>();
                for ( CertificatePropertiesForm cpf : form.getCaImports() ) {
                    X509Certificate cert = cpf.getCert();
                    CertificatePojo caPojo = new CertificatePojo();
                    caPojo.setBinaryData( cert.getEncoded() );
                    caPojo.setCreatedDate( new Date() );
                    caPojo.setModifiedDate( new Date() );
                    caPojo.setName( CertificateUtil.createCertificateId( cert ) );
                    caPojo.setType( Constants.CERTIFICATE_TYPE_CA );
                    updateableCertPojos.add( caPojo );
                }
                updateableCertPojos.add( pkcs12Pojo );

                File certbackup = new File( Engine.getNexusE2ERoot(), "backup" );
                if ( !certbackup.exists() ) {
                    certbackup.mkdirs();
                }
                SimpleDateFormat sdf = new SimpleDateFormat( "yyMMddHHmmss" );
                String date = sdf.format( new Date() );

                File currentDir = new File( certbackup, "importChain_" + date );
                if ( !currentDir.exists() ) {
                    currentDir.mkdirs();
                }

                for ( CertificatePojo pojo : updateableCertPojos ) {

                    if ( pojo.isPKCS12() ) {
                        File keyChainFile = new File( currentDir, "nexusKeyChain.p12" );
                        FileOutputStream fos = new FileOutputStream( keyChainFile );
                        fos.write( pojo.getBinaryData() );
                        fos.flush();
                        fos.close();
                    } else if ( pojo.isX509() ) {
                        File keyChainFile = new File( currentDir, pojo.getName() + ".pem" );
                        FileOutputStream fos = new FileOutputStream( keyChainFile );
                        X509Certificate cert = CertificateUtil.getX509Certificate( pojo );
                        byte[] pem = CertificateUtil.getPemData( cert ).getBytes();
                        fos.write( pem );
                        fos.flush();
                        fos.close();
                    }
                }

                Engine.getInstance().getActiveConfigurationAccessService().updateCertificates( updateableCertPojos );

            } catch ( Exception e ) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch ( Error e ) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            //            keyStore.setKeyEntry( CertificateUtil.DEFAULT_CERT_ALIAS, ( (KeyPair) csr[CertificateUtil.POS_KEYS] )
            //                    .getPrivate(), pwd.toCharArray(), certs );
            //            keyStore.store( baos, pwd.toCharArray() );
            //            baos.close();

            return done;
        }

        return accept;
    }

}
