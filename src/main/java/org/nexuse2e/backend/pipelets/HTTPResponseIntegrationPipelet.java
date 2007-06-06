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

import org.apache.log4j.Logger;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.backend.BackendPipelineDispatcher;
import org.nexuse2e.backend.pipelets.helper.RequestResponseData;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.Constants.ParameterType;
import org.nexuse2e.messaging.MessageContext;

/**
 * @author mbreilmann
 *
 */
public class HTTPResponseIntegrationPipelet extends HTTPIntegrationPipelet {

    private static Logger      LOG    = Logger.getLogger( HTTPIntegrationPipelet.class );

    public static final String ACTION = "action";
    public static final String DELAY  = "delay";

    public HTTPResponseIntegrationPipelet() {

        super();
        parameterMap.put( ACTION, new ParameterDescriptor( ParameterType.STRING, "Action",
                "Action to trigger for outbound message.", "" ) );
        parameterMap.put( DELAY, new ParameterDescriptor( ParameterType.STRING, "Delay",
                "Delay in milliseconds before outbound message is sent.", "1000" ) );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.Pipelet#processMessage(org.nexuse2e.messaging.MessageContext)
     */
    public MessageContext processMessage( MessageContext messageContext ) throws IllegalArgumentException,
            IllegalStateException, NexusException {

        RequestResponseData requestResponseData = null;

        String action = getParameter( ACTION );
        if ( action == null ) {
            LOG.error( "Parameter action has not been defined!" );
            return messageContext;
        }
        String delayString = getParameter( DELAY );
        int delay = 1000;
        if ( ( delayString != null ) && ( delayString.length() != 0 ) ) {
            delay = Integer.parseInt( delayString );
        }

        // Execute the HTTP call first
        messageContext = super.processMessage( messageContext );

        if ( !( messageContext.getData() instanceof RequestResponseData ) ) {
            LOG.error( "Wrong class detected in data field, found " + messageContext.getData().getClass() );
            throw new NexusException( "Wrong class detected in data field, found "
                    + messageContext.getData().getClass() );
        }
        requestResponseData = (RequestResponseData) messageContext.getData();

        // Trigger new response message
        new Thread( new ResponseSender( messageContext.getChoreography().getName(), messageContext.getPartner()
                .getPartnerId(), messageContext.getConversation().getConversationId(), action, requestResponseData,
                delay ) ).start();

        LOG.debug( "Done!" );
        return messageContext;
    }

    private class ResponseSender implements Runnable {

        private String              choreography        = null;
        private String              partner             = null;
        private String              conversation        = null;
        private String              action              = null;
        private RequestResponseData requestResponseData = null;
        private int                 delay               = 0;

        private ResponseSender( String choreography, String partner, String conversation, String action,
                RequestResponseData httpResponse, int delay ) {

            this.choreography = choreography;
            this.partner = partner;
            this.conversation = conversation;
            this.action = action;
            this.requestResponseData = httpResponse;
            this.delay = delay;
        }

        public void run() {

            BackendPipelineDispatcher backendPipelineDispatcher = Engine.getInstance().getCurrentConfiguration()
                    .getStaticBeanContainer().getBackendPipelineDispatcher();
            try {
                try {
                    LOG.debug( "Waiting " + delay + " milliseconds..." );
                    synchronized ( this ) {
                        this.wait( delay );
                    }
                } catch ( InterruptedException e ) {
                    LOG.warn( "Interrupted while waiting for response message submission: " + e );
                }
                if ( LOG.isTraceEnabled() ) {
                    LOG.trace( "Result code: " + requestResponseData.getResponseCode() );
                    LOG.trace( "Original   :\n--- PAYLOAD START ---\n" + requestResponseData.getRequestString()
                            + "\n---  PAYLOAD END  ---" );
                    LOG.trace( "Response   :\n--- RESPONSE START ---\n" + requestResponseData.getResponseString()
                            + "\n---  RESPONSE END  ---" );
                }

                backendPipelineDispatcher.processMessage( partner, choreography, action, conversation, null,
                        requestResponseData, requestResponseData.getResponseString().getBytes() );
            } catch ( NexusException e ) {
                LOG.error( "Error submitting response message for HTTP integration: " + e );
            }

        }
    } // ResponseSender

} // HTTPResponseIntegrationPipelet
