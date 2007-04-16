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
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * @author guido.esch
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ReportingPropertiesForm extends ActionForm {

    /**
     * 
     */
    private static final long serialVersionUID     = 3017346285374318341L;
    private String            status               = null;
    private String            choreographyId       = null;
    private Vector            participantIds       = null;
    private Vector            choreographyIds      = null;
    private Vector            originIds            = null;
    private String            conversationId       = null;
    private String            startYear            = null;
    private String            startMonth           = null;
    private String            startDay             = null;
    private String            startHour            = null;
    private String            startMin             = null;
    private String            endYear              = null;
    private String            endMonth             = null;
    private String            endDay               = null;
    private String            endHour              = null;
    private String            endMin               = null;
    private boolean           startEnabled         = true;
    private boolean           endEnabled           = true;
    private boolean           conversationEnabled  = false;
    private boolean           messageEnabled       = false;
    private boolean           messageTextEnabled   = false;
    private String            searchFor            = null;
    private String            messageId            = null;
    private boolean           nextActive           = false;
    private boolean           lastActive           = false;
    private boolean           firstActive          = false;
    private boolean           prevActive           = false;
    private int               startCount           = 0;
    private int               endCount             = 0;
    private int               allItemsCount        = 0;
    private int               pageSize             = 20;
    private String            command              = null;
    private String[]          selected             = new String[0];
    private String            participantId        = null;
    private String            origin               = null;
    private String            severity             = null;
    private String            messageText          = null;
    private String            timezone             = null;

    private boolean           convColSelect        = false;
    private boolean           convColChorId        = false;
    private boolean           convColConId         = false;
    private boolean           convColPartId        = false;
    private boolean           convColStatus        = false;
    private boolean           convColAction        = false;
    private boolean           convColCreated       = false;
    private boolean           convColTurnaround    = false;

    private boolean           messColSelect        = false;
    private boolean           messColMessageId     = false;
    private boolean           messColParticipantId = false;
    private boolean           messColStatus        = false;
    private boolean           messColType          = false;
    private boolean           messColAction        = false;
    private boolean           messColCreated       = false;
    private boolean           messColTurnaround    = false;

    private boolean           engineColSeverity    = false;
    private boolean           engineColIssued      = false;
    private boolean           engineColDescription = false;
    private boolean           engineColOrigin      = false;
    private boolean           engineColClassName   = false;
    private boolean           engineColmethodName  = false;

    /**
     * 
     */
    public ReportingPropertiesForm() {

        super();

        setFirstActive( false );
        setNextActive( false );
        setLastActive( false );
        setPrevActive( false );
        setStartCount( 0 );
        setEndCount( 0 );
        setAllItemsCount( 0 );

        setStartEnabled( true );
        setEndEnabled( true );
        setConversationEnabled( false );
        setMessageEnabled( false );
        setMessageTextEnabled( false );

        setSearchFor( "conversation" );

        setConvColSelect( true );
        setConvColChorId( true );
        setConvColConId( true );
        setConvColPartId( true );
        setConvColStatus( true );
        setConvColAction( true );
        setConvColCreated( true );
        setConvColTurnaround( false );

        setMessColSelect( true );
        setMessColMessageId( true );
        setMessColParticipantId( true );
        setMessColStatus( true );
        setMessColType( true );
        setMessColAction( true );
        setMessColCreated( true );
        setMessColTurnaround( false );

        setEngineColSeverity( true );
        setEngineColIssued( true );
        setEngineColDescription( true );
        setEngineColOrigin( true );
        setEngineColClassName( false );
        setEngineColmethodName( false );

        Date start = new Date();
        Date end = new Date();
        end.setTime( start.getTime() + ( 24 * 60 * 60 * 1000 ) );

        setStartYear( "" + ( 1900 + start.getYear() ) );
        setEndYear( "" + ( 1900 + end.getYear() ) );
        if ( ( start.getMonth() + 1 ) < 10 ) {
            setStartMonth( "0" + ( start.getMonth() + 1 ) );
        } else {
            setStartMonth( "" + ( start.getMonth() + 1 ) );
        }
        if ( ( end.getMonth() + 1 ) < 10 ) {
            setEndMonth( "0" + ( end.getMonth() + 1 ) );
        } else {
            setEndMonth( "" + ( end.getMonth() + 1 ) );
        }
        if ( start.getDate() < 10 ) {
            setStartDay( "0" + start.getDate() );

        } else {
            setStartDay( "" + start.getDate() );

        }
        if ( end.getDate() < 10 ) {
            setEndDay( "0" + end.getDate() );
        } else {
            setEndDay( "" + end.getDate() );
        }
        setStartHour( "00" );
        setEndHour( "00" );

        setStartMin( "00" );
        setEndMin( "00" );

    }

    @Override
    public void reset( ActionMapping actionMapping, HttpServletRequest request ) {

        //        LOG.trace( "path:" + arg0.getPath() );
        if ( request.getParameter( "noReset" ) != null ) {
            return;
        }
        if ( actionMapping.getPath().equals( "/ReportingForward" ) || actionMapping.getPath().equals( "/ProcessConversationReport" )
                || actionMapping.getPath().equals( "/ProcessEngineLog" ) ) {
            setStartEnabled( false );
            setEndEnabled( false );
            setConversationEnabled( false );
            setMessageEnabled( false );
            setMessageTextEnabled( false );

            setConvColSelect( false );
            setConvColConId( false );
            setConvColChorId( false );
            setConvColPartId( false );
            setConvColStatus( false );
            setConvColAction( false );
            setConvColCreated( false );
            setConvColTurnaround( false );

            setMessColSelect( false );
            setMessColMessageId( false );
            setMessColParticipantId( false );
            setMessColStatus( false );
            setMessColType( false );
            setMessColAction( false );
            setMessColCreated( false );
            setMessColTurnaround( false );

            setEngineColSeverity( false );
            setEngineColIssued( false );
            setEngineColDescription( false );
            setEngineColOrigin( false );
            setEngineColClassName( false );
            setEngineColmethodName( false );
        }
    }

    public void resetManually() {

        // LOG.trace( "reseting" );
        setStatus( null );
        setChoreographyId( null );
        setParticipantIds( new Vector() );
        setConversationId( null );
        setStartYear( null );
        setStartMonth( null );
        setStartDay( null );
        setStartHour( null );
        setStartMin( null );
        setEndYear( null );
        setEndMonth( null );
        setEndDay( null );
        setEndHour( null );
        setEndMin( null );
        setStartEnabled( true );
        setEndEnabled( true );
        setConversationEnabled( false );

        setNextActive( false );
        setLastActive( false );
        setPrevActive( false );
        setFirstActive( false );
        setStartCount( 0 );
        setEndCount( 0 );

        setAllItemsCount( 0 );
        setPageSize( 20 );
        setCommand( null );
        setSelected( new String[0] );
        setParticipantId( null );
    }

    public void setSelected( String[] key ) {

        selected = key;
    }

    public String[] getSelectedResults() {

        return selected;
    }

    public String[] getSelected() {

        return new String[0];
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

    public String getEndDay() {

        return endDay;
    }

    public void setEndDay( String endDay ) {

        this.endDay = endDay;
    }

    public String getEndHour() {

        return endHour;
    }

    public void setEndHour( String endHour ) {

        this.endHour = endHour;
    }

    public String getEndMin() {

        return endMin;
    }

    public void setEndMin( String endMin ) {

        this.endMin = endMin;
    }

    public String getEndMonth() {

        return endMonth;
    }

    public void setEndMonth( String endMonth ) {

        this.endMonth = endMonth;
    }

    public String getEndYear() {

        return endYear;
    }

    public void setEndYear( String endYear ) {

        this.endYear = endYear;
    }

    public Vector getParticipantIds() {

        return participantIds;
    }

    public void setParticipantIds( Vector participantIds ) {

        this.participantIds = participantIds;
    }

    public String getStartDay() {

        return startDay;
    }

    public void setStartDay( String startDay ) {

        this.startDay = startDay;
    }

    public String getStartHour() {

        return startHour;
    }

    public void setStartHour( String startHour ) {

        this.startHour = startHour;
    }

    public String getStartMin() {

        return startMin;
    }

    public void setStartMin( String startMin ) {

        this.startMin = startMin;
    }

    public String getStartMonth() {

        return startMonth;
    }

    public void setStartMonth( String startMonth ) {

        this.startMonth = startMonth;
    }

    public String getStartYear() {

        return startYear;
    }

    public void setStartYear( String startYear ) {

        this.startYear = startYear;
    }

    public String getStatus() {

        return status;
    }

    public void setStatus( String status ) {

        this.status = status;
    }

    public boolean isConversationEnabled() {

        return conversationEnabled;
    }

    public void setConversationEnabled( boolean conversationEnabled ) {

        this.conversationEnabled = conversationEnabled;
    }

    public boolean isEndEnabled() {

        return endEnabled;
    }

    public void setEndEnabled( boolean endEnabled ) {

        this.endEnabled = endEnabled;
    }

    public boolean isStartEnabled() {

        return startEnabled;
    }

    public void setStartEnabled( boolean startEnabled ) {

        this.startEnabled = startEnabled;
    }

    public int getEndCount() {

        return endCount;
    }

    public void setEndCount( int endCount ) {

        this.endCount = endCount;
    }

    public boolean isFirstActive() {

        return firstActive;
    }

    public void setFirstActive( boolean firstActive ) {

        this.firstActive = firstActive;
    }

    public boolean isLastActive() {

        return lastActive;
    }

    public void setLastActive( boolean lastActive ) {

        this.lastActive = lastActive;
    }

    public boolean isNextActive() {

        return nextActive;
    }

    public void setNextActive( boolean nextActive ) {

        this.nextActive = nextActive;
    }

    public boolean isPrevActive() {

        return prevActive;
    }

    public void setPrevActive( boolean prevActive ) {

        this.prevActive = prevActive;
    }

    public int getStartCount() {

        return startCount;
    }

    public void setStartCount( int startCount ) {

        this.startCount = startCount;
    }

    public int getPageSize() {

        return pageSize;
    }

    public void setPageSize( int pageSize ) {

        this.pageSize = pageSize;
    }

    public int getAllItemsCount() {

        return allItemsCount;
    }

    public void setAllItemsCount( int allItemsCount ) {

        this.allItemsCount = allItemsCount;
    }

    public String getCommand() {

        return command;
    }

    public void setCommand( String action ) {

        this.command = action;
    }

    public String getParticipantId() {

        return participantId;
    }

    public void setParticipantId( String participantId ) {

        this.participantId = participantId;
    }

    public Vector getChoreographyIds() {

        return choreographyIds;
    }

    public void setChoreographyIds( Vector choreographyIds ) {

        this.choreographyIds = choreographyIds;
    }

    public boolean isMessageEnabled() {

        return messageEnabled;
    }

    public void setMessageEnabled( boolean messageEnabled ) {

        this.messageEnabled = messageEnabled;
    }

    public String getMessageId() {

        return messageId;
    }

    public void setMessageId( String messageId ) {

        this.messageId = messageId;
    }

    public String getSearchFor() {

        return searchFor;
    }

    public void setSearchFor( String searchFor ) {

        this.searchFor = searchFor;
    }

    public String getOrigin() {

        return origin;
    }

    public void setOrigin( String origin ) {

        this.origin = origin;
    }

    public String getSeverity() {

        return severity;
    }

    public void setSeverity( String severity ) {

        this.severity = severity;
    }

    public Vector getOriginIds() {

        return originIds;
    }

    public void setOriginIds( Vector originIds ) {

        this.originIds = originIds;
    }

    public String getMessageText() {

        return messageText;
    }

    public void setMessageText( String messageText ) {

        this.messageText = messageText;
    }

    public boolean isMessageTextEnabled() {

        return messageTextEnabled;
    }

    public void setMessageTextEnabled( boolean messageTextEnabled ) {

        this.messageTextEnabled = messageTextEnabled;
    }

    public boolean isConvColSelect() {

        return convColSelect;
    }

    public void setConvColSelect( boolean convColSelect ) {

        this.convColSelect = convColSelect;
    }

    public boolean isConvColAction() {

        return convColAction;
    }

    public void setConvColAction( boolean convColAction ) {

        this.convColAction = convColAction;
    }

    public boolean isConvColConId() {

        return convColConId;
    }

    public void setConvColConId( boolean convColConId ) {

        this.convColConId = convColConId;
    }

    public boolean isConvColCreated() {

        return convColCreated;
    }

    public void setConvColCreated( boolean convColCreated ) {

        this.convColCreated = convColCreated;
    }

    public boolean isConvColPartId() {

        return convColPartId;
    }

    public void setConvColPartId( boolean convColPartId ) {

        this.convColPartId = convColPartId;
    }

    public boolean isConvColStatus() {

        return convColStatus;
    }

    public void setConvColStatus( boolean convColStatus ) {

        this.convColStatus = convColStatus;
    }

    public boolean isConvColTurnaround() {

        return convColTurnaround;
    }

    public void setConvColTurnaround( boolean convColTurnaround ) {

        this.convColTurnaround = convColTurnaround;
    }

    public boolean isConvColChorId() {

        return convColChorId;
    }

    public void setConvColChorId( boolean convColChorId ) {

        this.convColChorId = convColChorId;
    }

    public boolean isMessColAction() {

        return messColAction;
    }

    public void setMessColAction( boolean messColAction ) {

        this.messColAction = messColAction;
    }

    public boolean isMessColCreated() {

        return messColCreated;
    }

    public void setMessColCreated( boolean messColCreated ) {

        this.messColCreated = messColCreated;
    }

    public boolean isMessColMessageId() {

        return messColMessageId;
    }

    public void setMessColMessageId( boolean messColMessageId ) {

        this.messColMessageId = messColMessageId;
    }

    public boolean isMessColParticipantId() {

        return messColParticipantId;
    }

    public void setMessColParticipantId( boolean messColParticipantId ) {

        this.messColParticipantId = messColParticipantId;
    }

    public boolean isMessColSelect() {

        return messColSelect;
    }

    public void setMessColSelect( boolean messColSelect ) {

        this.messColSelect = messColSelect;
    }

    public boolean isMessColStatus() {

        return messColStatus;
    }

    public void setMessColStatus( boolean messColStatus ) {

        this.messColStatus = messColStatus;
    }

    public boolean isMessColTurnaround() {

        return messColTurnaround;
    }

    public void setMessColTurnaround( boolean messColTurnaround ) {

        this.messColTurnaround = messColTurnaround;
    }

    public boolean isMessColType() {

        return messColType;
    }

    public void setMessColType( boolean messColType ) {

        this.messColType = messColType;
    }

    public boolean isEngineColClassName() {

        return engineColClassName;
    }

    public void setEngineColClassName( boolean engineColClassName ) {

        this.engineColClassName = engineColClassName;
    }

    public boolean isEngineColDescription() {

        return engineColDescription;
    }

    public void setEngineColDescription( boolean engineColDescription ) {

        this.engineColDescription = engineColDescription;
    }

    public boolean isEngineColIssued() {

        return engineColIssued;
    }

    public void setEngineColIssued( boolean engineColIssued ) {

        this.engineColIssued = engineColIssued;
    }

    public boolean isEngineColmethodName() {

        return engineColmethodName;
    }

    public void setEngineColmethodName( boolean engineColmethodName ) {

        this.engineColmethodName = engineColmethodName;
    }

    public boolean isEngineColOrigin() {

        return engineColOrigin;
    }

    public void setEngineColOrigin( boolean engineColOrigin ) {

        this.engineColOrigin = engineColOrigin;
    }

    public boolean isEngineColSeverity() {

        return engineColSeverity;
    }

    public void setEngineColSeverity( boolean engineColSeverity ) {

        this.engineColSeverity = engineColSeverity;
    }

    public String getTimezone() {

        return timezone;
    }

    public void setTimezone( String timezone ) {

        this.timezone = timezone;
    }
}
