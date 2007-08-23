package org.nexuse2e.test.webservice;

import org.apache.log4j.Logger;
import org.nexuse2e.integration.BackendDeliveryInterface;
import org.nexuse2e.integration.ProcessInboundMessageException;

/**
 * Created: 22.08.2007
 * TODO Class documentation
 *
 * @author jonas.reese
 * @version $LastChangedRevision$ - $LastChangedDate$ by $LastChangedBy$
 */
public class BackendDeliveryWS implements BackendDeliveryInterface {
    
    private static Logger LOG = Logger.getLogger( BackendDeliveryWS.class );

    /* (non-Javadoc)
     * @see org.nexuse2e.integration.BackendDeliveryInterface#processInboundMessage(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String[])
     */
    public String processInboundMessage(
            String choreographyId, String businessPartnerId, String actionId,
            String conversationId, String messageId, String[] payload ) throws ProcessInboundMessageException {
        LOG.info( "choreographyId=" + choreographyId + " businessPartnerId="
                + businessPartnerId + " actionId=" + actionId
                + " conversationId=" + conversationId + " messageId=" + messageId + " payload=" + payload );
        return "success";
    }

    
/*
 * Add this to the WebServiceDispatcher-servlet.xml bean configuration:
 * 
    <!-- BEGIN TESTING SECTION -->
    <bean id="BackendDeliveryInterface" class="org.codehaus.xfire.spring.remoting.XFireExporter">
        <property name="serviceFactory">
            <ref bean="xfire.serviceFactory"/>
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
 */
}