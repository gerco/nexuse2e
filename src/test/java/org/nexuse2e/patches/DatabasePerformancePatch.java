package org.nexuse2e.patches;

import org.nexuse2e.NexusException;
import org.nexuse2e.patch.Patch;
import org.nexuse2e.patch.PatchException;


public class DatabasePerformancePatch extends DatabasePatch implements Patch {

    private String[][] statements = {
            
        {"nx_message","fk_message_message_id","message_id","CREATE INDEX fk_message_message_id ON nx_message(message_id)"},
        {"nx_message","fk_message_created_date","created_date","CREATE INDEX fk_message_created_date ON nx_message(created_date)"},
        {"nx_message","fk_message_recover","status,outbound","CREATE INDEX fk_message_recover ON nx_message(status,direction_flag)"},
        
        {"nx_log","fk_log_created_date","created_date","CREATE INDEX fk_log_created_date ON nx_log(created_date)"},
        
        {"nx_conversation","fk_conversation_created_date","created_date","CREATE INDEX fk_conversation_created_date ON nx_conversation(created_date)"},
        {"nx_conversation","fk_conversation_conversation_id","conversation_id","CREATE INDEX fk_conversation_conversation_id ON nx_conversation(conversation_id)"}
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
                    e.printStackTrace();
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

        return "modifies the database schema for performance optimization";
    }

    public String getPatchName() {

        return "Database Performance Patch";
    }

    public String getVersionInformation() {

        return "0.1";
    }

    public boolean isExecutedSuccessfully() {

        return false;
    }

    

}
