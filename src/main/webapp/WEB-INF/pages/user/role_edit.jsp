<%@ taglib uri="/tags/nexus" prefix="nexus" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/c" prefix="c" %>
<%@ page import="java.util.*" %>
<%@ page import="org.nexuse2e.*" %>
<%@ page import="org.nexuse2e.pojo.*" %>
<%@ page import="org.nexuse2e.ui.form.*" %>
<%@ page import="org.nexuse2e.ui.structure.*" %>

<nexus:helpBar helpDoc="documentation/User.htm"/>

<table class="NEXUS_TABLE" width="100%">
    <tr>
        <td>
        	<nexus:crumbs/>
        </td>
    </tr>
    <tr>
        <td class="NEXUSScreenName">Role Edit</td>
    </tr>
</table>

<html:form method="post" action="RoleSave.do">
	<input name="nxRoleId" type="hidden" value="${roleForm.nxRoleId}">
	<table class="NEXUS_TABLE">
		<tr>
			<td class="NEXUSName">
	 			Name
	 		</td>
	 		<td class="NEXUSValue">
	 			<input name="name" type="text" value="${roleForm.name}">
	 		</td>
	 	</tr>
	 	<tr>
			<td class="NEXUSName">
	 			Description
	 		</td>
	 		<td class="NEXUSValue">
	 			<input name="description" type="text" value="${roleForm.description}">
	 		</td>
	 	</tr>
	 	<tr>
			<td class="NEXUSName" style="vertical-align: top;">
	 			<script language="text/javascript">
	 				this.grantAll = function () {
	 					//debug("checkAll");
	 					for(var i = 0; i < roleForm.elements.length; i++) {
	 						var currElement = roleForm.elements[i];
	 						if(currElement.type == "checkbox") {
	 							//debug(currElement.name + " " + currElement.checked);
	 							currElement.checked = true;
	 							//debug(currElement.name + " " + currElement.checked);
	 						}
	 					}
	 				}
	 				this.grantNone = function () {
	 					//debug("checkNone");
	 					for(var i = 0; i < roleForm.elements.length; i++) {
	 						var currElement = roleForm.elements[i];
	 						if(currElement.type == "checkbox") {
	 							//debug(currElement.name + " " + currElement.checked);
	 							//currElement.checked = true;
	 							currElement.checked = false;
	 							//debug(currElement.name + " " + currElement.checked);
	 						}
	 					}
	 				}
	 			</script>
	 			Grants
	 			<p>
		 			<a href="javascript: scriptScope.grantAll();" class="button"><img src="images/submit_g.gif" class="button">Check all</a>
		 		</p>
		 		<p>
		 			<a href="javascript: scriptScope.grantNone();" class="button"><img src="images/reset_g.gif" class="button">Uncheck all</a>
		 		</p>
	 		</td>
	 		<td class="NEXUSValue">
	 			<nexus:grants allowedRequests="${ roleForm.allowedRequests }"/>	 			
	 		</td>
	 	</tr>
	</table>
	<table class="NEXUS_BUTTON_TABLE">
		<tr>
				<td>
					&nbsp;
				</td>
				<td class="NexusHeaderLink" style="text-align: right;">
					<nexus:submit styleClass="button"><img src="images/submit.gif" class="button">Save</nexus:submit>
				</td>
		    <td class="NexusHeaderLink" style="text-align: right;">
					<nexus:link precondition="confirmDelete('Are you sure you want to delete this role?')" href="RoleDelete.do?nxRoleId=${roleForm.nxRoleId}" styleClass="button"><img src="images/delete.gif" class="button">Delete</nexus:link>
				</td>
		</tr>
	</table>
</html:form>

<logic:messagesPresent>
	<div class="NexusError"><html:errors /></div>
</logic:messagesPresent>
