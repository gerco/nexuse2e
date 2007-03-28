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
package org.nexuse2e.ui.structure.impl;

import java.util.HashMap;
import java.util.Map;

import org.nexuse2e.ui.structure.TargetProvider;
import org.nexuse2e.ui.structure.TargetProviderManager;

/**
 * Implements the storage of TargetProvider instances.
 * @author Sebastian Schulze
 * @date 11.12.2006
 */
public class DefaultTargetProviderManager implements TargetProviderManager {

    protected Map<String, TargetProvider> tpMap;

    public DefaultTargetProviderManager() {

        tpMap = new HashMap<String, TargetProvider>();
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.ui.structure.TargetProviderManager#addTargetProvider(java.lang.String, org.nexuse2e.ui.structure.TargetProvider)
     */
    public void addTargetProvider( String targetId, TargetProvider targetProvider ) {

        tpMap.put( targetId, targetProvider );

    }

    /* (non-Javadoc)
     * @see org.nexuse2e.ui.structure.TargetProviderManager#addTargetProviders(java.util.Map)
     */
    public void setTargetProviders( Map<String, TargetProvider> targetProviders ) {

        tpMap.putAll( targetProviders );

    }

    /* (non-Javadoc)
     * @see org.nexuse2e.ui.structure.TargetProviderManager#getTargetProvider(java.lang.String)
     */
    public TargetProvider getTargetProvider( String targetId ) {

        return tpMap.get( targetId );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.ui.structure.TargetProviderManager#removeTargetProvider(java.lang.String)
     */
    public void removeTargetProvider( String targetId ) {

        tpMap.remove( targetId );

    }

}
