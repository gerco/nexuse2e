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
package org.nexuse2e.messaging.generic;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.nexuse2e.Constants;
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.ParameterType;
import org.nexuse2e.messaging.AbstractPipelet;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.pojo.MessagePayloadPojo;
import org.nexuse2e.pojo.MessagePojo;

/**
 * Created: 19.07.2007
 *
 * @author jonas.reese
 * @version $LastChangedRevision$ - $LastChangedDate$ by $LastChangedBy$
 */
public class RawDataMessagePipelet extends AbstractPipelet {

    private static Logger       LOG                  = Logger.getLogger( RawDataMessagePipelet.class );

    private static final String MIME_TYPE_PARAM_NAME = "mimeType";
    private static final String SET_CONTENT_ID       = "setContentId";
    private static final String USE_CUSTOM_PARAM     = "useCustomParam";

    private boolean             setContentId         = false;
    private String              useCustomParam       = null;

    public RawDataMessagePipelet() {

        forwardPipelet = true;
        frontendPipelet = true;
        parameterMap.put( MIME_TYPE_PARAM_NAME, new ParameterDescriptor( ParameterType.STRING, "MIME type",
                "The MIME type of the payload", "text/plain" ) );
        parameterMap.put( SET_CONTENT_ID, new ParameterDescriptor( ParameterType.BOOLEAN, "Set Content ID",
                "Flag whether to set the content ID based on a custom parameter", Boolean.FALSE ) );

        parameterMap.put( USE_CUSTOM_PARAM, new ParameterDescriptor( ParameterType.STRING, "Custom parameter name",
                "The custom paramter to use to set the content ID.", "fileName" ) );

    }

    @Override
    public void initialize( EngineConfiguration config ) throws InstantiationException {

        useCustomParam = getParameter( USE_CUSTOM_PARAM );

        Boolean tempFlag = getParameter( SET_CONTENT_ID );
        if ( !StringUtils.isEmpty( useCustomParam ) && tempFlag != null && tempFlag.equals( Boolean.TRUE ) ) {
            setContentId = true;
            LOG.info( "Setting specific content ID." );
        }
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.AbstractPipelet#processMessage(org.nexuse2e.messaging.MessageContext)
     */
    @Override
    public MessageContext processMessage( MessageContext messageContext ) throws IllegalArgumentException,
            IllegalStateException, NexusException {

        MessagePojo messagePojo = messageContext.getMessagePojo();
        MessagePayloadPojo messagePayloadPojo = new MessagePayloadPojo();
        messagePayloadPojo.setMessage( messagePojo );
        String contentId = Engine.getInstance().getIdGenerator( Constants.ID_GENERATOR_MESSAGE_PAYLOAD ).getId();
        if ( setContentId && messagePojo.getCustomParameters().get( useCustomParam ) != null ) {
            contentId = messagePojo.getCustomParameters().get( useCustomParam );
        }
        messagePayloadPojo.setContentId( contentId );
        // ensure that at least the default value will be used
        String mimeType = (String) getParameter( MIME_TYPE_PARAM_NAME );
        messagePayloadPojo.setMimeType( mimeType != null ? mimeType : "text/plain" );
        messagePayloadPojo.setPayloadData( (byte[]) messageContext.getData() );
        List<MessagePayloadPojo> messagePayloads = new ArrayList<MessagePayloadPojo>( 1 );
        messagePayloads.add( messagePayloadPojo );
        messagePojo.setMessagePayloads( messagePayloads );
        return messageContext;
    }

}
