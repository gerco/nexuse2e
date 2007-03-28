<%@ taglib uri="/tags/nexus" prefix="nexus"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>

<center>

<table class="NEXUS_TABLE" style="width:300px;margin-top:100px">
	<tr>
		<td class="NEXUSScreenName">Login</td>
	</tr>
</table>

<form name="loginForm" method="post" action="LoginCheck.do">
<table class="NEXUS_TABLE" style="width:300px;">
	<tr>
		<td class="NEXUSName">Login name</td>
		<td class="NEXUSValue"><input id="loginName" type="text"
			name="user" value="admin"> <script>
  				// set focus to login name input field
  				dojo.addOnLoad( function() {
  					document.getElementById('loginName').focus();
  				});
  			</script></td>
	</tr>
	<tr>
		<td class="NEXUSName">Password</td>
		<td class="NEXUSValue"><input type="password" name="pass" value="admin">
		</td>
	</tr>
</table>
<table class="NEXUS_BUTTON_TABLE" style="width:300px;">
	<tr>
		<td>&nbsp;</td>
		<td class="NexusHeaderLink"><input type="reset"
			style="display: none;"> <a
			href="javascript: document.forms['loginForm'].reset();"
			class="button"><img src="images/reset.gif" class="button">Reset</a>
		</td>
		<td><input type="submit" style="display: none;"> <a
			href="#" onClick="document.forms['loginForm'].submit();"
			class="button"><img src="images/submit.gif" class="button">Log
		in</a></td>
	</tr>
</table>
</form>
</center>

<logic:messagesPresent>
	<div class="NexusError"><html:errors /></div>
</logic:messagesPresent>
