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

<% /*<nexus:helpBar helpDoc="documentation/Tools.htm" /> */ %>

<table class="NEXUS_TABLE" width="100%">
    <tr>
        <td><nexus:crumbs/></td>
    </tr>
    <tr>
        <td class="NEXUSScreenName">Tools</td>
    </tr>
</table>

<table width="100%" class="NEXUS_TABLE">
    <tr>
        <td class="NEXUSIcon"><nexus:link styleClass="NexusImgLink"
                                          href="MessageSubmission.do">
            <img border="0" src="images/icons/lorry_go.png">
        </nexus:link></td>
        <td class="NEXUSNameNoWidth"><nexus:link styleClass="NexusLink"
                                                 href="MessageSubmission.do">Message Submission</nexus:link></td>
    </tr>
    <tr>
        <td class="NEXUSIcon"><nexus:link styleClass="NexusImgLink"
                                          href="ProcessConversationReport.do?type=purge">
            <img border="0" src="images/icons/database_lightning.png">
        </nexus:link></td>
        <td class="NEXUSNameNoWidth"><nexus:link styleClass="NexusLink"
                                                 href="ProcessConversationReport.do?type=purge">Database Purge</nexus:link></td>
    </tr>
    <tr>
        <td class="NEXUSIcon"><nexus:link styleClass="NexusImgLink"
                                          href="DatabasePurge.do?type=select">
            <img border="0" src="images/icons/database_lightning.png">
        </nexus:link></td>
        <td class="NEXUSNameNoWidth"><nexus:link styleClass="NexusLink"
                                                 href="DatabasePurge.do?type=select">Engine Log Purge</nexus:link></td>
    </tr>
    <tr>
        <td class="NEXUSIcon"><nexus:link styleClass="NexusImgLink"
                                          href="GroovyShell.do?type=select">
            <img border="0" src="images/icons/wrench.png">
        </nexus:link></td>
        <td class="NEXUSNameNoWidth"><nexus:link styleClass="NexusLink"
                                                 href="GroovyShell.do?type=select">Groovy Shell</nexus:link></td>
    </tr>
    <tr>
        <td class="NEXUSIcon"><nexus:link styleClass="NexusImgLink"
                                          href="MappingMaintenance.do">
            <img border="0" src="images/icons/database_table.png">
        </nexus:link></td>
        <td class="NEXUSNameNoWidth"><nexus:link styleClass="NexusLink"
                                                 href="MappingMaintenance.do">Mapping Maintenance</nexus:link></td>
    </tr>
    <tr>
        <td class="NEXUSIcon"><nexus:link styleClass="NexusImgLink"
                                          href="PersistentProperties.do">
            <img border="0" src="images/icons/database_save.png">
        </nexus:link></td>
        <td class="NEXUSNameNoWidth"><nexus:link styleClass="NexusLink"
                                                 href="PersistentProperties.do">Persistent Properties</nexus:link></td>
    </tr>
    <tr>
        <td class="NEXUSIcon"><nexus:link styleClass="NexusImgLink"
                                          href="FileDownload.do">
            <img border="0" src="images/icons/page_save.png">
        </nexus:link></td>
        <td class="NEXUSNameNoWidth"><nexus:link styleClass="NexusLink"
                                                 href="FileDownload.do">File Download</nexus:link></td>
    </tr>
    <tr>
        <td class="NEXUSIcon"><nexus:link styleClass="NexusImgLink"
                                          href="PatchManagement.do">
            <img border="0" src="images/icons/wrench.png">
        </nexus:link></td>
        <td class="NEXUSNameNoWidth"><nexus:link styleClass="NexusLink"
                                                 href="PatchManagement.do">Patches</nexus:link></td>
    </tr>
    <tr>
        <td class="NEXUSIcon"><nexus:link styleClass="NexusImgLink"
                                          href="ConfigurationManagement.do">
            <img border="0" src="images/icons/server_database.png">
        </nexus:link></td>
        <td class="NEXUSNameNoWidth"><nexus:link styleClass="NexusLink"
                                                 href="ConfigurationManagement.do">Configuration Management</nexus:link></td>
    </tr>

</table>
<center><logic:messagesPresent>
    <div class="NexusError"><html:errors/></div>
</logic:messagesPresent></center>
