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
package org.nexuse2e.transport;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.log4j.Logger;
import org.apache.ws.security.WSPasswordCallback;
import org.apache.ws.security.handler.WSHandlerConstants;
import org.codehaus.xfire.addressing.AddressingInHandler;
import org.codehaus.xfire.addressing.AddressingOutHandler;
import org.codehaus.xfire.client.Client;
import org.codehaus.xfire.client.XFireProxyFactory;
import org.codehaus.xfire.handler.AbstractHandler;
import org.codehaus.xfire.handler.Phase;
import org.codehaus.xfire.security.wss4j.WSS4JOutHandler;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.MessageBindingProvider;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.soap.handler.SoapActionInHandler;
import org.codehaus.xfire.transport.Channel;
import org.codehaus.xfire.util.dom.DOMOutHandler;
import org.codehaus.xfire.util.jdom.StaxBuilder;
import org.jdom.Element;
import org.nexuse2e.NexusException;
import org.nexuse2e.ProtocolSpecificKey;
import org.nexuse2e.Constants.Runlevel;
import org.nexuse2e.messaging.AbstractPipelet;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.pojo.MessagePayloadPojo;

public class WebServiceGenericXMLDocumentSender extends AbstractPipelet {

    private static Logger       LOG = Logger.getLogger( WebServiceGenericXMLDocumentSender.class );

    private ProtocolSpecificKey key;
   
    /**
     * Default constructor.
     */
    public WebServiceGenericXMLDocumentSender() {

        key = null;
    }

    public MessageContext processMessage( MessageContext frontendPipeletParameter )
            throws NexusException {

        LOG.debug( "Entering WebServiceGenericXMLDocumentSender.processMessage..." );
        String receiverURL = frontendPipeletParameter.getParticipant().getConnection().getUri();
        LOG.debug( "Web service connection URL: " + receiverURL );

        Client client;
        try {
            ObjectServiceFactory objectServiceFactory = new ObjectServiceFactory( new MessageBindingProvider() );

            objectServiceFactory.setStyle( "message" );
            objectServiceFactory.setUse( "literal" );

            Service serviceModel = objectServiceFactory.create( SimpleXMLDocService.class );

            serviceModel.setProperty( Channel.USERNAME, frontendPipeletParameter.getParticipant().getConnection()
                    .getLoginName() );
            serviceModel.setProperty( Channel.PASSWORD, frontendPipeletParameter.getParticipant().getConnection()
                    .getPassword() );
            // serviceModel.setName( new QName( "SetShipStatusWS" ) );

            serviceModel.addInHandler( new AddressingInHandler() );
            serviceModel.addOutHandler( new AddressingOutHandler() );
            serviceModel.addOutHandler( new SoapActionOutHandler( frontendPipeletParameter.getMessagePojo().getAction()
                    .getName() ) );

            List<MessagePayloadPojo> payloads = frontendPipeletParameter.getMessagePojo().getMessagePayloads();
            MessagePayloadPojo payload = payloads.iterator().next();

            StaxBuilder builder = new StaxBuilder();
            org.jdom.Document jDomDoc = builder.build( new ByteArrayInputStream( payload.getPayloadData() ) );

            Element element = jDomDoc.getRootElement();
            System.out.println( "Root element: " + element );

            XFireProxyFactory xFireProxyFactory = new XFireProxyFactory();
            Object service = xFireProxyFactory.create( serviceModel, receiverURL );

            client = Client.getInstance( service );
            client.addInHandler( new AddressingInHandler() );

            client.addOutHandler( new SoapActionOutHandler( frontendPipeletParameter.getMessagePojo().getAction()
                    .getName() ) );

            client.addOutHandler( new DOMOutHandler() );
            Properties outProperties = new Properties();
            configureOutProperties( outProperties );
            client.addOutHandler( new WSS4JOutHandler( outProperties ) );

            client.addOutHandler( new AddressingOutHandler() );

            // client.addInHandler( new DOMInHandler() );
            // Properties inProperties = new Properties();
            // configureOutProperties( inProperties );
            // client.addInHandler( new WSS4JInHandler( inProperties ) );

            Object[] result = client.invoke( "handleXMLDoc", new Object[] { element} );

            System.out.println( "Result: " + result );

        } catch ( Exception e ) {
            e.printStackTrace();

            throw new NexusException( "Error calling web service: " + e, e );
        }

        return null;
    } // processMessage

    protected void configureOutProperties( Properties properties ) {

        properties.setProperty( WSHandlerConstants.ACTION, WSHandlerConstants.SIGNATURE + " "
                + WSHandlerConstants.TIMESTAMP );
        // User in keystore
        properties.setProperty( WSHandlerConstants.USER, "1" );
        // This callback is used to specify password for given user for keystore
        properties.setProperty( WSHandlerConstants.PW_CALLBACK_CLASS, PasswordHandler.class.getName() );
        // Configuration for accessing private key in keystore
        properties.setProperty( WSHandlerConstants.SIG_PROP_FILE,
                "org/nexuse2e/transport/webservice/outsecurity_sign.properties" );
        // properties.setProperty( WSHandlerConstants.SIG_KEY_ID, "IssuerSerial" );
        properties.setProperty( WSHandlerConstants.SIG_KEY_ID, "DirectReference" );
        properties.setProperty( WSHandlerConstants.TTL_TIMESTAMP, "15" );

    }

   

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#getRunLevel()
     */
    public Runlevel getActivationRunlevel() {

        return Runlevel.INTERFACES;
    }


    public ProtocolSpecificKey getKey() {

        return key;
    } // getKey

    public void setKey( ProtocolSpecificKey key ) {

        this.key = key;
    } // setKey

    public static class SoapActionOutHandler extends AbstractHandler {

        String action = "";

        public SoapActionOutHandler( String action )

        {

            super();

            // LOG.debug( "SoapActionOutHandler" );

            this.action = action;

            setPhase( Phase.TRANSPORT );
            after( SoapActionInHandler.class.getName() );
        }

        public void invoke( org.codehaus.xfire.MessageContext context ) throws Exception {

            // LOG.debug( "SoapActionOutHandler.invoke: " + action );

            context.getExchange().getOutMessage().setProperty( SoapConstants.SOAP_ACTION, action );

            // LOG.debug( "SoapActionOutHandler.invoke done!" );
        }

    } // SoapActionOutHandler

    public static class PasswordHandler implements CallbackHandler {

        private Map<String, String> passwords = new HashMap<String, String>();

        public PasswordHandler() {

            passwords.put( "1", "xioma" );
        }

        public void handle( Callback[] callbacks ) throws IOException, UnsupportedCallbackException {

            WSPasswordCallback pc = (WSPasswordCallback) callbacks[0];
            String id = pc.getIdentifer();
            pc.setPassword( passwords.get( id ) );
        }
    } // PasswordHandler
   
} // WebServiceGenericXMLDocumentSender
