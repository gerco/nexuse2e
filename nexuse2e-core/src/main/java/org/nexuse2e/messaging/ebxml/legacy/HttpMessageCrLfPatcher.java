package org.nexuse2e.messaging.ebxml.legacy;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.nexuse2e.NexusException;
import org.nexuse2e.messaging.AbstractPipelet;
import org.nexuse2e.messaging.MessageContext;

/**
 * Patches outbound messages by adding CR/LF characters to the message payload. 
 *
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public class HttpMessageCrLfPatcher extends AbstractPipelet {

    private static Logger LOG = Logger.getLogger(HttpMessageCrLfPatcher.class);

    private String CRLF = "\r\n";

    /**
     * Default constructor.
     */
    public HttpMessageCrLfPatcher() {

        parameters = new HashMap<String, Object>();
        frontendPipelet = true;
    }

    
    
    @Override
    public MessageContext processMessage(MessageContext messageContext)
            throws IllegalArgumentException, IllegalStateException, NexusException {

        try {
            Object data = messageContext.getData();
            if (data instanceof byte[]) {
                byte[] payload = (byte[]) data;
                byte[] crlf = CRLF.getBytes("utf-8");
                byte[] copy = new byte[payload.length + crlf.length];
                System.arraycopy(payload, 0, copy, 0, payload.length);
                System.arraycopy(crlf, 0, copy, payload.length, crlf.length);
                messageContext.setData(copy);
                LOG.info("Appended CRLF to message data");
            } else {
                LOG.info("Skipped payload of type " + (data != null ? data.getClass().getName() : "null"));
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        
        return messageContext;
    }

}
