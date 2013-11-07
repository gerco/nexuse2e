/**
 * 
 */
package org.nexuse2e.backend.pipelets;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.digester.Digester;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.nexuse2e.BeanStatus;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.ParameterType;
import org.nexuse2e.logging.LogMessage;
import org.nexuse2e.messaging.AbstractPipelet;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.pojo.MessagePayloadPojo;
import org.nexuse2e.service.Service;
import org.nexuse2e.service.mapping.DataMapper;
import org.nexuse2e.tools.mapping.xmldata.MappingDefinition;
import org.nexuse2e.tools.mapping.xmldata.MappingDefinitions;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author JJerke
 *
 */
public class DataMappingPipelet extends AbstractPipelet {
	
	/*
	 * ---------------------
	 * CONSTANTS & VARIABLES
	 * ---------------------
	 */
	
    private static Logger      LOG                = Logger.getLogger( XMLDataMappingPipelet.class );

    public static final String CONFIG_FILE     	  = "config_file";

    private String             configFileName     = null;
    private MappingDefinitions mappingDefinitions = null;
    private Service		   	   mappingService	  = null;
    // localPartnerId should contain the name by which the data repository knows us, the service will replace the string 'LOCAL' with this string.
    private String			   localPartnerId	  = null;

    
    
    /*
	 * ------------
	 * CONSTRUCTORS
	 * ------------
	 */
    
    /**
     * Constructs a new DataMappingPipelet and fills it's configuration UI parameterMap.
     * Note that this pipelet uses a configuration where the user can add more lines to the config screen at will.
     */
    public DataMappingPipelet() {
    	parameterMap.put( CONFIG_FILE, new ParameterDescriptor( ParameterType.STRING, "Config File",
                "Path to configuration file", "" ) );
    }
    
    
    
    /*
     * -------
     * METHODS
     * -------
     */
    
    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.AbstractPipelet#initialize(org.nexuse2e.configuration.EngineConfiguration)
     */
    @Override
    public void initialize( EngineConfiguration config ) throws InstantiationException {

        File testFile = null;
        
        String configFileNameValue = getParameter( CONFIG_FILE );
        if ( ( configFileNameValue != null ) && ( configFileNameValue.length() != 0 ) ) {
            configFileName = configFileNameValue;
            testFile = new File( configFileName );
            if ( !testFile.exists() ) {
                status = BeanStatus.ERROR;
                LOG.error( "Configuration file does not exist!" );
                return;
            }

            mappingDefinitions = readConfiguration( configFileName );

        } else {
            status = BeanStatus.ERROR;
            LOG.error( "No value for setting 'Config File' provided!" );
            return;
        }

        LOG.trace( "configFileName  : " + configFileName );

        super.initialize( config );
    }
    
    /**
     * Reads and processes the Mapping Definitions.
     * Configuration is done with a mapping-config file, the path to which can be set in the config GUI for this pipelet.
     * 
     * @param configFileName
     * @return
     */
    private MappingDefinitions readConfiguration( String configFileName ) {

        MappingDefinitions mappingDefinitions = null;
        Digester digester = new Digester();
        digester.setValidating( false );
        digester.addObjectCreate( "MappingDefinitions", "org.nexuse2e.tools.mapping.xmldata.MappingDefinitions" );
        digester.addSetProperties( "MappingDefinitions" );
        digester.addObjectCreate( "MappingDefinitions/MappingDefinition",
                "org.nexuse2e.tools.mapping.xmldata.MappingDefinition" );
        digester.addSetProperties( "MappingDefinitions/MappingDefinition" );
        digester.addSetNext( "MappingDefinitions/MappingDefinition", "addMappingDefinition",
                "org.nexuse2e.tools.mapping.xmldata.MappingDefinition" );

        try {
            mappingDefinitions = (MappingDefinitions) digester.parse( configFileName );
        } catch ( IOException e ) {
            e.printStackTrace();
        } catch ( SAXException e ) {
            e.printStackTrace();
        }
        return mappingDefinitions;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.AbstractPipelet#teardown()
     */
    @Override
    public void teardown() {

        super.teardown();
    }

	/* (non-Javadoc)
	 * @see org.nexuse2e.messaging.AbstractPipelet#processMessage(org.nexuse2e.messaging.MessageContext)
	 */
	@Override
	public MessageContext processMessage(MessageContext messageContext)
			throws IllegalArgumentException, IllegalStateException,
			NexusException {
		
		if (null == configFileName) {
			LOG.error(new LogMessage("No configuration file specified - no mapping possible.", messageContext));
            throw new NexusException("No configuration file specified - no mapping possible.");
		}
		try {
            List<MessagePayloadPojo> payloads = messageContext.getMessagePojo().getMessagePayloads();
            for (MessagePayloadPojo messagePayloadPojo : payloads) {
                ByteArrayInputStream bais = new ByteArrayInputStream( messagePayloadPojo.getPayloadData() );

                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
                Document document = builder.parse(bais);
                
                LOG.debug(new LogMessage("Message: "+ messageContext.getMessagePojo()));
                if(messageContext.getMessagePojo() != null && messageContext.getMessagePojo().getParticipant() != null &&
                        messageContext.getMessagePojo().getParticipant().getLocalPartner() != null)
                {
                    localPartnerId = messageContext.getMessagePojo().getParticipant().getLocalPartner().getPartnerId();
                }
                document = processMappings(document);

                // Serialize result
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                transformer.transform(new DOMSource(document), new StreamResult(baos));
                
                messagePayloadPojo.setPayloadData(baos.toByteArray());

                if (LOG.isTraceEnabled()) {
                    LOG.trace(new LogMessage("....................",messageContext));
                    LOG.trace(new LogMessage(new String( messagePayloadPojo.getPayloadData(), messageContext.getEncoding()),messageContext));
                    LOG.trace(new LogMessage("....................",messageContext));
                }
            }
        } catch (Exception e) {
            LOG.error(new LogMessage("Error processing XML payload", messageContext, e), e);
            throw new NexusException("Error processing XML payload", e);
        }
		return messageContext;
	}
	
	/**
	 * Checks a single document for required mapping actions and - as needed - performs them by invoking mapData.
	 * 
	 * @param document
	 * @return
	 * @throws DOMException
	 * @throws NexusException 
	 */
	private Document processMappings(Document document) throws DOMException, NexusException {
        Document result = null;

        XPath xPath = XPathFactory.newInstance().newXPath();

        try {
            if (mappingDefinitions != null && mappingDefinitions.getMappingDefinitions() != null) {
                for (MappingDefinition mappingDefinition : mappingDefinitions.getMappingDefinitions()) {
                	LOG.debug("def.Category: " + mappingDefinition.getCategory());
                	LOG.debug("def.Command: " + mappingDefinition.getCommand());
                	LOG.debug("def.XPath: " + mappingDefinition.getXpath());
                	LOG.debug("def.MappingServiceId: " + mappingDefinition.getMappingServiceId());
                    
                    NodeList nodes = (NodeList) xPath.evaluate( mappingDefinition.getXpath(), document, XPathConstants.NODESET );
                    if (nodes != null && nodes.getLength() > 0) {
                        LOG.debug("Found " + nodes.getLength() + " matching nodes for " + mappingDefinition.getXpath());
                        for (int i = 0; i < nodes.getLength(); i++) {
                            Node node = nodes.item( i );
                            if ( node instanceof Element ) {
                            	LOG.debug("translating the node: " + node.getNodeValue());
                                ( (Element) node ).setTextContent( mapData( xPath, document, ( (Element) node ).getTextContent(),
                                        mappingDefinition ) );
                            } else if ( node instanceof Attr ) {
                                String nodeValue = ( (Attr) node ).getNodeValue();
                                String resultValue = mapData(xPath, document, nodeValue, mappingDefinition );
                                ( (Attr) node ).setNodeValue( resultValue );
                            } else {
                                LOG.error( "Node type not recognized: " + node.getClass() );
                            }
                        }
                    } else {
                        LOG.warn( "Could not find matching node for " + mappingDefinition.getXpath() );
                    }
                }
            }
            result = document;
        } catch ( XPathExpressionException e ) {
            LOG.error( e );
            e.printStackTrace();
        }

        return result;
	}
	
    /**
     * Handles a single mapping operation by calling the corresponding MappingService.
     * Note that the mapping-service to be used has to be configured on a per-MappingDefinition-basis.
     * 
     * @param xPath
     * @param document
     * @param value
     * @param mappingDefinition
     * @return
     * @throws NexusException 
     */
    private String mapData( XPath xPath, Document document, String value, MappingDefinition mappingDefinition ) throws NexusException {
        String targetValue = value;
        DataMapper dataMapper = null;
        
        if(mappingDefinition.getMappingServiceId() != 0) {
        	// Fetch the correct mapping service for this mapping
        	mappingService = Engine.getInstance().getCurrentConfiguration().getServiceInstanceFromPojo(
        						Engine.getInstance().getCurrentConfiguration().getServicePojoByNxServiceId(mappingDefinition.getMappingServiceId()));
        	if (null != mappingService && mappingService instanceof DataMapper) {
        		dataMapper = (DataMapper) mappingService;
        	}
			
        	// Parse the mapping command
	        String command = mappingDefinition.getCommand();
	        Pattern pattern = Pattern.compile("[a-zA-Z]+");
	        Matcher matcher = pattern.matcher(command);
	        if ( matcher.find() ) {
	        	
	        	// Check for correct command
	            String commandName = matcher.group();
	            if (!"mapdunsdata".matches(commandName)) {
	            	LOG.error("mapData was called without the mappingDefinition being useful. Use the command 'mapdunsdata' to configure this pipelet.");
	            	return targetValue;
	            }
	            
	            int endIndex = matcher.end();
	            // LOG.trace( "command(" + endIndex + "): " + commandName );

	            pattern = Pattern.compile( "[a-zA-Z0-9/\\(\\)\\'\\,\\:\\$\\#\\.\\\\\\-\\_\\. \\@\\[\\]\\+]+" );
	            matcher = pattern.matcher( command );
	            ArrayList<String> paramList = null;
	            if ( matcher.find( endIndex ) ) {
	                paramList = new ArrayList<String>();
	                String params = matcher.group();
	                // LOG.trace( "parameterlist:" + params );

	                pattern = Pattern
	                        .compile( "((\\'([a-zA-Z0-9/\\(\\)\\,\\:\\-\\_\\. \\@\\#\\[\\]\\+]|\\\\')+\\')|(\\$[a-zA-Z0-9\\_]+))+" );
	                matcher = pattern.matcher( params );
	                while ( matcher.find() ) {
	                    String param = matcher.group();
	                    paramList.add( param );
	                    // LOG.trace( "param: " + param );
	                }
	            }
	            String[] paramArray = null;
	            if ( paramList != null && paramList.size() > 0 ) {
	                paramArray = paramList.toArray( new String[paramList.size()] );
	                for (int ii = 0; ii < paramArray.length; ii++) {
	                	paramArray[ii] = paramArray[ii].substring(1,paramArray[ii].length()-1);
	                }
	            }

	            // Hand the data to the corresponding service
	            LOG.debug("calling mappingservice");
	            targetValue = dataMapper.processConversion(value, paramArray[0], paramArray[1], localPartnerId);
	        }

	        // Process any formatting rules (length etc.)
	        if ((mappingDefinition.getLengthTarget() != 0) && (targetValue.length() > mappingDefinition.getLengthTarget())) {
	            targetValue = targetValue.substring(0, mappingDefinition.getLengthTarget());
	        }
            if(!StringUtils.isEmpty(targetValue)) {
                return targetValue;
            } else {
                return "";
            }
        } else {
            LOG.error("Data Mapping Service must be configured!");
        }
        

        return targetValue;
    }

}
