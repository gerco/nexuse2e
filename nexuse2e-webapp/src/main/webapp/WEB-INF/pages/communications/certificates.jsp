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

<% /* <nexus:helpBar /> */ %>

<table class="NEXUS_TABLE" width="100%">
	<tr>
		<td><nexus:crumbs /></td>
	</tr>
	<tr>
		<td class="NEXUSScreenName">Certificates</td>
	</tr>
</table>

<table width="100%" class="NEXUS_TABLE">
	<tr>
		<td class="NEXUSIcon"><nexus:link styleClass="NexusImgLink"
			href="RequestOverview.do">
			<img border="0" src="images/icons/medal_silver_add.png">
		</nexus:link></td>
		<td class="NEXUSNameNoWidth"><nexus:link styleClass="NexusLink"
			href="RequestOverview.do">Certificate Request</nexus:link></td>
	</tr>
	<tr>
		<td class="NEXUSIcon"><nexus:link styleClass="NexusImgLink"
			href="StagingList.do">
			<img border="0" src="images/icons/medal_silver_3.png">
		</nexus:link></td>
		<td class="NEXUSNameNoWidth"><nexus:link styleClass="NexusLink"
			href="StagingList.do">Certificate Staging</nexus:link></td>
	</tr>


</table>
