<%--

     NEXUSe2e Business Messaging Open Source
     Copyright 2000-2009, Tamgroup and X-ioma GmbH

     This is free software; you can redistribute it and/or modify it
     under the terms of the GNU Lesser General Public License as
     published by the Free Software Foundation version 2.1 of
     the License.

     This software is distributed in the hope that it will be useful,
     but WITHOUT ANY WARRANTY; without even the implied warranty of
     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
     Lesser General Public License for more details.

     You should have received a copy of the GNU Lesser General Public
     License along with this software; if not, write to the Free
     Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
     02110-1301 USA, or see the FSF site: http://www.fsf.org.

--%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-html-el" prefix="html-el"%>
<%@ taglib uri="/tags/struts-nested" prefix="nested"%>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/nexus" prefix="nexus"%>

<% /*<nexus:helpBar helpDoc="documentation/Message_Reporting.htm" /> */ %>

<center>
<table class="NEXUS_TABLE" width="100%">
	<tr>
		<td><nexus:crumbs styleClass="NEXUSScreenPathLink" /></td>
	</tr>
	<tr>
		<td colspan="2" class="NEXUSScreenName">Message Detail</td>
	</tr>
</table>
<table class="NEXUS_TABLE" width="100%">
	<tr>
		<td class="NEXUSName">Choreography ID</td>
		<td class="NEXUSValue"><bean:write name="reportMessageEntryForm"
			property="choreographyId" /></td>
	</tr>
	<tr>
		<td class="NEXUSName">Participant ID</td>
		<td class="NEXUSValue"><bean:write name="reportMessageEntryForm"
			property="participantId" /></td>
	</tr>
</table>

<table class="NEXUS_TABLE" width="100%">
	<tr>
		<td class="NEXUSName">Message ID</td>
		<td class="NEXUSValue"><bean:write name="reportMessageEntryForm"
			property="messageId" /></td>
	</tr>
	<tr>
		<td class="NEXUSName">Conversation ID</td>
		<td class="NEXUSValue"><nexus:link styleClass="NexusLink"
			href="ConversationView.do?convId=${reportMessageEntryForm.nxConversationId}">
			<bean:write name="reportMessageEntryForm" property="conversationId" />
		</nexus:link></td>
	</tr>

	<tr>
		<td class="NEXUSName">Message Type</td>
		<td class="NEXUSValue"><bean:write name="reportMessageEntryForm"
			property="type" /></td>
	</tr>
	<tr>
		<td class="NEXUSName">Direction</td>
		<td class="NEXUSValue"><bean:write name="reportMessageEntryForm"
			property="direction" /></td>
	</tr>
	<tr>
		<td class="NEXUSName">Referenced Message ID</td>
		<td class="NEXUSValue"><bean:write name="reportMessageEntryForm"
			property="referencedId" /></td>
	</tr>
	<tr>
		<td class="NEXUSName">Action</td>
		<td class="NEXUSValue"><bean:write name="reportMessageEntryForm"
			property="action" /></td>
	</tr>
	<tr>
		<td class="NEXUSName">Status</td>
		<td class="NEXUSValue"><bean:write name="reportMessageEntryForm"
			property="status" /></td>
	</tr>
	<tr>
		<td class="NEXUSName">Date Created</td>
		<td class="NEXUSValue"><bean:write name="reportMessageEntryForm"
			property="createdDate" /></td>
	</tr>
	<tr>
		<td class="NEXUSName">Last Modified</td>
		<td class="NEXUSValue"><bean:write name="reportMessageEntryForm"
			property="modifiedDate" /></td>
	</tr>
	<tr>
		<td class="NEXUSName">Date Ended</td>
		<td class="NEXUSValue"><bean:write name="reportMessageEntryForm"
			property="endDate" /></td>
	</tr>
	<tr>
		<td class="NEXUSName">Turnaround Time</td>
		<td class="NEXUSValue"><bean:write name="reportMessageEntryForm"
			property="turnaroundTime" /></td>
	</tr>
	<tr>
		<td class="NEXUSName">Expiration Date</td>
		<td class="NEXUSValue"><bean:write name="reportMessageEntryForm"
			property="expireDate" /></td>
	</tr>
	<tr>
		<td class="NEXUSName">Retries</td>
		<td class="NEXUSValue"><bean:write name="reportMessageEntryForm"
			property="retries" /></td>
	</tr>
	<tr>
		<td class="NEXUSName">Protocol / Version</td>
		<td class="NEXUSValue"><bean:write name="reportMessageEntryForm"
			property="protocol" /></td>
	</tr>
</table>
<table class="NEXUS_TABLE" width="100%">
	<tr>
		<td class="NEXUSSection">Component</td>
		<td class="NEXUSSection">MIME Type</td>
		<td class="NEXUSSection">Action</td>
	</tr>

	<tr>
		<td class="NEXUSName">Header</td>
		<td class="NEXUSValue">text/xml</td>
		<td class="NEXUSValue"><a class="NexusLink"
			href="DataSaveAs.do?type=content&choreographyId=${reportMessageEntryForm.choreographyId}&participantId=${reportMessageEntryForm.participantId}&conversationId=${reportMessageEntryForm.conversationId}&messageId=${reportMessageEntryForm.messageId}"
			target="_blank"> View</a></td>
	</tr>
	<logic:iterate indexId="counter" id="payloads" name="collection">
		<tr>
			<td class="NEXUSName">Payload <bean:write name="counter" /></td>
			<td class="NEXUSValue"><bean:write name="payloads" /></td>
			<td class="NEXUSValue"><a class="NexusLink"
				href="DataSaveAs.do?type=content&choreographyId=${reportMessageEntryForm.choreographyId}&participantId=${reportMessageEntryForm.participantId}&conversationId=${reportMessageEntryForm.conversationId}&messageId=${reportMessageEntryForm.messageId}&no=${counter}"
				target="_blank"> View</a></td>
		</tr>
	</logic:iterate>
</table>

<table class="NEXUS_BUTTON_TABLE" width="100%">
	<tr>

	<logic:equal name="reportingPropertiesForm" property="searchFor" value="conversation">
		<td class="BUTTON_LEFT"><nobr><nexus:link
			href="ConversationView.do?noReset&choreographyId=${reportMessageEntryForm.choreographyId}&convId=${reportMessageEntryForm.nxConversationId}"
			styleClass="NexusHeaderLink">
			<img src="images/icons/resultset_previous.png" border="0" alt="" class="button">Back</nexus:link>
			<nexus:link
				href="MessageView.do?mId=${reportMessageEntryForm.messageId}&convId=${reportMessageEntryForm.conversationId}&chorId=${reportMessageEntryForm.choreographyId}&partnerId=${reportMessageEntryForm.participantId}"
				styleClass="NexusHeaderLink">
				<img src="images/icons/arrow_refresh.png" name="resultsButton"
				class="button" />Refresh</nexus:link>
			</nobr>
		</td>
	</logic:equal>
	<logic:equal name="reportingPropertiesForm" property="searchFor" value="message">
		<td class="BUTTON_RIGHT"><nobr><nexus:link
			href="ProcessConversationReport.do?noReset=true"
			styleClass="NexusHeaderLink">
			<img src="images/icons/resultset_previous.png" border="0" alt="" class="button">Back</nexus:link></nobr></td>
	</logic:equal>
		<td class="BUTTON_RIGHT"><nobr><nexus:link
			href="ModifyMessage.do?noReset&refresh&type=transaction&command=requeue&participantId=${reportMessageEntryForm.participantId}&choreographyId=${reportMessageEntryForm.choreographyId}&conversationId=${reportMessageEntryForm.conversationId}&messageId=${reportMessageEntryForm.messageId}&outbound=${reportMessageEntryForm.outbound}"
			styleClass="NexusHeaderLink">
			<img src="images/icons/arrow_redo.png" border="0" alt="" class="button">Re-Queue</nexus:link></nobr></td>
		<td class="BUTTON_RIGHT"><nobr><nexus:link
			href="ModifyMessage.do?noReset&refresh&type=transaction&command=stop&participantId=${reportMessageEntryForm.participantId}&choreographyId=${reportMessageEntryForm.choreographyId}&conversationId=${reportMessageEntryForm.conversationId}&messageId=${reportMessageEntryForm.messageId}&outbound=${reportMessageEntryForm.outbound}"
			styleClass="NexusHeaderLink">
			<img src="images/icons/stop.png" onclick="" border="0" alt="" class="button">Stop</nexus:link></nobr></td>
	</tr>
</table>

<logic:notEmpty name="collection_2">
	<table class="NEXUS_TABLE" width="100%">
		<tr>
			<th class="NEXUSSection">Severity</th>
			<th class="NEXUSSection">Issued Date</th>
			<th class="NEXUSSection">Description</th>
			<th class="NEXUSSection">Origin</th>
			<th class="NEXUSSection">Class Name</th>
			<th class="NEXUSSection">Method Name</th>
		</tr>

		<logic:iterate indexId="counter" id="conv" name="collection_2">
			<tr>
				<td class="NEXUSValue"><bean:write name="conv"
					property="severity" /></td>
				<td class="NEXUSValue"><bean:write name="conv"
					property="issuedDate" /></td>
				<td class="NEXUSValue"><bean:write name="conv"
					property="description" /></td>
				<td class="NEXUSValue"></td>
				<td class="NEXUSValue"><bean:write name="conv"
					property="className" /></td>
				<td class="NEXUSValue"><bean:write name="conv"
					property="methodName" /></td>
			</tr>
		</logic:iterate>
	</table>
</logic:notEmpty>


</center>
