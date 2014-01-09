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

// Generated 15.12.2006 16:07:02 by Hibernate Tools 3.2.0.beta6a

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.nexuse2e.ui.security.AccessController;
import org.nexuse2e.ui.security.AccessController.ParsedRequest;

/**
 * 
 */
@Entity
@Table(name = "nx_role")
@XmlType(name = "RoleType")
@XmlAccessorType(XmlAccessType.NONE )
public class RolePojo implements NEXUSe2ePojo {

    // Fields    

    private static final long      serialVersionUID = 4542844458155873891L;

    @Id
    @Column(name = "nx_role_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int                    nxRoleId;

    @Column(name = "name", length = 64, nullable = false)
    private String                 name;

    @Column(name = "description", length = 64)
    private String                 description;

    @Column(name = "created_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date                   createdDate;

    @Column(name = "modified_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date                   modifiedDate;

    @Column(name = "modified_nx_user_id", nullable = false)
    private int                    modifiedNxUserId;

    // mapping is defined on the getter method because of dedicated property access for this field.(grantMap is initialized on getGrantMap)
    /**
     * 
     */
    private Map<String,GrantPojo>  grantMap           = new HashMap<String,GrantPojo>();

    @Transient
    private Collection<GrantPojo>         grants;
    
    // Constructors

    /** default constructor */
    public RolePojo() {
        createdDate = new Date();
        modifiedDate = createdDate;
    }

    /** minimal constructor */
    public RolePojo( String name, Date createdDate, Date modifiedDate, int modifiedNxUserId ) {

        this.name = name;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.modifiedNxUserId = modifiedNxUserId;
    }

    /** full constructor */
    public RolePojo( String name, String description, Date createdDate, Date modifiedDate, int modifiedNxUserId,
            Map<String,GrantPojo> grants ) {

        this.name = name;
        this.description = description;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.modifiedNxUserId = modifiedNxUserId;
        this.grantMap = grants;
    }

    // Property accessors
    @XmlAttribute
    public int getNxRoleId() {

        return this.nxRoleId;
    }

    public void setNxRoleId( int nxRoleId ) {

        this.nxRoleId = nxRoleId;
    }

    public int getNxId() {
        return nxRoleId;
    }
    
    public void setNxId( int nxId ) {
        this.nxRoleId = nxId;
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

    /**
     * don't remove the access annotation, its currently required 
     */
    @Access(AccessType.PROPERTY)
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @MapKey(name = "target")
    @JoinColumn(name="nxGrantId", nullable=false)
    @Fetch(value = FetchMode.SUBSELECT)
    public Map<String,GrantPojo> getGrantMap() {

        if (grants != null) {
            for (GrantPojo grant : grants) {
                grantMap.clear();
                grantMap.put( grant.getTarget(), grant );
            }
            grants = null;
        }
        return this.grantMap;
    }

    public void setGrantMap( Map<String,GrantPojo> grants ) {
        this.grantMap = grants;   
        grants = null;
    }
    
    /**
     * Required for JAXB
     * @return
     */
    @XmlElementWrapper(name = "Grants")
    @XmlElement(name = "Grant")
    public Collection<GrantPojo> getGrants() {

        if (grants == null) {
            Set<GrantPojo> grants = new HashSet<GrantPojo>();
            grants.addAll( getGrantMap().values() );
            this.grants = grants;
        }
        return grants;
    }
    
    /**
     * Required for JAXB
     */
    public void setGrants( Collection<GrantPojo> grants ) {
        this.grants = grants;
    }
    
    public Map<String, Set<ParsedRequest>> getAllowedRequests() {
        Map<String, Set<ParsedRequest>> allowedRequests = new HashMap<String, Set<ParsedRequest>>();
        for ( GrantPojo grant : getGrantMap().values() ) {
            ParsedRequest pr = AccessController.parseRequestUrl( grant.getTarget() );
            Set<ParsedRequest> requestSet = allowedRequests.get( pr.getActionMapping() );
            if ( requestSet == null ) {
                requestSet = new HashSet<ParsedRequest>();
                allowedRequests.put( pr.getActionMapping(), requestSet );
            }
            requestSet.add( pr );
        }
        
        return allowedRequests;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals( Object obj ) {

        if ( !( obj instanceof RolePojo ) ) {
            return false;
        }

        if ( nxRoleId == 0 ) {
            return super.equals( obj );
        }

        return nxRoleId == ( (RolePojo) obj ).nxRoleId;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {

        if ( nxRoleId == 0 ) {
            return super.hashCode();
        }

        return nxRoleId;
    }
}
