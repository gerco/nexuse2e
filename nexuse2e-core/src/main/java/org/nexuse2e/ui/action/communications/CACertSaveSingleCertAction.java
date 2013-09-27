/**
 * NEXUSe2e Business Messaging Open Source
 * Copyright 2000-2009, Tamgroup and X-ioma GmbH
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

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.nexuse2e.configuration.CertificateType;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.pojo.CertificatePojo;
import org.nexuse2e.ui.action.NexusE2EAction;
import org.nexuse2e.ui.form.ProtectedFileAccessForm;
import org.nexuse2e.util.CertificateUtil;

/**
 * @author gesch
 * 
 */
public class CACertSaveSingleCertAction extends NexusE2EAction {

    private static String URL     = "cacerts.error.url";
    private static String TIMEOUT = "cacerts.error.timeout";

    /*
     * (non-Javadoc)
     * 
     * @see com.tamgroup.nexus.e2e.ui.action.NexusE2EAction#executeNexusE2EAction(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm,
     * javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.apache.struts.action.ActionMessages)
     */
    @Override
    public ActionForward executeNexusE2EAction(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest request, HttpServletResponse response,
            EngineConfiguration engineConfiguration, ActionMessages errors, ActionMessages messages) throws Exception {

        ActionForward success = actionMapping.findForward(ACTION_FORWARD_SUCCESS);
        ActionForward error = actionMapping.findForward(ACTION_FORWARD_FAILURE);
        ProtectedFileAccessForm form = (ProtectedFileAccessForm) actionForm;

        // String url = form.getCertficatePath();
        // TODO change save as Key from alias to nxCertificateId
        String alias = form.getAlias();
        String fileName = "File name n/a";

        try {

            if ((form.getCertficate() == null) || (form.getCertficate().getFileData() == null)) {
                ActionMessage errormessage = new ActionMessage("cacerts.certfilenotfound", "No data for certificate file submitted!");
                errors.add(ActionMessages.GLOBAL_MESSAGE, errormessage);
                addRedirect(request, URL, TIMEOUT);
                return error;
            }
            byte[] data = form.getCertficate().getFileData();
            if (form.getCertficate().getFileName() != null) {
                fileName = form.getCertficate().getFileName();
            }
            try {
                CertificateUtil.getX509Certificate(data);
            } catch (Exception e1) {
                ActionMessage errormessage = new ActionMessage("cacerts.invalidcert", fileName);
                errors.add(ActionMessages.GLOBAL_MESSAGE, errormessage);
                addRedirect(request, URL, TIMEOUT);
                return error;
            }

            CertificatePojo certificate = engineConfiguration.getCertificateByName(CertificateType.CA.getOrdinal(), alias);
            if (certificate == null) {

                certificate = new CertificatePojo();
                certificate.setName(alias);
                certificate.setType(CertificateType.CA.getOrdinal());
                certificate.setPassword("");
                certificate.setCreatedDate(new Date());

                certificate.setModifiedDate(new Date());
                certificate.setBinaryData(data);
                engineConfiguration.updateCertificate(certificate);

            } else {
                certificate.setModifiedDate(new Date());
                certificate.setBinaryData(data);
                engineConfiguration.updateCertificate(certificate);
            }

        } catch (Exception e) {
            e.printStackTrace();
            ActionMessage errormessage = new ActionMessage("generic.error", e.getMessage());
            errors.add(ActionMessages.GLOBAL_MESSAGE, errormessage);
            addRedirect(request, URL, TIMEOUT);
            return error;
        }

        return success;
    }

}
