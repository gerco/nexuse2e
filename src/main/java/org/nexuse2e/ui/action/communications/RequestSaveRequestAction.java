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

import java.io.File;
import java.io.FileOutputStream;
import java.security.KeyPair;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.bouncycastle.jce.PKCS10CertificationRequest;
import org.nexuse2e.Engine;
import org.nexuse2e.pojo.CertificatePojo;
import org.nexuse2e.ui.action.NexusE2EAction;
import org.nexuse2e.ui.form.CertificateRequestForm;
import org.nexuse2e.util.CertificateUtil;

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
        String e = form.getEmail();
        String pwd = form.getPassword();
        String vpwd = form.getVerifyPWD();
        if ( pwd == null || pwd.length() == 0 || !pwd.equals( vpwd ) ) {
            errors.add( ActionMessages.GLOBAL_MESSAGE, new ActionMessage( "servercert.pwdnotequal" ) );
            addRedirect( request, URL, TIMEOUT );
            return error;
        }

        try {
            KeyPair keyPair = CertificateUtil.generateKeyPair();

            PKCS10CertificationRequest pkcs10Request = CertificateUtil.generatePKCS10CertificateRequest( keyPair, cn,
                    o, ou, l, c, s, e );

            
            // Request
            CertificatePojo certificate = CertificateUtil.createPojoFromPKCS10( pkcs10Request );
//            certificate.setBinaryData( pkcs10Request.getEncoded() );
//            certificate.setType( Constants.CERTIFICATE_TYPE_REQUEST );
//            certificate.setName( pkcs10Request.getCertificationRequestInfo().getSubject().toString() );
//            certificate.setPassword( EncryptionUtil.encryptString( pwd ) );

//            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            // read public key DER file

            //            File cakey = new File( "/Users/gesch/Desktop/testcertdata/ca.key" );

            // read private key DER file
            //            DataInputStream dis = new DataInputStream( new FileInputStream( cakey ) );
            //            byte[] privKeyBytes = new byte[(int) cakey.length()];
            //            dis.read( privKeyBytes );
            //            dis.close();

            //            PasswordFinder pw = new PasswordFinder() {
            //
            //                public char[] getPassword() {
            //
            //                    return "nexus".toCharArray();
            //                };
            //            };
            //            PEMReader reader = new PEMReader( new StringReader( new String( privKeyBytes ) ), pw );
            //            Object ob = reader.readObject();
            //            System.out.println("ob: "+ob.getClass().getName());
            //            KeyPair kp = (KeyPair)ob;
            //            System.out.println("priv:" +kp.getPrivate());
            //            System.out.println("pub: "+ kp.getPublic());

//            StringWriter sw = new StringWriter();
//            PEMWriter writer = new PEMWriter( sw, Constants.DEFAULT_JCE_PROVIDER );
//
//            SecureRandom sr = new SecureRandom();
//            writer.writeObject( keyPair.getPrivate(), "DES-EDE3-CBC", pwd.toCharArray(), sr );
//            writer.flush();
//            sw.flush();
//            sw.close();
//            System.out.println( "PEM: " + sw.getBuffer().toString() );

            //            KeyStore keyStore = KeyStore.getInstance( CertificateUtil.DEFAULT_KEY_STORE,
            //                    CertificateUtil.DEFAULT_JCE_PROVIDER );
            //            keyStore.load( null, null );
            //            keyStore.setKeyEntry( CertificateUtil.DEFAULT_CERT_ALIAS, ( (KeyPair) csr[CertificateUtil.POS_KEYS] )
            //                    .getPrivate(), pwd.toCharArray(), certs );
            //            keyStore.store( baos, pwd.toCharArray() );
            //            baos.close();
            CertificatePojo privateKeyPojo = CertificateUtil.createPojoFromKeyPair( keyPair, certificate.getName(), pwd );
//            privateKeyPojo.setType( Constants.CERTIFICATE_TYPE_PRIVATE_KEY );
//
//            privateKeyPojo.setBinaryData( sw.getBuffer().toString().getBytes() );
//            privateKeyPojo.setName( certificate.getName() );
//            privateKeyPojo.setPassword( EncryptionUtil.encryptString( pwd ) );
            
            List<CertificatePojo> certs = new ArrayList<CertificatePojo>();
            certs.add( certificate );
            certs.add( privateKeyPojo );
            
            File certbackup = new File( Engine.getInstance().getNexusE2ERoot(), "WEB-INF" );
            certbackup = new File( certbackup, "backup" );
            if(!certbackup.exists()) {
                certbackup.mkdirs();
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");
            String date = sdf.format( new Date() );
            
            File currentDir = new File(certbackup,"requestCreate_"+date);
            if(!currentDir.exists()) {
                currentDir.mkdirs();
            }
            File privKeyFile = new File(currentDir,"privKey.pem");
            FileOutputStream fos = new FileOutputStream(privKeyFile);
            fos.write( privateKeyPojo.getBinaryData() );
            fos.flush();
            fos.close();
            
            
            File requestFile = new File(currentDir,"request.pem");           
            fos = new FileOutputStream(requestFile);
            fos.write( CertificateUtil.getPemData( pkcs10Request ).getBytes() );
            fos.flush();
            fos.close();
            
            
            Engine.getInstance().getActiveConfigurationAccessService().updateCertificates( certs );
            
            
            
            
            
            
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }

        return success;
    }

}
