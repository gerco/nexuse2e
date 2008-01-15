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
     * Constructs a new <code>ReferencedCertificateException</code>.
     * @param referringConnections The referring connections. Shall not be <code>null</code>
     * or empty.
     */
    public ReferencedCertificateException( Collection<ConnectionPojo> referringConnections ) {
        this.referringConnections = referringConnections;
    }

    @Override
    public Collection<ConnectionPojo> getReferringObjects() {
        return referringConnections;
    }
}
