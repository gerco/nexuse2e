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
		<logic:equal name="collaborationPartnerForm" property="type" value="1">
			<td class="NEXUSScreenName">Update Server Identity</td>
		</logic:equal>
		<logic:equal name="collaborationPartnerForm" property="type" value="2">
			<td class="NEXUSScreenName">Update Collaboration Partner</td>
		</logic:equal>
	</tr>
</table>
<html:form action="UpdatePartnerInfo.do">
	<table class="NEXUS_TAB_TABLE">
		<tr>
			<td class="NEXUS_TAB_LEFT_SELECTED"><img
				src="images/leftcccccc.gif"></td>
			<td class="NEXUS_TAB_SELECTED">Collaboration Partner</td>
			<td class="NEXUS_TAB_RIGHT_SELECTED"><img
				src="images/rightcccccc.gif"></td>
			<td class="NEXUS_TAB_LEFT_UNSELECTED"><img
				src="images/left666666.gif"></td>
			<td class="NEXUS_TAB_UNSELECTED"><nexus:link
				href="PartnerConnectionList.do?nxPartnerId=${collaborationPartnerForm.nxPartnerId}"
				styleClass="NEXUS_TAB_UNSELECTED_LINK">Connections</nexus:link></td>
			<td class="NEXUS_TAB_RIGHT_UNSELECTED"><img
				src="images/right666666.gif"></td>
			<td class="NEXUS_TAB_LEFT_UNSELECTED"><img
				src="images/left666666.gif"></td>
			<td class="NEXUS_TAB_UNSELECTED"><nexus:link
				href="PartnerCertificateList.do?nxPartnerId=${collaborationPartnerForm.nxPartnerId}"
				styleClass="NEXUS_TAB_UNSELECTED_LINK">Certificates</nexus:link></td>
			<td class="NEXUS_TAB_RIGHT_UNSELECTED"><img
				src="images/right666666.gif"></td>
		</tr>
	</table>
	<table class="NEXUS_TABLE" width="100%">
		<tr>
			<td class="NEXUSName">Partner Id</td>
			<td class="NEXUSValue"><html:text property="partnerId" size="50" /></td>
		</tr>
		<tr>
			<td class="NEXUSName">Partner Id Type</td>
			<td class="NEXUSValue"><html:text property="partnerIdType"
				size="50" /></td>
		</tr>
		<tr>
			<td class="NEXUSName">Name</td>
			<td class="NEXUSValue"><html:text property="name" size="50" /></td>
		</tr>
		<tr>
			<td class="NEXUSName">Company</td>
			<td class="NEXUSValue"><html:text property="company" size="50" /></td>
		</tr>
		<tr>
			<td class="NEXUSName">Address Line 1</td>
			<td class="NEXUSValue"><html:text property="address1" size="50" /></td>
		</tr>
		<tr>
			<td class="NEXUSName">Address Line 2</td>
			<td class="NEXUSValue"><html:text property="address2" size="50" /></td>
		</tr>
		<tr>
			<td class="NEXUSName">City</td>
			<td class="NEXUSValue"><html:text property="city" size="50" /></td>
		</tr>
		<tr>
			<td class="NEXUSName">State</td>
			<td class="NEXUSValue"><html:text property="state" size="50" /></td>
		</tr>
		<tr>
			<td class="NEXUSName">Zip Code</td>
			<td class="NEXUSValue"><html:text property="zip" size="50" /></td>
		</tr>
		<tr>
			<td class="NEXUSName">Country</td>
			<td class="NEXUSValue"><html:text property="country" size="50" /></td>
		</tr>
		<tr>
			<td valign="top" class="NEXUSName">Choreographies</td>
			<td valign="top" class="NEXUSValue">
			<table>
				<logic:iterate id="choreography" name="collaborationPartnerForm"
					property="choreographies">
					<tr>
						<td class="NEXUSIcon"><img
							src="images/icons/arrow_refresh_small.png" alts=""></td>
						<td class="NEXUSNameNoWidth"><nexus:link
							href="ChoreographyView.do?choreographyName=${choreography}"
							styleClass="NexusLink">
							<bean:write name="choreography" />
						</nexus:link></td>
					</tr>
				</logic:iterate>
			</table>
			</td>
		</tr>
	</table>

    <center> 
      <logic:messagesPresent> 
        <div class="NexusError"><html:errors/></div>
        </logic:messagesPresent>
    </center>

	<table class="NEXUS_BUTTON_TABLE">
		<tr>
			<td>&nbsp;</td>
			<td class="NexusHeaderLink" style="text-align: right;"><nexus:submit
				styleClass="button">
				<img src="images/icons/tick.png" class="button">Save</nexus:submit></td>
        <logic:equal name="collaborationPartnerForm" property="type" value="1">
            <td class="NexusHeaderLink" style="text-align: right;"><nexus:link
                href="ServerIdentityDelete.do?nxPartnerId=${collaborationPartnerForm.nxPartnerId}"
                styleClass="button">
                <img src="images/icons/delete.png" class="button">Delete</nexus:link></td>
        </logic:equal>
        <logic:notEqual name="collaborationPartnerForm" property="type" value="1">
            <td class="NexusHeaderLink" style="text-align: right;"><nexus:link
                href="CollaborationPartnerDelete.do?nxPartnerId=${collaborationPartnerForm.nxPartnerId}"
                styleClass="button">
                <img src="images/icons/delete.png" class="button">Delete</nexus:link></td>
        </logic:notEqual>
		</tr>
	</table>
</html:form>
