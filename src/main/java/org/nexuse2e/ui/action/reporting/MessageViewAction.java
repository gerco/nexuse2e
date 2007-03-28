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

import java.util.Iterator;
import java.util.Vector;

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
import org.nexuse2e.pojo.MessagePayloadPojo;
import org.nexuse2e.pojo.MessagePojo;
import org.nexuse2e.ui.action.NexusE2EAction;
import org.nexuse2e.ui.form.ReportMessageEntryForm;

/**
 * @author guido.esch
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MessageViewAction extends NexusE2EAction {

    private static final String VERSIONSTRING = "$Id: MessageViewAction.java 879 2005-07-21 14:17:36Z markus.breilmann $";

    private static String       URL           = "reporting.error.url";
    private static String       TIMEOUT       = "reporting.error.timeout";

    /* (non-Javadoc)
     * @see com.tamgroup.nexus.e2e.ui.action.NexusE2EAction#executeNexusE2EAction(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.apache.struts.action.ActionMessages)
     */
    @Override
    public ActionForward executeNexusE2EAction( ActionMapping actionMapping, ActionForm actionForm,
            HttpServletRequest request, HttpServletResponse response, ActionMessages errors, ActionMessages messages )
            throws Exception {

        ActionForward success = actionMapping.findForward( ACTION_FORWARD_SUCCESS );
        ActionForward error = actionMapping.findForward( ACTION_FORWARD_FAILURE );
        //request.getSession().setAttribute( Crumbs.CURRENT_LOCATION, Crumbs.REPORT_MESSAGE_VIEW );

        //request.getSession().setAttribute( Crumbs.CURRENT_LOCATION, Crumbs.REPORT_CONVERSATION_VIEW );

        ReportMessageEntryForm form = (ReportMessageEntryForm) actionForm;
        String messageId = request.getParameter( "mId" );
        //        String conversationId = request.getParameter( "convId" );
        //        String choreographyId = request.getParameter( "chorId" );
        //        String partnerId = request.getParameter( "partnerId" );

        LOG.trace( "messageId: " + messageId );

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

        Vector<String> parts = new Vector<String>();
        Iterator i;
        try {
            i = Engine.getInstance().getTransactionService().getMessagePayloadsFromMessage( message ).iterator();
        } catch ( LazyInitializationException e ) {
            throw new NexusException( e );
        }
        while ( i.hasNext() ) {
            MessagePayloadPojo payload = (MessagePayloadPojo) i.next();
            String type = payload.getMimeType();
            parts.addElement( type );
        }

        request.setAttribute( ATTRIBUTE_COLLECTION, parts );
        return success;
    }

}
