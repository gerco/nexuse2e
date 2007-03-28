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

import org.apache.log4j.Logger;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.backend.BackendPipelineDispatcher;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.pojo.ConversationPojo;

public class NEXUSe2eInterfaceImpl implements NEXUSe2eInterface {

    private static Logger LOG = Logger.getLogger( NEXUSe2eInterfaceImpl.class );

    /* (non-Javadoc)
     * @see org.nexuse2e.integration.NEXUSe2eInterface#createConversation(java.lang.String, java.lang.String)
     */
    public String createConversation( String choreographyId, String businessPartnerId ) throws NexusException {

        LOG.debug( "createConversation - choreographyId: " + choreographyId + ", businessPartnerId: "
                + businessPartnerId );
        ConversationPojo conversationPojo = Engine.getInstance().getTransactionService().createConversation(
                choreographyId, businessPartnerId, null );
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
                backendPipelineDispatcher.processMessage( conversationId, actionId, null, payload.getBytes() );
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

        MessageContext messagePipeletParameter = null;

        LOG.debug( "triggerSendingNewMessage - choreographyId: " + choreographyId + ", businessPartnerId: "
                + businessPartnerId + ", actionId: " + actionId + ", primaryKey: " + primaryKey );

        BackendPipelineDispatcher backendPipelineDispatcher = Engine.getInstance().getCurrentConfiguration()
                .getStaticBeanContainer().getBackendPipelineDispatcher();
        LOG.debug( "sendNewStringMessage - backendPipelineDispatcher: " + backendPipelineDispatcher );
        if ( backendPipelineDispatcher != null ) {
            try {
                messagePipeletParameter = backendPipelineDispatcher.processMessage( businessPartnerId, choreographyId,
                        actionId, null, null, primaryKey, null );
            } catch ( NexusException e ) {
                LOG.debug( "sendNewStringMessage - error: " + e );
                e.printStackTrace();
            }
        }

        return messagePipeletParameter.getMessagePojo().getConversation().getConversationId();
    } // triggerSendingNewMessage

    /* (non-Javadoc)
     * @see org.nexuse2e.integration.NEXUSe2eInterface#sendNewStringMessage(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public String sendNewStringMessage( String choreographyId, String businessPartnerId, String actionId, String payload )
            throws NexusException {

        MessageContext messagePipeletParameter = null;

        LOG.debug( "sendNewStringMessage - choreographyId: " + choreographyId + ", businessPartnerId: "
                + businessPartnerId + ", actionId: " + actionId + ", primaryKey: " + payload );

        BackendPipelineDispatcher backendPipelineDispatcher = Engine.getInstance().getCurrentConfiguration()
                .getStaticBeanContainer().getBackendPipelineDispatcher();
        LOG.debug( "sendNewStringMessage - backendPipelineDispatcher: " + backendPipelineDispatcher );
        if ( backendPipelineDispatcher != null ) {
            try {
                messagePipeletParameter = backendPipelineDispatcher.processMessage( businessPartnerId, choreographyId,
                        actionId, null, null, null, payload.getBytes() );
            } catch ( NexusException e ) {
                LOG.debug( "sendNewStringMessage - error: " + e );
                e.printStackTrace();
            }
        }

        return messagePipeletParameter.getMessagePojo().getConversation().getConversationId();
    } // sendNewStringMessage

} // NEXUSe2eInterfaceImpl
