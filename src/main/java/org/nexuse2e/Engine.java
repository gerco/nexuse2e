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
package org.nexuse2e;

import java.security.Security;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.activation.CommandMap;
import javax.activation.FileTypeMap;
import javax.activation.MailcapCommandMap;
import javax.activation.MimetypesFileTypeMap;
import javax.servlet.ServletContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.hibernate.cfg.Configuration;
import org.nexuse2e.Constants.BeanStatus;
import org.nexuse2e.Constants.Runlevel;
import org.nexuse2e.configuration.BaseConfigurationProvider;
import org.nexuse2e.configuration.ConfigurationAccessService;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.configuration.IdGenerator;
import org.nexuse2e.configuration.NexusUUIDGenerator;
import org.nexuse2e.controller.TransactionService;
import org.nexuse2e.controller.TransactionServiceImpl;
import org.nexuse2e.dao.BasicDAO;
import org.nexuse2e.integration.NEXUSe2eInterface;
import org.nexuse2e.integration.NEXUSe2eInterfaceImpl;
import org.nexuse2e.messaging.TimestampFormatter;
import org.nexuse2e.messaging.ebxml.EBXMLTimestampFormatter;
import org.nexuse2e.service.Service;
import org.nexuse2e.transport.TransportReceiver;
import org.nexuse2e.transport.TransportSender;
import org.nexuse2e.util.CertificateUtil;
import org.nexuse2e.util.XMLUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.orm.hibernate3.LocalSessionFactoryBean;
import org.springframework.web.context.support.WebApplicationObjectSupport;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Central component of the NEXUSe2e system tying together and providing access to all processing related 
 * components in the system based on the engine's current configuration.
 *
 * @author gesch
 */
public class Engine extends WebApplicationObjectSupport implements BeanNameAware, BeanFactoryAware, InitializingBean,
        Manageable {

    private static Logger                    LOG                            = Logger.getLogger( Engine.class );

    private static Engine                    instance                       = null;

    private EngineConfiguration              currentConfiguration;
    private BeanFactory                      beanFactory                    = null;
    private LocalSessionFactoryBean          localSessionFactoryBean        = null;
    private String                           beanId;
    private BeanStatus                       status                         = BeanStatus.UNDEFINED;
    private NEXUSe2eInterface                inProcessNEXUSe2eInterface     = new NEXUSe2eInterfaceImpl();

    private TransactionService               transactionService;

    private Map<String, IdGenerator>         idGenrators                    = null;

    private Map<String, TimestampFormatter>  timestampFormatters            = null;

    private BaseConfigurationProvider        baseConfigurationProvider      = null;
    private String                           baseConfigurationProviderClass = null;

    private String                           cacertsPath                    = null;
    private String                           certificatePath                = null;
    private static String                    nexusE2ERoot                   = null;

    private Map<Object, EngineConfiguration> configurations                 = new HashMap<Object, EngineConfiguration>();

    static {

        // Make sure we have the right JCE provider available...
        BouncyCastleProvider bcp = new BouncyCastleProvider();
        Security.removeProvider( CertificateUtil.DEFAULT_JCE_PROVIDER );
        if ( Security.getProvider( CertificateUtil.DEFAULT_JCE_PROVIDER ) == null ) {
            LOG.debug( "Engine - static initializer - Registering BouncyCastleProvider..." );
            Security.addProvider( bcp );
        }
    }

    /**
     * Singleton pattern constructor ?
     */
    public Engine() {

        if ( instance == null ) {
            instance = this;
        }
    }

    /**
     * @return
     */
    public static Engine getInstance() {

        return instance;
    }

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet() throws Exception {

        if ( getBeanFactory() == null ) {
            throw new InstantiationException( "No beanFactory found!" );
        }
        LOG.debug( "EngineId:" + getBeanId() );

    }

    /**
     * 
     */
    public void initialize() {

        initialize( null );
    }

    /**
     * temporary called by bean init-method!!!
     */
    public void initialize( EngineConfiguration config ) {

        /*
         String[] names = getWebApplicationContext().getBeanDefinitionNames();
         for ( String name : names ) {
         LOG.trace( "Bean name: " + name );
         }
         */

        try {
            // undefined - no db connection, spring not configured.
            // instanciated - db connected, string config loaded.
            // initialized - engineconfig loaded
            // started - transports and connectors are running.

            //TODO checking cluster settings and status

            LOG.info( "*** NEXUSe2e Server Version: " + Version.getVersion() );

            LOG.info( "*** JRE version is: " + System.getProperty( "java.version" ) );
            LOG.info( "*** Java class path: " + System.getProperty( "java.class.path" ) );
            LOG.info( "*** Java home: " + System.getProperty( "java.home" ) );

            LOG.info( "*** This software is licensed under the GNU Lesser General Public License (LGPL), Version 2.1" );

            try {
                if ( nexusE2ERoot == null ) {
                    ServletContext currentContext = getServletContext();
                    String webAppPath = currentContext.getRealPath( "" );
                    webAppPath = webAppPath.replace( '\\', '/' );
                    if ( !webAppPath.endsWith( "/" ) ) {
                        webAppPath += "/";
                    }
                    nexusE2ERoot = webAppPath;
                }
            } catch ( IllegalStateException isex ) { // Not in a WebApplicationContext
                if ( nexusE2ERoot == null ) {
                    throw new IllegalStateException(
                            "nexusE2ERoot must be set if not running in a WebApplicationContext" );
                }
            }

            LOG.debug( "NEXUSe2e root directory: " + nexusE2ERoot );

            initializeMime();

            // Set Derby home directory to determine where the DB will be created
            if ( System.getProperty( "derby.system.home" ) == null ) {
                LOG.trace( "Setting derby root directory to: " + nexusE2ERoot + Constants.DERBYROOT );
                System.setProperty( "derby.system.home", nexusE2ERoot + Constants.DERBYROOT );
            } else {
                LOG.trace( "Derby root directory already set: " + System.getProperty( "derby.system.home" ) );
            }

            try {
                // NOTE: & is needed to retrieve the actual bean class and not a proxy!!!
                localSessionFactoryBean = (LocalSessionFactoryBean) getBeanFactory().getBean(
                        "&HibernateSessionFactory" );
                if ( localSessionFactoryBean != null ) {
                    localSessionFactoryBean.createDatabaseSchema();

                    Configuration configuration = localSessionFactoryBean.getConfiguration();
                    String dialect = configuration.getProperty( "hibernate.dialect" );
                    if ( ( dialect != null ) && ( dialect.length() != 0 ) ) {
                        if ( dialect.indexOf( "DB2400" ) != -1 ) {
                            BasicDAO.setISeriesServer( true );
                        } else if ( dialect.indexOf( "SQLServer" ) != -1 ) {
                            BasicDAO.setMsSqlServer( true );
                        }
                    }
                } else {
                    LOG.error( "No Hibernate session factory found in configuration, exiting..." );
                }
            } catch ( Exception e1 ) {
                LOG.error( "Problem with database configuration, exiting..." );
                e1.printStackTrace();
                return;
            }

            // init TimestampGenerators 
            timestampFormatters = new HashMap<String, TimestampFormatter>();
            timestampFormatters.put( "ebxml", new EBXMLTimestampFormatter() );

            // init KeyGenerators
            idGenrators = new HashMap<String, IdGenerator>();
            idGenrators.put( "messageId", new NexusUUIDGenerator() );
            idGenrators.put( "messagePayloadId", new NexusUUIDGenerator() );
            idGenrators.put( "conversationId", new NexusUUIDGenerator() );

            //initialize TransactionDataService
            transactionService = new TransactionServiceImpl();

            // create new Config
            if ( config == null ) {
                try {
                    config = createEngineConfiguration();
                } catch ( InstantiationException e ) {
                    LOG.error( "Problem creating Engine configuration, exiting..." );
                    e.printStackTrace();
                    status = BeanStatus.ERROR;
                    return;
                }
            }

            currentConfiguration = config;

            // Add transaction service to static beans so its life cylcle is managed correctly
            currentConfiguration.getStaticBeanContainer().getManagableBeans().put( Constants.TRANSACTION_SERVICE,
                    transactionService );

            // init skeleton

            if ( currentConfiguration.getSkeletonStatus() != BeanStatus.INITIALIZED.getValue() ) {
                status = BeanStatus.ERROR;
            }

            for ( Manageable bean : currentConfiguration.getStaticBeanContainer().getManagableBeans().values() ) {
                if ( bean.getStatus().getValue() < BeanStatus.INITIALIZED.getValue() ) {
                    bean.initialize( currentConfiguration );
                } else {
                    System.out.println("bean allready initialized: "+bean.getClass().getName());
                }
            }
            // initialize TransportSender and TransportReceiver if they are configured by Spring
            for ( Object o : getApplicationContext().getBeansOfType( TransportSender.class ).values() ) {
                TransportSender ts = (TransportSender) o;
                if ( ts.getStatus().getValue() < BeanStatus.INITIALIZED.getValue() ) {
                    ts.initialize( currentConfiguration );
                }
            }
            for ( Object o : getApplicationContext().getBeansOfType( TransportReceiver.class ).values() ) {
                TransportReceiver tr = (TransportReceiver) o;
                if ( tr.getStatus().getValue() < BeanStatus.INITIALIZED.getValue() ) {
                    tr.initialize( currentConfiguration );
                }
            }

        } catch ( RuntimeException rex ) {
            rex.printStackTrace();
            throw rex;
        } catch ( Exception e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch ( Error e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        activate();
    }

    /**
     * Initialze the mime mappings/setup.  This setup is based on the MimeConfig.xml document found in config/mim.
     * @param home Home directory.
     * @throws InitializationException
     */

    private void initializeMime() throws NexusException {

        Document doc = null;

        doc = retreiveNexusConfig( getNexusE2ERoot(), Constants.CONFIGROOT, Constants.DEFAULT_MIME_CONFIG );

        if ( doc == null ) {
            LOG.info( "Nexus Mime mappings were not configured, " + Constants.DEFAULT_MIME_CONFIG + " was not found." );

            return;
        }

        parseMimeFileMappings( doc );
        parseMimeHandlers( doc );
    }

    /**
     * Parse the Mime configuration file to initialize the Mime handlers.
     * @param mimeConfig Document object for the mime configuration file.
     */
    private void parseMimeHandlers( Document mimeConfigDoc ) {

        MailcapCommandMap commandMap = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
        try {
            XPath xpath = XPathFactory.newInstance().newXPath();
            NodeList handlerNodeList = (NodeList) xpath.evaluate( "/MimeConfig/MimeHandlers/Handler", mimeConfigDoc,
                    XPathConstants.NODESET );

            if ( handlerNodeList != null ) {
                for ( int i = 0; i < handlerNodeList.getLength(); i++ ) {
                    Node handlerNode = handlerNodeList.item( i );
                    String mimeType = ( (Element) handlerNode ).getAttribute( "MimeType" ).trim();

                    xpath = XPathFactory.newInstance().newXPath();
                    Node classNode = (Node) xpath.evaluate( "./Class/text()", handlerNode, XPathConstants.NODE );

                    if ( classNode != null ) {
                        String handlerClass = classNode.getNodeValue().trim();

                        commandMap.addMailcap( mimeType + ";;x-java-content-handler=" + handlerClass );
                        // setMimeHandler( mimeType, handlerClass );
                    } else {
                        LOG.error( "Missing Class element for Mime handler:  " + mimeType );
                    }
                }
            }
        } catch ( XPathExpressionException tx ) {
            LOG.error( "Missing Class element for Mime handler.", tx );
        }
    }

    /**
     * Parse the Mime configuration file to initialize the mime file mappings.
     * The first file type for a MimeType will be added to the mimeMappings hashtable.
     * @param mimeConfigDoc Document object for the mime configuration file.
     */
    private void parseMimeFileMappings( Document mimeConfigDoc ) {

        MimetypesFileTypeMap mimetypesFileTypeMap = (MimetypesFileTypeMap) FileTypeMap.getDefaultFileTypeMap();

        try {

            XPath xpath = XPathFactory.newInstance().newXPath();
            NodeList mimeTypeNodeList = (NodeList) xpath.evaluate( "/MimeConfig/FileMappings/MimeMapping",
                    mimeConfigDoc, XPathConstants.NODESET );

            if ( mimeTypeNodeList != null ) {
                for ( int i = 0; i < mimeTypeNodeList.getLength(); i++ ) {
                    Node handlerNode = mimeTypeNodeList.item( i );
                    String mimeType = ( (Element) handlerNode ).getAttribute( "MimeType" );

                    StringBuffer sb = new StringBuffer( mimeType );

                    xpath = XPathFactory.newInstance().newXPath();
                    NodeList fileTypeNodeList = (NodeList) xpath.evaluate( "./FileType/text()", handlerNode,
                            XPathConstants.NODESET );

                    for ( int x = 0; x < fileTypeNodeList.getLength(); x++ ) {
                        Node fileTypeNode = fileTypeNodeList.item( x );
                        String fileType = fileTypeNode.getNodeValue();

                        // add the first extention found to the mimeMappings hash
                        /*
                         if ( x == 0 ) {
                         addMimeMapping( mimeType, fileType );
                         }
                         */

                        sb.append( " " + fileType );
                    }

                    mimetypesFileTypeMap.addMimeTypes( sb.toString() );
                }
            }
        } catch ( XPathExpressionException tx ) {
            LOG.error( "Error retrieving mime to file type mappings.", tx );
        }
    }

    /**
     * Retrieve the Nexus Configuration and create document.
     * @param home Location of the Nexus home passed in at startup time.
     * @param rootDir Location directory relative to the home directory.
     * @param co
     * @return Document object of configuration.
     */
    private Document retreiveNexusConfig( String home, String rootDir, String configFile ) throws NexusException {

        Document doc = null;
        String xmlFileName = null;

        try {
            if ( home.startsWith( "file:///" ) ) {
                xmlFileName = home + rootDir + configFile;
            } else if ( getNexusE2ERoot().startsWith( "file:///" ) ) {
                xmlFileName = "file:///" + home.substring( 6 ) + rootDir + configFile;
            } else if ( getNexusE2ERoot().startsWith( "file://" ) ) {
                xmlFileName = "file:///" + home.substring( 7 ) + rootDir + configFile;
            } else {
                xmlFileName = "file:///" + home + rootDir + configFile;
            }

            doc = XMLUtil.loadXMLFileFromFile( xmlFileName, false );
        } catch ( Exception ex ) {
            throw new NexusException( ex.toString() );
        }

        return doc;
    } // retreiveNexusConfig

    /**
     * @param type
     * @return
     * @throws NexusException
     */
    public IdGenerator getIdGenerator( String type ) throws NexusException {

        if ( idGenrators != null ) {
            IdGenerator idGenerator = idGenrators.get( type );
            if ( idGenerator == null ) {
                throw new NexusException( "no generator found for type:" + type );
            } else {
                return idGenerator;
            }
        }
        throw new NexusException( "no id generators found" );
    }

    /**
     * @param type
     * @return
     * @throws NexusException
     */
    public TimestampFormatter getTimestampFormatter( String type ) throws NexusException {

        if ( timestampFormatters != null ) {
            TimestampFormatter formatter = timestampFormatters.get( type );
            if ( formatter == null ) {
                throw new NexusException( "no formatter found for type:" + type );
            } else {
                return formatter;
            }
        }
        throw new NexusException( "no id formatter found" );
    }

    /**
     * @param daoName
     * @return
     */
    public BasicDAO getDao( String daoName ) throws Exception {

        if ( daoName != null ) {
            if ( getBeanFactory().containsBean( daoName ) ) {
                Object daoBean = null;
                try {
                    daoBean = getBeanFactory().getBean( daoName );
                } catch ( Exception e ) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch ( Error e ) {
                    e.printStackTrace();
                }

                if ( daoBean instanceof BasicDAO ) {
                    return (BasicDAO) daoBean;
                } else {
                    throw new NexusException( "invalid Object Type:" + daoBean.getClass().getName() );
                }
            } else {
                throw new InstantiationException( "Requested daoBean: " + daoName + " not found!" );
            }
        }
        return null;
    }

    /**
     * 
     */
    private EngineConfiguration createEngineConfiguration() throws InstantiationException {

        try {
            if ( ( baseConfigurationProvider == null ) && ( baseConfigurationProviderClass != null ) ) {
                try {
                    Class theClass = Class.forName( baseConfigurationProviderClass );
                    baseConfigurationProvider = (BaseConfigurationProvider) theClass.newInstance();
                } catch ( InstantiationException iEx ) {
                    LOG.error( "Base configuration class '" + baseConfigurationProviderClass
                            + "' could not be instantiated: " + iEx );
                }
            }
            EngineConfiguration config = new EngineConfiguration( baseConfigurationProvider );
            config.init();

            return config;
        } catch ( Exception e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch ( Error e ) {
            e.printStackTrace();
        }
        return null;
    }

    public TransactionService getTransactionService() {

        return transactionService;
    }

    /**
     * Gets the current <code>EngineConfiguration</code>. If you want to
     * make changes to the configuration, you should not use the
     * <code>EngineConfiguration</code> directly, but instead use the
     * <code>ConfigurationAccessService</code> returned by the
     * {@link #getActiveConfigurationAccessService()} method.
     * @return The current configuration.
     */
    public EngineConfiguration getCurrentConfiguration() {

        return currentConfiguration;
    }

    /**
     * Sets a new <code>EngineConfiguration</code>. If required, the <code>Engine</code>
     * will be restarted.
     * @param newConfiguration The new configuration to set.
     */
    public synchronized void setCurrentConfiguration( EngineConfiguration newConfiguration ) {

        LOG.trace( "setCurrentConfiguration" );
        if ( this.currentConfiguration != null ) {

            try {
                deactivate();
                teardown();
                LOG.debug( "Save configuration to DB" );
                newConfiguration.saveConfigurationToDB();
                LOG.debug( "initialize new configuration" );
                newConfiguration.init();
                this.currentConfiguration = newConfiguration;
            } catch ( Exception e ) {
                e.printStackTrace();
            }
            LOG.debug( "Initialize Engine" );
            LOG.debug( "1partners: " + newConfiguration.getPartners().size() );
            LOG.debug( "2partners: " + this.currentConfiguration.getPartners().size() );
            initialize( newConfiguration );
        } else {
            this.currentConfiguration = newConfiguration;
        }
        LOG.debug( "3partners: " + this.currentConfiguration.getPartners().size() );
    }

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.BeanFactoryAware#setBeanFactory(org.springframework.beans.factory.BeanFactory)
     */
    public void setBeanFactory( BeanFactory factory ) throws BeansException {

        this.beanFactory = factory;

    }

    /**
     * @return
     */
    public BeanFactory getBeanFactory() {

        return beanFactory;
    }

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.BeanNameAware#setBeanName(java.lang.String)
     */
    public void setBeanName( String beanId ) {

        this.beanId = beanId;

    }

    /**
     * @return
     */
    public String getBeanId() {

        return beanId;
    }

    /**
     * @param beanId
     */
    public void setBeanId( String beanId ) {

        this.beanId = beanId;
    }

    /**
     * @return the status
     */
    public BeanStatus getStatus() {

        return status;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#getRunLevel()
     */
    public Runlevel getActivationRunlevel() {

        return Runlevel.CORE;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#start()
     */
    public void activate() {

        LOG.info( "*** NEXUSe2e Server Version: " + Version.getVersion() + " ***" );
        LOG.debug( "Engine.activate..." );

        if ( currentConfiguration != null ) {
            for ( Runlevel runLevel : Runlevel.values() ) {

                LOG.trace( "Starting run level: " + runLevel );

                for ( Manageable bean : currentConfiguration.getStaticBeanContainer().getManagableBeans().values() ) {
                    if ( runLevel.equals( bean.getActivationRunlevel() ) ) {
                        if ( bean.getStatus().getValue() < BeanStatus.ACTIVATED.getValue() ) {
                            bean.activate();
                            if ( bean instanceof Service ) {
                                Service service = (Service) bean;
                                if ( service.isAutostart() ) {
                                    service.start();
                                }
                            }
                        }
                    }
                }
                
            }

            // Recover any pending messages...
            currentConfiguration.getStaticBeanContainer().getBackendOutboundDispatcher().recoverMessages();

            LOG.info( "***** Nexus E2E engine activated. *****" );
        } else {
            LOG.error( "Failed to start Engine, no configuration found!" );

        }
    } // start

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#deactivate()
     */
    public void deactivate() {

        LOG.debug( "Engine.deactivate..." );
        // new Exception().printStackTrace();

        if ( currentConfiguration != null ) {
            Runlevel[] runlevels = Runlevel.values();
            for ( int i = runlevels.length - 1; i >= 0; i-- ) {
                LOG.trace( "Stopping run level: " + runlevels[i] );
                if ( currentConfiguration.getStaticBeanContainer() != null ) {
                    for ( Manageable bean : currentConfiguration.getStaticBeanContainer().getManagableBeans().values() ) {
                        if ( runlevels[i].equals( bean.getActivationRunlevel() ) ) {
                            if ( bean instanceof Service ) {
                                Service service = (Service) bean;
                                if ( service.getStatus().getValue() > BeanStatus.ACTIVATED.getValue() ) {
                                    service.stop();
                                }
                            }
                            bean.deactivate();
                        }
                    }
                }
            }
        } else {
            LOG.error( "Failed to deactivate Engine, no configuration found!" );
        }

    } // deactivate

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#teardown()
     */
    public void teardown() {

        LOG.debug( "Engine.teardown..." );

        if ( currentConfiguration != null ) {
            if ( currentConfiguration.getStaticBeanContainer() != null ) {
                for ( Manageable bean : currentConfiguration.getStaticBeanContainer().getManagableBeans().values() ) {
                    bean.teardown();
                }
            }
        } else {
            LOG.error( "Failed to teardown Engine, no configuration found!" );
        }

    } // deactivate

    /**
     * Completely shuts down the engine. It shall not be restarted after
     * this method has been called.
     */
    public void shutdown() {

        deactivate();
        teardown();
        try {
            String dialect = localSessionFactoryBean.getHibernateProperties().getProperty( "hibernate.dialect" );
            if ( ( dialect != null ) && dialect.indexOf( "DerbyDialect" ) != -1 ) {
                DriverManager.getConnection( "jdbc:derby:;shutdown=true" );
            } else {
                LOG.info( "No Derby DB!" );
            }
        } catch ( SQLException e ) {
            LOG.info( "Derby DB shut down normally!" );
        } catch ( Exception e ) {
            LOG.error( "Error shutting down Derby DB: " + e );
        }

        LOG.info( "***** Nexus E2E engine deactivated. *****" );
    }

    /**
     * @return the timestampFormaters
     */
    public Map<String, TimestampFormatter> getTimestampFormaters() {

        return timestampFormatters;
    }

    /**
     * @param timestampFormaters the timestampFormaters to set
     */
    public void setTimestampFormaters( Map<String, TimestampFormatter> timestampFormaters ) {

        this.timestampFormatters = timestampFormaters;
    }

    /**
     * @return the idGenrators
     */
    public Map<String, IdGenerator> getIdGenrators() {

        return idGenrators;
    }

    /**
     * @param idGenrators the idGenrators to set
     */
    public void setIdGenrators( Map<String, IdGenerator> idGenrators ) {

        this.idGenrators = idGenrators;
    }

    /**
     * @return the configAccessService
     */
    public ConfigurationAccessService getActiveConfigurationAccessService() {

        return new ConfigurationAccessService( currentConfiguration );
    }

    /**
     * @param key
     * @return
     */
    public ConfigurationAccessService getConfigurationAccessService( Object key ) {

        EngineConfiguration configuration = configurations.get( key );
        if ( configuration == null ) {
            configuration = EngineConfiguration.cloneConfiguration( currentConfiguration );
            configurations.put( key, configuration );
        }
        return new ConfigurationAccessService( configuration );
    }

    public void invalidateConfiguration( Object key ) {

        if ( configurations != null ) {
            configurations.remove( key );
        }
    }

    /**
     * @return the localSessionFactoryBean
     */
    public LocalSessionFactoryBean getLocalSessionFactoryBean() {

        return localSessionFactoryBean;
    }

    /**
     * @param localSessionFactoryBean the localSessionFactoryBean to set
     */
    public void setLocalSessionFactoryBean( LocalSessionFactoryBean localSessionFactoryBean ) {

        this.localSessionFactoryBean = localSessionFactoryBean;
    }

    /**
     * @return the inProcessNEXUSe2eInterface
     */
    public NEXUSe2eInterface getInProcessNEXUSe2eInterface() {

        return inProcessNEXUSe2eInterface;
    }

    /**
     * @param inProcessNEXUSe2eInterface the inProcessNEXUSe2eInterface to set
     */
    public void setInProcessNEXUSe2eInterface( NEXUSe2eInterface inProcessNEXUSe2eInterface ) {

        this.inProcessNEXUSe2eInterface = inProcessNEXUSe2eInterface;
    }

    public BaseConfigurationProvider getBaseConfigurationProvider() {

        return baseConfigurationProvider;
    }

    public void setBaseConfigurationProvider( BaseConfigurationProvider baseConfigurationProvider ) {

        this.baseConfigurationProvider = baseConfigurationProvider;
    }

    /**
     * @return the cacertsPath
     */
    public String getCacertsPath() {

        return cacertsPath;
    }

    /**
     * @param cacertsPath the cacertsPath to set
     */
    public void setCacertsPath( String cacertsPath ) {

        this.cacertsPath = cacertsPath;
    }

    /**
     * @return the certificatePath
     */
    public String getCertificatePath() {

        return certificatePath;
    }

    /**
     * @param certificatePath the certificatePath to set
     */
    public void setCertificatePath( String certificatePath ) {

        this.certificatePath = certificatePath;
    }

    /**
     * @return
     */
    public static String getNexusE2ERoot() {

        return nexusE2ERoot;
    }

    /**
     * @param webApplicationRoot
     */
    public static void setNexusE2ERoot( String webApplicationRoot ) {

        nexusE2ERoot = webApplicationRoot;
    }

    public String getBaseConfigurationProviderClass() {

        return baseConfigurationProviderClass;
    }

    public void setBaseConfigurationProviderClass( String baseConfigurationProviderClass ) {

        this.baseConfigurationProviderClass = baseConfigurationProviderClass;
    }

}
