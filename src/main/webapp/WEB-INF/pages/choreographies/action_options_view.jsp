<%@ taglib uri="/tags/nexus" prefix="nexus" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-nested" prefix="nested" %>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %> 
<%@ taglib uri="/tags/struts-html-el" prefix="html-el" %>


<nexus:helpBar helpDoc="documentation/Filters.htm"/>

  <html:form action="ActionOptionsUpdate">
    <html:hidden property="choreographyId" name="choreographyActionForm"/>
      <html:hidden property="actionId" name="choreographyActionForm"/>
      
      <table class="NEXUS_TABLE" width="100%">
			    <tr>
			        <td>
			        	<nexus:crumbs/>
			        </td>
			    </tr>
			    <tr>
			        <td class="NEXUSScreenName">Update Action &gt; <bean:write name="choreographyActionForm" property="actionId"/></td>
			    </tr>
			</table>
			   
      <table class="NEXUS_TAB_TABLE">
          <tr>
              <td class="NEXUS_TAB_LEFT_UNSELECTED"><img src="images/left666666.gif"></td>
              <td class="NEXUS_TAB_UNSELECTED">
              	<nexus:link href="ActionSettingsView.do?choreographyId=<bean:write name="choreographyActionForm" property="choreographyId"/>&actionId=<bean:write name="choreographyActionForm" property="actionId"/>"
              		styleClass="NEXUS_TAB_UNSELECTED_LINK">Settings</nexus:link>
              </td>
              <td class="NEXUS_TAB_RIGHT_UNSELECTED"><img src="images/right666666.gif"></td>
              <td class="NEXUS_TAB_LEFT_SELECTED"><img src="images/leftcccccc.gif"></td>
              <td class="NEXUS_TAB_SELECTED">Options</td>
              <td class="NEXUS_TAB_RIGHT_SELECTED"><img src="images/rightcccccc.gif"></td>
          </tr>
      </table>
      <table class="NEXUS_TABLE">
          <tr>
              <td colspan="2" class="NEXUSSection">Inbound</td>
          </tr>
          <tr>
              <td class="NEXUSValue" colspan="2"><html:checkbox property="decompress">Decompress</html:checkbox></td>
          </tr>
          <tr>
              <td class="NEXUSName">Mapping File</td>
              <td class="NEXUSValue"><html:text property="inboundMappingFile"></html:text></td>
          </tr>
          <tr>
              <td class="NEXUSName">XSLT File 1</td>
              <td class="NEXUSValue"><html:text property="inboundXSLT1"></html:text></td>
          </tr>
          <tr>
              <td class="NEXUSName">XSLT File 2</td>
              <td class="NEXUSValue"><html:text property="inboundXSLT2"></html:text></td>
          </tr>
      </table>
      <table class="NEXUS_TABLE">
          <tr>
              <td colspan="2" class="NEXUSSection" width="13%">Outbound</td>
          </tr>
          <tr>
              <td class="NEXUSName">Mapping File</td>
              <td class="NEXUSValue"><html:text property="outboundMappingFile"></html:text></td>
          </tr>
          <tr>
              <td class="NEXUSName">XSLT File 1</td>
              <td class="NEXUSValue"><html:text property="outboundXSLT1"></html:text></td>
          </tr>
          <tr>
              <td class="NEXUSName">XSLT File 2</td>
              <td class="NEXUSValue"><html:text property="outboundXSLT2"></html:text></td>
          </tr> 
          <tr>
              <td class="NEXUSValue" colspan="2"><html:checkbox property="compress">Compress</html:checkbox></td>
          </tr>
      </table>
      <table>
          <tr>
              <td>
                  <table class="NEXUS_BUTTON_TABLE" width="100%">
                      <tr>
                          <td>&nbsp;</td>
                          <td class="BUTTON_RIGHT">
                                 <nexus:submit><img src="images/submit.gif"></nexus:submit></td>
                          <td class="NexusHeaderLink">Update</td>
                      </tr>
                  </table>
              </td>
          </tr>
      </table>
    </html:form>
