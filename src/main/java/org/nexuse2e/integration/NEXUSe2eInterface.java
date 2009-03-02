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
package org.nexuse2e.integration;

import java.rmi.Remote;
import java.rmi.RemoteException;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.nexuse2e.NexusException;

/**
 * Simple interface for submitting messages to the NEXUSe2e system for delivery to a communication partner.
 *
 * @author mbreilmann
 */
@WebService(name="NEXUSe2eInterface", targetNamespace = "http://integration.nexuse2e.org")
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface NEXUSe2eInterface extends Remote {

    /**
     * Create a new conversation (instance of a choreography) in order to submit messages to a specific partner.
     * @param choreographyId The ID of the choreography to create the conversation for.
     * @param businessPartnerId The ID of the partner to exchange messages with.
     * @return The ID of the newly created conversation.
     * 
     * @deprecated Method is not required any more. Use sendNewStringMessage when sending the first message in conversation.
     */
    @WebMethod(operationName="createConversation1", exclude=true)
    public String createConversation( String choreographyId, String businessPartnerId ) throws RemoteException, NexusException;

    /**
     * Create a new conversation (instance of a choreography) in order to submit messages to a specific partner.
     * @param choreographyId The ID of the choreography to create the conversation for.
     * @param businessPartnerId The ID of the partner to exchange messages with.
     * @param conversationId The ID to use for the new conversation.
     * @return The ID of the newly created conversation.
     * 
     * @deprecated Method is not required any more. Use sendNewStringMessage when sending the first message in conversation.
     */
    @WebMethod(operationName = "createConversation", action = "http://integration.nexuse2e.org/NEXUSe2eInterface/createConversation")
    @WebResult(name = "createConversationResponse", targetNamespace = "")
    public String createConversation(
            @WebParam(name = "choreographyId", targetNamespace = "")
            String choreographyId,
            @WebParam(name = "businessPartnerId", targetNamespace = "")
            String businessPartnerId,
            @WebParam(name = "conversationId", targetNamespace = "")
            String conversationId )
            throws
            RemoteException,
            NexusException;

    /**
     * Trigger sending a message by providing some sort of primary key that allows the <code>Pipelet</code>
     * instances in the <code>Pipeline</code> to retrieve or create the message payload(s).
     * @param conversationId The ID of a previously created conversation this message belongs to.
     * @param actionId The ID of the action to trigger in the choreography.
     * @param primaryKey The primary key used to retrieve/create the payload.
     * @return TRUE if the message was submitted to the engine successfully (i.e. a message could be created 
     * and was persisted).
     */
    @WebMethod(operationName = "triggerSendingMessage", action = "http://integration.nexuse2e.org/NEXUSe2eInterface/triggerSendingMessage")
    @WebResult(name = "triggerSendingMessageResponse", targetNamespace = "")
    public boolean triggerSendingMessage(
            @WebParam(name = "conversationId", targetNamespace = "")
            String conversationId,
            @WebParam(name = "actionId", targetNamespace = "")
            String actionId,
            @WebParam(name = "primaryKey", targetNamespace = "")
            Object primaryKey )
            throws RemoteException, NexusException;

    /**
     * Send a message with a String payload (e.g. XML or plain text).
     * @param conversationId The ID of a previously created conversation this message belongs to.
     * @param actionId The ID of the action to trigger in the choreography.
     * @param payload The single payload of the message.
     * @return TRUE if the message was submitted to the engine successfully (i.e. a message could be created 
     * and was persisted).
     */
    @WebMethod(operationName = "sendStringMessage", action = "http://integration.nexuse2e.org/NEXUSe2eInterface/sendStringMessage")
    @WebResult(name = "sendStringMessageResponse", targetNamespace = "")
    public boolean sendStringMessage(
            @WebParam(name = "conversationId", targetNamespace = "")
            String conversationId,
            @WebParam(name = "actionId", targetNamespace = "")
            String actionId,
            @WebParam(name = "payload", targetNamespace = "")
            String payload ) throws RemoteException, NexusException;

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
    @WebMethod(operationName="triggerSendingNewMessage1", exclude = true)
    public String triggerSendingNewMessage( String choreographyId, String businessPartnerId, String actionId,
            Object primaryKey ) throws RemoteException, NexusException;

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
    @WebMethod(operationName = "triggerSendingNewMessage", action = "http://integration.nexuse2e.org/NEXUSe2eInterface/triggerSendingNewMessage")
    @WebResult(name = "triggerSendingNewMessageResponse", targetNamespace = "")
    public String triggerSendingNewMessage(
            @WebParam(name = "choreographyId", targetNamespace = "")
            String choreographyId,
            @WebParam(name = "businessPartnerId", targetNamespace = "")
            String businessPartnerId,
            @WebParam(name = "actionId", targetNamespace = "")
            String actionId,
            @WebParam(name = "conversationId", targetNamespace = "")
            String conversationId,
            @WebParam(name = "primaryKey", targetNamespace = "")
            Object primaryKey ) throws RemoteException, NexusException;

    /**
     * Send a message with a String payload (e.g. XML or plain text) while also creating
     * a new conversation for the specified choreography and partner.
     * @param choreographyId The ID of the choreography to create the conversation for.
     * @param businessPartnerId The ID of the partner to exchange messages with.
     * @param actionId The ID of the action to trigger in the choreography.
     * @param payload The single payload of the message.
     * @return The ID of the conversation that was created for this message.
     */
    @WebMethod(operationName = "sendNewStringMessage", action = "http://integration.nexuse2e.org/NEXUSe2eInterface/sendNewStringMessage")
    @WebResult(name = "sendNewStringMessageResponse", targetNamespace = "")
    public String sendNewStringMessage(
            @WebParam(name = "choreographyId", targetNamespace = "")
            String choreographyId,
            @WebParam(name = "businessPartnerId", targetNamespace = "")
            String businessPartnerId,
            @WebParam(name = "actionId", targetNamespace = "")
            String actionId,
            @WebParam(name = "payload", targetNamespace = "")
            String payload )
            throws RemoteException, NexusException;

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
    @WebMethod(operationName = "sendNewStringMessage1", action = "http://integration.nexuse2e.org/NEXUSe2eInterface/sendNewStringMessage1")
    @WebResult(name = "sendNewStringMessage1Response", targetNamespace = "")
    public String sendNewStringMessage(
            @WebParam(name = "choreographyId", targetNamespace = "")
            String choreographyId,
            @WebParam(name = "businessPartnerId", targetNamespace = "")
            String businessPartnerId,
            @WebParam(name = "actionId", targetNamespace = "")
            String actionId,
            @WebParam(name = "conversationId", targetNamespace = "")
            String conversationId,
            @WebParam(name = "payload", targetNamespace = "")
            String payload ) throws RemoteException, NexusException;

} // NEXUSe2eInterface
