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
package org.nexuse2e.service.http;

import java.io.IOException;
import java.net.URL;
import java.security.KeyStore;
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
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.log4j.Logger;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.Constants.Layer;
import org.nexuse2e.configuration.Constants;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.logging.LogMessage;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.pojo.CertificatePojo;
import org.nexuse2e.pojo.ChoreographyPojo;
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

    private TransportSender transportSender;

    @Override
    public void fillParameterMap( Map<String, ParameterDescriptor> parameterMap ) {

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
        try {

            URL receiverURL = new URL( messageContext.getParticipant().getConnection().getUri() );
            String pwd = messageContext.getParticipant().getConnection().getPassword();
            String user = messageContext.getParticipant().getConnection().getLoginName();
            LOG.debug( "ConnectionURL:" + receiverURL );
            client = new HttpClient();
            //TODO: check for https and check isEnforced
            if ( receiverURL.toString().toLowerCase().startsWith( "https" ) ) {

                LOG.debug( "Using SSL" );
                Protocol myhttps;

                if ( LOG.isTraceEnabled() ) {
                    LOG.trace( "participant: " + participant );
                    LOG.trace( "participant.name: " + participant.getPartner().getName() );
                    LOG.trace( "participant.localcerts: " + participant.getLocalCertificate() );
                    if ( participant.getLocalCertificate() != null ) {
                        LOG.trace( "localcert.name: " + participant.getLocalCertificate().getName() );
                    }
                }

                if ( participant.getLocalCertificate() == null ) {
                    LOG.error( "No local certificate selected for using SSL with partner "
                            + participant.getPartner().getName() );
                    throw new NexusException( "No local certificate selected for using SSL with partner "
                            + participant.getPartner().getName() );
                }

                CertificatePojo localCert = Engine.getInstance().getActiveConfigurationAccessService()
                        .getCertificateByNxCertificateId( Constants.CERTIFICATE_TYPE_LOCAL,
                                participant.getLocalCertificate().getNxCertificateId() );
                CertificatePojo metaPojo = Engine.getInstance().getActiveConfigurationAccessService()
                        .getFirstCertificateByType( Constants.CERTIFICATE_TYPE_CACERT_METADATA, true );

                if ( localCert == null ) {
                    LOG.error( "No local certificate selected for using SSL with partner "
                            + participant.getPartner().getName() );
                    throw new NexusException( "No local certificate selected for using SSL with partner "
                            + participant.getPartner().getName() );
                }

                KeyStore privateKeyChain = CertificateUtil.getPKCS12KeyStore( localCert );

                myhttps = new Protocol( "https", (ProtocolSocketFactory) new CertSSLProtocolSocketFactory(
                        privateKeyChain, EncryptionUtil.decryptString( localCert.getPassword() ), Engine.getInstance()
                                .getActiveConfigurationAccessService().getCacertsKeyStore(), EncryptionUtil
                                .decryptString( metaPojo.getPassword() ) ), 443 );

                client.getHostConfiguration().setHost( receiverURL.getHost(), receiverURL.getPort(), myhttps );

            } else {
                client.getHostConfiguration().setHost( receiverURL.getHost(), receiverURL.getPort() );

            }

            client.getHttpConnectionManager().getParams().setConnectionTimeout( timeout );
            client.getHttpConnectionManager().getParams().setSoTimeout( timeout );
            method = new PostMethod( receiverURL.getPath() );
            method.setFollowRedirects( false );
            method.getParams().setSoTimeout( timeout );
            LOG.trace( "Created new NexusHttpConnection with timeout: " + timeout + ", SSL: "
                    + participant.getConnection().isSecure() );

            // Use basic auth if credentials are present
            if ( ( user != null ) && ( user.length() != 0 ) && ( pwd != null ) ) {
                Credentials credentials = new UsernamePasswordCredentials( user, pwd );
                LOG.debug( "HTTPBackendConnector: Using basic auth." );
                client.getState().setCredentials( AuthScope.ANY, credentials );
                method.setDoAuthentication( true );
            }
        } catch ( Exception e ) {
            e.printStackTrace();
            throw new NexusException( "Error creating HTTP POST call: " + e );
        }

        try {
            String httpReply = null;

            if ( LOG.isTraceEnabled() ) {
                LOG.trace( "HTTP Message Data:\n" + new String( (byte[]) messageContext.getData() ) );
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
                LOG.debug( "URI: " + uri );
                method.setRequestEntity( new StringRequestEntity( new String( (byte[]) messageContext.getData() ) ) );
            } else {
                RequestEntity requestEntity = new ByteArrayRequestEntity( (byte[]) messageContext.getData(), "text/xml" );
                method.setRequestEntity( requestEntity );
            }

            client.executeMethod( method );
            LOG.debug( "HTTP call done" );
            int statusCode = method.getStatusCode();
            if ( statusCode > 299 ) {
                LOG.error( new LogMessage( "Message submission failed, server responded with status: " + statusCode,
                        messageContext.getMessagePojo() ) );
                throw new NexusException( "Message submission failed, server responded with status: " + statusCode );
            } else if ( statusCode < 200 ) {
                LOG.warn( new LogMessage( "Partner server responded with status: " + statusCode, messageContext
                        .getMessagePojo() ) );
            }

            httpReply = getHTTPReply( method );
            LOG.debug( "Retrieved HTTP response:" + httpReply );

            method.releaseConnection();

        } catch ( ConnectTimeoutException e ) {
            LOG.warn( new LogMessage( "Message submission failed, connection timeout for URL: "
                    + messageContext.getParticipant().getConnection().getUri() + " - " + e, messageContext
                    .getMessagePojo() ) );
            throw new NexusException( "Message submission failed, connection timeout for URL: " + e );
        } catch ( Exception ex ) {
            LOG.warn( new LogMessage( "Message submission failed: " + ex, messageContext.getMessagePojo() ) );
            throw new NexusException( "Message submission failed: " + ex );
        }
        
        return null;
    }

    /**
     *  Retrieve a reply from an HTTP message post.
     *  @returns String
     */
    public String getHTTPReply( PostMethod method ) throws NexusException {

        StringBuffer reply = new StringBuffer();

        try {
            reply.append( method.getResponseBodyAsString() );
            int responseCode = method.getStatusCode();
            reply.append( "\nHTTP Response Code:  " + responseCode );

        } catch ( IOException ioEx ) {
            ioEx.printStackTrace();
            throw new NexusException( ioEx );
        }
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
