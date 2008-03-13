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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.Constants.ParameterType;

/**
 * 
 *
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public class ReportingSettingsForm extends ActionForm {

    private static final long serialVersionUID = 1L;

    
    public static String                            PARAM_NAME_ENGCOL_SEVERITY        = "engineColSeverity";
    public static String                            PARAM_NAME_ENGCOL_ISSUEDDATE      = "engineColIssueDate";
    public static String                            PARAM_NAME_ENGCOL_DESCRIPTION     = "engineColDescription";
    public static String                            PARAM_NAME_ENGCOL_ORIGIN          = "engineColOrigin";
    public static String                            PARAM_NAME_ENGCOL_CLASSNAME       = "engineColClassName";
    public static String                            PARAM_NAME_ENGCOL_METHODNAME      = "engineColMethodName";

    public static String                            PARAM_NAME_MSGCOL_SELECT          = "msgColSelect";
    public static String                            PARAM_NAME_MSGCOL_MSGID           = "msgColMsgId";
    public static String                            PARAM_NAME_MSGCOL_PARTICIPANTID   = "msgColParticipantId";
    public static String                            PARAM_NAME_MSGCOL_STATUS          = "msgColStatus";
    public static String                            PARAM_NAME_MSGCOL_TYPE            = "msgColType";
    public static String                            PARAM_NAME_MSGCOL_ACTION          = "msgColAction";
    public static String                            PARAM_NAME_MSGCOL_CREATED         = "msgColCreated";
    public static String                            PARAM_NAME_MSGCOL_TURNAROUND      = "msgColTurnaround";

    public static String                            PARAM_NAME_CONVCOL_SELECT         = "convColSelect";
    public static String                            PARAM_NAME_CONVCOL_CONVID         = "convColConvId";
    public static String                            PARAM_NAME_CONVCOL_PARTICIPANTID  = "convColParticipantId";
    public static String                            PARAM_NAME_CONVCOL_CHOREOGRAPHYID = "convColChoreographyId";
    public static String                            PARAM_NAME_CONVCOL_ACTION         = "convColAction";
    public static String                            PARAM_NAME_CONVCOL_CREATED        = "convColCreated";
    public static String                            PARAM_NAME_CONVCOL_TURNAROUND     = "convColTurnaround";
    public static String                            PARAM_NAME_CONVCOL_STATUS         = "convColStatus";

    public static String                            PARAM_NAME_TIMEZONE               = "timezone";
    public static String                            PARAM_NAME_ROWCOUNT               = "rowcount";

    private Map<String, ParameterDescriptor> parameterMap                      = null;

    
    private boolean                                 convColSelect                     = false;
    private boolean                                 convColChorId                     = false;
    private boolean                                 convColConId                      = false;
    private boolean                                 convColPartId                     = false;
    private boolean                                 convColStatus                     = false;
    private boolean                                 convColAction                     = false;
    private boolean                                 convColCreated                    = false;
    private boolean                                 convColTurnaround                 = false;

    private boolean                                 messColSelect                     = false;
    private boolean                                 messColMessageId                  = false;
    private boolean                                 messColParticipantId              = false;
    private boolean                                 messColStatus                     = false;
    private boolean                                 messColType                       = false;
    private boolean                                 messColAction                     = false;
    private boolean                                 messColCreated                    = false;
    private boolean                                 messColTurnaround                 = false;

    private boolean                                 engineColSeverity                 = false;
    private boolean                                 engineColIssued                   = false;
    private boolean                                 engineColDescription              = false;
    private boolean                                 engineColOrigin                   = false;
    private boolean                                 engineColClassName                = false;
    private boolean                                 engineColmethodName               = false;
    
    private String                                  action                            = null;
    private String                                  command                           = null;
    private String                                  timezone                          = null;
    private int                                     startCount                        = 0;
    private int                                     endCount                          = 0;
    private List<String>                            participantIds                    = null;
    private List<String>                            choreographyIds                   = null;
    private int                                     pageSize                          = 20;

    
    /**
     * @return the parameterMap
     */
    public Map<String, ParameterDescriptor> getParameterMap() {

        if ( parameterMap == null ) {
            parameterMap = new LinkedHashMap<String, ParameterDescriptor>();
            parameterMap.put( PARAM_NAME_ENGCOL_SEVERITY, new ParameterDescriptor( ParameterType.BOOLEAN,
                    "Log Column Severity", "Should Engine Log Column Severity be displayed", true ) );
            parameterMap.put( PARAM_NAME_ENGCOL_CLASSNAME, new ParameterDescriptor( ParameterType.BOOLEAN,
                    "Log Column Classname", "Should Engine Log Column Class Name be displayed", false ) );
            parameterMap.put( PARAM_NAME_ENGCOL_ISSUEDDATE, new ParameterDescriptor( ParameterType.BOOLEAN,
                    "Log Column Issue Date", "Should Engine Log Column Issues Date be displayed", true ) );
            parameterMap.put( PARAM_NAME_ENGCOL_DESCRIPTION, new ParameterDescriptor( ParameterType.BOOLEAN,
                    "Log Column Description", "Should Engine Log Column Description be displayed", true ) );
            parameterMap.put( PARAM_NAME_ENGCOL_ORIGIN, new ParameterDescriptor( ParameterType.BOOLEAN,
                    "Log Column Origin", "Should Engine Log Origin be displayed", false ) );
            parameterMap.put( PARAM_NAME_ENGCOL_METHODNAME, new ParameterDescriptor( ParameterType.BOOLEAN,
                    "Log Column Methodname", "Should Engine Log Method Name be displayed", false ) );

            parameterMap.put( PARAM_NAME_MSGCOL_SELECT, new ParameterDescriptor( ParameterType.BOOLEAN,
                    "Log Column select", "Should Message Log Method Name be displayed", false ) );
            parameterMap.put( PARAM_NAME_MSGCOL_MSGID, new ParameterDescriptor( ParameterType.BOOLEAN,
                    "Log Column Message Id", "Should Message Log MessageId be displayed", true ) );
            parameterMap.put( PARAM_NAME_MSGCOL_PARTICIPANTID, new ParameterDescriptor( ParameterType.BOOLEAN,
                    "Log Column Participant Id", "Should Message ParticipantId Name be displayed", true ) );
            parameterMap.put( PARAM_NAME_MSGCOL_STATUS, new ParameterDescriptor( ParameterType.BOOLEAN,
                    "Log Column Status", "Should Message Log Status be displayed", true ) );
            parameterMap.put( PARAM_NAME_MSGCOL_TYPE, new ParameterDescriptor( ParameterType.BOOLEAN,
                    "Log Column Type", "Should Message Log Type be displayed", true ) );
            parameterMap.put( PARAM_NAME_MSGCOL_ACTION, new ParameterDescriptor( ParameterType.BOOLEAN,
                    "Log Column Action", "Should Message Log Action be displayed", true ) );
            parameterMap.put( PARAM_NAME_MSGCOL_CREATED, new ParameterDescriptor( ParameterType.BOOLEAN,
                    "Log Column Created", "Should Message Log Created Date be displayed", true ) );
            parameterMap.put( PARAM_NAME_MSGCOL_TURNAROUND, new ParameterDescriptor( ParameterType.BOOLEAN,
                    "Log Column Turnaround", "Should Message Log Turnaround Time be displayed", false ) );

            parameterMap.put( PARAM_NAME_CONVCOL_SELECT, new ParameterDescriptor( ParameterType.BOOLEAN,
                    "Log Column Select", "Should Conversation Log Select Time be displayed", false ) );
            parameterMap.put( PARAM_NAME_CONVCOL_CONVID, new ParameterDescriptor( ParameterType.BOOLEAN,
                    "Log Column ConversationId", "Should Conversation Log Conversation Id be displayed", true ) );
            parameterMap.put( PARAM_NAME_CONVCOL_PARTICIPANTID, new ParameterDescriptor( ParameterType.BOOLEAN,
                    "Log Column ParticipantId", "Should Conversation Log Participant Id be displayed", true ) );
            parameterMap.put( PARAM_NAME_CONVCOL_CHOREOGRAPHYID, new ParameterDescriptor( ParameterType.BOOLEAN,
                    "Log Column ChoreographyId", "Should Conversation Log Choreography Id be displayed", true ) );
            parameterMap.put( PARAM_NAME_CONVCOL_ACTION, new ParameterDescriptor( ParameterType.BOOLEAN,
                    "Log Column ActionId", "Should Conversation Log Action Id be displayed", true ) );
            parameterMap.put( PARAM_NAME_CONVCOL_CREATED, new ParameterDescriptor( ParameterType.BOOLEAN,
                    "Log Column Created", "Should Conversation Log Created Date be displayed", true ) );
            parameterMap.put( PARAM_NAME_CONVCOL_TURNAROUND, new ParameterDescriptor( ParameterType.BOOLEAN,
                    "Log Column Turnaround", "Should Conversation Log Turnaround Time be displayed", false ) );
            parameterMap.put( PARAM_NAME_CONVCOL_STATUS, new ParameterDescriptor( ParameterType.BOOLEAN,
                    "Log Column Status", "Should Conversation Log Status be displayed", true ) );

            parameterMap.put( PARAM_NAME_TIMEZONE, new ParameterDescriptor( ParameterType.STRING, "Log Timezone", "",
                    "" ) );
            parameterMap.put( PARAM_NAME_ROWCOUNT, new ParameterDescriptor( ParameterType.STRING, "Log Row count",
                    "No of rows displayed on one reporting page", "20" ) );
        }
        return parameterMap;
    }

    /**
     * @param parameterMap the parameterMap to set
     */
    public void setParameterMap( Map<String, ParameterDescriptor> parameterMap ) {

        this.parameterMap = parameterMap;
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

    /**
     * @return the action
     */
    public String getAction() {

        return action;
    }

    /**
     * @param action the action to set
     */
    public void setAction( String action ) {

        this.action = action;
    }

    public String getCommand() {

        return command;
    }

    public void setCommand( String action ) {

        this.command = action;
    }

    public int getStartCount() {

        return startCount;
    }

    public void setStartCount( int startCount ) {

        this.startCount = startCount;
    }

    public int getEndCount() {

        return endCount;
    }

    public void setEndCount( int endCount ) {

        this.endCount = endCount;
    }

    public List<String> getParticipantIds() {

        return participantIds;
    }

    public void setParticipantIds( List<String> participantIds ) {

        this.participantIds = participantIds;
    }

    public List<String> getChoreographyIds() {

        return choreographyIds;
    }

    public void setChoreographyIds( List<String> choreographyIds ) {

        this.choreographyIds = choreographyIds;
    }

    public int getPageSize() {

        return pageSize;
    }

    public void setPageSize( int pageSize ) {

        this.pageSize = pageSize;
    }

    public void reset( ActionMapping mappping, HttpServletRequest request ) {
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
