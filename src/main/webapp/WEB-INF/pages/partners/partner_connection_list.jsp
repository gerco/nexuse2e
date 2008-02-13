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
            <td class="NEXUSScreenName">Connections</td>
        </tr>
    </table>

    <table class="NEXUS_TAB_TABLE">
        <tr>
          <td class="NEXUS_TAB_LEFT_UNSELECTED"><img src="images/left666666.gif"></td>
            <td class="NEXUS_TAB_UNSELECTED"><nexus:link href="PartnerInfoView.do?nxPartnerId=${collaborationPartnerForm.nxPartnerId}" styleClass="NEXUS_TAB_UNSELECTED_LINK">
            Collaboration Partner</nexus:link></td>
            <td class="NEXUS_TAB_RIGHT_UNSELECTED"><img src="images/right666666.gif"></td>
            <td class="NEXUS_TAB_LEFT_SELECTED"><img src="images/leftcccccc.gif"></td>
            <td class="NEXUS_TAB_SELECTED">Connections</td>
            <td class="NEXUS_TAB_RIGHT_SELECTED"><img src="images/rightcccccc.gif"></td>
            <td class="NEXUS_TAB_LEFT_UNSELECTED"><img src="images/left666666.gif"></td>
            <td class="NEXUS_TAB_UNSELECTED"><nexus:link href="PartnerCertificateList.do?nxPartnerId=${collaborationPartnerForm.nxPartnerId}" styleClass="NEXUS_TAB_UNSELECTED_LINK">Certificates</nexus:link></td>
            <td class="NEXUS_TAB_RIGHT_UNSELECTED"><img src="images/right666666.gif"></td>
        </tr>
    </table>

    <table class="NEXUS_TABLE"
           width="100%">
        <tr>
            <td class="NEXUSSection">Name</td>
            <td class="NEXUSSection">URI</td>
        </tr>
    <logic:iterate id="con" name="collaborationPartnerForm" property="connections"> 
        <tr>
            <td class="NEXUSValue"><nexus:link href="PartnerConnectionView.do?nxPartnerId=${collaborationPartnerForm.nxPartnerId}&nxConnectionId=${con.nxConnectionId}" styleClass="NexusLink"><bean:write name="con" property="name"/></nexus:link></td>
            <td class="NEXUSValue"><bean:write name="con" property="url"/></td>
        </tr>
    </logic:iterate>
  </table>
  <center> 
      <logic:messagesPresent> 
        <div class="NexusError"><html:errors/></div>
        </logic:messagesPresent>
    </center>
    <table class="NEXUS_BUTTON_TABLE">
      <tr>
         <td>
          &nbsp;
        </td>
        <td class="BUTTON_RIGHT" style="text-align: right;">
          <nexus:link href="PartnerConnectionAdd.do?partnerId=${collaborationPartnerForm.partnerId}" styleClass="button"><img src="images/icons/add.png" class="button">Add Connection</nexus:link>
        </td>
      </tr>
    </table>
