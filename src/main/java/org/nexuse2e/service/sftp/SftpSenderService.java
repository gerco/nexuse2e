package org.nexuse2e.service.sftp;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.nexuse2e.NexusException;
import org.nexuse2e.Constants.Layer;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.Constants.ParameterType;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.pojo.ConnectionPojo;
import org.nexuse2e.pojo.MessagePayloadPojo;
import org.nexuse2e.service.AbstractService;
import org.nexuse2e.service.SenderAware;
import org.nexuse2e.transport.TransportSender;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

/**
 * Created: 12.07.2007
 * <p>
 * Service implementation for sending via the FTP protocol.
 * </p>
 *
 * @author jonas.reese
 * @version $LastChangedRevision: 162 $ - $LastChangedDate: 2007-07-30 13:52:13 +0000 (Mo, 30 Jul 2007) $ by $LastChangedBy: gesch $
 */
public class SftpSenderService extends AbstractService implements SenderAware {

    private static Logger      LOG                       = Logger.getLogger( SftpSenderService.class );

    public static final String URL_PARAM_NAME            = "url";
    public static final String USER_PARAM_NAME           = "username";
    public static final String PASSWORD_PARAM_NAME       = "password";
    public static final String BASE_FILE_NAME_PARAM_NAME = "baseFileName";
    public static final String FILE_EXTENSION_PARAM_NAME = "fileExtension";
    public static final String APPEND_TIMESTAMP          = "appendTimestamp";

    private TransportSender    transportSender           = null;
    private String             baseFileName              = null;
    private String             fileExtension             = null;
    private boolean            useTimestamp              = true;

    /* (non-Javadoc)
     * @see org.nexuse2e.service.AbstractService#fillParameterMap(java.util.Map)
     */
    @Override
    public void fillParameterMap( Map<String, ParameterDescriptor> parameterMap ) {

        parameterMap.put( BASE_FILE_NAME_PARAM_NAME, new ParameterDescriptor( ParameterType.STRING, "Base file name",
                "The base file name used in uploads", "upload_" ) );

        parameterMap.put( FILE_EXTENSION_PARAM_NAME, new ParameterDescriptor( ParameterType.STRING, "File extension",
                "The file extension used in uploads", ".xml" ) );

        parameterMap.put( APPEND_TIMESTAMP, new ParameterDescriptor( ParameterType.BOOLEAN, "Append Timestamp",
                "Flag whether to append a timestamp to the filename", Boolean.TRUE ) );
    }

    @Override
    public void initialize( EngineConfiguration config ) throws InstantiationException {

        baseFileName = getParameter( BASE_FILE_NAME_PARAM_NAME );
        if ( baseFileName == null ) {
            LOG.error( "No base file name provided!" );
        }
        fileExtension = getParameter( FILE_EXTENSION_PARAM_NAME );
        if ( StringUtils.isEmpty( fileExtension ) ) {
            LOG.info( "No file extension provided!" );
            fileExtension = "";
        } else if ( !fileExtension.startsWith( "." ) ) {
            fileExtension = "." + fileExtension;
        }
        LOG.debug( "Using file extension: " + fileExtension );

        Boolean tempFlag = getParameter( APPEND_TIMESTAMP );
        if ( tempFlag != null && tempFlag.equals( Boolean.FALSE ) ) {
            useTimestamp = false;
            LOG.info( "Using base file name - not appending timestamp." );
        }

        super.initialize( config );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.service.SenderAware#sendMessage(org.nexuse2e.messaging.MessageContext)
     */
    public void sendMessage( MessageContext messageContext ) throws NexusException {

        JSch jsch = new JSch();
        ChannelSftp channelSftp = null;
        try {

            ConnectionPojo connection = messageContext.getMessagePojo().getParticipant().getConnection();
            if ( connection.getUri() == null ) {
                LOG.error( "No URL provided!" );
                throw new NexusException( "No URL provided!" );
            }
            if ( connection.getLoginName() == null ) {
                LOG.error( "No user name provided!" );
                throw new NexusException( "No user name provided!" );
            }
            if ( connection.getPassword() == null ) {
                LOG.error( "No password provided!" );
                throw new NexusException( "No password provided!" );
            }
            URL url = new URL( connection.getUri() );

            LOG.trace( "Trying to connect to " + url.getHost() + " with user " + connection.getLoginName() + "..." );
            Session session = jsch.getSession( connection.getLoginName(), url.getHost(), 22 );

            UserInfo userInfo = new UserInfo( connection.getPassword() );
            session.setUserInfo( userInfo );

            try {
                session.connect();
            } catch ( JSchException jSchEx ) {
                throw new NexusException( "SFTP connection/authentication failed: " + jSchEx );
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

            SimpleDateFormat sdf = new SimpleDateFormat( "yyyyMMddHHmmssSSS" );
            for ( MessagePayloadPojo messagePayloadPojo : messageContext.getMessagePojo().getMessagePayloads() ) {
                ByteArrayInputStream bais = new ByteArrayInputStream( messagePayloadPojo.getPayloadData() );

                String newFileName = baseFileName + ( useTimestamp ? sdf.format( new Date() ) : "" ) + fileExtension;

                channelSftp.put( bais, newFileName );
                LOG.trace( "Uploaded file: " + newFileName );
            }

        } catch ( Exception e ) {
            e.printStackTrace();
            LOG.error( "Error polling FTP account: " + e );
        } finally {
            if ( ( channelSftp != null ) && channelSftp.isConnected() ) {
                channelSftp.disconnect();
            }
        }
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.service.SenderAware#setTransportSender(org.nexuse2e.transport.TransportSender)
     */
    public void setTransportSender( TransportSender transportSender ) {

        this.transportSender = transportSender;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.service.SenderAware#getTransportSender()
     */
    public TransportSender getTransportSender() {

        return transportSender;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.service.AbstractService#getActivationRunlevel()
     */
    @Override
    public Layer getActivationLayer() {

        return Layer.OUTBOUND_PIPELINES;
    }

}
