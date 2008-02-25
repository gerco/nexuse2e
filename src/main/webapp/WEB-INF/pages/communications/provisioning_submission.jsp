<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-nested" prefix="nested"%>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/tags/nexus" prefix="nexus"%>

<% /* <nexus:helpBar /> */ %>

<table class="NEXUS_TABLE" width="100%">
	<tr>
		<td><nexus:crumbs /></td>
	</tr>

	<tr>
		<td class="NEXUSScreenName">Provisioning Submission</td>
	</tr>
</table>

<html:form action="ProvisioningSubmission.do" method="POST">
	<table width="100%">
		<tr>
			<td colspan="2" class="NEXUSSection">Parameters for submitting a
			Provisioning</td>
		</tr>

		<tr>
			<td class="NEXUSName">Choreography ID</td>
			<td class="NEXUSValue"><html:select property="choreographyId"
				onchange="document.provisioningSubmissionForm.submit()">
				<html:options property="choreographies"
					labelProperty="choreographies" />
			</html:select></td>
		</tr>

		<tr>
			<td class="NEXUSName">Receiver</td>
			<td class="NEXUSValue"><html:select property="receiver">
				<html:options property="receivers" labelProperty="receivers" />
			</html:select></td>
		</tr>
	</table>

	<table class="NEXUS_BUTTON_TABLE" width="100%">
		<tr>
			<td class="BUTTON_RIGHT"><input name="Submit" value="blank"
				src="images/icons/tick.png" type="hidden"> <input
				src="images/icons/tick.png" name="SubmitButton" value="Submit"
				type="image" onClick="setSubmitValue( 'Submit' )"></td>

			<td class="NexusHeaderLink">Execute</td>
		</tr>
	</table>
</html:form>

<center><logic:messagesPresent>
	<div class="NexusError"><html:errors /></div>
</logic:messagesPresent> <logic:messagesPresent message="true">
	<html:messages id="msg" message="true">
		<div class="NexusMessage"><bean:write name="msg" /></div>
		<br />
	</html:messages>
</logic:messagesPresent></center>
