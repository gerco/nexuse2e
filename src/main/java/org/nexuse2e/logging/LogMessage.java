/**
 * NEXUSe2e Business Messaging Open Source  
 * Copyright 2007, Tamgroup and X-ioma GmbH   
 *  
 * This is free software; you can redistribute it and/or modify it  
 * under the terms of the GNU Lesser General Public License as  
 * published by the Free Software Foundation version 2.1 of  
 * the License.  
 *  
 * This software is distributed in the hope that it will be useful,  
 * but WITHOUT ANY WARRANTY; without even the implied warranty of  
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU  
 * Lesser General Public License for more details.  
 *  
 * You should have received a copy of the GNU Lesser General Public  
 * License along with this software; if not, write to the Free  
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.nexuse2e.logging;

import java.io.Serializable;

import org.nexuse2e.pojo.MessagePojo;

/**
 * @author mbreilmann
 *
 */
public class LogMessage implements Serializable {

    private static final long serialVersionUID = 7284169389821283301L;
    
    private String description    = null;
    private String conversationId = "unknown";
    private String messageId      = "unknown";

    public LogMessage( String description ) {

        this.description = description;
    }

    public LogMessage( String description, String conversationId, String messageId ) {

        this.description = description;
        this.conversationId = conversationId;
        this.messageId = messageId;
    }

    public LogMessage( String description, MessagePojo messagePojo ) {

        this.description = description;
        if ( messagePojo != null ) {
            this.conversationId = messagePojo.getConversation().getConversationId();
            this.messageId = messagePojo.getMessageId();
        }
    }

    public String getConversationId() {

        return conversationId;
    }

    public void setConversationId( String conversationId ) {

        this.conversationId = conversationId;
    }

    public String getDescription() {

        return description;
    }

    public void setDescription( String description ) {

        this.description = description;
    }

    public String getMessageId() {

        return messageId;
    }

    public void setMessageId( String messageId ) {

        this.messageId = messageId;
    }

    @Override
    public String toString() {

        return conversationId + "/" + messageId + ": " + description;
    }

} // LogMessage
