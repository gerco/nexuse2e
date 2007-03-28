<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-nested" prefix="nested"%>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/nexus" prefix="nexus"%>

<!-- expand tree -->
<script>
	dojo.addOnLoad( function() {
		var rootNode = getTreeNode('Home.do');
		getMenuTreeLoadingController().expand(rootNode);
		getMenuTreeSelector().doSelect(rootNode);
	});
</script>

<nexus:helpBar helpDoc="documentation/NEXUSe2e.html" />

<table class="NEXUS_TABLE" width="100%">
	<tr>
		<td><nexus:crumbs /></td>
	</tr>
	<tr>
		<td class="NEXUSScreenName">NEXUSe2e</td>
	</tr>
</table>

<table class="NEXUS_TABLE" width="100%">
	<tr>
		<td class="NEXUSSection">Property</td>
		<td class="NEXUSSection">Value</td>
	</tr>
	<tr>
		<td class="NEXUSNameNoWidth">NEXUSe2e Version</td>
		<td class="NEXUSNameNoWidth">${NEXUSe2e_version}</td>
	</tr>
	<tr>
		<td class="NEXUSNameNoWidth">Java Version</td>
		<td class="NEXUSNameNoWidth">${java_version}</td>
	</tr>
	<tr>
		<td class="NEXUSNameNoWidth">Java Home</td>
		<td class="NEXUSNameNoWidth">${java_home}</td>
	</tr>
	<tr>
		<td class="NEXUSNameNoWidth">Java Classpath</td>
		<td class="NEXUSNameNoWidth">${java_classpath}</td>
	</tr>
	<tr>
		<td class="NEXUSNameNoWidth">License</td>
		<td class="NEXUSNameNoWidth"><a href="html/lgpl.html" target="#blank">GNU Lesser General Public License</a> (LGPL), Version 2.1</td>
	</tr>
</table>
<!--
<a href="#" onClick="refreshMenuTree();">Reload</a>
<div id="log" style="color: #FFFFFF; background-color: #000080; padding: 10px; text-align: left;"><p>DEBUG LOG</p></div>
-->

<center><logic:messagesPresent>
	<div class="NexusError"><html:errors /></div>
</logic:messagesPresent></center>
