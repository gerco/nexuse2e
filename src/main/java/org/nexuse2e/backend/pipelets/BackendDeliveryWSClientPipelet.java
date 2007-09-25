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
import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.xfire.annotations.AnnotationServiceFactory;
import org.codehaus.xfire.client.Client;
import org.codehaus.xfire.client.XFireProxyFactory;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.transport.Channel;
import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.Constants.ParameterType;
import org.nexuse2e.integration.BackendDeliveryInterface;
import org.nexuse2e.integration.ProcessInboundMessageException;
import org.nexuse2e.messaging.AbstractPipelet;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.pojo.MessagePayloadPojo;

/**
 * A pipelet that sends messages to a backend delivery web service that can be configured.
 * @see BackendDeliveryInterface
 * 
 * @author jonas.reese
 */
public class BackendDeliveryWSClientPipelet extends AbstractPipelet {

    private static Logger LOG = Logger.getLogger( BackendDeliveryWSClientPipelet.class );
    
    public static final String URL_PARAM_NAME = "url";
    public static final String BASIC_AUTH_PARAM_NAME = "basicAuth";
    public static final String USERNAME_PARAM_NAME = "username";
    public static final String PASSWORD_PARAM_NAME = "password";
    
    
    public BackendDeliveryWSClientPipelet() {
        parameterMap.put( URL_PARAM_NAME, new ParameterDescriptor( ParameterType.STRING, "URL",
                "The backend delivery web service URL", "" ) );
        parameterMap.put( BASIC_AUTH_PARAM_NAME,
                new ParameterDescriptor( ParameterType.BOOLEAN, "HTTP Basic Authentication",
                        "Enable HTTP Basic Authentication", Boolean.FALSE ) );
        parameterMap.put( USERNAME_PARAM_NAME,
                new ParameterDescriptor( ParameterType.STRING, "User Name",
                        "HTTP Basic Auth User Name", "" ) );
        parameterMap.put( PASSWORD_PARAM_NAME,
                new ParameterDescriptor( ParameterType.PASSWORD, "Password",
                        "HTTP Basic Auth Password", "" ) );
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.AbstractPipelet#processMessage(org.nexuse2e.messaging.MessageContext)
     */
    @Override
    public MessageContext processMessage( MessageContext messageContext )
            throws IllegalArgumentException, IllegalStateException,
            NexusException {

        AnnotationServiceFactory objectServiceFactory = new AnnotationServiceFactory();
        Service serviceModel = objectServiceFactory.create( BackendDeliveryInterface.class );
        try {
            BackendDeliveryInterface service = (BackendDeliveryInterface)
                    new XFireProxyFactory().create( serviceModel, (String) getParameter( URL_PARAM_NAME ) );
            
            // HTTP basic auth
            boolean httpAuth = ((Boolean) getParameter( BASIC_AUTH_PARAM_NAME )).booleanValue();
            if (httpAuth) {
                String username = getParameter( USERNAME_PARAM_NAME );
                String password = getParameter( PASSWORD_PARAM_NAME );
                
                if (username == null) {
                    username = "";
                }
                if (password == null) {
                    password = "";
                }
                
                Client client = Client.getInstance( service );
                client.setProperty( Channel.USERNAME, username );
                client.setProperty( Channel.PASSWORD, password );
            }
            
            List<MessagePayloadPojo> payloadPojos = messageContext.getMessagePojo().getMessagePayloads();
            String payload = null;
            if (!payloadPojos.isEmpty()) {
                payload = new String( payloadPojos.get( 0 ).getPayloadData() );
            }
            String actionId = null;
            if (messageContext.getMessagePojo().getConversation().getCurrentAction() != null) {
                actionId = messageContext.getMessagePojo().getConversation().getCurrentAction().getName();
            }
            String response = service.processInboundMessage( messageContext.getChoreography().getName(),
                    messageContext.getPartner().getName(),
                    actionId,
                    messageContext.getConversation().getConversationId(),
                    messageContext.getMessagePojo().getMessageId(), payload );
            LOG.debug( "response from backend delivery WS is " + response );
            return messageContext;
        } catch (MalformedURLException e) {
            throw new NexusException( e );
        } catch (RemoteException e) {
            throw new NexusException( e );
        } catch (ProcessInboundMessageException e) {
            throw new NexusException( e );
        }
    }

}
