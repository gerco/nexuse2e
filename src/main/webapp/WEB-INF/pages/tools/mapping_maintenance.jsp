<%@ taglib uri="/tags/nexus" prefix="nexus"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-nested" prefix="nested"%>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/struts-html-el" prefix="html-el"%>

<nexus:helpBar />

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
				<select name="leftType">
					<logic:iterate id="type" name="mappingMaintenanceForm" property="typenames">
						<option value="${type}">${type}</option>
					</logic:iterate>
				</select>
				
				</td>
				<td class="NEXUSName"><input type="text" size="20" name="leftValue"/></td>
				<td class="NEXUSName"></td>
				
				<td  class="NEXUSName">
				<select name="rightType">
					<logic:iterate id="type" name="mappingMaintenanceForm" property="typenames">
						<option value="${type}">${type}</option>
					</logic:iterate>
				</select>
				
				</td>
				<td class="NEXUSName"><input type="text" size="20" name="rightValue"/></td>
				<td class="NEXUSName">
				<nexus:submit
					onClick="document.forms[0].submitaction.value='add';">
					<img src="images/submit.gif" class="button" alt="Configure" id="addMapping"><span dojoType="tooltip" connectId="addMapping" toggle="explode">Add Mapping Entry</span>
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
					<logic:iterate id="type" name="mappingMaintenanceForm" property="typenames">
						<option value="${type}">${type}</option>
					</logic:iterate>
				</select>
				
				</td>
				<td class="NEXUSName"><input type="text" size="20" name="htmlleftValue${counter}" value="${mapping.leftValue}"/></td>
				<td class="NEXUSName"></td>
				
				<td  class="NEXUSName">
				<select name="htmlrightType${counter}" value="${mapping.rightType}">
					<logic:iterate id="type" name="mappingMaintenanceForm" property="typenames">
						<option value="${type}">${type}</option>
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
					<img src="images/delete.gif" class="button" alt="Delete" id="deleteMapping"><span dojoType="tooltip" connectId="deleteMapping" toggle="explode">Delete Mapping Entry</span>
				</nexus:submit> 
				<nexus:submit
					onClick="document.forms[0].submitaction.value='update';">
					<img src="images/submit.gif" class="button" alt="Configure" id="updateMapping"><span dojoType="tooltip" connectId="updateMapping" toggle="explode">Update Mapping Entry</span>
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
