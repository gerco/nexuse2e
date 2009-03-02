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
package org.nexuse2e.ui.action.communications;

import java.io.File;
import java.io.FileOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.bouncycastle.jce.PKCS10CertificationRequest;
import org.nexuse2e.configuration.Constants;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.pojo.CertificatePojo;
import org.nexuse2e.ui.action.NexusE2EAction;
import org.nexuse2e.ui.form.ProtectedFileAccessForm;
import org.nexuse2e.util.CertificateUtil;

/**
 * @author guido.esch
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class RequestSaveCSRFileAction extends NexusE2EAction {

    private static String URL     = "request.error.url";
    private static String TIMEOUT = "request.error.timeout";

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

        if ( form.getStatus() == 1 ) {

            String path = form.getCertficatePath();
            File certFile = new File( path );
            FileOutputStream fos = new FileOutputStream( certFile );

            CertificatePojo certificateRequest = engineConfiguration
                    .getFirstCertificateByType( Constants.CERTIFICATE_TYPE_REQUEST, true );
            PKCS10CertificationRequest csr = CertificateUtil.getPKCS10Request( certificateRequest );
            byte[] data = new byte[0];
            if ( form.getFormat() == ProtectedFileAccessForm.PEM ) {
                String pemCSR = CertificateUtil.getPemData( csr );
                data = pemCSR.getBytes();
            } else if ( form.getFormat() == ProtectedFileAccessForm.DER ) {
                data = (byte[]) CertificateUtil.getDERData( csr );
            } else {
                fos.flush();
                fos.close();
                ActionMessage errorMessage = new ActionMessage( "generic.error", "Specified format is not supported!" );
                errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
                addRedirect( request, URL, TIMEOUT );
                return error;
            }

            fos.write( data );
            fos.flush();
            fos.close();
        } else {
            request.setAttribute( "type", "csr" );
            CertificatePojo certificateRequest = engineConfiguration
                    .getFirstCertificateByType( Constants.CERTIFICATE_TYPE_REQUEST, true );

            System.out.println("nxCertId: "+certificateRequest.getNxCertificateId());
            request.setAttribute( "nxCertificateId", ""+certificateRequest.getNxCertificateId() );
//            form.setNxCertificateId( certificateRequest.getNxCertificateId() );
            if ( form.getFormat() == 1 ) {
                request.setAttribute( "format", "pem" );
            } else if ( form.getFormat() == 2 ) {
                request.setAttribute( "format", "der" );
            }
        }
        if ( !errors.isEmpty() ) {
            return error;
        }

        return success;
    }

}
