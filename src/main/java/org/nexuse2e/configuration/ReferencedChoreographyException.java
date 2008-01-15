package org.nexuse2e.configuration;

import java.util.Collection;

import org.nexuse2e.pojo.ConversationPojo;

/**
 * This exception shall be thrown if an attempt is made to remove a choreography from
 * a configuration that is still referenced by one or more conversations.
 *
 * @author Jonas Reese
 */
public class ReferencedChoreographyException extends ReferencedObjectException {

    private static final long serialVersionUID = 1L;

    private Collection<ConversationPojo> referringConversations;
    
    /**
     * Constructs a new <code>ReferencedCertificateException</code>.
     * @param referringConversations The referring conversations. Shall not be <code>null</code>
     * or empty.
     */
    public ReferencedChoreographyException( Collection<ConversationPojo> referringConversations ) {
        this.referringConversations = referringConversations;
    }
    
    
    @Override
    public Collection<ConversationPojo> getReferringObjects() {
        return referringConversations;
    }

}
