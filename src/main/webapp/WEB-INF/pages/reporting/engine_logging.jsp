<%@ taglib uri="/tags/struts-tiles" prefix="tiles" %>  
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-nested" prefix="nested" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %> 
<%@ taglib uri="/tags/struts-logic-el" prefix="logic-el" %>
<%@ taglib uri="/tags/struts-html-el" prefix="html-el" %>
<%@ taglib uri="/tags/nexus" prefix="nexus" %>

<% /*<nexus:helpBar helpDoc="documentation/Engine_Reporting.htm"/> */ %>

<html:form action="ProcessEngineLog.do">

        <html:hidden property="command"/>
        <html:hidden property="applyProperties" value="true"/>

        <table class="NEXUS_TABLE" width="100%">
            <tr>
                <td><nexus:crumbs styleClass="NEXUSScreenPathLink">Reporting</nexus:crumbs></td>
            </tr>
            <tr>
                <td class="NEXUSScreenName">Engine Logging</td>
            </tr>
        </table>

        <table class="NEXUS_TABLE" width="100%" style="margin-bottom: 0pt;">
            <tr>
                <td class="NEXUSValue">Severity</td>                  
                <td class="NEXUSValue">
                  <html:select onchange="javascript: scriptScope.disableLinks();" styleClass="fixedsize" property="severity">
                      <html:option value=""/>
                      <html:option value='<%=org.apache.log4j.Level.ERROR.toInt()+"" %>'>Error</html:option>
                      <html:option value='<%=org.apache.log4j.Level.WARN.toInt()+"" %>'>Warn</html:option>
                      <html:option value='<%=org.apache.log4j.Level.INFO.toInt()+"" %>'>Info</html:option>
                      <html:option value='<%=org.apache.log4j.Level.DEBUG.toInt()+"" %>'>Debug</html:option>
                      <html:option value='<%=org.apache.log4j.Level.TRACE.toInt()+"" %>'>Trace</html:option>
                 </html:select>
                </td>
                <td class="NEXUSValue">Message Text <html:checkbox onclick="javascript: scriptScope.disableLinks();" name="reportingPropertiesForm" property="messageTextEnabled"/></td>
                <td class="NEXUSValue">
                  <html:text onkeydown="javascript: scriptScope.disableLinks();" styleClass="fixedsize" property="messageText"></html:text>
                </td>
            </tr>
            
            <tr>
                <td class="NEXUSValue">Start Date <html:checkbox name="reportingPropertiesForm" property="startEnabled"/></td>
                <td class="NEXUSValue" align="left">
                <html:select onchange="javascript: scriptScope.disableLinks();" property="startYear">
                    <html:option value="2012" />
                    <html:option value="2011" />
                    <html:option value="2010" />
                    <html:option value="2009"/>
                    <html:option value="2008"/>
                    <html:option value="2007"/>
                    <html:option value="2006"/>
                    <html:option value="2005"/>
                    <html:option value="2004"/>
                    <html:option value="2003"/>
                </html:select>
                <html:select onchange="javascript: scriptScope.disableLinks();" property="startMonth">
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
                </html:select>
                <html:select onchange="javascript: scriptScope.disableLinks();" property="startDay">
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
                </html:select>
                <html:select onchange="javascript: scriptScope.disableLinks();" property="startHour">
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
                </html:select>
              <html:select onchange="javascript: scriptScope.disableLinks();" property="startMin">
              <html:option value="00">0</html:option>
               <html:option value="10">10</html:option>
                 <html:option value="20">20</html:option>
                 <html:option value="30">30</html:option>
                 <html:option value="40">40</html:option>
                 <html:option value="50">50</html:option>
                 
                </html:select></td>
              
              <td class="NEXUSValue">End Date <html:checkbox name="reportingPropertiesForm" property="endEnabled"/></td>
                <td class="NEXUSValue" align="left">
                <html:select onchange="javascript: scriptScope.disableLinks();" property="endYear">
                    <html:option value="2012" />
                    <html:option value="2011" />
                    <html:option value="2010" />
                    <html:option value="2009"/>
                    <html:option value="2008"/>
                    <html:option value="2007"/>
                    <html:option value="2006"/>
                    <html:option value="2005"/>
                    <html:option value="2004"/>
                    <html:option value="2003"/>
                </html:select>
                <html:select onchange="javascript: scriptScope.disableLinks();" property="endMonth">
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
                </html:select>
                <html:select onchange="javascript: scriptScope.disableLinks();" property="endDay">
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
                
                </html:select>
                <html:select onchange="javascript: scriptScope.disableLinks();" property="endHour">
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
                </html:select>
                <html:select onchange="javascript: scriptScope.disableLinks();" property="endMin">
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
				<td class="BUTTON_LEFT"><nobr><a class="button"
					href="#"
					onclick="javascript: scriptScope.Clear(); scriptScope.disableLinks();"><img
					src="images/icons/arrow_rotate_anticlockwise.png" name="clearButton" class="button">Reset
					Fields</a></nobr>
				</td>
                <td width="100%"><center>
                <logic:equal name="reportingPropertiesForm" property="firstActive" value="true"><nexus:submit id="startLink" onClick="document.forms['reportingPropertiesForm'].command.value='first';" styleClass="NexusHeaderLink">Start</nexus:submit></logic:equal>
                <logic:equal name="reportingPropertiesForm" property="firstActive" value="false">Start</logic:equal>
                |
                <logic:equal name="reportingPropertiesForm" property="prevActive" value="true"><nexus:submit id="previousLink" onClick="document.forms['reportingPropertiesForm'].command.value='back';" styleClass="NexusHeaderLink">Previous</nexus:submit></logic:equal>
                <logic:equal name="reportingPropertiesForm" property="prevActive" value="false">Previous</logic:equal>
                | <bean:write name="reportingPropertiesForm" property="startCount"/> - <bean:write name="reportingPropertiesForm" property="endCount"/> of <bean:write name="reportingPropertiesForm" property="allItemsCount"/> |
                <logic:equal name="reportingPropertiesForm" property="nextActive" value="true"><nexus:submit id="nextLink" onClick="document.forms['reportingPropertiesForm'].command.value='next';" styleClass="NexusHeaderLink">Next</nexus:submit></logic:equal>
                <logic:equal name="reportingPropertiesForm" property="nextActive" value="false">Next</logic:equal>
                |
                <logic:equal name="reportingPropertiesForm" property="lastActive" value="true"><nexus:submit id="endLink" onClick="document.forms['reportingPropertiesForm'].command.value='last';" styleClass="NexusHeaderLink">End</nexus:submit></logic:equal>
                <logic:equal name="reportingPropertiesForm" property="lastActive" value="false">End</logic:equal>
                </center></td>
				<td class="BUTTON_RIGHT">
					<nobr>
						<nexus:submit onClick="javascript: document.forms['reportingPropertiesForm'].command.value='first'; scriptScope.enableLinks();">
							<img src="images/icons/tick.png" name="resultsButton" class="button" />Refresh Results
						</nexus:submit>
					</nobr>
				</td>
            </tr>
        </table>
        <script language="JavaScript" type="text/javascript">
        
          this.Clear = function () {
            document.forms['reportingPropertiesForm'].severity.options[0].selected = "true";
            document.forms['reportingPropertiesForm'].messageTextEnabled.checked = null;
            document.forms['reportingPropertiesForm'].messageText.value = "";
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
        	link.style.cursor = 'default';
       	}
        
				link.className='NexusLinkDisabled';
      }
      
      this.enableLink = function (link) {
        if(link == null) {
          return;
        }
        link.onclick = link.oldOnClick ? link.oldOnClick : null;
        if (link.style) {
        	link.style.cursor = document.all ? 'hand' : 'pointer';
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
      </script> 
   </html:form> 
  

<logic:notEmpty name="collection">
  <table class="NEXUS_TABLE" width="100%">
        <tr>
            <logic:equal name="reportingSettingsForm" property="engineColSeverity" value="true"><th class="NEXUSSection">Severity</th></logic:equal>
            <logic:equal name="reportingSettingsForm" property="engineColIssued" value="true"><th class="NEXUSSection">Issued Date</th></logic:equal>
            <th class="NEXUSSection">Description</th>
            <logic:equal name="reportingSettingsForm" property="engineColOrigin" value="true"><th class="NEXUSSection">Origin</th></logic:equal>
            <logic:equal name="reportingSettingsForm" property="engineColClassName" value="true"><th class="NEXUSSection">Class Name</th></logic:equal>
            <logic:equal name="reportingSettingsForm" property="engineColmethodName" value="true"><th class="NEXUSSection">Method Name</th></logic:equal>
        </tr>
    <logic:iterate indexId="counter" id="conv" name="collection">
        <tr>                  
            <logic:equal name="reportingSettingsForm" property="engineColSeverity" value="true"><td class="NEXUSValue"><bean:write name="conv" property="severity"/></td></logic:equal>
            <logic:equal name="reportingSettingsForm" property="engineColIssued" value="true"><td class="NEXUSValue"><bean:write name="conv" property="issuedDate"/></td></logic:equal>
            <td class="NEXUSValue"><bean:write name="conv" property="description"/></td>
            <logic:equal name="reportingSettingsForm" property="engineColOrigin" value="true"><td class="NEXUSValue"></td></logic:equal>
            <logic:equal name="reportingSettingsForm" property="engineColClassName" value="true"><td class="NEXUSValue"><bean:write name="conv" property="className"/></td></logic:equal>
            <logic:equal name="reportingSettingsForm" property="engineColmethodName" value="true"><td class="NEXUSValue"><bean:write name="conv" property="methodName"/></td></logic:equal>
        </tr>
    </logic:iterate>
  </table>
</logic:notEmpty>
