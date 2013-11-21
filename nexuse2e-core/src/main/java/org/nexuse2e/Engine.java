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
package org.nexuse2e;

import java.io.InputStream;
import java.security.Security;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.FileTypeMap;
import javax.activation.MailcapCommandMap;
import javax.activation.MimetypesFileTypeMap;
import javax.servlet.ServletContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.hibernate.cfg.Configuration;
import org.nexuse2e.configuration.BaseConfigurationProvider;
import org.nexuse2e.configuration.ConfigurationAccessService;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.configuration.IdGenerator;
import org.nexuse2e.configuration.NexusUUIDGenerator;
import org.nexuse2e.configuration.XmlBaseConfigurationProvider;
import org.nexuse2e.controller.TransactionService;
import org.nexuse2e.dao.BasicDAO;
import org.nexuse2e.dao.ConfigDAO;
import org.nexuse2e.integration.NEXUSe2eInterface;
import org.nexuse2e.integration.NEXUSe2eInterfaceImpl;
import org.nexuse2e.messaging.BackendOutboundDispatcher;
import org.nexuse2e.messaging.MessageWorker;
import org.nexuse2e.messaging.TimestampFormatter;
import org.nexuse2e.messaging.ebxml.EBXMLTimestampFormatter;
import org.nexuse2e.messaging.mime.binary_base64;
import org.nexuse2e.service.Service;
import org.nexuse2e.util.CertificateUtil;
import org.nexuse2e.util.XMLUtil;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContextAware;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
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
public class Engine extends WebApplicationObjectSupport implements BeanNameAware, ApplicationContextAware, InitializingBean {

    private static Logger                    LOG                            = Logger.getLogger( Engine.class );

    private static Engine                    instance                       = null;
    private EngineController                 engineController               = null;

    private EngineConfiguration              currentConfiguration;
    private LocalSessionFactoryBean          localSessionFactoryBean        = null;
    private String                           beanId;
    private BeanStatus                       status                         = BeanStatus.UNDEFINED;
    private NEXUSe2eInterface                inProcessNEXUSe2eInterface     = new NEXUSe2eInterfaceImpl();

    private TransactionService               transactionService;
    private ConfigDAO                        configDao;
    
    private MessageWorker                    messageWorker;
    
    private Map<String, IdGenerator>         idGenrators                    = null;

    private Map<String, TimestampFormatter>  timestampFormatters            = null;

    private BaseConfigurationProvider        baseConfigurationProvider      = null;
    private String                           baseConfigurationProviderClass = null;

    private String                           cacertsPath                    = null;
    private String                           certificatePath                = null;
    private String                           nexusE2ERoot                   = null;
    private String                           databaseDialect                = null;

    private Map<Object, EngineConfiguration> configurations                 = new HashMap<Object, EngineConfiguration>();

    /**
     * File type mappings.
     * Keyed by mime type, content is the extension, without the '.'.
     */
    private Map<String, MimeMapping>         mimeMappings                   = new HashMap<String, MimeMapping>();
    private Map<String, MimeMapping>         fileExt2MimeMappings           = new HashMap<String, MimeMapping>();
    private long engineStartTime = 0;
    private long serviceStartTime = 0;
    private String                           timestampPattern               = null;
    private String							 defaultCharEncoding			= null;

    private MimetypesFileTypeMap             mimetypesFileTypeMap           = null;

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
        serviceStartTime = System.currentTimeMillis();
        LOG.trace( "creating engine instance" );
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
        LOG.debug( "EngineId: " + getBeanId() );
    }

    /**
     * @param targetStatus
     * @throws InstantiationException 
     */
    //public synchronized void changeStatus( BeanStatus targetStatus ) throws InstantiationException {
    public void changeStatus( BeanStatus targetStatus ) throws InstantiationException {

        while ( getStatus() != targetStatus && getStatus() != BeanStatus.ERROR ) {
            if ( getStatus().ordinal() < targetStatus.ordinal() ) {
                switch ( getStatus() ) {
                    case UNDEFINED:
                        instantiate();
                        status = BeanStatus.INSTANTIATED;
                        break;
                    case INSTANTIATED:
                        try {
                            initialize();
                            status = BeanStatus.INITIALIZED;
                        } catch ( InstantiationException e ) {
                            status = BeanStatus.ERROR;
                            e.printStackTrace();
                            throw e;
                        }
                        break;
                    case INITIALIZED:
                        activate();
                        status = BeanStatus.ACTIVATED;
                        break;
                    case ACTIVATED:
                        start();
                        status = BeanStatus.STARTED;
                        break;
                    case STARTED:
                        break;
                    case ERROR:
                        break;

                    default:
                        break;
                }
            } else if ( getStatus().ordinal() > targetStatus.ordinal() ) {
                switch ( getStatus() ) {
                    case STARTED:
                        status = BeanStatus.ACTIVATED;
                        stop();
                        break;
                    case ACTIVATED:
                        status = BeanStatus.INITIALIZED;
                        deactivate();
                        break;
                    case INITIALIZED:
                        status = BeanStatus.INSTANTIATED;
                        teardown();
                        break;
                    case INSTANTIATED:
                        break;
                    case UNDEFINED:
                        break;
                    case ERROR:
                        break;

                    default:
                        break;
                }
            }

        }

    }

    public void instantiate() throws InstantiationException {

        LOG.info( "*** NEXUSe2e Server Version: " + Version.getVersion() );

        LOG.info( "*** JRE version is: " + System.getProperty( "java.version" ) );
        LOG.info( "*** Java class path: " + System.getProperty( "java.class.path" ) );
        LOG.info( "*** Java home: " + System.getProperty( "java.home" ) );

        LOG.info( "*** This software is licensed under the GNU Lesser General Public License (LGPL), Version 2.1" );

        // Dump system properties
        Properties properties = System.getProperties();
        for ( Object property : properties.keySet() ) {
            LOG.debug( property + " - " + properties.get( property ) );
        }

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
        } catch ( IllegalStateException isex ) {
            if ( nexusE2ERoot == null ) {
                throw new IllegalStateException( "nexusE2ERoot must be set if not running in a WebApplicationContext" );
            }
        }

        LOG.debug( "NEXUSe2e root directory: " + nexusE2ERoot );
    }

    /**
     * Initialize the engine
     */
    public void initialize() throws InstantiationException {

        try {
            // undefined - no db connection, spring not configured.
            // instanciated - db connected, string config loaded.
            // initialized - engineconfig loaded
            // started - transports and connectors are running.

            try {
                initializeMime();
            } catch ( NexusException nEx ) {
                LOG.error( "No MIME mappings found: " + nEx );
            }

            try {
                // NOTE: & is needed to retrieve the actual bean class and not a proxy!!!
                localSessionFactoryBean = (LocalSessionFactoryBean) getBeanFactory().getBean(
                        "&hibernateSessionFactory" );
                if ( localSessionFactoryBean != null ) {
                    // localSessionFactoryBean.createDatabaseSchema();
//                    localSessionFactoryBean.updateDatabaseSchema();

                    Configuration configuration = localSessionFactoryBean.getConfiguration();
                    String dialect = configuration.getProperty( "hibernate.dialect" );
                    if ( ( dialect != null ) && ( dialect.length() != 0 ) ) {
                        BasicDAO dao = (BasicDAO)getConfigDao();
                        if ( dialect.indexOf( "DB2400" ) != -1 ) {
                            dao.setISeriesServer( true );
                        } else if ( dialect.indexOf( "SQLServer" ) != -1 ) {
                            dao.setMsSqlServer( true );
                        }
                        LOG.info( "DB dialect: " + dialect );
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
            transactionService = (TransactionService)Engine.getInstance().getBeanFactory().getBean( "transactionService" );

            // create new Config
            if ( currentConfiguration == null ) {
                try {
                    createEngineConfiguration();
                } catch ( InstantiationException e ) {
                    LOG.error( "Problem creating Engine configuration, exiting..." );
                    e.printStackTrace();
                    status = BeanStatus.ERROR;
                    return;
                }
            } else {
                currentConfiguration.init();
            }

            // Add transaction service to static beans so its life cycle is managed correctly
            currentConfiguration.getStaticBeanContainer().getManagableBeans().put( Constants.TRANSACTION_SERVICE,
                    transactionService );

            for ( Manageable bean : currentConfiguration.getStaticBeanContainer().getManagableBeans().values() ) {
                LOG.trace( "Initializing bean: " + bean.getClass().getName() );
                
                
                if ( bean.getStatus() == null || bean.getStatus().getValue() < BeanStatus.INITIALIZED.getValue() ) {
                    
                    try {
                        bean.initialize( currentConfiguration );
                    } catch ( Exception e ) {
                        LOG.error( "Error initializing managable bean - " + bean.getClass().getCanonicalName(), e );
                    }
                } else if ( !( bean instanceof org.nexuse2e.logging.LogAppender ) ) {
                    LOG.error( "Bean already initialized: " + bean.getClass().getName() );
                }
            }
        } catch ( RuntimeException rex ) {
            LOG.error( "Error initializing Engine", rex );
            throw new InstantiationException( rex.getMessage() );
        } catch ( Exception e ) {
            LOG.error( "Error initializing Engine", e );
            throw new InstantiationException( e.getMessage() );
        } catch ( Error e ) {
            LOG.error( "Error initializing Engine", e );
            throw new InstantiationException( e.getMessage() );
        }

        LOG.debug( "Engine initialized." );
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
                        setMimeHandler( mimeType, handlerClass );
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

        if ( mimetypesFileTypeMap == null ) {
            mimetypesFileTypeMap = (MimetypesFileTypeMap) FileTypeMap.getDefaultFileTypeMap();
        }
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

                        // add the first extension found to the mimeMappings hash
                        if ( x == 0 ) {
                            addMimeMapping( mimeType, fileType );
                        }

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
     * Inner class to wrap MIME type to handler mappings
     */
    class MimeMapping {

        String mimeType      = null;
        String dataHandler   = null;
        String fileExtension = null;
    }

    /**
     * Retrieve the file extension based on a mime type.
     * @param mimeType
     * @return String Extension
     */
    public String getFileExtensionFromMime( String mimeType ) {

        MimeMapping tempMimeMapping = mimeMappings.get( mimeType );

        if ( tempMimeMapping != null ) {
            return tempMimeMapping.fileExtension;
        }

        return null;
    }

    /**
     * Retrieve the MIME type based on the file extension.
     * @param mimeType
     * @return String Extension
     */
    public String getMimeFromFileName( String fileName ) {

        if ( mimetypesFileTypeMap != null ) {
            return mimetypesFileTypeMap.getContentType( fileName );
        }

        return null;
    }

    /**
     * Retrieve the MIME type based on the file extension.
     * @param mimeType
     * @return String Extension
     */
    public String getMimeFromFileExtension( String fileExtension ) {

        MimeMapping tempMimeMapping = fileExt2MimeMappings.get( fileExtension );

        if ( tempMimeMapping != null ) {
            return tempMimeMapping.fileExtension;
        }

        return null;
    }

    /**
     * Add a mime mapping and it's associated extension.
     * @param mimeType
     * @param extension
     */
    private void addMimeMapping( String newMimeType, String newExtension ) {

        MimeMapping tempMimeMapping = new MimeMapping();

        tempMimeMapping.mimeType = newMimeType;
        tempMimeMapping.fileExtension = newExtension;

        mimeMappings.put( newMimeType, tempMimeMapping );
        fileExt2MimeMappings.put( newExtension, tempMimeMapping );
    }

    /**
     * Set a Mime Handler DataHandler class name.
     * @param newMimeType
     * @param newDataHandlerClass
     */
    private void setMimeHandler( String newMimeType, String newDataHandlerClass ) {

        MimeMapping tempMimeMapping = (MimeMapping) mimeMappings.get( newMimeType );

        if ( tempMimeMapping != null ) {
            tempMimeMapping.dataHandler = newDataHandlerClass;
        }
    }

    /**
     * Used to determine if a given content type is of type Binary.
     */
    public boolean isBinaryType( String contentType ) {

        if (contentType != null) {
            contentType = contentType.toLowerCase().trim();
            
            MimeMapping tempMimeMapping = (MimeMapping) mimeMappings.get( contentType );
    
            if ( tempMimeMapping != null ) {
                if (!tempMimeMapping.dataHandler.equalsIgnoreCase( binary_base64.class.getName() ) ) {
                    return false;
                }
            }
        }

        return true;
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
     * Gets the data access object with the given bean name.
     * @param daoName The bean name.
     * @return The <code>BasicDAO</code> implementation instance, or <code>null</code>
     * if <code>daoName</code> is <code>null</code>.
     * @throws NexusException if the given bean name does not exist or has an invalid type.
     */
//    public BasicDAO getDao( String daoName ) throws NexusException {
//
//        if ( daoName != null ) {
//            if ( getBeanFactory().containsBean( daoName ) ) {
//                Object daoBean = null;
//                daoBean = getBeanFactory().getBean( daoName );
//
//                if ( daoBean instanceof BasicDAO ) {
//                    return (BasicDAO) daoBean;
//                } else {
//                    throw new NexusException( "invalid Object Type:" + daoBean.getClass().getName() );
//                }
//            } else {
//                throw new NexusException( "Requested daoBean: " + daoName + " not found!" );
//            }
//        }
//        return null;
//    }

    /**
     * Convenience method to get the configuration DAO.
     * @return The configuration DAO.
     * @throws NexusException If the configuration DAO is not available.
     */
//    public ConfigDAO getConfigDAO() throws NexusException {
//
//        return (ConfigDAO) getDao( "configDao" );
//    }

    /**
     * Convenience method to get the transaction DAO.
     * @return The transaction DAO.
     * @throws NexusException If the transaction DAO is not available.
     */
//    public TransactionDAO getTransactionDAO() throws NexusException {
//
//        return (TransactionDAO) getDao( "transactionDao" );
//    }

    /**
     * Convenience method to get the log DAO.
     * @return The log DAO.
     * @throws NexusException If the log DAO is not available.
     */
//    public LogDAO getLogDAO() throws NexusException {
//
//        return (LogDAO) getDao( "logDao" );
//    }

//    public PersistentPropertyDAO getPersistentPropertyDAO() throws NexusException {
//
//        return (PersistentPropertyDAO) getDao( "persistentPropertyDao" );
//    }
    
    private void createEngineConfiguration() throws InstantiationException {

        try {
            ConfigDAO configDAO = getConfigDao();
            currentConfiguration = new EngineConfiguration();
            if ( configDAO.isDatabasePopulated() ) {
                configDAO.loadDatafromDB( currentConfiguration );
                currentConfiguration.init();
            } else {
                LOG.info( "Empty database detected, creating and saving base configuration of type: "
                        + baseConfigurationProvider.getClass() );
                if ( ( baseConfigurationProvider == null || !baseConfigurationProvider.isConfigurationAvailable() )
                        && ( baseConfigurationProviderClass != null ) ) {
                    try {
                        Class<?> theClass = Class.forName( baseConfigurationProviderClass );
                        baseConfigurationProvider = (BaseConfigurationProvider) theClass.newInstance();
                    } catch ( InstantiationException iEx ) {
                        LOG.error( "Base configuration class '" + baseConfigurationProviderClass
                                + "' could not be instantiated: " + iEx );
                    }
                }
                if ( baseConfigurationProvider == null || !baseConfigurationProvider.isConfigurationAvailable() ) {
                    String message = "No base configuration available from BaseConfigurationProvider "
                            + baseConfigurationProvider;
                    LOG.error( message );
                    throw new InstantiationException( message );
                }
                currentConfiguration.createBaseConfiguration( baseConfigurationProvider );
                configDAO.saveConfigurationToDB( currentConfiguration );
            }

        } catch ( Exception e ) {
            if ( e instanceof InstantiationException ) {
                throw (InstantiationException) e;
            }
            e.printStackTrace();
        }
    }

    public TransactionService getTransactionService() {

        return transactionService;
    }

    /**
     * Imports the configuration from the given XML input stream and sets it as the
     * current configuration. This method should be called with care because it deletes
     * all configuration data, messages conversations and log records.
     * @param xmlInput The input stream that provides the configuration in XML.
     * @throws NexusException 
     */
    public void importConfiguration( InputStream xmlInput ) throws NexusException {

        BaseConfigurationProvider provider = new XmlBaseConfigurationProvider( xmlInput );

        try {
            ConfigDAO configDao = getConfigDao();
            configDao.deleteAll();
            EngineConfiguration newConfig = new EngineConfiguration();
            newConfig.createBaseConfiguration( provider );
            configDao.saveConfigurationToDB( newConfig );
            newConfig.init();
            setCurrentConfiguration( newConfig );
        } catch ( Exception ex ) {
            if ( ex instanceof NexusException ) {
                throw (NexusException) ex;
            }
            throw new NexusException( ex );
        }
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
                changeStatus( BeanStatus.INSTANTIATED );

                // try to fix ERROR status
                if (getStatus() == BeanStatus.ERROR) {
                    status = BeanStatus.INSTANTIATED;
                }
                LOG.debug( "Saving configuration..." );
                getConfigDao().saveDelta( newConfiguration );
                //                if ( oldServicePojo != null ) {
                //                    service = getService( oldServicePojo.getName() );
                //                    services.remove( oldServicePojo );
                //                    // service has been renamed
                //                    if ( !oldServicePojo.getName().equals( servicePojo.getName() ) ) {
                //                        renameService( oldServicePojo.getName(), servicePojo.getName() );
                //                    }
                //                    if ( servicePojo.getServiceParams() != null ) {
                //                        ConfigurationUtil.configureService( service, servicePojo.getServiceParams() );
                //                    }
                //                } else {
                //                    service = (Service) Class.forName( servicePojo.getComponent().getClassName() ).newInstance();
                //                    if ( servicePojo.getServiceParams() != null ) {
                //                        ConfigurationUtil.configureService( service, servicePojo.getServiceParams() );
                //                    }
                //                    service.initialize( this );
                //                    service.activate();
                //                    if ( service.isAutostart() ) {
                //                        service.start();
                //                    }
                //                    getStaticBeanContainer().getManagableBeans().put( servicePojo.getName(), service );
                //                }
            } catch ( Exception e ) {
                LOG.error( "Error saving configuration: " + e );
                e.printStackTrace();
            }
            try {
                LOG.debug( "Re-load  configuration to make sure it's consistent" );
                getConfigDao().loadDatafromDB( newConfiguration );
            } catch ( Exception e ) {
                LOG.error( "Error loading configuration: " + e );
                e.printStackTrace();
            }
            try {
                LOG.debug( "Initialize new configuration" );
                this.currentConfiguration = newConfiguration;
                // newConfiguration.init();
            } catch ( Exception e ) {
                LOG.error( "Error initializing configuration: " + e );
                e.printStackTrace();
            }
            LOG.debug( "Initialize Engine" );
            try {
                changeStatus( BeanStatus.STARTED );
            } catch ( InstantiationException e ) {
                e.printStackTrace();
                LOG.error( "Error setting new configuartion: " + e );
            }
        } else {
            this.currentConfiguration = newConfiguration;
        }
        if ( configurations != null ) {
            configurations.clear();
        }
        // LOG.debug( "3partners: " + this.currentConfiguration.getPartners().size() );
    }

    /**
     * @return
     */
    public BeanFactory getBeanFactory() {

        return getWebApplicationContext();
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
    public Layer getActivationLayer() {

        return Layer.CORE;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#start()
     */
    public void start() {
        
        LOG.info( "*** NEXUSe2e Server Version: " + Version.getVersion() + " ***" );
        LOG.debug( "Engine.start..." );

        if ( currentConfiguration != null ) {
            for ( Layer layer : Layer.values() ) {

                LOG.trace( "Starting layer: " + layer );

                for ( Manageable bean : currentConfiguration.getStaticBeanContainer().getManagableBeans().values() ) {
                    if ( layer.equals( bean.getActivationLayer() ) ) {
                        if ( bean instanceof Service ) {
                            Service service = (Service) bean;
                            if ( service.isAutostart() ) {
                                service.start();
                            }
                        }

                    }
                }

            }

            // Recover any pending messages...
            BackendOutboundDispatcher backendOutboundDispatcher = currentConfiguration.getStaticBeanContainer().getBackendOutboundDispatcher();
            if (backendOutboundDispatcher != null) {
                backendOutboundDispatcher.recoverMessages();
            }
            engineStartTime = System.currentTimeMillis();
            
            LOG.info( "***** Nexus E2E engine started. *****" );
        } else {
            LOG.error( "Failed to start Engine, no configuration found!" );

        }
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#start()
     */
    public void activate() {

        LOG.info( "*** NEXUSe2e Server Version: " + Version.getVersion() + " ***" );
        LOG.debug( "Engine.activate..." );

        if ( currentConfiguration != null ) {
            for ( Layer layer : Layer.values() ) {

                LOG.trace( "activation layer: " + layer );

                for ( Manageable bean : currentConfiguration.getStaticBeanContainer().getManagableBeans().values() ) {
                    if ( layer.equals( bean.getActivationLayer() ) ) {
                        if ( bean.getStatus().getValue() < BeanStatus.ACTIVATED.getValue() ) {
                            try {
                                bean.activate();
                            } catch ( Exception e ) {
                                LOG.error( "Could not activate bean: " + bean.getClass().getCanonicalName() );
                            }
                        }
                    }
                }

            }

            LOG.info( "***** Nexus E2E engine activated. *****" );
        } else {
            LOG.error( "Failed to start Engine, no configuration found!" );

        }
    }

    /**
     * 
     */
    public void stop() {

        LOG.debug( "Engine.stop..." );

        if ( currentConfiguration != null ) {
            Layer[] layers = Layer.values();
            for ( int i = layers.length - 1; i >= 0; i-- ) {
                LOG.trace( "Stopping layer: " + layers[i] );
                if ( currentConfiguration.getStaticBeanContainer() != null ) {
                    for ( Manageable bean : currentConfiguration.getStaticBeanContainer().getManagableBeans().values() ) {
                        if ( layers[i].equals( bean.getActivationLayer() ) ) {
                            if ( bean instanceof Service ) {
                                Service service = (Service) bean;
                                service.stop();
                            }
                        }
                    }
                }
            }
        } else {
            LOG.error( "Failed to deactivate Engine, no configuration found!" );
        }

    } // deactivate

    /**
     * 
     */
    public void deactivate() {

        LOG.debug( "Engine.deactivate..." );

        if ( currentConfiguration != null ) {
            Layer[] runlevels = Layer.values();
            for ( int i = runlevels.length - 1; i >= 0; i-- ) {
                LOG.trace( "deactivating layer: " + runlevels[i] );
                if ( currentConfiguration.getStaticBeanContainer() != null ) {
                    for ( Manageable bean : currentConfiguration.getStaticBeanContainer().getManagableBeans().values() ) {
                        if ( runlevels[i].equals( bean.getActivationLayer() ) ) {
                            bean.deactivate();
                            LOG.trace( "Bean status: " + bean.getClass().getCanonicalName() + " - " + bean.getStatus() );
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
        currentConfiguration = null;
        invalidateConfigurations();

    } // deactivate

    /**
     * Completely shuts down the engine. It shall not be restarted after
     * this method has been called.
     */
    public void shutdown() {

        // new Exception().printStackTrace();

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
    public void setTimestampFormatters( Map<String, TimestampFormatter> timestampFormaters ) {

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

        return currentConfiguration;
    }

    /**
     * Gets an <code>EngineConfiguration</code> for the specified key.
     * If no engine configuration exists for the given key, a copy of the current
     * configuration will be created and associated with the key.
     * @param key The key. Can be any object with proper hashValue() and equals()
     * method implementations.
     * @return An <code>EngineConfiguration</code> object. Not <code>null</code>.
     */
    public EngineConfiguration getConfiguration( Object key ) {

        EngineConfiguration configuration = configurations.get( key );
        if ( configuration == null ) {
            configuration = new EngineConfiguration( currentConfiguration );
            configurations.put( key, configuration );
        }
        return configuration;
    }
    /**
     * Detaches all configurations.
     * @param key The key.
     */
    public void invalidateConfigurations() {

        if ( configurations != null ) {
            configurations.clear();
        }
    }
    /**
     * Detaches the engine configuration that is associated with the given key.
     * @param key The key.
     */
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
    public String getNexusE2ERoot() {

        return nexusE2ERoot;
    }

    /**
     * @param webApplicationRoot
     */
    public void setNexusE2ERoot( String webApplicationRoot ) {

        nexusE2ERoot = webApplicationRoot;
    }

    public String getBaseConfigurationProviderClass() {

        return baseConfigurationProviderClass;
    }

    public void setBaseConfigurationProviderClass( String baseConfigurationProviderClass ) {

        this.baseConfigurationProviderClass = baseConfigurationProviderClass;
    }

    /**
     * @return
     */
    public EngineController getEngineController() {

        return engineController;
    }

    /**
     * @param engineController
     */
    public void setEngineController( EngineController engineController ) {

        this.engineController = engineController;
    }

    /**
     * @return the timestampPattern
     */
    public String getTimestampPattern() {

        return timestampPattern;
    }

    /**
     * @param timestampPattern the timestampPattern to set
     */
    public void setTimestampPattern( String timestampPattern ) {

        this.timestampPattern = timestampPattern;
    }

    /**
     * Gets the hibernate database dialect class name.
     * @return The hibernate db dialect class name.
     */
    public String getDatabaseDialect() {

        return databaseDialect;
    }

    /**
     * Sets the hibernate database dialect class name.
     * @param The hibernate db dialect class name to set.
     */
    public void setDatabaseDialect( String databaseDialect ) {

        this.databaseDialect = databaseDialect;
    }

    
    /**
     * @return the engineStartTime
     */
    public long getEngineStartTime() {
    
        return engineStartTime;
    }

    
    /**
     * @param engineStartTime the engineStartTime to set
     */
    public void setEngineStartTime( long engineStartTime ) {
    
        this.engineStartTime = engineStartTime;
    }

    
    /**
     * @return the serviceStartTime
     */
    public long getServiceStartTime() {
    
        return serviceStartTime;
    }

    
    /**
     * @param serviceStartTime the serviceStartTime to set
     */
    public void setServiceStartTime( long serviceStartTime ) {
    
        this.serviceStartTime = serviceStartTime;
    }

    
    /**
     * @return the configDao
     */
    public ConfigDAO getConfigDao() {
    
        return configDao;
    }

    
    /**
     * @param configDao the configDao to set
     */
    public void setConfigDao( ConfigDAO configDao ) {
    
        this.configDao = configDao;
    }

    
    /**
     * @param transactionService the transactionService to set
     */
    public void setTransactionService( TransactionService transactionService ) {
    
        this.transactionService = transactionService;
    }
    
    /**
     * Gets the <code>MessageWorker</code> implementation that is used
     * by this engine.
     * @return The <code>MessageWorker</code> implementation. Caller can assume that
     * a non-<code>null</code> value is returned if engine is configured properly..
     */
    public MessageWorker getMessageWorker() {
        return messageWorker;
    }
    
    /**
     * Sets the <code>MessageWorker</code> used by this engine.
     * @param messageWorker The message worker implementation to be used.
     * Shall not be <code>null</code>.
     */
    public void setMessageWorker(MessageWorker messageWorker) {
        this.messageWorker = messageWorker;
    }

	/**
	 * @return the defaultCharEncoding
	 */
	public String getDefaultCharEncoding() {
		if(StringUtils.isEmpty(defaultCharEncoding)) {
			defaultCharEncoding = java.nio.charset.Charset.defaultCharset().name();
		}
		return defaultCharEncoding;
	}

	/**
	 * @param defaultCharEncoding the defaultCharEncoding to set
	 */
	public void setDefaultCharEncoding(String defaultCharEncoding) {
		this.defaultCharEncoding = defaultCharEncoding;
	}

} // Engine
