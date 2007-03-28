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
package org.nexuse2e.ui.structure;

import java.util.Map;

/**
 * Defines the interface of a service that manages TargetProvider instances.
 * @author Sebastian Schulze
 * @date 11.12.2006
 */
public interface TargetProviderManager {

    /**
     * Returns the TargetProvider instance assigned to the given <code>targetId</code>
     * @param targetId The id of the desired TargetProvider.
     * @return The desired TargetProvider instance or <code>null</code>, if no such
     *         instance is assiged with the given <code>targetId</code>. 
     */
    TargetProvider getTargetProvider( String targetId );

    /**
     * Adds a new or replaces an existing target provider.
     * @param targetId The id of the TargetProvider instance to add/replace.
     * @param targetProvider The instance to add/replace.
     */
    void addTargetProvider( String targetId, TargetProvider targetProvider );

    /**
     * Removes a TargetProvider instance.
     * @param targetId The id of the TargetProvider instance to remove.
     */
    void removeTargetProvider( String targetId );

    /**
     * Sets (replaces all prior added) a bunch of TargetProvider instances.
     * @param targetProviders A Map containing ids as keys and TargetProvider instances as values.
     */
    void setTargetProviders( Map<String, TargetProvider> targetProviders );
}
