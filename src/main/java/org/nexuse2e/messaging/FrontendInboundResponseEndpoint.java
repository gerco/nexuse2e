package org.nexuse2e.messaging;

import org.nexuse2e.Manageable;
import org.nexuse2e.NexusException;
import org.nexuse2e.Constants.BeanStatus;
import org.nexuse2e.Constants.Layer;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.controller.StateTransitionException;

/**
 * This <code>MessageProcessor</code> implementation acts as an endpoint for frontend
 * inbound pipelines, on the return pipeline side. It basically manages the returned
 * response message status.
 *
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public class FrontendInboundResponseEndpoint implements MessageProcessor, Manageable {

    private BeanStatus status = BeanStatus.INSTANTIATED;

    
    public MessageContext processMessage( MessageContext messageContext )
            throws IllegalArgumentException, IllegalStateException,
            NexusException {

        if (messageContext != null && messageContext.isResponseMessage()) {
            try {
                messageContext.getStateMachine().sentMessage(); // response sent
            } catch (StateTransitionException e) {
                throw new NexusException( e );
            }
        }
        
        return messageContext;
    }

    public void activate() {
        status = BeanStatus.ACTIVATED;
    }

    public void deactivate() {
        status = BeanStatus.INITIALIZED;
    }

    public Layer getActivationLayer() {
        return Layer.INBOUND_PIPELINES;
    }

    public BeanStatus getStatus() {
        return status;
    }

    public void initialize( EngineConfiguration config )
            throws InstantiationException {
        status = BeanStatus.INITIALIZED;
    }

    public void teardown() {
        status = BeanStatus.INSTANTIATED;
    }
}
