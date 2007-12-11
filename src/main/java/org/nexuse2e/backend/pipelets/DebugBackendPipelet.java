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
package org.nexuse2e.backend.pipelets;

import java.util.List;

import org.apache.log4j.Logger;
import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.Constants.ParameterType;
import org.nexuse2e.messaging.AbstractPipelet;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.pojo.MessagePayloadPojo;

/**
 * Pipelet for debugging purposes.
 *
 * @author Jonas Reese
 */
public class DebugBackendPipelet extends AbstractPipelet {

    private static Logger LOG = Logger.getLogger( AbstractPipelet.class );
    
    public static final String TEXT_PARAM_NAME = "text";
    public static final String PRINT_PAYLOAD_PARAM_NAME = "printPayload";
    public static final String THROW_EXCEPTION_PARAM_NAME = "throwException";
    public static final String EXCEPTION_MESSAGE_PARAM_NAME = "exceptionMessage";
    

    public DebugBackendPipelet() {
        parameterMap.put( TEXT_PARAM_NAME, new ParameterDescriptor( ParameterType.STRING, "Output text",
                "The text to display on the console when a message is processed by this pipelet", "" ) );
        parameterMap.put( PRINT_PAYLOAD_PARAM_NAME, new ParameterDescriptor( ParameterType.BOOLEAN, "Output message payload",
                "Check if message payload shall be displayed on the console", Boolean.FALSE ) );
        parameterMap.put( THROW_EXCEPTION_PARAM_NAME, new ParameterDescriptor( ParameterType.BOOLEAN, "Throw an exception",
                "Ends the processing by throwing an exception", Boolean.FALSE ) );
        parameterMap.put( EXCEPTION_MESSAGE_PARAM_NAME, new ParameterDescriptor( ParameterType.STRING, "Exception message",
                "Message text if an exception shall be thrown", "" ) );
        setFrontendPipelet( false );
    }
    
    @Override
    public MessageContext processMessage( MessageContext messageContext )
            throws IllegalArgumentException, IllegalStateException,
            NexusException {
        String s = getParameter( TEXT_PARAM_NAME );
        if (s != null && s.trim().length() > 0) {
            LOG.info( s );
        }
        if (((Boolean) getParameter( PRINT_PAYLOAD_PARAM_NAME )).booleanValue()
                && messageContext != null && messageContext.getMessagePojo() != null) {
            List<MessagePayloadPojo> list = messageContext.getMessagePojo().getMessagePayloads();
            if (list != null) {
                for (MessagePayloadPojo payload : list) {
                    byte[] data = payload.getPayloadData();
                    LOG.info( "Payload " + payload.getContentId() + ", mime-type " + payload.getMimeType() );
                    if (data != null) {
                        LOG.info( new String( data ) );
                    } else {
                        LOG.info( null );
                    }
                }
            }
        }
        
        if (((Boolean) getParameter( THROW_EXCEPTION_PARAM_NAME )).booleanValue()) {
            throw new NexusException( (String) getParameter( EXCEPTION_MESSAGE_PARAM_NAME ) );
        }
        
        return messageContext;
    }
}
