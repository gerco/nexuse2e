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
package org.nexuse2e.backend.pipelets.helper;

import org.apache.log4j.Logger;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.backend.BackendPipelineDispatcher;

/**
 * @author mbreilmann
 *
 */
public class ResponseSender implements Runnable {

    private static Logger       LOG                 = Logger.getLogger( ResponseSender.class );

    private String              choreography        = null;
    private String              partner             = null;
    private String              conversation        = null;
    private String              action              = null;
    private RequestResponseData requestResponseData = null;
    private int                 delay               = 0;

    public ResponseSender( String choreography, String partner, String conversation, String action,
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
