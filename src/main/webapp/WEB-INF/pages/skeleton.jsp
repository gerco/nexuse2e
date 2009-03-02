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
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title><tiles:getAsString name="title" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=us-ascii">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="Content-Style-Type" content="text/css">

<LINK REL="SHORTCUT ICON" HREF="./favicon.ico">

<link type="text/css" rel="stylesheet" href="html/nexuse2e.css">
<!-- dojo/dijit library -->
<tiles:insert attribute="dojo"/>
<!-- dojo/dijit styles -->
<style type="text/css">
	@import "javascript/dijit/themes/tundra/tundra.css";
	@import "javascript/dojo/resources/dojo.css"
</style>
<style type="text/css" title="generatedCSS">
	/* container for automatically generated style classes */
</style>
<script src="javascript/Generic.js" language="JavaScript"
	type="text/javascript">
    </script>
<script src="javascript/CPA.js" language="JavaScript"
	type="text/javascript">
    </script>
<script src="javascript/Choreography.js" language="JavaScript"
	type="text/javascript">
    </script>
<script>
    	/*
			 * Allows display of debug messages.
			 */
			function debug(message) {
				var logDisplay = document.getElementById('logDisplay');
				var logArea = document.getElementById('logArea');
				if(logDisplay.style.display != "block") {
					logDisplay.style.display = "block";
				}
				logArea.value = logArea.value + "\n" + message;
			}
			
			/*
			 * Clears the log area.
			 */
			function clearLog() {
				var logArea = document.getElementById('logArea');
				logArea.value = "";
			}
			
			/*
			 * Hides the log.
			 */
			 function hideLog() {
			 		var logDisplay = document.getElementById('logDisplay');
			 		logDisplay.style.display = "none";
			 }
    </script>
</head>
<body class="tundra">
<div id="logDisplay"
	style="position: fixed; top: 10%; left: 10%; height: 80%; width: 80%; display: none; margin: 10px; color: #FFFFFF; background-color: #900000; font-weight: bold; font-family: courier; padding: 10px; text-align: left; z-index: 1000; filter: Alpha(opacity=80); -moz-opacity: 0.80;">
<div style="position: relative; width: 100%; height: 5%;">DEBUG
LOG [<a href="javascript: clearLog();"
	style="color: #FFFFFF; font-weight: bold;">clear</a>] [<a
	href="javascript: hideLog();"
	style="color: #FFFFFF; font-weight: bold;">hide</a>]</div>
<textarea id="logArea"
	style="position: relative; width: 100%; height: 95%;"></textarea></div>
<tiles:insert attribute="content" />
</body>
</html>
