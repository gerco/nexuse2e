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
package org.nexuse2e.service;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.Constants.BeanStatus;
import org.nexuse2e.Constants.Layer;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.Constants.ParameterType;

/**
 * @author gesch
 *
 */
public class DBCleanupService extends AbstractService implements SchedulerClient {

    private static Logger     LOG               = Logger.getLogger( DBCleanupService.class );

//    public static String      DATABASESERVICE   = "databasename";
    public static String      SCHEDULINGSERVICE = "schedulingname";
    public static String      PURGEMESSAGES     = "purgemessages";
    public static String      PURGELOGS         = "purgelogs";
    public static String      TIMEPATTERN       = "timepattern";
    public static String      DAYSREMAINING     = "daysremaining";
    public static String      DEBUG             = "debug";

    
//    protected DatabaseService   dbService         = null;
    protected SchedulingService schedulingService = null;
    protected Boolean            purgeMessages      = false;
    protected Boolean            purgeLogs            = false;
    protected Boolean            debug            = true;
    protected String            timepattern         = null;
    protected int               daysremaining          = 90;

    @Override
    public void fillParameterMap( Map<String, ParameterDescriptor> parameterMap ) {

//        parameterMap.put( DATABASESERVICE, new ParameterDescriptor( ParameterType.SERVICE, "Database Service",
//                "The name of the service that shall be used for database connection pooling", DatabaseService.class ) );
        parameterMap.put( SCHEDULINGSERVICE, new ParameterDescriptor( ParameterType.SERVICE, "Scheduling Service",
                "The name of the service that shall be used for time schedule", SchedulingService.class ) );
        parameterMap.put( PURGEMESSAGES, new ParameterDescriptor( ParameterType.BOOLEAN, "Purge Messages",
                "remove old messages, conversations and payloads from database", Boolean.FALSE ) );
        parameterMap.put( PURGELOGS, new ParameterDescriptor( ParameterType.BOOLEAN, "Purge Logs",
                "remove old log entries", Boolean.FALSE ) );
        parameterMap.put( TIMEPATTERN, new ParameterDescriptor( ParameterType.STRING, "Time pattern",
                "cron based time pattern e.g. 0 0/5 * * * ?", "" ) );
        parameterMap.put( DAYSREMAINING, new ParameterDescriptor( ParameterType.STRING, "Days remaining",
                "data within this days is not purged", "90" ) );
        parameterMap.put( DEBUG, new ParameterDescriptor( ParameterType.BOOLEAN, "Debug Only",
                "Data is not removed. Only a detailed log output is generated", Boolean.TRUE ) );
    }

    @Override
    public Layer getActivationLayer() {

        return Layer.INTERFACES;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.service.AbstractService#start()
     */
    @Override
    public void start() {

        LOG.trace( "starting" );
        try {
            ( (SchedulingService) schedulingService ).registerClient( this, timepattern );
            super.start();
        } catch ( Throwable e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.service.AbstractService#stop()
     */
    @Override
    public void stop() {

        LOG.trace( "stopping" );
        if ( schedulingService != null ) {
            schedulingService.deregisterClient( this );
        } else {
            LOG.error( "no scheduling service configured!" );
        }
        super.stop();
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.service.AbstractService#initialize(org.nexuse2e.configuration.EngineConfiguration)
     */
    @Override
    public void initialize( EngineConfiguration config ) throws InstantiationException {

        LOG.trace( "initializing" );
        String schedulingServiceName = getParameter( SCHEDULINGSERVICE );
        purgeMessages = getParameter( PURGEMESSAGES );
        purgeLogs = getParameter( PURGELOGS );
        timepattern = getParameter( TIMEPATTERN );
        daysremaining = Integer.parseInt((String) getParameter( DAYSREMAINING ) );
        debug = getParameter( DEBUG );

        if ( !StringUtils.isEmpty( schedulingServiceName ) ) {

            Service service = Engine.getInstance().getActiveConfigurationAccessService().getService(
                    schedulingServiceName );
            if ( service == null ) {
                status = BeanStatus.ERROR;
                LOG.error( "Service not found in configuration: " + schedulingServiceName );
                return;
            }
            if ( !( service instanceof SchedulingService ) ) {
                status = BeanStatus.ERROR;
                LOG.error( schedulingServiceName + " is instance of " + service.getClass().getName()
                        + " but SchedulingService is required" );
                return;
            }
            schedulingService = (SchedulingService) service;

        } else {
            status = BeanStatus.ERROR;
            LOG.error( "SchedulingService is not properly configured (schedulingServiceObj == null)!" );
            return;
        }
        super.initialize( config );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.service.AbstractService#teardown()
     */
    @Override
    public void teardown() {

        LOG.trace( "teardown" );
        schedulingService = null;
        super.teardown();
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.service.SchedulerClient#scheduleNotify()
     */
    public void scheduleNotify() {

        if ( status == BeanStatus.STARTED ) {
            boolean isPrimary = Engine.getInstance().getEngineController().getEngineControllerStub().isPrimaryNode() ;
            LOG.debug( "is primary Node: "+isPrimary);
            
            if(isPrimary) {
            
                LOG.debug( "DebugMode: "+debug );
                Calendar cal = Calendar.getInstance();
                cal.add( Calendar.DAY_OF_YEAR, (-1)*daysremaining );
                
                Date endDate = cal.getTime();
                long logCount = -1;
                long convCount = -1;
                LOG.debug( "Remaining data: "+endDate );
                if(purgeLogs) {
                    
                    // for automated pruge the start date is unlimited and end date is (now - days remaining)
                    try {
                        logCount =  Engine.getInstance().getTransactionDAO().getLogCount(null,endDate,null,null);
                        LOG.debug( "Log entries to purge: "+logCount );
                    } catch ( NexusException e ) {
                        e.printStackTrace();
                    }
                }
                if(purgeMessages) {
                    try {
                        convCount =   Engine.getInstance().getTransactionDAO().getConversationsCount( null, endDate, null, null );
                        LOG.debug( "Conversations to purge: "+convCount );
                    } catch ( NexusException e ) {
                        e.printStackTrace();
                    }    
                }
                
                if(!debug) {
                    try {
                        if(purgeMessages) {
                            LOG.debug( "purging selected messages" );
                            Session session = Engine.getInstance().getTransactionService().getDBSession();
                            Transaction transaction = session.beginTransaction();
                            
                            try {
                                if ( convCount > 0 ) {
                                        Engine.getInstance().getTransactionDAO().removeConversations( null, endDate, session, transaction );
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
                        if(purgeLogs) {
                            LOG.debug( "purging selected log entries" );
                            Session session = Engine.getInstance().getTransactionService().getDBSession();
                            Transaction transaction = session.beginTransaction();
                            try {
                                if ( logCount > 0 ) {
                                    Engine.getInstance().getTransactionDAO().removeLogEntries( null, endDate, session, transaction );
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
                    } catch ( HibernateException e ) {
                        e.printStackTrace();
                    } catch ( NexusException e ) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        LOG.info( "---------- Database Cleanup ------------" );
                        LOG.info( "Remaining Date: "+ endDate);
                        LOG.info("Purge Logs:"+purgeLogs);
                        if(purgeLogs){
                            LOG.info( "Log entries to purge: "+logCount );
                        }
                        LOG.info("Purge Messages:"+purgeMessages);
                        if(purgeMessages){
                            LOG.info( "Conversations to purge: "+convCount );
                        }
                        LOG.info( "----------------------------------------" );
                    } catch ( Exception e ) {
                        e.printStackTrace();
                    }
                }
            
            }
        }
    }

}
