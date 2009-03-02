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
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %> 

<nexus:fileUploadResponse>
<% /*<nexus:helpBar helpDoc="documentation/Collaboration_Partners.htm"/> */ %>

    <table class="NEXUS_TABLE" width="100%">
        <tr>
            <td><nexus:crumbs styleClass="NEXUSScreenPathLink"></nexus:crumbs></td>
        </tr>
        <tr>
            <td class="NEXUSScreenName">Add Certificate</td>
        </tr>
    </table>

  <html:form action="PartnerCertificateCreate" enctype="multipart/form-data">

    <html:hidden property="id"/>        
        
        <table class="NEXUS_TABLE" width="100%">
            <tr>
                <td class="NEXUSSection">Collaboration Partner</td>
                <td class="NEXUSSection"><bean:write name="protectedFileAccessForm" property="id"/></td>
            </tr>
            <tr>
                <td class="NEXUSName">Certificate ID</td>
                <td class="NEXUSValue"><html:text size="64" property="alias" onkeypress="return checkKey(event);"/></td>
            </tr>
            <tr>
                <td class="NEXUSName">FileName</td>
                <td class="NEXUSValue"><html:file size="50" property="certficate" onkeypress="return checkKey(event);"/><br>
                <font size="1">browse to select X509 compatible certificate</font></td>
            </tr>
        </table>
        <table class="NEXUS_BUTTON_TABLE">
          <tr>
            <td>
              &nbsp;
            </td>
            <td class="NexusHeaderLink" style="text-align: right;">
              <nexus:submit precondition="true /*certificateCheckFields()*/" sendFileForm="true" styleClass="button"><img src="images/icons/tick.png" class="button">Save</nexus:submit>
            </td>
          </tr>
        </table>
    </html:form>
</nexus:fileUploadResponse>