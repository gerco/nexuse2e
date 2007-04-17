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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.ws.security.WSPasswordCallback;
import org.apache.ws.security.handler.WSHandlerConstants;
import org.codehaus.xfire.XFire;
import org.codehaus.xfire.security.wss4j.WSS4JInHandler;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.MessageBindingProvider;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.service.invoker.BeanInvoker;
import org.codehaus.xfire.service.invoker.ObjectInvoker;
import org.codehaus.xfire.transport.http.XFireServletController;
import org.codehaus.xfire.util.dom.DOMInHandler;
import org.codehaus.xfire.util.jdom.StaxBuilder;
import org.codehaus.xfire.wsdl.WSDLWriter;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.nexuse2e.Engine;
import org.nexuse2e.Constants.BeanStatus;
import org.nexuse2e.Constants.Runlevel;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.Constants.ParameterType;
import org.nexuse2e.messaging.ErrorDescriptor;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.pojo.MessagePayloadPojo;
import org.nexuse2e.service.ReceiverAware;
import org.nexuse2e.transport.TransportReceiver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

public class GenericXMLDocumentWebServiceReceiver extends AbstractController implements org.nexuse2e.service.Service,
        ReceiverAware {

    private static Logger                      LOG               = Logger
                                                                         .getLogger( GenericXMLDocumentWebServiceReceiver.class );

    public static final String                 URL_PARAM_NAME    = "logical_name";

    private TransportReceiver                  transportReceiver = null;
    private XFire                              xFire             = null;
    private XFireServletController             controller        = null;
    protected byte[]                           wsdl              = null;
    private BeanStatus                         status            = BeanStatus.UNDEFINED;
    /**
     * Map parameter names to <code>ParameterDescriptor</code> objects in
     * this <code>Map</code>. The insertion order will be maintained.
     */
    protected Map<String, ParameterDescriptor> parameterMap      = null;

    private Map<String, Object>                parameters        = null;

    /**
     * Default constructor.
     */
    public GenericXMLDocumentWebServiceReceiver() {

        status = BeanStatus.INSTANTIATED;
        parameterMap = new LinkedHashMap<String, ParameterDescriptor>();
        parameters = new HashMap<String, Object>();
        fillParameterMap( parameterMap );
        for ( String key : parameterMap.keySet() ) {
            ParameterDescriptor pd = parameterMap.get( key );
            if ( pd != null ) {
                Object defaultValue = pd.getDefaultValue();
                if ( defaultValue != null ) {
                    parameters.put( key, defaultValue );
                }
            }
        }
    }

    @Override
    protected ModelAndView handleRequestInternal( HttpServletRequest request, HttpServletResponse response )
            throws Exception {

        if ( status == BeanStatus.STARTED ) {
            LOG.debug( "Processing inbound WS call... " );
            try {
                controller.doService( request, response );
            } catch ( Exception ex ) {
                LOG.error( "Exception calling XFire: " + ex );
                throw new Exception( ex );
            }
        } else {
            LOG.warn( "Ignoring inbound WS call - GenericXMLDocumentWebServiceReceiver not started!" );
        }

        return null;
    }

    /**
     * @param parameterMap
     */
    public void fillParameterMap( Map<String, ParameterDescriptor> parameterMap ) {

        parameterMap.put( URL_PARAM_NAME, new ParameterDescriptor( ParameterType.STRING, "Logical Name",
                "Logical name that is appended to the URL", "not_defined" ) );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.service.Service#isAutostart()
     */
    public boolean isAutostart() {

        return true;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.service.Service#start()
     */
    public void start() {

        status = BeanStatus.STARTED;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.service.Service#stop()
     */
    public void stop() {

        status = BeanStatus.ACTIVATED;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#activate()
     */
    public void activate() {

        LOG.debug( "Activating... " );

        status = BeanStatus.ACTIVATED;
    }

    public XFireServletController createController() {

        return new XFireServletController( xFire, getServletContext() );
    } // createController

    /**
     * Get the xfire controller. if it hasn't been created, then {@link #createController()} will be called.
     */
    public XFireServletController getController() throws Exception {

        if ( controller == null ) {
            controller = createController();
        }
        return controller;
    }

    protected void configureOutProperties( Properties properties ) {

        properties.setProperty( WSHandlerConstants.ACTION, WSHandlerConstants.SIGNATURE );
        // User in keystore
        properties.setProperty( WSHandlerConstants.USER, "1" );
        // This callback is used to specify password for given user for keystore
        properties.setProperty( WSHandlerConstants.PW_CALLBACK_CLASS, PasswordHandler.class.getName() );
        // Configuration for accessing private key in keystore
        properties.setProperty( WSHandlerConstants.SIG_PROP_FILE,
                "org/nexuse2e/transport/webservice/outsecurity_sign.properties" );
        properties.setProperty( WSHandlerConstants.SIG_KEY_ID, "IssuerSerial" );

    }

    protected void configureInProperties( Properties properties ) {

        properties.setProperty( WSHandlerConstants.ACTION, WSHandlerConstants.SIGNATURE + " "
                + WSHandlerConstants.TIMESTAMP );
        // User in keystore
        properties.setProperty( WSHandlerConstants.USER, "1" );
        // This callback is used to specify password for given user for keystore
        properties.setProperty( WSHandlerConstants.PW_CALLBACK_CLASS, PasswordHandler.class.getName() );
        // Configuration for accessing private key in keystore
        properties.setProperty( WSHandlerConstants.SIG_PROP_FILE,
                "org/nexuse2e/transport/webservice/insecurity_sign.properties" );
        properties.setProperty( WSHandlerConstants.SIG_KEY_ID, "IssuerSerial" );

        // How long ( in seconds ) message is valid since send.
        properties.setProperty( WSHandlerConstants.TTL_TIMESTAMP, "3600" );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#deactivate()
     */
    public void deactivate() {

        status = BeanStatus.INITIALIZED;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#getActivationRunlevel()
     */
    public Runlevel getActivationRunlevel() {

        return Runlevel.INBOUND_PIPELINES;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#getStatus()
     */
    public BeanStatus getStatus() {

        return status;
    }

    public void initialize( EngineConfiguration config ) {

        LOG.debug( "Initializing... " );

        xFire = (XFire) Engine.getInstance().getBeanFactory().getBean( "xfire" );

        if ( xFire == null ) {
            new Exception( "No XFire instance configured" );
            return;
        }

        try {
            
            // xfire = createXFire();
            getController();

            ObjectServiceFactory factory = new ObjectServiceFactory( xFire.getTransportManager(),
                    new MessageBindingProvider() );
            //we don't want to expose compareTo
            factory.addIgnoredMethods( "java.lang.Comparable" );
            factory.setStyle( "message" );
            Service service = factory.create( SimpleXMLDocServiceImpl.class, "SimpleXMLDocService",
                    "http://ws.nexuse2e.org/SimpleXMLDocService/", null );
            try {
                // FileInputStream fis = new FileInputStream( "/Volumes/NexusE2E/eclipse_3.2/XFireTest/webapp/WEB-INF/classes/SimpleXMLDocService.wsdl" );
                InputStream fis = factory.getClass().getResourceAsStream(
                        "/org/nexuse2e/service/ws/SimpleXMLDocService.wsdl" );
                wsdl = new byte[fis.available()];
                fis.read( wsdl );
                fis.close();
            } catch ( Exception ex ) {
                ex.printStackTrace();
            }
            WSDLWriter wsdlWriter = new WSDLWriter() {

                public void write( OutputStream out ) throws IOException {

                    out.write( wsdl );
                }

            };
            service.setWSDLWriter( wsdlWriter );
            // service.setProperty( ObjectInvoker.SERVICE_IMPL_CLASS, SimpleXMLDocServiceImpl.class );

            LOG.debug( "Setting invoker -  transportReceiver: " + transportReceiver );
            service.setInvoker( new BeanInvoker( new SimpleXMLDocServiceImpl( transportReceiver ) ) );

            // service.addInHandler( new DOMInHandler() );
            // Properties inProperties = new Properties();
            // configureInProperties( inProperties );
            // service.addInHandler( new WSS4JInHandler( inProperties ) );

            //          service.addOutHandler( new DOMOutHandler() );
            //          Properties outProperties = new Properties();
            //          configureOutProperties( outProperties );
            //          service.addOutHandler( new WSS4JOutHandler( outProperties ) );

            //we register the service with the controller that handles soap requests
            getController().getServiceRegistry().register( service );
            Iterator services = getController().getServiceRegistry().getServices().iterator();
            while ( services.hasNext() ) {
                Service tempService = (Service)services.next();
                LOG.debug( "Service: " + tempService.getSimpleName() );
                LOG.debug( "ServiceInfo: " + tempService.getServiceInfo() );
            }
        } catch ( Throwable ex ) {
            ex.printStackTrace();
            LOG.error( "Exception initializing WebServiceGenericXMLDocumentReceiver: " + ex );
        }
        status = BeanStatus.INITIALIZED;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#teardown()
     */
    public void teardown() {

        status = BeanStatus.INSTANTIATED;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Configurable#getParameter(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public <T> T getParameter( String name ) {

        return (T) parameters.get( name );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Configurable#getParameterMap()
     */
    public Map<String, ParameterDescriptor> getParameterMap() {

        return Collections.unmodifiableMap( parameterMap );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Configurable#getParameters()
     */
    public Map<String, Object> getParameters() {

        return Collections.unmodifiableMap( parameters );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Configurable#setParameter(java.lang.String, java.lang.Object)
     */
    public void setParameter( String name, Object value ) {

        parameters.put( name, value );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.service.ReceiverAware#getTransportReceiver()
     */
    public TransportReceiver getTransportReceiver() {

        return transportReceiver;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.service.ReceiverAware#setTransportReceiver(org.nexuse2e.transport.TransportReceiver)
     */
    public void setTransportReceiver( TransportReceiver transportReceiver ) {

        this.transportReceiver = transportReceiver;
    }

    /**
     * @author mbreilmann
     *
     */
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

    /**
     * @author mbreilmann
     *
     */
    public static class SimpleXMLDocServiceImpl {

        public SimpleXMLDocServiceImpl( TransportReceiver transportReceiver ) {

            LOG.debug( "Constructor - transportReceiver: " + transportReceiver );
        }

        //  public XMLStreamReader handleXMLDoc( XMLStreamReader xmlDoc, MessageContext context ) {

        public Element handleXMLDoc( Element xmlDoc, MessageContext context ) throws Exception {

            Element element = new Element( "OK" );

            MessagePayloadPojo payload = null;

            LOG.debug( "Received payload: \n" + xmlDoc );

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            XMLOutputter xmlOutputter = new XMLOutputter();
            try {

                xmlOutputter.output( xmlDoc, baos );

                // TODO: Retrieve the correct TransportReceiver
                TransportReceiver transportReceiver = null;

                if ( transportReceiver != null ) {
                    
                    MessageContext inboundContext = new MessageContext();
                    inboundContext.setData( baos.toByteArray() );
                    
                    MessageContext outboundContext = transportReceiver.processInboundData( inboundContext );

                    List<MessagePayloadPojo> payloads = outboundContext.getMessagePojo().getMessagePayloads();
                    if ( !payloads.isEmpty() ) {
                        payload = payloads.iterator().next();
                    } else {
                        StringBuffer errorMessage = new StringBuffer(
                                "Errors occured during processing of Web services request" );
                        if ( outboundContext.getMessagePojo().getErrors() != null ) {
                            for ( Iterator iter = outboundContext.getMessagePojo().getErrors().iterator(); iter
                                    .hasNext(); ) {
                                ErrorDescriptor errorDescriptor = (ErrorDescriptor) iter.next();
                                errorMessage.append( "\n" + errorDescriptor.getDescription() );
                            }
                        }
                        throw new Exception( errorMessage.toString() );
                    }

                    LOG.debug( "Sending response payload: \n" + new String( payload.getPayloadData() ) );

                    /*
                     try {
                     FileOutputStream fos = new FileOutputStream( "C:/order_status_response.xml" );
                     fos.write( payload.getPayloadData() );
                     fos.close();
                     } catch ( Exception ex ) {
                     ex.printStackTrace();
                     }
                     */

                    StaxBuilder builder = new StaxBuilder();
                    org.jdom.Document jDomDoc = builder.build( new ByteArrayInputStream( payload.getPayloadData() ) );

                    element = jDomDoc.getRootElement();
                    LOG.debug( "Root element: " + element );
                }

            } catch ( Exception e ) {
                LOG.error( "Error processing Web services call: " + e );
                e.printStackTrace();
                throw e;
            }

            return element;
        }
    } // inner class SimpleXMLDocServiceImpl

} // GenericXMLDocumentWebServiceReceiver
