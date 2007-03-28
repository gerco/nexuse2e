<%@ taglib uri="/tags/nexus" prefix="nexus" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-nested" prefix="nested" %>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %> 
<%@ taglib uri="/tags/struts-html-el" prefix="html-el" %>

<nexus:helpBar helpDoc="documentation/Choreography.htm"/>

    <html:form action="ChoreographyCreate">
        <center>
            <table class="NEXUS_TABLE" width="100%">
						    <tr>
						        <td>
						        	<nexus:crumbs/>
						        </td>
						    </tr>
						    <tr>
						        <td class="NEXUSScreenName">Add Choreography</td>
						    </tr>
						</table>

            <table class="NEXUS_TABLE" width="100%">
                <tr>
                    <td class="NEXUSSection">Choreography ID</td>
                    <td class="NEXUSSection"><html:text size="30" property="choreographyName"></html:text></td>
                </tr>
                <tr>
                    <td class="NEXUSName">Description</td>
                    <td class="NEXUSValue"><html:text size="30" property="description"></html:text></td>
                </tr>
            </table>
            <table class="NEXUS_BUTTON_TABLE" width="100%">
                <tr>
                    <td>&nbsp;</td>
                    <td class="BUTTON_RIGHT"><nexus:submit precondition="checkChoreographyID()" styleClass="button"><img src="images/submit.gif" class="button">Create</nexus:submit></td>
                </tr>
            </table>
        </center>
    </html:form>
</body>
