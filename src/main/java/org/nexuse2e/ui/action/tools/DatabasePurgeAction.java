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
package org.nexuse2e.ui.action.tools;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.dao.TransactionDAO;
import org.nexuse2e.pojo.ConversationPojo;
import org.nexuse2e.pojo.LogPojo;
import org.nexuse2e.ui.action.NexusE2EAction;
import org.nexuse2e.ui.form.DatabasePurgeForm;


public class DatabasePurgeAction extends NexusE2EAction {

    private static Logger LOG = Logger.getLogger( DatabasePurgeAction.class );

    @Override
    public ActionForward executeNexusE2EAction( ActionMapping actionMapping, ActionForm actionForm,
            HttpServletRequest request, HttpServletResponse response, EngineConfiguration engineConfiguration, ActionMessages errors, ActionMessages messages )
            throws Exception {

        ActionForward success = actionMapping.findForward( ACTION_FORWARD_SUCCESS );
        DatabasePurgeForm dbForm = (DatabasePurgeForm) actionForm;
        LOG.debug( "Type: "+dbForm.getType() );
        if ( dbForm.getType().equals( "select" ) ) {
            LOG.debug( "nothing to do" );
            
            dbForm.setEndEnabled( true );
            
        } else if ( dbForm.getType().equals( "preview" ) ) {
            LOG.debug( "start: "+ dbForm.getStartDay()+"."+dbForm.getStartMonth()+"."+dbForm.getStartYear()+" "+dbForm.getStartHour()+":"+dbForm.getStartMin() );
            
            if(dbForm.isPurgeMessages()) {
                List<ConversationPojo> conversations = getConversationsByForm( dbForm, null, null);
                dbForm.setConvCount( conversations.size() );
                int messageCount = 0;
                for ( ConversationPojo pojo : conversations ) {
                    if(pojo.getMessages() != null) {
                        messageCount += pojo.getMessages().size();
                    }
                }
                dbForm.setMessageCount( messageCount );
                System.out.println("size: "+conversations.size());
            }
            if(dbForm.isPurgeLog()) {
                List<LogPojo> logEntries = getLogEntiesByForm( dbForm, null, null );
                if(logEntries != null) {
                    dbForm.setLogEntryCount( logEntries.size() );
                } else {
                    LOG.debug( "no log entries found" );
                    dbForm.setLogEntryCount( 0);
                }
            }
            
            
            LOG.debug( "preview purgeable data" );
        } else if ( dbForm.getType().equals( "remove" ) ) {
           
            LOG.debug( "removing data" );
                
            if(dbForm.isPurgeMessages()) {
                LOG.debug( "purging selected messages" );
                Session session = Engine.getInstance().getTransactionService().getDBSession();
                Transaction transaction = session.beginTransaction();
                
                try {
                    List<ConversationPojo> conversations = getConversationsByForm( dbForm, session, transaction );
                    if ( conversations != null && conversations.size() > 0 ) {
                        for ( ConversationPojo pojo : conversations ) {
                            Engine.getInstance().getTransactionService().deleteConversation( pojo, session, transaction );
                        }
                    }
                    transaction.commit();
                    Engine.getInstance().getTransactionService().releaseDBSession( session );
                } catch ( Exception e ) {
                    LOG.error( "Error while deleting conversations: "+e.getMessage()  );
                    transaction.rollback();
                    Engine.getInstance().getTransactionService().releaseDBSession( session );
                    e.printStackTrace();
                }    
            }
            if(dbForm.isPurgeLog()) {
                LOG.debug( "purging selected log entries" );
                Session session = Engine.getInstance().getTransactionService().getDBSession();
                Transaction transaction = session.beginTransaction();
                try {
                    List<LogPojo> logEnties = getLogEntiesByForm( dbForm, session, transaction );
                    if ( logEnties != null && logEnties.size() > 0 ) {
                        for ( LogPojo pojo : logEnties ) {
                            Engine.getInstance().getTransactionService().deleteLogEntry( pojo, session, transaction );
                        }
                    }
                    transaction.commit();
                    Engine.getInstance().getTransactionService().releaseDBSession( session );
                } catch ( Exception e ) {
                    LOG.error( "Error while deleting conversations: "+e.getMessage()  );
                    transaction.rollback();
                    Engine.getInstance().getTransactionService().releaseDBSession( session );
                    e.printStackTrace();
                }    
            }
             
           
            
        }

        return success;
    }
    private List<LogPojo> getLogEntiesByForm(DatabasePurgeForm form,Session session,Transaction transaction) throws NexusException{
        
        Date startDate = null;
        Date endDate = null;
        
        if(form.isStartEnabled()){
            Calendar start = Calendar.getInstance();
            start.set( Calendar.DAY_OF_MONTH, Integer.parseInt( form.getStartDay()) );
            start.set( Calendar.MONTH, Integer.parseInt( form.getStartMonth())-1 );
            start.set( Calendar.YEAR, Integer.parseInt( form.getStartYear()) );
            start.set( Calendar.HOUR_OF_DAY, Integer.parseInt( form.getStartHour()) );
            start.set( Calendar.MINUTE, Integer.parseInt( form.getStartMin()) );
            startDate = start.getTime();
        }
        if(form.isEndEnabled()){
            Calendar end = Calendar.getInstance();
            end.set( Calendar.DAY_OF_MONTH, Integer.parseInt( form.getEndDay()) );
            end.set( Calendar.MONTH, Integer.parseInt( form.getEndMonth())-1 );
            end.set( Calendar.YEAR, Integer.parseInt( form.getEndYear()) );
            end.set( Calendar.HOUR_OF_DAY, Integer.parseInt( form.getEndHour()) );
            end.set( Calendar.MINUTE, Integer.parseInt( form.getEndMin()) );
            endDate = end.getTime();
        }
        System.out.println("startdate("+form.isStartEnabled()+"): "+startDate);
        System.out.println("enddate("+form.isEndEnabled()+"): "+endDate);

        System.out.println("Messages: "+form.isPurgeMessages());
        System.out.println("LogEntries: "+form.isPurgeLog());
        
        return Engine.getInstance().getTransactionService().getLogEntriesForReport( null, null, startDate, endDate, 0, 0, TransactionDAO.SORT_NONE, false , session,transaction);
    }
    
    private List<ConversationPojo> getConversationsByForm(DatabasePurgeForm form,Session session,Transaction transaction) throws NexusException{
        Date startDate = null;
        Date endDate = null;
        
        if(form.isStartEnabled()){
            Calendar start = Calendar.getInstance();
            start.set( Calendar.DAY_OF_MONTH, Integer.parseInt( form.getStartDay()) );
            start.set( Calendar.MONTH, Integer.parseInt( form.getStartMonth())-1 );
            start.set( Calendar.YEAR, Integer.parseInt( form.getStartYear()) );
            start.set( Calendar.HOUR_OF_DAY, Integer.parseInt( form.getStartHour()) );
            start.set( Calendar.MINUTE, Integer.parseInt( form.getStartMin()) );
            startDate = start.getTime();
        }
        if(form.isEndEnabled()){
            Calendar end = Calendar.getInstance();
            end.set( Calendar.DAY_OF_MONTH, Integer.parseInt( form.getEndDay()) );
            end.set( Calendar.MONTH, Integer.parseInt( form.getEndMonth())-1 );
            end.set( Calendar.YEAR, Integer.parseInt( form.getEndYear()) );
            end.set( Calendar.HOUR_OF_DAY, Integer.parseInt( form.getEndHour()) );
            end.set( Calendar.MINUTE, Integer.parseInt( form.getEndMin()) );
            endDate = end.getTime();
        }
        System.out.println("startdate("+form.isStartEnabled()+"): "+startDate);
        System.out.println("enddate("+form.isEndEnabled()+"): "+endDate);

        System.out.println("Messages: "+form.isPurgeMessages());
        System.out.println("LogEntries: "+form.isPurgeLog());
        
        
        return Engine.getInstance().getTransactionService().getConversationsForReport( null,0, 0, null, startDate, endDate, 0, 0, TransactionDAO.SORT_NONE, false, session, transaction );
        
    }
}
