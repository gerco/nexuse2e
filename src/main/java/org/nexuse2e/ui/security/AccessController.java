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
package org.nexuse2e.ui.security;

import java.util.Enumeration;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.nexuse2e.pojo.GrantPojo;
import org.nexuse2e.pojo.UserPojo;


/**
 * Controls the users' access permissions. 
 * @author Sebastian Schulze
 * @date 29.01.2007
 */
public class AccessController {
    
    protected static final Logger LOG = Logger.getLogger( AccessController.class );
    
    // actions that must be permitted for all users.
    protected static final String DEFAULT_FORWARD_ACTION = "NexusE2EAdmin.do";
    protected static final String LOGOUT_ACTION = "Logout.do";
    protected static final String WILDCARD = "*";
    private static final String QUERY_STRING_PREFIX = "?";
    private static final String QUERY_STRING_DELIMITER = "&";
    private static final String QUERY_STRING_ASSIGNMENT = "=";
    private static final String VARIABLE_START = "${";
    private static final String VARIABLE_END = "}";
    
    /**
     * Checks wheather a user has the permission to performe a request.
     * @param user The user.
     * @param request The request.
     * @return <code>true</code> if the access is granted. <code>false</code> if access is denied.
     */
    public static boolean hasAccess( UserPojo user, HttpServletRequest request ) {
        boolean result = false;
        
        //LOG.trace( getStringRepresentation( request ) );
        
        String path = request.getRequestURL().toString();;
        
        if ( user.getRole() != null ) {
            Map<String,GrantPojo> grants = user.getRole().getGrants();
            if ( grants != null ) {
                // check for wildcard
                if ( grants.containsKey( WILDCARD ) ) {
                    result = true;
                    LOG.trace( "Found wildcard for \"" + user.getLoginName() + "\"" );
                } else {
                    if ( path != null ) {
                        // extract path
                        int lastSlash = path.lastIndexOf( "/" );
                        path = path.substring( ( lastSlash > -1 ? lastSlash + 1 : 0 ) );
                        // check for default forward action
                        if ( DEFAULT_FORWARD_ACTION.equals( path ) || LOGOUT_ACTION.equals( path ) ) {
                            result = true;
                        } else {
                            // check grants
                            if ( grants.containsKey( path ) ) {
                                GrantPojo grant = grants.get( path );
                                String target = grant.getTarget();
                                // test whether grant is tied to a special query parameter
                                int paramStringStartPos = target.indexOf( QUERY_STRING_PREFIX );
                                if ( paramStringStartPos > -1 ) {
                                    StringTokenizer st = new StringTokenizer( target.substring( paramStringStartPos + 1 ), QUERY_STRING_DELIMITER );
                                    boolean paramMismatch = false;
                                    // iterate over query parameters
                                    while ( st.hasMoreTokens() && !paramMismatch ) {
                                        String token = st.nextToken();
                                        int assignmentCharPos = token.indexOf( QUERY_STRING_ASSIGNMENT );
                                        String name = null;
                                        String value = null;
                                        if ( assignmentCharPos > -1 ) {
                                            name = token.substring( 0, assignmentCharPos );
                                            value = token.substring( assignmentCharPos + 1 );
                                        } else {
                                            name = token;
                                        }
                                        
                                        paramMismatch = !isMatchingQueryConstraint( request, name, value );
                                    }
                                    
                                    // did all params match?
                                    if( !paramMismatch ) {
                                        result = true;
                                    }
                                } else {
                                    // no query condition
                                    result = true;
                                }
                            }
                        }
                    }
                }
            }
        }
        
        if ( result ) {
            LOG.trace( "Granted access to path " + path );
        } else {
            LOG.trace( "Denied access to path " + path );
        }
        
        return result;
    }
    
    private static boolean isMatchingQueryConstraint( HttpServletRequest request, String name, String value ) {
        boolean result = false;
        if ( name == null ) {
            // null always matches
            result = true;
        } else {
            // ignore variables denoted by ${...} contained in name
            int variableStartPos = name.indexOf( VARIABLE_START );
            int variableEndPos = name.indexOf( VARIABLE_END );
            if ( variableStartPos > -1 && variableEndPos > -1 ) {
                // names with variables always match
                result = true;
            } else {
                String requestParamValue = request.getParameter( name );
                if ( requestParamValue != null ) {
                    if ( value != null ) {
                        variableStartPos = value.indexOf( VARIABLE_START );
                        variableEndPos = value.indexOf( VARIABLE_END );
                        if ( variableStartPos > -1 && variableEndPos > -1 ) {
                            // values with variables always match
                            result = true;
                        } else {
                            result = value.equals( requestParamValue );
                        }
                    }
                }
            }
        }
        return result;
    }
    
    public static String getStringRepresentation( HttpServletRequest request ) {

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
        Enumeration headerNames = request.getHeaderNames();
        while ( headerNames.hasMoreElements() ) {
            String currName = (String) headerNames.nextElement();
            sb.append( currName + ": " + request.getHeader( currName ) );
            sb.append( "\n" );
        }
        sb.append( "--- Attributes ---" );
        sb.append( "\n" );
        Enumeration attributeNames = request.getAttributeNames();
        while ( attributeNames.hasMoreElements() ) {
            String currName = (String) attributeNames.nextElement();
            sb.append( currName + ": " + request.getAttribute( currName ) );
            sb.append( "\n" );
        }
        sb.append( "--- Parameters ---" );
        sb.append( "\n" );
        Enumeration paramNames = request.getParameterNames();
        while ( paramNames.hasMoreElements() ) {
            String currName = (String) paramNames.nextElement();
            sb.append( currName + ": " + request.getParameter( currName ) );
            sb.append( "\n" );
        }
        sb.append( "\n" );

        return sb.toString();
    }
}
