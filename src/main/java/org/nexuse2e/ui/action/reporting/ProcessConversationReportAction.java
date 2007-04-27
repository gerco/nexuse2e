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

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.nexuse2e.Engine;
import org.nexuse2e.dao.TransactionDAO;
import org.nexuse2e.pojo.ChoreographyPojo;
import org.nexuse2e.pojo.ConversationPojo;
import org.nexuse2e.pojo.MessagePojo;
import org.nexuse2e.pojo.PartnerPojo;
import org.nexuse2e.ui.action.NexusE2EAction;
import org.nexuse2e.ui.form.ReportConversationEntryForm;
import org.nexuse2e.ui.form.ReportMessageEntryForm;
import org.nexuse2e.ui.form.ReportingPropertiesForm;
import org.nexuse2e.util.DateUtil;

/**
 * @author guido.esch
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ProcessConversationReportAction extends NexusE2EAction {

    private static final String VERSIONSTRING = "$Id: ProcessConversationReportAction.java 1306 2006-08-15 16:07:20Z guido.esch $";

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

        ReportingPropertiesForm form = (ReportingPropertiesForm) actionForm;

        String dir = form.getCommand();
        String refresh = request.getParameter( "refresh" );
        if ( refresh != null ) {
            dir = "transaction";
        }
        String timezone = form.getTimezone();

        String searchFor = form.getSearchFor();

        form.setConvColSelect( true );
        form.setConvColAction( true );
        form.setConvColChorId( true );
        form.setConvColStatus( true );
        form.setConvColPartId( true );
        form.setConvColCreated( true );

        if ( searchFor != null && searchFor.equals( "message" ) && dir != null && dir.equals( "requeue" ) ) {
            //            for ( int i = 0; i < form.getSelectedResults().length; i++ ) {
            //                String messageIdent = form.getSelectedResults()[i];
            //                StringTokenizer st = new StringTokenizer( messageIdent, "|" );
            //                if ( st.countTokens() != 4 ) {
            //                    ActionMessage errorMessage = new ActionMessage( "generic.error", "cant re-queue Message: >"
            //                            + messageIdent + "<" );
            //                    errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
            //                    addRedirect( request, URL, TIMEOUT );
            //                    continue;
            //                }
            //                String participant = st.nextToken();
            //                String choreography = st.nextToken();
            //                String conversation = st.nextToken();
            //                String message = st.nextToken();
            //
            //                MessageDAO messageDao = new MessageDAO();
            //                MessagePojo messagePojo = messageDao.getMessageByMessageKey( choreography, conversation, message, participant );
            //                Message newMessage = new Message(messagePojo);
            //                
            //                
            //                try {
            //                    MessageDispatcher.getInstance().startMessage( newMessage );
            //                } catch ( Exception e ) {
            //                    ActionMessage errorMessage = new ActionMessage( "generic.error", e.getMessage() );
            //                    errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
            //                    addRedirect( request, URL, TIMEOUT );
            //                    continue;
            //                }
            //            }

            dir = "transaction";
        }
        if ( searchFor != null && searchFor.equals( "message" ) && dir != null && dir.equals( "stop" ) ) {
            //            for ( int i = 0; i < form.getSelectedResults().length; i++ ) {
            //                String messageIdent = form.getSelectedResults()[i];
            //                StringTokenizer st = new StringTokenizer( messageIdent, "|" );
            //                if ( st.countTokens() != 4 ) {
            //                    ActionMessage errorMessage = new ActionMessage( "generic.error", "cant stop Message: >"
            //                            + messageIdent + "<" );
            //                    errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
            //                    addRedirect( request, URL, TIMEOUT );
            //                    continue;
            //                }
            //                String participant = st.nextToken();
            //                String choreography = st.nextToken();
            //                String conversation = st.nextToken();
            //                String message = st.nextToken();
            //
            //                MessageDAO messageDao = new MessageDAO();
            //                MessagePojo messagePojo = messageDao.getMessageByMessageKey( choreography, conversation, message, participant );
            //                Message newMessage = new Message(messagePojo);
            //                
            //                try {
            //                    MessageDispatcher.getInstance().stopMessage( newMessage );
            //                } catch ( MessagingException e ) {
            //                    ActionMessage errorMessage = new ActionMessage( "generic.error", e.getMessage() );
            //                    errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
            //                    addRedirect( request, URL, TIMEOUT );
            //                    continue;
            //                }
            //            }
            dir = "transaction";
        }
        if ( searchFor != null && searchFor.equals( "conversation" ) && dir != null && dir.equals( "delete" ) ) {
            System.out.println( "deleting conversation" );

            for ( int i = 0; i < form.getSelectedResults().length; i++ ) {

                String messageIdent = form.getSelectedResults()[i];
                StringTokenizer st = new StringTokenizer( messageIdent, "|" );
                if ( st.countTokens() != 3 ) {
                    ActionMessage errorMessage = new ActionMessage( "generic.error", "cant delete conversation: >"
                            + messageIdent + "<" );
                    errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
                    addRedirect( request, URL, TIMEOUT );
                    continue;
                }

                String participantId = st.nextToken();
                String choreographyId = st.nextToken();
                String conversationId = st.nextToken();
                
                System.out.println("delete: "+participantId+","+choreographyId+","+conversationId);
                ConversationPojo conversation = Engine.getInstance().getTransactionService().getConversation( conversationId );
                
                if(conversation != null) {
                    Engine.getInstance().getTransactionService().deleteConversation( conversation, null, null );
                }
                
            }

            dir = "transaction";
        }

        String status = null;
        if ( form.getStatus() != null && !form.getStatus().equals( "" ) ) {
            status = form.getStatus();
        }
        String participantId = null;
        if ( form.getParticipantId() != null && !form.getParticipantId().equals( "" ) ) {
            participantId = form.getParticipantId();
        }

        String choreographyId = null;
        if ( form.getChoreographyId() != null && !form.getChoreographyId().equals( "" ) ) {
            choreographyId = form.getChoreographyId();
        }

        String conversationId = null;
        boolean convActive = form.isConversationEnabled();
        if ( convActive && form.getConversationId() != null && !form.getConversationId().equals( "" ) ) {
            conversationId = form.getConversationId();
        }

        String messageId = null;
        boolean messageActive = form.isMessageEnabled();
        if ( messageActive && form.getMessageId() != null && !form.getMessageId().equals( "" ) ) {
            messageId = form.getMessageId();
        }
        boolean startActive = form.isStartEnabled();
        boolean endActive = form.isEndEnabled();

        String startDate = null;
        if ( startActive ) {
            startDate = DateUtil.localTimeToTimezone( getStartDate( form ), null, null );
        }
        String endDate = null;
        if ( endActive ) {
            endDate = DateUtil.localTimeToTimezone( getEndDate( form ), null, null );
        }
        int items = 0;
        List logItems = new Vector();

        int nxPartnerId = 0;
        int nxChoreographyId = 0;

        if ( participantId != null ) {
            PartnerPojo partner = Engine.getInstance().getActiveConfigurationAccessService().getPartnerByPartnerId(
                    participantId );
            if ( partner != null ) {
                nxPartnerId = partner.getNxPartnerId();
            }
        }
        if ( choreographyId != null ) {
            ChoreographyPojo choreography = Engine.getInstance().getActiveConfigurationAccessService()
                    .getChoreographyByChoreographyId( choreographyId );
            if ( choreography != null ) {
                nxChoreographyId = choreography.getNxChoreographyId();
            }
        }

        if ( searchFor != null && searchFor.equals( "message" ) ) {

            MessagePojo message = new MessagePojo();

            items = Engine.getInstance().getTransactionService().getMessagesCount( status, nxChoreographyId,
                    nxPartnerId, conversationId, messageId, getStartDate( form ), getEndDate( form ) );

            form.setAllItemsCount( items );
            List reportMessages = null;
            if ( dir == null || dir.equals( "" ) || dir.equals( "first" ) || dir.equals( "transaction" ) ) {
                int pos = form.getStartCount();
                if ( pos == 0 || !dir.equals( "transaction" ) ) {
                    reportMessages = Engine.getInstance().getTransactionService().getMessagesForReport( status,
                            nxChoreographyId, nxPartnerId, conversationId, messageId, null, getStartDate( form ),
                            getEndDate( form ), form.getPageSize(), 0, TransactionDAO.SORT_CREATED, false );
                    if ( items > 0 ) {
                        form.setStartCount( 1 );
                    } else {
                        form.setStartCount( 0 );
                        form.setEndCount( 0 );
                    }
                    if ( items > form.getPageSize() ) {
                        form.setEndCount( form.getPageSize() );
                    } else {
                        form.setEndCount( items );
                    }
                } else {
                    int page = pos / form.getPageSize();

                    reportMessages = Engine.getInstance().getTransactionService().getMessagesForReport( status,
                            nxChoreographyId, nxPartnerId, conversationId, messageId, null, getStartDate( form ),
                            getEndDate( form ), form.getPageSize(), page, TransactionDAO.SORT_CREATED, false );

                    if ( form.getStartCount() + form.getPageSize() > items ) {
                        form.setEndCount( items );
                    } else {
                        form.setEndCount( form.getStartCount() + form.getPageSize() - 1 );
                    }
                }
            } else if ( dir.equals( "back" ) ) {
                int pos = form.getStartCount();
                if ( pos < form.getPageSize() ) {
                    reportMessages = Engine.getInstance().getTransactionService().getMessagesForReport( status,
                            nxChoreographyId, nxPartnerId, conversationId, messageId, null, getStartDate( form ),
                            getEndDate( form ), form.getPageSize(), 0, TransactionDAO.SORT_CREATED, false );

                    form.setStartCount( 1 );
                    form.setEndCount( form.getPageSize() );
                } else {

                    reportMessages = Engine.getInstance().getTransactionService().getMessagesForReport( status,
                            nxChoreographyId, nxPartnerId, conversationId, messageId, null, getStartDate( form ),
                            getEndDate( form ), form.getPageSize(), ( pos / form.getPageSize() ) - 1,
                            TransactionDAO.SORT_CREATED, false );

                    form.setStartCount( pos - form.getPageSize() );
                    form.setEndCount( form.getStartCount() + form.getPageSize() - 1 );
                }
            } else if ( dir.equals( "next" ) ) {
                int pos = form.getStartCount();
                if ( pos + 2 * form.getPageSize() >= items ) {

                    reportMessages = Engine.getInstance().getTransactionService().getMessagesForReport( status,
                            nxChoreographyId, nxPartnerId, conversationId, messageId, null, getStartDate( form ),
                            getEndDate( form ), form.getPageSize(), pos / form.getPageSize() + 1,
                            TransactionDAO.SORT_CREATED, false );

                    form.setStartCount( form.getStartCount() + form.getPageSize() );
                    form.setEndCount( items );

                } else {
                    int page = pos / form.getPageSize();

                    reportMessages = Engine.getInstance().getTransactionService().getMessagesForReport( status,
                            nxChoreographyId, nxPartnerId, conversationId, messageId, null, getStartDate( form ),
                            getEndDate( form ), form.getPageSize(), page + 1, TransactionDAO.SORT_CREATED, false );

                    form.setStartCount( form.getStartCount() + form.getPageSize() );
                    form.setEndCount( form.getStartCount() + form.getPageSize() - 1 );
                }
            } else if ( dir.equals( "last" ) ) {

                reportMessages = Engine.getInstance().getTransactionService().getMessagesForReport( status,
                        nxChoreographyId, nxPartnerId, conversationId, messageId, null, getStartDate( form ),
                        getEndDate( form ), form.getPageSize(), items / form.getPageSize(),
                        TransactionDAO.SORT_CREATED, false );

                form.setStartCount( items / form.getPageSize() * form.getPageSize() + 1 );
                form.setEndCount( items );
            }
            if ( reportMessages != null ) {
                Iterator i = reportMessages.iterator();
                while ( i.hasNext() ) {
                    MessagePojo pojo = (MessagePojo) i.next();
                    ReportMessageEntryForm entry = new ReportMessageEntryForm();
                    entry.setMessageProperties( pojo );
                    entry.setTimezone( timezone );
                    logItems.add( entry );

                }
            }

        } else {

            ConversationPojo conv = new ConversationPojo();

            items = Engine.getInstance().getTransactionService().getConversationsCount( status, nxChoreographyId,
                    nxPartnerId, conversationId, getStartDate( form ), getEndDate( form ), TransactionDAO.SORT_CREATED,
                    false );

            form.setAllItemsCount( items );
            List<ConversationPojo> conversations = null;
            if ( dir == null || dir.equals( "" ) || dir.equals( "first" ) || dir.equals( "transaction" ) ) {
                int pos = form.getStartCount();
                if ( pos == 0 || !"transaction".equals( dir ) ) {

                    conversations = Engine.getInstance().getTransactionService().getConversationsForReport( status,
                            nxChoreographyId, nxPartnerId, conversationId, getStartDate( form ), getEndDate( form ),
                            form.getPageSize(), 0, TransactionDAO.SORT_CREATED, false );
                    //LOG.trace("nxpartnerid: "+conversations.get( 0 ).getPartner().getNxPartnerId());
                    if ( items > 0 ) {
                        form.setStartCount( 1 );
                    } else {
                        form.setStartCount( 0 );
                        form.setEndCount( 0 );
                    }
                    if ( items > form.getPageSize() ) {
                        form.setEndCount( form.getPageSize() );
                    } else {
                        form.setEndCount( items );
                    }
                } else {
                    int page = pos / form.getPageSize();

                    conversations = Engine.getInstance().getTransactionService().getConversationsForReport( status,
                            nxChoreographyId, nxPartnerId, conversationId, getStartDate( form ), getEndDate( form ),
                            form.getPageSize(), page, TransactionDAO.SORT_CREATED, false );

                    if ( form.getStartCount() + form.getPageSize() > items ) {
                        form.setEndCount( items );
                    } else {
                        form.setEndCount( form.getStartCount() + form.getPageSize() - 1 );
                    }
                }
            } else if ( "back".equals( dir ) ) {
                int pos = form.getStartCount();
                if ( pos < form.getPageSize() ) {

                    conversations = Engine.getInstance().getTransactionService().getConversationsForReport( status,
                            nxChoreographyId, nxPartnerId, conversationId, getStartDate( form ), getEndDate( form ),
                            form.getPageSize(), 0, TransactionDAO.SORT_CREATED, false );

                    form.setStartCount( 1 );
                    form.setEndCount( form.getPageSize() );
                } else {

                    conversations = Engine.getInstance().getTransactionService().getConversationsForReport( status,
                            nxChoreographyId, nxPartnerId, conversationId, getStartDate( form ), getEndDate( form ),
                            form.getPageSize(), ( pos / form.getPageSize() ) - 1, TransactionDAO.SORT_CREATED, false );

                    form.setStartCount( pos - form.getPageSize() );
                    form.setEndCount( form.getStartCount() + form.getPageSize() - 1 );
                }
            } else if ( "next".equals( dir ) ) {
                int pos = form.getStartCount();
                if ( pos + 2 * form.getPageSize() >= items ) {

                    conversations = Engine.getInstance().getTransactionService().getConversationsForReport( status,
                            nxChoreographyId, nxPartnerId, conversationId, getStartDate( form ), getEndDate( form ),
                            form.getPageSize(), pos / form.getPageSize() + 1, TransactionDAO.SORT_CREATED, false );

                    form.setStartCount( form.getStartCount() + form.getPageSize() );
                    form.setEndCount( items );

                } else {
                    int page = pos / form.getPageSize();

                    conversations = Engine.getInstance().getTransactionService().getConversationsForReport( status,
                            nxChoreographyId, nxPartnerId, conversationId, getStartDate( form ), getEndDate( form ),
                            form.getPageSize(), page + 1, TransactionDAO.SORT_CREATED, false );

                    form.setStartCount( form.getStartCount() + form.getPageSize() );
                    form.setEndCount( form.getStartCount() + form.getPageSize() - 1 );
                }
            } else if ( "last".equals( dir ) ) {

                conversations = Engine.getInstance().getTransactionService().getConversationsForReport( status,
                        nxChoreographyId, nxPartnerId, conversationId, getStartDate( form ), getEndDate( form ),
                        form.getPageSize(), items / form.getPageSize(), TransactionDAO.SORT_CREATED, false );

                form.setStartCount( items / form.getPageSize() * form.getPageSize() + 1 );
                form.setEndCount( items );
            }
            if ( conversations != null ) {
                Iterator i = conversations.iterator();
                while ( i.hasNext() ) {
                    ConversationPojo pojo = (ConversationPojo) i.next();
                    ReportConversationEntryForm entry = new ReportConversationEntryForm();
                    entry.setValues( pojo );
                    entry.setTimezone( timezone );
                    logItems.add( entry );

                }
            }
        }
        if ( form.getStartCount() > 1 ) {
            form.setFirstActive( true );
            form.setPrevActive( true );
        } else {
            form.setFirstActive( false );
            form.setPrevActive( false );
        }
        if ( items > form.getEndCount() ) {
            form.setNextActive( true );
            form.setLastActive( true );
        } else {
            form.setNextActive( false );
            form.setLastActive( false );
        }
        // LOG.trace( "process.selected:" + form.isConvColSelect() );
        request.setAttribute( ATTRIBUTE_COLLECTION, logItems );

        return success;
    }

    /**
     * @param form
     * @return
     */
    private Date getEndDate( ReportingPropertiesForm form ) {

        if ( !form.isEndEnabled() ) {
            return null;
        }

        Calendar startCal = Calendar.getInstance();
        startCal.set( Calendar.YEAR, Integer.parseInt( form.getEndYear() ) );
        startCal.set( Calendar.MONTH, Integer.parseInt( form.getEndMonth() ) - 1 );
        startCal.set( Calendar.DAY_OF_MONTH, Integer.parseInt( form.getEndDay() ) );
        startCal.set( Calendar.HOUR_OF_DAY, Integer.parseInt( form.getEndHour() ) );
        startCal.set( Calendar.MINUTE, Integer.parseInt( form.getEndMin() ) );
        startCal.set( Calendar.SECOND, 0 );
        startCal.set( Calendar.MILLISECOND, 0 );

        return startCal.getTime();
    }

    /**
     * @param form
     * @return
     */
    private Date getStartDate( ReportingPropertiesForm form ) {

        if ( !form.isStartEnabled() ) {
            return null;
        }
        Calendar startCal = Calendar.getInstance();
        startCal.set( Calendar.YEAR, Integer.parseInt( form.getStartYear() ) );
        startCal.set( Calendar.MONTH, Integer.parseInt( form.getStartMonth() ) - 1 );
        startCal.set( Calendar.DAY_OF_MONTH, Integer.parseInt( form.getStartDay() ) );
        startCal.set( Calendar.HOUR_OF_DAY, Integer.parseInt( form.getStartHour() ) );
        startCal.set( Calendar.MINUTE, Integer.parseInt( form.getStartMin() ) );
        startCal.set( Calendar.SECOND, 0 );
        startCal.set( Calendar.MILLISECOND, 0 );

        return startCal.getTime();
    }

}
