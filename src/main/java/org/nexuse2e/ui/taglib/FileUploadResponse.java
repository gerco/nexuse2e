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
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * This tag provides the ability to render a page for either usual display as plain html,
 * and as an additionally copy wrapped by a hidden &lt;textarea&gt;-tag as required by dojo.io.iframe.
 * 
 * Example:
 * <pre>
 * &lt;html&gt;
 *  &lt;body&gt;
 *   &lt;nexus:fileUploadResponse&gt;
 *    &lt;h1&gt;Headline&lt;/h1&gt;
 *    &lt;div&gt;Some Text&lt;/div&gt;
 *   &lt;nexus:/fileUploadResponse&gt;
 *   &lt;div&gt;Some other Text&lt;/div&gt;
 *  &lt;/body&gt;
 * &lt;/html&gt;
 * </pre>
 * 
 * would be rendered to:
 * <pre>
 * &lt;html&gt;
 *  &lt;body&gt;
 *   &lt;h1&gt;Headline&lt;/h1&gt;
 *   &lt;div&gt;Some Text&lt;/div&gt;
 *   &lt;textarea style="diplay: none;"&gt;
 *    &lt;h1&gt;Headline&lt;/h1&gt;
 *    &lt;div&gt;Some Text&lt;/div&gt;
 *   &lt;/textarea&gt;
 *   &lt;div&gt;Some other Text&lt;/div&gt;
 *  &lt;/body&gt;
 * &lt;/html&gt;
 * </pre>
 * 
 * Background: If a page should be shown as the response to a file upload (form post), the Dojo-Toolkit
 * expects the page's data wrapped by a &lt;textarea&gt;-tag, in order to parse and render it correctly.
 * Otherwise the page's data would be interpreted by the browser and passed to Dojo as a DOM object instead of text.
 * In order to display the page ie. in a ContentPane, Dojo needs the plain text information.
 * See http://www.dojotoolkit.com for more information.
 * 
 * @author Sebastian Schulze
 * @date 22.01.2009
 */
public class FileUploadResponse extends BodyTagSupport {

    private static final long serialVersionUID = 1L;

    /* (non-Javadoc)
     * @see javax.servlet.jsp.tagext.TagSupport#doStartTag()
     */
    @Override
    public int doStartTag() throws JspException {
        // do eval body
        return EVAL_BODY_BUFFERED;
    }
        
    /* (non-Javadoc)
     * @see javax.servlet.jsp.tagext.TagSupport#doEndTag()
     */
    @Override
    public int doEndTag() throws JspException {
        try {
            // write the data as usual
            JspWriter out = pageContext.getOut();
            bodyContent.writeOut( out );
            // begin the text area
            out.println( "<textarea style=\"display: none;\">" );
            // write the data again into the textarea
            bodyContent.writeOut( out );
            // end the textarea
            out.println( "</textarea>" );
        } catch ( IOException e ) {
            throw new JspException( e );
        }
        return EVAL_PAGE;
    }
}
