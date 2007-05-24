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
package org.nexuse2e.tools.mapping.xml;

/**
 * @author guido.esch
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class XMLBlockEntry {

    private String entryID;
    private String position;
    private String node;
    
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
   
}