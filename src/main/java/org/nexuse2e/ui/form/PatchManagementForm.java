/**
 *  NEXUSe2e Business Messaging Open Source
 *  Copyright 2000-2009, Tamgroup and X-ioma GmbH
 *
 *  This is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation version 2.1 of
 *  the License.
 *
 *  This software is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this software; if not, write to the Free
 *  Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *  02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
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
