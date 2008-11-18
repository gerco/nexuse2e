/**
 * *#* ©2006 The Tamalpais Group, Inc., Xioma *+*
 */
package org.nexuse2e.ui.structure.impl;

import java.util.ArrayList;
import java.util.List;

import org.nexuse2e.configuration.Constants;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.pojo.UserPojo;
import org.nexuse2e.ui.structure.ParentalStructureNode;
import org.nexuse2e.ui.structure.StructureNode;
import org.nexuse2e.ui.structure.TargetProvider;

/**
 * {@link TargetProvider} that provides nodes for all known users.
 * @author Sebastian Schulze
 * @date 25.01.2007
 */
public class UserTargetProvider implements TargetProvider {

    public List<StructureNode> getStructure(
            StructureNode pattern, ParentalStructureNode parent, EngineConfiguration engineConfiguration ) {

        List<StructureNode> list = new ArrayList<StructureNode>();

        List<UserPojo> users = engineConfiguration.getUsers( Constants.COMPARATOR_USER_BY_NAME );

        for ( UserPojo user : users ) {
            if ( user.isVisible() ) {
                list.add( new PageNode( pattern.getTarget() + "?nxUserId=" + user.getNxUserId(), user.getLastName() + ", "
                        + user.getFirstName() + ( user.getMiddleName() != null ? " " + user.getMiddleName() : "" ),
                        pattern.getIcon() ) );
            }
        }

        return list;
    }

}
