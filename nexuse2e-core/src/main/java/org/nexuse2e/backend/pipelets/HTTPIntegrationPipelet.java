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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.log4j.Logger;
import org.nexuse2e.NexusException;
import org.nexuse2e.backend.pipelets.helper.RequestResponseData;
import org.nexuse2e.configuration.Constants.ParameterType;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.logging.LogMessage;
import org.nexuse2e.messaging.AbstractPipelet;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.pojo.MessageLabelPojo;
import org.nexuse2e.pojo.MessagePayloadPojo;
import org.nexuse2e.pojo.MessagePojo;

/**
 * @author mbreilmann
 *
 */
public class HTTPIntegrationPipelet extends AbstractPipelet {

    private static Logger      LOG            = Logger.getLogger( HTTPIntegrationPipelet.class );

    public static final String URL            = "URL";
    public static final String SEND_AS_PARAM  = "SendAsParameters";
    public static final String INCLUDE_LABELS = "IncludeLabels";
    public static final String LABEL_PREFIX   = "LabelPrefix";
    public static final String DEBUG          = "Debug";
    public static final String DEFAULT_URL    = "http://localhost:8080/NEXUSe2e/integration/http";
    public static final String USER           = "User";
    public static final String PASSWORD       = "Password";

    /**
     * Default constructor.
     */
    public HTTPIntegrationPipelet() {

        parameterMap.put( URL, new ParameterDescriptor( ParameterType.STRING, "URL of legacy system",
                "The URL that inbound messages are forwarded to.", DEFAULT_URL ) );
        parameterMap.put( SEND_AS_PARAM, new ParameterDescriptor( ParameterType.BOOLEAN, "Send content URL-encoded",
                "Send the content as a URL-encoded HTTP parameter.", Boolean.TRUE ) );
        parameterMap.put( USER, new ParameterDescriptor( ParameterType.STRING, "User name",
                "User name required for legacy system (optional).", "" ) );
        parameterMap.put( PASSWORD, new ParameterDescriptor( ParameterType.PASSWORD, "Password",
                "Password required for legacy system (optional).", "" ) );
        parameterMap.put( INCLUDE_LABELS, new ParameterDescriptor( ParameterType.BOOLEAN, "Include Labels",
                "Include Labels as HTTP parameters.", Boolean.FALSE ) );
        parameterMap.put( LABEL_PREFIX, new ParameterDescriptor( ParameterType.STRING, "Label Prefix", "Label Prefix",
                "" ) );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.Pipelet#processMessage(org.nexuse2e.messaging.MessageContext)
     */
    public MessageContext processMessage( MessageContext messageContext ) throws IllegalArgumentException,
            IllegalStateException, NexusException {

        boolean debug = false;
        String labelPrefix = "";
        String debugString = getParameter( DEBUG );
        Boolean sendAsParamBoolean = getParameter( SEND_AS_PARAM );
        boolean sendAsParam = sendAsParamBoolean.booleanValue();

        MessagePojo messagePojo = messageContext.getMessagePojo();
        MessageLabelPojo messageLabelPojo = null;
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

        boolean includeLabels = includeLabelsBoolean.booleanValue() && ( messageLabels != null )
                && ( messageLabels.size() != 0 );

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

        // System.out.println( "start *********************************************************************" );
        if ( debug ) {
            LOG.debug( new LogMessage( "Executing HTTP POST for choreography ID '"
                    + messagePojo.getConversation().getChoreography().getName() + "', conversation ID '"
                    + messagePojo.getConversation().getConversationId() + "', sender '"
                    + messagePojo.getConversation().getPartner().getPartnerId() + "'!",messagePojo) );
            LOG.debug(new LogMessage(  "Sending content as URL-encoded parameter: " + sendAsParam,messagePojo) );
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
            LOG.debug( new LogMessage( "HTTPBackendConnector: Using basic auth.",messagePojo) );
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

                LOG.debug( new LogMessage( "Response status code: " + result,messagePojo) );
                LOG.debug( new LogMessage( "Response status message:\n" + post.getResponseBodyAsString(),messagePojo) );
            } catch ( Exception ex ) {
                LOG.error( new LogMessage( "Error posting inbound message body to '" + getParameter( URL ) + "': " + ex,messagePojo) );
                ex.printStackTrace();
            } finally {
                // Release current connection to the connection pool once you are done
                post.releaseConnection();
            }
        } else {
            for ( MessagePayloadPojo messagePayloadPojo : messagePojo.getMessagePayloads() ) {
                String documentString;
				try {
				    LOG.debug( new LogMessage( "Content Encoding: " + messageContext.getEncoding(),messagePojo) );
				    documentString = new String( messagePayloadPojo.getPayloadData(), messageContext.getEncoding() );
				} catch (UnsupportedEncodingException e1) {
					throw new NexusException(new LogMessage("the configured encoding '"+messageContext.getEncoding()+"' is not supported.",messageContext),e1);
				}

                if ( sendAsParam ) {
                	// TODO (encoding) http parameters doesn't support binary content (Base64 Encoding?)
                    NameValuePair[] data = createHTTPParameters( messagePojo, documentString, messageLabels,
                            labelPrefix );
                    
                    post.setRequestBody( data );
                } else {
                    try {
                        post.setRequestEntity( new StringRequestEntity( documentString, "text/xml", "UTF-8" ) );
                    } catch ( UnsupportedEncodingException e ) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                // Execute request
                try {
                    if ( LOG.isTraceEnabled() ) {
                        LOG.trace(new LogMessage(  "Payload:\n--- PAYLOAD START ---\n" + documentString + "\n---  PAYLOAD END  ---",messagePojo) );
                    }

                    int result = httpclient.executeMethod( post );

                    // Store response in data field of context
                    messageContext.setData( new RequestResponseData( result, post.getResponseBodyAsString(),
                            documentString ) );

                    LOG.debug( new LogMessage( "Response status code: " + result,messagePojo) );
                    LOG.debug( new LogMessage( "Response status message:\n" + post.getResponseBodyAsString(),messagePojo) );
                    if ( LOG.isTraceEnabled() ) {
                        LOG.trace( new LogMessage( "Response:\n--- RESPONSE START ---\n"
                                + ( (RequestResponseData) messageContext.getData() ).getResponseString()
                                + "\n---  RESPONSE END  ---",messagePojo) );
                    }
                    if( result <200 || result >=300) {
                        throw new NexusException( new LogMessage("Error posting inbound message body to '" + getParameter( URL ) + "' HTTP ReturnCode: "+result,messageContext));
                    }
                } catch ( IOException ex ) {
                    throw new NexusException(new LogMessage( "Error posting inbound message body to '" + getParameter( URL ) + "': " + ex,messageContext),ex);
                } finally {
                    // Release current connection to the connection pool once you are done
                    post.releaseConnection();
                }
            } // for
        } // is Ack?

        LOG.debug( new LogMessage( "Done!",messagePojo) );
        return messageContext;
    }

    protected NameValuePair[] createHTTPParameters( MessagePojo messagePojo, String documentString,
            List<MessageLabelPojo> messageLabels, String labelPrefix ) {

        boolean includeLabels = ( messageLabels != null ) && ( messageLabels.size() != 0 );
        
        // Determine whether ack or error message
        if ( messagePojo.getReferencedMessage() != null ) {
            messagePojo = messagePojo.getReferencedMessage();
        }

        NameValuePair[] data = null;
        data = new NameValuePair[8 + ( documentString != null ? 1 : 2 ) + ( includeLabels ? messageLabels.size() : 0 )];

        data[0] = new NameValuePair( org.nexuse2e.integration.Constants.PARAM_CHOREOGRAPY_ID, messagePojo
                .getConversation().getChoreography().getName() );
        data[1] = new NameValuePair( org.nexuse2e.integration.Constants.PARAM_CONVERSATION_ID, messagePojo
                .getConversation().getConversationId() );
        data[2] = new NameValuePair( org.nexuse2e.integration.Constants.PARAM_MESSAGE_ID, messagePojo.getMessageId() );
        data[3] = new NameValuePair( org.nexuse2e.integration.Constants.PARAM_CONVERSATION_TIMESTAMP, messagePojo
                .getConversation().getCreatedDate().toGMTString() );
        data[4] = new NameValuePair( org.nexuse2e.integration.Constants.PARAM_MESSAGE_TIMESTAMP, messagePojo
                .getCreatedDate().toGMTString() );
        data[5] = new NameValuePair( org.nexuse2e.integration.Constants.PARAM_PARTNER_ID, messagePojo.getParticipant()
                .getLocalPartner().getPartnerId() );
        data[6] = new NameValuePair( org.nexuse2e.integration.Constants.PARAM_SENDER_ID, messagePojo.getConversation()
                .getPartner().getPartnerId() );
        data[7] = new NameValuePair( org.nexuse2e.integration.Constants.PARAM_ACTION_ID, messagePojo.getAction()
                .getName() );
        int i = 8;
        if ( documentString != null ) {
            data[i++] = new NameValuePair( org.nexuse2e.integration.Constants.PARAM_CONTENT, documentString );
        } else {

            data[i++] = new NameValuePair( org.nexuse2e.integration.Constants.PARAM_STATUS, ""
                    + messagePojo.getStatus() );
            data[i++] = new NameValuePair( org.nexuse2e.integration.Constants.PARAM_STATUSTEXT, Constants
                    .getMessageStatusString( messagePojo.getStatus() ) );
        }

        if ( includeLabels ) {
            for ( Iterator<MessageLabelPojo> iter = messageLabels.iterator(); iter.hasNext(); ) {
                MessageLabelPojo messageLabelPojo = iter.next();
                LOG.debug( new LogMessage( "Adding label to HTTP call: " + labelPrefix + messageLabelPojo.getLabel() + " - "
                        + messageLabelPojo.getValue(),messagePojo) );
                data[i++] = new NameValuePair( labelPrefix + messageLabelPojo.getLabel(), messageLabelPojo.getValue() );
            }
        }

        return data;
    }
} // HTTPIntegrationPipelet
