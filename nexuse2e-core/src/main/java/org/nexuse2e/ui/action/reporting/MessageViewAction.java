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
package org.nexuse2e.ui.action.reporting;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.hibernate.LazyInitializationException;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.pojo.LogPojo;
import org.nexuse2e.pojo.MessagePayloadPojo;
import org.nexuse2e.pojo.MessagePojo;
import org.nexuse2e.ui.action.NexusE2EAction;
import org.nexuse2e.ui.form.ReportEngineEntryForm;
import org.nexuse2e.ui.form.ReportMessageEntryForm;

/**
 * @author guido.esch
 */
public class MessageViewAction extends NexusE2EAction {

    private static String URL     = "reporting.error.url";
    private static String TIMEOUT = "reporting.error.timeout";
    private static String ATTRIBUTE_COLLECTION_2 = "collection_2";
    /* (non-Javadoc)
     * @see com.tamgroup.nexus.e2e.ui.action.NexusE2EAction#executeNexusE2EAction(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.apache.struts.action.ActionMessages)
     */
    @Override
    public ActionForward executeNexusE2EAction( ActionMapping actionMapping, ActionForm actionForm,
            HttpServletRequest request, HttpServletResponse response, EngineConfiguration engineConfiguration, ActionMessages errors, ActionMessages messages )
            throws Exception {

        ActionForward success = actionMapping.findForward( ACTION_FORWARD_SUCCESS );
        ActionForward error = actionMapping.findForward( ACTION_FORWARD_FAILURE );
        
        ReportMessageEntryForm form = (ReportMessageEntryForm) actionForm;
        String messageId = request.getParameter( "mId" );
        
        MessagePojo message = Engine.getInstance().getTransactionService().getMessage( messageId );

        if ( message == null ) {
            ActionMessage errorMessage = new ActionMessage( "generic.error", "selected messageid: " + messageId
                    + " not found" );
            errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
            addRedirect( request, URL, TIMEOUT );
            return error;
        }
        form.setMessageProperties( message );
        //        form.setTimezone( ( (ReportingPropertiesForm) request.getSession().getAttribute( "reportingPropertiesForm" ) )
        //                .getTimezone() );

        List<String> parts = new ArrayList<String>();
        try {
            List<MessagePayloadPojo> list = Engine.getInstance().getTransactionService().getMessagePayloadsFromMessage( message );
            for (MessagePayloadPojo payload : list) {
                String type = payload.getMimeType();
                parts.add( type );
            }
        } catch ( LazyInitializationException e ) {
            throw new NexusException( e );
        }
        
        List<LogPojo> errorMessages  = Engine.getInstance().getTransactionService().getLogEntriesForReport( null, message.getConversation().getConversationId(), message.getMessageId(), false );
        List<ReportEngineEntryForm> logItems = new ArrayList<ReportEngineEntryForm>();
        if(errorMessages != null && errorMessages.size() > 0) {
        	
            
            if ( errorMessages != null ) {

                for (LogPojo logPojo : errorMessages) {
                	    ReportEngineEntryForm entry = new ReportEngineEntryForm();
                        entry.setEnginePorperties( logPojo );
                        logItems.add( entry );
                }
            }
            
        }
        
        request.setAttribute( ATTRIBUTE_COLLECTION_2, logItems );

        request.setAttribute( ATTRIBUTE_COLLECTION, parts );
        return success;
    }

}
