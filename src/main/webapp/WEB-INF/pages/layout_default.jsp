<%@ taglib uri="/tags/nexus" prefix="nexus" %>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

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
	dojo.require("dojo.widget.ProgressBar");
	dojo.require("dojo.widget.Tooltip");
	dojo.require("dojo.io.IframeIO"); // Needed for file uploads


</script>

<script>
	/*
   * Initializes the tree.
   */
  function init() {
      <!-- get a reference to the treeSelector -->
      var menuTreeSelector = dojo.widget.manager.getWidgetById('menuTreeSelector');
  
      <!-- connect the select event to the function treeSelectFired() -->
      dojo.event.connect(menuTreeSelector,'select','update');
  }
  
  /*
   * Updates the content of the docpane by callback of the tree select event.
   */
  function update() {
      var menuTreeSelector = dojo.widget.manager.getWidgetById('menuTreeSelector');
      var menuTreeNode = menuTreeSelector.selectedNode;
			var docPane = dojo.widget.byId("docpane");
			var file = menuTreeNode.widgetId;
			if (!file){
				docPane.setContent("Unknown document \"" + file + "\"");
			}else{
				setContentUrl(file);
			}
  }
  
  /*
   * Returns the HTML that display the navigation crumbs
   * based on the currently selected tree node.
   */
  function getCrumbs() {
  	var selectedNode = getSelectedTreeNode();
  	if(selectedNode) {
	  	var crumbString = '<a href="javascript: getMenuTreeSelector().deselect(); getMenuTreeSelector().select({source: getTreeNode(\'' + selectedNode.widgetId + '\')});">' + selectedNode.title + '</a>';
	    while(selectedNode.parent.title != null) {
	    	selectedNode = selectedNode.parent;
	    	crumbString = '<a href="javascript: getMenuTreeSelector().select({source: getTreeNode(\'' + selectedNode.widgetId + '\')});">' + selectedNode.title + '</a>' + ' &gt ' + crumbString;
	    }
	    return crumbString;
	  } else {
	  	return "";
	  }
  }
  
  /*
   * Returns the widget the displays the content.
   */
  function getDocPane() {
  	return dojo.widget.byId('docpane');
  }
  
  /*
   * Returns the widget the displays the menu tree.
   */
  function getNavPane() {
  	return dojo.widget.byId('navigator');
  }
  
  /*
   * Returns the TreeSelector for the menu.
   */
  function getMenuTreeSelector() {
  	return dojo.widget.byId('menuTreeSelector');
  }
  
  /*
   * Returns the TreeController for the tree.
   */
  function getMenuTreeLoadingController() {
  	return dojo.widget.byId('menuTreeController');
  }
  
  /*
   * Returns the currently selected node.
   */
  function getSelectedTreeNode() {
  	return getMenuTreeSelector().selectedNode;
  }
  
  /*
   * Returns the tree node with the given widgetId.
   */
  function getTreeNode(widgetId) {
  	return dojo.widget.byId(widgetId);
  }
  
  /*
   * Returns the tree widget.
   */
  function getTree() {
  	return dojo.widget.byId('menuTree');
  }
  
  /*
   * Returns the progress dialog.
   */
  function getProgressDialog() {
  	return dojo.widget.byId('progressDialog');
  }
  
  /*
   * Returns the progress bar.
   */
  function getProgressBar() {
  	return dojo.widget.byId('progressBar');
  }
  
  /*
   * Show the progress bar dialog.
   */
  function showProgressBarDialog() {
  	getProgressDialog().show();
  	getProgressBar().startAnimation();
  }
  
  /*
   * Hide the progress bar dialog.
   */
  function hideProgressBarDialog() {
  	getProgressBar().stopAnimation();
  	getProgressDialog().hide();
  }
  
  /*
   * Displays an error.
   */
  function displayError(message) {
  	alert(message);
	hideProgressBarDialog();
  }
  
  function checkForChangedConfiguration(changed) {
  	if (changed) {
  	  document.getElementById('applyConfiguration').className='helpBar';
  	  document.getElementById('applyConfiguration').href='ApplyConfiguration.do';
  	  document.getElementById('revertConfiguration').className='helpBar';
  	  document.getElementById('revertConfiguration').href='RevertConfiguration.do';
  	} else {
  	  document.getElementById('applyConfiguration').className='helpBarDisabled';
  	  document.getElementById('applyConfiguration').href='#';
  	  document.getElementById('revertConfiguration').className='helpBarDisabled';
  	  document.getElementById('revertConfiguration').href='#';
  	}
  }
  
  /*
   * Refreshes all expanded dynamic nodes in the tree. 
   */
  function refreshMenuTree() {
  	
	var rootNode = getTreeNode('Home.do');
		//debug("Attempting to refresh the tree ...");
		//debug("Root node expandend: " + rootNode.isExpanded);
		//debug("Root has children: " + rootNode.children.length);
		if(rootNode && rootNode.isExpanded && rootNode.children) {
			reloadChildren(rootNode.children);
		}
		//debug("... done");
	}
	
	/*
	 * Called by refreshTree().
	 * Iterates over an array of nodes and finds nodes with dynamic
	 * children that have to be reloaded.
	 */
	function reloadChildren(nodes) {
		if(nodes && nodes[0]) { // not null not empty
	  	//debug("Updating " + nodes.length + " child nodes of " + nodes[0].parent.widgetId);
	  	for(var i=0; i < nodes.length; i++) {
	  		//debug(i + ". node is " + nodes[i].widgetId);
	  		if(nodes[i].isFolder && nodes[i].isExpanded) { // process only expanded nodes
	  			//debug(nodes[i].widgetId + " is expanded");
	  			if(nodes[i].objectId.substring(0,1) == "d") { // load children, 'cause "d" stands for "reload me"
	  				//debug(nodes[i].widgetId + " is dynamic parent -> reloading");
	  				updateChildren(nodes[i]);
	  				//debug(nodes[i].widgetId + "'s children reloaded");
	  			}
	  			
	  			// check whether child nodes need refresh
	  			//debug("recursively calling reload children of " + nodes[i].widgetId);
	  			reloadChildren(nodes[i].children);
	  			//debug("finished recursion step for " + nodes[i].widgetId);
	  		}
	  	}
	  }
	  //debug("finished");
	}
	
	/*
	 * Transitively called by refreshTree().
	 * Decides which nodes must be reloaded or removed
	 * and reloads or removes them.
	 */
	function updateChildren(node) {
		//debug("reloadChildrenPrototype");
		var tlc = getMenuTreeLoadingController();
		var oldChildren = node.children;
		var newChildren = new Array();
		var sync;
    var params = {
      node: tlc.getInfo(node),
      tree: tlc.getInfo(node.tree)
    };
    tlc.runRPC({
      url: tlc.getRPCUrl('getChildren'),
      load: function(result) {
	      cleanUpOldNodes(oldChildren, result);
      	for(var i=0; i < result.length; i++) {
        	//debug(result[i].widgetId + " (" + result[i].objectId + ")") ;
        	if(containsNode(oldChildren, result[i])) {
        		//debug(result[i].widgetId + " (" + result[i].objectId + ")" + " exists already");
        	} else {
        		//debug(result[i].widgetId + " (" + result[i].objectId + ")" + " is new");
        		var newChild = dojo.widget.createWidget(node.widgetType, result[i]);
      			node.addChild(newChild, i);
        	}
        }
      },
      sync: true,
      lock: [node],
      params: params
    });
		//debug("done");
	}
	
	/*
	 * Removes all nodes of the oldNodes array
	 * which are not contained in the newNodes array
	 * from the tree.
	 */
	function cleanUpOldNodes(oldNodes, newNodes) {
		for(var i=0; i < oldNodes.length; i++) {
			if(!containsNode(newNodes, oldNodes[i])) {
				// if this node is selected, select its parent node before it is removed
				if(getSelectedTreeNode() && getSelectedTreeNode().objectId == oldNodes[i].objectId) {
					getMenuTreeSelector().deselect();
					getMenuTreeSelector().doSelect(oldNodes[i].parent);
				}
				getTree().removeNode(oldNodes[i]);
			}
		}
	}
	
	/*
	 * Determines whether a given node is contained
	 * in a node array. Retruns true if the node
	 * is contained in the array; false otherwise.
	 */	
	function containsNode(nodes, node) {
		var result = false;
		if(nodes && node) {
			for(var i=0; i < nodes.length && !result; i++) {
				result = (nodes[i].objectId == node.objectId);
				//debug(nodes[i].title + " (" + nodes[i].objectId + ")" + " " + node.title + " (" + node.objectId + ")");
			}
		}
		
		return result;
	}
	
	function setContentUrl(contentUrl) {
		showProgressBarDialog();
		//debug(form);
		// alert( 'Form: ' + form  );
		// alert( 'Action: ' + form.action );
	 	var kw = {
	 		url: contentUrl,
	 		mimetype: "text/html",
	 		formNode: null,
	 		load: function(load, data, e) {	
	 			//debug( 'Data: ' + data );
	 			getDocPane().setContent(data);
	 			hideProgressBarDialog();
	 		},		
	 		error: function(t, e) {
	 			displayError(e.message);
	 		}
	 	};
	 	dojo.io.bind(kw);
	}
  
	function submitForm(form){	
		showProgressBarDialog();
		//debug(form);
		// alert( 'Form: ' + form  );
		// alert( 'Action: ' + form.action );
	 	var kw = {
	 		url: form.action,
	 		mimetype: "text/html",
	 		formNode: form,
	 		load: function(load, data, e) {	
	 			//debug( 'Data: ' + data );
	 			getDocPane().setContent(data);
	 			hideProgressBarDialog();
	 		},		
	 		error: function(t, e) {
	 			displayError(e.message);
	 		}
	 	};
	 	dojo.io.bind(kw);
	 }

	function submitFileForm(form){
	  dojo.event.connect(form, "onsubmit", submitFileFormData(form) );
	  form.submit();
	}
	
	function submitFileFormData(form){	
		showProgressBarDialog();
		//debug( 'Form: ' + form  );
		//debug( 'Action: ' + form.action );
	 	var kw = {
	 		url: form.action,
	 		mimetype: "text/html",
	 		formNode: form,
	 		load: function(load, data, e) {
	 			var res = dojo.byId( 'dojoIoIframe' ).contentWindow.document.body.innerHTML;
	 			//debug( 'Data: ' + res );
	 			getDocPane().setContent(res);
	 			hideProgressBarDialog();
	 		},		
	 		error: function(t, e) {
	 			displayError(e.message);
	 		}
	 	};
	 	dojo.io.bind(kw);
	 }

  dojo.addOnLoad(init);
 </script>

<div dojoType="LayoutContainer" id="content" layoutChildPriority="top-bottom">
	<div dojoType="ContentPane" layoutAlign="top" id="header">
		<tiles:insert attribute="header"/>
	</div>
<div dojoType="ContentPane" layoutAlign="top" id="top-nav">
<table style="padding: 0pt; margin: 0px;">
	<tr>
		<td style="text-align: left;padding-left: 10px">
		<nexus:link id="applyConfiguration" href="" styleClass="helpBarDisabled"><img class="helpBar" alt="" src="images/icons/server_go.png">Apply</nexus:link>
		|
		<nexus:link id="revertConfiguration" href="" styleClass="helpBarDisabled"><img class="helpBar" alt="" src="images/icons/arrow_rotate_anticlockwise.png">Revert</nexus:link></td>
		<td style="text-align: right;padding-right: 10px"><a
			href="documentation/nexuse2e_help.html" target="_blank"
			class="helpBar"><img src="images/icons/help.png" class="helpBar">&nbsp;Help</a>&nbsp;|&nbsp;<a
			href="Logout.do" class="helpBar">Logout</a></td>
	</tr>
</table>
</div>
<div dojoType="ContentPane" layoutAlign="left" style="overflow:auto;" id="navigator" cacheContent="false" preventCache="true" useCache="false">
		<div id="progressDialog" dojoType="Dialog">
			<div id="dialogContent" style="background-color: #FFFFFF; padding: 20px;">
				<div style="margin: 10px;">Please wait ...</div>
				<div id="progressBar" dojoType="ProgressBar"></div>
			</div>
		</div>
		<tiles:insert attribute="menu"/>
	</div>
	<div dojoType="ContentPane" layoutAlign="client" style="overflow:true;" id="docpane" executeScripts="true" cacheContent="false" preventCache="true" useCache="false">
		<tiles:insert attribute="document"/>
	</div>	
</div>
