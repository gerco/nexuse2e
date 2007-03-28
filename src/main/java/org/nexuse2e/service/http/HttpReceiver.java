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
package org.nexuse2e.service.http;

import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.nexuse2e.Constants.Runlevel;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.Constants.ParameterType;
import org.nexuse2e.service.AbstractService;
import org.nexuse2e.service.ReceiverAware;
import org.nexuse2e.transport.TransportReceiver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

/**
 * A service that can be used by a <code>TransportReceiver</code> in order
 * to receive messages via HTTP.
 *
 * @author gesch, jonas.reese
 */
/**
 * A service that can be used by a <code>TransportReceiver</code> in order
 * to receive messages via HTTP.
 *
 * @author gesch, jonas.reese
 */
public class HttpReceiver extends AbstractService implements Controller, ReceiverAware {

    private static Logger     LOG = Logger.getLogger( HttpReceiver.class );

    public static final String URL_PARAM_NAME          = "logical_name";

    private TransportReceiver transportReceiver;

    @Override
    public void fillParameterMap( Map<String, ParameterDescriptor> parameterMap ) {
        parameterMap.put( URL_PARAM_NAME, new ParameterDescriptor( ParameterType.STRING, "Logical Name",
                "Logical name that is appended to the URL", "not_defined" ) );
    }

    /* (non-Javadoc)
     * @see org.springframework.web.servlet.mvc.Controller#handleRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ModelAndView handleRequest( HttpServletRequest request, HttpServletResponse response ) throws Exception {

        try {

            transportReceiver.processInboundData( request );

            LOG.trace( "Processing Done" );

            PrintWriter out = new PrintWriter( response.getOutputStream() );
            response.setStatus( HttpServletResponse.SC_OK );
            out.println( "\n" );
            out.flush();
            out.close();

        } catch ( Exception e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            response.sendError( 500, "NEXUSe2e - Processing error: " + e );
        }

        return null;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.service.AbstractService#getActivationTime()
     */
    @Override
    public Runlevel getActivationRunlevel() {

        return Runlevel.INBOUND_PIPELINES;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#teardown()
     */
    public void teardown() {
        super.teardown();
        
        transportReceiver = null;
    } // teardown

    public TransportReceiver getTransportReceiver() {

        return transportReceiver;
    }

    public void setTransportReceiver( TransportReceiver transportReceiver ) {

        this.transportReceiver = transportReceiver;
    }
}
