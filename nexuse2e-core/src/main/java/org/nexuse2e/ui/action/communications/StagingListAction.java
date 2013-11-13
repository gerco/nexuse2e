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

import java.io.ByteArrayInputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.bouncycastle.asn1.x509.X509Name;
import org.nexuse2e.configuration.CertificateType;
import org.nexuse2e.configuration.Constants;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.pojo.CertificatePojo;
import org.nexuse2e.ui.action.NexusE2EAction;
import org.nexuse2e.ui.form.CertificatePropertiesForm;
import org.nexuse2e.util.CertificateUtil;
import org.nexuse2e.util.EncryptionUtil;

/**
 * @author gesch
 * 
 */
public class StagingListAction extends NexusE2EAction {

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
        CertificatePropertiesForm form = null;
        Certificate[] certsArray = null;

        Vector<CertificatePropertiesForm> certs = new Vector<CertificatePropertiesForm>();
        List<CertificatePojo> certPojos = engineConfiguration.getCertificates(CertificateType.STAGING.getOrdinal(), Constants.CERTIFICATECOMPARATOR);
        if (certPojos != null) {
            for (CertificatePojo certificate : certPojos) {
                byte[] data = certificate.getBinaryData();
                if (data != null) {

                    KeyStore jks = KeyStore.getInstance(CertificateUtil.DEFAULT_KEY_STORE, CertificateUtil.DEFAULT_JCE_PROVIDER);
                    jks.load(new ByteArrayInputStream(data), EncryptionUtil.decryptString(certificate.getPassword()).toCharArray());

                    Enumeration<String> aliases = jks.aliases();
                    if (!aliases.hasMoreElements()) {
                        LOG.info("No certificate aliases found!");
                        continue;
                    }

                    while (aliases.hasMoreElements()) {
                        String alias = aliases.nextElement();
                        if (jks.isKeyEntry(alias)) {
                            certsArray = jks.getCertificateChain(alias);
                            // LOG.trace( "Cert alias: " + alias );

                            if ((certsArray != null) && (certsArray.length != 0)) {
                                form = new CertificatePropertiesForm();
                                form.setCertificateProperties((X509Certificate) certsArray[0]);
                                form.setNxCertificateId(certificate.getNxCertificateId());
                                Date date = certificate.getCreatedDate();
                                SimpleDateFormat databaseDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                form.setCreated(databaseDateFormat.format(date));
                                String issuerCN = CertificateUtil.getIssuer((X509Certificate) certsArray[certsArray.length - 1], X509Name.CN);
                                form.setIssuerCN(issuerCN);
                                certs.addElement(form);

                                break;
                            }
                        }
                    } // while
                } else {
                    LOG.error("Certificate entry does not contain any binary data: " + certificate.getName());
                }
            } // while
        } else {
            LOG.info("no certs found");
        }

        request.setAttribute(ATTRIBUTE_COLLECTION, certs);
        // request.getSession().setAttribute( Crumbs.CURRENT_LOCATION, Crumbs.STAGING_OVERVIEW );

        return success;
    }

}
