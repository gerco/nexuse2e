<%@ taglib uri="/tags/nexus" prefix="nexus"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-nested" prefix="nested"%>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/struts-html-el" prefix="html-el"%>

<% /*<nexus:helpBar helpDoc="documentation/Participants.htm"/> */ %>

<center>

<table class="NEXUS_TABLE" width="100%">
   <tr>
      <td>
        	<nexus:crumbs/>
      </td>
  </tr>
	<tr>
		<td class="NEXUSScreenName">Add Participant</td>
	</tr>
</table>
<html:form action="ParticipantAdd">
	<html:hidden property="nxChoreographyId"
		value="${participantForm.nxChoreographyId}" />
	<html:hidden property="submitted" value="false" />

	<table class="NEXUS_TABLE" width="100%">
		<tr>
			<td class="NEXUSName">Partner ID</td>
			<td class="NEXUSValue"><nexus:select submitOnChange="true"
				name="nxPartnerId">
				<logic:iterate id="partner" property="partners"
					name="participantForm">
					<logic:equal name="participantForm" property="nxPartnerId"
						value="${partner.nxPartnerId}">
						<option value="${partner.nxPartnerId}" selected="true">${partner.name}
						(${partner.partnerId})</option>
					</logic:equal>
					<logic:notEqual name="participantForm" property="nxPartnerId"
						value="${partner.nxPartnerId}">
						<option value="${partner.nxPartnerId}">${partner.name}
						(${partner.partnerId})</option>
					</logic:notEqual>
				</logic:iterate>
			</nexus:select></td>
		</tr>
		<tr>
			<td class="NEXUSName">Description</td>
			<td class="NEXUSValue"><html:text property="description" /></td>
		</tr>
		<tr>
			<td class="NEXUSName">Local Partner ID</td>
			<td class="NEXUSValue"><nexus:select submitOnChange="true"
				name="nxLocalPartnerId">
				<logic:iterate id="localPartner" property="localPartners"
					name="participantForm">
					<logic:equal name="participantForm" property="nxLocalPartnerId"
						value="${partner.nxPartnerId}">
						<option value="${localPartner.nxPartnerId}" selected="true">${localPartner.name}
						(${localPartner.partnerId})</option>
					</logic:equal>
					<logic:notEqual name="participantForm" property="nxLocalPartnerId"
						value="${partner.nxPartnerId}">
						<option value="${localPartner.nxPartnerId}">${localPartner.name}
						(${localPartner.partnerId})</option>
					</logic:notEqual>
				</logic:iterate>
			</nexus:select></td>
		</tr>
		<tr>
			<td class="NEXUSName">Local Certificate</td>
			<td class="NEXUSValue"><html:select
				property="nxLocalCertificateId">
				<html-el:option value="0">none</html-el:option>
				<logic:iterate id="localCertficate" property="localCertificates"
					name="participantForm">
					<html-el:option value="${localCertficate.nxCertificateId}">${localCertficate.name}</html-el:option>
				</logic:iterate>
			</html:select></td>
		</tr>
		<tr>
			<td class="NEXUSName">Connection</td>
			<td class="NEXUSValue"><html:select property="nxConnectionId">
				<logic:iterate id="con" property="connections"
					name="participantForm">
					<html-el:option value="${con.nxConnectionId}">
						<bean:write name="con" property="name" /> (<bean:write name="con"
							property="uri" />)</html-el:option>
				</logic:iterate>
			</html:select></td>
		</tr>

	</table>
<logic:messagesPresent>
	<div class="NexusError"><html:errors /></div>
</logic:messagesPresent>

	<table class="NEXUS_BUTTON_TABLE" width="100%">
		<tr>
			<td>&nbsp;</td>
			<td class="BUTTON_RIGHT"><nexus:submit
				onClick="document.participantForm.submitted.value=true;"
				styleClass="button">
				<img src="images/icons/tick.png" class="button">Create</nexus:submit></td>
		</tr>
	</table>
</html:form></center>
