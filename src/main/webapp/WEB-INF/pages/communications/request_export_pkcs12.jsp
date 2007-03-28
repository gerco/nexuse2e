<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-nested" prefix="nested" %>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
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
				        <td class="NEXUSScreenName">Export Temporary KeyStructure</td>
				    </tr>
				</table>
        
    <html:form action="/RequestSavePKCS12File.do" method="POST"> 
    <table class="NEXUS_TABLE">
            <tr>
                <td colspan="100%" class="NEXUSSection">Certificates</td>
            </tr> 
                   
           
            <tr>
                <td class="NEXUSName"><html:radio property="status" value="1"></html:radio>
                </td>
                <td class="NEXUSName">TargetFile:
                </td>
        <td class="NEXUSName"><html:text property="certficatePath" size="80"/>
                </td>                
            </tr>
            <tr>
                <td class="NEXUSName"><html:radio property="status" value="2"></html:radio>
                </td>
                <td colspan="2" class="NEXUSName">save as...
                </td>                        
            </tr>                      
        </table>          
        <table class="NEXUS_BUTTON_TABLE" width="100%">
                <tr>
                    <td>&nbsp;</td>
                    <td class="BUTTON_RIGHT"><nexus:submit><img src="images/submit.gif" name="SUBMIT"></nexus:submit></td>
                    <td class="NexusHeaderLink">Export</td>
                </tr>
            </table>
        </html:form>
    </center>