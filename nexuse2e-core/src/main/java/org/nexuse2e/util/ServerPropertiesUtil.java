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
package org.nexuse2e.util;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.nexuse2e.Engine;
import org.nexuse2e.messaging.MessageContext;

public class ServerPropertiesUtil {

    public static enum ServerProperty {

        ROOTDIR("${nexus.server.root}"),
        PARTNERID("${nexus.message.partnerid}"),
        PARTNERNAME("${nexus.message.partnername}"),
        PARTNER_ID("${nexus.message.partner.id}"),
        PARTNER_ID_TYPE("${nexus.message.partner.id.type}"),
        PARTNER_NAME("${nexus.message.partner.name}"),
        PARTNER_COMPANY("${nexus.message.partner.company}"),
        LOCAL_PARTNER_ID("${nexus.message.localpartner.id}"),
        LOCAL_PARTNER_ID_TYPE("${nexus.message.localpartner.id.type}"),
        LOCAL_PARTNER_NAME("${nexus.message.localpartner.name}"),
        CHOREOGRAPHY("${nexus.message.choreography}"),
        ACTION("${nexus.message.action}"),
        DOCUMENT_TYPE("${nexus.message.action.documenttype}"),
        CONVERSATION("${nexus.message.conversation}"),
        MESSAGE("${nexus.message.message}"),
        CREATED_DATE("${nexus.message.createdDate}"),
        PAYLOAD_SEQUENCE("${nexus.message.payload.sequence}"),
        PAYLOAD_CONDITIONAL_SEQUENCE("${nexus.message.payload.conditionalsequence}"),
        PAYLOAD_MIME_TYPE("${nexus.message.payload.mimetype}"),
        SERVER_CURRENTMILLIS("${nexus.server.time.millis}"),
        SERVER_CURRENTTIME("${nexus.server.time.formated}");

        private String value;

        ServerProperty( String value ) {

            this.value = value;
        }

        public String getValue() {

            return value;
        }
    }

    /**
     * Replaces '/' and '\\' with File.seperatorChar. 
     * 
     * @param path
     * @return
     */
    public static String replacePathSeparators( String path ) {

        path.replace( '/', File.separatorChar );
        path.replace( '\\', File.separatorChar );
        return path;
    }

    /**
     * see replaceServerProperties( String value, MessageContext context )
     * @param value
     * @return
     */
    public static String replaceServerProperties( String value ) {

        return replaceServerProperties( value, null );
    }

    /**
     * replaces all variables with predefined server properties.
     * 
     * known properties (see enum ServerProperty)
     * 
     * ${nexus.server.root} - the nexusRootDiretory without the terminating separator
     * 
     * @param value
     * @param context
     * @return
     */
    public static String replaceServerProperties( String value, MessageContext context ) {

        if ( value != null ) {

            for ( ServerProperty property : ServerProperty.values() ) {
                if ( value.indexOf( '$' ) == -1 ) {
                    break;
                }
                if ( property.equals( ServerProperty.ROOTDIR ) ) {
                    String nexusRoot = Engine.getInstance().getNexusE2ERoot();
                    if ( !StringUtils.isEmpty( nexusRoot ) ) {
                        if ( nexusRoot.endsWith( "/" ) || nexusRoot.endsWith( "\\" ) ) {
                            nexusRoot = nexusRoot.substring( 0, nexusRoot.length() - 1 );
                        }
                        value = StringUtils.replace( value, property.getValue(), nexusRoot );
                    }
                } else if ( property.equals( ServerProperty.SERVER_CURRENTMILLIS ) ) {
                    value = StringUtils.replace( value, property.getValue(), ""+System.currentTimeMillis() );
                } else if ( property.equals( ServerProperty.SERVER_CURRENTTIME ) ) {
                    DateFormat df = new SimpleDateFormat( "yyyyMMddHHmmssSSS" );
                    value = StringUtils.replace( value, property.getValue(), df.format( new Date() ) );
                }
                if ( context != null ) {
                    if ( property.equals( ServerProperty.PARTNERID ) || property.equals( ServerProperty.PARTNER_ID ) ) {
                        if ( context.getMessagePojo().getParticipant().getPartner() != null ) {
                            String partnerId = context.getMessagePojo().getParticipant().getPartner().getPartnerId();
                            if ( !StringUtils.isEmpty( partnerId ) ) {
                                value = StringUtils.replace( value, property.getValue(), partnerId );
                            }
                        }
                    } else if ( property.equals( ServerProperty.PARTNER_ID_TYPE ) ) {
                        if ( context.getMessagePojo().getParticipant().getPartner() != null ) {
                            String idType = context.getMessagePojo().getParticipant().getPartner().getPartnerIdType();
                            if ( !StringUtils.isEmpty( idType ) ) {
                                value = StringUtils.replace( value, property.getValue(), idType );
                            }
                        }
                    } else if ( property.equals( ServerProperty.PARTNERNAME ) || property.equals( ServerProperty.PARTNER_NAME ) ) {
                        if ( context.getMessagePojo().getParticipant().getPartner() != null ) {
                            String partnerName = context.getMessagePojo().getParticipant().getPartner().getName();
                            if ( !StringUtils.isEmpty( partnerName ) ) {
                                value = StringUtils.replace( value, property.getValue(), partnerName );
                            }
                        }
                    } else if ( property.equals( ServerProperty.PARTNER_COMPANY ) ) {
                        if ( context.getMessagePojo().getParticipant().getPartner() != null ) {
                            String company = context.getMessagePojo().getParticipant().getPartner().getCompanyName();
                            if ( !StringUtils.isEmpty( company ) ) {
                                value = StringUtils.replace( value, property.getValue(), company );
                            }
                        }
                    } else if ( property.equals( ServerProperty.LOCAL_PARTNER_ID ) ) {
                        if ( context.getMessagePojo().getParticipant().getPartner() != null ) {
                            String partnerId = context.getMessagePojo().getParticipant().getLocalPartner().getPartnerId();
                            if ( !StringUtils.isEmpty( partnerId ) ) {
                                value = StringUtils.replace( value, property.getValue(), partnerId );
                            }
                        }
                    } else if ( property.equals( ServerProperty.LOCAL_PARTNER_ID_TYPE ) ) {
                        if ( context.getMessagePojo().getParticipant().getPartner() != null ) {
                            String idType = context.getMessagePojo().getParticipant().getLocalPartner().getPartnerIdType();
                            if ( !StringUtils.isEmpty( idType ) ) {
                                value = StringUtils.replace( value, property.getValue(), idType );
                            }
                        }
                    } else if ( property.equals( ServerProperty.LOCAL_PARTNER_NAME ) ) {
                        if ( context.getMessagePojo().getParticipant().getPartner() != null ) {
                            String partnerName = context.getMessagePojo().getParticipant().getLocalPartner().getName();
                            if ( !StringUtils.isEmpty( partnerName ) ) {
                                value = StringUtils.replace( value, property.getValue(), partnerName );
                            }
                        }

                    } else if ( property.equals( ServerProperty.CHOREOGRAPHY ) ) {
                        if ( context.getMessagePojo().getConversation().getChoreography() != null ) {
                            String choreography = context.getMessagePojo().getConversation().getChoreography().getName();
                            if ( !StringUtils.isEmpty( choreography ) ) {
                                value = StringUtils.replace( value, property.getValue(), choreography );
                            }
                        }
                    } else if ( property.equals( ServerProperty.ACTION ) ) {
                        if ( context.getConversation() != null && context.getConversation().getCurrentAction() != null ) {
                            String action = context.getConversation().getCurrentAction().getName();
                            if ( !StringUtils.isEmpty( action ) ) {
                                value = StringUtils.replace( value, property.getValue(), action );
                            }
                        } else if(context.getMessagePojo() != null && context.getMessagePojo().getAction() != null) {
                            String action = context.getMessagePojo().getAction().getName();
                            if ( !StringUtils.isEmpty( action ) ) {
                                value = StringUtils.replace( value, property.getValue(), action );
                            }
                        }
                    } else if (property.equals( ServerProperty.DOCUMENT_TYPE )) {
                        if (context.getConversation() != null && context.getConversation().getCurrentAction() != null) {
                            String docType = context.getConversation().getCurrentAction().getDocumentType();
                            if (!StringUtils.isEmpty( docType )) {
                                value = StringUtils.replace( value, property.getValue(), docType );
                            }
                        } else if(context.getMessagePojo() != null && context.getMessagePojo().getAction() != null) {
                            String docType = context.getMessagePojo().getAction().getDocumentType();
                            if ( !StringUtils.isEmpty( docType ) ) {
                                value = StringUtils.replace( value, property.getValue(), docType );
                            }
                        }
                    } else if ( property.equals( ServerProperty.CONVERSATION ) ) {
                        if ( context.getConversation() != null ) {
                            String conversationId = context.getConversation().getConversationId();
                            conversationId = conversationId.replaceAll( "[?:\\/*\"<>|]", "_" );
                            if ( !StringUtils.isEmpty( conversationId ) ) {
                                value = StringUtils.replace( value, property.getValue(), conversationId );
                            }
                        }
                    } else if ( property.equals( ServerProperty.MESSAGE ) ) {
                        if ( context.getMessagePojo() != null ) {
                            String messageId = context.getMessagePojo().getMessageId();
                            if ( !StringUtils.isEmpty( messageId ) ) {
                                messageId = messageId.replaceAll( "[?:\\/*\"<>|]", "_" );
                                value = StringUtils.replace( value, property.getValue(), messageId );
                            }
                        }
                    } else if ( property.equals( ServerProperty.CREATED_DATE ) ) {
                        if ( ( context.getMessagePojo() != null )
                                && ( context.getMessagePojo().getCreatedDate() != null ) ) {
                            DateFormat df = new SimpleDateFormat( "yyyyMMddHHmmssSSS" );
                            value = StringUtils.replace( value, property.getValue(), df.format( context
                                    .getMessagePojo().getCreatedDate() ) );
                        }
                    }
                }
            }
        }
        return value;
    }

    
    /**
     * Replace all payload-dependent variables with their values.
     * @param value The string value to be replaced.
     * @param sequenceNumber The payload sequence number.
     * @param context The message context.
     * @param anySequenceNumber If <code>true</code>, indicates that all sequence numbers shall be replaced. Otherwise,
     * only sequence number less than or equal to the payload count will be replaced.
     * @return The replaced string value.
     */
    public static String replacePayloadDependentValues( String value, int sequenceNumber, MessageContext context, boolean anySequenceNumber ) {
        
        if ( value != null && context != null && context.getMessagePojo() != null
                && context.getMessagePojo().getMessagePayloads() != null ) {
            if ( anySequenceNumber || sequenceNumber <= context.getMessagePojo().getMessagePayloads().size() ) {

                value = StringUtils.replace( value, ServerProperty.PAYLOAD_SEQUENCE.getValue(), "" + sequenceNumber );
                if ( context.getMessagePojo().getMessagePayloads().size() > 1 ) {
                    value = StringUtils.replace( value, ServerProperty.PAYLOAD_CONDITIONAL_SEQUENCE.getValue(), ""
                            + sequenceNumber );
                } else {
                    value = StringUtils.replace( value, ServerProperty.PAYLOAD_CONDITIONAL_SEQUENCE.getValue(), "");
                }
                
                if ( !anySequenceNumber ) {
                    String extension = Engine.getInstance().getFileExtensionFromMime(
                            context.getMessagePojo().getMessagePayloads().get( sequenceNumber - 1 ).getMimeType().toLowerCase() );
                    if ( StringUtils.isEmpty( extension ) ) {
                        extension = "dat";
                    }
                    value = StringUtils.replace( value, ServerProperty.PAYLOAD_MIME_TYPE.getValue(), extension );
                }
            }
        }

        return value;
    }

        
        
    public static String replacePayloadDependedValues( String value, int payload, MessageContext context ) {
        return replacePayloadDependentValues( value, payload, context, false );
    }

}
