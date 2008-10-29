<%@ taglib uri="/tags/nexus" prefix="nexus"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-nested" prefix="nested"%>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/struts-html-el" prefix="html-el"%>

<% /*<nexus:helpBar helpDoc="documentation/Collaboration_Partners.htm" /> */ %>

<logic:equal name="pipelineForm" property="frontend" value="true">
<script type="text/javascript">
function setBidirectional() {
  b = document.forms['pipelineForm'].bidirectional.value;
  document.getElementById("returnPipelets").visible = b;
}
</script>
</logic:equal>

<table class="NEXUS_TABLE" width="100%">
	<tr>
		<td><nexus:crumbs styleClass="NEXUSScreenPathLink"></nexus:crumbs></td>
	</tr>
	<tr>
		<td class="NEXUSScreenName">Update Pipeline</td>
	</tr>
</table>
<html:form action="PipelineUpdate.do">
	<html:hidden property="submitaction" value="unknown" />
	<html:hidden property="sortaction" value="unknown" />
	<html:hidden property="sortingDirection" value="0" />
	<table class="NEXUS_TABLE" width="100%">
		<tr>
			<td class="NEXUSName">Name</td>
			<td class="NEXUSValue"><html:text property="name" size="50" /></td>
		</tr>

		<tr>
			<td class="NEXUSName">Direction</td>
			<td class="NEXUSValue"><html:select property="direction">
				<html-el:option value="0">Inbound</html-el:option>
				<html-el:option value="1">Outbound</html-el:option>
			</html:select></td>
		</tr>

		<logic:equal name="pipelineForm" property="frontend" value="true">
			<tr>
				<td class="NEXUSName">TRP</td>
				<td class="NEXUSValue"><html:select property="nxTrpId">
					<logic:iterate id="trp" property="trps" name="pipelineForm">
						<html-el:option value="${trp.nxTRPId}">${trp.protocol}-${trp.version}-${trp.transport}</html-el:option>
					</logic:iterate>
				</html:select></td>
			</tr>
		</logic:equal>

		<tr>
			<td class="NEXUSName">Description</td>
			<td class="NEXUSValue"><html:text property="description" size="50" /></td>
		</tr>

		<logic:equal name="pipelineForm" property="frontend" value="true">
			<tr>
				<td class="NEXUSName">Bidirectional</td>
				<td class="NEXUSValue"><html-el:checkbox property="bidirectional" disabled="${pipelineForm.bidirectional}" onclick="var elem = document.getElementById('bidirectionalOnly'); if (document.forms['pipelineForm'].bidirectional.checked) { elem.style.visibility = 'visible' } else { elem.style.visibility = 'hidden' };"/></td>
			</tr>
		</logic:equal>

	</table>

	<table class="NEXUS_TABLE" width="100%">
		<tr>
			<td class="NEXUSSection">Name</td>
			<td class="NEXUSSection">Description</td>
			<td class="NEXUSSection"></td>
		</tr>
		<tr>
			<td class="NEXUSName" colspan="3">
			<logic:equal name="pipelineForm" property="direction" value="0"> <%/* inbound */ %>
				<logic:equal name="pipelineForm" property="frontend" value="true">
				Partner system
				</logic:equal>
				<logic:notEqual name="pipelineForm" property="frontend" value="true">
				NEXUSe2e frontend
				</logic:notEqual>
			</logic:equal>
			<logic:equal name="pipelineForm" property="direction" value="1"> <%/* outbound */ %>
				<logic:equal name="pipelineForm" property="frontend" value="true">
				NEXUSe2e backend
				</logic:equal>
				<logic:notEqual name="pipelineForm" property="frontend" value="true">
				Backend system
				</logic:notEqual>
			</logic:equal>
			</td>
		</tr>

		<logic:iterate id="pipelet" name="pipelineForm" property="forwardPipelets" indexId="index">
			<tr>
				<td class="NEXUSName">${index + 1}. <bean:write
					name="pipelet" property="name" /></td>
				<td class="NEXUSName"><bean:write name="pipelet"
					property="description" /></td>
				<td class="NEXUSName">
				<logic:greaterThan name="index" value="0">
				  <nexus:submit
					onClick="document.forms[0].sortaction.value=${index};document.forms[0].submitaction.value='sort';document.forms['pipelineForm'].sortingDirection.value=1;">
					<img src="images/icons/bullet_arrow_up.png" class="button" alt="Move up"
						id="moveUp"><span dojoType="tooltip" connectId="moveUp" toggle="explode">Move up</span>
				</nexus:submit></logic:greaterThan>
				<logic:lessEqual name="index" value="0">
					<img src="images/icons/bullet_arrow_up.png" class="button">
				</logic:lessEqual>
				<logic:lessThan name="index" value="${pipelineForm.forwardPipeletCount - 1}">
				<nexus:submit
					onClick="document.forms[0].sortaction.value=${index};document.forms[0].submitaction.value='sort';document.forms['pipelineForm'].sortingDirection.value=2;">
					<img src="images/icons/bullet_arrow_down.png" class="button" value="Submit"
						alt="Move down" id="moveDown"><span dojoType="tooltip" connectId="moveDown" toggle="explode">Move down</span>
				</nexus:submit>
				</logic:lessThan>
				<logic:greaterEqual name="index" value="${pipelineForm.forwardPipeletCount - 1}">
					<img src="images/icons/bullet_arrow_down.png" class="button" value="Submit" alt="Move down" id="moveDown">
				</logic:greaterEqual>
				<nexus:submit
					onClick="document.forms[0].sortaction.value=${index};document.forms[0].submitaction.value='delete';">
					<img src="images/icons/delete.png" class="button" alt="Delete" id="deletePipelet"><span dojoType="tooltip" connectId="deletePipelet" toggle="explode">Delete Pipelet</span>
				</nexus:submit> 
				<logic:notEqual name="pipelet" property="nxPipeletId" value="0">
				<nexus:submit
					onClick="document.forms[0].sortaction.value=${index};document.forms[0].submitaction.value='config';">
					<img src="images/icons/brick_edit.png" class="button" alt="Configure" id="configurePipelet"><span dojoType="tooltip" connectId="configurePipelet" toggle="explode">Configure Pipelet</span>
				</nexus:submit>
				</logic:notEqual>
				</td>
			</tr>
		</logic:iterate>

		<tr>
			<td class="NEXUSSection" colspan="2"><html:select
				property="actionNxId">
				<logic:iterate id="component" property="availableTemplates"
					name="pipelineForm">
					<html-el:option value="${component.nxComponentId}">${component.name}</html-el:option>
				</logic:iterate>
			</html:select> <nexus:submit
				onClick="document.forms['pipelineForm'].submitaction.value='add';" form="document.forms['pipelineForm']">
				<img src="images/icons/add.png" class="button">
			</nexus:submit></td>
			<td class="NEXUSSection"></td>
		</tr>
		<tr>
			<td class="NEXUSName" colspan="3">
			<logic:equal name="pipelineForm" property="direction" value="0"> <%/* inbound */ %>
				<logic:equal name="pipelineForm" property="frontend" value="true">
				NEXUSe2e backend
				</logic:equal>
				<logic:notEqual name="pipelineForm" property="frontend" value="true">
				Backend system
				</logic:notEqual>
			</logic:equal>
			<logic:equal name="pipelineForm" property="direction" value="1"> <%/* outbound */ %>
				<logic:equal name="pipelineForm" property="frontend" value="true">
				Partner system
				</logic:equal>
				<logic:notEqual name="pipelineForm" property="frontend" value="true">
				NEXUSe2e frontend
				</logic:notEqual>
			</logic:equal>
			</td>
		</tr>
	</table>
		
	<logic:equal name="pipelineForm" property="frontend" value="true">
		<logic:equal name="pipelineForm" property="bidirectional" value="true">
			<div id="bidirectionalOnly" style="visibility: visible">
		</logic:equal>
		<logic:notEqual name="pipelineForm" property="bidirectional" value="true">
			<div id="bidirectionalOnly" style="visibility: hidden">
		</logic:notEqual>

		<table class="NEXUS_TABLE" width="100%">
			<tr>
				<td class="NEXUSName" colspan="3">
				<logic:equal name="pipelineForm" property="direction" value="0"> <%/* inbound */ %>
					NEXUSe2e backend
				</logic:equal>
				<logic:equal name="pipelineForm" property="direction" value="1"> <%/* outbound */ %>
					Partner system
				</logic:equal>
				</td>
			</tr>

			<logic:iterate id="pipelet" name="pipelineForm" property="returnPipelets" indexId="index">
				<tr>
					<td class="NEXUSName">${index + 1}. <bean:write
						name="pipelet" property="name" /></td>
					<td class="NEXUSName"><bean:write name="pipelet"
						property="description" /></td>
					<td class="NEXUSName">
					<logic:greaterThan name="index" value="0">
					  <nexus:submit
						onClick="document.forms[0].sortaction.value=${index};document.forms[0].submitaction.value='sortReturn';document.forms['pipelineForm'].sortingDirection.value=1;">
						<img src="images/icons/bullet_arrow_up.png" class="button" alt="Move up"
							id="moveUpReturn"><span dojoType="tooltip" connectId="moveUp" toggle="explode">Move up</span>
					</nexus:submit></logic:greaterThan>
					<logic:lessEqual name="index" value="0">
						<img src="images/icons/bullet_arrow_up.png" class="button">
					</logic:lessEqual>
					<logic:lessThan name="index" value="${pipelineForm.returnPipeletCount - 1}">
					<nexus:submit
						onClick="document.forms[0].sortaction.value=${index};document.forms[0].submitaction.value='sortReturn';document.forms['pipelineForm'].sortingDirection.value=2;">
						<img src="images/icons/bullet_arrow_down.png" class="button" value="Submit"
							alt="Move down" id="moveDown"><span dojoType="tooltip" connectId="moveDownReturn" toggle="explode">Move down</span>
					</nexus:submit>
					</logic:lessThan>
					<logic:greaterEqual name="index" value="${pipelineForm.returnPipeletCount - 1}">
						<img src="images/icons/bullet_arrow_down.png" class="button" value="Submit" alt="Move down" id="moveDownReturn">
					</logic:greaterEqual>
					<nexus:submit
						onClick="document.forms[0].sortaction.value=${index};document.forms[0].submitaction.value='deleteReturn';">
						<img src="images/icons/delete.png" class="button" alt="Delete" id="deletePipelet"><span dojoType="tooltip" connectId="deletePipelet" toggle="explode">Delete Pipelet</span>
					</nexus:submit> 
					<logic:notEqual name="pipelet" property="nxPipeletId" value="0">
					<nexus:submit
						onClick="document.forms[0].sortaction.value=${index};document.forms[0].submitaction.value='config';">
						<img src="images/icons/brick_edit.png" class="button" alt="Configure" id="configurePipelet"><span dojoType="tooltip" connectId="configurePipelet" toggle="explode">Configure Pipelet</span>
					</nexus:submit>
					</logic:notEqual>
					</td>
				</tr>
			</logic:iterate>
	
			<tr>
				<td class="NEXUSSection" colspan="2"><html:select
					property="actionNxIdReturn">
					<logic:iterate id="component" property="availableTemplates"
						name="pipelineForm">
						<html-el:option value="${component.nxComponentId}">${component.name}</html-el:option>
					</logic:iterate>
				</html:select> <nexus:submit
					onClick="document.forms['pipelineForm'].submitaction.value='addReturn';" form="document.forms['pipelineForm']">
					<img src="images/icons/add.png" class="button">
				</nexus:submit></td>
				<td class="NEXUSSection"></td>
			</tr>
			<tr>
				<td class="NEXUSName" colspan="3">
				<logic:equal name="pipelineForm" property="direction" value="0"> <%/* inbound */ %>
					Partner system
				</logic:equal>
				<logic:equal name="pipelineForm" property="direction" value="1"> <%/* outbound */ %>
					NEXUSe2e backend
				</logic:equal>
				</td>
			</tr>
		</table>
	</div>
	</logic:equal>



	<table class="NEXUS_BUTTON_TABLE">
		<tr>
			<td>&nbsp;</td>
			<td class="NexusHeaderLink" style="text-align: right;"><nexus:submit
				onClick="document.forms['pipelineForm'].submitaction.value='update';"
				styleClass="button">
				<img src="images/icons/tick.png" class="button">Save</nexus:submit></td>
			<td class="NexusHeaderLink" style="text-align: right;"><nexus:link
				href="PipelineDelete.do?nxPipelineId=${pipelineForm.nxPipelineId}"
				styleClass="button">
				<img src="images/icons/delete.png" class="button">Delete</nexus:link></td>
		</tr>
	</table>

</html:form>
