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
package org.nexuse2e.client.webservice;

import java.io.FileInputStream;

import org.codehaus.xfire.client.XFireProxyFactory;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.nexuse2e.integration.NEXUSe2eInterface;

public class NEXUSe2eInterfaceClient {

    private static String serviceURLString = "http://localhost:8080/NexusE2EServerNG/webservice/NEXUSe2eInterface";

    /**
     * @param args
     */
    public static void main( String[] args ) {

        String webServiceURL = serviceURLString;

        try {

            ObjectServiceFactory objectServiceFactory = new ObjectServiceFactory();
            Service serviceModel = objectServiceFactory.create( NEXUSe2eInterface.class );
            NEXUSe2eInterface service = (NEXUSe2eInterface) new XFireProxyFactory()
                    .create( serviceModel, webServiceURL );

            // String result = service.createConversation( "GenericFile", "torino8080" );
            // String result = service.sendNewStringMessage( "GenericFile", "torino8080", "SendFile", "<test who=\"roma\" />" );

            FileInputStream fis = new FileInputStream( "/Volumes/NexusE2E/cidx/demo/demo-cidx-docs/ShipNotice_new.xml" );
            byte[] buffer = new byte[fis.available()];
            fis.read( buffer );
            fis.close();

            String result = service
                    .sendNewStringMessage( "ShipNotice", "293032454", "ShipNotice", new String( buffer ) );

            System.out.println( "NEXUSe2EInterfaceService done: " + result );
        } catch ( Exception e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
