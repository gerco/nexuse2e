package org.nexuse2e.service.ftp;

import java.util.Map;

import org.nexuse2e.NexusException;
import org.nexuse2e.Constants.Runlevel;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.messaging.MessageContext;
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

    private TransportSender transportSender;
    
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
        return Runlevel.OUTBOUND_PIPELINES;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.service.SenderAware#sendMessage(org.nexuse2e.messaging.MessageContext)
     */
    public void sendMessage( MessageContext message ) throws NexusException {
        // TODO Auto-generated method stub
        
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
