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
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/struts-html-el" prefix="html-el"%>

<nexus:fileUploadResponse>
<% /* <nexus:helpBar /> */ %>

<script language="JavaScript" type="text/javascript">
	this.clearConvId = function () {
		document.messageSubmissionForm.conversationId.value='';
	}
</script>

<center>
	<table class="NEXUS_TABLE" width="100%">
		<tr>
			<td><nexus:crumbs styleClass="NEXUSScreenPathLink"></nexus:crumbs></td>
		</tr>
		<tr>
			<td class="NEXUSScreenName">Message Submission</td>
		</tr>
	</table>

<html:form action="MessageSubmission.do" method="post" enctype="multipart/form-data">
	<table width="100%">
		<tr>
			<td colspan="2" class="NEXUSSection">Parameters for submitting a Message</td>
		</tr>

		<tr>
			<td class="NEXUSName">Choreography ID</td>
			<td class="NEXUSValue">
			
				<nexus:select name="choreographyId" submitOnChange="true" form="messageSubmissionForm" sendFileForm="true">
					<logic:iterate name="messageSubmissionForm" property="choreographies" id="choreography">
					<logic:equal name="messageSubmissionForm" property="choreographyId"
						value="${choreography}">
						<option value="${choreography}" selected="selected">${choreography}</option>
					</logic:equal>
					<logic:notEqual name="messageSubmissionForm"
						property="choreographyId" value="${choreography}">
						<option value="${choreography}">${choreography}</option>
					</logic:notEqual>
					</logic:iterate>
				</nexus:select>

			</td>
		</tr>

		<tr>
			<td class="NEXUSName">Action</td>
			<td class="NEXUSValue"><html:select property="actionId">
				<html:options property="actions" labelProperty="actions" />
			</html:select></td>
		</tr>

		<tr>
			<td class="NEXUSName">Receiver</td>
			<td class="NEXUSValue"><html:select property="receiver">
				<logic:iterate name="messageSubmissionForm" property="receivers"
					id="partner">
					<html-el:option value="${partner.nxPartnerId}">${partner.partnerId} (${partner.name})</html-el:option>
				</logic:iterate>
			</html:select></td>
		</tr>

		<tr>
			<td class="NEXUSName">File (XML/text)<br />
			- or -<br />
			Primary Key</td>
			<td class="NEXUSValue"><html:file size="50" property="payloadFile1" onkeypress="return checkKey(event);" /><br />
			&nbsp;<br />
			<html:text property="primaryKey" size="50" /></td>
		</tr>

		<tr>
			<td class="NEXUSName">Conversation ID</td>
			<td class="NEXUSValue"><html:text property="conversationId" size="50" onkeypress="return checkKey(event);" /> <a href="#"
				onClick="javascript:clearConvId()" class="button">Clear</a>
			</td>
		</tr>

		<tr>
			<td class="NEXUSName">Repeat</td>
			<td class="NEXUSValue"><html:text property="repeat" size="5" onkeypress="return checkKey(event);"/></td>
		</tr>
		<tr>
			<td class="NEXUSName">Encoding</td>
			<td class="NEXUSValue">
			<html:select property="encoding">
			<html:options property="encodings" labelProperty="encodings" />
			</html:select>
			</td>
		</tr>
  </table>

	<table class="NEXUS_BUTTON_TABLE" width="100%">
		<tr>
			<td class="BUTTON_RIGHT"><input name="Submit" value="blank"
				type="hidden"> <nexus:submit
				onClick="document.messageSubmissionForm.Submit.value='Submit';"
				sendFileForm="true" styleClass="button">
				<img src="images/icons/tick.png" name="SubmitButton" class="button">Execute</nexus:submit>
			</td>
		</tr>
	</table>
</html:form>
</center>
<center><logic:messagesPresent>
	<div class="NexusError"><html:errors /></div>
</logic:messagesPresent> <logic:messagesPresent message="true">
	<html:messages id="msg" message="true">
		<div class="NexusMessage"><bean:write name="msg" /></div>
		<br />
	</html:messages>
</logic:messagesPresent></center>
</nexus:fileUploadResponse>
