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
<%@ taglib uri="/tags/nexus" prefix="nexus" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/c" prefix="c" %>
<%@ page import="java.util.*" %>
<%@ page import="org.nexuse2e.*" %>
<%@ page import="org.nexuse2e.pojo.*" %>
<%@ page import="org.nexuse2e.ui.form.*" %>
<%@ page import="org.nexuse2e.ui.structure.*" %>

<% /*<nexus:helpBar helpDoc="documentation/User.htm"/> */ %>

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
		 			<a href="javascript: grantAll();" class="NexusLink">Check all</a>
		 		</p>
		 		<p>
		 			<a href="javascript: grantNone();" class="NexusLink">Uncheck all</a>
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
					<nexus:submit styleClass="button"><img src="images/icons/tick.png" class="button">Save</nexus:submit>
				</td>
		    <td class="NexusHeaderLink" style="text-align: right;">
					<nexus:link precondition="confirmDelete('Are you sure you want to delete this role?')" href="RoleDelete.do?nxRoleId=${roleForm.nxRoleId}" styleClass="button"><img src="images/icons/delete.png" class="button">Delete</nexus:link>
				</td>
		</tr>
	</table>
</html:form>

<logic:messagesPresent>
	<div class="NexusError"><html:errors /></div>
</logic:messagesPresent>
