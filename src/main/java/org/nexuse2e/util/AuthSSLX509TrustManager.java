/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//httpclient/src/contrib/org/apache/commons/httpclient/contrib/ssl/AuthSSLX509TrustManager.java,v 1.2 2004/06/10 18:25:24 olegk Exp $
 * $Revision: 1.2 $
 * $Date: 2004-06-10 14:25:24 -0400 (Thu, 10 Jun 2004) $
 *
 * ====================================================================
 *
 *  Copyright 2002-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package org.nexuse2e.util;

import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.HashMap;

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

    private X509TrustManager    defaultTrustManager = null;

    private HashMap             trustedCertificates = new HashMap();

    /** Log object for this class. */
    private static final Logger LOG                 = Logger.getLogger( AuthSSLX509TrustManager.class );

    /**
     * Constructor for AuthSSLX509TrustManager.
     */
    public AuthSSLX509TrustManager( final X509TrustManager defaultTrustManager, KeyStore keyStore ) {

        super();
        try {
            Enumeration enumeration = keyStore.aliases();
            while ( enumeration.hasMoreElements() ) {
                String alias = (String) enumeration.nextElement();
                X509Certificate cert = (X509Certificate) keyStore.getCertificate( alias );
                trustedCertificates.put( cert.getSubjectDN(), cert );
                LOG.debug( "Found trusted cert: " + cert.getSubjectDN() );
            }
        } catch ( Exception ex ) {
            LOG.error( "Error processing trusted certificates! " + ex );
            System.err.println( "Error processing trusted certificates! " + ex );
        }
        if ( defaultTrustManager == null ) {
            throw new IllegalArgumentException( "Trust manager may not be null" );
        }
        this.defaultTrustManager = defaultTrustManager;
        LOG.debug( "AuthSSLX509TrustManager - defaultTrustManager: " + defaultTrustManager.getClass().getName() );
    }

    /**
     * @see com.sun.net.ssl.X509TrustManager#isClientTrusted(X509Certificate[])
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
     * @see com.sun.net.ssl.X509TrustManager#isServerTrusted(X509Certificate[])
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
     * @see com.sun.net.ssl.X509TrustManager#getAcceptedIssuers()
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

        this.defaultTrustManager.checkServerTrusted( certificates, authType );

    }
}
