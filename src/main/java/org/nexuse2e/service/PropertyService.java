/**
 * NEXUSe2e Business Messaging Open Source  
 * Copyright 2008, X-ioma GmbH   
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


/**
 * This interface describes a generic service that enables
 * clients to store internal data on runtime.
 * 
 * Properties are stored in different namespaces.
 * Each namespace can have several versions that are separated.
 * A property is identified by a distinct name that is unique
 * for each namespace and version.
 * 
 * One usual implementation would be a service component that
 * can store/read key-value pairs in a persistent database. 
 * 
 * @author Sebastian Schulze
 * @date 27.08.2008
 */
public interface PropertyService<T> extends Service {
    
    /**
     * Stores a property for a component.
     * @param namespace The namespace.
     * @param namespaceVersion The namespace version.
     * @param propertyName The name of the property to store.
     * @param value The value to store.
     * @throws Exception, if an error occurs.
     */
    void store( String namespace, String namespaceVersion,
                String propertyName, T value ) throws Exception ;
    /**
     * Reads a property for a component.
     * @param namespace The namespace.
     * @param namespaceVersion The namespace version.
     * @param propertyName The name of the property to store.
     * @return The stored value for the given parameters
     *         or <code>null</code>, if none was found.
     * @throws Exception, if an error occurs.
     */
    T read( String namespace, String namespaceVersion,
            String propertyName ) throws Exception;
    
    /**
     * Removes a property for a component.
     * @param namespace The namespace.
     * @param namespaceVersion The namespace version.
     * @param propertyName The name of the property to store.
     * @return The stored value for the given parameters
     *         or <code>null</code>, if none was found. 
     * @throws Exception, if an error occurs.
     */
    T remove( String namespace, String namespaceVersion,
                 String propertyName ) throws Exception;
    
}
