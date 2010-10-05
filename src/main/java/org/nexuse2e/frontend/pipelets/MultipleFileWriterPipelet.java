package org.nexuse2e.frontend.pipelets;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.nexuse2e.NexusException;
import org.nexuse2e.Constants.BeanStatus;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.Constants.ParameterType;
import org.nexuse2e.logging.LogMessage;
import org.nexuse2e.messaging.AbstractPipelet;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.pojo.MessagePayloadPojo;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This pipelet writes message payloads to the file system for each choreography/action/partner specified.
 * Find the configuration file spec in file_writer_config.xsd in the project source.
 * 
 * Example:
 * <pre>
 * <?xml version="1.0" encoding="UTF-8"?>
 * <FileWriterConfig xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
 *     xsi:noNamespaceSchemaLocation="file_writer_config.xsd">
 *      <Writer
 *         <!-- The directory to put files -->
 *         dir="/foo/bar"
 *         <!-- Whether to use the contend id (optional; default is false) -->
 *         useContendId="false"
 *         <!-- The file prefix (optional; default is empty string) -->
 *         filePrefix="Order"
 *         <!-- File extension (optional; defailt is empty string) -->
 *         fileExtension=".xml"
 *         <!-- Use a temporary file name extension while writing the file (optional; default is no temporary file name) -->
 *         tmpFileNameExt=".part"
 *         <!-- Appemd a timestamp with the given pattern (optional; default is no timestamp) -->
 *         timestampPattern="yyyyMMddHHmmssSSS"
 *         <!-- The choreography this configuration applies to -->
 *         choreographyId="Order"
 *         <!-- The action this configuration applies to -->
 *         actionId="SendFile"
 *         <!-- The partner this configuration applies to -->
 *         partnerId="Darling"
 * </FileWriterConfig>
 * </pre>
 * 
 * @author Sebastian Schulze
 * @date 04.10.2010
 */
public class MultipleFileWriterPipelet extends AbstractPipelet {

    private static final Logger LOG = Logger.getLogger( MultipleFileWriterPipelet.class );
    
    public static final String CONFIG_FILE = "config_file";
    
    private Map<ConfigKey, WriterConfig> config;
    
    public MultipleFileWriterPipelet() {
        super();
        config = new HashMap<ConfigKey, WriterConfig>();
        parameterMap.put( CONFIG_FILE, new ParameterDescriptor( ParameterType.STRING, "Configuration file",
            "The XML file that contains the configuration for this service.", "" ) );
        setFrontendPipelet( true );
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.AbstractPipelet#initialize(org.nexuse2e.configuration.EngineConfiguration)
     */
    @Override
    public void initialize( EngineConfiguration config ) throws InstantiationException {
        super.initialize( config );
        if ( LOG.isTraceEnabled() ) {
            LOG.trace( "initializing" );
        }
        
        // clean up
        this.config.clear();
        
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

                private int writerCounter = 0;
                
                /* (non-Javadoc)
                 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
                 */
                @Override
                public void startElement(String uri, String localName,
                        String name, Attributes attributes) throws SAXException {
                    if ( "Writer".equals( name ) && attributes != null ) {
                        if ( LOG.isTraceEnabled() ) {
                            LOG.trace( "Writer attributes: " + attributes );
                        }
                        writerCounter++;
                        String choreographyId = attributes.getValue( "", "choreographyId" );
                        String actionId = attributes.getValue( "", "actionId" );
                        String partnerId = attributes.getValue( "", "partnerId" );
                        if ( StringUtils.isNotEmpty( choreographyId )
                                && StringUtils.isNotEmpty( actionId )
                                && StringUtils.isNotEmpty( partnerId ) ) {
                            try {
                                MultipleFileWriterPipelet.this.config.put(
                                    new ConfigKey( choreographyId, actionId, partnerId ),
                                    new WriterConfig(
                                            attributes.getValue( "", "dir" ),
                                            Boolean.parseBoolean( attributes.getValue( "", "useContentId" ) ),
                                            attributes.getValue( "", "filePrefix" ),
                                            attributes.getValue( "", "fileExtension" ),
                                            attributes.getValue( "", "tmpDir" ),
                                            attributes.getValue( "", "tmpFileNameExt" ),
                                            attributes.getValue( "", "timestampPattern" ),
                                            choreographyId,
                                            actionId,
                                            partnerId ) );
                            } catch ( IllegalArgumentException e ) {
                                LOG.error( "Cannot initialize directory scanner number " + writerCounter + " defined in the config file. Please check your configuration.");
                            }
                        } else {
                            LOG.warn( "Missing attribute choreographyId, actionId, or partner in writer config. That entry will be ignored. Check your configuration file." );
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
    }

    @Override
    public MessageContext processMessage( MessageContext messageContext ) throws IllegalArgumentException,
                                                                         IllegalStateException, NexusException {
        if ( config != null ) {
            if ( messageContext != null
                && messageContext.getMessagePojo() != null
                && messageContext.getChoreography() != null
                && messageContext.getPartner() != null ) {
                String choreographyId = messageContext.getChoreography().getName();
                String actionId = messageContext.getMessagePojo().getAction().getName();
                String partnerId = messageContext.getPartner().getPartnerId();
                WriterConfig currConfig = config.get( new ConfigKey( choreographyId,
                                                                     actionId,
                                                                     partnerId ) );
                if ( currConfig != null ) {
                    // render timestamp
                    String timestamp = ( StringUtils.isNotEmpty( currConfig.getTimestampPattern() ) ?
                            ( new SimpleDateFormat( currConfig.getTimestampPattern() ) ).format( new Date() ) : null );
                    for ( MessagePayloadPojo currPayload : messageContext.getMessagePojo().getMessagePayloads() ) {
                        if ( currPayload != null ) {
                            // prepare content id
                            String contentId = currPayload.getContentId();
                            int idxOfBody = contentId.indexOf( "__body" );
                            if ( idxOfBody != -1 ) {
                                contentId = contentId.substring( 0, contentId.indexOf( "__body" ) );
                            }
                            // render file name
                            String newFileName = currConfig.getFilePrefix()
                                    + ( currConfig.isUseContentId() ? contentId : "" )
                                    + ( timestamp != null
                                            ? "_" + timestamp
                                            : "" ) + currConfig.getFileExtension();
                            // check, if a temporary file name should be used 
                            boolean useTmpDir = StringUtils.isNotEmpty( currConfig.getTmpDir() );
                            boolean useTmpFileExt = StringUtils.isNotEmpty( currConfig.getTmpFileNameExt() );
                            // create logical file name
                            File file = new File( ( useTmpDir ? currConfig.getTmpDir() : currConfig.getDir() ),
                                ( useTmpFileExt ? newFileName + currConfig.getTmpFileNameExt() : newFileName ) );
                            // log warning, if file exists already (e.g. for multiple payloads without content id)
                            if ( file.exists() ) {
                                LOG.warn( new LogMessage( "Overwriting existing file " + file.getAbsolutePath()
                                    + " with payload  " + currPayload.getSequenceNumber()
                                    + " (" + currPayload.getContentId() + ")", messageContext ) );
                            }
                            try {
                                // write file
                                FileOutputStream fos = new FileOutputStream( file );
                                if ( currPayload.getPayloadData() != null ) {
                                    fos.write( currPayload.getPayloadData() );
                                }
                                fos.close();
                                if ( useTmpDir || useTmpFileExt ) {
                                    File finalDestFile = new File( currConfig.getDir(), newFileName );
                                    // log warning, if file exists alread (e.g. for multiple payloads without content id)
                                    if ( finalDestFile.exists() ) {
                                        LOG.warn( new LogMessage( "Overwriting existing file " + finalDestFile.getAbsolutePath()
                                            + " with payload  " + currPayload.getSequenceNumber()
                                            + " (" + currPayload.getContentId() + ")", messageContext ) );
                                    }
                                    try {
                                        if ( !file.renameTo( finalDestFile ) ) {
                                            throw new NexusException(
                                                new LogMessage( "Could not rename file " + file.getAbsolutePath()
                                                    + " to " + finalDestFile + ".", messageContext ) );
                                        }
                                    } catch ( SecurityException e ) {
                                        throw new NexusException( new LogMessage( "Could not rename file " + file.getAbsolutePath()
                                            + " to " + finalDestFile + ": " + e.getMessage(), messageContext ), e );
                                    }
                                }
                            } catch ( Exception e ) {
                                throw new NexusException(
                                    new LogMessage( "Error writing payload to file: " + e.getMessage(), messageContext ), e );
                            }                            
                        }
                    }
                } else {
                    throw new NexusException( new LogMessage( "No file writer configuration found for "
                        + choreographyId + "/" + actionId + "/" + partnerId + ".", messageContext ) );
                }
            } else {
                throw new NexusException( new LogMessage( "Incomplete message context. Message, choreography, or partner missing.", messageContext ) );
            }
        } else {
            throw new NexusException( new LogMessage( "No file writer configuration found.", messageContext ) );
        }
        
        return messageContext;
    }
    
    class WriterConfig {
        private String dir;
        private boolean useContentId;
        private String filePrefix;
        private String fileExtension;
        private String tmpDir;
        private String tmpFileNameExt;
        private String timestampPattern;
        private String choreographyId;
        private String actionId;
        private String partnerId;
        
        private WriterConfig( String dir, boolean useContentId, String filePrefix, String fileExtension,
                              String tmpDir, String tmpFileNameExt, String timestampPattern, String choreographyId,
                              String actionId, String partnerId ) {
            super();
            this.dir = dir != null ? dir : "";
            this.useContentId = useContentId;
            this.filePrefix = filePrefix != null ? filePrefix : "";
            this.fileExtension = fileExtension != null ? fileExtension : "";
            this.tmpDir = tmpDir != null ? tmpDir : "";
            this.tmpFileNameExt = tmpFileNameExt != null ? tmpFileNameExt : "";
            this.timestampPattern = timestampPattern != null ? timestampPattern : "";
            this.choreographyId = choreographyId;
            this.actionId = actionId;
            this.partnerId = partnerId;
        }
        
        /**
         * @return the dir
         */
        private String getDir() {
            return dir;
        }
        /**
         * @param dir the dir to set
         */
        private void setDir( String dir ) {
            this.dir = dir;
        }
        /**
         * @return the useContentId
         */
        private boolean isUseContentId() {
            return useContentId;
        }
        /**
         * @param useContentId the useContentId to set
         */
        private void setUseContentId( boolean useContentId ) {
            this.useContentId = useContentId;
        }
        /**
         * @return the filePrefix
         */
        private String getFilePrefix() {
            return filePrefix;
        }
        /**
         * @param filePrefix the filePrefix to set
         */
        private void setFilePrefix( String filePrefix ) {
            this.filePrefix = filePrefix;
        }
        /**
         * @return the fileExtension
         */
        private String getFileExtension() {
            return fileExtension;
        }
        /**
         * @param fileExtension the fileExtension to set
         */
        private void setFileExtension( String fileExtension ) {
            this.fileExtension = fileExtension;
        }
        /**
         * @return the tmpDir
         */
        private String getTmpDir() {
            return tmpDir;
        }
        /**
         * @param tmpDir the tmpDir to set
         */
        private void setTmpDir( String tmpDir ) {
            this.tmpDir = tmpDir;
        }
        /**
         * @return the tmpFileNameExt
         */
        private String getTmpFileNameExt() {
            return tmpFileNameExt;
        }
        /**
         * @param tmpFileNameExt the tmpFileNameExt to set
         */
        private void setTmpFileNameExt( String tmpFileNameExt ) {
            this.tmpFileNameExt = tmpFileNameExt;
        }
        /**
         * @return the timestampPattern
         */
        private String getTimestampPattern() {
            return timestampPattern;
        }
        /**
         * @param timestampPattern the timestampPattern to set
         */
        private void setTimestampPattern( String timestampPattern ) {
            this.timestampPattern = timestampPattern;
        }
        /**
         * @return the choreographyId
         */
        private String getChoreographyId() {
            return choreographyId;
        }
        /**
         * @param choreographyId the choreographyId to set
         */
        private void setChoreographyId( String choreographyId ) {
            this.choreographyId = choreographyId;
        }
        /**
         * @return the actionId
         */
        private String getActionId() {
            return actionId;
        }
        /**
         * @param actionId the actionId to set
         */
        private void setActionId( String actionId ) {
            this.actionId = actionId;
        }
        /**
         * @return the partnerId
         */
        private String getPartnerId() {
            return partnerId;
        }
        /**
         * @param partnerId the partnerId to set
         */
        private void setPartnerId( String partnerId ) {
            this.partnerId = partnerId;
        }
    }
    
    class ConfigKey {
        
        private String choreographyId;
        private String actionId;
        private String partnerId;
        
        private ConfigKey( String choreographyId, String actionId, String partnerId ) {
            this.choreographyId = choreographyId;
            this.actionId = actionId;
            this.partnerId = partnerId;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + getOuterType().hashCode();
            result = prime * result + ( ( actionId == null ) ? 0 : actionId.hashCode() );
            result = prime * result + ( ( choreographyId == null ) ? 0 : choreographyId.hashCode() );
            result = prime * result + ( ( partnerId == null ) ? 0 : partnerId.hashCode() );
            return result;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals( Object obj ) {
            if ( this == obj )
                return true;
            if ( obj == null )
                return false;
            if ( getClass() != obj.getClass() )
                return false;
            ConfigKey other = (ConfigKey) obj;
            if ( !getOuterType().equals( other.getOuterType() ) )
                return false;
            if ( actionId == null ) {
                if ( other.actionId != null )
                    return false;
            } else if ( !actionId.equals( other.actionId ) )
                return false;
            if ( choreographyId == null ) {
                if ( other.choreographyId != null )
                    return false;
            } else if ( !choreographyId.equals( other.choreographyId ) )
                return false;
            if ( partnerId == null ) {
                if ( other.partnerId != null )
                    return false;
            } else if ( !partnerId.equals( other.partnerId ) )
                return false;
            return true;
        }

        private MultipleFileWriterPipelet getOuterType() {
            return MultipleFileWriterPipelet.this;
        }
    }

}
