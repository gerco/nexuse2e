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

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@Entity
@Table(name = "nx_generic_param")
@XmlType(name = "GenericParamType")
@XmlAccessorType(XmlAccessType.NONE)
public class GenericParamPojo implements NEXUSe2ePojo {

    /**
     * 
     */
    private static final long serialVersionUID = -3820944519302968469L;

    @Id
    @Column(name = "nx_generic_param_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int               nxGenericParamId;

    @Column(name = "category", nullable = false, length = 128)
    private String            category;

    @Column(name = "param_tag", nullable = true, length = 128)
    private String            tag;

    @Column(name = "created_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date              createdDate;

    @Column(name = "modified_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date              modifiedDate;

    @Column(name = "modified_nx_user_id", nullable = false)
    private int               modifiedNxUserId;

    @Transient
    private RolePojo          role;

    @Column(name = "param_name", nullable = false, length = 64)
    private String            paramName;

    @Column(name = "param_label", length = 64)
    private String            label;

    @Column(name = "param_value", length = 1024, nullable = true)
    private String            value;

    @Column(name = "sequence_number")
    private int               sequenceNumber;
    
    /**
     * @return the createdDate
     */
    public Date getCreatedDate() {
    
        return createdDate;
    }
    
    /**
     * @param createdDate the createdDate to set
     */
    public void setCreatedDate( Date createdDate ) {
    
        this.createdDate = createdDate;
    }
    
        
    /**
     * @return the label
     */
    public String getLabel() {
    
        return label;
    }
    
    /**
     * @param label the label to set
     */
    public void setLabel( String label ) {
    
        this.label = label;
    }
    
    /**
     * @return the modifiedDate
     */
    public Date getModifiedDate() {
    
        return modifiedDate;
    }
    
    /**
     * @param modifiedDate the modifiedDate to set
     */
    public void setModifiedDate( Date modifiedDate ) {
    
        this.modifiedDate = modifiedDate;
    }
    
    /**
     * @return the modifiedNxUserId
     */
    public int getModifiedNxUserId() {
    
        return modifiedNxUserId;
    }
    
    /**
     * @param modifiedNxUserId the modifiedNxUserId to set
     */
    public void setModifiedNxUserId( int modifiedNxUserId ) {
    
        this.modifiedNxUserId = modifiedNxUserId;
    }
    
    
    
    /**
     * @return the paramName
     */
    public String getParamName() {
    
        return paramName;
    }
    
    /**
     * @param paramName the paramName to set
     */
    public void setParamName( String paramName ) {
    
        this.paramName = paramName;
    }
    
    /**
     * @return the role
     */
    public RolePojo getRole() {
    
        return role;
    }
    
    /**
     * @param role the role to set
     */
    public void setRole( RolePojo role ) {
    
        this.role = role;
    }
    
    /**
     * @return the sequenceNumber
     */
    public int getSequenceNumber() {
    
        return sequenceNumber;
    }
    
    /**
     * @param sequenceNumber the sequenceNumber to set
     */
    public void setSequenceNumber( int sequenceNumber ) {
    
        this.sequenceNumber = sequenceNumber;
    }
    
    /**
     * @return the tag
     */
    public String getTag() {
    
        return tag;
    }
    
    /**
     * @param tag the tag to set
     */
    public void setTag( String tag ) {
    
        this.tag = tag;
    }
    
    /**
     * @return the value
     */
    public String getValue() {
    
        return value;
    }
    
    /**
     * @param value the value to set
     */
    public void setValue( String value ) {
    
        this.value = value;
    }

    
    /**
     * @return the nxGenericParamId
     */
    public int getNxGenericParamId() {
    
        return nxGenericParamId;
    }

    
    /**
     * @param nxGenericParamId the nxGenericParamId to set
     */
    public void setNxGenericParamId( int nxGenericParamId ) {
    
        this.nxGenericParamId = nxGenericParamId;
    }

    public int getNxId() {
        return nxGenericParamId;
    }
    
    public void setNxId( int nxId ) {
        this.nxGenericParamId = nxId;
    }
    
    /**
     * @return the category
     */
    public String getCategory() {
    
        return category;
    }

    
    /**
     * @param category the category to set
     */
    public void setCategory( String category ) {
    
        this.category = category;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals( Object obj ) {

        if ( !( obj instanceof GenericParamPojo ) ) {
            return false;
        }
        if ( nxGenericParamId == 0 ) {
            return super.equals( obj );
        }

        return nxGenericParamId == ( (GenericParamPojo) obj ).nxGenericParamId;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {

        if ( nxGenericParamId == 0 ) {
            return super.hashCode();
        }

        return nxGenericParamId;
    }
    
}
