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
package org.nexuse2e.integration;

import org.nexuse2e.Engine;
import org.nexuse2e.EngineMonitor;
import org.nexuse2e.EngineStatusSummary;

/**
 * @author mbreilmann
 *
 */
public class EngineStatusInterfaceImpl implements EngineStatusInterface {

    /* (non-Javadoc)
     * @see org.nexuse2e.integration.EngineStatusInterface#getEngineStatus()
     */
    public String getEngineStatus() {
        try {
            EngineMonitor engineMonitor = Engine.getInstance().getEngineController().getEngineMonitor();
            EngineStatusSummary engineStatusSummary = engineMonitor.getStatus();

            if ( engineStatusSummary != null ) {
                return EngineStatusSummary.getStatusString( engineStatusSummary.getStatus() );
            }
        } catch ( Exception e ) {
            return e.getMessage();
        }

        return "Unknown";
    }

}
