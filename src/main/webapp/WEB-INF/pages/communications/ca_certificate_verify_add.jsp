<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-nested" prefix="nested" %>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/nexus" prefix="nexus" %>

<nexus:helpBar helpDoc="documentation/SSL.htm"/>

    <center>
		    <table class="NEXUS_TABLE" width="100%">
				    <tr>
				        <td>
				        	<nexus:crumbs/>
				        </td>
				    </tr>
				    <tr>
				        <td class="NEXUSScreenName">Server Certificate</td>
				    </tr>
				</table>
        
        <table class="NEXUS_TABLE" width="100%">
            <tr>            
            <td colspan="2" class="NEXUSSection">WARNING: Existing CA Certificate for Alias: <bean:write name="existingCertificate" property="alias"/></td>            
            </tr>
            <tr>
                <td class="NEXUSName">Common Name</td>
                <td class="NEXUSValue"><bean:write name="existingCertificate" property="commonName"/></td>
            </tr>
            <tr>
                <td class="NEXUSName">Organisation</td>
                <td class="NEXUSValue"><bean:write name="existingCertificate" property="organisation"/></td>
            </tr>
            <tr>
                <td class="NEXUSName">Organisation Unit</td>
                <td class="NEXUSValue"><bean:write name="existingCertificate" property="organisationUnit"/></td>
            </tr>
            <tr>
                <td class="NEXUSName">Country</td>
                <td class="NEXUSValue"><bean:write name="existingCertificate" property="country"/></td>
            </tr>
            <tr>
                <td class="NEXUSName">State</td>
                <td class="NEXUSValue"><bean:write name="existingCertificate" property="state"/></td>
            </tr>
            <tr>
                <td class="NEXUSName">Location</td>
                <td class="NEXUSValue"><bean:write name="existingCertificate" property="location"/></td>
            </tr>
            <tr>
                <td class="NEXUSName">E-Mail</td>
                <td class="NEXUSValue"><bean:write name="existingCertificate" property="email"/></td>
            </tr>
            <tr>
                <td class="NEXUSName">Not Valid Before</td>
                <td class="NEXUSValue"><bean:write name="existingCertificate" property="notBefore"/></td>
            </tr>
            <tr>
                <td class="NEXUSName">Not Valid After</td>
                <td class="NEXUSValue"><bean:write name="existingCertificate" property="notAfter"/></td>
            </tr>
            <tr>
                <td class="NEXUSName">Validity</td>
                <td class="NEXUSValue">
                <logic:equal name="existingCertificate" property="valid" value="Okay">
            <font color="green"><b><bean:write name="existingCertificate" property="valid"/></b></font> <bean:write name="existingCertificate" property="timeRemaining"/>
          </logic:equal>
          <logic:notEqual name="existingCertificate" property="valid" value="Okay">
            <font color="red"><b><bean:write name="existingCertificate" property="valid"/></b></font>
          </logic:notEqual>
          
                </td>
            </tr>
        </table>
        
        <center> 
          <logic:messagesPresent> 
                <div class="NexusError"><html:errors/></div> 
                </logic:messagesPresent>
            </center>
        <html:form action="CACertificateSaveSingleCert.do" method="POST"> 
        <html:hidden property="alias"/>
        <html:hidden property="certficatePath"/>
        <center>
            <table class="NEXUS_BUTTON_TABLE" width="100%">
                <tr>
                    <td>&nbsp;</td>
                    <td class="BUTTON_RIGHT">
                    	<nexus:submit precondition="confirmDelete('Are you sure you want to replace this Certificate?');"><img src="images/tree/plus.gif" name="SUBMIT"></nexus:submit>
                    </td>
                    <td class="NexusHeaderLink">Replace existing</td>
                </tr>
            </table>
        </center>
    </html:form>
    </center>