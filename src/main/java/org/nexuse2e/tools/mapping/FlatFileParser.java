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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.log4j.Logger;
import org.nexuse2e.NexusException;
import org.nexuse2e.tools.mapping.csv.RecordContainer;

/**
 * Parse a flat file based on a description provided in XML format. The parsed
 * input will be made available to other processors as an internal data structure
 * of Java objects.
 * The XML definition file has the following format:
 * <records version="1.0" id="" separator=";">
 *   <record id="" value="" active="true" conversionClass="">
 *       <fields>
 *           <field id="RecordIdentifier" sourceid="" type="relative" pos="0" length="-1" trim="false" filler="" align="" method=""/>
 *
 *
 * @author mbreilmann
 */
public class FlatFileParser {

    private static Logger   LOG             = Logger.getLogger( FlatFileParser.class );

    private RecordContainer recordContainer = null;

    /**
     * Initialize the parser with the flat file definition file
     * @param flatFileMapping The path to the flat file definition file
     */
    public void init( String flatFileMapping ) throws NexusException {

        // Make sure all mapping definition files are available
        File testFile = new File( flatFileMapping );
        if ( !testFile.exists() ) {
            String msg = "Flat File Mapping " + testFile + " doesn't exist, processing canceled!";
            LOG.error( msg );
            throw new NexusException( msg );
        }

        CSV2XMLMappingReader flatFileDefinitionReader = new CSV2XMLMappingReader();
        flatFileDefinitionReader.parseMappingFile( flatFileMapping );

        recordContainer = flatFileDefinitionReader.getFirstContainer();
        if ( recordContainer == null ) {
            String msg = "No format definition found in flat file definition!";
            LOG.error( msg );
            throw new NexusException( msg );
        }

    } // init

    /**
     * Process the input file and parse it into an intermediate format
     * @param inputStream
     * @return
     */
    public List<FlatFileRecord> process( InputStream inputStream ) throws NexusException {

        ArrayList<FlatFileRecord> result = new ArrayList<FlatFileRecord>();
        boolean detectedError = false;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;

        long startTime = System.currentTimeMillis();

        // Process input
        try {
            inputStreamReader = new InputStreamReader( inputStream, "ISO-8859-1" );
            bufferedReader = new BufferedReader( inputStreamReader );
            String line = "";
            if ( recordContainer.isSkipHeader() ) {
                line = bufferedReader.readLine();
            }
            while ( line != null ) {
                line = bufferedReader.readLine();
                if ( line != null && !line.trim().equals( "" ) ) {
                    FlatFileRecord flatFileRecord = new FlatFileRecord( line, recordContainer );
                    result.add( flatFileRecord );
                }
            }
            bufferedReader.close();
            inputStreamReader.close();
        } catch ( IOException e1 ) {
            LOG.error( "Error processing line: " + e1.getLocalizedMessage() );
            detectedError = true;
        }

        if ( detectedError ) {
            throw new NexusException( "Error converting one or more lines!" );
        }

        LOG.debug( "Time for conversion: " + ( System.currentTimeMillis() - startTime ) );

        return result;
    } // process

    /**
     * @param args
     */
    public static void main( String[] args ) {

        String flatFileMapping = null;
        String contentPath = null;

        Options options = new Options();

        Option option = new Option( "f", "flatfilemapping", true, "the flat file mapping file" );
        option.setArgName( "flatfilemapping" );
        option.setRequired( true );
        options.addOption( option );
        option = new Option( "i", "input", true, "the input flat file" );
        option.setArgName( "inputfile" );
        option.setRequired( true );
        options.addOption( option );

        CommandLineParser parser = new PosixParser();

        try {
            CommandLine cl = parser.parse( options, args );

            if ( !cl.hasOption( "f" ) || !cl.hasOption( "i" ) ) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp( ProcessCSV.class.getName(), options );
                System.exit( 1 );
            }

            flatFileMapping = cl.getOptionValue( "f" );
            contentPath = cl.getOptionValue( "i" );

        } catch ( ParseException e1 ) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( ProcessCSV.class.getName(), options );
            System.exit( 1 );
        }

        LOG.debug( "flatFileMapping : " + flatFileMapping );
        LOG.debug( "contentPath     : " + contentPath );

        try {
            FlatFileParser flatFileParser = new FlatFileParser();

            flatFileParser.init( flatFileMapping );

            FileInputStream fileInputStream = new FileInputStream( new File( contentPath ) );
            List<FlatFileRecord> out = flatFileParser.process( fileInputStream );
            fileInputStream.close();

            LOG.debug( "...................." );
            for ( FlatFileRecord flatFileRecord : out ) {
                LOG.debug( flatFileRecord.toString() );
            }
            LOG.debug( "...................." );

        } catch ( Exception e ) {
            e.printStackTrace();
        }
    } // main

} // FlatFileParser
