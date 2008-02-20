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
package org.nexuse2e.ui.ajax;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.nexuse2e.ui.ajax.dojo.TreeProvider;

/**
 * @author Sebastian Schulze
 * @date 13.12.2006
 */
public class AjaxServlet extends HttpServlet {

    /**
     * 
     */
    private static final long   serialVersionUID = -6583444281593200091L;

    private static final Logger LOG              = Logger.getLogger( AjaxServlet.class );

    private static final String PATH_MENU        = "/menu";

    private static final String PATH_COMMANDS    = "/commands";

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException,
            IOException {

        LOG.trace( "PROCESSING REQUEST" );

        try {
            String result = null;
            if ( PATH_MENU.equals( request.getPathInfo() ) ) {
                result = new TreeProvider().handleRequest( request );
            } else if ( PATH_COMMANDS.equals( request.getPathInfo() ) ) {
                result = new TreeProvider().handleRequest( request );
            } else {
                LOG.warn( "Unknown path requested: path=" + request.getPathInfo() );
            }

            if ( result != null ) {
                LOG.trace( "Result: " + result );
                response.setContentType( "text/json" );
                response.setStatus( HttpServletResponse.SC_OK );
                Writer writer = response.getWriter();
                writer.write( result );
                writer.flush();
            } else {
                response.setStatus( HttpServletResponse.SC_NOT_FOUND );
            }
        } catch ( JSONException e ) {
            LOG.error( e );
            response.setStatus( HttpServletResponse.SC_BAD_REQUEST );
        }

        LOG.trace( "RETURNING REQUEST" );
        // super.doGet( request, response );
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doPost( HttpServletRequest arg0, HttpServletResponse arg1 ) throws ServletException, IOException {

        BufferedReader br = new BufferedReader( new InputStreamReader( arg0.getInputStream() ) );
        while ( br.ready() ) {
            LOG.debug( br.readLine() );
        }
        super.doPost( arg0, arg1 );
    }

    public String getStringRepresentation( HttpServletRequest request ) {

        StringBuffer sb = new StringBuffer();

        sb.append( "\n" );
        sb.append( "Protocol: " + request.getProtocol() );
        sb.append( "\n" );
        sb.append( "Method: " + request.getMethod() );
        sb.append( "\n" );
        sb.append( "ContextPath: " + request.getContextPath() );
        sb.append( "\n" );
        sb.append( "PathInfo: " + request.getPathInfo() );
        sb.append( "\n" );
        sb.append( "RequestURL: " + request.getRequestURL() );
        sb.append( "\n" );
        sb.append( "QueryString: " + request.getQueryString() );
        sb.append( "\n" );
        sb.append( "ContentType: " + request.getContentType() );
        sb.append( "\n" );
        sb.append( "--- Headers ---" );
        sb.append( "\n" );
        Enumeration<?> headerNames = request.getHeaderNames();
        while ( headerNames.hasMoreElements() ) {
            String currName = (String) headerNames.nextElement();
            sb.append( currName + ": " + request.getHeader( currName ) );
            sb.append( "\n" );
        }
        sb.append( "--- Attributes ---" );
        sb.append( "\n" );
        Enumeration<?> attributeNames = request.getAttributeNames();
        while ( attributeNames.hasMoreElements() ) {
            String currName = (String) attributeNames.nextElement();
            sb.append( currName + ": " + request.getAttribute( currName ) );
            sb.append( "\n" );
        }
        sb.append( "--- Parameters ---" );
        sb.append( "\n" );
        Enumeration<?> paramNames = request.getParameterNames();
        while ( paramNames.hasMoreElements() ) {
            String currName = (String) paramNames.nextElement();
            sb.append( currName + ": " + request.getParameter( currName ) );
            sb.append( "\n" );
        }
        sb.append( "\n" );

        return sb.toString();
    }

}
