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
<%@page import="org.nexuse2e.Version"%>

<div id="logo_div" style="margin-left: 76px; height: 164px; width: 159px; display: inline-block; background-image: url(images/logo.gif);">
</div>
<div id="machine_div" style="position: absolute; top: 20px; right: 20px; height:28px; width:100%; text-align: right; display:inline;">
	<div id="machine_div2" style="color:#ffffff;font-size:20pt;">
		<%@ include file="../config/machine_name.txt" %>
	</div>
	<div  style="margin-top:10px; height:13px;" id="machine_div3">
	<%= Version.getVersion() %>
	</div>
</div>