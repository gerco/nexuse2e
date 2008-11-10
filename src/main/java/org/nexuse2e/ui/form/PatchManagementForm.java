package org.nexuse2e.ui.form;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;
import org.nexuse2e.patch.PatchBundles;

/**
 * Created: 07.01.2008
 * TODO Class documentation
 *
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public class PatchManagementForm extends ActionForm {

    /**
     * 
     */
    private static final long serialVersionUID = 9066165869803142603L;

    private FormFile          payloadFile     = null;
    private PatchBundles      patchBundles    = null;

    
    /**
     * @return Returns the payloadFile.
     */
    public FormFile getPatchFile() {

        return payloadFile;
    }

    /**
     * @param payloadFile1 The payloadFile to set.
     */
    public void setPatchFile( FormFile payloadFile ) {

        this.payloadFile = payloadFile;
    }
    
    /**
     * Gets the currently loaded <code>PatchBundles</code>.
     * @return The loaded patch bundles, not <code>null</code>.
     */
    public PatchBundles getPatchBundles() {
        if (patchBundles == null) {
            patchBundles = new PatchBundles();
        }
        return patchBundles;
    }
    
    public void reset( ActionMapping mapping, HttpServletRequest request ) {
        payloadFile = null;
    }
}
