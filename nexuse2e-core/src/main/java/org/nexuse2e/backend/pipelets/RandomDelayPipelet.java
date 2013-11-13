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
package org.nexuse2e.backend.pipelets;

import org.apache.log4j.Logger;
import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.ParameterType;
import org.nexuse2e.messaging.AbstractPipelet;
import org.nexuse2e.messaging.MessageContext;

/**
 * This pipelet creates a random time period from a user-defined range and blocks
 * the processing thread for that time period.
 * <p>
 * Can be used in order to detect race conditions or if a delay is required for other
 * reasons (e.g., load balancing or timing behavior in third-party systems).
 *
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public class RandomDelayPipelet extends AbstractPipelet {

    private static Logger LOG = Logger.getLogger( RandomDelayPipelet.class );
    
    
    public static final String MIN_TIME_PARAM_NAME = "minDelay";
    public static final String MAX_TIME_PARAM_NAME = "maxDelay";

    
    public RandomDelayPipelet() {
        parameterMap.put( MIN_TIME_PARAM_NAME, new ParameterDescriptor( ParameterType.STRING, "Minimum delay",
                "The minimum delay in milliseconds", "0" ) );
        parameterMap.put( MAX_TIME_PARAM_NAME, new ParameterDescriptor( ParameterType.STRING, "Maximum delay",
                "The maximum delay in milliseconds", "1000" ) );
    }
    
    @Override
    public MessageContext processMessage( MessageContext messageContext )
    throws IllegalArgumentException, IllegalStateException, NexusException {

        long minDelay = 0;
        try {
            minDelay = Long.parseLong( (String) getParameter( MIN_TIME_PARAM_NAME ) );
        } catch (NumberFormatException nfe) {
            LOG.warn( "'minimum delay' parameter is invalid, using default (" + minDelay + ")" );
        }
        long maxDelay = 1000;
        try {
            maxDelay = Long.parseLong( (String) getParameter( MAX_TIME_PARAM_NAME ) );
        } catch (NumberFormatException nfe) {
            LOG.warn( "'maximum delay' parameter is invalid, using default (" + maxDelay + ")" );
        }
        
        long delay = (long) (Math.random() * (maxDelay - minDelay));
        delay += minDelay;

        try {
            Thread.sleep( delay );
        } catch (InterruptedException ignored) {
        }

        return messageContext;
    }

}
