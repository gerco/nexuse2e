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
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-nested" prefix="nested"%>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/nexus" prefix="nexus"%>
<%@ taglib uri="/tags/struts-html-el" prefix="html-el"%>

<%@page import="org.nexuse2e.configuration.Constants.ParameterType"%>

<% /*<nexus:helpBar helpDoc="documentation/Collaboration_Partners.htm" /> */ %>

<table class="NEXUS_TABLE" width="100%">
	<tr>
		<td><nexus:crumbs styleClass="NEXUSScreenPathLink"></nexus:crumbs></td>
	</tr>
	<tr>
		<td class="NEXUSScreenName">Pipelet Parameters</td>
	</tr>
</table>

<html:form action="PipeletParamsUpdate">
	<html:hidden property="submitaction" value="unknown" />
	<html:hidden property="actionNxId" value="unknown" />
	<html:hidden property="paramName" value="unknown" />

	<logic:empty name="pipelineForm" property="parameters">
		<i>No Parameters</i>
	</logic:empty>
	<logic:notEmpty name="pipelineForm" property="parameters">
		<table class="NEXUS_TABLE" width="100%">
			<tr>
				<td class="NEXUSSection">Name</td>
				<td class="NEXUSSection">Value</td>
				<td class="NEXUSSection">Description</td>

			</tr>
			<logic:iterate id="parameter" property="parameters"
				name="pipelineForm">
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
					value="<%= ParameterType.TEXT.toString() %>">
					<tr>
						<td class="NEXUSValue" style="vertical-align: top;">${parameter.label}</td>
						<td class="NEXUSValue"><html:textarea property="${key}" cols="50" rows="100" /></td>
						<td class="NEXUSValue" style="vertical-align: top;">${parameter.parameterDescriptor.description}</td>
					</tr>
				</logic:equal>
				<logic:equal name="parameter"
					property="parameterDescriptor.parameterType"
					value="<%= ParameterType.PASSWORD.toString() %>">
					<tr>
						<td class="NEXUSValue">${parameter.label}</td>
						<td class="NEXUSValue"><html:password property="<%=key %>"
							size="50" /></td>
						<td class="NEXUSValue">${parameter.parameterDescriptor.description}</td>
					</tr>
				</logic:equal>
				<logic:equal name="parameter" property="parameterDescriptor.parameterType" value="<%= ParameterType.ENUMERATION.toString() %>">
                    <tr>
						<td class="NEXUSValue">
							<c:choose>
								<c:when test="${parameter.label != null}">
									<input type="text" size="20" value="${parameter.label}" disabled="disabled">
								</c:when>
								<c:otherwise>
									<html:text property="key" size="20"/>
								</c:otherwise>
							</c:choose>
						</td>
						<td class="NEXUSValue">
							<c:choose>
								<c:when test="${parameter.label != null}">
									<input type="text" size="35" value="${parameter.value}" disabled="disabled">
								</c:when>
								<c:otherwise>
									<html:text property="value" size="35"/>
								</c:otherwise>
							</c:choose>
						</td>
						<td class="NEXUSValue">
							<c:choose>
								<c:when test="${parameter.label != null}">
									<nexus:submit onClick="javascript:document.forms['pipelineForm'].paramName.value='${parameter.paramName}';document.forms['pipelineForm'].actionNxId.value=${parameter.sequenceNumber};document.forms['pipelineForm'].submitaction.value='delete';">
										<img src="images/icons/delete.png" name="delete" class="button">
									</nexus:submit>
 								</c:when>
 								<c:otherwise>
 									<nexus:submit onClick="javascript:document.forms['pipelineForm'].paramName.value='${parameter.paramName}';document.forms['pipelineForm'].submitaction.value='add';">
										<img src="images/icons/tick.png" name="add" class="button">
									</nexus:submit>${parameter.parameterDescriptor.description}
 								</c:otherwise>
							</c:choose>
						</td>
					</tr>
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
						<td class="NEXUSValue"><html:checkbox property="${key}" /></td>
						<td class="NEXUSValue">${parameter.parameterDescriptor.description}</td>
					</tr>
				</logic:equal>
				<logic:equal name="parameter"
					property="parameterDescriptor.parameterType"
					value="<%= ParameterType.SERVICE.toString() %>">
					<tr>
						<td class="NEXUSValue">${parameter.label}</td>
						<td class="NEXUSValue"><nexus:select name="${key}">
							<logic:iterate id="service" name="collection">
								<%
								org.nexuse2e.pojo.PipeletParamPojo prm = (org.nexuse2e.pojo.PipeletParamPojo) pageContext.getAttribute( "parameter" );
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
				onClick="javascript:document.forms['pipelineForm'].submitaction.value='update';"
				styleClass="button">
				<img src="images/icons/tick.png" class="button">Save</nexus:submit></td>
			<td class="NexusHeaderLink" style="text-align: right;"><nexus:submit
				onClick="javascript:document.forms['pipelineForm'].submitaction.value='back';">
				<img src="images/icons/delete.png" class="button">Cancel</nexus:submit></td>
		</tr>
	</table>
</html:form>