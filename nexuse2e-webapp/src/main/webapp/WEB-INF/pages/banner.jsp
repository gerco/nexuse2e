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
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="org.nexuse2e.Version"%>
<%@ page import="java.io.BufferedReader" %>
<%@ page import="java.io.FileReader" %>
<%@ page import="java.io.IOException" %>
<%@ page import="java.io.File" %>

<%
     String machineName = null;
     String configPath = System.getProperty("externalconfig");
     if (null != configPath && !"".equals(configPath)) {
          try {
               File file = new File(configPath, "machine_name.txt");
               BufferedReader br = new BufferedReader(new FileReader(file));
               machineName = br.readLine();
          } catch (IOException ioe) {
               machineName = "";
          }
     }

%>

<div id="logo_div">
</div>
<div id="machine_div_1">
     <c:set var="machine_name"><%= machineName %></c:set>
     <c:choose>
          <c:when test="${not empty machine_name}"><c:out value="${machine_name}" /></c:when>
          <c:otherwise><%@ include file="../config/machine_name.txt" %></c:otherwise>
     </c:choose>

</div>
<div id="machine_div_2">
	<%= Version.getVersion() %>
</div>
<div id="logged_in_user">
	<c:if test="${not empty nxUser}">
		You're logged in as ${nxUser.firstName} ${nxUser.lastName}
	</c:if>
</div>