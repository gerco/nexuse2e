package org.nexuse2e.service.ftp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.apache.commons.io.FileUtils;
import org.apache.commons.net.SocketFactory;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileEntryParser;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.parser.DefaultFTPFileEntryParserFactory;
import org.apache.commons.net.ftp.parser.UnixFTPEntryParser;
import org.apache.log4j.Logger;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.Constants.BeanStatus;
import org.nexuse2e.Constants.Layer;
import org.nexuse2e.configuration.ConfigurationAccessService;
import org.nexuse2e.configuration.Constants;
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
import org.nexuse2e.util.CertificateUtil;
import org.nexuse2e.util.EncryptionUtil;

/**
 * This service implementation acts as an (S)FTP client and receives files by
 * polling an FTP server from time to time.
 *
 * @author jonas.reese
 * @version $LastChangedRevision$ - $LastChangedDate$ by $LastChangedBy$
 */
public class FtpPollingReceiverService extends AbstractService implements ReceiverAware {

    private static Logger      LOG                         = Logger.getLogger( FtpReceiverService.class );

    public static final String PARTNER_PARAM_NAME          = "partnerId";
    public static final String DOWNLOAD_DIR_PARAM_NAME     = "downloadDir";
    public static final String ERROR_DIR_PARAM_NAME        = "errorDir";
    public static final String FTP_TYPE_PARAM_NAME         = "ftpType";
    public static final String CERTIFICATE_PARAM_NAME      = "certificate";
    public static final String URL_PARAM_NAME              = "url";
    public static final String FILE_PATTERN_PARAM_NAME     = "filePattern";
    public static final String USER_PARAM_NAME             = "username";
    public static final String PASSWORD_PARAM_NAME         = "password";
    public static final String INTERVAL_PARAM_NAME         = "interval";
    public static final String TRANSFER_MODE_PARAM_NAME    = "transferMode";

    public static final String CUSTOM_PARAMETER_FILE_NAME  = "fileName";
    public static final String CUSTOM_PARAMETER_PARTNER_ID = "partnerId";
    public static final String CUSTOM_PARAMETER_URL        = "url";

    private TransportReceiver  transportReceiver;
    private SchedulingService  schedulingService;
    private SchedulerClient    schedulerClient;

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
        ftpTypeDrowdown.addElement( "FTPS (encrypted)", "ftps" );
        ftpTypeDrowdown.addElement( "Plain FTP (not encrypted)", "ftp" );
        parameterMap.put( FTP_TYPE_PARAM_NAME, new ParameterDescriptor( ParameterType.LIST, "FTP type", "FTP type",
                ftpTypeDrowdown ) );

        parameterMap.put( URL_PARAM_NAME, new ParameterDescriptor( ParameterType.STRING, "URL",
                "Polling URL (use ftp://host.com:[port]/dir/subdir format)", "" ) );

        parameterMap.put( FILE_PATTERN_PARAM_NAME, new ParameterDescriptor( ParameterType.STRING, "File pattern",
                "DOS-like file pattern with wildcards * and ? (examples: *.xml, ab?.*)", "*" ) );

        parameterMap.put( USER_PARAM_NAME, new ParameterDescriptor( ParameterType.STRING, "User name",
                "The FTP user name", "anonymous" ) );

        parameterMap.put( PASSWORD_PARAM_NAME, new ParameterDescriptor( ParameterType.PASSWORD, "Password",
                "The FTP password", "" ) );

        parameterMap.put( INTERVAL_PARAM_NAME, new ParameterDescriptor( ParameterType.STRING, "Interval (minutes)",
                "Connect every n minutes to look for new files", "5" ) );

        ListParameter transferModeListParam = new ListParameter();
        transferModeListParam.addElement( "Auto", "auto" );
        transferModeListParam.addElement( "Binary", "binary" );
        transferModeListParam.addElement( "ASCII", "ascii" );
        parameterMap.put( TRANSFER_MODE_PARAM_NAME, new ParameterDescriptor( ParameterType.LIST, "Transfer mode",
                "Use Automatic/Binary/ASCII transfer mode (default is Auto)", transferModeListParam ) );

        final ParameterDescriptor certsParamDesc = new ParameterDescriptor( ParameterType.LIST, "Client certificate",
                "Use this certificate for client authentication", new ListParameter() );
        certsParamDesc.setUpdater( new Runnable() {

            public void run() {

                addCertificatesToDropdown( (ListParameter) certsParamDesc.getDefaultValue() );
            }
        } );
        parameterMap.put( CERTIFICATE_PARAM_NAME, certsParamDesc );
    }

    private void addCertificatesToDropdown( ListParameter certsDropdown ) {

        ConfigurationAccessService cas = Engine.getInstance().getActiveConfigurationAccessService();
        try {
            List<CertificatePojo> certs = cas.getCertificates( Constants.CERTIFICATE_TYPE_LOCAL, null );
            if ( certs != null ) {
                for ( CertificatePojo cert : certs ) {
                    String label = cert.getName();
                    if ( label == null || "".equals( label.trim() ) ) {
                        label = "Certificate #" + cert.getNxCertificateId();
                    }
                    if ( cert.getDescription() != null && !"".equals( cert.getDescription() ) ) {
                        label += " (" + cert.getDescription() + ")";
                    }
                    String value = Integer.toString( cert.getNxCertificateId() );
                    if ( certsDropdown.getElement( value ) == null ) {
                        certsDropdown.addElement( label, value );
                    }
                }
            }
        } catch ( NexusException nex ) {
            LOG.error( "Could not retrieve local certificate list", nex );
        }
    }

    @Override
    public void start() {

        System.setProperty( "javax.net.ssl.trustStore", "C:\\Programme\\Java\\jre1.5.0_09\\lib\\security\\cacerts" );
        try {
            ConfigurationAccessService cas = Engine.getInstance().getActiveConfigurationAccessService();
            schedulingService = null;
            for ( Service service : cas.getServiceInstances() ) {
                if ( service instanceof SchedulingService ) {
                    schedulingService = (SchedulingService) service;
                }
            }
            if ( schedulingService == null ) {
                throw new NexusException( "No ScheduleService implementation found" );
            }

            int interval = Integer.parseInt( (String) getParameter( INTERVAL_PARAM_NAME ) ) * 60000;
            schedulingService.registerClient( schedulerClient, interval );

            super.start();
            LOG.debug( "FtpPollingReceiver service started" );
        } catch ( Exception ex ) {
            LOG.error( "FtpPollingReceiver service could not be started", ex );
            status = BeanStatus.ERROR;
        }
    }

    @Override
    public void stop() {

        if ( getStatus() == BeanStatus.STARTED ) {
            if ( schedulingService != null ) {
                schedulingService.deregisterClient( schedulerClient );
            }
            LOG.debug( "FtpPollingReceiver service stopped" );
            super.stop();
        }
    }

    @Override
    public Layer getActivationLayer() {

        return Layer.INBOUND_PIPELINES;
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
    private void processFile( File file, File errorDir, String partnerId ) {

        counter++;

        if ( transportReceiver != null && file != null && file.exists() && file.length() != 0 ) {
            try {
                // Open the file to read one line at a time
                byte[] fileBuffer = FileUtils.readFileToByteArray( file );

                LOG.debug( "Read file " + file.getAbsolutePath() + " , size: " + fileBuffer.length );

                ConfigurationAccessService cas = Engine.getInstance().getActiveConfigurationAccessService();

                MessageContext messageContext = new MessageContext();
                messageContext.setData( fileBuffer );
                messageContext.setCommunicationPartner( cas.getPartnerByPartnerId( partnerId ) );
                messageContext.setMessagePojo( new MessagePojo() );
                messageContext.setOriginalMessagePojo( messageContext.getMessagePojo() );
                Map<String, String> customParameters = new HashMap<String, String>();
                customParameters.put( CUSTOM_PARAMETER_FILE_NAME, file.getName() );
                customParameters.put( CUSTOM_PARAMETER_PARTNER_ID, (String) getParameter( PARTNER_PARAM_NAME ) );
                customParameters.put( CUSTOM_PARAMETER_URL, (String) getParameter( URL_PARAM_NAME ) );
                messageContext.getMessagePojo().setCustomParameters( customParameters );
                LOG.debug( "Calling TransportReceiver..." );
                transportReceiver.processMessage( messageContext );

                file.delete();
            } catch ( Exception ex ) {
                LOG.error( "An error occurred while processing file " + file, ex );
                try {
                    String postfix = "_" + System.currentTimeMillis() + "_" + counter;
                    FileUtils.copyFile( file, new File( errorDir, file.getName() + postfix ) );
                    file.delete();
                } catch ( IOException ioex ) {
                    LOG.error( "Could not copy file " + file + " to error directory " + errorDir, ioex );
                }
            }
        } else {
            if ( transportReceiver == null ) {
                LOG.error( "No TransportReceiverAvailable!" );
            } else {
                LOG.error( "No file to process!" );
            }
        }
    }

    private CertificatePojo getSelectedCertificate( String certId ) throws NexusException {

        CertificatePojo cert;
        ConfigurationAccessService cas = Engine.getInstance().getActiveConfigurationAccessService();
        if ( certId == null || certId.length() == 0 ) {
            List<CertificatePojo> certs = cas.getCertificates( Constants.CERTIFICATE_TYPE_LOCAL, null );
            if ( certs == null || certs.isEmpty() || certs.get( 0 ) == null ) {
                throw new NexusException( "No appropriate certificate found for SFTP server authentication" );
            }
            cert = certs.get( 0 );
        } else {
            cert = cas.getCertificateByNxCertificateId( Constants.CERTIFICATE_TYPE_LOCAL, Integer.parseInt( certId ) );
            if ( cert == null ) {
                throw new NexusException( "No local certificate with ID " + certId
                        + " could be found for SFTP server authentication" );
            }
        }
        return cert;
    }
    
    private static String dosStyleToRegEx( String pattern ) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < pattern.length(); i++) {
            char c = pattern.charAt( i );
            if ('?' != c && '*' != c) {
                sb.append( "\\Q" );
                sb.append( c );
                sb.append( "\\E" );
            } else {
                sb.append( c );
            }
        }
        pattern = sb.toString().replaceAll( "\\*", "\\.\\*" );
        pattern = pattern.replaceAll( "\\?", "\\." );
        
        return pattern;
    }

    class FtpSchedulerClient implements SchedulerClient {

        public void scheduleNotify() {

            boolean ssl = false;
            ListParameter ftpTypeSel = getParameter( FTP_TYPE_PARAM_NAME );
            if ( !"ftp".equals( ftpTypeSel.getSelectedValue() ) ) {
                ssl = true;
            }

            FTPClient ftp = new FTPClient();
            ftp.setParserFactory( new DefaultFTPFileEntryParserFactory() {

                @Override
                public FTPFileEntryParser createUnixFTPEntryParser() {

                    return (FTPFileEntryParser) new UnixFTPEntryParser() {

                        @Override
                        public String readNextEntry( BufferedReader reader ) throws IOException {

                            try {
                                return super.readNextEntry( reader );
                            } catch ( SSLException e ) {
                                // since the SSL input stream seems to be throwing
                                // this exception when trying to read from a zero-length
                                // stream, we avoid this exception by catching it
                                return null;
                            }
                        }
                    };
                }
            } );
            try {
                URL url = new URL( (String) getParameter( URL_PARAM_NAME ) );
                int port = url.getPort() >= 0 ? url.getPort() : ( ssl ? 990 : 21 );

                if ( ssl ) {
                    ListParameter certSel = getParameter( CERTIFICATE_PARAM_NAME );
                    String certId = certSel.getSelectedValue();
                    getSelectedCertificate( certId );
                    ftp.setSocketFactory( new SocketFactoryImpl( getSelectedCertificate( certId ) ) );
                }

                ftp.connect( url.getHost(), port );
                LOG.info( "Connected to " + url.getHost() + "." );

                int reply = ftp.getReplyCode();

                if ( !FTPReply.isPositiveCompletion( reply ) ) {
                    throw new NexusException( "FTP server refused connection." );
                }
                ftp.enterLocalPassiveMode();

                String user = getParameter( USER_PARAM_NAME );
                String password = getParameter( PASSWORD_PARAM_NAME );
                boolean success = ftp.login( user, password );
                if ( !success ) {
                    throw new NexusException( "FTP authentication failed: " + ftp.getReplyString() );
                }
                LOG.debug( "Successfully logged in user " + user );
                if ( ssl ) {
                    reply = ftp.sendCommand( "PROT P" );
                    if ( !FTPReply.isPositiveCompletion( reply ) ) {
                        throw new NexusException( "PROT P command failed with code " + reply );
                    }
                }

                ftp.changeWorkingDirectory( url.getPath() );

                String localDir = getParameter( DOWNLOAD_DIR_PARAM_NAME );

                String filePattern = getParameter( FILE_PATTERN_PARAM_NAME );
                List<File> localFiles = new ArrayList<File>();
                FTPFile[] files = ftp.listFiles();
                LOG.debug( "Number of files in directory: "
                        + files.length + ", checking against pattern " + filePattern );
                String regEx = dosStyleToRegEx( filePattern );
                for ( FTPFile file : files ) {
                    if ( Pattern.matches( regEx, file.getName() ) ) {
                        File localFile = new File( localDir, file.getName() );
                        FileOutputStream fout = new FileOutputStream( localFile );
                        success = ftp.retrieveFile( file.getName(), fout );
                        if ( !success ) {
                            LOG.error( "Could not retrieve file " + file.getName() );
                        } else {
                            if ( !ftp.deleteFile( file.getName() ) ) {
                                LOG.error( "Could not delete file " + file.getName() );
                            } else {
                                localFiles.add( localFile );
                            }
                        }
                        fout.close();
                    }
                }

                File errorDir = new File( (String) getParameter( ERROR_DIR_PARAM_NAME ) );
                
                for ( File localFile : localFiles ) {
                    processFile( localFile, errorDir, (String) getParameter( PARTNER_PARAM_NAME ) );
                }

                // process files
            } catch ( Exception e ) {
                e.printStackTrace();
            } finally {
                if ( ftp.isConnected() ) {
                    try {
                        ftp.logout();
                        LOG.info( "Logged out" );
                    } catch ( IOException ioe ) {
                    }
                    try {
                        ftp.disconnect();
                    } catch ( IOException ioe ) {
                    }
                }
            }
        }
    }

    class SocketFactoryImpl implements SocketFactory {

        private KeyStore        keystore;
        private KeyStore        truststore;
        private SSLContext      sslContext;
        private CertificatePojo cert;

        public SocketFactoryImpl( CertificatePojo cert ) throws NexusException {

            this.cert = cert;
            ConfigurationAccessService cas = Engine.getInstance().getActiveConfigurationAccessService();
            keystore = CertificateUtil.getPKCS12KeyStore( cert );
            truststore = cas.getCacertsKeyStore();
        }

        /**
         * Get SSL Context.
         */
        private SSLContext getSSLContext() throws IOException {

            // if already stored - return it
            if ( sslContext != null ) {
                return sslContext;
            }

            try {
                KeyManager[] keymanagers = null;
                TrustManager[] trustmanagers = null;
                if ( keystore != null ) {
                    keymanagers = CertificateUtil.createKeyManagers( keystore, EncryptionUtil.decryptString( cert
                            .getPassword() ) );
                }
                if ( truststore != null ) {
                    trustmanagers = CertificateUtil.createTrustManagers( truststore );
                }
                SSLContext sslcontext = SSLContext.getInstance( "TLS" );
                sslcontext.init( keymanagers, trustmanagers, null );
                return sslcontext;
            } catch ( NoSuchAlgorithmException e ) {
                LOG.error( e.getMessage(), e );
                throw new IOException( "Unsupported algorithm exception: " + e.getMessage() );
            } catch ( KeyStoreException e ) {
                LOG.error( e.getMessage(), e );
                throw new IOException( "Keystore exception: " + e.getMessage() );
            } catch ( GeneralSecurityException e ) {
                LOG.error( e.getMessage(), e );
                throw new IOException( "Key management exception: " + e.getMessage() );
            } catch ( Exception e ) {
                LOG.error( e.getMessage(), e );
                throw new IOException( "error reading keystore/truststore file: " + e.getMessage() );
            }
        }

        public ServerSocket createServerSocket( int port ) throws IOException {

            throw new UnsupportedOperationException( "createServerSocket() not implemented" );
        }

        public ServerSocket createServerSocket( int port, int backlog ) throws IOException {

            throw new UnsupportedOperationException( "createServerSocket() not implemented" );
        }

        public ServerSocket createServerSocket( int port, int backlog, InetAddress address ) throws IOException {

            throw new UnsupportedOperationException( "createServerSocket() not implemented" );
        }

        public Socket createSocket( String host, int port ) throws UnknownHostException, IOException {

            // get socket factory
            SSLContext ctx = getSSLContext();
            SSLSocketFactory socFactory = ctx.getSocketFactory();

            // create socket
            SSLSocket sslSocket = (SSLSocket) socFactory.createSocket( host, port );
            initializeSocket( sslSocket );

            return sslSocket;
        }

        public Socket createSocket( InetAddress address, int port ) throws IOException {

            // get socket factory
            SSLContext ctx = getSSLContext();
            SSLSocketFactory socFactory = ctx.getSocketFactory();

            // create socket
            SSLSocket sslSocket = (SSLSocket) socFactory.createSocket( address, port );
            initializeSocket( sslSocket );

            return sslSocket;
        }

        public Socket createSocket( String host, int port, InetAddress localAddr, int localPort )
                throws UnknownHostException, IOException {

            // get socket factory
            SSLContext ctx = getSSLContext();
            SSLSocketFactory socFactory = ctx.getSocketFactory();

            // create socket
            SSLSocket sslSocket = (SSLSocket) socFactory.createSocket( host, port, localAddr, localPort );
            initializeSocket( sslSocket );

            return sslSocket;
        }

        public Socket createSocket( InetAddress address, int port, InetAddress localAddr, int localPort )
                throws IOException {

            // get socket factory
            SSLContext ctx = getSSLContext();
            SSLSocketFactory socFactory = ctx.getSocketFactory();

            // create socket
            SSLSocket sslSocket = (SSLSocket) socFactory.createSocket( address, port, localAddr, localPort );

            initializeSocket( sslSocket );
            return sslSocket;
        }

        private void initializeSocket( SSLSocket sslSocket ) {

            String cipherSuites[] = sslSocket.getSupportedCipherSuites();
            sslSocket.setEnabledCipherSuites( cipherSuites );
        }
    }
}
