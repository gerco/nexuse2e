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
		<td class="NEXUSScreenName">Import CA Certificate KeyStore</td>
	</tr>
</table>

<html:form action="CACertificateSaveKeyStore.do" method="POST"
	enctype="multipart/form-data">
	<table class="NEXUS_TABLE" width="100%">
		<tr>
			<td colspan="2" class="NEXUSSection">Import CA Keystore</td>
		</tr>
		<tr>
			<td class="NEXUSName">KeyStore Filename</td>
			<td class="NEXUSValue"><html:file property="certficate"
				size="20" /> <br>
			<font size="1">Select valid KeyStore File</font></td>
		</tr>
		<tr>
			<td class="NEXUSName">KeyStore Password</td>
			<td class="NEXUSValue"><html:password property="password"
				size="20" /></td>
		</tr>
	</table>

	<logic:messagesPresent>
		<div class="NexusError"><html:errors /></div>
	</logic:messagesPresent>

	<table class="NEXUS_BUTTON_TABLE" width="100%">
		<tr>
			<td>&nbsp;</td>
			<td class="NexusHeaderLink" style="text-align: right;"><nexus:submit
				styleClass="button" sendFileForm="true">
				<img src="images/submit.gif" class="button">Import</nexus:submit></td>
		</tr>
	</table>
</html:form>
