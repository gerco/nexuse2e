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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.nexuse2e.Engine;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.dao.LogDAO;
import org.nexuse2e.pojo.LogPojo;
import org.nexuse2e.ui.form.ReportEngineEntryForm;
import org.nexuse2e.ui.form.ReportingPropertiesForm;
import org.nexuse2e.ui.form.ReportingSettingsForm;

public class ProcessEngineLogAction extends ReportingAction {

    /* (non-Javadoc)
     * @see org.nexuse2e.ui.action.NexusE2EAction#executeNexusE2EAction(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.apache.struts.action.ActionMessages, org.apache.struts.action.ActionMessages)
     */
    @Override
    public ActionForward executeNexusE2EAction( ActionMapping actionMapping, ActionForm actionForm,
            HttpServletRequest request, HttpServletResponse response, EngineConfiguration engineConfiguration, ActionMessages errors, ActionMessages messages )
            throws Exception {

        List<ReportEngineEntryForm> logItems = new Vector<ReportEngineEntryForm>();

        ActionForward success = actionMapping.findForward( ACTION_FORWARD_SUCCESS );
        try {
            ReportingPropertiesForm form = (ReportingPropertiesForm) actionForm;

            String dir = form.getCommand();
            String refresh = request.getParameter( "refresh" );
            if ( refresh != null ) {
                dir = "engine";
            }

            String severity = null;
            if ( form.getSeverity() != null && !form.getSeverity().equals( "" ) ) {
                severity = form.getSeverity();
            }

            String messageText = null;
            boolean messageTextActive = form.isMessageTextEnabled();
            if ( messageTextActive && form.getMessageText() != null && !form.getMessageText().equals( "" ) ) {
                messageText = form.getMessageText();
            }

            boolean startActive = form.isStartEnabled();
            boolean endActive = form.isEndEnabled();

            Date startDate = null;
            if ( startActive ) {
                startDate = getStartDate( form );
            }
            Date endDate = null;
            if ( endActive ) {
                endDate = getEndDate( form );
            }

            int items = 0;

            items = Engine.getInstance().getTransactionService().getLogEntriesForReportCount( severity, messageText,
                    startDate, endDate, LogDAO.SORT_CREATED, false );

            // LOG.trace( "items: " + items );
            // LOG.trace( "test: " + lDao.getLog().size() );

            form.setAllItemsCount( items );
            List<LogPojo> reportMessages = null;

            // LOG.trace( "dir:" + dir );
            // LOG.trace( "test:" + form );

            if ( dir == null || dir.equals( "" ) || dir.equals( "first" ) || dir.equals( "engine" ) ) {
                int pos = form.getStartCount();
                if ( pos == 0 || ( !"engine".equals( dir ) ) ) {
                    reportMessages = Engine.getInstance().getTransactionService().getLogEntriesForReport( severity,
                            messageText, startDate, endDate, form.getPageSize(), 0, LogDAO.SORT_CREATED, false );

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
                    reportMessages = Engine.getInstance().getTransactionService().getLogEntriesForReport( severity,
                            messageText, startDate, endDate, form.getPageSize(), page, LogDAO.SORT_CREATED, false );

                    if ( form.getStartCount() + form.getPageSize() > items ) {
                        form.setEndCount( items );
                    } else {
                        form.setEndCount( form.getStartCount() + form.getPageSize() - 1 );
                    }

                }

            } else if ( dir.equals( "back" ) ) {
                int pos = form.getStartCount();
                if ( pos < form.getPageSize() ) {
                    reportMessages = Engine.getInstance().getTransactionService().getLogEntriesForReport( severity,
                            messageText, startDate, endDate, form.getPageSize(), 0, LogDAO.SORT_CREATED, false );
                    form.setStartCount( 1 );
                    form.setEndCount( form.getPageSize() );
                } else {
                    reportMessages = Engine.getInstance().getTransactionService().getLogEntriesForReport( severity,
                            messageText, startDate, endDate, form.getPageSize(), ( pos / form.getPageSize() ) - 1,
                            LogDAO.SORT_CREATED, false );
                    form.setStartCount( pos - form.getPageSize() );
                    form.setEndCount( form.getStartCount() + form.getPageSize() - 1 );
                }

            } else if ( dir.equals( "next" ) ) {
                int pos = form.getStartCount();
                if ( pos + 2 * form.getPageSize() >= items ) {
                    reportMessages = Engine.getInstance().getTransactionService().getLogEntriesForReport( severity,
                            messageText, startDate, endDate, form.getPageSize(), pos / form.getPageSize() + 1,
                            LogDAO.SORT_CREATED, false );
                    form.setStartCount( form.getStartCount() + form.getPageSize() );
                    form.setEndCount( items );
                } else {
                    int page = pos / form.getPageSize();
                    reportMessages = Engine.getInstance().getTransactionService().getLogEntriesForReport( severity,
                            messageText, startDate, endDate, form.getPageSize(), page + 1, LogDAO.SORT_CREATED, false );
                    form.setStartCount( form.getStartCount() + form.getPageSize() );
                    form.setEndCount( form.getStartCount() + form.getPageSize() - 1 );
                }

            } else if ( dir.equals( "last" ) ) {
                int page = 0;
                if ( items > 0 ) {
                    if ( items % form.getPageSize() == 0 ) {
                        page = items / form.getPageSize() - 1;
                    } else {
                        page = items / form.getPageSize();
                    }
                }
                reportMessages = Engine.getInstance().getTransactionService().getLogEntriesForReport( severity,
                        messageText, startDate, endDate, form.getPageSize(), page,
                        LogDAO.SORT_CREATED, false );
                form.setStartCount( page * form.getPageSize() + 1 );
                form.setEndCount( items );
            }

            if ( reportMessages != null ) {

                for (LogPojo logPojo : reportMessages) {
                	    ReportEngineEntryForm entry = new ReportEngineEntryForm();
                        entry.setEnginePorperties( logPojo );
                        logItems.add( entry );
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

            ReportingSettingsForm reportingSettings = new ReportingSettingsForm();
            fillForm( engineConfiguration, reportingSettings, form );
            request.setAttribute( "reportingSettingsForm", reportingSettings );
            request.setAttribute( ATTRIBUTE_COLLECTION, logItems );

        } catch ( Exception e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch ( Error e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return success;
    } // executeNexusE2EAction

    /**
     * @param form
     * @return
     */
    private Date getEndDate( ReportingPropertiesForm form ) {

        Date startDate = null;
        String time = form.getEndYear() + form.getEndMonth() + form.getEndDay() + form.getEndHour() + form.getEndMin();
        SimpleDateFormat sdf = new SimpleDateFormat( "yyyyMMddhhmm" );
        try {
            startDate = sdf.parse( time );
        } catch ( ParseException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return startDate;
    }

    /**
     * @param form
     * @return
     */
    private Date getStartDate( ReportingPropertiesForm form ) {

        Date endDate = null;
        String time = form.getStartYear() + form.getStartMonth() + form.getStartDay() + form.getStartHour()
                + form.getStartMin();
        SimpleDateFormat sdf = new SimpleDateFormat( "yyyyMMddhhmm" );
        try {
            endDate = sdf.parse( time );
        } catch ( ParseException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return endDate;
    }
} // ProcessEngineLogAction
