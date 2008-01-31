package org.nexuse2e.service.ws;

import java.util.Map;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.log4j.Logger;
import org.nexuse2e.NexusException;
import org.nexuse2e.Constants.BeanStatus;
import org.nexuse2e.Constants.Layer;
import org.nexuse2e.configuration.ListParameter;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.Constants.ParameterType;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.pojo.MessagePayloadPojo;
import org.nexuse2e.service.AbstractService;
import org.nexuse2e.service.SenderAware;
import org.nexuse2e.transport.TransportSender;

/**
 * Generic service that acts as a web service client.
 *
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public class WSClientService extends AbstractService implements SenderAware {

    private static Logger       LOG                     = Logger.getLogger( WSClientService.class );

    private static final String URL_PARAM_NAME          = "url";
    private static final String SERVICE_TYPE_PARAM_NAME = "serviceType";

    private TransportSender     transportSender;
    private XmlDocumentService  xmlDocumentService;

    public enum FrontendWebServiceType {

        XML_DOCUMENT("Generic XML document (with routing information)"), CIDX_DOCUMENT(
                "CIDX business document (no routing information)");

        private String name;

        FrontendWebServiceType( String name ) {

            this.name = name;
        }

        /**
         * Gets the human-readable service type name.
         * @return The name.
         */
        public String getName() {

            return name;
        }
    };

    @Override
    public void fillParameterMap( Map<String, ParameterDescriptor> parameterMap ) {

        ListParameter serviceTypeDrowdown = new ListParameter();

        for ( FrontendWebServiceType type : FrontendWebServiceType.values() ) {
            serviceTypeDrowdown.addElement( type.getName(), type.toString() );
        }
        parameterMap.put( SERVICE_TYPE_PARAM_NAME, new ParameterDescriptor( ParameterType.LIST, "Web service type",
                "The type of web service to connect to", serviceTypeDrowdown ) );
        parameterMap.put( URL_PARAM_NAME, new ParameterDescriptor( ParameterType.STRING, "Web service URL",
                "The fully qualified web service URL (e.g. http://foo.org/bar)", "" ) );
    }

    @Override
    public Layer getActivationLayer() {

        return Layer.INBOUND_PIPELINES;
    }

    public void start() {

        if ( getStatus() == BeanStatus.STARTED ) {
            return;
        }
        xmlDocumentService = null;
        ListParameter parameter = getParameter( SERVICE_TYPE_PARAM_NAME );
        FrontendWebServiceType wsType = null;
        if ( parameter != null ) {
            wsType = FrontendWebServiceType.valueOf( parameter.getSelectedValue() );
        }
        try {
            if ( wsType == FrontendWebServiceType.XML_DOCUMENT ) {

                JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
                factory.setServiceClass( XmlDocumentService.class );
                factory.setAddress( (String) getParameter( URL_PARAM_NAME ) );
                xmlDocumentService = (XmlDocumentService) factory.create();

                super.start();
            }
        } catch ( Exception ex ) {
            ex.printStackTrace();
            LOG.error( ex );
        }
    }

    public void stop() {

        super.stop();
    }

    public TransportSender getTransportSender() {

        return transportSender;
    }

    public void sendMessage( MessageContext messageContext ) throws NexusException {

        if ( getStatus() != BeanStatus.STARTED ) {
            throw new NexusException( "Service " + getClass().getSimpleName() + " not started" );
        }

        String receiverURL = messageContext.getParticipant().getConnection().getUri();

        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setServiceClass( XmlDocumentService.class );
        factory.setAddress( receiverURL );
        XmlDocumentService theXmlDocumentService = (XmlDocumentService) factory.create();

        if ( theXmlDocumentService != null ) {
            for ( MessagePayloadPojo payload : messageContext.getMessagePojo().getMessagePayloads() ) {
                LOG.trace( "Calling web service at: " + receiverURL );
                theXmlDocumentService.processXmlDocument( messageContext.getChoreography().getName(), messageContext
                        .getActionSpecificKey().getActionId(), messageContext.getParticipant().getLocalPartner()
                        .getPartnerId(), messageContext.getConversation().getConversationId(), messageContext
                        .getMessagePojo().getMessageId(), new String( payload.getPayloadData() ) );
            }

        } else {
            LOG.error( "Could not create WS client for partner: "
                    + messageContext.getParticipant().getPartner().getPartnerId() );
        }
    }

    public void setTransportSender( TransportSender transportSender ) {

        this.transportSender = transportSender;
    }
}
