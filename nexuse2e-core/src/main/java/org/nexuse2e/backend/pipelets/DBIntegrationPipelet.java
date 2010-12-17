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
package org.nexuse2e.backend.pipelets;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.Constants.BeanStatus;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.Constants.ParameterType;
import org.nexuse2e.messaging.AbstractPipelet;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.pojo.MessagePayloadPojo;
import org.nexuse2e.service.DatabaseService;
import org.nexuse2e.service.Service;
import org.nexuse2e.util.DateUtil;

/**
 * @author gesch
 *
 */
public class DBIntegrationPipelet extends AbstractPipelet {

    private static Logger   LOG             = Logger.getLogger( DBIntegrationPipelet.class );

    public static String    DATABASESERVICE = "databasename";
    public static String    TABLENAME       = "tablename";

    protected DatabaseService dbService       = null;
    protected String          tableName       = null;

    /**
     * Default constructor.
     */
    public DBIntegrationPipelet() {

        parameterMap.put( DATABASESERVICE, new ParameterDescriptor( ParameterType.SERVICE, "Database Service",
                "The name of the service that shall be used for database connection pooling", "" ) );
        parameterMap.put( TABLENAME, new ParameterDescriptor( ParameterType.STRING, "Table name",
                "The name of the table used for backend integration", "" ) );

    }

    @Override
    public MessageContext processMessage( MessageContext messageContext ) throws IllegalArgumentException,
            IllegalStateException, NexusException {

        
        if ( dbService == null ) {
            throw new IllegalStateException( "Database pooling service is not configured!" );
        }

        String sql = "INSERT INTO " + tableName
                + " ( ChoreographyID, ConversationID, MessageID, ActionID, ParticipantID, InboundFlag, "
                + "ContentType, PayloadField ,Source,Created_Date,LastModified_Date) "
                + "values( ?, ?, ?, ?, ?, ?, ?,?,?,?,?)";
        PreparedStatement preparedstatement = null;
        List<MessagePayloadPojo> payloads = messageContext.getMessagePojo().getMessagePayloads();

        for ( MessagePayloadPojo pojo : payloads ) {
            byte contentBytes[] = pojo.getPayloadData();
            // TODO: (encoding) Database Definition? maybe BLOB not String. Configurable Encoding?
            String payloadString = new String( contentBytes );
            Connection connection = null;
            try {

                connection = dbService.getDatabaseConnection();
                preparedstatement = connection.prepareStatement( sql );
                preparedstatement.setString( 1, messageContext.getMessagePojo().getConversation().getChoreography()
                        .getName() );
                preparedstatement.setString( 2, messageContext.getMessagePojo().getConversation().getConversationId() );
                preparedstatement.setString( 3, messageContext.getMessagePojo().getMessageId() );
                preparedstatement.setString( 4, messageContext.getMessagePojo().getAction().getName() );
                preparedstatement.setString( 5, messageContext.getMessagePojo().getParticipant().getPartner()
                        .getPartnerId() );
                preparedstatement.setInt( 6, 1 );
                preparedstatement.setString( 7, pojo.getMimeType() );
                preparedstatement.setString( 8, payloadString );
                preparedstatement.setString( 9, "XML" );
                preparedstatement.setString( 10, DateUtil.getFormatedNowString() );
                preparedstatement.setString( 11, DateUtil.getFormatedNowString() );
                preparedstatement.executeUpdate();
            } catch ( Exception exception ) {
                LOG.error( "Error processing inbound message, Conversation ID  "
                        + messageContext.getMessagePojo().getConversation().getConversationId() + exception.toString() );
            } finally {
                try {
                    preparedstatement.close();
                } catch ( SQLException sqlexception ) {
                    LOG.error( "Error while closing prepared statment: " + sqlexception );
                }
                try {
                    dbService.releaseDatabaseConnection( connection );
                } catch ( SQLException e ) {
                    LOG.error( "Error while releasing database connection: " + e );
                }
            }
        }

        return null;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.AbstractPipelet#initialize(org.nexuse2e.configuration.EngineConfiguration)
     */
    @Override
    public void initialize( EngineConfiguration config ) throws InstantiationException {

        LOG.trace( "initializing" );
        String dbServiceName = getParameter( DATABASESERVICE );
        tableName = getParameter( TABLENAME );

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
        super.initialize( config );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.AbstractPipelet#teardown()
     */
    @Override
    public void teardown() {

        dbService = null;
        
        super.teardown();
    }
}
