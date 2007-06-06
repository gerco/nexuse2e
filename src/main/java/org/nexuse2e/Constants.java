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
package org.nexuse2e;

/**
 * Constants used throughout the entire NEXUSe2e system
 *
 * @author mbreilmann
 */
public class Constants {

    public static String       HIBERNATESESSIONFACTORYBEANID      = "HybernateSessionFactory";

    // DAO bean IDs
    public static final String ACTION_DAO                         = "actionDao";
    public static final String CHOREOGRAPHY_DAO                   = "choreographyDao";
    public static final String PARTICIPANT_DAO                    = "participantDao";
    public static final String CERTIFICATE_DAO                    = "certificateDao";
    public static final String COMMUNICATIONPARTNERDAO            = "communicationPartnerDao";
    public static final String CONVERSATION_DAO                   = "conversationDao";
    public static final String MESSAGE_DAO                        = "messageDao";

    // skeleton bean IDs
    public static final String FRONTEND_INBOUND_DISPATCHER        = "frontendInboundDispatcher";
    public static final String FRONTEND_OUTBOUND_DISPATCHER       = "frontendOutboundDispatcher";
    public static final String BACKEND_INBOUND_DISPATCHER         = "backendInboundDispatcher";
    public static final String BACKEND_OUTBOUND_DISPATCHER        = "backendOutboundDispatcher";
    public static final String BACKEND_PIPELINE_DISPATCHER        = "backendPipelineDispatcher";
    public static final String TRANSACTION_SERVICE                = "transactionService";

    public static final String TRANSPORT_DISPATCHER_MAPPING       = "nexusTransportDispatcherMapping";

    // naming postfixes
    public static final String POSTFIX_INBOUND_QUEUE              = "-Inbound-Queue";
    public static final String POSTFIX_FRONTEND_ACTION_SERIALIZER = "-Frontend-Action-Serializer";
    public static final String POSTFIX_BACKEND_ACTION_SERIALIZER  = "-Backend-Action-Serializer";

    public static final String POSTFIX_BACKEND_PIPELINE  = "-Backend-Pipeline";
    public static final String POSTFIX_FRONTEND_PIPELINE  = "-Frontend-Pipeline";
    
    /**
     * the possible types for mapping value pairs
     * Do not change the order of this Enumeration
     */
    public static enum MappingType {
        STRING, INT, BOOLEAN ;
        
    }
    
    /**
     * The possible states of a bean that implements the <code>Manageable</code> interface.
     * @see org.nexuse2e.Manageable
     */
    public static enum BeanStatus {
        ERROR(-1), UNDEFINED(0), INSTANTIATED(1), INITIALIZED(2), ACTIVATED(3), STARTED(4);

        private int value;

        BeanStatus( int value ) {

            this.value = value;
        }

        public int getValue() {

            return value;
        }
    };

    public static final String ID_GENERATOR_MESSAGE                          = "messageId";
    public static final String ID_GENERATOR_CONVERSATION                     = "conversationId";
    public static final String ID_GENERATOR_MESSAGE_PAYLOAD                  = "messagePayloadId";

    // conversation status
    public static final int    CONVERSATION_STATUS_ERROR                     = -1;
    public static final int    CONVERSATION_STATUS_UNKNOWN                   = 0;
    public static final int    CONVERSATION_STATUS_CREATED                   = 1;
    public static final int    CONVERSATION_STATUS_PROCESSING                = 2;
    public static final int    CONVERSATION_STATUS_AWAITING_ACK              = 3;
    public static final int    CONVERSATION_STATUS_IDLE                      = 4;
    public static final int    CONVERSATION_STATUS_SENDING_ACK               = 5;
    public static final int    CONVERSATION_STATUS_ACK_SENT_AWAITING_BACKEND = 6;
    public static final int    CONVERSATION_STATUS_AWAITING_BACKEND          = 7;
    public static final int    CONVERSATION_STATUS_BACKEND_SENT_SENDING_ACK  = 8;
    public static final int    CONVERSATION_STATUS_COMPLETED                 = 9;

    // message status
    public static final int    MESSAGE_STATUS_FAILED                         = -1;
    public static final int    MESSAGE_STATUS_UNKNOWN                        = 0;
    public static final int    MESSAGE_STATUS_RETRYING                       = 1;
    public static final int    MESSAGE_STATUS_QUEUED                         = 2;
    public static final int    MESSAGE_STATUS_SENT                           = 3;
    public static final int    MESSAGE_STATUS_STOPPED                        = 4;

    // Database ID/primary key of system user
    public static final int    SYSTEM_USER_ID                                = 1;

    /**
     * The configuration root directory relative to Nexus home.
     */
    public static final String CONFIGROOT                                    = "WEB-INF/config/";

    /**
     * The default mime config file.
     */
    public static final String DEFAULT_MIME_CONFIG                           = "MimeConfig.xml";

    /**
     * The location of the TransportProtocol.xml root.
     */
    public static final String DERBYROOT                                     = CONFIGROOT + "database/derby/";

    public static final String PROTOCOL_ID_HTTP_PLAIN                        = "httpplain";

    /**
     * The possible run levels of a bean that implements the <code>Manageable</code> interface. 
     * The concept of run levels is similar to that used in operating systems like Linux.
     * @see org.nexuse2e.Manageable
     */
    public static enum Runlevel {
        UNKNOWN, CREATED, CONFIGURATION, CORE, OUTBOUND_PIPELINES, INBOUND_PIPELINES, INTERFACES
    };

}
