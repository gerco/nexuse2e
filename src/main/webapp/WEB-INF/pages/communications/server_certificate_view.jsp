<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-nested" prefix="nested"%>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/tags/nexus" prefix="nexus"%>

<nexus:helpBar helpDoc="documentation/SSL.htm" />

<logic:equal name="protectedFileAccessForm" property="status" value="3">
	<script type="text/javascript">
window.open('DataSaveAs?type=serverCert','Save as...')
</script>
</logic:equal>

<center>
<table class="NEXUS_TABLE" width="100%">
	<tr>
		<td><nexus:crumbs /></td>
	</tr>
	<tr>
		<td class="NEXUSScreenName">Server Certificate</td>
	</tr>
</table>

<bean:size id="size" name="collection" /> <logic:iterate id="cert"
	indexId="counter" name="collection">
	<table class="NEXUS_TABLE" width="100%">
		<tr>
			<%
			if ( counter.intValue() == 0 ) {
			%>
			<td colspan="2" class="NEXUSSection">Server Certificate</td>
			<%
			} else if ( counter.intValue() == ( size.intValue() - 1 ) ) {
			%>
			<td colspan="2" class="NEXUSSection">CA Root Certificate</td>
			<%
			} else {
			%>
			<td colspan="2" class="NEXUSSection">CA Intermediate Certificate
			<%=counter.intValue()%></td>
			<%
			}
			%>
		</tr>
		<tr>
			<td class="NEXUSName">Common Name</td>
			<td class="NEXUSValue"><bean:write name="cert"
				property="commonName" /></td>
		</tr>
		<tr>
			<td class="NEXUSName">Organisation</td>
			<td class="NEXUSValue"><bean:write name="cert"
				property="organisation" /></td>
		</tr>
		<tr>
			<td class="NEXUSName">Organisation Unit</td>
			<td class="NEXUSValue"><bean:write name="cert"
				property="organisationUnit" /></td>
		</tr>
		<tr>
			<td class="NEXUSName">Country</td>
			<td class="NEXUSValue"><bean:write name="cert"
				property="country" /></td>
		</tr>
		<tr>
			<td class="NEXUSName">State</td>
			<td class="NEXUSValue"><bean:write name="cert" property="state" /></td>
		</tr>
		<tr>
			<td class="NEXUSName">Location</td>
			<td class="NEXUSValue"><bean:write name="cert"
				property="location" /></td>
		</tr>
		<tr>
			<td class="NEXUSName">E-Mail</td>
			<td class="NEXUSValue"><bean:write name="cert" property="email" /></td>
		</tr>
		<tr>
			<td class="NEXUSName">Not Valid Before</td>
			<td class="NEXUSValue"><bean:write name="cert"
				property="notBefore" /></td>
		</tr>
		<tr>
			<td class="NEXUSName">Not Valid After</td>
			<td class="NEXUSValue"><bean:write name="cert"
				property="notAfter" /></td>
		</tr>
		<tr>
			<td class="NEXUSName">Validity</td>
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
		<tr>
			<td class="NEXUSName">Fingerprint</td>
			<td class="NEXUSValue"><bean:write name="cert"
				property="fingerprint" /></td>
		</tr>
	</table>
</logic:iterate>

<table class="NEXUS_BUTTON_TABLE" width="100%">
	<tr>
		<td>&nbsp;</td>
		<td class="BUTTON_RIGHT"><nexus:link
			href="ServerCertificateUpdate.do" styleClass="NexusLink">
			<image src="images/icons/tick.png" border="0" />
		</nexus:link></td>
		<td class="NexusHeaderLink">Change SSL Certificates</td>
	</tr>
	<tr>
		<td>&nbsp;</td>
		<td class="BUTTON_RIGHT"><nexus:link
			href="ServerCertificateExport.do" styleClass="NexusLink">
			<image src="images/icons/tick.png" border="0" />
		</nexus:link></td>
		<td class="NexusHeaderLink">Export this Certificate Chain</td>
	</tr>
	<tr>
		<td>&nbsp;</td>
		<td class="BUTTON_RIGHT"><nexus:link
			href="ServerCertificateDelete.do" styleClass="NexusLink">
			<image src="images/reset.gif" border="0" />
		</nexus:link></td>
		<td class="NexusHeaderLink">Delete this Certificate Chain</td>
	</tr>
</table>
</center>
