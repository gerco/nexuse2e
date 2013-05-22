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
package org.nexuse2e.backend.pipelets;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

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
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.Constants.BeanStatus;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.Constants.ParameterType;
import org.nexuse2e.logging.LogMessage;
import org.nexuse2e.messaging.AbstractPipelet;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.pojo.MessagePayloadPojo;
import org.nexuse2e.service.DataConversionException;
import org.nexuse2e.service.DataConversionService;
import org.nexuse2e.service.Service;
import org.nexuse2e.tools.mapping.xmldata.MappingDefinition;
import org.nexuse2e.tools.mapping.xmldata.MappingDefinitions;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author mbreilmann
 *
 */
public class XMLDataMappingPipelet extends AbstractPipelet {

    private static Logger      LOG                = Logger.getLogger( XMLDataMappingPipelet.class );

    public static final String CONFIG_FILE        = "config_file";
    public static final String MAPPING_SERVICE    = "mapping_service";

    public static final String COMMAND_MAP_LEFT   = "$map_left";
    public static final String COMMAND_MAP_RIGHT  = "$map_right";

    private String             configFileName     = null;
    private DataConversionService mappingService  = null;
    private MappingDefinitions mappingDefinitions = null;

    /**
     * 
     */
    public XMLDataMappingPipelet() {

        parameterMap.put( CONFIG_FILE, new ParameterDescriptor( ParameterType.STRING, "Configuration Path",
                "Path to configuration file", "" ) );
        parameterMap.put( MAPPING_SERVICE, new ParameterDescriptor( ParameterType.SERVICE, "Data Mapping Service",
                "The Data Mapping and Conversion Service", DataConversionService.class ) );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.AbstractPipelet#initialize(org.nexuse2e.configuration.EngineConfiguration)
     */
    @Override
    public void initialize( EngineConfiguration config ) throws InstantiationException {

        File testFile = null;

        String mappingServiceName = getParameter( MAPPING_SERVICE );
        if(!StringUtils.isEmpty( mappingServiceName )) {
            
            Service service = Engine.getInstance().getActiveConfigurationAccessService().getService( mappingServiceName );
            if(service != null && service instanceof DataConversionService) {
                this.mappingService = (DataConversionService)service;
            }
        }
        
        String configFileNameValue = getParameter( CONFIG_FILE );
        if ( ( configFileNameValue != null ) && ( configFileNameValue.length() != 0 ) ) {
            configFileName = configFileNameValue;
            testFile = new File( configFileName );
            if ( !testFile.exists() ) {
                status = BeanStatus.ERROR;
                LOG.error( "Cponfiguration file does not exist!" );
                return;
            }

            mappingDefinitions = readConfiguration( configFileName );

        } else {
            status = BeanStatus.ERROR;
            LOG.error( "No value for setting 'xslt file' provided!" );
            return;
        }

        LOG.trace( "configFileName  : " + configFileName );

        super.initialize( config );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.AbstractPipelet#processMessage(org.nexuse2e.messaging.MessageContext)
     */
    @Override
    public MessageContext processMessage( MessageContext messageContext ) throws IllegalArgumentException,
            IllegalStateException, NexusException {

        if ( configFileName != null ) {
            try {
                List<MessagePayloadPojo> payloads = messageContext.getMessagePojo().getMessagePayloads();
                for (MessagePayloadPojo messagePayloadPojo : payloads) {
                    ByteArrayInputStream bais = new ByteArrayInputStream( messagePayloadPojo.getPayloadData() );

                    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
                    Document document = builder.parse( bais );

                    document = processMappings( document );

                    // Serialize result
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    TransformerFactory transformerFactory = TransformerFactory.newInstance();
                    Transformer transformer = transformerFactory.newTransformer();
                    transformer.transform( new DOMSource( document ), new StreamResult( baos ) );
                    
                    messagePayloadPojo.setPayloadData( baos.toByteArray() );

                    if ( LOG.isTraceEnabled() ) {
                        LOG.trace( new LogMessage( "....................",messageContext ) );
                        LOG.trace( new LogMessage( new String( messagePayloadPojo.getPayloadData(), messageContext.getEncoding() ),messageContext ) );
                        LOG.trace( new LogMessage( "....................",messageContext ) );
                    }
                }
            } catch ( Exception e ) {
                LOG.error(new LogMessage(  "Error processing XML payload: " + e,messageContext ), e );
                throw new NexusException( "Error processing XML payload: " + e, e );
            }

        } else {
            LOG.error( new LogMessage( "No configuration file specified - no mapping possible.",messageContext ) );
            throw new NexusException( "No configuration file specified - no mapping possible." );
        }// if

        return messageContext;
    }

    /**
     * @param document
     * @return
     */
    private Document processMappings( Document document ) throws DataConversionException {

        Document result = null;

        XPath xPath = XPathFactory.newInstance().newXPath();

        try {
            if (mappingDefinitions != null && mappingDefinitions.getMappingDefinitions() != null) {
                for (MappingDefinition mappingDefinition : mappingDefinitions.getMappingDefinitions()) {
                    LOG.debug( "def.Category: "+mappingDefinition.getCategory());
                    LOG.debug( "def.Command: "+mappingDefinition.getCommand());
                    LOG.debug( "def.XPath: "+mappingDefinition.getXpath());
                    
                    NodeList nodes = (NodeList) xPath.evaluate( mappingDefinition.getXpath(), document, XPathConstants.NODESET );
                    if (nodes != null && nodes.getLength() > 0) {
                        for (int i = 0; i < nodes.getLength(); i++) {
                            Node node = nodes.item( i );
                            if ( node instanceof Element ) {
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

    private String mapData( XPath xPath, Document document, String sourceValue, MappingDefinition mappingDefinition ) throws DataConversionException {
        String result = null;
        
        
        if(mappingService != null) {
            LOG.debug( "calling mappingservice" );
            String targetValue = mappingService.processConversion( xPath, document, sourceValue, mappingDefinition );
            if(!StringUtils.isEmpty( targetValue )) {
                return targetValue;
            } else {
                return "";
            }
        } else {
            LOG.error( "Data Mapping Service must be configured!" );
        }
        

        return result;
    }

    /**
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

    /**
     * @param args
     */
    public static void main( String args[] ) {

        if ( args.length != 2 ) {
            System.err.println( "Wrong number of parameters. Usage: XMLDataMappingPipelet <xml file> <config file>" );
            return;
        }
        try {
            
            XMLDataMappingPipelet xmlDataMappingPipelet = new XMLDataMappingPipelet();
            xmlDataMappingPipelet.mappingService = new DataConversionService();
            MappingDefinitions mappingDefinitions = xmlDataMappingPipelet.readConfiguration( args[1] );
            xmlDataMappingPipelet.setMappingDefinitions( mappingDefinitions );
            System.out.println( "MappingDefinitions: " + mappingDefinitions );

            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder;
            builder = documentBuilderFactory.newDocumentBuilder();
            Document document = builder.parse( args[0] );

            document = xmlDataMappingPipelet.processMappings( document );

            // Serialize result
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.transform( new DOMSource( document ), new StreamResult( baos ) );

            System.out.println( "Result: " + baos.toString() );
        } catch ( Exception e ) {
            e.printStackTrace();
        } catch (Error e) {
            e.printStackTrace();
        }
    }

    public MappingDefinitions getMappingDefinitions() {

        return mappingDefinitions;
    }

    public void setMappingDefinitions( MappingDefinitions mappingDefinitions ) {

        this.mappingDefinitions = mappingDefinitions;
    }
} // XMLDataMappingPipelet
