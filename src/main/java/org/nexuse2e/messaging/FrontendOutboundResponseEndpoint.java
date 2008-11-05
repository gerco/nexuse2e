package org.nexuse2e.messaging;

import org.nexuse2e.Engine;
import org.nexuse2e.Manageable;
import org.nexuse2e.NexusException;
import org.nexuse2e.Constants.BeanStatus;
import org.nexuse2e.Constants.Layer;
import org.nexuse2e.configuration.EngineConfiguration;

/**
 * This <code>MessageProcessor</code> implementation acts as an endpoint for frontend
 * outbound pipelines, on the return pipeline side. It basically forwards messages
 * to the <code>BackendInboundDispatcher</code>.
 *
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public class FrontendOutboundResponseEndpoint implements MessageProcessor, Manageable {

    private BeanStatus status = BeanStatus.INSTANTIATED;

    
    public MessageContext processMessage( MessageContext messageContext )
            throws IllegalArgumentException, IllegalStateException,
            NexusException {

        if (messageContext != null && messageContext.isResponseMessage()) {
            BackendInboundDispatcher dispatcher =
                Engine.getInstance().getCurrentConfiguration().getStaticBeanContainer().getBackendInboundDispatcher();
            return dispatcher.processMessage( messageContext );
        }
        return null;
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
