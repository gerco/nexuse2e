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
package org.nexuse2e.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.Constants.BeanStatus;
import org.nexuse2e.Constants.Layer;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.Constants.ParameterType;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.util.DateUtil;

/**
 * @author gesch
 *
 */
public class DBIntegrationService extends AbstractService implements SchedulerClient {

    private static Logger     LOG               = Logger.getLogger( DBIntegrationService.class );

    public static String      DATABASESERVICE   = "databasename";
    public static String      SCHEDULINGSERVICE = "schedulingname";
    public static String      TABLENAME         = "tablename";
    public static String      CHOREOGRAPHY      = "choreography";
    public static String      ACTION            = "action";
    public static String      INTERVAL          = "interval";

    private DatabaseService   dbService         = null;
    private SchedulingService schedulingService = null;
    private String            choreography      = null;
    private String            action            = null;
    private String            tableName         = null;
    private int               interval          = 10000;

    @Override
    public void fillParameterMap( Map<String, ParameterDescriptor> parameterMap ) {

        parameterMap.put( DATABASESERVICE, new ParameterDescriptor( ParameterType.SERVICE, "Database Service",
                "The name of the service that shall be used for database connection pooling", "" ) );
        parameterMap.put( SCHEDULINGSERVICE, new ParameterDescriptor( ParameterType.SERVICE, "Scheduling Service",
                "The name of the service that shall be used for time schedule", "" ) );
        parameterMap.put( TABLENAME, new ParameterDescriptor( ParameterType.STRING, "Table name",
                "The name of the table used for backend integration", "Payloads" ) );
        parameterMap.put( CHOREOGRAPHY, new ParameterDescriptor( ParameterType.STRING, "Choreography",
                "The name of the Choreography  used for optionally filtering messages", "" ) );
        parameterMap.put( ACTION, new ParameterDescriptor( ParameterType.STRING, "Action",
                "Additional Action filter when Choreogrphy is specified", "" ) );
        parameterMap.put( INTERVAL, new ParameterDescriptor( ParameterType.STRING, "Interval",
                "Database polling interval (Millseconds)", "10000" ) );
    }

    @Override
    public Layer getActivationLayer() {

        return Layer.INTERFACES;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.service.AbstractService#start()
     */
    @Override
    public void start() {

        LOG.trace( "starting" );

        super.start();
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.service.AbstractService#stop()
     */
    @Override
    public void stop() {

        LOG.trace( "stopping" );
        if ( schedulingService != null ) {
            schedulingService.deregisterClient( this );
        } else {
            LOG.error( "no scheduling service configured!" );
        }
        super.stop();
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.service.AbstractService#initialize(org.nexuse2e.configuration.EngineConfiguration)
     */
    @Override
    public void initialize( EngineConfiguration config ) throws InstantiationException {

        LOG.trace( "initializing" );
        String dbServiceName = getParameter( DATABASESERVICE );
        String schedulingServiceName = getParameter( SCHEDULINGSERVICE );
        action = getParameter( ACTION );
        choreography = getParameter( CHOREOGRAPHY );
        tableName = getParameter( TABLENAME );
        interval = Integer.parseInt((String) getParameter( INTERVAL ) );

        if ( !StringUtils.isEmpty( dbServiceName ) ) {

            Service service = Engine.getInstance().getActiveConfigurationAccessService().getService( dbServiceName );
            if ( service == null ) {
                status = BeanStatus.ERROR;
                LOG.error( "Service not found in configuration: " + dbServiceName );
                return;
            }
            if ( !( service instanceof DatabaseService ) ) {
                status = BeanStatus.ERROR;
                LOG.error( "dbServiceName is instance of " + service.getClass().getName()
                        + " but DatabaseService is required" );
                return;
            }
            dbService = (DatabaseService) service;

        } else {
            status = BeanStatus.ERROR;
            LOG.error( "DatabaseService is not properly configured (databaseServiceObj == null)!" );
            return;
        }
        if ( !StringUtils.isEmpty( schedulingServiceName ) ) {

            Service service = Engine.getInstance().getActiveConfigurationAccessService().getService(
                    schedulingServiceName );
            if ( service == null ) {
                status = BeanStatus.ERROR;
                LOG.error( "Service not found in configuration: " + schedulingServiceName );
                return;
            }
            if ( !( service instanceof SchedulingService ) ) {
                status = BeanStatus.ERROR;
                LOG.error( schedulingServiceName + " is instance of " + service.getClass().getName()
                        + " but SchedulingService is required" );
                return;
            }
            schedulingService = (SchedulingService) service;

        } else {
            status = BeanStatus.ERROR;
            LOG.error( "SchedulingService is not properly configured (schedulingServiceObj == null)!" );
            return;
        }

        ( (SchedulingService) schedulingService ).registerClient( this, interval );
        super.initialize( config );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.service.AbstractService#teardown()
     */
    @Override
    public void teardown() {

        LOG.trace( "teardown" );
        dbService = null;
        schedulingService = null;
        super.teardown();
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.service.SchedulerClient#scheduleNotify()
     */
    public void scheduleNotify() {

        LOG.debug( "do something" );
        if ( status == BeanStatus.STARTED ) {
            if ( dbService == null ) {
                LOG.error( "no Database Service found!" );
                status = BeanStatus.ERROR;
                return;
            }
            LOG.trace( "checking for new messages in database" );
            checkForNewMessages();
        }
    }

    /**
     * 
     */
    public void checkForNewMessages() {

        ResultSet resultSet = null;
        Connection connection = null;
        Statement statement = null;
        String tableName = getParameter( TABLENAME );

        String sql = "select KeyField, ConversationID, MessageID, ChoreographyID, ActionID, ParticipantID, Contenttype, PayloadField, SentFlag, LastModified_Date from "
                + tableName
                + " with( TABLOCKX ) "
                + " where SentFlag=0 and InboundFlag=0";
        if ( !StringUtils.isEmpty( choreography ) ) {
            sql = sql + " and ChoreographyID='" + choreography;
            if ( !StringUtils.isEmpty( action ) ) {
                sql = sql + "' and ActionID = '" + action + "'";
            }

        }

        try {
            connection = dbService.getDatabaseConnection();

            // set transaction level to avoid other nodes in a cluster to send the same message
            connection.setTransactionIsolation( Connection.TRANSACTION_SERIALIZABLE );
            connection.setAutoCommit( false );

            statement = connection.createStatement( ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY );

            // Process all rows that match the criteria
            LOG.debug( "checkForNewMessages - running query..." );

            LOG.debug( "SQL: " + sql );

            for ( resultSet = statement.executeQuery( sql ); resultSet.next(); ) {
                int key = resultSet.getInt( "KeyField" );
                String conversationId = resultSet.getString( "ConversationID" );
                String messageId = resultSet.getString( "MessageID" );
                String choreographyId = resultSet.getString( "ChoreographyID" );
                String actionId = resultSet.getString( "ActionID" );
                String participantId = resultSet.getString( "ParticipantID" );
                String contentType = resultSet.getString( "Contenttype" );
                byte[] payload = resultSet.getString( "PayloadField" ).getBytes();

                LOG.debug( "KeyField: " + key );
                LOG.debug( "PaticipantId: " + participantId );
                LOG.debug( "ConversationId:" + conversationId );
                LOG.debug( "MessageId: " +messageId );
                LOG.debug( "contentType: " + contentType );
                LOG.debug( "choreography: " + choreographyId );
                LOG.debug( "Action: " + actionId );

                MessageContext message = Engine.getInstance().getCurrentConfiguration().getBackendPipelineDispatcher()
                        .processMessage( participantId, choreographyId, actionId, conversationId, null, null, payload );

                conversationId = message.getMessagePojo().getConversation().getConversationId();
                messageId = message.getMessagePojo().getMessageId();
                boolean inline = false;
                if ( inline ) {
                    resultSet.updateInt( "SentFlag", 1 );
                    resultSet.updateString( "ConversationID", conversationId );
                    resultSet.updateString( "MessageID", messageId );
                    resultSet.updateString( "LastModified_Date", DateUtil.getFormatedNowString() );
                    resultSet.updateRow();
                } else {
                    updateSentFlag( connection,messageId, conversationId, key );
                }

                LOG.debug( "ConversationId: " +conversationId );

            }
        } catch ( SQLException sqlException ) {
            sqlException.printStackTrace();
            LOG.error( "Error accessing result set retrieved for new payloads. Exception:  " + sqlException.toString() );
        } catch ( Exception exception ) {
            LOG.error( "Error sending new payloads. Exception:  " + exception.toString() );
            exception.printStackTrace();
        } finally {
            try {
                resultSet.close();
                statement.close();
                // Commit changes so other nodes in a cluster can access the data
                connection.commit();
                connection.setAutoCommit( true );
            } catch ( Exception exception1 ) {
            }
            try {
                dbService.releaseDatabaseConnection( connection );
            } catch ( SQLException e ) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * 
     * @param conversationId
     * @param key
     * @throws ConnectorException
     */
    public void updateSentFlag( Connection connection, String messageId, String conversationId, int key ) throws NexusException {

        String currentDate = DateUtil.getFormatedNowString();
        PreparedStatement preparedstatement = null;
        String sql = "update " + tableName + " set SentFlag=1, LastModified_Date=?, MessageID=?, ConversationID=? "
                + "where KeyField=?";
        try {
            preparedstatement = connection.prepareStatement( sql );
            preparedstatement.setString( 1, currentDate );
            preparedstatement.setString( 2, messageId );
            preparedstatement.setString( 3, conversationId );
            preparedstatement.setInt( 4, key );
            preparedstatement.executeUpdate();
        } catch ( Exception exception ) {
            exception.printStackTrace();
            LOG.error( "Error updating payload with the key of " + key + " Exception:  " + exception.toString() );
            throw new NexusException( "Error in ProcessOutboundMessage", exception );
        } finally {
            try {
                preparedstatement.close();
            } catch ( Exception exception2 ) {
            }
        }
    } // updateSentFlag

}
