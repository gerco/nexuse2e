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
/**
 * *#* �2006 The Tamalpais Group, Inc., Xioma *+*
 */
package org.nexuse2e.ui.structure.impl;

import java.util.ArrayList;
import java.util.List;

import org.nexuse2e.configuration.Constants;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.pojo.RolePojo;
import org.nexuse2e.ui.structure.ParentalStructureNode;
import org.nexuse2e.ui.structure.StructureNode;
import org.nexuse2e.ui.structure.TargetProvider;

/**
 * {@link TargetProvider} that provides nodes for all known roles.
 * @author Sebastian Schulze
 * @date 25.01.2007
 */
public class RoleTargetProvider implements TargetProvider {

    /* (non-Javadoc)
     * @see org.nexuse2e.ui.structure.TargetProvider#getStructure(org.nexuse2e.ui.structure.StructureNode, org.nexuse2e.ui.structure.ParentalStructureNode)
     */
    public List<StructureNode> getStructure(
            StructureNode pattern, ParentalStructureNode parent, EngineConfiguration engineConfiguration ) {

        List<StructureNode> list = new ArrayList<StructureNode>();

        List<RolePojo> roles = engineConfiguration.getRoles( Constants.COMPARATOR_ROLE_BY_NAME );

        for ( RolePojo role : roles ) {
            list.add( new PageNode( pattern.getTarget() + "?nxRoleId=" + role.getNxRoleId(), role.getName(), pattern.getIcon() ) );
        }

        return list;
    }

}
