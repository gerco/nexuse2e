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
package org.nexuse2e.tools.mapping.xmldata;

/**
 * @author mbreilmann
 *
 */
public class MappingDefinition {

    String  xpath        = null;
    String  category     = null;
    String  command      = null;
    String  fillerSource = null;
    boolean alignSource  = false;
    boolean trimSource   = false;
    int     lengthSource = 0;
    String  fillerTarget = null;
    boolean alignTarget  = false;
    boolean trimTarget   = false;
    int     lengthTarget = 0;
    
    int		mappingServiceId = 0;

    public String getXpath() {

        return xpath;
    }

    public void setXpath( String xpathValue ) {

        this.xpath = xpathValue;
    }

    public boolean isAlignSource() {

        return alignSource;
    }

    public void setAlignSource( boolean alignSource ) {

        this.alignSource = alignSource;
    }

    public boolean isAlignTarget() {

        return alignTarget;
    }

    public void setAlignTarget( boolean alignTarget ) {

        this.alignTarget = alignTarget;
    }

    public String getFillerSource() {

        return fillerSource;
    }

    public void setFillerSource( String fillerSource ) {

        this.fillerSource = fillerSource;
    }

    public String getFillerTarget() {

        return fillerTarget;
    }

    public void setFillerTarget( String fillerTarget ) {

        this.fillerTarget = fillerTarget;
    }

    public int getLengthSource() {

        return lengthSource;
    }

    public void setLengthSource( int lengthSource ) {

        this.lengthSource = lengthSource;
    }

    public int getLengthTarget() {

        return lengthTarget;
    }

    public void setLengthTarget( int lengthTarget ) {

        this.lengthTarget = lengthTarget;
    }

    public boolean isTrimSource() {

        return trimSource;
    }

    public void setTrimSource( boolean trimSource ) {

        this.trimSource = trimSource;
    }

    public boolean isTrimTarget() {

        return trimTarget;
    }

    public void setTrimTarget( boolean trimTarget ) {

        this.trimTarget = trimTarget;
    }

    public String getCommand() {

        return command;
    }

    public void setCommand( String value ) {

        this.command = value;
    }

    
    /**
     * @return the category
     */
    public String getCategory() {
    
        return category;
    }

    
    /**
     * @param category the category to set
     */
    public void setCategory( String category ) {
    
        this.category = category;
    }

	public int getMappingServiceId() {
		return mappingServiceId;
	}

	public void setMappingServiceId(int mappingServiceId) {
		this.mappingServiceId = mappingServiceId;
	}

}
