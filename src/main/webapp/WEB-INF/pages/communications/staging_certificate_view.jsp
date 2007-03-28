<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-nested" prefix="nested" %>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/nexus" prefix="nexus" %>
<%@ taglib uri="/tags/struts-html-el" prefix="html-el"%>

<nexus:helpBar helpDoc="documentation/SSL.htm"/>

    <center>
        <table class="NEXUS_TABLE" width="100%">
				    <tr>
				        <td>
				        	<nexus:crumbs/>
				        </td>
				    </tr>
				    <tr>
				        <td class="NEXUSScreenName">Staged Certificate</td>
				    </tr>
				</table>
        
    <bean:size id="size" name="certificatePromotionForm" property="certificateParts"/>
    <logic:iterate id="cert" indexId="counter" name="certificatePromotionForm" property="certificateParts"> 
        <table class="NEXUS_TABLE" width="100%">
            <tr>
            <% if(counter.intValue() == 0)
            {
            %>
            <td colspan="2" class="NEXUSSection">Server Certificate</td>
            <%
            }
            else if(counter.intValue() == (size.intValue()-1))
            {
            %>
            <td colspan="2" class="NEXUSSection">CA Root Certificate</td>
            <%
            }
            else
            {            
            %>
            <td colspan="2" class="NEXUSSection">CA Intermediate Certificate <%=counter.intValue()%></td>
            <%
            }
            %>
            </tr>
            <tr>
                <td class="NEXUSName">Common Name</td>
                <td class="NEXUSValue"><bean:write name="cert" property="commonName"/></td>
            </tr>
            <tr>
                <td class="NEXUSName">Organisation</td>
                <td class="NEXUSValue"><bean:write name="cert" property="organisation"/></td>
            </tr>
            <tr>
                <td class="NEXUSName">Organisation Unit</td>
                <td class="NEXUSValue"><bean:write name="cert" property="organisationUnit"/></td>
            </tr>
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
                <td class="NEXUSValue"><bean:write name="cert" property="fingerprint"/></td>
            </tr>
        </table>
        </logic:iterate>
        
        <html:form action="/StagingPromoteCertificate.do?seqNo=${seqNo}" method="POST"> 
		<table class="NEXUS_TABLE" width="100%">
			<tr><td colspan="2" class="NEXUSSection">Promote Certificate
			
			<html:select property="localNxPartnerId">
				<logic:iterate name="certificatePromotionForm" property="localPartners" id="localpartner">
					<html-el:option value="${localpartner.nxPartnerId}">${localpartner.partnerId}</html-el:option>
				</logic:iterate>
			</html:select> 
			<nexus:submit><img src="images/submit.gif" name="SUBMIT"></nexus:submit>
			</td></tr>
		</table>
		</html:form>
		
		
        <table class="NEXUS_BUTTON_TABLE" width="100%">
            <tr>
                <td>&nbsp;</td>
                <td class="BUTTON_RIGHT"><nexus:link href="StagingDeleteCertificate.do?nxCertificateId=${seqNo}" styleClass="NexusLink">
                <image src="images/submit.gif" border="0"/></nexus:link></td>
                <td class="NexusHeaderLink">Delete this Certificate</td>
            </tr>
            <tr>
                <td>&nbsp;</td>
                <td class="BUTTON_RIGHT"><nexus:link href="StagingExportCertificate.do?nxCertificateId=${seqNo}" styleClass="NexusLink">
                <image src="images/submit.gif" border="0"/></nexus:link></td>
                <td class="NexusHeaderLink">Export this Certificate</td>
            </tr>
        </table>
    </center>