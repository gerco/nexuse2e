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
package org.nexuse2e.integration;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

/**
 * Debug incoming HTTP requests by dumping all parameters and the content to the log
 *
 * @author mbreilmann
 */
public class DebugController implements Controller {

    private static Logger LOG = Logger.getLogger( DebugController.class );

    /* (non-Javadoc)
     * @see org.springframework.web.servlet.mvc.Controller#handleRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ModelAndView handleRequest( HttpServletRequest request, HttpServletResponse response ) throws Exception {

        String line = null;

        for ( Object parameterName : request.getParameterMap().keySet() ) {
            LOG.debug( parameterName + " = " + request.getParameter( (String)parameterName ) );
        }

        InputStream inStream = request.getInputStream();
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader( inStream ) );
        while ( ( line = bufferedReader.readLine() ) != null ) {
            LOG.debug( line );
        }

        response.setStatus( HttpServletResponse.SC_OK );

        return null;
    }

} // DebugController
