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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.nexuse2e.ActionSpecificKey;
import org.nexuse2e.Engine;
import org.nexuse2e.Constants.MappingType;
import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.pojo.ActionPojo;
import org.nexuse2e.pojo.ChoreographyPojo;
import org.nexuse2e.pojo.ConversationPojo;
import org.nexuse2e.pojo.MappingPojo;
import org.nexuse2e.pojo.MessagePojo;
import org.nexuse2e.pojo.ParticipantPojo;
import org.nexuse2e.pojo.PartnerPojo;

/**
 * Unit test for {@link MessageMappingPipelet}.
 *
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public class TestMessageMappingPipelet {

    private MessageMappingPipelet pipelet;
    private MessageContext messageContext;
    private ChoreographyPojo choreography;
    private ChoreographyPojo mappedChoreography;
    private ActionPojo action;
    private ActionPojo mappedAction;
    private PartnerPojo partner;
    private PartnerPojo mappedPartner;
    private ConversationPojo conversation;
    private MessagePojo message;
    private ParticipantPojo participant;
    private ParticipantPojo mappedParticipant;
    private MappingPojo actionMapping;
    private MappingPojo choreographyMapping;
    private MappingPojo partnerMapping;
    
    
    private <T> List<T> getList( T ...ts ) {
        List<T> list = new ArrayList<T>();
        for (T t : ts) {
            list.add( t );
        }
        return list;
    }
    
    private <T> Set<T> getSet( T ...ts ) {
        Set<T> set = new HashSet<T>();
        for (T t : ts) {
            set.add( t );
        }
        return set;
    }
    
    
    @Before
    public void setUp() throws Exception {
        
        choreography = new ChoreographyPojo();
        choreography.setName( "choreography" );
        mappedChoreography = new ChoreographyPojo();
        mappedChoreography.setName( "mappedChoreography" );

        action = new ActionPojo();
        action.setName( "action" );
        action.setChoreography( choreography );
        mappedAction = new ActionPojo();
        mappedAction.setName( "mappedAction" );
        mappedAction.setChoreography( mappedChoreography );

        choreography.setActions( getSet( action, mappedAction ) );
        mappedChoreography.setActions( getSet( action, mappedAction ) );

        partner = new PartnerPojo();
        partner.setPartnerId( "partner" );
        mappedPartner = new PartnerPojo();
        mappedPartner.setPartnerId( "mappedPartner" );

        conversation = new ConversationPojo();
        conversation.setChoreography( choreography );
        conversation.setConversationId( "12345" );
        conversation.setCurrentAction( null );
        conversation.setPartner( partner );
        
        participant = new ParticipantPojo();
        participant.setDescription( "participant" );
        participant.setChoreography( choreography );
        participant.setPartner( partner );
        
        mappedParticipant = new ParticipantPojo();
        mappedParticipant.setDescription( "mappedParticipant" );
        mappedParticipant.setChoreography( mappedChoreography );
        mappedParticipant.setPartner( mappedPartner );
        
        message = new MessagePojo();
        message.setAction( action );
        message.setConversation( conversation );
        
        choreographyMapping = new MappingPojo();
        choreographyMapping.setCategory( "choreographyMap" );
        choreographyMapping.setLeftType( MappingType.STRING.ordinal() );
        choreographyMapping.setRightType( MappingType.STRING.ordinal() );
        choreographyMapping.setLeftValue( choreography.getName() );
        choreographyMapping.setRightValue( mappedChoreography.getName() );
        
        actionMapping = new MappingPojo();
        actionMapping.setCategory( "actionMap" );
        actionMapping.setLeftType( MappingType.STRING.ordinal() );
        actionMapping.setRightType( MappingType.STRING.ordinal() );
        actionMapping.setLeftValue( action.getName() );
        actionMapping.setRightValue( mappedAction.getName() );
        
        partnerMapping = new MappingPojo();
        partnerMapping.setCategory( "partnerMap" );
        partnerMapping.setLeftType( MappingType.STRING.ordinal() );
        partnerMapping.setRightType( MappingType.STRING.ordinal() );
        partnerMapping.setLeftValue( partner.getPartnerId() );
        partnerMapping.setRightValue( mappedPartner.getPartnerId() );

        messageContext = new MessageContext();
        messageContext.setPartner( partner );
        messageContext.setParticipant( participant );
        messageContext.setChoreography( choreography );
        messageContext.setConversation( conversation );
        messageContext.setMessagePojo( message );
        messageContext.setActionSpecificKey( new ActionSpecificKey( "action", "choreography" ) );
        
        choreography.setParticipants( getList( participant, mappedParticipant ) );
        mappedChoreography.setParticipants( getList( participant, mappedParticipant ) );
        
        pipelet = new MessageMappingPipelet();

        if (Engine.getInstance() == null) {
            new Engine().setNexusE2ERoot( System.getProperty( "user.dir" ) );
            Engine.getInstance().setCurrentConfiguration( new EngineConfiguration() );
            Engine.getInstance().getCurrentConfiguration().setPartners( getList( partner, mappedPartner ) );
            Engine.getInstance().getCurrentConfiguration().setChoreographies( getList( choreography, mappedChoreography ) );
            Engine.getInstance().getCurrentConfiguration().setMappings( getList( choreographyMapping, actionMapping, partnerMapping ) );
        }
        
        int id = 1;
        choreography.setNxId( id++ );
        mappedChoreography.setNxId( id++ );
        action.setNxId( id++ );
        mappedAction.setNxId( id++ );
        partner.setNxId( id++ );
        mappedPartner.setNxId( id++ );
        conversation.setNxId( id++ );
        message.setNxId( id++ );
        participant.setNxId( id++ );
        mappedParticipant.setNxId( id++ );
        actionMapping.setNxId( id++ );
        choreographyMapping.setNxId( id++ );
        partnerMapping.setNxId( id++ );
    }

    @Ignore
    @Test 
    public void choreographyMap() throws Exception {
        // set parameter
        pipelet.setParameter( MessageMappingPipelet.CHOREOGRAPHY_MAP_CATEGORY_PARAMETER_NAME, "choreographyMap" );

        // go
        pipelet.processMessage( messageContext );
        Assert.assertEquals( "mappedChoreography", messageContext.getChoreography().getName() );
        Assert.assertEquals( "mappedChoreography", messageContext.getMessagePojo().getConversation().getChoreography().getName() );
        Assert.assertEquals( "mappedChoreography", messageContext.getConversation().getChoreography().getName() );
        
        // just to make sure
        Assert.assertEquals( "partner", messageContext.getPartner().getPartnerId() );
        Assert.assertSame( participant, messageContext.getMessagePojo().getParticipant() );
        Assert.assertEquals( "partner", messageContext.getParticipant().getPartner().getPartnerId() );
        Assert.assertSame( null, messageContext.getConversation().getCurrentAction() );
    }

    @Ignore
    @Test 
    public void actionMap() throws Exception {
        // set parameter
        pipelet.setParameter( MessageMappingPipelet.ACTION_MAP_CATEGORY_PARAMETER_NAME, "actionMap" );

        // go
        pipelet.processMessage( messageContext );
        Assert.assertSame( null, messageContext.getConversation().getCurrentAction() );
        Assert.assertEquals( "mappedAction", messageContext.getMessagePojo().getAction().getName() );

        Assert.assertEquals( "choreography", messageContext.getChoreography().getName() );
        Assert.assertEquals( "choreography", messageContext.getMessagePojo().getConversation().getChoreography().getName() );
        Assert.assertEquals( "choreography", messageContext.getConversation().getChoreography().getName() );
        Assert.assertEquals( "partner", messageContext.getPartner().getPartnerId() );
        Assert.assertSame( participant, messageContext.getMessagePojo().getParticipant() );
        Assert.assertEquals( "partner", messageContext.getParticipant().getPartner().getPartnerId() );
    }

    @Ignore
    @Test 
    public void partnerMap() throws Exception {
        // set parameter
        pipelet.setParameter( MessageMappingPipelet.PARTNER_MAP_CATEGORY_PARAMETER_NAME, "partnerMap" );

        // go
        pipelet.processMessage( messageContext );
        Assert.assertEquals( "mappedParticipant", messageContext.getMessagePojo().getParticipant().getDescription() );
        Assert.assertEquals( "mappedParticipant", messageContext.getParticipant().getDescription() );

        Assert.assertEquals( "mappedPartner", messageContext.getConversation().getPartner().getPartnerId() );
        Assert.assertEquals( "mappedPartner", messageContext.getPartner().getPartnerId() );
        Assert.assertEquals( "mappedPartner", messageContext.getParticipant().getPartner().getPartnerId() );
        Assert.assertEquals( "mappedPartner", messageContext.getConversation().getPartner().getPartnerId() );
    }
   
    @Ignore
    @Test
    public void testNullMessageContext() throws IllegalArgumentException, IllegalStateException, NexusException {
        MessageContext messageContextNull = new MessageContext();
        messageContextNull.setPartner( partner );
        messageContextNull.setParticipant( participant );
        messageContextNull.setChoreography( choreography );
        messageContextNull.setConversation( conversation );
        messageContextNull.setMessagePojo( message );
        messageContextNull.setActionSpecificKey( new ActionSpecificKey( "action", "choreography" ) );
        pipelet.processMessage(messageContextNull);
    }
}
