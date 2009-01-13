<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-nested" prefix="nested"%>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/nexus" prefix="nexus"%>

<% /*<nexus:helpBar helpDoc="documentation/Tools.htm" /> */ %>

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
			<img border="0" src="images/icons/lorry_go.png">
		</nexus:link></td>
		<td class="NEXUSNameNoWidth"><nexus:link styleClass="NexusLink"
			href="MessageSubmission.do">Message Submission</nexus:link></td>
	</tr>
	<tr>
		<td class="NEXUSIcon"><nexus:link styleClass="NexusImgLink"
			href="ProvisioningSubmission.do">
			<img border="0" src="images/icons/television_add.png">
		</nexus:link></td>
		<td class="NEXUSNameNoWidth"><nexus:link styleClass="NexusLink"
			href="ProvisioningSubmission.do">Partner Provisioning</nexus:link></td>
	</tr>
	<tr>
		<td class="NEXUSIcon"><nexus:link styleClass="NexusImgLink"
			href="DatabasePurge.do?type=select">
			<img border="0" src="images/icons/database_lightning.png">
		</nexus:link></td>
		<td class="NEXUSNameNoWidth"><nexus:link styleClass="NexusLink"
			href="DatabasePurge.do?type=select">Database Purge</nexus:link></td>
	</tr>
	<tr>
		<td class="NEXUSIcon"><nexus:link styleClass="NexusImgLink"
			href="MappingMaintenance.do">
			<img border="0" src="images/icons/database_table.png">
		</nexus:link></td>
		<td class="NEXUSNameNoWidth"><nexus:link styleClass="NexusLink"
			href="MappingMaintenance.do">Mapping Maintenance</nexus:link></td>
	</tr>
	<tr>
		<td class="NEXUSIcon"><nexus:link styleClass="NexusImgLink"
			href="PersistentProperties.do">
			<img border="0" src="images/icons/database_save.png">
		</nexus:link></td>
		<td class="NEXUSNameNoWidth"><nexus:link styleClass="NexusLink"
			href="MappingMaintenance.do">Persistent Properties</nexus:link></td>
	</tr>
    <tr>
        <td class="NEXUSIcon"><nexus:link styleClass="NexusImgLink"
            href="ConfigurationManagement.do">
            <img border="0" src="images/icons/page_save.png">
        </nexus:link></td>
        <td class="NEXUSNameNoWidth"><nexus:link styleClass="NexusLink"
            href="FileDownload.do">File Download</nexus:link></td>
    </tr>
    <tr>
        <td class="NEXUSIcon"><nexus:link styleClass="NexusImgLink"
            href="ConfigurationManagement.do">
            <img border="0" src="images/icons/wrench.png">
        </nexus:link></td>
        <td class="NEXUSNameNoWidth"><nexus:link styleClass="NexusLink"
            href="PatchManagement.do">Patches</nexus:link></td>
    </tr>
	<tr>
		<td class="NEXUSIcon"><nexus:link styleClass="NexusImgLink"
			href="ConfigurationManagement.do">
			<img border="0" src="images/icons/server_database.png">
		</nexus:link></td>
		<td class="NEXUSNameNoWidth"><nexus:link styleClass="NexusLink"
			href="ConfigurationManagement.do">Configuration Management</nexus:link></td>
	</tr>

</table>
<center><logic:messagesPresent>
	<div class="NexusError"><html:errors /></div>
</logic:messagesPresent></center>
