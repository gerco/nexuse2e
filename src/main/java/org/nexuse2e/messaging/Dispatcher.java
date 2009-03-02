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
package org.nexuse2e.messaging;

/**
 * Interface for a component that handles messaging protocol specific tasks and therefore requires
 * a set of <code>ProtocolAdapter</code> components.
 * @see org.nexuse2e.messaging.ProtocolAdapter
 *
 * @author mbreilmann
 */
public interface Dispatcher {

    /**
     * Set the array of <code>ProtocolAdapter</code> components
     * @param protocolAdapter The array of <code>ProtocolAdapter</code> components to set.
     */
    public void setProtocolAdapters( ProtocolAdapter[] protocolAdapter );

    /**
     * Return the currently configured array of <code>ProtocolAdapter</code> components.
     * @return The array of <code>ProtocolAdapter</code> components.
     */
    public ProtocolAdapter[] getProtocolAdapters();

} // Dispatcher
