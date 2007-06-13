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
package org.nexuse2e.ui;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Wrapper class to represent an entry in the navigator tree of the Web UI
 *
 * @author markus.breilmann
 */
public class NavigatorItem {

    private String        name        = null;
    private String        link        = null;
    private String        image       = null;
    private String        label       = null;
    private NavigatorItem parent      = null;
    private String        onClick     = null;
    private ArrayList     children    = new ArrayList();
    private boolean       inError     = false;
    private boolean       shownInTree = true;
    private String        id          = null;

    public NavigatorItem( NavigatorItem parent, String name, String link, String image, String label ) {

        this.id = name;
        if ( parent != null ) {
            this.name = parent.getName() + "_" + name;
        } else {
            this.name = name;
        }
        this.link = link;
        this.image = image;
        this.label = label;
        this.parent = parent;
    }

    public void addChild( NavigatorItem child ) {

        children.add( child );
    } // addChild

    @Override
    public String toString() {

        StringBuffer buffer = new StringBuffer();
        if ( isShownInTree() ) {
            if ( isFolder() ) {
                buffer.append( name + " = " );
                if ( parent != null ) {
                    buffer.append( "insFld( " + parent.getName() + ", " );
                }
                if ( isInError() ) {
                    buffer.append( "gFldErr( \"" + label + "\", \"" );
                } else {
                    buffer.append( "gFld( \"" + label + "\", \"" );
                }
                buffer.append( link + "\", \"" );
                buffer.append( image + "\", \"" );
                buffer.append( image + "\", \"" );
                buffer.append( name + "\" )" );

                if ( parent != null ) {
                    buffer.append( " )" );
                }
            } else {
                if ( parent != null ) {
                    buffer.append( "insDoc( " + parent.getName() + ", " );
                }
                buffer.append( "gLnk( 0, \"" );
                buffer.append( label + "\", \"" );
                buffer.append( link + "\", \"" );
                buffer.append( image + "\" )" );

                if ( parent != null ) {
                    buffer.append( " )" );
                }
            }
            buffer.append( "\n" );

            for ( Iterator iter = children.iterator(); iter.hasNext(); ) {
                NavigatorItem item = (NavigatorItem) iter.next();

                buffer.append( item.toString() );
            }
        }
        return buffer.toString();
    } // toString

    /**
     * @return Returns the folder.
     */
    public boolean isFolder() {

        if ( children.isEmpty() ) {
            return false;
        }
        Iterator i = children.iterator();
        while ( i.hasNext() ) {
            NavigatorItem temp = (NavigatorItem) i.next();
            if ( temp.isShownInTree() ) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return Returns the image.
     */
    public String getImage() {

        return image;
    }

    /**
     * @param image The image to set.
     */
    public void setImage( String image ) {

        this.image = image;
    }

    /**
     * @return Returns the label.
     */
    public String getLabel() {

        return label;
    }

    /**
     * @param label The label to set.
     */
    public void setLabel( String label ) {

        this.label = label;
    }

    /**
     * @return Returns the link.
     */
    public String getLink() {

        return link;
    }

    /**
     * @param link The link to set.
     */
    public void setLink( String link ) {

        this.link = link;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {

        return name;
    }

    /**
     * @param name The name to set.
     */
    public void setName( String name ) {

        this.name = name;
    }

    /**
     * @return Returns the inError.
     */
    public boolean isInError() {

        return inError;
    }

    /**
     * @param inError The inError to set.
     */
    public void setInError( boolean inError ) {

        this.inError = inError;
    }

    public ArrayList getChildren() {

        return children;
    }

    public void setChildren( ArrayList children ) {

        this.children = children;
    }

    public String getOnClick() {

        return onClick;
    }

    public void setOnClick( String onClick ) {

        this.onClick = onClick;
    }

    public boolean isShownInTree() {

        return shownInTree;
    }

    public void setShownInTree( boolean shownInTree ) {

        this.shownInTree = shownInTree;
    }

    public String getId() {

        return id;
    }

    public void setId( String id ) {

        this.id = id;
    }

    public NavigatorItem getParent() {

        return parent;
    }

    public void setParent( NavigatorItem parent ) {

        this.parent = parent;
    }
} // NavigatorItem
