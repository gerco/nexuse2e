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

package org.nexuse2e;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.nexuse2e.Constants.BeanStatus;
import org.nexuse2e.StatusSummary.Status;
import org.nexuse2e.dao.ConfigDAO;
import org.nexuse2e.pojo.TRPPojo;

/**
 * @author gesch
 *
 */
public class EngineMonitor {

    private static Logger               LOG               = Logger.getLogger( EngineMonitor.class );

    private List<EngineMonitorListener> listeners;
    private Timer                       timer;
    private boolean                     shutdownInitiated = false;

    /**
     * 
     */
    public void start() {

        LOG.debug( "Engine monitor initalized" );
        timer = new Timer();
        TestSuite suite = new TestSuite();
        timer.schedule( suite, 0, 10000 );
    }

    /**
     * 
     */
    public void stop() {

        timer.cancel();
    }

    /**
     * @return
     */
    public EngineStatusSummary getStatus() {

        return new EngineStatusSummary();
    }

    /**
     * @param summary
     * @return
     */
    public EngineStatusSummary filloutStatusSummary( EngineStatusSummary summary ) {

        if ( Engine.getInstance().getStatus() == BeanStatus.STARTED ) {
            summary.setStatus( Status.ACTIVE );
        } else {
            summary.setStatus( Status.INACTIVE );
        }
        return summary;
    }

    /**
     * @param listner
     */
    public void addListener( EngineMonitorListener listener ) {

        if ( listeners == null ) {
            listeners = new ArrayList<EngineMonitorListener>();
        }
        listeners.add( listener );
    }

    public void removeListener( EngineMonitorListener listener ) {

        if ( listeners != null ) {
            listeners.remove( listener );
        }
    }

    private synchronized EngineStatusSummary probe() {

        EngineStatusSummary summary = new EngineStatusSummary();

        // LOG.debug( "EngineMonitor probing..." );
        ConfigDAO configDao = null;
        try {
            configDao = (ConfigDAO) Engine.getInstance().getDao( "configDao" );
        } catch ( Exception e ) {
            summary.setCause( "Error while searching configDao: " + e );
            summary.setStatus( Status.ERROR );
            return summary;
        }
        List<TRPPojo> trps = null;
        try {
            trps = configDao.getTrps( null, null );
        } catch ( Exception e ) {
            summary.setCause( "Error while fetching testdata from database: " + e );
            summary.setDatabaseStatus( Status.ERROR );
            summary.setStatus( Status.ERROR );
            return summary;
        } catch ( Error e ) {
            System.out.println( "Error: " + e );
        }
        summary.setDatabaseStatus( Status.ACTIVE );
        if ( trps == null || trps.size() == 0 ) {

            summary.setCause( "no TRP's found in database" );
            summary.setStatus( Status.ERROR );
            return summary;
        }
        if ( Engine.getInstance().getStatus() == BeanStatus.STARTED ) {
            summary.setStatus( Status.ACTIVE );
        } else {
            summary.setStatus( Status.INACTIVE );
        }

        return summary;
    }

    /**
     * @author gesch
     *
     */
    public class TestSuite extends TimerTask {

        /**
         * 
         */
        public TestSuite() {

        }

        @Override
        public void run() {

            try {
                EngineStatusSummary summary = probe();
                if ( summary.getStatus() == Status.ERROR ) {
                    try {
                        shutdownInitiated = true;
                        Engine.getInstance().changeStatus( BeanStatus.INSTANTIATED );
                        LOG.info( "Engine shutdown triggered" );
                    } catch ( InstantiationException e ) {
                        LOG.error( "Error while handling error: (cause: " + summary.getCause() + "): " + e );
                    }
                } else if(shutdownInitiated) {
                    if(Engine.getInstance().getStatus().equals( BeanStatus.INSTANTIATED )) {
                        shutdownInitiated = false;
                        Engine.getInstance().changeStatus( BeanStatus.STARTED );
                        LOG.info( "Engine startup triggered" );
                    }
                } else {
                    shutdownInitiated = false;
                }
                if ( listeners != null ) {

                    for ( EngineMonitorListener listener : listeners ) {
                        EngineStatusSummary specialSummary = listener.getSummaryInstance();
                        specialSummary.update(summary);
                        listener.engineEvent( specialSummary );
                    }
                }
            } catch ( Exception e ) {
                System.out.println( "Monitoring: " + e );
                e.printStackTrace();
            }
        }

    }
}
