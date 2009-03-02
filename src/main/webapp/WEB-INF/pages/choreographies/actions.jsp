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
<%@ taglib uri="/tags/nexus" prefix="nexus" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-nested" prefix="nested" %>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>


<% /*<nexus:helpBar helpDoc="documentation/Action.htm"/> */ %>

  	<table class="NEXUS_TABLE" width="100%">
		    <tr>
		        <td>
		        	<nexus:crumbs/>
		        </td>
		    </tr>
		    <tr>
		        <td class="NEXUSScreenName">Actions</td>
		    </tr>
		</table>
    <table class="NEXUS_TABLE" width="100%">
        <tr>
            <td class="NEXUSSection">Action ID</td>
            <td class="NEXUSSection"></td>
        </tr>
    <logic:iterate id="action" name="collection"> 
          <tr>
              <td class="NEXUSName"><nexus:link href="ActionSettingsView.do?nxChoreographyId=${action.nxChoreographyId}&nxActionId=${action.nxActionId}" styleClass="NexusLink"><bean:write name="action" property="actionId"/></nexus:link></td>
              <td class="NEXUSName"></td>
          </tr>
    </logic:iterate>
    </table>
  <center>  
      <logic:messagesPresent>  
        <div class="NexusError"><html:errors/></div>  
      </logic:messagesPresent> 
      <logic:messagesPresent message="true">
        <html:messages id="msg" message="true">
          <div class="NexusMessage"><bean:write name="msg"/></div><br/>
        </html:messages>
      </logic:messagesPresent>
    </center>
    <table class="NEXUS_BUTTON_TABLE" width="100%">
        <tr>
            <td>&nbsp;</td>
            <td class="BUTTON_RIGHT"><nexus:link href="ActionAdd.do?nxChoreographyId=${nxChoreographyId}" styleClass="NexusHeaderLink">
              <img src="images/icons/add.png" border="0" alt="" class="button">Add Action</nexus:link></td>
        </tr>
    </table>
