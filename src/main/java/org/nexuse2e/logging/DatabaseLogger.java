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

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.Constants.BeanStatus;
import org.nexuse2e.Constants.Runlevel;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.Constants.ParameterType;
import org.nexuse2e.dao.LogDAO;
import org.nexuse2e.pojo.LogPojo;

/**
 * @author gesch
 *
 */
public class DatabaseLogger extends AppenderSkeleton implements Logger {

    private Map<String, Object>              parameters;
    private Map<String, ParameterDescriptor> parameterMap;

    private BeanStatus                       status = BeanStatus.UNDEFINED;

    /**
     * Default constructor.
     */
    public DatabaseLogger() {

        parameters = new HashMap<String, Object>();
        parameterMap = new LinkedHashMap<String, ParameterDescriptor>();
        parameterMap.put( "machineid", new ParameterDescriptor( ParameterType.STRING, false, "MachineId",
                "Used to separate different nexus process in logs", "nexus" ) );
        parameterMap.put( "details", new ParameterDescriptor( ParameterType.BOOLEAN, false, "Details",
                "Displays the detailed class and method names.", true ) );
        parameterMap.put( "external", new ParameterDescriptor( ParameterType.BOOLEAN, true, "external",
                "if enabled, the specified database is used to store upcomming log messages", true ) );
        parameterMap.put( "url", new ParameterDescriptor( ParameterType.STRING, false, "Database URL",
                "Connection String", "" ) );
        parameterMap
                .put( "username", new ParameterDescriptor( ParameterType.STRING, false, "Username", "Username", "" ) );
        parameterMap.put( "password", new ParameterDescriptor( ParameterType.PASSWORD, "Password", "Password",
                "/nexus/dump" ) );
        parameterMap.put( "prefix", new ParameterDescriptor( ParameterType.STRING, "Prefix", "obsolete", "" ) );
        //        DropdownParameter dropdown = new DropdownParameter();
        //        dropdown.addElement( "Option 1", "1" );
        //        dropdown.addElement( "Option 2", "2" );
        //        dropdown.addElement( "Option 3", "3" );
        //        dropdown.setSelectedValue( "1" );
        //        parameterMap.put( "dropdown", new ParameterDescriptor(
        //                ParameterType.DROPDOWN, "Choice", "Select an option", dropdown ) );

        threshold = Level.ERROR;
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

        if ( status != BeanStatus.ACTIVATED ) {
            return;
        }

        if ( loggingevent.level.isGreaterOrEqual( threshold ) ) {
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

        // LOG.debug( "Freeing resources..." );

    } // teardown

}
