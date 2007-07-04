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
import org.nexuse2e.Constants.BeanStatus;
import org.nexuse2e.Constants.Runlevel;
import org.nexuse2e.configuration.EngineConfiguration;

abstract public class AbstractPipeline implements Pipeline {

    protected boolean          frontendPipeline;
    protected boolean          outboundPipeline;

    /**
     * The pipelets used for processing the message.
     */
    protected Pipelet[]        forwardPipelets  = null;

    /**
     * The pipelets used for processing an optionally synchronously created return message in frontend pipelines.
     */
    protected Pipelet[]        returnPipelets   = null;

    /**
     * The endpoint of this pipeline handling the message when this pipeline is done processing it.
     */
    protected MessageProcessor pipelineEndpoint = null;

    abstract public MessageContext processMessage( MessageContext messageContext ) throws IllegalArgumentException,
            IllegalStateException, NexusException;

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

    /**
     * @return the forwardPipeline
     */
    public MessageProcessor[] getForwardPipelets() {

        return forwardPipelets;
    }

    /**
     * @param forwardPipeline the forwardPipeline to set
     */
    public void setForwardPipelets( Pipelet[] forwardPipelets ) {

        this.forwardPipelets = forwardPipelets;
    }

    /**
     * @return the returnPipeline
     */
    public MessageProcessor[] getReturnPipelets() {

        return returnPipelets;
    }

    /**
     * @param returnPipeline the returnPipeline to set
     */
    public void setReturnPipelets( Pipelet[] returnPipelets ) {

        this.returnPipelets = returnPipelets;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#activate()
     */
    public void activate() {

        if ( forwardPipelets != null ) {
            for ( int i = 0; i < forwardPipelets.length; i++ ) {
                forwardPipelets[i].activate();
            }
        }
        if ( returnPipelets != null ) {
            for ( int i = 0; i < returnPipelets.length; i++ ) {
                returnPipelets[i].activate();
            }
        }
        if ( getPipelineEndpoint() != null ) {
            ( (Pipelet) getPipelineEndpoint() ).activate();
        }
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#deactivate()
     */
    public void deactivate() {

        if ( forwardPipelets != null ) {
            for ( int i = 0; i < forwardPipelets.length; i++ ) {
                forwardPipelets[i].deactivate();
            }
        }
        if ( returnPipelets != null ) {
            for ( int i = 0; i < returnPipelets.length; i++ ) {
                returnPipelets[i].deactivate();
            }
        }
        if ( getPipelineEndpoint() != null ) {
            ( (Pipelet) getPipelineEndpoint() ).deactivate();
        }
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#getActivationRunlevel()
     */
    public Runlevel getActivationRunlevel() {

        if ( isOutboundPipeline() ) {
            return Runlevel.OUTBOUND_PIPELINES;
        }
        return Runlevel.INBOUND_PIPELINES;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#getStatus()
     */
    public BeanStatus getStatus() {

        try {
            BeanStatus minimumStatus = BeanStatus.STARTED;
            if ( forwardPipelets != null ) {
                for ( int i = 0; i < forwardPipelets.length; i++ ) {
                    if ( forwardPipelets[i] == null ) {
                        return BeanStatus.ERROR;
                    }
                    if ( forwardPipelets[i].getStatus().getValue() < minimumStatus.getValue() ) {
                        minimumStatus = forwardPipelets[i].getStatus();
                    }
                }
            }
            if ( returnPipelets != null ) {
                for ( int i = 0; i < returnPipelets.length; i++ ) {
                    if ( returnPipelets[i] == null ) {
                        return BeanStatus.ERROR;
                    }
                    if ( returnPipelets[i].getStatus().getValue() < minimumStatus.getValue() ) {
                        minimumStatus = returnPipelets[i].getStatus();
                    }
                }
            }
            if ( getPipelineEndpoint() == null ) {
                return BeanStatus.ERROR;
            }
            if ( getPipelineEndpoint() != null && getPipelineEndpoint() instanceof Pipelet ) {
                if ( ( (Pipelet) getPipelineEndpoint() ).getStatus().getValue() < minimumStatus.getValue() ) {
                    minimumStatus = ( (Pipelet) getPipelineEndpoint() ).getStatus();
                }
            }

            return minimumStatus;
        } catch ( Exception e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return BeanStatus.UNDEFINED;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#initialize(org.nexuse2e.configuration.EngineConfiguration)
     */
    public void initialize( EngineConfiguration config ) throws InstantiationException {

        if ( forwardPipelets != null ) {
            for ( int i = 0; i < forwardPipelets.length; i++ ) {
                forwardPipelets[i].initialize( null );
            }
        }
        if ( returnPipelets != null ) {
            for ( int i = 0; i < returnPipelets.length; i++ ) {
                returnPipelets[i].initialize( null );
            }
        }
        if ( getPipelineEndpoint() != null ) {
            ( (Pipelet) getPipelineEndpoint() ).initialize( config );
        }
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.Manageable#teardown()
     */
    public void teardown() {

        if ( forwardPipelets != null ) {
            for ( int i = 0; i < forwardPipelets.length; i++ ) {
                forwardPipelets[i].teardown();
            }
        }
        if ( returnPipelets != null ) {
            for ( int i = 0; i < returnPipelets.length; i++ ) {
                returnPipelets[i].teardown();
            }
        }
        if ( getPipelineEndpoint() != null ) {
            ( (Pipelet) getPipelineEndpoint() ).teardown();
        }
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.Pipeline#isFrontendPipeline()
     */
    public boolean isFrontendPipeline() {

        return frontendPipeline;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.Pipeline#isOutbountPipeline()
     */
    public boolean isOutboundPipeline() {

        return outboundPipeline;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.Pipeline#setFrontendPipeline(boolean)
     */
    public void setFrontendPipeline( boolean isFrontendPipeline ) {

        frontendPipeline = isFrontendPipeline;
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.Pipeline#setOutboundPipeline(boolean)
     */
    public void setOutboundPipeline( boolean isOutboundPipeline ) {

        outboundPipeline = isOutboundPipeline;
    }

}
