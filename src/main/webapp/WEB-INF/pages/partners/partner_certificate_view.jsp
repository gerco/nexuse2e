<%@ taglib uri="/tags/nexus" prefix="nexus" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-nested" prefix="nested" %>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %> 

<% /*<nexus:helpBar helpDoc="documentation/Collaboration_Partners.htm"/> */ %>

    <table class="NEXUS_TABLE" width="100%">
        <tr>
            <td><nexus:crumbs styleClass="NEXUSScreenPathLink"></nexus:crumbs></td>
        </tr>

        <tr>
            <td class="NEXUSScreenName">Update Certificate</td>
        </tr>
    </table>
    <html:form action="PartnerCertificateSave">
        <!--
        <input value="modena9080" type="hidden" name="PartnerID">
        <input value="1" type="hidden" name="CertSeqNo">
    -->
    <html:hidden property="seqNo"/>
    <logic:iterate name="certs" property="certificateParts" id="cert" indexId="index">
        <table class="NEXUS_TABLE" width="100%">
        <logic:equal name="index" value="0">
            <tr>
                <td class="NEXUSSection">Collaboration Partner</td>
                <td class="NEXUSSection"><bean:write name="partnerCertificateForm" property="partnerId"/></td>
            </tr>
            <tr>
                <td class="NEXUSName">Certificate ID</td>
                <td class="NEXUSValue"><html:text property="certificateId" size="50"/></td>
            </tr>
        </logic:equal>
        <logic:lessThan name="cert" property="nxCertificateId" value="0">
            <tr>
                <td class="NEXUSName" colspan="2" style="background-color: red; text-align: center;"><b>Missing certificate!</b></td>
            </tr>
            <tr>
            	<td class="NEXUSValue">Distinguished Name</td>
                <td class="NEXUSValue" style="white-space: normal;">${cert.fingerprint}</td>
            </tr>
        </logic:lessThan>
        <logic:greaterEqual name="cert" property="nxCertificateId" value="0">
            <tr>
                <td class="NEXUSName">Common Name</td>
                <td class="NEXUSValue"><bean:write name="cert" property="commonName"/></td>
            </tr>
        </logic:greaterEqual>
            <tr>
                <td class="NEXUSName">Organization</td>
                <td class="NEXUSValue"><bean:write name="cert" property="organisation"/></td>
            </tr>
            <tr>
                <td class="NEXUSName">Organization Unit</td>
                <td class="NEXUSValue"><bean:write name="cert" property="organisationUnit"/></td>
            </tr>
        <logic:greaterEqual name="cert" property="nxCertificateId" value="0">
            <tr>
                <td class="NEXUSName">Country</td>
                <td class="NEXUSValue"><bean:write name="cert" property="country"/></td>
            </tr>
            <tr>
                <td class="NEXUSName">State</td>
                <td class="NEXUSValue"><bean:write name="cert" property="state"/></td>
            </tr>
            <tr>
                <td class="NEXUSName">Location</td>
                <td class="NEXUSValue"><bean:write name="cert" property="location"/></td>
            </tr>
            <tr>
                <td class="NEXUSName">E-Mail</td>
                <td class="NEXUSValue"><bean:write name="cert" property="email"/></td>
            </tr>
            <tr>
                <td class="NEXUSName">Not Valid Before</td>
                <td class="NEXUSValue"><bean:write name="cert" property="notBefore"/></td>
            </tr>
            <tr>
                <td class="NEXUSName">Not Valid After</td>
                <td class="NEXUSValue"><bean:write name="cert" property="notAfter"/></td>
            </tr>
            <tr>
                <td class="NEXUSName">Validity</td>
                <td class="NEXUSValue">
                <logic:equal name="cert" property="valid" value="Okay">
            <font color="green"><b><bean:write name="cert" property="valid"/></b></font> <bean:write name="cert" property="timeRemaining"/>
          </logic:equal>
          <logic:notEqual name="cert" property="valid" value="Okay">
            <font color="red"><b><bean:write name="cert" property="valid"/></b></font>
          </logic:notEqual>
          </td>
        </tr>
           <tr>
               <td class="NEXUSName">Fingerprint</td>
               <td class="NEXUSValue">${cert.fingerprint}</td>
           </tr>
       </logic:greaterEqual>
        </table>
    </logic:iterate>

	<input value="${partnerCertificateForm.nxCertificateId}" type="hidden" name="nxCertificateId">
  <center> 
      <logic:messagesPresent> 
        <div class="NexusError"><html:errors/></div>
        </logic:messagesPresent>
    </center>

        <table class="NEXUS_BUTTON_TABLE"
               width="100%">
            <tr>
                <td>&nbsp;</td>
                <td class="NexusHeaderLink"><nexus:submit><img src="images/icons/tick.png" class="button" name="SUBMIT">Update</nexus:submit></td>
                <td class="NexusHeaderLink"><nexus:link href="PartnerCertificateDelete.do?nxPartnerId=${partnerCertificateForm.nxPartnerId}&nxCertificateId=${partnerCertificateForm.nxCertificateId}" precondition="confirmDelete('Are you sure you want to delete this Certificate?')" styleClass="button"><img src="images/icons/delete.png" class="button">Delete</nexus:link></td>
            </tr>
        </table>
        
    </html:form>
