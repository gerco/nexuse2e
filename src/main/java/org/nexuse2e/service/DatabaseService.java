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
import java.sql.SQLException;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;
import org.nexuse2e.Constants.Runlevel;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.configuration.ListParameter;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.Constants.ParameterType;

public class DatabaseService extends AbstractService {

    private static Logger LOG               = Logger.getLogger( DatabaseService.class );
    
    public static final String EXTERNALDATASOURCE = "externaldatasource";
    public static final String DATASOURCEID       = "datasourceid";
    public static final String CONNECTIONURL      = "url";
    public static final String USER               = "user";
    public static final String PASSWORD           = "password";
    public static final String DRIVERCLASSNAME    = "driverclassname";
    public static final String PROPERTIES         = "properties";
    public static final String AUTOCOMMIT         = "autocommit";
    public static final String READONLY           = "readonly";
    public static final String ISOLATIONLEVEL     = "isolationlevel";
    
    private DataSource    datasource        = null;
    
    /**
     * Used for debug issues
     */
    private int           connectionCounter = 0;

    /* (non-Javadoc)
     * @see org.nexuse2e.service.AbstractService#start()
     */
    @Override
    public void start() {

        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName( (String) getParameter( DRIVERCLASSNAME ) );
        ds.setUsername( (String) getParameter( USER ) );
        ds.setPassword( (String) getParameter( PASSWORD ) );
        ds.setUrl( (String) getParameter( CONNECTIONURL ) );

        datasource = ds;
        super.start();
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.service.AbstractService#stop()
     */
    @Override
    public void stop() {

        datasource = null;
        super.stop();
    }

    /**
     * @return
     * @throws SQLException
     */
    public Connection getDatabaseConnection() throws SQLException {

        if ( datasource != null ) {
            connectionCounter++;
            Connection connection = datasource.getConnection();
            LOG.trace( "connection requested ("+connectionCounter+"): "+connection );
            return connection;
        }
        return null;
    }

    /**
     * @param connection
     * @throws SQLException
     */
    public void releaseDatabaseConnection( Connection connection ) throws SQLException {

        connectionCounter--;
        LOG.trace( "connection released ("+connectionCounter+"): "+connection );
        connection.close();
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.service.AbstractService#initialize(org.nexuse2e.configuration.EngineConfiguration)
     */
    @Override
    public void initialize( EngineConfiguration config ) {

        // TODO Auto-generated method stub
        super.initialize( config );
    }

    @Override
    public void fillParameterMap( Map<String, ParameterDescriptor> parameterMap ) {

        parameterMap.put( EXTERNALDATASOURCE, new ParameterDescriptor( ParameterType.BOOLEAN,
                "Use External DataSource", "Use external configured datasource, or use local connection pool instead.",
                Boolean.FALSE ) );
        parameterMap.put( DATASOURCEID, new ParameterDescriptor( ParameterType.STRING, "DataSource Logical ID",
                "the externally configured logical datasource id", "" ) );
        parameterMap.put( CONNECTIONURL, new ParameterDescriptor( ParameterType.STRING, "Connection URL",
                "URL used to establish database connections", "" ) );
        parameterMap.put( USER, new ParameterDescriptor( ParameterType.STRING, "Username", "Database Username", "" ) );
        parameterMap.put( PASSWORD, new ParameterDescriptor( ParameterType.PASSWORD, "Password", "", "" ) );
        parameterMap.put( DRIVERCLASSNAME, new ParameterDescriptor( ParameterType.STRING, "Driver Classname",
                "Full qualified class name of the JDBC driver to be used", "" ) );
        parameterMap
                .put(
                        PROPERTIES,
                        new ParameterDescriptor(
                                ParameterType.STRING,
                                "Connection Properties",
                                "Additional Connection Properties that will be send to your JDBC Driver when establishing new Connections",
                                "" ) );
        parameterMap.put( AUTOCOMMIT, new ParameterDescriptor( ParameterType.BOOLEAN, "AutoCommit",
                "The autocommit state of connections", Boolean.TRUE ) );
        parameterMap.put( READONLY, new ParameterDescriptor( ParameterType.BOOLEAN, "ReadOnly",
                "The ReadOnly state of connections", Boolean.FALSE ) );

        ListParameter isolationLevelDropdown = new ListParameter();
        isolationLevelDropdown.addElement( "Driver", "driver" );
        isolationLevelDropdown.addElement( "None", "none" );
        isolationLevelDropdown.addElement( "Read Committed", "readcommitted" );
        isolationLevelDropdown.addElement( "Read UnCommitted", "readuncommitted" );
        isolationLevelDropdown.addElement( "Repeatable Read", "repeatableread" );
        isolationLevelDropdown.addElement( "Serializable", "serializable" );
        isolationLevelDropdown.setSelectedIndex( 0 ); // default ist driver

        parameterMap.put( ISOLATIONLEVEL, new ParameterDescriptor( ParameterType.LIST, "Isolation Level",
                "The default transaction isolation state of connections", isolationLevelDropdown ) );

    }

    @Override
    public Runlevel getActivationRunlevel() {

        return Runlevel.OUTBOUND_PIPELINES;
    }

    /**
     * @return the datasource
     */
    public DataSource getDatasource() {

        return datasource;
    }

    /**
     * @param datasource the datasource to set
     */
    public void setDatasource( DataSource datasource ) {

        this.datasource = datasource;
    }

    /**
     * @return the connectionCounter
     */
    public int getConnectionCounter() {

        return connectionCounter;
    }

    /**
     * @param connectionCounter the connectionCounter to set
     */
    public void setConnectionCounter( int connectionCounter ) {

        this.connectionCounter = connectionCounter;
    }

}
