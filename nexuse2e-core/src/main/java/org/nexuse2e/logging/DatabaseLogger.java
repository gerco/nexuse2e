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

import java.util.Date;

import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.nexuse2e.BeanStatus;
import org.nexuse2e.Engine;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.dao.LogDAO;
import org.nexuse2e.pojo.LogPojo;

/**
 * @author gesch
 *
 */
public class DatabaseLogger extends AbstractLogger {

    
    /**
     * Default constructor.
     */
    public DatabaseLogger() {

        status = BeanStatus.INSTANTIATED;
    }

    @Override
    protected void append( LoggingEvent loggingevent ) {

        LogDAO logDao;
        try {
            logDao = (LogDAO)Engine.getInstance().getBeanFactory().getBean( "logDao" );
        } catch ( Exception e ) {
            e.printStackTrace();
            return;
        }
        //        loggingevent.get
        if ( status != BeanStatus.ACTIVATED ) {
            return;
        }

        if ( !loggingevent.getLevel().isGreaterOrEqual( Level.toLevel( getLogThreshold(), Level.ERROR ) ) ) {
            return;
        }

        String description = "";
        if ( loggingevent.getMessage() instanceof LogMessage ) {
            description = ( (LogMessage) loggingevent.getMessage() ).toString(false);
        } else {
            description = loggingevent.getMessage().toString();
        }

        if ( ( description != null ) && ( description.length() > 4000 ) ) {
            description = description.substring( 0, 3999 );
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
            pojo.setLogId( Engine.getInstance().getEngineController().getEngineControllerStub().getMachineId() );

            pojo.setCreatedDate( new Date() );
            pojo.setClassName( normalizedClassName );
            pojo.setMethodName( methodName );
            pojo.setEventId( 0 );
            pojo.setSeverity( loggingevent.getLevel().toInt() );
            pojo.setDescription( description );
            pojo.setConversationId( "unknown" );
            pojo.setMessageId( "unknown" );
            if ( loggingevent.getMessage() instanceof LogMessage ) {
                LogMessage logMessage = (LogMessage) loggingevent.getMessage();
                if ( logMessage.getConversationId() != null ) {
                    pojo.setConversationId( logMessage.getConversationId() );
                }
                if ( logMessage.getMessageId() != null ) {
                    pojo.setMessageId( logMessage.getMessageId() );
                }
            }

            // avoid concurrent access to session
            synchronized (this) {
                
                logDao.saveLog( pojo );
            }
        } catch ( Exception ex ) {
            System.out.println("In case of truncation, please double check the database settings for the table nx_log. The description should be varchar(4000)");
        	ex.printStackTrace();
        }
    }

    @Override
    public void close() {
        
                
        
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#initialize(org.nexuse2e.configuration.EngineConfiguration)
     */
    public void initialize( EngineConfiguration config ) {

        status = BeanStatus.INITIALIZED;
    }

    @Override
    public void teardown() {

        close();
        super.teardown();
    }

}
