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

import java.util.Map;

import org.nexuse2e.configuration.ParameterDescriptor;

/**
 * A <code>Configurable</code> is an entity within the Nexus Server
 * Engine that can be configured in a generic way with key/value pairs.
 * 
 * @author jonas.reese
 */
public interface Configurable {

    /**
     * Gets the parameter with the given name and type.
     * @param <T> The parameter type. See
     * {@link org.nexuse2e.configuration.ParameterType#getType()}}.
     * @param name The parameter name.
     * @return The property.
     * @throws ClassCastException if type and parameter name do not match.
     */
    public <T> T getParameter( String name );

    /**
     * Sets the parameter with the given name to the given value.
     * @param name The parameter name.
     * @param value The parameter value.
     * @throws ClassCastException if the parameter name and value type do not
     * match.
     */
    public void setParameter( String name, Object value );

    /**
     * Gets a <code>Map</code> of all parameter names to their descriptors.
     * @return A <code>Map</code> with <b>all</b> parameter names that
     * this <code>Configurable</code> can be configured with as keys,
     * associated with their parameter descriptors as values. Implementors
     * shall verify that the returned <code>Map</code> is ordered the way
     * parameters shall occur (e.g., in a UI).
     */
    public Map<String, ParameterDescriptor> getParameterMap();

    /**
     * Gets all parameters that are currently set. The returned <code>Map</code>
     * shall not be modified and thus may be unmodifiable.
     * @return A <code>Map</code> that associates all parameter names with their
     * current values. <code>null</code> parameters may either be mapped to a
     * <code>null</code> value or not contained in the result <code>Map</code>
     * at all. The key order of the returned <code>Map</code> is not relevant.
     */
    public Map<String, Object> getParameters();
}
