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
package org.nexuse2e.tools.mapping.conversation;

import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.nexuse2e.tools.mapping.CSVMappingFileEntry;
import org.nexuse2e.tools.mapping.csv.Record;
import org.nexuse2e.tools.mapping.csv.RecordEntry;
import org.nexuse2e.tools.mapping.input.CSVLine;
import org.nexuse2e.tools.mapping.magic.Magic;
import org.nexuse2e.tools.mapping.magic.MagicEntry;
import org.nexuse2e.tools.mapping.xml.XMLBlock;
import org.nexuse2e.tools.mapping.xml.XMLBlockEntry;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * @author guido.esch
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ProcessXML {

    /**
     * Comment for <code>reader</code>
     */
    public CSV2XMLMappingReader  reader;
    /**
     * Comment for <code>blockReader</code>
     */
    public XMLBLockMappingReader blockReader;
    /**
     * Comment for <code>magicReader</code>
     */
    public MagicMappingReader    magicReader;
    private Document             document;

    /**
     * @param args
     */
    public static void main( String[] args ) {

        CSVMappingFileEntry mfe = new CSVMappingFileEntry();

        String xmlBlock = null;
        String csvMapping = null;
        String magic = null;
        String contentPath = null;

        Options options = new Options();
        Option option = new Option( "x", "xmlblocks", true, "the xml blocks file" );
        option.setArgName( "file" );
        option.setRequired( true );
        options.addOption( option );
        option = new Option( "c", "csvmapping", true, "the csv mapping file" );
        option.setArgName( "file" );
        option.setRequired( true );
        options.addOption( option );
        option = new Option( "m", "magic", true, "the magic mapping file" );
        option.setArgName( "file" );
        option.setRequired( true );
        options.addOption( option );
        option = new Option( "i", "input", true, "the input xml file" );
        option.setArgName( "file" );
        option.setRequired( true );
        options.addOption( option );

        CommandLineParser parser = new PosixParser();

        try {
            CommandLine cl = parser.parse( options, args );

            if ( !cl.hasOption( "x" ) || !cl.hasOption( "c" ) || !cl.hasOption( "m" ) || !cl.hasOption( "i" ) ) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp( ProcessXML.class.getName(), options );
                System.exit( 1 );
            }
            
            xmlBlock = cl.getOptionValue( "x" );
            csvMapping = cl.getOptionValue( "c" );
            magic = cl.getOptionValue( "m" );
            contentPath = cl.getOptionValue( "i" );
            
        } catch ( ParseException e1 ) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( ProcessXML.class.getName(), options );
            System.exit( 1 );
        }

        System.out.println("xmlBlock: "+xmlBlock);
        System.out.println("csvMapping: "+csvMapping);
        System.out.println("magic: "+magic);
        System.out.println("contentPath: "+contentPath);
        
        //        String xmlBlock = "d:/eclipse-SDK-3.0.1-win32/repository/mappings/test_OrderCreate_blocks.xml"; //$NON-NLS-1$
        //        String csvMapping = "d:/eclipse-SDK-3.0.1-win32/repository/mappings/test_flat.xml"; //$NON-NLS-1$
        //        String magic = "d:/eclipse-SDK-3.0.1-win32/repository/mappings/test_map.xml"; //$NON-NLS-1$
        //        String contentPath = "C:/Dokumente und Einstellungen/guido.esch/Desktop/umzug/OrderExample-1.xml"; //$NON-NLS-1$

        mfe.setCsvmappings( csvMapping );
        mfe.setXmlblocks( xmlBlock );
        mfe.setMapping( magic );
        mfe.setId( "test-mapping" ); //$NON-NLS-1$

        try {
            File contentFile = new File( contentPath );
            byte[] dataArray = new byte[(int) contentFile.length()];
            FileInputStream fis = new FileInputStream( contentFile );
            fis.read( dataArray );
            fis.close();
            String contentString = new String( dataArray );
            ProcessXML process = new ProcessXML();
            System.out.println( process.processXML( mfe, contentString ) );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /**
     * @param mfe
     * @param content
     * @return result
     */
    public String processXML( CSVMappingFileEntry mfe, String content ) {

        File testFile = new File( mfe.getCsvmappings() );
        if ( !testFile.exists() ) {
            //            Plugin.getDefault().log(
            //                    new LogMessage( LogMessage.ERROR,
            //                            "Processing", "Error", this, "processXML", 160, "mappingfile: " + testFile //$NON-NLS-1$ //$NON-NLS-2$
            //                                    + " doesn't exist, processing canceled!!", null ) ); //$NON-NLS-1$
            System.out.println("csv mapping file: " + testFile + " doesn't exist, processing canceled!!");
            return null;
        }
        testFile = new File( mfe.getMapping() );
        if ( !testFile.exists() ) {
            //            Plugin.getDefault().log(
            //                    new LogMessage( LogMessage.ERROR,
            //                            "Processing", "Error", this, "processXML", 161, "mappingfile: " + testFile //$NON-NLS-1$ //$NON-NLS-2$
            //                                    + " doesn't exist, processing canceled!!", null ) ); //$NON-NLS-1$
            System.out.println("magic file: " + testFile  + " doesn't exist, processing canceled!!");
            return null;
        }
        testFile = new File( mfe.getXmlblocks() );
        if ( !testFile.exists() ) {
            //            Plugin.getDefault().log(
            //                    new LogMessage( LogMessage.ERROR,
            //                            "Processing", "Error", this, "processXML", 162, "mappingfile: " + testFile //$NON-NLS-1$ //$NON-NLS-2$
            //                                    + " doesn't exist, processing canceled!!", null ) ); //$NON-NLS-1$
            System.out.println("XML Blocks file: " + testFile + " doesn't exist, processing canceled!!");
            return null;
        }

        reader = new CSV2XMLMappingReader();
        reader.parseMappingFile( mfe.getCsvmappings() );
        blockReader = new XMLBLockMappingReader();
        blockReader.parseMappingFile( mfe.getXmlblocks() );
        magicReader = new MagicMappingReader();
        magicReader.parseMappingFile( mfe.getMapping() );

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware( true );
        DocumentBuilder documentBuilder;
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch ( ParserConfigurationException e ) {

            //            Plugin.getDefault().log(
            //                    new LogMessage( LogMessage.ERROR,
            //                            "Processing", "Error", this, "processXML", 163, "Can't process Message!", e ) ); //$NON-NLS-1$ //$NON-NLS-2$
            e.printStackTrace();
            return null;
        }
        CSVLine rootLine = null;
        try {
            StringReader sr = new StringReader( content );
            InputSource input = new InputSource( sr );

            document = documentBuilder.parse( input );
            Map<String, XMLBlock> blocksHT = blockReader.getFirstContainer().getBlocks();
            List<XMLBlock> blocks = new ArrayList<XMLBlock>( blocksHT.values() );

            //search root block
            XMLBlock block = null;
            for ( int i = 0; i < blocks.size(); i++ ) {
                block = blocks.get( i );
                if ( isRootBlock( block ) ) {
                    break;
                }
                block = null;
            }
            if ( block == null ) {
                System.out.println( "no root block found!" ); //$NON-NLS-1$
                return null;
            }
            NodeList nl = document.getChildNodes();
            if ( nl == null || nl.getLength() > 1 ) {
                System.out.println( "invalid nodelist:" + nl ); //$NON-NLS-1$
            }
            Node root = nl.item( 0 );
            cleanBlock( block );

            List<CSVLine> lines = new ArrayList<CSVLine>();
            CSVLine templine = processBlock( root, block, root );
            if ( templine == null ) {
                System.out.println( "no rootline found" );
                return "";
            }
            lines.add( templine );
            blocks.remove( block );
            Iterator blockI = blocks.iterator();
            while ( blockI.hasNext() ) {
                XMLBlock b = (XMLBlock) blockI.next();
                if ( b.getBlockEntries() == null || b.getBlockEntries().size() == 0 ) {
                    continue;
                }
                XMLBlockEntry be = b.getBlockEntries().get( 0 );
                if ( be != null ) {

                    XPath xpath = XPathFactory.newInstance().newXPath();
                    NodeList refNodePos = (NodeList) xpath.evaluate( be.getPosition(), root, XPathConstants.NODESET );

                    for ( int iii = 0; iii < refNodePos.getLength(); iii++ ) {
                        Node refNode = refNodePos.item( iii );
                        xpath = XPathFactory.newInstance().newXPath();
                        NodeList refNodes = (NodeList) xpath.evaluate( be.getNode(), refNode, XPathConstants.NODESET );

                        for ( int i = 0; i < refNodes.getLength(); i++ ) {
                            refNode = refNodes.item( i );
                            cleanBlock( b );
                            templine = processBlock( root, b, refNode );
                            if ( templine != null ) {
                                lines.add( templine );
                            } else {
                                System.out.println( "line ignored!" );
                            }
                        }
                    }
                }
            }
            rootLine = sortCSVLines( lines );

        } catch ( Exception e1 ) {
            //            Plugin
            //                    .getDefault()
            //                    .log(
            //                            new LogMessage(
            //                                    LogMessage.ERROR,
            //                                    "Processing", e1.getClass().getName(), this, "processXML", 164, e1.getLocalizedMessage(), e1 ) ); //$NON-NLS-1$
            System.out.println("Error: "+e1.getLocalizedMessage());
            e1.printStackTrace();
        }
        if ( rootLine != null ) {
            return rootLine.toString();
        }
        return ""; //$NON-NLS-1$
    }

    private CSVLine sortCSVLines( List<CSVLine> lines ) {

        Map<Object, CSVLine> refs = new HashMap<Object, CSVLine>();
        for ( CSVLine templine : lines ) {
            refs.put( templine.getRef(), templine );
        }
        CSVLine rootline = (CSVLine) lines.get( 0 );
        if ( rootline == null ) {
            return null;
        }

        for ( CSVLine temp : lines ) {
            Node pointer = ( (Node) temp.getRef() ).getParentNode();
            while ( pointer != null ) {
                CSVLine refLine = refs.get( pointer );
                if ( refLine != null ) {
                    refLine.getChildren().add( temp );
                    break;
                }
                pointer = pointer.getParentNode();
            }
        }
        sortBySiblingSequence( rootline );

        return rootline;
    }

    /**
     * @param line
     */
    private void sortBySiblingSequence( CSVLine line ) {

        final List<CSVLine> children = line.getChildren();

        Collections.sort( children, new Comparator<CSVLine>() {

            public int compare( CSVLine o1, CSVLine o2 ) {

                int ret = o1.getSiblingSequence() - o2.getSiblingSequence();
                return ret;

            }
        } );
        Iterator i = children.iterator();
        while ( i.hasNext() ) {
            sortBySiblingSequence( (CSVLine) i.next() );
        }
    }

    private void cleanBlock( XMLBlock block ) {

        List<XMLBlockEntry> blockentries = block.getBlockEntries();
        Magic m = magicReader.getFirstContainer().getMagicbyXMLBlockID( block.getBlockID() );
        if ( m == null ) {
            block.setBlockEntries( new ArrayList<XMLBlockEntry>() );
            return;
        }
        for ( int i = 0; i < blockentries.size(); i++ ) {
            XMLBlockEntry entry = blockentries.get( i );
            if ( entry.getNode().indexOf( "@" ) == -1 && entry.getNode().indexOf( "()" ) == -1 ) { //$NON-NLS-1$ //$NON-NLS-2$
                blockentries.remove( i );
                i--;
                continue;
            }
            MagicEntry me = m.getEntryByXpathID( entry.getEntryID() );
            if ( me != null && !me.getValue().toLowerCase().startsWith( "file" ) ) { //$NON-NLS-1$
                blockentries.remove( i );
                i--;
                continue;
            }
        }
    }

    private CSVLine processBlock( Node root, XMLBlock block, Node refNode ) {

        CSVLine line = new CSVLine();

        line.ref = refNode;
        line.setSiblingSequence( block.getSiblingSequence() );
        line.addColumn( block.getBlockID(), 0 );
        Iterator i = block.getBlockEntries().iterator();
        while ( i.hasNext() ) {
            XMLBlockEntry be = (XMLBlockEntry) i.next();
            try {

                XPath xpath = XPathFactory.newInstance().newXPath();
                NodeList nl = (NodeList) xpath.evaluate( be.getPosition(), root, XPathConstants.NODESET );

                if ( nl != null ) {

                    for ( int ii = 0; ii < nl.getLength(); ii++ ) {

                        xpath = XPathFactory.newInstance().newXPath();
                        NodeList list = (NodeList) xpath.evaluate( be.getNode(), nl.item( ii ), XPathConstants.NODESET );

                        for ( int iii = 0; iii < list.getLength(); iii++ ) {
                            Node temp = list.item( iii );
                            if ( isValidNode( temp, refNode ) ) {
                                Magic m = magicReader.getFirstContainer().getMagicbyXMLBlockID( block.getBlockID() );
                                if ( m != null ) {
                                    MagicEntry me = m.getEntryByXpathID( be.getEntryID() );
                                    Record r = reader.getFirstContainer().getRecordByRecordID( m.getRecordID() );
                                    if ( !r.isActive() ) {
                                        return null;
                                    }
                                    if ( line.getId() == null || line.getId().equals( "" ) ) //$NON-NLS-1$
                                    {
                                        if ( reader.getFirstContainer().getSeparator().equals( "FIXED" ) ) {
                                            line.setSeparator( null );
                                            line.setDesc( r );
                                        } else {
                                            line.setSeparator( reader.getFirstContainer().getSeparator() );
                                            line.setDesc( r );
                                        }
                                        line.setId( r.getRecordValue() );
                                        line.addColumn( r.getRecordValue(), 0 );
                                    }
                                    if ( r != null ) {
                                        RecordEntry re = r.getEntry( me.getValue().substring( 5 ) );
                                        if ( re != null ) {
                                            if ( reader.getFirstContainer().getSeparator().equals( "FIXED" ) ) {
                                                line.addColumn( temp.getNodeValue(), r.getColumnNum( re ) );
                                            } else {
                                                line.addColumn( temp.getNodeValue(), re.getPosition() );
                                            }
                                        } else {
                                            System.out.println( "temp.getNodeName():" + me.getValue().substring( 5 ) ); //$NON-NLS-1$
                                        }
                                    }
                                    if ( r == null ) {
                                        System.out.println( "no record found!" );
                                        return null;
                                    }
                                } else {
                                    System.out.println( "no magic found!!!" );
                                }
                            }
                        }
                    }
                }
            } catch ( XPathExpressionException e ) {
                //                Plugin
                //                        .getDefault()
                //                        .log(
                //                                new LogMessage(
                //                                        LogMessage.ERROR,
                //                                        "Processing", e.getClass().getName(), this, "processXML", 165, e.getLocalizedMessage(), e ) ); //$NON-NLS-1$
                System.out.println("Error: "+ e.getLocalizedMessage());
                e.printStackTrace();
            }
        }
        if ( block.getBlockEntries().size() == 0 ) {
            Magic m = magicReader.getFirstContainer().getMagicbyXMLBlockID( block.getBlockID() );
            if ( m == null ) {
                return null;
            }
            Record r = reader.getFirstContainer().getRecordByRecordID( m.getRecordID() );
            if ( r == null ) {
                return null;
            }
            if ( !r.isActive() ) {
                return null;
            }
        }
        return line;

    }

    private boolean isValidNode( Node node, Node refNode ) {

        if ( node == refNode ) {
            return true;
        }
        Node parent = node;
        if ( node.getNodeType() == Node.ATTRIBUTE_NODE ) {
            parent = ( (Attr) node ).getOwnerElement();
        }
        while ( refNode != parent ) {
            if ( parent == null ) {
                return false;
            }
            parent = parent.getParentNode();
        }
        return true;
    }

    private boolean isRootBlock( XMLBlock block ) {

        List<XMLBlockEntry> entries = block.getBlockEntries();
        for ( int i = 0; i < entries.size(); i++ ) {
            XMLBlockEntry temp = entries.get( i );
            if ( temp.getPosition().equals( "/" ) ) { //$NON-NLS-1$
                return true;
            }
        }
        return false;
    }
}