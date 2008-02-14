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

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.nexuse2e.ui.action.NexusE2EAction;

/**
 * Prints code for the "help" and "login"/"logout" bar.
 * @author Sebastian Schulze
 * @date 03.01.2007
 */
public class HelpBar extends TagSupport {

    private static final long     serialVersionUID    = 1305066365561978638L;

    /**
     * Default style class for the generated elements (i.e. to reference in CSS) is <code>subheader</code>.
     */
    protected static final String DEFAULT_STYLE_CLASS = "helpBar";
    /**
     * The default widget id is <code>docpane</code>.
     */
    protected static final String DEFAULT_WIDGET_ID   = "docpane";

    protected String              helpDoc             = null;
    protected String              styleClass          = DEFAULT_STYLE_CLASS;
    protected String              widgetId            = DEFAULT_WIDGET_ID;

    /* (non-Javadoc)
     * @see javax.servlet.jsp.tagext.TagSupport#doStartTag()
     */
    @Override
    public int doStartTag() throws JspException {

        JspWriter writer = pageContext.getOut();
        try {
            boolean printDelimiter = false;
            writer.print( "<div class=\"" + styleClass + "\">&nbsp;" );
            // if helpDoc was defined
            if ( helpDoc != null ) {
                writer.print( "<a href=\"javascript: dojo.widget.byId('" + widgetId + "').setUrl('" + helpDoc
                        + "');\" class=\"" + styleClass + "\"><img src=\"images/icons/help.png\" class=\"" + styleClass
                        + "\"></a>&nbsp;<a href=\"javascript: dojo.widget.byId('" + widgetId + "').setUrl('" + helpDoc
                        + "');\" class=\"" + styleClass + "\">Help</a>" );
                printDelimiter = true;
            }
            // if user is logged in
            HttpSession session = pageContext.getSession();
            if ( session != null && session.getAttribute( NexusE2EAction.ATTRIBUTE_USER ) != null ) {
                if ( printDelimiter ) {
                    writer.print( "&nbsp;|&nbsp;" );
                }
                //writer.print( "<a href=\"javascript: dojo.widget.byId('" + widgetId + "').setUrl('Logout.do');\" class=\"" + styleClass + "\">Logout</a>" );
                writer.print( "<a href=\"Logout.do\" class=\"" + styleClass + "\">Logout</a>" );
            }
            writer.print( "</div>\n" );
        } catch ( IOException e ) {
            throw new JspException( e );
        }
        return SKIP_BODY;
    }

    /**
     * @return the helpDoc
     */
    public String getHelpDoc() {

        return helpDoc;
    }

    /**
     * Sets the path to the help document.
     * @param helpDoc the helpDoc to set
     */
    public void setHelpDoc( String helpDoc ) {

        this.helpDoc = helpDoc;
    }

    /**
     * @return the styleClass
     */
    public String getStyleClass() {

        return styleClass;
    }

    /**
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
}
