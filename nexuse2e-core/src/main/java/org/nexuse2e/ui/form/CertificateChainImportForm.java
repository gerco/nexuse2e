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

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class CertificateChainImportForm extends ActionForm {

    /**
     * 
     */
    private static final long     serialVersionUID = 3915131065107959684L;

    private Vector<CertificatePropertiesForm> caImports;
    private Vector<CertificatePropertiesForm> certChain;
    private boolean               accept           = false;
    
    public void reset( ActionMapping mapping, HttpServletRequest request ) {
        caImports = null;
        certChain = null;
        accept = false;
    }
    
    /**
     * @return the accept
     */
    public boolean isAccept() {
    
        return accept;
    }
    
    /**
     * @param accept the accept to set
     */
    public void setAccept( boolean accept ) {
    
        this.accept = accept;
    }
    
    /**
     * @return the caImports
     */
    public Vector<CertificatePropertiesForm> getCaImports() {
    
        return caImports;
    }
    
    /**
     * @param caImports the caImports to set
     */
    public void setCaImports( Vector<CertificatePropertiesForm> caImports ) {
    
        this.caImports = caImports;
    }
    
    /**
     * @return the certChain
     */
    public Vector<CertificatePropertiesForm> getCertChain() {
    
        return certChain;
    }
    
    /**
     * @param certChain the certChain to set
     */
    public void setCertChain( Vector<CertificatePropertiesForm> certChain ) {
    
        this.certChain = certChain;
    }
    
    

}
