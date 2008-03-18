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

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;

/**
 * This tag generates a HTML select that can click a button on change
 * in order to submit the specified form and display the reponse in a
 * ContentPane widget of the DOJO toolkit.
 * We need a little smelly workaround to achive our goals. See DOJO
 * toolkit documentation for details.
 * The select tag MUST be placed within the form definition!
 * If <code>form</code> parameter is not provided a JavaScript will be
 * generated that tries to reveal the form name automatically.
 * @author Sebastian Schulze
 * @date 11.01.2007
 */
public class Select extends BodyTagSupport {

    private static final long      serialVersionUID           = 6925838834510690118L;

    /**
     * The default CSS class is <code>NEXUSValue</code>.
     */
    protected static final String  DEFAULT_STYLE_CLASS        = "NEXUSSelect";

    /**
     * The default widget id is <code>docpane</code>.
     */
    protected static final String  DEFAULT_WIDGET_ID          = "docpane";

    /**
     * The default value for the <code>submitOnChange</code> paremeter is <code>false</code>.
     */
    protected static final boolean DEFAULT_SUBMIT_ON_CHANGE   = false;

    /**
     * Default length of the randomly generated ids for buttons.
     */
    protected static final int     DEFAULT_BUTTON_ID_LENGTH   = 10;
    /**
     * Default length of the randomly generated method names.
     */
    protected static final int     DEFAULT_METHOD_NAME_LENGTH = DEFAULT_BUTTON_ID_LENGTH;

    private String                 form;
    private String                 styleClass                 = DEFAULT_STYLE_CLASS;
    private String                 widgetId                   = DEFAULT_WIDGET_ID;
    private boolean                submitOnChange             = DEFAULT_SUBMIT_ON_CHANGE;
    private String                 onSubmit;
    private String                 name;
    private String                 sendFileForm               = null;
    // generated button id
    private String                 buttonId;

    /* (non-Javadoc)
     * @see javax.servlet.jsp.tagext.BodyTagSupport#doStartTag()
     */
    @Override
    public int doStartTag() throws JspException {

        JspWriter writer = pageContext.getOut();
        try {
            // create hidden button
            // first we need a UID as DOM id (in order to support multiple usage of this tag on one page) 
            buttonId = RandomStringUtils.randomAlphabetic( DEFAULT_BUTTON_ID_LENGTH );
            // create link that clicks the hidden button
            writer.print( "<select"
                    + ( name != null ? " name=\"" + name + "\"" : "" )
                    + ( submitOnChange ? " onchange=\"" + ( onSubmit == null ? "" : onSubmit + " " )
                            + ( "true".equalsIgnoreCase( sendFileForm ) ? "submitFileForm( " : "submitForm( " )
                            + ( form == null ? "document.forms[0]" : form ) + " );\"" : "" )
                            + ( StringUtils.isEmpty(styleClass) ? "" : " class=\"" + styleClass )
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
            writer.print( "</select>\n" );
            // add scriptlet that handles the click event and loads the data
            writer.print( "<script>\n" );
            // we need a UID as method name (in order to support multiple usage of this tag on one page)
            writer.print( "dojo.addOnLoad(" );
            writer.print( "function() { \n" );
            writer.print( "\tnew dojo.io.FormBind({\n" );
            if ( form != null && form.length() > 0 ) {
                writer.print( "\t\tformNode: document.forms['" + form + "'],\n" );
            } else {
                // try to find form automatically by referencing the parent of the generated button
                writer.print( "\t\tformNode: document.getElementById('" + buttonId + "').form,\n" );
            }
            writer.print( "\t\tload: function(load, data, e) {\n" );
            writer.print( "\t\t\tpanel = dojo.widget.byId(\"" + widgetId + "\");\n" );
            writer.print( "\t\t\tpanel.setContent(data);\n" );
            writer.print( "\t\t}\n" );
            writer.print( "\t}));\n" );
            writer.print( "}\n" );
            writer.print( "</script>\n" );
        } catch ( IOException e ) {
            throw new JspException( e );
        }
        return EVAL_PAGE;
    }

    /**
     * Retruns the DOM name of the form.
     * @return the form name
     */
    public String getForm() {

        return form;
    }

    /**
     * Sets the DOM name of the form.
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
     * @return the submitOnChange
     */
    public boolean getSubmitOnChange() {

        return submitOnChange;
    }

    /**
     * <code>true</code> if form shall be submitted on change. Otherwise <code>false</code>.
     * @param submitOnChange the submitOnChange to set
     */
    public void setSubmitOnChange( boolean submitOnChange ) {

        this.submitOnChange = submitOnChange;
    }

    /**
     * @return the name
     */
    public String getName() {

        return name;
    }

    /**
     * Sets the name of the select form element.
     * @param name the name to set
     */
    public void setName( String name ) {

        this.name = name;
    }

    public String getOnSubmit() {

        return onSubmit;
    }

    public void setOnSubmit( String onSubmit ) {

        this.onSubmit = onSubmit;
    }

    
    public String getSendFileForm() {
    
        return sendFileForm;
    }

    
    public void setSendFileForm( String sendFileForm ) {
    
        this.sendFileForm = sendFileForm;
    }

}
