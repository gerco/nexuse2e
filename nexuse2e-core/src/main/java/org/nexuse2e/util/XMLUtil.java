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
package org.nexuse2e.util;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;

import org.apache.log4j.Logger;
import org.apache.xerces.jaxp.SAXParserFactoryImpl;
import org.apache.xml.serialize.LineSeparator;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

/**
 * @author mbreilmann
 *
 */
public class XMLUtil {

    private static Logger LOG = Logger.getLogger( Engine.class );

    /** ************************************************************************************************************************
     * Parse an XML file and return the DOM tree
     ************************************************************************************************************************ */
    public static Document loadXMLFileFromURL( String url ) throws IOException {

        return loadXMLFileFromInputSource( new InputSource( url ) );
    }

    public static Element createNewElement( Document document, Node parentNode, String name ) {

        Element newElement = null;
        try {
            newElement = document.createElement( name );
            parentNode.appendChild( newElement );
        } catch ( DOMException domEx ) {
            System.err.println( "XMLHelper: createNewElement: Could not append record to DOM " + domEx );
        }
        return newElement;
    } // create a Node

    public static void serializeDocToDisk( Document document, String location, boolean resolveName )
            throws NexusException {

        String absoluteLocation = null;
        try {
            absoluteLocation = location;
            LOG.debug( "#### XMLHelper: serializeDocToDisk: Location : " + absoluteLocation );

            OutputFormat format = new OutputFormat( document );
            format.setLineSeparator( LineSeparator.Windows );
            format.setIndenting( true );
            format.setLineWidth( 0 );
            format.setPreserveSpace( true );
            XMLSerializer serializer = new XMLSerializer( new FileWriter( absoluteLocation ), format );
            serializer.asDOMSerializer();
            serializer.serialize( document );
        } catch ( FileNotFoundException fntfndEx ) {
            LOG.error( "XMLHelper: serializeDocToDIsk: Could not update TPA " + fntfndEx );
            throw new NexusException( "XMLHelper: serializeDocToDisk: Could not update XML, File not found." );
        } catch ( IOException ioEx ) {
            LOG.error( "XMLHelper: serializeDocToDIsk: IOException rewriting file Error updating TPA : " + ioEx );
            throw new NexusException( "XMLHelper: serializeDocToDisk: Could not update XML, IO error." );
        }

    }

    /** ************************************************************************************************************************
     * Parse an XML file and return the DOM tree
     ************************************************************************************************************************ */
    public static Document loadXMLFileFromStream( InputStream xmlInputStream ) throws IOException {

        return loadXMLFileFromInputSource( new InputSource( xmlInputStream ) );
    } // loadXMLFileFromStream

    public static Document loadXMLFileFromFile( String fileName ) throws IOException {

        if ( fileName.startsWith( "/" ) ) {
            fileName = "file://" + fileName;
        }
        return loadXMLFileFromInputSource( new InputSource( fileName ) );
    } // loadXMLFileFromFile

    /** ************************************************************************************************************************
     * Parse an XML file and return the DOM tree
     ************************************************************************************************************************ */
    public static Document loadXMLFileFromStream( InputStream xmlInputStream, boolean validating ) throws IOException {

        return loadXMLFileFromInputSource( new InputSource( xmlInputStream ), validating );
    } // loadXMLFileFromStream

    /** ************************************************************************************************************************
     * Parse an XML file and return the DOM tree
     ************************************************************************************************************************ */
    public static Document newDocument() {

        Document document = null;
        try {
            Properties properties = System.getProperties();
            properties.setProperty( "javax.xml.parsers.DocumentBuilderFactory",
                    "org.apache.xerces.jaxp.DocumentBuilderFactoryImpl" );
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            document = docBuilder.newDocument();
        } catch ( Exception ex ) {
            LOG.error( "Could not create xml document: " + ex );
        }
        return document;
    }

    /** ************************************************************************************************************************
     * Parse an XML file and return the DOM tree
     ************************************************************************************************************************ */
    public static Document loadXMLFileFromURL( String url, boolean validating ) throws IOException {

        return loadXMLFileFromInputSource( new InputSource( url ), validating );
    }

    public static Element createNewElement( Document document, Node parentNode, String name, String value ) {

        Element newElement = null;
        try {
            newElement = document.createElement( name );
            if ( value != null ) {
                Node textNode = document.createTextNode( value );
                newElement.appendChild( textNode );
            }
            parentNode.appendChild( newElement );
        } catch ( DOMException domEx ) {
            LOG.error( "Could not create new Element : " + name + " Exception: " + domEx );
        }
        return newElement;
    } // create a Node

    public static String serializeDocument( Document document ) {

        String xmlString = null;
        try {
            StringWriter stringWriter = new StringWriter();
            OutputFormat format = new OutputFormat( document );
            format.setLineSeparator( LineSeparator.Windows );
            format.setIndenting( true );
            format.setLineWidth( 0 );
            format.setPreserveSpace( true );
            XMLSerializer serializer = new XMLSerializer( stringWriter, format );
            serializer.asDOMSerializer();
            serializer.serialize( document );

            xmlString = stringWriter.getBuffer().toString();

        } catch ( Exception e ) {
            LOG.error( "Warning: " + e );
            //  e.printStackTrace();
        }

        return xmlString;
    }

    public static Document loadXMLFileFromFile( String fileName, boolean validating ) throws IOException {

        if ( !fileName.startsWith( "file" ) ) {
            if ( fileName.startsWith( "/" ) ) {
                fileName = "file://" + fileName;
            } else {
                fileName = "file:///" + fileName;
            }
        }

        return loadXMLFileFromInputSource( new InputSource( fileName ), validating );
    } // loadXMLFileFromFile

    /** ************************************************************************************************************************
     * Parse an XML file and return the DOM tree
     ************************************************************************************************************************ */
    public static Document loadXMLFileFromInputSource( InputSource inputSource, boolean validating ) throws IOException {

        Document document = null;
        try {
            // Properties properties = System.getProperties();

            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            docBuilderFactory.setValidating( validating );
            docBuilderFactory.setNamespaceAware( true );
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            docBuilder.setErrorHandler( new ErrorHandler() {

                public void fatalError( SAXParseException exception ) throws SAXException {

                    LOG.error( "Fatal Error XMLHelper: static: Fatal Error: : " + exception );
                    exception.printStackTrace();
                }

                public void warning( SAXParseException exception ) throws SAXException {

                    LOG.warn( "Warning: " + exception );
                }

                public void error( SAXParseException exception ) throws SAXException {

                    LOG.error( "Error: " + exception );
                }
            } );
            document = docBuilder.parse( inputSource );

        } catch ( IOException ioEx ) {
            throw new IOException( "Could not parse XML document: " + ioEx );
            // System.err.println( "Could not read xml document from input stream (IOException): " + ioEx );
        } catch ( Exception ex ) {
            throw new IOException( "Could not parse XML document: " + ex );
            // System.err.println( "Could not read xml document from input stream (Exception): " + ex );
        }
        return document;
    }

    /*
     public static void printXML( Document document ) {

     try {
     new org.apache.xml.serialize.XMLSerializer( Log.getLogFile(), null ).asDOMSerializer().serialize( document );
     } caLOG.error( "Error serializing document!" );
     }
     }
     */

    /** ************************************************************************************************************************
     * Parse an XML file and return the DOM tree
     ************************************************************************************************************************ */
    public static Document loadXMLFileFromInputSource( InputSource inputSource ) throws IOException {

        return loadXMLFileFromInputSource( inputSource, true );
    }

    /** ************************************************************************************************************************
     * Parse an XML file and return the DOM tree
     ************************************************************************************************************************ */
    public static Document loadXMLFileFromString( String inputString ) throws IOException {

        return loadXMLFileFromString( inputString, true );
    }

    /** ************************************************************************************************************************
     * Parse an XML file and return the DOM tree
     ************************************************************************************************************************ */
    public static Document loadXMLFileFromString( String inputString, boolean validating ) throws IOException {

        return loadXMLFileFromInputSource( new InputSource( new StringReader( inputString ) ), validating );
    }

    public static XMLReader getSaxParser( boolean validating ) {

        XMLReader xmlReader = null;

        try {

            SAXParserFactoryImpl spfi = new SAXParserFactoryImpl();
            spfi.setFeature( "http://xml.org/sax/features/namespaces", true );
            SAXParser spi = spfi.newSAXParser();
            xmlReader = spi.getXMLReader();
        } catch ( Exception saxEx ) {
            saxEx.printStackTrace();
        }

        return xmlReader;
    }

} // XMLUtil
