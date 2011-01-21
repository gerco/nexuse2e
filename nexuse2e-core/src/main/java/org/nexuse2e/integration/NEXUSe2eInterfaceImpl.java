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

import javax.jws.WebService;

import org.apache.log4j.Logger;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.backend.BackendPipelineDispatcher;
import org.nexuse2e.configuration.ConfigurationAccessService;
import org.nexuse2e.logging.LogMessage;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.pojo.ChoreographyPojo;
import org.nexuse2e.pojo.ConversationPojo;

@WebService(
        serviceName = "NEXUSe2eInterface",
        portName="NEXUSe2eInterfacePort",
        endpointInterface="org.nexuse2e.integration.NEXUSe2eInterface")
public class NEXUSe2eInterfaceImpl implements NEXUSe2eInterface {

    private static Logger LOG = Logger.getLogger( NEXUSe2eInterfaceImpl.class );

    /* (non-Javadoc)
     * @see org.nexuse2e.integration.NEXUSe2eInterface#createConversation(java.lang.String, java.lang.String)
     */
    public String createConversation( String choreographyId, String businessPartnerId ) throws NexusException {

        checkExist( choreographyId, businessPartnerId, null );
        
        LOG.debug( "createConversation - choreographyId: " + choreographyId + ", businessPartnerId: "
                + businessPartnerId );
        ConversationPojo conversationPojo = Engine.getInstance().getTransactionService().createConversation(
                choreographyId, businessPartnerId, null );
        return conversationPojo.getConversationId();
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.integration.NEXUSe2eInterface#createConversation(java.lang.String, java.lang.String, java.lang.String)
     */
    public String createConversation( String choreographyId, String businessPartnerId, String conversationId )
            throws NexusException {

        checkExist( choreographyId, businessPartnerId, null );
        
        LOG.debug( "createConversation - choreographyId: " + choreographyId + ", businessPartnerId: "
                + businessPartnerId + ", conversationId: " + conversationId );
        ConversationPojo conversationPojo = Engine.getInstance().getTransactionService().createConversation(
                choreographyId, businessPartnerId, conversationId );
        return conversationPojo.getConversationId();
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.integration.NEXUSe2eInterface#triggerSendingMessage(java.lang.String, java.lang.String, java.lang.Object)
     */
    public boolean triggerSendingMessage( String conversationId, String actionId, Object primaryKey )
            throws NexusException {

        LOG.debug( "triggerSendingMessage - conversationId: " + conversationId + ", actionId: " + actionId
                + ", primaryKey: " + primaryKey );

        BackendPipelineDispatcher backendPipelineDispatcher = Engine.getInstance().getCurrentConfiguration()
                .getStaticBeanContainer().getBackendPipelineDispatcher();

        if ( backendPipelineDispatcher != null ) {
            try {
                backendPipelineDispatcher.processMessage( conversationId, actionId, primaryKey, null );
            } catch ( NexusException e ) {
                LOG.debug( "sendStringMessage - error: " + e );
                e.printStackTrace();
            }
        }

        return true;
    } // triggerSendingMessage

    /* (non-Javadoc)
     * @see org.nexuse2e.integration.NEXUSe2eInterface#sendStringMessage(java.lang.String, java.lang.String, java.lang.String)
     */
    public boolean sendStringMessage( String conversationId, String actionId, String payload ) throws NexusException {

        LOG.debug( "sendMessage - conversationId: " + conversationId + ", actionId: " + actionId + ", primaryKey: "
                + payload );

        BackendPipelineDispatcher backendPipelineDispatcher = Engine.getInstance().getCurrentConfiguration()
                .getStaticBeanContainer().getBackendPipelineDispatcher();

        if ( backendPipelineDispatcher != null ) {
            try {
                backendPipelineDispatcher.processMessage(
                        conversationId, actionId, null, payload == null ? null : payload.getBytes() );
            } catch ( NexusException e ) {
                LOG.debug( "sendStringMessage - error: " + e );
                e.printStackTrace();
            }
        }
        return true;
    } // sendStringMessage

    /* (non-Javadoc)
     * @see org.nexuse2e.integration.NEXUSe2eInterface#triggerSendingNewMessage(java.lang.String, java.lang.String, java.lang.String, java.lang.Object)
     */
    public String triggerSendingNewMessage( String choreographyId, String businessPartnerId, String actionId,
            Object primaryKey ) throws NexusException {

        return triggerSendingNewMessage( choreographyId, businessPartnerId, actionId, null, primaryKey );
    } // triggerSendingNewMessage

    /* (non-Javadoc)
     * @see org.nexuse2e.integration.NEXUSe2eInterface#triggerSendingNewMessage(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.Object)
     */
    public String triggerSendingNewMessage( String choreographyId, String businessPartnerId, String actionId,
            String conversationId, Object primaryKey ) throws NexusException {

        return triggerSendingNewMessage( choreographyId, businessPartnerId, actionId, conversationId, null, primaryKey );
    } // triggerSendingNewMessage
    
    /* (non-Javadoc)
     * @see org.nexuse2e.integration.NEXUSe2eInterface#triggerSendingNewMessage(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.Object)
     */
    public String triggerSendingNewMessage( String choreographyId,
                                            String businessPartnerId,
                                            String actionId,
                                            String conversationId,
                                            String messageId,
                                            Object primaryKey ) throws NexusException {
        checkExist( choreographyId, businessPartnerId, actionId );
        
        MessageContext messageContext = null;

        LOG.debug( "Parameters - choreographyId: " + choreographyId + ", messageId: " + messageId + ", businessPartnerId: "
                + businessPartnerId + ", actionId: " + actionId + ", primaryKey: " + primaryKey );

        BackendPipelineDispatcher backendPipelineDispatcher = Engine.getInstance().getCurrentConfiguration()
                .getStaticBeanContainer().getBackendPipelineDispatcher();
        LOG.debug( "BackendPipelineDispatcher: " + backendPipelineDispatcher );
        if ( backendPipelineDispatcher != null ) {
            try {
                messageContext = backendPipelineDispatcher.processMessage( businessPartnerId, choreographyId, actionId,
                        conversationId, messageId, null, primaryKey, null );
            } catch ( NexusException e ) {
                LOG.debug( "Error submitting message: " + e );
                throw e;
            }
        }

        if (messageContext.getMessagePojo() != null && messageContext.getMessagePojo().getConversation() != null) {
            return messageContext.getMessagePojo().getConversation().getConversationId();
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.integration.NEXUSe2eInterface#sendNewStringMessage(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public String sendNewStringMessage( String choreographyId, String businessPartnerId, String actionId, String payload )
            throws NexusException {

        return sendNewStringMessage( choreographyId, businessPartnerId, actionId, null, payload );
    }

    public String sendNewStringMessage( String choreographyId, String businessPartnerId, String actionId,
            String conversationId, String payload ) throws NexusException {

        return sendNewStringMessage( choreographyId, businessPartnerId, actionId, conversationId, null, payload );
    } // sendNewStringMessage
    
    /* (non-Javadoc)
     * @see org.nexuse2e.integration.NEXUSe2eInterface#sendNewStringMessage(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public String sendNewStringMessage( String choreographyId,
                                        String businessPartnerId,
                                        String actionId,
                                        String conversationId,
                                        String messageId,
                                        String payload ) throws NexusException {
        checkExist( choreographyId, businessPartnerId, actionId );
        
        LOG.debug( "Parameters - choreographyId: " + choreographyId + ", messageId: " + messageId + ", businessPartnerId: "
                + businessPartnerId + ", actionId: " + actionId + ", primaryKey: " + payload );

        BackendPipelineDispatcher backendPipelineDispatcher = Engine.getInstance().getCurrentConfiguration()
                .getStaticBeanContainer().getBackendPipelineDispatcher();
        LOG.debug( "BackendPipelineDispatcher: " + backendPipelineDispatcher );
        if ( backendPipelineDispatcher != null ) {
            try {
                MessageContext messageContext = backendPipelineDispatcher.processMessage( businessPartnerId, choreographyId, actionId,
                        conversationId, messageId, null, null, (payload != null ? payload.getBytes() : null) );

                if (messageContext.getMessagePojo() != null && messageContext.getMessagePojo().getConversation() != null) {
                    return messageContext.getMessagePojo().getConversation().getConversationId();
                }
            } catch ( NexusException e ) {
                LOG.error(new LogMessage("Error submitting message: ", conversationId, messageId), e);
            }
        }

        return null;
    }

    /**
     * Checks if the given IDs exist.
     * @param choreographyId The choreography ID to check. Can be <code>null</code>.
     * @param businessPartnerId The business partner ID to check. Can be <code>null</code>.
     * @param actionId The actionId to check. Can be <code>null</code>.
     * @throws NexusException if a non-<code>null</code> ID does not exist.
     */
    protected void checkExist( String choreographyId, String businessPartnerId, String actionId )
    throws NexusException {
        ConfigurationAccessService cas = Engine.getInstance().getActiveConfigurationAccessService();
        String error = null;
        ChoreographyPojo choreography = null;
        if (choreographyId != null) {
            choreography = cas.getChoreographyByChoreographyId( choreographyId );
            if (choreography == null) {
                String msg = "No choreography with ID " + choreographyId + " found";
                error = msg;
            }
        }
        if (businessPartnerId != null) {
            if (cas.getPartnerByPartnerId( businessPartnerId ) == null) {
                String msg = "No partner with ID " + businessPartnerId + " found";
                if (error == null) {
                    error = msg;
                } else {
                    error += "; " + msg;
                }
            }
        }
        if (actionId != null && choreography != null) {
            if (cas.getActionFromChoreographyByActionId( choreography, actionId ) == null) {
                String msg = "No action with ID " + actionId + " found";
                if (error == null) {
                    error = msg;
                } else {
                    error += "; " + msg;
                }
            }
        }
        if (error != null) {
            throw new NexusException( error );
        }
    }

} // NEXUSe2eInterfaceImpl
