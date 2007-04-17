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
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.log4j.Logger;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.Constants.Runlevel;
import org.nexuse2e.configuration.Constants;
import org.nexuse2e.configuration.ParameterDescriptor;
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
    public Runlevel getActivationRunlevel() {

        return Runlevel.OUTBOUND_PIPELINES;
    }

    public TransportSender getTransportSender() {

        return transportSender;
    }

    public void setTransportSender( TransportSender transportSender ) {

        this.transportSender = transportSender;
    }

    public void sendMessage( MessageContext messagePipeletParameter ) throws NexusException {

        ParticipantPojo participant = messagePipeletParameter.getParticipant();
        int timeout = participant.getConnection().getTimeout() * 1000;
        PostMethod method = null;
        HttpClient client = null;
        try {

            URL receiverURL = new URL( messagePipeletParameter.getParticipant().getConnection().getUri() );
            LOG.debug( "ConnectionURL:" + receiverURL );
            client = new HttpClient();
            //TODO: check for https and check isEnforced
            if ( receiverURL.toString().toLowerCase().startsWith( "https" ) ) {

                LOG.error( "ssl" );
                Protocol myhttps;

                LOG.trace( "participant: " + participant );
                LOG.trace( "participant.name: " + participant.getPartner().getName() );
                LOG.trace( "participant.localcerts: " + participant.getLocalCertificate() );
                LOG.trace( "localcert.name: " + participant.getLocalCertificate().getName() );
                CertificatePojo localCert = Engine.getInstance().getActiveConfigurationAccessService()
                        .getCertificateByNxCertificateId( Constants.CERTIFICATE_TYPE_LOCAL,
                                participant.getLocalCertificate().getNxCertificateId() );
                CertificatePojo metaPojo = Engine.getInstance().getActiveConfigurationAccessService().getFirstCertificateByType(
                        Constants.CERTIFICATE_TYPE_CACERT_METADATA, true );

                KeyStore privateKeyChain = CertificateUtil.getPKCS12KeyStoreFromByteArray( localCert.getBinaryData(),
                        EncryptionUtil.decryptString( localCert.getPassword() ) );

                myhttps = new Protocol( "https", (ProtocolSocketFactory) new CertSSLProtocolSocketFactory(
                        privateKeyChain, EncryptionUtil.decryptString( localCert.getPassword() ), Engine.getInstance()
                                .getActiveConfigurationAccessService().getCacertsKeyStore(), EncryptionUtil.decryptString( metaPojo
                                .getPassword() ) ), 443 );

                client.getHostConfiguration().setHost( receiverURL.getHost(), receiverURL.getPort(), myhttps );

            } else {
                client.getHostConfiguration().setHost( receiverURL.getHost(), receiverURL.getPort() );

            }
            client.getHttpConnectionManager().getParams().setConnectionTimeout( timeout );
            client.getHttpConnectionManager().getParams().setSoTimeout( timeout );
            method = new PostMethod( receiverURL.getPath() );
            method.setFollowRedirects( false );
            method.getParams().setSoTimeout( timeout );
            LOG.debug( "Created new NexusHttpConnection with timeout: " + timeout + ", SSL: "
                    + participant.getConnection().isSecure() );
        } catch ( Exception e ) {
            e.printStackTrace();
            throw new NexusException( "Error creating HTTP POST call: " + e );
        }

        try {
            String httpReply = null;

            LOG.debug( "Data:\n" + new String( (byte[])messagePipeletParameter.getData() ) );

            // Support for HTTP plain
            TRPPojo trpPojo = messagePipeletParameter.getMessagePojo().getTRP();
            if ( trpPojo.getProtocol().equalsIgnoreCase( org.nexuse2e.Constants.PROTOCOL_ID_HTTP_PLAIN ) ) {
                StringBuffer uriParams = new StringBuffer();
                uriParams.append( "ChoreographyID="
                        + messagePipeletParameter.getMessagePojo().getConversation().getChoreography().getName() );
                uriParams.append( "&ActionID="
                        + messagePipeletParameter.getMessagePojo().getConversation().getCurrentAction().getName() );

                ChoreographyPojo choreographyPojo = messagePipeletParameter.getMessagePojo().getConversation()
                        .getChoreography();
                ParticipantPojo participantPojo = Engine.getInstance().getActiveConfigurationAccessService()
                        .getParticipantFromChoreographyByNxPartnerId(
                                choreographyPojo,
                                messagePipeletParameter.getMessagePojo().getConversation().getPartner()
                                        .getNxPartnerId() );
                uriParams.append( "&ParticipantID=" + participantPojo.getLocalPartner().getPartnerId() );
                uriParams.append( "&ConversationID="
                        + messagePipeletParameter.getMessagePojo().getConversation().getConversationId() );
                uriParams.append( "&MessageID=" + messagePipeletParameter.getMessagePojo().getMessageId() );
                URI uri = method.getURI();
                uri.setQuery( uriParams.toString() );
                method.setURI( uri );
                LOG.debug( "URI: " + uri );
                method.setRequestEntity( new StringRequestEntity( new String( (byte[])messagePipeletParameter.getData() ) ) );
            } else {
                ContentType contentType = new ContentType( "multipart/related" );
                contentType.setParameter( "type", "text/xml" );
                contentType.setParameter( "boundary", "MIME_boundary" );
                contentType.setParameter( "start", messagePipeletParameter.getMessagePojo().getMessageId()
                        + messagePipeletParameter.getMessagePojo().getTRP().getProtocol() + "-Header" );

                LOG.debug( "HTTP Message:\n" + new String( (byte[])messagePipeletParameter.getData() ) );
                RequestEntity requestEntity = new ByteArrayRequestEntity( (byte[])messagePipeletParameter.getData(),
                        "Content-Type:" + contentType.toString() );
                method.setRequestEntity( requestEntity );

                method.setRequestHeader( "SOAPAction", "\"ebXML\"" );
                method.setRequestHeader( "Content-Type", contentType.toString() );
            }

            client.executeMethod( method );
            LOG.debug( "HTTP call done" );
            httpReply = getHTTPReply( method );
            LOG.debug( "Retrieved HTTP response:" + httpReply );

            method.releaseConnection();

        } catch ( ConnectTimeoutException e ) {
            LOG.error( "Message submission failed, connection timeout for URL: "
                    + messagePipeletParameter.getParticipant().getConnection().getUri() + " - " + e );
            throw new NexusException( "Message submission failed, connection timeout for URL: " + e );
        } catch ( Exception ex ) {
            LOG.error( "Message submission failed: " + ex );
            throw new NexusException( "Message submission failed: " + ex );
        }
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
