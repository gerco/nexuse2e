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
package org.nexuse2e.pojo;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Storage class for persistent properties. Persistent properties are key-value style
 * generic properties that are not part of the NEXUSe2e configuration.
 *
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
@Entity
@Table(name = "nx_persistent_property")
public class PersistentPropertyPojo {
	@Access(AccessType.PROPERTY)
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int nxPersistentPropertyId;

    @Column(name = "namespace", length = 128)
    private String namespace;

    @Column(name = "version", length = 128)
    private String version;

    @Column(name = "name", length = 128)
    private String name;

    @Column(name = "value", length = 128)
    private String value;

    /**
     * Full constructor.
     * @param nxPersistentPropertyId The primary key.
     * @param namespace The namespace.
     * @param version The version.
     * @param name The property name.
     * @param value The property value.
     */
    public PersistentPropertyPojo(
            int nxPersistentPropertyId, String namespace, String version, String name, String value ) {
        this.nxPersistentPropertyId = nxPersistentPropertyId;
        this.namespace = namespace;
        this.version = version;
        this.name = name;
        this.value = value;
    }

    /**
     * Default constructor. Call setter methods to fill up.
     */
    public PersistentPropertyPojo() {
    }
    
    public int getNxPersistentPropertyId() {
        return nxPersistentPropertyId;
    }
    
    public void setNxPersistentPropertyId( int nxPersistentPropertyId ) {
        this.nxPersistentPropertyId = nxPersistentPropertyId;
    }
    
    public String getNamespace() {
        return namespace;
    }
    
    public void setNamespace( String namespace ) {
        this.namespace = namespace;
    }
    
    public String getVersion() {
        return version;
    }
    
    public void setVersion( String version ) {
        this.version = version;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName( String name ) {
        this.name = name;
    }
    
    public String getValue() {
        return value;
    }
    
    public void setValue( String value ) {
        this.value = value;
    }
}
