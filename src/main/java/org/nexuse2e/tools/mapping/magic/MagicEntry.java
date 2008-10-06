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
package org.nexuse2e.tools.mapping.magic;


/**
 * @author guido.esch
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MagicEntry {

    private String  xPathId;
    private String  value;
    private boolean fileSource   = false;
    private boolean staticSource = false;

    /**
     * @return value
     */
    public String getValue() {

        return value;
    }

    /**
     * @param value
     */
    public void setValue( String value ) {

        if ( value.toLowerCase().startsWith( "file" ) ) {
            fileSource = true;
            this.value = value.substring( 5 );
        } else if ( value.toLowerCase().startsWith( "static" ) ) {
            staticSource = true;
            this.value = value.substring( 7 );
        } else {
            this.value = value;
        }
    }

    /**
     * @return xpathid
     */
    public String getXPathId() {

        return xPathId;
    }

    /**
     * @param xpathid
     */
    public void setXPathId( String xpathid ) {

        this.xPathId = xpathid;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {

        StringBuffer buffer = new StringBuffer();

        buffer.append( "XPathID:" ); //$NON-NLS-1$
        buffer.append( xPathId );
        buffer.append( " Value:" ); //$NON-NLS-1$
        buffer.append( value );

        return buffer.toString();
    }

    public boolean isFileSource() {

        return fileSource;
    }

    public void setFileSource( boolean fileSource ) {

        this.fileSource = fileSource;
    }

    public boolean isStaticSource() {

        return staticSource;
    }

    public void setStaticSource( boolean staticSource ) {

        this.staticSource = staticSource;
    }
}