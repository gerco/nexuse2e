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
package org.nexuse2e.tools.mapping;

/**
 * @author guido.esch
 */
public class CSVMappingFileEntry {

    private String mapping;
    private String xmlblocks;
    private String csvmappings;
    private String id;

    private boolean mappingAvailable;
    private boolean xmlblocksAvailable;
    private boolean csvmappingsAvailable;
    
    /**
     * @return csvMappings
     */
    public String getCsvmappings() {

        return csvmappings;
    }

    /**
     * @param csvmappings
     */
    public void setCsvmappings( String csvmappings ) {

        this.csvmappings = csvmappings;
    }

    /**
     * @return mapping
     */
    public String getMapping() {

        return mapping;
    }

    /**
     * @param mapping
     */
    public void setMapping( String mapping ) {

        this.mapping = mapping;
    }

    /**
     * @return xmlBlocks
     */
    public String getXmlblocks() {

        return xmlblocks;
    }

    /**
     * @param xmlblocks
     */
    public void setXmlblocks( String xmlblocks ) {

        this.xmlblocks = xmlblocks;
    }

    /**
     * @return id
     */ 
    public String getId() {

        return id;
    }

    /**
     * @param id
     */
    public void setId( String id ) {

        this.id = id;
    }
    public boolean isCsvmappingsAvailable() {

        return csvmappingsAvailable;
    }
    public void setCsvmappingsAvailable( boolean csvmappingsAvailable ) {

        this.csvmappingsAvailable = csvmappingsAvailable;
    }
    public boolean isMappingAvailable() {

        return mappingAvailable;
    }
    public void setMappingAvailable( boolean mappingAvailable ) {

        this.mappingAvailable = mappingAvailable;
    }
    public boolean isXmlblocksAvailable() {

        return xmlblocksAvailable;
    }
    public void setXmlblocksAvailable( boolean xmlblocksAvailable ) {

        this.xmlblocksAvailable = xmlblocksAvailable;
    }
}