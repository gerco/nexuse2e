<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-html-el" prefix="html-el"%>
<%@ taglib uri="/tags/struts-nested" prefix="nested"%>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/nexus" prefix="nexus"%>

<nexus:helpBar helpDoc="documentation/Message_Reporting.htm" />

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
		<td class="NEXUSValue"><nexus:link
			href="ConversationView.do?convId=${reportMessageEntryForm.conversationId}&choreographyId=${reportMessageEntryForm.choreographyId}&partnerId=${reportMessageEntryForm.participantId}">
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
		<td class="NEXUSValue"><a
			href="DataSaveAs?type=content&chorId=${reportMessageEntryForm.choreographyId}&partnerId=${reportMessageEntryForm.participantId}&convId=${reportMessageEntryForm.conversationId}&mId=${reportMessageEntryForm.messageId}"
			target="_blank"> View</a></td>
	</tr>
	<logic:iterate indexId="counter" id="payloads" name="collection">
		<tr>
			<td class="NEXUSName">Payload <bean:write name="counter" /></td>
			<td class="NEXUSValue"><bean:write name="payloads" /></td>
			<td class="NEXUSValue"><a
				href="DataSaveAs?type=content&chorId=${reportMessageEntryForm.choreographyId}&partnerId=${reportMessageEntryForm.participantId}&convId=${reportMessageEntryForm.conversationId}&mId=${reportMessageEntryForm.messageId}&no=${counter}"
				target="_blank"> View</a></td>
		</tr>
	</logic:iterate>
</table>

<table class="NEXUS_BUTTON_TABLE" width="100%">
	<tr>

		<td class="BUTTON_RIGHT"><nexus:link
			href="ModifyMessage.do?noReset&refresh&type=transaction&command=requeue&partner=${reportMessageEntryForm.participantId}&chor=${reportMessageEntryForm.choreographyId}&conv=${reportMessageEntryForm.conversationId}&msg=${reportMessageEntryForm.messageId}"
			styleClass="NexusHeaderLink">
			<img src="images/submit.gif" border="0" alt="">
		</nexus:link></td>
		<td class="NexusHeaderLink">Re-Queue</td>
		<td class="BUTTON_RIGHT"><nexus:link
			href="ModifyMessage.do?noReset&refresh&type=transaction&command=stop&partner=${reportMessageEntryForm.participantId}&chor=${reportMessageEntryForm.choreographyId}&conv=${reportMessageEntryForm.conversationId}&msg=${reportMessageEntryForm.messageId}"
			styleClass="NexusHeaderLink">
			<img src="images/reset.gif" onclick="" border="0" alt="">
		</nexus:link></td>
		<td class="NexusHeaderLink">Stop</td>
	</tr>
</table>
</center>
