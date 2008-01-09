package org.nexuse2e.ui.form;

import org.apache.struts.action.ActionForm;
import org.apache.struts.upload.FormFile;

/**
 * Created: 07.01.2008
 * TODO Class documentation
 *
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public class ConfigurationManagementForm extends ActionForm {

    /**
     * 
     */
    private static final long serialVersionUID = 9066165869803142603L;

    private FormFile          payloadFile     = null;

    
    /**
     * @return Returns the payloadFile.
     */
    public FormFile getPayloadFile() {

        return payloadFile;
    }

    /**
     * @param payloadFile1 The payloadFile to set.
     */
    public void setPayloadFile( FormFile payloadFile ) {

        this.payloadFile = payloadFile;
    }
}
