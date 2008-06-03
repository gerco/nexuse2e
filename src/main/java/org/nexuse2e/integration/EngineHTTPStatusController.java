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

package org.nexuse2e.integration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.nexuse2e.Engine;
import org.nexuse2e.EngineMonitor;
import org.nexuse2e.EngineStatusSummary;
import org.nexuse2e.StatusSummary.Status;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

/**
 * @author mbreilmann
 *
 */
public class EngineHTTPStatusController implements Controller {

    private static Logger LOG = Logger.getLogger( EngineHTTPStatusController.class );

    /* (non-Javadoc)
     * @see org.springframework.web.servlet.mvc.Controller#handleRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ModelAndView handleRequest( HttpServletRequest request, HttpServletResponse response ) throws Exception {

        try {
            EngineMonitor engineMonitor = Engine.getInstance().getEngineController().getEngineMonitor();
            EngineStatusSummary engineStatusSummary = engineMonitor.getStatus();

            if ( ( engineStatusSummary != null ) && engineStatusSummary.getStatus().equals( Status.ACTIVE ) ) {
                response.setStatus( HttpServletResponse.SC_OK );
                response.getWriter().print( "NEXUSe2e active" );
                return null;
            }
        } catch ( Exception e ) {
            LOG.error( "Error retrieving engine status: " + e );
            e.printStackTrace();
        }

        response.setStatus( HttpServletResponse.SC_SERVICE_UNAVAILABLE );

        return null;
    }

}
