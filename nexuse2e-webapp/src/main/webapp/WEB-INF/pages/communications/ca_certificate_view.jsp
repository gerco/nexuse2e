<%--

     NEXUSe2e Business Messaging Open Source
     Copyright 2000-2009, Tamgroup and X-ioma GmbH

     This is free software; you can redistribute it and/or modify it
     under the terms of the GNU Lesser General Public License as
     published by the Free Software Foundation version 2.1 of
     the License.

     This software is distributed in the hope that it will be useful,
     but WITHOUT ANY WARRANTY; without even the implied warranty of
     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
     Lesser General Public License for more details.

     You should have received a copy of the GNU Lesser General Public
     License along with this software; if not, write to the Free
     Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
     02110-1301 USA, or see the FSF site: http://www.fsf.org.

--%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-nested" prefix="nested" %>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/nexus" prefix="nexus" %>

<% /*<nexus:helpBar helpDoc="documentation/SSL.htm"/> */ %>

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
            <td colspan="2" class="NEXUSSection">CA Certificate</td>            
            </tr>
            <tr>
                <td class="NEXUSName">Common Name</td>
                <td class="NEXUSValue"><bean:write name="certificatePropertiesForm" property="commonName"/></td>
            </tr>
            <tr>
                <td class="NEXUSName">Organisation</td>
                <td class="NEXUSValue"><bean:write name="certificatePropertiesForm" property="organisation"/></td>
            </tr>
            <tr>
                <td class="NEXUSName">Organisation Unit</td>
                <td class="NEXUSValue"><bean:write name="certificatePropertiesForm" property="organisationUnit"/></td>
            </tr>
            <tr>
                <td class="NEXUSName">Country</td>
                <td class="NEXUSValue"><bean:write name="certificatePropertiesForm" property="country"/></td>
            </tr>
            <tr>
                <td class="NEXUSName">State</td>
                <td class="NEXUSValue"><bean:write name="certificatePropertiesForm" property="state"/></td>
            </tr>
            <tr>
                <td class="NEXUSName">Location</td>
                <td class="NEXUSValue"><bean:write name="certificatePropertiesForm" property="location"/></td>
            </tr>
            <tr>
                <td class="NEXUSName">E-Mail</td>
                <td class="NEXUSValue"><bean:write name="certificatePropertiesForm" property="email"/></td>
            </tr>
            <tr>
                <td class="NEXUSName">Not Valid Before</td>
                <td class="NEXUSValue"><bean:write name="certificatePropertiesForm" property="notBefore"/></td>
            </tr>
            <tr>
                <td class="NEXUSName">Not Valid After</td>
                <td class="NEXUSValue"><bean:write name="certificatePropertiesForm" property="notAfter"/></td>
            </tr>
            <tr>
                <td class="NEXUSName">Validity</td>
                <td class="NEXUSValue">
                <logic:equal name="certificatePropertiesForm" property="valid" value="Okay">
            <font color="green"><b><bean:write name="certificatePropertiesForm" property="valid"/></b></font> <bean:write name="certificatePropertiesForm" property="timeRemaining"/>
          </logic:equal>
          <logic:notEqual name="certificatePropertiesForm" property="valid" value="Okay">
            <font color="red"><b><bean:write name="certificatePropertiesForm" property="valid"/></b></font>
          </logic:notEqual>
          
                </td>
            </tr>
            <tr>
                <td class="NEXUSName">Fingerprint</td>
                <td class="NEXUSValue"><bean:write name="certificatePropertiesForm" property="fingerprint"/></td>
            </tr>
        </table>
        
        <center> 
          <logic:messagesPresent> 
                <div class="NexusError"><html:errors/></div> 
                </logic:messagesPresent>
            </center>
            
        <html:form action="CACertificateDelete.do" method="POST"> 
        <html:hidden property="nxCertificateId"/>
        <html:hidden property="commonName"/> 
        <center>
            <table class="NEXUS_BUTTON_TABLE" width="100%">
                <tr>
                    <td>&nbsp;</td>
                    <td class="BUTTON_RIGHT">
                    	<nexus:submit precondition="confirmDelete('Are you sure you want to delete this Certificate?')"><img src="images/icons/tick.png" class="button">Delete</nexus:submit>
                    </td>
                </tr>
            </table>
        </center>
    </html:form>
    </center>