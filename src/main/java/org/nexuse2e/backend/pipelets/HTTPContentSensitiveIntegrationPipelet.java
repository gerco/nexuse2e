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
package org.nexuse2e.backend.pipelets;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.log4j.Logger;
import org.nexuse2e.NexusException;
import org.nexuse2e.backend.pipelets.helper.RequestResponseData;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.pojo.MessageLabelPojo;
import org.nexuse2e.pojo.MessagePayloadPojo;
import org.nexuse2e.pojo.MessagePojo;

/**
 * @author sroggensack,s_schulze
 * 
 */
public class HTTPContentSensitiveIntegrationPipelet extends HTTPIntegrationPipelet {

    private static Logger LOG = Logger.getLogger( HTTPContentSensitiveIntegrationPipelet.class );

    /**
     * Content sensitive processing of payloads. In comparison to the super
     * class this method takes care of content types, and charsets of the
     * payloads. It can handle binary payloads, if
     * "Send the content as a URL-encoded HTTP parameter" is disabled.
     * 
     * @see org.nexuse2e.backend.pipelets.HTTPIntegrationPipelet#processMessage(org.nexuse2e.messaging.MessageContext)
     */
    public MessageContext processMessage( MessageContext messageContext ) throws IllegalArgumentException,
                                                                         IllegalStateException, NexusException {

        boolean debug = false;
        String labelPrefix = "";
        String debugString = getParameter( DEBUG );
        Boolean sendAsParamBoolean = getParameter( SEND_AS_PARAM );
        boolean sendAsParam = sendAsParamBoolean.booleanValue();

        MessagePojo messagePojo = messageContext.getMessagePojo();
        List<MessageLabelPojo> messageLabels = null;

        Boolean includeLabelsBoolean = getParameter( SEND_AS_PARAM );
        if ( includeLabelsBoolean.booleanValue() ) {
            if ( ( messagePojo.getType() == org.nexuse2e.messaging.Constants.INT_MESSAGE_TYPE_ACK )
                    || ( messagePojo.getType() == org.nexuse2e.messaging.Constants.INT_MESSAGE_TYPE_ERROR ) ) {
                messageLabels = messagePojo.getReferencedMessage().getMessageLabels();
            } else {
                messageLabels = messagePojo.getMessageLabels();
            }
        }

        // Set label prefix
        String tempLabelPrefix = getParameter( LABEL_PREFIX );
        if ( tempLabelPrefix != null ) {
            labelPrefix = tempLabelPrefix;
        }

        String user = getParameter( USER );
        String password = getParameter( PASSWORD );

        if ( ( debugString != null )
                && ( debugString.trim().equalsIgnoreCase( "true" ) || debugString.trim().equalsIgnoreCase( "yes" ) ) ) {
            debug = true;
        }

        // System.out.println(
        // "start *********************************************************************"
        // );
        if ( debug ) {
            LOG.debug( "Executing HTTP POST for choreography ID '"
                    + messagePojo.getConversation().getChoreography().getName() + "', conversation ID '"
                    + messagePojo.getConversation().getConversationId() + "', sender '"
                    + messagePojo.getConversation().getPartner().getPartnerId() + "'!" );
            LOG.debug( "Sending content as URL-encoded parameter: " + sendAsParam );
        }
        PostMethod post = new PostMethod( (String) getParameter( URL ) );
        // Disable cookies
        HttpMethodParams httpMethodParams = new HttpMethodParams();
        httpMethodParams.setCookiePolicy( CookiePolicy.IGNORE_COOKIES );
        post.setParams( httpMethodParams );

        HttpClient httpclient = new HttpClient();

        // Use basic auth if credentials are present
        if ( ( user != null ) && ( user.length() != 0 ) && ( password != null ) ) {
            Credentials credentials = new UsernamePasswordCredentials( user, password );
            LOG.debug( "HTTPBackendConnector: Using basic auth." );
            httpclient.getState().setCredentials( AuthScope.ANY, credentials );
            post.setDoAuthentication( true );
        }

        if ( ( messagePojo.getType() == org.nexuse2e.messaging.Constants.INT_MESSAGE_TYPE_ACK )
                || ( messagePojo.getType() == org.nexuse2e.messaging.Constants.INT_MESSAGE_TYPE_ERROR ) ) {
            NameValuePair[] data = createHTTPParameters( messagePojo, null, messageLabels, labelPrefix );
            post.setRequestBody( data );
            // Execute request
            try {
                int result = httpclient.executeMethod( post );

                LOG.debug( "Response status code: " + result );
                LOG.debug( "Response status message:\n" + post.getResponseBodyAsString() );
            } catch ( Exception ex ) {
                LOG.error( "Error posting inbound message body to '" + getParameter( URL ) + "': " + ex );
                ex.printStackTrace();
            } finally {
                // Release current connection to the connection pool once you
                // are done
                post.releaseConnection();
            }
        } else {
            for ( MessagePayloadPojo messagePayloadPojo : messagePojo.getMessagePayloads() ) {
                String contentType = messagePayloadPojo.getMimeType();
                String charset = messagePayloadPojo.getCharset();
                boolean isTextContent = ( contentType != null && contentType.trim().toLowerCase().startsWith( "text" ) ) || charset != null;
                if ( charset == null ) {
                    // must be after text content detection! 
                    charset = Charset.defaultCharset().name();
                }
                
                if ( sendAsParam ) {
                    try {
                        NameValuePair[] data = createHTTPParameters(
                            messagePojo,
                            new String( messagePayloadPojo.getPayloadData(), charset ),
                            messageLabels,
                            labelPrefix );
                        post.setRequestBody( data );
                    } catch ( UnsupportedEncodingException e ) {
                        LOG.error( "Cannot encode payload with charset encoding '" + charset + "'", e );
                    }
                } else {
                    try {
                        if ( isTextContent ) {
                            post.setRequestEntity(
                                new StringRequestEntity(
                                    new String( messagePayloadPojo.getPayloadData(), charset ), contentType, charset ) );
                        } else {
                            post.setRequestEntity( new ByteArrayRequestEntity( messagePayloadPojo.getPayloadData() ) );
                        }
                    } catch ( UnsupportedEncodingException e ) {
                        LOG.error( "Cannot encode payload with charset encoding '" + charset + "'", e );
                    }
                }

                // Execute request
                try {
                    if ( LOG.isTraceEnabled() ) {
                        LOG.trace( "Payload:\n--- PAYLOAD START ---\n" + new String( messagePayloadPojo.getPayloadData(), charset ) + "\n---  PAYLOAD END  ---" );
                    }

                    int result = httpclient.executeMethod( post );

                    // Store response in data field of context
                    messageContext.setData(
                        new RequestResponseData( result,
                                                 post.getResponseBodyAsString(),
                                                 new String( messagePayloadPojo.getPayloadData(), charset ) ) );

                    LOG.debug( "Response status code: " + result );
                    LOG.debug( "Response status message:\n" + post.getResponseBodyAsString() );
                    if ( LOG.isTraceEnabled() ) {
                        LOG.trace( "Response:\n--- RESPONSE START ---\n"
                                + ( (RequestResponseData) messageContext.getData() ).getResponseString()
                                + "\n---  RESPONSE END  ---" );
                    }
                } catch ( Exception ex ) {
                    LOG.error( "Error posting inbound message body to '" + getParameter( URL ) + "': " + ex );
                    ex.printStackTrace();
                } finally {
                    // Release current connection to the connection pool once
                    // you are done
                    post.releaseConnection();
                }
            } // for
        } // is Ack?

        LOG.debug( "Done!" );
        return messageContext;
    }
} // HTTPContentSensitiveIntegrationPipelet
