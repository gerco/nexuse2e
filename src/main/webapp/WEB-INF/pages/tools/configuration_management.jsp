<%@ taglib uri="/tags/nexus" prefix="nexus"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-nested" prefix="nested"%>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/struts-html-el" prefix="html-el"%>

<nexus:helpBar />

<center>
<table class="NEXUS_TABLE" width="100%">
	<tr>
		<td><nexus:crumbs styleClass="NEXUSScreenPathLink"></nexus:crumbs></td>
	</tr>
	<tr>
		<td class="NEXUSScreenName">Configuration Management</td>
	</tr>
</table>

<html:form action="ImportConfiguration.do" method="post" enctype="multipart/form-data">
	<table width="100%">
		<tr>
			<td colspan="2" class="NEXUSSection">Import Configuration</td>
		</tr>
		<tr>
			<td class="NEXUSName">Configuration file</td>
			<td class="NEXUSValue"><html:file size="50" property="payloadFile" onkeypress="return checkKey(event);" /></td>
		</tr>
	</table>

	<table class="NEXUS_BUTTON_TABLE" width="100%">
		<tr>
			<td class="BUTTON_RIGHT"><input name="Submit" value="blank"
				type="hidden"> <nexus:submit
				precondition="confirm('Your current configuration will be replaced by the imported one.\nPlease create a backup of your configuration before proceeding. Confirm to proceed now.')"
				onClick="document.configurationManagementForm.Submit.value='Submit';"
				sendFileForm="true" styleClass="button">
				<img src="images/submit.gif" name="ExportButton" class="button">Import</nexus:submit>
			</td>
		</tr>
	</table>
	
	<table width="100%">
		<tr>
			<td colspan="2" class="NEXUSSection">Export Configuration</td>
		</tr>
		<tr>
			<td class="NEXUSName" colspan="2"><input  type="radio" checked="checked">Save As...</td>
		</tr>
	</table>
	
	<table class="NEXUS_BUTTON_TABLE" width="100%">
		<tr>
			<td class="BUTTON_RIGHT"><input name="Submit" value="blank"
				type="hidden"><nobr><a href="ExportConfiguration.do" class="button">
				<img src="images/submit.gif" name="ImportButton" class="button">Export</a>
				</nobr>
			</td>
		</tr>
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
