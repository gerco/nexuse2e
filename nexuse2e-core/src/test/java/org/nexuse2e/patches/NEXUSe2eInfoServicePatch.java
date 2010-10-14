/**
 * NEXUSe2e Business Messaging Open Source  
 * Copyright 2007, Tamgroup and X-ioma GmbH   
 *  
 * This is free software; you can redistribute it and/or modify it  
 * under the terms of the GNU Lesser General Public License as  
 * published by the Free Software Foundation version 2.1 of  
 * the License.  
 *  
 * This software is distributed in the hope that it will be useful,  
 * but WITHOUT ANY WARRANTY; without even the implied warranty of  
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU  
 * Lesser General Public License for more details.  
 *  
 * You should have received a copy of the GNU Lesser General Public  
 * License along with this software; if not, write to the Free  
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.nexuse2e.patches;

import org.nexuse2e.patch.Patch;
import org.nexuse2e.patch.PatchException;
import org.nexuse2e.patch.PatchReporter;

/**
 * Utility patch for WS mount point creation.
 * 
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public class NEXUSe2eInfoServicePatch implements Patch {
    private NEXUSe2eInfoServicePatchDelegate delegate;
    
    public NEXUSe2eInfoServicePatch() {
    }
    
    private NEXUSe2eInfoServicePatchDelegate getDelegate() {
        if (delegate == null) {
            delegate = new NEXUSe2eInfoServicePatchDelegate();
            ((NEXUSe2eInfoServicePatchDelegate) delegate).setAutostart( false );
        }
        return (NEXUSe2eInfoServicePatchDelegate) delegate;
    }



    public void executePatch() throws PatchException {
        getDelegate().executePatch();
    }

    public String getPatchDescription() {
        return getDelegate().getPatchDescription();
    }

    public String getPatchName() {
        return getDelegate().getPatchName();
    }

    public String getVersionInformation() {
        return getDelegate().getVersionInformation();
    }

    public boolean isExecutedSuccessfully() {
        return getDelegate().isExecutedSuccessfully();
    }

    public void setPatchReporter(PatchReporter patchReporter) {
        getDelegate().setPatchReporter(patchReporter);
    }
    
}
