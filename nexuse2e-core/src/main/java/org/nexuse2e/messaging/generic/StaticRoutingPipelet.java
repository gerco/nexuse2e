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

import org.apache.commons.lang.StringUtils;
import org.nexuse2e.Constants;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.IdGenerator;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.ParameterType;
import org.nexuse2e.messaging.AbstractPipelet;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.pojo.MessagePojo;

/**
 * This pipelet statically routes a message within a given choreography and action.
 * It generates a message id, and choreography id, and initializes the message.
 * If this pipelet's parameters are empty, the original message/context fields are used.
 * Otherwise the original fields will get overwritten.
 * If the passed message/context has already a message id, or conversation id assigned,
 * those ids will be kept, that is no newly generated ids will be assigned.
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

        if ( messageContext != null ) {
            MessagePojo message = messageContext.getMessagePojo();
            
            if ( message != null ) {
                IdGenerator messageIdGenerator = Engine.getInstance().getIdGenerator( Constants.ID_GENERATOR_MESSAGE );
                IdGenerator conversationIdGenerator = Engine.getInstance().getIdGenerator( Constants.ID_GENERATOR_CONVERSATION );
                message.setOutbound( false );
                message.setType( org.nexuse2e.messaging.Constants.INT_MESSAGE_TYPE_NORMAL );
                
                // allow preceding pipelets to initialize stuff, so only set new ids, if fields are yet empty,
                // and only override choreography and action, if this pipelet's according parameters are not empty
                String originalMessageId = message.getMessageId();
                String originalConversationId = ( message.getConversation() != null ? message.getConversation().getConversationId() : null );
                String originalChoreography = ( messageContext.getChoreography() != null ? messageContext.getChoreography().getName() : null );  
                String originalAction = ( message.getAction() != null ? message.getAction().getName() : null );
                
                String newAction = getParameter( ACTION_PARAM_NAME );
                String newChoreography = getParameter( CHOREOGRAPHY_PARAM_NAME );
                
                Engine.getInstance().getTransactionService().initializeMessage(
                        message,
                        ( StringUtils.isNotEmpty( originalMessageId ) ? originalMessageId : messageIdGenerator.getId() ),
                        ( StringUtils.isNotEmpty( originalConversationId ) ? originalConversationId : conversationIdGenerator.getId() ),
                        ( StringUtils.isNotEmpty( newAction ) ? newAction : originalAction ),
                        messageContext.getPartner().getPartnerId(),
                        ( StringUtils.isNotEmpty( newChoreography ) ? newChoreography : originalChoreography ) );
                return messageContext;
            } else {
                throw new NexusException( "MessagePojo is null" );
            }
        } else {
            throw new NexusException( "MessageContext is null" );
        }
        
    }

}
