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

import java.util.Map;

import org.nexuse2e.NexusException;
import org.nexuse2e.Constants.Layer;
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
    public Layer getActivationLayer() {
        return Layer.OUTBOUND_PIPELINES;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.service.SenderAware#sendMessage(org.nexuse2e.messaging.MessageContext)
     */
    public MessageContext sendMessage( MessageContext message ) throws NexusException {
        return null;
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
