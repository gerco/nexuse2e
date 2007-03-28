<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-nested" prefix="nested" %>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>

<logic:notEmpty name="redirectUrl">
    <logic:notEmpty name="redirectTimeout">    
    	<meta http-equiv="refresh" content="<bean:write name="redirectTimeout"/>;URL=<bean:write name="redirectUrl"/>"> 
		</logic:notEmpty>
</logic:notEmpty>

<nexus:helpBar/>

		<table class="NEXUS_TABLE" width="100%">
	    <tr>
	        <td class="NEXUSScreenName">Error</td>
	    </tr>
		</table>  

<logic:messagesPresent> 
<div class="NexusError"><html:errors/></div>
</logic:messagesPresent>