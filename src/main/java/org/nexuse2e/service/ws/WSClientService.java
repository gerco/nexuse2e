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
package org.nexuse2e.service.ws;

import java.security.KeyStore;
import java.util.Map;

import javax.net.ssl.KeyManager;
import javax.net.ssl.TrustManager;

import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.configuration.security.FiltersType;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.apache.log4j.Logger;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.Constants.BeanStatus;
import org.nexuse2e.Constants.Layer;
import org.nexuse2e.configuration.Constants;
import org.nexuse2e.configuration.ListParameter;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.Constants.ParameterType;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.pojo.CertificatePojo;
import org.nexuse2e.pojo.MessagePayloadPojo;
import org.nexuse2e.pojo.MessagePojo;
import org.nexuse2e.service.AbstractService;
import org.nexuse2e.service.SenderAware;
import org.nexuse2e.transport.TransportSender;
import org.nexuse2e.util.CertificateUtil;
import org.nexuse2e.util.EncryptionUtil;

/**
 * Generic service that acts as a web service client.
 *
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public class WSClientService extends AbstractService implements SenderAware {

    private static Logger       LOG                     = Logger.getLogger( WSClientService.class );

    private static final String SERVICE_TYPE_PARAM_NAME = "serviceType";

    private TransportSender     transportSender;

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
    }

    @Override
    public Layer getActivationLayer() {

        return Layer.INBOUND_PIPELINES;
    }

    public TransportSender getTransportSender() {

        return transportSender;
    }

    public MessageContext sendMessage( MessageContext messageContext ) throws NexusException {

        if ( getStatus() != BeanStatus.STARTED ) {
            throw new NexusException( "Service " + getClass().getSimpleName() + " not started" );
        }

        String receiverURL = messageContext.getParticipant().getConnection().getUri();

        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        ListParameter parameter = getParameter( SERVICE_TYPE_PARAM_NAME );
        FrontendWebServiceType wsType = null;
        if ( parameter != null ) {
            wsType = FrontendWebServiceType.valueOf( parameter.getSelectedValue() );
        }

        MessagePojo messagePojo = messageContext.getMessagePojo();

        if ( wsType == FrontendWebServiceType.XML_DOCUMENT ) {
            factory.setServiceClass( XmlDocumentService.class );
            factory.setAddress( receiverURL );
            XmlDocumentService theXmlDocumentService = (XmlDocumentService) factory.create();

            for ( MessagePayloadPojo payload : messageContext.getMessagePojo().getMessagePayloads() ) {
                LOG.trace( "Calling web service at: " + receiverURL );
                theXmlDocumentService.processXmlDocument( messagePojo.getConversation().getChoreography().getName(),
                        messageContext.getActionSpecificKey().getActionId(), messagePojo.getParticipant()
                                .getLocalPartner().getPartnerId(), messagePojo.getConversation().getConversationId(),
                        messagePojo.getMessageId(), new String( payload.getPayloadData() ) );
            }
        } else if ( wsType == FrontendWebServiceType.CIDX_DOCUMENT ) {
            factory.setServiceClass( CidxDocumentService.class );
            factory.setAddress( receiverURL );
            CidxDocumentService theCidxDocumentService = (CidxDocumentService) factory.create();

            Client cxfClient = ClientProxy.getClient( theCidxDocumentService );

            HTTPConduit httpConduit = (HTTPConduit) cxfClient.getConduit();

            HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
            httpClientPolicy.setConnectionTimeout( messagePojo.getParticipant().getConnection().getTimeout() );
            httpConduit.setClient( httpClientPolicy );

            // Enable SSL, see also http://cwiki.apache.org/confluence/display/CXF20DOC/Client+HTTP+Transport+%28including+SSL+support%29
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

            for ( MessagePayloadPojo payload : messagePojo.getMessagePayloads() ) {
                LOG.trace( "Calling web service at: " + receiverURL );
                theCidxDocumentService.processCidxDocument( new String( payload.getPayloadData() ) );
            }
        }
        
        return null;
    }

    public void setTransportSender( TransportSender transportSender ) {

        this.transportSender = transportSender;
    }
}
