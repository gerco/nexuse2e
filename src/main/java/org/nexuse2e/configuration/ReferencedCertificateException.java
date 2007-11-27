package org.nexuse2e.configuration;

import java.util.Collection;

import org.nexuse2e.pojo.ConnectionPojo;

/**
 * This exception shall be thrown if an attempt is made to remove a certificate from
 * a configuration that is still referenced by one or more connections.
 *
 * @author Jonas Reese
 */
public class ReferencedCertificateException extends ReferencedObjectException {

    private static final long serialVersionUID = 1L;

    private Collection<ConnectionPojo> referringConnections;
    
    /**
     * 
     */
    public ReferencedCertificateException( Collection<ConnectionPojo> referringConnections ) {
        this.referringConnections = referringConnections;
    }

    @Override
    public Collection<ConnectionPojo> getReferringObjects() {
        return referringConnections;
    }
}
