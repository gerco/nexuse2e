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
package org.nexuse2e.patch;


/**
 * This interface defines abstract methods that shall be implemented by a
 * (dynamic) patch. A patch can be uploaded to NEXUS and then executed.
 * This mechanism is used for code-level patches that can only be executed with
 * the application runtime environment.
 *
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public interface Patch {

    /**
     * Executes the patch.
     * @throws PatchException if the patch failed to execute. The exception's cause
     * contains the original exception that occurred.
     */
    public void executePatch() throws PatchException;
    
    /**
     * Gets (human-readable) information on which NEXUS version(s) this patch
     * can be executed.
     * @return The human-readable version information
     */
    public String getVersionInformation();

    /**
     * Gets the human-readable patch name.
     * @return A non-<code>null</code> name for this patch.
     */
    public String getPatchName();
    
    /**
     * Gets the human-readable patch description.
     * @return A non-<code>null</code> description for this patch.
     */
    public String getPatchDescription();
    
    /**
     * Sets the <code>PatchReporter</code> for this patch's execution.
     * @param patchReporter The patch reporter that shall be used. This method is called
     * before patch execution.
     */
    public void setPatchReporter( PatchReporter patchReporter );
    
    /**
     * Returns <code>true</code> if this patch was executed successfully.
     * @return <code>true</code> if {@link #executePatch()} has been called successfully,
     * <code>false</code> otherwise.
     */
    public boolean isExecutedSuccessfully();
}
