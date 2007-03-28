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

import java.util.Collection;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.beanutils.BeanUtils;

/**
 * This tag renders a list of <code>&lt;option&gt;</code> HTML tags
 * from a collection bean.
 * 
 * @author jonas.reese
 */
public class Options extends BodyTagSupport {

    private static final long serialVersionUID = 1L;

    private String            collection;
    private String            value;
    private String            property;
    private String            labelProperty;

    public String getCollection() {

        return collection;
    }

    public void setCollection( String collection ) {

        this.collection = collection;
    }

    public String getLabelProperty() {

        return labelProperty;
    }

    public void setLabelProperty( String labelProperty ) {

        this.labelProperty = labelProperty;
    }

    public String getProperty() {

        return property;
    }

    public void setProperty( String property ) {

        this.property = property;
    }

    public String getValue() {

        return value;
    }

    public void setValue( String value ) {

        // LOG.trace( "value = " + value );
        this.value = value;
    }

    @Override
    public int doStartTag() throws JspException {

        JspWriter out = pageContext.getOut();
        Collection collectionBean = (Collection) pageContext.getAttribute( collection );
        if (collectionBean == null) {
            collectionBean = (Collection) pageContext.getRequest().getAttribute( collection );
        }
        for ( Object bean : collectionBean ) {
            try {
                Object valObj = BeanUtils.getProperty( bean, property );
                out.print( "<option value=\"" );
                out.print( valObj );
                out.print( "\"" );
                if ( value.equals( valObj.toString() ) ) {
                    out.print( " selected" );
                }
                out.print( ">" );
                if ( labelProperty != null ) {
                    out.print( BeanUtils.getProperty( bean, labelProperty ) );
                } else {
                    out.print( bean );
                }
                out.println( "</option>" );
            } catch ( Exception e ) {
                throw new JspException( e );
            }
        }

        return EVAL_PAGE;
    }
}
