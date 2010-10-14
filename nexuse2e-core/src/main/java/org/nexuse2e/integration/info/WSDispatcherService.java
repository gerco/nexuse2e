/**
 *  NEXUSe2e Business Messaging Open Source
 *  Copyright 2000-2009, Tamgroup and X-ioma GmbH
 *
 *  This is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation version 2.1 of
 *  the License.
 *
 *  This software is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this software; if not, write to the Free
 *  Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *  02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.nexuse2e.integration.info;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
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
import org.nexuse2e.Constants.BeanStatus;
import org.nexuse2e.Constants.Layer;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.Constants.ParameterType;
import org.nexuse2e.pojo.UserPojo;
import org.nexuse2e.service.AbstractService;
import org.nexuse2e.util.PasswordUtil;

/**
 * A service that dynamically registers the NEXUSe2e statistics web service.
 *
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public class WSDispatcherService extends AbstractService {

    private static Logger       LOG                     = Logger.getLogger( WSDispatcherService.class );

    private static final String URL_PARAM_NAME          = "url";

    private Endpoint            endpoint;

    
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
            Object implementor = new NEXUSe2eInfoServiceImpl();
            endpoint = Endpoint.publish( url, implementor );
            org.apache.cxf.endpoint.Endpoint cxfEndpoint = ((EndpointImpl) endpoint).getServer().getEndpoint();
            Map<String,Object> inProps= new HashMap<String,Object>();
            inProps.put( WSHandlerConstants.ACTION, WSHandlerConstants.USERNAME_TOKEN );
            inProps.put( WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_TEXT );
            CallbackHandler callback = new CallbackHandler() {
                public void handle( Callback[] callbacks ) throws IOException, UnsupportedCallbackException {
                    WSPasswordCallback pc = (WSPasswordCallback) callbacks[0];
                    String user = pc.getIdentifer();
                    String pass = null;
                    try {
                        pass = PasswordUtil.hashPassword( pc.getPassword() );
                    } catch (NoSuchAlgorithmException e) {
                        LOG.error( e );
                    }

                    EngineConfiguration engineConfig = Engine.getInstance().getCurrentConfiguration();
                    if (engineConfig != null) {
                        UserPojo userInstance = engineConfig.getUserByLoginName( user );

                        // check password
                        if  (userInstance == null || !userInstance.getPassword().equals( pass )) {
                            String m = "User " + pc.getIdentifer() + " tried to access NEXUSe2eInfo web service with an incorrect password";
                            LOG.error( m );
                            throw new SecurityException( m );
                        }
                        pc.setPassword( pass );
                    }
                }
            };
            inProps.put( WSHandlerConstants.PW_CALLBACK_REF, callback );
            WSS4JInInterceptor wssIn = new WSS4JInInterceptor( inProps );
            cxfEndpoint.getInInterceptors().add( wssIn );
            if (LOG.isTraceEnabled()) {
                cxfEndpoint.getOutInterceptors().add( new LoggingOutInterceptor() );
                cxfEndpoint.getInInterceptors().add( new LoggingInInterceptor() );
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
}
