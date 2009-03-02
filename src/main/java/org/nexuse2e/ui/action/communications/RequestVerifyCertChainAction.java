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
import java.math.BigInteger;
import java.security.cert.Certificate;
import java.security.cert.PKIXCertPathBuilderResult;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.bouncycastle.jce.PKCS10CertificationRequest;
import org.nexuse2e.configuration.Constants;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.pojo.CertificatePojo;
import org.nexuse2e.ui.action.NexusE2EAction;
import org.nexuse2e.ui.form.CertificatePropertiesForm;
import org.nexuse2e.ui.form.ProtectedFileAccessForm;
import org.nexuse2e.util.CertificateUtil;

/**
 * @author gesch
 *
 */
public class RequestVerifyCertChainAction extends NexusE2EAction {

    private final static int MAX_CERTFILE_SIZE = 50000;
    private static String    URL               = "request.error.url";
    private static String    TIMEOUT           = "request.error.timeout";
    private final static int BUFFERSIZE        = 4096;

    /* (non-Javadoc)
     * @see com.tamgroup.nexus.e2e.ui.action.NexusE2EAction#executeNexusE2EAction(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.apache.struts.action.ActionMessages)
     */
    @Override
    public ActionForward executeNexusE2EAction( ActionMapping actionMapping, ActionForm actionForm,
            HttpServletRequest request, HttpServletResponse response, EngineConfiguration engineConfiguration, ActionMessages errors, ActionMessages messages )
            throws Exception {

        ActionForward complete = actionMapping.findForward( "complete" );
        ActionForward incomplete = actionMapping.findForward( "incomplete" );

        ProtectedFileAccessForm form = (ProtectedFileAccessForm) actionForm;
        ByteArrayInputStream bais = new ByteArrayInputStream(form.getCertficate().getFileData());
        BigInteger requestModulus;
        BigInteger requestExponent;
        List<X509Certificate> certs;
        try {
            ZipInputStream zip = new ZipInputStream(bais);
            


            List<CertificatePojo> requestPojos = engineConfiguration
                    .getCertificates( Constants.CERTIFICATE_TYPE_REQUEST, null );
            if ( requestPojos == null || requestPojos.size() == 0 ) {

                LOG.debug( "no request found in database!" );
                return incomplete;

            }
            if ( requestPojos.size() > 1 ) {
                LOG.warn( "there is more than one request in database, using first one!" );
            }
            CertificatePojo requestPojo = requestPojos.get( 0 );

            PKCS10CertificationRequest pkcs10req = CertificateUtil.getPKCS10Request( requestPojo );

            RSAPublicKey pub = (RSAPublicKey) pkcs10req.getPublicKey();

            requestModulus = pub.getModulus();
            requestExponent = pub.getPublicExponent();

            certs = new ArrayList<X509Certificate>();
//        System.out.println( "FileCount: " + zip.getChildren().length );
            ZipEntry entry = null;
            while((entry = zip.getNextEntry()) != null) {
                
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                
                byte[] buf = new byte[1024];
                int len;
                while((len = zip.read(buf)) > 0) {
                    baos.write(buf, 0, len);
                }
                
                byte [] data = baos.toByteArray();
                zip.closeEntry();
                
//            List<X509Certificate> tempcerts = getCertificates( file );
//          certs.addAll( tempcerts );
                X509Certificate  cert= CertificateUtil.getX509Certificate( data );
                if(cert != null) {
                    certs.add( cert );
                }
            }
        } catch ( Exception e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return incomplete;
        }
        if( certs.size() == 0) { // no zipfile?
            X509Certificate cert = CertificateUtil.getX509Certificate( form.getCertficate().getFileData());
            if(cert != null) {
                certs.add( cert );
            }
            
        }

        if ( certs.size() > 0 ) {

            

            List<CertificatePojo> cacertPojo = engineConfiguration
                    .getCertificates( Constants.CERTIFICATE_TYPE_CA, null );
            
            List<X509Certificate> cacerts = new ArrayList<X509Certificate>();
            
            for ( CertificatePojo pojo : cacertPojo ) {
                X509Certificate caCert = CertificateUtil.getX509Certificate( pojo );
                cacerts.add( caCert );
                
            }
            
            X509Certificate headcert = null;
            for ( X509Certificate cert : certs ) {

                BigInteger modulus = ( (RSAPublicKey) cert.getPublicKey() ).getModulus();
                BigInteger exponent = ( (RSAPublicKey) cert.getPublicKey() ).getPublicExponent();

                if ( modulus.equals( requestModulus ) && exponent.equals( requestExponent ) ) {
                    headcert = cert;
                }
            }

            if ( headcert == null ) {
                LOG.error( "no matching headcertificate found for request!" );
//                content.close();
//                zip.delete();
//                fs.delete();
                return incomplete;
            }

            Vector<CertificatePropertiesForm> caImports = new Vector<CertificatePropertiesForm>();
            PKIXCertPathBuilderResult result = CertificateUtil.getCertificateChain( headcert, certs, cacerts );
            
            
            X509Certificate root = result.getTrustAnchor().getTrustedCert();
            CertificatePropertiesForm rootCertForm = new CertificatePropertiesForm();
            rootCertForm.setCertificateProperties( root );
            rootCertForm.setAlias( CertificateUtil.createCertificateId( root ) );
            
            
            boolean found = false;
            for ( X509Certificate cacert : cacerts ) {
                if ( CertificateUtil.getMD5Fingerprint( cacert ).equals( CertificateUtil.getMD5Fingerprint( root ) ) ) {
                    found = true;
                }
            }
            if ( !found ) {
                caImports.addElement( rootCertForm );
            }

            
            Vector<CertificatePropertiesForm> chainCerts = new Vector<CertificatePropertiesForm>();
            for ( Certificate cert : result.getCertPath().getCertificates() ) {
                X509Certificate chaincert = (X509Certificate) cert;

                CertificatePropertiesForm chainForm = new CertificatePropertiesForm();
                chainForm.setCertificateProperties( chaincert );
                chainForm.setAlias( CertificateUtil.createCertificateId( chaincert ) );
                chainCerts.addElement( chainForm );
            }
            chainCerts.addElement( rootCertForm );
            
            
            request.setAttribute( "cacertsimports", caImports );
            request.setAttribute( "chain", chainCerts );
            request.getSession().setAttribute( "cacertsimports", caImports );
            request.getSession().setAttribute( "chain", chainCerts );

        }
        System.out.println( "done, deleting vfs" );
//        content.close();
//        System.out.println( "zip: " + zip.isContentOpen() );
//        System.out.println( "delete: " + zip.delete() );
//        fs.delete();

        return complete;

    }

//    /**
//     * @param file
//     * @return
//     */
//    private List<X509Certificate> getCertificates( FileObject file ) {
//
//        List<X509Certificate> certs = new ArrayList<X509Certificate>();
//        try {
//            if ( file.getType().equals( FileType.FOLDER ) ) {
//                for ( int i = 0; i < file.getChildren().length; i++ ) {
//
//                    FileObject tempfile = file.getChildren()[i];
//                    List<X509Certificate> tempcerts = getCertificates( tempfile );
//                    certs.addAll( tempcerts );
//                    tempfile.close();
//                }
//
//                return certs;
//            }
//        } catch ( FileSystemException e2 ) {
//            LOG.error( "Error while accessing fileType: " + e2 );
//        }
//
//        byte[] bytes = null;
//        try {
//            InputStream fis = file.getContent().getInputStream();
//            bytes = getCertificateBytes( fis );
//
//            fis.read( bytes );
//            fis.close();
//        } catch ( Exception e ) {
//            e.printStackTrace();
//            return certs;
//        }
//
//        try {
//            Collection tempCerts = CertificateUtil.getX509Certificates( bytes );
//            for ( Object object : tempCerts ) {
//                if ( object instanceof X509Certificate ) {
//                    LOG.debug( "adding certificate: " + ( (X509Certificate) object ).toString() );
//                    certs.add( (X509Certificate) object );
//                }
//            }
//        } catch ( Exception e ) {
//            LOG.warn( "unable to open file " + file + " using getCertificates: " + e );
//            try {
//                X509Certificate cert = CertificateUtil.getX509Certificate( bytes );
//                certs.add( cert );
//            } catch ( IllegalArgumentException e1 ) {
//                LOG.warn( "file: " + file + " is no valid certificate" );
//            }
//
//        }
//        return certs;
//    }

//    /**
//     * TODO: file size testing
//     * 
//     * @param is
//     * @return
//     */
//    private byte[] getCertificateBytes( InputStream is ) throws IllegalArgumentException {
//
//        if ( is == null ) {
//            return null;
//        }
//        try {
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//
//            byte[] imageData = new byte[BUFFERSIZE];
//            int size;
//            while ( ( size = is.read( imageData, 0, BUFFERSIZE ) ) != -1 ) {
//                baos.write( imageData, 0, size );
//                if ( size > MAX_CERTFILE_SIZE ) {
//                    throw new IllegalArgumentException( "File seems to be larger than MAX_CERTFILE_SIZE:"
//                            + MAX_CERTFILE_SIZE );
//                }
//            }
//            return baos.toByteArray();
//
//        } catch ( FileNotFoundException e ) {
//            e.printStackTrace();
//        } catch ( IOException e ) {
//            e.printStackTrace();
//        }
//        return null;
//    }

}
