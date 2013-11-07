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
package org.nexuse2e.service;

import java.util.HashMap;
import java.util.Map;

import org.nexuse2e.Layer;
import org.nexuse2e.configuration.ParameterDescriptor;


/**
 * This is an in-memory implementation of the PropertyService interface.
 * An instance can hold {@link String} properties until it gets garbage
 * collected.
 * 
 * @author Sebastian Schulze
 * @date 27.08.2008
 */
public class TransientPropertyService extends AbstractService implements PropertyService<String> {

    private Map<String,Map<String,Map<String,String>>> storage;
    
    public TransientPropertyService() {
        
        storage = new HashMap<String,Map<String,Map<String,String>>>();
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.service.AbstractService#fillParameterMap(java.util.Map)
     */
    @Override
    public void fillParameterMap( Map<String, ParameterDescriptor> parameterMap ) {
        
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.service.AbstractService#getActivationLayer()
     */
    @Override
    public Layer getActivationLayer() {

        return Layer.CORE;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.service.PropertyService#read(java.lang.String, java.lang.String, java.lang.String)
     */
    public String read( String namespace,
                        String namespaceVersion,
                        String propertyName ) throws Exception {
        return access( namespace, namespaceVersion, propertyName, false );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.service.PropertyService#store(java.lang.String, java.lang.String, java.lang.String, java.lang.Object)
     */
    public void store( String namespace,
                       String namespaceVersion,
                       String propertyName,
                       String value ) throws Exception {
        
        Map<String,Map<String,String>> componentDomain = storage.get( namespace );
        if ( componentDomain == null ) {
            componentDomain = new HashMap<String,Map<String,String>>();
            storage.put( namespace, componentDomain );
        }
        Map<String,String> versionDomain = componentDomain.get( namespaceVersion );
        if ( versionDomain == null ) {
            versionDomain = new HashMap<String,String>();
            componentDomain.put( namespaceVersion, versionDomain );
        }
        versionDomain.put( propertyName, value );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.service.PropertyService#remove(java.lang.String, java.lang.String, java.lang.String)
     */
    public String remove( String namespace,
                        String namespaceVersion,
                        String propertyName ) throws Exception {
        return access( namespace, namespaceVersion, propertyName, true );
    }
    
    /**
     * Internal handling.
     * @param namespace
     * @param namespaceVersion
     * @param propertyName
     * @param remove Whether the value should just be looked up, or removed from storage.
     * @return
     */
    private String access( String namespace, String namespaceVersion, String propertyName, boolean remove ) {
        String result = null;
        
        Map<String,Map<String,String>> componentDomain = storage.get( namespace );
        if ( componentDomain != null ) {
            Map<String,String> versionDomain = componentDomain.get( namespaceVersion );
            if ( versionDomain != null ) {
                if ( remove ) {
                    result = versionDomain.remove( propertyName );
                } else {
                    result = versionDomain.get( propertyName );
                }
            }            
        }
        
        return result;
    }
}
