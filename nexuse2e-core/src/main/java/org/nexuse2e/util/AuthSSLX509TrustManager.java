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
package org.nexuse2e.util;

import java.security.KeyStore;
import java.security.Principal;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.X509TrustManager;

import org.apache.log4j.Logger;

/**
 * <p>
 * AuthSSLX509TrustManager can be used to extend the default {@link X509TrustManager} 
 * with additional trust decisions.
 * </p>
 * 
 * @author <a href="mailto:oleg@ural.ru">Oleg Kalnichevski</a>
 * 
 * <p>
 * DISCLAIMER: HttpClient developers DO NOT actively support this component.
 * The component is provided as a reference material, which may be inappropriate
 * for use without additional customization.
 * </p>
 */

public class AuthSSLX509TrustManager implements X509TrustManager {

    private X509TrustManager                defaultTrustManager = null;
    private Map<Principal, X509Certificate> trustedCertificates;
    X509Certificate                         leafCertificate;

    /** Log object for this class. */
    private static final Logger LOG                 = Logger.getLogger( AuthSSLX509TrustManager.class );

    /**
     * Constructs a new <code>AuthSSLX509TrustManager</code>.
     * @param defaultTrustManager The default (parent) trust manager that is used for trusted CA checking.
     * Must not be <code>null</code>.
     * @param keyStore The client certificate <code>KeyStore</code>.
     * @param leafCertificate A special certificate that is expected when connecting to a server. Can be <code>null</code>
     * if no special leaf is expected but just a valid one with a trusted root.
     */
    public AuthSSLX509TrustManager( final X509TrustManager defaultTrustManager, KeyStore keyStore, X509Certificate leafCertificate ) {

        super();
        trustedCertificates = new HashMap<Principal, X509Certificate>();
        try {
            Enumeration<String> enumeration = keyStore.aliases();
            while ( enumeration.hasMoreElements() ) {
                String alias = enumeration.nextElement();
                X509Certificate cert = (X509Certificate) keyStore.getCertificate( alias );
				if (cert != null) {
					trustedCertificates.put(cert.getSubjectDN(), cert);
					LOG.debug("Found trusted cert: " + cert.getSubjectDN());
				} else {
					LOG.debug("No cert associated with alias: " + alias);
				}
            }
        } catch ( Exception ex ) {
            LOG.error( "Error processing trusted certificates! ", ex );
        }
        if ( defaultTrustManager == null ) {
            throw new IllegalArgumentException( "Trust manager may not be null" );
        }
        this.defaultTrustManager = defaultTrustManager;
        LOG.debug( "AuthSSLX509TrustManager - defaultTrustManager: " + defaultTrustManager.getClass().getName() );
        this.leafCertificate = leafCertificate;
    }

    /**
     * @see //com.sun.net.ssl.X509TrustManager#isClientTrusted(X509Certificate[])
     */
    public boolean isClientTrusted( X509Certificate[] certificates ) {

        /*
         if ( certificates != null ) {
         LOG.debug( "isClientTrusted - certificates.length: " + certificates.length );
         System.out.println( "isClientTrusted - certificates.length: " + certificates.length );
         for ( int c = 0; c < certificates.length; c++ ) {
         X509Certificate cert = certificates[c];
         LOG.debug( " Client certificate " + ( c + 1 ) + ":" );
         LOG.debug( "  Subject DN: " + cert.getSubjectDN() );
         LOG.debug( "  Signature Algorithm: " + cert.getSigAlgName() );
         LOG.debug( "  Valid from: " + cert.getNotBefore() );
         LOG.debug( "  Valid until: " + cert.getNotAfter() );
         LOG.debug( "  Issuer: " + cert.getIssuerDN() );
         System.out.println( " Client certificate " + ( c + 1 ) + ":" );
         System.out.println( "  Subject DN: " + cert.getSubjectDN() );
         System.out.println( "  Signature Algorithm: " + cert.getSigAlgName() );
         System.out.println( "  Valid from: " + cert.getNotBefore() );
         System.out.println( "  Valid until: " + cert.getNotAfter() );
         System.out.println( "  Issuer: " + cert.getIssuerDN() );
         }
         } else {
         LOG.debug( "isClientTrusted: no certificates provided!" );
         System.out.println( "isClientTrusted: no certificates provided!" );
         }
         */
        try {
            this.defaultTrustManager.checkClientTrusted( certificates, "RSA" );
        } catch ( CertificateException e ) {
            return false;
        }
        return true;
    }

    /**
     * @see //com.sun.net.ssl.X509TrustManager#isServerTrusted(X509Certificate[])
     */
    public boolean isServerTrusted( X509Certificate[] certificates ) {

        /*
         if ( certificates != null ) {
         LOG.debug( "isServerTrusted - certificates.length: " + certificates.length );
         System.out.println( "isServerTrusted - certificates.length: " + certificates.length );
         for ( int c = 0; c < certificates.length; c++ ) {
         X509Certificate cert = certificates[c];
         LOG.debug( " Server certificate " + ( c + 1 ) + ":" );
         LOG.debug( "  Subject DN: " + cert.getSubjectDN() );
         LOG.debug( "  Signature Algorithm: " + cert.getSigAlgName() );
         LOG.debug( "  Valid from: " + cert.getNotBefore() );
         LOG.debug( "  Valid until: " + cert.getNotAfter() );
         LOG.debug( "  Issuer: " + cert.getIssuerDN() );
         System.out.println( " Server certificate " + ( c + 1 ) + ":" );
         System.out.println( "  Subject DN: " + cert.getSubjectDN() );
         System.out.println( "  Signature Algorithm: " + cert.getSigAlgName() );
         System.out.println( "  Valid from: " + cert.getNotBefore() );
         System.out.println( "  Valid until: " + cert.getNotAfter() );
         System.out.println( "  Issuer: " + cert.getIssuerDN() );
         }
         } else {
         LOG.debug( "isServerTrusted: no certificates provided!" );
         System.out.println( "isServerTrusted: no certificates provided!" );
         }
         */
        try {
            this.defaultTrustManager.checkServerTrusted( certificates, "RSA" );
        } catch ( CertificateException e ) {
            return false;
        }
        return true;
    }

    /**
     * @see //com.sun.net.ssl.X509TrustManager#getAcceptedIssuers()
     */
    public X509Certificate[] getAcceptedIssuers() {

        return this.defaultTrustManager.getAcceptedIssuers();
    }

    /* (non-Javadoc)
     * @see javax.net.ssl.X509TrustManager#checkClientTrusted(java.security.cert.X509Certificate[], java.lang.String)
     */
    public void checkClientTrusted( X509Certificate[] certificates, String authType ) throws CertificateException {

        if ( certificates != null ) {
            for ( int c = 0; c < certificates.length; c++ ) {
                X509Certificate cert = certificates[c];
                LOG.debug( " Client certificate " + ( c + 1 ) + ":" );
                LOG.debug( "  Subject DN: " + cert.getSubjectDN() );
                LOG.debug( "  Signature Algorithm: " + cert.getSigAlgName() );
                LOG.debug( "  Valid from: " + cert.getNotBefore() );
                LOG.debug( "  Valid until: " + cert.getNotAfter() );
                LOG.debug( "  Issuer: " + cert.getIssuerDN() );
            }
            /*
             X509Certificate cert = certificates[0];
             LOG.debug( "checkClientTrusted - issuer: " + cert.getIssuerDN() );
             // System.out.println( "checkClientTrusted - issuer: " + cert.getIssuerDN() );
             X509Certificate trustedCertificate = (X509Certificate)trustedCertificates.get( cert.getIssuerDN() );
             if ( trustedCertificate != null ) {
             LOG.debug( "checkClientTrusted - found issuer!" );
             return;
             } else {
             LOG.error( "checkClientTrusted - issuer not found!" );
             throw new CertificateException( "No trusted CA certificate found!" );
             }
             */
        }

        this.defaultTrustManager.checkClientTrusted( certificates, authType );

    }

    /* (non-Javadoc)
     * @see javax.net.ssl.X509TrustManager#checkServerTrusted(java.security.cert.X509Certificate[], java.lang.String)
     */
    public void checkServerTrusted( X509Certificate[] certificates, String authType ) throws CertificateException {

        if ( certificates != null ) {
            for ( int c = 0; c < certificates.length; c++ ) {
                X509Certificate cert = certificates[c];
                LOG.debug( " Server certificate " + ( c + 1 ) + ":" );
                LOG.debug( "  Subject DN: " + cert.getSubjectDN() );
                LOG.debug( "  Signature Algorithm: " + cert.getSigAlgName() );
                LOG.debug( "  Valid from: " + cert.getNotBefore() );
                LOG.debug( "  Valid until: " + cert.getNotAfter() );
                LOG.debug( "  Issuer: " + cert.getIssuerDN() );
            }
            /*
             X509Certificate cert = certificates[0];
             System.out.println( "checkServerTrusted - issuer: " + cert.getIssuerDN() );
             X509Certificate trustedCertificate = (X509Certificate)trustedCertificates.get( cert.getIssuerDN() );
             if ( trustedCertificate != null ) {
             LOG.debug( "checkServerTrusted - found issuer!" );
             return;
             } else {
             LOG.error( "checkServerTrusted - issuer not found!" );
             throw new CertificateException( "No trusted CA certificate found!" );
             }
             */
        }

        // let parent check the trusted roots
        this.defaultTrustManager.checkServerTrusted( certificates, authType );

        // if leaf cert is given, check if that certificate matches
        if (leafCertificate != null) {
            if (!leafCertificate.equals( certificates[0] )) {
                throw new CertificateException(
                        "Expected certificate for subject DN '" + leafCertificate.getSubjectDN().getName() +
                        "' does not match provided certificate (subject DN '" + certificates[0].getSubjectDN().getName() + "'" );
            }
            LOG.debug( "Provided certificate for subject DN '" + certificates[0].getSubjectDN().getName() + "' matches expected certificate" );
        }
    }
}
