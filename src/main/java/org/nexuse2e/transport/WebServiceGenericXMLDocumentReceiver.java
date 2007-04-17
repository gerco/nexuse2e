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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
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
import org.codehaus.xfire.service.invoker.ObjectInvoker;
import org.codehaus.xfire.transport.http.XFireServletController;
import org.codehaus.xfire.util.dom.DOMInHandler;
import org.codehaus.xfire.util.jdom.StaxBuilder;
import org.codehaus.xfire.wsdl.WSDLWriter;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.nexuse2e.Engine;
import org.nexuse2e.ProtocolSpecific;
import org.nexuse2e.ProtocolSpecificKey;
import org.nexuse2e.messaging.ErrorDescriptor;
import org.nexuse2e.messaging.FrontendPipeline;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.pojo.MessagePayloadPojo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

public class WebServiceGenericXMLDocumentReceiver extends AbstractController implements ProtocolSpecific,
        InitializingBean {

    private static Logger          LOG              = Logger.getLogger( WebServiceGenericXMLDocumentReceiver.class );
    public final static String     XFIRE_INSTANCE   = "xfire.instance";

    private ProtocolSpecificKey    key              = null;
    private FrontendPipeline       frontendPipeline = null;
    private XFire                  xFire            = null;
    private XFireServletController controller       = null;
    protected byte[]               wsdl             = null;
    private boolean                synchronous      = false;

    public void afterPropertiesSet() throws Exception {

        LOG.debug( "WebServiceGenericXMLDocumentReceiver.afterPropertiesSet triggered... " );

        if ( xFire == null ) {
            new Exception( "WebServiceGenericXMLDocumentReceiver: No XFire instance configured" );
        }

        try {
            // xfire = createXFire();
            controller = createController();

            ObjectServiceFactory factory = new ObjectServiceFactory( getXFire().getTransportManager(),
                    new MessageBindingProvider() );
            //we don't want to expose compareTo
            factory.addIgnoredMethods( "java.lang.Comparable" );
            factory.setStyle( "message" );
            Service service = factory.create( SimpleXMLDocServiceImpl.class, "SimpleXMLDocService",
                    "http://ws.testing.xioma.de/SimpleXMLDocService/", null );
            try {
                // FileInputStream fis = new FileInputStream( "/Volumes/NexusE2E/eclipse_3.2/XFireTest/webapp/WEB-INF/classes/SimpleXMLDocService.wsdl" );
                InputStream fis = factory.getClass().getResourceAsStream(
                        "/org/nexuse2e/transport/SimpleXMLDocService.wsdl" );
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
            service.setProperty( ObjectInvoker.SERVICE_IMPL_CLASS, SimpleXMLDocServiceImpl.class );

            service.addInHandler( new DOMInHandler() );
            Properties inProperties = new Properties();
            configureInProperties( inProperties );
            service.addInHandler( new WSS4JInHandler( inProperties ) );

            //          service.addOutHandler( new DOMOutHandler() );
            //          Properties outProperties = new Properties();
            //          configureOutProperties( outProperties );
            //          service.addOutHandler( new WSS4JOutHandler( outProperties ) );

            //we register the service with the controller that handles soap requests
            getController().getServiceRegistry().register( service );
        } catch ( Throwable ex ) {
            LOG.error( "Exception initializing WebServiceGenericXMLDocumentReceiver: " + ex );
            throw new Exception( ex );
        }

    } // afterPropertiesSet

    /* (non-Javadoc)
     * @see org.springframework.web.servlet.mvc.AbstractController#handleRequestInternal(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected ModelAndView handleRequestInternal( HttpServletRequest request, HttpServletResponse response )
            throws Exception {

        LOG.debug( "WebServiceGenericXMLDocumentReceiver.handleRequestInternal triggered... " );
        try {
            controller.doService( request, response );
        } catch ( Exception ex ) {
            LOG.error( "Exception calling XFire: " + ex );
            throw new Exception( ex );
        }

        return null;
    } // handleRequestInternal

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

    public ProtocolSpecificKey getKey() {

        return key;
    } // getKey

    public void setKey( ProtocolSpecificKey key ) {

        this.key = key;
    } // setKey

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

    public static class SimpleXMLDocServiceImpl {

        //  public XMLStreamReader handleXMLDoc( XMLStreamReader xmlDoc, MessageContext context ) {

        public Element handleXMLDoc( Element xmlDoc, MessageContext context ) throws Exception {

            Element element = new Element( "OK" );

            MessagePayloadPojo payload = null;

            LOG.debug( "Received payload: \n" + xmlDoc );

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            XMLOutputter xmlOutputter = new XMLOutputter();
            try {

                xmlOutputter.output( xmlDoc, baos );

                WebServiceGenericXMLDocumentReceiver webServiceGenericXMLDocumentReceiver = (WebServiceGenericXMLDocumentReceiver) Engine
                        .getInstance().getBeanFactory().getBean( "wsGenericReceiver" );

                if ( webServiceGenericXMLDocumentReceiver != null ) {
                    MessageContext messageContext = new MessageContext();
                    messageContext.setData( baos.toByteArray() );
                    messageContext.setProtocolSpecificKey( webServiceGenericXMLDocumentReceiver.getKey() );

                    MessageContext replyMessageContext = webServiceGenericXMLDocumentReceiver
                            .getFrontendPipeline().processMessage( messageContext );
                    List<MessagePayloadPojo> payloads = replyMessageContext.getMessagePojo()
                            .getMessagePayloads();
                    if ( !payloads.isEmpty() ) {
                        payload = payloads.iterator().next();
                    } else {
                        StringBuffer errorMessage = new StringBuffer(
                                "Errors occured during processing of Web services request" );
                        if ( replyMessageContext.getMessagePojo().getErrors() != null ) {
                            for ( Iterator iter = replyMessageContext.getMessagePojo().getErrors().iterator(); iter
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
                // TODO Auto-generated catch block
                e.printStackTrace();
                throw e;
            }

            return element;
        }
    } // inner class SimpleXMLDocServiceImpl

    /**
     * @return the frontendPipeline
     */
    public FrontendPipeline getFrontendPipeline() {

        return frontendPipeline;
    }

    /**
     * @param frontendPipeline the frontendPipeline to set
     */
    public void setFrontendPipeline( FrontendPipeline frontendPipeline ) {

        this.frontendPipeline = frontendPipeline;
    }

    /**
     * @return the xfire
     */
    public XFire getXFire() {

        return xFire;
    }

    /**
     * @param xfire the xfire to set
     */
    public void setXFire( XFire xFire ) {

        this.xFire = xFire;
    }

    /**
     * @param controller the controller to set
     */
    public void setController( XFireServletController controller ) {

        this.controller = controller;
    }

    /**
     * @return the synchronous
     */
    public boolean isSynchronous() {

        return synchronous;
    }

    /**
     * @param synchronous the synchronous to set
     */
    public void setSynchronous( boolean synchronous ) {

        this.synchronous = synchronous;
    }

} // WebServiceGenericXMLDocumentReceiver
