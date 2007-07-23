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

import org.apache.commons.lang.StringUtils;
import org.nexuse2e.tools.mapping.csv.Record;
import org.nexuse2e.tools.mapping.csv.RecordContainer;
import org.nexuse2e.tools.mapping.csv.RecordEntry;
import org.nexuse2e.tools.mapping.csv.RecordEntry.Align;
import org.nexuse2e.tools.mapping.csv.RecordEntry.Trim;
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
public class CSV2XMLMappingReader {

    /**
     * Comment for <code>RECORDS</code>
     */
    public static String                 RECORDS        = "records";                             //$NON-NLS-1$
    /**
     * Comment for <code>RECORD</code>
     */
    public static String                 RECORD         = "record";                              //$NON-NLS-1$
    /**
     * Comment for <code>FIELDS</code>
     */
    public static String                 FIELDS         = "fields";                              //$NON-NLS-1$
    /**
     * Comment for <code>FIELD</code>
     */
    public static String                 FIELD          = "field";                               //$NON-NLS-1$
    /**
     * Comment for <code>FIELDID</code>
     */
    public static String                 FIELDID        = "id";                                  //$NON-NLS-1$
    /**
     * Comment for <code>SOURCEID</code>
     */
    public static String                 SOURCEID       = "sourceid";                            //$NON-NLS-1$
    /**
     * Comment for <code>TYPE</code>
     */
    public static String                 TYPE           = "type";                                //$NON-NLS-1$
    /**
     * Comment for <code>POS</code>
     */
    public static String                 POS            = "pos";                                 //$NON-NLS-1$
    /**
     * Comment for <code>LENGTH</code>
     */
    public static String                 LENGTH         = "length";                              //$NON-NLS-1$
    /**
     * 
     */
    public static String                 TRIM           = "trim";                              //$NON-NLS-1$
    /**
     * 
     */
    public static String                 ALIGN          = "align";                              //$NON-NLS-1$
    /**
     * 
     */
    public static String                 FILLER         = "filler";                              //$NON-NLS-1$
    /**
     * 
     */
    public static String                 METHOD         = "method";                              //$NON-NLS-1$

    /**
     * Comment for <code>ABSOLUTEID</code>
     */
    public static int                    ABSOLUTEID     = 0;
    /**
     * Comment for <code>RELATIVEID</code>
     */
    public static int                    RELATIVEID     = 1;

    private Map<String, RecordContainer> containers     = new HashMap<String, RecordContainer>();
    private RecordContainer              firstContainer = null;

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
                Node root = (Node) xpath.evaluate( "/" + RECORDS, document, XPathConstants.NODE );

                NamedNodeMap attribs = root.getAttributes();
                Node attr = attribs.getNamedItem( "id" ); //$NON-NLS-1$
                String nodeValue = null;
                if ( attr != null ) {
                    nodeValue = attr.getNodeValue();
                }
                RecordContainer container = new RecordContainer();
                container.setContainerID( nodeValue );
                if ( firstContainer == null ) {
                    firstContainer = container;
                }
                containers.put( nodeValue, container );

                attr = attribs.getNamedItem( "separator" ); //$NON-NLS-1$
                nodeValue = ","; //$NON-NLS-1$
                if ( attr != null ) {
                    nodeValue = attr.getNodeValue();
                }
                container.setSeparator( nodeValue );

                xpath = XPathFactory.newInstance().newXPath();
                NodeList records = (NodeList) xpath.evaluate( "/" + RECORDS + "/" + RECORD, document,
                        XPathConstants.NODESET );

                for ( int i = 0; i < records.getLength(); i++ ) {
                    Node recordNode = records.item( i );
                    container.addRecord( parseRecord( recordNode ) );
                }

            } else {
                System.out.println( "CSV2XML MappingFile: " + mappingFile.toString() + " doesn't exist!" ); //$NON-NLS-1$ //$NON-NLS-2$
            }
        } catch ( Exception e ) {

            //            Plugin
            //                    .getDefault()
            //                    .log(
            //                            new LogMessage(
            //                                    LogMessage.ERROR,
            //                                    "Processing", e.getClass().getName(), this, "parseMappingFile", 144, e.getLocalizedMessage(), e ) ); //$NON-NLS-1$
            System.out.println( "Error: " + e.getLocalizedMessage() );
            e.printStackTrace();
        }

    }

    /**
     * @param recordNode
     * @return record
     */
    public Record parseRecord( Node recordNode ) {

        if ( recordNode == null ) {
            System.out.println( "bad!!?!?!?" ); //$NON-NLS-1$
            return null;
        }
        Record record = new Record();
        NamedNodeMap attribs = recordNode.getAttributes();
        Node attr = attribs.getNamedItem( "value" ); //$NON-NLS-1$
        String nodeValue = null;
        if ( attr != null ) {
            nodeValue = attr.getNodeValue();
            record.setRecordValue( nodeValue );
        }

        attr = attribs.getNamedItem( "active" ); //$NON-NLS-1$
        nodeValue = null;
        if ( attr != null ) {
            nodeValue = attr.getNodeValue();
            if ( nodeValue.equals( "false" ) ) {
                record.setActive( false );
            } else {
                record.setActive( true );
            }
        } else {
            record.setActive( true );
        }

        attr = attribs.getNamedItem( "id" ); //$NON-NLS-1$
        nodeValue = null;
        if ( attr != null ) {
            nodeValue = attr.getNodeValue();
            record.setRecordID( nodeValue );
        }
        attr = attribs.getNamedItem( "conversationclass" ); //$NON-NLS-1$
        if ( attr != null ) {
            nodeValue = attr.getNodeValue();
            if(!StringUtils.isEmpty( nodeValue )) {
                record.setConversionClass( nodeValue );
            }
        }

        try {
            XPath xpath = XPathFactory.newInstance().newXPath();
            NodeList fields = (NodeList) xpath.evaluate(FIELDS + "/" + FIELD , recordNode, XPathConstants.NODESET);
            
            for ( int i = 0; i < fields.getLength(); i++ ) {
                Node field = fields.item( i );
                RecordEntry entry = new RecordEntry();
                record.addEntry( entry );
                attribs = field.getAttributes();
                attr = attribs.getNamedItem( FIELDID );

                if ( attr != null ) {
                    nodeValue = attr.getNodeValue();
                    entry.setEntryID( nodeValue );
                }
                attr = attribs.getNamedItem( SOURCEID );
                if ( attr != null ) {
                    nodeValue = attr.getNodeValue();
                    entry.setSourceID( nodeValue );
                }

                attr = attribs.getNamedItem( TYPE );
                if ( attr != null ) {
                    nodeValue = attr.getNodeValue();
                    if ( nodeValue.equals( "absolute" ) ) { //$NON-NLS-1$
                        entry.setType( ABSOLUTEID );
                    } else {
                        entry.setType( RELATIVEID );
                    }
                }
                attr = attribs.getNamedItem( POS );
                if ( attr != null ) {
                    nodeValue = attr.getNodeValue();
                    int value;
                    try {
                        value = Integer.parseInt( nodeValue );
                    } catch ( NumberFormatException e1 ) {
                        value = -1;
                    }
                    entry.setPosition( value );
                }
                attr = attribs.getNamedItem( LENGTH );
                if ( attr != null ) {
                    nodeValue = attr.getNodeValue();
                    int value;
                    try {
                        value = Integer.parseInt( nodeValue );
                    } catch ( NumberFormatException e1 ) {
                        value = -1;
                    }
                    entry.setLength( value );
                }
                attr = attribs.getNamedItem( FILLER );
                if ( attr != null ) {
                    nodeValue = attr.getNodeValue();
                    if(!StringUtils.isEmpty( nodeValue)) {
                        entry.setFiller( nodeValue.substring( 0,1 ) );
                    } else {
                        entry.setFiller(" ");
                    }
                } else {
                    entry.setFiller(" ");
                }
                attr = attribs.getNamedItem( METHOD );
                if ( attr != null ) {
                    nodeValue = attr.getNodeValue();
                    if(nodeValue != null&& record.getConversionClass() != null) {
                        
                        entry.setMethod( nodeValue.trim() );
                    }
                }
                attr = attribs.getNamedItem( ALIGN );
                if ( attr != null ) {
                    nodeValue = attr.getNodeValue();
                    if(!StringUtils.isEmpty(nodeValue)) {
                        if(nodeValue.trim().toLowerCase().equals( "left" )) {
                            entry.setAlign( Align.LEFT );    
                        } else if(nodeValue.trim().toLowerCase().equals( "right" )) {
                            entry.setAlign( Align.RIGHT );    
                        }
                        
                    }
                }
                attr = attribs.getNamedItem( TRIM );
                if ( attr != null ) {
                    nodeValue = attr.getNodeValue();
                    if(!StringUtils.isEmpty(nodeValue)) {
                        if(nodeValue.trim().toLowerCase().equals( "false" )) {
                            entry.setTrim( Trim.FALSE );    
                        } else if(nodeValue.trim().toLowerCase().equals( "true" )) {
                            entry.setTrim( Trim.TRUE );        
                        } else if(nodeValue.trim().toLowerCase().equals( "whitespaces" )) {
                            entry.setTrim( Trim.WHITESPACES );        
                        }
                        
                    }
                }

            }

        } catch ( Exception e ) {
//            Plugin
//                    .getDefault()
//                    .log(
//                            new LogMessage(
//                                    LogMessage.ERROR,
//                                    "Processing", e.getClass().getName(), this, "parseRecord", 145, e.getLocalizedMessage(), e ) ); //$NON-NLS-1$
            System.out.println("Error: "+e.getLocalizedMessage());
            e.printStackTrace();
        }

        return record;
    }

    /**
     * @return recordContainer
     */
    public RecordContainer getFirstContainer() {

        return firstContainer;
    }

    /**
     * @param containerID
     * @return recordContainer
     */
    public RecordContainer getContainerByID( String containerID ) {

        if ( containers == null ) {
            containers = new HashMap<String, RecordContainer>();
            return null;
        }
        return (RecordContainer) containers.get( containerID );
    }

    /**
     * @return containers
     */
    public Map<String, RecordContainer> getContainers() {

        return containers;
    }

    /**
     * @param containers
     */
    public void setContainers( Map<String, RecordContainer> containers ) {

        this.containers = containers;
    }
}