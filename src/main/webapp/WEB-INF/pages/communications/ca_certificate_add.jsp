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
				        <td class="NEXUSScreenName">Add CA Certificate</td>
				    </tr>
				</table>

        <html:form action="CACertificateVerifyAddCert.do" method="POST" enctype="multipart/form-data"> 
            <table class="NEXUS_TABLE" width="100%">
                <tr>
                    <td colspan="2" class="NEXUSSection">Add CA Certificate</td>
                </tr>
                <tr>
                    <td class="NEXUSName">Certificate Filename</td>
                    <td class="NEXUSValue">
                    <html:file property="certficate" size="20"/>
                    <br>
                    <font size="1">browse to select a valid certificate</font></td>
                </tr>
                <tr>
                    <td class="NEXUSName">Certificate Alias</td>
                    <td class="NEXUSValue"><html:text property="alias" size="20"/>
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
                    <td class="NexusHeaderLink" style="text-align: right;"><nexus:submit styleClass="button" sendFileForm="true"><img src="images/submit.gif" class="button">Import</nexus:submit></td>
                </tr>
            </table>
        </html:form>
    </center>