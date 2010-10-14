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
<%@ taglib uri="/tags/struts-nested" prefix="nested"%>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/struts-html-el" prefix="html-el"%>

<% /*<nexus:helpBar helpDoc="documentation/Action.htm" /> */ %>

<html:form action="ActionCreate">
	<table class="NEXUS_TABLE" width="100%">
		<tr>
			<td><nexus:crumbs /></td>
		</tr>
		<tr>
			<td class="NEXUSScreenName">Add Action</td>
		</tr>
	</table>

	<table class="NEXUS_TABLE" width="100%">
		<tr>
			<td class="NEXUSSection">Action ID</td>
			<td class="NEXUSSection"><html:text size="50"
				property="actionId"></html:text></td>
		</tr>
		<tr>
			<td class="NEXUSValue" colspan="2"><html:checkbox
				property="startAction">&nbsp;Valid Start Action</html:checkbox></td>
		</tr>
		<tr>
			<td class="NEXUSValue" colspan="2"><html:checkbox
				property="terminationAction">&nbsp;Valid Termination Action</html:checkbox></td>
		</tr>

		<tr>
			<td class="NEXUSName">Backend Inbound Pipeline</td>
			<td class="NEXUSValue"><html:select
				property="backendInboundPipelineId">
				<logic:iterate name="choreographyActionForm"
					property="backendInboundPipelines" id="inbound">
					<html-el:option value="${inbound.nxPipelineId}">${inbound.name}</html-el:option>
				</logic:iterate>
			</html:select></td>
		</tr>
		<tr>
			<td class="NEXUSName">Backend Outbound Pipeline</td>
			<td class="NEXUSValue"><html:select
				property="backendOutboundPipelineId">
				<logic:iterate name="choreographyActionForm"
					property="backendOutboundPipelines" id="outbound">
					<html-el:option value="${outbound.nxPipelineId}">${outbound.name}</html-el:option>
				</logic:iterate>
			</html:select></td>
		</tr>
		<tr>
			<td class="NEXUSName">Status Update Pipeline</td>
			<td class="NEXUSValue"><html:select
				property="statusUpdatePipelineId">
				<html-el:option value="0">None</html-el:option>
				<logic:iterate name="choreographyActionForm"
					property="statusUpdatePipelines" id="inbound">
					<html-el:option value="${inbound.nxPipelineId}">${inbound.name}</html-el:option>
				</logic:iterate>
			</html:select></td>
		</tr>
		<tr>
			<td class="NEXUSName">Polling Required</td>
			<td class="NEXUSValue">
				<html:checkbox property="pollingRequired"/>
			</td>
		</tr>
		<tr>
			<td class="NEXUSName">Document Type</td>
			<td class="NEXUSValue">
				<html:text size="50" property="documentType"/>
			</td>
		</tr>

	</table>
	<table class="NEXUS_BUTTON_TABLE" width="100%">
		<tr>
			<td>&nbsp;</td>
			<!-- 
                <td class="BUTTON_RIGHT"><nexus:submit precondition="actionCheckFields()" styleClass="button"><img src="images/icons/tick.png" class="button">Create</nexus:submit></td>
                 -->
			<td class="BUTTON_RIGHT"><nexus:submit styleClass="button">
				<img src="images/icons/tick.png" class="button">Create</nexus:submit></td>
		</tr>
	</table>
</html:form>
