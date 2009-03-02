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

import org.nexuse2e.NexusException;

public interface MessageProcessor {

    /**
     * Process the <code>MessageContext</code> by performing a well defined task.
     * The parameter includes a <code>MessagePojo</code>, potentially the raw message data
     * and additional information to characterize the nature of the message being processed.
     * @param messageContext The <code>MessageContext</code> progressing through the <code>Pipeline</code>.
     * @return The potentially modified or transformed message data encapsulated in the 
     * <code>MessageContext</code>.
     * @throws IllegalArgumentException Thrown if information provided in the <code>MessageContext</code> 
     * did not meet expectations.
     * @throws IllegalStateException Thrown if the system is not in a correct state to handle this specific message.
     * @throws NexusException Thrown if any other processing related exception occured.
     */
    public abstract MessageContext processMessage( MessageContext messageContext )
            throws IllegalArgumentException, IllegalStateException, NexusException;

}