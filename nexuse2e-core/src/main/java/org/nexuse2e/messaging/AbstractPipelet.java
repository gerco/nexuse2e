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
package org.nexuse2e.messaging;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.nexuse2e.NexusException;
import org.nexuse2e.Constants.BeanStatus;
import org.nexuse2e.Constants.Layer;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.configuration.ParameterDescriptor;

abstract public class AbstractPipelet implements Pipelet {

    private static Logger                      LOG                  = Logger.getLogger( AbstractPipelet.class );

    protected Map<String, Object>              parameters   = new HashMap<String, Object>();
    protected Map<String, ParameterDescriptor> parameterMap = new LinkedHashMap<String, ParameterDescriptor>();

    protected boolean                          frontendPipelet;
    protected boolean                          forwardPipelet;
    protected BeanStatus                       status       = BeanStatus.UNDEFINED;
    
    protected Pipeline                         pipeline;

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#activate()
     */
    public void activate() {

        status = BeanStatus.ACTIVATED;
    }

    public void deactivate() {

        status = BeanStatus.INITIALIZED;
    }

    public void initialize( EngineConfiguration config ) throws InstantiationException {
        
        LOG.trace( "Initializing " + getClass() );

        status = BeanStatus.INITIALIZED;
    }

    abstract public MessageContext processMessage( MessageContext messageContext ) throws IllegalArgumentException,
            IllegalStateException, NexusException;

    public void teardown() {

        status = BeanStatus.INSTANTIATED;
    }

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

        return parameterMap;
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
     * @see org.nexuse2e.Manageable#getActivationRunlevel()
     */
    public Layer getActivationLayer() {

        return null;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#getStatus()
     */
    public BeanStatus getStatus() {

        return status;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.Pipelet#isForwardPipelet()
     */
    public boolean isForwardPipelet() {

        return forwardPipelet;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.Pipelet#isFrontendPipelet()
     */
    public boolean isFrontendPipelet() {

        return frontendPipelet;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.Pipelet#setForwardPipelet(boolean)
     */
    public void setForwardPipelet( boolean isForwardPipelet ) {

        forwardPipelet = isForwardPipelet;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.Pipelet#setFrontendPipelet(boolean)
     */
    public void setFrontendPipelet( boolean isFrontendPipelet ) {

        frontendPipelet = isFrontendPipelet;

    }
    
    public Pipeline getPipeline() {
        return pipeline;
    }
    
    public void setPipeline( Pipeline pipeline ) {
        this.pipeline = pipeline;
    }
    
}
