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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.MD5Digest;
import org.bouncycastle.util.encoders.Hex;
import org.nexuse2e.configuration.Constants;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.pojo.CertificatePojo;
import org.nexuse2e.ui.action.NexusE2EAction;
import org.nexuse2e.ui.form.ProtectedFileAccessForm;
import org.nexuse2e.util.CertificateUtil;
import org.nexuse2e.util.EncryptionUtil;

/**
 * @author gesch
 *
 */
public class StagingStoreExportedAction extends NexusE2EAction {

    private static String URL     = "staging.error.url";
    private static String TIMEOUT = "staging.error.timeout";

    // private int           PEM_LINE_LENGTH = 64;

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

        // request.setAttribute( "redirect", "true" );

        int status = form.getStatus();
        int format = form.getFormat();
        int content = form.getContent();
        int nxCertificateId = form.getNxCertificateId();
        String idStr = form.getId();
        if ( status == 1 ) {
            // Save with path
            String path = form.getCertficatePath();

            if ( path == null || path.equals( "" ) ) {
                ActionMessage errorMessage = new ActionMessage( "generic.error", "no destination specified!" );
                errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
                addRedirect( request, URL, TIMEOUT );
                request.setAttribute( "seqNo", idStr );
                return error;
            }
            try {
                CertificatePojo cPojo = engineConfiguration
                        .getCertificateByNxCertificateId( Constants.CERTIFICATE_TYPE_ALL, nxCertificateId );
                if ( cPojo == null ) {
                    ActionMessage errorMessage = new ActionMessage( "generic.error", "no certificate found!" );
                    errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
                    addRedirect( request, URL, TIMEOUT );
                    request.setAttribute( "seqNo", idStr );
                    return error;
                }
                KeyStore jks = KeyStore.getInstance( CertificateUtil.DEFAULT_KEY_STORE,
                        CertificateUtil.DEFAULT_JCE_PROVIDER );
                jks.load( new ByteArrayInputStream( cPojo.getBinaryData() ), EncryptionUtil.decryptString(
                        cPojo.getPassword() ).toCharArray() );
                
                Certificate[] certs = CertificateUtil.getCertificateChain( jks );
                if (certs == null) {
                    ActionMessage errorMessage = new ActionMessage( "generic.error",
                            "no valid certificate chain found!" );
                    errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
                    addRedirect( request, URL, TIMEOUT );
                    request.setAttribute( "seqNo", idStr );
                    return error;
                }

                File destFile = new File( path );
                // 
                if ( content == 1 ) {
                    X509Certificate cert = (X509Certificate) certs[0];
                    byte[] data = null;
                    if ( format == ProtectedFileAccessForm.PEM ) {
                        data = CertificateUtil.getPemData( cert ).getBytes();
                    } else if ( format == ProtectedFileAccessForm.DER ) {
                        data = cert.getEncoded();
                    }
                    FileOutputStream fos = new FileOutputStream( destFile );
                    fos.write( data );
                    fos.flush();
                    fos.close();
                    // ZIP
                } else if ( content == 2 ) {
                    FileOutputStream fos = new FileOutputStream( destFile );
                    ZipOutputStream zos = new ZipOutputStream( fos );
                    String ext = "";

                    if ( format == ProtectedFileAccessForm.PEM ) {
                        ext = ".pem";
                    } else {
                        ext = ".der";
                    }
                    ByteArrayOutputStream indexStream = new ByteArrayOutputStream();
                    PrintWriter pw = new PrintWriter( indexStream );

                    for ( int i = 0; i < certs.length; i++ ) {
                        String certName = CertificateUtil
                                .createCertificateId( (X509Certificate) certs[i] );
                        String cn = CertificateUtil.getSubject( (X509Certificate) certs[i], X509Name.CN );
                        String o = CertificateUtil.getSubject( (X509Certificate) certs[i], X509Name.O );
                        String fingerprint = "NA";
                        byte[] resBuf;
                        try {
                            Digest digest = new MD5Digest();
                            resBuf = new byte[digest.getDigestSize()];
                            digest.update( certs[i].getEncoded(), 0, certs[i].getEncoded().length );
                            digest.doFinal( resBuf, 0 );
                            fingerprint = new String( Hex.encode( resBuf ) );
                        } catch ( CertificateEncodingException e1 ) {
                        }

                        ZipEntry ze = new ZipEntry( certName + ext );
                        zos.putNextEntry( ze );
                        pw.println( "CommonName: " + cn );
                        pw.println( "Organisation: " + o );
                        SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
                        pw.println( "expires: " + sdf.format( ( (X509Certificate) certs[i] ).getNotAfter() ) + " - "
                                + CertificateUtil.getRemainingValidity( (X509Certificate) certs[i] ) );
                        pw.println( "FingerPrint: " + fingerprint );

                        if ( i < certs.length - 1 ) {
                            pw.println( "---------------------------------------------------------" );
                        }

                        byte[] data = new byte[0];
                        if ( format == ProtectedFileAccessForm.PEM ) {
                            data = CertificateUtil.getPemData( (X509Certificate) certs[i] ).getBytes();
                        } else {
                            data = certs[i].getEncoded();
                        }
                        zos.write( data );
                    }
                    pw.flush();
                    pw.close();

                    ZipEntry index = new ZipEntry( "index.txt" );
                    zos.putNextEntry( index );
                    zos.write( indexStream.toByteArray() );
                    zos.flush();
                    zos.close();
                    fos.flush();
                    fos.close();

                    // PKCS 12 - full
                } else if ( content == 3 ) {
                    FileOutputStream fos = new FileOutputStream( destFile );
                    fos.write( cPojo.getBinaryData() );
                    fos.flush();
                    fos.close();
                }
            } catch ( Exception e ) {
                e.printStackTrace();
                ActionMessage errorMessage = new ActionMessage( "generic.error", e.getMessage() );
                errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
                addRedirect( request, URL, TIMEOUT );
                request.setAttribute( "seqNo", idStr );
                return error;
            }
        } else {
            // Save as...
            addRedirect( request, "certificates.staging.export.url", "certificates.staging.export.timeout" );
        }

        return success;
    }

}
