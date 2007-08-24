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

package org.nexuse2e.test.webservice;

import java.net.MalformedURLException;

import org.codehaus.xfire.client.XFireProxyFactory;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.nexuse2e.integration.NEXUSe2eInterface;

/**
 * @author jonas.reese
 */
public class NEXUSe2eInterfaceWSClient {

    /**
     * @param args
     */
    public static void main( String[] args ) throws Exception {

        Service serviceModel = new ObjectServiceFactory().create( NEXUSe2eInterface.class );
        try {
            NEXUSe2eInterface service = (NEXUSe2eInterface) new XFireProxyFactory().create( serviceModel,
                    "http://localhost:8080/NEXUSe2e/webservice/NEXUSe2eInterface" );
            String result = service.sendNewStringMessage( "GenericFile", "asdf", "SendFile", "<test></test>" );

            System.out.println( "Result: " + result );
        } catch ( MalformedURLException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
