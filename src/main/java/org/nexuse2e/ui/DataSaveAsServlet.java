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
package org.nexuse2e.ui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.MD5Digest;
import org.bouncycastle.jce.PKCS10CertificationRequest;
import org.bouncycastle.util.encoders.Hex;
import org.nexuse2e.Engine;
import org.nexuse2e.configuration.Constants;
import org.nexuse2e.pojo.CertificatePojo;
import org.nexuse2e.pojo.MessagePayloadPojo;
import org.nexuse2e.pojo.MessagePojo;
import org.nexuse2e.ui.form.ProtectedFileAccessForm;
import org.nexuse2e.util.CertificateUtil;
import org.nexuse2e.util.EncryptionUtil;

/**
 * @author guido.esch
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DataSaveAsServlet extends HttpServlet {

    /**
     * 
     */
    private static final long serialVersionUID = 215189147054155730L;
    private static Logger     LOG              = Logger.getLogger( DataSaveAsServlet.class );

    @Override
    public void service( HttpServletRequest request, HttpServletResponse res ) throws ServletException, IOException {

        String contentType = "application/unknown";

        LOG.debug( "Type: " + request.getParameter( "type" ) );
        try {
            if ( request.getParameter( "type" ).equals( "temppkcs12" ) ) {
                //                CertificateDAO cDao = new CertificateDAO();
                //                Object[] csr = cDao.getLocalCertificateRequest();
                //                byte[] data = new byte[0];
                //                res.setHeader( "Content-Disposition", "attachment; filename=\"Request-Backup.p12\"" );
                //
                //                KeyStore keyStore = KeyStore.getInstance( "PKCS12", "BC" );
                //                CertificatePojo cPojo = cDao.getCertificateByPartnerIdAndSequenceNumber( CertificateDAO.PRIVATE_KEY_ID,
                //                        1 );
                //                if ( cPojo != null ) {
                //
                //                    ByteArrayInputStream bais = new ByteArrayInputStream( cPojo.getCertificateImage() );
                //                    String pwd = cDao.decryptString( cPojo.getCertificatePassword() );
                //                    keyStore.load( bais, pwd.toCharArray() );
                //                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                //                    keyStore.store( baos, pwd.toCharArray() );
                //                    data = baos.toByteArray();
                //                }
                //
                //                res.setContentType( contentType );
                //                res.setContentLength( data.length );
                //                OutputStream os = res.getOutputStream();
                //                os.write( data );
                //                os.flush();
            } else if ( request.getParameter( "type" ).equals( "serverCert" ) ) {

                //                KeyStore jks = KeyStore.getInstance( CertificateDAO.DEFAULT_KEY_STORE,
                //                        CertificateDAO.DEFAULT_JCE_PROVIDER );
                //
                //                CertificateDAO cDao = new CertificateDAO();
                //                CertificatePojo cPojo = cDao.getCertificateByPartnerIdAndSequenceNumber( CertificateDAO.ENGINE_ID, 1 );
                //                byte[] data = new byte[0];
                //                if ( cPojo != null ) {
                //                    String pwd = cPojo.getCertificatePassword();
                //                    pwd = cDao.decryptString( pwd );
                //
                //                    ByteArrayInputStream bais = new ByteArrayInputStream( cPojo.getCertificateImage() );
                //                    jks.load( bais, pwd.toCharArray() );
                //
                //                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                //                    try {
                //                        jks.store( baos, pwd.toCharArray() );
                //                    } catch ( Exception e ) {
                //                        e.printStackTrace();
                //                    }
                //                    res.setHeader( "Content-Disposition", "attachment; filename=\"Nexus.p12\"" );
                //                    data = baos.toByteArray();
                //                }
                //                res.setContentType( contentType );
                //                res.setContentLength( data.length );
                //                OutputStream os = res.getOutputStream();
                //                os.write( data );
                //                os.flush();
            } else if ( request.getParameter( "type" ).equals( "cacerts" ) ) {

                List<CertificatePojo> certificates = Engine.getInstance().getActiveConfigurationAccessService()
                        .getCertificates( Constants.CERTIFICATE_TYPE_CA, null );

                KeyStore jks = CertificateUtil.generateKeyStoreFromPojos( certificates );
                CertificatePojo cPojo = Engine.getInstance().getActiveConfigurationAccessService()
                        .getFirstCertificateByType( Constants.CERTIFICATE_TYPE_CACERT_METADATA, true );
                String pwd = "changeit";
                if ( cPojo == null ) {
                    System.out.println( "metadata not found!" );
                } else {
                    pwd = EncryptionUtil.decryptString( cPojo.getPassword() );
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                try {
                    jks.store( baos, pwd.toCharArray() );
                } catch ( Exception e ) {
                    e.printStackTrace();
                }
                res.setHeader( "Content-Disposition", "attachment; filename=\"cacerts\"" );

                byte[] data = baos.toByteArray();
                res.setContentType( contentType );
                res.setContentLength( data.length );
                OutputStream os = res.getOutputStream();
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
                res.setHeader( "Content-Disposition", "attachment; filename=\"" + filename + "\"" );

                try {

                    CertificatePojo cPojo = Engine.getInstance().getActiveConfigurationAccessService()
                            .getCertificateByNxCertificateId( Constants.CERTIFICATE_TYPE_STAGING, nxCertificateId );
                    if ( cPojo == null ) {
                        return;
                    }
                    KeyStore jks = KeyStore.getInstance( CertificateUtil.DEFAULT_KEY_STORE,
                            CertificateUtil.DEFAULT_JCE_PROVIDER );
                    jks.load( new ByteArrayInputStream( cPojo.getBinaryData() ), EncryptionUtil.decryptString(
                            cPojo.getPassword() ).toCharArray() );
                    boolean foundKey = false;
                    String alias = null;
                    Enumeration e = jks.aliases();
                    while ( e.hasMoreElements() ) {
                        alias = (String) e.nextElement();
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
                            byte[] resBuf;
                            try {
                                Digest digest = new MD5Digest();
                                resBuf = new byte[digest.getDigestSize()];
                                digest.update( certs[i].getEncoded(), 0, certs[i].getEncoded().length );
                                digest.doFinal( resBuf, 0 );
                                fingerprint = new String( Hex.encode( resBuf ) );
                            } catch ( CertificateEncodingException e1 ) {
                                e1.printStackTrace();
                            }

                            ZipEntry ze = new ZipEntry( certName + ext );
                            zos.putNextEntry( ze );
                            pw.println( "CommonName: " + cn );
                            pw.println( "Organisation: " + o );
                            SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
                            pw.println( "expires: " + sdf.format( ( (X509Certificate) certs[i] ).getNotAfter() )
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
                        data = cPojo.getBinaryData();

                    }
                } catch ( Exception e ) {
                    e.printStackTrace();
                }
                //                }
                res.setContentType( contentType );
                res.setContentLength( data.length );
                OutputStream os = res.getOutputStream();
                os.write( data );
                os.flush();
            } else if ( request.getParameter( "type" ).equals( "privatepem" ) ) {
                LOG.debug( "exporting private pem structure " );

                CertificatePojo requestPojo = Engine.getInstance().getActiveConfigurationAccessService()
                        .getFirstCertificateByType( Constants.CERTIFICATE_TYPE_REQUEST, true );
                if ( requestPojo == null ) {
                    LOG.error( "no request found in database" );
                    return;
                }
                CertificatePojo privKeyPojo = Engine.getInstance().getActiveConfigurationAccessService()
                        .getFirstCertificateByType( Constants.CERTIFICATE_TYPE_PRIVATE_KEY, true );
                if ( privKeyPojo == null ) {
                    LOG.error( "no request found in database" );
                    return;
                }
                StringBuffer sb = new StringBuffer();

                KeyPair keyPair = CertificateUtil.getKeyPair( privKeyPojo );
                PKCS10CertificationRequest pkcs10Request = CertificateUtil.getPKCS10Request( requestPojo );
                sb.append( CertificateUtil.getPemData( pkcs10Request ) );
                sb.append( "\n" );
                sb.append( CertificateUtil.getPemData( keyPair, EncryptionUtil
                        .decryptString( privKeyPojo.getPassword() ) ) );
                
                byte[] data = new byte[0];
                res.setHeader( "Content-Disposition", "attachment; filename=\"private_data.pem\"" );
                data = sb.toString().getBytes();
                res.setContentType( contentType );
                res.setContentLength( data.length );
                OutputStream os = res.getOutputStream();
                os.write( data );
                os.flush();

            } else if ( request.getParameter( "type" ).equals( "request" ) ) {
                String format = request.getParameter( "format" );
                String nxCertIdString = request.getParameter( "nxCertificateId" );
                System.out.println( "NXParam: " + request.getParameter( "nxCertificateId" ) );
                System.out.println( "NXAttrib: " + request.getAttribute( "nxCertificateId" ) );

                int nxCertificateId = Integer.parseInt( nxCertIdString );
                System.out.println( "format:" + format );
                System.out.println( "nxCertificateId" + nxCertificateId );
                if ( nxCertificateId < 1 ) {
                    LOG.error( "no certificateId found!" );
                    return;
                }
                CertificatePojo certificate = Engine.getInstance().getActiveConfigurationAccessService()
                        .getCertificateByNxCertificateId( Constants.CERTIFICATE_TYPE_ALL, nxCertificateId );

                PKCS10CertificationRequest pkcs10request = CertificateUtil.getPKCS10Request( certificate );
                byte[] data = new byte[0];
                if ( format.toLowerCase().equals( "pem" ) ) {
                    res.setHeader( "Content-Disposition", "attachment; filename=\"CertficateRequest.pem\"" );
                    data = ( (String) CertificateUtil.getPemData( pkcs10request ) ).getBytes();
                } else {
                    res.setHeader( "Content-Disposition", "attachment; filename=\"CertficateRequest.der\"" );
                    data = pkcs10request.getEncoded();
                }

                res.setContentType( contentType );
                res.setContentLength( data.length );
                OutputStream os = res.getOutputStream();
                os.write( data );
                os.flush();
            } else if ( request.getParameter( "type" ).equals( "content" ) ) {

                byte[] data = new byte[0];

                String messageId = request.getParameter( "messageId" );
                String contentNo = request.getParameter( "no" );
                MessagePojo message = Engine.getInstance().getTransactionService().getMessage( messageId );
                if ( message != null ) {
                    if ( contentNo == null || contentNo.equals( "" ) ) {
                        data = message.getHeaderData();
                    } else {
                        List<MessagePayloadPojo> payloads = Engine.getInstance().getTransactionService()
                                .getMessagePayloadsFromMessage( message );
                        int no = Integer.parseInt( contentNo );
                        if ( no < payloads.size() ) {
                            MessagePayloadPojo payload = payloads.get( no );
                            data = payload.getPayloadData();
                        }
                    }
                }

                //                res.setHeader( "Content-Disposition", "attachment; filename=\"CertficateRequest.pem\"" );

                res.setContentType( "text/xml" );
                if ( data != null ) {
                    res.setContentLength( data.length );
                }
                OutputStream os = res.getOutputStream();
                os.write( data );
                os.flush();
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

}
