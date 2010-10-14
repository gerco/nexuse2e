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
<%@ taglib uri="/tags/nexus" prefix="nexus"%>

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
<div class="NexusError"><nexus:errors/></div>
</logic:messagesPresent>