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

import org.nexuse2e.Manageable;
import org.nexuse2e.NexusException;

/**
 * A <code>Pipeline</code> is a processing
 * chain that can contain a virtually unlimited number of processing
 * components, the so called <code>Pipelet</code>.
 * @author mbreilmann
 * @see org.nexuse2e.messaging.Pipelet
 * @see org.nexuse2e.messaging.MessageContext
 */
public interface Pipeline extends Manageable{

    /**
     * The method handling the processing of the message in this <code>Pipeline</code>. 
     * @param messagePipeletParameter The message progressing through the <code>Pipeline</code>. 
     * The <code>MessagePipeletParameter</code> contains additional meta data useful for the processing 
     * of the message in addition to the actual message itself.
     * @return The potentially modified The <code>MessagePipeletParameter</code>.
     * @throws IllegalArgumentException Thrown if information provided in the <code>MessagePipeletParameter</code> 
     * did not meet expectations.
     * @throws IllegalStateException Thrown if the system is not in a correct state to handle this specific message.
     * @throws NexusException Thrown if any other processing related exception occured.
     */
    public MessageContext processMessage( MessageContext messagePipeletParameter )
            throws IllegalArgumentException, IllegalStateException, NexusException;

    /**
     * The endpoint of this processing chain.
     * @return The endpoint of this <code>Pipeline</code>.
     */
    public MessageProcessor getPipelineEndpoint();

    /**
     * Set a <code>Pipelet</code> as the endpoint of this processing chain.
     * @param pipelineEndpoint The endpoint to set for this <code>Pipeline</code>.
     */
    public void setPipelineEndpoint( MessageProcessor pipelineEndpoint );
    
    /**
     * @return
     */
    public boolean isFrontendPipeline();
    
    /**
     * @param isFrontendPipeline
     */
    public void setFrontendPipeline(boolean isFrontendPipeline);
    
    /**
     * @return
     */
    public boolean isOutboundPipeline();
    
    /**
     * @param isOutboundPipeline
     */
    public void setOutboundPipeline(boolean isOutboundPipeline);
} // Pipeline
