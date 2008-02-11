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
package org.nexuse2e.reporting;


import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;

import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.IReportEngine;
import javax.servlet.*;
import org.eclipse.birt.core.framework.PlatformServletContext;
import org.eclipse.birt.core.framework.IPlatformContext;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.report.engine.api.IReportEngineFactory;


/**
 * Wrapper for the BIRT engine. This class was taken from a sample from Jason Weathersby
 * (available under http://www.onjava.com/pub/a/onjava/2006/07/26/deploying-birt.html).
 * 
 * @author Jonas Reese
 */
public class BirtEngine {

	private static IReportEngine birtEngine = null;

	private static Properties configProps = new Properties();

	private final static String configFile = "org/nexuse2e/reporting/BirtConfig.properties";

	public static synchronized void initBirtConfig() {
		loadEngineProperties();
	}

	public static synchronized IReportEngine getBirtEngine( ServletContext sc ) {
		if (birtEngine == null) {
			EngineConfig config = new EngineConfig();
			if ( configProps != null){
				String logLevel = configProps.getProperty("logLevel");
				Level level = Level.OFF;
				if ("SEVERE".equalsIgnoreCase(logLevel)) {
					level = Level.SEVERE;
				} else if ("WARNING".equalsIgnoreCase(logLevel)) {
					level = Level.WARNING;
				} else if ("INFO".equalsIgnoreCase(logLevel)) {
					level = Level.INFO;
				} else if ("CONFIG".equalsIgnoreCase(logLevel)) {
					level = Level.CONFIG;
				} else if ("FINE".equalsIgnoreCase(logLevel)) {
					level = Level.FINE;
				} else if ("FINER".equalsIgnoreCase(logLevel)) {
					level = Level.FINER;
				} else if ("FINEST".equalsIgnoreCase(logLevel)) {
					level = Level.FINEST;
				} else if ("OFF".equalsIgnoreCase(logLevel)) {
					level = Level.OFF;
				}

				config.setLogConfig(configProps.getProperty("logDirectory"), level);
			}

			config.setEngineHome( "" );
			IPlatformContext context = new PlatformServletContext( sc );
			config.setPlatformContext( context );

			try {
				Platform.startup( config );
			} catch ( BirtException e ) {
				throw new UnsupportedOperationException( e );
			}

			IReportEngineFactory factory =
			    (IReportEngineFactory) Platform.createFactoryObject(
			            IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY );
			birtEngine = factory.createReportEngine( config );


		}
		return birtEngine;
	}

	public static synchronized void shutdown() {
		if (birtEngine == null) {
			return;
		}		
		Platform.shutdown();
		birtEngine = null;
	}

	private static void loadEngineProperties() {
		try {
			ClassLoader cl = Thread.currentThread ().getContextClassLoader();
			InputStream in = null;
			in = cl.getResourceAsStream( configFile );
			configProps.load( in );
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
