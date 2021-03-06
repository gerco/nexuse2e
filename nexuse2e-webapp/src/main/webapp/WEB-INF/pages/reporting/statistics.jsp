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
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-nested" prefix="nested"%>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/nexus" prefix="nexus"%>

<% /*<nexus:helpBar helpDoc="documentation/NEXUSe2e.html" /> */ %>

<table class="NEXUS_TABLE" width="100%">
	<tr>
		<td><nexus:crumbs /></td>
	</tr>
	<tr>
		<td class="NEXUSScreenName">NEXUSe2e</td>
	</tr>
</table>

<logic:greaterThan name="messageCount" value="0">
	<nexus:reportsAvailable>
		<table width="100%">
			<tr>
				<td>
					<nexus:report name="message_stati_24h">
						<nexus:reportParam name="startDate" value="${last24Hours}"/>
					</nexus:report>
				</td>
				<td>
					<nexus:report name="conversation_stati_24h">
						<nexus:reportParam name="startDate" value="${last24Hours}"/>
					</nexus:report>
				</td>
			</tr>
			<tr>
				<td>
					<nexus:report name="messages_by_choreography_24h">
	                    <nexus:reportParam name="startDate" value="${last24Hours}"/>
					</nexus:report>
				</td>
				<td>
					<nexus:report name="messages_per_hour">
						<nexus:reportParam name="startDate" value="${last24HoursRounded}"/>
					</nexus:report>
				</td>
			</tr>
		</table>
	</nexus:reportsAvailable>
	<nexus:reportsUnavailable>
		Statistics cannot be displayed because the reporting add-on is not installed.
	</nexus:reportsUnavailable>
</logic:greaterThan>
<logic:lessEqual name="messageCount" value="0">
	No messages have been created within the last 24 hours. Please try again when messages have been received or sent.
</logic:lessEqual>

<center><logic:messagesPresent>
	<div class="NexusError"><html:errors /></div>
</logic:messagesPresent></center>
