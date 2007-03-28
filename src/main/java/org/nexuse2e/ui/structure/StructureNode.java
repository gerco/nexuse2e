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

/**
 * Super interface for all notes in the topology of the ui.
 * @author Sebastian Schulze
 * @date 29.11.2006
 */
public interface StructureNode {

    /**
     * Returns the label of the node.
     * @return Label string of the node.
     */
    String getLabel();

    /**
     * Returns the target of the node.
     * @return Target string of the node.
     */
    String getTarget();

    /**
     * Returns the icon of the node.
     * @return Icon path string of the node.
     */
    String getIcon();

    /**
     * Sets the parent for this node.
     * @param parent The parent for this node.
     */
    void setParentNode( ParentalStructureNode parent );

    /**
     * Returns the parent node of this node.
     * @return This node's parent node or <code>null</code> if this node has no parent.
     */
    StructureNode getParentNode();
}
