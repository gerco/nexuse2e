<%--

     NEXUSe2e Business Messaging Open Source
     Copyright 2000-2009, Tamgroup and X-ioma GmbH

     This is free software; you can redistribute it and/or modify it
     under the terms of the GNU Lesser General Public License as
     published by the Free Software Foundation version 2.1 of
     the License.

     This software is distributed in the hope that it will be useful,
     but WITHOUT ANY WARRANTY; without even the implied warranty of
     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
     Lesser General Public License for more details.

     You should have received a copy of the GNU Lesser General Public
     License along with this software; if not, write to the Free
     Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
     02110-1301 USA, or see the FSF site: http://www.fsf.org.

--%>
<%@ taglib uri="/tags/nexus" prefix="nexus"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-nested" prefix="nested"%>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>

<nexus:fileUploadResponse>
<% /*<nexus:helpBar helpDoc="documentation/Collaboration_Partners.htm" /> */ %>

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
				<logic:empty name="cert" property="id">n/a</logic:empty>
				<logic:notEmpty name="cert" property="id"><bean:write name="cert" property="id" /></logic:notEmpty>				
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
		<td class="BUTTON_RIGHT" style="text-align: right;"><logic:equal
			name="collaborationPartnerForm" property="type" value="1">
			<td class="NexusHeaderLink">Use Certificates -&gt; CertificateStaging to
			add Certificate</td>
		</logic:equal> <logic:equal name="collaborationPartnerForm" property="type"
			value="2">
			<nexus:link
				href="PartnerCertificateAdd.do?partnerId=${collaborationPartnerForm.partnerId}"
				styleClass="button">
				<img src="images/icons/add.png" class="button">Add Certificate</nexus:link>
		</logic:equal></td>
	</tr>
</table>
</nexus:fileUploadResponse>
