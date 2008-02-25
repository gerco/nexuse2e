<%@ taglib uri="/tags/nexus" prefix="nexus"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-nested" prefix="nested"%>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/struts-html-el" prefix="html-el"%>
<% /* <nexus:helpBar /> */ %>

<center>
	
	<table class="NEXUS_TABLE" width="100%">
		<tr>
			<td><nexus:crumbs styleClass="NEXUSScreenPathLink"></nexus:crumbs></td>
		</tr>
		<tr>
			<td class="NEXUSScreenName">Transport/Routing/Packaging Maintenance</td>
		</tr>
	</table>

	<html:form action="TrpMaintenance.do" method="post">
		<html:hidden property="submitaction" value="unknown" />
		<html:hidden property="nxTRPId" value="0" />
		<html:hidden property="protocol" value="unknown" />
		<html:hidden property="transport" value="unknown" />
		<html:hidden property="version" value="unknown" />
        <html:hidden property="adapterClassName" value="unknown" />
		<table width="100%">
			<tr>
				<td class="NEXUSName">Transport</td>
				<td class="NEXUSName">Protocol</td>
				<td class="NEXUSName">Version</td>
				<td class="NEXUSName">Actions</td>
			</tr>
			<tr>
				<td class="NEXUSName"><input type="text" size="20" name="htmlnewtransport"/></td>
				<td class="NEXUSName"><input type="text" size="20" name="htmlnewprotocol"/></td>
				<td class="NEXUSName"><input type="text" size="20" name="htmlnewversion"/></td>
                <td class="NEXUSName"><input type="text" size="20" name="htmlnewadapterclassname"/></td>
				<td class="NEXUSName">
				
				<nexus:submit
					onClick="document.forms[0].submitaction.value='add'; 
							document.forms[0].nxTRPId.value='0';
							document.forms[0].transport.value=document.getElementsByName('htmlnewtransport')[0].value;
							document.forms[0].protocol.value=document.getElementsByName('htmlnewprotocol')[0].value;
							document.forms[0].version.value=document.getElementsByName('htmlnewversion')[0].value;
                            document.forms[0].version.value=document.getElementsByName('htmlnewadapterclassname')[0].value;
							">
					<img src="images/icons/add.png" class="button" alt="Configure" id="addTrp"><span dojoType="tooltip" connectId="addTrp" toggle="explode">Add TRP Entry</span>
				</nexus:submit>
				</td>
			</tr>
		</table>
	
	 
	<table width="100%">
		<logic:iterate id="trp" name="collection" indexId="counter">
			<tr> 
				<td class="NEXUSName"><input type="text" size="20" name="htmltransport${counter}" value="${trp.transport}"/></td>
				<td class="NEXUSName"><input type="text" size="20" name="htmlprotocol${counter}" value="${trp.protocol}"/></td>
				<td class="NEXUSName"><input type="text" size="20" name="htmlversion${counter}" value="${trp.version}"/></td>
                <td class="NEXUSName"><input type="text" size="20" name="htmladapterclassname${counter}" value="${trp.adapterClassName}"/></td>
				<td class="NEXUSName">
				<nexus:submit
					onClick="document.forms[0].submitaction.value='delete'; 
							document.forms[0].nxTRPId.value='${trp.nxTRPId}';
							document.forms[0].transport.value=document.getElementsByName('htmltransport${counter}')[0].value;
							document.forms[0].protocol.value=document.getElementsByName('htmlprotocol${counter}')[0].value;
							document.forms[0].version.value=document.getElementsByName('htmlversion${counter}')[0].value;
                            document.forms[0].adapterClassName.value=document.getElementsByName('htmladapterclassname${counter}')[0].value;
							">
					<img src="images/icons/delete.png" class="button" alt="Delete" id="deleteTrp"><span dojoType="tooltip" connectId="deleteTrp" toggle="explode">Delete TRP Entry</span>
				</nexus:submit> 
				<nexus:submit
					onClick="document.forms[0].submitaction.value='update';
							document.forms[0].nxTRPId.value='${trp.nxTRPId}';
							document.forms[0].transport.value=document.getElementsByName('htmltransport${counter}')[0].value;
							document.forms[0].protocol.value=document.getElementsByName('htmlprotocol${counter}')[0].value;
							document.forms[0].version.value=document.getElementsByName('htmlversion${counter}')[0].value;
                            document.forms[0].adapterClassName.value=document.getElementsByName('htmladapterclassname${counter}')[0].value;
					">
					<img src="images/icons/tick.png" class="button" alt="Configure" id="updateTrp"><span dojoType="tooltip" connectId="updateTrp" toggle="explode">Update TRP Entry</span>
				</nexus:submit>
				</td>
			</tr>
		</logic:iterate>
	</table>
	</html:form>

</center>
<center><logic:messagesPresent>
	<div class="NexusError"><html:errors /></div>
</logic:messagesPresent> <logic:messagesPresent message="true">
	<html:messages id="msg" message="true">
		<div class="NexusMessage"><bean:write name="msg" /></div>
		<br />
	</html:messages>
</logic:messagesPresent></center>
