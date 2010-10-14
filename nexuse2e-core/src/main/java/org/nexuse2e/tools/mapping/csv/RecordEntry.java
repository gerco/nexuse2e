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
package org.nexuse2e.tools.mapping.csv;

/**
 * @author guido.esch
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class RecordEntry {

    public static enum Align {
        LEFT, RIGHT;
    }

    public static enum Trim {
        WHITESPACES, TRUE, FALSE;
    }

    private String entryID;
    private String sourceID;
    private int    type;
    private int    position;
    private int    length;
    private String method;
    private String filler;
    private Trim   trim  = Trim.FALSE;
    private Align  align = Align.LEFT;

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
     * @return length
     */
    public int getLength() {

        return length;
    }

    /**
     * @param length
     */
    public void setLength( int length ) {

        this.length = length;
    }

    /**
     * @return position
     */
    public int getPosition() {

        return position;
    }

    /**
     * @param position
     */
    public void setPosition( int position ) {

        this.position = position;
    }

    /**
     * @return sourceid
     */
    public String getSourceID() {

        return sourceID;
    }

    /**
     * @param sourceID
     */
    public void setSourceID( String sourceID ) {

        this.sourceID = sourceID;
    }

    /**
     * @return type
     */
    public int getType() {

        return type;
    }

    /**
     * @param type
     */
    public void setType( int type ) {

        this.type = type;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {

        StringBuffer buffer = new StringBuffer();
        buffer.append( "Entry:" ); //$NON-NLS-1$
        buffer.append( entryID );
        buffer.append( " SourceID:" ); //$NON-NLS-1$
        buffer.append( sourceID );
        buffer.append( " Type:" ); //$NON-NLS-1$
        buffer.append( type );
        buffer.append( " Position:" ); //$NON-NLS-1$
        buffer.append( position );
        buffer.append( " Length:" ); //$NON-NLS-1$
        buffer.append( length );

        return buffer.toString();
    }

    /**
     * @return the align
     */
    public Align getAlign() {

        return align;
    }

    /**
     * @param align the align to set
     */
    public void setAlign( Align align ) {

        this.align = align;
    }

    /**
     * @return the filler
     */
    public String getFiller() {

        return filler;
    }

    /**
     * @param filler the filler to set
     */
    public void setFiller( String filler ) {

        this.filler = filler;
    }

    /**
     * @return the method
     */
    public String getMethod() {

        return method;
    }

    /**
     * @param method the method to set
     */
    public void setMethod( String method ) {

        this.method = method;
    }

    /**
     * @return the trim
     */
    public Trim getTrim() {

        return trim;
    }

    /**
     * @param trim the trim to set
     */
    public void setTrim( Trim trim ) {

        this.trim = trim;
    }
}