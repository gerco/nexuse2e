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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.nexuse2e.Constants.Runlevel;
import org.nexuse2e.configuration.ParameterDescriptor;

/**
 * @author gesch
 *
 * 
 */
public class SchedulingServiceImpl extends AbstractService implements SchedulingService {

    private static Logger                                      LOG        = Logger
                                                                                  .getLogger( SchedulingServiceImpl.class );

    private HashMap<SchedulerClient, ScheduledFuture>          clients    = new HashMap<SchedulerClient, ScheduledFuture>();
    private HashMap<SchedulerClient, ScheduledExecutorService> schedulers = new HashMap<SchedulerClient, ScheduledExecutorService>();

    /* (non-Javadoc)
     * @see org.nexuse2e.service.AbstractService#start()
     */
    @Override
    public void start() {

        LOG.trace( "starting" );
        super.start();
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.service.AbstractService#stop()
     */
    @Override
    public void stop() {

        LOG.trace( "stopping" );
        super.stop();
    }

    @Override
    public void fillParameterMap( Map<String, ParameterDescriptor> parameterMap ) {

    }

    @Override
    public Runlevel getActivationRunlevel() {

        return Runlevel.CORE;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.service.SchedulingService#registerClient(org.nexuse2e.service.SchedulerClient, long)
     */
    public void registerClient( SchedulerClient client, long millseconds ) throws IllegalArgumentException {

        LOG.trace( "registerClient" );
        LOG.debug( "client: " + client + " Interval: " + millseconds + " millseconds" );

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool( 1 );

        SchedulingThread thread = new SchedulingThread( client );
        ScheduledFuture<?> handle = scheduler.scheduleAtFixedRate( thread, 0, millseconds, TimeUnit.MILLISECONDS );

        clients.put( client, handle );
        schedulers.put( client, scheduler );

    }

    /* (non-Javadoc)
     * @see org.nexuse2e.service.SchedulingService#deregisterClient(org.nexuse2e.service.SchedulerClient)
     */
    public void deregisterClient( SchedulerClient client ) throws IllegalArgumentException {

        LOG.trace( "deregistering client" );
        ScheduledFuture handle = clients.get( client );
        if ( handle != null ) {
            handle.cancel( false );
            LOG.debug( "deregisterClient - processing cancelled!" );
            try {
                ScheduledExecutorService scheduler = schedulers.remove( client );
                if ( scheduler != null ) {
                    LOG.debug( "Shutting down scheduler..." );
                    scheduler.shutdownNow();
                }
            } catch ( Exception e ) {
                e.printStackTrace();
            }
            clients.remove( client );
        }
    }

    /**
     * @author gesch
     *
     */
    public class SchedulingThread implements Runnable {

        private SchedulerClient client;

        /**
         * @param client
         */
        public SchedulingThread( SchedulerClient client ) {

            this.client = client;
        }

        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        public void run() {

            LOG.trace( "running" );
            if ( client != null ) {
                client.scheduleNotify();
            } else {
                LOG.error( "SchedulerClient == null" );
            }
        }

    }
}
