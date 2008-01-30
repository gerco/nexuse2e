package org.nexuse2e.messaging.cidx;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.EnumerationParameter;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.Constants.ParameterType;
import org.nexuse2e.messaging.AbstractPipelet;
import org.nexuse2e.messaging.MessageContext;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * This pipelet extracts the routing information
 *
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public class CidxRoutingPipelet extends AbstractPipelet {

    public static final String ACTION_MAP_PARAMETER_NAME = "actionMap";
    
    
    public CidxRoutingPipelet() {
        setFrontendPipelet( true );
    }
    
    
    @Override
    public Map<String, ParameterDescriptor> getParameterMap() {
        parameterMap.put(
                ACTION_MAP_PARAMETER_NAME,
                new ParameterDescriptor( ParameterType.ENUMERATION, "Parameter map", "", new EnumerationParameter() ) );
        return parameterMap;
    }

    @Override
    public MessageContext processMessage( MessageContext messageContext )
            throws IllegalArgumentException, IllegalStateException,
            NexusException {

        byte[] data = (byte[]) messageContext.getData();
        if (data == null) {
            throw new NexusException( "Payload must not be null" );
        }
        
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware( false );
            DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
            XPath xPath = XPathFactory.newInstance().newXPath();
            Document document = builder.parse( new ByteArrayInputStream( data ) );
            
        } catch (ParserConfigurationException e) {
            throw new NexusException( e );
        } catch (SAXException e) {
            throw new NexusException( e );
        } catch (IOException e) {
            throw new NexusException( e );
        }
        
        return messageContext;
    }
}
