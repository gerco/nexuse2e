<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-nested" prefix="nested"%>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/nexus" prefix="nexus"%>

<nexus:helpBar helpDoc="documentation/Notifier_Listing.htm" />

<center>
<table class="NEXUS_TABLE" width="100%">
	<tr>
		<td><nexus:crumbs /></td>
	</tr>
	<tr>
		<td class="NEXUSScreenName">Notifiers</td>
	</tr>
</table>

<table class="NEXUS_TABLE" width="100%">
	<tr>
		<td width="50%" class="NEXUSSection">Notifier Name</td>
		<td class="NEXUSSection">Filter</td>
		<!-- 
        <td class="NEXUSSection">Active Status</td>
       -->
	</tr>

	<logic:iterate id="notifier" name="collection">
		<tr>
			<td class="NEXUSValue"><nexus:link
				href="NotifierView.do?nxLoggerId=${notifier.nxLoggerId}"
				styleClass="NexusLink">
				<bean:write name="notifier" property="name" />
			</nexus:link></td>
			<td class="NEXUSValue"><bean:write name="notifier"
				property="filterString" /></td>
				<!-- 
			<td class="NEXUSValue"><bean:write name="notifier"
				property="runningString" /></td>
				 -->
		</tr>
	</logic:iterate>

</table>

<center><logic:messagesPresent>
	<div class="NexusError"><html:errors /></div>
</logic:messagesPresent></center>

<table class="NEXUS_BUTTON_TABLE">
	<tr>
		<td>&nbsp;</td>
		<td class="NexusHeaderLink" style="text-align: right;"><nexus:link
			href="NotifierAdd.do?nxComponentId=0" styleClass="button">
			<img src="images/tree/plus.gif" class="button">Add Notifier</nexus:link></td>
	</tr>
</table>
</center>
