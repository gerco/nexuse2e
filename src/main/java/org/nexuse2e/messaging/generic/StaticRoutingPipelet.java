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
