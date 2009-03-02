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
<script type="text/javascript">
/*
	dojo.addOnLoad( function() {
		var tree = getTree();
		tree.focusNode(tree.rootNode.getChildren[0]);
	} );
*/
</script>

<script type="text/javascript">
var myTreeModel = {
		_loadChildren: function (parentItem,onComplete) {

			// get some data, convert to JSON
			//console.log(parentItem);
			dojo.xhrGet({
				url:"ajax/menu?action=getChildren" + ( parentItem == undefined || parentItem.widgetId == undefined ? "" : "&parentId=" + encodeURI(parentItem.widgetId) ),
				handleAs:"json",
				load: function(data){
		    		//console.log(data);
		    		onComplete(data.items);
				}
			});
		},

		destroy: function(){
			//console.log("destroy");
		    // summary: destroys this object, releasing connections to the store
		},
		
		// =======================================================================
		// Methods for traversing hierarchy
		getRoot: function(onItem) {
			      // summary:
		        //            Calls onItem with the root item for the tree, possibly a fabricated item.
		        //            Throws exception on error.
			//console.log("getRoot");
			this._loadChildren(undefined,onItem);
		},
		
		mayHaveChildren: function(/*dojo.data.Item*/ item){
			      // summary
		        //            Tells if an item has or may have children.  Implementing logic here
		        //            avoids showing +/- expando icon for nodes that we know don't have children.
		        //            (For efficiency reasons we may not want to check if an element actually
		        //            has children until user clicks the expando node)
			//console.log("mayHaveChildren");
			return item.type == "folder";
		},
		
		getChildren: function(/*dojo.data.Item*/ parentItem, /*function(items)*/ onComplete){
			      // summary
		        //           Calls onComplete() with array of child items of given parent item, all loaded.
		        //            Throws exception on error.
			//console.log("getChildren");
			this._loadChildren(parentItem,onComplete);
		},
		
		// =======================================================================
		// Inspecting items
		getIdentity: function(/* item */ item){
			      // summary: returns identity for an item
			//console.log("getIdentity");
			return item.objectId;
		},
		
		getLabel: function(/*dojo.data.Item*/ item){
			      // summary: get the label for an item
			//console.log("getLabel");
			return item.title;
		},
		
		// =======================================================================
		// Write interface
		newItem: function(/* Object? */ args, /*Item?*/ parent){
			//console.log("newItem");
		        // summary
		        //            Creates a new item.   See dojo.data.api.Write for details on args.
		},
		
		pasteItem: function(/*Item*/ childItem, /*Item*/ oldParentItem, /*Item*/ newParentItem, /*Boolean*/ bCopy){
			//console.log("pasteIdem");
		        // summary
		        //            Move or copy an item from one parent item to another.
		        //            Used in drag & drop.
		        //            If oldParentItem is specified and bCopy is false, childItem is removed from oldParentItem.
		        //            If newParentItem is specified, childItem is attached to newParentItem.
		},
		
		// =======================================================================
		// Callbacks
		onChange: function(/*dojo.data.Item*/ item){
			//console.log("onChange");
		        // summary
		        //            Callback whenever an item has changed, so that Tree
		        //            can update the label, icon, etc.   Note that changes
		        //            to an item's children or parent(s) will trigger an
		        //            onChildrenChange() so you can ignore those changes here.
		},
		
		onChildrenChange: function(/*dojo.data.Item*/ parent, /*dojo.data.Item[]*/ newChildrenList){
			//console.log("onChildrenChange");
		        // summary
		        //            Callback to do notifications about new, updated, or deleted items.
		}
};

/*
 * Returns the style sheet object for generated CSS.
 */
function getGeneratedCSS() {
	for ( var i=0; i < document.styleSheets.length; i++ ) {
		if ( document.styleSheets[i].title == "generatedCSS" ) {
			return document.styleSheets[i];
		}
	}
}

/*
 * Check, whether the array of cssRules contains a rule with the given selector.
 * Return true, if the selector was found; false otherwise.
 */
function containsStyleClass(cssRules, selector) {
		for ( var i=0; i < cssRules.length; i++ ) {
			if ( cssRules[i].selectorText ) {
				//console.log( cssRules[i].selectorText );
			}
			if ( cssRules[i].selectorText == selector ) {
				//console.log("found selector " + cssRules[i].selectorText );
				return true;
			}
		}
		//console.log("not found selector " + selector );
		return false;
};
</script>

<div dojoType="dijit.Tree" id="menuTree" model="myTreeModel" showRoot="false">
  <script type="dojo/method" event="onClick" args="item">
		update(item.widgetId);
  </script>
  <script type="dojo/method" event="getIconClass" args="item,opened">
		if (item && item.objectId ) {
			var myStyleSheet = getGeneratedCSS();
			if ( myStyleSheet.insertRule ) { // FF, WebKit
				if ( !containsStyleClass(myStyleSheet.cssRules, "." + item.objectId) ) { 
					myStyleSheet.insertRule("." + item.objectId + " { background-image: url(" + item.childIconSrc + ") }", 0);
					//console.log("FF added style class ." + item.objectId );
				}
			} else if ( myStyleSheet.addRule ) { // IE
				if ( !containsStyleClass(myStyleSheet.rules, "." + item.objectId) ) {
					myStyleSheet.addRule("." + item.objectId, "background-image: url(" + item.childIconSrc + ")", 0);
					//console.log("IE added style class ." + item.objectId );
				}
			}
			return item.objectId;
		}
  </script>
</div>