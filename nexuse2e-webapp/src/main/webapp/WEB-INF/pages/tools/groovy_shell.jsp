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
<%@ taglib uri="/tags/nexus" prefix="nexus"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-nested" prefix="nested"%>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/struts-html-el" prefix="html-el"%>

<%
	/* <nexus:helpBar /> */
%>

<center>
<table class="NEXUS_TABLE" width="100%">
	<tr>
		<td><nexus:crumbs styleClass="NEXUSScreenPathLink"></nexus:crumbs></td>
	</tr>
	<tr>
		<td class="NEXUSScreenName">Groovy Shell</td>
	</tr>
</table>

<html:form action="GroovyShell.do" method="post"
	enctype="multipart/form-data">
	

	<html:textarea rows="5" property="script" value="${groovyShellForm.script}" cols="70" />
	</br></br>
	<html:textarea rows="15" property="response" value="${groovyShellForm.response}" readonly="true" cols="70" />
	<table class="NEXUS_BUTTON_TABLE" width="100%">
		<tr>
			<input name="type" value="blank" type="hidden">
			<td class="BUTTON_RIGHT">
				<nexus:submit
					onClick="document.groovyShellForm.type.value='execute';"
					styleClass="button">
					<img src="images/icons/tick.png" class="button">Execute</nexus:submit>
			</td>
		</tr>
	</table>
</html:form></center>
<center><logic:messagesPresent>
	<div class="NexusError"><html:errors /></div>
</logic:messagesPresent> <logic:messagesPresent message="true">
	<html:messages id="msg" message="true">
		<div class="NexusMessage"><bean:write name="msg" /></div>
		<br />
	</html:messages>
</logic:messagesPresent></center>
