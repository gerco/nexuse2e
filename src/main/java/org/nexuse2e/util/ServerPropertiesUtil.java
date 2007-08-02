package org.nexuse2e.util;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.nexuse2e.Engine;
import org.nexuse2e.messaging.MessageContext;

public class ServerPropertiesUtil {

    public static enum ServerProperty {

        ROOTDIR("${nexus.server.root}"), PARTNERID("${nexus.message.partnerid}"), PARTNERNAME(
                "${nexus.message.partnername}"), CHOREOGRAPHY("${nexus.message.choreography}"), ACTION(
                "${nexus.message.action}");

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

        for ( ServerProperty property : ServerProperty.values() ) {
            if ( value.indexOf( '$' ) == -1 ) {
                break;
            }
            if ( property.equals( ServerProperty.ROOTDIR ) ) {
                String nexusRoot = Engine.getInstance().getNexusE2ERoot();
                if ( !StringUtils.isEmpty( nexusRoot ) ) {
                    if(nexusRoot.endsWith( "/" ) || nexusRoot.endsWith( "\\" )) {
                        nexusRoot = nexusRoot.substring( 0,nexusRoot.length()-1 );
                    }
                    value = StringUtils.replace( value, property.getValue(), nexusRoot );
                }
            }
            if ( context != null ) {
                if ( property.equals( ServerProperty.PARTNERID ) ) {
                    if ( context.getPartner() != null ) {
                        String partnerId = context.getPartner().getPartnerId();
                        if ( !StringUtils.isEmpty( partnerId ) ) {
                            value = StringUtils.replace( value, property.getValue(), partnerId );
                        }
                    }
                } else if ( property.equals( ServerProperty.PARTNERNAME ) ) {
                    if ( context.getPartner() != null ) {
                        String partnerName = context.getPartner().getName();
                        if ( !StringUtils.isEmpty( partnerName ) ) {
                            value = StringUtils.replace( value, property.getValue(), partnerName );
                        }
                    }
                } else if ( property.equals( ServerProperty.CHOREOGRAPHY ) ) {
                    if ( context.getChoreography() != null ) {
                        String choreography = context.getChoreography().getName();
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
                    }
                }
            }

        }
        return value;
    }

}