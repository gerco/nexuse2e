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
package org.nexuse2e.client.webservice;

import java.io.FileInputStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.nexuse2e.integration.NEXUSe2eInterface;

public class NEXUSe2eInterfaceClient {

    public final static String CHOREOGRAPHY     = "c";
    public final static String URL              = "u";
    public final static String PARTNER          = "p";
    public final static String ACTION           = "a";
    public final static String FILENAME         = "f";

    private static String      serviceURLString = "http://localhost:8080/NEXUSe2e/cxf/NEXUSe2eInterface";

    /**
     * @param args
     */
    public static void main( String[] args ) {

        String webServiceURL = serviceURLString;
        String fileName = null;
        String choreographyId = null;
        String partnerId = null;
        String actionId = null;
        Options options = null;

        try {

            options = new Options();
            options.addOption( URL, "url", true, "Web service URL" );
            options.addOption( FILENAME, "file", true, "File name" );
            options.addOption( CHOREOGRAPHY, "choreography", true, "Choreography ID" );
            options.addOption( PARTNER, "partner", true, "Partner ID" );
            options.addOption( ACTION, "action", true, "Action ID" );

            CommandLineParser parser = new PosixParser();
            CommandLine cl = parser.parse( options, args );

            if ( !cl.hasOption( URL ) || !cl.hasOption( FILENAME ) || !cl.hasOption( CHOREOGRAPHY )
                    || !cl.hasOption( PARTNER ) || !cl.hasOption( ACTION ) ) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp( "org.nexuse2e.client.webservice.NEXUSe2eInterfaceClient", options );
                System.exit( 1 );
            }

            webServiceURL = cl.getOptionValue( URL );
            fileName = cl.getOptionValue( FILENAME );
            choreographyId = cl.getOptionValue( CHOREOGRAPHY );
            partnerId = cl.getOptionValue( PARTNER );
            actionId = cl.getOptionValue( ACTION );

            System.out.println( "webServiceURL: " + webServiceURL );
            System.out.println( "fileName: " + fileName );
            System.out.println( "choreographyId: " + choreographyId );
            System.out.println( "partnerId: " + partnerId );
            System.out.println( "actionId: " + actionId );

            //QName serviceName = new QName( "http://integration.nexuse2e.org", "NEXUSe2eInterface" );
            //QName portName = new QName( "http://integration.nexuse2e.org", "NEXUSe2eInterfacePort" );
            
            JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
            factory.getInInterceptors().add(new LoggingInInterceptor());
            factory.getOutInterceptors().add(new LoggingOutInterceptor());
            factory.setServiceClass(NEXUSe2eInterface.class);
            factory.setAddress(webServiceURL);
            NEXUSe2eInterface nexuse2eInterface = (NEXUSe2eInterface) factory.create();
            
            
            // String result = service.createConversation( "GenericFile", "torino8080" );
            // String result = service.sendNewStringMessage( "GenericFile", "torino8080", "SendFile", "<test who=\"roma\" />" );

            FileInputStream fis = new FileInputStream( fileName );
            byte[] buffer = new byte[fis.available()];
            fis.read( buffer );
            fis.close();

            String result = nexuse2eInterface.sendNewStringMessage( choreographyId, partnerId, actionId, new String( buffer ) );

            System.out.println( "NEXUSe2EInterfaceService done: " + result );
        } catch ( Throwable t ) {
            // TODO Auto-generated catch block
            t.printStackTrace();
        }

    }

}
