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

import org.nexuse2e.service.AbstractControllerService;

public interface EngineControllerStub {

    /**
     * Return a controller wrapper for the specified controller ID.
     * @param controllerId The ID of the controller to wrap.
     * @return The wrapper for the specified controller.
     */
    public AbstractControllerService getControllerWrapper( String controllerId, AbstractControllerService controller );

    /**
     * is called before using the underlying stub implementation
     */
    public void initialize();
    
    public String getMachineId();
    
} // EngineControllerStub
