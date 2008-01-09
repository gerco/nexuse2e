package org.nexuse2e.configuration;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;
import org.nexuse2e.pojo.ActionPojo;
import org.nexuse2e.pojo.CertificatePojo;
import org.nexuse2e.pojo.ChoreographyPojo;
import org.nexuse2e.pojo.ComponentPojo;
import org.nexuse2e.pojo.ConnectionPojo;
import org.nexuse2e.pojo.FollowUpActionPojo;
import org.nexuse2e.pojo.LoggerParamPojo;
import org.nexuse2e.pojo.LoggerPojo;
import org.nexuse2e.pojo.MappingPojo;
import org.nexuse2e.pojo.ParticipantPojo;
import org.nexuse2e.pojo.PartnerPojo;
import org.nexuse2e.pojo.PipeletParamPojo;
import org.nexuse2e.pojo.PipeletPojo;
import org.nexuse2e.pojo.PipelinePojo;
import org.nexuse2e.pojo.RolePojo;
import org.nexuse2e.pojo.ServiceParamPojo;
import org.nexuse2e.pojo.ServicePojo;
import org.nexuse2e.pojo.TRPPojo;
import org.nexuse2e.pojo.UserPojo;
import org.w3c.dom.Node;

/**
 * This is a <code>BaseConfigurationProvider</code> implementation that can provide a
 * base configuration from an XML file.
 *
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public class XmlBaseConfigurationProvider implements BaseConfigurationProvider {

    private static Logger LOG = Logger.getLogger( XmlBaseConfigurationProvider.class );
    
    private Unmarshaller unmarshaller;
    private Object source;
    private Object root;
    
    public XmlBaseConfigurationProvider( InputStream in ) {
        source = in;
        root = null;
    }
    
    public XmlBaseConfigurationProvider( File file ) {
        source = file;
        root = null;
    }
    
    public XmlBaseConfigurationProvider( String url ) throws MalformedURLException {
        if (url == null || url.trim().length() == 0) {
            source = null;
        } else {
            source = new URL( url );
        }
        root = null;
    }
    
    public XmlBaseConfigurationProvider( URL url ) {
        source = url;
        root = null;
    }
    
    public XmlBaseConfigurationProvider( Node node ) {
        source = node;
        root = null;
    }

    
    public void createBaseConfiguration(
            List<ComponentPojo> components,
            List<ChoreographyPojo> choreographies,
            List<PartnerPojo> partners,
            List<PipelinePojo> backendPipelineTemplates,
            List<PipelinePojo> frontendPipelineTemplates,
            List<ServicePojo> services,
            List<CertificatePojo> certificates,
            List<TRPPojo> trps,
            List<UserPojo> users,
            List<RolePojo> roles,
            List<LoggerPojo> loggers,
            List<MappingPojo> mappings )
            throws InstantiationException {
        
        try {
            unmarshall();
            if (root == null) {
                throw new InstantiationException( getClass().getSimpleName() + " cannot provide configuration" );
            }
            
            EngineConfiguration configuration = (EngineConfiguration) root;

            // create component map
            Map<Integer, ComponentPojo> componentMap = new HashMap<Integer, ComponentPojo>();
            if (configuration.getComponents() != null) {
                for (ComponentPojo component : configuration.getComponents()) {
                    componentMap.put( component.getNxComponentId(), component );
                }
            }

            // map TRPs
            Map<Integer, TRPPojo> trpMap = new HashMap<Integer, TRPPojo>();
            if (configuration.getTrps() != null) {
                for (TRPPojo trp : configuration.getTrps()) {
                    trpMap.put( trp.getNxTRPId(), trp );
                }
            }
            
            // map certificates
            Map<Integer, CertificatePojo> certificateMap = new HashMap<Integer, CertificatePojo>();
            if (configuration.getCertificates() != null) {
                for (CertificatePojo certificate : configuration.getCertificates()) {
                    certificateMap.put( certificate.getNxCertificateId(), certificate );
                }
            }
            
            // patch pipelines and pipelets, map pipelines
            Map<Integer, PipelinePojo> pipelineMap = new HashMap<Integer, PipelinePojo>();
            for (int i = 0; i < 2; i++) {
                List<PipelinePojo> pipelines; 
                if (i == 0) { // backend
                    pipelines = configuration.getBackendPipelineTemplates();
                } else {      // frontend
                    pipelines = configuration.getFrontendPipelineTemplates();
                }
                if (pipelines != null) {
                    for (PipelinePojo pipeline : pipelines) {
                        pipelineMap.put( pipeline.getNxPipelineId(), pipeline );
                        if (pipeline.getPipelets() != null) {
                            for (PipeletPojo pipelet : pipeline.getPipelets()) {
                                pipelet.setPipeline( pipeline );
                                pipelet.setComponent( componentMap.get( pipelet.getComponentId() ) );
                                if (pipelet.getPipeletParams() != null) {
                                    for (PipeletParamPojo param : pipelet.getPipeletParams()) {
                                        param.setPipelet( pipelet );
                                        param.setNxPipeletParamId( 0 );
                                    }
                                }
                                pipelet.setNxPipeletId( 0 );
                            }
                        } else {
                            pipeline.setPipelets( new ArrayList<PipeletPojo>() );
                        }
                        pipeline.setTrp( trpMap.get( pipeline.getTrpId() ) );
                    }
                }
            }
            
            // patch connections, patch and map partners and connections
            Map<Integer, PartnerPojo> partnerMap = new HashMap<Integer, PartnerPojo>();
            Map<Integer, ConnectionPojo> connectionMap = new HashMap<Integer, ConnectionPojo>();
            if (configuration.getPartners() != null) {
                for (PartnerPojo partner : configuration.getPartners()) {
                    partnerMap.put( partner.getNxPartnerId(), partner );
                    if (partner.getConnections() != null) {
                        for (ConnectionPojo connection : partner.getConnections()) {
                            connectionMap.put( connection.getNxConnectionId(), connection );
                            connection.setTrp( trpMap.get( connection.getTrpId() ) );
                            connection.setPartner( partner );
                            connection.setNxConnectionId( 0 );
                        }
                    }
                    Set<CertificatePojo> certificateSet = new HashSet<CertificatePojo>();
                    partner.setCertificates( null );
                    if (partner.getCertificateIds() != null) {
                        for (Integer id : partner.getCertificateIds()) {
                            CertificatePojo cert = certificateMap.get( id );
                            if (cert != null) {
                                cert.setPartner( partner );
                                certificateSet.add( cert );
                            }
                        }
                    }
                    partner.setCertificates( certificateSet );
                }
            }
            
            // map actions
            Map<Integer, ActionPojo> actionMap = new HashMap<Integer, ActionPojo>();
            Map<Integer, FollowUpActionPojo> followUpActionMap = new HashMap<Integer, FollowUpActionPojo>();
            if (configuration.getChoreographies() != null) {
                for (ChoreographyPojo choreography : configuration.getChoreographies()) {
                    if (choreography.getActions() != null) {
                        for (ActionPojo action : choreography.getActions()) {
                            actionMap.put( action.getNxActionId(), action );
                            if (action.getFollowUpActions() != null) {
                                for (FollowUpActionPojo followUpAction : action.getFollowUpActions()) {
                                    followUpActionMap.put( followUpAction.getNxFollowUpActionId(), followUpAction );
                                }
                            }
                            if (action.getFollowedActions() != null) {
                                for (FollowUpActionPojo followUpAction : action.getFollowedActions()) {
                                    followUpActionMap.put( followUpAction.getNxFollowUpActionId(), followUpAction );
                                }
                            }
                        }
                    }
                }
            }
            
            // patch choreographies, actions and participants
            if (configuration.getChoreographies() != null) {
                for (ChoreographyPojo choreography : configuration.getChoreographies()) {
                    if (choreography.getActions() != null) {
                        for (ActionPojo action : choreography.getActions()) {
                            action.setChoreography( choreography );
                            action.setInboundPipeline( pipelineMap.get( action.getInboundPipelineId() ) );
                            action.setOutboundPipeline( pipelineMap.get( action.getOutboundPipelineId() ) );
                            action.setStatusUpdatePipeline( pipelineMap.get( action.getStatusUpdatePipelineId() ) );
                            Set<FollowUpActionPojo> followUpActions = new HashSet<FollowUpActionPojo>();
                            Set<FollowUpActionPojo> followedActions = new HashSet<FollowUpActionPojo>();
                            for (int i = 0; i < 2; i++) {
                                Set<FollowUpActionPojo> followActions;
                                if (i == 0) {
                                    followActions = action.getFollowUpActions();
                                } else {
                                    followActions = action.getFollowedActions();
                                }
                                if (followActions != null) {
                                    for (FollowUpActionPojo followUpAction : followActions) {
                                        followUpAction = followUpActionMap.get( followUpAction.getNxFollowUpActionId() );
                                        followUpAction.setFollowUpAction( actionMap.get( followUpAction.getFollowUpActionId() ) );
                                        followUpAction.setAction( actionMap.get( followUpAction.getActionId() ) );
                                        if (i == 0) {
                                            followUpActions.add( followUpAction );
                                        } else {
                                            followedActions.add( followUpAction );
                                        }
                                    }
                                }
                            }

                            action.setFollowUpActions( followUpActions );
                            action.setFollowedActions( followedActions );
                        }
                    } else {
                        choreography.setActions( new HashSet<ActionPojo>() );
                    }
                    if (choreography.getParticipants() != null) {
                        for (ParticipantPojo participant : choreography.getParticipants()) {
                            participant.setChoreography( choreography );
                            participant.setPartner( partnerMap.get( participant.getPartnerId() ) );
                            participant.setLocalPartner( partnerMap.get( participant.getLocalPartnerId() ) );
                            participant.setConnection( connectionMap.get( participant.getConnectionId() ) );
                            participant.setNxParticipantId( 0 );
                        }
                    } else {
                        choreography.setParticipants( new ArrayList<ParticipantPojo>() );
                    }
                    choreography.setNxChoreographyId( 0 );
                }
            }
            
            // patch action IDs
            for (ActionPojo action : actionMap.values()) {
                action.setNxActionId( 0 );
            } 

            // patch follow-up action IDs
            for (FollowUpActionPojo followUpAction : followUpActionMap.values()) {
                followUpAction.setNxFollowUpActionId( 0 );
            }
            
            // map roles
            Map<Integer, RolePojo> roleMap = new HashMap<Integer, RolePojo>();
            if (configuration.getRoles() != null) {
                for (RolePojo role : configuration.getRoles()) {
                    roleMap.put( role.getNxRoleId(), role );
                }
            }
            
            // patch users
            if (configuration.getUsers() != null) {
                for (UserPojo user : configuration.getUsers()) {
                    user.setRole( roleMap.get( user.getRoleId() ) );
                    user.setNxUserId( 0 );
                }
            }
            
            // patch loggers
            if (configuration.getLoggers() != null) {
                for (LoggerPojo logger : configuration.getLoggers()) {
                    logger.setComponent( componentMap.get( logger.getComponentId() ) );
                    logger.setNxLoggerId( 0 );
                    if (logger.getLoggerParams() != null) {
                        for (LoggerParamPojo loggerParam : logger.getLoggerParams()) {
                            loggerParam.setLogger( logger );
                            loggerParam.setNxLoggerParamId( 0 );
                        }
                    }
                }
            }
            
            // patch services
            if (configuration.getServices() != null) {
                for (ServicePojo service : configuration.getServices()) {
                    service.setComponent( componentMap.get( service.getComponentId() ) );
                    service.setNxServiceId( 0 );
                    if (service.getServiceParams() != null) {
                        for (ServiceParamPojo serviceParam : service.getServiceParams()) {
                            serviceParam.setService( service );
                            serviceParam.setNxServiceParamId( 0 );
                        }
                    }
                }
            }
            
            addAll( components, configuration.getComponents() );
            addAll( choreographies, configuration.getChoreographies() );
            addAll( partners, configuration.getPartners() );
            addAll( backendPipelineTemplates, configuration.getBackendPipelineTemplates() );
            addAll( frontendPipelineTemplates, configuration.getFrontendPipelineTemplates() );
            addAll( services, configuration.getServices() );
            addAll( certificates, configuration.getCertificates() );
            addAll( trps, configuration.getTrps() );
            addAll( users, configuration.getUsers() );
            addAll( roles, configuration.getRoles() );
            addAll( loggers, configuration.getLoggers() );
            addAll( mappings, configuration.getMappings() );
            
            // patch primary object's IDs
            for (ComponentPojo component : components) {
                component.setNxComponentId( 0 );
            }
            for (ChoreographyPojo choreography : choreographies) {
                choreography.setNxChoreographyId( 0 );
            }
            for (PartnerPojo partner : partners) {
                partner.setNxPartnerId( 0 );
            }
            for (PipelinePojo pipeline : backendPipelineTemplates) {
                pipeline.setNxPipelineId( 0 );
            }
            for (PipelinePojo pipeline : frontendPipelineTemplates) {
                pipeline.setNxPipelineId( 0 );
            }
            for (ServicePojo service : services) {
                service.setNxServiceId( 0 );
            }
            for (CertificatePojo certificate : certificates) {
                certificate.setNxCertificateId( 0 );
            }
            for (TRPPojo trp : trps) {
                trp.setNxTRPId( 0 );
            }
            for (RolePojo role : roles) {
                role.setNxRoleId( 0 );
            }
            for (MappingPojo mapping : mappings) {
                mapping.setNxMappingId( 0 );
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            LOG.error( ex );
            throw new InstantiationException( ex.getClass().getName() + ", message: " + ex.getMessage() );
        }
    }
    
    // null-safe addAll method
    private static <T> void addAll( List<T> target, List<T> source ) {
        if (source != null && target != null) {
            target.addAll( source );
        }
    }
    
    private void unmarshall() throws JAXBException {
        if (root != null) {
            return;
        }
        
        JAXBContext jaxbContext = JAXBContext.newInstance( EngineConfiguration.class );
        unmarshaller = jaxbContext.createUnmarshaller();
        unmarshaller.setEventHandler(new javax.xml.bind.helpers.DefaultValidationEventHandler());

        if (source == null) {
            return;
        }
        
        if (source instanceof InputStream) {
            root = unmarshaller.unmarshal( (InputStream) source );
        } else if (source instanceof File) {
            root = unmarshaller.unmarshal( (File) source );
        } else if (source instanceof URL) {
            root = unmarshaller.unmarshal( (URL) source );
        } else {
            root = unmarshaller.unmarshal( (Node) source );
        }
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.configuration.BaseConfigurationProvider#isConfigurationAvailable()
     */
    public boolean isConfigurationAvailable() {
        if (source == null) {
            return false;
        }
        try {
            unmarshall();
        } catch (JAXBException ignored) {
        }
        return root != null;
    }

}