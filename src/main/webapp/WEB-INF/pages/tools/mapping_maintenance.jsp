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
			<td class="NEXUSScreenName">Mapping Maintenance</td>
		</tr>
	</table>

	<html:form action="MappingMaintenance.do" method="post">
		<html:hidden property="submitaction" value="unknown" />
		<html:hidden property="nxMappingId" value="unknown" />
		<html:hidden property="category" value="unknown" />
		<html:hidden property="leftType" value="unknown" />
		<html:hidden property="leftValue" value="unknown" />
		<html:hidden property="rightType" value="unknown" />
		<html:hidden property="rightValue" value="unknown" />

		<table class="NEXUS_BUTTON_TABLE" width="100%">
		<tr>
			<td class="button" width="100%">
			<center>
			<logic:greaterThan name="mappingMaintenanceForm" property="currentPage" value="0">
				<nexus:submit id="startLink"
					onClick="document.forms['mappingMaintenanceForm'].currentPage.value='0';"
					styleClass="NexusHeaderLink">Start</nexus:submit>
			</logic:greaterThan>
			<logic:lessEqual name="mappingMaintenanceForm" property="currentPage" value="0">Start
			</logic:lessEqual> | 
			<logic:greaterThan name="mappingMaintenanceForm" property="currentPage" value="0">
				<nexus:submit id="previousLink"
					onClick="document.forms['mappingMaintenanceForm'].currentPage.value='${mappingMaintenanceForm.currentPage - 1}';"
					styleClass="NexusHeaderLink">Previous</nexus:submit>
			</logic:greaterThan>
			<logic:lessEqual name="mappingMaintenanceForm" property="currentPage" value="0">Previous
			</logic:lessEqual>
				|
				<nexus:select name="currentPage" form="mappingMaintenanceForm" submitOnChange="true">
					<% 
					int pageCount = ((org.nexuse2e.ui.form.MappingMaintenanceForm) session.getAttribute( "mappingMaintenanceForm" )).getPageCount();
					for (int i = 0; i < pageCount; i++) {
						pageContext.setAttribute( "index", i ); %>
						<logic:equal name="mappingMaintenanceForm" property="currentPage" value="${index}">
							<option value="${index}" selected="selected">Page ${index + 1} of ${mappingMaintenanceForm.pageCount}</option>
						</logic:equal>
						<logic:notEqual name="mappingMaintenanceForm" property="currentPage" value="${index}">
							<option value="${index}">Page ${index + 1} of ${mappingMaintenanceForm.pageCount}</option>
						</logic:notEqual>
					<% } %>
				</nexus:select> 
				|
			<logic:lessThan name="mappingMaintenanceForm" property="currentPage" value="${mappingMaintenanceForm.pageCount - 1}">
				<nexus:submit id="nextLink" onClick="document.forms['mappingMaintenanceForm'].currentPage.value='${mappingMaintenanceForm.currentPage + 1}';" styleClass="NexusHeaderLink">Next</nexus:submit>
			</logic:lessThan>
			<logic:greaterEqual name="mappingMaintenanceForm" property="currentPage" value="${mappingMaintenanceForm.pageCount - 1}">Next</logic:greaterEqual> |
			<logic:lessThan name="mappingMaintenanceForm" property="currentPage" value="${mappingMaintenanceForm.pageCount - 1}">
				<nexus:submit id="endLink"
					onClick="document.forms['mappingMaintenanceForm'].currentPage.value='${mappingMaintenanceForm.pageCount - 1}';"
					styleClass="NexusHeaderLink">End</nexus:submit>
			</logic:lessThan>
			<logic:greaterEqual name="mappingMaintenanceForm" property="currentPage" value="${mappingMaintenanceForm.pageCount - 1}">End</logic:greaterEqual></center>
			</center>
			</td>
		</tr>
		</table>


		<table width="100%">
			<tr>
				<td class="NEXUSName">Category</td>
				<td class="NEXUSName">Type</td>
				<td class="NEXUSName">Value</td>
				<td class="NEXUSName"> &lt;-&gt; </td>
				<td class="NEXUSName">Type</td>
				<td class="NEXUSName">Value</td>
				<td class="NEXUSName">Actions</td>
			</tr>
			<tr>
				<td class="NEXUSName"><input type="text" size="20" name="htmlnewcategory"/></td>
				<td  class="NEXUSName">
				<select name="htmlnewleftType">
					<logic:iterate id="type" name="mappingMaintenanceForm" property="typenames">
						<option value="${type}">${type}</option>
					</logic:iterate>
				</select>
				
				</td>
				<td class="NEXUSName"><input type="text" size="20" name="htmlnewleftValue"/></td>
				<td class="NEXUSName"></td>
				
				<td  class="NEXUSName">
				<select name="htmlnewrightType">
					<logic:iterate id="type" name="mappingMaintenanceForm" property="typenames">
						<option value="${type}">${type}</option>
					</logic:iterate>
				</select>
				
				</td>
				<td class="NEXUSName"><input type="text" size="20" name="htmlnewrightValue"/></td>
				<td class="NEXUSName">
				
				<nexus:submit
					onClick="document.forms[0].submitaction.value='add'; 
							document.forms[0].nxMappingId.value='${mapping.nxMappingId}';
							document.forms[0].category.value=document.getElementsByName('htmlnewcategory${counter}')[0].value;
							document.forms[0].leftType.value=document.getElementsByName('htmlnewleftType${counter}')[0].value;
							document.forms[0].leftValue.value=document.getElementsByName('htmlnewleftValue${counter}')[0].value;
							document.forms[0].rightType.value=document.getElementsByName('htmlnewrightType${counter}')[0].value;
							document.forms[0].rightValue.value=document.getElementsByName('htmlnewrightValue${counter}')[0].value;
							">
					<img src="images/icons/add.png" class="button" alt="Configure" id="addMapping"><span dojoType="dijit.Tooltip" connectId="addMapping" toggle="explode">Add Mapping Entry</span>
				</nexus:submit>
				</td>
			</tr>
		</table>
	
	 
	<table width="100%">
		<logic:iterate id="mapping" name="collection" indexId="counter">
			<tr> 
				<td class="NEXUSName"><input type="text" size="20" name="htmlcategory${counter}" value="${mapping.category}"/></td>
				<td  class="NEXUSName">
				<select name="htmlleftType${counter}" value="${mapping.leftType}">
					<logic:iterate indexId="count" id="type" name="mappingMaintenanceForm" property="typenames">
						<c:choose>
							<c:when test="${count == mapping.leftType}">
								<option selected="selected" value="${count}">${type}</option>
							</c:when>
							<c:otherwise>
								<option value="${count}">${type}</option>
							</c:otherwise>
						</c:choose>
						
					</logic:iterate>
				</select>
				
				</td>
				<td class="NEXUSName"><input type="text" size="20" name="htmlleftValue${counter}" value="${mapping.leftValue}"/></td>
				<td class="NEXUSName"></td>
				
				<td  class="NEXUSName">
				<select name="htmlrightType${counter}" value="${mapping.rightType}">
					<logic:iterate indexId="count" id="type" name="mappingMaintenanceForm" property="typenames">
					<c:choose>
					<c:when test="${count == mapping.rightType}">
						<option selected="selected" value="${count}">${type}</option>
					</c:when>
					<c:otherwise>
						<option value="${count}">${type}</option>
					</c:otherwise>
					</c:choose>
					</logic:iterate>
				</select>
				</td>
				<td class="NEXUSName"><input type="text" size="20" name="htmlrightValue${counter}" value="${mapping.rightValue}"/></td>
				<td class="NEXUSName">
				<nexus:submit
					onClick="document.forms[0].submitaction.value='delete'; 
							document.forms[0].nxMappingId.value='${mapping.nxMappingId}';
							document.forms[0].category.value=document.getElementsByName('htmlcategory${counter}')[0].value;
							document.forms[0].leftType.value=document.getElementsByName('htmlleftType${counter}')[0].value;
							document.forms[0].leftValue.value=document.getElementsByName('htmlleftValue${counter}')[0].value;
							document.forms[0].rightType.value=document.getElementsByName('htmlrightType${counter}')[0].value;
							document.forms[0].rightValue.value=document.getElementsByName('htmlrightValue${counter}')[0].value;
							">
					<img src="images/icons/delete.png" class="button" alt="Delete" id="deleteMapping"><span dojoType="dijit.Tooltip" connectId="deleteMapping" toggle="explode">Delete Mapping Entry</span>
				</nexus:submit> 
				<nexus:submit
					onClick="document.forms[0].submitaction.value='update';
							document.forms[0].nxMappingId.value='${mapping.nxMappingId}';
							document.forms[0].category.value=document.getElementsByName('htmlcategory${counter}')[0].value;
							document.forms[0].leftType.value=document.getElementsByName('htmlleftType${counter}')[0].value;
							document.forms[0].leftValue.value=document.getElementsByName('htmlleftValue${counter}')[0].value;
							document.forms[0].rightType.value=document.getElementsByName('htmlrightType${counter}')[0].value;
							document.forms[0].rightValue.value=document.getElementsByName('htmlrightValue${counter}')[0].value;
					">
					<img src="images/icons/tick.png" class="button" alt="Configure" id="updateMapping"><span dojoType="dijit.Tooltip" connectId="updateMapping" toggle="explode">Update Mapping Entry</span>
				</nexus:submit>
				</td>
			</tr>
		</logic:iterate>
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
