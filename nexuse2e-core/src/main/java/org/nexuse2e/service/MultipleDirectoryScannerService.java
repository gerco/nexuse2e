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
package org.nexuse2e.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.activation.FileTypeMap;
import javax.activation.MimetypesFileTypeMap;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.nexuse2e.BeanStatus;
import org.nexuse2e.Constants;
import org.nexuse2e.Engine;
import org.nexuse2e.Layer;
import org.nexuse2e.NexusException;
import org.nexuse2e.backend.BackendPipelineDispatcher;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.ParameterType;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.pojo.MessagePayloadPojo;
import org.nexuse2e.pojo.MessagePojo;
import org.nexuse2e.tools.mapping.xmldata.MappingDefinition;
import org.nexuse2e.transport.TransportReceiver;
import org.w3c.dom.Document;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This service can scan many directories in the file system for files to process.
 * Find the configuration file spec in dir_scanner_config.xsd in the project source.
 * 
 * Example:
 * <pre>
 * <?xml version="1.0" encoding="UTF-8"?>
 *	<DirectoryScannerConfig xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
 *	 xsi:noNamespaceSchemaLocation="dir_scanner_config.xsd">
 *	    <Scanner
 *			<!-- The directory to scan for files -->
 *	        dir="/var/backup/order_out"
 *			<!-- The directory to backup files to (optional) -->
 *	        backupDir="/var/exchange/order_out"
 *			<!-- Regular expression (java.util.regex.Pattern style) file pattern (optional) -->
 *	        filePattern="ORDER.+[0-9]{8}\.xml"
 *          <!-- If true, assemble as single message from all files found in one scanning step, instead of one message per file (optional; default is false) -->
 *          isMultiPayloadAssemblingEnabled="true"
 *			<!-- Interval inbetween directory scans (millseconds, optional, default is 10000) -->
 *	        interval="15000"
 *			<!-- The partner to send the message -->
 *	        partnerId="Pollux"
 *			<!-- The choreography to use -->
 *	        choreographyId="GenericFile"
 *			<!-- The action to trigger -->
 *	        actionId="SendFile"
 *			<!-- The XPATH statement to create the conversation ID to use (optional) -->
 *			conversationXPath="//order/@orderId"
 *	        <!-- The mapping and conversion service (optional) -->
 *			mappingServiceName="MyMappingService"
 *          <!-- The direction of new messages (optional; default is "outbound").
 *           If value is "outbound", the message will be dispatched to the outbound backend dispatcher.
 *           If value is "inbound", the message will be dispatched to a given instance of ReceiverAware interface.
 *          -->
 *          direction="outbound"
 *	    />
 *	    <Scanner
 *	        <!-- The directory to scan for files -->
 *	        dir="/var/backup/order_change_out"
 *			<!-- The directory to backup files to (optional) -->
 *	        backupDir="/var/exchange/order_change_out"
 *			<!-- The partner to send the message -->
 *	        partnerId="Pollux"
 *			<!-- The choreography to use -->
 *	        choreographyId="GenericFile"
 *			<!-- The action to trigger -->
 *	        actionId="SendFile"
 *	    />
 *		<!-- define as much scanners as you like -->
 *	</DirectoryScannerConfig>
 * </pre>
 * 
 * @author s_schulze
 */
public class MultipleDirectoryScannerService extends AbstractService implements ReceiverAware {


	private static final Logger        LOG                        = Logger.getLogger( MultipleDirectoryScannerService.class );
    
    public static final String        CONFIG_FILE                 = "config_file";
    public final static String        SCHEDULING_SERVICE          = "scheduling_service";
    public final static String        ASSEMBLE_MULTI_PAYLOAD_MSG  = "assemble_multi_payload_msg";
    
    public static final long		  DEFAULT_INTERVAL			  = 10000;
    
    protected SchedulingService         schedulingService         = null;
    
    protected List<DirectoryScanner>	directoryScanners		  = null;
    
    protected TransportReceiver         transportReceiver         = null;
    
    /**
     * Initializes the {@link MultipleDirectoryScannerService}.
     */
    public MultipleDirectoryScannerService() {
    	super();
    	directoryScanners = new ArrayList<DirectoryScanner>();
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.service.AbstractService#fillParameterMap(java.util.Map)
     */
    @Override
    public void fillParameterMap( Map<String, ParameterDescriptor> parameterMap ) {

        parameterMap.put( CONFIG_FILE, new ParameterDescriptor( ParameterType.STRING, "Configuration file",
                "The XML file that contains the configuration for this service.", "" ) );
        parameterMap.put( SCHEDULING_SERVICE, new ParameterDescriptor( ParameterType.SERVICE, "Scheduling Service",
                "The name of the service that shall be used for scheduling.", SchedulingService.class ) );
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.service.AbstractService#getActivationRunlevel()
     */
    @Override
    public Layer getActivationLayer() {

        return Layer.OUTBOUND_PIPELINES;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.service.AbstractService#initialize(org.nexuse2e.configuration.EngineConfiguration)
     */
    @Override
    public void initialize( EngineConfiguration config ) throws InstantiationException {

        if ( LOG.isTraceEnabled() ) {
        	LOG.trace( "initializing" );
        }
        
        String schedulingServiceName = getParameter( SCHEDULING_SERVICE );
        
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

        final BackendPipelineDispatcher backendPipelineDispatcher = (BackendPipelineDispatcher) Engine.getInstance().getCurrentConfiguration()
                .getStaticBeanContainer().getBackendPipelineDispatcher();
        
        // load config file
        String configFileName = getParameter( CONFIG_FILE );
	    if ( ( configFileName != null ) && ( configFileName.length() != 0 ) ) {
			File configFile = new File( configFileName );
			if ( !configFile.exists() || !configFile.isFile() ) {
				status = BeanStatus.ERROR;
				LOG.error( "Cannot read configuration file '" + configFileName + "'!" );
			}
			
			// init directory scanners
		    DefaultHandler handler = new DefaultHandler() {

				private int scannerCounter = 0;
		    	
		    	/* (non-Javadoc)
				 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
				 */
				@Override
				public void startElement(String uri, String localName,
						String name, Attributes attributes) throws SAXException {
					if ( "Scanner".equals( name ) && attributes != null ) {
					    if ( LOG.isTraceEnabled() ) {
					        LOG.trace( "Scanner attributes: " + attributes );
					    }
						scannerCounter++;
						String intervalString = attributes.getValue( "", "interval" );
						long interval = DEFAULT_INTERVAL;
						try {
							interval = Long.parseLong( intervalString );
						} catch ( NumberFormatException e ) {
							LOG.warn( "Cannot parse scheduling interval '" + intervalString + "'. Long value expected. Falling back to default: " + interval + ".", e );
						}
						try {
							MultipleDirectoryScannerService.this.directoryScanners.add(
									new DirectoryScanner(
											attributes.getValue( "", "dir" ),
											attributes.getValue( "", "backupDir" ),
											attributes.getValue( "", "filePattern" ),
											Boolean.parseBoolean( attributes.getValue( "", "isMultiPayloadAssemblingEnabled" ) ),
											interval,
											attributes.getValue( "", "partnerId" ),
											attributes.getValue( "", "choreographyId" ),
											attributes.getValue( "", "actionId" ),
											attributes.getValue( "", "conversationXPath" ),
											attributes.getValue( "", "mappingServiceName" ),
											attributes.getValue( "", "direction" ),
											backendPipelineDispatcher ) );
						} catch ( IllegalArgumentException e ) {
							LOG.error( "Cannot initialize directory scanner number " + scannerCounter + " defined in the config file. Please check your configuration.");
						}
					}
				}
				
		    };
		    // Validate and parse the input
		    try {
			    SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
			    InputStream schemaData = this.getClass().getClassLoader().getResourceAsStream( "org/nexuse2e/service/dir_scanner_config.xsd" );
			    SchemaFactory schemaFactory = SchemaFactory.newInstance( XMLConstants.W3C_XML_SCHEMA_NS_URI );
			    saxParserFactory.setSchema( schemaFactory.newSchema( new StreamSource( schemaData ) ) );
			    SAXParser saxParser = saxParserFactory.newSAXParser();
			    saxParser.parse( configFile, handler );
		    } catch ( SAXException e ) {
		    	LOG.error( "Cannot parse configuration file '" + configFileName + "'. Check syntax!", e );
		    } catch ( ParserConfigurationException e ) {
		    	LOG.error( "Cannot parse configuration file '" + configFileName + "'.", e );
		    } catch ( IOException e ) {
		    	LOG.error( "Cannot parse configuration file '" + configFileName + "'.", e );
		    }
			
		} else {
			status = BeanStatus.ERROR;
			LOG.error( "No configuration file set!" );
		}

        super.initialize( config );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.service.AbstractService#start()
     */
    @Override
    public void start() {
    	
    	// start scanners
    	if ( schedulingService != null ) {
	    	for ( DirectoryScanner currDirScanner : directoryScanners ) {
	    		LOG.trace( "starting" );
	    			schedulingService.registerClient( currDirScanner, currDirScanner.getInterval() );
	    	}
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
    	
    	// stop all scanners
    	LOG.trace( "stopping" );
    	if ( schedulingService != null ) {
	        for ( DirectoryScanner currDirScanner : directoryScanners ) {
	        	schedulingService.deregisterClient( currDirScanner );
	        }
	        super.stop();
    	} else {
    		LOG.error( "No scheduling service configured!" );
    	}
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.service.ReceiverAware#getTransportReceiver()
     */
    public TransportReceiver getTransportReceiver() {
        return transportReceiver;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.service.ReceiverAware#setTransportReceiver(org.nexuse2e.transport.TransportReceiver)
     */
    public void setTransportReceiver( TransportReceiver transportReceiver ) {
        this.transportReceiver = transportReceiver;
        
    }

    protected class DirectoryScanner implements SchedulerClient {
    	// Time to wait after retrieving the list of files to send
        // This is to avoid timing problems when files are still being written
        private static final int          DIRECTORY_LISTING_WAIT_TIME = 5000;
        
        public static final String        DIRECTORY                   = "directory";
        public static final String        BACKUP_DIRECTORY            = "backup_directory";
        public static final String        CHOREOGRAPHY                = "choreography";
        public static final String        CONVERSATION                = "conversation";
        public static final String        ACTION                      = "action";
        public static final String        PARTNER                     = "partner";
        public final static String        INTERVAL                    = "interval";
        public final static String        FILTER                      = "filter";
        public static final String        MAPPING_SERVICE             = "mapping_service";

        // Directory to scan.
        private String                    directory                   = null;
        // Backup directory.
        private String                    backupDirectory             = null;
        // Scan interval in milliseconds.
        private long                      interval                    = 10000;

        private String                    partnerId                   = null;
        private String                    choreographyId              = null;
        private String                    actionId                    = null;
        private String                    conversationStatement       = null;
        private FilenameFilter			  filenameFilter			  = null;
        
        private DataConversionService     mappingService              = null;
        
        private String                    direction                   = null;

        private BackendPipelineDispatcher backendPipelineDispatcher;
        
        protected boolean                 isMultiPayloadAssemblingEnabled = false;
        
        /**
         * Initializes a new directory scanner.
         * @param dir
         * @param backupDir
         * @param interval in milliseconds
         * @param partnerId
         * @param choreographyId
         * @param actionId
         * @param conversationXPath
         * @param filePattern
         * @param isMultiPayloadAssemblingEnabled
         * @param mappingService
         * @param direction
         * @param backendPipelineDispatcher
         */
        protected DirectoryScanner( String dir,
        							String backupDir,
        							String filePattern,
        							boolean isMultiPayloadAssemblingEnabled,
        							long interval,
        							String partnerId,
        							String choreographyId,
        							String actionId,
        							String conversationXPath,
        							String mappingServiceName,
        							String direction,
        							BackendPipelineDispatcher backendPipelineDispatcher ) {
        	this.directory = dir;
        	this.backupDirectory = backupDir;
        	if ( filePattern != null && filePattern.trim().length() > 0 ) {
        		this.filenameFilter = new RegexFilenameFilter( filePattern );
        	}
        	this.isMultiPayloadAssemblingEnabled = isMultiPayloadAssemblingEnabled;
        	this.interval = interval;
        	this.partnerId = partnerId;
        	this.choreographyId = choreographyId;
        	this.actionId = actionId;
        	this.conversationStatement = conversationXPath;
        	if ( !StringUtils.isEmpty( mappingServiceName ) ) {
                Service service = Engine.getInstance().getActiveConfigurationAccessService().getService( mappingServiceName );
                if ( service != null && service instanceof DataConversionService ) {
                    mappingService = (DataConversionService) service;
                } else {
                	MultipleDirectoryScannerService.LOG.error( "The selected service name '" + mappingServiceName + "' references no valid data conversion service." );
                }
            } else {
                MultipleDirectoryScannerService.LOG.info( "No mapping service configured" );
            }
        	this.direction = direction;
        	this.backendPipelineDispatcher = backendPipelineDispatcher;
        	
        	// plausability check
        	if ( dir == null ) {
        		throw new IllegalArgumentException( "Parameter 'dir' must contain a valid directory name. Please check configuration." ); 
        	}
        	if ( partnerId == null || partnerId.length() == 0
        			|| choreographyId == null || choreographyId.length() == 0
        			|| actionId == null || actionId.length() == 0 ) {
        		throw new IllegalArgumentException( "Parameters 'partnerId', 'choreographyId', and 'actionId' must not be empty. Please check configuration." );
        	}
        }
        
        public long getInterval() {
        	return interval;
        }

        public void scheduleNotify() {

            if ( MultipleDirectoryScannerService.LOG.isTraceEnabled() ) {
            	MultipleDirectoryScannerService.LOG.trace( "Scanning directory " + directory + " ..." );
            }

            try {
                File scanDirFile = new File( directory );
                if ( !scanDirFile.isDirectory() ) {
                    MultipleDirectoryScannerService.LOG.error( "Scan directory parameter not pointing to a valid directory: " + directory );
                    return;
                }
                File[] files = null;
                if ( filenameFilter != null ) {
                    files = scanDirFile.listFiles( filenameFilter );
                } else {
                    files = scanDirFile.listFiles();
                }

                // Work around for timing problems (Growmark)
                // Wait a few seconds to give other processes a change to finish writing the file
                try {
                    // System.out.println("Waiting " + DIRECTORY_LISTING_WAIT_TIME + " ms for files to be completely written!");
                    Thread.sleep( DIRECTORY_LISTING_WAIT_TIME );
                } catch ( InterruptedException e ) {
                    MultipleDirectoryScannerService.LOG.error( "Waiting thread was interrupted", e );
                }

                List<String> multiPayloadFilePaths = new ArrayList<String>();
                
                for ( int i = 0; i < files.length; i++ ) {
                    if ( files[i].isFile() && files[i].canWrite()
                            && ( files[i].length() != 0 /* work around for files not completely ready/empty */) ) {
                        try {
                            if ( isMultiPayloadAssemblingEnabled ) {
                                MultipleDirectoryScannerService.LOG.trace( "Processing file as multi payload: " + files[i].getAbsoluteFile() );
                                multiPayloadFilePaths.add( files[i].getAbsolutePath() );
                            } else {
                                MultipleDirectoryScannerService.LOG.trace( "Processing file: " + files[i].getAbsoluteFile() );
                                processFiles( Collections.singletonList( files[i].getAbsolutePath() ) );
                            }
                        } catch ( Exception ex ) {
                            MultipleDirectoryScannerService.LOG.error( "Exception submitting file", ex );
                        }
                    } else {
                        MultipleDirectoryScannerService.LOG.trace( "Skipping file: " + files[i].getAbsoluteFile() );
                    }
                }
                
                if ( isMultiPayloadAssemblingEnabled && multiPayloadFilePaths.size() > 0 ) {
                    // process all files at once
                    processFiles( multiPayloadFilePaths );
                }
            } catch ( Exception ioEx ) {
                MultipleDirectoryScannerService.LOG.error( "Error reading directory", ioEx );
            }

        } // scheduleNotify

        /**
         * Process a list of files found and assemble a multi payload message.
         * @param files List of files to process as message payloads.
         */
        protected void processFiles( List<String> files ) {
            List<MessagePayloadPojo> payloads = new ArrayList<MessagePayloadPojo>();
            
            try {
                Date creationDate = new Date();
                
                // only construct a message, if there is at least one file for payload
                if ( files != null ) {
                    for ( String currFile : files ) {
                        byte[] currBuff = getBytesAndBackup( currFile );
                        if ( currBuff != null ) {
                            MessagePayloadPojo currPayload = new MessagePayloadPojo();
                            currPayload.setPayloadData( currBuff );
                            File currFileObj = new File( currFile );
                            currPayload.setContentId( currFileObj.getName() );
                            currPayload.setCreatedDate( creationDate );
                            
                            MimetypesFileTypeMap mimetypesFileTypeMap = (MimetypesFileTypeMap) FileTypeMap.getDefaultFileTypeMap();
                            String mimeType = mimetypesFileTypeMap.getContentType( currFileObj );
                            currPayload.setMimeType( mimeType != null ? mimeType : "unknown" );
                                                        
                            payloads.add( currPayload );
                        }
                    }
                    
                    String currConversationId = null;
                    String currPartnerId = partnerId;
                    String currChoreographyId = choreographyId;
                    String currActionId = actionId;
                    String currLabel = null;
                    try {
                        // the following block is legacy code, that works only in case of one file per message
                        if ( files.size() == 1 ) {
                            Map<String, String> variables = getVariables( files.get( 0 ) );
                            
                            currLabel = org.nexuse2e.Constants.NX_LABEL_FILE_NAME + "|" + variables.get( "$filename" );

                            if ( partnerId.startsWith( "${" ) ) {
                                if ( mappingService == null ) {
                                    MultipleDirectoryScannerService.LOG
                                            .error( "no valid Mapping service configured. Mapping and conversion features are not available!" );
                                } else {
                                    MappingDefinition mappingDef = new MappingDefinition();
                                    mappingDef.setCommand( partnerId );
                                    currPartnerId = mappingService.processConversion( null, null, null, mappingDef, variables );
                                }
                            }

                            if ( choreographyId.startsWith( "${" ) ) {
                                if ( mappingService == null ) {
                                    MultipleDirectoryScannerService.LOG
                                            .error( "no valid Mapping service configured. Mapping and conversion features are not available!" );
                                } else {
                                    MappingDefinition mappingDef = new MappingDefinition();
                                    mappingDef.setCommand( choreographyId );
                                    currChoreographyId = mappingService.processConversion( null, null, null, mappingDef, variables );
                                }
                            }

                            if ( actionId.startsWith( "${" ) ) {
                                if ( mappingService == null ) {
                                    MultipleDirectoryScannerService.LOG
                                            .error( "no valid Mapping service configured. Mapping and conversion features are not available!" );
                                } else {
                                    MappingDefinition mappingDef = new MappingDefinition();
                                    mappingDef.setCommand( actionId );
                                    currActionId = mappingService.processConversion( null, null, null, mappingDef, variables );
                                }
                            }
                            
                            if ( !StringUtils.isEmpty( conversationStatement ) ) {
                                ByteArrayInputStream bais = new ByteArrayInputStream( payloads.get( 0 ).getPayloadData() );

                                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                                DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
                                Document document = builder.parse( bais );

                                XPath xPathObj = XPathFactory.newInstance().newXPath();
                                Object xPathResult = xPathObj.evaluate( conversationStatement, document, XPathConstants.STRING );
                                currConversationId = (String)xPathResult;
                            }
                        } // end of legacy code
                        /* mapping file names to actionId, choreographyId, partnerId,
                         * or conversationId makes no sense with in multi payload mode,
                         * because you we do not now which of the files is crucial */
                        if ( "inbound".equalsIgnoreCase( direction ) ) {
                            if ( MultipleDirectoryScannerService.this.transportReceiver != null ) {
                                // init msg context
                                MessageContext messageContext = new MessageContext();
                                // init msg
                                MessagePojo message = new MessagePojo();
                                message.setCreatedDate( creationDate );
                                message.setOutbound( false );
                                message.setType( org.nexuse2e.messaging.Constants.INT_MESSAGE_TYPE_NORMAL );
                                for ( MessagePayloadPojo currPayload : payloads ) {
                                    currPayload.setMessage( message );
                                }
                                message.setMessagePayloads( payloads );
                                Engine.getInstance().getTransactionService().initializeMessage(
                                    message,
                                    Engine.getInstance().getIdGenerator( Constants.ID_GENERATOR_MESSAGE ).getId(),
                                    currConversationId != null ? currConversationId : Engine.getInstance().getIdGenerator( Constants.ID_GENERATOR_CONVERSATION ).getId(),
                                    currActionId,
                                    currPartnerId,
                                    currChoreographyId );
                                // putting things together
                                messageContext.setMessagePojo( message );
                                messageContext.setOriginalMessagePojo( message );
                                // flush
                                MultipleDirectoryScannerService.this.transportReceiver.processMessage( messageContext );
                            } else {
                                throw new NexusException(
                                    "Missing TransportReceiver for directory scanner with direction\"inbound\". Please check configuration." );
                            }
                        } else { // default
                            if ( backendPipelineDispatcher != null ) {
                                backendPipelineDispatcher.processMessage( currPartnerId,
                                                                          currChoreographyId,
                                                                          currActionId,
                                                                          currConversationId, // could be null
                                                                          null,
                                                                          currLabel, // could be null
                                                                          null,
                                                                          payloads,
                                                                          null );
                            } else {
                                throw new NexusException(
                                    "Missing BackendPipelineDispatcher for directory scanner with direction\"outbound\". Please check configuration." );
                            }
                        }
                        // Remove files from the file system.
                        for ( String currFile : files ) {
                            deleteFile( currFile );
                        }
                    } catch ( NexusException e ) {
                        // build list of affected files
                        StringBuilder sb = new StringBuilder();
                        for ( String currFile : files ) {
                            sb.append( currFile + File.pathSeparator );
                        }
                        MultipleDirectoryScannerService.LOG.error( "An exception occured while creating messages for files " + sb.toString()
                            + ". Files will not be deleted, but have been copied to the backup directory already.", e );
                    }
                }
            } catch ( Exception e ) {
                // build list of affected files
                StringBuilder sb = new StringBuilder();
                for ( String currFile : files ) {
                    sb.append( currFile + File.pathSeparator );
                }
                // chop last pathSeperator
                sb.deleteCharAt( sb.length() - 1 );
                MultipleDirectoryScannerService.LOG.error( "An exception occured, thus will not create messages for files " + sb.toString()
                    + ". Possibly some of the files were already copied to the backup directory.", e );
            }
        }
        
        /**
         * Reads the bytes of the give file, if it exists, and backups it, if a backup directory is specified.
         * @param filePath The absolute path to the file
         * @return The bytes of the file or <code>null</code>, if it does not exist.
         * @throws IOException, if the file cannot be read.
         */
        private byte[] getBytesAndBackup( String filePath ) throws IOException {
            byte[] fileBuffer = null;
            
            if ( ( filePath != null ) && ( filePath.length() != 0 ) ) {
                // Open the file to read one line at a time
                BufferedInputStream bufferedInputStream = new BufferedInputStream( new FileInputStream( filePath ) );

                // Determine the size of the file
                int fileSize = bufferedInputStream.available();
                fileBuffer = new byte[fileSize]; // Create a buffer that will hold the data from the file

                bufferedInputStream.read( fileBuffer, 0, fileSize ); // Read the file content into the buffer
                bufferedInputStream.close();

                if ( ( backupDirectory != null ) && ( backupDirectory.length() != 0 ) ) {
                    backupFile( filePath, fileBuffer );
                }
            }
            
            return fileBuffer;
        }
        
        /*
         * Helper method that creates variables for the mapping service.
         * @param filePath The path of the file.
         * @return A map containing <code>$filename</code>, and <code>$pathname</code>
         *         assigned with the related values extracted from <code>filePath</code>.
         */
        private Map<String, String> getVariables( String filePath ) {
            Map<String, String> variables = new HashMap<String, String>();
            variables.put( "$filename", new File( filePath ).getName() );
            variables.put( "$pathname", filePath );
            return variables;
        }
        
        /**
         * Delete a file that was found by the scanner.
         * @param killFile
         */
        protected void deleteFile( String killFile ) {

            File killFileObject = new File( killFile );

            if ( killFileObject.delete() ) {
            	MultipleDirectoryScannerService.LOG.debug( "File " + killFile + " deleted." );
            } else {
            	MultipleDirectoryScannerService.LOG.error( "File " + killFile + " could not be deleted." );
            }
        }

        /**
         * Backup a given file to the backup directory initialized at startup.
         */
        protected void backupFile( String newFileName, byte[] document ) {

            String localDir = backupDirectory;

            if ( ( localDir != null ) && ( localDir.length() != 0 ) ) {
                File file = new File( newFileName );
                String fileShortName = file.getName();

                if ( !localDir.endsWith( "/" ) && !localDir.endsWith( "\\" ) ) {
                    localDir += "/";
                }

                String bakFileName = localDir + fileShortName + ".bak";

                try {
                    File newFile = new File( bakFileName );
                    BufferedOutputStream fileOutputStream = new BufferedOutputStream( new FileOutputStream( newFile ) );
                    fileOutputStream.write( document );
                    fileOutputStream.flush();
                    fileOutputStream.close();
                } catch ( Exception ex ) {
                	MultipleDirectoryScannerService.LOG.error( "File " + newFileName + " backup failed." );
                }
            }
        }

        protected class RegexFilenameFilter implements FilenameFilter {

            String filePattern = null;
            Pattern pattern = null;

            RegexFilenameFilter( String filePattern ) {

                this.filePattern = filePattern;
                this.pattern = Pattern.compile( filePattern );
            }

            /**
             * Ignores the <code>dir</code> parameter.
             */
            public boolean accept( File dir, String name ) {
            	return pattern.matcher( name ).matches();                
            }
        } // RegexFilenameFilter
    } // DirectoryScanner
    
} // MultipleDirectoryScannerService
