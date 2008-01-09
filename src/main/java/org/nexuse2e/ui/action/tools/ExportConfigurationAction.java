package org.nexuse2e.ui.action.tools;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.nexuse2e.ui.action.NexusE2EAction;

/**
 * Created: 07.01.2008
 * TODO Class documentation
 *
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public class ExportConfigurationAction extends NexusE2EAction {

    @Override
    public ActionForward executeNexusE2EAction(
            ActionMapping actionMapping,
            ActionForm actionForm, HttpServletRequest request,
            HttpServletResponse response, ActionMessages errors,
            ActionMessages messages) throws Exception {

        return actionMapping.findForward( ACTION_FORWARD_SUCCESS );
    }

}
