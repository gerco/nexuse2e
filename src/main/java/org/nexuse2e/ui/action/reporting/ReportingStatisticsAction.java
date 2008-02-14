package org.nexuse2e.ui.action.reporting;

import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.nexuse2e.ui.action.NexusE2EAction;

/**
 * Fills the context for the statistics report(s).
 *
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public class ReportingStatisticsAction extends NexusE2EAction {

    @Override
    public ActionForward executeNexusE2EAction( ActionMapping actionMapping,
            ActionForm actionForm, HttpServletRequest request,
            HttpServletResponse response, ActionMessages errors,
            ActionMessages messages ) throws Exception {

        Calendar cal = Calendar.getInstance();
        cal.add( Calendar.DATE, -1 );
        request.setAttribute( "last24Hours", cal.getTime() );

        return actionMapping.findForward( ACTION_FORWARD_SUCCESS );
    }

}
