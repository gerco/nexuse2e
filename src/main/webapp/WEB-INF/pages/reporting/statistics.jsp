<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-nested" prefix="nested"%>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/nexus" prefix="nexus"%>

<nexus:helpBar helpDoc="documentation/NEXUSe2e.html" />

<table class="NEXUS_TABLE" width="100%">
	<tr>
		<td><nexus:crumbs /></td>
	</tr>
	<tr>
		<td class="NEXUSScreenName">NEXUSe2e</td>
	</tr>
</table>

<nexus:reportsAvailable>
	<table width="100%">
		<tr>
			<td>
				<nexus:report name="message_stati_24h">
					<nexus:reportParam name="startDate" value="<%=  new java.text.SimpleDateFormat( "yyyy-MM-dd" ).parse( "2008-01-20" ) %>"/>
				</nexus:report>
			</td>
			<td>
				<nexus:report name="conversation_stati_24h">
					<nexus:reportParam name="startDate" value="<%=  new java.text.SimpleDateFormat( "yyyy-MM-dd" ).parse( "2008-01-20" ) %>"/>
				</nexus:report>
			</td>
		</tr>
		<tr>
			<td>
				<nexus:report name="messages_by_choreography_24h">
					<nexus:reportParam name="startDate" value="<%=  new java.text.SimpleDateFormat( "yyyy-MM-dd" ).parse( "2008-01-20" ) %>"/>
				</nexus:report>
			</td>
			<td>
				<nexus:report name="messages_per_hour">
					<nexus:reportParam name="startDate" value="<%=  new java.text.SimpleDateFormat( "yyyy-MM-dd" ).parse( "2008-01-20" ) %>"/>
				</nexus:report>
			</td>
		</tr>
	</table>
</nexus:reportsAvailable>
<nexus:reportsUnavailable>
	Statistics cannot be displayed because the reporting add-on is not installed.
</nexus:reportsUnavailable>

<center><logic:messagesPresent>
	<div class="NexusError"><html:errors /></div>
</logic:messagesPresent></center>
