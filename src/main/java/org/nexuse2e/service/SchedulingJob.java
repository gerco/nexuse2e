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
package org.nexuse2e.service;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;


/**
 * @author gesch
 *
 */
public class SchedulingJob implements Job {
    
    private static Logger LOG = Logger.getLogger( SchedulingJob.class );

    /* (non-Javadoc)
     * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
     */
    public void execute( JobExecutionContext jobExecutionContext ) throws JobExecutionException {

        LOG.debug( "firing job: "+jobExecutionContext.getJobDetail().getName() );
        SchedulerClient client = (SchedulerClient)jobExecutionContext.getMergedJobDataMap().get( "client" );        
        if ( client != null ) {
            client.scheduleNotify();
            LOG.debug("next Schedule Date: "+jobExecutionContext.getTrigger().getNextFireTime());
        } else {
            LOG.error( "SchedulerClient must not benull" );
        }
    }
    
}
