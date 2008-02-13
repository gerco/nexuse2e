<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-nested" prefix="nested" %>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %> 
<%@ taglib uri="/tags/nexus" prefix="nexus" %>
<%@ taglib uri="/tags/struts-html-el" prefix="html-el" %>            

<nexus:helpBar helpDoc="documentation/Collaboration_Partners.htm"/>

    <table class="NEXUS_TABLE" width="100%">
        <tr>
            <td></td>
        </tr>
        <tr>
            <td><nexus:crumbs styleClass="NEXUSScreenPathLink"></nexus:crumbs></td>
        </tr>

        <tr>
            <td class="NEXUSScreenName">Update Connection</td>
        </tr>
    </table>

    <html:form action="PartnerConnectionSave">
        
        <html:hidden name="partnerConnectionForm" property="nxPartnerId"/>
        <html:hidden name="partnerConnectionForm" property="nxConnectionId"/>
        
        <table class="NEXUS_TABLE" width="100%">
            <tr>
                <td class="NEXUSSection" colspan="2">Connection Information</td>
            </tr>
            <tr>
                <td class="NEXUSName">Name</td>
                <td class="NEXUSValue"><html:text size="50" property="name"/></td>
            </tr>
            <tr>
                <td class="NEXUSName">Connection URL</td>
                <td class="NEXUSValue"><html:text size="50" property="url"/></td>
            </tr>
            <tr>
                <td class="NEXUSName">Description</td>
                <td class="NEXUSValue"><html:text size="50" property="description"/></td>
            </tr>
            
            <tr>
            <td class="NEXUSName">Certificate</td>
            <td class="NEXUSValue">
                <html:select property="nxCertificateId">
                	<html:option value="0">- none -</html:option>
                  	<logic:iterate id="certificate" property="certificates" name="partnerConnectionForm">
                  		<html-el:option value="${certificate.nxCertificateId}">${certificate.name}</html-el:option>
					</logic:iterate>
              	</html:select>
            </td>
            </tr>
            
            <tr>
            <td class="NEXUSName">TRP</td>
            <td class="NEXUSValue">
                <html:select property="nxTrpId">
                  	<logic:iterate id="trp" property="trps" name="partnerConnectionForm">
                  		<html-el:option value="${trp.nxTRPId}">${trp.protocol}-${trp.version}-${trp.transport}</html-el:option>
					</logic:iterate>
              	</html:select>
            </td>
            </tr>
            <tr>
                <td class="NEXUSName">Timeout</td>
                <td class="NEXUSValue"><html:text size="50" property="timeout"/></td>
            </tr>
            <tr>
                <td class="NEXUSName">Message Interval</td>
                <td class="NEXUSValue"><html:text size="50" property="messageInterval"/></td>
            </tr>
            <tr>
        		<td class="NEXUSValue" colspan="2"><html:checkbox property="secure">Secure</html:checkbox></td>
		    </tr>
            <tr>
        		<td class="NEXUSValue" colspan="2"><html:checkbox property="reliable">Reliable</html:checkbox></td>
		    </tr>
            <tr>
        		<td class="NEXUSValue" colspan="2"><html:checkbox property="synchronous">Synchronous</html:checkbox></td>
		    </tr>
		    <tr>
                <td class="NEXUSName">Synchronous Timeout</td>
                <td class="NEXUSValue"><html:text size="50" property="synchronousTimeout"/></td>
            </tr>
            <tr>
                <td class="NEXUSName">Retries</td>
                <td class="NEXUSValue"><html:text size="50" property="retries"/></td>
            </tr>
		    <tr>
                <td class="NEXUSName">Login Name</td>
                <td class="NEXUSValue"><html:text size="50" property="loginName"/></td>
            </tr>
            <tr>
                <td class="NEXUSName">Password</td>
                <td class="NEXUSValue"><html:password size="50" property="password"/></td>
            </tr>
            
        </table>
        <table class="NEXUS_BUTTON_TABLE">
          <tr>
            <td>
              &nbsp;
            </td>
            <td class="NexusHeaderLink" style="text-align: right;">
            <!-- 
              <nexus:submit precondition="connectionCheckFields()" styleClass="button"><img src="images/icons/tick.png" class="button">Save</nexus:submit>
             -->
              <nexus:submit styleClass="button"><img src="images/icons/tick.png" class="button">Save</nexus:submit>
            </td>
            <td class="NexusHeaderLink" style="text-align: right;">
              <nexus:link href="PartnerConnectionDelete.do?partnerId=${collaborationPartnerForm.partnerId}&nxConnectionId=${partnerConnectionForm.nxConnectionId}" styleClass="button"><img src="images/icons/delete.png" class="button">Delete</nexus:link>
            </td>
          </tr>
        </table>
    </html:form>
