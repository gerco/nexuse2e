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
