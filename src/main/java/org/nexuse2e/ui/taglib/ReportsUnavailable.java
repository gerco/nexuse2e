package org.nexuse2e.ui.taglib;

import javax.servlet.ServletContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.eclipse.birt.report.engine.api.IReportEngine;
import org.nexuse2e.reporting.BirtEngine;

/**
 * Evaluates the body only if BIRT reports are unavailable
 *
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public class ReportsUnavailable extends BodyTagSupport {

    private static final long serialVersionUID = 1L;
    
    @Override
    public int doStartTag() throws JspException {
        ServletContext sc = pageContext.getSession().getServletContext();
        IReportEngine birtReportEngine = (IReportEngine) getValue( "birtReportEngine" );
        if (birtReportEngine == null) {
            try {
                birtReportEngine = BirtEngine.getBirtEngine(sc);
                setValue( "birtReportEngine", birtReportEngine );
            } catch (UnsupportedOperationException uoex) {
                return EVAL_BODY_INCLUDE;
            }
        }
        
        return SKIP_BODY;
    }
}