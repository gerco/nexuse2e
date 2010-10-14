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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.nexuse2e.Constants.BeanStatus;
import org.nexuse2e.Constants.Layer;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.configuration.ParameterDescriptor;

/**
 * @author mbreilmann
 *
 */
public abstract class AbstractLogger extends AppenderSkeleton implements LogAppender {

    protected Map<String, Object>              parameters   = new HashMap<String, Object>();
    protected Map<String, ParameterDescriptor> parameterMap = new LinkedHashMap<String, ParameterDescriptor>();
    protected List<Logger>                     loggers      = new ArrayList<Logger>();
    protected BeanStatus                       status       = BeanStatus.UNDEFINED;
    protected int                              logThreshold = 0;

    /* (non-Javadoc)
     * @see org.apache.log4j.AppenderSkeleton#append(org.apache.log4j.spi.LoggingEvent)
     */
    @Override
    abstract protected void append( LoggingEvent arg0 );

    /* (non-Javadoc)
     * @see org.apache.log4j.AppenderSkeleton#close()
     */
    @Override
    abstract public void close();

    /* (non-Javadoc)
     * @see org.apache.log4j.AppenderSkeleton#requiresLayout()
     */
    @Override
    public boolean requiresLayout() {

        return false;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#initialize(org.nexuse2e.configuration.EngineConfiguration)
     */
    abstract public void initialize( EngineConfiguration config ) throws InstantiationException;
    
    @SuppressWarnings("unchecked")
    public <T> T getParameter( String name ) {

        return (T) parameters.get( name );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Configurable#setParameter(java.lang.String, java.lang.Object)
     */
    public void setParameter( String name, Object value ) {

        parameters.put( name, value );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Configurable#getParameters()
     */
    public Map<String, Object> getParameters() {

        return Collections.unmodifiableMap( parameters );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Configurable#getParameterMap()
     */
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
     * @see org.nexuse2e.Manageable#teardown()
     */
    public void teardown() {

        deregisterLoggers();
        loggers.clear();
    } // teardown

    public void deregisterLoggers() {

        if ( loggers != null ) {
            for ( Logger logger : loggers ) {
                logger.removeAppender( this );
            }
        }

    }

    public void registerLogger( Logger logger ) {

        if ( loggers != null ) {
            loggers.add( logger );
        }
    }

    public int getLogThreshold() {

        return logThreshold;
    }

    public void setLogThreshold( int threshold ) {

        logThreshold = threshold;
    }

}
