<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-nested" prefix="nested" %>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="/tags/nexus" prefix="nexus" %>

<nexus:helpBar helpDoc="documentation/Components.htm"/>

		<table class="NEXUS_TABLE" width="100%">
	    <tr>
	        <td>
	        	<nexus:crumbs/>
	        </td>
	    </tr>
	    <tr>
	        <td class="NEXUSScreenName">Pipelines</td>
	    </tr>
		</table>
  
    <table class="NEXUS_TABLE" width="100%">
        <tr>
            <td class="NEXUSSection">Name</td>
            <td class="NEXUSSection">Description</td>
        </tr>
  
    <logic:iterate id="pipeline" name="collection"> 
    	<tr>
            <td class="NEXUSName"><nexus:link href="PipelineView.do?nxPipelineId=${pipeline.nxPipelineId}" styleClass="NexusLink"><bean:write name="pipeline" property="name"/></nexus:link></td>
            <td class="NEXUSName"><bean:write name="pipeline" property="description"/></td>
            
        </tr>
    </logic:iterate>

    </table>

    <table class="NEXUS_BUTTON_TABLE">
      <tr>
        <td>
          &nbsp;
        </td>
        <td class="NexusHeaderLink" style="text-align: right;">
 	  	    <nexus:link href="PipelineAdd.do" styleClass="button"><img src="images/tree/plus.gif" class="button">Add Pipeline</nexus:link>
        </td>
      </tr>
    </table>
    <center> 
      <logic:messagesPresent> 
        <div class="NexusError"><html:errors/></div> 
        </logic:messagesPresent>
    </center>
