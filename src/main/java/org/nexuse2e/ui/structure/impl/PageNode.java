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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.nexuse2e.ui.structure.ParentalStructureNode;
import org.nexuse2e.ui.structure.StructureNode;

/**
 * Interface for a page node.
 * @author Sebastian Schulze
 * @date 29.11.2006
 */
public class PageNode extends AbstractStructureNode implements ParentalStructureNode {

    protected List<StructureNode>     children           = null;
    private boolean                   hasDynamicChildren = false;
    protected HashMap<String, String> properties;

    /**
     * Constructor.
     * @param target Target URL.
     * @param label Label of the node.
     * @param icon URL of the nodes icon.
     */
    public PageNode( String target, String label, String icon ) {

        super( target, label, icon );
        children = new ArrayList<StructureNode>();
        properties = new HashMap<String, String>();
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.ui.structure.impl.ParentalStructureNode#addChild(org.nexuse2e.ui.structure.StructureNode)
     */
    public void addChild( StructureNode child ) {

        child.setParentNode( this );
        children.add( child );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.ui.structure.impl.ParentalStructureNode#addChildren(java.util.List)
     */
    public void addChildren( List<StructureNode> children ) {

        for ( StructureNode child : children ) {
            child.setParentNode( this );
        }
        this.children.addAll( children );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.ui.structure.impl.ParentalStructureNode#getChildren()
     */
    public List<StructureNode> getChildren() {

        return children;

    }

    /* (non-Javadoc)
     * @see org.nexuse2e.ui.structure.impl.ParentalStructureNode#hasChildren()
     */
    public boolean hasChildren() {

        return children.size() > 0;

    }

    /* (non-Javadoc)
     * @see org.nexuse2e.ui.structure.impl.ParentalStructureNode#setDynamicChildren(boolean)
     */
    public void setDynamicChildren( boolean hasDynamicChildren ) {

        this.hasDynamicChildren = hasDynamicChildren;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.ui.structure.impl.ParentalStructureNode#hasDynamicChildren()
     */
    public boolean hasDynamicChildren() {

        return hasDynamicChildren;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.ui.structure.impl.ParentalStructureNode#hasProperty(java.lang.String)
     */
    public boolean hasProperty( String name ) {

        boolean result = properties.containsKey( name );
        if ( !result && parent != null ) {
            result = parent.hasProperty( name );
        }
        return result;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.ui.structure.impl.ParentalStructureNode#setProperty(java.lang.String, java.lang.String)
     */
    public void setProperty( String name, String value ) {

        properties.put( name, value );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.ui.structure.impl.ParentalStructureNode#getProperty(java.lang.String)
     */
    public String getProperty( String name ) {

        String result = properties.get( name );
        if ( result == null && parent != null ) {
            result = parent.getProperty( name );
        }
        return result;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.ui.structure.impl.AbstractStructureNode#toString()
     */
    @Override
    public String toString() {

        return toString( this, 0 );
    }

    private String getStringRepresentation() {

        return super.toString();
    }

    /**
     * Used to provide correct indention (tree view) the to toString() method.
     * @see org.nexuse2e.ui.structure.impl.AbstractStructureNode#toString()
     */
    protected String toString( StructureNode node, int indent ) {

        StringBuffer sb = new StringBuffer();
        for ( int i = 0; i < indent; i++ ) {
            sb.append( "  " );
        }
        sb.append( node.toString() );
        sb.append( "\n" );

        return sb.toString();
    }

    /**
     * Used to provide correct indention (tree view) the to toString() method.
     * @see org.nexuse2e.ui.structure.impl.AbstractStructureNode#toString()
     */
    protected String toString( PageNode node, int indent ) {

        StringBuffer sb = new StringBuffer();
        for ( int i = 0; i < indent; i++ ) {
            sb.append( "  " );
        }
        sb.append( node.getStringRepresentation() );
        sb.append( "\n" );
        List<StructureNode> children = ( (ParentalStructureNode) node ).getChildren();
        for ( StructureNode child : children ) {
            if ( child instanceof PageNode ) {
                sb.append( toString( (PageNode) child, indent + 1 ) );
            } else {
                sb.append( toString( child, indent + 1 ) );
            }
        }

        return sb.toString();
    }

}
