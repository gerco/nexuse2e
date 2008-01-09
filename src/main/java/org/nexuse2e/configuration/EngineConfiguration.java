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
package org.nexuse2e.configuration;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.nexuse2e.ActionSpecificKey;
import org.nexuse2e.Engine;
import org.nexuse2e.Manageable;
import org.nexuse2e.NexusException;
import org.nexuse2e.ProtocolSpecificKey;
import org.nexuse2e.Constants.BeanStatus;
import org.nexuse2e.backend.BackendPipelineDispatcher;
import org.nexuse2e.configuration.Constants.ComponentType;
import org.nexuse2e.dao.ConfigDAO;
import org.nexuse2e.messaging.BackendActionSerializer;
import org.nexuse2e.messaging.BackendPipeline;
import org.nexuse2e.messaging.FrontendActionSerializer;
import org.nexuse2e.messaging.FrontendPipeline;
import org.nexuse2e.messaging.Pipelet;
import org.nexuse2e.pojo.ActionPojo;
import org.nexuse2e.pojo.CertificatePojo;
import org.nexuse2e.pojo.ChoreographyPojo;
import org.nexuse2e.pojo.ComponentPojo;
import org.nexuse2e.pojo.ConnectionPojo;
import org.nexuse2e.pojo.ConversationPojo;
import org.nexuse2e.pojo.GenericParamPojo;
import org.nexuse2e.pojo.LoggerPojo;
import org.nexuse2e.pojo.MappingPojo;
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
import org.nexuse2e.ui.structure.StructureException;
import org.springframework.context.support.ApplicationObjectSupport;

/**
 * @author gesch
 *
 */
@XmlRootElement(name = "NEXUSe2eConfiguration")
@XmlType(name = "NEXUSe2eConfigurationType")
@XmlAccessorType(XmlAccessType.NONE)
public class EngineConfiguration {

    private static Logger                               LOG                       = Logger
                                                                                          .getLogger( EngineConfiguration.class );

    private HashMap<ActionSpecificKey, BackendPipeline> backendInboundPipelines   = new HashMap<ActionSpecificKey, BackendPipeline>();
    private HashMap<ActionSpecificKey, BackendPipeline> backendOutboundPipelines  = new HashMap<ActionSpecificKey, BackendPipeline>();
    private HashMap<TRPPojo, FrontendPipeline>          frontendInboundPipelines  = new HashMap<TRPPojo, FrontendPipeline>();
    private HashMap<TRPPojo, FrontendPipeline>          frontendOutboundPipelines = new HashMap<TRPPojo, FrontendPipeline>();
    private HashMap<String, FrontendActionSerializer>   frontendActionSerializers = new HashMap<String, FrontendActionSerializer>();
    private HashMap<String, BackendActionSerializer>    backendActionSerializers  = new HashMap<String, BackendActionSerializer>();
    private StaticBeanContainer                         staticBeanContainer       = null;

    private int                                         skeletonStatus            = BeanStatus.UNDEFINED.getValue();
    private int                                         dataStatus                = BeanStatus.UNDEFINED.getValue();
    private int                                         structureStatus           = BeanStatus.UNDEFINED.getValue();
    private long                                        timestamp;

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

    /**
     * 
     */
    private List<LoggerPojo>                            loggers                   = null;

    private List<ServicePojo>                           services                  = null;

    private List<UserPojo>                              users                     = null;

    private List<RolePojo>                              roles                     = null;

    private List<MappingPojo>                           mappings                  = null;

    private HashMap<String, List<GenericParamPojo>>     genericParameters         = new HashMap<String, List<GenericParamPojo>>();

    BaseConfigurationProvider                           baseConfigurationProvider = null;

    private HashMap<String, List<String>>               logCategories             = new HashMap<String, List<String>>();

    /**
     * @param baseConfigurationProvider
     */
    public EngineConfiguration( BaseConfigurationProvider baseConfigurationProvider ) {

        timestamp = new Date().getTime();
        this.baseConfigurationProvider = baseConfigurationProvider;
    } // EngineConfiguration

    private EngineConfiguration() {

        timestamp = new Date().getTime();
    }

    /**
     * @throws InstantiationException
     */
    public void init() throws InstantiationException, StructureException {

        initLoggerCategories();

        createStaticBeanContainer();
        if ( isDatabasePopulated() ) {
            loadDatafromDB();
        } else {
            LOG.info( "Empty database detected, creating and saving base configuration of type: "
                    + baseConfigurationProvider.getClass() );

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

            baseConfigurationProvider.createBaseConfiguration( components, choreographies, partners,
                    backendPipelineTemplates, frontendPipelineTemplates, services, certificates, trps, users, roles,
                    loggers, mappings );
            try {
                saveConfigurationToDB();
                LOG.info( "Base configurations saved to database." );
            } catch ( NexusException e ) {
                throw new InstantiationException( "Error saving base configuration: " + e.getMessage() );
            }
        }

        initializeLogAppenders();

        createConfiguration();

        exportToXML();
    } // init

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

    private void initializeLogAppenders() throws InstantiationException {

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
                        throw new InstantiationError( "Error while creating instance for logger: " + logger.getName()
                                + " - " + e.getMessage() );
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
                                    || targetlogger.getLevel().toInt() < logger.getThreshold() ) {
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
                    } catch ( RuntimeException rex ) {
                        rex.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * @throws InstantiationException 
     * 
     */
    public void createStaticBeanContainer() throws InstantiationException {

        staticBeanContainer = new StaticBeanContainer();
        HashMap<String, Manageable> beanContainer = new LinkedHashMap<String, Manageable>();
        staticBeanContainer.setManagableBeans( beanContainer );

        Object bean = Engine.getInstance().getBeanFactory()
                .getBean( org.nexuse2e.Constants.FRONTEND_INBOUND_DISPATCHER );
        if ( bean != null && bean instanceof Manageable ) {
            staticBeanContainer.getManagableBeans().put( org.nexuse2e.Constants.FRONTEND_INBOUND_DISPATCHER,
                    (Manageable) bean );
        } else {
            skeletonStatus = BeanStatus.ERROR.getValue();
            throw new InstantiationException( "FrontendInboundDispatcher Bean not found!" );

        }
        bean = Engine.getInstance().getBeanFactory().getBean( org.nexuse2e.Constants.FRONTEND_OUTBOUND_DISPATCHER );
        if ( bean != null && bean instanceof Manageable ) {
            staticBeanContainer.getManagableBeans().put( org.nexuse2e.Constants.FRONTEND_OUTBOUND_DISPATCHER,
                    (Manageable) bean );
        } else {
            skeletonStatus = BeanStatus.ERROR.getValue();
            throw new InstantiationException( "FrontendOutboundDispatcher Bean not found!" );

        }
        bean = Engine.getInstance().getBeanFactory().getBean( org.nexuse2e.Constants.BACKEND_INBOUND_DISPATCHER );
        if ( bean != null && bean instanceof Manageable ) {
            staticBeanContainer.getManagableBeans().put( org.nexuse2e.Constants.BACKEND_INBOUND_DISPATCHER,
                    (Manageable) bean );
        } else {
            skeletonStatus = BeanStatus.ERROR.getValue();
            throw new InstantiationException( "BackendInboundDispatcher Bean not found!" );

        }
        bean = Engine.getInstance().getBeanFactory().getBean( org.nexuse2e.Constants.BACKEND_OUTBOUND_DISPATCHER );
        if ( bean != null && bean instanceof Manageable ) {
            staticBeanContainer.getManagableBeans().put( org.nexuse2e.Constants.BACKEND_OUTBOUND_DISPATCHER,
                    (Manageable) bean );
        } else {
            skeletonStatus = BeanStatus.ERROR.getValue();
            throw new InstantiationException( "BackendOutboundDispatcher Bean not found!" );

        }
        bean = Engine.getInstance().getBeanFactory().getBean( org.nexuse2e.Constants.BACKEND_PIPELINE_DISPATCHER );
        if ( bean != null && bean instanceof Manageable ) {
            staticBeanContainer.getManagableBeans().put( org.nexuse2e.Constants.BACKEND_PIPELINE_DISPATCHER,
                    (Manageable) bean );
        } else {
            skeletonStatus = BeanStatus.ERROR.getValue();
            throw new InstantiationException( "BackendPipelineDispatcher Bean not found!" );

        }

        skeletonStatus = BeanStatus.INITIALIZED.getValue();
    }

    /**
     * @return
     * @throws InstantiationException
     */
    public boolean isDatabasePopulated() throws InstantiationException {

        ConfigDAO configDao = null;
        try {
            configDao = (ConfigDAO) Engine.getInstance().getDao( "configDao" );
            List<TRPPojo> tempTRPs = configDao.getTrps( null, null );
            if ( ( tempTRPs != null ) && ( tempTRPs.size() != 0 ) ) {
                return true;
            }
        } catch ( Exception e ) {
            InstantiationException ie = new InstantiationException( e.getMessage() );
            ie.setStackTrace( e.getStackTrace() );
            throw ie;
        }

        return false;
    }

    /**
     * 
     */
    public void loadDatafromDB() throws InstantiationException {

        ConfigDAO configDao = null;
        try {
            configDao = (ConfigDAO) Engine.getInstance().getDao( "configDao" );
        } catch ( Exception e ) {
            InstantiationException ie = new InstantiationException( e.getMessage() );
            ie.setStackTrace( e.getStackTrace() );
            throw ie;
        }
        Session session = configDao.getDBSession();
        try {

            List<ChoreographyPojo> tempChoreographies = configDao.getChoreographies( session, null );
            if ( tempChoreographies == null ) {
                LOG.debug( "No choreographies available in database!" );
            } else {
                LOG.trace( "ChoreographyCount:" + tempChoreographies.size() );
                setChoreographies( tempChoreographies );
            }

            List<PartnerPojo> tempPartners = configDao.getPartners( session, null );
            if ( tempPartners == null ) {
                LOG.debug( "No partners available in database!" );
            } else {
                LOG.trace( "PartnerCount:" + tempPartners.size() );
                setPartners( tempPartners );
            }

            List<CertificatePojo> allCertificates = configDao.getCertificates( session, null );
            if ( allCertificates == null || allCertificates.size() == 0 ) {
                LOG.debug( "No certificates available in database!" );
            } else {
                setCertificates( allCertificates );

            }

            List<PipelinePojo> pipelines = configDao.getFrontendPipelines( session, null );
            if ( pipelines == null || pipelines.size() == 0 ) {
                LOG.debug( "No frontend pipelines available in database!" );
            } else {
                setFrontendPipelineTemplates( pipelines );

            }

            pipelines = configDao.getBackendPipelines( session, null );
            if ( pipelines == null || pipelines.size() == 0 ) {
                LOG.debug( "No backend pipelines available in database!" );
            } else {
                setBackendPipelineTemplates( pipelines );

            }

            List<TRPPojo> tempTRPs = configDao.getTrps( session, null );
            setTrps( tempTRPs );

            List<ComponentPojo> tempComponents = configDao.getComponents( session, null );
            setComponents( tempComponents );

            List<LoggerPojo> loggers = configDao.getLoggers( session, null );
            setLoggers( loggers );

            List<ServicePojo> services = configDao.getServices( session, null );
            setServices( services );

            List<UserPojo> users = configDao.getUsers( session, null );
            setUsers( users );

            List<RolePojo> roles = configDao.getRoles( session, null );
            setRoles( roles );

            List<MappingPojo> mappings = configDao.getMappings( session, null );
            setMappings( mappings );

            setGenericParameters( new HashMap<String, List<GenericParamPojo>>() );
            List<GenericParamPojo> tempParams = configDao.getGenericParameters( session, null );
            if ( tempParams != null && tempParams.size() > 0 ) {
                for ( GenericParamPojo pojo : tempParams ) {
                    List<GenericParamPojo> catParams = getGenericParameters().get( pojo.getCategory() );
                    if ( catParams == null ) {
                        catParams = new ArrayList<GenericParamPojo>();
                        getGenericParameters().put( pojo.getCategory(), catParams );
                    }
                    catParams.add( pojo );
                }
            }

        } catch ( NexusException e ) {
            InstantiationException ie = new InstantiationException( e.getMessage() );
            ie.setStackTrace( e.getStackTrace() );
            throw ie;
        }
        configDao.releaseDBSession( session );
    } // loadDataFromDB

    private void exportToXML() {

        try {
            JAXBContext jaxbContext = JAXBContext.newInstance( EngineConfiguration.class );
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, true );
            System.out.println( "********************************************" );
            marshaller.marshal( this, System.out );
            System.out.println( "********************************************" );
        } catch ( JAXBException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * @param pipeline
     * @throws NexusException
     */
    public void deletePipelineInDB( PipelinePojo pipeline ) throws NexusException {

        if ( pipeline == null ) {
            return;
        }
        ConfigDAO configDao = null;
        try {
            configDao = (ConfigDAO) Engine.getInstance().getDao( "configDao" );
        } catch ( Exception e ) {
            NexusException ie = new NexusException( e );
            ie.setStackTrace( e.getStackTrace() );
            throw ie;
        }
        configDao.deletePipeline( pipeline, null, null );
    }

    /**
     * @param component
     * @throws NexusException
     */
    public void deleteComponentInDB( ComponentPojo component ) throws NexusException {

        if ( component == null ) {
            return;
        }
        ConfigDAO configDao = null;
        try {
            configDao = (ConfigDAO) Engine.getInstance().getDao( "configDao" );
        } catch ( Exception e ) {
            NexusException ie = new NexusException( e );
            ie.setStackTrace( e.getStackTrace() );
            throw ie;
        }
        configDao.deleteComponent( component, null, null );
    }

    /**
     * @param partner
     * @throws NexusException
     */
    public void deletePartnerInDB( PartnerPojo partner ) throws NexusException {

        if ( partner == null ) {
            return;
        }
        ConfigDAO configDao = null;
        try {
            configDao = (ConfigDAO) Engine.getInstance().getDao( "configDao" );
        } catch ( Exception e ) {
            NexusException ie = new NexusException( e );
            ie.setStackTrace( e.getStackTrace() );
            throw ie;
        }
        configDao.deletePartner( partner, null, null );
    }

    /**
     * @param connection
     * @throws NexusException
     */
    public void deleteConnectionInDB( ConnectionPojo connection ) throws NexusException {

        if ( connection == null ) {
            return;
        }
        ConfigDAO configDao = null;
        try {
            configDao = (ConfigDAO) Engine.getInstance().getDao( "configDao" );
        } catch ( Exception e ) {
            NexusException ie = new NexusException( e );
            ie.setStackTrace( e.getStackTrace() );
            throw ie;
        }
        configDao.deleteConnection( connection, null, null );
    }

    /**
     * @param choreography
     * @throws NexusException
     */
    public void deleteChoreographyInDB( ChoreographyPojo choreography ) throws NexusException {

        if ( choreography == null ) {
            return;
        }
        ConfigDAO configDao = null;
        try {
            configDao = (ConfigDAO) Engine.getInstance().getDao( "configDao" );
        } catch ( Exception e ) {
            NexusException ie = new NexusException( e );
            ie.setStackTrace( e.getStackTrace() );
            throw ie;
        }
        configDao.deleteChoreography( choreography, null, null );
    }

    /**
     * @throws NexusException
     */
    public void saveConfigurationToDB() throws NexusException {

        ConfigurationAccessService current = Engine.getInstance().getActiveConfigurationAccessService();

        ConfigDAO configDao = null;
        try {
            configDao = (ConfigDAO) Engine.getInstance().getDao( "configDao" );
        } catch ( Exception e ) {
            NexusException ie = new NexusException( e );
            ie.setStackTrace( e.getStackTrace() );
            throw ie;
        }
        Session session = configDao.getDBSession();

        Transaction transaction = null;//session.beginTransaction();

        List<TRPPojo> obsoleteTRPs = getObsoleteEntries( trps, current.getTrps() );
        for ( TRPPojo pojo : obsoleteTRPs ) {
            configDao.deleteTrp( pojo, session, transaction );
        }
        obsoleteTRPs.clear();

        if ( trps != null && trps.size() > 0 ) {

            Iterator<TRPPojo> i = trps.iterator();
            while ( i.hasNext() ) {
                TRPPojo pojo = i.next();
                LOG.debug( "TRP: " + pojo.getNxTRPId() + " - " + pojo.getProtocol() + " - " + pojo.getVersion() + " - "
                        + pojo.getTransport() );
                if ( pojo.getNxTRPId() != 0 ) {
                    configDao.updateTRP( pojo, session, transaction );
                } else {
                    configDao.saveTRP( pojo, session, transaction );
                }
            }
        }

        List<ComponentPojo> obsoleteComponents = getObsoleteEntries( components, current.getComponents(
                ComponentType.ALL, null ) );
        for ( ComponentPojo pojo : obsoleteComponents ) {
            configDao.deleteComponent( pojo, session, transaction );
        }
        obsoleteComponents.clear();

        if ( components != null && components.size() > 0 ) {

            for ( ComponentPojo pojo : components ) {
                LOG.debug( "Component: " + pojo.getNxComponentId() + " - " + pojo.getType() + " - " + pojo.getName() );
                if ( pojo.getNxComponentId() != 0 ) {
                    pojo.setModifiedDate( new Date() );
                    configDao.updateComponent( pojo, session, transaction );
                } else {
                    pojo.setCreatedDate( new Date() );
                    pojo.setModifiedDate( new Date() );
                    configDao.saveComponent( pojo, session, transaction );
                }
            }
        }

        List<PipelinePojo> obsoleteBackendPipelineTemplates = getObsoleteEntries( backendPipelineTemplates, current
                .getBackendPipelinePojos( Constants.PIPELINE_TYPE_ALL, null ) );
        for ( PipelinePojo pojo : obsoleteBackendPipelineTemplates ) {
            configDao.deletePipeline( pojo, session, transaction );
        }
        obsoleteBackendPipelineTemplates.clear();

        if ( backendPipelineTemplates != null && backendPipelineTemplates.size() > 0 ) {

            for ( PipelinePojo pojo : backendPipelineTemplates ) {
                LOG.debug( "BackendPipeline: " + pojo.getNxPipelineId() + " - " + pojo.getName() );
                if ( pojo.getNxPipelineId() != 0 ) {
                    pojo.setModifiedDate( new Date() );
                    configDao.updatePipeline( pojo, session, transaction );
                } else {
                    pojo.setCreatedDate( new Date() );
                    pojo.setModifiedDate( new Date() );
                    configDao.savePipeline( pojo, session, transaction );
                }
            }
        }

        List<PipelinePojo> obsoleteFrontendPipelineTemplates = getObsoleteEntries( frontendPipelineTemplates, current
                .getFrontendPipelinePojos( Constants.PIPELINE_TYPE_ALL, null ) );
        for ( PipelinePojo pojo : obsoleteBackendPipelineTemplates ) {
            configDao.deletePipeline( pojo, session, transaction );
        }
        obsoleteFrontendPipelineTemplates.clear();

        if ( frontendPipelineTemplates != null && frontendPipelineTemplates.size() > 0 ) {

            for ( PipelinePojo pojo : frontendPipelineTemplates ) {
                LOG.debug( "FrontendPipeline: " + pojo.getNxPipelineId() + " - " + pojo.getName() );
                if ( pojo.getNxPipelineId() != 0 ) {
                    pojo.setModifiedDate( new Date() );
                    configDao.updatePipeline( pojo, session, transaction );
                } else {
                    pojo.setCreatedDate( new Date() );
                    pojo.setModifiedDate( new Date() );
                    configDao.savePipeline( pojo, session, transaction );
                }
            }
        }

        if ( partners != null && partners.size() > 0 ) {

            Iterator<PartnerPojo> i = partners.iterator();
            while ( i.hasNext() ) {
                PartnerPojo pojo = i.next();
                LOG.debug( "Partner: " + pojo.getNxPartnerId() + " - " + pojo.getPartnerId() + " - " + pojo.getName() );

                if ( pojo.getNxPartnerId() != 0 ) {
                    pojo.setModifiedDate( new Date() );
                    configDao.updatePartner( pojo, session, transaction );
                } else {
                    pojo.setCreatedDate( new Date() );
                    pojo.setModifiedDate( new Date() );
                    configDao.savePartner( pojo, session, transaction );
                }
            }
        }

        List<ChoreographyPojo> obsoleteChoreographies = getObsoleteEntries( choreographies, current.getChoreographies() );
        for ( ChoreographyPojo pojo : obsoleteChoreographies ) {
            boolean removeConversations = false;

            List<ConversationPojo> conversations = Engine.getInstance().getTransactionService()
                    .getConversationsByChoreography( pojo, session, transaction );
            if ( conversations == null || conversations.size() == 0 ) {
                configDao.deleteChoreography( pojo, session, transaction );
            } else if ( removeConversations ) {
                for ( ConversationPojo conv : conversations ) {
                    Engine.getInstance().getTransactionService().deleteConversation( conv, session, transaction );
                }
                configDao.deleteChoreography( pojo, session, transaction );
            }
        }
        obsoleteChoreographies.clear();

        if ( choreographies != null && choreographies.size() > 0 ) {

            Iterator<ChoreographyPojo> i = choreographies.iterator();
            while ( i.hasNext() ) {

                ChoreographyPojo pojo = i.next();
                LOG.debug( "Choreography: " + pojo.getNxChoreographyId() + " - " + pojo.getName() );

                if ( pojo.getNxChoreographyId() != 0 ) {
                    pojo.setModifiedDate( new Date() );
                    configDao.updateChoreography( pojo, session, transaction );
                } else {
                    pojo.setCreatedDate( new Date() );
                    pojo.setModifiedDate( new Date() );
                    configDao.saveChoreography( pojo, session, transaction );
                }
            }
        }

        List<PartnerPojo> obsoletePartners = getObsoleteEntries( partners, current.getPartners(
                Constants.PARTNER_TYPE_ALL, null ) );
        for ( PartnerPojo pojo : obsoletePartners ) {
            configDao.deletePartner( pojo, session, transaction );
        }
        obsoletePartners.clear();

        List<ServicePojo> obsoleteServices = getObsoleteEntries( services, current.getServices() );
        for ( ServicePojo pojo : obsoleteServices ) {
            configDao.deleteService( pojo, session, transaction );
        }
        obsoleteServices.clear();

        if ( services != null && services.size() > 0 ) {
            for ( ServicePojo pojo : services ) {
                LOG.debug( "Service: " + pojo.getNxServiceId() + " - " + pojo.getName() );
                if ( pojo.getNxServiceId() != 0 ) {
                    pojo.setModifiedDate( new Date() );
                    configDao.updateService( pojo, session, transaction );
                } else {
                    pojo.setCreatedDate( new Date() );
                    pojo.setModifiedDate( new Date() );
                    configDao.saveService( pojo, session, transaction );
                }
            }
        }

        List<LoggerPojo> obsoleteLoggers = getObsoleteEntries( loggers, current.getLoggers() );
        for ( LoggerPojo pojo : obsoleteLoggers ) {
            configDao.deleteLogger( pojo, session, transaction );
        }
        obsoleteLoggers.clear();

        if ( loggers != null && loggers.size() > 0 ) {

            for ( LoggerPojo logger : loggers ) {
                LOG.debug( "Logger: " + logger.getNxLoggerId() + " - " + logger.getName() );
                if ( logger.getNxLoggerId() != 0 ) {
                    logger.setModifiedDate( new Date() );
                    configDao.updateLogger( logger, session, transaction );
                } else {
                    logger.setCreatedDate( new Date() );
                    logger.setModifiedDate( new Date() );
                    configDao.saveLogger( logger, session, transaction );
                }
            }
        }

        List<CertificatePojo> obsoleteCertificates = getObsoleteEntries( certificates, current.getCertificates(
                Constants.CERTIFICATE_TYPE_ALL, null ) );
        for ( CertificatePojo pojo : obsoleteCertificates ) {
            configDao.deleteCertificate( pojo, session, transaction );
        }
        obsoleteCertificates.clear();

        if ( certificates != null && certificates.size() > 0 ) {

            for ( CertificatePojo certificate : certificates ) {
                LOG.debug( "Certificate: " + certificate.getNxCertificateId() + " - " + certificate.getName() );
                if ( certificate.getNxCertificateId() != 0 ) {
                    certificate.setModifiedDate( new Date() );
                    configDao.updateCertificate( certificate, session, transaction );
                } else {
                    certificate.setCreatedDate( new Date() );
                    certificate.setModifiedDate( new Date() );
                    configDao.saveCertificate( certificate, session, transaction );
                }
            }
        }

        /*
         * User configuration
         */
        List<RolePojo> obsoleteRoles = getObsoleteEntries( roles, current.getRoles( null ) );
        for ( RolePojo pojo : obsoleteRoles ) {
            configDao.deleteRole( pojo, session, transaction );
        }
        obsoleteRoles.clear();

        // save roles first to ensure referential integrity
        if ( roles != null && roles.size() > 0 ) {

            for ( RolePojo role : roles ) {
                LOG.debug( "Role: " + role.getNxRoleId() + " - " + role.getName() );
                if ( role.getNxRoleId() != 0 ) {
                    role.setModifiedDate( new Date() );
                    configDao.updateRole( role, session, transaction );
                } else {
                    role.setCreatedDate( new Date() );
                    role.setModifiedDate( new Date() );
                    configDao.saveRole( role, session, transaction );
                }
            }
        }

        List<UserPojo> obsoleteUsers = getObsoleteEntries( users, current.getUsers( null ) );
        for ( UserPojo pojo : obsoleteUsers ) {
            configDao.deleteUser( pojo, session, transaction );
        }
        obsoleteUsers.clear();

        if ( users != null && users.size() > 0 ) {

            for ( UserPojo user : users ) {
                LOG.debug( "User: " + user.getNxUserId() + " - " + user.getLoginName() );
                if ( user.getNxUserId() != 0 ) {
                    user.setModifiedDate( new Date() );
                    configDao.updateUser( user, session, transaction );
                } else {
                    user.setCreatedDate( new Date() );
                    user.setModifiedDate( new Date() );
                    configDao.saveUser( user, session, transaction );
                }
            }
        }

        List<GenericParamPojo> tempList = new ArrayList<GenericParamPojo>();
        for ( String name : genericParameters.keySet() ) {
            List<GenericParamPojo> values = genericParameters.get( name );
            tempList.addAll( values );
        }

        //        List<UserPojo> obsoleteUsers = getObsoleteEntries( users, current.getUsers( null ) );
        //        for ( UserPojo pojo : obsoleteUsers ) {
        //            configDao.deleteUser( pojo, session, transaction );
        //        }
        //        obsoleteUsers.clear();

        if ( tempList != null && tempList.size() > 0 ) {

            for ( GenericParamPojo param : tempList ) {
                LOG.debug( "Parameter: " + param.getNxGenericParamId() + " - (" + param.getCategory() + "/"
                        + param.getTag() + "):" + param.getParamName() + "=" + param.getValue() );
                if ( param.getNxGenericParamId() != 0 ) {
                    param.setModifiedDate( new Date() );
                    configDao.updateGenericParameter( param, session, transaction );
                } else {
                    param.setCreatedDate( new Date() );
                    param.setModifiedDate( new Date() );
                    configDao.saveGenericParameter( param, session, transaction );
                }
            }
        }

        List<MappingPojo> obsoleteMappingEntries = getObsoleteEntries( mappings, current.getMappings( null ) );
        for ( MappingPojo pojo : obsoleteMappingEntries ) {
            configDao.deleteMapping( pojo, session, transaction );
        }
        obsoleteMappingEntries.clear();

        if ( mappings != null && mappings.size() > 0 ) {

            for ( MappingPojo pojo : mappings ) {
                LOG.debug( "Mapping: " + pojo.getNxMappingId() + " - " + pojo.getCategory() + " - "
                        + pojo.getLeftValue() + " - " + pojo.getRightValue() );
                if ( pojo.getNxMappingId() != 0 ) {
                    pojo.setModifiedDate( new Date() );
                    configDao.updateMapping( pojo, session, transaction );
                } else {
                    pojo.setCreatedDate( new Date() );
                    pojo.setModifiedDate( new Date() );
                    configDao.saveMapping( pojo, session, transaction );
                }
            }
        }

        //transaction.commit();
        configDao.releaseDBSession( session );
    } // saveConfigurationToDB

    /**
     * @param newObjects 
     * @param oldObjects
     * @return
     */
    private <T> List<T> getObsoleteEntries( List<T> newObjects, List<T> oldObjects ) {

        List<T> obsoleteObjects = new ArrayList<T>();
        if ( newObjects == null ) {
            newObjects = new ArrayList<T>();
        }
        if ( oldObjects != null ) {
            for ( T oldPojo : oldObjects ) {
                if ( !newObjects.contains( oldPojo ) ) {
                    obsoleteObjects.add( oldPojo );
                }
            }
        }
        return obsoleteObjects;
    }

    /**
     * 
     */
    public void createConfiguration() throws InstantiationException {

        int pos = 0;
        Pipelet[] pipelets = null;
        BackendPipeline backendPipeline = null;
        Map<String, AbstractControllerService> mappings = new HashMap<String, AbstractControllerService>();
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
                        //throw new InstantiationException( "No endpoint for inbound pipeline found for action: "
                        //        + action.getName() );
                        LOG.fatal( "No endpoint for inbound pipeline found for action: " + action.getName() );
                    }
                    if ( outboundPipelinePojo.getPipelets().size() == 0 ) {
                        LOG.warn( "No pipelets found for outbound pipeline for action: " + action.getName() );
                    }

                    pos = 0;
                    int pipeletCount = inboundPipelinePojo.getPipelets().size();
                    pipelets = new Pipelet[pipeletCount > 0 ? pipeletCount - 1 : 0];

                    for ( PipeletPojo pipeletPojo : inboundPipelinePojo.getPipelets() ) {
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
                    pos = 0;
                    pipelets = new Pipelet[outboundPipelinePojo.getPipelets().size()];
                    try {
                        for ( PipeletPojo pipeletPojo : outboundPipelinePojo.getPipelets() ) {
                            Pipelet pipelet = getPipeletInstanceFromPojo( pipeletPojo );
                            pipelets[pos++] = pipelet;
                        }
                    } catch ( NexusException e ) {
                        throw new InstantiationException( e.getMessage() );
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

                    Pipelet[] forwardPipelets = null;
                    if ( pipelinePojo.getPipelets() != null && pipelinePojo.getPipelets().size() > 0 ) {
                        forwardPipelets = new Pipelet[pipelinePojo.getPipelets().size() - 1];
                    } else {
                        forwardPipelets = new Pipelet[0];
                    }

                    int i = 0;
                    for ( PipeletPojo pipeletPojo : pipelinePojo.getPipelets() ) {
                        Pipelet pipelet = getPipeletInstanceFromPojo( pipeletPojo );
                        if ( i == pipelinePojo.getPipelets().size() - 1 ) {
                            // TransportSender
                            frontendPipeline.setPipelineEndpoint( pipelet );
                            ConfigurationUtil.configurePipelet( pipelet, pipeletPojo.getPipeletParams() );
                        } else {
                            forwardPipelets[i++] = pipelet;
                        }
                    }
                    frontendPipeline.setForwardPipelets( forwardPipelets );
                } else {
                    getFrontendInboundPipelines().put( pipelinePojo.getTrp(), frontendPipeline );
                    LOG.trace( "Frontend inbound pipeline: " + pipelinePojo.getName() );
                    Pipelet[] forwardPipelets = null;
                    if ( pipelinePojo.getPipelets() != null && pipelinePojo.getPipelets().size() > 0 ) {
                        forwardPipelets = new Pipelet[pipelinePojo.getPipelets().size() - 1];
                    } else {
                        forwardPipelets = new Pipelet[0];
                    }
                    int i = -1;

                    // Special treatment for first entry: it's a TransportReceiver
                    for ( PipeletPojo pipeletPojo : pipelinePojo.getPipelets() ) {
                        LOG.trace( "Pipelet: " + pipeletPojo.getName() + " - " + pipeletPojo.getPosition() + " - "
                                + pipeletPojo.getClass() );
                        if ( i >= 0 ) {
                            Pipelet pipelet = getPipeletInstanceFromPojo( pipeletPojo );
                            forwardPipelets[i] = pipelet;
                        } else {
                            TransportReceiver transportReceiver = (TransportReceiver) Class.forName(
                                    pipeletPojo.getComponent().getClassName() ).newInstance();
                            transportReceiver.setFrontendPipeline( frontendPipeline );
                            transportReceiver.setKey( frontendPipeline.getKey() );
                            ConfigurationUtil.configurePipelet( transportReceiver, pipeletPojo.getPipeletParams() );
                            // String beanKey = "TransportReceiver" + frontendPipeline.getKey().toString();
                            String beanKey = "TransportReceiver_" + pipelinePojo.getName() + "_"
                                    + frontendPipeline.getKey().toString();
                            if ( !staticBeanContainer.getManagableBeans().containsKey( beanKey ) ) {
                                LOG
                                        .trace( "Registering managable bean: " + beanKey + "(" + pipeletPojo.getName()
                                                + ")" );
                                staticBeanContainer.getManagableBeans().put( beanKey, transportReceiver );
                            } else {
                                LOG.warn( "Managable bean already registered, using first one: " + beanKey + "("
                                        + pipeletPojo.getName() + ")" );
                            }
                        }

                        i++;
                    }
                    frontendPipeline.setForwardPipelets( forwardPipelets );
                    frontendPipeline.setPipelineEndpoint( getStaticBeanContainer().getFrontendInboundDispatcher() );
                }

            } catch ( Exception e ) {
                e.printStackTrace();
                throw new InstantiationException( e.getMessage() );
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
                    staticBeanContainer.getManagableBeans().put( servicePojo.getName(), service );

                    // Register Controller for inbound HTTP messages
                    if ( service instanceof AbstractControllerService ) {
                        String urlAppendix = service.getParameter( "logical_name" );
                        if ( urlAppendix == null ) {
                            urlAppendix = servicePojo.getName();
                        }
                        AbstractControllerService controller = Engine.getInstance().getEngineController()
                                .getControllerWrapper( urlAppendix, (AbstractControllerService) service );
                        LOG.trace( "Registering controller: " + urlAppendix + " - " + controller );
                        mappings.put( urlAppendix, controller );
                    }
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

    private Service getServiceInstanceFromPojo( ServicePojo pojo ) throws NexusException {

        try {
            Object newService = Class.forName( pojo.getComponent().getClassName() ).newInstance();
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
     * @return the dataStatus
     */
    public int getDataStatus() {

        return dataStatus;
    }

    /**
     * @return the skeletonStatus
     */
    public int getSkeletonStatus() {

        return skeletonStatus;
    }

    /**
     * @return the structureStatus
     */
    public int getStructureStatus() {

        return structureStatus;
    }

    /**
     * @return the frontendActionSerializers
     */
    public HashMap<String, FrontendActionSerializer> getFrontendActionSerializers() {

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
    public HashMap<ActionSpecificKey, BackendPipeline> getBackendInboundPipelines() {

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
    public HashMap<ActionSpecificKey, BackendPipeline> getBackendOutboundPipelines() {

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
    public HashMap<String, BackendActionSerializer> getBackendActionSerializers() {

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
     * Deletes a persistent user.
     * @param user The user to delete.
     * @throws NexusException
     */
    public void deleteUserInDB( UserPojo user ) throws NexusException {

        if ( user != null ) {
            ConfigDAO configDao = null;
            try {
                configDao = (ConfigDAO) Engine.getInstance().getDao( "configDao" );
            } catch ( Exception e ) {
                NexusException ie = new NexusException( e );
                ie.setStackTrace( e.getStackTrace() );
                throw ie;
            }
            configDao.deleteUser( user, null, null );
        }
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
     * Deletes a persistent role.
     * @param role The role to delete.
     * @throws NexusException
     */
    public void deleteRoleInDB( RolePojo role ) throws NexusException {

        if ( role != null ) {
            ConfigDAO configDao = null;
            try {
                configDao = (ConfigDAO) Engine.getInstance().getDao( "configDao" );
            } catch ( Exception e ) {
                NexusException ie = new NexusException( e );
                ie.setStackTrace( e.getStackTrace() );
                throw ie;
            }
            configDao.deleteRole( role, null, null );
        }
    }

    /**
     * @param logger
     * @throws NexusException
     */
    public void deleteLoggerInDB( LoggerPojo logger ) throws NexusException {

        if ( logger == null ) {
            return;
        }
        ConfigDAO configDao = null;
        try {
            configDao = (ConfigDAO) Engine.getInstance().getDao( "configDao" );
        } catch ( Exception e ) {
            NexusException ie = new NexusException( e );
            ie.setStackTrace( e.getStackTrace() );
            throw ie;
        }
        configDao.deleteLogger( logger, null, null );

    }

    /**
     * Permanently removes a service from the DB.
     * @param service The service to be deleted.
     * @throws NexusException
     */
    public void deleteServiceInDB( ServicePojo service ) throws NexusException {

        if ( service == null ) {
            return;
        }
        ConfigDAO configDao = null;
        try {
            configDao = (ConfigDAO) Engine.getInstance().getDao( "configDao" );
        } catch ( Exception e ) {
            NexusException ie = new NexusException( e );
            ie.setStackTrace( e.getStackTrace() );
            throw ie;
        }
        configDao.deleteService( service, null, null );

    }

    /**
     * @param certificate
     * @throws NexusException
     */
    public void deleteCertificateInDB( CertificatePojo certificate ) throws NexusException {

        if ( certificate == null ) {
            return;
        }
        ConfigDAO configDao = null;
        try {
            configDao = (ConfigDAO) Engine.getInstance().getDao( "configDao" );
        } catch ( Exception e ) {
            NexusException ie = new NexusException( e );
            ie.setStackTrace( e.getStackTrace() );
            throw ie;
        }
        configDao.deleteCertificate( certificate, null, null );

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
    public HashMap<TRPPojo, FrontendPipeline> getFrontendInboundPipelines() {

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
    public HashMap<TRPPojo, FrontendPipeline> getFrontendOutboundPipelines() {

        return frontendOutboundPipelines;
    }

    /**
     * @param frontendOutboundPipelines
     */
    public void setFrontendOutboundPipelines( HashMap<TRPPojo, FrontendPipeline> frontendOutboundPipelines ) {

        this.frontendOutboundPipelines = frontendOutboundPipelines;
    }

    /**
     * @param original
     * @return
     */
    public static EngineConfiguration cloneConfiguration( EngineConfiguration original ) {

        EngineConfiguration tempConfiguration = new EngineConfiguration();

        CloneContainer container = new CloneContainer( original );
        container.cloneContainer( tempConfiguration );

        return tempConfiguration;
    }

    /**
     * @return the logCategories
     */
    public HashMap<String, List<String>> getLogCategories() {

        return logCategories;
    }

    /**
     * @param logCategories the logCategories to set
     */
    public void setLogCategories( HashMap<String, List<String>> logCategories ) {

        this.logCategories = logCategories;
    }

    /**
     * @return the genericParameters
     */
    public HashMap<String, List<GenericParamPojo>> getGenericParameters() {

        return genericParameters;
    }

    /**
     * @param genericParameters the genericParameters to set
     */
    public void setGenericParameters( HashMap<String, List<GenericParamPojo>> genericParameters ) {

        this.genericParameters = genericParameters;
    }

    /**
     * @param mapping
     * @throws NexusException
     */
    public void deleteMappingInDB( MappingPojo mapping ) throws NexusException {

        if ( mapping != null ) {
            ConfigDAO configDao = null;
            try {
                configDao = (ConfigDAO) Engine.getInstance().getDao( "configDao" );
            } catch ( Exception e ) {
                NexusException ie = new NexusException( e );
                ie.setStackTrace( e.getStackTrace() );
                throw ie;
            }
            configDao.deleteMapping( mapping, null, null );
        }
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

}
