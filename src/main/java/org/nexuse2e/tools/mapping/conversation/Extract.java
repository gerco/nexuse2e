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
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.junit.Ignore;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


/**
 * @author gesch
 *
 */
public class Extract {

    private static final String INPUT     = "i";
    private static final String OUTPUT    = "o";
    private static final String OVERWRITE = "w";

    /**
     * 
     * 
     * @param args
     */
    
    @SuppressWarnings("static-access")
    public static void main( String[] args ) {

        System.out.println( "Extract Main:" );

        String inputFileStr = null;
        String outputFileStr = null;
        boolean overwrite = true;

        Options options = new Options();
        options.addOption( OptionBuilder.withArgName( "file" ).withLongOpt( "input" ).hasArg().withValueSeparator( '=' )
                .withDescription( "input filename" ).create( INPUT ) );
        options.addOption( OptionBuilder.withArgName( "file" ).withLongOpt( "output" ).hasArg()
                .withValueSeparator( '=' ).withDescription( "output filename" ).create( OUTPUT ) );
        options.addOption( OptionBuilder.withLongOpt( "overwrite" ).withDescription( "overwrite outputfile?" ).create(
                OVERWRITE ) );
        options.addOption( OVERWRITE, "overwrite", false, "overwrite outputfile?" );

        CommandLineParser parser = new PosixParser();

        try {
            CommandLine cl = parser.parse( options, args );

            
            if ( !cl.hasOption( INPUT ) || !cl.hasOption( OUTPUT ) ) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp( Extract.class.getName(), options );
                System.exit( 1 );
            }

            inputFileStr = cl.getOptionValue( INPUT );
            outputFileStr = cl.getOptionValue( OUTPUT );
            overwrite = cl.hasOption( OVERWRITE );
        } catch ( ParseException e1 ) {
            e1.printStackTrace();
            System.exit( 1 );
        }

        System.out.println( "InputFile: " + inputFileStr );
        System.out.println( "OutputFile: " + outputFileStr );
        System.out.println( "Overwrite: " + overwrite );

        
//        String input;
//        String output;
//        if ( args.length > 2 ) {
//            input = args[0];
//            output = args[1];
//        } else {
//            //            input = "C:/Dokumente und Einstellungen/guido.esch/Desktop/OrderExample-1.xml";
//
//            input = "C:/Dokumente und Einstellungen/guido.esch/Desktop/ShipNotice/Feed Track-Trace Minimum5.xml"; //$NON-NLS-1$
//            output = "C:/Dokumente und Einstellungen/guido.esch/Desktop/ShipNotice/shipnotice-template.xml"; //$NON-NLS-1$
//            //input = "C:/Dokumente und Einstellungen/guido.esch/Desktop/Feed Track-Trace Minimum3.xml"; //$NON-NLS-1$
//            //output = "C:/Dokumente und Einstellungen/guido.esch/Desktop/shipnotice-template.xml"; //$NON-NLS-1$
//
//        }
        File inputFile = new File( inputFileStr );
        File outputFile = new File( outputFileStr );
        Extract e = new Extract();
        e.extract( inputFile, outputFile );

    }

    /**
     * @param inputFile
     * @param outputFile
     */
    public void extract( File inputFile, File outputFile ) {

        if ( inputFile.exists() ) {
            try {
                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

                FileReader fileReaderXml = new FileReader( inputFile );
                InputSource inputSource = new InputSource( fileReaderXml );
                Document document = documentBuilder.parse( inputSource );

                NodeList nl = document.getChildNodes();
                for ( int i = 0; i < nl.getLength(); i++ ) {
                    Node root = nl.item( i );
                    FileOutputStream fos = new FileOutputStream( outputFile );
                    PrintWriter pw = new PrintWriter( fos );

                    pw.println( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" ); //$NON-NLS-1$
                    pw.println( "<blocks version=\"1.0\" id=\"\" siblingsequence=\"0\">" ); //$NON-NLS-1$
                    pw.println( "  <block id=\"root\">" ); //$NON-NLS-1$
                    appendNodes( root, pw, "/" ); //$NON-NLS-1$
                    pw.println( "  </block>" ); //$NON-NLS-1$
                    pw.println( "</blocks>" ); //$NON-NLS-1$
                    pw.flush();
                    fos.flush();
                    fos.close();
                }

            } catch ( Exception e ) {

                //                Plugin
                //                        .getDefault()
                //                        .log(
                //                                new LogMessage(
                //                                        LogMessage.ERROR,
                //                                        "Processing", e.getClass().getName(), this, "extract", 146, e.getLocalizedMessage(), e ) ); //$NON-NLS-1$
            }
        }
    }

    private void appendNodes( Node node, PrintWriter pw, String prefix ) {

        pw.println( "    <xpath id=\"\" position=\"" + prefix + "\" node=\"" + node.getNodeName() + "\"/>" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        NamedNodeMap nnm = node.getAttributes();
        if ( nnm != null ) {
            for ( int i = 0; i < nnm.getLength(); i++ ) {
                Node attrib = nnm.item( i );
                pw.println( "    <xpath id=\"" + prefix + "_" + node.getNodeName() + "_" + attrib.getNodeName() //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        + "\" position=\"" + prefix + "\" node=\"" + node.getNodeName() + "/@" + attrib.getNodeName() //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        + "\"/>" ); //$NON-NLS-1$
            }
        }
        NodeList children = node.getChildNodes();
        for ( int i = 0; i < children.getLength(); i++ ) {
            Node temp = children.item( i );
            if ( temp.getNodeType() == Node.TEXT_NODE && !temp.getNodeValue().trim().equals( "" ) ) { //$NON-NLS-1$

                pw.println( "    <xpath id=\"" + prefix + "_" + node.getNodeName() + "_#text\" position=\"" + prefix //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        + "\" node=\"" + node.getNodeName() + "/text()\"/>" ); //$NON-NLS-1$ //$NON-NLS-2$
                break;
            }
        }
        for ( int i = 0; i < children.getLength(); i++ ) {
            Node temp = children.item( i );
            if ( temp.getNodeType() != Node.TEXT_NODE ) {
                if ( prefix.equals( "/" ) ) { //$NON-NLS-1$
                    prefix = ""; //$NON-NLS-1$
                }
                appendNodes( temp, pw, prefix + "/" + node.getNodeName() ); //$NON-NLS-1$
            }
        }
    }

}