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
package org.nexuse2e.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.protocol.ControllerThreadSocketFactory;
import org.apache.commons.httpclient.protocol.SecureProtocolSocketFactory;
import org.apache.log4j.Logger;

/**
 * @author gesch
 *
 */
public class CertSSLProtocolSocketFactory implements SecureProtocolSocketFactory {

    /** Log object for this class. */
    private static final Logger LOG                = Logger.getLogger( CertSSLProtocolSocketFactory.class );

    private KeyStore            keystore           = null;
    private String              keystorePassword   = null;
    private KeyStore            truststore         = null;
    private String              truststorePassword = null;
    private SSLContext          sslcontext         = null;

    /**
     * Constructor for AuthSSLProtocolSocketFactory. Either a keystore or truststore file
     * must be given. Otherwise SSL context initialization error will result.
     * 
     * @param keystoreUrl URL of the keystore file. May be <tt>null</tt> if HTTPS client
     *        authentication is not to be used.
     * @param keystorePassword Password to unlock the keystore. IMPORTANT: this implementation
     *        assumes that the same password is used to protect the key and the keystore itself.
     * @param truststoreUrl URL of the truststore file. May be <tt>null</tt> if HTTPS server
     *        authentication is not to be used.
     * @param truststorePassword Password to unlock the truststore.
     */
    public CertSSLProtocolSocketFactory( final KeyStore keystore, final String keystorePassword,
            final KeyStore truststore, final String truststorePassword ) {

        super();
        //        System.out.println( "keystore:" + keystore.toString() );

        this.keystore = keystore;
        this.keystorePassword = keystorePassword;
        this.truststore = truststore;
        this.truststorePassword = truststorePassword;
    }

//    private static KeyStore createKeyStore( final InputStream store, final String password ) throws KeyStoreException,
//            NoSuchAlgorithmException, CertificateException, IOException {
//
//        if ( store == null ) {
//            throw new IllegalArgumentException( "Keystore stream may not be null" );
//        }
//        LOG.debug( "Initializing key store" );
//        KeyStore keystore = KeyStore.getInstance( "jks" );
//        keystore.load( store, password != null ? password.toCharArray() : null );
//        return keystore;
//    }


    private SSLContext createSSLContext() {

        //        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
        //
        //        System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
        //
        //        System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire.header", "debug");
        //
        //        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient", "debug");
        //        
        //        LOG.debug( "------------------------- createSSLContext -----------------------------" );
        try {
            KeyManager[] keymanagers = null;
            TrustManager[] trustmanagers = null;
            if ( keystore != null ) {
                //                System.out.println( "creating keymanagers" );
                keymanagers = CertificateUtil.createKeyManagers( keystore, this.keystorePassword );
                //                SLOG.debug( "......................................................." );
                //                LOG.debug( "count:" + keymanagers.length );
                //                for ( int i = 0; i < keymanagers.length; i++ ) {
                //                    LOG.debug(keymanagers[i].toString());
                //                    X509KeyManager impl = (X509KeyManager)keymanagers[i];
                //                    X509Certificate[] chain = impl.getCertificateChain("nexuscert");
                //                    LOG.debug("cert:"+chain[0].getPublicKey().getAlgorithm());
                //                    LOG.debug("cert:"+chain[0].getIssuerX500Principal());
                //                }
                //                LOG.debug( "......................................................." );
            }
            if ( truststore != null ) {
                //                System.out.println( "creating trustedkeymanagers" );
                trustmanagers = CertificateUtil.createTrustManagers( truststore );
                LOG.debug( "......................................................." );

                LOG.debug( "count:" + trustmanagers.length );
                for ( int i = 0; i < trustmanagers.length; i++ ) {
                    AuthSSLX509TrustManager manager = (AuthSSLX509TrustManager) trustmanagers[i];
                    X509Certificate[] certs = manager.getAcceptedIssuers();
                    for ( int j = 0; j < certs.length; j++ ) {
                        X509Certificate certificate = certs[j];
                        LOG.debug( "DN:" + certificate.getSubjectDN() );
                    }
                }
                LOG.debug( "......................................................." );

            }
            SSLContext sslcontext = SSLContext.getInstance( "SSL" );

            sslcontext.init( keymanagers, trustmanagers, null );
            return sslcontext;
        } catch ( NoSuchAlgorithmException e ) {
            LOG.error( e.getMessage(), e );
            throw new Error( "Unsupported algorithm exception: " + e.getMessage() );
        } catch ( KeyStoreException e ) {
            LOG.error( e.getMessage(), e );
            throw new Error( "Keystore exception: " + e.getMessage() );
        } catch ( GeneralSecurityException e ) {
            LOG.error( e.getMessage(), e );
            throw new Error( "Key management exception: " + e.getMessage() );
        } catch ( Exception e ) {
            LOG.error( e.getMessage(), e );
            throw new Error( "error reading keystore/truststore file: " + e.getMessage() );
        }
    }

    private SSLContext getSSLContext() {

        if ( this.sslcontext == null ) {
            this.sslcontext = createSSLContext();
        }
        return this.sslcontext;
    }

    /**
     * Attempts to get a new socket connection to the given host within the given time limit.
     * <p>
     * To circumvent the limitations of older JREs that do not support connect timeout a 
     * controller thread is executed. The controller thread attempts to create a new socket 
     * within the given limit of time. If socket constructor does not return until the 
     * timeout expires, the controller terminates and throws an {@link ConnectTimeoutException}
     * </p>
     *  
     * @param host the host name/IP
     * @param port the port on the host
     * @param clientHost the local host name/IP to bind the socket to
     * @param clientPort the port on the local machine
     * @param params {@link HttpConnectionParams Http connection parameters}
     * 
     * @return Socket a new socket
     * 
     * @throws IOException if an I/O error occurs while creating the socket
     * @throws UnknownHostException if the IP address of the host cannot be
     * determined
     */
    public Socket createSocket( final String host, final int port, final InetAddress localAddress, final int localPort,
            final HttpConnectionParams params ) throws IOException, UnknownHostException, ConnectTimeoutException {

        if ( params == null ) {
            throw new IllegalArgumentException( "Parameters may not be null" );
        }
        int timeout = params.getConnectionTimeout();
        if ( timeout == 0 ) {
            return createSocket( host, port, localAddress, localPort );
        } else {
            // To be eventually deprecated when migrated to Java 1.4 or above
            return ControllerThreadSocketFactory.createSocket( this, host, port, localAddress, localPort, timeout );
        }
    }

    /**
     * @see SecureProtocolSocketFactory#createSocket(java.lang.String,int,java.net.InetAddress,int)
     */
    public Socket createSocket( String host, int port, InetAddress clientHost, int clientPort ) throws IOException,
            UnknownHostException {

        return getSSLContext().getSocketFactory().createSocket( host, port, clientHost, clientPort );
    }

    /**
     * @see SecureProtocolSocketFactory#createSocket(java.lang.String,int)
     */
    public Socket createSocket( String host, int port ) throws IOException, UnknownHostException {

        return getSSLContext().getSocketFactory().createSocket( host, port );
    }

    /**
     * @see SecureProtocolSocketFactory#createSocket(java.net.Socket,java.lang.String,int,boolean)
     */
    public Socket createSocket( Socket socket, String host, int port, boolean autoClose ) throws IOException,
            UnknownHostException {

        return getSSLContext().getSocketFactory().createSocket( socket, host, port, autoClose );
    }
}