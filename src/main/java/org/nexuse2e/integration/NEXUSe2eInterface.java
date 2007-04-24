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
package org.nexuse2e.integration;

import org.nexuse2e.NexusException;

/**
 * Simple interface for submitting messages to the NEXUSe2e system for delivery to a communication partner.
 *
 * @author mbreilmann
 */
public interface NEXUSe2eInterface {

    /**
     * Create a new conversation (instance of a choreography) in order to submit messages to a specific partner.
     * @param choreographyId The ID of the choreography to create the conversation for.
     * @param businessPartnerId The ID of the partner to exchange messages with.
     * @return The ID of the newly created conversation.
     */
    public String createConversation( String choreographyId, String businessPartnerId ) throws NexusException;

    /**
     * Create a new conversation (instance of a choreography) in order to submit messages to a specific partner.
     * @param choreographyId The ID of the choreography to create the conversation for.
     * @param businessPartnerId The ID of the partner to exchange messages with.
     * @param conversationId The ID to use for the new conversation.
     * @return The ID of the newly created conversation.
     */
    public String createConversation( String choreographyId, String businessPartnerId, String conversationId )
            throws NexusException;

    /**
     * Trigger sending a message by providing some sort of primary key that allows the <code>Pipelet</code>
     * instances in the <code>Pipeline</code> to retrieve or create the message payload(s).
     * @param conversationId The ID of a previously created conversation this message belongs to.
     * @param actionId The ID of the action to trigger in the choreography.
     * @param primaryKey The primary key used to retrieve/create the payload.
     * @return TRUE if the message was submitted to the engine successfully (i.e. a message could be created 
     * and was persisted).
     */
    public boolean triggerSendingMessage( String conversationId, String actionId, Object primaryKey )
            throws NexusException;

    /**
     * Send a message with a String payload (e.g. XML or plain text).
     * @param conversationId The ID of a previously created conversation this message belongs to.
     * @param actionId The ID of the action to trigger in the choreography.
     * @param payload The single payload of the message.
     * @return TRUE if the message was submitted to the engine successfully (i.e. a message could be created 
     * and was persisted).
     */
    public boolean sendStringMessage( String conversationId, String actionId, String payload ) throws NexusException;

    /**
     * Trigger sending a message by providing some sort of primary key that allows the <code>Pipelet</code>
     * instances in the <code>Pipeline</code> to retrieve or create the message payload(s) while also creating
     * a new conversation for the specified choreography and partner.
     * @param choreographyId The ID of the choreography to create the conversation for.
     * @param businessPartnerId The ID of the partner to exchange messages with.
     * @param actionId The ID of the action to trigger in the choreography.
     * @param primaryKey The primary key used to retrieve/create the payload.
     * @return The ID of the conversation that was created for this message.
     */
    public String triggerSendingNewMessage( String choreographyId, String businessPartnerId, String actionId,
            Object primaryKey ) throws NexusException;

    /**
     * Trigger sending a message by providing some sort of primary key that allows the <code>Pipelet</code>
     * instances in the <code>Pipeline</code> to retrieve or create the message payload(s) while also creating
     * a new conversation for the specified choreography and partner.
     * @param choreographyId The ID of the choreography to create the conversation for.
     * @param businessPartnerId The ID of the partner to exchange messages with.
     * @param actionId The ID of the action to trigger in the choreography.
     * @param conversationId The ID to use for the new conversation.
     * @param primaryKey The primary key used to retrieve/create the payload.
     * @return The ID of the conversation that was created for this message.
     */
    public String triggerSendingNewMessage( String choreographyId, String businessPartnerId, String actionId,
            String conversationId, Object primaryKey ) throws NexusException;

    /**
     * Send a message with a String payload (e.g. XML or plain text) while also creating
     * a new conversation for the specified choreography and partner.
     * @param choreographyId The ID of the choreography to create the conversation for.
     * @param businessPartnerId The ID of the partner to exchange messages with.
     * @param actionId The ID of the action to trigger in the choreography.
     * @param payload The single payload of the message.
     * @return The ID of the conversation that was created for this message.
     */
    public String sendNewStringMessage( String choreographyId, String businessPartnerId, String actionId, String payload )
            throws NexusException;

    /**
     * Send a message with a String payload (e.g. XML or plain text) while also creating
     * a new conversation for the specified choreography and partner.
     * @param choreographyId The ID of the choreography to create the conversation for.
     * @param businessPartnerId The ID of the partner to exchange messages with.
     * @param actionId The ID of the action to trigger in the choreography.
     * @param conversationId The ID to use for the new conversation.
     * @param payload The single payload of the message.
     * @return The ID of the conversation that was created for this message.
     */
    public String sendNewStringMessage( String choreographyId, String businessPartnerId, String actionId,
            String conversationId, String payload ) throws NexusException;

} // NEXUSe2eInterface
