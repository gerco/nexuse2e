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