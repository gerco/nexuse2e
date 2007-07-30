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

import java.io.Serializable;

import org.nexuse2e.ActionSpecificKey;
import org.nexuse2e.ProtocolSpecificKey;
import org.nexuse2e.pojo.ChoreographyPojo;
import org.nexuse2e.pojo.ConversationPojo;
import org.nexuse2e.pojo.MessagePojo;
import org.nexuse2e.pojo.ParticipantPojo;
import org.nexuse2e.pojo.PartnerPojo;

/**
 * Wrapper class that combines a message with additional meta data required for processing.
 *
 * @author mbreilmann
 */
public class MessageContext implements Serializable {

    /**
     * 
     */
    private static final long   serialVersionUID    = 5184344055202810866L;
    private MessagePojo         messagePojo         = null;
    private MessagePojo         originalMessagePojo = null;
    private Object              data                = null;
    
    private Object              routingData         = null;
    
    private ProtocolSpecificKey protocolSpecificKey = null;
    private ActionSpecificKey   actionSpecificKey   = null;

    private ChoreographyPojo    choreography        = null;
    private PartnerPojo         partner             = null;
    private ParticipantPojo     participant         = null;
    private ConversationPojo    conversation        = null;

    /**
     * @return the data
     */
    public Object getData() {

        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData( Object data ) {

        this.data = data;
    }

    /**
     * @return the inboundMessagePojo
     */
    public MessagePojo getMessagePojo() {

        return messagePojo;
    }

    /**
     * @param inboundMessagePojo the inboundMessagePojo to set
     */
    public void setMessagePojo( MessagePojo messagePojo ) {

        this.messagePojo = messagePojo;
    }

    /**
     * @return the actionSpecificKey
     */
    public ActionSpecificKey getActionSpecificKey() {

        return actionSpecificKey;
    }

    /**
     * @param actionSpecificKey the actionSpecificKey to set
     */
    public void setActionSpecificKey( ActionSpecificKey actionSpecificKey ) {

        this.actionSpecificKey = actionSpecificKey;
    }

    /**
     * @return the protocolSpecificKey
     */
    public ProtocolSpecificKey getProtocolSpecificKey() {

        return protocolSpecificKey;
    }

    /**
     * @param protocolSpecificKey the protocolSpecificKey to set
     */
    public void setProtocolSpecificKey( ProtocolSpecificKey protocolSpecificKey ) {

        this.protocolSpecificKey = protocolSpecificKey;
    }

    /**
     * @return the choreography
     */
    public ChoreographyPojo getChoreography() {

        return choreography;
    }

    /**
     * @param choreography the choreography to set
     */
    public void setChoreography( ChoreographyPojo choreography ) {

        this.choreography = choreography;
    }

    /**
     * @return the communicationPartner
     */
    public PartnerPojo getPartner() {

        return partner;
    }

    /**
     * @param communicationPartner the communicationPartner to set
     */
    public void setCommunicationPartner( PartnerPojo partner ) {

        this.partner = partner;
    }

    /**
     * @return the conversation
     */
    public ConversationPojo getConversation() {

        return conversation;
    }

    /**
     * @param conversation the conversation to set
     */
    public void setConversation( ConversationPojo conversation ) {

        this.conversation = conversation;
    }

    /**
     * @return the participant
     */
    public ParticipantPojo getParticipant() {

        return participant;
    }

    /**
     * @param participant the participant to set
     */
    public void setParticipant( ParticipantPojo participant ) {

        this.participant = participant;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    @Override
    protected Object clone() throws CloneNotSupportedException {

        MessageContext clone = new MessageContext();
        clone.setActionSpecificKey( this.getActionSpecificKey() );
        clone.setChoreography( this.getChoreography() );
        clone.setCommunicationPartner( this.getPartner() );
        clone.setConversation( this.getConversation() );
        clone.setData( this.getData() );
        clone.setMessagePojo( this.getMessagePojo() );
        clone.setOriginalMessagePojo( this.getOriginalMessagePojo() );
        clone.setParticipant( this.getParticipant() );
        clone.setProtocolSpecificKey( this.getProtocolSpecificKey() );
        return clone;
    }

    public MessagePojo getOriginalMessagePojo() {

        return originalMessagePojo;
    }

    public void setOriginalMessagePojo( MessagePojo originalMessagePojo ) {

        this.originalMessagePojo = originalMessagePojo;
    }

    
    /**
     * @return the routingData
     */
    public Object getRoutingData() {
    
        return routingData;
    }

    
    /**
     * @param routingData the routingData to set
     */
    public void setRoutingData( Object routingData ) {
    
        this.routingData = routingData;
    }
} // MessageContext
