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
package org.nexuse2e.configuration;

import java.io.IOException;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.nexuse2e.ActionSpecificKey;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.ProtocolSpecificKey;
import org.nexuse2e.Constants.BeanStatus;
import org.nexuse2e.backend.BackendPipelineDispatcher;
import org.nexuse2e.configuration.Constants.ComponentType;
import org.nexuse2e.configuration.Constants.ParameterType;
import org.nexuse2e.messaging.BackendActionSerializer;
import org.nexuse2e.messaging.BackendInboundDispatcher;
import org.nexuse2e.messaging.BackendOutboundDispatcher;
import org.nexuse2e.messaging.BackendPipeline;
import org.nexuse2e.messaging.FrontendActionSerializer;
import org.nexuse2e.messaging.FrontendInboundDispatcher;
import org.nexuse2e.messaging.FrontendInboundResponseEndpoint;
import org.nexuse2e.messaging.FrontendOutboundDispatcher;
import org.nexuse2e.messaging.FrontendOutboundResponseEndpoint;
import org.nexuse2e.messaging.FrontendPipeline;
import org.nexuse2e.messaging.Pipelet;
import org.nexuse2e.messaging.ProtocolAdapter;
import org.nexuse2e.pojo.ActionPojo;
import org.nexuse2e.pojo.CertificatePojo;
import org.nexuse2e.pojo.ChoreographyPojo;
import org.nexuse2e.pojo.ComponentPojo;
import org.nexuse2e.pojo.ConnectionPojo;
import org.nexuse2e.pojo.ConversationPojo;
import org.nexuse2e.pojo.GenericParamPojo;
import org.nexuse2e.pojo.LoggerPojo;
import org.nexuse2e.pojo.MappingPojo;
import org.nexuse2e.pojo.NEXUSe2ePojo;
import org.nexuse2e.pojo.ParticipantPojo;
import org.nexuse2e.pojo.PartnerPojo;
import org.nexuse2e.pojo.PipeletPojo;
import org.nexuse2e.pojo.PipelinePojo;
import org.nexuse2e.pojo.RolePojo;
import org.nexuse2e.pojo.ServicePojo;
import org.nexuse2e.pojo.TRPPojo;
import org.nexuse2e.pojo.UserPojo;
import org.nexuse2e.service.AbstractControllerService;
import org.nexuse2e.service.Service;
import org.nexuse2e.transport.TransportReceiver;
import org.nexuse2e.util.CertificateUtil;
import org.nexuse2e.util.EncryptionUtil;
import org.springframework.context.support.ApplicationObjectSupport;

/**
 * @author gesch
 *
 */
@XmlRootElement(name = "NEXUSe2eConfiguration")
@XmlType(name = "NEXUSe2eConfigurationType")
@XmlAccessorType(XmlAccessType.NONE)
public class EngineConfiguration implements ConfigurationAccessService {

    private static Logger                               LOG                       = Logger
                                                                                          .getLogger( EngineConfiguration.class );

    private Map<ActionSpecificKey, BackendPipeline>     backendInboundPipelines   = new HashMap<ActionSpecificKey, BackendPipeline>();
    private Map<ActionSpecificKey, BackendPipeline>     backendOutboundPipelines  = new HashMap<ActionSpecificKey, BackendPipeline>();
    private Map<TRPPojo, FrontendPipeline>              frontendInboundPipelines  = new HashMap<TRPPojo, FrontendPipeline>();
    private Map<TRPPojo, FrontendPipeline>              frontendOutboundPipelines = new HashMap<TRPPojo, FrontendPipeline>();
    private Map<String, FrontendActionSerializer>       frontendActionSerializers = new HashMap<String, FrontendActionSerializer>();
    private Map<String, BackendActionSerializer>        backendActionSerializers  = new HashMap<String, BackendActionSerializer>();
    private StaticBeanContainer                         staticBeanContainer       = null;

    private long                                        timestamp;
    
    private int                                         recentNxId                = -1;
    private List<NEXUSe2ePojo>                          updateList                = new ArrayList<NEXUSe2ePojo>();
    private Map<NEXUSe2ePojo, List<NEXUSe2ePojo>>       implicitUpdateList        = new HashMap<NEXUSe2ePojo, List<NEXUSe2ePojo>>();
    private List<NEXUSe2ePojo>                          deleteList                = new ArrayList<NEXUSe2ePojo>();

    
    /**
     * contains the ChoreographyPojos incl. ActionPojos
     */
    @XmlElementWrapper(name = "Choreographies")
    @XmlElement(name = "Choreography")
    private List<ChoreographyPojo>                      choreographies            = null;

    /**
     * contains all certicates and requests. 
     */
    @XmlElementWrapper(name = "Certificates")
    @XmlElement(name = "Certificate")
    private List<CertificatePojo>                       certificates              = null;

    /**
     * contains all PartnerPojos incl. partnerdependent Certificate(pojos)s, Connection(pojo)s and Contact(pojo)s
     */
    @XmlElementWrapper(name = "Partners")
    @XmlElement(name = "Partner")
    private List<PartnerPojo>                           partners                  = null;

    /**
     * List that contains all frontend <code>PipelinePojo<code> instances.
     */
    @XmlElementWrapper(name = "FrontendPipelines")
    @XmlElement(name = "Pipeline")
    private List<PipelinePojo>                          frontendPipelineTemplates = null;

    /**
     * List that contains all backend <code>PipelinePojo<code> instances.
     */
    @XmlElementWrapper(name = "BackendPipeline")
    @XmlElement(name = "Pipeline")
    private List<PipelinePojo>                          backendPipelineTemplates  = null;

    /**
     /**
     * List that contains all <code>TRPPojo<code> instances.
     */
    @XmlElementWrapper(name = "TRPs")
    @XmlElement(name = "TRP")
    private List<TRPPojo>                               trps                      = null;

    /**
     * List that contains all <code>ComponentPojo<code> instances.
     */
    @XmlElementWrapper(name = "Components")
    @XmlElement(name = "Component")
    private List<ComponentPojo>                         components                = null;

    @XmlElementWrapper(name = "Loggers")
    @XmlElement(name = "Logger")
    private List<LoggerPojo>                            loggers                   = null;

    @XmlElementWrapper(name = "Services")
    @XmlElement(name = "Service")
    private List<ServicePojo>                           services                  = null;

    @XmlElementWrapper(name = "Users")
    @XmlElement(name = "User")
    private List<UserPojo>                              users                     = null;

    @XmlElementWrapper(name = "Roles")
    @XmlElement(name = "Role")
    private List<RolePojo>                              roles                     = null;

    @XmlElementWrapper(name = "Mappings")
    @XmlElement(name = "Mapping")
    private List<MappingPojo>                           mappings                  = null;

    private Map<String, List<GenericParamPojo>>         genericParameters         = new HashMap<String, List<GenericParamPojo>>();

    private Map<String, List<String>>                   logCategories             = new HashMap<String, List<String>>();

    /**
     * Creates a new, empty <code>EngineConfiguration</code>.
     */
    public EngineConfiguration() {

        timestamp = new Date().getTime();
        staticBeanContainer = new StaticBeanContainer();
        initLoggerCategories();
    }

    /**
     * Creates an <code>EngineConfiguration</code> that is an exact copy of the
     * given existing <code>EngineConfiguration</code>.
     * @param config The original configuration to create a copy of.
     */
    public EngineConfiguration( EngineConfiguration config ) {

        this();
        
        try {
            CloneContainer container = new CloneContainer( config );
            container.copy( this );
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Determines if this configuration is changed.
     * @return <code>true</code> if this configuration has been changed, <code>false</code>
     * otherwise.
     */
    public boolean isChanged() {
        return !(updateList.isEmpty() && deleteList.isEmpty());
    }
    
    /**
     * Gets the list of updated objects.
     * @return The updated objects list, not <code>null</code>.
     */
    public List<NEXUSe2ePojo> getUpdateList() {
        return updateList;
    }
    
    public List<NEXUSe2ePojo> getImplicitUpdateList() {
        List<NEXUSe2ePojo> l = new ArrayList<NEXUSe2ePojo>();
        for (List<NEXUSe2ePojo> list : implicitUpdateList.values()) {
            l.addAll( list );
        }
        return l;
    }
    
    /**
     * Gets the list of deleted objects.
     * @return The deleted objects list, not <code>null</code>.
     */
    public List<NEXUSe2ePojo> getDeleteList() {
        return deleteList;
    }
    
    /**
     * Fills this <code>EngineConfiguration</code> with the base configuration provided by
     * the specified <code>BaseConfigurationProvider</code>.
     * @param baseConfigurationProvider The <code>BaseConfigurationProvider</code> that provides
     * the base configuration. It must be available ({@link BaseConfigurationProvider#isConfigurationAvailable()}
     * must return <code>true</code>).
     * @throws NexusException If the base configuration could not be created (e.g.,
     * {@link BaseConfigurationProvider#isConfigurationAvailable()} did not return <code>true</code>).
     */
    public void createBaseConfiguration( BaseConfigurationProvider baseConfigurationProvider ) throws NexusException {

        trps = new ArrayList<TRPPojo>();
        partners = new ArrayList<PartnerPojo>();
        choreographies = new ArrayList<ChoreographyPojo>();
        components = new ArrayList<ComponentPojo>();
        frontendPipelineTemplates = new ArrayList<PipelinePojo>();
        backendPipelineTemplates = new ArrayList<PipelinePojo>();
        services = new ArrayList<ServicePojo>();
        loggers = new ArrayList<LoggerPojo>();
        certificates = new ArrayList<CertificatePojo>();
        users = new ArrayList<UserPojo>();
        roles = new ArrayList<RolePojo>();
        mappings = new ArrayList<MappingPojo>();

        try {
            baseConfigurationProvider.createBaseConfiguration( components, choreographies, partners,
                    backendPipelineTemplates, frontendPipelineTemplates, services, certificates, trps, users, roles,
                    loggers, mappings );
        } catch (InstantiationException iex) {
            throw new NexusException( iex );
        }

        init();
    } // createBaseConfiguration
    
    /**
     * Initializes this <code>EngineConfiguration</code>.
     * This method shall be called after a configuration has been loaded from the DB. If
     * a base configuration is created using the {@link #createBaseConfiguration(BaseConfigurationProvider)}
     * method, it is not required to call this method.
     * @throws NexusException If the initialization failed.
     */
    public void init() throws NexusException {

        initStaticBeanContainer();

        ProtocolAdapter[] protocolAdapters = new ProtocolAdapter[getTrps().size()];
        int index = 0;
        for ( TRPPojo trpPojo : getTrps() ) {
            String protocolAdapterClass = trpPojo.getAdapterClassName();
            try {
                ProtocolAdapter protocolAdapter = (ProtocolAdapter) Class.forName( protocolAdapterClass ).newInstance();
                ProtocolSpecificKey protocolSpecificKey = new ProtocolSpecificKey( trpPojo.getProtocol(), trpPojo
                        .getVersion(), trpPojo.getTransport() );
                protocolAdapter.setKey( protocolSpecificKey );
                protocolAdapters[index++] = protocolAdapter;
            } catch ( Exception e ) {
                LOG.error( "Could not instantiate protocol adapter class: " + protocolAdapterClass + " - " + e );
                throw new NexusException( "Could not instantiate protocol adapter class: "
                        + protocolAdapterClass + " - " + e, e );
            }
        }
        FrontendInboundDispatcher frontendInboundDispatcher = (FrontendInboundDispatcher) staticBeanContainer
                .getManagableBeans().get( org.nexuse2e.Constants.FRONTEND_INBOUND_DISPATCHER );
        frontendInboundDispatcher.setProtocolAdapters( protocolAdapters );

        BackendOutboundDispatcher backendOutboundDispatcher = (BackendOutboundDispatcher) staticBeanContainer
                .getManagableBeans().get( org.nexuse2e.Constants.BACKEND_OUTBOUND_DISPATCHER );
        backendOutboundDispatcher.setProtocolAdapters( protocolAdapters );

        initializeLogAppenders();
        
        createConfiguration();
    }

    private void initLoggerCategories() {

        String categoryName = "core";
        List<String> categoryList = new ArrayList<String>();
        categoryList.add( "org.nexuse2e.Engine" );
        categoryList.add( "org.nexuse2e.EngineController" );
        categoryList.add( "org.nexuse2e.EngineMonitor" );
        categoryList.add( "org.nexuse2e.DefaultEngineControllerStub" );
        categoryList.add( "org.nexuse2e.configuration" );
        categoryList.add( "org.nexuse2e.controller" );
        categoryList.add( "org.nexuse2e.service" );
        logCategories.put( categoryName, categoryList );

        categoryName = "database";
        categoryList = new ArrayList<String>();
        categoryList.add( "org.nexuse2e.dao" );
        categoryList.add( "org.nexuse2e.pojo" );
        logCategories.put( categoryName, categoryList );

        categoryName = "backend";
        categoryList = new ArrayList<String>();
        categoryList.add( "org.nexuse2e.backend" );
        logCategories.put( categoryName, categoryList );

        categoryName = "frontend";
        categoryList = new ArrayList<String>();
        categoryList.add( "org.nexuse2e.messaging" );
        categoryList.add( "org.nexuse2e.transport" );
        logCategories.put( categoryName, categoryList );

        categoryName = "ui";
        categoryList = new ArrayList<String>();
        categoryList.add( "org.nexuse2e.ui" );
        logCategories.put( categoryName, categoryList );

    }

    private void initializeLogAppenders() throws NexusException {

        LOG.trace( "Initializing Appenders" );
        if ( loggers != null ) {
            if ( components != null ) {
                for ( LoggerPojo logger : loggers ) {
                    LOG.trace( "initializing Logger: " + logger.getName() );
                    if ( logger.getComponent() == null ) {
                        throw new InstantiationError( "No ComponentReference found for logger: " + logger.getName() );
                    }
                    String classname = logger.getComponent().getClassName();
                    if ( StringUtils.isEmpty( classname ) ) {
                        throw new InstantiationError( "No Classname found for component("
                                + logger.getComponent().getNxComponentId() + "): " + logger.getComponent().getName() );
                    }
                    Object obj = null;
                    try {
                        obj = Class.forName( classname ).newInstance();
                    } catch ( Exception e ) {
                        throw new NexusException( "Error while creating instance for logger: " + logger.getName()
                                + " - " + e.getMessage(), e );
                    }
                    if ( !( obj instanceof org.nexuse2e.logging.LogAppender ) ) {
                        throw new InstantiationError( "class: " + classname
                                + " is not instance of org.nexuse2e.logging.Logger" );
                    }
                    org.nexuse2e.logging.LogAppender logAppender = (org.nexuse2e.logging.LogAppender) obj;
                    ConfigurationUtil.configureLogger( logAppender, logger.getLoggerParams() );

                    logAppender.setLogThreshold( logger.getThreshold() );

                    StringTokenizer st = new StringTokenizer( logger.getFilter(), "," );
                    LOG.debug( "filter: " + logger.getFilter() );
                    while ( st.hasMoreElements() ) {
                        String token = st.nextToken().trim();
                        if ( token.startsWith( "group_" ) ) {
                            List<String> packageNames = logCategories.get( token.substring( 6 ).trim() );
                            if ( packageNames != null ) {
                                for ( String packageName : packageNames ) {
                                    if ( packageName != null ) {
                                        Logger targetlogger = Logger.getLogger( packageName );
                                        logAppender.registerLogger( targetlogger );
                                        targetlogger.addAppender( logAppender );
                                    }
                                }
                            }
                        } else {
                            if ( StringUtils.isEmpty( token ) ) {
                                continue;
                            }
                            LOG.debug( "adding logger: " + token );
                            Logger targetlogger = Logger.getLogger( token );
                            logAppender.registerLogger( targetlogger );
                            if ( targetlogger.getLevel() == null
                                    || targetlogger.getLevel().toInt() > logger.getThreshold() ) {
                                targetlogger.setLevel( Level.toLevel( logger.getThreshold() ) );
                            }
                            targetlogger.addAppender( logAppender );
                        }
                    }

                    staticBeanContainer.getManagableBeans().put( logger.getName(), logAppender );
                    try {
                        logAppender.initialize( this );
                        LOG.debug( "activating logger: " + logAppender.getName() );
                        logAppender.activate();
                    } catch ( Exception ex ) {
                        throw new NexusException( ex );
                    }
                }
            }
        }
    }

    /**
     * @throws InstantiationException 
     * 
     */
    public void initStaticBeanContainer() {

        /*
        // HashMap<String, Manageable> beanContainer = new LinkedHashMap<String, Manageable>();
        HashMap<String, Manageable> beanContainer = new HashMap<String, Manageable>( 100 );
        staticBeanContainer.setManagableBeans( beanContainer );
        */
        staticBeanContainer.getManagableBeans().clear();

        staticBeanContainer.getManagableBeans().put( org.nexuse2e.Constants.FRONTEND_INBOUND_DISPATCHER,
                new FrontendInboundDispatcher() );
        staticBeanContainer.getManagableBeans().put( org.nexuse2e.Constants.FRONTEND_OUTBOUND_DISPATCHER,
                new FrontendOutboundDispatcher() );
        staticBeanContainer.getManagableBeans().put( org.nexuse2e.Constants.BACKEND_INBOUND_DISPATCHER,
                new BackendInboundDispatcher() );
        staticBeanContainer.getManagableBeans().put( org.nexuse2e.Constants.BACKEND_OUTBOUND_DISPATCHER,
                new BackendOutboundDispatcher() );
        staticBeanContainer.getManagableBeans().put( org.nexuse2e.Constants.BACKEND_PIPELINE_DISPATCHER,
                new BackendPipelineDispatcher() );
        staticBeanContainer.getManagableBeans().put( org.nexuse2e.Constants.FRONTEND_INBOUND_RESPONSE_ENDPOINT,
                new FrontendInboundResponseEndpoint() );
        staticBeanContainer.getManagableBeans().put( org.nexuse2e.Constants.FRONTEND_OUTBOUND_RESPONSE_ENDPOINT,
                new FrontendOutboundResponseEndpoint() );
    }

    /**
     * 
     */
    public void createConfiguration() throws NexusException {

        int pos = 0;
        Pipelet[] pipelets = null;
        BackendPipeline backendPipeline = null;
        Map<String, Service> mappings = new HashMap<String, Service>();
        UpdateableUrlHandlerMapping updateableUrlHandlerMapping = null;

        backendInboundPipelines = new HashMap<ActionSpecificKey, BackendPipeline>();
        backendOutboundPipelines = new HashMap<ActionSpecificKey, BackendPipeline>();
        frontendInboundPipelines = new HashMap<TRPPojo, FrontendPipeline>();
        frontendOutboundPipelines = new HashMap<TRPPojo, FrontendPipeline>();
        frontendActionSerializers = new HashMap<String, FrontendActionSerializer>();
        backendActionSerializers = new HashMap<String, BackendActionSerializer>();

        Iterator<ChoreographyPojo> choreographiesI = getChoreographies().iterator();
        while ( choreographiesI.hasNext() ) {
            ChoreographyPojo choreography = (ChoreographyPojo) choreographiesI.next();

            // FrontendActionSerializer
            FrontendActionSerializer frontendActionSerializer = new FrontendActionSerializer( choreography.getName() );
            staticBeanContainer.getManagableBeans().put(
                    frontendActionSerializer.getChoreographyId()
                            + org.nexuse2e.Constants.POSTFIX_FRONTEND_ACTION_SERIALIZER, frontendActionSerializer );
            getFrontendActionSerializers().put( frontendActionSerializer.getChoreographyId(), frontendActionSerializer );

            // BackendActionSerializer
            BackendActionSerializer backendActionSerializer = new BackendActionSerializer( choreography.getName() );
            staticBeanContainer.getManagableBeans().put(
                    backendActionSerializer.getChoreographyId()
                            + org.nexuse2e.Constants.POSTFIX_BACKEND_ACTION_SERIALIZER, backendActionSerializer );
            getBackendActionSerializers().put( backendActionSerializer.getChoreographyId(), backendActionSerializer );

            // Backend pipelines
            Set<ActionPojo> actions = choreography.getActions();
            if ( actions != null ) {
                Iterator<ActionPojo> actionsI = actions.iterator();
                while ( actionsI.hasNext() ) {
                    ActionPojo action = actionsI.next();

                    ActionSpecificKey actionSpecificKey = new ActionSpecificKey( action.getName(), action
                            .getChoreography().getName() );
                    backendPipeline = new BackendPipeline();
                    backendPipeline.setKey( actionSpecificKey );

                    //TODO define behavior
                    PipelinePojo outboundPipelinePojo = action.getOutboundPipeline();
                    PipelinePojo inboundPipelinePojo = action.getInboundPipeline();

                    if ( inboundPipelinePojo.getPipelets().size() == 0 ) {
                        LOG.fatal( "No endpoint for inbound pipeline found for action: " + action.getName() );
                    }
                    if ( outboundPipelinePojo.getPipelets().size() == 0 ) {
                        LOG.warn( "No pipelets found for outbound pipeline for action: " + action.getName() );
                    }

                    pos = 0;
                    Collection<PipeletPojo> forwardPipelets = inboundPipelinePojo.getForwardPipelets();
                    int pipeletCount = forwardPipelets.size();
                    pipelets = new Pipelet[pipeletCount > 0 ? pipeletCount - 1 : 0];

                    for ( PipeletPojo pipeletPojo : forwardPipelets ) {
                        Pipelet pipelet;
                        try {
                            pipelet = getPipeletInstanceFromPojo( pipeletPojo );
                        } catch ( NexusException e ) {
                            LOG.error( "Error while creating pipelet instance: " + e.getMessage() );
                            break;
                        }
                        if ( pos == 0 ) {
                            backendPipeline.setPipelineEndpoint( pipelet );
                        } else {
                            pipelets[pos - 1] = pipelet;
                        }
                        pos++;
                    }

                    backendPipeline.setForwardPipelets( pipelets );
                    getBackendInboundPipelines().put( backendPipeline.getKey(), backendPipeline );

                    // staticBeanContainer.getManagableBeans().put( inboundPipelinePojo.getName() + Constants.POSTFIX_BACKEND_PIPELINE, backendPipeline );
                    staticBeanContainer.getManagableBeans().put(
                            inboundPipelinePojo.getName() + "-" + backendPipeline.getKey() + "-"
                                    + Constants.POSTFIX_BACKEND_PIPELINE, backendPipeline );

                    backendPipeline = new BackendPipeline();
                    backendPipeline.setKey( actionSpecificKey );
                    Collection<PipeletPojo> forwardPipeletList = outboundPipelinePojo.getForwardPipelets();
                    pos = 0;
                    pipelets = new Pipelet[forwardPipeletList.size()];
                    for ( PipeletPojo pipeletPojo : forwardPipeletList ) {
                        Pipelet pipelet = getPipeletInstanceFromPojo( pipeletPojo );
                        pipelets[pos++] = pipelet;
                    }
                    backendPipeline.setForwardPipelets( pipelets );
                    backendPipeline.setPipelineEndpoint( getStaticBeanContainer().getBackendOutboundDispatcher() );
                    LOG.trace( "PipelineKey: " + backendPipeline.getKey() + " - " + backendPipeline );
                    getBackendOutboundPipelines().put( backendPipeline.getKey(), backendPipeline );
                    staticBeanContainer.getManagableBeans().put(
                            outboundPipelinePojo.getName() + "-" + backendPipeline.getKey() + "-"
                                    + Constants.POSTFIX_BACKEND_PIPELINE, backendPipeline );
                }

            }
        }

        // Frontend pipelines
        List<PipelinePojo> frontendPipelines = getFrontendPipelineTemplates();
        for ( PipelinePojo pipelinePojo : frontendPipelines ) {
            FrontendPipeline frontendPipeline = new FrontendPipeline();
            TRPPojo trpPojo = pipelinePojo.getTrp();
            frontendPipeline.setKey( new ProtocolSpecificKey( trpPojo.getProtocol(), trpPojo.getVersion(), trpPojo
                    .getTransport() ) );
            frontendPipeline.setReturnPipelets( new Pipelet[0] );
            try {
                staticBeanContainer.getManagableBeans().put(
                        pipelinePojo.getName() + "-" + frontendPipeline.getKey() + "-"
                                + Constants.POSTFIX_FRONTEND_PIPELINE, frontendPipeline );
                if ( pipelinePojo.isOutbound() ) {
                    getFrontendOutboundPipelines().put( pipelinePojo.getTrp(), frontendPipeline );

                    Collection<PipeletPojo> forwardPipeletList = pipelinePojo.getForwardPipelets();
                    Pipelet[] forwardPipelets = null;
                    if ( forwardPipeletList.size() > 0 ) {
                        forwardPipelets = new Pipelet[forwardPipeletList.size() - 1];
                    } else {
                        forwardPipelets = new Pipelet[0];
                    }

                    int i = 0;
                    for ( PipeletPojo pipeletPojo : forwardPipeletList ) {
                        Pipelet pipelet = getPipeletInstanceFromPojo( pipeletPojo );
                        if ( i == forwardPipeletList.size() - 1 ) {
                            // TransportSender
                            frontendPipeline.setPipelineEndpoint( pipelet );
                            ConfigurationUtil.configurePipelet( pipelet, pipeletPojo.getPipeletParams() );
                        } else {
                            forwardPipelets[i++] = pipelet;
                        }
                    }
                    frontendPipeline.setForwardPipelets( forwardPipelets );
                    Collection<PipeletPojo> returnPipeletList = pipelinePojo.getReturnPipelets();
                    Pipelet[] returnPipelets = new Pipelet[returnPipeletList.size()];
                    i = 0;
                    for ( PipeletPojo pipeletPojo : returnPipeletList ) {
                        Pipelet pipelet = getPipeletInstanceFromPojo( pipeletPojo );
                        returnPipelets[i++] = pipelet;
                    }
                    frontendPipeline.setReturnPipelets( returnPipelets );
                    frontendPipeline.setReturnPipelineEndpoint( getStaticBeanContainer().getFrontendOutboundResponseEndpoint() );
                } else {
                    getFrontendInboundPipelines().put( pipelinePojo.getTrp(), frontendPipeline );
                    LOG.trace( "Frontend inbound pipeline: " + pipelinePojo.getName() );
                    Pipelet[] forwardPipelets = null;
                    Collection<PipeletPojo> forwardPipeletList = pipelinePojo.getForwardPipelets();
                    if ( forwardPipeletList.size() > 0 ) {
                        forwardPipelets = new Pipelet[forwardPipeletList.size() - 1];
                    } else {
                        forwardPipelets = new Pipelet[0];
                    }
                    int i = -1;

                    // Special treatment for first entry: it's a TransportReceiver
                    for ( PipeletPojo pipeletPojo : forwardPipeletList ) {
                        LOG.trace( "Pipelet: " + pipeletPojo.getName() + " - " + pipeletPojo.getPosition() + " - "
                                + pipeletPojo.getClass() );
                        Pipelet pipelet = getPipeletInstanceFromPojo( pipeletPojo );
                        
                        if ( i >= 0 ) {
                            forwardPipelets[i] = pipelet;
                        } else {
                            String urlAppendix = ""+pipeletPojo.getNxPipeletId();
                            if ( urlAppendix == null ) {
                                urlAppendix = pipeletPojo.getName();
                            }
                            
                            TransportReceiver transportReceiver = (TransportReceiver) Engine.getInstance().getEngineController().getTransportReceiver( 
                                    urlAppendix,pipeletPojo.getComponent().getClassName());
                            
                            
                            transportReceiver.setFrontendPipeline( frontendPipeline );
                            transportReceiver.setKey( frontendPipeline.getKey() );
                            ConfigurationUtil.configurePipelet( transportReceiver, pipeletPojo.getPipeletParams() );
                            // String beanKey = "TransportReceiver" + frontendPipeline.getKey().toString();
                            String beanKey = "TransportReceiver_" + pipelinePojo.getName() + "_"
                                    + frontendPipeline.getKey().toString();
                            if ( !staticBeanContainer.getManagableBeans().containsKey( beanKey ) ) {
                                LOG.trace( "Registering managable bean: " + beanKey + "(" + pipeletPojo.getName() + ")" );
                                staticBeanContainer.getManagableBeans().put( beanKey, transportReceiver );
                            } else {
                                LOG.warn( "Managable bean already registered, using first one: " + beanKey + "(" + pipeletPojo.getName() + ")" );
                            }
                        }

                        i++;
                    }
                    frontendPipeline.setForwardPipelets( forwardPipelets );
                    frontendPipeline.setPipelineEndpoint( getStaticBeanContainer().getFrontendInboundDispatcher() );
                    Collection<PipeletPojo> returnPipeletList = pipelinePojo.getReturnPipelets();
                    Pipelet[] returnPipelets = new Pipelet[returnPipeletList.size()];
                    i = 0;
                    for ( PipeletPojo pipeletPojo : returnPipeletList ) {
                        Pipelet pipelet = getPipeletInstanceFromPojo( pipeletPojo );
                        returnPipelets[i++] = pipelet;
                    }
                    frontendPipeline.setReturnPipelets( returnPipelets );
                    frontendPipeline.setReturnPipelineEndpoint( getStaticBeanContainer().getFrontendInboundResponseEndpoint() );
                }

            } catch (Exception e) {
                e.printStackTrace();
                throw new NexusException( e );
            } 

        }
        FrontendPipeline[] frontendOutboundPipelinesArray = new FrontendPipeline[getFrontendOutboundPipelines().size()];
        int i = 0;
        for ( FrontendPipeline pipeline : getFrontendOutboundPipelines().values() ) {
            frontendOutboundPipelinesArray[i++] = pipeline;
        }
        staticBeanContainer.getFrontendOutboundDispatcher().setFrontendOutboundPipelines(
                frontendOutboundPipelinesArray );

        // initialize services
        if ( getServices() != null ) {
            for ( ServicePojo servicePojo : getServices() ) {
                try {
                    Service service = getServiceInstanceFromPojo( servicePojo );
                    
                    // Register Controller for inbound HTTP messages
                    if ( service instanceof AbstractControllerService ) {
                        String urlAppendix = service.getParameter( "logical_name" );
                        if ( urlAppendix == null ) {
                            urlAppendix = servicePojo.getName();
                        }
                        Service wrappedService = Engine.getInstance().getEngineController()
                                .getServiceWrapper( service );
                        LOG.trace( "Registering controller: " + urlAppendix + " - " + service );
                        
                        mappings.put( urlAppendix, service );
                        
                        service = wrappedService;
                    }
                    
                    staticBeanContainer.getManagableBeans().put( servicePojo.getName(), service );
                    
                    
                    if ( service instanceof ApplicationObjectSupport ) {
                        ( (ApplicationObjectSupport) service ).setApplicationContext( Engine.getInstance()
                                .getApplicationContext() );
                    }
                } catch ( NexusException e ) {
                    LOG.error( "Error while creating service " + servicePojo.getName() + ": " + e.getMessage() );
                }
            }
        }

        // Register URLs
        if ( Engine.getInstance().getBeanFactory().containsBean( org.nexuse2e.Constants.TRANSPORT_DISPATCHER_MAPPING ) ) {
            updateableUrlHandlerMapping = (UpdateableUrlHandlerMapping) Engine.getInstance().getBeanFactory().getBean(
                    org.nexuse2e.Constants.TRANSPORT_DISPATCHER_MAPPING );
            try {
                updateableUrlHandlerMapping.setUrlMap( mappings );
                updateableUrlHandlerMapping.initApplicationContext();
            } catch ( IllegalStateException isex ) {
                LOG.error( isex );
            }
        }

    }

    /**
     * Return the BackendPipelineDispatcher instance. Convenience method for
     * <code>getSkeletonContainer().getBackendPipelineDispatcher()</code>
     * @return The BackendPipelineDispatcher instance
     */
    public BackendPipelineDispatcher getBackendPipelineDispatcher() {

        return staticBeanContainer.getBackendPipelineDispatcher();
    } // getBackendPipelineDispatcher

    /**
     * @param pojo
     * @return
     * @throws NexusException
     */
    private Pipelet getPipeletInstanceFromPojo( PipeletPojo pojo ) throws NexusException {

        try {

            Object newPipelet = Class.forName( pojo.getComponent().getClassName() ).newInstance();

            if ( newPipelet instanceof Pipelet ) {
                Pipelet pipelet = (Pipelet) newPipelet;
                ConfigurationUtil.configurePipelet( pipelet, pojo.getPipeletParams() );
                return pipelet;
            } else {
                throw new NexusException( "Component is not instance of Pipelet: " + newPipelet.getClass().getName() );
            }
        } catch ( InstantiationException e ) {
            throw new NexusException( e );
        } catch ( IllegalAccessException e ) {
            throw new NexusException( e );
        } catch ( ClassNotFoundException e ) {
            throw new NexusException( e );
        }
    }

    /**
     * Creates a temporary <code>Service</code> instance from the given <code>ServicePojo</code>.
     * @param pojo The service pojo.
     * @return A <code>Service</code> object.
     * @throws NexusException If the service instance could not be created.
     */
    public Service getServiceInstanceFromPojo( ServicePojo pojo ) throws NexusException {

        try {
            Class<?> serviceClass = Class.forName( pojo.getComponent().getClassName() );
            Object newService = serviceClass.newInstance();
            if ( newService instanceof Service ) {
                Service service = (Service) newService;
                service.setAutostart( pojo.isAutostart() );
                ConfigurationUtil.configureService( service, pojo.getServiceParams() );
                return service;
            } else {
                throw new NexusException( "Component is not instance of Service: " + newService.getClass().getName() );
            }
        } catch ( InstantiationException e ) {
            throw new NexusException( e );
        } catch ( IllegalAccessException e ) {
            throw new NexusException( e );
        } catch ( ClassNotFoundException e ) {
            throw new NexusException( e );
        }
    }

    /**
     * @return the caCertificates
     */
    public List<CertificatePojo> getCertificates() {

        if ( certificates == null ) {
            certificates = new ArrayList<CertificatePojo>();
        }
        return certificates;
    }

    /**
     * @param caCertificates the caCertificates to set
     */
    public void setCertificates( List<CertificatePojo> certificates ) {

        this.certificates = certificates;
    }

    /**
     * @return the choreographies
     */
    public List<ChoreographyPojo> getChoreographies() {

        return choreographies;
    }

    /**
     * @param choreographies the choreographies to set
     */
    public void setChoreographies( List<ChoreographyPojo> choreographies ) {

        this.choreographies = choreographies;
    }

    /**
     * @return the partners
     */
    public List<PartnerPojo> getPartners() {

        return partners;
    }

    /**
     * @param partners the partners to set
     */
    public void setPartners( List<PartnerPojo> partners ) {

        this.partners = partners;
    }

    /**
     * @return the staticBeanContainer
     */
    public StaticBeanContainer getStaticBeanContainer() {

        return staticBeanContainer;
    }

    /**
     * @param staticBeanContainer the staticBeanContainer to set
     */
    public void setStaticBeanContainer( StaticBeanContainer skeletonContainer ) {

        this.staticBeanContainer = skeletonContainer;
    }

    /**
     * @return the frontendActionSerializers
     */
    public Map<String, FrontendActionSerializer> getFrontendActionSerializers() {

        return frontendActionSerializers;
    }

    /**
     * @param frontendActionSerializers the frontendActionSerializers to set
     */
    public void setFrontendActionSerializers( HashMap<String, FrontendActionSerializer> frontendActionSerializers ) {

        this.frontendActionSerializers = frontendActionSerializers;
    }

    /**
     * @return the backendPipelines
     */
    public Map<ActionSpecificKey, BackendPipeline> getBackendInboundPipelines() {

        return backendInboundPipelines;
    }

    /**
     * @param backendPipelines the backendPipelines to set
     */
    public void setBackendInboundPipelines( HashMap<ActionSpecificKey, BackendPipeline> backendPipelines ) {

        this.backendInboundPipelines = backendPipelines;
    }

    /**
     * @return the backendOutboundPipelines
     */
    public Map<ActionSpecificKey, BackendPipeline> getBackendOutboundPipelines() {

        return backendOutboundPipelines;
    }

    /**
     * @param backendOutboundPipelines the backendOutboundPipelines to set
     */
    public void setBackendOutboundPipelines( HashMap<ActionSpecificKey, BackendPipeline> backendOutboundPipelines ) {

        this.backendOutboundPipelines = backendOutboundPipelines;
    }

    /**
     * @return the backendActionSerializers
     */
    public Map<String, BackendActionSerializer> getBackendActionSerializers() {

        return backendActionSerializers;
    }

    /**
     * @param backendActionSerializers the backendActionSerializers to set
     */
    public void setBackendActionSerializers( HashMap<String, BackendActionSerializer> backendActionSerializers ) {

        this.backendActionSerializers = backendActionSerializers;
    }

    /**
     * @return the timestamp
     */
    public long getTimestamp() {

        return timestamp;
    }

    /**
     * @return the trps
     */
    public List<TRPPojo> getTrps() {

        return trps;
    }

    /**
     * @param trps the trps to set
     */
    public void setTrps( List<TRPPojo> trps ) {

        this.trps = trps;
    }

    /**
     * @return
     */
    public List<ComponentPojo> getComponents() {

        return components;
    }

    /**
     * @param components the components to set
     */
    public void setComponents( List<ComponentPojo> components ) {

        this.components = components;
    }

    /**
     * @return
     */
    public List<PipelinePojo> getBackendPipelineTemplates() {

        return backendPipelineTemplates;
    }

    /**
     * @param backendPipelineTemplates the backendPipelineTemplates to set
     */
    public void setBackendPipelineTemplates( List<PipelinePojo> backendPipelineTemplates ) {

        this.backendPipelineTemplates = backendPipelineTemplates;
    }

    /**
     * @return the loggers
     */
    public List<LoggerPojo> getLoggers() {

        return loggers;
    }

    /**
     * @param loggers the loggers to set
     */
    public void setLoggers( List<LoggerPojo> loggers ) {

        this.loggers = loggers;
    }

    /**
     * @return the services
     */
    public List<ServicePojo> getServices() {

        return services;
    }

    /**
     * @param services the services to set
     */
    public void setServices( List<ServicePojo> services ) {

        this.services = services;
    }

    /**
     * @return the users
     */
    public List<UserPojo> getUsers() {

        return users;
    }

    /**
     * @param users the users to set
     */
    public void setUsers( List<UserPojo> users ) {

        this.users = users;
    }

    /**
     * @return the roles
     */
    public List<RolePojo> getRoles() {

        return roles;
    }

    /**
     * @param roles the roles to set
     */
    public void setRoles( List<RolePojo> roles ) {

        this.roles = roles;
    }

    /**
     * @return
     */
    public List<PipelinePojo> getFrontendPipelineTemplates() {

        return frontendPipelineTemplates;
    }

    /**
     * @param frontendPipelineTemplates
     */
    public void setFrontendPipelineTemplates( List<PipelinePojo> frontendPipelineTemplates ) {

        this.frontendPipelineTemplates = frontendPipelineTemplates;
    }

    /**
     * @return
     */
    public Map<TRPPojo, FrontendPipeline> getFrontendInboundPipelines() {

        return frontendInboundPipelines;
    }

    /**
     * @param frontendInboundPipelines
     */
    public void setFrontendInboundPipelines( HashMap<TRPPojo, FrontendPipeline> frontendInboundPipelines ) {

        this.frontendInboundPipelines = frontendInboundPipelines;
    }

    /**
     * @return
     */
    public Map<TRPPojo, FrontendPipeline> getFrontendOutboundPipelines() {

        return frontendOutboundPipelines;
    }

    /**
     * @param frontendOutboundPipelines
     */
    public void setFrontendOutboundPipelines( HashMap<TRPPojo, FrontendPipeline> frontendOutboundPipelines ) {

        this.frontendOutboundPipelines = frontendOutboundPipelines;
    }

    /**
     * @return the logCategories
     */
    public Map<String, List<String>> getLogCategories() {

        return logCategories;
    }

    /**
     * @param logCategories the logCategories to set
     */
    public void setLogCategories( Map<String, List<String>> logCategories ) {

        this.logCategories = logCategories;
    }

    /**
     * @return the genericParameters
     */
    public Map<String, List<GenericParamPojo>> getGenericParameters() {

        return genericParameters;
    }

    /**
     * @param genericParameters the genericParameters to set
     */
    public void setGenericParameters( Map<String, List<GenericParamPojo>> genericParameters ) {

        this.genericParameters = genericParameters;
    }

    /**
     * @return
     */
    public List<MappingPojo> getMappings() {

        return mappings;
    }

    /**
     * @param mappings
     */
    public void setMappings( List<MappingPojo> mappings ) {

        this.mappings = mappings;
    }


    //asdf
    
    /* (non-Javadoc)
     * @see org.nexuse2e.configuration.ConfigurationAccessService#getPipelinePojoByNxPipelineId(int)
     */
    public PipelinePojo getPipelinePojoByNxPipelineId( int nxPipelineId ) {
    
        PipelinePojo pipeline = null;
    
        List<PipelinePojo> pipelines = getBackendPipelinePojos( Constants.PIPELINE_TYPE_ALL, null );
        Iterator<PipelinePojo> pipelineI = pipelines.iterator();
        while ( pipelineI.hasNext() ) {
            pipeline = pipelineI.next();
            if ( pipeline.getNxPipelineId() == nxPipelineId ) {
                return pipeline;
            }
        }
    
        pipelines = getFrontendPipelinePojos( Constants.PIPELINE_TYPE_ALL, null );
        pipelineI = pipelines.iterator();
        while ( pipelineI.hasNext() ) {
            pipeline = pipelineI.next();
            if ( pipeline.getNxPipelineId() == nxPipelineId ) {
                return pipeline;
            }
        }
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.configuration.ConfigurationAccessService#getPipelineByName(java.lang.String)
     */
    public PipelinePojo getPipelineByName( String name ) {
    
        PipelinePojo pipeline = null;
    
        List<PipelinePojo> pipelines = getBackendPipelinePojos( Constants.PIPELINE_TYPE_ALL, null );
        Iterator<PipelinePojo> pipelineI = pipelines.iterator();
        while ( pipelineI.hasNext() ) {
            pipeline = pipelineI.next();
            if ( pipeline.getName().equals( name ) ) {
                return pipeline;
            }
        }
        pipelines = getFrontendPipelinePojos( Constants.PIPELINE_TYPE_ALL, null );
        pipelineI = pipelines.iterator();
        while ( pipelineI.hasNext() ) {
            pipeline = pipelineI.next();
            if ( pipeline.getName().equals( name ) ) {
                return pipeline;
            }
        }
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.configuration.ConfigurationAccessService#getServicePojoByNxServiceId(int)
     */
    public ServicePojo getServicePojoByNxServiceId( int nxServiceId ) {
    
        for ( ServicePojo service : getServices() ) {
            if ( service.getNxServiceId() == nxServiceId ) {
                return service;
            }
        }
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.configuration.ConfigurationAccessService#getServicePojoName(java.lang.String)
     */
    public ServicePojo getServicePojoName( String name ) {
    
        for ( ServicePojo service : getServices() ) {
            if ( service.getName().equals( name ) ) {
                return service;
            }
        }
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.configuration.ConfigurationAccessService#getFrontendPipelinePojos(int, org.nexuse2e.configuration.GenericComparator)
     */
    public List<PipelinePojo> getFrontendPipelinePojos( int type, Comparator<PipelinePojo> comparator ) {
    
        List<PipelinePojo> filteredList = null;
        if ( type != Constants.PIPELINE_TYPE_ALL ) {
            filteredList = new ArrayList<PipelinePojo>();
            Iterator<PipelinePojo> i = getFrontendPipelineTemplates().iterator();
            while ( i.hasNext() ) {
                PipelinePojo pipeline = i.next();
                if ( type == Constants.PIPELINE_TYPE_INBOUND && !pipeline.isOutbound() ) {
                    filteredList.add( pipeline );
                } else if ( type == Constants.PIPELINE_TYPE_OUTBOUND && pipeline.isOutbound() ) {
                    filteredList.add( pipeline );
                }
            }
        } else {
            filteredList = getFrontendPipelineTemplates();
        }
        if ( comparator != null ) {
            Collections.sort( filteredList, comparator );
        }
        return filteredList;
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.configuration.ConfigurationAccessService#getBackendPipelinePojos(int, org.nexuse2e.configuration.GenericComparator)
     */
    public List<PipelinePojo> getBackendPipelinePojos( int type, Comparator<PipelinePojo> comparator ) {
    
        List<PipelinePojo> filteredList = null;
        if ( type != Constants.PIPELINE_TYPE_ALL ) {
            filteredList = new ArrayList<PipelinePojo>();
            Iterator<PipelinePojo> i = getBackendPipelineTemplates().iterator();
            while ( i.hasNext() ) {
                PipelinePojo pipeline = i.next();
                if ( type == Constants.PIPELINE_TYPE_INBOUND && !pipeline.isOutbound() ) {
                    filteredList.add( pipeline );
                } else if ( type == Constants.PIPELINE_TYPE_OUTBOUND && pipeline.isOutbound() ) {
                    filteredList.add( pipeline );
                }
            }
        } else {
            filteredList = new ArrayList<PipelinePojo>( getBackendPipelineTemplates() );
        }
        if ( comparator != null ) {
            Collections.sort( filteredList, comparator );
        }
        return filteredList;
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.configuration.ConfigurationAccessService#getTrpByNxTrpId(int)
     */
    public TRPPojo getTrpByNxTrpId( int nxTrpId ) {
    
        TRPPojo trp = null;
    
        List<TRPPojo> trps = getTrps();
        Iterator<TRPPojo> trpI = trps.iterator();
        while ( trpI.hasNext() ) {
            trp = trpI.next();
            if ( trp.getNxTRPId() == nxTrpId ) {
                return trp;
            }
        }
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.configuration.ConfigurationAccessService#getTrpByProtocolVersionAndTransport(java.lang.String, java.lang.String, java.lang.String)
     */
    public TRPPojo getTrpByProtocolVersionAndTransport( String protocol, String version, String transport ) {
    
        List<TRPPojo> trps = getTrps();
        for ( TRPPojo trp : trps ) {
            if ( StringUtils.equals( trp.getProtocol(), protocol ) && StringUtils.equals( trp.getVersion(), version )
                    && StringUtils.equals( trp.getTransport(), transport ) ) {
                return trp;
            }
        }
    
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.configuration.ConfigurationAccessService#getPartnerByNxPartnerId(int)
     */
    public PartnerPojo getPartnerByNxPartnerId( int nxPartnerId ) throws NexusException {
    
        List<PartnerPojo> partners = getPartners();
        if ( partners != null && partners.size() > 0 ) {
            Iterator<PartnerPojo> i = partners.iterator();
            while ( i.hasNext() ) {
                PartnerPojo partner = i.next();
                if ( partner.getNxPartnerId() == nxPartnerId ) {
                    return partner;
                }
            }
        }
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.configuration.ConfigurationAccessService#getComponentByNxComponentId(int)
     */
    public ComponentPojo getComponentByNxComponentId( int nxComponentId ) throws NexusException {
    
        List<ComponentPojo> components = getComponents();
        if ( components != null && components.size() > 0 ) {
            Iterator<ComponentPojo> i = components.iterator();
            while ( i.hasNext() ) {
                ComponentPojo component = i.next();
                if ( component.getNxComponentId() == nxComponentId ) {
                    return component;
                }
            }
        }
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.configuration.ConfigurationAccessService#getPartnerByPartnerId(java.lang.String)
     */
    public PartnerPojo getPartnerByPartnerId( String partnerId ) throws NexusException {

        List<PartnerPojo> partners = getPartners();
        if ( partners != null && partners.size() > 0 ) {
            Iterator<PartnerPojo> i = partners.iterator();
            while ( i.hasNext() ) {
                PartnerPojo partner = i.next();
                if ( partner.getPartnerId().equals( partnerId ) ) {
                    return partner;
                }
            }
        }
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.configuration.ConfigurationAccessService#getCertificateFromPartnerByNxCertificateId(org.nexuse2e.pojo.PartnerPojo, int)
     */
    public CertificatePojo getCertificateFromPartnerByNxCertificateId( PartnerPojo partner, int nxCertificateId ) {
    
        if ( partner == null ) {
            return null;
        }
        CertificatePojo certificate = null;
        Set<CertificatePojo> certificates = partner.getCertificates();
        if ( certificates == null || certificates.size() == 0 ) {
            return null;
        }
        Iterator<CertificatePojo> certificateI = certificates.iterator();
        while ( certificateI.hasNext() ) {
            certificate = certificateI.next();
            if ( certificate.getNxCertificateId() == nxCertificateId ) {
                return certificate;
            }
        }
    
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.configuration.ConfigurationAccessService#getConnectionFromPartnerByNxConnectionId(org.nexuse2e.pojo.PartnerPojo, int)
     */
    public ConnectionPojo getConnectionFromPartnerByNxConnectionId( PartnerPojo partner, int nxConnectionId ) {
    
        if ( partner == null ) {
            return null;
        }
        ConnectionPojo connection = null;
        Set<ConnectionPojo> connections = partner.getConnections();
        if ( connections == null || connections.size() == 0 ) {
            return null;
        }
        Iterator<ConnectionPojo> connectionI = connections.iterator();
        while ( connectionI.hasNext() ) {
            connection = connectionI.next();
            if ( connection.getNxConnectionId() == nxConnectionId ) {
                return connection;
            }
        }
    
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.configuration.ConfigurationAccessService#getComponents(org.nexuse2e.configuration.Constants.ComponentType, java.util.Comparator)
     */
    @SuppressWarnings("unchecked")
    public List<ComponentPojo> getComponents( ComponentType type, Comparator comparator ) throws NexusException {
    
        List<ComponentPojo> filteredList = null;
        if ( type != ComponentType.ALL ) {
            filteredList = new ArrayList<ComponentPojo>();
            for ( ComponentPojo component : getComponents() ) {
                if ( type.getValue() == component.getType() ) {
                    filteredList.add( component );
                }
            }
        } else {
            filteredList = getComponents();
        }
        if ( comparator != null ) {
            Collections.sort( filteredList, comparator );
        }
        return filteredList;
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.configuration.ConfigurationAccessService#getPipelets(boolean)
     */
    @SuppressWarnings("unchecked")
    public List<ComponentPojo> getPipelets( boolean frontend ) throws NexusException {
    
        Pipelet pipelet = null;
        List<ComponentPojo> components = getComponents( ComponentType.PIPELET, Constants.COMPONENTCOMPARATOR );
        List<ComponentPojo> filteredList = new ArrayList<ComponentPojo>();
        for ( Iterator iter = components.iterator(); iter.hasNext(); ) {
            ComponentPojo componentPojo = (ComponentPojo) iter.next();
            try {
                Object tempObject = Class.forName( componentPojo.getClassName() ).newInstance();
                if ( tempObject instanceof Pipelet ) {
                    pipelet = (Pipelet) tempObject;
                    LOG.trace( "Pipelet " + componentPojo.getClassName() + " - is frontend: "
                            + pipelet.isFrontendPipelet() );
                    if ( pipelet.isFrontendPipelet() == frontend ) {
                        filteredList.add( componentPojo );
                    }
                }
            } catch ( InstantiationException e ) {
                LOG.error( "Problem instantiating class: " + componentPojo.getClassName() );
            } catch ( IllegalAccessException e ) {
                LOG.error( "Problem instantiating/accessing class: " + componentPojo.getClassName() );
            } catch ( ClassNotFoundException e ) {
                LOG.error( "Could not find class: " + componentPojo.getClassName() );
            }
        }
    
        return filteredList;
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.configuration.ConfigurationAccessService#getPartners(int, java.util.Comparator)
     */
    @SuppressWarnings("unchecked")
    public List<PartnerPojo> getPartners( int type, Comparator comparator ) throws NexusException {
    
        List<PartnerPojo> filteredList = null;
        if ( type != Constants.PARTNER_TYPE_ALL ) {
            filteredList = new ArrayList<PartnerPojo>();
            Iterator<PartnerPojo> i = getPartners().iterator();
            while ( i.hasNext() ) {
                PartnerPojo partner = i.next();
    
                if ( type == partner.getType() ) {
                    filteredList.add( partner );
                }
            }
        } else {
            filteredList = getPartners();
        }
        if ( comparator != null ) {
            Collections.sort( filteredList, comparator );
        }
        return filteredList;
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.configuration.ConfigurationAccessService#getCertificates(int, java.util.Comparator)
     */
    @SuppressWarnings("unchecked")
    public List<CertificatePojo> getCertificates( int type, Comparator comparator ) throws NexusException {
    
        List<CertificatePojo> filteredList = null;
        if ( type != 0 ) {
            filteredList = new ArrayList<CertificatePojo>();
            for ( CertificatePojo certificate : getCertificates() ) {
    
                if ( type == Constants.CERTIFICATE_TYPE_ALL || type == certificate.getType() ) {
                    filteredList.add( certificate );
                }
            }
        } else {
            filteredList = getCertificates();
        }
        if ( comparator != null ) {
            Collections.sort( filteredList, comparator );
        }
        return filteredList;
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.configuration.ConfigurationAccessService#getCertificateByName(int, java.lang.String)
     */
    public CertificatePojo getCertificateByName( int type, String name ) throws NexusException {
    
        List<CertificatePojo> certificates = getCertificates( type, null );
        for ( CertificatePojo certificate : certificates ) {
            if ( certificate.getName().equals( name ) ) {
                return certificate;
            }
        }
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.configuration.ConfigurationAccessService#getCertificateByNxCertificateId(int, int)
     */
    public CertificatePojo getCertificateByNxCertificateId( int type, int nxCertificateId ) throws NexusException {
    
        List<CertificatePojo> certificates = getCertificates( type, null );
        for ( CertificatePojo certificate : certificates ) {
            if ( certificate.getNxCertificateId() == nxCertificateId ) {
                return certificate;
            }
        }
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.configuration.ConfigurationAccessService#getFirstCertificateByType(int, boolean)
     */
    public CertificatePojo getFirstCertificateByType( int type, boolean isUnique ) throws NexusException {
    
        List<CertificatePojo> certificates = getCertificates( type, null );
        if ( certificates == null || certificates.size() == 0 ) {
            return null;
        }
        if ( isUnique && certificates.size() > 1 ) {
            throw new NexusException( "There is more than one certificate of type: " + type + " in database" );
        }
        return certificates.get( 0 );
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.configuration.ConfigurationAccessService#getChoreographyByChoreographyId(java.lang.String)
     */
    public ChoreographyPojo getChoreographyByChoreographyId( String choreographyId ) throws NexusException {
    
        List<ChoreographyPojo> choreographies = getChoreographies();
        if ( choreographies != null && choreographies.size() > 0 ) {
            Iterator<ChoreographyPojo> i = choreographies.iterator();
            while ( i.hasNext() ) {
                ChoreographyPojo choreography = i.next();
                if ( choreography.getName().equals( choreographyId ) ) {
                    return choreography;
                }
            }
        }
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.configuration.ConfigurationAccessService#getChoreographyByNxChoreographyId(int)
     */
    public ChoreographyPojo getChoreographyByNxChoreographyId( int nxChoreographyId ) throws NexusException {
    
        List<ChoreographyPojo> choreographies = getChoreographies();
        if ( choreographies != null && choreographies.size() > 0 ) {
            Iterator<ChoreographyPojo> i = choreographies.iterator();
            while ( i.hasNext() ) {
                ChoreographyPojo choreography = i.next();
                if ( choreography.getNxChoreographyId() == nxChoreographyId ) {
                    return choreography;
                }
            }
        }
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.configuration.ConfigurationAccessService#getParticipantFromChoreographyByNxPartnerId(org.nexuse2e.pojo.ChoreographyPojo, int)
     */
    public ParticipantPojo getParticipantFromChoreographyByNxPartnerId( ChoreographyPojo choreography, int nxPartnerId ) {
    
        Collection<ParticipantPojo> participants = choreography.getParticipants();
        ParticipantPojo participant = null;
        Iterator<ParticipantPojo> i = participants.iterator();
        while ( i.hasNext() ) {
            participant = i.next();
            if ( participant.getPartner().getNxPartnerId() == nxPartnerId ) {
                return participant;
            }
        }
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.configuration.ConfigurationAccessService#getActionFromChoreographyByNxActionId(org.nexuse2e.pojo.ChoreographyPojo, int)
     */
    public ActionPojo getActionFromChoreographyByNxActionId( ChoreographyPojo choreography, int nxActionId ) {
    
        Set<ActionPojo> actions = choreography.getActions();
        if ( actions != null && actions.size() > 0 ) {
            Iterator<ActionPojo> i = actions.iterator();
            while ( i.hasNext() ) {
                ActionPojo action = i.next();
                if ( action.getNxActionId() == nxActionId ) {
                    return action;
                }
            }
        }
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.configuration.ConfigurationAccessService#getActionFromChoreographyByActionId(org.nexuse2e.pojo.ChoreographyPojo, java.lang.String)
     */
    public ActionPojo getActionFromChoreographyByActionId( ChoreographyPojo choreography, String actionId ) {
    
        Set<ActionPojo> actions = choreography.getActions();
        if ( actions != null && actions.size() > 0 ) {
            Iterator<ActionPojo> i = actions.iterator();
            while ( i.hasNext() ) {
                ActionPojo action = i.next();
                if ( action.getName().equals( actionId ) ) {
                    return action;
                }
            }
        }
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.configuration.ConfigurationAccessService#getParticipantFromChoreographyByPartner(org.nexuse2e.pojo.ChoreographyPojo, org.nexuse2e.pojo.PartnerPojo)
     */
    public ParticipantPojo getParticipantFromChoreographyByPartner( ChoreographyPojo choreography, PartnerPojo partner ) {
    
        Collection<ParticipantPojo> participants = choreography.getParticipants();
        if ( participants != null && participants.size() > 0 ) {
            Iterator<ParticipantPojo> i = participants.iterator();
            while ( i.hasNext() ) {
                ParticipantPojo participant = i.next();
    
                if ( participant.getPartner().getNxPartnerId() == partner.getNxPartnerId() ) {
                    return participant;
                }
            }
        }
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.configuration.ConfigurationAccessService#getEngineConfig()
     */
    public EngineConfiguration getEngineConfig() {
    
        return this;
    }
    
    /**
     * Adds the given POJO to the update list.
     */
    protected void addToUpdateList( NEXUSe2ePojo pojo ) {
        int index = -1;
        if (pojo.getNxId() == 0) {
            pojo.setNxId( recentNxId-- );
        } else {
            for (int i = 0; i < updateList.size(); i++) {
                NEXUSe2ePojo p = updateList.get( i );
                if (p.getClass().equals( pojo.getClass() ) && p.getNxId() == pojo.getNxId()) {
                    index = i;
                }
            }
        }
        if (index >= 0) {
            updateList.remove( index );
        }
        updateList.add( pojo );
    }
    
    /**
     * Adds the given pojo to the delete list.
     * @param pojo The pojo to be deleted.
     */
    protected void addToDeleteList( NEXUSe2ePojo pojo ) {
        implicitUpdateList.remove( pojo );
        if (updateList.remove( pojo ) && pojo.getNxId() < 0) {
            // nothing to do because a newly created object was deleted
        } else {
            deleteList.add( pojo );
        }
    }
    
    /**
     * Adds the given pojo list to the list of implicitly updated pojos.
     * @param parent The parent pojo.
     * @param list The child pojos that will implicitly be updated.
     */
    protected <T extends NEXUSe2ePojo> void addToImplicitUpdateList( NEXUSe2ePojo parent, Collection<T> list ) {
        if (list != null) {
            // Sets have problems if the object was added with another hash value
            // avoid this by removing and re-adding after nxId has been set
            Collection<T> collection = list;
            if (list instanceof Set) {
                collection = new ArrayList<T>( list );
                list.clear();
            }
            
            for (T pojo : collection) {
                if (pojo.getNxId() == 0) {
                    pojo.setNxId( recentNxId-- );
                    List<NEXUSe2ePojo> l = implicitUpdateList.get( parent );
                    if (l == null) {
                        l = new ArrayList<NEXUSe2ePojo>();
                        implicitUpdateList.put( parent, l );
                    }
                    l.add( pojo );
                }
                if (collection != list) { // add to original set (if Set is used)
                    list.add( pojo );
                }
            }
        }
    }
    

    public void updatePartner( PartnerPojo partner ) throws NexusException {
    
        PartnerPojo oldPartner = getPartnerByNxPartnerId( partner.getNxPartnerId() );
        if ( oldPartner != null ) {
            getPartners( 0, null ).remove( oldPartner );
        }
        getPartners( 0, null ).add( partner );
        addToUpdateList( partner );
        addToImplicitUpdateList( partner, partner.getCertificates() );
        addToImplicitUpdateList( partner, partner.getConnections() );
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.configuration.ConfigurationAccessService#updateChoreography(org.nexuse2e.pojo.ChoreographyPojo)
     */
    public void updateChoreography( ChoreographyPojo choreography ) throws NexusException {
    
        ChoreographyPojo oldChoreography = getChoreographyByChoreographyId( choreography.getName() );
        if ( oldChoreography != null ) {
            getChoreographies().remove( oldChoreography );
        }
        getChoreographies().add( choreography );
        addToUpdateList( choreography );
        addToImplicitUpdateList( choreography, choreography.getActions() );
        addToImplicitUpdateList( choreography, choreography.getParticipants() );
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.configuration.ConfigurationAccessService#deleteChoreography(org.nexuse2e.pojo.ChoreographyPojo)
     */
    public void deleteChoreography( ChoreographyPojo choreography ) throws ReferencedChoreographyException, NexusException {
    
        ChoreographyPojo oldChoreography = getChoreographyByChoreographyId( choreography.getName() );
        List<ConversationPojo> conversations = Engine.getInstance().getTransactionService().getConversationsByChoreography(
                choreography );
        if (conversations != null && !conversations.isEmpty()) {
            throw new ReferencedChoreographyException( conversations );
        }
        if ( oldChoreography != null ) {
            getChoreographies().remove( oldChoreography );
        }
        addToDeleteList( choreography );
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.configuration.ConfigurationAccessService#deletePartner(org.nexuse2e.pojo.PartnerPojo)
     */
    public void deletePartner( PartnerPojo partner ) throws ReferencedPartnerException, NexusException {
    
        PartnerPojo oldPartner = getPartnerByNxPartnerId( partner.getNxPartnerId() );
        if ( oldPartner != null ) {
            // check references
            if (!oldPartner.getParticipants().isEmpty()) {
                throw new ReferencedPartnerException( oldPartner.getParticipants() );
            }
            
            getPartners( 0, null ).remove( oldPartner );
    
            getCertificates( Constants.CERTIFICATE_TYPE_ALL, null ).removeAll( partner.getCertificates() );
            
            addToDeleteList( oldPartner );
        }
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.configuration.ConfigurationAccessService#deleteConnection(org.nexuse2e.pojo.ConnectionPojo)
     */
    public void deleteConnection( ConnectionPojo connection ) throws ReferencedConnectionException, NexusException {
        
        if (connection.getPartcipants() != null && !connection.getPartcipants().isEmpty()) {
            throw new ReferencedConnectionException( connection.getPartcipants() );
        }
        Set<ConnectionPojo> connections = connection.getPartner().getConnections();
        connections = new HashSet<ConnectionPojo>( connections );
        if (connections.remove( connection )) {
            connection.getPartner().setConnections( connections );
            addToDeleteList( connection );
        }
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.configuration.ConfigurationAccessService#updateTrp(org.nexuse2e.pojo.TRPPojo)
     */
    public void updateTrp( TRPPojo trp ) throws NexusException {
    
        TRPPojo oldTrp = getTrpByNxTrpId( trp.getNxTRPId() );
        if ( oldTrp != null ) {
            getTrps().remove( oldTrp );
        }
        getTrps().add( trp );
        addToUpdateList( trp );
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.configuration.ConfigurationAccessService#deleteTrp(org.nexuse2e.pojo.TRPPojo)
     */
    public void deleteTrp( TRPPojo trp ) throws NexusException {
    
        if (getTrps().remove( trp )) {
            addToDeleteList( trp );
        }
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.configuration.ConfigurationAccessService#updateComponent(org.nexuse2e.pojo.ComponentPojo)
     */
    public void updateComponent( ComponentPojo component ) throws NexusException {
    
        ComponentPojo oldComponent = getComponentByNxComponentId( component.getNxComponentId() );
        if ( oldComponent != null ) {
            getComponents( ComponentType.ALL, null ).remove( oldComponent );
        }
        getComponents( ComponentType.ALL, null ).add( component );
        addToUpdateList( component );
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.configuration.ConfigurationAccessService#deleteComponent(org.nexuse2e.pojo.ComponentPojo)
     */
    public void deleteComponent( ComponentPojo component ) throws NexusException {
        ComponentPojo oldComponent = getComponentByNxComponentId( component.getNxComponentId() );
        if ( oldComponent != null ) {
            getComponents( ComponentType.ALL, null ).remove( oldComponent );
        }
        addToDeleteList( component );
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.configuration.ConfigurationAccessService#updatePipeline(org.nexuse2e.pojo.PipelinePojo)
     */
    public void updatePipeline( PipelinePojo pipeline ) throws NexusException {
    
        PipelinePojo oldPipeline = getPipelinePojoByNxPipelineId( pipeline.getNxPipelineId() );
        List<PipelinePojo> pt = (pipeline.isFrontend() ? getFrontendPipelineTemplates() : getBackendPipelineTemplates());
        if ( oldPipeline != null ) {
            pt.remove( oldPipeline );
        }
        pt.add( pipeline );
        addToUpdateList( pipeline );
        addToImplicitUpdateList( pipeline, pipeline.getPipelets() );
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.configuration.ConfigurationAccessService#deletePipeline(org.nexuse2e.pojo.PipelinePojo)
     */
    public void deletePipeline( PipelinePojo pipeline ) throws ReferencedPipelineException, NexusException {
    
        // check if backend pipeline is being referenced
        if (!pipeline.isFrontend()) {
            int id = pipeline.getNxPipelineId();
            List<ActionPojo> referrers = new ArrayList<ActionPojo>();
            for (ChoreographyPojo choreography : getChoreographies()) {
                for (ActionPojo action : choreography.getActions()) {
                    if (action != null &&
                            ((action.getInboundPipeline() != null && action.getInboundPipeline().getNxPipelineId() == id) ||
                            (action.getOutboundPipeline() != null && action.getOutboundPipeline().getNxPipelineId() == id))) {
                        referrers.add( action );
                    }
                }
            }
            if (!referrers.isEmpty()) {
                throw new ReferencedPipelineException( referrers );
            }
        }
        
        PipelinePojo oldPipeline = getPipelinePojoByNxPipelineId( pipeline.getNxPipelineId() );
        if ( oldPipeline != null ) {
            List<PipelinePojo> pt = (oldPipeline.isFrontend() ? getFrontendPipelineTemplates() : getBackendPipelineTemplates());
            pt.remove( oldPipeline );
            addToDeleteList( oldPipeline );
        }
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.configuration.ConfigurationAccessService#updateService(java.lang.String)
     */
    public void updateService( String name ) throws NexusException {
    
        Service service = getService( name );
        if ( service != null ) {
            for ( ServicePojo servicePojo : getServices() ) {
                if ( servicePojo.getName().equals( name ) ) {
                    servicePojo.setServiceParams( ConfigurationUtil.getConfiguration( service, servicePojo ) );
                    updateService( servicePojo );
                    break;
                }
            }
        }
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.configuration.ConfigurationAccessService#updateService(org.nexuse2e.pojo.ServicePojo)
     */
    public void updateService( ServicePojo servicePojo ) throws NexusException {
    
        if ( servicePojo.getName() == null || servicePojo.getName().trim().length() == 0 ) {
            throw new IllegalArgumentException( "Service name must not be empty" );
        }
    
        ServicePojo oldServicePojo = getServicePojoByNxServiceId( servicePojo.getNxServiceId() );
        if (oldServicePojo != null) {
            services.remove( oldServicePojo );
        }
        services.add( servicePojo );
        addToUpdateList( servicePojo );
        addToImplicitUpdateList( servicePojo, servicePojo.getServiceParams() );
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.configuration.ConfigurationAccessService#deleteService(org.nexuse2e.pojo.ServicePojo)
     */
    public void deleteService( ServicePojo servicePojo ) throws NexusException {
    
        List<ServicePojo> services = getServices();
        ServicePojo oldServicePojo = getServicePojoByNxServiceId( servicePojo.getNxServiceId() );
        if ( oldServicePojo != null ) {
            Service oldService = getService( oldServicePojo.getName() );
            if ( oldService != null ) {
                if ( oldService.getStatus() == BeanStatus.STARTED ) {
                    oldService.stop();
                }
                if ( oldService.getStatus() == BeanStatus.ACTIVATED ) {
                    oldService.deactivate();
                }
                getServiceInstances().remove( oldService );
            }
            Service service = getService( servicePojo.getName() );
            if ( service != null ) {
                if ( service.getStatus() == BeanStatus.STARTED ) {
                    service.stop();
                }
                if ( service.getStatus() == BeanStatus.ACTIVATED ) {
                    service.deactivate();
                }
                getStaticBeanContainer().getManagableBeans().remove( servicePojo.getName() );
            }
            services.remove( oldServicePojo );
            addToDeleteList( oldServicePojo );
        }
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.configuration.ConfigurationAccessService#updateLogger(org.nexuse2e.pojo.LoggerPojo)
     */
    public void updateLogger( LoggerPojo loggerPojo ) throws NexusException {
    
        if ( loggerPojo.getName() == null || loggerPojo.getName().trim().length() == 0 ) {
            throw new IllegalArgumentException( "Logger name must not be empty" );
        }
    
        try {
            List<LoggerPojo> loggers = getLoggers();
            LoggerPojo oldLoggerPojo = getLoggerByNxLoggerId( loggerPojo.getNxLoggerId() );
            org.nexuse2e.logging.LogAppender logger = null;
            if ( oldLoggerPojo != null ) {
                logger = getLogger( oldLoggerPojo.getName() );
                loggers.remove( oldLoggerPojo );
                // service has been renamed
                if ( !oldLoggerPojo.getName().equals( loggerPojo.getName() ) ) {
                    renameLogger( oldLoggerPojo.getName(), loggerPojo.getName() );
                }
                if ( loggerPojo.getLoggerParams() != null ) {
                    ConfigurationUtil.configureLogger( logger, loggerPojo.getLoggerParams() );
                }
            } else {
                logger = (org.nexuse2e.logging.LogAppender) Class.forName( loggerPojo.getComponent().getClassName() )
                        .newInstance();
                if ( loggerPojo.getLoggerParams() != null ) {
                    ConfigurationUtil.configureLogger( logger, loggerPojo.getLoggerParams() );
                }
                logger.initialize( this );
                logger.activate();
                getStaticBeanContainer().getManagableBeans().put( loggerPojo.getName(), logger );
            }
            loggers.add( loggerPojo );
            addToUpdateList( loggerPojo );
    
        } catch ( Exception e ) {
            if (e instanceof NexusException) {
                throw (NexusException) e;
            }
            throw new NexusException( e );
        }
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.configuration.ConfigurationAccessService#deleteLogger(org.nexuse2e.pojo.LoggerPojo)
     */
    public void deleteLogger( LoggerPojo logger ) throws NexusException {
    
        LoggerPojo oldLogger = getLoggerByNxLoggerId( logger.getNxLoggerId() );
        if ( oldLogger != null ) {
            getLoggers().remove( oldLogger );
            addToDeleteList( oldLogger );
        }
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.configuration.ConfigurationAccessService#getLoggerByNxLoggerId(int)
     */
    public LoggerPojo getLoggerByNxLoggerId( int nxLoggerId ) {
    
        for ( LoggerPojo logger : getLoggers() ) {
            if ( logger.getNxLoggerId() == nxLoggerId ) {
                return logger;
            }
        }
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.configuration.ConfigurationAccessService#getLogger(java.lang.String)
     */
    public org.nexuse2e.logging.LogAppender getLogger( String name ) {
    
        return getStaticBeanContainer().getLogger( name );
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.configuration.ConfigurationAccessService#renameLogger(java.lang.String, java.lang.String)
     */
    public org.nexuse2e.logging.LogAppender renameLogger( String oldName, String newName ) throws NexusException {
    
        return getStaticBeanContainer().renameLogger( oldName, newName );
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.configuration.ConfigurationAccessService#getService(java.lang.String)
     */
    public Service getService( String name ) {
    
        return getStaticBeanContainer().getService( name );
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.configuration.ConfigurationAccessService#renameService(java.lang.String, java.lang.String)
     */
    public Service renameService( String oldName, String newName ) throws NexusException {
    
        return getStaticBeanContainer().renameService( oldName, newName );
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.configuration.ConfigurationAccessService#getServiceInstances()
     */
    public List<Service> getServiceInstances() {
    
        return getStaticBeanContainer().getServices();
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.configuration.ConfigurationAccessService#deleteCertificate(org.nexuse2e.pojo.CertificatePojo)
     */
    public void deleteCertificate( CertificatePojo certificate )
    throws ReferencedCertificateException, NexusException {
    
        CertificatePojo oldCertificate = getCertificateByNxCertificateId(
                Constants.CERTIFICATE_TYPE_ALL, certificate.getNxCertificateId() );

        if (oldCertificate != null) {
            // check if certificate is referenced by any connection
            if (certificate.getPartner() != null) {
                List<ConnectionPojo> referringObjects = new ArrayList<ConnectionPojo>();
                for (ConnectionPojo connection : certificate.getPartner().getConnections()) {
                    CertificatePojo cert = connection.getCertificate();
                    if (cert != null && cert.getNxCertificateId() == certificate.getNxCertificateId()) {
                        referringObjects.add( connection );
                    }
                }
                if (!referringObjects.isEmpty()) {
                    throw new ReferencedCertificateException( referringObjects );
                }
                
                Set<CertificatePojo> certs = certificate.getPartner().getCertificates();
                if (certs != null) {
                    certs = new HashSet<CertificatePojo>( certs );
                    certs.remove( oldCertificate );
                    certificate.getPartner().setCertificates( certs );
                }
            }
        
            getCertificates( Constants.CERTIFICATE_TYPE_ALL, null ).remove( oldCertificate );
            addToDeleteList( oldCertificate );
        }
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.configuration.ConfigurationAccessService#deleteCertificates(java.util.Collection)
     */
    public void deleteCertificates( Collection<CertificatePojo> certificates )
    throws NexusException, ReferencedCertificateException {
        for (CertificatePojo certificate : certificates) {
            deleteCertificate( certificate );
        }
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.configuration.ConfigurationAccessService#updateCertificates(java.util.List)
     */
    public void updateCertificates( List<CertificatePojo> certs ) throws NexusException {
    
        for ( CertificatePojo certificate : certs ) {
            CertificatePojo oldCertificate = getCertificateByNxCertificateId( Constants.CERTIFICATE_TYPE_ALL,
                    certificate.getNxCertificateId() );
            if ( oldCertificate != null ) {
                getCertificates( Constants.CERTIFICATE_TYPE_ALL, null );
            }
    
            getCertificates( Constants.CERTIFICATE_TYPE_ALL, null ).add( certificate );
            for (CertificatePojo cert : certificates) {
                addToUpdateList( cert );
            }
        }
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.configuration.ConfigurationAccessService#updateCertificate(org.nexuse2e.pojo.CertificatePojo)
     */
    public void updateCertificate( CertificatePojo certificate ) throws NexusException {
    
        CertificatePojo oldCertificate = getCertificateByNxCertificateId(
                Constants.CERTIFICATE_TYPE_ALL, certificate.getNxCertificateId() );
        if ( oldCertificate != null ) {
            getCertificates( Constants.CERTIFICATE_TYPE_ALL, null ).remove( oldCertificate );
        }
    
        getCertificates( Constants.CERTIFICATE_TYPE_ALL, null ).add( certificate );
        addToUpdateList( certificate );
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.configuration.ConfigurationAccessService#getCacertsKeyStore()
     */
    public KeyStore getCacertsKeyStore() throws NexusException {
    
        try {
            CertificatePojo key = getFirstCertificateByType( Constants.CERTIFICATE_TYPE_CACERT_METADATA, true );
            String cacertspwd = "changeit";
            if ( key == null ) {
                key = new CertificatePojo();
                key.setName( "CaKeyStoreData" );
                key.setType( Constants.CERTIFICATE_TYPE_CACERT_METADATA );
                key.setBinaryData( new byte[0] );
                key.setPassword( EncryptionUtil.encryptString( cacertspwd ) );
                updateCertificate( key );
            } else {
                cacertspwd = EncryptionUtil.decryptString( key.getPassword() );
            }
            KeyStore keyStore = KeyStore.getInstance( "JKS" );
            keyStore.load( null, cacertspwd.toCharArray() );
            List<CertificatePojo> certs = getCertificates( Constants.CERTIFICATE_TYPE_CA, null );
            // log.debug( "getCACertificates - count: " + certs.size() );
            for ( CertificatePojo tempCert : certs ) {
                byte[] data = tempCert.getBinaryData();
                if ( ( data != null ) && ( data.length != 0 ) ) {
                    try {
                        X509Certificate x509Certificate = CertificateUtil.getX509Certificate( data );
                        // log.debug( "cert: " + x509Certificate.getSubjectDN() + " - " + tempCert.getCertificateId() );
                        keyStore.setCertificateEntry( tempCert.getName(), x509Certificate );
                    } catch ( Exception e ) {
                        LOG.error( "Error importing certificate " + tempCert.getName() + ": " + e.getMessage() );
                    }
                }
    
            }
            return keyStore;
        } catch ( Exception e ) {
            LOG.error( "Error initializing Certificate store.  Exception:  " + e.getMessage() );
            e.printStackTrace();
        }
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.configuration.ConfigurationAccessService#getUsers(java.util.Comparator)
     */
    public List<UserPojo> getUsers( Comparator<UserPojo> comparator ) {
    
        List<UserPojo> users = getUsers();
        if ( comparator != null ) {
            Collections.sort( users, comparator );
        }
        return users;
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.configuration.ConfigurationAccessService#getUserByLoginName(java.lang.String)
     */
    public UserPojo getUserByLoginName( String loginName ) {
    
        if ( loginName != null ) {
            for ( UserPojo user : getUsers() ) {
                if ( loginName.equals( user.getLoginName() ) ) {
                    return user;
                }
            }
        }
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.configuration.ConfigurationAccessService#getUserByNxUserId(int)
     */
    public UserPojo getUserByNxUserId( int nxUserId ) {
    
        for ( UserPojo user : getUsers() ) {
            if ( nxUserId == user.getNxUserId() ) {
                return user;
            }
        }
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.configuration.ConfigurationAccessService#updateUser(org.nexuse2e.pojo.UserPojo)
     */
    public void updateUser( UserPojo user ) throws NexusException {
        UserPojo oldUser = getUserByNxUserId( user.getNxUserId() );
        if ( oldUser != null ) {
            getUsers( null ).remove( oldUser );
        }
        getUsers( null ).add( user );
        addToUpdateList( user );
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.configuration.ConfigurationAccessService#deleteUser(org.nexuse2e.pojo.UserPojo)
     */
    public void deleteUser( UserPojo user ) throws NexusException {
    
        UserPojo oldUser = getUserByNxUserId( user.getNxUserId() );
        if ( oldUser != null ) {
            getUsers( null ).remove( oldUser );
            addToDeleteList( oldUser );
        }
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.configuration.ConfigurationAccessService#getRoles(java.util.Comparator)
     */
    public List<RolePojo> getRoles( Comparator<RolePojo> comparator ) {
    
        List<RolePojo> roles = getRoles();
        if ( comparator != null ) {
            Collections.sort( roles, comparator );
        }
        return roles;
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.configuration.ConfigurationAccessService#getRoleByNxRoleId(int)
     */
    public RolePojo getRoleByNxRoleId( int nxRoleId ) {
    
        for ( RolePojo role : getRoles() ) {
            if ( nxRoleId == role.getNxRoleId() ) {
                return role;
            }
        }
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.configuration.ConfigurationAccessService#getRoleByName(java.lang.String)
     */
    public RolePojo getRoleByName( String name ) {
    
        if ( name != null ) {
            for ( RolePojo role : getRoles() ) {
                if ( name.equals( role.getName() ) ) {
                    return role;
                }
            }
        }
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.configuration.ConfigurationAccessService#updateRole(org.nexuse2e.pojo.RolePojo)
     */
    public void updateRole( RolePojo role ) throws NexusException {

        RolePojo oldRole = getRoleByNxRoleId( role.getNxRoleId() );
        if ( oldRole != null ) {
            getRoles( null ).remove( oldRole );
        }
        getRoles( null ).add( role );
        addToUpdateList( role );
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.configuration.ConfigurationAccessService#deleteRole(org.nexuse2e.pojo.RolePojo)
     */
    public void deleteRole( RolePojo role ) throws NexusException {
    
        RolePojo oldRole = getRoleByNxRoleId( role.getNxRoleId() );
        if ( oldRole != null ) {
            getRoles( null ).remove( oldRole );
            addToDeleteList( oldRole );
        }
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.configuration.ConfigurationAccessService#getMappings(java.util.Comparator)
     */
    public List<MappingPojo> getMappings( Comparator<MappingPojo> comparator ) {
    
        List<MappingPojo> mappings = getMappings();
        if ( comparator != null ) {
            Collections.sort( mappings, comparator );
        }
        return mappings;
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.configuration.ConfigurationAccessService#getMappingByNxMappingId(int)
     */
    public MappingPojo getMappingByNxMappingId( int nxMappingId ) {
    
        for ( MappingPojo mapping : getMappings() ) {
            if ( nxMappingId == mapping.getNxMappingId() ) {
                return mapping;
            }
        }
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.configuration.ConfigurationAccessService#getMappingByCategoryDirectionAndKey(java.lang.String, boolean, java.lang.String)
     */
    public MappingPojo getMappingByCategoryDirectionAndKey( String category, boolean left, String key ) {
    
        if ( category != null && key != null ) {
            if ( getMappings() != null ) {
                for ( MappingPojo mapping : getMappings() ) {
                    if ( category.equals( mapping.getCategory() ) ) {
                        String tempKey = left ? mapping.getLeftValue() : mapping.getRightValue();
                        if ( tempKey.equals( key ) ) {
                            return mapping;
                        }
                    }
                }
            } else {
                LOG.error( "engine config is not initalized! No mapping available" );
            }
    
        }
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.configuration.ConfigurationAccessService#updateMapping(org.nexuse2e.pojo.MappingPojo)
     */
    public void updateMapping( MappingPojo mapping ) throws NexusException {
    
        MappingPojo oldMapping = getMappingByNxMappingId( mapping.getNxMappingId() );
        if ( oldMapping != null ) {
            getMappings( null ).remove( oldMapping );
        }
        getMappings( null ).add( mapping );
        addToUpdateList( mapping );
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.configuration.ConfigurationAccessService#updateMappings(java.util.List, java.util.List)
     */
    public void updateMappings( List<MappingPojo> addMappings, List<MappingPojo> removeMappings ) throws NexusException {
    
        for (MappingPojo mapping : removeMappings) {
            MappingPojo oldMapping = getMappingByNxMappingId( mapping.getNxMappingId() );
            if ( oldMapping != null ) {
                getMappings( null ).remove( oldMapping );
                addToDeleteList( oldMapping );
            }
        }
    
        for (MappingPojo mapping : addMappings) {
            MappingPojo oldMapping = null;
            if (mapping.getNxMappingId() != 0) {
                oldMapping = getMappingByNxMappingId( mapping.getNxMappingId() );
            }
            if ( oldMapping != null ) {
                getMappings( null ).remove( oldMapping );
            }
            getMappings( null ).add( mapping );
            addToUpdateList( mapping );
        }
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.configuration.ConfigurationAccessService#deleteMapping(org.nexuse2e.pojo.MappingPojo)
     */
    public void deleteMapping( MappingPojo mapping ) throws NexusException {
    
        MappingPojo oldMapping = getMappingByNxMappingId( mapping.getNxMappingId() );
        if ( oldMapping != null ) {
            getMappings( null ).remove( oldMapping );
            addToDeleteList( oldMapping );
        }
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.configuration.ConfigurationAccessService#getGenericParameters(java.lang.String, java.lang.String, java.util.Map)
     */
    public Map<String, Object> getGenericParameters( String category, String tag,
            Map<String, ParameterDescriptor> descriptors ) {
    
        if ( StringUtils.isEmpty( category ) ) {
            return null;
        }
        if ( descriptors == null || descriptors.size() == 0 ) {
            return null;
        }
        Map<String, Object> resultMap = new HashMap<String, Object>();
        List<GenericParamPojo> values = getGenericParameters().get( category );
        if ( values == null ) {
            values = new ArrayList<GenericParamPojo>();
        }
        boolean isUpdated = false;
        for ( String name : descriptors.keySet() ) {
            ParameterDescriptor descriptor = descriptors.get( name );
            if ( descriptor != null ) {
                isUpdated = false;
                for ( GenericParamPojo value : values ) {
                    if ( ( ( value.getTag() == null && tag == null ) || ( value.getTag() != null && value.getTag()
                            .equals( tag ) ) )
                            && value.getParamName().equals( name ) ) {
                        resultMap.put( value.getParamName(), getParameterValue( descriptor.getParameterType(), value.getValue() ) );
                        isUpdated = true;
                    }
                }
                if ( !isUpdated ) {
                    if ( descriptor.getDefaultValue() != null ) {
                        resultMap.put( name, descriptor.getDefaultValue() );
                    }
                }
            }
        }
    
        return resultMap;
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.configuration.ConfigurationAccessService#getGenericParameter(java.lang.String, java.lang.String, org.nexuse2e.configuration.Constants.ParameterType, java.lang.Object)
     */
    public Object getGenericParameter( String category, String tag, ParameterType type, Object defaultValue ) {
        List<GenericParamPojo> values = getGenericParameters().get( category );
        if ( values != null ) {
            for (GenericParamPojo param : values) {
                if (param.getParamName().equals( tag )) {
                    return getParameterValue( type, param.getValue() );
                }
            }
        }
        return defaultValue;
    }
    
    public void setGenericParameters( String category, String tag, Map<String, Object> values,
            Map<String, ParameterDescriptor> descriptors ) throws NexusException {
    
        if ( StringUtils.isEmpty( category ) ) {
            return;
        }
        if ( StringUtils.isEmpty( tag ) ) {
            tag = null;
        }
        if ( values == null || descriptors == null || values.size() == 0 || descriptors.size() == 0 ) {
            return;
        }
        List<GenericParamPojo> oldValues = getGenericParameters().get( category );
        if ( oldValues == null ) {
            oldValues = new ArrayList<GenericParamPojo>();
            getGenericParameters().put( category, oldValues );
        }
        int seqNo = 0;
        for ( String name : values.keySet() ) {
            ParameterDescriptor pd = descriptors.get( name );
            if ( pd == null ) {
                continue;
            }
            GenericParamPojo param = null;
            for ( GenericParamPojo pojo : oldValues ) {
                if ( pojo.getParamName().equals( name ) ) {
                    if ( ( pojo.getTag() == null && tag == null ) || ( pojo.getTag() != null && pojo.equals( tag ) ) ) {
                        param = pojo;
                    }
                }
            }
            if ( param == null ) {
                param = new GenericParamPojo();
                param.setCreatedDate( new Date() );
                param.setModifiedDate( new Date() );
                param.setParamName( name );
                param.setLabel( pd.getLabel() );
                param.setSequenceNumber( seqNo++ );
                param.setTag( tag );
                param.setCategory( category );
                oldValues.add( param );
            }
            if ( pd.getParameterType() == ParameterType.LIST ) {
                ListParameter dropdown = (ListParameter) values.get( name );
                if ( dropdown != null ) {
                    param.setValue( dropdown.getSelectedValue() );
                }
            } else if ( pd.getParameterType() == ParameterType.ENUMERATION ) {
                // TODO: Implement this!
            } else {
                Object value = values.get( name );
                if ( value == null ) {
                    param.setValue( toString( pd.getDefaultValue() ) );
                } else {
                    param.setValue( toString( value ) );
                }
            }
            addToUpdateList( param );
        }
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.configuration.ConfigurationAccessService#containsParameters(java.lang.String, java.lang.String)
     */
    public boolean containsParameters( String category, String tag ) {
    
        List<GenericParamPojo> values = getGenericParameters().get( category );
        if ( values != null ) {
            for ( GenericParamPojo pojo : values ) {
                if ( ( tag == null && pojo.getTag() == null ) || ( tag != null && tag.equals( pojo.getTag() ) ) ) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * encapsulates the required checks for NULL values.
     * 
     * @param value
     * @return
     */
    private static String toString( Object value ) {
    
        if ( value == null ) {
            return null;
        }
        return value.toString();
    }
    
    /**
     * Converts a <code>String</code> parameter value into an object
     * of it's domain type.
     *
     * @param type The parameter type.
     * @param value The <code>String</code> representation to be converted.
     * @return The domain type, except for types ENUMERATION and DROPDOWN
     */
    private static Object getParameterValue( ParameterType type, String value ) {
    
        switch ( type ) {
            case UNKNOWN:
            case STRING:
            case PASSWORD:
            case SERVICE:
                return value;
            case BOOLEAN:
                return Boolean.valueOf( value );
            default:
                return null;
        }
    }
    
}
