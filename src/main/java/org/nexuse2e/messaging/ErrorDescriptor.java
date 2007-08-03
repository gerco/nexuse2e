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
package org.nexuse2e.messaging;

import java.io.Serializable;

import org.nexuse2e.Constants.Severity;

/**
 * Helper class to procide a data structure for more descriptive error information.
 *
 * @author gesch
 */
public class ErrorDescriptor implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -208729675952740787L;

    private String            description      = "Unknown error occured";
    private String            location         = "Unknown error location";
    private int               errorCode;
    private Severity          severity;
    private Exception         cause;

    /**
     * Default constructor
     */
    public ErrorDescriptor() {

    }

    /**
     * @param description
     */
    public ErrorDescriptor( String description ) {

        this.description = description;
    }

    /**
     * @return the description
     */
    public String getDescription() {

        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription( String description ) {

        this.description = description;
    }

    /**
     * @return the location
     */
    public String getLocation() {

        return location;
    }

    /**
     * @param location the location to set
     */
    public void setLocation( String location ) {

        this.location = location;
    }

    /**
     * @return the errorCode
     */
    public int getErrorCode() {

        return errorCode;
    }

    /**
     * @param errorCode the errorCode to set
     */
    public void setErrorCode( int errorCode ) {

        this.errorCode = errorCode;
    }

    /**
     * @return the severity
     */
    public Severity getSeverity() {

        return severity;
    }

    /**
     * @param severity the severity to set
     */
    public void setSeverity( Severity severity ) {

        this.severity = severity;
    }

    /**
     * @return the cause
     */
    public Exception getCause() {

        return cause;
    }

    /**
     * @param cause the cause to set
     */
    public void setCause( Exception cause ) {

        this.cause = cause;
    }

} // ErrorDescriptor
