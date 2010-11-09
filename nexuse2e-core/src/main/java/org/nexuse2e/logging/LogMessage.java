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
package org.nexuse2e.logging;

import java.io.Serializable;

import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.pojo.MessagePojo;

/**
 * @author mbreilmann
 * 
 */
public class LogMessage implements Serializable {

	private static final long serialVersionUID = 7284169389821283301L;

	private String description = null;
	private String conversationId = "unknown";
	private String messageId = "unknown";

	/**
	 * @param description
	 */
	public LogMessage(String description) {

		this.description = description;
	}

	/**
	 * @param description
	 * @param conversationId
	 * @param messageId
	 */
	public LogMessage(String description, String conversationId,
			String messageId) {

		this.description = description;
		this.conversationId = conversationId;
		this.messageId = messageId;
	}

	/**
	 * @param description
	 * @param messagePojo
	 */
	public LogMessage(String description, MessagePojo messagePojo) {

		this.description = description;
		if (messagePojo != null) {
			if (messagePojo.getConversation() != null) {
				this.conversationId = messagePojo.getConversation()
						.getConversationId();
			}
			this.messageId = messagePojo.getMessageId();
		}
	}

	/**
	 * @param description
	 * @param messageContext
	 */
	public LogMessage(String description, MessageContext messageContext) {
		this(description, (messageContext != null ? messageContext
				.getMessagePojo() : null));
	}

	/**
	 * @return
	 */
	public String getConversationId() {

		return conversationId;
	}

	/**
	 * @param conversationId
	 */
	public void setConversationId(String conversationId) {

		this.conversationId = conversationId;
	}

	/**
	 * @return
	 */
	public String getDescription() {

		return description;
	}

	/**
	 * @param description
	 */
	public void setDescription(String description) {

		this.description = description;
	}

	/**
	 * @return
	 */
	public String getMessageId() {

		return messageId;
	}

	/**
	 * @param messageId
	 */
	public void setMessageId(String messageId) {

		this.messageId = messageId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		return toString(true);
	}

	/**
	 * by default toString prepends message and conversation id. Use full=false
	 * to suppress unnecessary ids.
	 * 
	 * @param full
	 * @return
	 */
	public String toString(boolean full) {

		if (full) {
			return conversationId + "/" + messageId + ": " + description;
		} else {
			return description;
		}
	}
}
