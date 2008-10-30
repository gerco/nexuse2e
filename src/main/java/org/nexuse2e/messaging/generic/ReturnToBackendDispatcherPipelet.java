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
