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

import java.net.Socket;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import javax.net.ssl.X509KeyManager;

import org.apache.log4j.Logger;

/**
 * @author guido.esch
 */
public class NexusKeyManager implements X509KeyManager {

    private static Logger LOG      = Logger.getLogger( NexusKeyManager.class );
    private String        password = null;
    private KeyStore      keystore = null;

    /**
     * 
     */
    public NexusKeyManager() {

        super();
    }

    /**
     * @param keystore
     * @param password
     */
    public NexusKeyManager( KeyStore keystore, String password ) {

        this.password = password;
        this.keystore = keystore;
    }

    /* (non-Javadoc)
     * @see com.sun.net.ssl.X509KeyManager#getPrivateKey(java.lang.String)
     */
    public PrivateKey getPrivateKey( String alias ) {

        if ( keystore == null ) {
            return null;
        }
        try {
            return (PrivateKey) keystore.getKey( alias, password.toCharArray() );
        } catch ( KeyStoreException e ) {
            e.printStackTrace();
        } catch ( NoSuchAlgorithmException e ) {
            e.printStackTrace();
        } catch ( UnrecoverableKeyException e ) {
            e.printStackTrace();
        }
        return null;
    }

    /* (non-Javadoc)
     * @see com.sun.net.ssl.X509KeyManager#getCertificateChain(java.lang.String)
     */
    public X509Certificate[] getCertificateChain( String alias ) {

        try {
            Certificate[] certs = keystore.getCertificateChain( alias );
            X509Certificate[] newCerts = new X509Certificate[certs.length];
            System.arraycopy( certs, 0, newCerts, 0, certs.length );

            return newCerts;
        } catch ( KeyStoreException e ) {
            e.printStackTrace();
        }
        return null;
    }

    /* (non-Javadoc)
     * @see javax.net.ssl.X509KeyManager#chooseClientAlias(java.lang.String[], java.security.Principal[], java.net.Socket)
     */
    public String chooseClientAlias( String[] keyTypes, Principal[] issuers, Socket socket ) {

        //        LOG.debug( "entering chooseClientAlias" );
        if ( keystore == null ) {
            LOG.debug( "no keystore found" );
            return null;
        }
        Enumeration<String> e;
        try {
            e = keystore.aliases();
        } catch ( KeyStoreException e1 ) {
            e1.printStackTrace();
            return null;
        }
        String alias = null;
        boolean foundKey = false;
        try {
            while ( e.hasMoreElements() ) {
                alias = (String) e.nextElement();
                // LOG.debug( "Alias: '" + alias + "', entry is cert: " +  jks.isCertificateEntry( alias ) );
                if ( keystore.isKeyEntry( alias ) ) {
                    foundKey = true;
                    break;
                }
            }
        } catch ( KeyStoreException e1 ) {
            LOG.debug( "Error accesing private key in Keystore!" );
        }
        if ( foundKey ) {
            return alias;
        } else {
            LOG.debug( "No private key found in Keystore!" );
            return null;
        }

        /*
         if ( !e.hasMoreElements() ) {
         LOG.debug( "no aliases found in Keystore!" );
         return null;
         }
         String alias = (String) e.nextElement();
         if ( e.hasMoreElements() ) {
         LOG.debug( "There is more than one alias in Keystore!!!" );
         LOG.debug( "(usesd): " + alias );
         while ( e.hasMoreElements() ) {
         LOG.debug( ":" + (String) e.nextElement() );
         }
         }
         //        LOG.debug("exit chooseClientAlias, return="+alias);
         return alias;
         */
    }

    /* (non-Javadoc)
     * @see javax.net.ssl.X509KeyManager#chooseServerAlias(java.lang.String, java.security.Principal[], java.net.Socket)
     */
    public String chooseServerAlias( String keyType, Principal[] issuers, Socket socket ) {

        LOG.debug( "entering chooseServerAlias" );
        try {
            Enumeration<String> enumeration = keystore.aliases();
            while (enumeration.hasMoreElements()) {
                String alias = (String) enumeration.nextElement();
                if (keystore.isKeyEntry( alias )) {
                    return alias;
                }
            }
        } catch (KeyStoreException kex) {
            LOG.error( "Could not determine server alias", kex );
        }
        
        return null;
    }

    /* (non-Javadoc)
     * @see javax.net.ssl.X509KeyManager#getClientAliases(java.lang.String, java.security.Principal[])
     */
    public String[] getClientAliases( String keyType, Principal[] issuers ) {

        //        LOG.debug( "entering getClientAliases" );
        if ( keystore == null ) {
            LOG.debug( "no keystore found" );
            return null;
        }
        Enumeration<String> e;
        try {
            e = keystore.aliases();
        } catch ( KeyStoreException e1 ) {
            e1.printStackTrace();
            return null;
        }
        if ( !e.hasMoreElements() ) {
            LOG.debug( "no aliases found in Keystore!" );
            return null;
        }
        String[] aliases = new String[] { (String) e.nextElement()};
        if ( e.hasMoreElements() ) {
            LOG.debug( "There is more than one alias in Keystore! (getClientAliases)" );
            LOG.debug( "(used): " + aliases[0] );
            while ( e.hasMoreElements() ) {
                LOG.debug( "- " + (String) e.nextElement() );
            }
        }
        return null;
    }

    /* (non-Javadoc)
     * @see javax.net.ssl.X509KeyManager#getServerAliases(java.lang.String, java.security.Principal[])
     */
    public String[] getServerAliases( String keyType, Principal[] issuers ) {

        LOG.debug( "entering getServerAliases" );
        return null;
    }
}
