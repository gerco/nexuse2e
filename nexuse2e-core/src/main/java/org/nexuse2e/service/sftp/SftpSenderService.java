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
package org.nexuse2e.service.sftp;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.nexuse2e.Layer;
import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.ParameterType;
import org.nexuse2e.logging.LogMessage;
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
 * Extended: 15.05.2009
 * <ul>
 * 	<li>Added support for public key authentication.</li>
 *  <li>To provide a password is not necessary anymore.</li>
 *  <li>The date pattern is configurable now.</li>
 * </ui>
 *
 * @author jonas.reese
 * @version $LastChangedRevision: 162 $ - $LastChangedDate: 2007-07-30 13:52:13 +0000 (Mo, 30 Jul 2007) $ by $LastChangedBy: gesch $
 */
public class SftpSenderService extends AbstractService implements SenderAware {

    private static Logger      LOG                       = Logger.getLogger( SftpSenderService.class );

    public static final String PRIVATE_KEY 			     = "privateKey";
    public static final String BASE_FILE_NAME_PARAM_NAME = "baseFileName";
    public static final String FILE_EXTENSION_PARAM_NAME = "fileExtension";
    public static final String TEMP_FILE_PARAM_NAME      = "useTempFile";
    public static final String APPEND_TIMESTAMP          = "appendTimestamp";
    public static final String TIMESTAMP_PATTERN         = "timestampPattern";
    
    public static final String DEFAULT_TIMESTAMP_PATTERN = "yyyyMMddHHmmssSSS";

    private TransportSender    transportSender           = null;
    private String             baseFileName              = null;
    private String             fileExtension             = null;
    private boolean            useTimestamp              = true;
    private String             timestampPattern          = DEFAULT_TIMESTAMP_PATTERN;
    private boolean            useTempFileName           = true;

    /* (non-Javadoc)
     * @see org.nexuse2e.service.AbstractService#fillParameterMap(java.util.Map)
     */
    @Override
    public void fillParameterMap( Map<String, ParameterDescriptor> parameterMap ) {

        parameterMap.put( BASE_FILE_NAME_PARAM_NAME, new ParameterDescriptor( ParameterType.STRING, "Base file name",
                "The base file name used in uploads", "upload_" ) );

        parameterMap.put( FILE_EXTENSION_PARAM_NAME, new ParameterDescriptor( ParameterType.STRING, "File extension",
                "The file extension used in uploads", ".xml" ) );

        parameterMap.put( TEMP_FILE_PARAM_NAME, new ParameterDescriptor( ParameterType.BOOLEAN,
                "Use temporary file name", "Use temporary file name during upload", Boolean.TRUE ) );

        parameterMap.put( APPEND_TIMESTAMP, new ParameterDescriptor( ParameterType.BOOLEAN, "Append Timestamp",
                "Flag whether to append a timestamp to the filename", Boolean.TRUE ) );
        
        parameterMap.put( TIMESTAMP_PATTERN, new ParameterDescriptor( ParameterType.STRING, "Timestamp Pattern",
                "The pattern of the timestamp that should be appended. Use pattern syntax of <a href=\"http://java.sun.com/j2se/1.5.0/docs/api/java/text/SimpleDateFormat.html\">SimpleDateFormat</a>.", DEFAULT_TIMESTAMP_PATTERN ) );
        
        parameterMap
        .put( PRIVATE_KEY,
              new ParameterDescriptor(
                      ParameterType.STRING,
                      "Private Key File",
                      "File that contains the private key (DSA or RSA) for SFTP authentication in PEM format. ",
                      "" ) );
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

        tempFlag = getParameter( TEMP_FILE_PARAM_NAME );
        if ( tempFlag != null && tempFlag.equals( Boolean.FALSE ) ) {
            useTempFileName = false;
            LOG.info( "Not using temporary file name during upload." );
        }
        
        timestampPattern = getParameter( TIMESTAMP_PATTERN );
        // An empty timestamp pattern makes no sense (you could deactivate the timestamp at all, if you want).
        // That's why we use the default here, if the pattern is empty
        if ( timestampPattern == null || timestampPattern.length() == 0 ) {
        	timestampPattern = DEFAULT_TIMESTAMP_PATTERN;
        }

        super.initialize( config );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.service.SenderAware#sendMessage(org.nexuse2e.messaging.MessageContext)
     */
    public MessageContext sendMessage( MessageContext messageContext ) throws NexusException {

        JSch jsch = new JSch();
        ChannelSftp channelSftp = null;
        try {

            ConnectionPojo connection = messageContext.getMessagePojo().getParticipant().getConnection();
            if ( connection.getUri() == null ) {
                //LOG.error( "No URL provided!" ); // this should be logged by the sender
                throw new NexusException( new LogMessage( "No URL provided!", messageContext.getMessagePojo() ) );
            }
            if ( connection.getLoginName() == null ) {
                //LOG.error( "No user name provided!" ); // this should be logged by the sender
            	throw new NexusException( new LogMessage( "No user name provided!", messageContext.getMessagePojo() ) );
            }
            URL url = new URL( connection.getUri() );
            
            // set private key file, if configured
            String pk_file = getParameter( PRIVATE_KEY );
            if ( pk_file != null && pk_file.length() > 0 ) {
            	LOG.trace( "Using public key authentication." );
	            jsch.addIdentity( (String) getParameter( PRIVATE_KEY ) );
            }

            LOG.trace( "Trying to connect to " + url.getHost() + " with user " + connection.getLoginName() + "..." );
            Session session = jsch.getSession( connection.getLoginName(), url.getHost(), 22 );

            // only set password, if specified
            if ( connection.getPassword() != null && connection.getPassword().length() > 0 ) {
            	session.setUserInfo( new UserInfo( connection.getPassword() ) );
            } else {
            	LOG.warn( "No password provided!" );
            }

            try {
                session.connect();
            } catch ( JSchException jSchEx ) {
                throw new NexusException( new LogMessage( "SFTP connection/authentication failed", messageContext.getMessagePojo() ), jSchEx );
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
                    throw new NexusException( new LogMessage( "SFTP server did not change directory", messageContext.getMessagePojo() ), sftpEx );
                }
            }
            LOG.trace( "Working Directory: " + channelSftp.pwd() );

            SimpleDateFormat sdf = new SimpleDateFormat( timestampPattern );
            for ( MessagePayloadPojo messagePayloadPojo : messageContext.getMessagePojo().getMessagePayloads() ) {
                ByteArrayInputStream bais = new ByteArrayInputStream( messagePayloadPojo.getPayloadData() );

                String newFileName = baseFileName + ( useTimestamp ? sdf.format( new Date() ) : "" ) + fileExtension;
                String tempFileName = newFileName;
                if ( useTempFileName ) {
                    tempFileName = newFileName + ".part";
                }

                channelSftp.put( bais, tempFileName );
                LOG.trace( "Uploaded file: " + tempFileName );

                if ( useTempFileName ) {
                    channelSftp.rename( tempFileName, newFileName );
                    LOG.trace( "Renamed file " + tempFileName + " to " + newFileName );
                }
            }

        } catch ( Exception e ) {
            LOG.error(new LogMessage("Error uploading to SFTP account (" + messageContext.getMessagePojo().getParticipant().getConnection().getUri() + ")", messageContext, e), e);
            // bugfix: #10
            if ( e instanceof NexusException ) {
                throw (NexusException) e;
            } else {
                throw new NexusException(new LogMessage("Error uploading to SFTP account (" + messageContext.getMessagePojo().getParticipant().getConnection().getUri() + ")", messageContext, e), e);
            }
        } finally {
            if ( ( channelSftp != null ) && channelSftp.isConnected() ) {
                channelSftp.disconnect();
            }
        }
        
        return messageContext;
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
