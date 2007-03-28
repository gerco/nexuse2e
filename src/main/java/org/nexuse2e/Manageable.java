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
package org.nexuse2e;

import org.nexuse2e.Constants.BeanStatus;
import org.nexuse2e.Constants.Runlevel;
import org.nexuse2e.configuration.EngineConfiguration;

/**
 * Defines a life cycle for extensions/components that exist
 * inside the Nexus Server Engine. It is the implementor's responsibility
 * to maintain the correct state within that life cycle.
 * 
 * @author gesch, jonas.reese
 */
public interface Manageable {

    /**
     * Deactivates this <code>Manageable</code>.
     * It is the implementation's responsibility to reject subsequent
     * calls to service methods after <code>deactivate()</code> has
     * been called.
     */
    public void deactivate();

    /**
     * Activates this <code>Manageable</code>.
     * After this call, the <code>Manageable</code> shall be available
     * for calls to service methods. The point in time when this method
     * is called for the first time depends on the <code>RUN_LEVEL</code>
     * returned by <code>getActivationTime()</code>.
     */
    public void activate();

    /**
     * Initializes this <code>Manageable</code>.
     */
    public void initialize( EngineConfiguration config );

    /**
     * Cleans up this <code>Manageable</code> component freeing any resources.
     */
    public void teardown();

    /**
     * Gets the <code>Manageable</code>'s status. These are
     * @return bean status
     */
    public BeanStatus getStatus();

    /**
     * Return the run level of this component. The component will be activated dependent
     * on the <code>Runlevel</code> returned here.
     * @return The run level of this component.
     * @see Runlevel
     */
    public Runlevel getActivationRunlevel();
}
