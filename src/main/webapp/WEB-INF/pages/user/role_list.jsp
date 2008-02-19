<%@ taglib uri="/tags/nexus" prefix="nexus" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/c" prefix="c" %>

<% /*<nexus:helpBar helpDoc="documentation/Role.htm"/> */ %>

<table class="NEXUS_TABLE" width="100%">
    <tr>
        <td>
        	<nexus:crumbs/>
        </td>
    </tr>
    <tr>
        <td class="NEXUSScreenName">Role List</td>
    </tr>
</table>

<table class="NEXUS_TABLE">
	<tr>
		<td class="NEXUSSection">
 			Name
 		</td>
 		<td class="NEXUSSection">
 			Description
 		</td>
 	</tr>
 	<logic:iterate name="collection" id="role">
 		<tr>
	 		<td class="NEXUSValue">
	 			<nexus:link href="RoleEdit.do?nxRoleId=${role.nxRoleId}" styleClass="NexusLink">${role.name}</nexus:link>
	 		</td>
	 		<td class="NEXUSValue">
	 			${role.description}
	 		</td>
	 	</tr>
 	</logic:iterate> 	
</table>
<table class="NEXUS_BUTTON_TABLE">
	<tr>
			<td>
				&nbsp;
			</td>
	    <td class="NexusHeaderLink" style="text-align: right;">
				<nexus:link href="RoleAdd.do" styleClass="button"><img src="images/icons/add.png" class="button">Add Role</nexus:link>
			</td>
	</tr>
</table>