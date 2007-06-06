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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.nexuse2e.NexusException;
import org.nexuse2e.backend.pipelets.helper.RequestResponseData;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.Constants.ParameterType;
import org.nexuse2e.messaging.AbstractPipelet;
import org.nexuse2e.messaging.MessageContext;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author mbreilmann
 *
 */
public class XML2ParameterMapPipelet extends AbstractPipelet {

    private static Logger      LOG            = Logger.getLogger( XML2ParameterMapPipelet.class );

    public static final String USE_DATA_FIELD = "useDataField";

    private boolean            useDataField   = true;

    public XML2ParameterMapPipelet() {

        parameterMap.put( USE_DATA_FIELD, new ParameterDescriptor( ParameterType.STRING, "Use Data Field",
                "Use the MessageContext Data Field to retrieve XML.", "" ) );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.AbstractPipelet#initialize(org.nexuse2e.configuration.EngineConfiguration)
     */
    @Override
    public void initialize( EngineConfiguration config ) {

        Boolean useDataFieldValue = getParameter( USE_DATA_FIELD );
        if ( useDataFieldValue != null ) {
            useDataField = useDataFieldValue.booleanValue();
        }

        LOG.trace( "useDataField  : " + useDataField );

        super.initialize( config );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.AbstractPipelet#processMessage(org.nexuse2e.messaging.MessageContext)
     */
    @Override
    public MessageContext processMessage( MessageContext messageContext ) throws IllegalArgumentException,
            IllegalStateException, NexusException {

        RequestResponseData requestResponseData = null;

        if ( ( messageContext.getData() == null ) || !( messageContext.getData() instanceof RequestResponseData ) ) {
            LOG.error( "Wrong class detected in data field, found " + messageContext.getData().getClass() );
            throw new NexusException( "Wrong class detected in data field, found "
                    + messageContext.getData().getClass() );
        }
        requestResponseData = (RequestResponseData) messageContext.getData();

        InputSource inputSource = new InputSource( new StringReader( requestResponseData.getResponseString() ) );
        
        Map map = flattenXML( inputSource );
        
        requestResponseData.setParameters( map );

        return messageContext;
    }

    private Map<String, String> flattenXML( InputSource xmlSource ) throws NexusException {

        Map<String, String> map = new HashMap<String, String>();

        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        try {
            SAXParser saxParser = saxParserFactory.newSAXParser();

            DefaultHandler genericHandler = new GenericHandler( map );

            saxParser.parse( xmlSource, genericHandler );
        } catch ( ParserConfigurationException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();

            throw new NexusException( e );
        } catch ( SAXException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new NexusException( e );
        } catch ( IOException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new NexusException( e );
        }

        return map;
    }

    private class GenericHandler extends DefaultHandler {

        private Map<String, String> map   = null;
        private Stack<String>       stack = new Stack<String>();
        private StringBuffer        value = new StringBuffer();

        protected GenericHandler( Map<String, String> map ) {

            this.map = map;
        }

        public void startElement( String uri, String localName, String qName, Attributes attributes )
                throws SAXException {

            LOG.trace( "startElement: '" + uri + "' - '" + localName + "' - '" + qName + "'" );
            stack.push( qName );
        }

        public void endElement( String uri, String localName, String qName ) throws SAXException {

            LOG.trace( "endElement: '" + localName + "'" );
            StringBuffer path = new StringBuffer();

            for ( Iterator iter = stack.iterator(); iter.hasNext(); ) {
                String element = (String) iter.next();
                path.append( "/" + element );
            }
            LOG.trace( "path: " + path );

            String tempValue = value.toString();

            if ( tempValue.length() != 0 ) {
                map.put( path.toString(), tempValue );
            }

            stack.pop();

            value = new StringBuffer();
        }

        public void characters( char[] ch, int start, int length ) throws SAXException {

            value.append( new String( ch, start, length ).trim() );

            LOG.trace( "String: " + value );
        }

    }

    public static void main( String args[] ) {

        if ( args.length != 1 ) {
            System.err.println( "Wrong number of parameters. Usage: XML2ParameterMapPipelet <xml file>" );
            return;
        }
        try {
            InputSource xmlSource = new InputSource( new FileInputStream( args[0] ) );

            Map<String, String> map = new XML2ParameterMapPipelet().flattenXML( xmlSource );
            for ( Iterator iter = map.keySet().iterator(); iter.hasNext(); ) {
                String key = (String) iter.next();
                System.out.println( key + " - " + map.get( key ) );
            }
        } catch ( Exception e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
} // XML2ParameterMapPipelet
