/**
 *  NEXUSe2e Business Messaging Open Source
 *  Copyright 2000-2010, Tamgroup and X-ioma GmbH
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
package org.nexuse2e.messaging.sync;

import org.nexuse2e.pojo.ConversationPojo;

/**
 * Defines a common service interface for classes that can synchronizes parallel activity on conversations.
 * @author sschulze
 */
public interface ConversationLockManager {
	
	/**
	 * Locks the given conversation for exclusive operation.
	 * If the lock is currently acquired by a different entity, this method blocks until the lock becomes available.
	 * Locks must always be released by calling {@link unlock(ConversationPojo)}!  
	 * <code>Note:</code> Acquiring the lock does not disable third parties from operating on the given conversation.
	 * The one holding the lock only indicates that a critical operation is in progress to other collaborative processes (those that respect locks).
	 * In order to ensure that a lock will not block forever, use the following idiom:
	 * <pre>
	 * ...
	 * convLockMgr.lock();
	 * try {
	 *     // exclusive operation
	 * } finally {
	 *     convLockMgr.unlock();
	 * }
	 * ...
 	 * </pre>
	 * @param conversation The conversation must not be <code>null</null>.
	 */
	void lock( ConversationPojo conversation );
	
	/**
	 * Same behavior as {@link ConversationLockManager#lock(ConversationPojo)}.
	 * @param conversationId The conversation's id (not the database primary key!). 
	 */
	void lock( String conversationId );
	
	/**
	 * Releases a lock on the given conversation that was previously acquired by {@link unlock(ConversationPojo)}.
	 * Locks must always be released immediately after the exclusive operation was executed.
	 * <code>Note:</code> Stubborn processes (those that do not respect locks) are able to unlock locks that were previously
	 * acquired by others. Since the purpose of locks is synchronization, and collaboration of parallel processes,
	 * you should not use implementations in any security related context.
	 * @param conv The conversation must not be <code>null</null>.
	 */
	void unlock( ConversationPojo conversation );
	
	/**
	 * Same behavior as {@link ConversationLockManager#unlock(ConversationPojo)}.
	 * @param conversationId The conversation's id (not the database primary key!). 
	 */
	void unlock( String conversationId );
}
