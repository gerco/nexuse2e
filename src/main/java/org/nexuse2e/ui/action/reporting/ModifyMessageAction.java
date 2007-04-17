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
package org.nexuse2e.ui.action.reporting;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.nexuse2e.Engine;
import org.nexuse2e.messaging.BackendActionSerializer;
import org.nexuse2e.messaging.FrontendActionSerializer;
import org.nexuse2e.ui.action.NexusE2EAction;
import org.nexuse2e.ui.form.ReportingPropertiesForm;

/**
 * Requeue or stop a message
 *
 * @author guido.esch
 */
public class ModifyMessageAction extends NexusE2EAction {

    private static Logger LOG = Logger.getLogger( ModifyMessageAction.class );

    /* (non-Javadoc)
     * @see org.nexuse2e.ui.action.NexusE2EAction#executeNexusE2EAction(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.apache.struts.action.ActionMessages, org.apache.struts.action.ActionMessages)
     */
    public ActionForward executeNexusE2EAction( ActionMapping actionMapping, ActionForm actionForm,
            HttpServletRequest request, HttpServletResponse response, ActionMessages errors, ActionMessages messages )
            throws Exception {

        ActionForward success = actionMapping.findForward( ACTION_FORWARD_SUCCESS );
        // ActionForward error = actionMapping.findForward( ACTION_FORWARD_FAILURE );

        ReportingPropertiesForm reportingPropertiesForm = (ReportingPropertiesForm) actionForm;

        String participantId = reportingPropertiesForm.getParticipantId();
        String choreographyId = reportingPropertiesForm.getChoreographyId();
        String conversationId = reportingPropertiesForm.getConversationId();
        String messageId = reportingPropertiesForm.getMessageId();
        String action = reportingPropertiesForm.getCommand();
        boolean outbound = reportingPropertiesForm.isOutbound();

        if ( action == null || participantId == null || choreographyId == null || conversationId == null
                || messageId == null ) {
            ActionMessage errorMessage = new ActionMessage( "generic.error", "can't modify message status" );
            errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
        }
        if ( action.equals( "requeue" ) ) {

            if ( outbound ) {
                LOG.debug( "Requeueing outbound message " + messageId + " for choreography " + choreographyId
                        + " and participant " + participantId );
                HashMap<String, BackendActionSerializer> backendActionSerializers = Engine.getInstance()
                        .getCurrentConfiguration().getBackendActionSerializers();
                BackendActionSerializer backendActionSerializer = backendActionSerializers.get( choreographyId );

                try {
                    backendActionSerializer.requeueMessage( choreographyId, participantId, conversationId, messageId );
                } catch ( Exception e ) {
                    ActionMessage errorMessage = new ActionMessage( "generic.error", e.getMessage() );
                    errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
                }
            } else {
                LOG.debug( "Requeueing inbound message " + messageId + " for choreography " + choreographyId
                        + " and participant " + participantId );

                HashMap<String, FrontendActionSerializer> frontendActionSerializers = Engine.getInstance()
                        .getCurrentConfiguration().getFrontendActionSerializers();
                FrontendActionSerializer frontendActionSerializer = frontendActionSerializers.get( choreographyId );

                try {
                    frontendActionSerializer.requeueMessage( choreographyId, participantId, conversationId, messageId );
                } catch ( Exception e ) {
                    ActionMessage errorMessage = new ActionMessage( "generic.error", e.getMessage() );
                    errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
                }
            }
        } else if ( action.equals( "stop" ) ) {

            LOG.debug( "Stopping message " + messageId + " for choreography " + choreographyId + " and participant "
                    + participantId );
            // Message newMessage = MessageJDBCPersistent.getMessage( choreography, participant, conversation, message );
            try {
                // MessageDispatcher.getInstance().stopMessage( newMessage );
            } catch ( Exception e ) {
                ActionMessage errorMessage = new ActionMessage( "generic.error", e.getMessage() );
                errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
            }

        }
        return success;
    }
} // ModifyMessageAction
