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
package org.nexuse2e.configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nexuse2e.Manageable;
import org.nexuse2e.NexusException;
import org.nexuse2e.backend.BackendPipelineDispatcher;
import org.nexuse2e.logging.LogAppender;
import org.nexuse2e.messaging.BackendInboundDispatcher;
import org.nexuse2e.messaging.BackendOutboundDispatcher;
import org.nexuse2e.messaging.FrontendInboundDispatcher;
import org.nexuse2e.messaging.FrontendOutboundDispatcher;
import org.nexuse2e.messaging.MessageProcessor;
import org.nexuse2e.service.Service;

/**
 * @author gesch
 *
 */

public class StaticBeanContainer {
    
    private Map<String, Manageable> managableBeans = new HashMap<String, Manageable>( 100 );


    /**
     * @return the managableBeans
     */
    public Map<String, Manageable> getManagableBeans() {

        return managableBeans;
    }

    /**
     * @param managableBeans the managableBeans to set
     */
    public void setManagableBeans( Map<String, Manageable> managableBeans ) {

        this.managableBeans = managableBeans;
    }

    /**
     * @return
     */
    public FrontendInboundDispatcher getFrontendInboundDispatcher() {

        if ( managableBeans != null ) {

            Object bean = managableBeans.get( org.nexuse2e.Constants.FRONTEND_INBOUND_DISPATCHER );
            if ( bean != null && bean instanceof FrontendInboundDispatcher ) {
                return (FrontendInboundDispatcher) bean;
            }
        }
        return null;
    }

    /**
     * @return
     */
    public FrontendOutboundDispatcher getFrontendOutboundDispatcher() {

        if ( managableBeans != null ) {

            Object bean = managableBeans.get( org.nexuse2e.Constants.FRONTEND_OUTBOUND_DISPATCHER );
            if ( bean != null && bean instanceof FrontendOutboundDispatcher ) {
                return (FrontendOutboundDispatcher) bean;
            }
        }
        return null;
    }

    /**
     * @return
     */
    public BackendInboundDispatcher getBackendInboundDispatcher() {

        if ( managableBeans != null ) {

            Object bean = managableBeans.get( org.nexuse2e.Constants.BACKEND_INBOUND_DISPATCHER );
            if ( bean != null && bean instanceof BackendInboundDispatcher ) {
                return (BackendInboundDispatcher) bean;
            }
        }
        return null;
    }

    /**
     * @return
     */
    public BackendOutboundDispatcher getBackendOutboundDispatcher() {

        if ( managableBeans != null ) {

            Object bean = managableBeans.get( org.nexuse2e.Constants.BACKEND_OUTBOUND_DISPATCHER );
            if ( bean != null && bean instanceof BackendOutboundDispatcher ) {
                return (BackendOutboundDispatcher) bean;
            }
        }
        return null;
    }

    /**
     * @return
     */
    public BackendPipelineDispatcher getBackendPipelineDispatcher() {

        if ( managableBeans != null ) {

            Object bean = managableBeans.get( org.nexuse2e.Constants.BACKEND_PIPELINE_DISPATCHER );
            if ( bean != null && bean instanceof BackendPipelineDispatcher ) {
                return (BackendPipelineDispatcher) bean;
            }
        }
        return null;
    }

    /**
     * Gets the response pipeline endpoint for frontend inbound pipelines.
     * 
     * @return The frontend inbound response endpoint, or <code>null</code> if not configured.
     */
    public MessageProcessor getFrontendInboundResponseEndpoint() {
        
        if ( managableBeans != null ) {

            Object bean = managableBeans.get( org.nexuse2e.Constants.FRONTEND_INBOUND_RESPONSE_ENDPOINT );
            if ( bean instanceof MessageProcessor ) {
                return (MessageProcessor) bean;
            }
        }
        return null;
    }
    
    /**
     * Gets the response pipeline endpoint for frontend outbound pipelines.
     * 
     * @return The frontend outbound response endpoint, or <code>null</code> if not configured.
     */
    public MessageProcessor getFrontendOutboundResponseEndpoint() {
        
        if ( managableBeans != null ) {

            Object bean = managableBeans.get( org.nexuse2e.Constants.FRONTEND_OUTBOUND_RESPONSE_ENDPOINT );
            if ( bean instanceof MessageProcessor ) {
                return (MessageProcessor) bean;
            }
        }
        return null;
    }
    
    /**
     * Gets a logger by it's unique name.
     * @param name the logger name.
     * @return A <code>Logger</code> instance, or <code>null</code>
     * if no logger with the given name exists.
     */
    public LogAppender getLogger( String name ) {

        Map<String, Manageable> manageableBeans = this.managableBeans;
        if ( manageableBeans == null ) {
            return null;
        }
        Manageable m = manageableBeans.get( name );
        if ( m instanceof LogAppender ) {
            return (LogAppender) m;
        }
        return null;
    }

    /**
     * Renames a logger.
     * @param oldName The old logger name.
     * @param newName The new logger name.
     * @return The renamed logger. Never <code>null</code>.
     * @throws NexusException if <code>oldName</code> was not found or <code>newName</code>
     * already exists.
     */
    public LogAppender renameLogger( String oldName, String newName ) throws NexusException {

        if ( newName == null || newName.trim().length() == 0 ) {
            throw new NexusException( "Logger name must not be empty" );
        }
        Map<String, Manageable> manageableBeans = this.managableBeans;
        if ( manageableBeans == null ) {
            throw new NexusException( "Logger with name '" + oldName + "' not found" );
        }
        if ( manageableBeans.get( newName ) != null ) {
            throw new NexusException( "Cannot rename logger: Target name '" + newName + "' already exists" );
        }
        Manageable m = manageableBeans.get( oldName );
        LogAppender l = null;
        if ( m instanceof LogAppender ) {
            l = (LogAppender) m;
        } else {
            throw new NexusException( "Logger with name '" + oldName + "' not found" );
        }
        manageableBeans.remove( oldName );
        manageableBeans.put( newName, l );
        return l;
    }

    /**
     * Gets a service by it's unique name.
     * @param name the service name.
     * @return A <code>Service</code> instance, or <code>null</code>
     * if no service with the given name exists.
     */
    public Service getService( String name ) {

        Map<String, Manageable> manageableBeans = this.managableBeans;
        if ( manageableBeans == null ) {
            return null;
        }
        Manageable m = manageableBeans.get( name );
        if ( m instanceof Service ) {
            return (Service) m;
        }
        return null;
    }

    /**
     * Renames a service.
     * @param oldName The old service name.
     * @param newName The new service name.
     * @return The renamed service. Never <code>null</code>.
     * @throws NexusException if <code>oldName</code> was not found or <code>newName</code>
     * already exists.
     */
    public Service renameService( String oldName, String newName ) throws NexusException {

        if ( newName == null || newName.trim().length() == 0 ) {
            throw new NexusException( "Service name must not be empty" );
        }
        Map<String, Manageable> manageableBeans = this.managableBeans;
        if ( manageableBeans == null ) {
            throw new NexusException( "Service with name '" + oldName + "' not found" );
        }
        if ( manageableBeans.get( newName ) != null ) {
            throw new NexusException( "Cannot rename service: Target name '" + newName + "' already exists" );
        }
        Manageable m = manageableBeans.get( oldName );
        Service s = null;
        if ( m instanceof Service ) {
            s = (Service) m;
        } else {
            throw new NexusException( "Service with name '" + oldName + "' not found" );
        }
        manageableBeans.remove( oldName );
        manageableBeans.put( newName, s );
        return s;
    }

    /**
     * Gets a list of all services that are registered.
     * @return A copied list of all services. Can be empty, but not <code>null</code>.
     */
    @SuppressWarnings("unchecked")
    public List<Service> getServices() {

        Map<String, Manageable> manageableBeans = this.managableBeans;
        if ( manageableBeans == null ) {
            return Collections.EMPTY_LIST;
        }
        List<Service> result = new ArrayList<Service>();
        for ( Manageable m : manageableBeans.values() ) {
            if ( m instanceof Service ) {
                result.add( (Service) m );
            }
        }
        return result;
    }
}
