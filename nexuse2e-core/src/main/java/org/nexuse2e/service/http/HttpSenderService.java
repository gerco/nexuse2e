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
package org.nexuse2e.service.http;

import java.net.URL;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Map;

import javax.mail.internet.ContentType;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.log4j.Logger;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.Constants.Layer;
import org.nexuse2e.configuration.Constants;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.Constants.ParameterType;
import org.nexuse2e.logging.LogMessage;
import org.nexuse2e.messaging.FrontendPipeline;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.pojo.CertificatePojo;
import org.nexuse2e.pojo.ChoreographyPojo;
import org.nexuse2e.pojo.MessagePayloadPojo;
import org.nexuse2e.pojo.MessagePojo;
import org.nexuse2e.pojo.ParticipantPojo;
import org.nexuse2e.pojo.TRPPojo;
import org.nexuse2e.service.AbstractService;
import org.nexuse2e.service.SenderAware;
import org.nexuse2e.transport.TransportSender;
import org.nexuse2e.util.CertSSLProtocolSocketFactory;
import org.nexuse2e.util.CertificateUtil;
import org.nexuse2e.util.EncryptionUtil;

/**
 * A service that can be used by a <code>TransportSender</code> in order
 * to send messages via HTTP.
 * 
 * @author gesch, jonas.reese
 */
public class HttpSenderService extends AbstractService implements SenderAware {

    private static Logger   LOG = Logger.getLogger( HttpSenderService.class );

    /** PARAMETERS **/
    public static final String PREEMPTIVE_AUTH_PARAM_NAME = "preemptiveAuth";
    
    private TransportSender transportSender;

    @Override
    public void fillParameterMap( Map<String, ParameterDescriptor> parameterMap ) {
        parameterMap.put( PREEMPTIVE_AUTH_PARAM_NAME, new ParameterDescriptor(
            ParameterType.BOOLEAN, "Preemptive Authentication",
            "Check, if the HTTP client should use preemtive authentication.", Boolean.FALSE ) );
    }

    @Override
    public Layer getActivationLayer() {

        return Layer.OUTBOUND_PIPELINES;
    }

    public TransportSender getTransportSender() {

        return transportSender;
    }

    public void setTransportSender( TransportSender transportSender ) {

        this.transportSender = transportSender;
    }

    public MessageContext sendMessage( MessageContext messageContext ) throws NexusException {

        ParticipantPojo participant = messageContext.getParticipant();
        int timeout = participant.getConnection().getTimeout() * 1000;
        PostMethod method = null;
        HttpClient client = null;
        
        MessageContext returnMessageContext = null;
        try {

            URL receiverURL = new URL( messageContext.getParticipant().getConnection().getUri() );
            String pwd = messageContext.getParticipant().getConnection().getPassword();
            String user = messageContext.getParticipant().getConnection().getLoginName();
            LOG.debug( new LogMessage("ConnectionURL:" + receiverURL,messageContext.getMessagePojo()) );
            client = new HttpClient();
            //TODO: check for https and check isEnforced
            if ( receiverURL.toString().toLowerCase().startsWith( "https" ) ) {

                LOG.debug( "Using SSL" );
                Protocol myhttps;

                if ( LOG.isTraceEnabled() ) {
                    LOG.trace( new LogMessage("participant: " + participant,messageContext.getMessagePojo()) );
                    LOG.trace( new LogMessage("participant.name: " + participant.getPartner().getName(),messageContext.getMessagePojo()) );
                    LOG.trace( new LogMessage("participant.localcerts: " + participant.getLocalCertificate(),messageContext.getMessagePojo()) );
                    if ( participant.getLocalCertificate() != null ) {
                        LOG.trace( new LogMessage("localcert.name: " + participant.getLocalCertificate().getName(),messageContext.getMessagePojo()) );
                    }
                }

                CertificatePojo localCert = participant.getLocalCertificate();
                if ( localCert == null ) {
                    LOG.error( new LogMessage("No local certificate selected for using SSL with partner "
                            + participant.getPartner().getName(),messageContext.getMessagePojo()) );
                    throw new NexusException( "No local certificate selected for using SSL with partner "
                            + participant.getPartner().getName() );
                }

                CertificatePojo partnerCert = participant.getConnection().getCertificate();
                CertificatePojo metaPojo = Engine.getInstance().getActiveConfigurationAccessService()
                        .getFirstCertificateByType( Constants.CERTIFICATE_TYPE_CACERT_METADATA, true );

                String cacertspwd = "changeit";
                if(metaPojo != null) {
                	cacertspwd = EncryptionUtil.decryptString( metaPojo.getPassword() );
                }
                KeyStore privateKeyChain = CertificateUtil.getPKCS12KeyStore( localCert );

                myhttps = new Protocol( "https",
                        (ProtocolSocketFactory) new CertSSLProtocolSocketFactory(
                                privateKeyChain, EncryptionUtil.decryptString( localCert.getPassword() ),
                                Engine.getInstance().getActiveConfigurationAccessService().getCacertsKeyStore(),
                                cacertspwd, partnerCert ), 443 );

                client.getHostConfiguration().setHost( receiverURL.getHost(), receiverURL.getPort(), myhttps );

            } else {
                client.getHostConfiguration().setHost( receiverURL.getHost(), receiverURL.getPort() );

            }

            client.getHttpConnectionManager().getParams().setConnectionTimeout( timeout );
            client.getHttpConnectionManager().getParams().setSoTimeout( timeout );
            method = new PostMethod( receiverURL.getPath() );
            method.setFollowRedirects( false );
            method.getParams().setSoTimeout( timeout );
            LOG.trace( new LogMessage("Created new NexusHttpConnection with timeout: " + timeout + ", SSL: "
                    + participant.getConnection().isSecure(),messageContext.getMessagePojo()) );

            // Use basic auth if credentials are present
            if ( ( user != null ) && ( user.length() != 0 ) && ( pwd != null ) ) {
                Credentials credentials = new UsernamePasswordCredentials( user, pwd );
                LOG.debug( new LogMessage("HTTPBackendConnector: Using basic auth.",messageContext.getMessagePojo()) );
                client.getParams().setAuthenticationPreemptive( (Boolean) getParameter( PREEMPTIVE_AUTH_PARAM_NAME ) );
                client.getState().setCredentials( AuthScope.ANY, credentials );
                method.setDoAuthentication( true );
            }
        } catch ( Exception e ) {
            e.printStackTrace();
            throw new NexusException( "Error creating HTTP POST call: " + e, e );
        }

        try {
            String httpReply = null;

            if ( LOG.isTraceEnabled() ) {
                LOG.trace(new LogMessage("HTTP Message Data:\n" + (messageContext.getData() == null ? null : new String((byte[]) messageContext.getData())), messageContext));
            }

            // Support for HTTP plain
            TRPPojo trpPojo = messageContext.getMessagePojo().getTRP();
            if ( trpPojo.getProtocol().equalsIgnoreCase( org.nexuse2e.Constants.PROTOCOL_ID_EBXML ) ) {

                String contentTypeString = null;

                /* No bug with wrapping the content type, see section 2.2 of http://www.ietf.org/rfc/rfc2616.txt */
                ContentType contentType = new ContentType( "multipart/related" );
                contentType.setParameter( "type", "text/xml" );
                contentType.setParameter( "boundary", "MIME_boundary" );
                contentType.setParameter( "start", messageContext.getMessagePojo().getMessageId()
                        + messageContext.getMessagePojo().getTRP().getProtocol() + "-Header" );

                // LOG.trace( "********* Content-Type:" + contentType.toString() );
                contentTypeString = contentType.toString();

                /* Alternative implementation for content type
                StringBuffer buffer = new StringBuffer( "multipart/related" );
                ParameterFormatter tempParameterFormatter = new ParameterFormatter();
                buffer.append( "; " );
                tempParameterFormatter.format( buffer, new NameValuePair( "type", "text/xml" ) );
                buffer.append( "; " );
                tempParameterFormatter.format( buffer, new NameValuePair( "boundary", "MIME_boundary" ) );
                buffer.append( "; " );
                tempParameterFormatter.format( buffer, new NameValuePair( "start", messageContext.getMessagePojo()
                        .getMessageId()
                        + messageContext.getMessagePojo().getTRP().getProtocol() + "-Header" ) );

                contentTypeString = buffer.toString();
                 */

                // LOG.trace( "********* NEW Content-Type:" + contentTypeString );
                
                
                
                
                
                RequestEntity requestEntity = new ByteArrayRequestEntity( (byte[]) messageContext.getData(),
                        "Content-Type:" + contentTypeString );

                method.setRequestEntity( requestEntity );

                method.setRequestHeader( "SOAPAction", "\"ebXML\"" );
                method.setRequestHeader( "Content-Type", contentTypeString );
            } else if ( trpPojo.getProtocol().equalsIgnoreCase( org.nexuse2e.Constants.PROTOCOL_ID_HTTP_PLAIN ) ) {
                StringBuffer uriParams = new StringBuffer();
                uriParams.append( "ChoreographyID="
                        + messageContext.getMessagePojo().getConversation().getChoreography().getName() );
                uriParams.append( "&ActionID="
                        + messageContext.getMessagePojo().getConversation().getCurrentAction().getName() );

                ChoreographyPojo choreographyPojo = messageContext.getMessagePojo().getConversation().getChoreography();
                ParticipantPojo participantPojo = Engine.getInstance().getActiveConfigurationAccessService()
                        .getParticipantFromChoreographyByNxPartnerId( choreographyPojo,
                                messageContext.getMessagePojo().getConversation().getPartner().getNxPartnerId() );
                uriParams.append( "&ParticipantID=" + participantPojo.getLocalPartner().getPartnerId() );
                uriParams.append( "&ConversationID="
                        + messageContext.getMessagePojo().getConversation().getConversationId() );
                uriParams.append( "&MessageID=" + messageContext.getMessagePojo().getMessageId() );
                URI uri = method.getURI();
                uri.setQuery( uriParams.toString() );
                method.setURI( uri );
                LOG.debug(new LogMessage( "URI: " + uri,messageContext.getMessagePojo()) );
                // TODO (encoding) http plain should use ByteArrayRequestEntity / InputStreamRequestEnity? Content Type ?
                method.setRequestEntity( new ByteArrayRequestEntity( (byte[]) messageContext.getData() ) );
            } else {
                RequestEntity requestEntity = new ByteArrayRequestEntity( (byte[]) messageContext.getData(), "text/xml" );
                method.setRequestEntity( requestEntity );
            }

            client.executeMethod( method );
            LOG.debug(new LogMessage( "HTTP call done",messageContext.getMessagePojo()) );
            int statusCode = method.getStatusCode();
            if ( statusCode > 299 ) {
                LogMessage lm = new LogMessage(
                        "Message submission failed, server " + messageContext.getParticipant().getConnection().getUri() +
                        " responded with status: " + statusCode, messageContext.getMessagePojo() ); 
                LOG.error( lm );
                throw new NexusException( lm );
            } else if ( statusCode < 200 ) {
                LOG.warn( new LogMessage( "Partner server " + messageContext.getParticipant().getConnection().getUri() +
                        " responded with status: " + statusCode, messageContext.getMessagePojo() ) );
            }

            
            boolean processReturn = (transportSender != null &&
                    transportSender.getPipeline() instanceof FrontendPipeline &&
                    messageContext.isProcessThroughReturnPipeline() &&
                    ((FrontendPipeline) transportSender.getPipeline()).getReturnPipelets() != null &&
                    ((FrontendPipeline) transportSender.getPipeline()).getReturnPipelets().length > 0);
            
            if (processReturn || LOG.isTraceEnabled()) {
                byte[] body = method.getResponseBody();
                
                if (LOG.isTraceEnabled()) {
                    httpReply = getHTTPReply( body, statusCode );
                    LOG.trace(new LogMessage( "Retrieved HTTP response:" + httpReply, messageContext.getMessagePojo()) );
                }
                
                if (processReturn) {
                    MessagePojo message = (MessagePojo) messageContext.getMessagePojo().clone();
                    message.setOutbound(false);
                    message.setStatus(Constants.MESSAGE_STATUS_UNKNOWN);
                    message.setEndDate(null);
                    message.setRetries(0);
                    message.setMessageId(null);
                    // important: Payload needs to be reset, shall be set from data field by pipeline processing
                    message.setMessagePayloads(new ArrayList<MessagePayloadPojo>());
                    returnMessageContext = Engine.getInstance().getTransactionService().createMessageContext(message);
                    returnMessageContext.setData(body);
                    returnMessageContext.setOriginalMessagePojo(messageContext.getMessagePojo());
                }
            }

            method.releaseConnection();

        } catch ( ConnectTimeoutException e ) {
            LogMessage lm =  new LogMessage( "Message submission failed, connection timeout for URL: "
                    + messageContext.getParticipant().getConnection().getUri() + " - " + e, messageContext
                    .getMessagePojo() );
            LOG.warn( lm, e );
            throw new NexusException( lm, e );
        } catch ( Exception ex ) {
            LogMessage lm = new LogMessage( "Message submission failed: " + ex, messageContext.getMessagePojo() );
            LOG.warn( lm, ex );
            throw new NexusException( lm, ex );
        }
        
        return returnMessageContext;
    }

    /**
     *  Retrieve a reply from an HTTP message post.
     *  @returns String
     */
    public String getHTTPReply( byte[] responseBody, int responseCode ) throws NexusException {

        StringBuffer reply = new StringBuffer();

        if (responseBody != null) {
            reply.append( new String( responseBody ) );
            reply.append( "\n" );
        }
        reply.append( "HTTP Response Code:  " + responseCode );

        return reply.toString();
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#teardown()
     */
    public void teardown() {

        super.teardown();

        transportSender = null;
    } // teardown

}
