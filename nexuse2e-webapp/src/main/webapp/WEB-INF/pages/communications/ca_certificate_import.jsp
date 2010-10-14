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
<%@ taglib uri="/tags/nexus" prefix="nexus"%>

<nexus:fileUploadResponse>
<% /*<nexus:helpBar helpDoc="documentation/SSL.htm" /> */ %>
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
			<td class="NEXUSValue"><html:file property="certficate" size="20" onkeypress="return checkKey(event);" /> <br>
			<font size="1">Select valid KeyStore File</font></td>
		</tr>
		<tr>
			<td class="NEXUSName">KeyStore Password</td>
			<td class="NEXUSValue"><html:password property="password" size="20" onkeypress="return checkKey(event);" /></td>
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
				<img src="images/icons/tick.png" class="button">Import</nexus:submit></td>
		</tr>
	</table>
</html:form>
</nexus:fileUploadResponse>
