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

import java.util.List;

import org.apache.struts.action.ActionForm;
import org.nexuse2e.pojo.PartnerPojo;

/**
 * @author gesch
 *
 */
public class CertificatePromotionForm extends ActionForm {

    /**
     * 
     */
    private static final long               serialVersionUID = 536952753723321140L;

    private List<PartnerPojo>               localPartners;
    private List<CertificatePropertiesForm> certificateParts;
    private int                             localNxPartnerId;
    private int                             nxCertificateId  = 0;

    /**
     * @return the localNxPartnerId
     */
    public int getLocalNxPartnerId() {

        return localNxPartnerId;
    }

    /**
     * @param localNxPartnerId the localNxPartnerId to set
     */
    public void setLocalNxPartnerId( int localNxPartnerId ) {

        this.localNxPartnerId = localNxPartnerId;
    }

    /**
     * @return the localPartners
     */
    public List<PartnerPojo> getLocalPartners() {

        return localPartners;
    }

    /**
     * @param localPartners the localPartners to set
     */
    public void setLocalPartners( List<PartnerPojo> localPartners ) {

        this.localPartners = localPartners;
    }

    /**
     * @return the certificateParts
     */
    public List<CertificatePropertiesForm> getCertificateParts() {

        return certificateParts;
    }

    /**
     * @param certificateParts the certificateParts to set
     */
    public void setCertificateParts( List<CertificatePropertiesForm> certificateParts ) {

        this.certificateParts = certificateParts;
    }

    public int getNxCertificateId() {

        return nxCertificateId;
    }

    public void setNxCertificateId( int nxCertificateId ) {

        this.nxCertificateId = nxCertificateId;
    }

}
