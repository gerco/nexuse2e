<%@ taglib uri="/tags/nexus" prefix="nexus"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-nested" prefix="nested"%>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/struts-html-el" prefix="html-el"%>

<nexus:fileUploadResponse>
<% /* <nexus:helpBar /> */ %>

<center>
<table class="NEXUS_TABLE" width="100%">
	<tr>
		<td><nexus:crumbs styleClass="NEXUSScreenPathLink"></nexus:crumbs></td>
	</tr>
	<tr>
		<td class="NEXUSScreenName">Patches</td>
	</tr>
</table>

<html:form action="PatchManagement.do" method="post" enctype="multipart/form-data">
	<table width="100%">
		<tr>
			<td colspan="2" class="NEXUSSection">Patch Upload</td>
		</tr>
		<tr>
			<td class="NEXUSName">Patch File</td>
			<td class="NEXUSValue"><html:file size="50" property="patchFile" onkeypress="return checkKey(event);" /></td>
		</tr>
	</table>

	<table class="NEXUS_BUTTON_TABLE" width="100%">
		<tr>
			<td class="BUTTON_RIGHT"><input name="Submit" value="blank"
				type="hidden"> <nexus:submit
				onClick="document.patchManagementForm.Submit.value='Submit';"
				sendFileForm="true" styleClass="button">
				<img src="images/icons/tick.png" name="ExportButton" class="button">Upload Patch</nexus:submit>
			</td>
		</tr>
	</table>
	
	<table width="100%">
		<tr>
			<td class="NEXUSSection">Patch</td>
			<td class="NEXUSSection">Description</td>
			<td class="NEXUSSection">Version Information</td>
			<td class="NEXUSSection">Run</td>
		</tr>
		<logic:empty name="patchManagementForm" property="patchBundles.patchBundles">
			<tr>
				<td class="NEXUSName" colspan="4">
					<i>&nbsp;&nbsp;No patches uploaded yet</i>
				</td>
			</tr>
		</logic:empty>
		<logic:notEmpty name="patchManagementForm" property="patchBundles.patchBundles">
			<logic:iterate id="patch" name="patchManagementForm" property="patchBundles.patches" indexId="index">
			<tr>
				<td class="NEXUSName">
					${patch.patchName}
				</td>
				<td class="NEXUSName">
					${patch.patchDescription}
				</td>
				<td class="NEXUSName">
					${patch.versionInformation}
				</td>
				<td class="NEXUSName">
					<a href="ExecutePatch?index=${index}" target="_blank">
						<img src="images/icons/resultset_next.png" id="run" name="ExportButton" alt="Run Patch" class="button">
						<span dojoType="dijit.Tooltip" connectId="run" toggle="explode">Run Patch</span>
					</a>
				</td>
			</tr>
			</logic:iterate>
		</logic:notEmpty>
	</table>
	
	
</html:form></center>
<center><logic:messagesPresent>
	<div class="NexusError"><html:errors /></div>
</logic:messagesPresent> <logic:messagesPresent message="true">
	<html:messages id="msg" message="true">
		<div class="NexusMessage"><bean:write name="msg" /></div>
		<br />
	</html:messages>
</logic:messagesPresent></center>
</nexus:fileUploadResponse>
