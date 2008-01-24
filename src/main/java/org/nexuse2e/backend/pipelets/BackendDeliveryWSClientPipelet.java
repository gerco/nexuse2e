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

import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.xerces.dom.AttrNSImpl;
import org.apache.xerces.dom.CoreDocumentImpl;
import org.codehaus.xfire.annotations.AnnotationServiceFactory;
import org.codehaus.xfire.client.Client;
import org.codehaus.xfire.client.XFireProxyFactory;
import org.codehaus.xfire.exchange.OutMessage;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.transport.Channel;
import org.codehaus.xfire.util.dom.DOMOutHandler;
import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.Constants.ParameterType;
import org.nexuse2e.integration.BackendDeliveryInterface;
import org.nexuse2e.integration.ProcessInboundMessageException;
import org.nexuse2e.messaging.AbstractPipelet;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.pojo.ActionPojo;
import org.nexuse2e.pojo.ChoreographyPojo;
import org.nexuse2e.pojo.ConversationPojo;
import org.nexuse2e.pojo.MessagePayloadPojo;
import org.nexuse2e.pojo.MessagePojo;
import org.nexuse2e.pojo.PartnerPojo;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A pipelet that sends messages to a backend delivery web service that can be configured.
 * @see BackendDeliveryInterface
 * 
 * @author jonas.reese
 */
public class BackendDeliveryWSClientPipelet extends AbstractPipelet {

    private static Logger      LOG                   = Logger.getLogger( BackendDeliveryWSClientPipelet.class );

    public static final String URL_PARAM_NAME        = "url";
    public static final String BASIC_AUTH_PARAM_NAME = "basicAuth";
    public static final String USERNAME_PARAM_NAME   = "username";
    public static final String PASSWORD_PARAM_NAME   = "password";

    public BackendDeliveryWSClientPipelet() {

        parameterMap.put( URL_PARAM_NAME, new ParameterDescriptor( ParameterType.STRING, "URL",
                "The backend delivery web service URL", "" ) );
        parameterMap.put( BASIC_AUTH_PARAM_NAME, new ParameterDescriptor( ParameterType.BOOLEAN,
                "HTTP Basic Authentication", "Enable HTTP Basic Authentication", Boolean.FALSE ) );
        parameterMap.put( USERNAME_PARAM_NAME, new ParameterDescriptor( ParameterType.STRING, "User Name",
                "HTTP Basic Auth User Name", "" ) );
        parameterMap.put( PASSWORD_PARAM_NAME, new ParameterDescriptor( ParameterType.PASSWORD, "Password",
                "HTTP Basic Auth Password", "" ) );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.AbstractPipelet#processMessage(org.nexuse2e.messaging.MessageContext)
     */
    @Override
    public MessageContext processMessage( MessageContext messageContext ) throws IllegalArgumentException,
            IllegalStateException, NexusException {

        AnnotationServiceFactory serviceFactory = new AnnotationServiceFactory();
        Service serviceModel = serviceFactory.create( BackendDeliveryInterface.class );

        try {
            BackendDeliveryInterface service = (BackendDeliveryInterface) new XFireProxyFactory().create( serviceModel,
                    (String) getParameter( URL_PARAM_NAME ) );

            Client client = Client.getInstance( service );
            client.addOutHandler( new RemoveNamespaceOutHandler() );

            // Tell XFire to cache a DOM document for the various in/out/fault flows
            client.addInHandler( new org.codehaus.xfire.util.dom.DOMInHandler() );
            client.addOutHandler( new org.codehaus.xfire.util.dom.DOMOutHandler() );
            client.addFaultHandler( new org.codehaus.xfire.util.dom.DOMOutHandler() );

            // Add logging
            client.addInHandler( new org.codehaus.xfire.util.LoggingHandler() );
            client.addOutHandler( new org.codehaus.xfire.util.LoggingHandler() );
            client.addFaultHandler( new org.codehaus.xfire.util.LoggingHandler() );

            // HTTP basic auth
            boolean httpAuth = ( (Boolean) getParameter( BASIC_AUTH_PARAM_NAME ) ).booleanValue();
            if ( httpAuth ) {
                String username = getParameter( USERNAME_PARAM_NAME );
                String password = getParameter( PASSWORD_PARAM_NAME );

                if ( username == null ) {
                    username = "";
                }
                if ( password == null ) {
                    password = "";
                }

                client.setProperty( Channel.USERNAME, username );
                client.setProperty( Channel.PASSWORD, password );
            }

            List<MessagePayloadPojo> payloadPojos = messageContext.getMessagePojo().getMessagePayloads();
            String payload = null;
            if ( !payloadPojos.isEmpty() ) {
                payload = new String( payloadPojos.get( 0 ).getPayloadData() );
            }
            String actionId = null;
            if ( messageContext.getMessagePojo().getConversation().getCurrentAction() != null ) {
                actionId = messageContext.getMessagePojo().getConversation().getCurrentAction().getName();
            }
            String response = service.processInboundMessage( messageContext.getChoreography().getName(), messageContext
                    .getPartner().getName(), actionId, messageContext.getConversation().getConversationId(),
                    messageContext.getMessagePojo().getMessageId(), payload );
            LOG.debug( "response from backend delivery WS is " + response );
            return messageContext;
        } catch ( MalformedURLException e ) {
            throw new NexusException( e );
        } catch ( RemoteException e ) {
            throw new NexusException( e );
        } catch ( ProcessInboundMessageException e ) {
            throw new NexusException( e );
        }
    }

    /**
     * This handler removes the namespace attributes from the SOAP request parameters.
     * Obviously some WebService implementations cannot cope with the default namespace
     * being present for every parameter.
     * 
     * @author jonas.reese
     */
    public static class RemoveNamespaceOutHandler extends DOMOutHandler {

        public void invoke( org.codehaus.xfire.MessageContext context ) throws Exception {

            super.invoke( context );

            OutMessage msg = context.getOutMessage();
            Document doc = (Document) msg.getProperty( DOMOutHandler.DOM_MESSAGE );
            if ( doc != null ) {
                if ( doc.getFirstChild() != null
                        && doc.getFirstChild().getFirstChild() != null
                        && doc.getFirstChild().getFirstChild().getFirstChild() != null
                        && "processInboundMessage".equals( doc.getFirstChild().getFirstChild().getFirstChild()
                                .getNodeName() ) ) {
                    Node node = doc.getFirstChild().getFirstChild().getFirstChild();
                    node.setPrefix( "x" );
                    Node namespaceNode = node.getAttributes().getNamedItem( "xmlns" );
                    if ( namespaceNode != null && node.getOwnerDocument() instanceof CoreDocumentImpl ) {
                        node.getAttributes().removeNamedItem( namespaceNode.getNodeName() );
                        AttrNSImpl newNSNode = new AttrNSImpl( (CoreDocumentImpl) node.getOwnerDocument(),
                                namespaceNode.getNamespaceURI(), namespaceNode.getNodeName() + ":x", namespaceNode
                                        .getLocalName() );
                        newNSNode.setValue( namespaceNode.getNodeValue() );
                        node.getAttributes().setNamedItem( newNSNode );
                    }
                    NodeList nodes = node.getChildNodes();
                    for ( int i = 0; i < nodes.getLength(); i++ ) {
                        Node paramNode = nodes.item( i );
                        namespaceNode = paramNode.getAttributes().getNamedItem( "xmlns" );
                        if ( namespaceNode != null ) {
                            paramNode.getAttributes().removeNamedItem( namespaceNode.getNodeName() );
                        }
                    }
                }
            }
        }
    }

    public static void main( String[] args ) throws Exception {

        BackendDeliveryWSClientPipelet pipelet = new BackendDeliveryWSClientPipelet();
        pipelet.setParameter( URL_PARAM_NAME, "http://localhost:8080/NEXUSe2e/webservice/TrafficLogger" );
        pipelet.setParameter( BASIC_AUTH_PARAM_NAME, Boolean.FALSE );
        MessageContext mc = new MessageContext();
        ConversationPojo conversation = new ConversationPojo();
        ActionPojo action = new ActionPojo();
        action.setName( "actionId" );
        conversation.setCurrentAction( action );
        conversation.setConversationId( "conversationId" );
        mc.setConversation( conversation );
        MessagePojo message = new MessagePojo();
        message.setMessageId( "messageId" );
        MessagePayloadPojo payload = new MessagePayloadPojo();
        payload.setPayloadData( "payload".getBytes() );
        message.setMessagePayloads( Collections.singletonList( payload ) );
        message.setConversation( conversation );
        mc.setMessagePojo( message );
        ChoreographyPojo choreo = new ChoreographyPojo();
        choreo.setName( "choreographyId" );
        mc.setChoreography( choreo );
        PartnerPojo partner = new PartnerPojo();
        partner.setName( "partnerId" );
        mc.setCommunicationPartner( partner );
        pipelet.processMessage( mc );
    }

}
