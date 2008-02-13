<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-nested" prefix="nested" %>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %> 
<%@ taglib uri="/tags/nexus" prefix="nexus" %>

<nexus:helpBar helpDoc="documentation/Service_Listing.htm"/>

    <center>
    <table class="NEXUS_TABLE" width="100%">
		    <tr>
		        <td>
		        	<nexus:crumbs/>
		        </td>
		    </tr>
		    <tr>
		        <td class="NEXUSScreenName">Services</td>
		    </tr>
		</table>
    
    <table class="NEXUS_TABLE" width="100%">
      <tr>
        <td width="50%" class="NEXUSSection">Service Name</td>
        <td class="NEXUSSection">Component</td>
        <td class="NEXUSSection">Autostart</td>
        <td class="NEXUSSection">Status</td>
        <td class="NEXUSSection">&nbsp;</td>
      </tr>

      <logic:iterate id="service" name="collection"> 
        <tr>
          <td class="NEXUSValue">
            <nexus:link href="ServiceView.do?nxServiceId=${service.nxServiceId}" styleClass="NexusLink">
              <bean:write name="service" property="name"/>
            </nexus:link>
          </td>
          <td class="NEXUSValue">
            <bean:write name="service" property="componentName"/>
          </td>
          <td class="NEXUSValue">
            <logic:equal name="service" property="serviceInstance.autostart" value="true">
              Yes
            </logic:equal>
            <logic:notEqual name="service" property="serviceInstance.autostart" value="true">
              No
            </logic:notEqual>
          </td>
          <td class="NEXUSValue">
            <bean:write name="service" property="serviceInstance.status"/>
          </td>
          <td class="NEXUSValue">
            <logic:equal name="service" property="serviceInstance.status" value="STARTED">
              <nexus:link href="ServiceStop.do?nxServiceId=${service.nxServiceId}"><img width="16" height="16" src="images/icons/stop.png" class="button" alt="Stop" id="stop"><span dojoType="tooltip" connectId="stop" toggle="explode">Stop Service</span></nexus:link>
            </logic:equal>
            <logic:notEqual name="service" property="serviceInstance.status" value="STARTED">
            <logic:notEqual name="service" property="serviceInstance.status" value="ERROR">
              <nexus:link href="ServiceStart.do?nxServiceId=${service.nxServiceId}"><img width="16" height="16" src="images/icons/resultset_next.png" class="button" alt="Start" id="start"><span dojoType="tooltip" connectId="start" toggle="explode">Start Service</span></nexus:link>
            </logic:notEqual>
            </logic:notEqual>
          </td>
        </tr>
      </logic:iterate>

    </table>

    <center>  
      <logic:messagesPresent>  
        <div class="NexusError"><html:errors/></div> 
      </logic:messagesPresent> 
    </center>

      <table class="NEXUS_BUTTON_TABLE">
        <tr>
     			<td>
			    	&nbsp;
    			</td>
	        <td class="NexusHeaderLink" style="text-align: right;">
				    <nexus:link href="ServiceAdd.do?nxComponentId=0" styleClass="button"><img src="images/icons/add.png" class="button">Add Service</nexus:link>
    			</td>
      	</tr>
      </table>
    <center> 
      <logic:messagesPresent> 
        <div class="NexusError"><html:errors/></div> 
        </logic:messagesPresent>
    </center>
  </center>
