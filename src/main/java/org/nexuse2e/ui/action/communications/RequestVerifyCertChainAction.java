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
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertPath;
import java.security.cert.CertPathBuilder;
import java.security.cert.CertPathBuilderException;
import java.security.cert.CertStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.PKIXCertPathBuilderResult;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.vfs.CacheStrategy;
import org.apache.commons.vfs.FileContent;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.FileSystemOptions;
import org.apache.commons.vfs.FileType;
import org.apache.commons.vfs.VFS;
import org.apache.commons.vfs.cache.OnCallRefreshFileObject;
import org.apache.commons.vfs.impl.DefaultFileSystemManager;
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
import org.nexuse2e.ui.form.CertificatePropertiesForm;
import org.nexuse2e.ui.form.ProtectedFileAccessForm;
import org.nexuse2e.util.CertificateUtil;
import org.nexuse2e.util.EncryptionUtil;

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
            HttpServletRequest request, HttpServletResponse response, ActionMessages errors, ActionMessages messages )
            throws Exception {

        System.out.println( "execute..." );
        ActionForward complete = actionMapping.findForward( "complete" );
        ActionForward incomplete = actionMapping.findForward( "incomplete" );

        ProtectedFileAccessForm form = (ProtectedFileAccessForm) actionForm;

        FileSystemManager fsManager = VFS.getManager();

        //        FileObject fs = fsManager.createVirtualFileSystem( "ram:///certstore.zip" );
        FileObject fs = fsManager.resolveFile( "ram:///certstore.zip" );

        //        fs.createFile();

        FileContent content = fs.getContent();
        OutputStream os = content.getOutputStream();
        System.out.println( ":::::::::::::::::::::::::::Filename: " + form.getCertficate().getFileName() );
        System.out.println( ":::::::::::::::::::::::::::Filesize: " + form.getCertficate().getFileSize() );
        os.write( form.getCertficate().getFileData() );

        os.flush();
        os.close();
        fs.close();

        FileObject zip = fsManager.resolveFile( "zip:ram:///certstore.zip" );
        //        FileObject zip = fs;

        //        for ( int i = 0; i < zip.getChildren().length; i++ ) {
        //
        //            FileObject file = zip.getChildren()[i];
        //
        //            InputStream fis = file.getContent().getInputStream();
        //
        //            fis.close();
        //
        //        }

        List<CertificatePojo> requestPojos = Engine.getInstance().getActiveConfigurationAccessService()
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

        BigInteger requestModulus = pub.getModulus();
        BigInteger requestExponent = pub.getPublicExponent();

        List<X509Certificate> certs = new ArrayList<X509Certificate>();
        System.out.println( "FileCount: " + zip.getChildren().length );
        for ( int i = 0; i < zip.getChildren().length; i++ ) {

            FileObject file = zip.getChildren()[i];
            System.out.println( "file: " + file );
            List<X509Certificate> tempcerts = getCertificates( file );
            file.close();
            certs.addAll( tempcerts );
        }

        if ( certs.size() > 0 ) {

            List list = new ArrayList();
            Set<TrustAnchor> trust = new HashSet<TrustAnchor>();

            List<CertificatePojo> cacertPojo = Engine.getInstance().getActiveConfigurationAccessService()
                    .getCertificates( Constants.CERTIFICATE_TYPE_CA, null );
            List<X509Certificate> cacerts = new ArrayList<X509Certificate>();
            for ( CertificatePojo pojo : cacertPojo ) {
                X509Certificate caCert = CertificateUtil.getX509Certificate( pojo );
                cacerts.add( caCert );
                trust.add( new TrustAnchor( caCert, null ) );
            }
            X509Certificate headcert = null;
            for ( X509Certificate cert : certs ) {
                list.add( cert );
                BigInteger modulus = ( (RSAPublicKey) cert.getPublicKey() ).getModulus();
                BigInteger exponent = ( (RSAPublicKey) cert.getPublicKey() ).getPublicExponent();

                System.out.println( "request: " );
                System.out.println( "modulus: " + modulus );
                System.out.println( "exponent: " + exponent );

                System.out.println( "cert: " );
                System.out.println( "modulus: " + requestModulus );
                System.out.println( "exponent: " + requestExponent );

                if ( modulus.equals( requestModulus ) && exponent.equals( requestExponent ) ) {
                    headcert = cert;
                } else {
                    trust.add( new TrustAnchor( cert, null ) );
                }
            }

            if ( headcert == null ) {
                LOG.error( "no matching headcertificate found for request!" );
                content.close();
                zip.delete();
                fs.delete();
                return incomplete;
            }

            CollectionCertStoreParameters ccsp = new CollectionCertStoreParameters( list );
            CertStore store = CertStore.getInstance( "Collection", ccsp, Constants.DEFAULT_JCE_PROVIDER );

            CertPathBuilder cpb = CertPathBuilder.getInstance( "PKIX", Constants.DEFAULT_JCE_PROVIDER );
            X509CertSelector targetConstraints = new X509CertSelector();

            targetConstraints.setCertificate( headcert );

            PKIXBuilderParameters params = new PKIXBuilderParameters( trust, targetConstraints );
            params.setRevocationEnabled( false );
            params.addCertStore( store );
            params.setDate( new Date() );
            PKIXCertPathBuilderResult result;
            try {
                result = (PKIXCertPathBuilderResult) cpb.build( params );
            } catch ( CertPathBuilderException e ) {
                e.printStackTrace();
                System.out.println( "Error: " + e.getCause() );
                content.close();
                zip.delete();
                fs.delete();
                return incomplete;
            }

            Vector<CertificatePropertiesForm> caImports = new Vector<CertificatePropertiesForm>();

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
        content.close();
        System.out.println( "zip: " + zip.isContentOpen() );
        System.out.println( "delete: " + zip.delete() );
        fs.delete();

        return complete;

    }

    /**
     * @param file
     * @return
     */
    private List<X509Certificate> getCertificates( FileObject file ) {

        List<X509Certificate> certs = new ArrayList<X509Certificate>();
        try {
            if ( file.getType().equals( FileType.FOLDER ) ) {
                for ( int i = 0; i < file.getChildren().length; i++ ) {

                    FileObject tempfile = file.getChildren()[i];
                    List<X509Certificate> tempcerts = getCertificates( tempfile );
                    certs.addAll( tempcerts );
                    tempfile.close();
                }

                return certs;
            }
        } catch ( FileSystemException e2 ) {
            LOG.error( "Error while accessing fileType: " + e2 );
        }

        byte[] bytes = null;
        try {
            InputStream fis = file.getContent().getInputStream();
            bytes = getCertificateBytes( fis );

            fis.read( bytes );
            fis.close();
        } catch ( Exception e ) {
            e.printStackTrace();
            return certs;
        }

        try {
            Collection tempCerts = CertificateUtil.getX509Certificates( bytes );
            for ( Object object : tempCerts ) {
                if ( object instanceof X509Certificate ) {
                    LOG.debug( "adding certificate: " + ( (X509Certificate) object ).toString() );
                    certs.add( (X509Certificate) object );
                }
            }
        } catch ( Exception e ) {
            LOG.warn( "unable to open file " + file + " using getCertificates: " + e );
            try {
                X509Certificate cert = CertificateUtil.getX509Certificate( bytes );
                certs.add( cert );
            } catch ( IllegalArgumentException e1 ) {
                LOG.warn( "file: " + file + " is no valid certificate" );
            }

        }
        return certs;
    }

    /**
     * TODO: file size testing
     * 
     * @param is
     * @return
     */
    private byte[] getCertificateBytes( InputStream is ) throws IllegalArgumentException {

        if ( is == null ) {
            return null;
        }
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            byte[] imageData = new byte[BUFFERSIZE];
            int size;
            while ( ( size = is.read( imageData, 0, BUFFERSIZE ) ) != -1 ) {
                baos.write( imageData, 0, size );
                if ( size > MAX_CERTFILE_SIZE ) {
                    throw new IllegalArgumentException( "File seems to be larger than MAX_CERTFILE_SIZE:"
                            + MAX_CERTFILE_SIZE );
                }
            }
            return baos.toByteArray();

        } catch ( FileNotFoundException e ) {
            e.printStackTrace();
        } catch ( IOException e ) {
            e.printStackTrace();
        }
        return null;
    }

}
