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
package org.nexuse2e.pojo;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

/**
 * @author GEsch
 *
 */
@Entity
@Table(name = "nx_partner", uniqueConstraints = { @UniqueConstraint(columnNames = { "type", "partner_id" }) })
@XmlType(name = "PartnerType")
@XmlAccessorType(XmlAccessType.NONE)
public class PartnerPojo implements NEXUSe2ePojo {

     /**
     * 
     */
    private static final long     serialVersionUID = -2216607774115857281L;
    
    @Id
    @Column(name = "nx_partner_id")
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int                   nxPartnerId;

	@Column(name = "type", nullable=false, columnDefinition = "INTEGER")
    private int                   type;

	@Column(name = "company_name", length=128)
	private String                companyName;
	
	@Column(name = "address_line_1", length=128)
	private String                addressLine1;
	
	@Column(name = "address_line_2", length=128)
	private String                addressLine2;
    
	@Column(name = "city", length=128)
	private String                city;
	
	@Column(name = "state", length=128)
	private String                state;
	
	@Column(name = "zip", length=64)
	private String                zip;
    
	@Column(name = "country", length=128)
	private String                country;
    
	@Column(name = "created_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date                  createdDate;
    
	@Column(name = "modified_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date                  modifiedDate;
    
	@Column(name = "modified_nx_user_id", nullable=false, columnDefinition = "INTEGER")
	private int                   modifiedNxUserId;
	
	@Column(name = "name", length=128)
	private String                name;
	
	@Column(name = "description", length=128)
	private String                description;
	
	@Column(name = "partner_id", length=128, nullable=false)
	private String                partnerId;
	
	@Column(name = "partner_id_type", length=128, nullable=false)
	private String                partnerIdType;
    
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "partner")
    @Fetch(value = FetchMode.SUBSELECT)
    private Set<ConversationPojo> conversations    = new HashSet<ConversationPojo>( 0 );
	
    @OneToMany(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE }, mappedBy = "partner")
    @Fetch(value = FetchMode.SUBSELECT)
    private Set<CertificatePojo>  certificates     = new HashSet<CertificatePojo>( 0 );
	
    @OneToMany(fetch = FetchType.EAGER, cascade = { CascadeType.ALL }, mappedBy = "partner")
    @Fetch(value = FetchMode.SUBSELECT)
    @XmlElementWrapper(name = "Connections")
    @XmlElement(name = "Connection")
    private Set<ConnectionPojo>   connections      = new HashSet<ConnectionPojo>( 0 );
    
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "partner")
    @Fetch(value = FetchMode.SUBSELECT)
    private List<ParticipantPojo> participants     = new ArrayList<ParticipantPojo>( 0 );
    
	
	@Transient
	@XmlElementWrapper(name = "CertificateReferences")
    @XmlElement(name = "NxCertificateId")
    private Set<Integer>          nxCertificateIds;
    
    // Constructors

    /** default constructor */
    public PartnerPojo() {

        createdDate = new Date();
        modifiedDate = createdDate;
    }

    /** minimal constructor */
    public PartnerPojo( int type, Date createdDate, Date modifiedDate, int modifiedNxUserId, String partnerId,
            String partnerIdType ) {

        this.type = type;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.modifiedNxUserId = modifiedNxUserId;
        this.partnerId = partnerId;
        this.partnerIdType = partnerIdType;
    }

    /** full constructor */
    public PartnerPojo( int type, String companyName, String addressLine1, String addressLine2, String city,
            String state, String zip, String country, Date createdDate, Date modifiedDate, int modifiedNxUserId,
            String name, String description, String partnerId, String partnerIdType,
            Set<ConversationPojo> conversations, Set<CertificatePojo> certificates, Set<ConnectionPojo> connections,
            List<ParticipantPojo> participants ) {

        this.type = type;
        this.companyName = companyName;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.country = country;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.modifiedNxUserId = modifiedNxUserId;
        this.name = name;
        this.description = description;
        this.partnerId = partnerId;
        this.partnerIdType = partnerIdType;
        this.conversations = conversations;
        this.certificates = certificates;
        this.connections = connections;
        this.participants = participants;
    }

    // Property accessors
    @XmlAttribute
    public int getNxPartnerId() {

        return this.nxPartnerId;
    }

    public void setNxPartnerId( int nxPartnerId ) {

        this.nxPartnerId = nxPartnerId;
    }

    public int getNxId() {
        return nxPartnerId;
    }
    
    public void setNxId( int nxId ) {
        this.nxPartnerId = nxId;
    }
    
    @XmlAttribute
    public int getType() {

        return this.type;
    }

    public void setType( int type ) {

        this.type = type;
    }

    @XmlAttribute
    public String getCompanyName() {

        return this.companyName;
    }

    public void setCompanyName( String companyName ) {

        this.companyName = companyName;
    }

    @XmlAttribute
    public String getAddressLine1() {

        return this.addressLine1;
    }

    public void setAddressLine1( String addressLine1 ) {

        this.addressLine1 = addressLine1;
    }

    @XmlAttribute
    public String getAddressLine2() {

        return this.addressLine2;
    }

    public void setAddressLine2( String addressLine2 ) {

        this.addressLine2 = addressLine2;
    }

    @XmlAttribute
    public String getCity() {

        return this.city;
    }

    public void setCity( String city ) {

        this.city = city;
    }

    @XmlAttribute
    public String getState() {

        return this.state;
    }

    public void setState( String state ) {

        this.state = state;
    }

    @XmlAttribute
    public String getZip() {

        return this.zip;
    }

    public void setZip( String zip ) {

        this.zip = zip;
    }

    @XmlAttribute
    public String getCountry() {

        return this.country;
    }

    public void setCountry( String country ) {

        this.country = country;
    }

    public Date getCreatedDate() {

        return this.createdDate;
    }

    public void setCreatedDate( Date createdDate ) {

        this.createdDate = createdDate;
    }

    public Date getModifiedDate() {

        return this.modifiedDate;
    }

    public void setModifiedDate( Date modifiedDate ) {

        this.modifiedDate = modifiedDate;
    }

    public int getModifiedNxUserId() {

        return this.modifiedNxUserId;
    }

    public void setModifiedNxUserId( int modifiedNxUserId ) {

        this.modifiedNxUserId = modifiedNxUserId;
    }

    @XmlAttribute
    public String getName() {

        return this.name;
    }

    public void setName( String name ) {

        this.name = name;
    }

    @XmlAttribute
    public String getDescription() {

        return this.description;
    }

    public void setDescription( String description ) {

        this.description = description;
    }

    @XmlAttribute
    public String getPartnerId() {

        return this.partnerId;
    }

    public void setPartnerId( String partnerId ) {

        this.partnerId = partnerId;
    }

    @XmlAttribute
    public String getPartnerIdType() {

        return this.partnerIdType;
    }

    public void setPartnerIdType( String partnerIdType ) {

        this.partnerIdType = partnerIdType;
    }

    public Set<ConversationPojo> getConversations() {

        return this.conversations;
    }

    public void setConversations( Set<ConversationPojo> conversations ) {

        this.conversations = conversations;
    }

    /**
     * Required for JAXB
     * @return
     * @deprecated Use {@link CertificatePojo#getNxPartnerId()} for certificate association.
     */
    @Deprecated
    public Set<Integer> getNxCertificateIds() {


        if ( this.certificates != null && (!this.certificates.isEmpty() || nxCertificateIds == null)) {
            Set<Integer> resultSet = new HashSet<Integer>();
            for ( Iterator<CertificatePojo> certificatesIterator = certificates.iterator(); certificatesIterator
                    .hasNext(); ) {
                CertificatePojo certificatePojo = (CertificatePojo) certificatesIterator.next();
                resultSet.add( new Integer( certificatePojo.getNxCertificateId() ) );
            }
            return resultSet;
        }
        return nxCertificateIds;
    }
    
    /**
     * Required for JAXB
     * @param certificateIds
     * @deprecated Use {@link CertificatePojo#setNxPartnerId(int))} for certificate association.
     */
    @Deprecated
    public void setNxCertificateIds( Set<Integer> certificateIds ) {
        this.nxCertificateIds = certificateIds;
    }

    public Set<CertificatePojo> getCertificates() {

        return this.certificates;
    }

    public void setCertificates( Set<CertificatePojo> certificates ) {

        this.certificates = certificates;
    }

    public Set<ConnectionPojo> getConnections() {

        return this.connections;
    }

    public void setConnections( Set<ConnectionPojo> connections ) {

        this.connections = connections;
    }

    public List<ParticipantPojo> getParticipants() {

        return this.participants;
    }

    public void setParticipants( List<ParticipantPojo> participants ) {

        this.participants = participants;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals( Object obj ) {

        if ( obj instanceof PartnerPojo ) {
            if ( nxPartnerId == 0 ) {
                return super.equals( obj );
            }
            return ( this.getNxPartnerId() == ( (PartnerPojo) obj ).getNxPartnerId() );
        }
        return false;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {

        if ( nxPartnerId == 0 ) {
            return super.hashCode();
        }

        return nxPartnerId;
    }

}
