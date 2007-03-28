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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.nexuse2e.Engine;
import org.nexuse2e.dao.LogDAO;
import org.nexuse2e.pojo.LogPojo;
import org.nexuse2e.ui.action.NexusE2EAction;
import org.nexuse2e.ui.form.ReportEngineEntryForm;
import org.nexuse2e.ui.form.ReportingPropertiesForm;

public class ProcessEngineLogAction extends NexusE2EAction {

    private static final String VERSIONSTRING = "$Id: ProcessEngineLogAction.java 967 2005-08-26 13:24:08Z markus.breilmann $";

    private static String       URL           = "reporting.error.url";

    private static String       TIMEOUT       = "reporting.error.timeout";

    /* (non-Javadoc)
     * @see org.nexuse2e.ui.action.NexusE2EAction#executeNexusE2EAction(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.apache.struts.action.ActionMessages, org.apache.struts.action.ActionMessages)
     */
    @Override
    public ActionForward executeNexusE2EAction( ActionMapping actionMapping, ActionForm actionForm,
            HttpServletRequest request, HttpServletResponse response, ActionMessages errors, ActionMessages messages )
            throws Exception {

        List<ReportEngineEntryForm> logItems = new Vector<ReportEngineEntryForm>();

        ActionForward success = actionMapping.findForward( ACTION_FORWARD_SUCCESS );
        ActionForward error = actionMapping.findForward( ACTION_FORWARD_FAILURE );

        ReportingPropertiesForm form = (ReportingPropertiesForm) actionForm;

        form.setEngineColSeverity( true );
        form.setEngineColIssued( true );
        form.setEngineColDescription( true );
        /*
         ConfigFieldDAO cfDao = new ConfigFieldDAO();

         HashMap configFieldMap = new HashMap<K, V>();

         List configFields = cfDao.getConfigFieldsByChoreographyIdAndComponentId( "#ENGINE#", "Reporting" );

         for ( Iterator<E> iter = configFields.iterator(); iter.hasNext(); ) {
         ConfigFieldPojo configFieldPojo = (ConfigFieldPojo) iter.next();
         configFieldMap.put( configFieldPojo.getConfigFieldKey().getName(), configFieldPojo );
         }

         ConfigFieldPojo field = (ConfigFieldPojo) configFieldMap.get( "engineColSeverity" );

         // field = cfDao.getConfigFieldByChoreographyIdComponentIdAndName( "#ENGINE#", "Reporting", "engineColSeverity" );

         if ( field != null ) {
         form.setEngineColSeverity( Boolean.valueOf( field.getValue() ).booleanValue() );
         }

         field = (ConfigFieldPojo) configFieldMap.get( "engineColIssued" );
         // field = cfDao.getConfigFieldByChoreographyIdComponentIdAndName( "#ENGINE#", "Reporting", "engineColIssued" );
         if ( field != null ) {
         form.setEngineColIssued( Boolean.valueOf( field.getValue() ).booleanValue() );
         }

         field = (ConfigFieldPojo) configFieldMap.get( "engineColDescription" );
         // field = cfDao.getConfigFieldByChoreographyIdComponentIdAndName( "#ENGINE#", "Reporting", "engineColDescription" );
         if ( field != null ) {
         form.setEngineColDescription( Boolean.valueOf( field.getValue() ).booleanValue() );
         }

         field = (ConfigFieldPojo) configFieldMap.get( "engineColOrigin" );
         // field = cfDao.getConfigFieldByChoreographyIdComponentIdAndName( "#ENGINE#", "Reporting", "engineColOrigin" );
         if ( field != null ) {
         form.setEngineColOrigin( Boolean.valueOf( field.getValue() ).booleanValue() );
         }

         field = (ConfigFieldPojo) configFieldMap.get( "engineColClassName" );
         // field = cfDao.getConfigFieldByChoreographyIdComponentIdAndName( "#ENGINE#", "Reporting", "engineColClassName" );
         if ( field != null ) {
         form.setEngineColClassName( Boolean.valueOf( field.getValue() ).booleanValue() );
         }

         field = (ConfigFieldPojo) configFieldMap.get( "engineColmethodName" );
         // field = cfDao.getConfigFieldByChoreographyIdComponentIdAndName( "#ENGINE#", "Reporting", "engineColmethodName" );
         if ( field != null ) {
         form.setEngineColmethodName( Boolean.valueOf( field.getValue() ).booleanValue() );
         }
         */

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
            if ( pos == 0 || !dir.equals( "engine" ) ) {
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
            reportMessages = Engine.getInstance().getTransactionService().getLogEntriesForReport( severity,
                    messageText, startDate, endDate, form.getPageSize(), items / form.getPageSize(),
                    LogDAO.SORT_CREATED, false );
            form.setStartCount( items / form.getPageSize() * form.getPageSize() + 1 );
            form.setEndCount( items );
        }

        if ( reportMessages != null ) {

            Iterator<LogPojo> i = reportMessages.iterator();

            while ( i.hasNext() ) {
                ReportEngineEntryForm entry = new ReportEngineEntryForm();
                entry.setEnginePorperties( i.next() );
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

        request.setAttribute( ATTRIBUTE_COLLECTION, logItems );

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
