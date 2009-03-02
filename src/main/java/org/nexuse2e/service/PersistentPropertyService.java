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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.Constants.ParameterType;

/**
 * Stores persistent {@link String} properties into a database table.
 * DB, table, and column names can be customized.
 * 
 * The table has to be created manually. Example SQL:
 * <pre>
 * CREATE TABLE nx_persistent_property (
 *  id INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
 *  namespace VARCHAR(128),
 *  version VARCHAR(128),
 *  name VARCHAR(128),
 *  value VARCHAR(128),
 *  UNIQUE ( namespace, version, name )
 * );
 * </pre>
 * 
 * @author Sebastian Schulze
 * @date 02.09.2008
 */
public class PersistentPropertyService extends DatabaseServiceImpl implements
        PropertyService<String> {
    
    /* ** CONSTANTS ** */
    // parameter names
    private static final String TABLE_PARAM_NAME = "tableName";
    private static final String COLUMN_NAMESPACE_PARAM_NAME = "namespaceColumn";
    private static final String COLUMN_VERSION_PARAM_NAME = "versionColumn";
    private static final String COLUMN_PROPERTY_NAME_PARAM_NAME = "propertyNameColumn";
    private static final String COLUMN_PROPERTY_VALUE_PARAM_NAME = "propertyValueColumn";
    // parameter defaults
    private static final String COLUMN_NAMESPACE_PARAM_DEFAULT = "namespace";
    private static final String COLUMN_VERSION_PARAM_DEFAULT = "version";
    private static final String COLUMN_PROPERTY_NAME_PARAM_DEFAULT = "name";
    private static final String COLUMN_PROPERTY_VALUE_PARAM_DEFAULT = "value";
    
    /* ** INSTANCE VARIABLES ** */
    private String insertQuery;
    private String updateQuery;
    private String selectQuery;
    private String deleteQuery;
    
    /* (non-Javadoc)
     * @see org.nexuse2e.service.DatabaseServiceImpl#fillParameterMap(java.util.Map)
     */
    @Override
    public void fillParameterMap( Map<String, ParameterDescriptor> parameterMap ) {
        super.fillParameterMap( parameterMap );
        parameterMap.put( TABLE_PARAM_NAME,
            new ParameterDescriptor( ParameterType.STRING,
                                     true,
                                     "Table Name",
                                     "Name of the database table where persistent properties should be stored",
                                     "") );
        parameterMap.put( COLUMN_NAMESPACE_PARAM_NAME,
            new ParameterDescriptor( ParameterType.STRING,
                                     true,
                                     "Namespace Colum",
                                     "Name of the namespace column",
                                     COLUMN_NAMESPACE_PARAM_DEFAULT ) );
        parameterMap.put( COLUMN_VERSION_PARAM_NAME,
            new ParameterDescriptor( ParameterType.STRING,
                                     true,
                                     "Version Column",
                                     "Name of the namespace version column",
                                     COLUMN_VERSION_PARAM_DEFAULT ) );
        parameterMap.put( COLUMN_PROPERTY_NAME_PARAM_NAME,
            new ParameterDescriptor( ParameterType.STRING,
                                     true,
                                     "Property Name Column",
                                     "Name of ther property name column",
                                     COLUMN_PROPERTY_NAME_PARAM_DEFAULT ) );
        parameterMap.put( COLUMN_PROPERTY_VALUE_PARAM_NAME,
            new ParameterDescriptor( ParameterType.STRING,
                                     true,
                                     "Property Value Column",
                                     "Name of the property value column",
                                     COLUMN_PROPERTY_VALUE_PARAM_DEFAULT ) );
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.service.DatabaseServiceImpl#initialize(org.nexuse2e.configuration.EngineConfiguration)
     */
    @Override
    public void initialize( EngineConfiguration config )
                                                        throws InstantiationException {
        super.initialize( config );
        
        // get parameter values
        String tableName = getParameter( TABLE_PARAM_NAME );
        String namespaceColumn = getParameter( COLUMN_NAMESPACE_PARAM_NAME );
        String versionColumn = getParameter( COLUMN_VERSION_PARAM_NAME );
        String nameColumn = getParameter( COLUMN_PROPERTY_NAME_PARAM_NAME );
        String valueColumn = getParameter( COLUMN_PROPERTY_VALUE_PARAM_NAME );
        
        if ( tableName != null && tableName.length() > 0
                && namespaceColumn != null && namespaceColumn.length() > 0
                && versionColumn != null && versionColumn.length() > 0
                && nameColumn != null && nameColumn.length() > 0
                && valueColumn != null && valueColumn.length() > 0 ) {
            // build prepared statements
            insertQuery = "INSERT INTO " + tableName
                            + " (" + namespaceColumn + ", " + versionColumn
                            + ", " + nameColumn + ", " + valueColumn + ")"
                            + " VALUES(?, ?, ?, ?)";
            updateQuery = "UPDATE " + tableName
                            + " SET " + valueColumn + " = ?"
                            + " WHERE " + namespaceColumn + " = ?"
                            + " AND " + versionColumn + " = ?"
                            + " AND " + nameColumn + " = ?";
            selectQuery = "SELECT " + valueColumn + " FROM " + tableName
                            + " WHERE " + namespaceColumn + " = ?"
                            + " AND " + versionColumn + " = ?"
                            + " AND " + nameColumn + " = ?";
            deleteQuery = "DELETE FROM " + tableName
                            + " WHERE " + namespaceColumn + " = ?"
                            + " AND " + versionColumn + " = ?"
                            + " AND " + nameColumn + " = ?";  
        } else {
            throw new InstantiationException( "None of the parameters related"
                + " to the properties table must be empty");      
        }
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.service.PropertyService#read(java.lang.String, java.lang.String, java.lang.String)
     */
    public synchronized String read( String namespace,
                        String namespaceVersion,
                        String propertyName ) throws SQLException {
        String value = null;
        
        Connection con = null;
        PreparedStatement stmt = null;
        int origTxIsolation = -1;
        boolean origAutoCommitMode = false;
        
        try {
            con = getDatabaseConnection();
            // preserve settings to restore later
            origTxIsolation = con.getTransactionIsolation();
            origAutoCommitMode = con.getAutoCommit();
            // make connection transactional
            con.setTransactionIsolation( Connection.TRANSACTION_SERIALIZABLE );
            con.setAutoCommit( false );
            stmt = con.prepareStatement( selectQuery );
            stmt.setString( 1, namespace );
            stmt.setString( 2, namespaceVersion );
            stmt.setString( 3, propertyName );
            ResultSet result = stmt.executeQuery(); 
            
            if ( result != null && result.next() ) {
                value = result.getString( 1 );
            }
        } finally {
            if ( stmt != null ) {
                stmt.close();
            }
            if ( con != null ) {
                // restore original settings
                con.setTransactionIsolation( origTxIsolation );
                con.setAutoCommit( origAutoCommitMode );
                releaseDatabaseConnection( con );
            }
        }
        
        return value;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.service.PropertyService#remove(java.lang.String, java.lang.String, java.lang.String)
     */
    public synchronized String remove( String namespace,
                          String namespaceVersion,
                          String propertyName ) throws SQLException {
        String value = null;
        
        Connection con = null;
        PreparedStatement stmt = null;
        int origTxIsolation = -1;
        boolean origAutoCommitMode = false;
        
        try {
            con = getDatabaseConnection();
            // preserve settings to restore later
            origTxIsolation = con.getTransactionIsolation();
            origAutoCommitMode = con.getAutoCommit();
            // make connection transactional
            con.setTransactionIsolation( Connection.TRANSACTION_SERIALIZABLE );
            con.setAutoCommit( false );
            // first get
            stmt = con.prepareStatement( selectQuery );
            stmt.setString( 1, namespace );
            stmt.setString( 2, namespaceVersion );
            stmt.setString( 3, propertyName );
            ResultSet result = stmt.executeQuery(); 
            
            if ( result != null && result.next() ) {
                value = result.getString( 1 );
                stmt.close();
                // then delete
                stmt = con.prepareStatement( deleteQuery );
                stmt.setString( 1, namespace );
                stmt.setString( 2, namespaceVersion );
                stmt.setString( 3, propertyName );
                stmt.executeUpdate(); // result is ignored
            }
            
            con.commit();
        } catch ( SQLException e ) {
            con.rollback();
            throw e;
        } finally {
            if ( stmt != null ) {
                stmt.close();
            }
            if ( con != null ) {
                // restore original settings
                con.setTransactionIsolation( origTxIsolation );
                con.setAutoCommit( origAutoCommitMode );
                releaseDatabaseConnection( con );
            }
        }
        
        return value;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.service.PropertyService#store(java.lang.String, java.lang.String, java.lang.String, java.lang.Object)
     */
    public synchronized void store( String namespace,
                       String namespaceVersion,
                       String propertyName,
                       String value ) throws SQLException {
        Connection con = null;
        PreparedStatement stmt = null;
        int origTxIsolation = -1;
        boolean origAutoCommitMode = false;
        
        try {
            con = getDatabaseConnection();
            // preserve settings to restore later
            origTxIsolation = con.getTransactionIsolation();
            origAutoCommitMode = con.getAutoCommit();
            // make connection transactional
            con.setTransactionIsolation( Connection.TRANSACTION_SERIALIZABLE );
            con.setAutoCommit( false );
            // first get
            stmt = con.prepareStatement( selectQuery );
            stmt.setString( 1, namespace );
            stmt.setString( 2, namespaceVersion );
            stmt.setString( 3, propertyName );
            ResultSet result = stmt.executeQuery(); 
            
            if ( result != null && result.next() ) {
                stmt.close();
                // then update
                stmt = con.prepareStatement( updateQuery );
                stmt.setString( 1, value );
                stmt.setString( 2, namespace );
                stmt.setString( 3, namespaceVersion );
                stmt.setString( 4, propertyName );
                stmt.executeUpdate(); // result is ignored
            } else {
                stmt.close();
                // or insert
                stmt = con.prepareStatement( insertQuery );
                stmt.setString( 1, namespace );
                stmt.setString( 2, namespaceVersion );
                stmt.setString( 3, propertyName );
                stmt.setString( 4, value );
                stmt.executeUpdate(); // result is ignored
            }
            
            con.commit();
        } catch ( SQLException e ) {
            con.rollback();
            throw e;
        } finally {
            if ( stmt != null ) {
                stmt.close();
            }
            if ( con != null ) {
                // restore original settings
                con.setTransactionIsolation( origTxIsolation );
                con.setAutoCommit( origAutoCommitMode );
                releaseDatabaseConnection( con );
            }
        }
    }
}
