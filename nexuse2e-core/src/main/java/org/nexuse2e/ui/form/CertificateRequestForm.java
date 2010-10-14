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

import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * @author guido.esch
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CertificateRequestForm extends ActionForm {

    /**
     * 
     */
    private static final long serialVersionUID = 205631836185559481L;
    private String            commonName       = null;
    private String            organisation     = null;
    private String            organisationUnit = null;
    private String            location         = null;
    private String            state            = null;
    private String            countryCode      = null;
    private String            email            = null;
    private String            password         = null;
    private String            verifyPWD        = null;

    private String            subject          = null;
    private String            pemCSR           = null;

    @Override
    public void reset( ActionMapping arg0, HttpServletRequest arg1 ) {

        setCommonName( null );
        setOrganisation( null );
        setOrganisationUnit( null );
        setLocation( null );
        setState( null );
        setCountryCode( null );
        setEmail( null );
        setPassword( null );
        setVerifyPWD( null );
        setSubject( null );
        setPemCSR( null );
    }

    public void setRequestProperties( String subject ) {

        String token;
        StringTokenizer st = new StringTokenizer( subject, "," );
        while ( st.hasMoreTokens() ) {
            token = st.nextToken();
            if ( token.startsWith( "C=" ) ) {
                setCountryCode( token.substring( 2 ) );
            } else if ( token.startsWith( "CN=" ) ) {
                setCommonName( token.substring( 3 ) );
            } else if ( token.startsWith( "O=" ) ) {
                setOrganisation( token.substring( 2 ) );
            } else if ( token.startsWith( "OU=" ) ) {
                setOrganisationUnit( token.substring( 3 ) );
            } else if ( token.startsWith( "ST=" ) ) {
                setState( token.substring( 3 ) );
            } else if ( token.startsWith( "L=" ) ) {
                setLocation( token.substring( 2 ) );
            } else if ( token.startsWith( "E=" ) ) {
                setEmail( token.substring( 2 ) );
            }
        }
    }

    public String getCommonName() {

        return commonName;
    }

    public void setCommonName( String commonName ) {

        this.commonName = commonName;
    }

    public String getCountryCode() {

        return countryCode;
    }

    public void setCountryCode( String countryCode ) {

        this.countryCode = countryCode;
    }

    public String getLocation() {

        return location;
    }

    public void setLocation( String location ) {

        this.location = location;
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

    public String getPassword() {

        return password;
    }

    public void setPassword( String password ) {

        this.password = password;
    }

    public String getState() {

        return state;
    }

    public void setState( String state ) {

        this.state = state;
    }

    public String getVerifyPWD() {

        return verifyPWD;
    }

    public void setVerifyPWD( String verifyPWD ) {

        this.verifyPWD = verifyPWD;
    }

    public String getEmail() {

        return email;
    }

    public void setEmail( String email ) {

        this.email = email;
    }

    public String getPemCSR() {

        return pemCSR;
    }

    public void setPemCSR( String pemCSR ) {

        this.pemCSR = pemCSR;
    }

    public String getSubject() {

        return subject;
    }

    public void setSubject( String subject ) {

        this.subject = subject;
    }
}
