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
package org.nexuse2e.service.ws.aggateway;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.KeyManager;
import javax.net.ssl.TrustManager;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.configuration.security.AuthorizationPolicy;
import org.apache.cxf.configuration.security.FiltersType;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.apache.log4j.Logger;
import org.apache.ws.security.WSConstants;
import org.apache.ws.security.WSPasswordCallback;
import org.apache.ws.security.handler.WSHandlerConstants;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.Constants.BeanStatus;
import org.nexuse2e.Constants.Layer;
import org.nexuse2e.configuration.Constants;
import org.nexuse2e.configuration.ListParameter;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.Constants.ParameterType;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.pojo.ActionPojo;
import org.nexuse2e.pojo.CertificatePojo;
import org.nexuse2e.pojo.MessagePayloadPojo;
import org.nexuse2e.pojo.MessagePojo;
import org.nexuse2e.pojo.ParticipantPojo;
import org.nexuse2e.service.AbstractService;
import org.nexuse2e.service.SenderAware;
import org.nexuse2e.service.ws.aggateway.wsdl.DocExchangeFault;
import org.nexuse2e.service.ws.aggateway.wsdl.DocExchangePortType;
import org.nexuse2e.service.ws.aggateway.wsdl.InboundData;
import org.nexuse2e.service.ws.aggateway.wsdl.OutboundData;
import org.nexuse2e.service.ws.aggateway.wsdl.XmlPayload;
import org.nexuse2e.transport.TransportSender;
import org.nexuse2e.util.CertificateUtil;
import org.nexuse2e.util.EncryptionUtil;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Service that acts as an Ag Gateway compliant web service client.
 *
 * @author Markus Breilmann
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public class WSClientService extends AbstractService implements SenderAware {

    private static Logger   LOG = Logger.getLogger( WSClientService.class );

    private static final String AUTH_TYPE_PARAM_NAME = "authType";
    
    private TransportSender transportSender;

    @Override
    public void fillParameterMap( Map<String, ParameterDescriptor> parameterMap ) {

        ListParameter authTypeDrowdown = new ListParameter();
        authTypeDrowdown.addElement( "Basic authentication (HTTP)", "basic" );
        authTypeDrowdown.addElement( "WS* authentication", "ws" );
        parameterMap.put( AUTH_TYPE_PARAM_NAME, new ParameterDescriptor( ParameterType.LIST, "Authentication type",
                "The client authentication type", authTypeDrowdown ) );
    }

    @Override
    public Layer getActivationLayer() {

        return Layer.INBOUND_PIPELINES;
    }

    public TransportSender getTransportSender() {

        return transportSender;
    }

    public MessageContext sendMessage( MessageContext messageContext ) throws NexusException {

        MessageContext replyMessageContext = null;

        if ( getStatus() != BeanStatus.STARTED ) {
            throw new NexusException( "Service " + getClass().getSimpleName() + " not started" );
        }

        String receiverURL = messageContext.getParticipant().getConnection().getUri();

        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();

        MessagePojo messagePojo = messageContext.getMessagePojo();

        factory.setServiceClass( DocExchangePortType.class );
        factory.setServiceName( new QName( "urn:aggateway:names:ws:docexchange", "AgGatewayDocumentExchange" ) );
        factory.setWsdlLocation( "classpath:org/nexuse2e/integration/AgGateway.wsdl" );
        factory.setAddress( receiverURL );
        DocExchangePortType theDocExchangePortType = (DocExchangePortType) factory.create();

        Client cxfClient = ClientProxy.getClient( theDocExchangePortType );

        HTTPConduit httpConduit = (HTTPConduit) cxfClient.getConduit();

        HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
        httpClientPolicy.setConnectionTimeout( messagePojo.getParticipant().getConnection().getTimeout() * 1000 );
        httpConduit.setClient( httpClientPolicy );

        // HTTP basic auth
        String username = messageContext.getMessagePojo().getParticipant().getConnection().getLoginName();
        final String password = messageContext.getMessagePojo().getParticipant().getConnection().getPassword();
        if ( !StringUtils.isEmpty( username ) ) {
            ListParameter authType = getParameter( AUTH_TYPE_PARAM_NAME );
            if (authType != null && "ws".equals( authType.getSelectedValue() )) {
                // WS-Security - User Tokens
                Endpoint cxfEndpoint = cxfClient.getEndpoint();

                Map<String, Object> outProps = new HashMap<String, Object>();
                outProps.put( WSHandlerConstants.ACTION, WSHandlerConstants.USERNAME_TOKEN );
                // Specify our username
                outProps.put( WSHandlerConstants.USER, username );
                // Password type : plain text
                outProps.put( WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_TEXT );
                // Callback used to retrieve password for given user.
                CallbackHandler callback = new CallbackHandler() {
                    public void handle( Callback[] callbacks ) throws IOException, UnsupportedCallbackException {
                        WSPasswordCallback pc = (WSPasswordCallback) callbacks[0];
                        // set the password for our message.
                        pc.setPassword( password );
                    }
                };
                outProps.put( WSHandlerConstants.PW_CALLBACK_REF, callback );
                WSS4JOutInterceptor wssOut = new WSS4JOutInterceptor( outProps );
                LoggingOutInterceptor loggingOutInterceptor = new LoggingOutInterceptor();
                cxfEndpoint.getOutInterceptors().add( loggingOutInterceptor );
                cxfEndpoint.getOutInterceptors().add( wssOut );
            } else {
                // basic auth
                LOG.debug( "Using basic auth" );
                factory.setUsername( username );
                factory.setPassword( password );

                AuthorizationPolicy authorizationPolicy = httpConduit.getAuthorization();
                if ( authorizationPolicy == null ) {
                    authorizationPolicy = new AuthorizationPolicy();
                    httpConduit.setAuthorization( authorizationPolicy );
                }
                authorizationPolicy.setUserName( username );
                authorizationPolicy.setPassword( password );
                LOG.debug( "Credentials set" );
            }
        } else {
            LOG.debug( "Not using auth" );
        }

        // Enable SSL, see also http://cwiki.apache.org/confluence/display/CXF20DOC/Client+HTTP+Transport+%28including+SSL+support%29
        if ( messageContext.getMessagePojo().getParticipant().getConnection().isSecure() ) {
            try {

                CertificatePojo localCert = Engine.getInstance().getActiveConfigurationAccessService()
                        .getCertificateByNxCertificateId( Constants.CERTIFICATE_TYPE_LOCAL,
                                messagePojo.getParticipant().getLocalCertificate().getNxCertificateId() );
                KeyStore privateKeyChain = CertificateUtil.getPKCS12KeyStore( localCert );
                KeyManager[] keyManagers = CertificateUtil.createKeyManagers( privateKeyChain, EncryptionUtil
                        .decryptString( localCert.getPassword() ) );
                TrustManager[] trustManagers = CertificateUtil.createTrustManagers( Engine.getInstance()
                        .getActiveConfigurationAccessService().getCacertsKeyStore() );

                FiltersType filters = new FiltersType();
                filters.getInclude().add( ".*_EXPORT_.*" );
                filters.getInclude().add( ".*_EXPORT1024_.*" );
                filters.getInclude().add( ".*_WITH_DES_.*" );

                TLSClientParameters tlsClientParameters = new TLSClientParameters();
                tlsClientParameters.setCipherSuitesFilter( filters );
                tlsClientParameters.setTrustManagers( trustManagers );
                tlsClientParameters.setKeyManagers( keyManagers );

                httpConduit.setTlsClientParameters( tlsClientParameters );

            } catch ( Exception e ) {
                throw new NexusException( e );
            }
        }

        replyMessageContext = new MessageContext();
        try {
            replyMessageContext.setProtocolSpecificKey( messageContext.getProtocolSpecificKey() );
            replyMessageContext.setActionSpecificKey( messageContext.getActionSpecificKey() );
            replyMessageContext.setConversation( messageContext.getConversation() );
            replyMessageContext.setMessagePojo( (MessagePojo) messagePojo.clone() );
            replyMessageContext.getMessagePojo().getMessagePayloads().clear();
        } catch ( CloneNotSupportedException e ) {
            LOG.error( "Error cloning outbound message: " + e );
        }

        ParticipantPojo participant = messagePojo.getParticipant();
        for ( MessagePayloadPojo payload : messagePojo.getMessagePayloads() ) {
            LOG.trace( "Calling web service at: " + receiverURL );

            InboundData inboundData = new InboundData();
            inboundData.setBusinessProcess( messagePojo.getConversation().getChoreography().getName() );
            inboundData.setProcessStep( messagePojo.getConversation().getCurrentAction().getName() );
            inboundData.setPartnerId( participant.getLocalPartner().getPartnerId() );
            inboundData.setPartnerType( participant.getLocalPartner().getPartnerIdType() );
            inboundData.setConversationId( messagePojo.getConversation().getConversationId() );
            inboundData.setMessageId( messagePojo.getMessageId() );
            XmlPayload p = new XmlPayload();
            Document d;
            try {
                d = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse( new ByteArrayInputStream( payload.getPayloadData() ) );
                p.setAny( d.getDocumentElement() );
            } catch (SAXException e) {
                throw new NexusException( e );
            } catch (IOException e) {
                throw new NexusException( e );
            } catch (ParserConfigurationException e) {
                throw new NexusException( e );
            }
            inboundData.setXmlPayload( p );

            try {
                OutboundData outboundData = theDocExchangePortType.execute( inboundData );

                
                if ( outboundData.getXmlPayload() != null ) {
                    List<MessagePayloadPojo> messagePayloads = new ArrayList<MessagePayloadPojo>( 1 );
                    for (XmlPayload xmlPayload : outboundData.getXmlPayload()) {
                        LOG.trace( "Returned document:\n" + xmlPayload );
                        MessagePayloadPojo messagePayloadPojo = new MessagePayloadPojo();
                        messagePayloadPojo.setMessage( replyMessageContext.getMessagePojo() );
                        messagePayloadPojo.setContentId( Engine.getInstance().getIdGenerator(
                                Constants.ID_GENERATOR_MESSAGE_PAYLOAD ).getId() );
                        messagePayloadPojo.setMimeType( "text/xml" );
                        Object data = xmlPayload.getAny();
                        messagePayloadPojo.setPayloadData( data == null ? null : data.toString().getBytes() );
                        messagePayloads.add( messagePayloadPojo );
                    }
                    replyMessageContext.getMessagePojo().setMessagePayloads( messagePayloads );

                    if ( outboundData.getProcessStep() != null ) {
                        replyMessageContext.getMessagePojo().setMessageId( outboundData.getMessageId() );
                    }

                    if ( outboundData.getMessageId() != null ) {
                        ActionPojo action = Engine.getInstance().getActiveConfigurationAccessService()
                                .getActionFromChoreographyByActionId(
                                        replyMessageContext.getMessagePojo().getConversation().getChoreography(),
                                        outboundData.getProcessStep() );
                        replyMessageContext.getMessagePojo().setAction( action );
                    } else {
                        replyMessageContext.getMessagePojo().setMessageId( null );
                    }
                }

            } catch ( DocExchangeFault e ) {
                LOG.error( "Error calling web service: " + e );
                throw new NexusException( e );
            }
        }

        if ( replyMessageContext.getMessagePojo().getMessagePayloads().isEmpty() ) {
            replyMessageContext = null;
        }

        return replyMessageContext;
    }

    public void setTransportSender( TransportSender transportSender ) {

        this.transportSender = transportSender;
    }
}
