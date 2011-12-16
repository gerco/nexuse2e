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
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.Constants.ParameterType;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.logging.LogMessage;
import org.nexuse2e.messaging.AbstractPipelet;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.pojo.MessagePayloadPojo;
import org.nexuse2e.service.Service;
import org.nexuse2e.service.mail.SmtpSender;

/**
 * Pipelet for debugging purposes.
 *
 * @author Jonas Reese
 */
public class XMLSmtpSenderBackendPipelet extends AbstractPipelet {

    private static Logger LOG = Logger.getLogger( AbstractPipelet.class );
    
    public static final String SENDER_SERVICE = "sender_service";
    public static final String RECEIVER_PARAM_NAME = "receiver";
//    public static final String CC_PARAM_NAME = "cc";
//    public static final String BCC_PARAM_NAME = "bcc";
    public static final String SUBJECT_PARAM_NAME = "printPayload";

    private SmtpSender smtpSenderSerivce= null;
    private String receiver = null;
//    private String cc = null;
//    private String bcc = null;
    private String subject = null;
    
    // TODO: It might be an idea to add support for selecting MIME-Types to the AbstractPipelet, and facilitate support for user-chosen filtering in the GUI
    @SuppressWarnings("serial")
	private static final List<String> ACCEPTED_MIME_TYPES = new ArrayList<String>() {{
    	add("text/xml");
    	add("application/xhtml+xml");
    	add("application/xml");
    }};
    
    public XMLSmtpSenderBackendPipelet() {
        parameterMap.put( SENDER_SERVICE, new ParameterDescriptor( ParameterType.SERVICE, "SMTP Sender Service",
            "The SMTP Service", SmtpSender.class ) );
        parameterMap.put( RECEIVER_PARAM_NAME, new ParameterDescriptor( ParameterType.STRING, "Email Receiver",
                "The email address of the receiver", "" ) );
//        parameterMap.put( CC_PARAM_NAME, new ParameterDescriptor( ParameterType.STRING, "Email CC",
//            "The email CC", "" ) );
//        parameterMap.put( BCC_PARAM_NAME, new ParameterDescriptor( ParameterType.STRING, "Email BCC",
//            "The email BCC", "" ) );
        parameterMap.put( SUBJECT_PARAM_NAME, new ParameterDescriptor( ParameterType.STRING, "The Email Subject",
                "The email subject", "" ) );
        setFrontendPipelet( false );
    }
    
    @Override
    public MessageContext processMessage( MessageContext messageContext )
            throws IllegalArgumentException, IllegalStateException,
            NexusException {
        
    	// Fetch sender service if it isn't set
        String senderServiceName = getParameter(SENDER_SERVICE);
        if(!StringUtils.isEmpty( senderServiceName )) {
            Service service = Engine.getInstance().getActiveConfigurationAccessService().getService( senderServiceName );
            if(service != null && service instanceof SmtpSender) {
                this.smtpSenderSerivce = (SmtpSender)service;
            }
        }
        
        // Fetch mail info
        String receiverParam = getParameter(RECEIVER_PARAM_NAME);
        if (receiverParam != null && receiverParam.trim().length() > 0) {
            this.receiver = receiverParam;
        }
        String subParam = getParameter(SUBJECT_PARAM_NAME);
        if (subParam != null && subParam.trim().length() > 0) {
        	this.subject = subParam;
        }
        
        if (receiver != null && subject != null && messageContext != null && messageContext.getMessagePojo() != null) {
            List<MessagePayloadPojo> list = messageContext.getMessagePojo().getMessagePayloads();
            if (list != null) {
                for (MessagePayloadPojo payload : list) {
                	// Filter by MIME-type
                	String payloadMimeType = payload.getMimeType();
                	if (null == payloadMimeType 
                			|| "".equals(payloadMimeType)
                			|| !ACCEPTED_MIME_TYPES.contains(payloadMimeType)) {
                		continue;
                	}
                	// Hoch damit und raus mit ihnen
                    byte[] data = payload.getPayloadData();
                    LOG.info( "Payload " + payload.getContentId() + ", mime-type " + payload.getMimeType() );
                    if (data != null) {
                    	String encoding = messageContext.getEncoding();
                    	try {
							smtpSenderSerivce.sendMessage(receiver, subject, new String(data, encoding));
							
							LOG.info(new LogMessage( new String( data, encoding ),messageContext) );
						} catch (UnsupportedEncodingException e) {
							LOG.warn(new LogMessage("configured payload encoding '"+encoding+"' is not supported", messageContext));
							LOG.info(new LogMessage( new String( data ), messageContext) ); // use jvm encoding
						}
                    } else {
                        LOG.info( null );
                    }
                }
            }
        }
        
        return messageContext;
    }
}
