/**
 * NEXUSe2e Business Messaging Open Source  
 * Copyright 2007, Tamgroup and X-ioma GmbH   
 *  
 * This is free software; you can redistribute it and/or modify it  
 * under the terms of the GNU Lesser General Public License as  
 * published by the Free Software Foundation version 2.1 of  
 * the License.  
 *  
 * This software is distributed in the hope that it will be useful,  
 * but WITHOUT ANY WARRANTY; without even the implied warranty of  
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU  
 * Lesser General Public License for more details.  
 *  
 * You should have received a copy of the GNU Lesser General Public  
 * License along with this software; if not, write to the Free  
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.nexuse2e.ui.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * This tag generates a link that can click a button in order
 * to submit a specified form and display the reponse in a ContentPane
 * widget of the DOJO toolkit.
 * We need a little smelly workaround to achive our goals. See DOJO
 * toolkit documentation for details.
 * The submit tag MUST be placed within the form definition!
 * If <code>form</code> parameter is not provided a JavaScript will be
 * generated that tries to reveal the form name automatically.
 * @author Sebastian Schulze
 * @date 02.01.2007
 */
public class Submit extends BodyTagSupport {

    private static final long     serialVersionUID           = 5236597686816394132L;

    /**
     * The default CSS class is <code>button</code>.
     */
    protected static final String DEFAULT_STYLE_CLASS        = "button";

    /**
     * The default widget id is <code>docpane</code>.
     */
    protected static final String DEFAULT_WIDGET_ID          = "docpane";

    /**
     * Default length of the randomly generated ids for buttons.
     */
    protected static final int    DEFAULT_BUTTON_ID_LENGTH   = 10;
    /**
     * Default length of the randomly generated method names.
     */
    protected static final int    DEFAULT_METHOD_NAME_LENGTH = DEFAULT_BUTTON_ID_LENGTH;

    private String                form;
    private String                styleClass                 = DEFAULT_STYLE_CLASS;
    private String                widgetId                   = DEFAULT_WIDGET_ID;
    private String                onClick;
    private String                precondition;
    private String                id;
    private String                sendFileForm               = null;

    // generated button id
    // private String buttonId;

    /* (non-Javadoc)
     * @see javax.servlet.jsp.tagext.BodyTagSupport#doStartTag()
     */
    @Override
    public int doStartTag() throws JspException {

        JspWriter writer = pageContext.getOut();
        try {
            // create hidden button
            // first we need a UID as DOM id (in order to support multiple usage of this tag on one page) 
            // buttonId = RandomStringUtils.randomAlphabetic( DEFAULT_BUTTON_ID_LENGTH );
            // writer.print( "<input type=\"submit\" id=\"" + buttonId + "\" style=\"display: none;\">\n" );
            // create link that clicks the hidden button
            writer.print( "<a" + ( id != null ? " id=" + id : "" ) + " href=\"javascript: "
                    + ( precondition != null && precondition.length() > 0 ? "if(" + precondition + ") { " : "" )
                    + ( onClick != null ? onClick + " " : "" )
                    + ( "true".equalsIgnoreCase( sendFileForm ) ? "submitFileForm( " : "submitForm( " )
                    + ( form != null ? form : "document.forms[0]" ) + " );"
                    + ( precondition != null && precondition.length() > 0 ? " }" : "" ) + "\" class=\"" + styleClass
                    + "\">" );
        } catch ( IOException e ) {
            throw new JspException( e );
        }
        return EVAL_BODY_INCLUDE;
    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.tagext.BodyTagSupport#doEndTag()
     */
    @Override
    public int doEndTag() throws JspException {

        JspWriter writer = pageContext.getOut();
        try {
            // close anchor tag
            writer.print( "</a>\n" );
            /* This approach does not work, sitched over to submitFormData in layout_default.jsp
             // add scriptlet that handles the click event and loads the data
             writer.print( "<script>\n" );
             // we need a UID as method name (in order to support multiple usage of this tag on one page)
             String methodName = RandomStringUtils.randomAlphabetic( DEFAULT_METHOD_NAME_LENGTH );
             writer.print( "function " + methodName + "() { \n" );
             writer.print( "\tnew dojo.io.FormBind({\n" );
             if ( form != null && form.length() > 0 ) {
             writer.print( "\t\tformNode: document.forms['" + form + "'],\n" );
             }
             else {
             // try to find form automatically by referencing the parent of the generated button
             writer.print( "\t\tformNode: document.getElementById('" + buttonId + "').form,\n" );
             }
             writer.print( "\t\tload: function(load, data, e) {\n" );
             writer.print( "\t\t\tpanel = dojo.widget.byId(\"" + widgetId + "\");\n" );
             writer.print( "\t\t\tpanel.setContent(data);\n" );
             writer.print( "\t\t}\n" );
             writer.print( "\t});\n" );
             writer.print( "}\n" );
             writer.print( "dojo.addOnLoad(" + methodName + ");" );
             writer.print( "</script>\n" );
             */
        } catch ( IOException e ) {
            throw new JspException( e );
        }
        return EVAL_PAGE;
    }

    /**
     * Retruns the DOM reference of the form.
     * @return the form name
     */
    public String getForm() {

        return form;
    }

    /**
     * Sets the DOM reference of the form.
     * @param form the form name
     */
    public void setForm( String form ) {

        this.form = form;
    }

    /**
     * @return the styleClass
     */
    public String getStyleClass() {

        return styleClass;
    }

    /**
     * Sets the CSS class to use for the generated link.
     * @param styleClass the styleClass to set
     */
    public void setStyleClass( String styleClass ) {

        this.styleClass = styleClass;
    }

    /**
     * @return the widgetId
     */
    public String getWidgetId() {

        return widgetId;
    }

    /**
     * @param widgetId the widgetId to set
     */
    public void setWidgetId( String widgetId ) {

        this.widgetId = widgetId;
    }

    /**
     * @return the precondition
     */
    public String getPrecondition() {

        return precondition;
    }

    /**
     * Sets a JavaScript expression that must evaluate
     * to <code>true</code> before the form will be submitted.
     * @param precondition the precondition to set
     */
    public void setPrecondition( String precondition ) {

        this.precondition = precondition;
    }

    /**
     * @return the onClick
     */
    public String getOnClick() {

        return onClick;
    }

    /**
     * Sets an additional JavaScript string that shall be executed right before the submit.
     * @param onClick the onClick to set
     */
    public void setOnClick( String onClick ) {

        this.onClick = onClick;
    }

    /**
     * @return the id
     */
    @Override
    public String getId() {

        return id;
    }

    /**
     * The DOM id of the button.
     * @param id the id to set
     */
    @Override
    public void setId( String id ) {

        this.id = id;
    }

    public String getSendFileForm() {

        return sendFileForm;
    }

    public void setSendFileForm( String sendFileForm ) {

        this.sendFileForm = sendFileForm;
    }

}
