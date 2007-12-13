<%@ taglib uri="/tags/nexus" prefix="nexus" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-nested" prefix="nested" %>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %> 

<nexus:helpBar helpDoc="documentation/Collaboration_Partners.htm"/>

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
              <nexus:submit precondition="true /*certificateCheckFields()*/" sendFileForm="true" styleClass="button"><img src="images/submit.gif" class="button">Save</nexus:submit>
            </td>
          </tr>
        </table>
    </html:form>