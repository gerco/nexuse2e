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
package org.nexuse2e.ui.structure.impl;

import java.util.ArrayList;
import java.util.List;

import org.nexuse2e.configuration.Constants;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.pojo.PipelinePojo;
import org.nexuse2e.ui.structure.ParentalStructureNode;
import org.nexuse2e.ui.structure.StructureNode;
import org.nexuse2e.ui.structure.TargetProvider;

/**
 * A <code>TargetProvider</code> that lists all configured pipelines as structure nodes.
 * 
 * @author jonas.reese
 */
public class PipelineTargetProvider implements TargetProvider {

    private boolean frontend = false;

    public List<StructureNode> getStructure(
            StructureNode pattern, ParentalStructureNode parent, EngineConfiguration engineConfiguration ) {

        List<StructureNode> list = new ArrayList<StructureNode>();
        List<PipelinePojo> pipelinePojos = null;

        if ( frontend ) {
            pipelinePojos = engineConfiguration.getFrontendPipelinePojos(
                    Constants.PIPELINE_TYPE_ALL, Constants.PIPELINECOMPARATOR );
        } else {
            pipelinePojos = engineConfiguration.getBackendPipelinePojos(
                    Constants.PIPELINE_TYPE_ALL, Constants.PIPELINECOMPARATOR );
        }

        if ( pipelinePojos != null ) {
            for ( PipelinePojo pipelinePojo : pipelinePojos ) {
                StructureNode sn = new PageNode( pattern.getTarget() + "?nxPipelineId=" + pipelinePojo.getNxPipelineId(),
                        pipelinePojo.getName(), pattern.getIcon() );
                list.add( sn );
            }
        }
        return list;
    }

    public boolean isFrontend() {

        return frontend;
    }

    public void setFrontend( boolean frontend ) {

        this.frontend = frontend;
    }

} // PipelineTargetProvider
