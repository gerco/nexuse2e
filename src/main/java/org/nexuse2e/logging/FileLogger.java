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
import org.apache.log4j.Logger;
import org.nexuse2e.Constants.BeanStatus;
import org.nexuse2e.Constants.Runlevel;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.Constants.ParameterType;

/**
 * @author gesch
 *
 */
public class FileLogger extends FileAppender implements LogAppender {

    private Map<String, Object>              parameters;
    private Map<String, ParameterDescriptor> parameterMap;
    private List<Logger>                     loggers = new ArrayList<Logger>();
    private BeanStatus                       status = BeanStatus.UNDEFINED;

    /**
     * Default constructor.
     */
    public FileLogger() {

        parameters = new HashMap<String, Object>();
        parameterMap = new LinkedHashMap<String, ParameterDescriptor>();
        parameterMap.put( "directory", new ParameterDescriptor( ParameterType.STRING, "Directory", "Target directory",
                "/nexus/dump" ) );
        parameterMap.put( "prefix", new ParameterDescriptor( ParameterType.STRING, "Prefix",
                "Prefix used to create the Filename", "" ) );
        parameterMap.put( "append", new ParameterDescriptor( ParameterType.BOOLEAN, "Append", "Appends log entries",
                Boolean.TRUE ) );
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
    public Runlevel getActivationRunlevel() {

        return Runlevel.CONFIGURATION;
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

        status = BeanStatus.INITIALIZED;

    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#teardown()
     */
    public void teardown() {
        // LOG.debug( "Freeing resources..." );
        deregisterLoggers();
        loggers.clear();
    } // teardown
    
    public void deregisterLoggers() {

        for ( Logger logger : loggers ) {
            logger.removeAppender( this );
        }
        
    }

    public void registerLogger( Logger logger ) {

        if(loggers != null) {
            loggers.add( logger );
        }
    }

    public int getLogThreshold() {

        // TODO Auto-generated method stub
        return 0;
    }

    public void setLogThreshold( int threshold ) {

        // TODO Auto-generated method stub
        
    }
}
