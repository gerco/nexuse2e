<%@page import="org.nexuse2e.Version"%>

<div style="margin-left: 76px; height: 164px; width: 159px; display: inline-block; background-image: url(images/logo.gif);">
</div>
<div style="position: absolute; top: 20px; right: 20px;  width:100%; text-align: right; display:inline;">
	<div style="color:#ffffff;font-size:20pt;">
		<%@ include file="../config/machine_name.txt" %>
	</div>
	<br>
	<div>
	<%= Version.getVersion() %>
	</div>
</div>