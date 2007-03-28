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

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.nexuse2e.pojo.ConversationPojo;
import org.nexuse2e.util.DateUtil;

/**
 * @author guido.esch
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ReportConversationEntryForm extends ActionForm {

    /**
     * 
     */
    private static final long serialVersionUID = -2341300310540390803L;
    private String            choreographyId   = null;
    private String            conversationId   = null;
    private String            participantId    = null;
    private String            status           = null;
    private String            action           = null;
    private Date              createdDate      = null;
    private Date              modifiedDate     = null;
    private Date              endDate          = null;
    private String            turnaroundTime   = null;
    private String            timezone         = null;

    public void setValues( ConversationPojo pojo ) {

        setChoreographyId( pojo.getChoreography().getName() );
        setConversationId( pojo.getConversationId() );
        setParticipantId( pojo.getPartner().getPartnerId() );

        setStatus( "" + pojo.getStatus() );
        switch ( pojo.getStatus() ) {
            case org.nexuse2e.Constants.CONVERSATION_STATUS_ACK_SENT_AWAITING_BACKEND:
                setStatus( "Ack sent, awaiting Backend (" + pojo.getStatus() + ")" );
                break;
            case org.nexuse2e.Constants.CONVERSATION_STATUS_AWAITING_ACK:
                setStatus( "Awaiting Ack (" + pojo.getStatus() + ")" );
                break;
            case org.nexuse2e.Constants.CONVERSATION_STATUS_AWAITING_BACKEND:
                setStatus( "Awaiting Backend (" + pojo.getStatus() + ")" );
                break;
            case org.nexuse2e.Constants.CONVERSATION_STATUS_BACKEND_SENT_SENDING_ACK:
                setStatus( "Backend sent, sending Ack (" + pojo.getStatus() + ")" );
                break;
            case org.nexuse2e.Constants.CONVERSATION_STATUS_CREATED:
                setStatus( "Created (" + pojo.getStatus() + ")" );
                break;
            case org.nexuse2e.Constants.CONVERSATION_STATUS_ERROR:
                setStatus( "Error (" + pojo.getStatus() + ")" );
                break;
            case org.nexuse2e.Constants.CONVERSATION_STATUS_IDLE:
                setStatus( "Idle (" + pojo.getStatus() + ")" );
                break;
            case org.nexuse2e.Constants.CONVERSATION_STATUS_PROCESSING:
                setStatus( "Processing (" + pojo.getStatus() + ")" );
                break;
            case org.nexuse2e.Constants.CONVERSATION_STATUS_COMPLETED:
                setStatus( "Completed (" + pojo.getStatus() + ")" );
                break;
            case org.nexuse2e.Constants.CONVERSATION_STATUS_SENDING_ACK:
                setStatus( "Sending Ack (" + pojo.getStatus() + ")" );
                break;
            default:
                setStatus( "unknown(" + pojo.getStatus() + ")" );
        }

        setAction( pojo.getCurrentAction().getName() );
        setCreatedDate( pojo.getCreatedDate() );
        if ( pojo.getEndDate() == null ) {
            //setEndDate( "" );
        } else {
            setEndDate( pojo.getEndDate() );
        }
        setModifiedDate( pojo.getModifiedDate() );
        //        if ( pojo.getEndDate() == null || pojo.getEndDate().equals( "" ) ) {
        //            setTurnaroundTime( "not terminated" );
        //        } else {
        //            setTurnaroundTime( DateWrapper.getDiffTimeRounded( pojo.getCreatedDate(), pojo.getEndDate() ) );
        //        }
    }

    @Override
    public void reset( ActionMapping arg0, HttpServletRequest arg1 ) {

        setChoreographyId( null );
        setConversationId( null );
        setParticipantId( null );
        setStatus( null );
        setAction( null );
        setCreatedDate( null );
        setModifiedDate( null );
        setEndDate( null );
        setTurnaroundTime( null );
    }

    public String getAction() {

        return action;
    }

    public void setAction( String action ) {

        this.action = action;
    }

    public String getCreatedDate() {

        if ( createdDate == null || createdDate.equals( "" ) ) {
            return "";
        }
        return DateUtil.localTimeToTimezone( createdDate, timezone, null );
    }

    public void setCreatedDate( Date createdDate ) {

        this.createdDate = createdDate;
    }

    public String getParticipantId() {

        return participantId;
    }

    public void setParticipantId( String participantId ) {

        this.participantId = participantId;
    }

    public String getStatus() {

        return status;
    }

    public void setStatus( String status ) {

        this.status = status;
    }

    public String getChoreographyId() {

        return choreographyId;
    }

    public void setChoreographyId( String choreographyId ) {

        this.choreographyId = choreographyId;
    }

    public String getConversationId() {

        return conversationId;
    }

    public void setConversationId( String conversationId ) {

        this.conversationId = conversationId;
    }

    public String getEndDate() {

        if ( endDate == null || endDate.equals( "" ) ) {
            return "";
        }
        return DateUtil.localTimeToTimezone( endDate, timezone, null );
    }

    public void setEndDate( Date endDate ) {

        this.endDate = endDate;
    }

    public String getModifiedDate() {

        if ( modifiedDate == null || modifiedDate.equals( "" ) ) {
            return "";
        }
        return DateUtil.localTimeToTimezone( modifiedDate, timezone, null );
    }

    public void setModifiedDate( Date modifiedDate ) {

        this.modifiedDate = modifiedDate;
    }

    public String getTurnaroundTime() {

        return turnaroundTime;
    }

    public void setTurnaroundTime( String turnaroundTime ) {

        this.turnaroundTime = turnaroundTime;
    }

    public String getTimezone() {

        return timezone;
    }

    public void setTimezone( String timezone ) {

        this.timezone = timezone;
    }
}
