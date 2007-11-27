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
            <td class="NEXUSScreenName">Update Certificate</td>
        </tr>
    </table>
    <html:form action="PartnerCertificateSave">
        <!--
        <input value="modena9080" type="hidden" name="PartnerID">
        <input value="1" type="hidden" name="CertSeqNo">
    -->
    <html:hidden property="seqNo"/>
        <table class="NEXUS_TABLE" width="100%">
            <tr>
                <td class="NEXUSSection">Collaboration Partner</td>
                <td class="NEXUSSection"><bean:write name="partnerCertificateForm" property="partnerId"/></td>
            </tr>
            <tr>
                <td class="NEXUSName">Certificate ID</td>
                <td class="NEXUSValue"><html:text property="certificateId" size="50"/></td>
            </tr>

            <tr>
                <td class="NEXUSName">Common Name</td>
                <td class="NEXUSValue"><bean:write name="partnerCertificateForm" property="commonName"/></td>
            </tr>
            <tr>
                <td class="NEXUSName">Organisation</td>
                <td class="NEXUSValue"><bean:write name="partnerCertificateForm" property="organisation"/></td>
            </tr>
            <tr>
                <td class="NEXUSName">Organisation Unit</td>
                <td class="NEXUSValue"><bean:write name="partnerCertificateForm" property="organisationUnit"/></td>
            </tr>
            <tr>
                <td class="NEXUSName">Country</td>
                <td class="NEXUSValue"><bean:write name="partnerCertificateForm" property="country"/></td>
            </tr>
            <tr>
                <td class="NEXUSName">State</td>
                <td class="NEXUSValue"><bean:write name="partnerCertificateForm" property="state"/></td>
            </tr>
            <tr>
                <td class="NEXUSName">Location</td>
                <td class="NEXUSValue"><bean:write name="partnerCertificateForm" property="location"/></td>
            </tr>
            <tr>
                <td class="NEXUSName">E-Mail</td>
                <td class="NEXUSValue"><bean:write name="partnerCertificateForm" property="email"/></td>
            </tr>
            <tr>
                <td class="NEXUSName">Not Valid Before</td>
                <td class="NEXUSValue"><bean:write name="partnerCertificateForm" property="notBefore"/></td>
            </tr>
            <tr>
                <td class="NEXUSName">Not Valid After</td>
                <td class="NEXUSValue"><bean:write name="partnerCertificateForm" property="notAfter"/></td>
            </tr>
            <tr>
                <td class="NEXUSName">Validity</td>
                <td class="NEXUSValue">
                <logic:equal name="partnerCertificateForm" property="valid" value="Okay">
            <font color="green"><b><bean:write name="partnerCertificateForm" property="valid"/></b></font> <bean:write name="partnerCertificateForm" property="timeRemaining"/>
          </logic:equal>
          <logic:notEqual name="partnerCertificateForm" property="valid" value="Okay">
            <font color="red"><b><bean:write name="partnerCertificateForm" property="valid"/></b></font>
          </logic:notEqual>
          </td>
        </tr>
            
        </table>

  <center> 
      <logic:messagesPresent> 
        <div class="NexusError"><html:errors/></div>
        </logic:messagesPresent>
    </center>

        <table class="NEXUS_BUTTON_TABLE"
               width="100%">
            <tr>
                <td>&nbsp;</td>
                <td class="NexusHeaderLink"><nexus:submit><img src="images/submit.gif" class="button" name="SUBMIT">Update</nexus:submit></td>
                <td class="NexusHeaderLink"><nexus:link href="PartnerCertificateDelete.do?nxPartnerId=${partnerCertificateForm.nxPartnerId}&nxCertificateId=${partnerCertificateForm.nxCertificateId}" precondition="confirmDelete('Are you sure you want to delete this Certificate?')" styleClass="button"><img src="images/delete.gif" class="button">Delete</nexus:link></td>
            </tr>
        </table>
        
    </html:form>
