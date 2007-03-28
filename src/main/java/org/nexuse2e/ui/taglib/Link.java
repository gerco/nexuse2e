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
 * Handles the display of linked pages in the correct DOJO ContentPane widget.
 * @author Sebastian Schulze
 * @date 02.01.2007
 */
public class Link extends BodyTagSupport {

    private static final long     serialVersionUID  = 8745028952283187452L;

    // private static final Logger LOG = Logger.getLogger( Link.class );

    /**
     * The default widget id is <code>docpane</code>.
     */
    protected static final String DEFAULT_WIDGET_ID = "docpane";

    private String                href;
    private String                styleClass;
    private String                widgetId          = DEFAULT_WIDGET_ID;
    private String                precondition;
    private String                onClick;
    private String                id;

    /* (non-Javadoc)
     * @see javax.servlet.jsp.tagext.BodyTagSupport#doStartTag()
     */
    @Override
    public int doStartTag() throws JspException {

        JspWriter writer = pageContext.getOut();
        try {
            writer.print( "<a" + ( id != null ? " id=" + id : "" ) + " href=\"javascript: "
                    + ( precondition != null && precondition.length() > 0 ? "if(" + precondition + ") { " : "" )
                    + ( onClick != null ? onClick + " " : "" ) + "dojo.widget.byId('" + widgetId + "').setUrl('" + href + "');"
                    + ( precondition != null && precondition.length() > 0 ? " }" : "" )
                    + "\"" + ( styleClass != null ? " class=\"" + styleClass + "\"" : "" ) + ">" );
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
            writer.print( "</a>" );
        } catch ( IOException e ) {
            throw new JspException( e );
        }
        return EVAL_PAGE;
    }

    /**
     * @return the href
     */
    public String getHref() {

        return href;
    }

    /**
     * @param href the href to set
     */
    public void setHref( String href ) {

        this.href = href;
    }

    /**
     * @return the widgetId
     */
    public String getWidgetId() {

        return widgetId;
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
     * to <code>true</code> before the link will be followed.
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
     * Sets additional JavaSript code to execute "on click".
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
     * Sets the DOM id of the generated link element.
     * @param id the id to set
     */
    @Override
    public void setId( String id ) {

        this.id = id;
    }

}
