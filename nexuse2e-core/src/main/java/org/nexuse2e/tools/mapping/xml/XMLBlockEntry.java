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
package org.nexuse2e.tools.mapping.xml;

import org.nexuse2e.tools.mapping.csv.RecordEntry.Align;
import org.nexuse2e.tools.mapping.csv.RecordEntry.Trim;

/**
 * @author guido.esch
 */
public class XMLBlockEntry {

    private String  entryID;
    private String  position;
    private String  nodePath;
    private String  node;
    private boolean attribute = false;
    private boolean textNode  = false;
    private String  attributeName;
    private String  textNodeName;
    private int     length;
    private String  method;
    private String  filler;
    private Trim    trim      = Trim.FALSE;
    private Align   align     = Align.LEFT;

    /**
     * @return id
     */
    public String getEntryID() {

        return entryID;
    }

    /**
     * @param entryID
     */
    public void setEntryID( String entryID ) {

        this.entryID = entryID;
    }

    /**
     * @return node
     */
    public String getNode() {

        return node;
    }

    /**
     * @param node
     */
    public void setNode( String node ) {

        if ( node.toLowerCase().endsWith( "text()" ) ) {
            textNode = true;
            nodePath = node.substring( 0, node.length() - 7 );
        } else if ( node.indexOf( '@' ) > -1 ) {
            attribute = true;
            attributeName = node.substring( node.indexOf( '@' ) + 1, node.length() );
            nodePath = node.substring( 0, node.lastIndexOf( '/' ) );
        }

        this.node = node;
    }

    /**
     * @return position
     */
    public String getPosition() {

        return position;
    }

    /**
     * @param position
     */
    public void setPosition( String position ) {

        this.position = position;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {

        StringBuffer buffer = new StringBuffer();
        buffer.append( "Entry:" ); //$NON-NLS-1$
        buffer.append( entryID );
        buffer.append( " Position:" ); //$NON-NLS-1$
        buffer.append( position );
        buffer.append( " Node:" ); //$NON-NLS-1$
        buffer.append( node );
        return buffer.toString();
    }

    public boolean isAttribute() {

        return attribute;
    }

    public void setAttribute( boolean attribute ) {

        this.attribute = attribute;
    }

    public boolean isTextNode() {

        return textNode;
    }

    public void setTextNode( boolean textNode ) {

        this.textNode = textNode;
    }

    public String getAttributeName() {

        return attributeName;
    }

    public void setAttributeName( String attributeName ) {

        this.attributeName = attributeName;
    }

    public String getNodePath() {

        return nodePath;
    }

    public void setNodePath( String nodePath ) {

        this.nodePath = nodePath;
    }

    public String getTextNodeName() {

        return textNodeName;
    }

    public void setTextNodeName( String textNodeName ) {

        this.textNodeName = textNodeName;
    }

    
    public int getLength() {
    
        return length;
    }

    
    public void setLength( int length ) {
    
        this.length = length;
    }

    
    public String getMethod() {
    
        return method;
    }

    
    public void setMethod( String method ) {
    
        this.method = method;
    }

    
    public String getFiller() {
    
        return filler;
    }

    
    public void setFiller( String filler ) {
    
        this.filler = filler;
    }

    
    public Trim getTrim() {
    
        return trim;
    }

    
    public void setTrim( Trim trim ) {
    
        this.trim = trim;
    }

    
    public Align getAlign() {
    
        return align;
    }

    
    public void setAlign( Align align ) {
    
        this.align = align;
    }

}