<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-nested" prefix="nested"%>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/nexus" prefix="nexus"%>

<nexus:helpBar helpDoc="documentation/SSL.htm" />

<logic:equal name="protectedFileAccessForm" property="status" value="3">
	<script type="text/javascript">
window.open('DataSaveAs?type=cacerts','Save as...')
</script>
</logic:equal>

<center>
<table class="NEXUS_TABLE" width="100%">
	<tr>
		<td><nexus:crumbs /></td>
	</tr>
	<tr>
		<td class="NEXUSScreenName">CA Certificates</td>
	</tr>
</table>

<table class="NEXUS_TABLE">
	<tr>
		<td colspan="100%" class="NEXUSSection">Certificates</td>
	</tr>

	<logic:iterate id="cert" name="collection">
		<tr>
			<td class="NEXUSName"><nexus:link styleClass="NexusLink"
				href="CACertificateView.do?alias=${cert.alias}">
				<bean:write name="cert" property="alias" />
			</nexus:link></td>
			<td class="NEXUSName"><bean:write name="cert"
				property="commonName" /></td>
			<td class="NEXUSValue"><logic:equal name="cert" property="valid"
				value="Okay">
				<font color="green"><b><bean:write name="cert"
					property="valid" /></b></font>
				<bean:write name="cert" property="timeRemaining" />
			</logic:equal> <logic:notEqual name="cert" property="valid" value="Okay">
				<font color="red"><b><bean:write name="cert"
					property="valid" /></b></font>
			</logic:notEqual></td>
		</tr>
	</logic:iterate>
</table>

<table class="NEXUS_BUTTON_TABLE">
	<tr>
		<td class="NexusHeaderLink" style="text-align: right;"><nexus:link
			href="CACertificateAddSingleCert.do" styleClass="button">
			<img src="images/tree/plus.gif" class="button">Add CA Certificate</nexus:link>
		</td>
		<td class="NexusHeaderLink" style="text-align: right;"><nexus:link
			href="CACertificateImportKeyStore.do" styleClass="button">
			<img src="images/tree/plus.gif" class="button">Import CA KeyStore</nexus:link>
		</td>
		<td class="NexusHeaderLink" style="text-align: right;"><nexus:link
			href="CACertificateExportKeyStore.do" styleClass="button">
			<img src="images/tree/plus.gif" class="button">Export CA KeyStore to Filesystem</nexus:link>
		</td>
	</tr>
</table>
</center>
