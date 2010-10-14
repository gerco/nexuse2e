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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.nexuse2e.pojo.CertificatePojo;
import org.nexuse2e.pojo.ConnectionPojo;
import org.nexuse2e.pojo.ParticipantPojo;
import org.nexuse2e.pojo.PartnerPojo;

/**
 * @author gesch
 *
 */
public class ParticipantForm extends ActionForm {

    /**
     * 
     */
    private static final long    serialVersionUID     = -2729340836135335512L;

    private String               partnerDisplayName   = null;
    private int                  nxPartnerId          = 0;
    private int                  nxChoreographyId     = 0;
    private int                  nxLocalPartnerId     = 0;
    private int                  nxLocalCertificateId = 0;
    private String               description          = "";
    private String               choreographyId       = null;
    private String               url                  = null;
    private Set<ConnectionPojo>  connections          = new HashSet<ConnectionPojo>();
    private int                  nxConnectionId       = 0;

    private List<PartnerPojo>    partners             = new ArrayList<PartnerPojo>();
    private List<PartnerPojo>    localPartners        = new ArrayList<PartnerPojo>();
    private Set<CertificatePojo> localCertificates    = new HashSet<CertificatePojo>();

    private boolean              submitted            = false;

    /**
     * 
     */
    public ParticipantForm() {

    }

    public void cleanSetting() {

        setConnections( new HashSet<ConnectionPojo>() );
        setNxConnectionId( 0 );
        setNxLocalPartnerId( 0 );
        setNxLocalCertificateId( 0 );
        setNxPartnerId( 0 );
        setDescription( "" );
        setChoreographyId( null );
        setUrl( null );

    }

    public ParticipantPojo getProperties( ParticipantPojo participant ) {

        //        participant.setConnectionSequenceNo( getCurrentConSeqNo() );
        //        String proto = getCurrentProtocol();
        //        
        //        participant.setTransportId( getCurrentTransport() );
        //        participant.setEnforceClientCertificate( isEnforceClientCerts() );
        //        participant.setReliableMessagingActive( isReliable() );
        //        participant.setReliableMessagingTimeout( getTimeout() );
        //        participant.setReliableMessagingInterval( getInterval() );
        //        participant.setReliableMessagingRetries( getRetries() );
        return participant;
    }

    public void setProperties( ParticipantPojo participant ) {

        //        setPartnerId( participant.getParticipantKey().getPartnerId() );
        //
        //        setConnections( participant.getPartner().getConnections() );
        //

        //        ConnectionPojo con = getCurrentConnection();
        //        if ( con != null ) {
        //            setUrl( con.getUri() );
        //        }
    }

    public String getUrl() {

        return url;
    }

    public void setUrl( String url ) {

        this.url = url;
    }

    public Set<ConnectionPojo> getConnections() {

        return connections;
    }

    public void setConnections( Set<ConnectionPojo> connections ) {

        this.connections = connections;
    }

    public String getChoreographyId() {

        return choreographyId;
    }

    public void setChoreographyId( String choreographyId ) {

        this.choreographyId = choreographyId;
    }

    public List<PartnerPojo> getPartners() {

        return partners;
    }

    public void setPartners( List<PartnerPojo> partners ) {

        this.partners = partners;
    }

    public boolean isSubmitted() {

        return submitted;
    }

    public void setSubmitted( boolean submitted ) {

        this.submitted = submitted;
    }

    @Override
    public void reset( ActionMapping arg0, HttpServletRequest arg1 ) {

        setSubmitted( false );
    }

    /**
     * @return the description
     */
    public String getDescription() {

        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription( String description ) {

        this.description = description;
    }

    /**
     * @return the nxConnectionId
     */
    public int getNxConnectionId() {

        return nxConnectionId;
    }

    /**
     * @param nxConnectionId the nxConnectionId to set
     */
    public void setNxConnectionId( int nxConnectionId ) {

        this.nxConnectionId = nxConnectionId;
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
     * @return the nxLocalPartnerId
     */
    public int getNxLocalPartnerId() {

        return nxLocalPartnerId;
    }

    /**
     * @param nxLocalPartnerId the nxLocalPartnerId to set
     */
    public void setNxLocalPartnerId( int nxLocalPartnerId ) {

        this.nxLocalPartnerId = nxLocalPartnerId;
    }

    /**
     * @return the nxPartnerId
     */
    public int getNxPartnerId() {

        return nxPartnerId;
    }

    /**
     * @param nxPartnerId the nxPartnerId to set
     */
    public void setNxPartnerId( int nxPartnerId ) {

        this.nxPartnerId = nxPartnerId;
    }

    /**
     * @return the partnerDisplayName
     */
    public String getPartnerDisplayName() {

        return partnerDisplayName;
    }

    /**
     * @param partnerDisplayName the partnerDisplayName to set
     */
    public void setPartnerDisplayName( String partnerDisplayName ) {

        this.partnerDisplayName = partnerDisplayName;
    }

    /**
     * @return the nxLocalCertificateId
     */
    public int getNxLocalCertificateId() {

        return nxLocalCertificateId;
    }

    /**
     * @param nxLocalCertificateId the nxLocalCertificateId to set
     */
    public void setNxLocalCertificateId( int nxLocalCertificateId ) {

        this.nxLocalCertificateId = nxLocalCertificateId;
    }

    /**
     * @return the localCertificats
     */
    public Set<CertificatePojo> getLocalCertificates() {

        return localCertificates;
    }

    /**
     * @param localCertificats the localCertificats to set
     */
    public void setLocalCertificates( Set<CertificatePojo> localCertificates ) {

        this.localCertificates = localCertificates;
    }

    public int getNxChoreographyId() {

        return nxChoreographyId;
    }

    public void setNxChoreographyId( int nxChoreographyId ) {

        this.nxChoreographyId = nxChoreographyId;
    }

}
