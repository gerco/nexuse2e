<%@ taglib uri="/tags/nexus" prefix="nexus" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/c" prefix="c" %>

<nexus:helpBar helpDoc="documentation/User.htm"/>

<table class="NEXUS_TABLE" width="100%">
    <tr>
        <td>
        	<nexus:crumbs/>
        </td>
    </tr>
    <tr>
        <td class="NEXUSScreenName">User List</td>
    </tr>
</table>

<table class="NEXUS_TABLE">
	<tr>
		<td class="NEXUSSection">
 			Name
 		</td>
 		<td class="NEXUSSection">
 			Login
 		</td>
 		<td class="NEXUSSection">
 			Role
 		</td>
 		<td class="NEXUSSection">
 			Active
 		</td>
 	</tr>
 	<logic:iterate name="collection" id="user">
 		<logic:equal name="user" property="visible" value="true">
		 	<tr>
		 		<td class="NEXUSValue">
		 			<nexus:link href="UserEdit.do?nxUserId=${user.nxUserId}" styleClass="NexusLink">${user.lastName}, ${user.firstName} ${user.middleName}</nexus:link>
		 		</td>
		 		<td class="NEXUSValue">
		 			${user.loginName}
		 		</td>
		 		<td class="NEXUSValue">
		 			${user.role.name}
		 		</td>
		 		<td class="NEXUSValue">
		 			${user.active}
		 		</td>
		 	</tr>
		</logic:equal>
 	</logic:iterate> 	
</table>
<table class="NEXUS_BUTTON_TABLE">
	<tr>
			<td>
				&nbsp;
			</td>
	    <td class="NexusHeaderLink" style="text-align: right;">
				<nexus:link href="UserAdd.do" styleClass="button"><img src="images/icons/add.png" class="button">Add User</nexus:link>
			</td>
	</tr>
</table>