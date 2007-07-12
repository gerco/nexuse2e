package org.nexuse2e.service.ftp;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.LogFactory;
import org.apache.ftpserver.FtpConfigImpl;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.config.PropertiesConfiguration;
import org.apache.ftpserver.ftplet.Configuration;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.User;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.interfaces.IFtpConfig;
import org.apache.ftpserver.usermanager.BaseUser;
import org.apache.log4j.Logger;
import org.nexuse2e.Constants.BeanStatus;
import org.nexuse2e.Constants.Runlevel;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.service.AbstractService;
import org.nexuse2e.service.ReceiverAware;
import org.nexuse2e.transport.TransportReceiver;

/**
 * Created: 12.07.2007
 * <p>
 * Service implementation for receiving via the FTP protocol.
 * </p>
 *
 * @author jonas.reese
 * @version $LastChangedRevision$ - $LastChangedDate$ by $LastChangedBy$
 */
public class FtpReceiverService extends AbstractService implements ReceiverAware {
    
    private static Logger LOG = Logger.getLogger( FtpReceiverService.class );
    
    private TransportReceiver transportReceiver;
    private FtpServer server;

    /* (non-Javadoc)
     * @see org.nexuse2e.service.AbstractService#fillParameterMap(java.util.Map)
     */
    @Override
    public void fillParameterMap( Map<String, ParameterDescriptor> parameterMap ) {
        
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.service.AbstractService#getActivationRunlevel()
     */
    @Override
    public Runlevel getActivationRunlevel() {
        return Runlevel.INBOUND_PIPELINES;
    }

    @Override
    public void start() {
        if ( getStatus().getValue() < BeanStatus.STARTED.getValue() ) {
            try {
                Properties properties = new Properties();
                properties.setProperty( "config.create-default-user", "false" );
                properties.setProperty( "config.connection-manager.anonymous-login-enabled", "false" );
                properties.setProperty( "config.user-manager.class", FtpUserManager.class.getName() );
                
                // get the configuration object
                Configuration config = new PropertiesConfiguration( properties );
        
                // create servce context
                IFtpConfig ftpConfig = new FtpConfigImpl( config );
        
                // create the server object and start it
                server = new FtpServer( ftpConfig );
                server.start();
                
                super.start();
                LOG.debug( "FtpReceiver service started" );
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void stop() {
        if ( getStatus() == BeanStatus.STARTED ) {
            if ( server != null ) {
                server.stop();
            }
            LOG.debug( "FtpReceiver service stopped" );
            super.stop();
        }
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.service.ReceiverAware#getTransportReceiver()
     */
    public TransportReceiver getTransportReceiver() {
        return transportReceiver;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.service.ReceiverAware#setTransportReceiver(org.nexuse2e.transport.TransportReceiver)
     */
    public void setTransportReceiver( TransportReceiver transportReceiver ) {
        this.transportReceiver = transportReceiver;
    }
    
    public static class FtpUserManager implements UserManager {
        
        private BaseUser defaultUser;
        
        public FtpUserManager() {
            defaultUser = new BaseUser();
            defaultUser.setEnabled( true );
            defaultUser.setHomeDirectory( "./" );
            defaultUser.setName( "nexus" );
            defaultUser.setPassword( "nexus" );
        }
        
        public boolean authenticate( String login, String password ) throws FtpException {
            boolean auth = defaultUser.getName().equals( login ) && defaultUser.getPassword().equals( password );
            if (auth) {
                LOG.debug( "User " + login + " successfully authenticated" );
            } else {
                LOG.error( "User authentication failed for " + login );
            }
            return auth;
        }

        public void delete( String login ) throws FtpException {
        }

        public boolean doesExist( String login ) throws FtpException {
            return defaultUser.getName().equals( login );
        }

        public String getAdminName() throws FtpException {
            return null;
        }

        public Collection getAllUserNames() throws FtpException {
            return Collections.singletonList( defaultUser.getName() );
        }

        /* (non-Javadoc)
         * @see org.apache.ftpserver.ftplet.UserManager#getUserByName(java.lang.String)
         */
        public User getUserByName( String login ) throws FtpException {
            return defaultUser.getName().equals( login ) ? defaultUser : null;
        }

        public boolean isAdmin( String login ) throws FtpException {
            return false;
        }

        /* (non-Javadoc)
         * @see org.apache.ftpserver.ftplet.UserManager#save(org.apache.ftpserver.ftplet.User)
         */
        public void save( User user ) throws FtpException {
        }

        /* (non-Javadoc)
         * @see org.apache.ftpserver.ftplet.Component#configure(org.apache.ftpserver.ftplet.Configuration)
         */
        public void configure( Configuration conf ) throws FtpException {
        }

        /* (non-Javadoc)
         * @see org.apache.ftpserver.ftplet.Component#dispose()
         */
        public void dispose() {
        }

        /* (non-Javadoc)
         * @see org.apache.ftpserver.ftplet.Component#setLogFactory(org.apache.commons.logging.LogFactory)
         */
        public void setLogFactory( LogFactory logFactory ) {
        }
    }
}
