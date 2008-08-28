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

import javax.servlet.http.HttpServletRequest;

import org.json.JSONException;

/**
 * Interface for instances that can handle Ajax requests.
 * @author Sebastian Schulze
 * @date 14.12.2006
 */
public interface AjaxRequestHandler {

    /**
     * Handles an Ajax request.
     * @param request The HTTP request.
     * @return JSON encoded response data.
     * @throws JSONException there is a problem with the JSON encoding.
     */
    String handleRequest( HttpServletRequest request ) throws JSONException;

}
