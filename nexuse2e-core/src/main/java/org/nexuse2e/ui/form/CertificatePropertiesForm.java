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
package org.nexuse2e.ui.form;

import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;

import org.apache.struts.action.ActionForm;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.jce.X509Principal;
import org.nexuse2e.util.CertificateUtil;

/**
 * @author guido.esch
 */
public class CertificatePropertiesForm extends ActionForm {

    /**
     * 
     */
    private static final long serialVersionUID = 8526202214940679659L;
    private String            alias            = null;                // alias
    private String            commonName       = null;                // cn
    private String            organisation     = null;                // o
    private String            organisationUnit = null;                // ou
    private String            state            = null;                // st
    private String            location         = null;                // l 
    private String            email            = null;                // e
    private String            country          = null;                // c
    private String            domain           = null;                // dc
    private String            surname          = null;                // sn
    //    private String t = null; // t
    private String            notAfter         = null;
    private String            notBefore        = null;
    private String            valid            = null;
    private String            timeRemaining    = null;
    private String            fingerprint      = null;
    private String            created          = null;
    private String            issuerCN         = null;

    private X509Certificate   cert             = null;

    private int               nxCertificateId  = 0;

    public void setCertificateProperties( X509Certificate x509 ) {

        setCert( x509 );
        setPrincipal( CertificateUtil.getPrincipalFromCertificate( x509, true ) );
        setNotAfter( "" + x509.getNotAfter() );
        setNotBefore( "" + x509.getNotBefore() );
        String valid = "Okay";
        try {
            x509.checkValidity();
        } catch ( CertificateExpiredException e ) {
            valid = "Certificate has expired";
        } catch ( CertificateNotYetValidException e ) {
            valid = "Certificate not valid yet";
        }
        setValid( valid );

        String remaining = CertificateUtil.getRemainingValidity( x509 );
        setTimeRemaining( remaining );

        try {
            setFingerprint( CertificateUtil.getMD5Fingerprint( x509 ) );
        } catch ( CertificateEncodingException e1 ) {
            setFingerprint( "not available" );
        }
    }
    
    public void setPrincipal( X509Principal principal ) {
        setCommonName( CertificateUtil.getCertificateInfo( principal, X509Name.CN ) );
        setCountry( CertificateUtil.getCertificateInfo( principal, X509Name.C ) );
        setOrganisation( CertificateUtil.getCertificateInfo( principal, X509Name.O ) );
        setOrganisationUnit( CertificateUtil.getCertificateInfo( principal, X509Name.OU ) );
        setEmail( CertificateUtil.getCertificateInfo( principal, X509Name.E ) );
        setState( CertificateUtil.getCertificateInfo( principal, X509Name.ST ) );
        setLocation( CertificateUtil.getCertificateInfo( principal, X509Name.L ) );
    }

    public String getCommonName() {

        return commonName;
    }

    public void setCommonName( String commonName ) {

        this.commonName = commonName;
    }

    public String getCountry() {

        return country;
    }

    public void setCountry( String country ) {

        this.country = country;
    }

    public String getDomain() {

        return domain;
    }

    public void setDomain( String domain ) {

        this.domain = domain;
    }

    public String getEmail() {

        return email;
    }

    public void setEmail( String email ) {

        this.email = email;
    }

    public String getLocation() {

        return location;
    }

    public void setLocation( String location ) {

        this.location = location;
    }

    public String getNotAfter() {

        return notAfter;
    }

    public void setNotAfter( String notAfter ) {

        this.notAfter = notAfter;
    }

    public String getNotBefore() {

        return notBefore;
    }

    public void setNotBefore( String notBefore ) {

        this.notBefore = notBefore;
    }

    public String getOrganisation() {

        return organisation;
    }

    public void setOrganisation( String organisation ) {

        this.organisation = organisation;
    }

    public String getOrganisationUnit() {

        return organisationUnit;
    }

    public void setOrganisationUnit( String organisationUnit ) {

        this.organisationUnit = organisationUnit;
    }

    public String getState() {

        return state;
    }

    public void setState( String state ) {

        this.state = state;
    }

    public String getSurname() {

        return surname;
    }

    public void setSurname( String surname ) {

        this.surname = surname;
    }

    public String getValid() {

        return valid;
    }

    public void setValid( String valid ) {

        this.valid = valid;
    }

    public String getTimeRemaining() {

        return timeRemaining;
    }

    public void setTimeRemaining( String timeRemaining ) {

        this.timeRemaining = timeRemaining;
    }

    public String getAlias() {

        return alias;
    }

    public void setAlias( String alias ) {

        this.alias = alias;
    }

    public String getFingerprint() {

        return fingerprint;
    }

    public void setFingerprint( String fingerprint ) {

        this.fingerprint = fingerprint;
    }

    public String getCreated() {

        return created;
    }

    public void setCreated( String created ) {

        this.created = created;
    }

    public String getIssuerCN() {

        return issuerCN;
    }

    public void setIssuerCN( String issuerCN ) {

        this.issuerCN = issuerCN;
    }

    /**
     * @return the nxCertificateId
     */
    public int getNxCertificateId() {

        return nxCertificateId;
    }

    /**
     * @param nxCertificateId the nxCertificateId to set
     */
    public void setNxCertificateId( int nxCertificateId ) {

        this.nxCertificateId = nxCertificateId;
    }

    
    /**
     * @return the cert
     */
    public X509Certificate getCert() {
    
        return cert;
    }

    
    /**
     * @param cert the cert to set
     */
    public void setCert( X509Certificate cert ) {
    
        this.cert = cert;
    }
}
