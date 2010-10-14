package org.nexuse2e.messaging.generic;

import org.nexuse2e.NexusException;
import org.nexuse2e.messaging.AbstractPipelet;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.pojo.MessagePayloadPojo;
import org.nexuse2e.util.ServerPropertiesUtil;

/**
 * This pipelet replaces the server property variables in the payload section(s)'s content IDs with the
 * variable values.
 *
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public class PayloadContentIdServerPropertiesReplacePipelet extends AbstractPipelet {
    
    public PayloadContentIdServerPropertiesReplacePipelet() {
        frontendPipelet = true;
    }

    @Override
    public MessageContext processMessage( MessageContext messageContext )
            throws IllegalArgumentException, IllegalStateException, NexusException {

        for (MessagePayloadPojo payload : messageContext.getMessagePojo().getMessagePayloads()) {
            String s = payload.getContentId();
            if (s != null) {
                s = ServerPropertiesUtil.replacePayloadDependentValues( s, payload.getSequenceNumber(), messageContext, true );
            }
            payload.setContentId(s);
        }
        
        return messageContext;
    }

}
