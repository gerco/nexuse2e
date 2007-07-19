package org.nexuse2e.messaging.generic;

import java.util.ArrayList;
import java.util.List;

import org.nexuse2e.Constants;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.messaging.AbstractPipelet;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.pojo.MessagePayloadPojo;
import org.nexuse2e.pojo.MessagePojo;

/**
 * Created: 19.07.2007
 * TODO Class documentation
 *
 * @author jonas.reese
 * @version $LastChangedRevision$ - $LastChangedDate$ by $LastChangedBy$
 */
public class RawDataMessagePipelet extends AbstractPipelet {

    public RawDataMessagePipelet() {
        forwardPipelet = true;
        frontendPipelet = true;
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.AbstractPipelet#processMessage(org.nexuse2e.messaging.MessageContext)
     */
    @Override
    public MessageContext processMessage( MessageContext messageContext )
    throws IllegalArgumentException, IllegalStateException, NexusException {
        MessagePojo messagePojo = messageContext.getMessagePojo();
        MessagePayloadPojo messagePayloadPojo = new MessagePayloadPojo();
        messagePayloadPojo.setContentId(
                Engine.getInstance().getIdGenerator( Constants.ID_GENERATOR_MESSAGE_PAYLOAD ).getId() );
        messagePayloadPojo.setMimeType( "text/plain" );
        messagePayloadPojo.setPayloadData( (byte[]) messageContext.getData() );
        List<MessagePayloadPojo> messagePayloads = new ArrayList<MessagePayloadPojo>( 1 );
        messagePayloads.add( messagePayloadPojo );
        messagePojo.setMessagePayloads( messagePayloads );
        return messageContext;
    }

}
