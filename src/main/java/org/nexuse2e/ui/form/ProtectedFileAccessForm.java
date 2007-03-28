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
package org.nexuse2e.ui.form;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

/**
 * @author guido.esch
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

//protectedfileaccessform
public class ProtectedFileAccessForm extends ActionForm {

    /**
     * 
     */
    private static final long serialVersionUID = -5681743313841088090L;

    public final static int   PEM              = 1;
    public final static int   DER              = 2;

    String                    certficatePath   = null;
    FormFile                  certficate       = null;
    String                    password         = null;
    String                    verifyPwd        = null;
    String                    alias            = null;
    int                       status           = 0;
    int                       format           = 0;
    int                       content          = 0;
    String                    id               = null;
    boolean                   preserve         = false;
    private int               nxCertificateId  = 0;

    @Override
    public void reset( ActionMapping mapping, HttpServletRequest request ) {

        if ( !preserve ) {
            setPassword( null );
            setVerifyPwd( null );
            setCertficate( null );
            // setCertficatePath( null );
            setAlias( null );
            setStatus( 0 );
            setFormat( 0 );
            setContent( 0 );
            setId( null );
            super.reset( mapping, request );
        } else {
            preserve = false;
        }
    }

    public String getCertficatePath() {

        return certficatePath;
    }

    public void setCertficatePath( String certficatePath ) {

        this.certficatePath = certficatePath;
    }

    public String getPassword() {

        return password;
    }

    public void setPassword( String password ) {

        this.password = password;
    }

    public String getVerifyPwd() {

        return verifyPwd;
    }

    public void setVerifyPwd( String verifyPwd ) {

        this.verifyPwd = verifyPwd;
    }

    public String getAlias() {

        return alias;
    }

    public void setAlias( String alias ) {

        this.alias = alias;
    }

    public int getStatus() {

        return status;
    }

    public void setStatus( int status ) {

        this.status = status;
    }

    public int getFormat() {

        return format;
    }

    public void setFormat( int format ) {

        this.format = format;
    }

    public int getContent() {

        return content;
    }

    public void setContent( int content ) {

        this.content = content;
    }

    public String getId() {

        return id;
    }

    public void setId( String id ) {

        this.id = id;
    }

    /**
     * @return Returns the certficate.
     */
    public FormFile getCertficate() {

        return certficate;
    }

    /**
     * @param certficate The certficate to set.
     */
    public void setCertficate( FormFile certficate ) {

        this.certficate = certficate;
    }

    /**
     * @return Returns the preserve.
     */
    public boolean isPreserve() {

        return preserve;
    }

    /**
     * @param preserve The preserve to set.
     */
    public void setPreserve( boolean preserve ) {

        this.preserve = preserve;
    }

    public int getNxCertificateId() {

        return nxCertificateId;
    }

    public void setNxCertificateId( int nxCertificateId ) {

        this.nxCertificateId = nxCertificateId;
    }
}
