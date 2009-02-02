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
import java.io.StringWriter;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

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
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsClientFactoryBean;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.apache.log4j.Logger;
import org.apache.ws.security.WSConstants;
import org.apache.ws.security.WSPasswordCallback;
import org.apache.ws.security.handler.WSHandlerConstants;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.Constants.BeanStatus;
import org.nexuse2e.Constants.Layer;
import org.nexuse2e.configuration.Constants;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.configuration.ListParameter;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.Constants.ParameterType;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.pojo.ActionPojo;
import org.nexuse2e.pojo.CertificatePojo;
import org.nexuse2e.pojo.ChoreographyPojo;
import org.nexuse2e.pojo.MessagePayloadPojo;
import org.nexuse2e.pojo.MessagePojo;
import org.nexuse2e.pojo.ParticipantPojo;
import org.nexuse2e.service.AbstractService;
import org.nexuse2e.service.SenderAware;
import org.nexuse2e.service.ws.aggateway.wsdl.DocExchangePortType;
import org.nexuse2e.service.ws.aggateway.wsdl.InboundData;
import org.nexuse2e.service.ws.aggateway.wsdl.OutboundData;
import org.nexuse2e.service.ws.aggateway.wsdl.XmlPayload;
import org.nexuse2e.transport.TransportSender;
import org.nexuse2e.util.CertificateUtil;
import org.nexuse2e.util.EncryptionUtil;
import org.nexuse2e.util.FileUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
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
    private static final String SEND_RESPONSE_TO_FRONTEND_PARAM_NAME = "sendResponseToFrontend";
    private static final String EXCEPTIONS_PARAM_NAME = "exceptions";
    
    private TransportSender transportSender;

    @Override
    public void fillParameterMap( Map<String, ParameterDescriptor> parameterMap ) {

        ListParameter authTypeDrowdown = new ListParameter();
        authTypeDrowdown.addElement( "Basic authentication (HTTP)", "basic" );
        authTypeDrowdown.addElement( "WS* authentication", "ws" );
        parameterMap.put( AUTH_TYPE_PARAM_NAME, new ParameterDescriptor( ParameterType.LIST, "Authentication type",
                "The client authentication type", authTypeDrowdown ) );
        parameterMap.put( SEND_RESPONSE_TO_FRONTEND_PARAM_NAME, new ParameterDescriptor( ParameterType.BOOLEAN, "Process response",
                "Activate in order to process the WS response through a frontend inbound pipeline", Boolean.TRUE ) );
        parameterMap.put( EXCEPTIONS_PARAM_NAME, new ParameterDescriptor( ParameterType.STRING, "Exceptions",
                "Comma-separated list of action names (process steps) that shall/shall not be processed through a frontend inbound pipeline", "" ) );
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

        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean(new JaxWsClientFactoryBean());

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
                cxfEndpoint.getOutInterceptors().add( wssOut );
                if (LOG.isTraceEnabled()) {
                    cxfEndpoint.getOutInterceptors().add( new LoggingOutInterceptor() );
                    cxfEndpoint.getInInterceptors().add( new LoggingInInterceptor() );
                }
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

                ParticipantPojo participant = messagePojo.getParticipant();
                if (participant == null || participant.getLocalCertificate() == null) {
                    throw new NexusException( "Connection to participant " + participant.getDescription() + " configured 'secure', but no certificate was selected" );
                }
                CertificatePojo localCert = participant.getLocalCertificate();
                CertificatePojo partnerCert = messagePojo.getParticipant().getConnection().getCertificate();
                KeyStore caCerts = Engine.getInstance().getActiveConfigurationAccessService().getCacertsKeyStore();
                KeyStore privateKeyChain = CertificateUtil.getPKCS12KeyStore( localCert );
                KeyManager[] keyManagers = CertificateUtil.createKeyManagers(
                        privateKeyChain, EncryptionUtil.decryptString( localCert.getPassword() ) );
                TrustManager[] trustManagers = CertificateUtil.createTrustManagers( caCerts, partnerCert );

                FiltersType filters = new FiltersType();
                filters.getInclude().add( ".*_EXPORT_.*" );
                filters.getInclude().add( ".*_EXPORT1024_.*" );
                filters.getInclude().add( ".*_WITH_DES_.*" );

                TLSClientParameters tlsClientParameters = new TLSClientParameters();
                tlsClientParameters.setCipherSuitesFilter( filters );
                tlsClientParameters.setTrustManagers( trustManagers );
                tlsClientParameters.setKeyManagers( keyManagers );

                httpConduit.setTlsClientParameters( tlsClientParameters );

            } catch (NexusException nex) {
                throw nex;
            } catch ( Exception e ) {
                throw new NexusException( e );
            }
        }

        replyMessageContext = new MessageContext();
        replyMessageContext.setRequestMessage( messageContext );
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
        LOG.trace( "Calling web service at: " + receiverURL );

        for ( MessagePayloadPojo payload : messagePojo.getMessagePayloads() ) {

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
                DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
                builderFactory.setNamespaceAware( true );
                d = builderFactory.newDocumentBuilder().parse( new ByteArrayInputStream( payload.getPayloadData() ) );
                Element element = d.getDocumentElement();
                p.setAny( element );
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

                // process messageContext through applicable frontend inbound pipeline
                replyMessageContext = initializeReplyMessageContext(
                        replyMessageContext,
                        outboundData,
                        messageContext.getConversation().getConversationId(),
                        messageContext.getChoreography().getName() );
            } catch ( Exception e ) {
                messageContext.getMessagePojo().setStatus( Constants.MESSAGE_STATUS_FAILED );
                messageContext.getMessagePojo().getConversation().setStatus( Constants.CONVERSATION_STATUS_ERROR );
                LOG.error( "Error calling web service: ", e );
                throw new NexusException( e );
            }
        }
        return replyMessageContext;
    }
    
    private boolean filtered( String actionId ) {
        
        String exceptions = getParameter( EXCEPTIONS_PARAM_NAME );
        boolean match = false;
        if (exceptions != null) {
            StringTokenizer st = new StringTokenizer( exceptions, "," );
            while (st.hasMoreTokens()) {
                String s = st.nextToken().trim();
                if (actionId.matches( FileUtil.dosStyleToRegEx( s ) )) {
                    match = true;
                    break;
                }
            }
        }
        
        Boolean b = getParameter( SEND_RESPONSE_TO_FRONTEND_PARAM_NAME );
        return !(match ^ (b == null || b.booleanValue()));
    }
    
    private MessageContext initializeReplyMessageContext(
            MessageContext replyMessageContext,
            OutboundData outboundData,
            String conversationId,
            String choreographyId ) throws NexusException {

        String actionId = outboundData.getProcessStep();
        
        String messageId = outboundData.getMessageId();
        MessagePojo message = replyMessageContext.getMessagePojo();
        EngineConfiguration config = Engine.getInstance().getCurrentConfiguration();
        ChoreographyPojo choreography = config.getChoreographyByChoreographyId( choreographyId );
        if (choreography == null) {
            throw new NexusException( "Choreography " + choreographyId + " was not found" );
        }
        ActionPojo action = null;
        if (actionId == null || filtered( actionId )) {
            LOG.info( "Message with message ID " + outboundData.getMessageId() +
                    " (" + outboundData.getProcessStep() + ") filtered, not processing through return pipeline." );
            replyMessageContext.setProcessThroughReturnPipeline( false );
        } else {
            action = config.getActionFromChoreographyByActionId( choreography, actionId );
            if (action == null) {
                throw new NexusException( "Action " + actionId + " was not found in choreography " + choreographyId );
            }
        }

        message.setAction( action );
        message.setCreatedDate( new Date() );
        message.setMessageId( messageId );
        message.setOutbound( false );
        replyMessageContext.setMessagePojo( message );
        replyMessageContext.setOriginalMessagePojo( message );
        if (action != null) {
            replyMessageContext.setChoreography( action.getChoreography() );
        }
        if (message != null && message.getConversation() != null) {
            replyMessageContext.setPartner( message.getConversation().getPartner() );
        }
        
        if ( outboundData.getXmlPayload() != null ) {

            List<MessagePayloadPojo> messagePayloads = new ArrayList<MessagePayloadPojo>( 1 );
            int sn = 1;
            for (XmlPayload xmlPayload : outboundData.getXmlPayload()) {
                String document = null;
                try {
                    OutputFormat outputFormat = new OutputFormat( "XML", "UTF-8", true );
                    StringWriter writer = new StringWriter();
                    XMLSerializer xmlSerializer = new XMLSerializer( writer, outputFormat );
                    xmlSerializer.asDOMSerializer();

                    Element element = (Element) xmlPayload.getAny();

                    if (element != null) {
                        // serialize the document
                        xmlSerializer.setNamespaces( true );
                        xmlSerializer.serialize( element.getOwnerDocument() );
                        document = writer.getBuffer().toString();
                    }
                } catch (IOException e) {
                    throw new NexusException( e );
                }
                if (document != null) {
                    LOG.trace( "Returned document:\n" + document );
                    MessagePayloadPojo messagePayloadPojo = new MessagePayloadPojo();
                    messagePayloadPojo.setSequenceNumber( sn++ );
                    messagePayloadPojo.setMessage( replyMessageContext.getMessagePojo() );
                    messagePayloadPojo.setContentId( Engine.getInstance().getIdGenerator(
                            Constants.ID_GENERATOR_MESSAGE_PAYLOAD ).getId() );
                    messagePayloadPojo.setMimeType( "text/xml" );
                    messagePayloadPojo.setPayloadData( (document == null ? null : document.getBytes()) );
                    messagePayloads.add( messagePayloadPojo );
                } else {
                    LOG.trace( "Empty document returned" );
                }
            }
            replyMessageContext.getMessagePojo().setMessagePayloads( messagePayloads );

            if ( outboundData.getProcessStep() != null ) {
                replyMessageContext.getMessagePojo().setMessageId( outboundData.getMessageId() );
            }
        }

        
        return replyMessageContext;
    }

    public void setTransportSender( TransportSender transportSender ) {

        this.transportSender = transportSender;
    }
}
