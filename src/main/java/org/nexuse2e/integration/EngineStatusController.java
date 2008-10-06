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

import org.nexuse2e.Engine;
import org.nexuse2e.EngineMonitor;
import org.nexuse2e.EngineStatusSummary;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

/**
 * @author mbreilmann
 *
 */
public class EngineStatusController implements Controller {

    /* (non-Javadoc)
     * @see org.springframework.web.servlet.mvc.Controller#handleRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ModelAndView handleRequest( HttpServletRequest request, HttpServletResponse response ) throws Exception {

        StringBuffer result = new StringBuffer( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" );
        result.append( "<EngineStatusSummary>\n" );
        try {
            EngineMonitor engineMonitor = Engine.getInstance().getEngineController().getEngineMonitor();
            EngineStatusSummary engineStatusSummary = engineMonitor.getStatus();

            if ( engineStatusSummary != null ) {
                result.append( "\t<Status>" + EngineStatusSummary.getStatusString( engineStatusSummary.getStatus() ) + "</Status>\n" );
                result.append( "\t<DatabaseStatus>" + EngineStatusSummary.getStatusString( engineStatusSummary.getDatabaseStatus() )
                        + "</DatabaseStatus>\n" );
                result.append( "\t<InboundStatus>" + EngineStatusSummary.getStatusString( engineStatusSummary.getInboundStatus() )
                        + "</InboundStatus>\n" );
                result.append( "\t<OutboundStatus>" + EngineStatusSummary.getStatusString( engineStatusSummary.getOutboundStatus() )
                        + "</OutboundStatus>\n" );
                result.append( "\t<Cause>"
                        + ( engineStatusSummary.getCause() != null ? engineStatusSummary.getCause() : "" )
                        + "</Cause>\n" );
            }
        } catch ( Exception e ) {
            result.append( "\t<Status>Error</Status>\n" );
            result.append( "\t<Cause>" + e.getMessage() + "</Cause>\n" );
            e.printStackTrace();
        }
        result.append( "</EngineStatusSummary>\n" );

        response.setStatus( HttpServletResponse.SC_OK );
        response.getWriter().print( result.toString() );

        return null;
    }

}
