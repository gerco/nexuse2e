package org.nexuse2e.ui.action.configuration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.nexuse2e.Engine;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.pojo.UserPojo;
import org.nexuse2e.ui.action.NexusE2EAction;

/**
 * Applies the pending configuration for the current user. Does nothing if the configuration
 * did not change.
 *
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public class ApplyAction extends NexusE2EAction {

    @Override
    public ActionForward executeNexusE2EAction(ActionMapping actionMapping,
            ActionForm actionForm, HttpServletRequest request,
            HttpServletResponse response,
            EngineConfiguration engineConfiguration, ActionMessages errors,
            ActionMessages messages) throws Exception {
        
        if (engineConfiguration.isChanged()) {
            
            UserPojo user = (UserPojo) request.getSession().getAttribute( ATTRIBUTE_USER );
            
            if (user != null) {
                Engine.getInstance().invalidateConfiguration( user.getNxUserId() );
                Engine.getInstance().setCurrentConfiguration( engineConfiguration );
                request.setAttribute(
                        ATTRIBUTE_CONFIGURATION, Engine.getInstance().getConfiguration( user.getNxUserId() ) );
            }
        }
        
        return actionMapping.findForward( ACTION_FORWARD_SUCCESS );
    }

}
