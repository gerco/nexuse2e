package org.nexuse2e.ui.action.reporting;

import java.sql.Timestamp;
import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.nexuse2e.Engine;
import org.nexuse2e.configuration.EngineConfiguration;
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
            HttpServletResponse response,
            EngineConfiguration engineConfiguration,
            ActionMessages errors,
            ActionMessages messages ) throws Exception {

        Calendar cal = Calendar.getInstance();
        cal.add( Calendar.DATE, -1 );
        Timestamp timestamp = new Timestamp( cal.getTimeInMillis() );
        
        // check if there are any messages that have been created after timestamp
        request.setAttribute( "messageCount", Engine.getInstance().getTransactionService().getCreatedMessagesSinceCount( timestamp ) );
        
        request.setAttribute( "last24Hours", timestamp );
        
        cal.set( Calendar.MINUTE, 0 );
        cal.set( Calendar.SECOND, 0 );
        cal.set( Calendar.MILLISECOND, 0 );
        cal.add( Calendar.HOUR, 1 );
        request.setAttribute( "last24HoursRounded", new Timestamp( cal.getTimeInMillis() ) );

        return actionMapping.findForward( ACTION_FORWARD_SUCCESS );
    }

}
