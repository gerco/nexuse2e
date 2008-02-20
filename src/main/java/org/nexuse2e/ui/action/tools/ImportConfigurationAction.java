package org.nexuse2e.ui.action.tools;

import java.io.ByteArrayInputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.nexuse2e.Engine;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.ui.action.NexusE2EAction;
import org.nexuse2e.ui.form.ConfigurationManagementForm;

/**
 * Created: 07.01.2008
 * TODO Class documentation
 *
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public class ImportConfigurationAction extends NexusE2EAction {

    @Override
    public ActionForward executeNexusE2EAction(
            ActionMapping actionMapping,
            ActionForm actionForm, HttpServletRequest request,
            HttpServletResponse response,
            EngineConfiguration engineConfiguration,
            ActionMessages errors,
            ActionMessages messages) throws Exception {

        ConfigurationManagementForm form = (ConfigurationManagementForm) actionForm;
        
        if (form.getPayloadFile() != null && !StringUtils.isEmpty( form.getPayloadFile().getFileName() )) {
            Engine.getInstance().importConfiguration(
                    new ByteArrayInputStream( form.getPayloadFile().getFileData() ) );
            request.setAttribute( REFRESH_TREE, "true" );
            return actionMapping.findForward( ACTION_FORWARD_SUCCESS );
        } else {
            ActionMessage errorMessage = new ActionMessage( "configuration.import.nofile" );
            errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
            return actionMapping.findForward( ACTION_FORWARD_FAILURE );
        }
    }

}
