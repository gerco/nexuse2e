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

import javax.xml.ws.WebFault;

/**
 * Indicates a processing error on an inbound message.
 * @author jonas.reese
 */
@WebFault(name = "ProcessInboundMessageException", targetNamespace="http://integration.nexuse2e.org")
public class ProcessInboundMessageException extends Exception {

    private static final long serialVersionUID = 1L;

    
    private int errorCode;
    private String errorDescription;

    public ProcessInboundMessageException() {
        super();
    }

    public ProcessInboundMessageException( String message, Throwable cause ) {
        super( message, cause );
    }

    public ProcessInboundMessageException( String message ) {
        super( message );
    }

    public ProcessInboundMessageException( Throwable cause ) {
        super(cause);
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public void setErrorDescription( String errorDescription ) {
        this.errorDescription = errorDescription;
    }

    public String getErrorDescription() {
        return errorDescription;
    }
}
