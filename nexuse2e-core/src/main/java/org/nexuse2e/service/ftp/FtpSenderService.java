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
package org.nexuse2e.service.ftp;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;
import org.nexuse2e.NexusException;
import org.nexuse2e.Constants.Layer;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.configuration.ListParameter;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.Constants.ParameterType;
import org.nexuse2e.logging.LogMessage;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.pojo.ConnectionPojo;
import org.nexuse2e.pojo.MessagePayloadPojo;
import org.nexuse2e.service.AbstractService;
import org.nexuse2e.service.SenderAware;
import org.nexuse2e.transport.TransportSender;

/**
 * Created: 12.07.2007
 * <p>
 * Service implementation for sending via the FTP protocol.
 * </p>
 *
 * @author jonas.reese
 * @version $LastChangedRevision$ - $LastChangedDate$ by $LastChangedBy$
 */
public class FtpSenderService extends AbstractService implements SenderAware {

    private static Logger      LOG                       = Logger.getLogger( FtpSenderService.class );

    public static final String BASE_FILE_NAME_PARAM_NAME = "baseFileName";
    public static final String FILE_EXTENSION_PARAM_NAME = "fileExtension";
    public static final String TEMP_FILE_PARAM_NAME      = "useTempFile";
    public static final String APPEND_TIMESTAMP          = "appendTimestamp";
    public static final String TIMESTAMP_PATTERN         = "timestampPattern";
    public static final String USE_CONTENT_ID            = "useContentId";
    public static final String TRANSFER_MODE_PARAM_NAME  = "transferMode";

    public static final String DEFAULT_TIMESTAMP_PATTERN = "yyyyMMddHHmmssSSS";

    private TransportSender    transportSender           = null;
    private String             baseFileName              = null;
    private String             fileExtension             = null;
    private boolean            useTimestamp              = true;
    private String             timestampPattern          = DEFAULT_TIMESTAMP_PATTERN;
    private boolean            useContentId              = false;
    private boolean            useTempFileName           = true;

    /* (non-Javadoc)
     * @see org.nexuse2e.service.AbstractService#fillParameterMap(java.util.Map)
     */
    @Override
    public void fillParameterMap( Map<String, ParameterDescriptor> parameterMap ) {

        parameterMap.put( USE_CONTENT_ID, new ParameterDescriptor( ParameterType.BOOLEAN, "Use Content ID",
                "Flag whether to use the content ID as the file name", Boolean.TRUE ) );

        parameterMap.put( BASE_FILE_NAME_PARAM_NAME, new ParameterDescriptor( ParameterType.STRING, "Base file name",
                "The base file name used in uploads", "upload_" ) );

        parameterMap.put( FILE_EXTENSION_PARAM_NAME, new ParameterDescriptor( ParameterType.STRING, "File extension",
                "The file extension used in uploads", ".xml" ) );

        parameterMap.put( TEMP_FILE_PARAM_NAME, new ParameterDescriptor( ParameterType.BOOLEAN,
                "Use temporary file name", "Use temporary file name during upload", Boolean.TRUE ) );

        parameterMap.put( APPEND_TIMESTAMP, new ParameterDescriptor( ParameterType.BOOLEAN, "Append Timestamp",
                "Flag whether to append a timestamp to the file name", Boolean.TRUE ) );

        parameterMap
                .put(
                        TIMESTAMP_PATTERN,
                        new ParameterDescriptor(
                                ParameterType.STRING,
                                "Timestamp Pattern",
                                "The pattern of the timestamp that should be appended. Use pattern syntax of <a href=\"http://java.sun.com/j2se/1.5.0/docs/api/java/text/SimpleDateFormat.html\">SimpleDateFormat</a>.",
                                DEFAULT_TIMESTAMP_PATTERN ) );
        
        ListParameter transferModeListParam = new ListParameter();
        transferModeListParam.addElement( "Auto", "auto" );
        transferModeListParam.addElement( "Binary", "binary" );
        transferModeListParam.addElement( "ASCII", "ascii" );
        parameterMap.put( TRANSFER_MODE_PARAM_NAME, new ParameterDescriptor( ParameterType.LIST, "Transfer mode",
                "Use Automatic/Binary/ASCII transfer mode (default is Auto)", transferModeListParam ) );

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

        tempFlag = getParameter( USE_CONTENT_ID );
        if ( tempFlag != null && tempFlag.equals( Boolean.TRUE ) ) {
            useContentId = true;
            LOG.info( "Using content ID in file name." );
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
     * @see org.nexuse2e.service.AbstractService#getActivationRunlevel()
     */
    @Override
    public Layer getActivationLayer() {

        return Layer.OUTBOUND_PIPELINES;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.service.SenderAware#sendMessage(org.nexuse2e.messaging.MessageContext)
     */
    public MessageContext sendMessage( MessageContext messageContext ) throws NexusException {

        FTPClient ftpClient = null;

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

            LOG.trace( "Trying to connect to " + url.getHost() + " with user " + connection.getLoginName() + "..." );
            ftpClient = new FTPClient();
            ftpClient.connect( url.getHost() );
            int reply = ftpClient.getReplyCode();

            if ( FTPReply.isPositiveCompletion( reply ) ) {

                // only set password, if specified
                if ( StringUtils.isEmpty( connection.getPassword() ) ) {
                    LOG.warn( "No password provided!" );
                }

                if ( ftpClient.login( connection.getLoginName(), connection.getPassword() ) ) {
                    LOG.trace( "Connected to " + url.getHost() + "." );

                    ListParameter transfermode = getParameter( TRANSFER_MODE_PARAM_NAME );
                    if (transfermode != null && "binary".equals( transfermode.getSelectedValue() )) {
                        ftpClient.setFileType( FTP.BINARY_FILE_TYPE );
                        if ( LOG.isDebugEnabled() ) {
                            LOG.debug( "Entered binary mode" );
                        }
                    }
                    
                    LOG.trace( "Directory URL Path: " + url.getPath() );
                    String directory = url.getPath();
                    if ( directory.startsWith( "/" ) ) {
                        directory = directory.substring( 1 );
                    }

                    if ( StringUtils.isNotEmpty( directory ) ) {
                        LOG.trace( "Directory requested: " + directory );
                        try {
                            reply = ftpClient.cwd( directory );
                            if ( !FTPReply.isPositiveCompletion( reply ) ) {
                                throw new NexusException( new LogMessage( "FTP server did not change directory",
                                        messageContext.getMessagePojo() ) );
                            }
                        } catch ( IOException ioEx ) {
                            throw new NexusException( new LogMessage( "FTP server did not change directory",
                                    messageContext.getMessagePojo() ), ioEx );
                        }
                    }

                    SimpleDateFormat sdf = new SimpleDateFormat( timestampPattern );
                    for ( MessagePayloadPojo messagePayloadPojo : messageContext.getMessagePojo().getMessagePayloads() ) {
                        ByteArrayInputStream bais = new ByteArrayInputStream( messagePayloadPojo.getPayloadData() );
                        
                        String contentId = messagePayloadPojo.getContentId();
                        if ( contentId.indexOf( "__body" ) != -1 ) {
                            contentId = contentId.substring( 0, messagePayloadPojo.getContentId().indexOf( "__body" ) );
                        }

                        String newFileName = baseFileName + (useContentId ? contentId : "") +( useTimestamp ? "_" + sdf.format( new Date() ) : "" )
                                + fileExtension;
                        String tempFileName = newFileName;
                        if ( useTempFileName ) {
                            tempFileName = newFileName + ".part";
                        }

                        ftpClient.storeFile( tempFileName, bais );
                        LOG.trace( "Uploaded file: " + tempFileName );

                        if ( useTempFileName ) {
                            ftpClient.rename( tempFileName, newFileName );
                            LOG.trace( "Renamed file " + tempFileName + " to " + newFileName );
                        }
                    }

                    ftpClient.logout();
                }

            } else {
                ftpClient.disconnect();
            }

        } catch ( Exception e ) {
            e.printStackTrace();
            LOG.error( new LogMessage( "Error uploading to SFTP account ("
                    + messageContext.getMessagePojo().getParticipant().getConnection().getUri() + "): " + e,
                    messageContext.getMessagePojo() ), e );
            // bugfix: #10
            if ( e instanceof NexusException ) {
                throw (NexusException) e;
            } else {
                throw new NexusException( new LogMessage( "Error uploading to SFTP account ("
                        + messageContext.getMessagePojo().getParticipant().getConnection().getUri() + ")",
                        messageContext.getMessagePojo() ), e );
            }
        } finally {
            if ( ftpClient.isConnected() ) {
                try {
                    ftpClient.disconnect();
                } catch ( IOException ioe ) {
                    // do nothing
                }
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

}
