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

/**
 * @author gesch
 * command implementation which should be used for sending implementation specific instructions to an instance object.
 */
public class InstanceCommand {

	
	private String label;
	private String name;
	/**
	 * used to create the commands containing a label an the matching name for the internal use in the instance specific implementation.
	 * The ui will create an object and provides this to the instance.
	 * @param label
	 * @param name
	 */
	public InstanceCommand(String label, String name) {
		super();
		this.label = label;
		this.name = name;
	}

	
	
	/**
	 * the label shown in the ui
	 * @return
	 */
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
	/**
	 * a name used for submitting the command to the instance.
	 * @return
	 */
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
}
