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
package org.nexuse2e.pojo;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.UserType;

public class BinaryBlobType implements UserType {

    private static final String   ORACLE_DRIVER_NAME            = "Oracle JDBC driver";
    private static final int      ORACLE9_DRIVER_MAJOR_VERSION  = 9;
    private static final int      ORACLE10_DRIVER_MAJOR_VERSION = 10;
    private static final int      ORACLE_DRIVER_MINOR_VERSION   = 0;

    private static final String   ISERIES_DRIVER_NAME           = "AS/400 Toolbox for Java JDBC Driver";

    private static final String   MICROSOFT_DRIVER_NAME         = "SQLServer";
    private static final String   MYSQL_DRIVER_NAME             = "MySQL-AB JDBC Driver";

    private Map<String, Class<?>> map                           = new HashMap<String, Class<?>>();

    public BinaryBlobType() {

        map.put( "BLOB", Blob.class );
        map.put( "LONGVARBINARY", byte[].class );
    }

    public int[] sqlTypes() {

        return new int[] { Types.BLOB};
    } // sqlTypes

    public Class<?> returnedClass() {

        return byte[].class;
    } // returnedClass

    public boolean equals( Object x, Object y ) {

        return ( x == y ) || ( x != null && y != null && java.util.Arrays.equals( (byte[]) x, (byte[]) y ) );
    } // equals

    public Object nullSafeGet( ResultSet rs, String[] names, Object owner ) throws HibernateException, SQLException {

        byte[] result = null;

        try {
            Object object = rs.getObject( names[0], map );
            if ( object instanceof Blob ) {
                result = ( (Blob) object ).getBytes( 1, (int) ( (Blob) object ).length() );
            } else if ( object instanceof byte[] ) {
                result = (byte[]) object;
            } else if ( object != null ) {
                // LOG.trace( "nullSafeGet -class: " + object.getClass() );
            }
        } catch ( Exception ex ) {
            final Blob blob = rs.getBlob( names[0] );
            if ( blob == null || blob.length() == 0 ) {
                return null;
            }
            return blob.getBytes( 1, (int) blob.length() );
        }

        return result;
    } // nullSafeGet

    public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {

        DatabaseMetaData dbMetaData = st.getConnection().getMetaData();

        // LOG.trace( "Driver: " + dbMetaData.getDriverName() );

        if ( value != null ) {
            if ( ORACLE_DRIVER_NAME.equalsIgnoreCase( dbMetaData.getDriverName() ) ) {

                if ( ( dbMetaData.getDriverMajorVersion() == ORACLE9_DRIVER_MAJOR_VERSION )
                        && ( dbMetaData.getDriverMinorVersion() >= ORACLE_DRIVER_MINOR_VERSION ) ) {
                    try {
                        // Code compliments of Scott Miller
                        // support oracle blobs without requiring oracle libraries
                        // at compile time
                        // Note this assumes that if you are using the Oracle Driver.
                        // then you have access to the oracle.sql.BLOB class

                        // First get the oracle blob class
                        Class<?> oracleBlobClass = Class.forName( "oracle.sql.BLOB" );

                        // Get the oracle connection class for checking
                        Class<?> oracleConnectionClass = Class.forName( "oracle.jdbc.OracleConnection" );

                        // now get the static factory method
                        Class<?> partypes[] = new Class[3];
                        partypes[0] = Connection.class;
                        partypes[1] = Boolean.TYPE;
                        partypes[2] = Integer.TYPE;
                        Method createTemporaryMethod = oracleBlobClass.getDeclaredMethod( "createTemporary", partypes );
                        // now get ready to call the factory method
                        Field durationSessionField = oracleBlobClass.getField( "DURATION_SESSION" );
                        Object arglist[] = new Object[3];
                        Connection conn = st.getConnection();

                        // Unwrap pooled connection
                        if ( conn instanceof org.apache.commons.dbcp.PoolableConnection ) {
                            conn = ( (org.apache.commons.dbcp.PoolableConnection) conn ).getDelegate();
                        }

                        // Make sure connection object is right type
                        if ( !oracleConnectionClass.isAssignableFrom( conn.getClass() ) ) {
                            throw new HibernateException(
                                    "JDBC connection object must be a oracle.jdbc.OracleConnection. "
                                            + "Connection class is " + conn.getClass().getName() );
                        }

                        arglist[0] = conn;
                        arglist[1] = Boolean.TRUE;
                        arglist[2] = durationSessionField.get( null ); //null is valid because of static field

                        // Create our BLOB
                        Object tempBlob = createTemporaryMethod.invoke( null, arglist ); //null is valid because of static method

                        // get the open method
                        partypes = new Class[1];
                        partypes[0] = Integer.TYPE;
                        Method openMethod = oracleBlobClass.getDeclaredMethod( "open", partypes );

                        // prepare to call the method
                        Field modeReadWriteField = oracleBlobClass.getField( "MODE_READWRITE" );
                        arglist = new Object[1];
                        arglist[0] = modeReadWriteField.get( null ); //null is valid because of static field

                        // call open(BLOB.MODE_READWRITE);
                        openMethod.invoke( tempBlob, arglist );

                        // get the getCharacterOutputStream method
                        Method getBinaryOutputStreamMethod = oracleBlobClass.getDeclaredMethod(
                                "getBinaryOutputStream", (Class[]) null );

                        // call the getBinaryOutputStream method
                        OutputStream out = (OutputStream) getBinaryOutputStreamMethod
                                .invoke( tempBlob, (Object[]) null );

                        // write the data to the blob
                        out.write( (byte[]) value );
                        out.flush();
                        out.close();

                        // get the close method
                        Method closeMethod = oracleBlobClass.getDeclaredMethod( "close", (Class[]) null );

                        // call the close method
                        closeMethod.invoke( tempBlob, (Object[]) null );

                        // add the blob to the statement
                        st.setBlob( index, (Blob) tempBlob );
                    } catch ( ClassNotFoundException e ) {
                        // could not find the class with reflection
                        throw new HibernateException( "Unable to find a required class.\n" + e.getMessage() );
                    } catch ( NoSuchMethodException e ) {
                        // could not find the metho with reflection
                        throw new HibernateException( "Unable to find a required method.\n" + e.getMessage() );
                    } catch ( NoSuchFieldException e ) {
                        // could not find the field with reflection
                        throw new HibernateException( "Unable to find a required field.\n" + e.getMessage() );
                    } catch ( IllegalAccessException e ) {
                        throw new HibernateException( "Unable to access a required method or field.\n" + e.getMessage() );
                    } catch ( InvocationTargetException e ) {
                        throw new HibernateException( e.getMessage() );
                    } catch ( IOException e ) {
                        throw new HibernateException( e.getMessage() );
                    }
                } else if ( ( dbMetaData.getDriverMajorVersion() == ORACLE10_DRIVER_MAJOR_VERSION )
                        && ( dbMetaData.getDriverMinorVersion() >= ORACLE_DRIVER_MINOR_VERSION ) ) {
                    st.setBytes( index, (byte[]) value );
                } else {
                    throw new HibernateException( "No BLOBS support. Use Oracle driver version "
                            + ORACLE9_DRIVER_MAJOR_VERSION + ", minor " + ORACLE_DRIVER_MINOR_VERSION + " or higher!" );
                }
            } else if ( ISERIES_DRIVER_NAME.equalsIgnoreCase( dbMetaData.getDriverName() )
                    || MICROSOFT_DRIVER_NAME.equalsIgnoreCase( dbMetaData.getDriverName() ) ) {
                st.setBytes( index, (byte[]) value );
            } else if ( MYSQL_DRIVER_NAME.equalsIgnoreCase( dbMetaData.getDriverName() ) ) {
                // LOG.trace( "MySQL..." );
                st.setBytes( index, (byte[]) value );
            } else {
                st.setBlob(index, Hibernate.getLobCreator(session).createBlob((byte[]) value));
            }

            /*
             if ( st instanceof org.apache.commons.dbcp.DelegatingPreparedStatement
             && ( (org.apache.commons.dbcp.DelegatingPreparedStatement) st ).getDelegate() instanceof oracle.jdbc.OraclePreparedStatement ) {

             BLOB blob = BLOB.createTemporary( ( (org.apache.commons.dbcp.PoolableConnection) st.getConnection() )
             .getDelegate(), false, BLOB.DURATION_SESSION );

             blob.open( BLOB.MODE_READWRITE );

             OutputStream out = blob.getBinaryOutputStream();

             try {
             out.write( (byte[]) value );
             out.flush();
             out.close();
             } catch ( IOException e ) {
             throw new SQLException( "failed write to blob" + e.getMessage() );
             }

             blob.close();

             ( (oracle.jdbc.OraclePreparedStatement) ( (org.apache.commons.dbcp.DelegatingPreparedStatement) st )
             .getDelegate() ).setBLOB( index, blob );
             } else if ( st instanceof oracle.jdbc.OraclePreparedStatement ) {
             BLOB blob = BLOB.createTemporary( st.getConnection(), false, BLOB.DURATION_SESSION );

             blob.open( BLOB.MODE_READWRITE );

             OutputStream out = blob.getBinaryOutputStream();

             try {
             out.write( (byte[]) value );
             out.flush();
             out.close();
             } catch ( IOException e ) {
             throw new SQLException( "failed write to blob" + e.getMessage() );
             }

             blob.close();

             ( (oracle.jdbc.OraclePreparedStatement) ( st ) ).setBLOB( index, blob );
             } else if ( st.getClass().getName().equalsIgnoreCase( "com.ibm.as400.access.AS400JDBCPreparedStatement" )
             || st.getClass().getName().equalsIgnoreCase( "com.mysql.jdbc.PreparedStatement" ) ) {
             st.setBytes( index, (byte[]) value );

             } else if ( st instanceof org.apache.tomcat.dbcp.dbcp.DelegatingPreparedStatement
             && ( (org.apache.tomcat.dbcp.dbcp.DelegatingPreparedStatement) st ).getDelegate() instanceof com.microsoft.jdbc.base.BasePreparedStatement ) {
             st.setBytes( index, (byte[]) value );
             } else {
             st.setBlob( index, Hibernate.createBlob( (byte[]) value ) );
             }
             */
        } else {
            // Could be a problem with DBs other than Derby, needs testing...
            st.setNull( index, Types.BLOB );

            // Use this in case of problems
            // st.setNull( index, Types.BINARY );
        }
    } // nullSafeSet

    public Object deepCopy( Object value ) {

        if ( value == null )
            return null;

        byte[] bytes = (byte[]) value;
        byte[] result = new byte[bytes.length];
        System.arraycopy( bytes, 0, result, 0, bytes.length );

        return result;
    } // deepCopy    

    public boolean isMutable() {

        return true;
    } // isMutable

    public int hashCode( Object x ) throws HibernateException {

        return x.hashCode();
    }

    public Object assemble( Serializable cached, Object owner ) throws HibernateException {

        return cached;
    }

    public Serializable disassemble( Object value ) throws HibernateException {

        return (Serializable) value;
    }

    public Object replace( Object original, Object target, Object owner ) throws HibernateException {

        return original;
    }

	@Override
	public Object nullSafeGet(ResultSet rs, String[] names,	SessionImplementor session, Object owner) throws HibernateException, SQLException {
		// TODO Auto-generated method stub
		return null;
	}
} // BinaryBlobType