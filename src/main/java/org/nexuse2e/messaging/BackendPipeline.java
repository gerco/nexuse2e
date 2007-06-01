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

import org.apache.log4j.Logger;
import org.nexuse2e.ActionSpecific;
import org.nexuse2e.ActionSpecificKey;
import org.nexuse2e.NexusException;

/**
 * A <code>Pipeline</code> handling the processing of messages in the NEXUSe2e backend. 
 * It will trigger a set of <code>Pipelet</code> components for actually processing the messages.
 * 
 * @author mbreilmann
 */
public class BackendPipeline extends AbstractPipeline implements ActionSpecific {

    private static Logger     LOG = Logger.getLogger( BackendPipeline.class );

    /**
     * The key identifying this pipeline
     */
    private ActionSpecificKey key = null;

    // private int               nxPipelineId     = 0;
    // private String            name             = null;

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.Pipeline#processMessage(org.nexuse2e.messaging.MessageContext)
     */
    public MessageContext processMessage( MessageContext messageContext ) throws IllegalArgumentException,
            IllegalStateException, NexusException {

        LOG.debug( "BackendPipeline.processMessage..." );

        if ( messageContext == null ) {
            throw new IllegalArgumentException( "No content found" );
        }

        if ( pipelineEndpoint == null ) {
            throw new NexusException( "PipelineEndpoint must not be null!" );
        }

        if ( messageContext.getMessagePojo() == null ) {
            throw new IllegalStateException( "MessagePojo must not be null" );
        }

        try {
            if ( forwardPipelets != null ) {

                LOG.debug( "pipeletCount=" + forwardPipelets.length );
                for ( int i = 0; i < forwardPipelets.length; i++ ) {
                    LOG.debug( "processing pipelet[" + i + "]" );
                    MessageProcessor backendPipelet = forwardPipelets[i];

                    messageContext = backendPipelet.processMessage( messageContext );
                    if ( messageContext == null ) {
                        LOG
                                .warn( "Pipelet " + backendPipelet.getClass()
                                        + " did not return a MessageContext instance!" );
                    }
                }
            } else {
                LOG.error( "No pipelets found." );
            }
            pipelineEndpoint.processMessage( messageContext );

        } catch ( Exception e ) {
            if ( LOG.isTraceEnabled() ) {
                e.printStackTrace();
            }
            LOG.error( "Error processing backend pipeline: " + e );
            throw new NexusException( "Error processing backend pipeline: " + e );
        }

        return messageContext;
    } // processMessage

    /* (non-Javadoc)
     * @see org.nexuse2e.ActionSpecific#getKey()
     */
    public ActionSpecificKey getKey() {

        return key;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.ActionSpecific#setKey(org.nexuse2e.ActionSpecificKey)
     */
    public void setKey( ActionSpecificKey key ) {

        this.key = key;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.Pipeline#getPipelineEndpoint()
     */
    public MessageProcessor getPipelineEndpoint() {

        return pipelineEndpoint;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.Pipeline#setPipelineEndpoint(org.nexuse2e.messaging.Pipelet)
     */
    public void setPipelineEndpoint( MessageProcessor pipelineEndpoint ) {

        this.pipelineEndpoint = pipelineEndpoint;
    }

    /*
     public String getName() {

     return name;
     }

     public void setName( String name ) {

     this.name = name;
     }

     public int getNxPipelineId() {

     return nxPipelineId;
     }

     public void setNxPipelineId( int nxPipelineId ) {

     this.nxPipelineId = nxPipelineId;
     }
     */

} // BackendPipeline
