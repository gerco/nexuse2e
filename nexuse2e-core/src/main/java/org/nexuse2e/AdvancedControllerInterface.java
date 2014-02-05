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

import java.util.List;

/**
 * @author GEsch
 * this interface provides the different options for additional ui rendering and command execution options in different NEXUSe2e intances. 
 */
public interface AdvancedControllerInterface {
	
	/**
	 * returns a list of all currently visible and accessable instances including "this" instance. For standalone instances 
	 * there is only one entry in this list.
	 * @return
	 * 
	 */
	public List<InstanceInterface> getInstances();
	
	/**
	 * a string which is currently only rendered in the main screen below the instances.
	 * @return
	 */
	public String getDescription();

	/**
	 * this method should be used to access the different instances. The instance id should match one of the ids provided with the
	 * getInstances list. The commandId depend on the different instance implementations and is provided by getCommands on InstanceInterface
	 * @param instanceId
	 * @param commandId
	 */
	public void executeCommand(String instanceId, String commandId);
	
}
