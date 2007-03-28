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

/**
 * Resets the specified form or the form this tag is placed within.
 * @author Sebastian Schulze
 * @date 09.01.2007
 */
public class Reset extends BodyTagSupport {

    private static final long     serialVersionUID         = -5889680197134035059L;

    /**
     * The default CSS class is <code>button</code>.
     */
    protected static final String DEFAULT_STYLE_CLASS      = "button";

    /**
     * Default length of the randomly generated ids for buttons.
     */
    protected static final int    DEFAULT_BUTTON_ID_LENGTH = 10;

    private String                form;
    private String                styleClass               = DEFAULT_STYLE_CLASS;
    // generated button id
    private String                buttonId;

    /* (non-Javadoc)
     * @see javax.servlet.jsp.tagext.BodyTagSupport#doStartTag()
     */
    @Override
    public int doStartTag() throws JspException {

        JspWriter writer = pageContext.getOut();
        try {
            if ( form != null && form.length() > 0 ) {
                // create link that clicks the hidden button
                writer.print( "<a href=\"javascript: document.forms['" + form + "'].reset();\" class=\"" + styleClass
                        + "\">" );
            } else {
                // create hidden button
                // first we need a UID as DOM id (in order to support multiple usage of this tag on one page) 
                buttonId = RandomStringUtils.randomAlphabetic( DEFAULT_BUTTON_ID_LENGTH );
                writer.print( "<input type=\"reset\" id=\"" + buttonId + "\" style=\"display: none;\">\n" ); // needed to automatically detect the form
                // create link that clicks the hidden button
                writer.print( "<a href=\"javascript: dojo.byId('" + buttonId + "').click();\" class=\"" + styleClass
                        + "\">" );
            }
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

}
