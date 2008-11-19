<%@ taglib uri="/tags/nexus" prefix="nexus" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-nested" prefix="nested" %>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %> 
<%@ taglib uri="/tags/struts-html-el" prefix="html-el" %>

<% /*<nexus:helpBar helpDoc="documentation/Participants.htm"/> */ %>

<table class="NEXUS_TABLE" width="100%">
    <tr>
        <td>
        	<nexus:crumbs/>
        </td>
    </tr>
    <tr>
        <td class="NEXUSScreenName">Participants</td>
    </tr>
</table>
      
     <table class="NEXUS_TABLE" width="100%">
         <tr>
             <td class="NEXUSSection">Partner ID</td>
             <td class="NEXUSSection">Participant Description</td>
         </tr>            
         <logic:iterate id="participant" name="collection"> 
      <tr>
          <td class="NEXUSName">
			<nexus:link href="ParticipantView.do?nxChoreographyId=${choreographyForm.nxChoreographyId}&nxPartnerId=${participant.nxPartnerId}"
			styleClass="NexusLink"><bean:write name="participant" property="partnerDisplayName"/></nexus:link>
          </td>
          <td class="NEXUSValue"><bean:write name="participant" property="description"/></td>
      </tr>
</logic:iterate>            
</table>
<table class="NEXUS_BUTTON_TABLE" width="100%">
    <tr>
        <td>&nbsp;</td>
        <td class="BUTTON_RIGHT"><nexus:link href="ParticipantAdd.do?nxChoreographyId=${choreographyForm.nxChoreographyId}"
           styleClass="button"><img src="images/icons/add.png" border="0" alt="" class="button">Add Participant</nexus:link></td>
    </tr>
</table>

