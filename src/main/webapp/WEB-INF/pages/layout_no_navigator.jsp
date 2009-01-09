<%@ taglib uri="/tags/nexus" prefix="nexus" %>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles" %>

<script type="text/javascript">
   var djConfig = {isDebug: true };
</script>
  
<script type="text/javascript" src="javascript/dojo/dojo.js"></script>

<script language="JavaScript" type="text/javascript">
	dojo.require("dojo.event.*");
  dojo.require("dojo.io.*");
	dojo.require("dojo.widget.LayoutContainer");
	dojo.require("dojo.widget.LinkPane");
	dojo.require("dojo.widget.ContentPane");
	dojo.require("dojo.widget.Tree");
  dojo.require("dojo.widget.TreeLoadingController");
  dojo.require("dojo.widget.TreeNode");
	dojo.require("dojo.widget.TreeSelector");
	dojo.require("dojo.widget.FloatingPane");
	dojo.require("dojo.widget.Dialog");

</script>

<div dojoType="LayoutContainer" id="content" layoutChildPriority="top-bottom">
	<div dojoType="ContentPane" layoutAlign="top" id="header">
		<tiles:insert attribute="header"/>
	</div>
	<div dojoType="ContentPane" layoutAlign="top">
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
	<div dojoType="ContentPane" layoutAlign="client" id="docpane" executeScripts="true" cacheContent="false" preventCache="true" useCache="false">
		<tiles:insert attribute="document"/>
	</div>
</div>
