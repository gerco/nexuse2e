package org.nexuse2e.backend.pipelets;

import org.nexuse2e.NexusException;
import org.nexuse2e.messaging.AbstractPipelet;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.pojo.MessagePayloadPojo;
import org.nexuse2e.util.ServerPropertiesUtil;

/**
 * This pipelet replaces the server property variables in the payload section(s) with the
 * variable values.
 *
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public class ServerPropertiesReplacePipelet extends AbstractPipelet {

    @Override
    public MessageContext processMessage( MessageContext messageContext )
            throws IllegalArgumentException, IllegalStateException, NexusException {

        for (MessagePayloadPojo payload : messageContext.getMessagePojo().getMessagePayloads()) {
            String s = new String( payload.getPayloadData() );
            s = ServerPropertiesUtil.replaceServerProperties( s, messageContext );
            payload.setPayloadData( s.getBytes() );
        }
        
        return messageContext;
    }

}
