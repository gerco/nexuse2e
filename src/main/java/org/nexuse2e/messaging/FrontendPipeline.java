/**
 * NEXUSe2e Business Messaging Open Source  
 * Copyright 2007, Tamgroup and X-ioma GmbH   
 *  
 * This is free software; you can redistribute it and/or modify it  
 * under the terms of the GNU Lesser General Public License as  
 * published by the Free Software Foundation version 2.1 of  
 * the License.  
 *  
 * This software is distributed in the hope that it will be useful,  
 * but WITHOUT ANY WARRANTY; without even the implied warranty of  
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU  
 * Lesser General Public License for more details.  
 *  
 * You should have received a copy of the GNU Lesser General Public  
 * License along with this software; if not, write to the Free  
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.nexuse2e.messaging;

import org.nexuse2e.NexusException;
import org.nexuse2e.ProtocolSpecific;
import org.nexuse2e.ProtocolSpecificKey;
import org.nexuse2e.pojo.MessagePojo;

/**
 * A <code>Pipeline</code> handling the processing of messages in the NEXUSe2e frontend. 
 * A <code>FrontendPipeline</code> can optionally process a synchronously generated reply
 * (represented by a new <code>MessageContext</code>) and will trigger a different
 * set of <code>Pipelet</code> components for processing it. This set of <code>Pipelet</code> 
 * components be similar to that of an outbound <code>FrontendPipeline</code> processing 
 * outbound messages or asynchronously created reply messages.
 *
 * @author mbreilmann
 */
public class FrontendPipeline extends AbstractPipeline implements ProtocolSpecific {

    
// to be tested , InitializingBean
    

    private ProtocolSpecificKey key;

    /**
     * Constructs a new empty <code>FrontendPipeline</code>.
     */
    public FrontendPipeline() {
        
    }
    
    /**
     * Process a message in the NEXUSe2e frontent. Processing of inbound messages could contains steps like these:
     * <ol>
     * <li>The massage data is unpacked using an approriate MessageUnpackager</li>
     * <li>The massage header is de-serialized using an approriate HeaderDeserializer</li>
     * </ol>
     * @param messageContext The message progressing through the <code>Pipeline</code>. 
     * The <code>MessageContext</code> contains additional meta data useful for the processing 
     * of the message in addition to the actual message itself.
     * @return The potentially modified The <code>MessageContext</code>.
     * @throws IllegalArgumentException Thrown if information provided in the <code>MessageContext</code> 
     * did not meet expectations.
     * @throws IllegalStateException Thrown if the system is not in a correct state to handle this specific message.
     * @throws NexusException Thrown if any other processing related exception occured.
     */
    public MessageContext processMessage( MessageContext messageContext )
            throws IllegalArgumentException, IllegalStateException, NexusException {

        if ( !validateProtocolSpecificKey( messageContext.getProtocolSpecificKey() ) ) {
            throw new IllegalArgumentException( "PipelineKey:" + getKey() + " doesn't match MessageKey:"
                    + messageContext.getProtocolSpecificKey() );
        }

        if ( messageContext == null ) {
            throw new IllegalArgumentException( "No content found" );
        }

        if ( forwardPipelets == null ) {
            throw new IllegalStateException( "MessageUnpackager not configured/instantiated!" );
        }

        if ( messageContext.getMessagePojo() == null ) {
            messageContext.setMessagePojo( new MessagePojo() );
            messageContext.setOriginalMessagePojo( messageContext.getMessagePojo() );
        }

        try {
            for ( int i = 0; i < forwardPipelets.length; i++ ) {
                MessageProcessor messagePipelet = forwardPipelets[i];

                messageContext = messagePipelet.processMessage( messageContext );
            }
    
        } catch ( Exception e ) {
            e.printStackTrace();
            
            if( messageContext.getErrors() == null || messageContext.getErrors().size() == 0) {
                messageContext.addError( new ErrorDescriptor("Pipelet processing aborted: "+e) );
            }
            
        }
                
        // Set conversation on context, should be available now
        if(messageContext.getMessagePojo() != null) {
            messageContext.setConversation( messageContext.getMessagePojo().getConversation() );
        }
        
        messageContext = pipelineEndpoint.processMessage( messageContext );

        if ( returnPipelets != null ) {
            for ( int i = 0; i < returnPipelets.length; i++ ) {
                MessageProcessor messagePipelet = returnPipelets[i];

                messageContext = messagePipelet.processMessage( messageContext );
            }
        }

        return messageContext;
    } // processMessage

    /**
     * @param protocolSpecificKey
     * @return
     */
    private boolean validateProtocolSpecificKey( ProtocolSpecificKey protocolSpecificKey ) {

        if ( getKey().equals( protocolSpecificKey ) ) {
            return true;
        }

        return false;

    }

    

    /**
     * @return the key
     */
    public ProtocolSpecificKey getKey() {

        return key;
    }

    /**
     * @param key the key to set
     */
    public void setKey( ProtocolSpecificKey key ) {

        this.key = key;
    }

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet() throws Exception {

        if ( getKey() == null ) {
            throw new InstantiationException( "no valid protocol key specified" );

        }

    }

} // FrontendDispatcher
