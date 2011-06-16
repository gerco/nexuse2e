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
package org.nexuse2e.ui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.MD5Digest;
import org.bouncycastle.jce.PKCS10CertificationRequest;
import org.bouncycastle.util.encoders.Hex;
import org.nexuse2e.Engine;
import org.nexuse2e.configuration.Constants;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.pojo.CertificatePojo;
import org.nexuse2e.pojo.MessagePayloadPojo;
import org.nexuse2e.pojo.MessagePojo;
import org.nexuse2e.ui.action.NexusE2EAction;
import org.nexuse2e.ui.form.ProtectedFileAccessForm;
import org.nexuse2e.util.CertificateUtil;
import org.nexuse2e.util.EncryptionUtil;

/**
 * This class substitutes the former DataSaveAsServlet
 * for the purpose of access control and issues with not yet applied configuration changes.
 * 
 * This action implements exports of several certificate/key related files. 
 * 
 * @author Sebastian Schulze
 * @date 29.01.2009
 */
public class DataSaveAsAction extends NexusE2EAction {

    private static Logger     LOG              = Logger.getLogger( DataSaveAsAction.class );
    
    /* (non-Javadoc)
     * @see org.nexuse2e.ui.action.NexusE2EAction#executeNexusE2EAction(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.nexuse2e.configuration.EngineConfiguration, org.apache.struts.action.ActionMessages, org.apache.struts.action.ActionMessages)
     */
    @Override
    public ActionForward executeNexusE2EAction( ActionMapping actionMapping,
                                                ActionForm actionForm,
                                                HttpServletRequest request,
                                                HttpServletResponse response,
                                                EngineConfiguration engineConfiguration,
                                                ActionMessages errors,
                                                ActionMessages messages )
                                                                         throws Exception {
        String contentType = "application/unknown";
        
        LOG.debug( "type = " + request.getParameter( "type" ) );
        
        try {
            if ( request.getParameter( "type" ).equals( "temppkcs12" ) ) {
            } else if ( request.getParameter( "type" ).equals( "serverCert" ) ) {
            } else if ( request.getParameter( "type" ).equals( "cacerts" ) ) {

                List<CertificatePojo> certificates = engineConfiguration
                        .getCertificates( Constants.CERTIFICATE_TYPE_CA, null );

                KeyStore jks = CertificateUtil.generateKeyStoreFromPojos( certificates );
                CertificatePojo cPojo = engineConfiguration
                        .getFirstCertificateByType( Constants.CERTIFICATE_TYPE_CACERT_METADATA, true );
                String pwd = "changeit";
                if ( cPojo == null ) {
                    LOG.warn( "ca certificate metadata not found!" );
                } else {
                    pwd = EncryptionUtil.decryptString( cPojo.getPassword() );
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                try {
                    jks.store( baos, pwd.toCharArray() );
                } catch ( Exception e ) {
                    e.printStackTrace();
                }
                response.setHeader( "Content-Disposition", "attachment; filename=\"cacerts\"" );

                byte[] data = baos.toByteArray();
                response.setContentType( contentType );
                response.setContentLength( data.length );
                OutputStream os = response.getOutputStream();
                os.write( data );
                os.flush();
            } else if ( request.getParameter( "type" ).equals( "staging" ) ) {
                byte[] data = new byte[0];

                ProtectedFileAccessForm form = (ProtectedFileAccessForm) request.getSession().getAttribute(
                        "protectedFileAccessForm" );

                int format = form.getFormat();
                int content = form.getContent();
                int nxCertificateId = form.getNxCertificateId();
                String filename = "unknown";
                if ( content == 1 ) {
                    if ( format == ProtectedFileAccessForm.PEM ) {
                        filename = "certificate.pem";
                    } else {
                        filename = "certificate.der";
                    }
                } else if ( content == 2 ) {
                    filename = "certificates.zip";
                } else if ( content == 3 ) {
                    filename = "keychain-backup.p12";
                }
                response.setHeader( "Content-Disposition", "attachment; filename=\"" + filename + "\"" );

                try {

                    CertificatePojo cPojo = engineConfiguration.getCertificateByNxCertificateId( Constants.CERTIFICATE_TYPE_STAGING, nxCertificateId );
                    if ( cPojo == null ) {
                        return null;
                    }
                    KeyStore jks = KeyStore.getInstance( CertificateUtil.DEFAULT_KEY_STORE,
                            CertificateUtil.DEFAULT_JCE_PROVIDER );
                    jks.load( new ByteArrayInputStream( cPojo.getBinaryData() ), EncryptionUtil.decryptString(
                            cPojo.getPassword() ).toCharArray() );
                    boolean foundKey = false;
                    String alias = null;
                    Enumeration<String> e = jks.aliases();
                    while ( e.hasMoreElements() ) {
                        alias = e.nextElement();
                        // System.out.println( "Alias: '" + alias + "', entry is cert: " +  jks.isCertificateEntry( alias ) );
                        if ( jks.isKeyEntry( alias ) ) {
                            foundKey = true;
                            break;
                        }
                    }
                    if ( !foundKey ) {
                        throw new Exception( "No certificate found!" );
                    }
                    Certificate[] certs = jks.getCertificateChain( alias );
                    if ( content == 1 ) {
                        X509Certificate cert = (X509Certificate) certs[0];
                        if ( format == ProtectedFileAccessForm.PEM ) {
                            data = CertificateUtil.getPemData( cert ).getBytes();
                        } else if ( format == ProtectedFileAccessForm.DER ) {
                            data = cert.getEncoded();
                        }

                    } else if ( content == 2 ) {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ZipOutputStream zos = new ZipOutputStream( baos );
                        String ext = "";

                        if ( format == ProtectedFileAccessForm.PEM ) {
                            ext = ".pem";
                        } else {
                            ext = ".der";
                        }
                        ByteArrayOutputStream indexStream = new ByteArrayOutputStream();
                        PrintWriter pw = new PrintWriter( indexStream );

                        for ( int i = 0; i < certs.length; i++ ) {
                            String certName = CertificateUtil.createCertificateId( (X509Certificate) certs[i] );
                            String cn = CertificateUtil.getSubject( (X509Certificate) certs[i], X509Name.CN );
                            String o = CertificateUtil.getSubject( (X509Certificate) certs[i], X509Name.O );
                            String fingerprint = "NA";
                            byte[] responseBuf;
                            try {
                                Digest digest = new MD5Digest();
                                responseBuf = new byte[digest.getDigestSize()];
                                digest.update( certs[i].getEncoded(), 0, certs[i].getEncoded().length );
                                digest.doFinal( responseBuf, 0 );
                                fingerprint = new String( Hex.encode( responseBuf ) );
                            } catch ( CertificateEncodingException e1 ) {
                                e1.printStackTrace();
                            }

                            ZipEntry ze = new ZipEntry( certName + ext );
                            zos.putNextEntry( ze );
                            pw.println( "CommonName: " + cn );
                            pw.println( "Organisation: " + o );
                            SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
                            pw.println( "expiresponse: " + sdf.format( ( (X509Certificate) certs[i] ).getNotAfter() )
                                    + " - " + CertificateUtil.getRemainingValidity( (X509Certificate) certs[i] ) );
                            pw.println( "FingerPrint: " + fingerprint );

                            if ( i < certs.length - 1 ) {
                                pw.println( "---------------------------------------------------------" );
                            }

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
                        data = baos.toByteArray();

                    } else if ( content == 3 ) {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        String password = "nexus";
                        if (form.getPassword() != null && form.getPassword().equals(form.getVerifyPwd())) {
                            password = form.getPassword();
                        }
                        jks.store(baos, password.toCharArray());
                        data = baos.toByteArray();

                    }
                } catch ( Exception e ) {
                    e.printStackTrace();
                }
                //                }
                response.setContentType( contentType );
                response.setContentLength( data.length );
                OutputStream os = response.getOutputStream();
                os.write( data );
                os.flush();
            } else if ( request.getParameter( "type" ).equals( "privatepem" ) ) {
                LOG.debug( "exporting private pem structure " );

                CertificatePojo requestPojo = engineConfiguration.getFirstCertificateByType( Constants.CERTIFICATE_TYPE_REQUEST, true );
                if ( requestPojo == null ) {
                    LOG.error( "no request found in database" );
                    return null;
                }
                CertificatePojo privKeyPojo = engineConfiguration.getFirstCertificateByType( Constants.CERTIFICATE_TYPE_PRIVATE_KEY, true );
                if ( privKeyPojo == null ) {
                    LOG.error( "no request found in database" );
                    return null;
                }
                StringBuffer sb = new StringBuffer();

                KeyPair keyPair = CertificateUtil.getKeyPair( privKeyPojo );
                PKCS10CertificationRequest pkcs10Request = CertificateUtil.getPKCS10Request( requestPojo );
                sb.append( CertificateUtil.getPemData( pkcs10Request ) );
                sb.append( "\n" );
                sb.append( CertificateUtil.getPemData( keyPair, EncryptionUtil
                        .decryptString( privKeyPojo.getPassword() ) ) );

                byte[] data = new byte[0];
                response.setHeader( "Content-Disposition", "attachment; filename=\"private_data.pem\"" );
                data = sb.toString().getBytes();
                response.setContentType( contentType );
                response.setContentLength( data.length );
                OutputStream os = response.getOutputStream();
                os.write( data );
                os.flush();

            } else if ( request.getParameter( "type" ).equals( "request" ) ) {
                String format = request.getParameter( "format" );
                String nxCertIdString = request.getParameter( "nxCertificateId" );

                int nxCertificateId = Integer.parseInt( nxCertIdString );

                if ( nxCertificateId == 0 ) {
                    LOG.error( "no certificateId found!" );
                    return null;
                }
                CertificatePojo certificate = engineConfiguration.getCertificateByNxCertificateId( Constants.CERTIFICATE_TYPE_ALL, nxCertificateId );

                PKCS10CertificationRequest pkcs10request = CertificateUtil.getPKCS10Request( certificate );
                byte[] data = new byte[0];
                if ( format.toLowerCase().equals( "pem" ) ) {
                    response.setHeader( "Content-Disposition", "attachment; filename=\"CertficateRequest.pem\"" );
                    data = ( (String) CertificateUtil.getPemData( pkcs10request ) ).getBytes();
                } else {
                    response.setHeader( "Content-Disposition", "attachment; filename=\"CertficateRequest.der\"" );
                    data = pkcs10request.getEncoded();
                }

                response.setContentType( contentType );
                response.setContentLength( data.length );
                OutputStream os = response.getOutputStream();
                os.write( data );
                os.flush();
            } else if ( request.getParameter( "type" ).equals( "content" ) ) {

                byte[] data = new byte[0];
                String contenType = "text/xml";
                String fileExtension = "dat";

                String messageId = request.getParameter( "messageId" );
                String contentNo = request.getParameter( "no" );
                MessagePojo message = Engine.getInstance().getTransactionService().getMessage( messageId );
                if ( message != null ) {
                    if ( contentNo == null || contentNo.equals( "" ) ) {
                        byte[] b = message.getHeaderData();
                        if (b != null) {
                            data = b;
                        }
                    } else {
                        List<MessagePayloadPojo> payloads = Engine.getInstance().getTransactionService()
                                .getMessagePayloadsFromMessage( message );
                        int no = Integer.parseInt( contentNo );
                        if ( no < payloads.size() ) {
                            MessagePayloadPojo payload = payloads.get( no );
                            byte[] b = payload.getPayloadData();
                            if (b != null) {
                                data = b;
                            }
                            if ( !StringUtils.isEmpty( payload.getMimeType() ) ) {
                                contenType = payload.getMimeType();
                            }
                        }
                    }
                }

                response.setContentType( contenType );
                if ( data != null ) {
                    response.setContentLength( data.length );
                }

                String tempFileExtension = Engine.getInstance().getFileExtensionFromMime( contenType );
                if ( !StringUtils.isEmpty( tempFileExtension ) ) {
                    fileExtension = tempFileExtension;
                }

                response.setHeader( "Content-Disposition", "attachment; filename=\"" + message.getMessageId() + "_payload-"
                        + contentNo + "." + fileExtension + "\"" );
                OutputStream os = response.getOutputStream();
                os.write( data );
                os.flush();
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return null;
    }
}
