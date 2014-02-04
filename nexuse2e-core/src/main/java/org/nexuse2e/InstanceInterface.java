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
 * @author gesch
 * used to identify a single NEXUSe2e instance for rendering additional controls. The Instance object is used for sending 
 * commands to the instance. For localhost its simply a shortcut to the engine controls. 
 */
public interface InstanceInterface {

	/**
	 * must return a list of valid command objects which can be executed on the specific instance. 
	 * The command information are used in the web ui for rendering and the command name is used to execute a method.
	 * @return
	 */
	public List<InstanceCommand> getCommands();
	/**
	 * returns a label shown in the web ui, just for visual identification
	 * @return
	 */
	public String getLabel();
	/**
	 * the id is used for executing a specific command on a dedicated instance.
	 * @return
	 */
	public String getId();
	/**
	 * only a simple text, used for ui rendering.
	 * @return
	 */
	public String getStatus();
	/**
	 * the color code is used for rendering a traffic lights style status image.
	 * @return
	 */
	public Color getStatusColor();
	/**
	 * the method should encapsulate the execution of a command on this instance. e.g. for a local instance, 
	 * accessing the engine and swtiching the state or something like that. The commandId should match at least
	 * one of the commands returned by the getCommands list.
	 * @param commandId
	 */
	public void executeCommand(String commandId);
}
