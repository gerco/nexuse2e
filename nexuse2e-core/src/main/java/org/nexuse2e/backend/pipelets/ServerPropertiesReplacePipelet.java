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
package org.nexuse2e.backend.pipelets;

import java.io.UnsupportedEncodingException;

import org.nexuse2e.NexusException;
import org.nexuse2e.logging.LogMessage;
import org.nexuse2e.messaging.AbstractPipelet;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.pojo.MessagePayloadPojo;
import org.nexuse2e.util.ServerPropertiesUtil;

/**
 * This pipelet replaces the server property variables in the payload section(s) with the
 * variable values.
 *
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public class ServerPropertiesReplacePipelet extends AbstractPipelet {

    @Override
    public MessageContext processMessage( MessageContext messageContext )
            throws IllegalArgumentException, IllegalStateException, NexusException {

        for (MessagePayloadPojo payload : messageContext.getMessagePojo().getMessagePayloads()) {
            try {
				String s = new String( payload.getPayloadData(), messageContext.getEncoding() );
				s = ServerPropertiesUtil.replaceServerProperties( s, messageContext );
				payload.setPayloadData( s.getBytes( messageContext.getEncoding() ) );
			} catch (UnsupportedEncodingException e) {
				throw new NexusException(new LogMessage("configured encoding '"+messageContext.getEncoding()+"' is not supported",messageContext),e);
			}
        }
        
        return messageContext;
    }

}
