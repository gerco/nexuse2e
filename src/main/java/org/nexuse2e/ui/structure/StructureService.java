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

import java.util.List;

/**
 * Interface for the service that builds and maintains the page topology information.
 * @author Sebastian Schulze
 * @date 29.11.2006
 */
public interface StructureService {

    /**
     * Returns the topology of the page's menu.
     * @return A list containing the hierarchic structure of the menu.
     * @throws StructureException 
     */
    List<StructureNode> getMenuStructure() throws StructureException;

    /**
     * Returns the topology of the page (all nodes not included in the menu).
     * @return A list containing the hierarchic structure of the page.
     * @throws StructureException 
     */
    List<StructureNode> getSiteStructure() throws StructureException;
    
    /**
     * Returns the exact definition of the page's menu without evaluating
     * dynamic nodes (<code>type="provider"</code>), but with pattern nodes instead.
     * @return A list containing the skeleton of the hierarchic structure
     *          definition of the menu.
     * @throws StructureException 
     */
    List<StructureNode> getMenuSkeleton() throws StructureException;
    
    /**
     * Returns the exact definition of the page (all nodes not included in
     * the menu) without evaluating dynamic nodes (<code>type="provider"</code>),
     * but with pattern nodes instead.
     * @return A list containing the skeleton of the hierarchic structure
     *          definition of the page.
     * @throws StructureException 
     */
    List<StructureNode> getSiteSkeleton() throws StructureException;
}
