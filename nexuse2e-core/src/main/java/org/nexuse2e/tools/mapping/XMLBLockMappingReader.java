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
package org.nexuse2e.tools.mapping;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.nexuse2e.tools.mapping.xml.XMLBlock;
import org.nexuse2e.tools.mapping.xml.XMLBlockContainer;
import org.nexuse2e.tools.mapping.xml.XMLBlockEntry;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * @author guido.esch
 */
public class XMLBLockMappingReader {

    private Map<String, XMLBlockContainer> containers      = new HashMap<String, XMLBlockContainer>();
    private XMLBlockContainer              firstContainer  = null;

    /**
     * Comment for <code>BLOCKS</code>
     */
    public static String                   BLOCKS          = "blocks";
    /**
     * Comment for <code>BLOCK</code>
     */
    public static String                   BLOCK           = "block";
    /**
     * Comment for <code>XPATH</code>
     */
    public static String                   XPATH           = "xpath";
    /**
     * Comment for <code>ENTRYID</code>
     */
    public static String                   ENTRYID         = "id";
    /**
     * Comment for <code>POS</code>
     */
    public static String                   BLOCKID         = "id";
    /**
     * Comment for <code>POS</code>
     */
    public static String                   POS             = "position";
    /**
     * Comment for <code>NODE</code>
     */
    public static String                   NODE            = "node";
    /**
     * 
     */
    public static String                   LENGTH          = "length";
    /**
     * 
     */
    public static String                   TRIM            = "trim";
    /**
     * 
     */
    public static String                   ALIGN           = "align";
    /**
     * 
     */
    public static String                   FILLER          = "filler";
    /**
     * 
     */
    public static String                   METHOD          = "method";

    /**
     * Comment for <code>SIBLINGSEQUENCE</code>
     */
    public static String                   SIBLINGSEQUENCE = "siblingsequence";

    /**
     * @param file
     */
    public void parseMappingFile( String file ) {

        try {
            File mappingFile = new File( file );
            if ( mappingFile.exists() ) {

                FileReader fileReaderXml = new FileReader( mappingFile );
                InputSource input = new InputSource( fileReaderXml );

                DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
                docBuilderFactory.setValidating( false );
                docBuilderFactory.setNamespaceAware( true );
                DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
                docBuilder.setErrorHandler( new ErrorHandler() {

                    public void fatalError( SAXParseException exception ) throws SAXException {

                        System.out.println( "Fatal Error XMLHelper: static: Fatal Error: : " + exception );
                        exception.printStackTrace();
                    }

                    public void warning( SAXParseException exception ) throws SAXException {

                        System.out.println( "Warning: " + exception );
                    }

                    public void error( SAXParseException exception ) throws SAXException {

                        System.out.println( "Error: " + exception );
                    }
                } );
                Document document = docBuilder.parse( input );

                XPath xpath = XPathFactory.newInstance().newXPath();
                String expression = "/" + BLOCKS;
                Node root = (Node) xpath.evaluate( expression, document, XPathConstants.NODE );

                NamedNodeMap attribs = root.getAttributes();
                Node attr = attribs.getNamedItem( "id" );
                String nodeValue = null;
                if ( attr != null ) {
                    nodeValue = attr.getNodeValue();
                }
                XMLBlockContainer container = new XMLBlockContainer();
                container.setXmlContainerID( nodeValue );
                containers.put( nodeValue, container );
                if ( firstContainer == null ) {
                    firstContainer = container;
                }

                xpath = XPathFactory.newInstance().newXPath();
                expression = "/" + BLOCKS + "/" + BLOCK;
                NodeList blocks = (NodeList) xpath.evaluate( expression, document, XPathConstants.NODESET );

                for ( int i = 0; i < blocks.getLength(); i++ ) {
                    Node recordNode = blocks.item( i );
                    container.addXMLBLock( parseBlock( recordNode ) );
                }

            } else {
                System.out.println( "XML Block File: " + mappingFile.toString() + " doesn't exist!" ); //$NON-NLS-2$
            }
        } catch ( Exception e ) {

            //            Plugin
            //                    .getDefault()
            //                    .log(
            //                            new LogMessage(
            //                                    LogMessage.ERROR,
            //                                    "Processing", e.getClass().getName(), this, "parseMappingFile", 166, e.getLocalizedMessage(), e ) ); 
            System.out.println( "Error: " + e.getLocalizedMessage() );
            e.printStackTrace();
        }

    }

    /**
     * @param blockNode
     * @return xmlBlock
     */
    public XMLBlock parseBlock( Node blockNode ) {

        if ( blockNode == null ) {
            // System.out.println( "bad!!?!?!?" ); 
            return null;
        }
        XMLBlock block = new XMLBlock();
        NamedNodeMap attribs = blockNode.getAttributes();
        Node attr = attribs.getNamedItem( BLOCKID );
        String nodeValue = null;
        if ( attr != null ) {
            nodeValue = attr.getNodeValue();
            block.setBlockID( nodeValue );
        }
        attr = attribs.getNamedItem( SIBLINGSEQUENCE );
        if ( attr != null ) {
            nodeValue = attr.getNodeValue();
            try {
                block.setSiblingSequence( Integer.parseInt( nodeValue ) );
            } catch ( NumberFormatException e1 ) {
                // System.out.println( "error while parsing siblingsequence:" + nodeValue );
                block.setSiblingSequence( 0 );
            }
        } else {
            // System.out.println("no siblingsequence specified");
            block.setSiblingSequence( 0 );
        }
        try {

            XPath xpath = XPathFactory.newInstance().newXPath();
            String expression = XPATH;
            NodeList fields = (NodeList) xpath.evaluate( expression, blockNode, XPathConstants.NODESET );

            for ( int i = 0; i < fields.getLength(); i++ ) {
                Node field = fields.item( i );
                XMLBlockEntry entry = new XMLBlockEntry();
                block.addBlockEntry( entry );
                attribs = field.getAttributes();
                attr = attribs.getNamedItem( ENTRYID );

                if ( attr != null ) {
                    nodeValue = attr.getNodeValue();
                    entry.setEntryID( nodeValue );
                }
                attr = attribs.getNamedItem( POS );
                if ( attr != null ) {
                    nodeValue = attr.getNodeValue();
                    entry.setPosition( nodeValue );
                }

                attr = attribs.getNamedItem( NODE );
                if ( attr != null ) {
                    nodeValue = attr.getNodeValue();
                    entry.setNode( nodeValue );
                }

                attr = attribs.getNamedItem( LENGTH );
                if ( attr != null ) {
                    nodeValue = attr.getNodeValue();
                    entry.setLength( Integer.parseInt( nodeValue ) );
                }

            }

        } catch ( Exception e ) {
            System.out.println( "Error: " + e.getLocalizedMessage() );
            e.printStackTrace();
        }

        return block;
    }

    /**
     * @return xmlContainer
     */
    public XMLBlockContainer getFirstContainer() {

        return firstContainer;
    }

    /**
     * @param containerID
     * @return xmlContainer
     */
    public XMLBlockContainer getContainerByID( String containerID ) {

        if ( containers == null ) {
            containers = new HashMap<String, XMLBlockContainer>();
            return null;
        }
        return containers.get( containerID );
    }
}