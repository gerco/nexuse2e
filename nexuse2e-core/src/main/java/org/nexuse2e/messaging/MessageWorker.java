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
 * Interface for message worker implementations. A message worker is a class that manages
 * the asynchonous processing inbound messages through the backend and outbound messages through the frontend.
 * <p>
 * This is done using the single method {@link #queue(MessageContext)}.
 * 
 * @author sschulze, jreese
 */
public interface MessageWorker {

    /**
     * Adds a message to the message scheduler.
     * @param messageContext The message context to be queued. Must not be <code>null</code>.
     */
    public abstract void queue(MessageContext messageContext);
}
