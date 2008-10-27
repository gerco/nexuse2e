package org.nexuse2e.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.Constants.BeanStatus;
import org.nexuse2e.Constants.Layer;
import org.nexuse2e.configuration.ConfigurationAccessService;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.Constants.ParameterType;
import org.nexuse2e.pojo.ActionPojo;
import org.nexuse2e.pojo.ChoreographyPojo;
import org.nexuse2e.pojo.ConnectionPojo;
import org.nexuse2e.pojo.ParticipantPojo;
import org.nexuse2e.pojo.PartnerPojo;
import org.nexuse2e.pojo.TRPPojo;

/**
 * This is a generic service implementation that can initiate polling activity over a
 * configurable TRP from the backend side.
 * 
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public class BackendPollingService extends AbstractService implements SchedulerClient {

    private static Logger      LOG                      = Logger.getLogger( BackendPollingService.class );

    public static final String POLL_INTERVAL_PARAM_NAME = "pollInterval";
    public static final String TRP_PROTOCOL_PARAM_NAME  = "trpProtocol";
    public static final String TRP_VERSION_PARAM_NAME   = "trpVersion";
    public static final String TRP_TRANSPORT_PARAM_NAME = "trpTransport";

    private SchedulingService schedulingService;
    

    @Override
    public void fillParameterMap( Map<String, ParameterDescriptor> parameterMap ) {

        parameterMap.put( POLL_INTERVAL_PARAM_NAME, new ParameterDescriptor( ParameterType.STRING,
                "Polling interval (sec)", "Polling interval in seconds", "300" ) );
        parameterMap.put( TRP_TRANSPORT_PARAM_NAME, new ParameterDescriptor( ParameterType.STRING,
                "TRP transport", "The TRP transport protocol identifier", "" ) );
        parameterMap.put( TRP_PROTOCOL_PARAM_NAME, new ParameterDescriptor( ParameterType.STRING,
                "TRP protocol", "The TRP application-level protocol identifier", "" ) );
        parameterMap.put( TRP_VERSION_PARAM_NAME, new ParameterDescriptor( ParameterType.STRING,
                "TRP version", "The TRP version number", "" ) );
    }

    
    @Override
    public Layer getActivationLayer() {

        return Layer.OUTBOUND_PIPELINES;
    }
    
    @Override
    public void start() {
        try {
            if ( getStatus() == BeanStatus.ACTIVATED ) {
                ConfigurationAccessService cas = Engine.getInstance().getActiveConfigurationAccessService();
                schedulingService = null;
                for ( Service service : cas.getServiceInstances() ) {
                    if ( service instanceof SchedulingService ) {
                        schedulingService = (SchedulingService) service;
                    }
                }
                if ( schedulingService == null ) {
                    throw new NexusException( "No ScheduleService implementation found" );
                }

                long interval = Integer.parseInt( (String) getParameter( POLL_INTERVAL_PARAM_NAME ) ) * 1000l;
                LOG.debug( "Using interval: " + interval );
                schedulingService.registerClient( this, interval );
                super.start();
            } else {
                LOG.error( "Service not in correct state to be started: " + status );
            }
        } catch (Exception ex) {
            LOG.error( "FtpPollingReceiver service could not be started", ex );
            status = BeanStatus.ERROR;
        }
    }
    
    public void poll() throws NexusException {
        LOG.trace( "Polling AgGateway web service..." );
        
        ConfigurationAccessService cas = Engine.getInstance().getActiveConfigurationAccessService();
        TRPPojo trp = cas.getTrpByProtocolVersionAndTransport(
                (String) getParameter( TRP_PROTOCOL_PARAM_NAME ),
                (String) getParameter( TRP_VERSION_PARAM_NAME ),
                (String) getParameter( TRP_TRANSPORT_PARAM_NAME ) );
        
        // find out which partners to poll
        List<ChoreographyPojo> choreographies = cas.getChoreographies();
        for (ChoreographyPojo choreography : choreographies) {
            for (ParticipantPojo participant : choreography.getParticipants()) {
                ConnectionPojo conn = participant.getConnection();
                if (conn.isPickUp() && (trp == null || trp.equals( conn.getTrp()))) {
                    PartnerPojo partner = participant.getPartner();
                    for (ActionPojo action : choreography.getActions()) {
                        if (!StringUtils.isBlank( action.getDocumentType() )) {
                            if (LOG.isTraceEnabled()) {
                                LOG.trace( "Polling for partner " + partner.getName() +
                                        " on choreography " + choreography.getName() +
                                        ", documentType is " + action.getDocumentType() );
                            }
                            // we got a pickUp connection on an action with a document type defined
                            // now, we map the document type to the template

                        }
                    }
                }
            }
        }
    }


    public void scheduleNotify() {
        if ( getStatus() == BeanStatus.STARTED ) {
            try {
                poll();
            } catch (NexusException e) {
                LOG.error( "Error polling AgGateway web service", e );
            }
        }
    }
}
