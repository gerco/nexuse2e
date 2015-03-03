/*
 * Copyright (c) 2014 solutions direkt Gesellschaft für Lösungsentwicklung mbH
 *
 * Eigentum der solutions direkt GmbH.
 * Alle Rechte vorbehalten.
 *
 * Property of solutions direkt GmbH.
 * All rights reserved.
 *
 * solutions direkt Gesellschaft für Lösungsentwicklung mbH
 * Griegstraße 75, Haus 26
 * 22763 Hamburg
 * Deutschland / Germany
 *
 * Phone: +49 40 88155-0
 * Fax: +49 40 88155-5400
 * Email: info@direkt-gruppe.de
 * Web: http://www.direkt-gruppe.de
 */
package org.nexuse2e.integration.secure;


import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;
import org.apache.log4j.Logger;
import org.apache.ws.security.WSConstants;
import org.apache.ws.security.WSPasswordCallback;
import org.apache.ws.security.handler.WSHandlerConstants;
import org.nexuse2e.BeanStatus;
import org.nexuse2e.DynamicWSDispatcherServlet;
import org.nexuse2e.Layer;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.ParameterType;
import org.nexuse2e.integration.NEXUSe2eInterfaceImpl;
import org.nexuse2e.service.AbstractService;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.xml.ws.Endpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * Dispatcher service for the NEXUSe2eInterfaceSecure web service.
 *
 * @author Jascha Jerke
 */
public class WSDispatcherService extends AbstractService {
    private static Logger LOG = Logger.getLogger(WSDispatcherService.class);

    private static String URL_PARAM_NAME = "url";
    private static String USER_PARAM_NAME = "username";
    private static String PASS_PARAM_NAME = "password";

    private Endpoint endpoint;

    @Override
    public void fillParameterMap(Map<String, ParameterDescriptor> parameterMap) {
        parameterMap.put(URL_PARAM_NAME,
                         new ParameterDescriptor(ParameterType.STRING, "Web service URL", "The last part of the web service URL, e.g. /sendMessage", ""));
        parameterMap.put(USER_PARAM_NAME, new ParameterDescriptor(ParameterType.STRING, "Username", "Username for WS-Security on this endpoint.", ""));
        parameterMap.put(PASS_PARAM_NAME, new ParameterDescriptor(ParameterType.PASSWORD, "Password", "Password for WS-Security on this endpoint.", ""));
    }

    @Override
    public Layer getActivationLayer() {
        return Layer.INBOUND_PIPELINES;
    }

    public void start() {
        if (BeanStatus.STARTED == getStatus()) {
            return;
        }

        String url = getParameter(URL_PARAM_NAME);
        final String username = getParameter(USER_PARAM_NAME);
        final String password = getParameter(PASS_PARAM_NAME);
        LOG.debug("Secure web service URL extension: " + url);

        try {
            Object implementer = new NEXUSe2eInterfaceImpl();
            endpoint = Endpoint.publish(url, implementer);
            org.apache.cxf.endpoint.Endpoint cxfEndpoint = ((EndpointImpl) endpoint).getServer().getEndpoint();
            Map<String, Object> inProps = new HashMap<>();
            inProps.put(WSHandlerConstants.ACTION, WSHandlerConstants.USERNAME_TOKEN);
            inProps.put(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_TEXT);
            CallbackHandler callback = new CallbackHandler() {
                public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
                    WSPasswordCallback pc = (WSPasswordCallback) callbacks[0];
                    String user = pc.getIdentifier();
                    if (user.equals(username)) {
                        pc.setPassword(password);
                    }

                }
            };
            inProps.put(WSHandlerConstants.PW_CALLBACK_REF, callback);
            WSS4JInInterceptor wssIn = new WSS4JInInterceptor(inProps);
            cxfEndpoint.getInInterceptors().add(wssIn);
            if (LOG.isTraceEnabled()) {
                cxfEndpoint.getOutInterceptors().add(new LoggingOutInterceptor());
                cxfEndpoint.getInInterceptors().add(new LoggingInInterceptor());
            }
            super.start();
        } catch (Exception ex) {
            ex.printStackTrace();
            LOG.error(ex);
        }
    }

    public void stop() {

        if (endpoint != null) {
            endpoint.stop();
            endpoint = null;
            DynamicWSDispatcherServlet.getInstance().reinitialize();
        }
        super.stop();
    }
}
