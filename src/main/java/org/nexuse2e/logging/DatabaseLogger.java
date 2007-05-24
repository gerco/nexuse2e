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
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.Constants.BeanStatus;
import org.nexuse2e.Constants.Runlevel;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.dao.LogDAO;
import org.nexuse2e.pojo.LogPojo;

/**
 * @author gesch
 *
 */
public class DatabaseLogger extends AppenderSkeleton implements LogAppender {

    private Map<String, Object>              parameters;
    private Map<String, ParameterDescriptor> parameterMap;
    private List<Logger>                     loggers      = new ArrayList<Logger>();
    private BeanStatus                       status       = BeanStatus.UNDEFINED;
    private int                              logThreshold = 0;

    /**
     * Default constructor.
     */
    public DatabaseLogger() {

        parameters = new HashMap<String, Object>();
        parameterMap = new LinkedHashMap<String, ParameterDescriptor>();
        status = BeanStatus.INSTANTIATED;
    }

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

    @Override
    protected void append( LoggingEvent loggingevent ) {

        //        loggingevent.get
        if ( status != BeanStatus.ACTIVATED ) {
            return;
        }

        if ( !loggingevent.getLevel().isGreaterOrEqual( Level.toLevel( getLogThreshold(), Level.ERROR ) ) ) {
            return;
        }

        String description = loggingevent.getMessage().toString();

        if ( ( description != null ) && ( description.length() > 255 ) ) {
            description = description.substring( 0, 254 );
        }

        try {
            LogPojo pojo = new LogPojo();

            String className = loggingevent.getLocationInformation().getClassName();
            String methodName = loggingevent.getLocationInformation().getMethodName();
            int endIndex = className.indexOf( "." );
            String normalizedClassName;

            if ( endIndex > 0 ) {
                normalizedClassName = className;//.substring( begineIndex, endIndex );
            } else {
                normalizedClassName = className;
            }

            //TODO get machine id ?
            pojo.setLogId( "nexus" );

            pojo.setCreatedDate( new Date() );
            pojo.setClassName( normalizedClassName );
            pojo.setMethodName( methodName );
            pojo.setEventId( 0 );
            pojo.setSeverity( loggingevent.getLevel().toInt() );
            pojo.setDescription( description );
            pojo.setConversationId( "unknown" );
            pojo.setMessageId( "unknown" );

            LogDAO logDao = null;
            try {
                logDao = (LogDAO) Engine.getInstance().getDao( "logDao" );
            } catch ( Exception e ) {
                NexusException ie = new NexusException( e );
                ie.setStackTrace( e.getStackTrace() );
                throw ie;
            }
            logDao.saveLog( pojo, null, null );
        } catch ( Exception ex ) {
            //TODO call errorhandler ?
            ex.printStackTrace();
        }
    }

    @Override
    public void close() {

        // TODO Auto-generated method stub

    }

    @Override
    public boolean requiresLayout() {

        return false;
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

        return logThreshold;
    }

    public void setLogThreshold( int threshold ) {

        logThreshold = threshold;
    }

}
