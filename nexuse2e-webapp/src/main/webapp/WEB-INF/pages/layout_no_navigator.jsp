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
<%@ taglib uri="/tags/nexus" prefix="nexus" %>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles" %>

<div dojoType="dijit.layout.BorderContainer" id="content" design="headline" gutters="false" style="width: 100%; height: 100%;">
  <div dojoType="dijit.layout.ContentPane" id="header" region="top">
		<tiles:insert attribute="header"/>

	  	<div id="nav-global">
			<div id="nav-global-box">
				<div id="nav-global-box-spacer">
					<span>&nbsp;</span>
				</div>
				<ul>
					<li>	
						<a href="http://www.nexuse2e.org" target="_blank" class="navigationactive"><span>Go to NEXUSe2e website</span></a>
					</li>
					<li class="last">	
						<a href="http://www.nexuse2e.org/NEXUSe2e/Documentation.html" target="_blank" class="navigationactive"><span>Documentation</span></a>
					</li>
				</ul>
			</div>
		</div>

  </div>
  <div dojoType="dojox.layout.ContentPane" id="docpane" region="center">
		<tiles:insert attribute="document"/>
	</div>
</div>