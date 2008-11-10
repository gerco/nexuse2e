package org.nexuse2e.ui.action.tools;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.patch.PatchBundle;
import org.nexuse2e.patch.PatchBundles;
import org.nexuse2e.ui.action.NexusE2EAction;
import org.nexuse2e.ui.form.PatchManagementForm;

/**
 * This action handles patch upload and execution.
 *
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public class PatchAction extends NexusE2EAction {

    @Override
    public ActionForward executeNexusE2EAction(
            ActionMapping actionMapping,
            ActionForm actionForm, HttpServletRequest request,
            HttpServletResponse response,
            EngineConfiguration engineConfiguration,
            ActionMessages errors,
            ActionMessages messages) throws Exception {

        PatchManagementForm form = (PatchManagementForm) actionForm;
        
        if (form.getPatchFile() != null && !StringUtils.isEmpty( form.getPatchFile().getFileName() )) {
            // patch uploaded
            PatchBundles bundles = form.getPatchBundles();
            
            try {
                PatchBundle patchBundle = new PatchBundle(
                        form.getPatchFile().getInputStream(),
                        getClass().getClassLoader() );
                bundles.addPatchBundle( patchBundle );
            } catch (IOException ioex) {
                ioex.printStackTrace();
                ActionMessage errorMessage = new ActionMessage( "patch.upload.error.onearg", ioex.getMessage() );
                errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
                return actionMapping.findForward( ACTION_FORWARD_FAILURE );
            }

            return actionMapping.findForward( ACTION_FORWARD_SUCCESS );
        } else {
            return actionMapping.findForward( ACTION_FORWARD_SUCCESS );
        }
    }

}
