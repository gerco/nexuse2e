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
     * @param value
     * @param payload
     * @param context
     * @return
     */
    public static String replacePayloadDependedValues( String value, int payload, MessageContext context ) {

        if ( value != null && context != null && context.getMessagePojo() != null
                && context.getMessagePojo().getMessagePayloads() != null ) {
            if ( payload <= context.getMessagePojo().getMessagePayloads().size() ) {

                value = StringUtils.replace( value, ServerProperty.PAYLOAD_SEQUENCE.getValue(), "" + payload );
                if ( context.getMessagePojo().getMessagePayloads().size() > 1 ) {
                    value = StringUtils.replace( value, ServerProperty.PAYLOAD_CONDITIONAL_SEQUENCE.getValue(), ""
                            + payload );
                } else {
                    value = StringUtils.replace( value, ServerProperty.PAYLOAD_CONDITIONAL_SEQUENCE.getValue(), "");
                }
                
                String extension = Engine.getInstance().getFileExtensionFromMime(
                        context.getMessagePojo().getMessagePayloads().get( payload-1 ).getMimeType().toLowerCase() );
                if ( StringUtils.isEmpty( extension ) ) {
                    extension = "dat";
                }
                value = StringUtils.replace( value, ServerProperty.PAYLOAD_MIME_TYPE.getValue(), extension );
            }
        }

        return value;
    }

}
