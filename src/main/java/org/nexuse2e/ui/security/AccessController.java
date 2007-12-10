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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
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
//    private static final String QUERY_STRING_PREFIX = "?";
//    private static final String QUERY_STRING_DELIMITER = "&";
//    private static final String QUERY_STRING_ASSIGNMENT = "=";
    private static final String VARIABLE_START = "${";
    private static final String VARIABLE_END = "}";
    
    /**
     * Checks whether a user has the permission to perform a request.
     * @param user The user.
     * @param request The request.
     * @return <code>true</code> if the access is granted. <code>false</code> if access is denied.
     */
    @SuppressWarnings("unchecked")
    public static boolean hasAccess( UserPojo user, HttpServletRequest request ) {
        String requestUrl = request.getRequestURL().toString();
        boolean result = hasAccess( user.getRole().getAllowedRequests(), requestUrl, request.getParameterMap(), false );
        if ( LOG.isTraceEnabled() ) {
            if ( result ) {
                LOG.trace( "Granted access to path " + requestUrl + " for user " + user.getLoginName() );
            } else {
                LOG.trace( "Denied access to path " + requestUrl  + " for user " + user.getLoginName() );
            }
        }
        return result;
    }
    
    /**
     * Checks whether a user has the permission to perform a request.
     * @param allowedRequests The requests one is allowed to make.
     * @param requestUrl The url of the request.
     * @param requestParameters The request parameters.
     * @param ignoreWildcard if <code>true</code> the wildcard "*" will not be taken into account,
     *                       but only the explicit allowed requests will be checked for access permission.
     * @return <code>true</code> if the access is granted. <code>false</code> if access is denied.
     */        
    public static boolean hasAccess( Map<String,Set<ParsedRequest>> allowedRequests, String requestUrl, Map<String,String[]> requestParameters, boolean ignoreWildcard ) {
        boolean result = false;
        
        //LOG.trace( getStringRepresentation( request ) );
        
        if ( allowedRequests != null ) {
            // check for wildcard
            if ( !ignoreWildcard && allowedRequests.containsKey( WILDCARD ) ) {
                result = true;                
            } else {
                if ( requestUrl != null ) {
                    ParsedRequest pr = parseRequestUrl( requestUrl );
                    // check for default forward action
                    if ( DEFAULT_FORWARD_ACTION.equals( pr.getActionMapping() ) || LOGOUT_ACTION.equals( pr.getActionMapping() ) ) {
                        result = true;
                    } else {
                        // check grants
                        if ( allowedRequests.containsKey( pr.getActionMapping() ) ) {
                            // test whether allowedRequest is tied to special query parameters
                            Set<ParsedRequest> requests = allowedRequests.get( pr.getActionMapping() );
                            if ( requests != null ) {
                                Iterator<ParsedRequest> requestIter = requests.iterator();
                                while ( !result && requestIter.hasNext() ) {
                                    ParsedRequest currRequest = requestIter.next();
                                    boolean paramMismatch = false;
                                    if ( currRequest.getRequestParameters().size() > 0 ) {
                                        Iterator<String> keyIter = currRequest.getRequestParameters().keySet().iterator();
                                        while ( !paramMismatch && keyIter.hasNext() ) {
                                            String key = keyIter.next();
                                            paramMismatch = !isMatchingQueryConstraint( requestParameters,
                                                    key,
                                                    currRequest.getRequestParameters().get( key ) );
                                        }
                                        
                                        // did all parameters match?
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
        }
        
        
        if ( result ) {
            LOG.trace( "Granted access to path " + parseRequestUrl( requestUrl ).getActionMappingWithParameters() + " with request parameters " + requestParameters );
        } else {
            LOG.trace( "Denied access to path " + parseRequestUrl( requestUrl ).getActionMappingWithParameters() + " with request parameters " + requestParameters );
        }
                
        return result;
    }
    
    private static boolean isMatchingQueryConstraint( Map<String,String[]> requestParameters, String name, String value[] ) {
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
                /* TODO: Maybe we need support for multiple values for the same query parameter in the future.
                 * We only care of the first value for now.
                 */
                String[] requestParamValue = requestParameters.get( name );
                if ( requestParamValue != null && requestParamValue.length > 0 ) {
                    if ( value != null && value.length > 0 ) {
                        variableStartPos = value[0].indexOf( VARIABLE_START );
                        variableEndPos = value[0].indexOf( VARIABLE_END );
                        if ( variableStartPos > -1 && variableEndPos > -1 ) {
                            // values with variables always match
                            result = true;
                        } else {
                            result = value[0].equals( requestParamValue[0] );
                        }
                    }
                }
            }
        }
        return result;
    }
    
    public static ParsedRequest parseRequestUrl( String requestUrl ) {
        String actionMapping = null;
        String actionMappingWithParams = null;
        Map<String,String[]> requestParameters = new HashMap<String,String[]>();
        // parse request url
        Pattern p1 = Pattern.compile( "([^/\\?]*)(\\?(.*))?$" );
        Matcher m1 = p1.matcher( requestUrl );
        if ( m1.find() ) {
            actionMapping = m1.group( 1 );
            actionMappingWithParams = m1.group( 0 );
            String params = m1.group( 3 );
            if ( params != null ) {
                // parse request parameters
                Pattern p2 = Pattern.compile( "([^\\&\\=]+)(\\=([^\\&\\=]+))?" );
                Matcher m2 = p2.matcher( params );
                while ( m2.find() ) {
                    String[] values = requestParameters.get( m2.group( 1 ) );
                    if ( values != null ) {
                        String[] newValues = new String[ values.length + 1 ];
                        System.arraycopy( values, 0, newValues, 0, values.length );
                        newValues[ values.length ] = m2.group( 3 );
                        requestParameters.put( m2.group( 1 ), newValues );
                    } else {
                        requestParameters.put( m2.group( 1 ), new String[] { m2.group( 3 ) } );
                    }
                }
            }
        }
        
        return new ParsedRequest( actionMapping, actionMappingWithParams, requestParameters );
    }
    
    public static class ParsedRequest {
        private String actionMapping;
        private String actionMappingWithParams;
        private Map<String,String[]> requestParameters;
        
        protected ParsedRequest( String actionMapping, String actionMappingWithParams, Map<String,String[]> requestParameters ) {
            this.actionMapping = actionMapping;
            this.actionMappingWithParams = actionMappingWithParams;
            this.requestParameters = requestParameters;            
        }
        
        public String getActionMapping() {
            return actionMapping;
        }
        
        public String getActionMappingWithParameters() {
            return actionMappingWithParams;
        }
        
        public Map<String,String[]> getRequestParameters() {
            return requestParameters;
        }
    }
    
    @SuppressWarnings("unchecked")
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
        Enumeration<String> headerNames = request.getHeaderNames();
        while ( headerNames.hasMoreElements() ) {
            String currName = (String) headerNames.nextElement();
            sb.append( currName + ": " + request.getHeader( currName ) );
            sb.append( "\n" );
        }
        sb.append( "--- Attributes ---" );
        sb.append( "\n" );
        Enumeration<String> attributeNames = request.getAttributeNames();
        while ( attributeNames.hasMoreElements() ) {
            String currName = (String) attributeNames.nextElement();
            sb.append( currName + ": " + request.getAttribute( currName ) );
            sb.append( "\n" );
        }
        sb.append( "--- Parameters ---" );
        sb.append( "\n" );
        Enumeration<String> paramNames = request.getParameterNames();
        while ( paramNames.hasMoreElements() ) {
            String currName = (String) paramNames.nextElement();
            sb.append( currName + ": " + request.getParameter( currName ) );
            sb.append( "\n" );
        }
        sb.append( "\n" );

        return sb.toString();
    }
}
