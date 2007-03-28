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
	<div dojoType="ContentPane" layoutAlign="client" id="docpane" executeScripts="true" cacheContent="false">
		<tiles:insert attribute="document"/>
	</div>
</div>
