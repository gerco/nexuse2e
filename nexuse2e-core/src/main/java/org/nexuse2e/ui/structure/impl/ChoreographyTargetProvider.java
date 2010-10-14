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
package org.nexuse2e.ui.structure.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.configuration.GenericComparator;
import org.nexuse2e.pojo.ChoreographyPojo;
import org.nexuse2e.ui.structure.ParentalStructureNode;
import org.nexuse2e.ui.structure.StructureNode;
import org.nexuse2e.ui.structure.TargetProvider;

public class ChoreographyTargetProvider implements TargetProvider {

    /* (non-Javadoc)
     * @see org.nexuse2e.ui.structure.TargetProvider#getStructure(org.nexuse2e.ui.structure.StructureNode, org.nexuse2e.ui.structure.ParentalStructureNode)
     */
    public List<StructureNode> getStructure(
            StructureNode pattern, ParentalStructureNode parent, EngineConfiguration engineConfiguration ) {

        List<StructureNode> list = new ArrayList<StructureNode>();

        List<ChoreographyPojo> choreographies = engineConfiguration.getChoreographies();
        TreeSet<ChoreographyPojo> sortedChoreographies = new TreeSet<ChoreographyPojo>(
                new GenericComparator<ChoreographyPojo>( "name", true ) );
        sortedChoreographies.addAll( choreographies );
        for ( ChoreographyPojo choreographyPojo : sortedChoreographies ) {
            ParentalStructureNode sn = new PageNode( pattern.getTarget() + "?nxChoreographyId="
                    + choreographyPojo.getNxChoreographyId(), choreographyPojo.getName(), pattern.getIcon() );
            sn.setProperty( "nxChoreographyId", Integer.toString( choreographyPojo.getNxChoreographyId() ) );
            list.add( sn );
        }

        return list;
    }

}
