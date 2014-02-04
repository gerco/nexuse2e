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
package org.nexuse2e;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gesch
 * default implementation for a single node instance of NEXUSe2e
 */
public class DefaultAdvancedController implements AdvancedControllerInterface {

	/* (non-Javadoc)
	 * @see org.nexuse2e.AdvancedControllerInterface#getInstances()
	 */
	@Override
	public List<InstanceInterface> getInstances() {
		List<InstanceInterface> interfaces = new ArrayList<>();
		InstanceInterface defaultInstance = new DefaultInstance(); // contains the engine access for monitoring status and the predefined commands.
		interfaces.add(defaultInstance);
		return interfaces;
		
	}

	/* (non-Javadoc)
	 * @see org.nexuse2e.AdvancedControllerInterface#getDescription()
	 */
	@Override
	public String getDescription() {
		return "local instance, no cluster administration configured/installed (visit www.nexuse2e.org for further details)";
	}

	@Override
	public void executeCommand(String instanceId, String commandId) {
		InstanceInterface defaultInstance = new DefaultInstance();
		defaultInstance.executeCommand(commandId);
	}

}
