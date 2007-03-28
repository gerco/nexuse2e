<%@ taglib uri="/tags/nexus" prefix="nexus" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-nested" prefix="nested" %>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %> 

<nexus:helpBar helpDoc="documentation/Collaboration_Partners.htm"/>

      <c:set var="formAction" value="ComponentCreate"/>
      <c:set var="titleText" value="Add Component"/>

      <logic:notEqual name="componentForm" property="nxComponentId" value="0">
        <c:set var="formAction" value="ComponentUpdate"/>
      <c:set var="titleText" value="Update Component"/>
      </logic:notEqual>

      <table class="NEXUS_TABLE" width="100%">
          <tr>
              <td><nexus:crumbs styleClass="NEXUSScreenPathLink"/></td>
          </tr>
          <tr>
              <td class="NEXUSScreenName">${titleText}</td>
          </tr>
      </table>
      <html:form action="${formAction}"> 
        <table class="NEXUS_TABLE" width="100%">
            <tr>
                <td class="NEXUSName">Name</td>
                <td class="NEXUSValue"><html:text property="name" size="50"/></td>
            </tr>
          <logic:notEqual name="componentForm" property="nxComponentId" value="0">
            <tr>
                <td class="NEXUSName">Type</td>
                <td class="NEXUSValue"><bean:write name="componentForm" property="typeString"/></td>
            </tr>
          </logic:notEqual>
            <tr>
                <td class="NEXUSName">Class Name</td>
                <td class="NEXUSValue"><html:text property="className" size="50"/></td>
            </tr>
            <tr>
                <td class="NEXUSName">Description</td>
                <td class="NEXUSValue"><html:text property="description" size="50"/></td>
            </tr>
           
            <logic:equal name="componentForm" property="type" value="1">
            <tr>
                <td valign="top" class="NEXUSName">pipelines ?</td>
                <td valign="top" class="NEXUSValue">
                   
                </td>
            </tr>
            </logic:equal>
        </table>

        <table class="NEXUS_BUTTON_TABLE">
          <tr>
            <td>
              &nbsp;
            </td>
            <td class="NexusHeaderLink" style="text-align: right;">
              <nexus:submit styleClass="button"><img src="images/submit.gif" class="button">Save</nexus:submit>
            </td>
          <logic:notEqual name="componentForm" property="nxComponentId" value="0">
            <td class="NexusHeaderLink" style="text-align: right;">
              <nexus:link href="ComponentDelete.do?nxComponentId=${componentForm.nxComponentId}" styleClass="button"><img src="images/delete.gif" class="button">Delete</nexus:link>
            </td>
          </logic:notEqual>
          </tr>
        </table>
    </html:form>
