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

import java.util.Date;

import org.nexuse2e.NexusException;

/**
 * Interface for helper classes providing timestamps in a String representation required by a specific protocol.
 *
 * @author gesch
 */
public interface TimestampFormatter {

    /**
     * @param time
     * @return timestamp matching the specific protocol requirements
     */
    public String getTimestamp( Date time );

    /**
     * @param time
     * @return java Date Object
     * @throws NexusException
     */
    public Date getTimestamp( String time ) throws NexusException;
} // TimestampFormatter
