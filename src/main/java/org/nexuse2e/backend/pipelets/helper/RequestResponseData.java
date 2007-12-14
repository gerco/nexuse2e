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

package org.nexuse2e.backend.pipelets.helper;

import java.util.Map;

/**
 * @author mbreilmann
 *
 */
public class RequestResponseData {

    private int       responseCode   = 0;
    private String    requestString  = null;
    private String    responseString = null;
    private Map<String, String> parameters     = null;

    public RequestResponseData( int responseCode, String responseString, String requestString ) {

        this.requestString = requestString;
        this.responseString = responseString;
        this.responseCode = responseCode;
    }

    public Map<String, String> getParameters() {

        return parameters;
    }

    public void setParameters( Map<String, String> parameters ) {

        this.parameters = parameters;
    }

    public String getRequestString() {

        return requestString;
    }

    public void setRequestString( String requestString ) {

        this.requestString = requestString;
    }

    public int getResponseCode() {

        return responseCode;
    }

    public void setResponseCode( int responseCode ) {

        this.responseCode = responseCode;
    }

    public String getResponseString() {

        return responseString;
    }

    public void setResponseString( String responseString ) {

        this.responseString = responseString;
    }

} // RequestResponseData
