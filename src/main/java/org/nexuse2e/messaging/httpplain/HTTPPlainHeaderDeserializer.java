/**
 * NEXUSe2e Business Messaging Open Source  
 * Copyright 2007, Tamgroup and X-ioma GmbH   
 *  
 * This is free software; you can redistribute it and/or modify it  
 * under the terms of the GNU Lesser General Public License as  
 * published by the Free Software Foundation version 2.1 of  
 * the License.  
 *  
 * This software is distributed in the hope that it will be useful,  
 * but WITHOUT ANY WARRANTY; without even the implied warranty of  
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU  
 * Lesser General Public License for more details.  
 *  
 * You should have received a copy of the GNU Lesser General Public  
 * License along with this software; if not, write to the Free  
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.nexuse2e.messaging.httpplain;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.messaging.AbstractPipelet;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.pojo.MessagePojo;

/**
 * @author mbreilmann
 *
 */
public class HTTPPlainHeaderDeserializer extends AbstractPipelet {

    private static Logger       LOG = Logger.getLogger( HTTPPlainHeaderDeserializer.class );

    /**
     * Default constructor.
     */
    public HTTPPlainHeaderDeserializer() {

    }

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.MessageUnpackager#processMessage(com.tamgroup.nexus.e2e.persistence.pojo.MessagePojo, byte[])
     */
    public MessageContext processMessage( MessageContext messageContext )
            throws IllegalArgumentException, IllegalStateException, NexusException {

        Object object = messageContext.getData();
        if ( !( object instanceof HttpServletRequest ) ) {
            throw new IllegalArgumentException( "Unable to process message: raw data not of type HttpServletRequest!" );
        }

        HttpServletRequest request = (HttpServletRequest) object;

        // required params from post.
        String choreographyId = request.getParameter( "ChoreographyID" );
        String participantId = request.getParameter( "ParticipantID" );
        String actionId = request.getParameter( "ActionID" );

        // optional params, if they don't exist, new ones will be generated.
        String conversationId = request.getParameter( "ConversationID" );
        String messageId = request.getParameter( "MessageID" );

        //Verify params, if required one do not exist, reject post.
        if ( choreographyId == null || actionId == null || participantId == null ) {
            LOG.error( "Received post without required parameters." );
            throw new NexusException( "Received post without required parameters." ); //invalid post, Action, choreography, and participantid parameters are required.
        }
        if ( conversationId == null ) {
            conversationId = Engine.getInstance().getIdGenerator( org.nexuse2e.Constants.ID_GENERATOR_CONVERSATION )
                    .getId();
        }

        if ( messageId == null ) {
            messageId = Engine.getInstance().getIdGenerator( org.nexuse2e.Constants.ID_GENERATOR_MESSAGE ).getId();
        }

        MessagePojo messagePojo = messageContext.getMessagePojo();

        messagePojo = Engine.getInstance().getTransactionService().initializeMessage( messagePojo, messageId,
                conversationId, actionId, participantId, choreographyId );

        messagePojo.setOutbound( false );
        messagePojo.setType( org.nexuse2e.messaging.Constants.INT_MESSAGE_TYPE_NORMAL );

        return messageContext;
    } // processMessage
    

} // HTTPPlainHeaderDeserializer
