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

<table class="NEXUS_TABLE" width="100%">
	<tr>
		<td class="NEXUSSection">Property</td>
		<td class="NEXUSSection">Value</td>
	</tr>
	<tr>
		<td class="NEXUSNameNoWidth">NEXUSe2e Version</td>
		<td class="NEXUSNameNoWidth">${NEXUSe2e_version}</td>
	</tr>
	<tr>
		<td class="NEXUSNameNoWidth">Java Version</td>
		<td class="NEXUSNameNoWidth">${java_version}</td>
	</tr>
	<tr>
		<td class="NEXUSNameNoWidth">Java Home</td>
		<td class="NEXUSNameNoWidth">${java_home}</td>
	</tr>
	<tr>
		<td class="NEXUSNameNoWidth">Java Classpath</td>
		<td class="NEXUSNameNoWidth">${java_classpath}</td>
	</tr>
	<tr>
		<td class="NEXUSNameNoWidth">Service Uptime</td>
		<td class="NEXUSNameNoWidth">${service_uptime}</td>
	</tr>
	<tr>
		<td class="NEXUSNameNoWidth">Engine Uptime</td>
		<td class="NEXUSNameNoWidth">${engine_uptime}</td>
	</tr>
	<tr>
		<td class="NEXUSNameNoWidth">License</td>
		<td class="NEXUSNameNoWidth"><a href="html/lgpl.html" target="#blank">GNU Lesser General Public License</a> (LGPL), Version 2.1</td>
	</tr>
	<tr>
		<td class="NEXUSNameNoWidth">JVM Charset Encoding</td>
		<td class="NEXUSNameNoWidth"><%= java.nio.charset.Charset.defaultCharset().name() %></td>
	</tr>
	<tr>
		<td class="NEXUSNameNoWidth">NEXUSe2e Charset Encoding</td>
		<td class="NEXUSNameNoWidth"><%= org.nexuse2e.Engine.getInstance().getDefaultCharEncoding() %></td>
	</tr>
	<tr>
		<td class="NEXUSNameNoWidth">JCE status / cipher limitations</td>
		<td class="NEXUSNameNoWidth"><%= org.nexuse2e.Engine.getInstance().getJCEInstalledStatus() %></td>
	</tr>
</table>
<table colspan="4" class="NEXUS_TABLE" width="100%">

	<tr>
		<td class="NEXUSSection">Instances</td>
		
	</tr>
</table>

	<nested:iterate id="instance" name="instances">
		
		<td>
			<table class="NEXUS_TABLE" style="width:20%">
				<tr>
					<td class="NEXUSSection">
						Label:
					</td>
					<td class="NEXUSSection">
						<bean:write name="instance" property="label" />
					</td>
				</tr>
				<bean:size id="size" name="instance" property="commands"/>
				<% 
				int filler = 0;
					if(size < 4) {
						filler = 4-size;
						size = 4;
					} 
				%>
				<nested:iterate id="command" indexId="counter" name="instance" property="commands">
					<tr>
						<% if(counter.intValue() == 0) {%>
						<td style="background-color:<bean:write name="instance" property="statusColor" />;" rowspan="<%=size %>" class="NEXUSNameNoWidth">
							
						</td>
						<% } %>
						
						<td class="NEXUSNameNoWidth">
							<nexus:link href="InstanceController.do?instanceId=${instance.id}&commandId=${command.name}" styleClass="NexusLink">
            					<bean:write name="command" property="label" />
            				</nexus:link>
						</td>
					</tr>
				</nested:iterate>
				<% if (filler > 0){
					for(int i = 0; i < filler; i++){%>
					<tr>
						<td class="NEXUSNameNoWidth">&nbsp;
						</td>
					</tr>
					<%	
					}
				}
				%>
			</table>
			
		</td>
	</nested:iterate>
${description}
<center><logic:messagesPresent>
	<div class="NexusError"><html:errors /></div>
</logic:messagesPresent></center>
