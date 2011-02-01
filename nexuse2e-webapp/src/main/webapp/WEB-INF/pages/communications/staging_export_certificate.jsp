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
<%@ taglib uri="/tags/nexus" prefix="nexus" %>

<% /*<nexus:helpBar helpDoc="documentation/SSL.htm"/> */ %>

    <center>
        
        <table class="NEXUS_TABLE" width="100%">
            <tr>
                <td><nexus:crumbs styleClass="NEXUSScreenPathLink"></nexus:crumbs></td>
            </tr>
            <tr>
                <td class="NEXUSScreenName">Staged Certificate: <bean:write name="protectedFileAccessForm" property="alias"/></td>
            </tr>
        </table>
        
         <html:form action="StagingStoreExported.do" method="POST">         
     <html:hidden property="id" value='<%=request.getParameter("seqNo")%>'/>
         <table class="NEXUS_TABLE" width="100%">
            <tr>            
            <td colspan="2" class="NEXUSSection">Certificate Format</td>            
            </tr>
            <tr>
                <td class="NEXUSValue"><html:radio name="protectedFileAccessForm" property="format" value="1"/></td>
                <td class="NEXUSName">PEM</td>
            </tr>
            <tr>
                <td class="NEXUSValue"><html:radio name="protectedFileAccessForm" property="format" value="2"/></td>
                <td class="NEXUSName">DER</td>
            </tr>
        </table>
        <table class="NEXUS_TABLE" width="100%">
            <tr>            
            <td colspan="2" class="NEXUSSection">Elements</td>            
            </tr>
            <tr>
                <td class="NEXUSValue"><html:radio name="protectedFileAccessForm" property="content" value="1"/></td>
                <td class="NEXUSName">Certificate</td>
            </tr>
            <tr>
                <td class="NEXUSValue"><html:radio name="protectedFileAccessForm" property="content" value="2"/></td>
                <td class="NEXUSName">Certificate and required CA Certificates including Fingerprints for older Nexus Installs (Zip-File)</td>
            </tr>
            <tr>
                <td class="NEXUSValue"><html:radio name="protectedFileAccessForm" property="content" value="3"/></td>
                <td class="NEXUSName">Complete Keystore with PrivateKey as PKCS12</td>
            </tr>
        </table>
        <table class="NEXUS_TABLE" width="100%">
            <tr>            
            <td colspan="2" class="NEXUSSection">Destination</td>            
            </tr>
            <tr>
                <td class="NEXUSName"><html:radio name="protectedFileAccessForm" property="status" value="1"/>Target File (on the Server)</td>
                <td class="NEXUSValue"><html:text name="protectedFileAccessForm" property="certficatePath" size="60"/></td>
            </tr>
            <tr>
                <td class="NEXUSName"><html:radio name="protectedFileAccessForm" property="status" value="2"/>Save as...</td>
                <td class="NEXUSValue">&nbsp;</td>
            </tr>                        
        </table>
        <center> 
          <logic:messagesPresent> 
                <div class="NexusError"><html:errors/></div> 
                </logic:messagesPresent>
            </center>
        <table class="NEXUS_BUTTON_TABLE" width="100%">
                <tr>
                    <td class="BUTTON_RIGHT"><nexus:submit><img src="images/icons/tick.png" class="button">Save</nexus:submit></td>
                </tr>
            </table>
      </html:form>
    </center>