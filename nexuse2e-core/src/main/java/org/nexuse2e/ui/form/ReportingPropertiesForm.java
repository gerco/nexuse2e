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

import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * @author guido.esch
 */
public class ReportingPropertiesForm extends ActionForm {

    /**
     * 
     */
    private static final long                       serialVersionUID                  = 3017346285374318341L;
    private String                                  status                            = null;
    private String            backendStatus             = null;
    private String                                  choreographyId                    = null;
    private List<Integer>                           originIds                         = null;
    private String                                  conversationId                    = null;
    private String                                  startYear                         = null;
    private String                                  startMonth                        = null;
    private String                                  startDay                          = null;
    private String                                  startHour                         = null;
    private String                                  startMin                          = null;
    private String                                  endYear                           = null;
    private String                                  endMonth                          = null;
    private String                                  endDay                            = null;
    private String                                  endHour                           = null;
    private String                                  endMin                            = null;
    private boolean                                 startEnabled                      = true;
    private boolean                                 startEnabledCalled                = false;
    private boolean                                 endEnabled                        = true;
    private boolean                                 endEnabledCalled                  = false;
    private boolean                                 applyProperties                   = false;
    private boolean                                 conversationEnabled               = false;
    private boolean                                 conversationEnabledCalled         = false;
    private boolean                                 messageEnabled                    = false;
    private boolean                                 messageEnabledCalled              = false;
    private boolean                                 messageTextEnabled                = false;
    private boolean                                 messageTextEnabledCalled          = false;
    private String                                  searchFor                         = null;
    private String                                  messageId                         = null;
    private boolean                                 nextActive                        = false;
    private boolean                                 lastActive                        = false;
    private boolean                                 firstActive                       = false;
    private boolean                                 prevActive                        = false;
    private int                                     startCount                        = 0;
    private int                                     endCount                          = 0;
    private int                                     allItemsCount                     = 0;
    private int                                     pageSize                          = 20;
    private String                                  command                           = null;
    private String                                  type                              = null;
    private String                                  participantId                     = null;
    private String                                  origin                            = null;
    private String                                  severity                          = null;
    private String                                  messageText                       = null;
    private boolean                                 outbound                          = false;
    private String                                  timezone                          = null;
    private List<String>                            participantIds                    = null;
    private List<String>                            choreographyIds                   = null;
    private String[]                                selected                          = new String[0];


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

        setConversationEnabled( false );
        setMessageEnabled( false );
        setMessageTextEnabled( false );

        setSearchFor( "conversation" );

        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        end.add( Calendar.DATE, 1 );

        setStartYear( Integer.toString( start.get( Calendar.YEAR ) ) );
        setEndYear( Integer.toString( end.get( Calendar.YEAR ) ) );
        int month = start.get( Calendar.MONTH ) + 1;
        if (month < 10) {
            setStartMonth( "0" + month );
        } else {
            setStartMonth( Integer.toString( month ) );
        }
        month = end.get( Calendar.MONTH ) + 1;
        if (month < 10) {
            setEndMonth( "0" + month );
        } else {
            setEndMonth( Integer.toString( month ) );
        }
        int date = start.get( Calendar.DATE );
        if (date < 10) {
            setStartDay( "0" + date );

        } else {
            setStartDay( Integer.toString( date ) );

        }
        date = end.get( Calendar.DATE );
        if (date < 10) {
            setEndDay( "0" + date );

        } else {
            setEndDay( Integer.toString( date ) );

        }
        setStartHour( "00" );
        setEndHour( "00" );

        setStartMin( "00" );
        setEndMin( "00" );
    }
    

    public ActionErrors validate( ActionMapping mapping, HttpServletRequest request ) {
        if (applyProperties) {
            if (startEnabled && !startEnabledCalled) {
                startEnabled = false;
            }
            if (endEnabled && !endEnabledCalled) {
                endEnabled = false;
            }
            if (conversationEnabled && !conversationEnabledCalled) {
                conversationEnabled = false;
            }
            if (messageEnabled && !messageEnabledCalled) {
                messageEnabled = false;
            }
            if (messageTextEnabled && !messageTextEnabledCalled) {
                messageTextEnabled = false;
            }
        }

        reset();
        return null;
    }
    
    @Override
    public void reset( ActionMapping mapping, HttpServletRequest request ) {
        reset();
    }
    
    public void reset() {
        startEnabledCalled = false;
        endEnabledCalled = false;
        conversationEnabledCalled = false;
        messageEnabledCalled = false;
        messageTextEnabledCalled = false;
        applyProperties = false;
    }
    
    
    public boolean isApplyProperties() {
        return applyProperties;
    }


    public void setApplyProperties(boolean applyProperties) {
        this.applyProperties = applyProperties;
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

    public String getBackendStatus() {

        return backendStatus;
    }

    public void setBackendStatus(String backendStatus) {

        this.backendStatus = backendStatus;
    }

    public boolean isConversationEnabled() {

        return conversationEnabled;
    }

    public void setConversationEnabled( boolean conversationEnabled ) {

        this.conversationEnabled = conversationEnabled;
        conversationEnabledCalled = true;
    }

    public boolean isEndEnabled() {

        return endEnabled;
    }

    public void setEndEnabled( boolean endEnabled ) {

        this.endEnabled = endEnabled;
        endEnabledCalled = true;
    }

    public boolean isStartEnabled() {

        return startEnabled;
    }

    public void setStartEnabled( boolean startEnabled ) {

        this.startEnabled = startEnabled;
        startEnabledCalled = true;
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

    public boolean isMessageEnabled() {

        return messageEnabled;
    }

    public void setMessageEnabled( boolean messageEnabled ) {

        this.messageEnabled = messageEnabled;
        messageEnabledCalled = true;
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

    public List<Integer> getOriginIds() {

        return originIds;
    }

    public void setOriginIds( List<Integer> originIds ) {

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
        messageTextEnabledCalled = true;
    }

    public boolean isOutbound() {

        return outbound;
    }

    public void setOutbound( boolean outbound ) {

        this.outbound = outbound;
    }

    public String getTimezone() {

        return timezone;
    }

    public void setTimezone( String timezone ) {

        this.timezone = timezone;
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


    public String[] getSelected() {

        return selected;
    }


    public void setSelected(String[] selected) {

        this.selected = selected;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
