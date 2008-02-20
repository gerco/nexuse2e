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
package org.nexuse2e.ui.action.communications;

import java.io.ByteArrayInputStream;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.nexuse2e.configuration.Constants;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.pojo.CertificatePojo;
import org.nexuse2e.ui.action.NexusE2EAction;
import org.nexuse2e.ui.form.ProtectedFileAccessForm;
import org.nexuse2e.util.EncryptionUtil;

/**
 * @author guido.esch
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CACertSaveKeyStoreAction extends NexusE2EAction {

    private static String URL     = "cacerts.error.url";
    private static String TIMEOUT = "cacerts.error.timeout";

    /* (non-Javadoc)
     * @see com.tamgroup.nexus.e2e.ui.action.NexusE2EAction#executeNexusE2EAction(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.apache.struts.action.ActionMessages)
     */
    @Override
    public ActionForward executeNexusE2EAction( ActionMapping actionMapping, ActionForm actionForm,
            HttpServletRequest request, HttpServletResponse response, EngineConfiguration engineConfiguration, ActionMessages errors, ActionMessages messages )
            throws Exception {

        ActionForward success = actionMapping.findForward( ACTION_FORWARD_SUCCESS );
        ActionForward error = actionMapping.findForward( ACTION_FORWARD_FAILURE );
        ProtectedFileAccessForm form = (ProtectedFileAccessForm) actionForm;

        String pwd = form.getPassword();

        if ( ( form.getCertficate() == null ) || ( form.getCertficate().getFileData() == null ) ) {
            ActionMessage errormessage = new ActionMessage( "cacerts.certfilenotfound",
                    "No data for certificate file submitted!" );
            errors.add( ActionMessages.GLOBAL_MESSAGE, errormessage );
            addRedirect( request, URL, TIMEOUT );
            return error;
        }
        byte[] data = form.getCertficate().getFileData();

        try {
            ByteArrayInputStream bais = new ByteArrayInputStream( data );
            KeyStore jks = KeyStore.getInstance( "JKS" );
            jks.load( bais, pwd.toCharArray() );
            Enumeration e = jks.aliases();
            List<CertificatePojo> certs = new ArrayList<CertificatePojo>();
            
            while ( e.hasMoreElements() ) {
                String alias = (String) e.nextElement();
                X509Certificate cert = (X509Certificate) jks.getCertificate( alias );
                CertificatePojo existingPojo = engineConfiguration.getCertificateByName(
                        Constants.CERTIFICATE_TYPE_CA, alias );
                if ( existingPojo == null ) {
                    CertificatePojo certPojo = new CertificatePojo();
                    certPojo.setType( Constants.CERTIFICATE_TYPE_CA );
                    certPojo.setName( alias );
                    certPojo.setBinaryData( cert.getEncoded() );
                    certPojo.setPassword( "" );
                    certPojo.setCreatedDate( new Date() );
                    certPojo.setModifiedDate( new Date() );
                    LOG.debug( "importing certificate: " + certPojo.getName() );

                    certs.add( certPojo );
                } else {
                    LOG.info( "Alias: " + alias + " already imported" );
                }
            }
            
            // setting password pojo
            
            List<CertificatePojo> metaPojos = engineConfiguration.getCertificates( Constants.CERTIFICATE_TYPE_CACERT_METADATA, null );
            
            if ( metaPojos == null || metaPojos.size() == 0 ) {
                CertificatePojo certPojo = new CertificatePojo();
                certPojo.setType( Constants.CERTIFICATE_TYPE_CACERT_METADATA );
                certPojo.setName( "CaKeyStoreData" );
                certPojo.setPassword( EncryptionUtil.encryptString( pwd ) );
                certPojo.setCreatedDate( new Date() );
                certPojo.setModifiedDate( new Date() );
                
                certs.add( certPojo );
            }
            
            engineConfiguration.updateCertificates( certs );

        } catch ( Exception e ) {
            ActionMessage errorMessage = new ActionMessage( "generic.error", e.getMessage() );
            errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
            addRedirect( request, URL, TIMEOUT );
            return error;
        }

        if ( !errors.isEmpty() ) {
            LOG.error( "CACertSaveKeyStoreAction" + error.toString() );
            return error;
        }

        return success;
    }

}
