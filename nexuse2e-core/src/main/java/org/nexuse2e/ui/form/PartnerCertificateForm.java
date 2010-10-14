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

/**
 * @author guido.esch
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PartnerCertificateForm extends CertificatePropertiesForm {

    /**
     * 
     */
    private static final long serialVersionUID = -7839929061895341728L;

    int                       nxPartnerId;
    int                       nxCertificateId;
    String                    partnerId;
    String                    certificateId;
    int                       seqNo;

    public String getCertificateId() {

        return certificateId;
    }

    public void setCertificateId( String certificateId ) {

        this.certificateId = certificateId;
    }

    public String getPartnerId() {

        return partnerId;
    }

    public void setPartnerId( String partnerId ) {

        this.partnerId = partnerId;
    }

    public int getSeqNo() {

        return seqNo;
    }

    public void setSeqNo( int seqNo ) {

        this.seqNo = seqNo;
    }

    public int getNxPartnerId() {

        return nxPartnerId;
    }

    public void setNxPartnerId( int nxPartnerId ) {

        this.nxPartnerId = nxPartnerId;
    }

    @Override
    public int getNxCertificateId() {

        return nxCertificateId;
    }

    @Override
    public void setNxCertificateId( int nxCertificateId ) {

        this.nxCertificateId = nxCertificateId;
    }
}
