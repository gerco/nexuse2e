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

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.nexuse2e.Engine;
import org.nexuse2e.pojo.ActionPojo;
import org.nexuse2e.pojo.PipelinePojo;

/**
 * @author gesch
 *
 */
public class ChoreographyActionForm extends ActionForm {

    /**
     * 
     */
    private static final long  serialVersionUID          = -3829647024934114389L;

    private int                nxActionId                = 0;
    private int                nxChoreographyId          = 0;
    private String             actionId                  = null;
    private String             choreographyId            = null;
    private boolean            startAction               = false;
    private boolean            terminationAction         = false;
    private List<String>       followupActions           = new ArrayList<String>();
    private String             documentType              = null;
    private int                backendInboundPipelineId  = 0;
    private int                backendOutboundPipelineId = 0;
    private int                statusUpdatePipelineId    = 0;

    private List<PipelinePojo> statusUpdatePipelines     = new Vector<PipelinePojo>();
    private List<PipelinePojo> backendInboundPipelines   = new Vector<PipelinePojo>();
    private List<PipelinePojo> backendOutboundPipelines  = new Vector<PipelinePojo>();
    private String[]           followups                 = new String[0];

    /**
     * 
     */
    public ChoreographyActionForm() {

    }

    @Override
    public void reset( ActionMapping arg0, HttpServletRequest arg1 ) {

        setDocumentType( null );
        setStartAction( false );
        setTerminationAction( false );
        setFollowups( new String[0] );
        setBackendInboundPipelineId( 0 );
        setBackendOutboundPipelineId( 0 );
        setStatusUpdatePipelineId( 0 );
    }

    public void cleanSettings() {

        setActionId( null );
        setChoreographyId( null );
        setDocumentType( null );
        setStartAction( false );
        setTerminationAction( false );
        setBackendInboundPipelineId( 0 );
        setBackendOutboundPipelineId( 0 );
        setStatusUpdatePipelineId( 0 );
    }

    public void setProperties( ActionPojo action ) {

        setNxActionId( action.getNxActionId() );
        setNxChoreographyId( action.getChoreography().getNxChoreographyId() );
        setActionId( action.getName() );
        setDocumentType( action.getDocumentType() );
        setChoreographyId( action.getChoreography().getName() );
        setStartAction( action.isStart() );
        setTerminationAction( action.isEnd() );
        if ( action.getInboundPipeline() != null ) {
            setBackendInboundPipelineId( action.getInboundPipeline().getNxPipelineId() );
        }
        if ( action.getOutboundPipeline() != null ) {
            setBackendOutboundPipelineId( action.getOutboundPipeline().getNxPipelineId() );
        }
        if ( action.getStatusUpdatePipeline() != null ) {
            setStatusUpdatePipelineId( action.getStatusUpdatePipeline().getNxPipelineId() );
        }
    }

    public ActionPojo getProperties( ActionPojo action ) {

        action.setDocumentType( getDocumentType() );
        action.setName( getActionId() );
        action.setStart( isStartAction() );
        action.setEnd( isTerminationAction() );
        action.setInboundPipeline( Engine.getInstance().getActiveConfigurationAccessService()
                .getPipelinePojoByNxPipelineId( getBackendInboundPipelineId() ) );
        action.setOutboundPipeline( Engine.getInstance().getActiveConfigurationAccessService()
                .getPipelinePojoByNxPipelineId( getBackendOutboundPipelineId() ) );
        if ( getStatusUpdatePipelineId() != 0 ) {
            action.setStatusUpdatePipeline( Engine.getInstance().getActiveConfigurationAccessService()
                    .getPipelinePojoByNxPipelineId( getStatusUpdatePipelineId() ) );
        }

        return action;
    }

    public String getActionId() {

        return actionId;
    }

    public void setActionId( String actionId ) {

        this.actionId = actionId;
    }

    public String getChoreographyId() {

        return choreographyId;
    }

    public void setChoreographyId( String choreographyId ) {

        this.choreographyId = choreographyId;
    }
    
    public String getDocumentType() {

        return documentType;
    }

    public void setDocumentType(String documentType) {
        
        this.documentType = documentType;
    }

    public List<String> getFollowupActions() {

        return followupActions;
    }

    public void setFollowupActions( List<String> followupActions ) {

        this.followupActions = followupActions;
    }

    public boolean isStartAction() {

        return startAction;
    }

    public void setStartAction( boolean startAction ) {

        this.startAction = startAction;
    }

    public boolean isTerminationAction() {

        return terminationAction;
    }

    public void setTerminationAction( boolean terminationAction ) {

        this.terminationAction = terminationAction;
    }

    public String[] getFollowups() {

        return followups;
    }

    public void setFollowups( String[] followups ) {

        this.followups = followups;
    }

    /**
     * @return the backendInboundPipelineId
     */
    public int getBackendInboundPipelineId() {

        return backendInboundPipelineId;
    }

    /**
     * @param backendInboundPipelineId the backendInboundPipelineId to set
     */
    public void setBackendInboundPipelineId( int backendInboundPipelineId ) {

        this.backendInboundPipelineId = backendInboundPipelineId;
    }

    /**
     * @return the backendInboundPipelines
     */
    public List<PipelinePojo> getBackendInboundPipelines() {

        return backendInboundPipelines;
    }

    /**
     * @param backendInboundPipelinePojos the backendInboundPipelines to set
     */
    public void setBackendInboundPipelines( List<PipelinePojo> backendInboundPipelinePojos ) {

        this.backendInboundPipelines = backendInboundPipelinePojos;
    }

    /**
     * @return the backendOutboundPipelineId
     */
    public int getBackendOutboundPipelineId() {

        return backendOutboundPipelineId;
    }

    /**
     * @param backendOutboundPipelineId the backendOutboundPipelineId to set
     */
    public void setBackendOutboundPipelineId( int backendOutboundPipelineId ) {

        this.backendOutboundPipelineId = backendOutboundPipelineId;
    }

    /**
     * @return the backendOutboundPipelines
     */
    public List<PipelinePojo> getBackendOutboundPipelines() {

        return backendOutboundPipelines;
    }

    /**
     * @param backendOutboundPipelinePojos the backendOutboundPipelines to set
     */
    public void setBackendOutboundPipelines( List<PipelinePojo> backendOutboundPipelinePojos ) {

        this.backendOutboundPipelines = backendOutboundPipelinePojos;
    }

    public int getNxActionId() {

        return nxActionId;
    }

    public void setNxActionId( int nxActionId ) {

        this.nxActionId = nxActionId;
    }

    public int getNxChoreographyId() {

        return nxChoreographyId;
    }

    public void setNxChoreographyId( int nxChoreographyId ) {

        this.nxChoreographyId = nxChoreographyId;
    }

    public int getStatusUpdatePipelineId() {

        return statusUpdatePipelineId;
    }

    public void setStatusUpdatePipelineId( int statusUpdatePipelineId ) {

        this.statusUpdatePipelineId = statusUpdatePipelineId;
    }

    
    public List<PipelinePojo> getStatusUpdatePipelines() {
    
        return statusUpdatePipelines;
    }

    
    public void setStatusUpdatePipelines( List<PipelinePojo> statusUpdatePipelines ) {
    
        this.statusUpdatePipelines = statusUpdatePipelines;
    }
}
