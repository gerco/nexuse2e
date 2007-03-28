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
				        <td class="NEXUSScreenName">Certificate Request</td>
				    </tr>
				</table>
        
        <table class="NEXUS_TABLE" width="100%">
            <tr>            
            <td colspan="2" class="NEXUSSection">CA Certificate</td>            
            </tr>            
            <tr>
                <td class="NEXUSName">Common Name</td>
                <td class="NEXUSValue"><bean:write name="request" property="commonName"/></td>
            </tr>
            <tr>
                <td class="NEXUSName">Organisation</td>
                <td class="NEXUSValue"><bean:write name="request" property="organisation"/></td>
            </tr>
            <tr>
                <td class="NEXUSName">Organisation Unit</td>
                <td class="NEXUSValue"><bean:write name="request" property="organisationUnit"/></td>
            </tr>
            <tr>
                <td class="NEXUSName">Country</td>
                <td class="NEXUSValue"><bean:write name="request" property="countryCode"/></td>
            </tr>
            <tr>
                <td class="NEXUSName">State</td>
                <td class="NEXUSValue"><bean:write name="request" property="state"/></td>
            </tr>
            <tr>
                <td class="NEXUSName">Location</td>
                <td class="NEXUSValue"><bean:write name="request" property="location"/></td>
            </tr>
            <tr>
                <td class="NEXUSName">E-Mail</td>
                <td class="NEXUSValue"><bean:write name="request" property="email"/></td>
            </tr>
         </table>
         <html:form action="RequestSaveCSRFile.do" method="POST">         
    
         <table class="NEXUS_TABLE" width="100%">
            <tr>            
            <td colspan="2" class="NEXUSSection">CSR Format</td>            
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
            <td colspan="2" class="NEXUSSection">Destination</td>            
            </tr>
            <tr>
                <td class="NEXUSName"><html:radio name="protectedFileAccessForm" property="status" value="1"/>Traget File (on the Server)</td>
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
                    <td>&nbsp;</td>
                    <td class="BUTTON_RIGHT"><nexus:submit><img src="images/submit.gif" name="SUBMIT"></nexus:submit></td>
                    <td class="NexusHeaderLink">save</td>
                </tr>
            </table>
      </html:form>
    </center>