package org.nexuse2e.service.ftp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
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
import org.nexuse2e.pojo.MessagePojo;
import org.nexuse2e.service.AbstractService;
import org.nexuse2e.service.ReceiverAware;
import org.nexuse2e.service.SchedulerClient;
import org.nexuse2e.service.SchedulingService;
import org.nexuse2e.service.Service;
import org.nexuse2e.transport.TransportReceiver;

/**
 * This service implementation acts as an (S)FTP client and receives files by
 * polling an FTP server from time to time.
 *
 * @author jonas.reese
 * @version $LastChangedRevision$ - $LastChangedDate$ by $LastChangedBy$
 */
public class FtpPollingReceiverService extends AbstractService implements ReceiverAware {

    private static Logger LOG = Logger.getLogger( FtpReceiverService.class );

    public static final String PARTNER_PARAM_NAME = "partnerId";
    public static final String DOWNLOAD_DIR_PARAM_NAME = "downloadDir";
    public static final String ERROR_DIR_PARAM_NAME = "errorDir";
    public static final String FTP_TYPE_PARAM_NAME = "ftpType";
    public static final String CERTIFICATE_PARAM_NAME = "certificate";
    public static final String URL_PARAM_NAME = "url";
    public static final String USER_PARAM_NAME = "username";
    public static final String PASSWORD_PARAM_NAME = "password";
    public static final String INTERVAL_PARAM_NAME = "interval";
    public static final String ASCII_PARAM_NAME = "ascii";

    private TransportReceiver transportReceiver;
    private ListParameter certsDropdown;
    private SchedulingService schedulingService;
    private SchedulerClient schedulerClient;
    
    public FtpPollingReceiverService() {
        schedulerClient = new FtpSchedulerClient();
    }
    
    /* (non-Javadoc)
     * @see org.nexuse2e.service.AbstractService#fillParameterMap(java.util.Map)
     */
    @Override
    public void fillParameterMap( Map<String, ParameterDescriptor> parameterMap ) {

        parameterMap.put( PARTNER_PARAM_NAME, new ParameterDescriptor( ParameterType.STRING, "Partner",
                "The partner ID", "" ) );
        
        
        parameterMap.put( DOWNLOAD_DIR_PARAM_NAME, new ParameterDescriptor( ParameterType.STRING, "Download directory",
                "FTP download directory", new File( "" ).getAbsolutePath() ) );
        parameterMap.put( ERROR_DIR_PARAM_NAME, new ParameterDescriptor( ParameterType.STRING, "Error directory",
                "Directory where files are stored if an error occurs", new File( "" ).getAbsolutePath() ) );
        ListParameter ftpTypeDrowdown = new ListParameter();
        ftpTypeDrowdown.addElement( "SFTP (encrypted)", "sftp" );
        ftpTypeDrowdown.addElement( "Plain FTP (not encrypted)", "ftp" );
        parameterMap.put( FTP_TYPE_PARAM_NAME, new ParameterDescriptor( ParameterType.LIST,
                "FTP type", "FTP type", ftpTypeDrowdown ) );

        parameterMap.put( URL_PARAM_NAME, new ParameterDescriptor( ParameterType.STRING, "URL",
                "Polling URL (use ftp://host.com:[port]/dir/subdir format)", "" ) );

        parameterMap.put( USER_PARAM_NAME, new ParameterDescriptor( ParameterType.STRING, "User name",
                "The FTP user name", "anonymous" ) );

        parameterMap.put( PASSWORD_PARAM_NAME, new ParameterDescriptor( ParameterType.PASSWORD, "Password",
                "The FTP password", "" ) );

        parameterMap.put( INTERVAL_PARAM_NAME, new ParameterDescriptor( ParameterType.STRING, "Interval (minutes)",
                "Connect every n minutes to look for new files", "5" ) );

        parameterMap.put( ASCII_PARAM_NAME, new ParameterDescriptor( ParameterType.BOOLEAN, "ASCII",
                "Use ASCII transfer mode (otherwise transfer mode is binary)", Boolean.FALSE ) );
        
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
    
    @Override
    public void start() {

        try {
            ConfigurationAccessService cas = Engine.getInstance().getActiveConfigurationAccessService();
            schedulingService = null;
            for (Service service : cas.getServiceInstances()) {
                if (service instanceof SchedulingService) {
                    schedulingService = (SchedulingService) service;
                }
            }
            if (schedulingService == null) {
                throw new NexusException( "No ScheduleService implementation found" );
            }
            
            int interval = Integer.parseInt( (String) getParameter( INTERVAL_PARAM_NAME ) ) * 60000;
            schedulingService.registerClient( schedulerClient, interval );
            
            super.start();
            LOG.debug( "FtpPollingReceiver service started" );
        } catch (Exception ex) {
            LOG.error( "FtpPollingReceiver service could not be started", ex );
            status = BeanStatus.ERROR;
        }
    }

    @Override
    public void stop() {
        if ( getStatus() == BeanStatus.STARTED ) {
            if (schedulingService != null) {
                schedulingService.deregisterClient( schedulerClient );
            }
            LOG.debug( "FtpPollingReceiver service stopped" );
            super.stop();
        }
    }

    @Override
    public Runlevel getActivationRunlevel() {
         return Runlevel.INBOUND_PIPELINES;
    }

    public TransportReceiver getTransportReceiver() {
        return transportReceiver;
    }

    public void setTransportReceiver( TransportReceiver transportReceiver ) {
        this.transportReceiver = transportReceiver;
    }
    
    private static int counter = 0;
    
    /**
     * Process any files found.
     * @param file File found to be processed.
     * @param errorDir The error directory.
     * @param partnerId The partner ID.
     */
    private void processFile(
            File file, File errorDir, String partnerId ) {

        counter++;
        
        if (transportReceiver != null && file != null && file.exists() && file.length() != 0) {
            try {
                // Open the file to read one line at a time
                byte[] fileBuffer = FileUtils.readFileToByteArray( file );

                ConfigurationAccessService cas = Engine.getInstance().getActiveConfigurationAccessService();
                
                MessageContext messageContext = new MessageContext();
                messageContext.setData( fileBuffer );
                messageContext.setCommunicationPartner( cas.getPartnerByPartnerId( partnerId ) );
                messageContext.setMessagePojo( new MessagePojo() );
                messageContext.setOriginalMessagePojo( messageContext.getMessagePojo() );
                messageContext.getMessagePojo().setCustomParameters( new HashMap<String, String>() );
                transportReceiver.processMessage( messageContext );
                
                file.delete();
            } catch ( Exception ex ) {
                LOG.error( "An error occurred while processing file " + file, ex );
                try {
                    String postfix = "_" + System.currentTimeMillis() + "_" + counter;
                    FileUtils.copyFile(
                            file, new File( errorDir, file.getName() + postfix ) );
                    file.delete();
                } catch (IOException ioex) {
                    LOG.error( "Could not copy file " + file + " to error directory " + errorDir, ioex );
                }
            }
        }
    }

    class FtpSchedulerClient implements SchedulerClient {
        public void scheduleNotify() {
            
            boolean ssl = false;
            ListParameter ftpTypeSel = getParameter( FTP_TYPE_PARAM_NAME );
            if (!"ftp".equals( ftpTypeSel.getSelectedValue())) {
                ssl = true;
            }
            
            FTPClient ftp = new FTPClient();
            try {
                URL url = new URL( (String) getParameter( URL_PARAM_NAME ) );
                
                int port = url.getPort() >= 0 ? url.getPort() : (ssl ? 22 : 21);
                ftp.connect( url.getHost(), port );
                LOG.info( "Connected to " + url.getHost() + "." );
                
                int reply = ftp.getReplyCode();

                if (!FTPReply.isPositiveCompletion( reply )) {
                    throw new NexusException( "FTP server refused connection." );
                }
                
                String user = getParameter( USER_PARAM_NAME );
                String password = getParameter( PASSWORD_PARAM_NAME );
                boolean success = ftp.login( user, password );
                if (!success) {
                    throw new NexusException( "FTP authentication failed: " + ftp.getReplyString() );
                }
                LOG.debug( "Successfully logged in user " + user );
                ftp.changeWorkingDirectory( url.getPath() );
                
                String localDir = getParameter( DOWNLOAD_DIR_PARAM_NAME );
                
                List<File> localFiles = new ArrayList<File>();
                
                FTPFile[] files = ftp.listFiles();
                for (FTPFile file : files) {
                    File localFile = new File( localDir, file.getName() );
                    FileOutputStream fout = new FileOutputStream( localFile );
                    success = ftp.retrieveFile( file.getName(), fout );
                    if (!success) {
                        LOG.error( "Could not retrieve file " + file.getName() );
                    } else {
                        if (!ftp.deleteFile( file.getName() )) {
                            LOG.error( "Could not delete file " + file.getName() );
                        } else {
                            localFiles.add( localFile );
                        }
                    }
                    fout.close();
                }
                
                ftp.logout();
                LOG.info( "Logged out" );
                
                File errorDir = new File( (String) getParameter( ERROR_DIR_PARAM_NAME ) );
                
                for (File localFile : localFiles) {
                    processFile( localFile, errorDir, (String) getParameter( PARTNER_PARAM_NAME ) );
                }
                
                // process files
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if(ftp.isConnected()) {
                    try {
                        ftp.disconnect();
                    } catch(IOException ioe) {
                    }
                }
            }
        }
    }
}
