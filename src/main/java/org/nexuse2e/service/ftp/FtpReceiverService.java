package org.nexuse2e.service.ftp;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
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
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.configuration.ListParameter;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.Constants.ParameterType;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.pojo.CertificatePojo;
import org.nexuse2e.pojo.ServicePojo;
import org.nexuse2e.service.AbstractService;
import org.nexuse2e.service.ReceiverAware;
import org.nexuse2e.service.Service;
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
    public static final String FTP_ERROR_DIR_PARAM_NAME = "errorDir";
    public static final String FTP_TYPE_PARAM_NAME = "ftpType";
    public static final String CLIENT_AUTH_PARAM_NAME = "clientAuthentication";
    public static final String CERTIFICATE_PARAM_NAME = "certificate";
    public static final String FTP_PORT_PARAM_NAME = "ftpPort";
    public static final String SFTP_PORT_PARAM_NAME = "sftpPort";
    
    private TransportReceiver transportReceiver;
    private FtpServer server;
    private ListParameter certsDropdown;

    /* (non-Javadoc)
     * @see org.nexuse2e.service.AbstractService#fillParameterMap(java.util.Map)
     */
    @Override
    public void fillParameterMap( Map<String, ParameterDescriptor> parameterMap ) {
        parameterMap.put( FTP_ROOT_PARAM_NAME, new ParameterDescriptor( ParameterType.STRING, "Root directory",
                "FTP upload root directory", new File( "" ).getAbsolutePath() ) );
        parameterMap.put( FTP_ERROR_DIR_PARAM_NAME, new ParameterDescriptor( ParameterType.STRING, "Error directory",
                "Directory where files are stored if an error occurs", new File( "" ).getAbsolutePath() ) );
        ListParameter ftpTypeDrowdown = new ListParameter();
        ftpTypeDrowdown.addElement( "SFTP (encrypted)", "sftp" );
        ftpTypeDrowdown.addElement( "Plain FTP (not encrypted)", "ftp" );
        parameterMap.put( FTP_TYPE_PARAM_NAME, new ParameterDescriptor( ParameterType.LIST,
                "FTP type", "FTP type", ftpTypeDrowdown ) );
        parameterMap.put( CLIENT_AUTH_PARAM_NAME, new ParameterDescriptor(
                ParameterType.BOOLEAN, "Client authentication",
                "Require client authentication", Boolean.TRUE ) );

        parameterMap.put( FTP_PORT_PARAM_NAME, new ParameterDescriptor( ParameterType.STRING, "FTP Port",
                "FTP port (default is 21)", "21" ) );
        parameterMap.put( SFTP_PORT_PARAM_NAME, new ParameterDescriptor( ParameterType.STRING, "SFTP Port",
                "SFTP port (default is 22)", "22" ) );
        certsDropdown = new ListParameter();
        addCertificatesToDropdown();
        parameterMap.put( CERTIFICATE_PARAM_NAME, new ParameterDescriptor( ParameterType.LIST,
                "Server certificate", "Use this certificate for server authentication", certsDropdown ) );
    }
    
    private void addCertificatesToDropdown() {
        ConfigurationAccessService cas = Engine.getInstance().getActiveConfigurationAccessService();
        try {
            List<CertificatePojo> certs = cas.getCertificates( Constants.CERTIFICATE_TYPE_LOCAL, null );
            if (certs != null) {
                for (CertificatePojo cert : certs) {
                    String label = cert.getName();
                    if (label == null || "".equals( label.trim() )) {
                        label = "Certificate #" + cert.getNxCertificateId();
                    }
                    if (cert.getDescription() != null && !"".equals( cert.getDescription() )) {
                        label += " (" + cert.getDescription() + ")";
                    }
                    String value = Integer.toString( cert.getNxCertificateId() );
                    if (certsDropdown.getElement( value ) == null) {
                        certsDropdown.addElement( label, value );
                    }
                }
            }
        } catch (NexusException nex) {
            LOG.error( "Could not retrieve local certificate list", nex );
        }
    }
    
    @Override
    public void initialize( EngineConfiguration configuration ) {
        super.initialize( configuration );
        addCertificatesToDropdown();
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
        
        // find service name
        ConfigurationAccessService cas = Engine.getInstance().getActiveConfigurationAccessService();
        List<ServicePojo> services = cas.getServices();
        String serviceName = null;
        for (ServicePojo service : services) {
            Service serviceInstance = cas.getService( service.getName() );
            if (serviceInstance == this) {
                // found service name
                serviceName = service.getName();
            }
        }
        LOG.info( "Found service name: " + serviceName );
        
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
                ListParameter ftpTypeSel = getParameter( FTP_TYPE_PARAM_NAME );
                if (!"ftp".equals( ftpTypeSel.getSelectedValue())) {
                    properties.setProperty( "config.listeners.default.implicit-ssl", "true" );

                    properties.setProperty( "config.socket-factory.nxssl.port",
                            (String) getParameter( SFTP_PORT_PARAM_NAME ) );
                    String certId = certSel.getSelectedValue();
                    properties.setProperty( "config.socket-factory.nxssl.certificate-id",
                            certId == null ? "" : certId );
                    properties.setProperty( "config.socket-factory.nxssl.ssl-protocol", "TLS" );
                    properties.setProperty( "config.socket-factory.nxssl.client-authentication",
                            (String) getParameter( CLIENT_AUTH_PARAM_NAME ) );

                    properties.setProperty( "config.listeners.default.data-connection.ssl.ssl-protocol", "TLS" );
                    properties.setProperty( "config.listeners.default.data-connection.ssl.client-authentication", "false" );
                }
                
                // configure ftplet
                properties.setProperty( "config.ftplets", "f0" );
                properties.setProperty( "config.ftplet.f0.class", TransactionHandler.class.getName() );
                properties.setProperty( "config.ftplet.f0.serviceName", serviceName );
                properties.setProperty( "config.ftplet.f0.errorDir",
                        (String) getParameter( FTP_ERROR_DIR_PARAM_NAME ) );
                properties.setProperty( "config.ftplet.f0.errorDir",
                        (String) getParameter( FTP_ERROR_DIR_PARAM_NAME ) );
                

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

    public TransportReceiver getTransportReceiver() {
        return transportReceiver;
    }

    public void setTransportReceiver( TransportReceiver transportReceiver ) {
        this.transportReceiver = transportReceiver;
    }

    /**
     * Process any files found.
     * @param file File found to be processed.
     * @param errorDir The error directory.
     * @param partnerId The partner ID.
     * @param transportReceiver The <code>TransportReceiver</code> to dispatch to.
     */
    private static void processFile(
            File file, File errorDir, String partnerId, TransportReceiver transportReceiver ) {

        if (transportReceiver != null && file != null && file.exists() && file.length() != 0) {
            try {
                // Open the file to read one line at a time
                byte[] fileBuffer = FileUtils.readFileToByteArray( file );

                ConfigurationAccessService cas = Engine.getInstance().getActiveConfigurationAccessService();
                
                MessageContext messageContext = new MessageContext();
                messageContext.setData( fileBuffer );
                messageContext.setCommunicationPartner( cas.getPartnerByPartnerId( partnerId ) );
                transportReceiver.processInboundData( messageContext );
                
                file.delete();
            } catch ( Exception ex ) {
                LOG.error( "An error occurred while processing file " + file, ex );
                try {
                    FileUtils.copyFileToDirectory( file, errorDir );
                    file.delete();
                } catch (IOException ioex) {
                    LOG.error( "Could not copy file " + file + " to error directory " + errorDir, ioex );
                }
            }
        }
    }
    
    
    public static class TransactionHandler extends DefaultFtplet {
        
        private File errorDir;
        private String serviceName;
        
        @Override
        public void init( FtpConfig ftpConfig, Configuration config ) throws FtpException {
            errorDir = new File( config.getString( "errorDir"  ) );
            serviceName = config.getString( "serviceName" );
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
            
            File file = new File( user.getHomeDirectory(), request.getArgument() );
            LOG.info( "Received file " + file + " from user " + user.getName() );
            
            ConfigurationAccessService cas = Engine.getInstance().getActiveConfigurationAccessService();
            FtpReceiverService service = (FtpReceiverService) cas.getService( serviceName );
            processFile( file, errorDir, user.getName(), service.getTransportReceiver() );
            
            return FtpletEnum.RET_DEFAULT;
        }
    }
}
