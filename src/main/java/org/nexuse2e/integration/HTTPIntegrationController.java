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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class HTTPIntegrationController implements Controller {

    private static Logger LOG = Logger.getLogger( HTTPIntegrationController.class );

    public ModelAndView handleRequest( HttpServletRequest request, HttpServletResponse response ) throws Exception {

        String choreographyId = request.getParameter( Constants.PARAM_CHOREOGRAPY_ID );
        String conversationId = request.getParameter( Constants.PARAM_CONVERSATION_ID );
        String actionId = request.getParameter( Constants.PARAM_ACTION );
        String partnerId = request.getParameter( Constants.PARAM_PARTNER_ID );
        String content = request.getParameter( Constants.PARAM_CONTENT );
        StringBuffer contentBuffer = new StringBuffer();

        LOG.debug( "Handling HTTP request from legacy system..." );

        if ( content == null ) {
            String line = null;
            InputStream inStream = request.getInputStream();
            BufferedReader bufferedReader = new BufferedReader( new InputStreamReader( inStream ) );
            // System.out.println("Reading from input stream...");
            while ( ( line = bufferedReader.readLine() ) != null ) {
                // System.out.println( line + "\n" );
                contentBuffer.append( line + "\n" );
            }
            // System.out.println("Reading from input stream done!");
            content = contentBuffer.toString();
        }

        if ( ( choreographyId == null ) || ( choreographyId.length() == 0 ) ) {
            response.sendError( HttpServletResponse.SC_BAD_REQUEST, "Choreography ID parameter must be provided!" );
            return null;
        }
        if ( ( actionId == null ) || ( actionId.length() == 0 ) ) {
            response.sendError( HttpServletResponse.SC_BAD_REQUEST, "Action name parameter must be provided!" );
            return null;
        }
        if ( ( partnerId == null ) || ( partnerId.length() == 0 ) ) {
            response.sendError( HttpServletResponse.SC_BAD_REQUEST, "Partner ID parameter must be provided!" );
            return null;
        }
        if ( ( content == null ) || ( content.length() == 0 ) ) {
            response.sendError( HttpServletResponse.SC_BAD_REQUEST, "Content parameter must be provided!" );
            return null;
        }

        NEXUSe2eInterface theNEXUSe2eInterface = Engine.getInstance().getInProcessNEXUSe2eInterface();

        try {
            if ( conversationId == null || conversationId.length() == 0 ) { // Create a new conversation if none was specified
                conversationId = theNEXUSe2eInterface.sendNewStringMessage( choreographyId, partnerId, actionId,
                        content );
                LOG.debug( "##--> New conversation ID ( choreography '" + choreographyId + "', conversation ID '"
                        + conversationId + "')!" );
            } else {
                theNEXUSe2eInterface.sendStringMessage( conversationId, actionId, content );
            }

            LOG.debug( "Message sent ( choreography '" + choreographyId + "', conversation ID '" + conversationId
                    + "')!" );

            response.setStatus( HttpServletResponse.SC_OK );
        } catch ( NexusException e ) {
            e.printStackTrace();
            response.sendError( HttpServletResponse.SC_BAD_REQUEST,
                    "NexusE2EException was thrown during submission of message: " + e );
        }
        return null;
    }

} // HTTPIntegrationController
