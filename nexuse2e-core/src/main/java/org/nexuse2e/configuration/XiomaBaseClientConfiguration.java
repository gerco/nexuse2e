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
package org.nexuse2e.configuration;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Level;
import org.nexuse2e.configuration.Constants.ComponentType;
import org.nexuse2e.pojo.ActionPojo;
import org.nexuse2e.pojo.CertificatePojo;
import org.nexuse2e.pojo.ChoreographyPojo;
import org.nexuse2e.pojo.ComponentPojo;
import org.nexuse2e.pojo.ConnectionPojo;
import org.nexuse2e.pojo.LoggerPojo;
import org.nexuse2e.pojo.MappingPojo;
import org.nexuse2e.pojo.ParticipantPojo;
import org.nexuse2e.pojo.PartnerPojo;
import org.nexuse2e.pojo.PipeletPojo;
import org.nexuse2e.pojo.PipelinePojo;
import org.nexuse2e.pojo.RolePojo;
import org.nexuse2e.pojo.ServicePojo;
import org.nexuse2e.pojo.TRPPojo;
import org.nexuse2e.pojo.UserPojo;

public class XiomaBaseClientConfiguration extends XiomaBaseServerConfiguration {

    /* (non-Javadoc)
     * @see org.nexuse2e.configuration.XiomaBaseServerConfiguration#createBaseConfiguration(java.util.List, java.util.List, java.util.List, java.util.List, java.util.List, java.util.List, java.util.List, java.util.List, java.util.List, java.util.List, java.util.List)
     */
    @Override
    public void createBaseConfiguration( List<ComponentPojo> components, List<ChoreographyPojo> choreographies,
            List<PartnerPojo> partners, List<PipelinePojo> backendPipelineTemplates,
            List<PipelinePojo> frontendPipelineTemplates, List<ServicePojo> services,
            List<CertificatePojo> caCertificates, List<TRPPojo> trps, List<UserPojo> users, List<RolePojo> roles,
            List<LoggerPojo> loggers, List<MappingPojo> mappings ) throws InstantiationException {

        super.createBaseConfiguration( components, choreographies, partners, backendPipelineTemplates,
                frontendPipelineTemplates, services, caCertificates, trps, users, roles, loggers, mappings );

        if ( partners != null && partners.size() > 0 ) {
            PartnerPojo partner = null;
            for ( PartnerPojo pojo : partners ) {
                if ( pojo.getPartnerId().equals( "Xioma" ) ) {
                    partner = pojo;
                    break;
                }
            }
            if ( partner.getConnections() != null && partner.getConnections().size() > 0 ) {
                Iterator<ConnectionPojo> i = partner.getConnections().iterator();
                while ( i.hasNext() ) {
                    ConnectionPojo connection = i.next();
                    if ( !connection.getTrp().getTransport().equals( "mail" ) ) {
                        i.remove();
                    }
                }
            }
            if ( partner.getParticipants() != null && partner.getParticipants().size() > 0 ) {
                Iterator<ParticipantPojo> i = partner.getParticipants().iterator();
                while ( i.hasNext() ) {
                    ParticipantPojo participant = i.next();
                    if ( !participant.getDescription().equals( "XiomaMail" ) ) {
                        i.remove();
                    }
                }
            }

            ChoreographyPojo choreography = null;
            for ( ChoreographyPojo pojo : choreographies ) {
                if ( pojo.getName().equals( "GenericFile" ) ) {
                    choreography = pojo;
                    break;
                }
            }

            if ( choreography.getParticipants() != null && choreography.getParticipants().size() > 0 ) {
                Iterator<ParticipantPojo> i = choreography.getParticipants().iterator();
                while ( i.hasNext() ) {
                    ParticipantPojo participant = i.next();
                    if ( !participant.getDescription().equals( "XiomaMail" ) ) {
                        i.remove();
                    }
                }
            }

            ComponentPojo xml2csvComponentPojo = new ComponentPojo( new Date(), new Date(), 1, ComponentType.PIPELET
                    .getValue(), "XML2CSVConverterPipelet", "org.nexuse2e.client.scanner.XML2CSVConverterPipelet",
                    "XML2CSVConverterPipelet" );
            ComponentPojo csv2xmlComponentPojo = new ComponentPojo( new Date(), new Date(), 1, ComponentType.PIPELET
                    .getValue(), "CSV2XMLConverterPipelet", "org.nexuse2e.client.scanner.CSV2XMLConverterPipelet",
                    "CSV2XMLConverterPipelet" );
            
            ComponentPojo uiLoggerComponentPojo = new ComponentPojo( new Date(), new Date(), 1, ComponentType.LOGGER
                    .getValue(), "DBLogger", "org.nexuse2e.client.ClientUILogger", "Client UI Logger" );

            LoggerPojo clientUILogger = new LoggerPojo( uiLoggerComponentPojo, new Date(), new Date(), 1,
                    "Client UI Logger", true, "group_core,group_database,group_backend,group_frontend,group_ui" );
            clientUILogger.setThreshold( Level.WARN_INT );
            loggers.add( clientUILogger );
            
            
            components.add( xml2csvComponentPojo );
            components.add( csv2xmlComponentPojo );
            components.add( uiLoggerComponentPojo );
            
            PipelinePojo clientInboundPipelinePojo = new PipelinePojo();
            PipelinePojo clientOutboundPipelinePojo = new PipelinePojo();

            PipeletPojo clientInboundPipeletPojo = new PipeletPojo( clientInboundPipelinePojo, xml2csvComponentPojo,
                    new Date(), new Date(), 1, 0, "InboundClientMapping", "InboundClientMapping", null );

            PipeletPojo clientOutboundPipeletPojo = new PipeletPojo( clientOutboundPipelinePojo, csv2xmlComponentPojo,
                    new Date(), new Date(), 1, 0, "OutboundClientMapping", "OutboundClientMapping", null );

            ArrayList<PipeletPojo> pipelets = new ArrayList<PipeletPojo>();
            pipelets.add( clientInboundPipeletPojo );

            clientInboundPipelinePojo.setPipelets( pipelets );
            clientInboundPipelinePojo.setOutbound( false );
            clientInboundPipelinePojo.setCreatedDate( new Date() );
            clientInboundPipelinePojo.setModifiedDate( new Date() );
            clientInboundPipelinePojo.setModifiedNxUserId( 1 );
            clientInboundPipelinePojo.setDescription( "ClientInboundPipeline" );
            clientInboundPipelinePojo.setName( "ClientInboundPipeline" );

            pipelets = new ArrayList<PipeletPojo>();
            pipelets.add( clientOutboundPipeletPojo );

            clientOutboundPipelinePojo.setPipelets( pipelets );
            clientOutboundPipelinePojo.setOutbound( true );
            clientOutboundPipelinePojo.setCreatedDate( new Date() );
            clientOutboundPipelinePojo.setModifiedDate( new Date() );
            clientOutboundPipelinePojo.setModifiedNxUserId( 1 );
            clientOutboundPipelinePojo.setDescription( "ClientOutboundPipeline" );
            clientOutboundPipelinePojo.setName( "ClientOutboundPipeline" );

            backendPipelineTemplates.add( clientInboundPipelinePojo );
            backendPipelineTemplates.add( clientOutboundPipelinePojo );
            
            
            ChoreographyPojo shipNoticeChoreographyPojo = new ChoreographyPojo();
            ActionPojo sendFileActionPojo = new ActionPojo( shipNoticeChoreographyPojo, new Date(), new Date(), 1, true,
                    true, clientInboundPipelinePojo, clientOutboundPipelinePojo, "SendFile" );
            List<ParticipantPojo> shipNoticeParticipants = new ArrayList<ParticipantPojo>();
            Set<ActionPojo> shipNoticeActions = new HashSet<ActionPojo>();
            shipNoticeActions.add( sendFileActionPojo );
            shipNoticeChoreographyPojo.setName( "ShipNotice" );
            shipNoticeChoreographyPojo.setDescription( "ShipNotice" );
            shipNoticeChoreographyPojo.setModifiedDate( new Date() );
            shipNoticeChoreographyPojo.setModifiedNxUserId( 1 );
            shipNoticeChoreographyPojo.setCreatedDate( new Date() );
            shipNoticeChoreographyPojo.setParticipants( shipNoticeParticipants );
            shipNoticeChoreographyPojo.setActions( shipNoticeActions );
            choreographies.add( shipNoticeChoreographyPojo );

            
            for ( ActionPojo pojo : choreography.getActions() ) {
                pojo.setInboundPipeline( clientInboundPipelinePojo );
                pojo.setOutboundPipeline( clientOutboundPipelinePojo );
            }
            
            
            for ( ServicePojo service : services ) {
                if(service.getName().equals( "Pop3ReceiverService" )) {
                    service.setAutostart( true );
                }
            }
        }
    }
}
