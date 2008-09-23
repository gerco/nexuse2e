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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.ws.Endpoint;

import org.apache.log4j.Logger;
import org.nexuse2e.DynamicWSDispatcherServlet;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.Constants.BeanStatus;
import org.nexuse2e.Constants.Layer;
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
import org.nexuse2e.transport.TransportReceiver;

/**
 * A service that dynamically registers a Ag Gateway compliant web service endpoint.
 *
 * @author Markus Breilmann
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public class WSDispatcherService extends AbstractService implements ReceiverAware {

    private static Logger       LOG            = Logger.getLogger( WSDispatcherService.class );

    private static final String URL_PARAM_NAME = "url";

    private Endpoint            endpoint;
    private TransportReceiver   transportReceiver;

    @Override
    public void fillParameterMap( Map<String, ParameterDescriptor> parameterMap ) {

        parameterMap.put( URL_PARAM_NAME, new ParameterDescriptor( ParameterType.STRING, "Web service URL",
                "The last part of the web service URL (e.g. /sendMessage)", "" ) );
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

        try {
            Object implementor = new AgGatewayDocumentExchangeImpl();
            ( (ReceiverAware) implementor ).setTransportReceiver( transportReceiver );
            endpoint = Endpoint.publish( url, implementor );
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

    private static void process( TransportReceiver transportReceiver, String choreography, String action,
            String partner, String conversationId, String messageId, String document ) {

        MessageContext messageContext = new MessageContext();

        byte[] payload = ( document != null ? document.getBytes() : null );

        TransportReceiver receiver = transportReceiver;
        if ( receiver != null ) {
            messageContext.setData( payload );
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
                }
                receiver.processMessage( messageContext );
            } catch ( NexusException nex ) {
                nex.printStackTrace();
                LOG.error( nex );
            }
        }
    }

    @javax.jws.WebService(
            portName = "DocExchangePortType",
            serviceName = "AgGatewayDocumentExchange",
            targetNamespace = "urn:aggateway:names:ws:docexchange",
            endpointInterface = "org.nexuse2e.service.ws.aggateway.wsdl.DocExchangePortType",
            wsdlLocation = "classpath:org/nexuse2e/integration/AgGateway.wsdl")
    public static class AgGatewayDocumentExchangeImpl implements DocExchangePortType, ReceiverAware {

        private TransportReceiver transportReceiver;

        public TransportReceiver getTransportReceiver() {

            return transportReceiver;
        }

        public void setTransportReceiver( TransportReceiver transportReceiver ) {

            this.transportReceiver = transportReceiver;
        }

        public OutboundData execute( InboundData parameters ) throws DocExchangeFault {

            System.out.println( "getBusinessProcess: " + parameters.getBusinessProcess() );
            process( transportReceiver, parameters.getBusinessProcess(), parameters.getProcessStep(), parameters
                    .getPartnerId(), parameters.getConversationId(), parameters.getMessageId(), parameters
                    .getXmlPayload() );

            return new OutboundData();
        }
    }
}
