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

package org.nexuse2e.messaging.generic.ws;

import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.xfire.client.XFireProxyFactory;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
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
public class BackendDeliveryClientPipelet extends AbstractPipelet {

    private static Logger LOG = Logger.getLogger( BackendDeliveryClientPipelet.class );
    
    public static final String URL_PARAM_NAME = "url";
    
    
    public BackendDeliveryClientPipelet() {
        parameterMap.put( URL_PARAM_NAME, new ParameterDescriptor( ParameterType.STRING, "URL",
                "The backend delivery web service URL", "" ) );
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.AbstractPipelet#processMessage(org.nexuse2e.messaging.MessageContext)
     */
    @Override
    public MessageContext processMessage( MessageContext messageContext )
            throws IllegalArgumentException, IllegalStateException,
            NexusException {

        ObjectServiceFactory objectServiceFactory = new ObjectServiceFactory();
        Service serviceModel = objectServiceFactory.create( BackendDeliveryInterface.class );
        try {
            BackendDeliveryInterface service = (BackendDeliveryInterface)
                    new XFireProxyFactory().create( serviceModel, (String) getParameter( URL_PARAM_NAME ) );
            List<MessagePayloadPojo> payloadPojos = messageContext.getMessagePojo().getMessagePayloads();
            String[] payloadStrings = new String[payloadPojos.size()];
            for (int i = 0; i < payloadPojos.size(); i++) {
                payloadStrings[i] = new String( payloadPojos.get( i ).getPayloadData() );
            }
            String actionId = null;
            if (messageContext.getMessagePojo().getAction() != null) {
                messageContext.getMessagePojo().getAction().getName();
            }
            String response = service.processInboundMessage( messageContext.getChoreography().getName(),
                    messageContext.getPartner().getName(),
                    actionId,
                    messageContext.getConversation().getConversationId(),
                    messageContext.getMessagePojo().getMessageId(), payloadStrings );
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
