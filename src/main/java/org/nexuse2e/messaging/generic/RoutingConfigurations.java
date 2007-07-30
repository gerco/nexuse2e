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

package org.nexuse2e.messaging.generic;

import java.util.ArrayList;
import java.util.List;

/**
 * Container for partner specific configurations. The entries mainly map a partner ID to a configuration file.
 * Sample format:
 * 
 * <RoutingConfigurations>
 *   <RoutingConfiguration configurationFile="/Volumes/mapping/partner1.xml" partnerId="partner1"/>
 *   <RoutingConfiguration configurationFile="/Volumes/mapping/partner2.xml" partnerId="partner2"/>
 * </RoutingConfigurations>
 * 
 * 
 * 
 * @author mbreilmann
 *
 */
public class RoutingConfigurations {

    private List<RoutingConfiguration> RoutingConfigurations = new ArrayList<RoutingConfiguration>();

    public void addRoutingConfiguration( RoutingConfiguration RoutingConfiguration ) {

        RoutingConfigurations.add( RoutingConfiguration );
    }

    public List<RoutingConfiguration> getRoutingConfigurations() {

        return RoutingConfigurations;
    }

    public void setRoutingConfigurations( List<RoutingConfiguration> RoutingConfigurations ) {

        this.RoutingConfigurations = RoutingConfigurations;
    }

}
