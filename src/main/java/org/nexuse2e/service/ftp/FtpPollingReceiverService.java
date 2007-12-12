package org.nexuse2e.service.ftp;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.Constants.Layer;
import org.nexuse2e.configuration.ConfigurationAccessService;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.pojo.MessagePojo;
import org.nexuse2e.service.ReceiverAware;
import org.nexuse2e.transport.TransportReceiver;

/**
 * This service implementation acts as an FTP(S) client and receives files by
 * polling an FTP server from time to time. The file content will be processed
 * by an associated {@link TransportReceiver} implementation.
 *
 * @author jonas.reese
 * @version $LastChangedRevision$ - $LastChangedDate$ by $LastChangedBy$
 */
public class FtpPollingReceiverService extends AbstractFtpService implements ReceiverAware {

    private static Logger      LOG                         = Logger.getLogger( FtpPollingReceiverService.class );

    public static final String CUSTOM_PARAMETER_FILE_NAME  = "fileName";
    public static final String CUSTOM_PARAMETER_PARTNER_ID = "partnerId";
    public static final String CUSTOM_PARAMETER_URL        = "url";

    private TransportReceiver  transportReceiver;

    
    @Override
    public void fillParameterMap( Map<String, ParameterDescriptor> parameterMap ) {

        super.fillParameterMap( parameterMap );
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
    protected void processFile( File file, File errorDir, String partnerId ) throws NexusException {

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
                throw new NexusException( "An error occurred while processing file " + file, ex );
            }
        } else {
            if ( transportReceiver == null ) {
                LOG.error( "No TransportReceiverAvailable!" );
            } else {
                LOG.error( "No file to process!" );
            }
        }
    }
}
