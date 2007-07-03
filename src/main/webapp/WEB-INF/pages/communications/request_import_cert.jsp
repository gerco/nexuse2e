<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-nested" prefix="nested"%>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/tags/nexus" prefix="nexus"%>

<nexus:helpBar helpDoc="documentation/SSL.htm" />

<center>
<table class="NEXUS_TABLE" width="100%">
	<tr>
		<td><nexus:crumbs /></td>
	</tr>
	<tr>
		<td class="NEXUSScreenName">Import Certificate</td>
	</tr>
</table>

<html:form action="RequestVerifyCertChainCert.do" method="POST"
	enctype="multipart/form-data">
	<table class="NEXUS_TABLE" width="100%">
		<tr>
			<td colspan="2" class="NEXUSSection">Select import directory: <bean:write
				name="protectedFileAccessForm" property="alias" /></td>
		</tr>
		<tr>
			<td class="NEXUSName">Path</td>
			<td class="NEXUSValue"><html:file property="certficate" size="60" /></td>
		</tr>
	</table>
	<center><logic:messagesPresent>
		<div class="NexusError"><html:errors /></div>
	</logic:messagesPresent></center>

	<table class="NEXUS_BUTTON_TABLE" width="100%">
		<tr>
			<td>&nbsp;</td>
			<td class="BUTTON_RIGHT"><nexus:submit sendFileForm="true">
				<img src="images/submit.gif" name="SUBMIT">
			</nexus:submit></td>
			<td class="NexusHeaderLink">Import</td>
		</tr>
	</table>
</html:form></center>
