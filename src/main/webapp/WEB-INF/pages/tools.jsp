<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-nested" prefix="nested"%>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/nexus" prefix="nexus"%>

<nexus:helpBar helpDoc="documentation/Tools.htm" />

<table class="NEXUS_TABLE" width="100%">
	<tr>
		<td><nexus:crumbs /></td>
	</tr>
	<tr>
		<td class="NEXUSScreenName">Tools</td>
	</tr>
</table>

<table width="100%" class="NEXUS_TABLE">
	<tr>
		<td class="NEXUSIcon"><nexus:link styleClass="NexusImgLink"
			href="MessageSubmission.do">
			<img border="0" src="images/tree/communications.gif">
		</nexus:link></td>
		<td class="NEXUSNameNoWidth"><nexus:link styleClass="NexusLink"
			href="MessageSubmission.do">Message Submission</nexus:link></td>
	</tr>
	<tr>
		<td class="NEXUSIcon"><nexus:link styleClass="NexusImgLink"
			href="ProvisioningSubmission.do">
			<img border="0" src="images/tree/communications.gif">
		</nexus:link></td>
		<td class="NEXUSNameNoWidth"><nexus:link styleClass="NexusLink"
			href="ProvisioningSubmission.do">Partner Provisioning</nexus:link></td>
	</tr>
	<tr>
		<td class="NEXUSIcon"><nexus:link styleClass="NexusImgLink"
			href="DatabasePurge.do?type=select">
			<img border="0" src="images/delete.gif">
		</nexus:link></td>
		<td class="NEXUSNameNoWidth"><nexus:link styleClass="NexusLink"
			href="DatabasePurge.do?type=select">Database Purge</nexus:link></td>
	</tr>
	<tr>
		<td class="NEXUSIcon"><nexus:link styleClass="NexusImgLink"
			href="MappingMaintenance.do">
			<img border="0" src="images/tree/collaborativepartners.gif">
		</nexus:link></td>
		<td class="NEXUSNameNoWidth"><nexus:link styleClass="NexusLink"
			href="MappingMaintenance.do">Mapping Maintenance</nexus:link></td>
	</tr>



</table>
<center><logic:messagesPresent>
	<div class="NexusError"><html:errors /></div>
</logic:messagesPresent></center>
