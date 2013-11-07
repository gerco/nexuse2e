package org.nexuse2e.messaging.generic;

import org.apache.commons.lang.StringUtils;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.ParameterType;
import org.nexuse2e.dao.PersistentPropertyDAO;
import org.nexuse2e.dao.PersistentPropertyUpdateCallback;
import org.nexuse2e.messaging.AbstractPipelet;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.pojo.MessagePayloadPojo;
import org.nexuse2e.pojo.PersistentPropertyPojo;

/**
 * This pipelet creates a unique sequence number by incrementing the last sequence number found
 * (in persistent properties) by one for each message payload object.
 * If for example the current sequence number is 28 and the message has three payloads, sequence
 * numbers 29, 30 and 31 will be set on payload objects. 
 *
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public class IncrementingSequenceNumberGeneratorPipelet extends AbstractPipelet {

    public static final String DEFAULT_SEQUENCE_NAME = "seq";
    public static final String SEQUENCE_NAME_PARAM_NAME = "sequence.name";
    public static final String PARTNER_SPECIFIC_PARAM_NAME = "specific.partner";
    public static final String CHOREOGRAPHY_SPECIFIC_PARAM_NAME = "specific.choreography";
    public static final String ACTION_SPECIFIC_PARAM_NAME = "specific.action";

    public static final String SEQUENCE_NAMESPACE = "payload.sequence.number.incrementing";
    public static final String SEQUENCE_VERSION = "1.0";
    
    
    public IncrementingSequenceNumberGeneratorPipelet() {
        frontendPipelet = true;
        
        parameterMap.put( SEQUENCE_NAME_PARAM_NAME, new ParameterDescriptor( ParameterType.STRING, "Sequence name",
                "Provide a sequence name here (if you want to use different sequences) or leave blank for default sequence", DEFAULT_SEQUENCE_NAME ) );
        parameterMap.put( PARTNER_SPECIFIC_PARAM_NAME, new ParameterDescriptor( ParameterType.BOOLEAN, "Partner-specific sequence number range",
                "Activate this in order to have a different sequence number count for each partner", true ) );
        parameterMap.put( CHOREOGRAPHY_SPECIFIC_PARAM_NAME, new ParameterDescriptor( ParameterType.BOOLEAN, "Choreography-specific sequence number range",
                "Activate this in order to have a different sequence number count for each choreography", true ) );
        parameterMap.put( ACTION_SPECIFIC_PARAM_NAME, new ParameterDescriptor( ParameterType.BOOLEAN, "Action-specific sequence number range",
                "Activate this in order to have a different sequence number count for each action", true ) );

    }

    /**
     * Process the message. See class comment for details.
     * @param messageContext The message context.
     * @return <code>messageContext</code>.
     */
    @Override
    public MessageContext processMessage(MessageContext messageContext)
            throws IllegalArgumentException, IllegalStateException,
            NexusException {

        if (messageContext != null && messageContext.getMessagePojo() != null
                && messageContext.getMessagePojo().getMessagePayloads() != null) {
            for (MessagePayloadPojo payload : messageContext.getMessagePojo().getMessagePayloads()) {
                payload.setSequenceNumber(getNextSequenceNumber(messageContext));
            }
        }
        
        return messageContext;
    }
    
    /**
     * Get next sequence number from persistent properties and increment persisten property (transactional) 
     * @param messageContext The message context.
     * @return The next sequence number.
     */
    protected int getNextSequenceNumber(MessageContext messageContext) {
        
        Boolean partnerSpecific = getParameter(PARTNER_SPECIFIC_PARAM_NAME);
        Boolean choreographySpecific = getParameter(CHOREOGRAPHY_SPECIFIC_PARAM_NAME);
        Boolean actionSpecific = getParameter(ACTION_SPECIFIC_PARAM_NAME);
        String sequenceName = getParameter(SEQUENCE_NAME_PARAM_NAME);
        if (StringUtils.isBlank(sequenceName)) {
            sequenceName = DEFAULT_SEQUENCE_NAME;
        }
        
        String key = sequenceName
            + (partnerSpecific != null && partnerSpecific.booleanValue() && messageContext.getPartner() != null ? "_" + messageContext.getPartner().getName() : "")
            + (choreographySpecific != null && choreographySpecific.booleanValue() && messageContext.getChoreography() != null ? "_" + messageContext.getChoreography().getName() : "")
            + (actionSpecific != null && actionSpecific.booleanValue() && messageContext.getConversation() != null && messageContext.getConversation().getCurrentAction() != null ?
                    "_" + messageContext.getConversation().getCurrentAction().getName() : "");
        
        PersistentPropertyDAO dao = (PersistentPropertyDAO)Engine.getInstance().getBeanFactory().getBean( "persistentPropertyDao" );

        final int[] seqRef = new int[1];
        
        dao.updatePersistentPropertyInTransaction(SEQUENCE_NAMESPACE, SEQUENCE_VERSION, key,
                new PersistentPropertyUpdateCallback() {
                    public boolean update(PersistentPropertyPojo property) {
                        int seq = 0;
                        if (property.getValue() != null) {
                            try {
                                seq = Integer.parseInt(property.getValue());
                            } catch (NumberFormatException nfex) {
                            }
                        }
                        
                        property.setValue(Integer.toString(++seq));
                        
                        seqRef[0] = seq;
                        
                        return true;
                    }
                });
        
        return seqRef[0];
    }

}
