/**
 * *#* ©2006 The Tamalpais Group, Inc., Xioma *+*
 */
package org.nexuse2e.ui.structure.impl;

import java.util.ArrayList;
import java.util.List;

import org.nexuse2e.Engine;
import org.nexuse2e.configuration.Constants;
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
    @SuppressWarnings("unchecked")
    public List<StructureNode> getStructure( StructureNode pattern, ParentalStructureNode parent ) {

        List<StructureNode> list = new ArrayList<StructureNode>();

        List<RolePojo> roles = Engine.getInstance().getActiveConfigurationAccessService().getRoles(
                Constants.COMPARATOR_ROLE_BY_NAME );

        for ( RolePojo role : roles ) {
            list.add( new PageNode( pattern.getTarget() + "?nxRoleId=" + role.getNxRoleId(), role.getName(), pattern.getIcon() ) );
        }

        return list;
    }

}
