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
package org.nexuse2e.test.http;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpHost;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.io.FileUtils;

/**
 * @author mbreilmann
 *
 */
public class PlainHttpTestClient {

    private static final int SOCKET_TIMEOUT = 120000;

    private static void request( URL url, File file ) throws IOException {

        String content = FileUtils.readFileToString( file, "UTF-8" );
        HttpClient client = new HttpClient();
        HostConfiguration configuration = new HostConfiguration();
        configuration.setHost( new HttpHost( url.getHost(), url.getPort(), Protocol.getProtocol( url.getProtocol() ) ) );
        client.setHostConfiguration( configuration );
        PostMethod method = new PostMethod( url.toExternalForm() );
        method.getParams().setSoTimeout( SOCKET_TIMEOUT );
        RequestEntity requestEntity = new StringRequestEntity( content, "text/xml", "UTF-8" );
        method.setRequestEntity( requestEntity );
        long time = System.currentTimeMillis();
        int result = client.executeMethod( method );
        time = System.currentTimeMillis() - time;
        System.out.println( "Request took " + time + " ms, HTTP result is " + result + "("
                + method.getResponseBodyAsString() + ")" );
    }

    /**
     * @param args
     */
    @SuppressWarnings("rawtypes")
    public static void main( String[] args ) {

        CommandLineParser parser = new GnuParser();
        Options options = new Options();
        options.addOption( "?", "help", false, "print out command-line options" );
        /*
        Option option = new Option( "f", "file", true, "The file to send" );
        option.setArgName( "file" );
        option.setRequired( true );
        options.addOption( option );
        */
        try {
            CommandLine commandLine = parser.parse( options, args );
            if ( commandLine.hasOption( "?" ) ) {
                printHelp( null, options );
                System.exit( 0 );
            }
            List l = commandLine.getArgList();
            if ( l.size() < 1 ) {
                throw new MissingArgumentException( "file" );
            }
            if ( l.size() < 2 ) {
                throw new MissingArgumentException( "request URL" );
            }
            String file = l.get( 0 ).toString();
            URL url;
            String urlString = l.get( 1 ).toString();
            try {
                url = new URL( urlString );
            } catch ( MalformedURLException mfuex ) {
                throw new ParseException( "Malformed URL: " + urlString );
            }

            System.out.println( "requesting " + url + " using plain HTTP, posting file " + file );

            request( url, new File( file ) );
        } catch ( ParseException e ) {
            printHelp( e, options );
            System.exit( -1 );
        } catch ( IOException ioex ) {
            System.err.println( ioex.getMessage() );
        }
    }

    private static void printHelp( ParseException e, Options options ) {

        if ( e != null && e.getMessage() != null ) {
            if ( e instanceof MissingOptionException ) {
                System.err.println( "Missing option(s): " + e.getMessage() );
            } else if ( e instanceof MissingArgumentException ) {
                System.err.println( "Missing argument(s): " + e.getMessage() );
            } else {
                System.err.println( e.getMessage() );
            }
        }
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "java " + PlainHttpTestClient.class.getCanonicalName() + " <file> <url>",
                options );
    }

}
