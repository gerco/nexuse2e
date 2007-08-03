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
package org.nexuse2e.test.backend;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.Constants.BeanStatus;
import org.nexuse2e.Constants.Layer;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.Constants.ParameterType;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.service.AbstractService;
import org.nexuse2e.service.SchedulerClient;
import org.nexuse2e.service.SchedulingService;
import org.nexuse2e.service.Service;

public class TestMessageSenderService extends AbstractService implements SchedulerClient {

    private static Logger       LOG               = Logger.getLogger( TestMessageSenderService.class );

    public final static String  SCHEDULINGSERVICE = "schedulingname";
    public final static String  CHOREOGRAPHY      = "choreography";
    public final static String  ACTION            = "action";
    public final static String  PARTNER           = "partner";
    public final static String  INTERVAL          = "interval";
    private final static String DEFAULT_PAYLOAD   = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<test from=\"Xioma\"/>";

    private SchedulingService   schedulingService = null;
    private String              choreography      = null;
    private String              action            = null;
    private String              partner           = null;
    private int                 interval          = 5000;

    @Override
    public void fillParameterMap( Map<String, ParameterDescriptor> parameterMap ) {

        parameterMap.put( SCHEDULINGSERVICE, new ParameterDescriptor( ParameterType.SERVICE, "Scheduling Service",
                "The name of the service that shall be used for time schedule", "" ) );
        parameterMap.put( CHOREOGRAPHY, new ParameterDescriptor( ParameterType.STRING, "Choreography",
                "The name of the Choreography used for testing", "" ) );
        parameterMap.put( ACTION, new ParameterDescriptor( ParameterType.STRING, "Action",
                "The Action used for testing", "" ) );
        parameterMap.put( PARTNER, new ParameterDescriptor( ParameterType.STRING, "Partner",
                "The Partner used for testing", "" ) );
        parameterMap.put( INTERVAL, new ParameterDescriptor( ParameterType.STRING, "Interval",
                "Interval inbetween test messages (Millseconds)", "5000" ) );
    }

    @Override
    public Layer getActivationLayer() {

        return Layer.INTERFACES;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.service.AbstractService#start()
     */
    @Override
    public void start() {

        LOG.trace( "starting" );
        if ( schedulingService != null ) {
            ( (SchedulingService) schedulingService ).registerClient( this, interval );
        } else {
            LOG.error( "No scheduling service configured!" );
        }

        super.start();
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.service.AbstractService#stop()
     */
    @Override
    public void stop() {

        LOG.trace( "stopping" );
        if ( schedulingService != null ) {
            schedulingService.deregisterClient( this );
        } else {
            LOG.error( "No scheduling service configured!" );
        }
        super.stop();
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.service.AbstractService#initialize(org.nexuse2e.configuration.EngineConfiguration)
     */
    @Override
    public void initialize( EngineConfiguration config ) throws InstantiationException {

        LOG.trace( "initializing" );
        String schedulingServiceName = getParameter( SCHEDULINGSERVICE );
        action = getParameter( ACTION );
        choreography = getParameter( CHOREOGRAPHY );
        partner = getParameter( PARTNER );
        interval = Integer.parseInt( (String) getParameter( INTERVAL ) );

        if ( !StringUtils.isEmpty( schedulingServiceName ) ) {

            Service service = Engine.getInstance().getActiveConfigurationAccessService().getService(
                    schedulingServiceName );
            if ( service == null ) {
                status = BeanStatus.ERROR;
                throw new InstantiationException( "Service not found in configuration: " + schedulingServiceName );
            }
            if ( !( service instanceof SchedulingService ) ) {
                status = BeanStatus.ERROR;
                throw new InstantiationException( schedulingServiceName + " is instance of "
                        + service.getClass().getName() + " but SchedulingService is required" );
            }
            schedulingService = (SchedulingService) service;

        } else {
            status = BeanStatus.ERROR;
            throw new InstantiationException(
                    "SchedulingService is not properly configured (schedulingServiceObj == null)!" );
        }

        super.initialize( config );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.service.AbstractService#teardown()
     */
    @Override
    public void teardown() {

        LOG.trace( "teardown" );
        schedulingService = null;
        super.teardown();
    }

    public void scheduleNotify() {

        MessageContext messageContext;
        try {
            messageContext = Engine.getInstance().getCurrentConfiguration().getBackendPipelineDispatcher()
                    .processMessage( partner, choreography, action, null, null, null, DEFAULT_PAYLOAD.getBytes() );
            String conversationId = messageContext.getMessagePojo().getConversation().getConversationId();
            String messageId = messageContext.getMessagePojo().getMessageId();

            LOG.debug( "Created test message " + conversationId + "/" + messageId );
        } catch ( NexusException e ) {
            LOG.error( "Error sending test message: " + e );
            e.printStackTrace();
        }
    }
}
