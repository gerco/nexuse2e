<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-html-el" prefix="html-el" %>
<%@ taglib uri="/tags/struts-nested" prefix="nested" %>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/nexus" prefix="nexus" %>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %> 

<% /*<nexus:helpBar helpDoc="documentation/Conversation_Reporting.htm"/> */ %>

    <center>
        <table class="NEXUS_TABLE" width="100%">
            <tr>
            <td><nexus:crumbs styleClass="NEXUSScreenPathLink"/></td>
            
            </tr>

            <tr>
                <td colspan="2" class="NEXUSScreenName">Messages for Conversation: <bean:write name="reportConversationEntryForm" property="conversationId"/></td>
            </tr>
        </table>

        <table class="NEXUS_TABLE" width="100%">
            <tr>
                <td class="NEXUSName">Conversation Date Created</td>
                <td class="NEXUSValue"><bean:write name="reportConversationEntryForm" property="createdDate"/></td>
            </tr>

            <tr>
                <td class="NEXUSName">Conversation Date Modified</td>
                <td class="NEXUSValue"><bean:write name="reportConversationEntryForm" property="modifiedDate"/></td>
            </tr>

            <tr>
                <td class="NEXUSName">Conversation Date Ended</td>
                <td class="NEXUSValue"><bean:write name="reportConversationEntryForm" property="endDate"/></td>
            </tr>
            
            <tr>
                <td class="NEXUSName">Conversation Turnaround Time</td>
                <td class="NEXUSValue"><bean:write name="reportConversationEntryForm" property="turnaroundTime"/></td>
            </tr>

            <tr>
                <td class="NEXUSName">Participant ID</td>
                <td class="NEXUSValue"><bean:write name="reportConversationEntryForm" property="participantId"/></td>
            </tr>

            <tr>
                <td class="NEXUSName">Status</td>
                <td class="NEXUSValue"><bean:write name="reportConversationEntryForm" property="status"/></td>
            </tr>

            <tr>
                <td class="NEXUSName">Current Action</td>
                <td class="NEXUSValue"><bean:write name="reportConversationEntryForm" property="action"/></td>
            </tr>
        </table>

        <table class="NEXUS_TABLE" width="100%">
            <tr>
                <th class="NEXUSSection">Message ID</th>
                <th class="NEXUSSection">Status</th>
                <th class="NEXUSSection">Message Type</th>
                <th class="NEXUSSection">Action</th>
                <th class="NEXUSSection">Direction</th>
                <th class="NEXUSSection">Created Date</th>
                <th class="NEXUSSection">End Date</th>
                <th class="NEXUSSection">Turnaround Time</th>
            </tr>
      <logic:iterate indexId="counter" id="message" name="collection">
            <tr>
                <td class="NEXUSValue"><nexus:link styleClass="NexusLink" href="MessageView.do?mId=${message.messageId}&convId=${message.conversationId}&chorId=${message.choreographyId}&partnerId=${message.participantId}"><bean:write name="message" property="messageId"/></nexus:link></td>
                <td class="NEXUSValue"><bean:write name="message" property="status"/></td>
                <td class="NEXUSValue"><bean:write name="message" property="type"/></td>
                <td class="NEXUSValue"><bean:write name="message" property="action"/></td>
                <td class="NEXUSValue"><bean:write name="message" property="direction"/></td>
                <td class="NEXUSValue"><bean:write name="message" property="createdDate"/></td>
                <td class="NEXUSValue"><bean:write name="message" property="endDate"/></td>
                <td class="NEXUSValue"><bean:write name="message" property="turnaroundTime"/></td>
            </tr>
      </logic:iterate>
        </table>
        <table width="100%">
      		<tr>
                <td class="BUTTON_RIGHT">
                <nobr><nexus:link href="ProcessConversationReport.do?noReset=true" styleClass="NexusHeaderLink"><img src="images/icons/resultset_previous.png" name="resultsButton" class="button"/>Back</nexus:link></nobr></td>
            </tr>
        </table>
    </center>