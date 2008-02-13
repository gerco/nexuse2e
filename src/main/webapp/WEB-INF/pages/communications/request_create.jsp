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
        
        <html:form action="RequestSaveRequest.do" method="POST"> 
        
    <table class="NEXUS_TABLE" width="100%">
            <tr>            
            <td colspan="2" class="NEXUSSection">CA Certificate</td>            
            </tr>
            <tr>
                <td class="NEXUSName">Common Name</td>
                <td class="NEXUSValue"><html:text property="commonName" size="50"/></td>
            </tr>
            <tr>
                <td class="NEXUSName">Organisation</td>
                <td class="NEXUSValue"><html:text property="organisation" size="50"/></td>
            </tr>
            <tr>
                <td class="NEXUSName">Organisation Unit</td>
                <td class="NEXUSValue"><html:text property="organisationUnit" size="50"/></td>
            </tr>
            <tr>
                <td class="NEXUSName">Country</td>
                <td class="NEXUSValue"><html:text property="countryCode" size="50"/></td>
            </tr>
            <tr>
                <td class="NEXUSName">State</td>
                <td class="NEXUSValue"><html:text property="state" size="50"/></td>
            </tr>
            <tr>
                <td class="NEXUSName">Location</td>
                <td class="NEXUSValue"><html:text property="location" size="50"/></td>
            </tr>
            <tr>
                <td class="NEXUSName">E-Mail</td>
                <td class="NEXUSValue"><html:text property="email" size="50"/></td>
            </tr>
            <tr>
                <td class="NEXUSName">Password</td>
                <td class="NEXUSValue"><html:password property="password" size="50"/></td>
            </tr>
            <tr>
                <td class="NEXUSName">Password (verify)</td>
                <td class="NEXUSValue"><html:password property="verifyPWD" size="50"/></td>
            </tr>
            
        </table>
        
        <center> 
          <logic:messagesPresent> 
                <div class="NexusError"><html:errors/></div> 
                </logic:messagesPresent>
            </center>
            
        <center>
            <table class="NEXUS_BUTTON_TABLE" width="100%">
                <tr>
                    <td>&nbsp;</td>
                    <td class="NexusHeaderLink" style="text-align: right;">
                    	<nexus:submit><img src="images/icons/tick.png" class="button">Create Request</nexus:submit>
                    </td>
                </tr>
            </table>
        </center>
    </html:form>
    </center>