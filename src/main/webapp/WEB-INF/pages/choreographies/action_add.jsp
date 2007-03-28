<%@ taglib uri="/tags/nexus" prefix="nexus" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-nested" prefix="nested" %>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %> 
<%@ taglib uri="/tags/struts-html-el" prefix="html-el" %>

<nexus:helpBar helpDoc="documentation/Action.htm"/>

    <html:form action="ActionCreate">
        <html:hidden property="choreographyId"/>
                
				<table class="NEXUS_TABLE" width="100%">
				    <tr>
				        <td>
				        	<nexus:crumbs/>
				        </td>
				    </tr>
				    <tr>
				        <td class="NEXUSScreenName">Add Action</td>
				    </tr>
				</table>

        <table class="NEXUS_TABLE" width="100%">
            <tr>
                <td class="NEXUSSection">Action ID</td>
                <td class="NEXUSSection"><html:text size="50" property="actionId"></html:text></td>
            </tr>
            <tr>
                <td class="NEXUSValue" colspan="2"><html:checkbox property="startAction">&nbsp;Valid Start Action</html:checkbox></td>
            </tr>
            <tr>
                <td class="NEXUSValue" colspan="2"><html:checkbox property="terminationAction">&nbsp;Valid Termination Action</html:checkbox></td>
            </tr>
            
            <tr>
				<td class="NEXUSName">Backend Inbound Pipeline</td>
				<td class="NEXUSValue">
					<html:select property="backendInboundPipelineId">
						<logic:iterate name="choreographyActionForm" property="backendInboundPipelines" id="inbound">
							<html-el:option value="${inbound.nxPipelineId}">${inbound.name}</html-el:option>
						</logic:iterate>
					</html:select> 
				</td>
			</tr>
			<tr>
				<td class="NEXUSName">Backend Outbound Pipeline</td>
				<td class="NEXUSValue">
					<html:select property="backendOutboundPipelineId">
						<logic:iterate name="choreographyActionForm" property="backendOutboundPipelines" id="outbound">
							<html-el:option value="${outbound.nxPipelineId}">${outbound.name}</html-el:option>
						</logic:iterate>
					</html:select> 
				</td>
			</tr>
            
        </table>
        <table class="NEXUS_BUTTON_TABLE" width="100%">
            <tr>
                <td>&nbsp;</td>
                <td class="BUTTON_RIGHT"><nexus:submit precondition="actionCheckFields()" styleClass="button"><img src="images/submit.gif" class="button">Create</nexus:submit></td>
            </tr>
        </table>
    </html:form>
