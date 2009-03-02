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
package org.nexuse2e.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.nexuse2e.Constants.BeanStatus;
import org.nexuse2e.Constants.Layer;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.configuration.ParameterDescriptor;

/**
 * Abstract <code>Service</code> implementation that provides common
 * functionality and can easily be extended.
 * <p>
 * Invoke <code>super.lifecycleMethod()</code> for all lifecycle methods
 * in order to maintain the correct status.
 * 
 * @author jonas.reese
 */
public abstract class AbstractService implements Service {

    protected BeanStatus                       status;

    private boolean                            autostart = false;

    /**
     * Map parameter names to <code>ParameterDescriptor</code> objects in
     * this <code>Map</code>. The insertion order will be maintained.
     */
    protected Map<String, ParameterDescriptor> parameterMap;

    private Map<String, Object>                parameters;

    /**
     * Default constructor.
     */
    public AbstractService() {

        status = BeanStatus.INSTANTIATED;
        parameterMap = new LinkedHashMap<String, ParameterDescriptor>();
        parameters = new HashMap<String, Object>();
        fillParameterMap( parameterMap );
        for ( String key : parameterMap.keySet() ) {
            ParameterDescriptor pd = parameterMap.get( key );
            if ( pd != null ) {
                Object defaultValue = pd.getDefaultValue();
                if ( defaultValue != null ) {
                    parameters.put( key, defaultValue );
                }
            }
        }
    }

    /**
     * This method shall be overwritten in order to fill the map of parameters that
     * are supported by the service.
     * @param parameterMap The parameter map. Same as protected map <code>parameterMap</code>.
     */
    public abstract void fillParameterMap( Map<String, ParameterDescriptor> parameterMap );

    public void start() {

        status = BeanStatus.STARTED;
    }

    public void stop() {

        status = BeanStatus.ACTIVATED;
    }

    public void activate() {

        status = BeanStatus.ACTIVATED;
    }

    public void deactivate() {

        status = BeanStatus.INITIALIZED;
    }

    public abstract Layer getActivationLayer();

    public BeanStatus getStatus() {

        return status;
    }

    /**
     * Sets the status.
     * @param status The lifecycle status.
     */
    protected void setStatus( BeanStatus status ) {

        this.status = status;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#initialize(org.nexuse2e.configuration.EngineConfiguration)
     */
    public void initialize( EngineConfiguration config ) throws InstantiationException {

        status = BeanStatus.INITIALIZED;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#teardown()
     */
    public void teardown() {

        // LOG.debug( "Freeing resources..." );

        status = BeanStatus.INSTANTIATED;
    } // teardown

    /* (non-Javadoc)
     * @see org.nexuse2e.Configurable#getParameter(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public <T> T getParameter( String name ) {

        return (T) parameters.get( name );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Configurable#getParameterMap()
     */
    public Map<String, ParameterDescriptor> getParameterMap() {

        return Collections.unmodifiableMap( parameterMap );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Configurable#getParameters()
     */
    public Map<String, Object> getParameters() {

        return Collections.unmodifiableMap( parameters );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Configurable#setParameter(java.lang.String, java.lang.Object)
     */
    public void setParameter( String name, Object value ) {

        parameters.put( name, value );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.service.Service#isAutostart()
     */
    public boolean isAutostart() {

        return autostart;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.service.Service#setAutostart(boolean)
     */
    public void setAutostart( boolean autostart ) {

        this.autostart = autostart;
    }
}
