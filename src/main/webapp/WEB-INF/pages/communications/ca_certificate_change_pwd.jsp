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
				        <td class="NEXUSScreenName">Import CA Certificate KeyStore</td>
				    </tr>
				</table>
        
        <html:form action="CACertificateSavePWD.do" method="POST"> 
            <table class="NEXUS_TABLE" width="100%">
                <tr>
                    <td colspan="2" class="NEXUSSection">Change KeyStore Password</td>
                </tr>
                <tr>
                    <td class="NEXUSName">KeyStore Password</td>
                    <td class="NEXUSValue"><html:password property="password" size="20"/>
                </tr>
                <tr>
                    <td class="NEXUSName">Verify Password</td>
                    <td class="NEXUSValue"><html:password property="verifyPwd" size="20"/>
                    </td>
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
                    <td class="NexusHeaderLink" style="text-align: right;"><nexus:submit styleClass="button"><img src="images/submit.gif" class="button">Update</nexus:submit></td>
                </tr>
            </table>
        </html:form>
    </center>