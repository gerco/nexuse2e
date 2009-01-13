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

import java.util.ArrayList;
import java.util.List;

import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.patch.Patch;
import org.nexuse2e.patch.PatchException;
import org.nexuse2e.patch.PatchReporter;
import org.nexuse2e.pojo.PipeletPojo;
import org.nexuse2e.pojo.PipelinePojo;

/**
 * Patches the pipelets' <code>forward</code> flag. This is required for updating
 * from 4.x to 4.2.x versions of NEXUS.
 *
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public class ForwardPipelinesPatch implements Patch {

    private PatchReporter patchReporter;
    
    public void executePatch() throws PatchException {
        patchReporter.info( "Starting patch..." );
        try {
            List<PipelinePojo> pipelines = new ArrayList<PipelinePojo>(
                    Engine.getInstance().getCurrentConfiguration().getFrontendPipelinePojos( 0, null ) );
            for (PipelinePojo pipeline : pipelines) {
                patchPipeline( pipeline );
            }
            pipelines = new ArrayList<PipelinePojo>(
                    Engine.getInstance().getCurrentConfiguration().getBackendPipelinePojos( 0, null ) );
            for (PipelinePojo pipeline : pipelines) {
                patchPipeline( pipeline );
            }
            patchReporter.info( "Restarting engine..." );
            Engine.getInstance().setCurrentConfiguration( Engine.getInstance().getCurrentConfiguration() );
            patchReporter.info( "Done" );
        } catch (Exception ex) {
            patchReporter.fatal( "Patch failed: " + ex );
            throw new PatchException( ex );
        }
        patchReporter.info( "Patch successful." );
    }
    
    private void patchPipeline( PipelinePojo pipeline ) throws NexusException {
        patchReporter.info( "Patching pipeline " + pipeline.getName() + "..." );
        for (PipeletPojo pipelet : pipeline.getPipelets()) {
            pipelet.setForward( true );
        }
        Engine.getInstance().getCurrentConfiguration().updatePipeline( pipeline );
    }

    public String getPatchDescription() {
        return "Fixes the 'forward' flag for all pipelets";
    }

    public String getPatchName() {
        return "Patch for forward pipelets";
    }

    public String getVersionInformation() {
        return "Update from 4.x version to 4.2.x. Only run this directly after you have updated from a 4.1.x or earlier version!";
    }

    public boolean isExecutedSuccessfully() {
        // TODO Auto-generated method stub
        return false;
    }

    public void setPatchReporter( PatchReporter patchReporter ) {
        this.patchReporter = patchReporter;
    }

}
