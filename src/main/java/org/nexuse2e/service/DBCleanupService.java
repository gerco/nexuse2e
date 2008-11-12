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

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.nexuse2e.Engine;
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

//    protected DatabaseService   dbService         = null;
    protected SchedulingService schedulingService = null;
    protected Boolean            purgeMessages      = false;
    protected Boolean            purgeLogs            = false;
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
        ( (SchedulingService) schedulingService ).registerClient( this, timepattern );
        super.start();
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

        // LOG.debug( "do something" );
        if ( status == BeanStatus.STARTED ) {
            LOG.debug( "is primary: "+Engine.getInstance().getEngineController().getEngineControllerStub().isPrimaryNode() );
        }
    }

}
