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
import java.util.HashSet;
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
import javax.persistence.ManyToOne;
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
import org.hibernate.annotations.Index;

@Entity
@Table(name = "nx_action")
@XmlType(name = "ActionType")
@XmlAccessorType(XmlAccessType.NONE)
public class ActionPojo implements NEXUSe2ePojo {

    /**
     * 
     */
    private static final long       serialVersionUID = 3011019828384391232L;

    @Access(AccessType.PROPERTY)
    @Id
    @Column(name = "nx_action_id")
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int                     nxActionId;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @Index(name="ix_action_1")
    @JoinColumn(name = "nx_choreography_id", nullable=false)    
    private ChoreographyPojo        choreography;
    
    @Column(name = "created_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date                    createdDate;
    
    @Column(name = "modified_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date                    modifiedDate;
    
    @Column(name = "modified_nx_user_id", nullable = false)
	private int                     modifiedNxUserId;
    
    @Column(name = "start_flag", nullable=false)
	private boolean                 start=false;
    
    @Column(name = "end_flag", nullable=false)
	private boolean                 end=false;
    
    @Column(name = "polling_required", nullable = false)
	private boolean                 pollingRequired=false;
    
    @ManyToOne(fetch=FetchType.EAGER)
    @Index(name="ix_action_2")
    @JoinColumn(name = "inbound_nx_pipeline_id", nullable=false)    
    private PipelinePojo            inboundPipeline;
    
    @ManyToOne(fetch=FetchType.EAGER)
    @Index(name="ix_action_3")
    @JoinColumn(name = "outbound_nx_pipeline_id", nullable=false)    
    private PipelinePojo            outboundPipeline;
    
    @ManyToOne(fetch=FetchType.EAGER)
    @Index(name="ix_action_4")
    @JoinColumn(name = "status_update_nx_pipeline_id")
    private PipelinePojo            statusUpdatePipeline;
    
    @Column(name = "name", length = 64, nullable = false)
    private String                  name;
    
    @Column(name = "document_type", length = 64, nullable = true)
    private String                  documentType;
    
    
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "action")
    @Fetch(value = FetchMode.SELECT)
    @XmlElementWrapper(name = "FollowUpActions")
    @XmlElement(name = "FollowUpAction")
    private Set<FollowUpActionPojo> followUpActions  = new HashSet<FollowUpActionPojo>( 0 );
    
    
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "followUpAction")
    @Fetch(value = FetchMode.SELECT)
    @XmlElementWrapper(name = "FollowedActions")
    @XmlElement(name = "FollowedAction")
    private Set<FollowUpActionPojo> followedActions  = new HashSet<FollowUpActionPojo>( 0 );

    // required for jaxb serialization.
    @Transient
    private int                     statusUpdateNxPipelineId;
    
    @Transient
    private int                     inboundNxPipelineId;
    
    @Transient
    private int                     outboundNxPipelineId;


    /**
     * 
     */
    public ActionPojo() {

        createdDate = new Date();
        modifiedDate = createdDate;
    }

    /**
     * @param choreography
     * @param createdDate
     * @param modifiedDate
     * @param modifiedNxUserId
     * @param start
     * @param end
     * @param inboundPipeline
     * @param outboundPipeline
     * @param name
     */
    public ActionPojo( ChoreographyPojo choreography, Date createdDate, Date modifiedDate, int modifiedNxUserId,
            boolean start, boolean end, PipelinePojo inboundPipeline, PipelinePojo outboundPipeline, String name ) {

        this.choreography = choreography;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.modifiedNxUserId = modifiedNxUserId;
        this.start = start;
        this.end = end;
        this.inboundPipeline = inboundPipeline;
        this.outboundPipeline = outboundPipeline;
        this.name = name;
    }

    /** full constructor */
    public ActionPojo( ChoreographyPojo choreography, Date createdDate, Date modifiedDate, int modifiedNxUserId,
            boolean start, boolean end, PipelinePojo inboundPipeline, PipelinePojo outboundPipeline, String name,
            Set<FollowUpActionPojo> followUpActions, Set<FollowUpActionPojo> followedActions ) {

        this.choreography = choreography;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.modifiedNxUserId = modifiedNxUserId;
        this.start = start;
        this.end = end;
        this.inboundPipeline = inboundPipeline;
        this.outboundPipeline = outboundPipeline;
        this.name = name;
        this.followUpActions = followUpActions;
        this.followedActions = followedActions;
    }

    // Property accessors
    @XmlAttribute
    public int getNxActionId() {

        return this.nxActionId;
    }

    public void setNxActionId( int nxActionId ) {

        this.nxActionId = nxActionId;
    }

    public int getNxId() {

        return nxActionId;
    }

    public void setNxId( int nxId ) {

        this.nxActionId = nxId;
    }

    public ChoreographyPojo getChoreography() {

        return this.choreography;
    }

    public void setChoreography( ChoreographyPojo choreography ) {

        this.choreography = choreography;
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
    public boolean isStart() {

        return this.start;
    }

    public void setStart( boolean start ) {

        this.start = start;
    }

    @XmlAttribute
    public boolean isEnd() {

        return this.end;
    }

    public void setEnd( boolean end ) {

        this.end = end;
    }

    /**
     * Required for JAXB 
     * @return
     */
    @XmlAttribute
    public int getInboundNxPipelineId() {

        if ( this.inboundPipeline != null ) {
            return this.inboundPipeline.getNxPipelineId();

        }
        return inboundNxPipelineId;
    }

    /**
     * Required for JAXB
     * @param inboundPipelineId
     */
    public void setInboundNxPipelineId( int inboundPipelineId ) {

        this.inboundNxPipelineId = inboundPipelineId;
    }

    public PipelinePojo getInboundPipeline() {

        return this.inboundPipeline;
    }

    public void setInboundPipeline( PipelinePojo inboundPipeline ) {

        this.inboundPipeline = inboundPipeline;
    }

    /**
     * Required for JAXB 
     * @return
     */
    @XmlAttribute
    public int getOutboundNxPipelineId() {

        if ( this.outboundPipeline != null ) {
            return this.outboundPipeline.getNxPipelineId();

        }
        return outboundNxPipelineId;
    }

    /**
     * Required for JAXB
     * @param outboundPipelineId
     */
    public void setOutboundNxPipelineId( int outboundPipelineId ) {

        this.outboundNxPipelineId = outboundPipelineId;
    }

    public PipelinePojo getOutboundPipeline() {

        return this.outboundPipeline;
    }

    public void setOutboundPipeline( PipelinePojo outboundPipeline ) {

        this.outboundPipeline = outboundPipeline;
    }

    @XmlAttribute
    public String getName() {

        return this.name;
    }

    public void setName( String name ) {

        this.name = name;
    }

    public Set<FollowUpActionPojo> getFollowUpActions() {

        return this.followUpActions;
    }

    public void setFollowUpActions( Set<FollowUpActionPojo> followUpActions ) {

        this.followUpActions = followUpActions;
    }

    public Set<FollowUpActionPojo> getFollowedActions() {

        return this.followedActions;
    }

    public void setFollowedActions( Set<FollowUpActionPojo> followedActions ) {

        this.followedActions = followedActions;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals( Object obj ) {

        if ( !( obj instanceof ActionPojo ) ) {
            return false;
        }
        if ( nxActionId == 0 ) {
            return super.equals( obj );
        }

        return nxActionId == ( (ActionPojo) obj ).nxActionId;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {

        if ( nxActionId == 0 ) {
            return super.hashCode();
        }

        return nxActionId;
    }

    /**
     * Required for JAXB 
     * @return
     */
    @XmlAttribute
    public int getStatusUpdateNxPipelineId() {

        if ( this.statusUpdatePipeline != null ) {
            return this.statusUpdatePipeline.getNxPipelineId();

        }
        return statusUpdateNxPipelineId;
    }

    /**
     * Required for JAXB
     * @param statusUpdateNxPipelineId
     */
    public void setStatusUpdateNxPipelineId( int statusUpdateNxPipelineId ) {

        this.statusUpdateNxPipelineId = statusUpdateNxPipelineId;
    }

    public PipelinePojo getStatusUpdatePipeline() {

        return statusUpdatePipeline;
    }

    public void setStatusUpdatePipeline( PipelinePojo statusUpdatePipeline ) {

        this.statusUpdatePipeline = statusUpdatePipeline;
    }

    /**
     * Required for JAXB
     */
    @XmlAttribute
    public boolean isPollingRequired() {

        return pollingRequired;
    }

    public void setPollingRequired( boolean pollingRequired ) {

        this.pollingRequired = pollingRequired;
    }

    /**
     * Checks if this <code>ActionPojo</code> has a follow-up action with the given action ID.
     * @param actionId The action ID. Shall not be <code>null</code>.
     * @return <code>true</code> if <code>actionId</code> is a valid follow-up action,
     * <code>false</code> otherwise.
     */
    public boolean hasFollowUpAction(String actionId) {
        Set<FollowUpActionPojo> followUpActions = getFollowUpActions();
        if (followUpActions != null) {
            for ( FollowUpActionPojo followUpAction : followUpActions ) {
                if ( followUpAction.getFollowUpAction().getName().equals( actionId ) ) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Required for JAXB
     */
    @XmlAttribute
    public String getDocumentType() {

        return documentType;
    }

    public void setDocumentType( String documentType ) {

        this.documentType = documentType;
    }

}
