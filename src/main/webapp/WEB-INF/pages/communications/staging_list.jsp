<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-nested" prefix="nested"%>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/nexus" prefix="nexus"%>

<nexus:helpBar helpDoc="documentation/SSL.htm" />

<logic:notEmpty name="redirectUrl">
	<logic:notEmpty name="redirectTimeout">
		<script type="text/javascript">
window.open('<bean:write name="redirectUrl"/>','Save as...')
</script>
	</logic:notEmpty>
</logic:notEmpty>

<center>

<table class="NEXUS_TABLE" width="100%">
	<tr>
		<td><nexus:crumbs styleClass="NEXUSScreenPathLink" /></td>
	</tr>
	<tr>
		<td class="NEXUSScreenName">Staged Certificates</td>
	</tr>
</table>

<table class="NEXUS_TABLE">
	<tr>
		<td colspan="100%" class="NEXUSSection">Certificates</td>
	</tr>

	<logic:iterate id="cert" name="collection">
		<tr>
			<td class="NEXUSName"><nexus:link styleClass="NexusLink"
				href="StagingCertificateView.do?nxCertificateId=${cert.nxCertificateId}">
				<bean:write name="cert" property="commonName" />
			</nexus:link></td>
			<td class="NEXUSName"><bean:write name="cert"
				property="issuerCN" /></td>
			<td class="NEXUSName"><bean:write name="cert" property="created" /></td>
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
<table class="NEXUS_BUTTON_TABLE" width="100%">
	<tr>
		<td>&nbsp;</td>
		<td class="NexusHeaderLink" style="text-align: right;"><nexus:link
			href="StagingImportCertificate.do" styleClass="button">
			<image src="images/submit.gif" class="button" />Import Certificate</nexus:link></td>
	</tr>

</table>

</center>
