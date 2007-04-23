<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-nested" prefix="nested"%>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/tags/nexus" prefix="nexus"%>
<%@ taglib uri="/tags/struts-html-el" prefix="html-el"%>
<%@page import="java.util.*"%>
<%@page import="org.nexuse2e.configuration.Constants.ParameterType"%>
<%@page import="org.nexuse2e.configuration.*"%>
<%@page import="org.nexuse2e.pojo.ServiceParamPojo"%>

<nexus:helpBar helpDoc="documentation/Service_Listing.htm" />

<html:form action="ServiceUpdate.do" method="POST">
	<html:hidden property="nxServiceId" />
	<html:hidden property="paramsNxComponentId" />
	<html:hidden property="submitted" value="false" />
	<center>

	<table class="NEXUS_TABLE" width="100%">
		<tr>
			<td><nexus:crumbs /></td>
		</tr>
		<tr>
			<td class="NEXUSScreenName">Service</td>
		</tr>
	</table>

	<table class="NEXUS_TABLE" width="100%">
		<tr>
			<td colspan="4" class="NEXUSSection">Service</td>
		</tr>
		<tr>
			<td class="NEXUSName">Name</td>

			<td colspan="3" class="NEXUSValue"><html:text property="name"
				size="80" /></td>
		</tr>
		<tr>
			<td class="NEXUSName">Component</td>

			<td class="NEXUSValue"><logic:equal name="serviceForm"
				property="nxServiceId" value="0">
				<nexus:select name="nxComponentId"
					form="document.forms['serviceForm']"
					onSubmit="document.forms['serviceForm'].action='ServiceAdd.do';"
					submitOnChange="true">
					<nexus:options collection="collection"
						value="${serviceForm.nxComponentId}" property="nxComponentId"
						labelProperty="name" />
				</nexus:select>
			</logic:equal> <logic:notEqual name="serviceForm" property="nxServiceId" value="0">
				<bean:write name="serviceForm" property="componentName" />
			</logic:notEqual></td>
		</tr>

	</table>

	<table class="NEXUS_BUTTON_TABLE">
		<tr>
			<td>&nbsp;</td>
			<logic:notEqual name="serviceForm" property="nxServiceId" value="0">
				<td class="NexusHeaderLink" style="text-align: right;"><nexus:link
					href="ServiceStart.do?nxServiceId=${serviceForm.nxServiceId}"
					styleClass="button">
					<img src="images/submit.gif" class="button">Start</nexus:link></td>
				<td class="NexusHeaderLink" style="text-align: right;"><nexus:link
					href="ServiceStop.do?nxServiceId=${serviceForm.nxServiceId}"
					styleClass="button">
					<img src="images/delete.gif" class="button">Stop</nexus:link></td>
			</logic:notEqual>
		</tr>
	</table>

	<logic:notEmpty name="serviceForm" property="parameters">
		<table class="NEXUS_TABLE" width="100%">
			<tr>
				<td class="NEXUSSection">Name</td>
				<td class="NEXUSSection">Value</td>
				<td class="NEXUSSection">Description</td>

			</tr>
			<logic:iterate id="parameter" property="parameters"
				name="serviceForm">
				<bean:define id="key" value="paramValue(${parameter.paramName})" />
				<logic:equal name="parameter"
					property="parameterDescriptor.parameterType"
					value="<%= ParameterType.STRING.toString() %>">
					<tr>
						<td class="NEXUSValue">${parameter.label}</td>
						<td class="NEXUSValue"><html:text property="${key}" size="50" /></td>
						<td class="NEXUSValue">${parameter.parameterDescriptor.description}</td>
					</tr>
				</logic:equal>
				<logic:equal name="parameter"
					property="parameterDescriptor.parameterType"
					value="<%= ParameterType.PASSWORD.toString() %>">
					<tr>
						<td class="NEXUSValue">${parameter.label}</td>
						<td class="NEXUSValue"><html:password property="${key}"
							size="50" /></td>
						<td class="NEXUSValue">${parameter.parameterDescriptor.description}</td>
					</tr>
				</logic:equal>
				<logic:equal name="parameter"
					property="parameterDescriptor.parameterType"
					value="<%= ParameterType.ENUMERATION.toString() %>">
					<logic:equal name="parameter" property="sequenceNumber" value="0">
						<tr>
							<td class="NEXUSValue" colspan="2"><nexus:submit
								onClick="javascript:document.forms['pipelineForm'].paramName.value='${parameter.paramName}';document.forms['pipelineForm'].submitaction.value='add';">
								<img src="images/submit.gif" name="add">
							</nexus:submit>${parameter.name}</td>

							<td class="NEXUSValue">${parameter.parameterDescriptor.description}</td>
						</tr>
					</logic:equal>
					<logic:greaterThan name="parameter" property="sequenceNumber"
						value="0">
						<bean:define id="valueKey"
							value="paramValue(${parameter.paramName})" />
						<tr>
							<td class="NEXUSValue"><nexus:submit
								onClick="javascript:document.forms['pipelineForm'].paramName.value='${parameter.paramName}';document.forms['pipelineForm'].actionNxId.value=${parameter.sequenceNumber};document.forms['pipelineForm'].submitaction.value='delete';">
								<img src="images/submit.gif" name="delete">
							</nexus:submit> ${parameter.label}</td>
							<td class="NEXUSValue"><html:text property="${valueKey}"
								size="30" /></td>
							<td class="NEXUSValue"></td>
						</tr>
					</logic:greaterThan>
				</logic:equal>

				<logic:equal name="parameter"
					property="parameterDescriptor.parameterType"
					value="<%= ParameterType.LIST.toString() %>">
					<tr>
						<td class="NEXUSValue">${parameter.label}</td>
						<td class="NEXUSValue"><html:select property="${key}">
							<logic:iterate id="element" name="parameter"
								property="parameterDescriptor.defaultValue.elements">
								<html-el:option value="${element.value}">${element.label}</html-el:option>
							</logic:iterate>
						</html:select></td>
						<td class="NEXUSValue">${parameter.parameterDescriptor.description}</td>
					</tr>
				</logic:equal>
				<logic:equal name="parameter"
					property="parameterDescriptor.parameterType"
					value="<%= ParameterType.BOOLEAN.toString() %>">
					<tr>
						<td class="NEXUSValue">${parameter.label}</td>
						<td class="NEXUSValue"><html:checkbox property="<%=key %>" /></td>
						<td class="NEXUSValue">${parameter.parameterDescriptor.description}</td>
					</tr>
				</logic:equal>
				<logic:equal name="parameter"
					property="parameterDescriptor.parameterType"
					value="<%= ParameterType.SERVICE.toString() %>">
					<tr>
						<td class="NEXUSValue">${parameter.label}</td>
						<td class="NEXUSValue"><nexus:select name="<%= key %>">
							<nexus:options collection="collection" value="${parameter.value}"
								property="name" labelProperty="name" />
						</nexus:select></td>
						<td class="NEXUSValue">${parameter.parameterDescriptor.description}</td>
					</tr>
				</logic:equal>
			</logic:iterate>
		</table>
	</logic:notEmpty></center>

	<table class="NEXUS_BUTTON_TABLE">
		<tr>
			<td>&nbsp;</td>
			<td class="NexusHeaderLink" style="text-align: right;"><nexus:submit
				onClick="document.forms['serviceForm'].submitted.value='true';"
				styleClass="button">
				<img src="images/submit.gif" class="button">Save</nexus:submit></td>
			<logic:notEqual name="serviceForm" property="nxServiceId" value="0">
				<td class="NexusHeaderLink" style="text-align: right;"><nexus:link
					href="ServiceDelete.do?nxServiceId=${serviceForm.nxServiceId}"
					styleClass="button">
					<img src="images/delete.gif" class="button">Delete</nexus:link></td>
			</logic:notEqual>
		</tr>
	</table>

</html:form>

