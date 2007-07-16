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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
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
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.nexuse2e.tools.mapping.CSVMappingFileEntry;
import org.nexuse2e.tools.mapping.csv.Record;
import org.nexuse2e.tools.mapping.csv.RecordContainer;
import org.nexuse2e.tools.mapping.csv.RecordEntry;
import org.nexuse2e.tools.mapping.input.CSVLine;
import org.nexuse2e.tools.mapping.input.CSVReader;
import org.nexuse2e.tools.mapping.magic.Magic;
import org.nexuse2e.tools.mapping.magic.MagicContainer;
import org.nexuse2e.tools.mapping.magic.MagicEntry;
import org.nexuse2e.tools.mapping.xml.XMLBlock;
import org.nexuse2e.tools.mapping.xml.XMLBlockContainer;
import org.nexuse2e.tools.mapping.xml.XMLBlockEntry;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author guido.esch
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ProcessCSV {

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

    public static void main( String[] args ) {

        //        String xmlBlock = "d:/eclipse-SDK-3.0.1-win32/repository/mappings/test_OrderCreate_blocks.xml"; //$NON-NLS-1$
        //        String csvMapping = "d:/eclipse-SDK-3.0.1-win32/repository/mappings/test_flat.xml"; //$NON-NLS-1$
        //        String magic = "d:/eclipse-SDK-3.0.1-win32/repository/mappings/test_map.xml"; //$NON-NLS-1$
        //        String contentPath = "C:/Dokumente und Einstellungen/guido.esch/Desktop/umzug/fixed.fix"; //$NON-NLS-1$

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
        option = new Option( "i", "input", true, "the input flat file" );
        option.setArgName( "file" );
        option.setRequired( true );
        options.addOption( option );

        CommandLineParser parser = new PosixParser();

        try {
            CommandLine cl = parser.parse( options, args );

            if ( !cl.hasOption( "x" ) || !cl.hasOption( "c" ) || !cl.hasOption( "m" ) || !cl.hasOption( "i" ) ) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp( ProcessCSV.class.getName(), options );
                System.exit( 1 );
            }

            xmlBlock = cl.getOptionValue( "x" );
            csvMapping = cl.getOptionValue( "c" );
            magic = cl.getOptionValue( "m" );
            contentPath = cl.getOptionValue( "i" );

        } catch ( ParseException e1 ) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( ProcessCSV.class.getName(), options );
            System.exit( 1 );
        }

        System.out.println( "xmlBlock: " + xmlBlock );
        System.out.println( "csvMapping: " + csvMapping );
        System.out.println( "magic: " + magic );
        System.out.println( "contentPath: " + contentPath );

        mfe.setCsvmappings( csvMapping );
        mfe.setXmlblocks( xmlBlock );
        mfe.setMapping( magic );
        mfe.setId( "test-mapping" ); //$NON-NLS-1$

        try {
            ProcessCSV process = new ProcessCSV();

            FileInputStream fis = new FileInputStream( new File( contentPath ) );
            String out = process.process( mfe, fis );
            fis.close();
            System.out.println( "...................." );
            System.out.println( out );
            System.out.println( "...................." );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /**
     * @param mfe
     * @param content
     * @return result
     */
    public String process( CSVMappingFileEntry mfe, InputStream content ) {

        File testFile = new File( mfe.getCsvmappings() );
        if ( !testFile.exists() ) {
            //            Plugin.getDefault().log(
            //                    new LogMessage( LogMessage.ERROR,
            //                            "Processing", "Error", this, "process", 149, "mappingfile: " + testFile //$NON-NLS-1$ //$NON-NLS-2$
            //                                    + " doesn't exist, processing canceled!!", null ) ); //$NON-NLS-1$
            System.out.println( "mappingfile: " + testFile + " doesn't exist, processing canceled!!" );
            return null;
        }
        testFile = new File( mfe.getMapping() );
        if ( !testFile.exists() ) {
            //            Plugin.getDefault().log(
            //                    new LogMessage( LogMessage.ERROR,
            //                            "Processing", "Error", this, "process", 150, "mappingfile: " + testFile //$NON-NLS-1$ //$NON-NLS-2$
            //                                    + " doesn't exist, processing canceled!!", null ) ); //$NON-NLS-1$
            System.out.println( "mappingfile: " + testFile + " doesn't exist, processing canceled!!" );
            return null;
        }
        testFile = new File( mfe.getXmlblocks() );
        if ( !testFile.exists() ) {
            //            Plugin.getDefault().log(
            //                    new LogMessage( LogMessage.ERROR,
            //                            "Processing", "Error", this, "process", 151, "mappingfile: " + testFile //$NON-NLS-1$ //$NON-NLS-2$
            //                                    + " doesn't exist, processing canceled!!", null ) ); //$NON-NLS-1$
            System.out.println( "mappingfile: " + testFile + " doesn't exist, processing canceled!!" );
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
            //                            "Processing", "Error", this, "process", 152, "Can't process Message!", e ) ); //$NON-NLS-1$ //$NON-NLS-2$
            System.out.println( "Can't process Message: " + e.getLocalizedMessage() );
            e.printStackTrace();
            return null;
        }
        document = documentBuilder.newDocument();

        CSVReader input = new CSVReader();

        RecordContainer rc = reader.getFirstContainer();
        if ( rc == null ) {
            //            Plugin.getDefault().log( new LogMessage( LogMessage.ERROR, "Processing", "Error", this, "process", 153, //$NON-NLS-1$
            //                    "Can't process Message! (Mapping not Found!)", null ) ); //$NON-NLS-1$
            System.out.println( "Can't process Message! (Mapping not Found!)" );
            return null;
        }
        input.readCSV( this, content, rc );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputFormat of = new OutputFormat();
        of.setIndenting( true );
        System.out.println( "indent:" + of.getIndent() ); //$NON-NLS-1$
        XMLSerializer ser = new XMLSerializer( baos, of );
        try {
            ser.serialize( document );

        } catch ( IOException e1 ) {

            //            Plugin.getDefault().log(
            //                    new LogMessage( LogMessage.ERROR,
            //                            "Processing", "Error", this, "process", 154, "Can't process Message!", e1 ) ); //$NON-NLS-1$ //$NON-NLS-2$
            System.out.println( "Can't process Message: " + e1.getLocalizedMessage() );
            e1.printStackTrace();
            return null;
        }
        return baos.toString();
    }

    /**
     * @param line
     */
    public void processLine( CSVLine line ) {

        if ( line == null || line.getId() == null ) {
            return;
        }
        RecordContainer rc = reader.getFirstContainer();

        if ( rc == null ) {
            System.out.println( "no recordContainer found" ); //$NON-NLS-1$
            return;
        }
        Record r = rc.getRecordByValue( line.getId() );
        if ( r == null ) {
            System.out.println( "no record for: " + line.getId() + " found" ); //$NON-NLS-1$ //$NON-NLS-2$
            return;
        }
        if ( !r.isActive() ) {
            return;
        }
        MagicContainer mc = magicReader.getFirstContainer();
        Magic m = mc.getMagicbyRecordID( r.getRecordID() );
        if ( m == null ) {
            System.out.println( "no magic for: " + r.getRecordID() + " found" ); //$NON-NLS-1$ //$NON-NLS-2$
            return;
        }
        XMLBlockContainer bc = blockReader.getFirstContainer();
        if ( bc == null ) {
            System.out.println( "no blockContainer for:" + m.getBlockID() + " found" ); //$NON-NLS-1$ //$NON-NLS-2$
            return;
        }
        XMLBlock b = bc.getXMLBLockbyBlockID( m.getBlockID() );
        if ( b == null ) {
            System.out.println( "no block found for:" + m.getBlockID() ); //$NON-NLS-1$
            return;
        }
        if ( b.getBlockEntries() == null ) {
            System.out.println( "no block entries found for:" + m.getBlockID() ); //$NON-NLS-1$
            return;
        }
        Iterator i = b.getBlockEntries().iterator();
        while ( i.hasNext() ) {
            XMLBlockEntry entry = (XMLBlockEntry) i.next();
            if ( entry.getNode().toLowerCase().endsWith( "text()" ) || entry.getNode().indexOf( '@' ) > -1 ) { //$NON-NLS-1$

                boolean isAttr = false;
                if ( entry.getNode().indexOf( '@' ) > -1 ) {
                    isAttr = true;
                }
                try {
                    String node = entry.getNode();
                    if ( isAttr ) {
                        node = node.substring( 0, node.lastIndexOf( '/' ) );
                    } else {
                        node = node.substring( 0, node.length() - 7 );
                    }

                    XPath xpath = XPathFactory.newInstance().newXPath();
                    NodeList nodes = (NodeList) xpath.evaluate( entry.getPosition(), document, XPathConstants.NODESET );

                    if ( nodes == null || nodes.getLength() == 0 ) {
                        System.out.println( "pos:" + entry.getPosition() + " not found" ); //$NON-NLS-1$ //$NON-NLS-2$
                        return;
                    }

                    Node root = nodes.item( nodes.getLength() - 1 );

                    xpath = XPathFactory.newInstance().newXPath();
                    nodes = (NodeList) xpath.evaluate( node, root, XPathConstants.NODESET );

                    if ( nodes == null ) {
                        System.out.println( "targetnode not found" ); //$NON-NLS-1$
                        return;
                    }

                    Node target = nodes.item( nodes.getLength() - 1 );
                    MagicEntry me = m.getEntryByXpathID( entry.getEntryID() );
                    String value = "static/"; //$NON-NLS-1$
                    if ( me != null ) {
                        value = me.getValue();
                    } else {
                        System.out.println( "entryID: " + entry.getEntryID() + " not found" ); //$NON-NLS-1$ //$NON-NLS-2$
                    }
                    if ( value.toLowerCase().startsWith( "file" ) ) { //$NON-NLS-1$
                        value = value.substring( 5 );
                        RecordEntry e = r.getEntry( value );
                        try {
                            if ( !rc.getSeparator().equals( "FIXED" ) ) {
                                value = line.columns.get( e.getPosition() );
                            } else {
                                value = line.columns.get( r.getColumnNum( e ) );
                            }
                        } catch ( RuntimeException e1 ) {
                            value = "invalid"; //$NON-NLS-1$
                            //                            Plugin
                            //                                    .getDefault()
                            //                                    .log(
                            //                                            new LogMessage(
                            //                                                    LogMessage.ERROR,
                            //                                                    "Processing", e1.getClass().getName(), this, "processLine", 155, e1.getLocalizedMessage(), e1 ) ); //$NON-NLS-1$
                        }
                    } else if ( value.toLowerCase().startsWith( "static" ) ) { //$NON-NLS-1$
                        value = value.substring( 7 );
                    }
                    //                    else if ( value.toLowerCase().startsWith( "internal" ) ) { //$NON-NLS-1$
                    //                        value = value.substring( 9 );
                    //                        try {
                    //                            value = Plugin.getDefault().getInternalMappingByID( value );
                    //                        } catch ( RuntimeException e1 ) {
                    //                            value = "invalid"; //$NON-NLS-1$
                    //                            Plugin
                    //                                    .getDefault()
                    //                                    .log(
                    //                                            new LogMessage(
                    //                                                    LogMessage.ERROR,
                    //                                                    "Processing", e1.getClass().getName(), this, "processLine", 156, e1.getLocalizedMessage(), e1 ) ); //$NON-NLS-1$
                    //                        }

                    //                    }
                    else {
                        System.out.println( "only file/static is supported!" ); //$NON-NLS-1$
                        value = "invalid"; //$NON-NLS-1$
                    }

                    // modify

                    if ( isAttr ) {
                        String attrString = entry.getNode().substring( entry.getNode().indexOf( '@' ) + 1,
                                entry.getNode().length() );
                        Node attr = document.createAttribute( attrString );
                        attr.setNodeValue( value );
                        ( (Element) target ).setAttributeNode( (Attr) attr );
                    } else {
                        Node text = document.createTextNode( value );
                        target.appendChild( text );
                    }
                } catch ( XPathExpressionException e ) {

                    //                    Plugin
                    //                            .getDefault()
                    //                            .log(
                    //                                    new LogMessage(
                    //                                            LogMessage.ERROR,
                    //                                            "Processing", e.getClass().getName(), this, "processLine", 157, e.getLocalizedMessage(), e ) ); //$NON-NLS-1$
                    System.out.println( "Error: " + e.getLocalizedMessage() );
                    e.printStackTrace();
                }
            } else {
                try {

                    XPath xpath = XPathFactory.newInstance().newXPath();
                    NodeList nodes = (NodeList) xpath.evaluate( entry.getPosition(), document, XPathConstants.NODESET );

                    if ( nodes == null || nodes.getLength() == 0 ) {
                        System.out.println( "pos:" + entry.getPosition() + " not found" ); //$NON-NLS-1$ //$NON-NLS-2$
                        return;
                    }
                    Node root = nodes.item( nodes.getLength() - 1 );

                    xpath = XPathFactory.newInstance().newXPath();
                    nodes = (NodeList) xpath.evaluate( entry.getNode(), root, XPathConstants.NODESET );

                    Node temp = document.createElement( entry.getNode() );
                    root.appendChild( temp );

                } catch ( XPathExpressionException e ) {

                    //                    Plugin
                    //                            .getDefault()
                    //                            .log(
                    //                                    new LogMessage(
                    //                                            LogMessage.ERROR,
                    //                                            "Processing", e.getClass().getName(), this, "processLine", 158, e.getLocalizedMessage(), e ) ); //$NON-NLS-1$
                    System.out.println( "Error: " + e.getLocalizedMessage() );
                } catch ( Exception e ) {
                    System.out.println( "Error: " + e.getLocalizedMessage() );
                    System.out.println( "Node: " + entry.getNode() );
                }
            }
        }
    }
}