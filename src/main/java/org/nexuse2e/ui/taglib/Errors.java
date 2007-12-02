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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Iterator;

import javax.servlet.jsp.JspException;

import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.taglib.TagUtils;
import org.apache.struts.taglib.html.ErrorsTag;

/**
 * Generates the crumbs navigation bar.
 * @author guido.esch
 * @modified Sebastian Schulze
 */
public class Errors extends ErrorsTag {

    /**
     * 
     */
    private static final long serialVersionUID = 3734553574494067353L;

    /* (non-Javadoc)
     * @see org.apache.struts.taglib.html.ErrorsTag#doStartTag()
     */
    @SuppressWarnings("unchecked")
    @Override
    public int doStartTag() throws JspException {

        ActionMessages errors = null;
        try {
            errors = TagUtils.getInstance().getActionMessages( pageContext, name );
        } catch ( JspException e ) {
            TagUtils.getInstance().saveException( pageContext, e );
            throw e;
        }
        if ( ( errors == null ) || errors.isEmpty() ) {
            return ( EVAL_BODY_INCLUDE );
        }
        boolean headerPresent = TagUtils.getInstance().present( pageContext, bundle, locale, getHeader() );
        boolean footerPresent = TagUtils.getInstance().present( pageContext, bundle, locale, getFooter() );
        boolean prefixPresent = TagUtils.getInstance().present( pageContext, bundle, locale, getPrefix() );
        boolean suffixPresent = TagUtils.getInstance().present( pageContext, bundle, locale, getSuffix() );
        StringBuffer results = new StringBuffer();
        StringBuffer stacktraces = new StringBuffer();
        boolean headerDone = false;
        String message = null;
        Iterator<ActionMessage> reports = ( property == null ) ? errors.get() : errors.get( property );
        while ( reports.hasNext() ) {
            ActionMessage report = (ActionMessage) reports.next();
            if ( !headerDone ) {
                if ( headerPresent ) {
                    message = TagUtils.getInstance().message( pageContext, bundle, locale, getHeader() );
                    results.append( message );
                }
                headerDone = true;
            }
            if ( prefixPresent ) {
                message = TagUtils.getInstance().message( pageContext, bundle, locale, getPrefix() );
                results.append( message );
            }
            message = TagUtils.getInstance().message( pageContext, bundle, locale, report.getKey(), report.getValues() );
            if ( report.getValues() != null ) {
                for ( Object param : report.getValues() ) {
                    if ( param instanceof Exception ) {
                        Exception e = (Exception) param;
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        PrintStream ps = new PrintStream( baos );
                        e.printStackTrace( ps );
                        stacktraces.append( "<!--\n" );
                        stacktraces.append( new String( baos.toByteArray() ) );
                        stacktraces.append( "-->\n" );
    
                    }
                }
            }
            if ( message != null ) {
                results.append( message );
            }
            if ( suffixPresent ) {
                message = TagUtils.getInstance().message( pageContext, bundle, locale, getSuffix() );
                results.append( message );
            }
        }
        if ( headerDone && footerPresent ) {
            message = TagUtils.getInstance().message( pageContext, bundle, locale, getFooter() );
            results.append( message );
        }
        TagUtils.getInstance().write( pageContext, results.toString() );
        TagUtils.getInstance().write( pageContext, stacktraces.toString() );
        return ( EVAL_BODY_INCLUDE );
    }

}
