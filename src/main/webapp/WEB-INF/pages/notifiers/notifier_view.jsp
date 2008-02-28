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
<%@page import="org.nexuse2e.pojo.LoggerParamPojo"%>

<% /*<nexus:helpBar helpDoc="documentation/Notifier_Listing.htm" /> */ %>

<html:form action="NotifierUpdate.do" method="POST">
	<html:hidden property="choreographyId" />
	<html:hidden property="nxLoggerId" />
	<html:hidden property="paramsNxComponentId" />
	<html:hidden property="submitted" value="false" />
	<center>

	<table class="NEXUS_TABLE" width="100%">
		<tr>
			<td><nexus:crumbs /></td>
		</tr>
		<tr>
			<td class="NEXUSScreenName">Logger</td>
		</tr>
	</table>

	<table class="NEXUS_TABLE" width="100%">
		<tr>
			<td colspan="4" class="NEXUSSection">Logger</td>
		</tr>
		<tr>
			<td class="NEXUSName">Name</td>

			<td colspan="3" class="NEXUSValue"><html:text property="name"
				size="60" /></td>
		</tr>
		<tr>
			<td class="NEXUSName">Component</td>

			<td class="NEXUSValue"><logic:equal name="loggerForm"
				property="nxLoggerId" value="0">
				<nexus:select name="nxComponentId"
					form="document.forms['loggerForm']"
					onSubmit="document.forms['loggerForm'].action='NotifierAdd.do';"
					submitOnChange="true">
					<nexus:options collection="collection"
						value="${loggerForm.nxComponentId}" property="nxComponentId"
						labelProperty="name" />
				</nexus:select>
			</logic:equal> <logic:notEqual name="loggerForm" property="nxLoggerId" value="0">
				<bean:write name="loggerForm" property="componentName" />
			</logic:notEqual></td>


			<td class="NEXUSName">Threshold</td>

			<td class="NEXUSValue"><html:select property="threshold">
				<html:option value="5000">Trace</html:option>
				<html:option value="10000">Debug</html:option>
				<html:option value="20000">Info</html:option>
				<html:option value="30000">Warning</html:option>
				<html:option value="40000">Error</html:option>
				<html:option value="50000">Fatal</html:option>
			</html:select></td>
		</tr>

		<%
		            boolean odd = false;
		            String name = null;
		%>

		<logic:iterate id="groupName" property="groupNames" name="loggerForm">
			<%
			            String key = "logFilterValue(group_" + groupName + ")";

			            if ( !odd ) {
			%>
			<tr>
				<%
				}
				%>
				<td class="NEXUSValue"><%=groupName%></td>
				<td class="NEXUSValue"><html:checkbox property="<%=key %>"
					value="true" /></td>
				<%
				if ( odd ) {
				%>
			</tr>
			<%
			            }
			            odd = !odd;
			%>
		</logic:iterate>
		<tr>
			<td class="NEXUSName">JavaPackagePattern</td>

			<td colspan="3" class="NEXUSValue"><html:text
				property="filterJavaPackagePattern" size="60" /></td>
		</tr>
	</table>
	<logic:notEmpty name="loggerForm" property="parameters">
		<table class="NEXUS_TABLE" width="100%">
			<tr>
				<td class="NEXUSSection">Name</td>
				<td class="NEXUSSection">Value</td>
				<td class="NEXUSSection">Description</td>

			</tr>
			<logic:iterate id="parameter" property="parameters" name="loggerForm">
				<bean:define id="key" value="paramValue(${parameter.paramName})" />
				<logic:equal name="parameter"
					property="parameterDescriptor.parameterType"
					value="<%= ParameterType.STRING.toString() %>">
					<tr>
						<td class="NEXUSValue">${parameter.label}</td>
						<td class="NEXUSValue"><html:text property="${key}" size="30" /></td>
						<td class="NEXUSValue">${parameter.parameterDescriptor.description}</td>
					</tr>
				</logic:equal>
				<logic:equal name="parameter"
					property="parameterDescriptor.parameterType"
					value="<%= ParameterType.PASSWORD.toString() %>">
					<tr>
						<td class="NEXUSValue">${parameter.label}</td>
						<td class="NEXUSValue"><html:password property="${key}"
							size="30" /></td>
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
								<img src="images/icons/tick.png" name="add">
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
								<img src="images/icons/tick.png" name="delete">
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
						<td class="NEXUSValue"><nexus:select name="${key}">
							<logic:iterate id="service" name="service_collection">
								<%
								org.nexuse2e.pojo.LoggerParamPojo prm = (org.nexuse2e.pojo.LoggerParamPojo) pageContext.getAttribute( "parameter" );
								org.nexuse2e.pojo.ServicePojo srv = (org.nexuse2e.pojo.ServicePojo) pageContext.getAttribute( "service" );
								if (!(prm.getParameterDescriptor().getDefaultValue() instanceof Class) ||
								        (srv.getComponent() != null
								                && srv.getComponent().isSubtypeOf( (Class<?>) prm.getParameterDescriptor().getDefaultValue() ))) {
								%>
								<nexus:option name="service" value="${parameter.value}" property="name" labelProperty="name" />
								<%
								}
								%>
							</logic:iterate>
						</nexus:select></td>
						<td class="NEXUSValue">${parameter.parameterDescriptor.description}</td>
					</tr>
				</logic:equal>
			</logic:iterate>
		</table>
	</logic:notEmpty>
	<table class="NEXUS_BUTTON_TABLE">
		<tr>
			<td>&nbsp;</td>
			<td class="NexusHeaderLink" style="text-align: right;"><nexus:submit
				onClick="document.forms['loggerForm'].submitted.value='true';"
				styleClass="button">
				<img src="images/icons/tick.png" class="button">Save</nexus:submit></td>
			<logic:notEqual name="loggerForm" property="nxLoggerId" value="0">
				<td class="NexusHeaderLink" style="text-align: right;"><nexus:link
					href="NotifierDelete.do?nxLoggerId=${loggerForm.nxLoggerId}"
					styleClass="button">
					<img src="images/icons/delete.png" class="button">Delete</nexus:link></td>
			</logic:notEqual>
		</tr>
	</table>
	</center>
</html:form>
