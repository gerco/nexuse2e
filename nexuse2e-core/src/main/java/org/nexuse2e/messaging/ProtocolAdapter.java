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
package org.nexuse2e.messaging;

import java.util.List;

import org.nexuse2e.NexusException;
import org.nexuse2e.ProtocolSpecific;
import org.nexuse2e.ProtocolSpecificKey;
import org.nexuse2e.pojo.ChoreographyPojo;

/**
 * Interface for a messaging protocol specific component that implements creation of acknowledgement 
 * or error messages if the messaging protocol knows these types of messages (e.g. ebXML).
 *
 * @author gesch
 */
public interface ProtocolAdapter extends ProtocolSpecific {

    /**
     * Create an acknowledgement message if the implemented messaging protocol supports acknowledgements.
     * 
     * @param choreography The choreography for which to create the acknowledgement.
     * @param messageContext The <code>MessageContext</code> for which the acknowledgement shall be created.
     * @return The <code>MessageContext</code> representing the acknowledgement, null if none was created.
     * @throws NexusException
     */
    public MessageContext createAcknowledgement( ChoreographyPojo choreography, MessageContext messageContext )
            throws NexusException;

    /**
     * Create an error message if the implemented messaging protocol supports error messages.
     * 
     * @param reasonCode Code identifying the reason for the error. 
     * @param choreography The choreography for which to create the error.
     * @param messageContext  The <code>MessageContext</code> for which the acknowledgement shall be created.
     * @param errorMessages A list of error <code>ErrorDescriptor</code> objects.
     * @return A newly created <code>MessageContext</code> that represents an error acknowledgement.
     * <code>null</code> if none was created.
     */
    public MessageContext createErrorAcknowledgement( Constants.ErrorMessageReasonCode reasonCode,
            ChoreographyPojo choreography, MessageContext messageContext, List<ErrorDescriptor> errorMessages );
    
    /**
     * Create a response message for the given message context.
     * 
     * @param messageContext The message context. It is assumed to be a request
     * ({@link MessageContext#isRequestMessage()} returns <code>true</code>). Must not be <code>null</code>.
     * @return A newly created <code>MessageContext</code> that is a valid response to the request information
     * set on the <code>messageContext</code> parameter. <code>null</code> if none was created.
     * @throws NexusException If the response creation failed.
     */
    public MessageContext createResponse( MessageContext messageContext ) throws NexusException;

    public void addProtcolSpecificParameters( MessageContext messageContext );

    public ProtocolSpecificKey getKey();
    
    public void setKey( ProtocolSpecificKey key );
    
} // ProtocolAdapter
