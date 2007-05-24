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
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 *
 * @author markus.breilmann
 */
public class MapBuilder extends DefaultHandler {

    private static final String   ELEMENT_BLOCK       = "block";
    private static final String   ELEMENT_XPATH       = "xpath";

    private static final String   ATTRIBUTE_ID        = "id";
    private static final String   ATTRIBUTE_NODE      = "node";
    private static final String   ATTRIBUTE_POSITION  = "position";
    private static final String   TEXT_NODE           = "text()";

    private String                outputDir           = null;
    private String                outputFileBaseName  = null;

    private String                currentBlockName    = null;

    private StringBuffer          mappingBuffer       = null;
    private StringBuffer          flatfileBuffer      = null;
    private int                   positionCount       = 0;

    /** Default parser name. */
    protected static final String DEFAULT_PARSER_NAME = "org.apache.xerces.parsers.SAXParser";

    public MapBuilder( String outputDir, String outputFileBaseName ) {

        if ( !outputDir.endsWith( File.separator ) ) {
            this.outputDir = outputDir + File.separator;
        } else {
            this.outputDir = outputDir;
        }
        this.outputFileBaseName = outputFileBaseName;
    }

    /* (non-Javadoc)
     * @see org.xml.sax.ContentHandler#startDocument()
     */
    public void startDocument() throws SAXException {

        mappingBuffer = new StringBuffer( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" );
        mappingBuffer.append( "<moremagic version=\"1.0\" id=\"\">\n" );

        flatfileBuffer = new StringBuffer( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" );
        flatfileBuffer.append( "<records version=\"1.0\" id=\"\" separator=\",\">\n" );
    }

    /* (non-Javadoc)
     * @see org.xml.sax.ContentHandler#endDocument()
     */
    public void endDocument() throws SAXException {

        mappingBuffer.append( "</moremagic>\n" );

        flatfileBuffer.append( "</records>\n" );

        System.out.println( mappingBuffer.toString() );
        System.out.println( "###########################" );
        System.out.println( flatfileBuffer.toString() );

        try {
            String mappingFileName = outputDir + outputFileBaseName + "map.xml";
            System.out.println( "Writing " + mappingFileName );
            FileWriter mappingFileWriter = new FileWriter( mappingFileName );
            mappingFileWriter.write( mappingBuffer.toString() );
            mappingFileWriter.flush();
            mappingFileWriter.close();

            String flatfileFileName = outputDir + outputFileBaseName + "flat.xml";
            System.out.println( "Writing " + flatfileFileName );
            FileWriter flatfileFileWriter = new FileWriter( flatfileFileName );
            flatfileFileWriter.write( flatfileBuffer.toString() );
            flatfileFileWriter.flush();
            flatfileFileWriter.close();
        } catch ( IOException e ) {
            // TODO log stack trace
            e.printStackTrace();
        }
    }

    public void startElement( String uri, String localName, String qname, Attributes attributes ) throws SAXException {

        // System.out.println( "Start element: '" + localName + "' - '" + qname + "'" );

        if ( localName.equals( ELEMENT_BLOCK ) && ( attributes.getValue( ATTRIBUTE_ID ) != null )
                && !attributes.getValue( ATTRIBUTE_ID ).equals( currentBlockName ) ) {
            currentBlockName = attributes.getValue( ATTRIBUTE_ID );
            System.out.println( "\n\nNew block: " + currentBlockName );
            mappingBuffer.append( "\t<magic BlockID=\"" + currentBlockName + "\" RecordID=\"" + currentBlockName
                    + "\">\n" );

            flatfileBuffer.append( "\t<record id=\"" + currentBlockName + "\" value=\"" + currentBlockName
                    + "\" active=\"true\">\n" );
            flatfileBuffer.append( "\t\t<fields>\n" );
            flatfileBuffer
                    .append( "\t\t\t<field id=\"RecordIdentifier\" sourceid=\"\" type=\"relative\" pos=\"0\" length=\"-1\"/>\n" );
            positionCount = 1;
        } else if ( localName.equals( ELEMENT_XPATH ) ) {
            String id = attributes.getValue( ATTRIBUTE_ID );
            String node = attributes.getValue( ATTRIBUTE_NODE );
            String position = attributes.getValue( ATTRIBUTE_POSITION );
            if ( node != null ) {
                String name = null;
                if ( node.endsWith( TEXT_NODE ) && ( node.length() >= 7 ) ) {
                    name = node.substring( 0, ( node.length() - 7 ) );
                } else if ( node.indexOf( "@" ) != -1 ) {
                    name = node.replace( '/', '_' );
                    name = name.replace( '@', '_' );
                }
                if ( name != null ) {
                    name = position.replace( '/', '_' ) + "_" + name;
                    name = name.substring( 1 );
                    System.out.println( "Name: " + name );
                    mappingBuffer.append( "\t\t<mapping xpathid=\"" + id + "\" value=\"file/" + name + "\" />\n" );

                    flatfileBuffer.append( "\t\t\t<field id=\"" + name + "\" sourceid=\"\" type=\"relative\" pos=\""
                            + positionCount++ + "\" length=\"-1\"/>\n" );
                }
            }
        }

    } // startElement

    /* (non-Javadoc)
     * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
     */
    public void endElement( String uri, String localName, String qName ) throws SAXException {

        if ( localName.equals( ELEMENT_BLOCK ) ) {
            mappingBuffer.append( "\t</magic>\n" );
            flatfileBuffer.append( "\t\t</fields>\n" );
            flatfileBuffer.append( "\t</record>\n" );
        }
    } // endElement

    public static void main( String[] args ) {

        String input = null;
        String outputDir = null;
        String outputFileBaseName = null;

        Options options = new Options();
        Option option = new Option( "i", "inputmappingpath", true, "the input mapping path" );
        option.setArgName( "file" );
        option.setRequired( true );
        options.addOption( option );
        option = new Option( "o", "outputdirectory", true, "the output directory" );
        option.setArgName( "dir" );
        option.setRequired( true );
        options.addOption( option );
        option = new Option( "b", "outputbasename", true, "the output base file name" );
        option.setArgName( "prefix" );
        option.setRequired( true );
        options.addOption( option );

        CommandLineParser parsercl = new PosixParser();

        try {
            CommandLine cl = parsercl.parse( options, args );

            if ( !cl.hasOption( "i" ) || !cl.hasOption( "o" ) ) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp( MapBuilder.class.getName(), options );
                System.exit( 1 );
            }

            input = cl.getOptionValue( "i" );
            outputDir = cl.getOptionValue( "o" );
            outputFileBaseName = cl.getOptionValue( "b" );
        } catch ( ParseException e1 ) {
            e1.printStackTrace();
            System.exit( 1 );
        }
        

        XMLReader parser = null;
        try {
            MapBuilder rapidMapBuilder = new MapBuilder( outputDir, outputFileBaseName );
            parser = XMLReaderFactory.createXMLReader( DEFAULT_PARSER_NAME );

            parser.setDTDHandler( rapidMapBuilder );
            parser.setErrorHandler( rapidMapBuilder );
            if ( parser instanceof XMLReader ) {
                parser.setContentHandler( rapidMapBuilder );
                /*
                 try {
                 parser.setProperty( "http://xml.org/sax/properties/declaration-handler", saxTest );
                 } catch ( SAXException e ) {
                 e.printStackTrace( System.err );
                 }
                 try {
                 parser.setProperty( "http://xml.org/sax/properties/lexical-handler", saxTest );
                 } catch ( SAXException e ) {
                 e.printStackTrace( System.err );
                 }
                 */
            } else {
                System.err.println( "error: parser is of wrong type: " + parser.getClass() );
            }

            parser.parse( input );
        } catch ( Exception e ) {
            System.err.println( "error: Unable to instantiate parser: " + e );
        }

    }
} // MapBuilder
