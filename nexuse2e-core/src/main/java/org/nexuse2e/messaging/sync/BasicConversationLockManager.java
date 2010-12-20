/**
 * 
 */
package org.nexuse2e.messaging.sync;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

import org.apache.log4j.Logger;
import org.nexuse2e.pojo.ConversationPojo;

/**
 * Basic implementation of the {@link ConversationLockManager} interface,
 * that works for a single instance of NEXUSe2e.
 * @author sschulze
 */
public class BasicConversationLockManager implements ConversationLockManager {

	private static final Logger LOG = Logger.getLogger( BasicConversationLockManager.class );
	
	private Map<String,Semaphore> locks;
	
	public BasicConversationLockManager() {
		locks = new HashMap<String,Semaphore>();
	}
	
	/* (non-Javadoc)
	 * @see org.nexuse2e.messaging.sync.ConversationLockManager#lock(org.nexuse2e.pojo.ConversationPojo)
	 */
	public void lock( ConversationPojo conversation ) {
		lock( conversation.getConversationId() );
	}

	/* (non-Javadoc)
	 * @see org.nexuse2e.messaging.sync.ConversationLockManager#unlock(org.nexuse2e.pojo.ConversationPojo)
	 */
	public void unlock( ConversationPojo conversation ) {
		unlock( conversation.getConversationId() );
	}

	/*
	 * (non-Javadoc)
	 * @see org.nexuse2e.messaging.sync.ConversationLockManager#lock(java.lang.String)
	 */
	public void lock( String conversationId ) {
//		Semaphore lock = locks.get( conversationId );
//		synchronized ( locks ) {
//			lock = locks.get( conversationId );
//			if ( lock == null ) {
//				lock = new Semaphore( 1 );
//				locks.put( conversationId, lock );
//			}
//		}
//		if ( LOG.isDebugEnabled() ) {
//			LOG.debug( "Requesting lock for: [" + Thread.currentThread().getName() + "] " + getCallerFromStackTrace()
//					+ "\nin conversation: " + conversationId );
//		}
//		try {
//			lock.acquire();
//		} catch ( InterruptedException e ) {
//			LOG.warn( "Lock acquirement for conversation " + conversationId
//					+ " of [" + Thread.currentThread().getName() + "] " + getCallerFromStackTrace()
//					+ " was interrupted: " + e.getMessage(), e );
//		}
//		if ( LOG.isDebugEnabled() ) {
//			LOG.debug( "Acquired lock for: [" + Thread.currentThread().getName() + "] " + getCallerFromStackTrace()
//					+ "\nin conversation: " + conversationId );
//		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.nexuse2e.messaging.sync.ConversationLockManager#unlock(java.lang.String)
	 */
	public void unlock(String conversationId) {
//		if ( LOG.isDebugEnabled() ) {
//			LOG.debug( "Releasing lock for: [" + Thread.currentThread().getName() + "] " + getCallerFromStackTrace()
//					+ "\nin conversation: " + conversationId );
//		}
//		synchronized ( locks ) {
//			Semaphore lock = locks.remove( conversationId );
//			if ( lock != null ) {
//				lock.release();
//			}
//		}
	}
	
	/**
	 * For debugging purposes.
	 * @return The callers StackElement.toString()
	 */
	private String getCallerFromStackTrace() {
		StackTraceElement[] st = Thread.currentThread().getStackTrace();
		String thisClassesName = this.getClass().getName();
		for ( int i = 2; i < st.length; i++ ) { // i = 2 because first is "getStackTrace", second is this method
			if ( !thisClassesName.equals( st[i].getClassName() ) ) {
				return st[i].toString();
			}
		}
		// fall-back
		return "unknown";
	}
}
