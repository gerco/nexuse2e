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
<%@ taglib uri="/tags/struts-html-el" prefix="html-el"%>

<% /*<nexus:helpBar helpDoc="documentation/Collaboration_Partners.htm"/> */ %>

    <table class="NEXUS_TABLE" width="100%">
        <tr>
            <td><nexus:crumbs styleClass="NEXUSScreenPathLink"></nexus:crumbs></td>
        </tr>
        <tr>
            <td class="NEXUSScreenName">Add Pipeline</td>
        </tr>
    </table>

    <html:form action="PipelineCreate">
    	<table class="NEXUS_TABLE" width="100%">
            <tr>
                <td class="NEXUSName">Name</td>
                <td class="NEXUSValue"><html:text property="name" size="50"/></td>
            </tr>
            <tr>
                <td class="NEXUSName">Direction</td>
                <td class="NEXUSValue">
	                <html:select property="direction">
						<html-el:option value="0">Inbound</html-el:option>
						<html-el:option value="1">Outbound</html-el:option>
					</html:select>
                </td>
            </tr>
		<logic:equal name="pipelineForm" property="frontend" value="true">
			<tr>
				<td class="NEXUSName">TRP</td>
				<td class="NEXUSValue"><html:select property="nxTrpId">
					<logic:iterate id="trp" property="trps" name="pipelineForm">
						<html-el:option value="${trp.nxTRPId}">${trp.protocol}-${trp.version}-${trp.transport}</html-el:option>
					</logic:iterate>
				</html:select></td>
			</tr>
		</logic:equal>

            <tr>
                <td class="NEXUSName">Description</td>
                <td class="NEXUSValue"><html:text property="description" size="50"/></td>
            </tr>
        </table>

        <table class="NEXUS_BUTTON_TABLE">
          <tr>
            <td>
              &nbsp;
            </td>
            <td class="NexusHeaderLink" style="text-align: right;">
              <!-- 
              <nexus:submit precondition="pipelineCheckFields()" styleClass="button"><img src="images/icons/tick.png" class="button">Save</nexus:submit>
               -->
              <nexus:submit styleClass="button"><img src="images/icons/tick.png" class="button">Save</nexus:submit>
            </td>
          </tr>
        </table>
    </html:form>
    