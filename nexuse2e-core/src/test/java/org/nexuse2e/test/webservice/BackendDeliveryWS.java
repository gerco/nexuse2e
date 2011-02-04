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
package org.nexuse2e.test.webservice;

import javax.jws.WebService;

import org.apache.log4j.Logger;
import org.nexuse2e.integration.BackendDeliveryInterface;

/**
 * @author jonas.reese
 * @version $LastChangedRevision$ - $LastChangedDate$ by $LastChangedBy$
 */
@WebService(endpointInterface="org.nexuse2e.integration.BackendDeliveryInterface")
public class BackendDeliveryWS implements BackendDeliveryInterface {
    
    private static Logger LOG = Logger.getLogger( BackendDeliveryWS.class );

    /* (non-Javadoc)
     * @see org.nexuse2e.integration.BackendDeliveryInterface#processInboundMessage(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String[])
     */
    public void processInboundMessage(
            String choreographyId,
            String businessPartnerId,
            String actionId,
            String conversationId,
            String messageId,
            String payload) {
        LOG.info( "choreographyId=" + choreographyId + " businessPartnerId="
                + businessPartnerId + " actionId=" + actionId
                + " conversationId=" + conversationId + " messageId=" + messageId + " payload=" + payload );
        return;
    }

    
/*
 * Add this to the NexusWebServiceDispatcher-servlet.xml bean configuration:
 * 
    <!-- BEGIN TESTING SECTION -->
    <bean id="xfire.annotServiceFactory"
      class="org.codehaus.xfire.annotations.AnnotationServiceFactory"
      singleton="true">
    </bean>
    <bean id="BackendDeliveryInterface" class="org.codehaus.xfire.spring.remoting.XFireExporter">
        <property name="serviceFactory">
            <ref bean="xfire.annotServiceFactory"/>
        </property>
        <property name="xfire">
            <ref bean="xfire"/>
        </property>
        <property name="serviceBean">
            <bean class="org.nexuse2e.test.webservice.BackendDeliveryWS"/>
        </property>
        <property name="serviceClass">
            <value>org.nexuse2e.integration.BackendDeliveryInterface</value>
        </property>
    </bean>
    <!-- END TESTING SECTION -->
    
 *
 * Add this to the URL handler mapping 
 * 
    <entry key="/BackendDeliveryInterface">
        <ref bean="BackendDeliveryInterface"/>
    </entry>
 * 
 * For client testing against the output logger, add this to URL handler mapping:
 * 
    <entry key="/RequestLogger">
        <bean class="org.nexuse2e.test.RequestLogger"/>
    </entry>
 */
}
