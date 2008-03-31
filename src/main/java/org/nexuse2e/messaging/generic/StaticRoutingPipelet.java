package org.nexuse2e.messaging.generic;

import org.nexuse2e.Constants;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.IdGenerator;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.Constants.ParameterType;
import org.nexuse2e.messaging.AbstractPipelet;
import org.nexuse2e.messaging.MessageContext;

/**
 * Created: 19.07.2007
 * TODO Class documentation
 *
 * @author jonas.reese
 * @version $LastChangedRevision$ - $LastChangedDate$ by $LastChangedBy$
 */
public class StaticRoutingPipelet extends AbstractPipelet {

    public static final String CHOREOGRAPHY_PARAM_NAME = "choreographyId";
    public static final String ACTION_PARAM_NAME       = "actionId";

    public StaticRoutingPipelet() {

        forwardPipelet = true;
        frontendPipelet = true;

        parameterMap.put( CHOREOGRAPHY_PARAM_NAME, new ParameterDescriptor( ParameterType.STRING, "Choreography",
                "The choreography to set on the processed message", "" ) );
        parameterMap.put( ACTION_PARAM_NAME, new ParameterDescriptor( ParameterType.STRING, "Action",
                "The action to set on the processed message", "" ) );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.AbstractPipelet#processMessage(org.nexuse2e.messaging.MessageContext)
     */
    @Override
    public MessageContext processMessage( MessageContext messageContext ) throws IllegalArgumentException,
            IllegalStateException, NexusException {

        IdGenerator messageIdGenerator = Engine.getInstance().getIdGenerator( Constants.ID_GENERATOR_MESSAGE );
        IdGenerator conversationIdGenerator = Engine.getInstance().getIdGenerator( Constants.ID_GENERATOR_CONVERSATION );
        messageContext.getMessagePojo().setOutbound( false );
        messageContext.getMessagePojo().setType( org.nexuse2e.messaging.Constants.INT_MESSAGE_TYPE_NORMAL );
        Engine.getInstance().getTransactionService().initializeMessage( messageContext.getMessagePojo(),
                messageIdGenerator.getId(), conversationIdGenerator.getId(),
                (String) getParameter( ACTION_PARAM_NAME ), messageContext.getPartner().getPartnerId(),
                (String) getParameter( CHOREOGRAPHY_PARAM_NAME ) );

        return messageContext;
    }

}
