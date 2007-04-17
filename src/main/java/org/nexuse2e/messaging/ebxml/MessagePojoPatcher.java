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
package org.nexuse2e.messaging.ebxml;

import org.apache.log4j.Logger;
import org.nexuse2e.messaging.AbstractPipelet;
import org.nexuse2e.messaging.MessageContext;

/**
 * @author mbreilmann
 *
 */
public class MessagePojoPatcher extends AbstractPipelet {

    private static Logger LOG = Logger.getLogger( HTTPMessageUnpackager.class );

    /**
     * Default constructor.
     */
    public MessagePojoPatcher() {

    }

    public MessageContext processMessage( MessageContext messageContext )
            throws IllegalArgumentException, IllegalStateException {

        //MessagePojo messagePojo = messageContext.getMessagePojo();

        LOG.trace( "MessagePojoPatcher - processMessage" );

        /*
         Set<MessagePayloadPojo> messagePayloads = messagePojo.getMessagePayloads();
         if ( messagePojo.getMessagePayloads().size() > 1 ) {
         for ( int i = 1; i < messagePayloads.size(); i++ ) {
         MessagePayloadPojo messagePayloadPojo = new MessagePayloadPojo();
         // messagePayloadPojo.setSequenceNumber( i );
         messagePayloadPojo.setMessage( messagePojo );
         // messagePayloads.add( messagePayloadPojo );
         }
         }
         */

        return messageContext;
    }

} // MessagePojoPatcher
