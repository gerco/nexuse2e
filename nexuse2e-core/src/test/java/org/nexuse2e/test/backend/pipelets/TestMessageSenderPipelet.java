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
package org.nexuse2e.test.backend.pipelets;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.activation.FileTypeMap;
import javax.activation.MimetypesFileTypeMap;

import org.apache.log4j.Logger;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.messaging.AbstractPipelet;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.pojo.MessagePayloadPojo;
import org.nexuse2e.service.SchedulerClient;

public class TestMessageSenderPipelet extends AbstractPipelet implements SchedulerClient {

    private static Logger       LOG             = Logger.getLogger( TestMessageSenderPipelet.class );

    private final static String DEFAULT_PAYLOAD = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<test from=\"Xioma\"/>";

    private String              mimeType        = null;

    //  The binary data buffer that will hold the document
    private byte[]              documentBuffer  = DEFAULT_PAYLOAD.getBytes();

    public void activate() {

        // Determine the MIME type of the document
        MimetypesFileTypeMap mimetypesFileTypeMap = (MimetypesFileTypeMap) FileTypeMap.getDefaultFileTypeMap();
        mimeType = mimetypesFileTypeMap.getContentType( "dummy.xml" );

        super.activate();
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.AbstractPipelet#processMessage(org.nexuse2e.messaging.MessageContext)
     */
    @Override
    public MessageContext processMessage( MessageContext messageContext ) throws IllegalArgumentException,
            IllegalStateException, NexusException {

        List<MessagePayloadPojo> messagePayloads = new ArrayList<MessagePayloadPojo>();

        // Prepare the Payload and set the MIME content type
        MessagePayloadPojo messagePayloadPojo = new MessagePayloadPojo( messageContext.getMessagePojo(), 0, mimeType,
                Engine.getInstance().getIdGenerator( org.nexuse2e.Constants.ID_GENERATOR_MESSAGE_PAYLOAD ).getId(),
                documentBuffer, new Date(), new Date(), 1 );
        messagePayloads.add( messagePayloadPojo );

        messageContext.getMessagePojo().setMessagePayloads( messagePayloads );

        LOG.debug( "Created test message" );

        return messageContext;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.service.SchedulerClient#scheduleNotify()
     */
    public void scheduleNotify() {

    }

} // TestMessageSenderPipelet
