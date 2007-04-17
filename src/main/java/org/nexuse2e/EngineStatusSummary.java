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
package org.nexuse2e;

import org.nexuse2e.Constants.Runlevel;


/**
 * @author gesch
 *
 */
public class EngineStatusSummary implements StatusSummary {

    /**
     * 
     */
    private static final long serialVersionUID = 5958453423169252669L;
    private Status status = Status.UNKNOWN;
    private Runlevel engineRunlevel = Runlevel.UNKNOWN;
    private Status databaseStatus = Status.UNKNOWN;
    private Status inboundStatus = Status.UNKNOWN;
    private Status outboundStatus = Status.UNKNOWN;
    
    
    /* (non-Javadoc)
     * @see org.nexuse2e.StatusSummary#getStatus()
     */
    public Status getStatus() {

        return status;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.StatusSummary#setStatus()
     */
    public void setStatus(Status status) {

        this.status = status;
    }

    
    /**
     * @return the databaseStatus
     */
    public Status getDatabaseStatus() {
    
        return databaseStatus;
    }

    
    /**
     * @param databaseStatus the databaseStatus to set
     */
    public void setDatabaseStatus( Status databaseStatus ) {
    
        this.databaseStatus = databaseStatus;
    }

    
    /**
     * @return the engineRunlevel
     */
    public Runlevel getEngineRunlevel() {
    
        return engineRunlevel;
    }

    
    /**
     * @param engineRunlevel the engineRunlevel to set
     */
    public void setEngineRunlevel( Runlevel engineRunlevel ) {
    
        this.engineRunlevel = engineRunlevel;
    }

    
    /**
     * @return the inboundStatus
     */
    public Status getInboundStatus() {
    
        return inboundStatus;
    }

    
    /**
     * @param inboundStatus the inboundStatus to set
     */
    public void setInboundStatus( Status inboundStatus ) {
    
        this.inboundStatus = inboundStatus;
    }

    
    /**
     * @return the outboundStatus
     */
    public Status getOutboundStatus() {
    
        return outboundStatus;
    }

    
    /**
     * @param outboundStatus the outboundStatus to set
     */
    public void setOutboundStatus( Status outboundStatus ) {
    
        this.outboundStatus = outboundStatus;
    }
    
}
