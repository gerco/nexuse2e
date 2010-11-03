/**
 * 
 */
package org.nexuse2e.backend.pipelets;

import org.nexuse2e.messaging.AbstractGroovyPipelet;

/**
 * @author sschulze
 */
public class GroovyPipelet extends AbstractGroovyPipelet {
	
	public GroovyPipelet() {
		super();
		setFrontendPipelet( false );
	}	
}
