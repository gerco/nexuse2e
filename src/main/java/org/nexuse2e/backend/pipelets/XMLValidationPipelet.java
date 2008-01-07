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

package org.nexuse2e.backend.pipelets;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
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
import org.nexuse2e.Constants.Severity;
import org.nexuse2e.backend.pipelets.helper.PartnerSpecificConfiguration;
import org.nexuse2e.backend.pipelets.helper.PartnerSpecificConfigurations;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.Constants.ParameterType;
import org.nexuse2e.messaging.AbstractPipelet;
import org.nexuse2e.messaging.ErrorDescriptor;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.pojo.MessagePayloadPojo;
import org.nexuse2e.service.DataConversionService;
import org.nexuse2e.service.Service;
import org.nexuse2e.tools.mapping.xmldata.MappingDefinition;
import org.nexuse2e.tools.validation.ValidationDefinition;
import org.nexuse2e.tools.validation.ValidationDefinitions;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XMLValidationPipelet extends AbstractPipelet {

    private static Logger                          LOG                          = Logger
                                                                                        .getLogger( XMLValidationPipelet.class );

    public static final String                     CONFIG_FILE                  = "config_file";
    public static final String                     PARTNER_SPECIFIC             = "partner_specific";
    public static final String                     MAPPING_SERVICE              = "mapping_service";

    private String                                 configFileName               = null;
    private DataConversionService                  mappingService               = null;
    private ValidationDefinitions                  validationDefinitions        = null;

    private boolean                                partnerSpecific              = false;
    private HashMap<String, ValidationDefinitions> partnerValidationDefinitions = null;

    public XMLValidationPipelet() {

        parameterMap.put( CONFIG_FILE, new ParameterDescriptor( ParameterType.STRING, "Validation Definitions",
                "Path to validation definitions file", "" ) );
        parameterMap.put( PARTNER_SPECIFIC, new ParameterDescriptor( ParameterType.BOOLEAN, "Partner Specific",
                "use partner specific validation configuration file", Boolean.FALSE ) );
        parameterMap.put( MAPPING_SERVICE, new ParameterDescriptor( ParameterType.SERVICE, "Data Mapping Service",
                "The Data Mapping and Conversion Service", DataConversionService.class ) );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.AbstractPipelet#initialize(org.nexuse2e.configuration.EngineConfiguration)
     */
    @Override
    public void initialize( EngineConfiguration config ) throws InstantiationException {

        File testFile = null;

        Boolean partnerSpecificValue = getParameter( PARTNER_SPECIFIC );
        if ( partnerSpecificValue != null ) {
            partnerSpecific = partnerSpecificValue.booleanValue();
        }

        String mappingServiceName = getParameter( MAPPING_SERVICE );
        if ( !StringUtils.isEmpty( mappingServiceName ) ) {

            Service service = Engine.getInstance().getActiveConfigurationAccessService()
                    .getService( mappingServiceName );
            if ( service != null && service instanceof DataConversionService ) {
                this.mappingService = (DataConversionService) service;
            }
        }

        String configFileNameValue = getParameter( CONFIG_FILE );
        if ( ( configFileNameValue != null ) && ( configFileNameValue.length() != 0 ) ) {
            configFileName = configFileNameValue;
            testFile = new File( configFileName );
            if ( !testFile.exists() ) {
                status = BeanStatus.ERROR;
                LOG.error( "Configuration file does not exist!" );
                return;
            }

            if ( partnerSpecific ) {
                ValidationDefinitions partnerDefinitions = null;
                PartnerSpecificConfigurations partnerSpecificConfigurations = readPartnerSpecificConfigurations( configFileName );
                partnerValidationDefinitions = new HashMap<String, ValidationDefinitions>();

                for ( PartnerSpecificConfiguration partnerSpecificConfiguration : partnerSpecificConfigurations
                        .getPartnerSpecificConfigurations() ) {
                    partnerDefinitions = readConfiguration( partnerSpecificConfiguration.getConfigurationFile() );
                    partnerValidationDefinitions.put( partnerSpecificConfiguration.getPartnerId(), partnerDefinitions );

                }
                for ( String partnerId : partnerValidationDefinitions.keySet() ) {
                    LOG.debug( "Configuration fpr partner: '" + partnerId + "' - "
                            + partnerValidationDefinitions.get( partnerId ) );
                }
            } else {

                validationDefinitions = readConfiguration( configFileName );

            }

        } else {
            status = BeanStatus.ERROR;
            LOG.error( "No value for setting 'xslt file' provided!" );
            return;
        }

        LOG.trace( "configFileName  : " + configFileName );

        super.initialize( config );
    }

    @Override
    public MessageContext processMessage( MessageContext messageContext ) throws IllegalArgumentException,
            IllegalStateException, NexusException {

        try {

            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware( false );
            DocumentBuilder builder;
            builder = documentBuilderFactory.newDocumentBuilder();

            for ( MessagePayloadPojo payload : messageContext.getMessagePojo().getMessagePayloads() ) {
                if ( payload.getPayloadData() != null && payload.getPayloadData().length > 0 ) {
                    String doc = new String( payload.getPayloadData() );
                    if ( doc == null || doc.length() == 0 ) {
                        continue;
                    } else {
                        LOG.debug( ">>> start doc <<<" );
                        LOG.debug( "doc: " + doc );
                        LOG.debug( ">>> end doc <<<" );
                    }
                    Document document = null;
                    try {
                        document = builder.parse( new InputSource( new StringReader( doc ) ) );
                    } catch ( Exception e ) {
                        e.printStackTrace();

                        continue;
                    } catch ( Error e ) {
                        e.printStackTrace();
                        continue;
                    }

                    ValidationDefinitions definitions = validationDefinitions;
                    if ( partnerSpecific ) {
                        LOG.debug( "partnerValidationDefinitions: " + partnerValidationDefinitions );
                        LOG.debug( "getPartnerId: "
                                + messageContext.getMessagePojo().getParticipant().getPartner().getPartnerId() );
                        definitions = partnerValidationDefinitions.get( messageContext.getMessagePojo()
                                .getParticipant().getPartner().getPartnerId() );
                        LOG.debug( "definitions: " + definitions );
                    }

                    // Validate document
                    document = processValidation( document, messageContext, definitions );

                    // Serialize result
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    TransformerFactory transformerFactory = TransformerFactory.newInstance();
                    Transformer transformer = transformerFactory.newTransformer();

                    String outputEncoding = "UTF-8"; // or use "ISO-8859-1"
                    if ( ( definitions.getOutputEncoding() != null )
                            && ( definitions.getOutputEncoding().length() != 0 ) ) {
                        outputEncoding = definitions.getOutputEncoding();
                    }

                    LOG.debug( "Using output encoding: " + outputEncoding );

                    transformer.setOutputProperty( OutputKeys.ENCODING, outputEncoding );

                    transformer.transform( new DOMSource( document ), new StreamResult( baos ) );
                    payload.setPayloadData( baos.toByteArray() );
                }
            }

        } catch ( Exception e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return messageContext;
    }

    /**
     * @param configFileName
     * @return
     */
    private PartnerSpecificConfigurations readPartnerSpecificConfigurations( String configFileName ) {

        PartnerSpecificConfigurations partnerSpecificConfigurations = null;
        Digester digester = new Digester();
        digester.setValidating( false );
        digester.addObjectCreate( "PartnerSpecificConfigurations",
                "org.nexuse2e.backend.pipelets.helper.PartnerSpecificConfigurations" );
        digester.addSetProperties( "PartnerSpecificConfigurations" );
        digester.addObjectCreate( "PartnerSpecificConfigurations/PartnerSpecificConfiguration",
                "org.nexuse2e.backend.pipelets.helper.PartnerSpecificConfiguration" );
        digester.addSetProperties( "PartnerSpecificConfigurations/PartnerSpecificConfiguration" );
        digester.addSetNext( "PartnerSpecificConfigurations/PartnerSpecificConfiguration",
                "addPartnerSpecificConfiguration", "org.nexuse2e.backend.pipelets.helper.PartnerSpecificConfiguration" );

        try {
            partnerSpecificConfigurations = (PartnerSpecificConfigurations) digester.parse( configFileName );
        } catch ( IOException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch ( SAXException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return partnerSpecificConfigurations;
    }

    /**
     * @param configFileName
     * @return
     */
    private ValidationDefinitions readConfiguration( String configFileName ) {

        ValidationDefinitions validationDefinitions = null;
        Digester digester = new Digester();
        digester.setValidating( false );
        digester.addObjectCreate( "ValidationDefinitions", "org.nexuse2e.tools.validation.ValidationDefinitions" );
        digester.addSetProperties( "ValidationDefinitions" );
        digester.addObjectCreate( "ValidationDefinitions/ValidationDefinition",
                "org.nexuse2e.tools.validation.ValidationDefinition" );
        digester.addSetProperties( "ValidationDefinitions/ValidationDefinition" );
        digester.addSetNext( "ValidationDefinitions/ValidationDefinition", "addValidationDefinition",
                "org.nexuse2e.tools.validation.ValidationDefinition" );

        try {
            validationDefinitions = (ValidationDefinitions) digester.parse( configFileName );
        } catch ( IOException e ) {
            e.printStackTrace();
        } catch ( SAXException e ) {
            e.printStackTrace();
        }
        return validationDefinitions;
    }

    /**
     * @param args
     */
    public static void main( String args[] ) {

        if ( args.length != 2 ) {
            System.err.println( "Wrong number of parameters. Usage: XMLValidationPipelet <xml file> <config file>" );
            return;
        }
        try {
            XMLValidationPipelet xmlValidationPipelet = new XMLValidationPipelet();
            ValidationDefinitions validationDefinitions = xmlValidationPipelet.readConfiguration( args[1] );
            xmlValidationPipelet.setValidationDefinitions( validationDefinitions );
            xmlValidationPipelet.setMappingService( new DataConversionService() );
            xmlValidationPipelet.setPartnerSpecific( true );
            System.out.println( "MappingDefinitions: " + validationDefinitions );

            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder;
            builder = documentBuilderFactory.newDocumentBuilder();
            Document document = builder.parse( args[0] );

            MessageContext mc = new MessageContext();
            document = xmlValidationPipelet.processValidation( document, mc, validationDefinitions );

            // Serialize result
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.transform( new DOMSource( document ), new StreamResult( baos ) );

            if ( mc != null ) {
                for ( ErrorDescriptor descriptor : mc.getErrors() ) {
                    System.out.println( "errorMessage: " + descriptor.getErrorCode() );
                }
            }

            System.out.println( "Result: " + baos.toString() );
        } catch ( Exception e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * @param document
     * @param messageContext
     * @return
     */
    private Document processValidation( Document document, MessageContext messageContext,
            ValidationDefinitions definitions ) {

        Document result = null;
        try {
            XPath xPath = XPathFactory.newInstance().newXPath();

            for ( ValidationDefinition definition : definitions.getValidationDefinitions() ) {

                Node node = (Node) xPath.evaluate( definition.getXpath(), document, XPathConstants.NODE );
                if ( node != null ) {
                    if ( node instanceof Element ) {
                        String value = ( (Element) node ).getTextContent();
                        String resultValue = mapData( xPath, document, value, definition );
                        if ( resultValue == null || resultValue.length() != value.length() ) {
                            if ( messageContext != null ) {
                                ErrorDescriptor rd = new ErrorDescriptor();
                                rd.setDescription( "Invalid Field: " + node.getNodeName() );
                                if ( resultValue == null ) {
                                    rd.setErrorCode( definition.getFatalCode() );
                                } else {
                                    rd.setErrorCode( definition.getModifiedCode() );
                                }
                                rd.setSeverity( Severity.ERROR );
                                messageContext.addError( rd );
                            }
                            if ( definition.getDefaultValue() != null ) {
                                resultValue = definition.getDefaultValue();
                            }
                        }
                        ( (Element) node ).setTextContent( resultValue );
                    } else if ( node instanceof Attr ) {
                        String nodeValue = ( (Attr) node ).getNodeValue();
                        String resultValue = mapData( xPath, document, nodeValue, definition );
                        ( (Attr) node ).setNodeValue( resultValue );
                    } else {
                        LOG.error( "Node type not recognized: " + node.getClass() );
                    }
                } else {
                    LOG.warn( "Could not find matching node for " + definition.getXpath() );
                }
            }
            result = document;
        } catch ( XPathExpressionException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch ( DOMException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch ( Exception e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    private String mapData( XPath xPath, Document document, String textContent, ValidationDefinition definition ) {

        //        System.out.println( "mapping Data..." );
        if ( mappingService != null ) {
            System.out.println( "value: " + textContent );
            MappingDefinition mappingDef = new MappingDefinition();
            mappingDef.setCommand( definition.getCommand() );
            return mappingService.processConversion( xPath, document, textContent, mappingDef );
        } else {
            LOG.error( "MappingService must be configured" );
        }
        return null;
    }

    /**
     * @return the validationDefinitions
     */
    public ValidationDefinitions getValidationDefinitions() {

        return validationDefinitions;
    }

    /**
     * @param validationDefinitions the validationDefinitions to set
     */
    public void setValidationDefinitions( ValidationDefinitions validationDefinitions ) {

        this.validationDefinitions = validationDefinitions;
    }

    /**
     * @return the mappingService
     */
    public DataConversionService getMappingService() {

        return mappingService;
    }

    /**
     * @param mappingService the mappingService to set
     */
    public void setMappingService( DataConversionService mappingService ) {

        this.mappingService = mappingService;
    }

    /**
     * @return the partnerSpecific
     */
    public boolean isPartnerSpecific() {

        return partnerSpecific;
    }

    /**
     * @param partnerSpecific the partnerSpecific to set
     */
    public void setPartnerSpecific( boolean partnerSpecific ) {

        this.partnerSpecific = partnerSpecific;
    }

    /**
     * @return the partnerValidationDefinitions
     */
    public HashMap<String, ValidationDefinitions> getPartnerValidationDefinitions() {

        return partnerValidationDefinitions;
    }

    /**
     * @param partnerValidationDefinitions the partnerValidationDefinitions to set
     */
    public void setPartnerValidationDefinitions( HashMap<String, ValidationDefinitions> partnerValidationDefinitions ) {

        this.partnerValidationDefinitions = partnerValidationDefinitions;
    }
}
