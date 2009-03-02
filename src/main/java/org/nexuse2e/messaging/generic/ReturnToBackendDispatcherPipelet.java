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
package org.nexuse2e.messaging.generic;

import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.messaging.AbstractPipelet;
import org.nexuse2e.messaging.BackendInboundDispatcher;
import org.nexuse2e.messaging.MessageContext;

/**
 * This pipelet implementation can be used for symmetric (bidirectional) frontend pipelines
 * on the "return" way. It acts as a return pipeline endpoint and dispatches the received
 * <code>MessageContext</code> to the NEXUSe2e backend.
 *
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public class ReturnToBackendDispatcherPipelet extends AbstractPipelet {
    
    /**
     * Default constructor.
     */
    public ReturnToBackendDispatcherPipelet() {
        frontendPipelet = true;
    }
    

    @Override
    public MessageContext processMessage( MessageContext messageContext )
            throws IllegalArgumentException, IllegalStateException,
            NexusException {

        if (messageContext != null) {
            BackendInboundDispatcher dispatcher =
                Engine.getInstance().getCurrentConfiguration().getStaticBeanContainer().getBackendInboundDispatcher();
            return dispatcher.processMessage( messageContext );
        }
        return null;
    }
    
}
