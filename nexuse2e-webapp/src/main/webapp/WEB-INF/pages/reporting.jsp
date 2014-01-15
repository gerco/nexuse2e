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

<% /*<nexus:helpBar helpDoc="documentation/Reporting.htm" /> */ %>

<table class="NEXUS_TABLE" width="100%">
	<tr>
		<td><nexus:crumbs /></td>
	</tr>
	<tr>
		<td class="NEXUSScreenName">Reporting</td>
	</tr>
</table>


<html:form action="ReportingForward.do">
    <!--
    <table class="NEXUS_TABLE" width="100%">
        <tr>
            <td class="NEXUSSection">Reporting Tools</td>
        </tr>
        <tr>
            <td class="NEXUSName"><a class="NexusLink" href="" onclick="javascript: document.reportingPropertiesForm.command.value='transaction'; document.reportingPropertiesForm.noReset.value='true'; document.reportingPropertiesForm.submit()">Transaction Reporting</a></td>
        </tr>
        <tr>
            <td class="NEXUSName"><a class="NexusLink" href="" onclick="javascript: document.reportingPropertiesForm.command.value='engine'; document.reportingPropertiesForm.noReset.value='true'; document.reportingPropertiesForm.submit()">Engine Logging</a></td>
        </tr>
    </table>
         -->
 
  
      <table class="NEXUS_TABLE" width="100%">
          <tr>
              <td colspan="2" class="NEXUSScreenName">Result Settings</td>
          </tr>
          <tr>
            <td class="NEXUSValue">Rows per Page <html:select style="width: 100;" property="pageSize">
                <html:option value="10"/>
                <html:option value="20"/>
                <html:option value="30"/>
                <html:option value="40"/>
                <html:option value="50"/>
                <html:option value="60"/>
                <html:option value="70"/>
                <html:option value="80"/>
                <html:option value="90"/>
                <html:option value="100"/>
              </html:select>
            </td>
            <td class="NEXUSValue">Timezone <html:select property="timezone">
                <html:option value="">Local System Time (depends on Systemsettings)</html:option>
                <html:option value="GMT">UTC</html:option>
                <html:option value="GMT+2">CEST Central European Summer Time</html:option>
                <html:option value="GMT+1">CET Central European Time</html:option>
                <html:option value="GMT-4">AST Atlantic Standard Time (Puerto Rico)</html:option>
                <html:option value="GMT-3">ADT Atlantic Daylight Time</html:option>
                <html:option value="GMT-5">EST Eastern Standard Time (New York)</html:option>
                <html:option value="GMT-4">EDT Eastern Daylight Time</html:option>
                <html:option value="GMT-6">CST Central Standard Time (Chicago, Dallas)</html:option>
                <html:option value="GMT-5">CDT Central Daylight Time</html:option>
                <html:option value="GMT-7">MST Mountain Standard Time (Denver)</html:option>
                <html:option value="GMT-6">MDT Mountain Daylight Time</html:option>
                <html:option value="GMT-8">PST Pacific Standard Time (Los Angeles)</html:option>
                <html:option value="GMT-7">PDT Pacific Daylight Time</html:option>
                <html:option value="GMT-10">HST Hawaii-Aleutian Standard Time</html:option>
                
              </html:select>
            </td>
          </tr>
      </table>
      <table class="NEXUS_TABLE" width="100%" style="margin-bottom: 0pt;">
          <tr>
              <td colspan="3" class="NEXUSSection">Displayed Rows for Conversations</td>
          </tr>
          
          
          <tr>
            <td class="NEXUSValue"><html:checkbox property="convColSelect"></html:checkbox>Selection Checkbox</td>
            <td class="NEXUSValue">
            <input type="checkbox" disabled="true" checked="checked"></input>
            <!--<html:checkbox property="convColConId"></html:checkbox>-->Conversation ID</td>
            <td class="NEXUSValue"><html:checkbox property="convColPartId"></html:checkbox>Participant ID</td>
          </tr>
          <tr>
            <td class="NEXUSValue"><html:checkbox property="convColChorId"></html:checkbox>Choreorgraphy ID</td>
            <td class="NEXUSValue"><html:checkbox property="convColAction"></html:checkbox>Current Action</td>
            <td class="NEXUSValue"><html:checkbox property="convColCreated"></html:checkbox>Date Created</td>
          </tr>
          <tr>
            <td class="NEXUSValue"><html:checkbox property="convColTurnaround"></html:checkbox>Turnaround Time</td>
            <td class="NEXUSValue"><html:checkbox property="convColStatus"></html:checkbox>Status</td>
            <td class="NEXUSValue">&nbsp;</td>
          </tr>         
    </table>    
    <table class="NEXUS_TABLE" width="100%" style="margin-bottom: 0pt;">
          <tr>
              <td colspan="3" class="NEXUSSection">Displayed Rows for Messages</td>
          </tr>
          <tr>
            <td class="NEXUSValue"><html:checkbox property="messColSelect"></html:checkbox>Selection Checkbox</td>
            <td class="NEXUSValue">
            <input type="checkbox" disabled="true" checked="checked"></input>
            <!--<html:checkbox property="messColMessageId"></html:checkbox>-->Message ID</td>
            <td class="NEXUSValue"><html:checkbox property="messColParticipantId"></html:checkbox>Participant ID</td>
          </tr>
          <tr>
            <td class="NEXUSValue"><html:checkbox property="messColStatus"></html:checkbox>Status</td>
            <td class="NEXUSValue"><html:checkbox property="messColBackendStatus"></html:checkbox>Backend Status</td>
            <td class="NEXUSValue"><html:checkbox property="messColType"></html:checkbox>Type</td>
          </tr>
          <tr>
            <td class="NEXUSValue"><html:checkbox property="messColCreated"></html:checkbox>Created Date</td>
            <td class="NEXUSValue"><html:checkbox property="messColTurnaround"></html:checkbox>Turnaround Time</td>
            <td class="NEXUSValue"><html:checkbox property="messColAction"></html:checkbox>Action</td>
          </tr>         
    </table>
    <table class="NEXUS_TABLE" width="100%" style="margin-bottom: 0pt;">
          <tr>
              <td colspan="3" class="NEXUSSection">Displayed Rows for Engine Logentries</td>
          </tr>         
          <tr>
            <td class="NEXUSValue"><html:checkbox property="engineColSeverity"></html:checkbox>Severity</td>
            <td class="NEXUSValue"><html:checkbox property="engineColIssued"></html:checkbox>Issued Date</td>
            
            <td class="NEXUSValue"><input type="checkbox" disabled="true" checked="checked"></input>
            <!--<html:checkbox property="engineColDescription"></html:checkbox>-->Description</td>
          </tr>
          <tr>
            <td class="NEXUSValue"><html:checkbox property="engineColOrigin"></html:checkbox>Origin</td>
            <td class="NEXUSValue"><html:checkbox property="engineColClassName"></html:checkbox>Class Name</td>
            <td class="NEXUSValue"><html:checkbox property="engineColmethodName"></html:checkbox>Method Name</td>
          </tr>         
    </table>
    
    <table class="NEXUS_BUTTON_TABLE" width="100%">
            <tr>
               
                <td class="BUTTON_RIGHT">
                
                	<nexus:submit onClick="document.reportingPropertiesForm.command.value='saveFields';" styleClass="button"><img src="images/icons/tick.png" class="button">Save Settings</nexus:submit>
				
                  
            </td>
            
      		</tr>
        </table>
    	<html:hidden property="command"/>
        <html:hidden property="startEnabled"/>
        <html:hidden property="endEnabled"/>
        <html:hidden property="noReset" value="false"/>
  </html:form>

<center><logic:messagesPresent>
	<div class="NexusError"><html:errors /></div>
</logic:messagesPresent></center>
