/**
 *  NEXUSe2e Business Messaging Open Source
 *  Copyright 2000-2009, Tamgroup and X-ioma GmbH
 *
 *  This is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation version 2.1 of
 *  the License.
 *
 *  This software is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this software; if not, write to the Free
 *  Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *  02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.nexuse2e.ui.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang.RandomStringUtils;
import org.nexuse2e.configuration.EngineConfiguration;

/**
 * Generates the crumbs navigation bar.
 * @author guido.esch
 * @modified Sebastian Schulze
 */
public class Crumbs extends TagSupport {

    private static final long     serialVersionUID            = -4310828993090572143L;

    /**
     * The default CSS class is <code>NEXUSScreenPathLink</code>.
     */
    protected static final String DEFAULT_STYLE_CLASS         = "NEXUSScreenPathLink";

    /**
     * The default separator is <code>&gt;</code>.
     */
    protected static final String DEFAULT_SEPARATOR           = ">";

    /**
     * Default length of the randomly generated ids for div container.
     */
    protected static final int    DEFAULT_CONTAINER_ID_LENGTH = 10;

    private String                styleClass                  = DEFAULT_STYLE_CLASS;
    private String                separator                   = DEFAULT_SEPARATOR;

    /* (non-Javadoc)
     * @see javax.servlet.jsp.tagext.TagSupport#doStartTag()
     */
    @Override
    public int doStartTag() throws JspException {

        JspWriter writer = pageContext.getOut();
        EngineConfiguration engineConfiguration =
            (EngineConfiguration) pageContext.getRequest().getAttribute( "engineConfiguration" );
        try {
            // div container
            // first we need a UID as DOM id (in order to support multiple usage of this tag on one page) 
            String containerId = RandomStringUtils.randomAlphabetic( DEFAULT_CONTAINER_ID_LENGTH );
            writer.write( "<div id=\"" + containerId + "\" class=\"" + styleClass + "\">&nbsp;</div>\n" );
            writer.write( "<script type=\"text/javascript\">\n" );
            writer.write( "\tdojo.addOnLoad(function() {\n" );
            writer.print( "\t\trefreshMenuTree();\n" );
            if (engineConfiguration != null) {
                writer.print( "\t\tcheckForChangedConfiguration(" + engineConfiguration.isChanged() + ");\n" );
            }
            writer.write( "\t\tvar crumbsElement = document.getElementById('" + containerId + "');\n" );
            writer.write( "\t\tcrumbsElement.innerHTML = getCrumbs();\n" );
            writer.write( "\t});\n" );
            writer.write( "</script>\n" );
        } catch ( IOException e ) {
            throw new JspException( e );
        }
        return SKIP_BODY;
    }

    public String getStyleClass() {

        return styleClass;
    }

    public void setStyleClass( String styleClass ) {

        this.styleClass = styleClass;
    }

    public String getSeparator() {

        return separator;
    }

    public void setSeparator( String separator ) {

        this.separator = separator;
    }
}
