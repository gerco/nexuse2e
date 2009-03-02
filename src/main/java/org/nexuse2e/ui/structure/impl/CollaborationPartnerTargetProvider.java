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

import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.Constants;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.pojo.PartnerPojo;
import org.nexuse2e.ui.structure.ParentalStructureNode;
import org.nexuse2e.ui.structure.StructureNode;
import org.nexuse2e.ui.structure.TargetProvider;

/**
 *  A <code>TargetProvider</code> that lists all configured collaboration partners
 *  as structure nodes.
 * 
 * @author jonas.reese
 */
public class CollaborationPartnerTargetProvider implements TargetProvider {

    public List<StructureNode> getStructure(
            StructureNode pattern, ParentalStructureNode parent, EngineConfiguration engineConfiguration ) {

        List<StructureNode> list = new ArrayList<StructureNode>();
        try {
            List<PartnerPojo> partnerPojos = engineConfiguration.getPartners(
                    Constants.PARTNER_TYPE_PARTNER, Constants.PARTNERCOMPARATOR );
            if ( partnerPojos != null ) {
                for ( PartnerPojo partnerPojo : partnerPojos ) {
                    StructureNode sn = new PageNode( pattern.getTarget() + "?nxPartnerId="
                            + partnerPojo.getNxPartnerId(), partnerPojo.getPartnerId() + " (" + partnerPojo.getName()
                            + ")", pattern.getIcon() );
                    list.add( sn );
                }
            }
        } catch ( NexusException e ) {
            e.printStackTrace();
        }
        return list;
    }

}
