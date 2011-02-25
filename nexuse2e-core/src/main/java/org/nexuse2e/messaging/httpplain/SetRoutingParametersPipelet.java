package org.nexuse2e.messaging.httpplain;

import org.apache.commons.lang.StringUtils;
import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.Constants.ParameterType;
import org.nexuse2e.messaging.AbstractPipelet;
import org.nexuse2e.messaging.MessageContext;

/**
 * This pipelet can set (override) routing parameters (choreo, partner, action)
 * for HTTP plain transport. Put an HTTPPlainHeaderDeserializer pipelet after this in order to apply those parameters.
 *
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public class SetRoutingParametersPipelet extends AbstractPipelet {

    protected static final String CHOREOGRAPHY_PARAM_NAME = "choreographyId";
    protected static final String ACTION_PARAM_NAME = "actionId";
    protected static final String PARTNER_PARAM_NAME = "partnerId";
    
    /**
     * Default constructor
     */
    public SetRoutingParametersPipelet() {
        forwardPipelet = true;
        frontendPipelet = true;

        parameterMap.put(CHOREOGRAPHY_PARAM_NAME,
                new ParameterDescriptor(
                        ParameterType.STRING,
                        "Choreography",
                        "The choreography ID to set on the processed message. Leave blank if choreography shall not be set by this pipelet.",
                        ""));
        parameterMap.put(ACTION_PARAM_NAME,
                new ParameterDescriptor(
                        ParameterType.STRING,
                        "Action",
                        "The action ID to set on the processed message. Leave blank if action shall not be set by this pipelet.",
                        ""));
        parameterMap.put(PARTNER_PARAM_NAME,
                new ParameterDescriptor(
                        ParameterType.STRING,
                        "Partner",
                        "The partner ID to set on the processed message. Leave blank if partner shall not be set by this pipelet.",
                        ""));
    }

    @Override
    public MessageContext processMessage(MessageContext messageContext)
    throws IllegalArgumentException, IllegalStateException, NexusException {

        String choreographyId = getParameter(CHOREOGRAPHY_PARAM_NAME);
        String actionId = getParameter(ACTION_PARAM_NAME);
        String partnerId = getParameter(PARTNER_PARAM_NAME);

        if (StringUtils.isNotBlank(choreographyId)) {
            messageContext.getMessagePojo().getCustomParameters().put(
                    Constants.PARAMETER_PREFIX_HTTP_REQUEST_PARAM + Constants.PARAM_CHOREOGRAPY_ID, choreographyId );
        }
        if (StringUtils.isNotBlank(actionId)) {
            messageContext.getMessagePojo().getCustomParameters().put(
                    Constants.PARAMETER_PREFIX_HTTP_REQUEST_PARAM + Constants.PARAM_ACTION_ID, actionId );
        }
        if (StringUtils.isNotBlank(partnerId)) {
            messageContext.getMessagePojo().getCustomParameters().put(
                    Constants.PARAMETER_PREFIX_HTTP_REQUEST_PARAM + Constants.PARAM_PARTNER_ID, partnerId );
        }

        return messageContext;
    }

}
