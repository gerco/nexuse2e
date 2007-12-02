<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-nested" prefix="nested"%>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/nexus" prefix="nexus"%>

<nexus:helpBar helpDoc="documentation/User.htm" />

<table class="NEXUS_TABLE" width="100%">
	<tr>
		<td><nexus:crumbs /></td>
	</tr>
	<tr>
		<td class="NEXUSScreenName">User Configuration</td>
	</tr>
</table>

<table width="100%" class="NEXUS_TABLE">
	<tr>
		<td class="NEXUSIcon"><nexus:link styleClass="NexusImgLink"
			href="UserList.do">
			<img border="0" src="images/tree/users_g.gif">
		</nexus:link></td>
		<td class="NEXUSNameNoWidth"><nexus:link styleClass="NexusLink"
			href="UserList.do">Users</nexus:link></td>
	</tr>
	<tr>
		<td class="NEXUSIcon"><nexus:link styleClass="NexusImgLink"
			href="RoleList.do">
			<img border="0" src="images/tree/ssl_g.gif">
		</nexus:link></td>
		<td class="NEXUSNameNoWidth"><nexus:link styleClass="NexusLink"
			href="RoleList.do">Roles</nexus:link></td>
	</tr>
</table>

<center><logic:messagesPresent>
	<div class="NexusError"><html:errors /></div>
</logic:messagesPresent></center>
