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

import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.nexuse2e.integration.EngineStatusInterface;

/**
 * @author mbreilmann
 *
 */
public class EngineStatusWSClient {

    /**
     * @param args
     */
    public static void main( String[] args ) throws Exception {

        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.getInInterceptors().add( new LoggingInInterceptor() );
        factory.getOutInterceptors().add( new LoggingOutInterceptor() );
        factory.setServiceClass( EngineStatusInterface.class );
        factory.setAddress( "http://localhost:8080/NEXUSe2e/webservice/EngineStatusInterface" );
        EngineStatusInterface service = (EngineStatusInterface) factory.create();

        String engineStatus = service.getEngineStatus();

        System.out.println( "EngineStatusSummary: " + engineStatus );
    }

}
