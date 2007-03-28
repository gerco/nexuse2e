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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.bouncycastle.asn1.DERBMPString;
import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.jce.PKCS10CertificationRequest;
import org.bouncycastle.jce.PrincipalUtil;
import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier;
import org.bouncycastle.jce.provider.JDKDigestSignature;
import org.bouncycastle.x509.X509V1CertificateGenerator;
import org.codehaus.xfire.util.Base64;
import org.nexuse2e.NexusException;
import org.nexuse2e.pojo.CertificatePojo;

/**
 * Utility class to work with certificates (mostly instances of <code>X509Certificate</code>).
 * Uses the BouncyCastly cryptography provider under the covers.
 * @author gesch
 * @version $ID:$
 */
public class CertificateUtil {

    private static Logger      LOG                                 = Logger.getLogger( CertificateUtil.class );

    public static final int    DEFAULT_RSA_KEY_LENGTH              = 1024;
    public static final String DEFAULT_DIGITAL_SIGNATURE_ALGORITHM = "SHA1withRSA";
    public static final String DEFAULT_KEY_ALGORITHM               = "RSA";
    public static final String DEFAULT_CERT_TYPE                   = "X.509";
    public static final String DEFAULT_KEY_STORE                   = "PKCS12";
    public static final String DEFAULT_JCE_PROVIDER                = "BC";
    private static final int   PEM_LINE_LENGTH                     = 64;
    public static final String DEFAULT_CERT_ALIAS                  = "nexuscert";
    public static final String DEFAULT_SELFSIGNED_PREFIX           = "SELFSIGNED-TEMPORARY:";

    public static final int    POS_REQUEST                         = 0;
    public static final int    POS_PASSWORD                        = 1;
    public static final int    POS_PEM                             = 2;
    public static final int    POS_DER                             = 3;

    public static final int    POS_KEYS                            = 1;
    public static final int    POS_CERT                            = 3;

    /**
     * Return the remaining time till certificate expires as rounded human readable String.
     * @param x509certificate The <code>X509Certificate</code> to inspect
     * @return remaining time till certificate expires as rounded human readable string
     */
    public static String getRemainingValidity( X509Certificate x509certificate ) {

        long nowMils = System.currentTimeMillis();
        long expMils = x509certificate.getNotAfter().getTime();
        String remaining = "";
        if ( nowMils < expMils ) {
            long div = expMils - nowMils;
            long day = 1000 * 60 * 60 * 24;
            long remDays = div / day;
            if ( remDays > 14 ) {
                long week = day * 7;
                long remWeeks = div / week;
                if ( remWeeks > 8 ) {
                    long month = day * 30;
                    long remMonth = div / month;
                    if ( remMonth > 24 ) {
                        long year = month * 12;
                        long remYears = div / year;
                        remaining = "(~ " + remYears + " Years remaining)";
                    } else {
                        remaining = "(~ " + remMonth + " Month remaining)";
                    }
                } else {
                    remaining = "(~ " + remWeeks + " weeks remaining)";
                }
            } else {
                remaining = "(~ " + remDays + " days remaining)";
            }
        }
        return remaining;
    } // getRemainingValidity

    /**
     * Get a <code>X509Certificate</code> as a PEM encoded byte array
     * @param x509Certificate The <code>X509Certificate</code>
     * @return The <code>X509Certificate</code> as a PEM encoded byte array
     * @throws CertificateEncodingException
     */
    public static byte[] getPemData( X509Certificate x509Certificate ) throws CertificateEncodingException {

        byte[] data;
        String pem = Base64.encode( x509Certificate.getEncoded() );
        byte[] pembytes = pem.getBytes();
        StringBuffer buffer = new StringBuffer();
        buffer.append( "-----BEGIN CERTIFICATE-----\n" );
        int offset = 0;
        while ( offset < pembytes.length ) {
            byte[] temp;
            if ( pembytes.length >= offset + PEM_LINE_LENGTH ) {
                temp = new byte[PEM_LINE_LENGTH];
            } else {
                temp = new byte[pembytes.length - offset];
            }
            System.arraycopy( pembytes, offset, temp, 0, temp.length );
            for ( int i = 0; i < temp.length; i++ ) {
                buffer.append( (char) temp[i] );
            }
            buffer.append( "\n" );

            offset += PEM_LINE_LENGTH;
        }
        buffer.append( "-----END CERTIFICATE-----" );

        data = buffer.toString().getBytes();
        return data;
    } // getPemData

    /**
     * @param certificates
     * @return
     */
    public static KeyStore generateKeyStoreFromPojos( List<CertificatePojo> certificates ) {

        KeyStore jks = null;
        try {
            jks = KeyStore.getInstance( "JKS" );
            jks.load( null, null );
            if ( certificates != null ) {
                for ( CertificatePojo certificate : certificates ) {
                    byte[] data = certificate.getBinaryData();
                    if ( data == null ) {
                        continue;
                    }
                    X509Certificate x509Certificate = CertificateUtil.getX509Certificate( data );
                    jks.setCertificateEntry( certificate.getName(), x509Certificate );
                }
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return jks;
    }

    /**
     * Create a <code>java.security.KeyStore</code> from a byte array.
     * @param pkcs12 The byte array representing the PKCS key store
     * @param password The password to open the pro
     * @return
     */
    public static KeyStore getPKCS12KeyStoreFromByteArray( byte[] pkcs12, String password ) {

        KeyStore keyStore = null;

        try {
            keyStore = KeyStore.getInstance( DEFAULT_KEY_STORE, DEFAULT_JCE_PROVIDER );
            ByteArrayInputStream bais = new ByteArrayInputStream( pkcs12 );
            keyStore.load( bais, password.toCharArray() );
            Enumeration enumeration = keyStore.aliases();
            if ( enumeration.hasMoreElements() ) {
                String alias = (String) enumeration.nextElement();
                LOG.debug( "getPKCS12KeyStoreFromByteArray - alias: " + alias );
                // PrivateKey privateKey = (PrivateKey) keyStore.getKey( alias, password.toCharArray() );
                Certificate certificate = keyStore.getCertificate( alias );
                LOG.trace( "getPKCS12KeyStoreFromByteArray: " + certificate );
            }
        } catch ( Exception ex ) {
            ex.printStackTrace();
            keyStore = null;
        }

        return keyStore;
    } // getPKCS12KeyStoreFromByteArray

    /**
     * Get the Common Name String from the certificate
     * @param x509Certificate The certificate to analyze
     * @param subject TRUE when the subject information is requested, FALSE for issuer
     * @return The String containing the info or NULL if not found
     */
    public static String getCertificateCN( X509Certificate x509Certificate, boolean subject ) {

        if ( x509Certificate == null ) {
            return "";
        }
        return getCertificateInfo( getPrincipalFromCertificate( x509Certificate, subject ), X509Name.CN );
    } // getCertificateCN

    /**
     * Get the Country String from the certificate
     * @param x509Certificate The certificate to analyze
     * @param subject TRUE when the subject information is requested, FALSE for issuer
     * @return The String containing the info or NULL if not found
     */
    public static String getCertificateC( X509Certificate x509Certificate, boolean subject ) {

        if ( x509Certificate == null ) {
            return "";
        }
        return getCertificateInfo( getPrincipalFromCertificate( x509Certificate, subject ), X509Name.C );
    } // getCertificateC

    /**
     * Get the State String from the certificate
     * @param x509Certificate The certificate to analyze
     * @param subject TRUE when the subject information is requested, FALSE for issuer
     * @return The String containing the info or NULL if not found
     */
    public static String getCertificateST( X509Certificate x509Certificate, boolean subject ) {

        if ( x509Certificate == null ) {
            return "";
        }
        return getCertificateInfo( getPrincipalFromCertificate( x509Certificate, subject ), X509Name.ST );
    } // getCertificateST

    /**
     * Get the Organization String from the certificate
     * @param x509Certificate The certificate to analyze
     * @param subject TRUE when the subject information is requested, FALSE for issuer
     * @return The String containing the info or NULL if not found
     */
    public static String getCertificateO( X509Certificate x509Certificate, boolean subject ) {

        if ( x509Certificate == null ) {
            return "";
        }
        return getCertificateInfo( getPrincipalFromCertificate( x509Certificate, subject ), X509Name.O );
    } // getCertificateO

    /**
     * Get the Organization Unit String from the certificate
     * @param x509Certificate The certificate to analyze
     * @param subject TRUE when the subject information is requested, FALSE for issuer
     * @return The String containing the info or NULL if not found
     */
    public static String getCertificateOU( X509Certificate x509Certificate, boolean subject ) {

        if ( x509Certificate == null ) {
            return "";
        }
        return getCertificateInfo( getPrincipalFromCertificate( x509Certificate, subject ), X509Name.OU );
    } // getCertificateOU

    /**
     * Get the Location String from the certificate
     * @param x509Certificate The certificate to analyze
     * @param subject TRUE when the subject information is requested, FALSE for issuer
     * @return The String containing the info or NULL if not found
     */
    public static String getCertificateL( X509Certificate x509Certificate, boolean subject ) {

        if ( x509Certificate == null ) {
            return "";
        }
        return getCertificateInfo( getPrincipalFromCertificate( x509Certificate, subject ), X509Name.L );
    } // getCertificateL

    /**
     * Get the Email String from the certificate
     * @param x509Certificate The certificate to analyze
     * @param subject TRUE when the subject information is requested, FALSE for issuer
     * @return The String containing the info or NULL if not found
     */
    public static String getCertificateE( X509Certificate x509Certificate, boolean subject ) {

        if ( x509Certificate == null ) {
            return "";
        }
        return getCertificateInfo( getPrincipalFromCertificate( x509Certificate, subject ), X509Name.E );
    } // getCertificateE

    /**
     * @param x509Certificate
     * @param subject
     * @return
     */
    public static X509Principal getPrincipalFromCertificate( X509Certificate x509Certificate, boolean subject ) {

        X509Principal x509Principal = null;
        try {
            if ( subject ) {
                x509Principal = PrincipalUtil.getSubjectX509Principal( x509Certificate );
            } else {
                x509Principal = PrincipalUtil.getIssuerX509Principal( x509Certificate );
            }
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
        return x509Principal;
    }

    /**
     * @param x509Principal
     * @param id
     * @return
     */
    public static String getCertificateInfo( X509Name x509Principal, DERObjectIdentifier id ) {

        String result = null;
        try {
            Vector ids = x509Principal.getOIDs();
            Vector values = x509Principal.getValues();
            for ( int i = 0; i < ids.size(); i++ ) {
                DERObjectIdentifier innerId = (DERObjectIdentifier) ids.elementAt( i );
                if ( innerId.equals( id ) ) {
                    result = (String) values.elementAt( i );
                    break;
                }
            }
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }

        return result;
    }

    /**
     * @param certData
     * @return
     * @throws CertificateException
     */
    public static X509Certificate getX509Certificate( byte[] certData ) throws NexusException {

        X509Certificate x509Certificate = null;
        CertificateFactory certificateFactory = null;

        try {
            if ( certData != null ) {
                ByteArrayInputStream bais = new ByteArrayInputStream( certData );
                try {
                    certificateFactory = CertificateFactory.getInstance( DEFAULT_CERT_TYPE, DEFAULT_JCE_PROVIDER );
                } catch ( NoSuchProviderException e ) {
                    LOG.error( "Could not create CertificateFactory for Bouncy Castle provider!" );
                    certificateFactory = CertificateFactory.getInstance( DEFAULT_CERT_TYPE );
                }
                x509Certificate = (X509Certificate) certificateFactory.generateCertificate( bais );
            }
        } catch ( CertificateException e ) {
            throw new NexusException( e );
        }

        return x509Certificate;
    } // getX509Certificate

    /**
     * @param certs
     * @return
     */
    public static Collection getX509CertificatesFromByteArray( byte[] certs ) throws CertificateException {

        Collection certCollection = null;

        CertificateFactory certificateFactory;
        try {
            try {
                certificateFactory = CertificateFactory.getInstance( DEFAULT_CERT_TYPE, DEFAULT_JCE_PROVIDER );
            } catch ( NoSuchProviderException e ) {
                LOG.error( "Could not create CertificateFactory for Bouncy Castle provider!" );
                certificateFactory = CertificateFactory.getInstance( DEFAULT_CERT_TYPE );
            }
            ByteArrayInputStream bais = new ByteArrayInputStream( certs );
            certCollection = certificateFactory.generateCertificates( bais );
            Iterator i = certCollection.iterator();
            while ( i.hasNext() ) {
                Certificate cert = (Certificate) i.next();
                System.out.println( cert );
            }
        } catch ( CertificateException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return certCollection;
    } // getX509CertificateFromByteArray

    /**
     * Retrieve the certificate request
     * @return Array with certificate request if available [0], PEM encoded request [1] and
     * DER encoded request [2]
     */
    static public Object[] getLocalCertificateRequestFromPojo( CertificatePojo certificate ) {

        Object[] result = new Object[4];

        try {
            // Request

            if ( certificate != null ) {
                PKCS10CertificationRequest request = new PKCS10CertificationRequest( certificate.getBinaryData() );
                result[POS_REQUEST] = request;
                result[POS_PASSWORD] = EncryptionUtil.decryptString( certificate.getPassword() );
                result[POS_PEM] = CertificateUtil.getPKCS10CertificateRequestAsPem( request );
                result[POS_DER] = request.getEncoded();
            }
        } catch ( Exception ex ) {
            result = new Object[3];
            ex.printStackTrace();
        }
        return result;
    } // getLocalCertificateRequest

    /**
     * Convert a PKCS10CertificationRequest into the PEM string representation
     * @param certificationRequest The certifcate to transform
     * @return The PEM string representation of the certificate
     */
    public static String getPKCS10CertificateRequestAsPem( PKCS10CertificationRequest certificationRequest ) {

        byte[] requestData = certificationRequest.getEncoded();

        return getDERCertificateRequestAsPem( requestData );
    } // getPKCS10CertificateRequestAsPem

    /**
     * @param requestData
     * @return
     */
    public static String getDERCertificateRequestAsPem( byte[] requestData ) {

        String encodedRequestData = Base64.encode( requestData );

        // Convert into PEM format
        StringBuffer buffer = new StringBuffer();
        buffer.append( "-----BEGIN NEW CERTIFICATE REQUEST-----\n" );
        int offset = 0;
        while ( offset < encodedRequestData.length() ) {
            if ( encodedRequestData.length() >= offset + PEM_LINE_LENGTH ) {
                buffer.append( encodedRequestData.substring( offset, offset + PEM_LINE_LENGTH ) + "\n" );
            } else {
                buffer.append( encodedRequestData.substring( offset ) + "\n" );
            }
            offset += PEM_LINE_LENGTH;
        }
        buffer.append( "-----END NEW CERTIFICATE REQUEST-----" );

        return buffer.toString();
    }

    /**
     * Retrieve the next missing certificate subject to complete the request certificate chain
     * @return The subject of the next missing certificate in the chain.
     * NULL if chain is complete or no request exists.
     */
    public static X509Principal getMissingCertificateSubjectDNFromKeyStore( PKCS10CertificationRequest request,
            CertificatePojo certificate ) {

        if ( request == null ) {
            return null;
        }
        HashMap certHashMap = getX509CertificateHashMapFromKeyStore( certificate );
        X509Name x509Name = request.getCertificationRequestInfo().getSubject();
        System.out.println( "x509Name: " + x509Name.toString() );
        X509Principal x509Principal = new X509Principal( x509Name );
        x509Principal = cleanPrincipal( x509Principal );

        return getMissingCertificateSubjectDN( certHashMap, x509Principal );
    } // getMissingCertificateSubjectDNFromKeyStore

    /**
     * Retrieve the next missing certificate subject to complete the request certificate chain
     * @return The subject of the next missing certificate in the chain.
     * NULL if chain is complete or no request exists.
     */
    public static X509Principal getMissingCertificateSubjectDN( PKCS10CertificationRequest request,
            List<CertificatePojo> certificateParts ) throws NexusException {

        if ( request == null ) {
            return null;
        }
        HashMap certHashMap = getX509CertificateHashMap( certificateParts );
        X509Name x509Name = request.getCertificationRequestInfo().getSubject();
        X509Principal x509Principal = new X509Principal( x509Name );

        return getMissingCertificateSubjectDN( certHashMap, x509Principal );
    } // getMissingCertificateSubjectDN

    /**
     * @param certHashMap
     * @param x509Principal
     * @return
     */
    private static X509Principal getMissingCertificateSubjectDN( HashMap certHashMap, X509Principal x509Principal ) {

        ArrayList certChain = getCertChainDN( certHashMap, x509Principal );
        LOG.debug( "getMissingCertificateSubjectDN - certChain:\n" + certChain );
        if ( !isCertChainComplete( certChain ) ) {
            if ( certChain.isEmpty() ) {
                // log.debug( "chain is empty" );
                return x509Principal;
            } else {
                X509Certificate x509Certificate = (X509Certificate) certChain.get( certChain.size() - 1 );
                x509Principal = CertificateUtil.getPrincipalFromCertificate( x509Certificate, false );
                LOG.debug( "getMissingCertificateSubjectDN - issuer:\n" + x509Principal );
                return x509Principal;
            }
        }

        return null;
    } // getMissingCertificateSubjectDN

    /**
     * @param certChain
     * @return
     */
    private static boolean isCertChainComplete( ArrayList certChain ) {

        if ( !certChain.isEmpty() ) {
            X509Certificate x509Certificate = (X509Certificate) certChain.get( certChain.size() - 1 );
            X509Principal subject = CertificateUtil.getPrincipalFromCertificate( x509Certificate, true );
            X509Principal issuer = CertificateUtil.getPrincipalFromCertificate( x509Certificate, false );
            LOG.debug( "isCertChainComplete - subject:\n" + subject );
            LOG.debug( "isCertChainComplete - issuer:\n" + issuer );
            return subject.equals( issuer );
        }

        return false;
    } // isCertChainComplete

    /**
     * @return
     */
    public static HashMap<X509Principal, X509Certificate> getX509CertificateHashMap(
            List<CertificatePojo> certificateParts ) throws NexusException {

        HashMap<X509Principal, X509Certificate> hashMap = new HashMap<X509Principal, X509Certificate>();

        if ( certificateParts != null ) {

            for ( CertificatePojo certificatePart : certificateParts ) {

                // Create X509 instance
                X509Certificate x509Certificate = CertificateUtil.getX509Certificate( certificatePart.getBinaryData() );

                // String id = certificatePojo.getCertificateId();
                X509Principal id = CertificateUtil.getPrincipalFromCertificate( x509Certificate, true );

                hashMap.put( id, x509Certificate );
            }
        }
        return hashMap;
    } // getX509CertificateHashMap

    /**
     * @param certificateHashMap
     * @param subject
     * @return
     */
    private static ArrayList<X509Certificate> getCertChainDN( HashMap certificateHashMap, X509Principal subject ) {

        ArrayList<X509Certificate> certChain = new ArrayList<X509Certificate>();
        X509Principal issuer = null;
        try {
            X509Certificate x509Certificate = (X509Certificate) certificateHashMap.get( subject );
            while ( !subject.equals( issuer ) && ( x509Certificate != null ) ) {
                issuer = CertificateUtil.getPrincipalFromCertificate( x509Certificate, false );
                subject = CertificateUtil.getPrincipalFromCertificate( x509Certificate, true );
                // log.debug( "add entry:" + subject );
                certChain.add( x509Certificate );

                // log.debug( "Found cert:" + subject + " - " + issuer );

                x509Certificate = (X509Certificate) certificateHashMap.get( issuer );
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }

        return certChain;
    } // getCertChainDN

    /**
     * @return
     */
    private static HashMap getX509CertificateHashMapFromKeyStore( CertificatePojo certificate ) {

        HashMap hashMap = new HashMap();
        try {

            if ( certificate != null ) {
                byte[] data = certificate.getBinaryData();
                KeyStore jks = KeyStore.getInstance( "PKCS12", "BC" );
                String pwd = EncryptionUtil.decryptString( certificate.getPassword() );
                jks.load( new ByteArrayInputStream( data ), pwd.toCharArray() );
                Certificate[] certs = jks.getCertificateChain( DEFAULT_CERT_ALIAS );
                // log.debug( "certs:" + certs );
                if ( certs != null ) {
                    for ( int i = 0; i < certs.length; i++ ) {
                        // log.debug( "test: " + ( (X509Certificate) certs[i] ).getSubjectDN().toString() );
                        /*
                         String cn = CertificateTools.getCertificateCN( (X509Certificate) certs[i], true );
                         log.debug( "adding cert to hashmap: " + cn );
                         hashMap.put( cn, certs[i] );
                         */
                        X509Principal x509Principal = CertificateUtil.getPrincipalFromCertificate(
                                (X509Certificate) certs[i], true );
                        System.out.println( "x509Principal.toString(): " + x509Principal.toString() );
                        // Remove junk added by VeriSign
                        x509Principal = cleanPrincipal( x509Principal );

                        hashMap.put( x509Principal, certs[i] );
                    }
                }
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return hashMap;
    }

    /**
     * @param principal
     * @return
     */
    public static X509Principal cleanPrincipal( X509Principal principal ) {

        System.out.println( "principal: " + principal.toString() );
        X509Principal cleanedPrincipal = null;

        Vector oids = principal.getOIDs();
        Vector<String> values = principal.getValues();
        Vector<DERObjectIdentifier> newOids = new Vector<DERObjectIdentifier>();
        Vector<String> newValues = new Vector<String>();
        boolean isVeriSign = false;

        int i = 0;
        for ( Iterator iter = oids.iterator(); iter.hasNext(); ) {
            DERObjectIdentifier oid = (DERObjectIdentifier) iter.next();
            if ( X509Name.O.equals( oid ) && ( values.elementAt( i ) ).toLowerCase().indexOf( "verisign" ) != -1 ) {
                isVeriSign = true;
                break;
                //System.out.println( "Found VeriSign cert: " + principal.toString() );
            }
            i++;
        }
        if ( isVeriSign ) {
            cleanedPrincipal = principal;
        } else {
            i = 0;
            // System.out.println( "Cleaning cert: " + principal.toString() );
            for ( Iterator iter = oids.iterator(); iter.hasNext(); ) {
                DERObjectIdentifier oid = (DERObjectIdentifier) iter.next();
                // System.out.println( "OID: " + oid.getId() + " - " + oid.toString() + " - " + values.elementAt( i ) );
                if ( !( X509Name.OU.equals( oid ) & ( ( values.elementAt( i ) ).toLowerCase().indexOf( "verisign" ) != -1 ) )
                        && !X509Name.E.equals( oid ) ) {
                    // System.out.println( "copy OID: " + oid.getId() + " - " + values.elementAt( i ) );
                    newOids.add( oid );
                    newValues.add( values.elementAt( i ) );
                }
                i++;
            }

            cleanedPrincipal = new X509Principal( newOids, newValues );
            System.out.println( "principal: " + cleanedPrincipal.toString() );
        }

        return cleanedPrincipal;
    } // cleanPrincipal

    /**
     * Create a self signed X.509 V1 certificate
     */
    public static Certificate createSelfSignedCert( String cn, String o, String ou, String c, String st, String l,
            String e, PublicKey pubKey, PrivateKey privKey ) throws Exception {

        //
        // signers name 
        //
        //        String issuer = "C=US, O=TamGroup, OU=Tamgroup Temp Certificate";

        //
        // subjects name - the same as we are self signed.
        //
        //        String subject = "C=US, O=TamGroup, OU=Tamgroup Temp Certificate";

        Hashtable attrs = new Hashtable();

        attrs.put( X509Name.CN, DEFAULT_SELFSIGNED_PREFIX + cn );
        if ( c != null ) {
            attrs.put( X509Name.C, c );
        }
        if ( o != null ) {
            attrs.put( X509Name.O, o );
        }
        if ( ou != null ) {
            attrs.put( X509Name.OU, ou );
        }
        if ( l != null ) {
            attrs.put( X509Name.L, l );
        }
        if ( st != null ) {
            attrs.put( X509Name.ST, st );
        }
        if ( e != null ) {
            attrs.put( X509Name.E, e );
            attrs.put( X509Name.EmailAddress, e );
        }

        X509Name issuerX509Name = new X509Name( attrs );

        //
        // create the certificate - version 1
        //
        X509V1CertificateGenerator v1CertGen = new X509V1CertificateGenerator();
        v1CertGen.setSerialNumber( BigInteger.valueOf( 1 ) );
        v1CertGen.setIssuerDN( new X509Principal( issuerX509Name ) );
        // v1CertGen.setIssuerDN( new X509Principal( issuer ) );
        v1CertGen.setNotBefore( new Date( System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 30 ) );
        v1CertGen.setNotAfter( new Date( System.currentTimeMillis() + ( 1000L * 60 * 60 * 24 * 30 ) ) );
        v1CertGen.setSubjectDN( new X509Principal( issuerX509Name ) );
        v1CertGen.setPublicKey( pubKey );
        v1CertGen.setSignatureAlgorithm( "SHA1WithRSAEncryption" );

        X509Certificate cert = v1CertGen.generateX509Certificate( privKey );

        cert.checkValidity( new Date() );

        cert.verify( pubKey );

        return cert;
    }

    /**
     * Flag whether the provided cert matches the next required subject
     * @param certBytes The X509 certificate as a byte array
     * @param subject The subject of the next missing certificate in the chain
     * @return TRUE if the certificate matches the subject, FALSE otherwise.
     */
    public static boolean isCertificateMatchingMissingSubjectDN( byte[] certBytes, X509Principal x509Subject ) {

        X509Certificate x509Certificate = null;
        if ( x509Subject != null ) {

            try {
                x509Certificate = getX509Certificate( certBytes );

                System.out.println( "getCertificateCN( x509Certificate, true ):"
                        + getCertificateCN( x509Certificate, true ) );

                X509Principal x509Principal = getPrincipalFromCertificate( x509Certificate, true );

                System.out.println( "x509Principal.toString(): " + x509Principal.toString() );

                // Remove junk added by VeriSign :-(
                x509Principal = cleanPrincipal( x509Principal );

                return x509Subject.equals( x509Principal );
            } catch ( Exception e ) {
                e.printStackTrace();
            }
        }

        return false;
    }

    /**
     * @param certBytes
     * @param x509Subject
     * @return
     */
    public static boolean isCertificateCNMatchingMissingCN( byte[] certBytes, X509Principal x509Subject ) {

        X509Certificate x509Certificate = null;
        String cnLocal = null;
        String cnImported = null;

        if ( x509Subject != null ) {

            try {
                x509Certificate = getX509Certificate( certBytes );

                cnLocal = getCertificateCN( x509Certificate, true );

                String subject = x509Subject.getName();
                StringTokenizer st = new StringTokenizer( subject, "," );
                while ( st.hasMoreTokens() ) {
                    String temp = st.nextToken().trim();
                    if ( temp.startsWith( "CN=" ) ) {
                        cnImported = temp.substring( 3 );
                        System.out.println( "Imported CN: " + cnImported );
                        break;
                    }
                }

                if ( cnLocal != null ) {
                    return cnLocal.equals( cnImported );
                }
            } catch ( Exception e ) {
                e.printStackTrace();
            }
        }

        return false;
    }

    /**
     * Create a PKCS10 compliant certificate request.
     * @param commonName
     * @param organization
     * @param organizationUnit
     * @param location
     * @param country
     * @param state
     * @param email
     * @return Object array containing the <code>PKCS10CertificateRequest</code> in [0], 
     * the <code>KeyPair</code> in [1] and a String with the PEM encoded certificate request in [2]
     */
    public static Object[] generatePKCS10CertificateRequest( String commonName, String organization,
            String organizationUnit, String location, String country, String state, String email ) {

        byte[] requestData = null;
        Object[] result = new Object[4];

        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance( DEFAULT_KEY_ALGORITHM /*, DEFAULT_JCE_PROVIDER*/);

            kpg.initialize( DEFAULT_RSA_KEY_LENGTH );

            KeyPair keyPair = kpg.genKeyPair();

            Hashtable attrs = new Hashtable();

            attrs.put( X509Name.CN, commonName );
            attrs.put( X509Name.C, country );
            // organization = organization.replaceAll( ",", "\\\\," );
            // System.out.println( "O: " + organization );
            attrs.put( X509Name.O, organization );
            attrs.put( X509Name.OU, organizationUnit );
            attrs.put( X509Name.L, location );
            attrs.put( X509Name.ST, state );
            // mail removed for testing..
            //            if ( ( email != null ) && ( email.length() != 0 ) ) {
            //                attrs.put( X509Principal.E, email );
            //                attrs.put( X509Principal.EmailAddress, email );
            //            }

            X509Name subject = new X509Name( attrs );

            PKCS10CertificationRequest certificationRequest = new PKCS10CertificationRequest(
                    DEFAULT_DIGITAL_SIGNATURE_ALGORITHM, subject, keyPair.getPublic(), null, keyPair.getPrivate() );

            result[POS_REQUEST] = certificationRequest;

            result[POS_KEYS] = keyPair;

            // PEM encoded
            result[POS_PEM] = getPKCS10CertificateRequestAsPem( certificationRequest );

            Certificate cert = createSelfSignedCert( commonName, organization, organizationUnit, country, state,
                    location, email, keyPair.getPublic(), keyPair.getPrivate() );
            result[POS_CERT] = cert;

        } catch ( Exception ex ) {
            ex.printStackTrace();
        }

        return result;
    } // generatePKCS10CertificateRequest

    /**
     * @param styleClass
     * @param keyStore
     * @param password
     * @throws Exception
     */
    public static CertificatePojo createPojoFromKeystore( int type, KeyStore keyStore, String password )
            throws NexusException {

        NexusException exception = null;
        try {
            // Fix MS IIS exported PKCS12 structures (.pfx)
            PrivateKey pk = getPrivateKey( keyStore );
            Certificate[] tempCerts = getAllX509Certificate( keyStore );
            KeyStore newKs = KeyStore.getInstance( "PKCS12", "BC" );
            newKs.load( null, null );
            newKs.setKeyEntry( "nexuscert", pk, password.toCharArray(), tempCerts );
            keyStore = newKs;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            keyStore.store( baos, password.toCharArray() );
            byte[] certData = baos.toByteArray();

            Enumeration e = keyStore.aliases();
            if ( !e.hasMoreElements() ) {
                exception = new NexusException( "No alias found in key store!" );
            }
            Certificate[] certificates = keyStore.getCertificateChain( (String) e.nextElement() );
            if ( ( certificates != null ) && ( certificates.length != 0 ) ) {
                String dn = createCertificateIdFromCertificate( (X509Certificate) certificates[0] );

                CertificatePojo certificatePojo = new CertificatePojo();
                certificatePojo.setType( type );
                certificatePojo.setName( dn );
                certificatePojo.setBinaryData( certData );
                certificatePojo.setPassword( EncryptionUtil.encryptString( password ) );
                certificatePojo.setCreatedDate( new Date() );
                certificatePojo.setModifiedDate( new Date() );

                return certificatePojo;

            } else {
                exception = new NexusException( "No certificate chain found, can't import certificate!!" );
            }
        } catch ( Exception e ) {
            exception = new NexusException( "Error importing key store: " + e );
            e.printStackTrace();
        }

        if ( exception != null ) {
            throw exception;
        }
        return null;
    }

    /**
     * Returns the Private key of the certificate's owner from .pfx or .p12
     * file (pkcs12 format)
     * @param keyStore container for information from .pfx or .p12 file
     * @return Owners private key
     * @exception Exception caused by non Exception which can be one of
     * the following: KeyStoreException, UnrecoverableKeyException or
     * NoSuchAlgorithmException.
     */
    public static PrivateKey getPrivateKey( KeyStore keyStore ) throws Exception {

        String keyAlias = null;
        PrivateKey returnPrivateKey = null;

        try {
            Enumeration enumeration = keyStore.aliases();

            while ( enumeration.hasMoreElements() ) {
                String temp = (String) enumeration.nextElement();

                if ( keyStore.isKeyEntry( temp ) ) {
                    keyAlias = temp;
                }
            }
            returnPrivateKey = (PrivateKey) keyStore.getKey( keyAlias, null );
        } catch ( Exception e ) {
            throw new Exception( "Error finding private key!" );
        }
        return returnPrivateKey;
    }

    /**
     * Returns all X509 Certificates stored in .pfx, or .p12 files
     * (KeyStore). This method performs same task as method getCertificateChain() but
     * on less elegant way. In the future project version, it will be completely
     * replaced with the getCertificateChain() method.
     * @param keyStore container for information from .pfx or .p12 file
     * @return Certificate chain represented as array of X509Certificate objects
     * with the owner's certificate at the first place.
     * @exception Exception if problem with extracting certificate
     * chain from .pfx or .p12 file or with aliases in pfx or p12 file arrises.
     * Also, it can be caused by non Exception which is KeyStoreException.
     */
    public static X509Certificate[] getAllX509Certificate( KeyStore keyStore ) throws Exception {

        Vector vector = new Vector( 0, 1 );
        Certificate[] keyEntryCerts = null;
        int numberOfAlias = 0;
        int numberOfCert = 0;
        int numberOfKeyEntry = 0;

        try {
            Enumeration en = keyStore.aliases();

            while ( en.hasMoreElements() ) {
                String temp = (String) en.nextElement();

                numberOfAlias++;
                if ( keyStore.isKeyEntry( temp ) ) { // owner
                    numberOfKeyEntry++;
                    // keyEntryCert = (X509Certificate) keyStore.getCertificate( temp );
                    keyEntryCerts = keyStore.getCertificateChain( temp );
                }
                if ( keyStore.isCertificateEntry( temp ) ) {
                    X509Certificate cerCert;

                    cerCert = (X509Certificate) keyStore.getCertificate( temp ); // Getting certificate
                    vector.add( cerCert );
                    numberOfCert++;
                }
            }
            if ( ( numberOfAlias == numberOfCert + numberOfKeyEntry ) & ( numberOfKeyEntry == 1 ) ) {
                if ( ( keyEntryCerts != null ) && ( keyEntryCerts.length != 0 ) ) {
                    for ( int i = 0; i < keyEntryCerts.length; i++ ) {
                        vector.add( 0, keyEntryCerts[i] );
                    }
                    // vector.add( 0, keyEntryCert ); // if owners certificate is asociated with "key entry" alias
                }
            } else {
                throw new Exception( "Wrong number of entries in key store!" );
            }
        } catch ( Exception e ) {
            e.printStackTrace();
            throw new Exception( "Error extracting certificates from key store! " + e );
        }
        if ( vector.size() != 1 ) {
            vector = getOwnersCertOnTop( vector );
        }
        X509Certificate[] certChain = new X509Certificate[vector.size()];

        for ( int i = 0; i != vector.size(); i++ ) {
            certChain[i] = (X509Certificate) vector.elementAt( i );
        }
        return certChain;
    }

    /**
     * Orders certificates in certificate chain with owner's certificate at the
     * first position, in Vector representation, and root CA at the last position.
     * @param certVector all certificates from .pfx or .p12 file
     * @return Ordered certificates in Vector. Starts from owner's certificate (at
     * first position) and ends with root CA certificate (at last position).
     * @exception Exception if problem with extracting certificate
     * chain from .pfx or .p12 file arrises.
     */
    private static Vector getOwnersCertOnTop( Vector certVector ) throws Exception {

        Vector inOrder = new Vector( 0, 1 );
        HashMap certs = new HashMap();
        boolean ver = false;
        int j = 0;

        // System.out.println( "Before clean-up" );
        // Remove duplicates
        for ( int i = 0; i != certVector.size(); i++ ) {
            // System.out.println( "Cert: " + ((X509Certificate) certVector.elementAt( i )).getSubjectDN() );
            certs.put( ( (X509Certificate) certVector.elementAt( i ) ).getSubjectDN(), certVector.elementAt( i ) );
        }
        certVector = new Vector();
        for ( Iterator iter = certs.values().iterator(); iter.hasNext(); ) {
            certVector.add( iter.next() );
        }
        //        System.out.println( "After clean-up" );
        //        for ( int i = 0; i != certVector.size(); i++ ) {
        //            System.out.println( "Cert: " + ((X509Certificate) certVector.elementAt( i )).getSubjectDN() );
        //        }

        while ( inOrder.size() == 0 ) {
            if ( j == certVector.size() ) {
                throw new Exception( "No certificates found in PKCS12 key store!" );
            }
            for ( int i = 0; i != certVector.size(); i++ ) {
                if ( i != j ) {
                    ver = verification( (X509Certificate) certVector.elementAt( j ), (X509Certificate) certVector
                            .elementAt( i ) );
                    if ( ver ) {
                        inOrder.add( certVector.elementAt( j ) );
                        inOrder.add( certVector.elementAt( i ) );
                        if ( i > j ) {
                            certVector.removeElementAt( i );
                            certVector.removeElementAt( j );
                        } else {
                            certVector.removeElementAt( j );
                            certVector.removeElementAt( i );
                        }
                        break;
                    }
                }
            }
            if ( ver )
                break;
            j++;
        }
        j = 0;
        int lenBefore = certVector.size();

        while ( certVector.size() != 0 ) {
            if ( j > lenBefore ) {
                throw new Exception( "Certificate found which is not part of the certificate chain!" );
            }
            for ( int i = 0; i != certVector.size(); i++ ) {
                ver = verification( (X509Certificate) certVector.elementAt( i ), (X509Certificate) inOrder
                        .firstElement() );
                if ( ver ) {
                    inOrder.add( 0, certVector.elementAt( i ) );
                    certVector.removeElementAt( i );
                    break;
                }
                ver = verification( (X509Certificate) inOrder.lastElement(), (X509Certificate) certVector.elementAt( i ) );
                if ( ver ) {
                    inOrder.add( certVector.elementAt( i ) );
                    certVector.removeElementAt( i );
                    break;
                }
            }
            j++;
        }
        return inOrder;
    }

    /**
     * Checks the relations between two certificates: if one of them is signer
     * of another one.
     * @param cerOwner certificate for check
     * @param cerIssuer certificate for check
     * @return true or false information about signing informatin between certificates
     */
    private static boolean verification( X509Certificate cerOwner, X509Certificate cerIssuer ) {

        boolean ret = false;

        //        System.out.println( "owner : " + cerOwner.getSubjectDN() );
        //        System.out.println( "issuer: " + cerIssuer.getSubjectDN() );

        try {
            if ( cerOwner.getSigAlgOID().equalsIgnoreCase( "1.2.840.113549.1.1.5" ) ) {
                JDKDigestSignature.SHA1WithRSAEncryption sig = new JDKDigestSignature.SHA1WithRSAEncryption();

                sig.initVerify( cerIssuer.getPublicKey() );
                sig.update( cerOwner.getTBSCertificate() );
                ret = sig.verify( cerOwner.getSignature() );
            } else if ( cerOwner.getSigAlgOID().equalsIgnoreCase( "1.2.840.10040.4.3" ) ) {
                Signature sig = Signature.getInstance( "SHA1withDSA", "SUN" );

                sig.initVerify( cerIssuer.getPublicKey() );
                sig.update( cerOwner.getTBSCertificate() );
                ret = sig.verify( cerOwner.getSignature() );
            } else if ( cerOwner.getSigAlgOID().equalsIgnoreCase( "1.2.840.113549.1.1.2" ) ) {
                JDKDigestSignature.MD2WithRSAEncryption sig = new JDKDigestSignature.MD2WithRSAEncryption();

                sig.initVerify( cerIssuer.getPublicKey() );
                sig.update( cerOwner.getTBSCertificate() );
                ret = sig.verify( cerOwner.getSignature() );
            } else if ( cerOwner.getSigAlgOID().equalsIgnoreCase( "1.2.840.113549.1.1.4" ) ) {
                JDKDigestSignature.MD5WithRSAEncryption sig = new JDKDigestSignature.MD5WithRSAEncryption();

                sig.initVerify( cerIssuer.getPublicKey() );
                sig.update( cerOwner.getTBSCertificate() );
                ret = sig.verify( cerOwner.getSignature() );
            }
        } catch ( Exception e ) {
            ret = false;
        }
        return ret;
    }

    /**
     * @param x509Certificate
     * @return
     */
    public static String createCertificateIdFromCertificate( X509Certificate x509Certificate ) {

        return createCertificateIdFromCertificate( x509Certificate, true );
    }

    /**
     * @param x509Certificate
     * @param replaceNonPrintable
     * @return
     */
    public static String createCertificateIdFromCertificate( X509Certificate x509Certificate,
            boolean replaceNonPrintable ) {

        String certId = null;
        PKCS12BagAttributeCarrier bagAttr = null;
        DERBMPString friendlyName = null;

        try {
            /*
             ASN1InputStream ais =  new ASN1InputStream( x509Certificate.getEncoded() );
             X509CertificateStructure certStr = new X509CertificateStructure( (ASN1Sequence)ais.readObject() );
             X509CertificateObject cetObject = new X509CertificateObject( certStr );
             */

            if ( x509Certificate instanceof PKCS12BagAttributeCarrier ) {
                bagAttr = (PKCS12BagAttributeCarrier) x509Certificate;

                // if ( bagAttr instanceof DERBMPString ) {
                friendlyName = (DERBMPString) bagAttr.getBagAttribute( PKCSObjectIdentifiers.pkcs_9_at_friendlyName );
                if ( friendlyName != null ) {
                    LOG.debug( "createCertificateIdFromCertificate - Found friendly name: " + friendlyName.getString() );
                    certId = friendlyName.getString();
                    // Remove all non-printable characters
                    if ( replaceNonPrintable ) {
                        certId = certId.replaceAll( "[\\P{Alpha}]+", "" );
                    }
                }
                // } else {
                //     log.error( "Attribute not an instance of DERBMPString!" );
                // }
            } else {
                LOG.error( "X509Certificate not an instance of PKCS12BagAttributeCarrier!" );
            }

            if ( certId == null ) {
                X509Name x509Name = PrincipalUtil.getSubjectX509Principal( x509Certificate );
                certId = getCertificateInfo( x509Name, X509Name.CN );
                String o = getCertificateInfo( x509Name, X509Name.O );
                String ou = getCertificateInfo( x509Name, X509Name.OU );

                if ( certId == null ) {
                    LOG.debug( "createCertificateIdFromCertificate - CN not found, trying O" );
                    certId = o;
                }
                if ( certId == null ) {
                    LOG.debug( "createCertificateIdFromCertificate - O not found, trying OU" );
                    certId = ou;
                }
            }
        } catch ( Exception e ) {
            LOG.error( "Error retrieving principal from certificate! (" + e + ")" );
        }
        LOG.debug( "createCertificateIdFromCertificate - cert ID: '" + certId + "'" );

        return certId;
    } // createCertificateIdFromCertificate    

    /**
     * Add a certificate RequestPojo
     * @param certBytes The certificate as a byte array
     * @return TRUE if the certificate could be added to the DB
     */
    public static boolean addCertificateToTempKeyStore( byte[] certBytes, CertificatePojo requestPojo ) {

        boolean result = false;
        X509Principal certSubject = null;

        if ( requestPojo == null ) {
            LOG.error( "required certificatepojo is null" );
            return false;
        }
        try {
            X509Certificate x509Certificate = getX509Certificate( certBytes );

            certSubject = getPrincipalFromCertificate( x509Certificate, true );

            HashMap certHashMap = getX509CertificateHashMapFromKeyStore( requestPojo );
            if ( certHashMap.containsKey( certSubject ) ) {
                LOG.debug( "cert already exists: " + certSubject.toString( false, X509Name.RFC2253Symbols ) );
                return false;
            }

            byte[] data = requestPojo.getBinaryData();
            String pwd = EncryptionUtil.decryptString( requestPojo.getPassword() );
            KeyStore jks = KeyStore.getInstance( DEFAULT_KEY_STORE, DEFAULT_JCE_PROVIDER );
            jks.load( new ByteArrayInputStream( data ), pwd.toCharArray() );
            Certificate[] certs = jks.getCertificateChain( DEFAULT_CERT_ALIAS );
            if ( certs.length == 1 ) {
                if ( getCertificateCN( ( (X509Certificate) certs[0] ), true ).startsWith( DEFAULT_SELFSIGNED_PREFIX ) ) {
                    certs = new Certificate[0];
                }
            }
            Certificate[] certsNew = new Certificate[certs.length + 1];
            for ( int i = 0; i < certs.length; i++ ) {
                certsNew[i] = certs[i];
            }
            certsNew[certsNew.length - 1] = x509Certificate;
            RSAPrivateKey privateKey = (RSAPrivateKey) jks.getKey( DEFAULT_CERT_ALIAS, pwd.toCharArray() );

            jks.setKeyEntry( DEFAULT_CERT_ALIAS, privateKey, pwd.toCharArray(), certsNew );

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            jks.store( baos, pwd.toCharArray() );
            data = baos.toByteArray();
            requestPojo.setBinaryData( data );

            certHashMap.put( certSubject, x509Certificate );

            result = true;
        } catch ( Exception e ) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Get the certificate chain for the local certificate
     * @return The certificate chain for the local certificate
     */
    public static Certificate[] getLocalCertificateChain( CertificatePojo certificate ) {

        Certificate[] certs = null;
        byte[] pkcs12 = null;
        String password = null;

        try {
            if ( certificate != null ) {
                pkcs12 = certificate.getBinaryData();
                password = EncryptionUtil.decryptString( certificate.getPassword() );
                if ( pkcs12 != null ) {
                    KeyStore keyStore = KeyStore.getInstance( DEFAULT_KEY_STORE, DEFAULT_JCE_PROVIDER );
                    ByteArrayInputStream bais = new ByteArrayInputStream( pkcs12 );
                    keyStore.load( bais, password.toCharArray() );
                    Enumeration enumeration = keyStore.aliases();
                    if ( enumeration.hasMoreElements() ) {
                        String alias = (String) enumeration.nextElement();
                        certs = keyStore.getCertificateChain( alias );
                    }
                }
            }
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }

        return certs;
    }// getLocalCertificateChain

    /**
     * Retrieve the next missing certificate subject to complete the request certificate chain
     * @return The subject of the next missing certificate in the chain.
     * NULL if chain is complete or no request exists.
     */
    public static X509Principal getMissingPartnerCertificateSubjectDN( CertificatePojo partnerCertificate,
            List<CertificatePojo> certificates ) {

        X509Certificate partnerCert = null;
        byte[] partnerCertBytes = null;
        CertificatePojo certificate = null;

        try {
            if ( partnerCertificate != null ) {
                partnerCertBytes = partnerCertificate.getBinaryData();
                if ( partnerCertBytes != null ) {
                    partnerCert = CertificateUtil.getX509Certificate( partnerCertBytes );
                    HashMap certHashMap = getX509CertificateHashMap( certificates );
                    // log.debug( "getMissingPartnerCertificateSubjectDN - certHashMap before:\n" + certHashMap );
                    X509Principal requestSubject = CertificateUtil.getPrincipalFromCertificate( partnerCert, true );
                    certHashMap.put( requestSubject, partnerCert );
                    // log.debug( "getMissingPartnerCertificateSubjectDN - certHashMap after:\n" + certHashMap );
                    return getMissingCertificateSubjectDN( certHashMap, requestSubject );
                }
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return null;
    } // getMissingPartnerCertificateSubject

    /**
     * Get the certificate chain for the specified partner
     * @param partnerId The partner to retrieve the certificate chain for
     * @return The certificate chain for the partner
     */
    public static Certificate[] getPartnerCertificateChain( CertificatePojo certificate,
            List<CertificatePojo> certificates ) {

        Certificate[] certs = null;
        X509Certificate partnerCert = null;
        byte[] partnerCertBytes = null;

        try {
            if ( certificate != null ) {
                // log.debug("getPartnerCertificateChain: cert pojo found");
                partnerCertBytes = certificate.getBinaryData();
                if ( partnerCertBytes != null ) {
                    partnerCert = getX509Certificate( partnerCertBytes );
                    // log.debug("getPartnerCertificateChain: X509 cert found");

                    X509Principal subject = CertificateUtil.getPrincipalFromCertificate( partnerCert, true );

                    HashMap certHashMap = getX509CertificateHashMap( certificates );

                    certHashMap.put( subject, partnerCert );
                    ArrayList certsList = getCertChainDN( certHashMap, subject );
                    // log.debug("getPartnerCertificateChain: count: " + certsList.size());
                    certs = new Certificate[certsList.size()];
                    int pos = 0;
                    for ( Iterator iter = certsList.iterator(); iter.hasNext(); ) {
                        X509Certificate cert = (X509Certificate) iter.next();
                        certs[pos++] = cert;
                    }
                }
            }
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }

        return certs;
    } // getPartnerCertificateChain

} // CertificateUtil
