package org.nexuse2e.patches;

import org.nexuse2e.NexusException;
import org.nexuse2e.patch.Patch;
import org.nexuse2e.patch.PatchException;


public class MSSQLIndexPatch extends DatabasePatch implements Patch {

    private String[][] statements = {
            {"nx_action","fk_action_status_pipeline_id","status_update_nx_pipeline_id","CREATE INDEX fk_action_status_pipeline_id ON nx_action(status_update_nx_pipeline_id)"},
            {"nx_action","fk_action_inbound_pipeline_id","inbound_nx_pipeline_id","CREATE INDEX fk_action_inbound_pipeline_id ON nx_action(inbound_nx_pipeline_id)"},
            {"nx_action","fk_action_outbound_pipeline_id","outbound_nx_pipeline_id","CREATE INDEX fk_action_outbound_pipeline_id ON nx_action(outbound_nx_pipeline_id)"},
            {"nx_action","fk_action_choreography_id","nx_choreography_id","CREATE INDEX fk_action_choreography_id ON nx_action(nx_choreography_id)"},
            
            {"nx_certificate","fk_certificate_partner_id","nx_partner_id","CREATE INDEX fk_certificate_partner_id ON nx_certificate(nx_partner_id)"},
            
            {"nx_choreography","name","name","CREATE INDEX name unique ON nx_choreography(name)"},
            
            {"nx_connection","fk_connection_certificate_id","nx_certificate_id","CREATE INDEX fk_connection_certificate_id ON nx_connection(nx_certificate_id)"},
            {"nx_connection","fk_connection_partner_id","nx_partner_id","CREATE INDEX fk_connection_partner_id ON nx_connection(nx_partner_id)"},
            {"nx_connection","fk_connection_trp_id","nx_trp_id","CREATE INDEX fk_connection_trp_id ON nx_connection(nx_trp_id)"},
            
            {"nx_conversation","fk_conversation_current_action_id","current_nx_action_id","CREATE INDEX fk_conversation_current_action_id ON nx_conversation(current_nx_action_id)"},
            {"nx_conversation","fk_conversation_partner_id","nx_partner_id","CREATE INDEX fk_conversation_partner_id ON nx_conversation(nx_partner_id)"},
            {"nx_conversation","fk_conversation_choreography_id","nx_choreography_id","CREATE INDEX fk_conversation_choreography_id ON nx_conversation(nx_choreography_id)"},
            
            {"nx_follow_up_action","fk_fua_follow_up_action_id","follow_up_nx_action_id","CREATE INDEX fk_fua_follow_up_action_id ON nx_follow_up_action(follow_up_nx_action_id)"},
            {"nx_follow_up_action","fk_fua_action_id","nx_action_id","CREATE INDEX fk_fua_action_id ON nx_follow_up_action(nx_action_id)"},
            
            {"nx_grant","fk_grant_grant_id","nxGrantId","CREATE INDEX fk_grant_grant_id ON nx_grant(nxGrantId)"},
            
            {"nx_logger","fk_logger_component_id","nx_component_id","CREATE INDEX fk_logger_component_id ON nx_logger(nx_component_id)"},
            
            {"nx_logger_param","fk_logger_param_logger_id","nx_logger_id","CREATE INDEX fk_logger_param_logger_id ON nx_logger_param(nx_logger_id)"},
            
            {"nx_message","fk_message_conersation_id","nx_conversation_id","CREATE INDEX fk_message_conersation_id ON nx_message(nx_conversation_id)"},
            {"nx_message","fk_message_trp_id","nx_trp_id","CREATE INDEX fk_message_trp_id ON nx_message(nx_trp_id)"},
            {"nx_message","fk_message_action_id","nx_action_id","CREATE INDEX fk_message_action_id ON nx_message(nx_action_id)"},
            {"nx_message","fk_message_ref_message_id","referenced_nx_message_id","CREATE INDEX fk_message_ref_message_id ON nx_message(referenced_nx_message_id)"},
            
            {"nx_message_label","fk_message_label_message_id","nx_message_id","CREATE INDEX fk_message_label_message_id ON nx_message_label(nx_message_id)"},
            
            {"nx_message_payload","fk_message_payload_message_id","nx_message_id","CREATE INDEX fk_message_payload_message_id ON nx_message_payload(nx_message_id)"},
            
            {"nx_participant","fk_part_connection_id","nx_connection_id","CREATE INDEX fk_part_connection_id ON nx_participant(nx_connection_id)"},
            {"nx_participant","fk_part_partner_id","nx_partner_id","CREATE INDEX fk_part_partner_id ON nx_participant(nx_partner_id)"},
            {"nx_participant","fk_part_local_cert_id","nx_local_certificate_id","CREATE INDEX fk_part_local_cert_id ON nx_participant(nx_local_certificate_id)"},
            {"nx_participant","fk_part_choreography_id","nx_choreography_id","CREATE INDEX fk_part_choreography_id ON nx_participant(nx_choreography_id)"},
            {"nx_participant","fk_part_local_partner_id","nx_local_partner_id","CREATE INDEX fk_part_local_partner_id ON nx_participant(nx_local_partner_id)"},
            
            {"nx_partner","type","type,partner_id","CREATE INDEX type ON nx_partner(type,partner_id)"},
            
            {"nx_persistent_property","namespace","namespace,version,name","CREATE INDEX namespace unique ON nx_persistent_property(namespace,version,name)"},
            
            {"nx_pipelet","fk_pipelet_component_id","nx_component_id","CREATE INDEX fk_pipelet_component_id ON nx_pipelet(nx_component_id)"},
            {"nx_pipelet","fk_pipelet_pipeline_id","nx_pipeline_id","CREATE INDEX fk_pipelet_pipeline_id ON nx_pipelet(nx_pipeline_id)"},
            
            {"nx_pipelet_param","fk_pipelet_param_pipeline_id","nx_pipelet_id","CREATE INDEX fk_pipelet_param_pipeline_id ON nx_pipelet_param(nx_pipelet_id)"},
            
            {"nx_pipeline","fk_pipeline_trp_id","nx_trp_id","CREATE INDEX fk_pipeline_trp_id ON nx_pipeline(nx_trp_id)"},
            
            {"nx_service","fk_service_component_id","nx_component_id","CREATE INDEX fk_service_component_id ON nx_service(nx_component_id)"},
            
            {"nx_service_param","fk_service_param_service_id","nx_service_id","CREATE INDEX fk_service_param_service_id ON nx_service_param(nx_service_id)"},
            
            {"nx_user","fk_user_role_id","nx_role_id","CREATE INDEX fk_user_role_id ON nx_user(nx_role_id)"}
            };
    
    public void executePatch() throws PatchException {
        try {
            
            for(int i = 0; i < statements.length; i++) {
                String[] statement = statements[i];
                
                boolean isPresent = true;
                try {
                    isPresent = isIndexPresent( statement[1],statement[0],statement[2],null );
                    patchReporter.info( "isIndexPresent:"+ isPresent );
                } catch ( NexusException e ) {
                    patchReporter.info( "index for columns("+statement[2]+") already exists" );
                    continue;
                    
                }
                if(!isPresent){
                    alterTable( statement[3], statement[0], null);
                    patchReporter.info( "alter table: "+statement[3] );
                }
            }
        } catch ( NexusException ne ) {
            throw new PatchException("error while creating indexes: "+ne.getMessage(),ne);
        }
    }

    public String getPatchDescription() {

        return "This patch while check the database metadata and adds missing indexes";
    }

    public String getPatchName() {

        return "MS SQL Database Patch";
    }

    public String getVersionInformation() {

        return "0.1";
    }

    public boolean isExecutedSuccessfully() {

        return false;
    }

}
