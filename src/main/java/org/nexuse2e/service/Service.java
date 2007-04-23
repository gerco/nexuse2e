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
package org.nexuse2e.service;

import org.nexuse2e.Configurable;
import org.nexuse2e.Manageable;

/**
 * Generic interface that allows any kind of additional functionality
 * to be executed and managed by the Nexus Server Engine.
 * In addition to the life cycle defined in the <code>Manageable</code>
 * interface, <code>start()</code> and <code>stop()</code> methods are
 * provided by <code>Service</code> in order to start/stop background
 * processing.
 *
 * @author jonas.reese
 */
public interface Service extends Manageable, Configurable {

    /**
     * Starts the <code>Service</code>. It is up to the service
     * implementation if a background process is stared here. However,
     * if it does, it should stop processing ASAP when the {@link #stop()}
     * method is called.
     */
    public void start();

    /**
     * Stops any background processing that may have been started on
     * a call to the {@link #start()} method.
     */
    public void stop();

    /**
     * Gets the <code>autostart</code> flag.
     * @return <code>true</code> if the service shall be automatically started.
     */
    public boolean isAutostart();

    /**
     * Sets the <code>autostart</code> flag.
     * @param autostart Flag whether this service needs to be started automatically at startup
     */
    public void setAutostart( boolean autostart );
}
