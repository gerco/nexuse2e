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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import org.apache.log4j.Logger;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.nexuse2e.NexusException;
import org.nexuse2e.tools.mapping.csv.CSVLine;
import org.nexuse2e.tools.mapping.csv.Record;
import org.nexuse2e.tools.mapping.csv.RecordContainer;
import org.nexuse2e.tools.mapping.csv.RecordEntry;
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

    private static Logger        LOG = Logger.getLogger( ProcessCSV.class );

    /**
     * Comment for <code>reader</code>
     */
    public CSV2XMLMappingReader  flatFileDefinitionReader;
    /**
     * Comment for <code>blockReader</code>
     */
    public XMLBLockMappingReader xmlFileDefinitionReader;
    /**
     * Comment for <code>magicReader</code>
     */
    public MagicMappingReader    mappingDefinitionReader;

    private Document             document;

    public static void main( String[] args ) {

        //        String xmlBlock = "d:/eclipse-SDK-3.0.1-win32/repository/mappings/test_OrderCreate_blocks.xml"; 
        //        String csvMapping = "d:/eclipse-SDK-3.0.1-win32/repository/mappings/test_flat.xml"; 
        //        String magic = "d:/eclipse-SDK-3.0.1-win32/repository/mappings/test_map.xml"; 
        //        String contentPath = "C:/Dokumente und Einstellungen/guido.esch/Desktop/umzug/fixed.fix"; 

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

        LOG.error( "xmlBlock: " + xmlBlock );
        LOG.error( "csvMapping: " + csvMapping );
        LOG.error( "magic: " + magic );
        LOG.error( "contentPath: " + contentPath );

        mfe.setCsvmappings( csvMapping );
        mfe.setXmlblocks( xmlBlock );
        mfe.setMapping( magic );
        mfe.setId( "test-mapping" );

        try {
            ProcessCSV process = new ProcessCSV();

            FileInputStream fis = new FileInputStream( new File( contentPath ) );
            String out = process.process( mfe, fis );
            fis.close();
            LOG.error( "...................." );
            LOG.error( out );
            LOG.error( "...................." );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /**
     * @param mappingFileEntry
     * @param content
     * @return result
     */
    public String process( CSVMappingFileEntry mappingFileEntry, InputStream content ) throws NexusException {

        boolean detectedError = false;

        long startTime = System.currentTimeMillis();

        // Make sure all mapping definition files are available
        File testFile = new File( mappingFileEntry.getCsvmappings() );
        if ( !testFile.exists() ) {
            LOG.error( "mappingfile: " + testFile + " doesn't exist, processing canceled!!" );
            return null;
        }
        testFile = new File( mappingFileEntry.getMapping() );
        if ( !testFile.exists() ) {
            LOG.error( "mappingfile: " + testFile + " doesn't exist, processing canceled!!" );
            return null;
        }
        testFile = new File( mappingFileEntry.getXmlblocks() );
        if ( !testFile.exists() ) {
            LOG.error( "Mapping file: " + testFile + " doesn't exist, processing canceled!!" );
            return null;
        }

        // Parse definition files
        flatFileDefinitionReader = new CSV2XMLMappingReader();
        flatFileDefinitionReader.parseMappingFile( mappingFileEntry.getCsvmappings() );
        xmlFileDefinitionReader = new XMLBLockMappingReader();
        xmlFileDefinitionReader.parseMappingFile( mappingFileEntry.getXmlblocks() );
        mappingDefinitionReader = new MagicMappingReader();
        mappingDefinitionReader.parseMappingFile( mappingFileEntry.getMapping() );

        LOG.debug( "Time for set-up: " + ( System.currentTimeMillis() - startTime ) );
        startTime = System.currentTimeMillis();

        // Create empty output XML DOM
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware( true );
        DocumentBuilder documentBuilder;
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch ( ParserConfigurationException e ) {
            LOG.error( "Can't process message: " + e.getLocalizedMessage() );
            e.printStackTrace();
            return null;
        }
        document = documentBuilder.newDocument();

        RecordContainer recordContainer = flatFileDefinitionReader.getFirstContainer();
        if ( recordContainer == null ) {
            LOG.error( "Can't process message (mapping not found)!" );
            return null;
        }

        // Process input
        InputStreamReader isr = new InputStreamReader( content );
        BufferedReader br = new BufferedReader( isr );
        String line = "";
        while ( line != null ) {
            try {
                line = br.readLine();
                if ( line != null && !line.trim().equals( "" ) ) {
                    CSVLine newLine = new CSVLine( line, recordContainer );
                    processLine( newLine );
                }
            } catch ( IOException e1 ) {
                LOG.error( "Error processing line: " + e1.getLocalizedMessage() );
                detectedError = true;
            }
        }
        try {
            br.close();
            isr.close();
        } catch ( IOException e1 ) {
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputFormat outputFormat = new OutputFormat();
        outputFormat.setIndenting( true );
        LOG.debug( "Indent: " + outputFormat.getIndent() );
        XMLSerializer ser = new XMLSerializer( baos, outputFormat );
        try {
            ser.serialize( document );
        } catch ( IOException e1 ) {
            LOG.error( "Can't serialize XML output: " + e1.getLocalizedMessage() );
            e1.printStackTrace();
            throw new NexusException( "Can't serialize XML output: " + e1.getLocalizedMessage() );
        }

        LOG.debug( "Time for conversion: " + ( System.currentTimeMillis() - startTime ) );

        if ( detectedError ) {
            throw new NexusException( "Error converting one or more lines!" );
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
        RecordContainer recordContainer = flatFileDefinitionReader.getFirstContainer();

        if ( recordContainer == null ) {
            LOG.error( "no recordContainer found" );
            return;
        }
        Record record = recordContainer.getRecordByValue( line.getId() );
        if ( record == null ) {
            LOG.error( "no record for: " + line.getId() + " found" );
            return;
        }
        if ( !record.isActive() ) {
            return;
        }
        MagicContainer mc = mappingDefinitionReader.getFirstContainer();
        Magic m = mc.getMappingByRecordId( record.getRecordID() );
        if ( m == null ) {
            LOG.error( "no magic for: " + record.getRecordID() + " found" );
            return;
        }
        XMLBlockContainer bc = xmlFileDefinitionReader.getFirstContainer();
        if ( bc == null ) {
            LOG.error( "no blockContainer for:" + m.getBlockID() + " found" );
            return;
        }
        XMLBlock b = bc.getXMLBLockbyBlockID( m.getBlockID() );
        if ( b == null ) {
            LOG.error( "no block found for:" + m.getBlockID() );
            return;
        }
        if ( b.getBlockEntries() == null ) {
            LOG.error( "no block entries found for:" + m.getBlockID() );
            return;
        }
        Iterator<XMLBlockEntry> i = b.getBlockEntries().iterator();
        while ( i.hasNext() ) {
            XMLBlockEntry xmlMappingEntry = i.next();
            if ( xmlMappingEntry.isTextNode() || xmlMappingEntry.isAttribute() ) {

                try {
                    String node = xmlMappingEntry.getNodePath();

                    XPath xpath = XPathFactory.newInstance().newXPath();
                    NodeList nodes = (NodeList) xpath.evaluate( xmlMappingEntry.getPosition(), document,
                            XPathConstants.NODESET );

                    if ( nodes == null || nodes.getLength() == 0 ) {
                        LOG.error( "Pos: " + xmlMappingEntry.getPosition() + " not found" );
                        return;
                    }

                    Node root = nodes.item( nodes.getLength() - 1 );

                    xpath = XPathFactory.newInstance().newXPath();
                    nodes = (NodeList) xpath.evaluate( node, root, XPathConstants.NODESET );

                    if ( nodes == null ) {
                        LOG.error( "Target node not found" );
                        return;
                    }

                    Node target = nodes.item( nodes.getLength() - 1 );
                    MagicEntry mappingEntry = m.getEntryByXpathID( xmlMappingEntry.getEntryID() );
                    String value = "static/";
                    if ( mappingEntry != null ) {
                        value = mappingEntry.getValue();
                    } else {
                        LOG.error( "entryID: " + xmlMappingEntry.getEntryID() + " not found" );
                    }
                    if ( mappingEntry.isFileSource() ) {
                        RecordEntry recordEntry = record.getEntry( value );
                        try {
                            if ( !recordContainer.getSeparator().equals( "FIXED" ) ) {
                                value = line.columns.get( recordEntry.getPosition() );
                            } else {
                                value = line.columns.get( record.getColumnNum( recordEntry ) );
                            }

                            if ( xmlMappingEntry.getLength() != -1 && ( value.length() > xmlMappingEntry.getLength() ) ) {
                                value = value.substring( 0, xmlMappingEntry.getLength() );
                            }
                        } catch ( RuntimeException e1 ) {
                            LOG.error( "Error converting flat file line: " + e1 );
                            value = "error";
                        }
                    } else if ( !mappingEntry.isStaticSource() ) {
                        LOG.error( "Only file/static data source is supported!" );
                        value = "invalid";
                    }

                    if ( xmlMappingEntry.isAttribute() ) {
                        Node attr = document.createAttribute( xmlMappingEntry.getAttributeName() );
                        attr.setNodeValue( value );
                        ( (Element) target ).setAttributeNode( (Attr) attr );
                    } else {
                        Node text = document.createTextNode( value );
                        target.appendChild( text );
                    }
                } catch ( XPathExpressionException e ) {

                    LOG.error( "Error: " + e.getLocalizedMessage() );
                    e.printStackTrace();
                }
            } else {
                try {

                    XPath xpath = XPathFactory.newInstance().newXPath();
                    NodeList nodes = (NodeList) xpath.evaluate( xmlMappingEntry.getPosition(), document,
                            XPathConstants.NODESET );

                    if ( nodes == null || nodes.getLength() == 0 ) {
                        LOG.error( "pos:" + xmlMappingEntry.getPosition() + " not found" );
                        return;
                    }
                    Node root = nodes.item( nodes.getLength() - 1 );

                    xpath = XPathFactory.newInstance().newXPath();
                    nodes = (NodeList) xpath.evaluate( xmlMappingEntry.getNode(), root, XPathConstants.NODESET );

                    Node temp = document.createElement( xmlMappingEntry.getNode() );
                    root.appendChild( temp );

                } catch ( XPathExpressionException e ) {

                    LOG.error( "Error: " + e.getLocalizedMessage() );
                } catch ( Exception e ) {
                    LOG.error( "Error: " + e.getLocalizedMessage() );
                    LOG.error( "Node: " + xmlMappingEntry.getNode() );
                }
            }
        }
    }
}