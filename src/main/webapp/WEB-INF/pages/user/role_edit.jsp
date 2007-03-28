<%@ taglib uri="/tags/nexus" prefix="nexus" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/c" prefix="c" %>
<%@ page import="java.util.*" %>
<%@ page import="org.nexuse2e.*" %>
<%@ page import="org.nexuse2e.pojo.*" %>
<%@ page import="org.nexuse2e.ui.form.*" %>
<%@ page import="org.nexuse2e.ui.structure.*" %>

<nexus:helpBar helpDoc="documentation/User.htm"/>

<table class="NEXUS_TABLE" width="100%">
    <tr>
        <td>
        	<nexus:crumbs/>
        </td>
    </tr>
    <tr>
        <td class="NEXUSScreenName">Role Edit</td>
    </tr>
</table>

<html:form method="post" action="RoleSave.do">
	<input name="nxRoleId" type="hidden" value="${roleForm.nxRoleId}">
	<table class="NEXUS_TABLE">
		<tr>
			<td class="NEXUSName">
	 			Name
	 		</td>
	 		<td class="NEXUSValue">
	 			<input name="name" type="text" value="${roleForm.name}">
	 		</td>
	 	</tr>
	 	<tr>
			<td class="NEXUSName">
	 			Description
	 		</td>
	 		<td class="NEXUSValue">
	 			<input name="description" type="text" value="${roleForm.description}">
	 		</td>
	 	</tr>
	 	<tr>
			<td class="NEXUSName" style="vertical-align: top;">
	 			<script language="text/javascript">
	 				this.grantAll = function () {
	 					//debug("checkAll");
	 					for(var i = 0; i < roleForm.elements.length; i++) {
	 						var currElement = roleForm.elements[i];
	 						if(currElement.type == "checkbox") {
	 							//debug(currElement.name + " " + currElement.checked);
	 							currElement.checked = true;
	 							//debug(currElement.name + " " + currElement.checked);
	 						}
	 					}
	 				}
	 			</script>
	 			Grants
	 			<p>
		 			<a href="javascript: scriptScope.grantAll();" class="button"><img src="images/submit.gif" class="button">Check all</a>
		 		</p>
	 		</td>
	 		<td class="NEXUSValue">
	 			<%!
	 				// method to print structure tree recursively
	 				public void iterateTree( List<StructureNode> nodes, StringBuffer sb, int indent, Map<String,GrantPojo> grants ) {
	 					if ( nodes != null ) {
		 					for ( StructureNode node : nodes ) {
		 						sb.append( "<div style=\"padding-left: " + indent * 20 + "; background-color: " + ( indent % 2 == 0 ? "#D0D0E0" : "#D8D8E8" ) + "\">" );
		 						sb.append( "\t<input type=\"checkbox\" name=\"__grant:" + node.getTarget() + "\"" + ( grants.containsKey( node.getTarget() ) ? " checked" : "" ) + "> " + node.getLabel() + "\n" );
		 						sb.append( "</div>" );
		 						if ( node instanceof ParentalStructureNode ) {
		 							ParentalStructureNode parentNode = (ParentalStructureNode) node;
									iterateTree( parentNode.getChildren(), sb, indent + 1, grants );
		 						}
		 					}
		 				}
	 				}
	 			%>
	 			<%
	 				List<StructureNode> nodes = ( (StructureService) Engine.getInstance().getBeanFactory().getBean( "structureService" ) ).getMenuSkeleton();
	 				StringBuffer sb = new StringBuffer();
	 				Map<String,GrantPojo> grants = ( (RoleForm) session.getAttribute( "roleForm" ) ).getGrants();
	 				// add wildcard
	 				sb.append( "<div style=\"padding-left: 0; background-color: #D8D8E8\">" );
 					sb.append( "\t<input type=\"checkbox\" name=\"__grant:*\"" + ( grants.containsKey( "*" ) ? " checked" : "" ) + "> <span style=\"font-style: italic;\">WILDCARD (grant full access)</span>\n" );
 					sb.append( "</div>" );
	 				iterateTree( nodes, sb, 0, grants );
	 				out.print( sb.toString() );
	 			%>	 			
	 		</td>
	 	</tr>
	</table>
	<table class="NEXUS_BUTTON_TABLE">
		<tr>
				<td>
					&nbsp;
				</td>
				<td class="NexusHeaderLink" style="text-align: right;">
					<nexus:submit styleClass="button"><img src="images/submit.gif" class="button">Save</nexus:submit>
				</td>
		    <td class="NexusHeaderLink" style="text-align: right;">
					<nexus:link precondition="confirmDelete('Are you sure you want to delete this role?')" href="RoleDelete.do?nxRoleId=${roleForm.nxRoleId}" styleClass="button"><img src="images/delete.gif" class="button">Delete</nexus:link>
				</td>
		</tr>
	</table>
</html:form>

<logic:messagesPresent>
	<div class="NexusError"><html:errors /></div>
</logic:messagesPresent>
