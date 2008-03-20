package org.nexuse2e.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.apache.commons.net.SocketFactory;
import org.apache.log4j.Logger;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.ConfigurationAccessService;
import org.nexuse2e.pojo.CertificatePojo;

/**
 * {@link SocketFactory} implementation that uses a {@link CertificatePojo} to create
 * SSL sockets.
 *
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public class CertificatePojoSocketFactory implements SocketFactory {

    private static Logger   LOG = Logger.getLogger( CertificatePojoSocketFactory.class );
    
    private KeyStore        keystore;
    private KeyStore        truststore;
    private SSLContext      sslContext;
    private CertificatePojo cert;

    /**
     * Constructs a new <code>CertificatePojoSocketFactory</code> using the given
     * <code>CertificatePojo</code> as certificate.
     * @param cert The certificate to use. If <code>cert</code> is <code>null</code>,
     * this socket factory cannot be used for connections that require client authentication.
     * @throws NexusException If the socket factory could not be created (e.g., if
     * the certificate data is corrupt).
     */
    public CertificatePojoSocketFactory( CertificatePojo cert ) throws NexusException {

        this.cert = cert;
        ConfigurationAccessService cas = Engine.getInstance().getActiveConfigurationAccessService();
        if (cert != null) {
            keystore = CertificateUtil.getPKCS12KeyStore( cert );
        } else {
            keystore = null;
        }
        truststore = cas.getCacertsKeyStore();
    }
    
    /**
     * Constructs a new <code>CertificatePojoSocketFactory</code> without client
     * authentication capabilities.
     */
    public CertificatePojoSocketFactory() throws NexusException {

        this( null );
    }

    /**
     * Get SSL Context.
     */
    private SSLContext getSSLContext() throws IOException {

        // if already stored - return it
        if ( sslContext != null ) {
            return sslContext;
        }

        try {
            KeyManager[] keymanagers = null;
            TrustManager[] trustmanagers = null;
            if ( keystore != null ) {
                keymanagers = CertificateUtil.createKeyManagers( keystore, EncryptionUtil.decryptString( cert
                        .getPassword() ) );
            }
            if ( truststore != null ) {
                trustmanagers = CertificateUtil.createTrustManagers( truststore );
            }
            SSLContext sslcontext = SSLContext.getInstance( "TLS" );
            sslcontext.init( keymanagers, trustmanagers, null );
            return sslcontext;
        } catch ( NoSuchAlgorithmException e ) {
            LOG.error( e.getMessage(), e );
            throw new IOException( "Unsupported algorithm exception: " + e.getMessage() );
        } catch ( KeyStoreException e ) {
            LOG.error( e.getMessage(), e );
            throw new IOException( "Keystore exception: " + e.getMessage() );
        } catch ( GeneralSecurityException e ) {
            LOG.error( e.getMessage(), e );
            throw new IOException( "Key management exception: " + e.getMessage() );
        } catch ( Exception e ) {
            LOG.error( e.getMessage(), e );
            throw new IOException( "error reading keystore/truststore file: " + e.getMessage() );
        }
    }

    public ServerSocket createServerSocket( int port ) throws IOException {

        throw new UnsupportedOperationException( "createServerSocket() not implemented" );
    }

    public ServerSocket createServerSocket( int port, int backlog ) throws IOException {

        throw new UnsupportedOperationException( "createServerSocket() not implemented" );
    }

    public ServerSocket createServerSocket( int port, int backlog, InetAddress address ) throws IOException {

        throw new UnsupportedOperationException( "createServerSocket() not implemented" );
    }

    public Socket createSocket( String host, int port ) throws UnknownHostException, IOException {

        // get socket factory
        SSLContext ctx = getSSLContext();
        SSLSocketFactory socFactory = ctx.getSocketFactory();

        // create socket
        SSLSocket sslSocket = (SSLSocket) socFactory.createSocket( host, port );
        initializeSocket( sslSocket );

        return sslSocket;
    }

    public Socket createSocket( InetAddress address, int port ) throws IOException {

        // get socket factory
        SSLContext ctx = getSSLContext();
        SSLSocketFactory socFactory = ctx.getSocketFactory();

        // create socket
        SSLSocket sslSocket = (SSLSocket) socFactory.createSocket( address, port );
        initializeSocket( sslSocket );

        return sslSocket;
    }

    public Socket createSocket( String host, int port, InetAddress localAddr, int localPort )
            throws UnknownHostException, IOException {

        // get socket factory
        SSLContext ctx = getSSLContext();
        SSLSocketFactory socFactory = ctx.getSocketFactory();

        // create socket
        SSLSocket sslSocket = (SSLSocket) socFactory.createSocket( host, port, localAddr, localPort );
        initializeSocket( sslSocket );

        return sslSocket;
    }

    public Socket createSocket( InetAddress address, int port, InetAddress localAddr, int localPort )
            throws IOException {

        // get socket factory
        SSLContext ctx = getSSLContext();
        SSLSocketFactory socFactory = ctx.getSocketFactory();

        // create socket
        SSLSocket sslSocket = (SSLSocket) socFactory.createSocket( address, port, localAddr, localPort );

        initializeSocket( sslSocket );
        return sslSocket;
    }

    private void initializeSocket( SSLSocket sslSocket ) {

        String cipherSuites[] = sslSocket.getSupportedCipherSuites();
        sslSocket.setEnabledCipherSuites( cipherSuites );
    }
}
