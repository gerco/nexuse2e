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
package org.nexuse2e.configuration;

import java.util.UUID;

import org.nexuse2e.NexusException;

public class NexusUUIDGenerator implements IdGenerator {

    // private static Logger LOG = Logger.getLogger( NexusUUIDGenerator.class );

    public String getId() throws NexusException {

        /*
        String uuid = null;
        String uid = null;
        String host = null;

        try {
            host = java.net.InetAddress.getLocalHost().getHostName();
            uid = new java.rmi.server.UID().toString();
            uuid = host + "/" + uid;
        } catch ( UnknownHostException e ) {
            LOG.error( "Error while creating UUID:" + e.getMessage() );
            throw new NexusException( "unable to create UUID", e );
        }
        return uuid;
        */

        // ISO-11578 compliant UUIDs
        UUID isoUuid = UUID.randomUUID();
        return isoUuid.toString();
    }

}
