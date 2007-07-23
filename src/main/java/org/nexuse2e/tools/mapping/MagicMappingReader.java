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


import org.nexuse2e.tools.mapping.magic.Magic;
import org.nexuse2e.tools.mapping.magic.MagicContainer;
import org.nexuse2e.tools.mapping.magic.MagicEntry;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;




/**
 * @author guido.esch
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MagicMappingReader {

    private Map<String,MagicContainer> containers = new HashMap<String,MagicContainer>();
    private MagicContainer firstContainer = null;
    /**
     * Comment for <code>MOREMAGIC</code>
     */
    public static String   MOREMAGIC      = "moremagic";    //$NON-NLS-1$
    /**
     * Comment for <code>MAGIC</code>
     */
    public static String   MAGIC          = "magic";        //$NON-NLS-1$
    /**
     * Comment for <code>MAPPING</code>
     */
    public static String   MAPPING        = "mapping";      //$NON-NLS-1$
    /**
     * Comment for <code>XPATHID</code>
     */
    public static String   XPATHID        = "xpathid";      //$NON-NLS-1$
    /**
     * Comment for <code>VALUE</code>
     */
    public static String   VALUE          = "value";        //$NON-NLS-1$

    /**
     * @param file
     */
    public void parseMappingFile( String file ) {

        try {
            File mappingFile = new File( file );
            if ( mappingFile.exists() ) {
                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

                FileReader fileReaderXml = new FileReader( mappingFile );
                InputSource input = new InputSource( fileReaderXml );
                Document document = documentBuilder.parse( input );

                XPath xpath = XPathFactory.newInstance().newXPath();
                Node root = (Node) xpath.evaluate("/" + MOREMAGIC , document, XPathConstants.NODE);
                
                NamedNodeMap attribs = root.getAttributes();
                Node attr = attribs.getNamedItem( "id" ); //$NON-NLS-1$
                String nodeValue = null;
                if ( attr != null ) {
                    nodeValue = attr.getNodeValue();
                }
                MagicContainer container = new MagicContainer();
                container.setContainerID( nodeValue );
                if ( firstContainer == null ) {
                    firstContainer = container;
                }
                containers.put( nodeValue, container );
                
                
                xpath = XPathFactory.newInstance().newXPath();
                NodeList blocks = (NodeList) xpath.evaluate("/" + MOREMAGIC + "/" + MAGIC , document, XPathConstants.NODESET);
                
                
                for ( int i = 0; i < blocks.getLength(); i++ ) {
                    Node recordNode = blocks.item( i );
                    container.addMagic( parseBlock( recordNode ) );
                }

            } else {
                System.out.println( "Magic Mapping File: " + mappingFile.toString() + " doesn't exist!" ); //$NON-NLS-1$ //$NON-NLS-2$
            }
        } catch ( Exception e ) {

//            Plugin
//                    .getDefault()
//                    .log(
//                            new LogMessage(
//                                    LogMessage.ERROR,
//                                    "Processing", e.getClass().getName(), this, "parseMappingFile", 147, e.getLocalizedMessage(), e ) ); //$NON-NLS-1$
            System.out.println("Error: "+e.getLocalizedMessage());
            e.printStackTrace();
        }

    }

    /**
     * @param blockNode
     * @return magic
     */
    public Magic parseBlock( Node blockNode ) {

        if ( blockNode == null ) {
            System.out.println( "bad!!?!?!?" ); //$NON-NLS-1$
            return null;
        }
        Magic magic = new Magic();
        NamedNodeMap attribs = blockNode.getAttributes();
        Node attr = attribs.getNamedItem( "BlockID" ); //$NON-NLS-1$
        if ( attr != null ) {
            magic.setBlockID( attr.getNodeValue() );
        }
        attr = attribs.getNamedItem( "RecordID" ); //$NON-NLS-1$
        if ( attr != null ) {
            magic.setRecordID( attr.getNodeValue() );
        }

        try {
            
            XPath xpath = XPathFactory.newInstance().newXPath();
            NodeList fields = (NodeList) xpath.evaluate(MAPPING , blockNode, XPathConstants.NODESET);
            
            
            for ( int i = 0; i < fields.getLength(); i++ ) {
                Node field = fields.item( i );
                MagicEntry entry = new MagicEntry();
                attribs = field.getAttributes();
                attr = attribs.getNamedItem( XPATHID );

                if ( attr != null ) {

                    entry.setXPathId( attr.getNodeValue() );
                }
                attr = attribs.getNamedItem( VALUE );
                if ( attr != null ) {

                    entry.setValue( attr.getNodeValue() );
                }
                magic.addEntry( entry );
            }

        } catch ( Exception e ) {
//            Plugin
//                    .getDefault()
//                    .log(
//                            new LogMessage(
//                                    LogMessage.ERROR,
//                                    "Processing", e.getClass().getName(), this, "parseBlock", 148, e.getLocalizedMessage(), e ) ); //$NON-NLS-1$
            System.out.println("Error: "+e.getLocalizedMessage());
            e.printStackTrace();
        }

        return magic;
    }

    /**
     * @return magicContainer
     */
    public MagicContainer getFirstContainer() {

        return firstContainer;
    }

    /**
     * @param containerID
     * @return magicContainer
     */
    public MagicContainer getContainerByID( String containerID ) {

        if ( containers == null ) {
            containers = new HashMap<String,MagicContainer>();
            return null;
        }
        return (MagicContainer) containers.get( containerID );
    }
}