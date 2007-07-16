package org.nexuse2e.service.ftp;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.ftpserver.FtpConfigImpl;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.config.PropertiesConfiguration;
import org.apache.ftpserver.ftplet.Configuration;
import org.apache.ftpserver.ftplet.DefaultFtplet;
import org.apache.ftpserver.ftplet.FtpConfig;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.ftplet.FtpResponse;
import org.apache.ftpserver.ftplet.FtpletEnum;
import org.apache.ftpserver.ftplet.User;
import org.apache.ftpserver.interfaces.IFtpConfig;
import org.apache.log4j.Logger;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.Constants.BeanStatus;
import org.nexuse2e.Constants.Runlevel;
import org.nexuse2e.configuration.ConfigurationAccessService;
import org.nexuse2e.configuration.Constants;
import org.nexuse2e.configuration.ListParameter;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.Constants.ParameterType;
import org.nexuse2e.pojo.CertificatePojo;
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
    
    public static final String FTP_ROOT_PARAM_NAME = "ftpRoot";
    public static final String SFTP_PARAM_NAME = "sftp";
    public static final String CLIENT_AUTH_PARAM_NAME = "clientAuthentication";
    public static final String CERTIFICATE_PARAM_NAME = "certificate";
    public static final String FTP_PORT_PARAM_NAME = "ftpPort";
    public static final String SFTP_PORT_PARAM_NAME = "sftpPort";
    
    private TransportReceiver transportReceiver;
    private FtpServer server;

    /* (non-Javadoc)
     * @see org.nexuse2e.service.AbstractService#fillParameterMap(java.util.Map)
     */
    @Override
    public void fillParameterMap( Map<String, ParameterDescriptor> parameterMap ) {
        parameterMap.put( FTP_ROOT_PARAM_NAME, new ParameterDescriptor( ParameterType.STRING, "Root directory",
                "FTP upload root directory", new File( "" ).getAbsolutePath() ) );
        parameterMap.put( SFTP_PARAM_NAME, new ParameterDescriptor( ParameterType.BOOLEAN, "SFTP",
                "Enable secure FTP (encrypted data transfer)", Boolean.TRUE ) );
        parameterMap.put( CLIENT_AUTH_PARAM_NAME, new ParameterDescriptor(
                ParameterType.BOOLEAN, "Client authentication",
                "Require client authentication", Boolean.TRUE ) );
        parameterMap.put( FTP_PORT_PARAM_NAME, new ParameterDescriptor( ParameterType.STRING, "ftpPort",
                "FTP port (default is 21)", "21" ) );
        parameterMap.put( SFTP_PORT_PARAM_NAME, new ParameterDescriptor( ParameterType.STRING, "sftpPort",
                "SFTP port (default is 22)", "22" ) );
        ConfigurationAccessService cas = Engine.getInstance().getActiveConfigurationAccessService();
        try {
            List<CertificatePojo> certs = cas.getCertificates( Constants.CERTIFICATE_TYPE_LOCAL, null );
            ListParameter certsDropdown = new ListParameter();
            if (certs != null) {
                for (CertificatePojo cert : certs) {
                    String label = cert.getName();
                    if (label == null || "".equals( label.trim() )) {
                        label = "Certificate #" + cert.getNxCertificateId();
                    }
                    if (cert.getDescription() != null && !"".equals( cert.getDescription() )) {
                        label += " (" + cert.getDescription() + ")";
                    }
                    certsDropdown.addElement( label, Integer.toString( cert.getNxCertificateId() ) );
                }
            }
            parameterMap.put( CERTIFICATE_PARAM_NAME, new ParameterDescriptor( ParameterType.LIST,
                    "Certificate", "Use this certificate for server authentication", certsDropdown ) );
        } catch (NexusException nex) {
            LOG.error( "Could not retrieve local certificate list", nex );
        }
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
                ListParameter certSel = getParameter( CERTIFICATE_PARAM_NAME );
                Properties properties = new Properties();
                String baseDir = getParameter( FTP_ROOT_PARAM_NAME );
                properties.setProperty( "config.create-default-user", "false" );
                properties.setProperty( "config.connection-manager.anonymous-login-enabled", "false" );
                properties.setProperty( "config.user-manager.class", FtpUserManager.class.getName() );
                properties.setProperty( "config.user-manager.basedir", baseDir );
                properties.setProperty( "config.socket-factory.class", NexusSslSocketFactory.class.getName() );
                properties.setProperty( "config.socket-factory.port", (String) getParameter( FTP_PORT_PARAM_NAME ) );
                if (((Boolean) getParameter( SFTP_PARAM_NAME )).booleanValue()) {
                    properties.setProperty( "config.listeners.default.implicit-ssl", "true" );

                    properties.setProperty( "config.socket-factory.nxssl.port",
                            (String) getParameter( SFTP_PORT_PARAM_NAME ) );
                    String certId = certSel.getSelectedValue();
                    properties.setProperty( "config.socket-factory.nxssl.certificate-id",
                            certId == null ? "" : certId );
                    properties.setProperty( "config.socket-factory.nxssl.ssl-protocol", "TLS" );
                    properties.setProperty( "config.socket-factory.nxssl.client-authentication",
                            getParameter( CLIENT_AUTH_PARAM_NAME ).toString() );

                    properties.setProperty( "config.listeners.default.data-connection.ssl.ssl-protocol", "TLS" );
                    properties.setProperty( "config.listeners.default.data-connection.ssl.client-authentication", "false" );
                }
                
                // configure ftplet
                properties.setProperty( "config.ftplets", "f0" );
                properties.setProperty( "config.ftplet.f0.class", TransactionHandler.class.getName() );
                properties.setProperty( "config.ftplet.f0.basedir", baseDir );
                

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
                super.status = BeanStatus.ERROR;
                LOG.error( "FtpReceiver service could not be started", ex );
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
    
    
    public static class TransactionHandler extends DefaultFtplet {
        
        private String baseDir;
        
        @Override
        public void init( FtpConfig ftpConfig, Configuration config ) throws FtpException {
            baseDir = config.getString( "basedir" );
        }
        
        @Override
        public FtpletEnum onLogin( FtpRequest request, FtpResponse response )
        throws FtpException, IOException {
            
            User user = request.getUser();
            File homeDir = new File( user.getHomeDirectory() );
            if (!homeDir.exists()) {
                boolean succ = homeDir.mkdirs();
                if (!succ) {
                    LOG.error( "Could not create FTP home directory "
                            + homeDir.getAbsolutePath() + " for user " + user.getName() );
                    return FtpletEnum.RET_DISCONNECT;
                }
            }
            return FtpletEnum.RET_DEFAULT;
        }

        @Override
        public FtpletEnum onUploadEnd(
                FtpRequest request, FtpResponse response ) throws FtpException, IOException {

            User user = request.getUser();
            
            File file = new File( new File( baseDir, user.getHomeDirectory() ), request.getArgument() );
            LOG.info( "Received file " + file + " from user " + user.getName() );
            
            return FtpletEnum.RET_DEFAULT;
        }
    }
}
