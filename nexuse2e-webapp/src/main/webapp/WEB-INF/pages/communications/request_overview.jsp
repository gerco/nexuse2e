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
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-nested" prefix="nested"%>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/nexus" prefix="nexus"%>

<nexus:fileUploadResponse>
<% /*<nexus:helpBar helpDoc="documentation/SSL.htm" /> */ %>

<logic:equal name="type" value="csr">
	<script type="text/javascript">
window.open('DataSaveAs.do?type=request&format=<bean:write name="format"/>&nxCertificateId=<bean:write name="nxCertificateId"/>','Save as...')
</script>
</logic:equal>
<logic:equal name="type" value="privatepem">
	<script type="text/javascript">
window.open('DataSaveAs.do?type=privatepem','Save as...')
</script>
</logic:equal>

<center>

<table class="NEXUS_TABLE" width="100%">
	<tr>
		<td><nexus:crumbs /></td>
	</tr>
	<tr>
		<td colspan="2" class="NEXUSScreenName">Request Handling</td>
	</tr>
	<tr>
		<td colspan="2" class="NEXUSSection">Create Request</td>
	</tr>
	<tr>
		<logic:equal name="requestButtonStateForm" property="createRequest"
			value="true">
			<td class="NEXUSValue"><nexus:link href="RequestCreate.do"
				styleClass="button">
				<img src="images/icons/add.png" class="button" />Create Request</nexus:link></td>
		</logic:equal>
		<logic:notEqual name="requestButtonStateForm" property="createRequest"
			value="true">
			<td class="NEXUSValue"><font class="NEXUSScreenPathLinkInactive"><img
				src="images/icons/add.png" class="button" />Create Request</font></td>
		</logic:notEqual>
		<td class="NEXUSValue">&nbsp;</td>
	</tr>
	<tr>
		<td colspan="2" class="NEXUSSpacing">&nbsp;</td>
	</tr>
	<tr>
		<td colspan="2" class="NEXUSSection">Import Certificate Data</td>
	</tr>
	<tr>
		<logic:equal name="requestButtonStateForm" property="importCert"
			value="true">
			<td class="NEXUSValue"><nexus:link href="RequestImportCert.do"
				styleClass="button">
				<img src="images/icons/key_add.png" class="button" />Import Certificate</nexus:link></td>
		</logic:equal>
		<logic:notEqual name="requestButtonStateForm" property="importCert"
			value="true">
			<td class="NEXUSValue"><font class="NEXUSScreenPathLinkInactive"><img
				src="images/icons/key_add.png" class="button" />Import Certificate</font></td>
		</logic:notEqual>

		<logic:equal name="requestButtonStateForm" property="importBackup"
			value="true">
			<td class="NEXUSValue"><nexus:link href="RequestImportBackup.do"
				styleClass="button">
				<img src="images/icons/key_add.png" class="button" />Import Key Backup</nexus:link></td>
		</logic:equal>
		<logic:notEqual name="requestButtonStateForm" property="importBackup"
			value="true">
			<td class="NEXUSValue"><font class="NEXUSScreenPathLinkInactive"><img
				src="images/icons/key_add.png" class="button" />Import Key Backup</font></td>
		</logic:notEqual>

	</tr>

	<tr>
		<td colspan="2" class="NEXUSSpacing">&nbsp;</td>
	</tr>
	<tr>
		<td colspan="2" class="NEXUSSection">Show Certificate Request</td>
	</tr>
	<tr>
		<logic:equal name="requestButtonStateForm" property="showRequest"
			value="true">
			<td class="NEXUSValue"><nexus:link href="RequestShowCSR.do"
				styleClass="button">
				<img src="images/icons/magnifier.png" class="button" />Show Request</nexus:link></td>
		</logic:equal>
		<logic:notEqual name="requestButtonStateForm" property="showRequest"
			value="true">
			<td class="NEXUSValue"><font class="NEXUSScreenPathLinkInactive"><img
				src="images/icons/magnifier.png" class="button" />Show Request</font></td>
		</logic:notEqual>
		<td class="NEXUSValue">&nbsp;</td>
	</tr>

	<tr>
		<td colspan="2" class="NEXUSSpacing">&nbsp;</td>
	</tr>
	<tr>
		<td colspan="2" class="NEXUSSection">Export Certificate Data</td>
	</tr>
	<tr>
		<logic:equal name="requestButtonStateForm" property="exportPKCS12"
			value="true">
			<td class="NEXUSValue"><nexus:link href="RequestExportPKCS12.do"
				styleClass="button">
				<img src="images/icons/disk.png" class="button" />Export Full Key Information (Backup)</nexus:link></td>
		</logic:equal>
		<logic:notEqual name="requestButtonStateForm" property="exportPKCS12"
			value="true">
			<td class="NEXUSValue"><font class="NEXUSScreenPathLinkInactive"><img
				src="images/icons/disk.png" class="button" />Export Full Key Information
			(Backup)</font></td>
		</logic:notEqual>
		<logic:equal name="requestButtonStateForm" property="exportRequest"
			value="true">
			<td class="NEXUSValue"><nexus:link href="RequestExportCSR.do"
				styleClass="button">
				<img src="images/icons/disk.png" class="button" />Export Request (CSR)</nexus:link></td>
		</logic:equal>
		<logic:notEqual name="requestButtonStateForm" property="exportRequest"
			value="true">
			<td class="NEXUSValue"><font class="NEXUSScreenPathLinkInactive"><img
				src="images/icons/disk.png" class="button" />Export Request(CSR)</font></td>
		</logic:notEqual>
	</tr>
	<tr>
		<td colspan="2" class="NEXUSSpacing">&nbsp;</td>
	</tr>
	<tr>
		<td colspan="2" class="NEXUSSection">Remove Certificate Data</td>
	</tr>
	<tr>
		<logic:equal name="requestButtonStateForm" property="deleteRequest"
			value="true">
			<td class="NEXUSValue"><nexus:link href="RequestDelete.do"
				styleClass="button">
				<img src="images/icons/delete.png" class="button" />Delete Request</nexus:link></td>
		</logic:equal>
		<logic:notEqual name="requestButtonStateForm" property="deleteRequest"
			value="true">
			<td class="NEXUSValue"><font class="NEXUSScreenPathLinkInactive"><img
				src="images/icons/delete.png" class="button" />Delete Request</font></td>
		</logic:notEqual>
		<td class="NEXUSValue">&nbsp;</td>
	</tr>
</table>

</center>
</nexus:fileUploadResponse>