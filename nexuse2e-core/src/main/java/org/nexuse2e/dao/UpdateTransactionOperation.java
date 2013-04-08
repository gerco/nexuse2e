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
package org.nexuse2e.dao;

import org.nexuse2e.NexusException;
import org.nexuse2e.controller.StateTransitionException;
import org.nexuse2e.pojo.ConversationPojo;
import org.nexuse2e.pojo.MessagePojo;

/**
 * Implementors of this interface can be handed over to a {@link TransactionDAO} implementation
 * in order to perform update operations to messages, conversations, or both.
 *
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public interface UpdateTransactionOperation {

    public enum UpdateScope {
        /** Conv only */
        CONVERSATION_ONLY(true, false, false),
        /** Conv and message */
        CONVERSATION_AND_MESSAGE(true, true, false),
        /** Conv and referenced message */
        CONVERSATION_AND_REFERENCED_MESSAGE(true, false, true),
        /** Update message only */
        MESSAGE_ONLY(false, true, false),
        /** Update referenced message only */
        REFERENCED_MESSAGE_ONLY(false, false, true),
        /** Update message and referenced message */
        MESSAGES(false, true, true),
        /** Update conv, message, and referenced message */
        ALL(true, true, true),
        /** Update nothing */
        NOTHING(false, false, false);
        
        private boolean conv;
        private boolean msg;
        private boolean refMsg;
        
        UpdateScope(boolean conv, boolean msg, boolean refMsg) {
            this.conv = conv;
            this.msg = msg;
            this.refMsg = refMsg;
        }
        
        public boolean updateConversation() {
            return conv;
        }
        public boolean updateMessage() {
            return msg;
        }
        public boolean updateReferencedMessage() {
            return refMsg;
        }
    }
    
    /**
     * Perform the update operation on persistent conversation and message.
     * @param conversation The persistent conversation. If no persistent conversation was found, the conversation
     * passed to the <code>TransactionDAO</code>'s update method will be passed here.
     * @param message The persistent message. If no persistent message was found, the message passed to the
     * <code>TransactionDAO</code>'s update method will be passed here.
     * @return This method shall return an <code>UpdateScope</code> enum element, depending on what shall be persisted.
     * @throws NexusException if the update failed due to an unexpected problem. 
     * @throws StateTransitionException if the update operation failed because an illegal state transition was detected.
     */
    public UpdateScope update(ConversationPojo conversation, MessagePojo message, MessagePojo referencedMessage) throws NexusException, StateTransitionException;
}
