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
        {"nx_conversation","fk_conv_conv_id","conversation_id","CREATE INDEX fk_conv_conv_id ON nx_conversation(conversation_id)"}
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
