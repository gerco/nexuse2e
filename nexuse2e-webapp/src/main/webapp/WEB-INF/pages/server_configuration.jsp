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

<% /*<nexus:helpBar helpDoc="documentation/Server.htm" /> */ %>

<table class="NEXUS_TABLE" width="100%">
	<tr>
		<td><nexus:crumbs /></td>
	</tr>
	<tr>
		<td class="NEXUSScreenName">Server Configuration</td>
	</tr>
</table>

<table width="100%" class="NEXUS_TABLE">
	<tr>
		<td class="NEXUSIcon"><nexus:link styleClass="NexusImgLink"
			href="UserMaintenance.do">
			<img border="0" src="images/icons/user.png">
		</nexus:link></td>
		<td class="NEXUSNameNoWidth"><nexus:link styleClass="NexusLink"
			href="UserMaintenance.do">User Maintenance</nexus:link></td>
	</tr>
	<tr>
		<td class="NEXUSIcon"><nexus:link styleClass="NexusImgLink"
			href="ServerIdentities.do?type=1">
			<img border="0" src="images/icons/server.png">
		</nexus:link></td>
		<td class="NEXUSNameNoWidth"><nexus:link styleClass="NexusLink"
			href="ServerIdentities.do?type=1">Server Identities</nexus:link></td>
	</tr>
    <tr>
        <td class="NEXUSIcon"><nexus:link styleClass="NexusImgLink"
            href="TrpMaintenance.do">
            <img border="0" src="images/icons/package.png">
        </nexus:link></td>
        <td class="NEXUSNameNoWidth"><nexus:link styleClass="NexusLink"
            href="TrpMaintenance.do">Transport/Routing/Packaging</nexus:link></td>
    </tr>
	<tr>
		<td class="NEXUSIcon"><nexus:link styleClass="NexusImgLink"
			href="Components.do">
			<img border="0" src="images/icons/brick.png">
		</nexus:link></td>
		<td class="NEXUSNameNoWidth"><nexus:link styleClass="NexusLink"
			href="Components.do">Components</nexus:link></td>
	</tr>
	<tr>
		<td class="NEXUSIcon"><nexus:link styleClass="NexusImgLink"
			href="BackendPipelines.do">
			<img border="0" src="images/icons/bricks.png">
		</nexus:link></td>
		<td class="NEXUSNameNoWidth"><nexus:link styleClass="NexusLink"
			href="BackendPipelines.do">Backend Pipelines</nexus:link></td>
	</tr>
	<tr>
		<td class="NEXUSIcon"><nexus:link styleClass="NexusImgLink"
			href="FrontendPipelines.do">
			<img border="0" src="images/icons/bricks.png">
		</nexus:link></td>
		<td class="NEXUSNameNoWidth"><nexus:link styleClass="NexusLink"
			href="FrontendPipelines.do">Frontend Pipelines</nexus:link></td>
	</tr>
	<tr>
		<td class="NEXUSIcon"><nexus:link styleClass="NexusImgLink"
			href="ServiceList.do">
			<img border="0" src="images/icons/cog.png">
		</nexus:link></td>
		<td class="NEXUSNameNoWidth"><nexus:link styleClass="NexusLink"
			href="ServiceList.do">Services</nexus:link></td>
	</tr>
	<tr>
		<td class="NEXUSIcon"><nexus:link styleClass="NexusImgLink"
			href="NotifierList.do">
			<img border="0" src="images/icons/transmit_blue.png">
		</nexus:link></td>
		<td class="NEXUSNameNoWidth"><nexus:link styleClass="NexusLink"
			href="NotifierList.do">Logger</nexus:link></td>
	</tr>
	<tr>
		<td class="NEXUSIcon"><nexus:link styleClass="NexusImgLink"
			href="Certificates.do">
			<img border="0" src="images/icons/medal_silver_3.png">
		</nexus:link></td>
		<td class="NEXUSNameNoWidth"><nexus:link styleClass="NexusLink"
			href="Certificates.do">Certificates</nexus:link></td>
	</tr>
	<tr>
		<td class="NEXUSIcon"><nexus:link styleClass="NexusImgLink"
			href="CACertificatesList.do">
			<img border="0" src="images/icons/medal_gold_3.png">
		</nexus:link></td>
		<td class="NEXUSNameNoWidth"><nexus:link styleClass="NexusLink"
			href="CACertificatesList.do">Certificate Authority Certificates</nexus:link></td>
	</tr>
</table>

<center><logic:messagesPresent>
	<div class="NexusError"><html:errors /></div>
</logic:messagesPresent></center>
