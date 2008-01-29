<%@ taglib uri="/tags/nexus" prefix="nexus"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-nested" prefix="nested"%>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/struts-html-el" prefix="html-el"%>

<nexus:helpBar />

<center>

	<table width="100%">
		<tr>
			<td colspan="2" class="NEXUSSection">File Download</td>
		</tr>
		<tr>
			<td class="NEXUSValue">
				<c:forEach var="dir" items="${collection}">
				<li>${dir.name}
				<blockquote>
					<c:forEach var="file" items="${dir.files}">
						<li><a href="DownloadFile.do?file=${file}">${file.name}</a> [<a href="DownloadFile.do?file=${file}&compress=true">ZIP</a>]
					</c:forEach>
				</blockquote>
				</c:forEach>
			</td>
		</tr>
		
	</table>

	
</center>
<center><logic:messagesPresent>
	<div class="NexusError"><html:errors /></div>
</logic:messagesPresent> <logic:messagesPresent message="true">
	<html:messages id="msg" message="true">
		<div class="NexusMessage"><bean:write name="msg" /></div>
		<br />
	</html:messages>
</logic:messagesPresent></center>
