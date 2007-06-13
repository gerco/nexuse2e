<%@ taglib uri="/tags/nexus" prefix="nexus"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-nested" prefix="nested"%>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>

<nexus:helpBar helpDoc="documentation/Collaboration_Partners.htm" />

<table class="NEXUS_TABLE" width="100%">
	<tr>
		<td><nexus:crumbs styleClass="NEXUSScreenPathLink"></nexus:crumbs></td>
	</tr>
	<tr>
		<td class="NEXUSScreenName">Certificates</td>
	</tr>
</table>

<table class="NEXUS_TAB_TABLE">
	<tr>
		<td class="NEXUS_TAB_LEFT_UNSELECTED"><img
			src="images/left666666.gif"></td>
		<td class="NEXUS_TAB_UNSELECTED"><nexus:link
			href="PartnerInfoView.do?nxPartnerId=${collaborationPartnerForm.nxPartnerId}"
			styleClass="NEXUS_TAB_UNSELECTED_LINK">
            Collaboration Partner</nexus:link></td>
		<td class="NEXUS_TAB_RIGHT_UNSELECTED"><img
			src="images/right666666.gif"></td>
		<td class="NEXUS_TAB_LEFT_UNSELECTED"><img
			src="images/left666666.gif"></td>
		<td class="NEXUS_TAB_UNSELECTED"><nexus:link
			href="PartnerConnectionList.do?nxPartnerId=${collaborationPartnerForm.nxPartnerId}"
			styleClass="NEXUS_TAB_UNSELECTED_LINK">Connections</nexus:link></td>
		<td class="NEXUS_TAB_RIGHT_UNSELECTED"><img
			src="images/right666666.gif"></td>
		<td class="NEXUS_TAB_LEFT_SELECTED"><img
			src="images/leftcccccc.gif"></td>
		<td class="NEXUS_TAB_SELECTED">Certificates</td>
		<td class="NEXUS_TAB_RIGHT_SELECTED"><img
			src="images/rightcccccc.gif"></td>
	</tr>
</table>
<table class="NEXUS_TABLE" width="100%">
	<tr>
		<td class="NexusSection">Certificate ID</td>
		<td class="NexusSection">Common Name</td>
		<td class="NexusSection">Organisation</td>
		<td class="NexusSection">Issuer Organisation</td>
		<td class="NexusSection">Validity</td>
	</tr>
	<logic:iterate id="cert" name="collaborationPartnerForm"
		property="certificates">
		<tr>
			<td class="NEXUSValue"><nexus:link
				href="PartnerCertificateView.do?nxPartnerId=${collaborationPartnerForm.nxPartnerId}&nxCertificateId=${cert.nxCertificateId}"
				styleClass="NexusLink">
				<bean:write name="cert" property="id" />
			</nexus:link></td>
			<td class="NEXUSValue"><bean:write name="cert"
				property="commonName" /></td>
			<td class="NEXUSValue"><bean:write name="cert"
				property="organisation" /></td>
			<td class="NEXUSValue"><bean:write name="cert" property="issuer" /></td>
			<td class="NEXUSValue"><logic:equal name="cert"
				property="validity" value="Okay">
				<font color="green"><b><bean:write name="cert"
					property="validity" /></b></font>
				<bean:write name="cert" property="remaining" />
			</logic:equal> <logic:notEqual name="cert" property="validity" value="Okay">
				<font color="red"><b><bean:write name="cert"
					property="validity" /></b></font>
			</logic:notEqual></td>
		</tr>
	</logic:iterate>
</table>
<center><logic:messagesPresent>
	<div class="NexusError"><html:errors /></div>
</logic:messagesPresent></center>
<table class="NEXUS_BUTTON_TABLE">
	<tr>
		<td>&nbsp;</td>
		<td class="NexusHeaderLink" style="text-align: right;"><logic:equal
			name="collaborationPartnerForm" property="type" value="1">
			<td class="NexusHeaderLink">Use Certificates -> CertificateStaging to
			add Certificate</td>
		</logic:equal> <logic:equal name="collaborationPartnerForm" property="type"
			value="2">
			<nexus:link
				href="PartnerCertificateAdd.do?partnerId=${collaborationPartnerForm.partnerId}"
				styleClass="button">
				<img src="images/tree/plus.gif" class="button">Add Certificate</nexus:link>
		</logic:equal></td>
	</tr>
</table>
