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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;
import org.nexuse2e.Constants.BeanStatus;
import org.nexuse2e.Constants.Layer;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.Constants.ParameterType;

/**
 * @author gesch
 *
 */
public class FileLogger extends FileAppender implements LogAppender {

    private static Logger                    LOG       = Logger.getLogger( FileLogger.class );
    public final static String               DIRECTORY = "directory";
    public final static String               PREFIX    = "prefix";
    public final static String               APPEND    = "append";
    public final static String               PATTERN   = "pattern";

    private Map<String, Object>              parameters;
    private Map<String, ParameterDescriptor> parameterMap;
    private List<Logger>                     loggers   = new ArrayList<Logger>();
    private BeanStatus                       status    = BeanStatus.UNDEFINED;

    /**
     * Default constructor.
     */
    public FileLogger() {

        parameters = new HashMap<String, Object>();
        parameterMap = new LinkedHashMap<String, ParameterDescriptor>();
        parameterMap.put( DIRECTORY, new ParameterDescriptor( ParameterType.STRING, "Directory", "Target directory",
                "/nexus/dump" ) );
        parameterMap.put( PREFIX, new ParameterDescriptor( ParameterType.STRING, "Prefix",
                "Prefix used to create the Filename", "" ) );
        parameterMap.put( APPEND, new ParameterDescriptor( ParameterType.BOOLEAN, "Append", "Appends log entries",
                Boolean.TRUE ) );
        parameterMap.put( PATTERN, new ParameterDescriptor( ParameterType.STRING, "Pattern",
                "The Logger Pattern to be used", " [%c %M] %-5p %m%n" ) );
    }

    @SuppressWarnings("unchecked")
    public <T> T getParameter( String name ) {

        return (T) parameters.get( name );
    }

    public void setParameter( String name, Object value ) {

        parameters.put( name, value );
    }

    public Map<String, Object> getParameters() {

        return Collections.unmodifiableMap( parameters );
    }

    public Map<String, ParameterDescriptor> getParameterMap() {

        return parameterMap;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#activate()
     */
    public void activate() {

        status = BeanStatus.ACTIVATED;

    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#deactivate()
     */
    public void deactivate() {

        status = BeanStatus.INITIALIZED;

    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#getActivationTime()
     */
    public Layer getActivationLayer() {

        return Layer.CONFIGURATION;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#getStatus()
     */
    public BeanStatus getStatus() {

        return status;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#initialize(org.nexuse2e.configuration.EngineConfiguration)
     */
    public void initialize( EngineConfiguration config ) {

        LOG.debug( "initialized" );
        status = BeanStatus.INITIALIZED;

        PatternLayout patternLayout = new PatternLayout();
        String pattern = (String) getParameter( PATTERN );
        if ( pattern == null ) {
            pattern = " [%c %M] %-5p %m%n";
        }
        patternLayout.setConversionPattern( pattern );
        setName( "NexusFileAppender" );
        setFile( getParameter( DIRECTORY ) + "/log.log" );
        setLayout( patternLayout );
        setBufferedIO( false );
        setAppend( ( (Boolean) getParameter( APPEND ) ).booleanValue() );
        activateOptions();
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#teardown()
     */
    public void teardown() {

        deregisterLoggers();
        loggers.clear();
    } // teardown

    public void deregisterLoggers() {

        for ( Logger logger : loggers ) {
            logger.removeAppender( this );
        }

    }

    public void registerLogger( Logger logger ) {

        if ( loggers != null ) {
            loggers.add( logger );
        }
    }

    public int getLogThreshold() {

        return threshold.toInt();
    }

    public void setLogThreshold( int threshold ) {

        this.threshold = Level.toLevel( threshold );

    }

    /* (non-Javadoc)
     * @see org.apache.log4j.WriterAppender#append(org.apache.log4j.spi.LoggingEvent)
     */
    @Override
    public void append( LoggingEvent arg0 ) {

        super.append( arg0 );
    }
}
