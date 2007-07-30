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

package org.nexuse2e.messaging.generic;

/**
 * @author mbreilmann
 *
 */
public class RoutingConfiguration {

    private String customParameter = null;
    private String command         = null;
    private String length          = null;
    private String trim            = null;
    private String align           = null;
    private String filler          = null;
    private String routingInfo     = null;

    public String getCustomParameter() {

        return customParameter;
    }

    public void setCustomParameter( String customParameter ) {

        this.customParameter = customParameter;
    }

    public String getLength() {

        return length;
    }

    public void setLength( String length ) {

        this.length = length;
    }

    public String getTrim() {

        return trim;
    }

    public void setTrim( String trim ) {

        this.trim = trim;
    }

    public String getAlign() {

        return align;
    }

    public void setAlign( String align ) {

        this.align = align;
    }

    public String getFiller() {

        return filler;
    }

    public void setFiller( String filler ) {

        this.filler = filler;
    }

    public String getCommand() {

        return command;
    }

    public void setCommand( String command ) {

        this.command = command;
    }

    
    public String getRoutingInfo() {
    
        return routingInfo;
    }

    
    public void setRoutingInfo( String routingInfo ) {
    
        this.routingInfo = routingInfo;
    }

}
