package org.nexuse2e.service.sftp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.Constants.BeanStatus;
import org.nexuse2e.Constants.Layer;
import org.nexuse2e.configuration.ConfigurationAccessService;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.Constants.ParameterType;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.pojo.MessagePojo;
import org.nexuse2e.service.AbstractService;
import org.nexuse2e.service.ReceiverAware;
import org.nexuse2e.service.SchedulerClient;
import org.nexuse2e.service.SchedulingService;
import org.nexuse2e.service.Service;
import org.nexuse2e.transport.TransportReceiver;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.ChannelSftp.LsEntry;

/**
 * This service implementation acts as an SFTP client and receives files by
 * polling an SFTP server from time to time.
 *
 * @author markus.breilmann
 * @version $LastChangedRevision: 262 $ - $LastChangedDate: 2007-09-18 11:58:43 +0000 (Di, 18 Sep 2007) $ by $LastChangedBy: mbreilmann $
 */
public class SftpPollingReceiverService extends AbstractService implements ReceiverAware {

    private static Logger      LOG                         = Logger.getLogger( SftpPollingReceiverService.class );

    public static final String PARTNER_PARAM_NAME          = "partnerId";
    public static final String DOWNLOAD_DIR_PARAM_NAME     = "downloadDir";
    public static final String ERROR_DIR_PARAM_NAME        = "errorDir";
    public static final String URL_PARAM_NAME              = "url";
    public static final String FILE_PATTERN_PARAM_NAME     = "filePattern";
    public static final String USER_PARAM_NAME             = "username";
    public static final String PASSWORD_PARAM_NAME         = "password";
    public static final String INTERVAL_PARAM_NAME         = "interval";
    public static final String TRANSFER_MODE_PARAM_NAME    = "transferMode";
    public static final String RENAMING_PREFIX_PARAM_NAME  = "prefix";
    public static final String CHANGE_FILE_PARAM_NAME      = "changeFile";

    public static final String CUSTOM_PARAMETER_FILE_NAME  = "fileName";
    public static final String CUSTOM_PARAMETER_PARTNER_ID = "partnerId";
    public static final String CUSTOM_PARAMETER_URL        = "url";

    private static int         counter                     = 0;

    private TransportReceiver  transportReceiver           = null;                                                 ;
    private SchedulingService  schedulingService           = null;                                                 ;
    private SchedulerClient    schedulerClient             = null;                                                 ;
    private boolean            fileChangeActive            = true;

    private String             user                        = null;
    private String             password                    = null;

    private SimpleDateFormat   simpleDateFormat            = new SimpleDateFormat( "HH:mm" );

    public SftpPollingReceiverService() {

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

        parameterMap.put( RENAMING_PREFIX_PARAM_NAME, new ParameterDescriptor( ParameterType.STRING, "File Prefix",
                "Prefix is prepended to successfully transfered files, instead of file delete", "5" ) );

        parameterMap.put( CHANGE_FILE_PARAM_NAME, new ParameterDescriptor( ParameterType.BOOLEAN, "Change/Delete File",
                "Rename/Delete file active", Boolean.TRUE ) );

    }

    @Override
    public void initialize( EngineConfiguration config ) throws InstantiationException {

        Boolean tempFileChangeActive = (Boolean) getParameter( CHANGE_FILE_PARAM_NAME );
        if ( tempFileChangeActive != null ) {
            fileChangeActive = tempFileChangeActive.booleanValue();
            LOG.debug( "fileChangeActive: " + fileChangeActive );
        }

        user = getParameter( USER_PARAM_NAME );
        password = getParameter( PASSWORD_PARAM_NAME );

        // TODO Auto-generated method stub
        super.initialize( config );
    }

    @Override
    public void start() {

        if ( getStatus() == BeanStatus.ACTIVATED ) {

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

                String intervalString = getParameter( INTERVAL_PARAM_NAME );
                if ( StringUtils.isEmpty( intervalString ) ) {
                    LOG.warn( "No interval definition found!" );
                    schedulingService.registerClient( schedulerClient, ( 5 * 60000 ) );
                } else if ( intervalString.indexOf( ":" ) != -1 ) {
                    StringTokenizer st = new StringTokenizer( intervalString, "," );
                    ArrayList<Date> times = new ArrayList<Date>();
                    while ( st.hasMoreTokens() ) {
                        String time = st.nextToken();
                        LOG.debug( "Parsing scheduled time: " + time );
                        Date scheduledTime = simpleDateFormat.parse( time );
                        Calendar tempCalendar = new GregorianCalendar();
                        tempCalendar.setTime( scheduledTime );
                        Calendar scheduledCalendar = new GregorianCalendar();
                        scheduledCalendar.set( Calendar.HOUR_OF_DAY, tempCalendar.get( Calendar.HOUR_OF_DAY ) );
                        scheduledCalendar.set( Calendar.MINUTE, tempCalendar.get( Calendar.MINUTE ) );
                        scheduledCalendar.set( Calendar.SECOND, 0 );
                        scheduledCalendar.set( Calendar.MILLISECOND, 0 );
                        scheduledTime = new Date( scheduledCalendar.getTimeInMillis() );
                        times.add( scheduledTime );
                        LOG.debug( "Scheduled time: " + scheduledTime );
                    }
                    if ( !times.isEmpty() ) {
                        schedulingService.registerClient( schedulerClient, times );

                    }
                } else {
                    int interval = Integer.parseInt( intervalString ) * 60000;
                    LOG.debug( "Using interval: " + interval );
                    schedulingService.registerClient( schedulerClient, interval );
                }

                super.start();
                LOG.debug( "FtpPollingReceiver service started" );
            } catch ( Exception ex ) {
                LOG.error( "FtpPollingReceiver service could not be started", ex );
                status = BeanStatus.ERROR;
            }
        } else {
            LOG.error( "Service not in correct state to be started: " + status );
        }
    }

    @Override
    public void stop() {

        if ( ( getStatus() == BeanStatus.STARTED ) || ( getStatus() == BeanStatus.ERROR ) ) {
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

    /**
     * Process any files found.
     * @param file File found to be processed.
     * @param errorDir The error directory.
     * @param partnerId The partner ID.
     */
    private boolean processFile( File file, File errorDir, String partnerId ) throws NexusException {

        counter++;
        
        boolean processedSucessfully = false;

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
                
                processedSucessfully = true;

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
                throw new NexusException( "An error occurred while processing file " + file, ex );
            }
        } else {
            if ( transportReceiver == null ) {
                LOG.error( "No TransportReceiverAvailable!" );
            } else {
                LOG.error( "No file to process!" );
            }
        }
        
        return processedSucessfully;
    }

    private static String dosStyleToRegEx( String pattern ) {

        StringBuffer sb = new StringBuffer();
        for ( int i = 0; i < pattern.length(); i++ ) {
            char c = pattern.charAt( i );
            if ( '?' != c && '*' != c ) {
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

    @SuppressWarnings("unchecked")
    public static void main( String args[] ) {

        JSch jsch = new JSch();
        ChannelSftp channelSftp = null;
        Session session = null;

        try {
            String host = "sftp.wirecard.com";
            String user = "T2078";
            String password = "NTum-93!";
            String path = "/toT2078/WD_RESP/new";
            String prefix = "../processed/";

            session = jsch.getSession( user, host, 22 );

            UserInfo userInfo = new UserInfo( password );
            session.setUserInfo( userInfo );

            try {
                session.connect();
            } catch ( JSchException jSchEx ) {
                throw new NexusException( "SFTP authentication failed: " + jSchEx );
            }
            LOG.trace( "Connected to " + host + "." );

            Channel channel = session.openChannel( "sftp" );
            channel.connect();
            channelSftp = (ChannelSftp) channel;

            LOG.trace( "Directory URL Path: " + path );
            String directory = path;
            if ( directory.startsWith( "/" ) ) {
                directory = directory.substring( 1 );
            }

            if ( StringUtils.isNotEmpty( directory ) ) {
                LOG.trace( "Directory requested: " + directory );
                try {
                    channelSftp.cd( directory );
                } catch ( SftpException sftpEx ) {
                    throw new NexusException( "SFTP server did not change directory: " + sftpEx );
                }
            }
            LOG.trace( "Working Directory: " + channelSftp.pwd() );

            String filePattern = "*.xml";
            Vector<LsEntry> files = channelSftp.ls( "." );
            LOG.trace( "Number of files in directory: " + files.size() + ", checking against pattern " + filePattern );

            String regEx = dosStyleToRegEx( filePattern );
            for ( LsEntry file : files ) {
                if ( Pattern.matches( regEx, file.getFilename() ) ) {
                    Vector<LsEntry> targetFiles = null;
                    try {
                        targetFiles = channelSftp.ls( prefix + file.getFilename() );
                    } catch ( SftpException sftpEx ) {
                        // No message/output required
                    }
                    if ( targetFiles == null || targetFiles.isEmpty() ) {
                        LOG.trace( "Target file does not exist!" );
                    } else {
                        LOG.trace( "Target file DOES exist :-(" );
                    }
                }
            }

        } catch ( Exception e ) {
            e.printStackTrace();
            LOG.error( "Error polling FTP account: " + e );
        } finally {
            if ( ( channelSftp != null ) && channelSftp.isConnected() ) {
                LOG.trace( "Closing channel..." );
                channelSftp.disconnect();
            } else {
                LOG.trace( "Channel not connected." );
            }
            if ( ( session != null ) && session.isConnected() ) {
                LOG.trace( "Closing session..." );
                session.disconnect();
            } else {
                LOG.trace( "Session not connected." );
            }
        }
    }

    class FtpSchedulerClient implements SchedulerClient {

        @SuppressWarnings("unchecked")
        public void scheduleNotify() {

            JSch jsch = new JSch();
            ChannelSftp channelSftp = null;
            Session session = null;

            try {
                URL url = new URL( (String) getParameter( URL_PARAM_NAME ) );

                session = jsch.getSession( user, url.getHost(), 22 );

                UserInfo userInfo = new UserInfo( password );
                session.setUserInfo( userInfo );

                try {
                    session.connect();
                } catch ( JSchException jSchEx ) {
                    throw new NexusException( "SFTP authentication failed: " + jSchEx );
                }
                LOG.trace( "Connected to " + url.getHost() + "." );

                Channel channel = session.openChannel( "sftp" );
                channel.connect();
                channelSftp = (ChannelSftp) channel;

                LOG.trace( "Directory URL Path: " + url.getPath() );
                String directory = url.getPath();
                if ( directory.startsWith( "/" ) ) {
                    directory = directory.substring( 1 );
                }

                if ( StringUtils.isNotEmpty( directory ) ) {
                    LOG.trace( "Directory requested: " + directory );
                    try {
                        channelSftp.cd( directory );
                    } catch ( SftpException sftpEx ) {
                        throw new NexusException( "SFTP server did not change directory: " + sftpEx );
                    }
                }
                LOG.trace( "Working Directory: " + channelSftp.pwd() );

                String localDir = getParameter( DOWNLOAD_DIR_PARAM_NAME );

                String filePattern = getParameter( FILE_PATTERN_PARAM_NAME );
                List<File> localFiles = new ArrayList<File>();
                Vector<LsEntry> files = channelSftp.ls( "." );
                LOG.trace( "Number of files in directory: " + files.size() + ", checking against pattern "
                        + filePattern );

                File errorDir = new File( (String) getParameter( ERROR_DIR_PARAM_NAME ) );

                String regEx = dosStyleToRegEx( filePattern );
                String prefix = getParameter( RENAMING_PREFIX_PARAM_NAME );
                for ( LsEntry file : files ) {
                    if ( Pattern.matches( regEx, file.getFilename() ) ) {
                        Vector<LsEntry> targetFiles = null;
                        try {
                            targetFiles = channelSftp.ls( prefix + file.getFilename() );
                        } catch ( SftpException sftpEx ) {
                            LOG.debug( "File does not exist in backup location: " + file.getFilename() );
                        }
                        if ( !fileChangeActive || (targetFiles == null) || targetFiles.isEmpty() ) {
                            LOG.trace( "Processing file: " + file.getFilename() );

                            File localFile = new File( localDir, file.getFilename() );
                            FileOutputStream fout = new FileOutputStream( localFile );

                            try {
                                channelSftp.get( file.getFilename(), fout );
                                fout.flush();
                                fout.close();
                                try {

                                    boolean processedSucessfully = processFile( localFile, errorDir, (String) getParameter( PARTNER_PARAM_NAME ) );

                                    boolean error = false;
                                    if ( processedSucessfully && fileChangeActive ) {
                                        if ( StringUtils.isEmpty( prefix ) ) {
                                            try {
                                                channelSftp.rm( file.getFilename() );
                                            } catch ( SftpException sftpEx ) {
                                                LOG.error( "Could not delete file " + file.getFilename() );
                                                error = true;
                                            }
                                        } else {
                                            try {
                                                channelSftp.rename( file.getFilename(), prefix + file.getFilename() );
                                            } catch ( SftpException sftpEx ) {
                                                LOG.error( "Could not rename file from " + file.getFilename() + " to "
                                                        + prefix + file.getFilename() );
                                                error = true;
                                            }
                                        }
                                    }

                                    if ( !error ) {
                                        localFiles.add( localFile );
                                    }
                                } catch ( Exception e ) {
                                    LOG.error( "Error processing file " + file.getFilename() + ": " + e );
                                }
                            } catch ( SftpException sftpEx ) {
                                LOG.error( "Could not retrieve file " + file.getFilename() );
                            }
                        } else {
                            LOG.error( "Target file " + file.getFilename() + " DOES exist - won't process!" );
                        }
                    }
                }

                /*
                for ( File localFile : localFiles ) {
                    processFile( localFile, errorDir, (String) getParameter( PARTNER_PARAM_NAME ) );
                }
                */

                // process files
            } catch ( Exception e ) {
                e.printStackTrace();
                LOG.error( "Error polling FTP account: " + e );
            } finally {
                if ( ( channelSftp != null ) && channelSftp.isConnected() ) {
                    LOG.trace( "Closing channel..." );
                    channelSftp.disconnect();
                } else {
                    LOG.trace( "Channel not connected." );
                }
                if ( ( session != null ) && session.isConnected() ) {
                    LOG.trace( "Closing session..." );
                    session.disconnect();
                } else {
                    LOG.trace( "Session not connected." );
                }
            }
        }
    }

}
