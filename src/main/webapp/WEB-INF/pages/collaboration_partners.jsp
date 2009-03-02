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

<% /*<nexus:helpBar helpDoc="documentation/Collaboration_Partners.htm" /> */ %>

<table class="NEXUS_TABLE" width="100%">
	<tr>
		<td><nexus:crumbs /></td>
	</tr>
	<tr>
		<td class="NEXUSScreenName"><bean:write name="HEADLINE" /></td>
	</tr>
</table>

<table class="NEXUS_TABLE" width="100%">
	<tr>
		<td class="NEXUSSection">Partner ID</td>
		<td colspan="2" class="NEXUSSection">Company Name</td>
	</tr>

	<logic:iterate id="partner" name="collection">
		<tr>
			<td class="NEXUSName"><nexus:link
				href="PartnerInfoView.do?nxPartnerId=${partner.nxPartnerId}&type=${TYPE}"
				styleClass="NexusLink">
				<bean:write name="partner" property="partnerId" /> (<bean:write name="partner" property="name" />)
			</nexus:link></td>
			<td class="NEXUSName"><bean:write name="partner"
				property="company" /></td>
			<td><nexus:link
				href="PartnerConnectionList.do?nxPartnerId=${partner.nxPartnerId}"
				styleClass="NexusDrillDownLink">Connections</nexus:link></td>
		</tr>
	</logic:iterate>

</table>
<table class="NEXUS_BUTTON_TABLE">
  <tr>
     <td>
      &nbsp;
    </td>
    <td class="BUTTON_RIGHT">
      <nexus:link href="CollaborationPartnerAdd.do?type=${TYPE}" styleClass="button"><span style="white-space: nowrap;"><img src="images/icons/add.png" class="button"><bean:write name="BUTTONTEXT"/></span></nexus:link>
    </td>
  </tr>
</table>
<center><logic:messagesPresent>
	<div class="NexusError"><html:errors /></div>
</logic:messagesPresent></center>
