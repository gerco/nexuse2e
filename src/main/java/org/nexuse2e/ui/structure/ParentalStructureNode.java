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

public interface ParentalStructureNode extends StructureNode {

    /**
     * Adds a child node to the current instance.
     * This method automatically calls {@link StructureNode.setParent(ParentalStructureNode)}
     * of the <code>child</code>.
     * @param child The child to add.
     */
    public abstract void addChild( StructureNode child );

    /**
     * Adds a list of child nodes to the current instance.
     * This method automatically calls {@link StructureNode.setParent(ParentalStructureNode)}
     * of each <code>child</code>.
     * @param children List of children to add.
     */
    public abstract void addChildren( List<StructureNode> children );
    
    /**
     * Removes all children from this <code>ParentalStructureNode</code>.
     */
    public abstract void removeChildren();

    /**
     * Returns the child nodes of the current instance.
     * @return List of child nodes.
     */
    public abstract List<StructureNode> getChildren();

    /**
     * Returns the current instance has children.
     * @return <code>true</code> if the current instance has at least one
     *         child node; <code>false</code> otherwise.
     */
    public abstract boolean hasChildren();

    /**
     * Returns <code>true</code>, if this node has dynamic child nodes
     * (the list of child nodes may vary by time).
     * @return
     */
    public abstract void setDynamicChildren( boolean hasDynamicChildren );

    /**
     * Returns <code>true</code>, if this node has dynamic child nodes.
     * @return <code>true</code> if the list of child nodes may vary by time.
     *          <code>false</code> if the list of children is static. 
     */
    public abstract boolean hasDynamicChildren();

    /**
     * Checks whether this node contains a property with the given <code>name</code>.
     * If the current node's properties does not contain the given <code>name</code>
     * the method <code>hasProperty(String)</code> of the parent node will be called.
     * This results in a recursive call up to the root node.
     * @param name Name of the property.
     * @return <code>true</code> if a property with the given <code>name</code> exists.
     *          <code>false</code> if the property is unknown. 
     */
    public abstract boolean hasProperty( String name );

    /**
     * Sets the provider key.
     * @param provider The provider key.
     */
    public abstract void setProvider( String provider );
    
    /**
     * Gets the provider key.
     * @return The provider key, or <code>null</code> if none is set
     * (i.e., if the node is not dynamic).
     */
    public abstract String getProvider();
    
    /**
     * Sets the target for this node's children.
     * @param childTarget The child target.
     */
    public abstract void setChildTarget( String childTarget );
    
    /**
     * Gets target for this node's children.
     * @return The child target.
     */
    public abstract String getChildTarget();
    
    /**
     * Sets the icon for children.
     * @param childIcon The icon for this node's children.
     */
    public abstract void setChildIcon( String childIcon );

    /**
     * Gets the icon for children.
     * @return The children's icon.
     */
    public abstract String getChildIcon();

    /**
     * Sets the label for children.
     * @param childLabel The label for this node's children.
     */
    public abstract void setChildLabel( String childLabel );

    /**
     * Gets the label for children.
     * @return The children's label.
     */
    public abstract String getChildLabel();

    /**
     * Sets a property.
     * @param name Property name.
     * @param value Property value.
     */
    public abstract void setProperty( String name, String value );

    /**
     * Returns the value of a property.
     * If the current node's properties does not contain the given <code>name</code>
     * the method <code>getProperty(String)</code> of the parent node will be called.
     * This results in a recursive call up to the root node.
     * @param name Property name.
     * @return Value of the property if it exists.
     *          <code>null</code> if the <code>name</code> is unknown.
     */
    public abstract String getProperty( String name );
}