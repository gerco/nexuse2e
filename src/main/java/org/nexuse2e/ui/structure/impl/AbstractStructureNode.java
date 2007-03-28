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

import org.apache.commons.lang.text.StrLookup;
import org.apache.commons.lang.text.StrSubstitutor;
import org.nexuse2e.ui.structure.ParentalStructureNode;
import org.nexuse2e.ui.structure.StructureNode;

/**
 * Implements the StrucureNode interface for the "page" type.
 * This classes implementations of the getter methods of the interface {@link StructureNode}
 * do variable substitution: If one of the values (<code>target</code>, <code>label</code> etc.)
 * contain an expression like <code>${myVariable}</code>, the implementation will call the <code>parent</code>'s
 * {@link ParentalStructureNode.getProperty(String)} to get the value of <code>myVariable</code> and substitutes
 * the variable expression with the returned value.
 * @see org.apache.commons.lang.text.StrSubstitutor
 * @author Sebastian Schulze
 * @date 29.11.2006
 */
public abstract class AbstractStructureNode implements StructureNode {

    protected String                target;
    protected String                label;
    protected String                icon;
    protected ParentalStructureNode parent;
    protected StrSubstitutor        substitutor;

    public AbstractStructureNode( String target, String label, String icon ) {

        this.target = target;
        this.label = label;
        this.icon = icon;
        this.parent = null;
        substitutor = new StrSubstitutor( new StrLookup() {

            /* (non-Javadoc)
             * @see org.apache.commons.lang.text.StrLookup#lookup(java.lang.String)
             */
            @Override
            public String lookup( String key ) {

                if ( parent != null ) {
                    return parent.getProperty( key );
                } else {
                    throw new NullPointerException( "No parent node set for " + this.toString()
                            + ". Variable substitution cannot be applied for variable (" + key + ")!" );
                }
            }

        } );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.ui.structure.StructureNode#getIcon()
     */
    public String getIcon() {

        return substituteVariables( icon );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.ui.structure.StructureNode#getLabel()
     */
    public String getLabel() {

        return substituteVariables( label );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.ui.structure.StructureNode#getTarget()
     */
    public String getTarget() {

        return substituteVariables( target );
    }

    /**
     * Replaces variables in the given string.
     * @param s The string containing the variables.
     * @return The string with substituted variables.
     * @see org.apache.commons.lang.text.StrSubstitutor
     */
    private String substituteVariables( String s ) {

        return substitutor.replace( s );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.ui.structure.StructureNode#getParentNode(org.nexuse2e.ui.structure.ParentalStructureNode)
     */
    public void setParentNode( ParentalStructureNode parent ) {

        this.parent = parent;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.ui.structure.StructureNode#getParentNode()
     */
    public ParentalStructureNode getParentNode() {

        return parent;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {

        return super.toString() + "[target=" + target + ",label=" + label + ",icon=" + icon + "]";
    }
}
