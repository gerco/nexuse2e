package org.nexuse2e.pojo;

/**
 * Storage class for persistent properties. Persistent properties are key-value style
 * generic properties that are not part of the NEXUSe2e configuration.
 *
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public class PersistentPropertyPojo {

    private int nxPersistentPropertyId;
    private String namespace;
    private String version;
    private String name;
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
