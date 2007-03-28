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

import org.apache.log4j.Logger;
import org.nexuse2e.Constants.Runlevel;
import org.nexuse2e.configuration.ParameterDescriptor;


/**
 * @author gesch
 *
 * 
 */
public class SchedulingService extends AbstractService {

    private static Logger LOG = Logger.getLogger( SchedulingService.class );
        
    /* (non-Javadoc)
     * @see org.nexuse2e.service.AbstractService#start()
     */
    @Override
    public void start() {
        
        super.start();
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.service.AbstractService#stop()
     */
    @Override
    public void stop() {

        super.stop();
    }

    @Override
    public void fillParameterMap( Map<String, ParameterDescriptor> parameterMap ) {
        
    }

    @Override
    public Runlevel getActivationRunlevel() {

        return Runlevel.CORE;
    }
    
    /**
     * @param client
     * @param millseconds
     * @throws IllegalArgumentException
     */
    public void registerClient(SchedulerClient client, long millseconds) throws IllegalArgumentException {
        
    }
    

}
