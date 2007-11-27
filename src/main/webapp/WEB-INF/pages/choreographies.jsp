<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-nested" prefix="nested"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/tags/nexus" prefix="nexus"%>

<nexus:helpBar helpDoc="documentation/Choreography.htm" />

<center>
<table class="NEXUS_TABLE" width="100%">
	<tr>
		<td><nexus:crumbs /></td>
	</tr>
	<tr>
		<td class="NEXUSScreenName">Choreographies</td>
	</tr>
</table>

<table class="NEXUS_TABLE" width="100%">
	<logic:iterate id="choreography" name="collection">
		<tr>
			<td class="NEXUSName"><nexus:link
				href="ChoreographyView.do?nxChoreographyId=${choreography.nxChoreographyId}"
				styleClass="NexusLink">
				<bean:write name="choreography" property="choreographyName" />
			</nexus:link></td>
			<!-- 
                <td class="NEXUSName" align="right"><nexus:link href="Choreography?Type=quickExport&amp;nxChoreographyId=${choreography.nxChoreographyId}"
                   styleClass="NexusLink"><i>quickExport</i></nexus:link></td>
                    -->
		</tr>
	</logic:iterate>
</table>
<table class="NEXUS_BUTTON_TABLE" width="100%">
	<tr>
		<td>&nbsp;</td>
		<td class="BUTTON_RIGHT"><nexus:link href="ChoreographyAdd.do"
			styleClass="NexusHeaderLink">
			<img src="images/tree/plus.gif" border="0" alt="" class="button">
		Add Choreography</nexus:link></td>
	</tr>
</table>
</center>
<center><logic:messagesPresent>
	<div class="NexusError"><html:errors /></div>
</logic:messagesPresent> <logic:messagesPresent message="true">
	<html:messages id="msg" message="true">
		<div class="NexusMessage"><bean:write name="msg" /></div>
		<br />
	</html:messages>
</logic:messagesPresent></center>
