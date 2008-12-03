<%@ taglib uri="/tags/nexus" prefix="nexus"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-nested" prefix="nested"%>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/struts-html-el" prefix="html-el"%>
<% /* <nexus:helpBar /> */ %>

<center>
	
	<table class="NEXUS_TABLE" width="100%">
		<tr>
			<td><nexus:crumbs styleClass="NEXUSScreenPathLink"></nexus:crumbs></td>
		</tr>
		<tr>
			<td class="NEXUSScreenName">Persistent Properties</td>
		</tr>
	</table>

	<html:form action="PersistentProperties.do" method="post">
		<html:hidden property="submitaction" value="unknown" />
		<html:hidden property="nxPersistentPropertyId" value="0" />
		<html:hidden property="namespace" value="unknown" />
		<html:hidden property="version" value="unknown" />
		<html:hidden property="name" value="unknown" />
		<html:hidden property="value" value="unknown" />


		<table class="NEXUS_BUTTON_TABLE" width="100%">
		<tr>
			<td class="button" width="100%">
			<center>
			<logic:greaterThan name="persistentPropertiesForm" property="currentPage" value="0">
				<nexus:submit id="startLink"
					onClick="document.forms['persistentPropertiesForm'].currentPage.value='0';"
					styleClass="NexusHeaderLink">Start</nexus:submit>
			</logic:greaterThan>
			<logic:lessEqual name="persistentPropertiesForm" property="currentPage" value="0">Start
			</logic:lessEqual> | 
			<logic:greaterThan name="persistentPropertiesForm" property="currentPage" value="0">
				<nexus:submit id="previousLink"
					onClick="document.forms['persistentPropertiesForm'].currentPage.value='${persistentPropertiesForm.currentPage - 1}';"
					styleClass="NexusHeaderLink">Previous</nexus:submit>
			</logic:greaterThan>
			<logic:lessEqual name="persistentPropertiesForm" property="currentPage" value="0">Previous
			</logic:lessEqual>
				|
				<nexus:select name="currentPage" form="persistentPropertiesForm" submitOnChange="true">
					<% 
					int pageCount = ((org.nexuse2e.ui.form.PersistentPropertiesForm) session.getAttribute( "persistentPropertiesForm" )).getPageCount();
					for (int i = 0; i < pageCount; i++) {
						pageContext.setAttribute( "index", i ); %>
						<logic:equal name="persistentPropertiesForm" property="currentPage" value="${index}">
							<option value="${index}" selected="selected">Page ${index + 1} of ${persistentPropertiesForm.pageCount}</option>
						</logic:equal>
						<logic:notEqual name="persistentPropertiesForm" property="currentPage" value="${index}">
							<option value="${index}">Page ${index + 1} of ${persistentPropertiesForm.pageCount}</option>
						</logic:notEqual>
					<% } %>
				</nexus:select> 
				|
			<logic:lessThan name="persistentPropertiesForm" property="currentPage" value="${persistentPropertiesForm.pageCount - 1}">
				<nexus:submit id="nextLink" onClick="document.forms['persistentPropertiesForm'].currentPage.value='${persistentPropertiesForm.currentPage + 1}';" styleClass="NexusHeaderLink">Next</nexus:submit>
			</logic:lessThan>
			<logic:greaterEqual name="persistentPropertiesForm" property="currentPage" value="${persistentPropertiesForm.pageCount - 1}">Next</logic:greaterEqual> |
			<logic:lessThan name="persistentPropertiesForm" property="currentPage" value="${persistentPropertiesForm.pageCount - 1}">
				<nexus:submit id="endLink"
					onClick="document.forms['persistentPropertiesForm'].currentPage.value='${persistentPropertiesForm.pageCount - 1}';"
					styleClass="NexusHeaderLink">End</nexus:submit>
			</logic:lessThan>
			<logic:greaterEqual name="persistentPropertiesForm" property="currentPage" value="${persistentPropertiesForm.pageCount - 1}">End</logic:greaterEqual></center>
			</center>
			</td>
		</tr>
		</table>

		<table width="100%">
			<tr>
				<td class="NEXUSName" style="width: 0;">Namespace</td>
				<td class="NEXUSName" style="width: 0;">Version</td>
				<td class="NEXUSName" style="width: 0;">Name</td>
				<td class="NEXUSName" style="width: 0;">Value</td>
				<td class="NEXUSName">&nbsp;</td>
			</tr>
			<tr>
				<td class="NEXUSName" style="width: 0;"><input type="text" size="22" maxlength="128" name="namespace_"/></td>
				<td class="NEXUSName" style="width: 0;"><input type="text" size="5" maxlength="128" name="version_"/></td>
				<td class="NEXUSName" style="width: 0;"><input type="text" size="15" maxlength="128" name="name_"/></td>
				<td class="NEXUSName" style="width: 100%;"><input style="width: 100%;" size="25" type="text" maxlength="128" name="value_"/></td>
				<td class="NEXUSName">
				
				<nexus:submit
					onClick="document.forms['persistentPropertiesForm'].submitaction.value='add'; 
							document.forms['persistentPropertiesForm'].nxPersistentPropertyId.value='${persistentProperty.nxPersistentPropertyId}';
							document.forms['persistentPropertiesForm'].namespace.value=document.getElementsByName('namespace_')[0].value;
							document.forms['persistentPropertiesForm'].version.value=document.getElementsByName('version_')[0].value;
							document.forms['persistentPropertiesForm'].name.value=document.getElementsByName('name_')[0].value;
							document.forms['persistentPropertiesForm'].value.value=document.getElementsByName('value_')[0].value;
							">
					<img src="images/icons/add.png" class="button" alt="Configure" id="addProperty"><span dojoType="tooltip" connectId="addProperty" toggle="explode">Add Property</span>
				</nexus:submit>
				</td>
			</tr>
		</table>

<center><logic:messagesPresent>
<div class="NexusError"><html:errors /></div>
</logic:messagesPresent> <logic:messagesPresent message="true">
<html:messages id="msg" message="true">
<div class="NexusMessage"><bean:write name="msg" /></div>
<br />
</html:messages>
</logic:messagesPresent></center>


	 
	<table width="100%">
		<logic:iterate id="persistentProperty" name="collection" indexId="counter">
			<tr> 
				<td class="NEXUSName" style="width: 0;"><input type="text" size="22" maxlength="128" name="namespace${counter}" value="${persistentProperty.namespace}"/></td>
				<td class="NEXUSName" style="width: 0;"><input type="text" size="5" maxlength="128" name="version${counter}" value="${persistentProperty.version}"/></td>
				<td class="NEXUSName" style="width: 0;"><input type="text" size="15" maxlength="128" name="name${counter}" value="${persistentProperty.name}"/></td>
				<td class="NEXUSName" style="width: 100%;"><input type="text" style="width: 100%;" size="25" maxlength="128" name="value${counter}" value="${persistentProperty.value}"/></td>
				<td class="NEXUSName">
				<nexus:submit
					onClick="document.forms['persistentPropertiesForm'].submitaction.value='delete'; 
							document.forms['persistentPropertiesForm'].nxPersistentPropertyId.value='${persistentProperty.nxPersistentPropertyId}';
							document.forms['persistentPropertiesForm'].namespace.value=document.getElementsByName('namespace${counter}')[0].value;
							document.forms['persistentPropertiesForm'].version.value=document.getElementsByName('version${counter}')[0].value;
							document.forms['persistentPropertiesForm'].name.value=document.getElementsByName('name${counter}')[0].value;
							document.forms['persistentPropertiesForm'].value.value=document.getElementsByName('value${counter}')[0].value;
							">
					<img src="images/icons/delete.png" class="button" alt="Configure" id="addProperty"><span dojoType="tooltip" connectId="addProperty" toggle="explode">Delete Property</span>
				</nexus:submit>
				<nexus:submit
					onClick="document.forms['persistentPropertiesForm'].submitaction.value='update'; 
							document.forms['persistentPropertiesForm'].nxPersistentPropertyId.value='${persistentProperty.nxPersistentPropertyId}';
							document.forms['persistentPropertiesForm'].namespace.value=document.getElementsByName('namespace${counter}')[0].value;
							document.forms['persistentPropertiesForm'].version.value=document.getElementsByName('version${counter}')[0].value;
							document.forms['persistentPropertiesForm'].name.value=document.getElementsByName('name${counter}')[0].value;
							document.forms['persistentPropertiesForm'].value.value=document.getElementsByName('value${counter}')[0].value;
							">
					<img src="images/icons/tick.png" class="button" alt="Configure" id="addProperty"><span dojoType="tooltip" connectId="addProperty" toggle="explode">Update Property</span>
				</nexus:submit>
				</td>
			</tr>
		</logic:iterate>
	</table>


	</html:form>

</center>
