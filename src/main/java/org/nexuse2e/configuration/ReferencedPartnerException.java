package org.nexuse2e.configuration;

import java.util.Collection;

import org.nexuse2e.pojo.ParticipantPojo;

/**
 * This exception shall be thrown if an attempt is made to remove a partner from a configuration
 * that is still referenced by one or more participants.
 *
 * @author Jonas Reese
 */
public class ReferencedPartnerException extends ReferencedObjectException {

    private static final long serialVersionUID = 1L;

    private Collection<ParticipantPojo> referringParticipants;
    
    public ReferencedPartnerException( Collection<ParticipantPojo> referringParticipants ) {
        this.referringParticipants = referringParticipants;
    }

    @Override
    public Collection<ParticipantPojo> getReferringObjects() {
        return referringParticipants;
    }
}
