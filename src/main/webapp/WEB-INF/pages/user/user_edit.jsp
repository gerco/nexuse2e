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
        <td class="NEXUSScreenName">User Edit</td>
    </tr>
</table>

<html:form method="post" action="UserSave.do">
	<input name="nxUserId" type="hidden" value="${userForm.nxUserId}">
	<table class="NEXUS_TABLE">
		<tr>
			<td class="NEXUSName">
	 			Last Name
	 		</td>
	 		<td class="NEXUSValue">
	 			<input name="lastName" type="text" value="${userForm.lastName}">
	 		</td>
	 	</tr>
	 	<tr>
			<td class="NEXUSName">
	 			First Name
	 		</td>
	 		<td class="NEXUSValue">
	 			<input name="firstName" type="text" value="${userForm.firstName}">
	 		</td>
	 	</tr>
	 	<tr>
			<td class="NEXUSName">
	 			Middle Name
	 		</td>
	 		<td class="NEXUSValue">
	 			<input name="middleName" type="text" value="${userForm.middleName}">
	 		</td>
	 	</tr>
	 	<tr>
			<td class="NEXUSName">
	 			Login Name
	 		</td>
	 		<td class="NEXUSValue">
	 			<input name="loginName" type="text" value="${userForm.loginName}">
	 		</td>
	 	</tr>
	 	<tr>
			<td class="NEXUSName">
	 			Password
	 		</td>
	 		<td class="NEXUSValue">
	 			<input name="password" type="password" value="${userForm.password}">
	 		</td>
	 	</tr>
	 	<tr>
			<td class="NEXUSName">
	 			Repeat Password
	 		</td>
	 		<td class="NEXUSValue">
	 			<input name="passwordRepeat" type="password" value="${userForm.passwordRepeat}">
	 		</td>
	 	</tr>
	 	<tr>
			<td class="NEXUSName">
	 			Active
	 		</td>
	 		<td class="NEXUSValue">
	 			<html:radio property="active" value="true"/> Yes
	 			<html:radio property="active" value="false"/> No
	 		</td>
	 	</tr>
	 	<tr>
			<td class="NEXUSName">
	 			Role
	 		</td>
	 		<td class="NEXUSValue">
	 			<%
	 			request.setAttribute( "collection", org.nexuse2e.Engine.getInstance().getActiveConfigurationAccessService().getRoles( org.nexuse2e.configuration.Constants.COMPARATOR_ROLE_BY_NAME ) );
	 			%>
	 			<html:select property="nxRoleId">
	 				<html:options collection="collection" property="nxRoleId" labelProperty="name"/>
	 			</html:select>
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
					<nexus:link precondition="confirmDelete('Are you sure you want to delete this user?')" href="UserDelete.do?userId=${userForm.nxUserId}" styleClass="button"><img src="images/icons/delete.png" class="button">Delete</nexus:link>
				</td>
		</tr>
	</table>
</html:form>

<logic:messagesPresent>
	<div class="NexusError"><html:errors /></div>
</logic:messagesPresent>
