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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.Endpoint;

import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;
import org.apache.log4j.Logger;
import org.apache.ws.security.WSConstants;
import org.apache.ws.security.WSPasswordCallback;
import org.apache.ws.security.handler.WSHandlerConstants;
import org.nexuse2e.DynamicWSDispatcherServlet;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.Constants.BeanStatus;
import org.nexuse2e.Constants.Layer;
import org.nexuse2e.configuration.NexusUUIDGenerator;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.Constants.ParameterType;
import org.nexuse2e.messaging.Constants;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.pojo.MessagePayloadPojo;
import org.nexuse2e.pojo.MessagePojo;
import org.nexuse2e.service.AbstractService;
import org.nexuse2e.service.ReceiverAware;
import org.nexuse2e.service.ws.aggateway.wsdl.DocExchangeFault;
import org.nexuse2e.service.ws.aggateway.wsdl.DocExchangePortType;
import org.nexuse2e.service.ws.aggateway.wsdl.InboundData;
import org.nexuse2e.service.ws.aggateway.wsdl.OutboundData;
import org.nexuse2e.service.ws.aggateway.wsdl.XmlPayload;
import org.nexuse2e.transport.TransportReceiver;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * A service that dynamically registers a Ag Gateway compliant web service endpoint.
 *
 * @author Markus Breilmann
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public class WSDispatcherService extends AbstractService implements ReceiverAware {

    private static Logger       LOG            = Logger.getLogger( WSDispatcherService.class );

    private static final String URL_PARAM_NAME = "url";
    private static final String WS_AUTH_PARAM_NAME = "wsAuth";
    private static final String USERNAME_PARAM_NAME = "user";
    private static final String PASSWORD_PARAM_NAME = "password";
    private static final String CACHE_DOM_TREE_PARAM_NAME = "cacheDomTree";

    private Endpoint            endpoint;
    private TransportReceiver   transportReceiver;

    @Override
    public void fillParameterMap( Map<String, ParameterDescriptor> parameterMap ) {

        parameterMap.put( URL_PARAM_NAME, new ParameterDescriptor( ParameterType.STRING, "Web service URL",
                "The last part of the web service URL (e.g. /sendMessage)", "" ) );
        parameterMap.put( WS_AUTH_PARAM_NAME, new ParameterDescriptor( ParameterType.BOOLEAN,
                "WS* authentication", "Enable WS* authentication (username/password)", Boolean.FALSE ) );
        parameterMap.put( USERNAME_PARAM_NAME, new ParameterDescriptor( ParameterType.STRING, "User name",
                "The user name for WS* authentication", "" ) );
        parameterMap.put( PASSWORD_PARAM_NAME, new ParameterDescriptor( ParameterType.PASSWORD, "Password",
                "The password for WS* authentication", "" ) );
        parameterMap.put( CACHE_DOM_TREE_PARAM_NAME, new ParameterDescriptor( ParameterType.BOOLEAN, "Cache DOM tree",
                "Enable to cache the payload document's DOM tree in the message context (faster, but greater memory consumption)", Boolean.TRUE ) );
    }

    @Override
    public Layer getActivationLayer() {

        return Layer.INBOUND_PIPELINES;
    }

    public void start() {

        if ( getStatus() == BeanStatus.STARTED ) {
            return;
        }
        
        String url = (String) getParameter( URL_PARAM_NAME );
        LOG.debug( "Web service URL extension: " + url );

        Boolean cache = (Boolean) getParameter( CACHE_DOM_TREE_PARAM_NAME );

        try {
            AgGatewayDocumentExchangeImpl implementor = new AgGatewayDocumentExchangeImpl();
            implementor.cache = (cache == null || cache.booleanValue());
            implementor.setTransportReceiver( transportReceiver );
            endpoint = Endpoint.publish( url, implementor );

            Boolean b = getParameter( WS_AUTH_PARAM_NAME );
            // configure WS security
            if (b != null && b.booleanValue()) {
                org.apache.cxf.endpoint.Endpoint cxfEndpoint = ((EndpointImpl) endpoint).getServer().getEndpoint();
                Map<String,Object> inProps= new HashMap<String,Object>();
                inProps.put( WSHandlerConstants.ACTION, WSHandlerConstants.USERNAME_TOKEN );
                inProps.put( WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_TEXT );
                inProps.put( WSHandlerConstants.USER, getParameter( USERNAME_PARAM_NAME ) );
                final String password = (String) getParameter( PASSWORD_PARAM_NAME );
                CallbackHandler callback = new CallbackHandler() {
                    public void handle( Callback[] callbacks ) throws IOException, UnsupportedCallbackException {
                        WSPasswordCallback pc = (WSPasswordCallback) callbacks[0];
                        // check password
                        if  (password == null || !password.equals( pc.getPassword() )) {
                            String m = "User " + pc.getIdentifer() + " tried to access AgGateway web service with an incorrect password";
                            LOG.error( m );
                            throw new SecurityException( m );
                        }
                        pc.setPassword( password );
                    }
                };
                inProps.put( WSHandlerConstants.PW_CALLBACK_REF, callback );
                WSS4JInInterceptor wssIn = new WSS4JInInterceptor( inProps );
                cxfEndpoint.getInInterceptors().add( wssIn );
                if (LOG.isTraceEnabled()) {
                    cxfEndpoint.getOutInterceptors().add( new LoggingOutInterceptor() );
                    cxfEndpoint.getInInterceptors().add( new LoggingInInterceptor() );
                }
            }
            
            super.start();
        } catch ( Exception ex ) {
            ex.printStackTrace();
            LOG.error( ex );
        }
    }

    public void stop() {

        if ( endpoint != null ) {
            endpoint.stop();
            endpoint = null;
            DynamicWSDispatcherServlet.getInstance().reinitialize();
        }
        super.stop();
    }

    public TransportReceiver getTransportReceiver() {

        return transportReceiver;
    }

    public void setTransportReceiver( TransportReceiver transportReceiver ) {

        if ( endpoint != null && endpoint.getImplementor() != null ) {
            ( (ReceiverAware) endpoint.getImplementor() ).setTransportReceiver( transportReceiver );
        }
        this.transportReceiver = transportReceiver;
    }

    @javax.jws.WebService(
            portName = "DocExchangePortType",
            serviceName = "AgGatewayDocumentExchange",
            targetNamespace = "urn:aggateway:names:ws:docexchange",
            endpointInterface = "org.nexuse2e.service.ws.aggateway.wsdl.DocExchangePortType",
            wsdlLocation = "classpath:org/nexuse2e/integration/AgGateway.wsdl")
    public static class AgGatewayDocumentExchangeImpl implements DocExchangePortType, ReceiverAware {

        private TransportReceiver transportReceiver;
        private boolean cache;

        public TransportReceiver getTransportReceiver() {

            return transportReceiver;
        }

        public void setTransportReceiver( TransportReceiver transportReceiver ) {

            this.transportReceiver = transportReceiver;
        }

        public OutboundData execute( InboundData parameters ) throws DocExchangeFault {

            Object data = parameters.getXmlPayload().getAny();
            Node n = (Node) data;

            byte[] result = null;
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                DOMSource xmlSource = new DOMSource( n );
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                transformer.transform( xmlSource, new StreamResult( baos ) );

                result = baos.toByteArray();
            } catch ( Exception e ) {
                if ( LOG.isTraceEnabled() ) {
                    e.printStackTrace();
                }
            }

            MessageContext messageContext = process(
                    n,
                    parameters.getBusinessProcess(),
                    parameters.getProcessStep(),
                    parameters.getPartnerId(),
                    parameters.getConversationId(),
                    parameters.getMessageId(),
                    new String( result ) );

            
            try {
                OutboundData od = new OutboundData();
                od.setMessageId( new NexusUUIDGenerator().getId() );
    
                // send acknowledgment only
                if (messageContext == null) {
                    od.setProcessStep( "TechnicalAck" );
                } else { // send 
                    od.setProcessStep( messageContext.getConversation().getCurrentAction().getName() );
                    
                    DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
                    builderFactory.setNamespaceAware( true );
                    for (MessagePayloadPojo payload : messageContext.getMessagePojo().getMessagePayloads()) {
                        Document d = builderFactory.newDocumentBuilder().parse(
                                new ByteArrayInputStream( payload.getPayloadData() ) );
                        Element element = d.getDocumentElement();
                        XmlPayload xmlPayload = new XmlPayload();
                        xmlPayload.setAny( element );
                        od.getXmlPayload().add( xmlPayload );
                    }
                }
            
                return od;
            } catch (Exception e) {
                throw new DocExchangeFault( "Error while trying to set xmlPayload", e );
            }
        }
        
        private MessageContext process( Node n, String choreography, String action,
                String partner, String conversationId, String messageId, String document ) {

            MessageContext messageContext = new MessageContext();

            if (cache) {
                messageContext.setData( n );
            }
            
            byte[] payload = ( document != null ? document.getBytes() : null );

            if ( transportReceiver != null ) {

                if ( LOG.isTraceEnabled() ) {
                    LOG.trace( "Inbound message:\n" + document );
                }

                MessagePojo messagePojo = new MessagePojo();
                messagePojo.setType( Constants.INT_MESSAGE_TYPE_NORMAL );

                messageContext.setMessagePojo( messagePojo );
                messageContext.setOriginalMessagePojo( messagePojo );
                messageContext.getMessagePojo().setCustomParameters( new HashMap<String, String>() );

                try {
                    if ( choreography != null && action != null && partner != null && conversationId != null
                            && messageId != null ) {
                        Engine.getInstance().getTransactionService().initializeMessage( messagePojo, messageId,
                                conversationId, action, partner, choreography );

                        MessagePayloadPojo messagePayloadPojo = new MessagePayloadPojo();
                        messagePayloadPojo.setMessage( messagePojo );
                        messagePayloadPojo.setContentId( Engine.getInstance().getIdGenerator(
                                Constants.ID_GENERATOR_MESSAGE_PAYLOAD ).getId() );
                        messagePayloadPojo.setMimeType( "text/xml" );
                        messagePayloadPojo.setPayloadData( payload );
                        List<MessagePayloadPojo> messagePayloads = new ArrayList<MessagePayloadPojo>( 1 );
                        messagePayloads.add( messagePayloadPojo );
                        messagePojo.setMessagePayloads( messagePayloads );
                        messagePojo.getConversation().setCurrentAction( messagePojo.getAction() );
                        messagePojo.getConversation().getMessages().add( messagePojo );
                    }
                    messageContext = transportReceiver.processMessage( messageContext );
                } catch ( NexusException nex ) {
                    nex.printStackTrace();
                    LOG.error( nex );
                }
            }
            return messageContext;
        }

    }
}
