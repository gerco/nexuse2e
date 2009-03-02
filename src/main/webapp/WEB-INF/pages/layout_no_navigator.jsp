<%@ taglib uri="/tags/nexus" prefix="nexus" %>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles" %>

<div dojoType="dijit.layout.BorderContainer" id="content" design="headline" gutters="false" style="width: 100%; height: 100%;">
  <div dojoType="dijit.layout.ContentPane" id="header" region="top">
		<tiles:insert attribute="header"/>
		<table id="toolbar" cellpadding="0" cellspacing="0">
		<tr style="margin: 0px; padding: 0px">
			<td id="toolbar-left"></td>
			<td style="text-align: left;"></td>
			<td style="text-align: right;padding-right: 10px">
				<span style="margin: 6px 0 0 22px;">
					<a href="documentation/nexuse2e_help.html" target="_blank" class="navigationactive"><img src="images/icons/help.png" class="navigationactive">&nbsp;help</a>
				</span>
			</td>
		</tr>
	</table>
	</div>
  <div dojoType="dojox.layout.ContentPane" id="docpane" region="center">
		<tiles:insert attribute="document"/>
	</div>
</div>