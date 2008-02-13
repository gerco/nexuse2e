<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-nested" prefix="nested"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/struts-html-el" prefix="html-el"%>
<%@ taglib uri="/tags/nexus" prefix="nexus"%>

<style type="text/css" media="screen">
<!--
.fixedsize {
	width: 240;
	height: 22;
}
-->
</style>

<script>
    this.Clear = function () {
    	document.forms['reportingPropertiesForm'].status.options[0].selected = "true";
	    document.forms['reportingPropertiesForm'].choreographyId.options[0].selected = "true";
	    document.forms['reportingPropertiesForm'].participantId.options[0].selected = "true";
	    document.forms['reportingPropertiesForm'].conversationEnabled.checked = null;
	    document.forms['reportingPropertiesForm'].conversationId.value = "";
	    document.forms['reportingPropertiesForm'].messageEnabled.checked = null;
	    document.forms['reportingPropertiesForm'].messageId.value = "";
    }
    
		this.cancelLink = function () {
    	return false;
  	}
  
  	this.disableLink = function (link) {
        if(link == null) {
          return;
        }
        if (link.onclick) {
          link.oldOnClick = link.onclick;
        }
        link.onclick = _container_.scriptScope.cancelLink;
        if (link.style) {
        	link.style.cursor = 'text';
          link.style.color = '#000080';
        }
            
        link.className='NexusLinkDisabled';
    }
    
    this.enableLink = function (link) {
	    if(link == null) {
	      return;
	    }
	    link.onclick = (link.oldOnClick ? link.oldOnClick : null);
	    if (link.style) {
	        link.style.cursor = (document.all ? 'hand' : 'pointer');
	        link.style.color = '#000000';
	    }
				
			link.className='NexusLink';
    }
    
    this.disableLinks = function () {
			_container_.scriptScope.disableLink(document.getElementById('nextLink'));
			_container_.scriptScope.disableLink(document.getElementById('startLink'));
			_container_.scriptScope.disableLink(document.getElementById('previousLink'));
			_container_.scriptScope.disableLink(document.getElementById('endLink'));
		}
    
    this.enableLinks = function () {
    	_container_.scriptScope.enableLink(document.getElementById('nextLink'));
      _container_.scriptScope.enableLink(document.getElementById('previousLink'));
      _container_.scriptScope.enableLink(document.getElementById('startLink'));
      _container_.scriptScope.enableLink(document.getElementById('endLink'));
    }
    
    this.searchForMessages = function () {
			document.getElementById('messageIdText').className='NEXUSValue';
			document.forms['reportingPropertiesForm'].messageEnabled.disabled=false;
			document.forms['reportingPropertiesForm'].messageId.disabled=false;
			
			var length = document.forms['reportingPropertiesForm'].status.options.length;
      if(length == 11) {
        return;
      }
      for (x = length ; x >= 0; x--) {
      	document.forms['reportingPropertiesForm'].status.options[x] = null;
      }
         
      document.forms['reportingPropertiesForm'].status.options[0] = new Option('','');
      document.forms['reportingPropertiesForm'].status.options[2] = new Option('Queued','2');
      document.forms['reportingPropertiesForm'].status.options[3] = new Option('Error','-1');
      document.forms['reportingPropertiesForm'].status.options[4] = new Option('Stopped','4');
      document.forms['reportingPropertiesForm'].status.options[5] = new Option('Retrying','1');
      document.forms['reportingPropertiesForm'].status.options[6] = new Option('Transmitted','3');
      document.forms['reportingPropertiesForm'].status.options[1] = new Option('Unknown','0');
      document.forms['reportingPropertiesForm'].status.options[7] = new Option('#active#','1,2');
      document.forms['reportingPropertiesForm'].status.options[8] = new Option('#inactive#','-1,3,4');
    }
    
    this.searchForConversations = function () {
      var length = document.forms['reportingPropertiesForm'].status.options.length;
      if(length == 9) {
        return;
      }
      for (x = length ; x >= 0; x--) { 
					document.forms['reportingPropertiesForm'].status.options[x] = null;
    	}
    	
    	
    	
    	document.forms['reportingPropertiesForm'].status.options[0] = new Option('','');
		document.forms['reportingPropertiesForm'].status.options[2] = new Option('Active','2');
		document.forms['reportingPropertiesForm'].status.options[5] = new Option('Completed','9');
		document.forms['reportingPropertiesForm'].status.options[1] = new Option('Created','1');
		document.forms['reportingPropertiesForm'].status.options[3] = new Option('Error','-1');
  		document.forms['reportingPropertiesForm'].status.options[4] = new Option('Idle','4');
		document.forms['reportingPropertiesForm'].status.options[6] = new Option('Unkown','0');
		document.forms['reportingPropertiesForm'].status.options[7] = new Option('#active#','1,2,3,5,6,7,8');
		document.forms['reportingPropertiesForm'].status.options[8] = new Option('#inactive#','4,9,-1');
          
    	document.getElementById('messageIdText').className='NEXUSValueDisabled';
    	document.forms['reportingPropertiesForm'].messageEnabled.disabled=true;
    	document.forms['reportingPropertiesForm'].messageId.disabled=true;
  	}

    _container_.addOnLoad( function () {  
			var seqNo = 0;
			var msg = "";
	    var check = 0;      
	    for (var i = 0; i < 2; i++) {
				var checked = document.reportingPropertiesForm.searchFor[i].checked;
	      if (checked) {
					msg = (document.reportingPropertiesForm.searchFor[i].value);
				  check = 1;
				  break;
				}
			}
			if (check == 1) {
	    	if ( msg == "message" ) {
	        _container_.scriptScope.searchForMessages();
	        document.getElementById('messageIdText').className='NEXUSValue';
	        document.forms['reportingPropertiesForm'].messageEnabled.disabled=false;
	        document.forms['reportingPropertiesForm'].messageId.disabled=false;
	      } else {
	        _container_.scriptScope.searchForConversations();
          document.getElementById('messageIdText').className='NEXUSValueDisabled';
          document.forms['reportingPropertiesForm'].messageEnabled.disabled=true;
          document.forms['reportingPropertiesForm'].messageId.disabled=true;
        }
			}
		});
		
	this.selectAll = function (state) {
      var checkboxes = document.forms['reportingPropertiesForm'].selected;
      for (i = 0; i < checkboxes.length; i++) {     
      	checkboxes[i].checked = state; 
	  	}
	  }
          
  </script>

<nexus:helpBar helpDoc="html/NoHelpAvailable.html" />

<html:form action="ProcessConversationReport.do">
	<html:hidden property="applyProperties" value="true"/>
	<html:hidden property="command"/>

	<table class="NEXUS_TABLE" width="100%">
		<tr>
			<td><nexus:crumbs styleClass="NEXUSScreenPathLink">Reporting</nexus:crumbs></td>
		</tr>
		<tr>
			<td class="NEXUSScreenName">Conversation Reporting</td>
		</tr>
	</table>

	<table class="NEXUS_TABLE" width="100%" style="margin-bottom: 0pt;">
		<tr>
			<td class="NEXUSValue">Search for</td>
			<td class="NEXUSValue"><html:radio styleId="convSearch"
				onclick="javascript: scriptScope.disableLinks(); scriptScope.searchForConversations();"
				property="searchFor" value="conversation" />Conversations&nbsp;&nbsp;&nbsp;<html:radio
				onclick="javascript: scriptScope.disableLinks(); scriptScope.searchForMessages();"
				property="searchFor" value="message" />Messages</td>

			<td class="NEXUSValue">Choreography ID</td>
			<td class="NEXUSValue"><html:select
				onchange="javascript: scriptScope.disableLinks();"
				styleClass="fixedsize" property="choreographyId">
				<html-el:option value="" />
				<logic:notEmpty name="reportingPropertiesForm"
					property="choreographyIds">
					<logic:iterate id="choreographies" name="reportingPropertiesForm"
						property="choreographyIds">
						<html-el:option value="${choreographies}" />
					</logic:iterate>
				</logic:notEmpty>
			</html:select></td>
		</tr>
		<tr>
			<td class="NEXUSValue">Participant ID</td>
			<td class="NEXUSValue"><html:select
				onchange="javascript: scriptScope.disableLinks();"
				styleClass="fixedsize" property="participantId">
				<html-el:option value="" />
				<logic:notEmpty name="reportingPropertiesForm"
					property="participantIds">
					<logic:iterate id="participants" name="reportingPropertiesForm"
						property="participantIds">
						<html-el:option value="${participants}" />
					</logic:iterate>
				</logic:notEmpty>
			</html:select></td>
			<td class="NEXUSValue">Conversation ID <html:checkbox
				onchange="javascript: scriptScope.disableLinks();"
				name="reportingPropertiesForm" property="conversationEnabled" /></td>
			<td class="NEXUSValue"><html:text
				onchange="javascript: scriptScope.disableLinks();"
				styleClass="fixedsize" property="conversationId"></html:text></td>
		</tr>

		<tr>


			<td class="NEXUSValue">Status</td>
			<td class="NEXUSValue"><html:select
				onchange="javascript: scriptScope.disableLinks();"
				styleClass="fixedsize" property="status">
				<logic:equal name="reportingPropertiesForm" property="searchFor"
					value="message">
					<html:option value="" />
					<html:option value="2">Queued</html:option>
					<html:option value="-1">Error</html:option>
					<html:option value="4">Stopped</html:option>
					<html:option value="1">Retrying</html:option>
					<html:option value="30">Transmitted</html:option>
					<html:option value="0">Unknown</html:option>
					<html:option value="1,2">#active#</html:option>
					<html:option value="-1,3,4">#inactive#</html:option>
				</logic:equal>
				<logic:notEqual name="reportingPropertiesForm" property="searchFor"
					value="message">
					<html:option value="" />
					<html:option value="2">Active</html:option>
					<html:option value="9">Completed</html:option>
					<html:option value="1">Created</html:option>
					<html:option value="-1">Error</html:option>
					<html:option value="4">Idle</html:option>
					<html:option value="0">Unknown</html:option>
					<html:option value="1,2,3,5,6,7,8">#active#</html:option>
					<html:option value="4,9,-1">#inactive#</html:option>
				</logic:notEqual>
			</html:select></td>


			<td id="messageIdText" class="NEXUSValue">Message ID<html:checkbox
				onchange="javascript: scriptScope.disableLinks();"
				name="reportingPropertiesForm" property="messageEnabled" /></td>
			<td class="NEXUSValue"><html:text
				onchange="javascript: scriptScope.disableLinks();"
				styleClass="fixedsize" property="messageId"></html:text></td>
		</tr>

		<tr>
			<td class="NEXUSValue">Start Date <html:checkbox
				onchange="scriptScope.disableLinks();"
				name="reportingPropertiesForm" property="startEnabled" /></td>
			<td class="NEXUSValue" align="left"><html:select
				onchange="javascript: scriptScope.disableLinks();"
				property="startYear">
				<html:option value="2009" />
				<html:option value="2008" />
				<html:option value="2007" />
				<html:option value="2006" />
				<html:option value="2005" />
				<html:option value="2004" />
				<html:option value="2003" />
			</html:select> <html:select onchange="javascript: scriptScope.disableLinks();"
				property="startMonth">
				<html:option value="01">January</html:option>
				<html:option value="02">February</html:option>
				<html:option value="03">March</html:option>
				<html:option value="04">April</html:option>
				<html:option value="05">May</html:option>
				<html:option value="06">June</html:option>
				<html:option value="07">July</html:option>
				<html:option value="08">August</html:option>
				<html:option value="09">September</html:option>
				<html:option value="10">October</html:option>
				<html:option value="11">November</html:option>
				<html:option value="12">December</html:option>
			</html:select> <html:select onchange="javascript: scriptScope.disableLinks();"
				property="startDay">
				<html:option value="01">1</html:option>
				<html:option value="02">2</html:option>
				<html:option value="03">3</html:option>
				<html:option value="04">4</html:option>
				<html:option value="05">5</html:option>
				<html:option value="06">6</html:option>
				<html:option value="07">7</html:option>
				<html:option value="08">8</html:option>
				<html:option value="09">9</html:option>
				<html:option value="10">10</html:option>
				<html:option value="11">11</html:option>
				<html:option value="12">12</html:option>
				<html:option value="13">13</html:option>
				<html:option value="14">14</html:option>
				<html:option value="15">15</html:option>
				<html:option value="16">16</html:option>
				<html:option value="17">17</html:option>
				<html:option value="18">18</html:option>
				<html:option value="19">19</html:option>
				<html:option value="20">20</html:option>
				<html:option value="21">21</html:option>
				<html:option value="22">22</html:option>
				<html:option value="23">23</html:option>
				<html:option value="24">24</html:option>
				<html:option value="25">25</html:option>
				<html:option value="26">26</html:option>
				<html:option value="27">27</html:option>
				<html:option value="28">28</html:option>
				<html:option value="29">29</html:option>
				<html:option value="30">30</html:option>
				<html:option value="31">31</html:option>
			</html:select> <html:select onchange="javascript: scriptScope.disableLinks();"
				property="startHour">
				<html:option value="00">12 A.M.</html:option>
				<html:option value="01">1 A.M.</html:option>
				<html:option value="02">2 A.M.</html:option>
				<html:option value="03">3 A.M.</html:option>
				<html:option value="04">4 A.M.</html:option>
				<html:option value="05">5 A.M.</html:option>
				<html:option value="06">6 A.M.</html:option>
				<html:option value="07">7 A.M.</html:option>
				<html:option value="08">8 A.M.</html:option>
				<html:option value="09">9 A.M.</html:option>
				<html:option value="10">10 A.M.</html:option>
				<html:option value="11">11 A.M.</html:option>
				<html:option value="12">12 P.M.</html:option>
				<html:option value="13">1 P.M.</html:option>
				<html:option value="14">2 P.M.</html:option>
				<html:option value="15">3 P.M.</html:option>
				<html:option value="16">4 P.M.</html:option>
				<html:option value="17">5 P.M.</html:option>
				<html:option value="18">6 P.M.</html:option>
				<html:option value="19">7 P.M.</html:option>
				<html:option value="20">8 P.M.</html:option>
				<html:option value="21">9 P.M.</html:option>
				<html:option value="22">10 P.M.</html:option>
				<html:option value="23">11 P.M.</html:option>
			</html:select> <html:select onchange="javascript: scriptScope.disableLinks();"
				property="startMin">
				<html:option value="00">0</html:option>
				<html:option value="10">10</html:option>
				<html:option value="20">20</html:option>
				<html:option value="30">30</html:option>
				<html:option value="40">40</html:option>
				<html:option value="50">50</html:option>

			</html:select></td>

			<td class="NEXUSValue">End Date <html:checkbox
				onchange="javascript: scriptScope.disableLinks();"
				name="reportingPropertiesForm" property="endEnabled" /></td>
			<td class="NEXUSValue" align="left"><html:select
				onchange="javascript: scriptScope.disableLinks();"
				property="endYear">
				<html:option value="2009" />
				<html:option value="2008" />
				<html:option value="2007" />
				<html:option value="2006" />
				<html:option value="2005" />
				<html:option value="2004" />
				<html:option value="2003" />
			</html:select> <html:select onchange="javascript: scriptScope.disableLinks();"
				property="endMonth">
				<html:option value="01">January</html:option>
				<html:option value="02">February</html:option>
				<html:option value="03">March</html:option>
				<html:option value="04">April</html:option>
				<html:option value="05">May</html:option>
				<html:option value="06">June</html:option>
				<html:option value="07">July</html:option>
				<html:option value="08">August</html:option>
				<html:option value="09">September</html:option>
				<html:option value="10">October</html:option>
				<html:option value="11">November</html:option>
				<html:option value="12">December</html:option>
			</html:select> <html:select onchange="javascript: scriptScope.disableLinks();"
				property="endDay">
				<html:option value="01">1</html:option>
				<html:option value="02">2</html:option>
				<html:option value="03">3</html:option>
				<html:option value="04">4</html:option>
				<html:option value="05">5</html:option>
				<html:option value="06">6</html:option>
				<html:option value="07">7</html:option>
				<html:option value="08">8</html:option>
				<html:option value="09">9</html:option>
				<html:option value="10">10</html:option>
				<html:option value="11">11</html:option>
				<html:option value="12">12</html:option>
				<html:option value="13">13</html:option>
				<html:option value="14">14</html:option>
				<html:option value="15">15</html:option>
				<html:option value="16">16</html:option>
				<html:option value="17">17</html:option>
				<html:option value="18">18</html:option>
				<html:option value="19">19</html:option>
				<html:option value="20">20</html:option>
				<html:option value="21">21</html:option>
				<html:option value="22">22</html:option>
				<html:option value="23">23</html:option>
				<html:option value="24">24</html:option>
				<html:option value="25">25</html:option>
				<html:option value="26">26</html:option>
				<html:option value="27">27</html:option>
				<html:option value="28">28</html:option>
				<html:option value="29">29</html:option>
				<html:option value="30">30</html:option>
				<html:option value="31">31</html:option>

			</html:select> <html:select onchange="javascript: scriptScope.disableLinks();"
				property="endHour">
				<html:option value="00">12 A.M.</html:option>
				<html:option value="01">1 A.M.</html:option>
				<html:option value="02">2 A.M.</html:option>
				<html:option value="03">3 A.M.</html:option>
				<html:option value="04">4 A.M.</html:option>
				<html:option value="05">5 A.M.</html:option>
				<html:option value="06">6 A.M.</html:option>
				<html:option value="07">7 A.M.</html:option>
				<html:option value="08">8 A.M.</html:option>
				<html:option value="09">9 A.M.</html:option>
				<html:option value="10">10 A.M.</html:option>
				<html:option value="11">11 A.M.</html:option>
				<html:option value="12">12 P.M.</html:option>
				<html:option value="13">1 P.M.</html:option>
				<html:option value="14">2 P.M.</html:option>
				<html:option value="15">3 P.M.</html:option>
				<html:option value="16">4 P.M.</html:option>
				<html:option value="17">5 P.M.</html:option>
				<html:option value="18">6 P.M.</html:option>
				<html:option value="19">7 P.M.</html:option>
				<html:option value="20">8 P.M.</html:option>
				<html:option value="21">9 P.M.</html:option>
				<html:option value="22">10 P.M.</html:option>
				<html:option value="23">11 P.M.</html:option>
			</html:select> <html:select onchange="javascript: scriptScope.disableLinks();"
				property="endMin">
				<html:option value="00">0</html:option>
				<html:option value="10">10</html:option>
				<html:option value="20">20</html:option>
				<html:option value="30">30</html:option>
				<html:option value="40">40</html:option>
				<html:option value="50">50</html:option>

			</html:select></td>
		</tr>
	</table>

	<table class="NEXUS_BUTTON_TABLE" width="100%">
		<tr>
			<td class="BUTTON_LEFT"><nobr><a class="NexusHeaderLink"
				href="#"
				onclick="javascript: scriptScope.Clear(); scriptScope.disableLinks();"><img
				src="images/reset.gif" name="clearButton" class="button">Reset
			Fields</a></nobr></td>
			<td width="100%">
			<center><logic:equal name="reportingPropertiesForm"
				property="firstActive" value="true">
				<nexus:submit id="startLink"
					onClick="document.forms['reportingPropertiesForm'].command.value='first';"
					styleClass="NexusLink">Start</nexus:submit>
			</logic:equal> <logic:equal name="reportingPropertiesForm" property="firstActive"
				value="false">Start</logic:equal> | <logic:equal
				name="reportingPropertiesForm" property="prevActive" value="true">
				<nexus:submit id="previousLink"
					onClick="document.forms['reportingPropertiesForm'].command.value='back';"
					styleClass="NexusLink">Previous</nexus:submit>
			</logic:equal> <logic:equal name="reportingPropertiesForm" property="prevActive"
				value="false">Previous</logic:equal> | <bean:write
				name="reportingPropertiesForm" property="startCount" /> - <bean:write
				name="reportingPropertiesForm" property="endCount" /> of <bean:write
				name="reportingPropertiesForm" property="allItemsCount" /> | <logic:equal
				name="reportingPropertiesForm" property="nextActive" value="true">
				<nexus:submit id="nextLink"
					onClick="document.forms['reportingPropertiesForm'].command.value='next';"
					styleClass="NexusLink">Next</nexus:submit>
			</logic:equal> <logic:equal name="reportingPropertiesForm" property="nextActive"
				value="false">Next</logic:equal> | <logic:equal
				name="reportingPropertiesForm" property="lastActive" value="true">
				<nexus:submit id="endLink"
					onClick="document.forms['reportingPropertiesForm'].command.value='last';"
					styleClass="NexusLink">End</nexus:submit>
			</logic:equal> <logic:equal name="reportingPropertiesForm" property="lastActive"
				value="false">End</logic:equal></center>
			</td>
			<td class="BUTTON_RIGHT">
				<nobr>
					<nexus:submit onClick="javascript: document.forms['reportingPropertiesForm'].command.value='first'; scriptScope.enableLinks();">
						<img src="images/submit.gif" name="resultsButton" class="button" />Refresh Results
					</nexus:submit>
				</nobr>
			</td>
		</tr>
	</table>

	<logic:messagesPresent>
		<div class="NexusError"><html:errors /></div>
	</logic:messagesPresent>

	<logic:equal name="reportingPropertiesForm" property="searchFor"
		value="message">

		<logic:notEmpty name="collection">
			<table class="NEXUS_TABLE" width="100%">
				<tr>
					<logic:equal name="reportingSettingsForm"
						property="messColSelect" value="true">
						<th class="NEXUSSection"></th>
					</logic:equal>
					<th class="NEXUSSection">Message ID</th>
					<logic:equal name="reportingSettingsForm"
						property="messColParticipantId" value="true">
						<th class="NEXUSSection">Participant ID</th>
					</logic:equal>
					<logic:equal name="reportingSettingsForm"
						property="messColStatus" value="true">
						<th class="NEXUSSection">Status</th>
					</logic:equal>
					<logic:equal name="reportingSettingsForm" property="messColType"
						value="true">
						<th class="NEXUSSection">Message Type</th>
					</logic:equal>
					<logic:equal name="reportingSettingsForm"
						property="messColAction" value="true">
						<th class="NEXUSSection">Action</th>
					</logic:equal>
					<logic:equal name="reportingSettingsForm"
						property="messColCreated" value="true">
						<th class="NEXUSSection">Date Created</th>
					</logic:equal>
					<logic:equal name="reportingSettingsForm"
						property="messColTurnaround" value="true">
						<th class="NEXUSSection">Turnaround Time</th>
					</logic:equal>
				</tr>
				<logic:iterate indexId="counter" id="message" name="collection">
					<tr>

						<logic:equal name="reportingSettingsForm"
							property="messColSelect" value="true">
							<td class="NEXUSValue"><html-el:multibox
								name="reportingSettingsForm" property="selected"
								value="${message.participantId}|${message.choreographyId}|${message.conversationId}|${message.messageId}" /></td>
						</logic:equal>
						<td class="NEXUSValue"><nexus:link
							href="MessageView.do?mId=${message.messageId}&convId=${message.conversationId}&chorId=${message.choreographyId}&partnerId=${message.participantId}">
							<bean:write name="message" property="messageId" />
						</nexus:link></td>
						<logic:equal name="reportingSettingsForm"
							property="messColParticipantId" value="true">
							<td class="NEXUSValue"><bean:write name="message"
								property="participantId" /></td>
						</logic:equal>
						<logic:equal name="reportingSettingsForm"
							property="messColStatus" value="true">
							<td class="NEXUSValue"><bean:write name="message"
								property="status" /></td>
						</logic:equal>
						<logic:equal name="reportingSettingsForm" property="messColType"
							value="true">
							<td class="NEXUSValue"><bean:write name="message"
								property="type" /></td>
						</logic:equal>
						<logic:equal name="reportingSettingsForm"
							property="messColAction" value="true">
							<td class="NEXUSValue"><bean:write name="message"
								property="action" /></td>
						</logic:equal>
						<logic:equal name="reportingSettingsForm"
							property="messColCreated" value="true">
							<td class="NEXUSValue"><bean:write name="message"
								property="createdDate" /></td>
						</logic:equal>
						<logic:equal name="reportingSettingsForm"
							property="messColTurnaround" value="true">
							<td class="NEXUSValue"><bean:write name="message"
								property="turnaroundTime" /></td>
						</logic:equal>
					</tr>
				</logic:iterate>
			</table>
			<logic:equal name="reportingSettingsForm" property="messColSelect"
				value="true">
				<table class="NEXUS_BUTTON_TABLE" width="100%">
					<tr>
						<td class="BUTTON_LEFT"><a href="#" id="startLink"
							onclick="javascript: scriptScope.selectAll(true);"
							class="NexusLink" styleClass="white-space:nowrap;">Select all</a></td>
						<td class="BUTTON_LEFT"><a href="#" id="startLink"
							onclick="javascript: scriptScope.selectAll(false);"
							class="NexusLink" styleClass="white-space:nowrap;">Deselect
						all</a></td>
						<td class="BUTTON_RIGHT"><nobr><nexus:submit
							onClick="document.forms['reportingPropertiesForm'].command.value='requeue';">
							<img src="images/submit.gif" name="clearButton" class="button">
							Re-Queue</nexus:submit></nobr></td>
						<td class="BUTTON_RIGHT"><nobr><nexus:submit
							onClick="document.forms['reportingPropertiesForm'].command.value='stop';">
							<img src="images/reset.gif" name="clearButton" class="button">
							Stop</nexus:submit></nobr></td>
					</tr>
				</table>
			</logic:equal>
		</logic:notEmpty>
	</logic:equal>
	<logic:notEqual name="reportingPropertiesForm" property="searchFor"
		value="message">
		<logic:notEmpty name="collection">
			<table class="NEXUS_TABLE" width="100%">
				<tr>
					<logic:equal name="reportingSettingsForm"
						property="convColSelect" value="true">
						<th class="NEXUSSection"></th>
					</logic:equal>
					<logic:equal name="reportingSettingsForm"
						property="convColChorId" value="true">
						<th class="NEXUSSection">Choreography ID</th>
					</logic:equal>
					<th class="NEXUSSection">Conversation ID</th>
					<logic:equal name="reportingSettingsForm"
						property="convColPartId" value="true">
						<th class="NEXUSSection">Participant ID</th>
					</logic:equal>
					<logic:equal name="reportingSettingsForm"
						property="convColStatus" value="true">
						<th class="NEXUSSection">Status</th>
					</logic:equal>
					<logic:equal name="reportingSettingsForm"
						property="convColAction" value="true">
						<th class="NEXUSSection">Current Action</th>
					</logic:equal>
					<logic:equal name="reportingSettingsForm"
						property="convColCreated" value="true">
						<th class="NEXUSSection">Date Created</th>
					</logic:equal>
					<logic:equal name="reportingSettingsForm"
						property="convColTurnaround" value="true">
						<th class="NEXUSSection">Turnaround Time</th>
					</logic:equal>
				</tr>
				<logic:iterate indexId="counter" id="conv" name="collection">
					<tr>
						<logic:equal name="reportingSettingsForm"
							property="convColSelect" value="true">
							<td class="NEXUSValue"><html-el:multibox
								name="reportingSettingsForm" property="selected"
								value="${conv.participantId}|${conv.choreographyId}|${conv.conversationId}" /></td>
						</logic:equal>
						<logic:equal name="reportingSettingsForm"
							property="convColChorId" value="true">
							<td class="NEXUSValue"><bean:write name="conv"
								property="choreographyId" /></td>
						</logic:equal>
						<td class="NEXUSValue"><nexus:link
							href="ConversationView.do?convId=${conv.conversationId}&choreographyId=${conv.choreographyId}&partnerId=${conv.participantId}">
							<bean:write name="conv" property="conversationId" />
						</nexus:link></td>
						<logic:equal name="reportingSettingsForm"
							property="convColPartId" value="true">
							<td class="NEXUSValue"><bean:write name="conv"
								property="participantId" /></td>
						</logic:equal>
						<logic:equal name="reportingSettingsForm"
							property="convColStatus" value="true">
							<td class="NEXUSValue"><bean:write name="conv"
								property="status" /></td>
						</logic:equal>
						<logic:equal name="reportingSettingsForm"
							property="convColAction" value="true">
							<td class="NEXUSValue"><bean:write name="conv"
								property="action" /></td>
						</logic:equal>
						<logic:equal name="reportingSettingsForm"
							property="convColCreated" value="true">
							<td class="NEXUSValue"><bean:write name="conv"
								property="createdDate" /></td>
						</logic:equal>
						<logic:equal name="reportingSettingsForm"
							property="convColTurnaround" value="true">
							<td class="NEXUSValue"><bean:write name="conv"
								property="turnaroundTime" /></td>
						</logic:equal>
					</tr>
				</logic:iterate>
			</table>
			<logic:equal name="reportingSettingsForm" property="convColSelect" value="true">
				<table class="NEXUS_BUTTON_TABLE" width="100%" border="1">
					<tr>
						<td class="BUTTON_LEFT" width="75px"><a href=#" id="startLink"
							onClick="javascript: scriptScope.selectAll(true);return false;" class="NexusLink">
							<nobr>Select all</nobr>
						</a><br /><a href="#" id="startLink"
							onClick="javascript: scriptScope.selectAll(false)" class="NexusLink">
							<nobr>Deselect all</nobr>
						</a></td>
						<td  class="BUTTON_RIGHT"><nexus:submit
							onClick="document.forms['reportingPropertiesForm'].command.value='delete';">
							<img src="images/reset.gif" name="clearButton" class="button">
						Delete</nexus:submit></td>
					</tr>
				</table>
			</logic:equal>
		</logic:notEmpty>
	</logic:notEqual>

</html:form>