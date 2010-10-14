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
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-nested" prefix="nested" %>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %> 
<%@ taglib uri="/tags/nexus" prefix="nexus" %>

<% /*<nexus:helpBar helpDoc="documentation/Components.htm"/> */ %>

		<table class="NEXUS_TABLE" width="100%">
	    <tr>
	        <td>
	        	<nexus:crumbs/>
	        </td>
	    </tr>
	    <tr>
	        <td class="NEXUSScreenName">Components</td>
	    </tr>
		</table>

    <table class="NEXUS_TABLE" width="100%">
        <tr>
            <td class="NEXUSSection">Component Name</td>
            <td class="NEXUSSection">ClassName</td>
            <td class="NEXUSSection">Description</td>
        </tr>
  
    <logic:iterate id="component" name="collection"> 
    	<tr>
            <td class="NEXUSName"><nexus:link href="ComponentView.do?nxComponentId=${component.nxComponentId}" styleClass="NexusLink"><bean:write name="component" property="name"/></nexus:link></td>
            <td class="NEXUSName"><bean:write name="component" property="className"/></td>
            <td class="NEXUSName"><bean:write name="component" property="description"/></td>
            
        </tr>
    </logic:iterate>

    </table>

      <table class="NEXUS_BUTTON_TABLE">
        <tr>
     			<td>
			    	&nbsp;
    			</td>
	        <td class="BUTTON_RIGHT" style="text-align: right;">
				    <nexus:link href="ComponentAdd.do?nxComponentId=0" styleClass="button"><img src="images/icons/add.png" class="button">Add Component</nexus:link>
    			</td>
      	</tr>
      </table>

    <center> 
      <logic:messagesPresent> 
        <div class="NexusError"><html:errors/></div> 
        </logic:messagesPresent>
    </center>