package org.nexuse2e.ui.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.Tag;

/**
 * Pass parameters to a BIRT report.
 *
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public class ReportParam extends BodyTagSupport {

    private static final long serialVersionUID = 1L;
    
    private String name;
    private Object value;
    

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public int doStartTag() throws JspException {
        Tag parent = getParent();
        if (parent instanceof Report) {
            ((Report) parent).setParameter( name, value );
        }
        return SKIP_BODY;
    }
}
