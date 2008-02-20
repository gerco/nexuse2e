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
import java.util.TreeSet;

import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.configuration.GenericComparator;
import org.nexuse2e.pojo.ServicePojo;
import org.nexuse2e.ui.structure.ParentalStructureNode;
import org.nexuse2e.ui.structure.StructureNode;
import org.nexuse2e.ui.structure.TargetProvider;

/**
 * A <code>TargetProvider</code> that lists all configured components as structure nodes.
 * 
 * @author jonas.reese
 */
public class ServiceTargetProvider implements TargetProvider {

    /* (non-Javadoc)
     * @see org.nexuse2e.ui.structure.TargetProvider#getStructure(org.nexuse2e.ui.structure.StructureNode)
     */
    @SuppressWarnings("unchecked")
    public List<StructureNode> getStructure(
            StructureNode pattern, ParentalStructureNode parent, EngineConfiguration engineConfiguration ) {

        List<StructureNode> list = new ArrayList<StructureNode>();
        List<ServicePojo> servicePojos = engineConfiguration.getServices();

        TreeSet<ServicePojo> sortedServices = new TreeSet<ServicePojo>( new GenericComparator( ServicePojo.class,
                "name", true ) );
        sortedServices.addAll( servicePojos );

        for ( ServicePojo servicePojo : sortedServices ) {
            StructureNode sn = new PageNode( pattern.getTarget() + "?nxServiceId=" + servicePojo.getNxServiceId(),
                    servicePojo.getName(), pattern.getIcon() );
            list.add( sn );
        }
        return list;
    }

}
