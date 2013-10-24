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
package org.nexuse2e.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.pojo.CertificatePojo;
import org.nexuse2e.pojo.ChoreographyPojo;
import org.nexuse2e.pojo.ComponentPojo;
import org.nexuse2e.pojo.ConnectionPojo;
import org.nexuse2e.pojo.GenericParamPojo;
import org.nexuse2e.pojo.LoggerPojo;
import org.nexuse2e.pojo.MappingPojo;
import org.nexuse2e.pojo.NEXUSe2ePojo;
import org.nexuse2e.pojo.PartnerPojo;
import org.nexuse2e.pojo.PipelinePojo;
import org.nexuse2e.pojo.RolePojo;
import org.nexuse2e.pojo.ServicePojo;
import org.nexuse2e.pojo.TRPPojo;
import org.nexuse2e.pojo.UserPojo;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author gesch
 *
 */

@Repository
public class ConfigDAOImpl extends BasicDAOImpl implements ConfigDAO {

	
    private static Logger LOG = Logger.getLogger( ConfigDAOImpl.class );

   
    
    /* (non-Javadoc)
     * @see org.nexuse2e.dao.ConfigDAO#saveTRP(org.nexuse2e.pojo.TRPPojo)
     */
    public void saveTRP( TRPPojo trp ) {

        saveRecord( trp );

    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.ConfigDAO#updateTRP(org.nexuse2e.pojo.TRPPojo)
     */
    public void updateTRP( TRPPojo trp ) {

        updateRecord( trp );

    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.ConfigDAO#savePartner(org.nexuse2e.pojo.PartnerPojo)
     */
    public void savePartner( PartnerPojo partner ) {

        partner.setCreatedDate( new Date() );
        partner.setModifiedDate( new Date() );
        saveRecord( partner );

    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.ConfigDAO#updatePartner(org.nexuse2e.pojo.PartnerPojo)
     */
    public void updatePartner( PartnerPojo partner ) {

        partner.setModifiedDate( new Date() );
        updateRecord( partner );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.ConfigDAO#deletePartner(org.nexuse2e.pojo.PartnerPojo)
     */
    public void deletePartner( PartnerPojo partner ) {

        if ( partner != null ) {
            ArrayList<Object> list = new ArrayList<Object>();
            list.addAll( partner.getConnections() );
            list.addAll( partner.getCertificates() );
            list.add( partner );
            deleteRecords( list );
        }
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.ConfigDAO#deleteConnection(org.nexuse2e.pojo.ConnectionPojo)
     */
    public void deleteConnection( ConnectionPojo connection ) {

        if ( connection != null ) {
            deleteRecord( connection );
        }
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.ConfigDAO#getPartners()
     */
    @SuppressWarnings("unchecked")
    public List<PartnerPojo> getPartners() {

        DetachedCriteria dc = DetachedCriteria.forClass( PartnerPojo.class );
        return (List<PartnerPojo>) getListThroughSessionFind( dc,0,0 );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.ConfigDAO#saveChoreography(org.nexuse2e.pojo.ChoreographyPojo)
     */
    public void saveChoreography( ChoreographyPojo choreography ) {

        choreography.setCreatedDate( new Date() );
        choreography.setModifiedDate( new Date() );
        saveOrUpdateRecord( choreography );

    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.ConfigDAO#updateChoreography(org.nexuse2e.pojo.ChoreographyPojo)
     */
    public void updateChoreography( ChoreographyPojo choreography ) {

        choreography.setModifiedDate( new Date() );
        updateRecord( choreography );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.ConfigDAO#deleteChoreography(org.nexuse2e.pojo.ChoreographyPojo)
     */
    public void deleteChoreography( ChoreographyPojo choreography ) {

        if ( choreography != null ) {
            deleteRecord( choreography );
        }
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.ConfigDAO#getChoreographies()
     */
    @SuppressWarnings("unchecked")
    public List<ChoreographyPojo> getChoreographies() {

        DetachedCriteria dc = DetachedCriteria.forClass( ChoreographyPojo.class );
        return (List<ChoreographyPojo>) getListThroughSessionFind( dc,0,0 );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.ConfigDAO#savePipeline(org.nexuse2e.pojo.PipelinePojo)
     */
    public void savePipeline( PipelinePojo pipeline ) {

        pipeline.setCreatedDate( new Date() );
        pipeline.setModifiedDate( new Date() );
        saveRecord( pipeline );

    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.ConfigDAO#updatePipeline(org.nexuse2e.pojo.PipelinePojo)
     */
    public void updatePipeline( PipelinePojo pipeline ) {

        pipeline.setModifiedDate( new Date() );
        updateRecord( pipeline );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.ConfigDAO#deletePipeline(org.nexuse2e.pojo.PipelinePojo)
     */
    public void deletePipeline( PipelinePojo pipeline ) {

        if ( pipeline != null ) {
            deleteRecord( pipeline );
        }
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.ConfigDAO#getFrontendPipelines()
     */
    @SuppressWarnings("unchecked")
    public List<PipelinePojo> getFrontendPipelines() {

        DetachedCriteria dc = DetachedCriteria.forClass( PipelinePojo.class );
        dc.add( Restrictions.eq( "frontend", true ) );
        return (List<PipelinePojo>) getListThroughSessionFind( dc,0,0 );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.ConfigDAO#getBackendPipelines()
     */
    @SuppressWarnings("unchecked")
    public List<PipelinePojo> getBackendPipelines( ) {

        DetachedCriteria dc = DetachedCriteria.forClass( PipelinePojo.class );
        dc.add( Restrictions.eq( "frontend", false ) );
        return (List<PipelinePojo>) getListThroughSessionFind( dc,0,0 );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.ConfigDAO#getPipelines()
     */
    @SuppressWarnings("unchecked")
    public List<PipelinePojo> getPipelines( ) {

        DetachedCriteria dc = DetachedCriteria.forClass( PipelinePojo.class );
        return (List<PipelinePojo>) getListThroughSessionFind( dc,0,0 );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.ConfigDAO#saveTrp(org.nexuse2e.pojo.TRPPojo)
     */
    public void saveTrp( TRPPojo trp ) {

        saveRecord( trp );

    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.ConfigDAO#updateTrp(org.nexuse2e.pojo.TRPPojo)
     */
    public void updateTrp( TRPPojo trp ) {

        updateRecord( trp );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.ConfigDAO#deleteTrp(org.nexuse2e.pojo.TRPPojo)
     */
    public void deleteTrp( TRPPojo trp ) {

        if ( trp != null ) {
            deleteRecord( trp );
        }
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.ConfigDAO#getTrps()
     */
    @SuppressWarnings("unchecked")
    public List<TRPPojo> getTrps() {

        DetachedCriteria dc = DetachedCriteria.forClass( TRPPojo.class );
        return (List<TRPPojo>) getListThroughSessionFind( dc,0,0 );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.ConfigDAO#getComponents()
     */
    @SuppressWarnings("unchecked")
    public List<ComponentPojo> getComponents() throws NexusException {

        DetachedCriteria dc = DetachedCriteria.forClass( ComponentPojo.class );
        return (List<ComponentPojo>) getListThroughSessionFind( dc,0,0 );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.ConfigDAO#saveComponent(org.nexuse2e.pojo.ComponentPojo)
     */
    public void saveComponent( ComponentPojo component ) {

        saveRecord( component );

    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.ConfigDAO#updateComponent(org.nexuse2e.pojo.ComponentPojo)
     */
    public void updateComponent( ComponentPojo component ) {

        updateRecord( component );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.ConfigDAO#deleteComponent(org.nexuse2e.pojo.ComponentPojo)
     */
    public void deleteComponent( ComponentPojo component ) throws NexusException {

        if ( component != null ) {
            deleteRecord( component );
        }
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.ConfigDAO#getLoggers()
     */
    @SuppressWarnings("unchecked")
    public List<LoggerPojo> getLoggers() {

        Criteria c = sessionFactory.getCurrentSession().createCriteria(LoggerPojo.class);
    	return (List<LoggerPojo>) c.list(); 
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.ConfigDAO#deleteLogger(org.nexuse2e.pojo.LoggerPojo)
     */
    public void deleteLogger( LoggerPojo logger ) {

        if ( logger != null ) {
            deleteRecord( logger );
        }
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.ConfigDAO#updateLogger(org.nexuse2e.pojo.LoggerPojo)
     */
    public void updateLogger( LoggerPojo logger ) {

        updateRecord( logger );

    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.ConfigDAO#saveLogger(org.nexuse2e.pojo.LoggerPojo)
     */
    public void saveLogger( LoggerPojo logger ) {

        saveRecord( logger );

    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.ConfigDAO#getServices()
     */
    @SuppressWarnings("unchecked")
    public List<ServicePojo> getServices() {

        DetachedCriteria dc = DetachedCriteria.forClass( ServicePojo.class );
        dc.addOrder( Order.asc( "position" ) );
        return (List<ServicePojo>) getListThroughSessionFind( dc,0,0 );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.ConfigDAO#deleteService(org.nexuse2e.pojo.ServicePojo)
     */
    public void deleteService( ServicePojo service ) {

        if ( service != null ) {
            deleteRecord( service );
        }
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.ConfigDAO#updateService(org.nexuse2e.pojo.ServicePojo)
     */
    public void updateService( ServicePojo service ) {

        updateRecord( service );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.ConfigDAO#saveService(org.nexuse2e.pojo.ServicePojo)
     */
    public void saveService( ServicePojo service ) {

        saveRecord( service );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.ConfigDAO#getCertificates()
     */
    @SuppressWarnings("unchecked")
    public List<CertificatePojo> getCertificates() {

        DetachedCriteria dc = DetachedCriteria.forClass( CertificatePojo.class );
        return (List<CertificatePojo>) getListThroughSessionFind( dc,0,0 );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.ConfigDAO#deleteCertificate(org.nexuse2e.pojo.CertificatePojo)
     */
    public void deleteCertificate( CertificatePojo certificate ) {

        if ( certificate != null ) {
            deleteRecord( certificate );
        }
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.ConfigDAO#updateCertificate(org.nexuse2e.pojo.CertificatePojo)
     */
    public void updateCertificate( CertificatePojo certificate ) {

        updateRecord( certificate );

    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.ConfigDAO#saveCertificate(org.nexuse2e.pojo.CertificatePojo)
     */
    public void saveCertificate( CertificatePojo certificate ) {

        saveRecord( certificate );

    }

    /*
     * USER SECTION
     */
    /* (non-Javadoc)
     * @see org.nexuse2e.dao.ConfigDAO#getUsers()
     */
    @SuppressWarnings("unchecked")
    public List<UserPojo> getUsers() {

        DetachedCriteria dc = DetachedCriteria.forClass( UserPojo.class );
        return (List<UserPojo>) getListThroughSessionFind( dc,0,0 );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.ConfigDAO#updateUser(org.nexuse2e.pojo.UserPojo)
     */
    public void updateUser( UserPojo user ) {

        updateRecord( user );

    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.ConfigDAO#saveUser(org.nexuse2e.pojo.UserPojo)
     */
    public void saveUser( UserPojo user ) {

        saveRecord( user );

    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.ConfigDAO#deleteUser(org.nexuse2e.pojo.UserPojo)
     */
    public void deleteUser( UserPojo user ) {

        if ( user != null ) {
            deleteRecord( user );
        }
    }

    /*
     * ROLE SECTION
     */
    /* (non-Javadoc)
     * @see org.nexuse2e.dao.ConfigDAO#getRoles()
     */
    @SuppressWarnings("unchecked")
    public List<RolePojo> getRoles() {

        DetachedCriteria dc = DetachedCriteria.forClass( RolePojo.class );
        return (List<RolePojo>) getListThroughSessionFind( dc,0,0 );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.ConfigDAO#updateRole(org.nexuse2e.pojo.RolePojo)
     */
    public void updateRole( RolePojo role ) {

        updateRecord( role );

    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.ConfigDAO#saveRole(org.nexuse2e.pojo.RolePojo)
     */
    public void saveRole( RolePojo role ) throws NexusException {

        saveRecord( role );

    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.ConfigDAO#deleteRole(org.nexuse2e.pojo.RolePojo)
     */
    public void deleteRole( RolePojo role ) throws NexusException {

        if ( role != null ) {
            deleteRecord( role );
        }
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.ConfigDAO#getGenericParameters()
     */
    @SuppressWarnings("unchecked")
    public List<GenericParamPojo> getGenericParameters() {

        DetachedCriteria dc = DetachedCriteria.forClass( GenericParamPojo.class );
        return (List<GenericParamPojo>) getListThroughSessionFind( dc,0,0 );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.ConfigDAO#updateGenericParameter(org.nexuse2e.pojo.GenericParamPojo)
     */
    public void updateGenericParameter( GenericParamPojo param ) {

        updateRecord( param );

    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.ConfigDAO#saveGenericParameter(org.nexuse2e.pojo.GenericParamPojo)
     */
    public void saveGenericParameter( GenericParamPojo param ) {

        saveRecord( param );

    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.ConfigDAO#deleteGemericParameter(org.nexuse2e.pojo.GenericParamPojo)
     */
    public void deleteGemericParameter( GenericParamPojo param ) {

        if ( param != null ) {
            deleteRecord( param );
        }
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.ConfigDAO#getMappings()
     */
    @SuppressWarnings("unchecked")
    public List<MappingPojo> getMappings() {

        DetachedCriteria dc = DetachedCriteria.forClass( MappingPojo.class );
        return (List<MappingPojo>) getListThroughSessionFind( dc,0,0 );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.ConfigDAO#updateMapping(org.nexuse2e.pojo.MappingPojo)
     */
    public void updateMapping( MappingPojo mapping ) {

        updateRecord( mapping );

    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.ConfigDAO#saveMapping(org.nexuse2e.pojo.MappingPojo)
     */
    public void saveMapping( MappingPojo mapping ) {

        saveRecord( mapping );

    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.ConfigDAO#deleteMapping(org.nexuse2e.pojo.MappingPojo)
     */
    public void deleteMapping( MappingPojo mapping ) {

        if ( mapping != null ) {
            deleteRecord( mapping );
        }
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.ConfigDAO#deleteAll()
     */
    public void deleteAll() throws NexusException {

        String[] typeNames = new String[] { "MessageLabelPojo", "MessagePayloadPojo", "MessagePojo",
                "ConversationPojo", "FollowUpActionPojo", "ActionPojo", "ParticipantPojo", "ConnectionPojo",
                "CertificatePojo", "PartnerPojo", "ChoreographyPojo", "ServiceParamPojo", "ServicePojo",
                "LoggerParamPojo", "LoggerPojo", "PipeletParamPojo", "PipeletPojo", "ComponentPojo", "PipelinePojo",
                "TRPPojo", "UserPojo", "GrantPojo", "RolePojo", "GenericParamPojo", "MappingPojo",};

        for ( String typeName : typeNames ) {
            if ("MessagePojo".equals(typeName)) {
                try {
                	sessionFactory.getCurrentSession().createQuery("delete from MessagePojo where referencedMessage is not null").executeUpdate();
                } catch (Exception ex) {
                    LOG.warn(ex);
                }
            }
            sessionFactory.getCurrentSession().createQuery( "delete from " + typeName ).executeUpdate();
        }

    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.ConfigDAO#isDatabasePopulated()
     */
    @Transactional
    public boolean isDatabasePopulated() throws NexusException {

        List<TRPPojo> tempTRPs = getTrps();
        return ( tempTRPs != null ) && ( tempTRPs.size() != 0 );
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.ConfigDAO#loadDatafromDB(org.nexuse2e.configuration.EngineConfiguration)
     */
    public void loadDatafromDB( EngineConfiguration configuration ) throws NexusException {

        List<ChoreographyPojo> tempChoreographies = getChoreographies();
        if ( tempChoreographies == null ) {
            LOG.debug( "No choreographies available in database!" );
        } else {
            LOG.trace( "ChoreographyCount:" + tempChoreographies.size() );
        }
        configuration.setChoreographies( tempChoreographies );

        List<PartnerPojo> tempPartners = getPartners();
        if ( tempPartners == null ) {
            LOG.debug( "No partners available in database!" );
        } else {
            LOG.trace( "PartnerCount:" + tempPartners.size() );
        }
        configuration.setPartners( tempPartners );

        List<CertificatePojo> allCertificates = getCertificates();
        if ( allCertificates == null || allCertificates.size() == 0 ) {
            LOG.debug( "No certificates available in database!" );
        }
        configuration.setCertificates( allCertificates );

        List<PipelinePojo> pipelines = getFrontendPipelines();
        if ( pipelines == null || pipelines.size() == 0 ) {
            LOG.debug( "No frontend pipelines available in database!" );
        }
        configuration.setFrontendPipelineTemplates( pipelines );

        pipelines = getBackendPipelines();
        if ( pipelines == null || pipelines.size() == 0 ) {
            LOG.debug( "No backend pipelines available in database!" );
        }
        configuration.setBackendPipelineTemplates( pipelines );

        List<TRPPojo> tempTRPs = getTrps();
        configuration.setTrps( tempTRPs );

        List<ComponentPojo> tempComponents = getComponents();
        configuration.setComponents( tempComponents );

        List<LoggerPojo> loggers = getLoggers();
        configuration.setLoggers( loggers );

        List<ServicePojo> services = getServices();
        configuration.setServices( services );

        List<UserPojo> users = getUsers();
        configuration.setUsers( users );

        List<RolePojo> roles = getRoles();
        configuration.setRoles( roles );

        List<MappingPojo> mappings = getMappings();
        configuration.setMappings( mappings );

        configuration.setGenericParameters( new HashMap<String, List<GenericParamPojo>>() );
        List<GenericParamPojo> tempParams = getGenericParameters();
        if ( tempParams != null && tempParams.size() > 0 ) {
            for ( GenericParamPojo pojo : tempParams ) {
                List<GenericParamPojo> catParams = configuration.getGenericParameters().get( pojo.getCategory() );
                if ( catParams == null ) {
                    catParams = new ArrayList<GenericParamPojo>();
                    configuration.getGenericParameters().put( pojo.getCategory(), catParams );
                }
                catParams.add( pojo );
            }
        }

        // Fix for database schema update of nx_trp table
        // Add protocol adapter class name in case it's not there

        for ( TRPPojo trpPojo : configuration.getTrps() ) {
            if ( StringUtils.isEmpty( trpPojo.getAdapterClassName() ) ) {
                if ( trpPojo.getProtocol().equalsIgnoreCase( "ebxml" ) ) {
                    if ( trpPojo.getVersion().equalsIgnoreCase( "1.0" ) ) {
                        trpPojo.setAdapterClassName( "org.nexuse2e.messaging.ebxml.v10.ProtocolAdapter" );
                    } else {
                        trpPojo.setAdapterClassName( "org.nexuse2e.messaging.ebxml.v20.ProtocolAdapter" );
                    }
                } else {
                    trpPojo.setAdapterClassName( "org.nexuse2e.messaging.DefaultProtocolAdapter" );
                }
                LOG.trace( "Set adapterClassName to: " + trpPojo.getAdapterClassName() );
            }
        }

    } // loadDataFromDB

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.ConfigDAO#saveDelta(org.nexuse2e.configuration.EngineConfiguration)
     */
    public void saveDelta( EngineConfiguration configuration ) {

        // patch records that got a temporary (negative) ID
        for ( NEXUSe2ePojo record : configuration.getUpdateList() ) {
            if ( record.getNxId() < 0 ) {
                record.setNxId( 0 );
            }
        }
        for ( NEXUSe2ePojo record : configuration.getImplicitUpdateList() ) {
            if ( record.getNxId() < 0 ) {
                record.setNxId( 0 );
            }
        }
        // save records
        for ( NEXUSe2ePojo record : configuration.getUpdateList() ) {
            saveOrUpdateRecord( record );
        }
        // delete records
        for ( NEXUSe2ePojo record : configuration.getDeleteList() ) {
            deleteRecord( record );
        }

    }

    /* (non-Javadoc)
     * @see org.nexuse2e.dao.ConfigDAO#saveConfigurationToDB(org.nexuse2e.configuration.EngineConfiguration)
     */
    public void saveConfigurationToDB( EngineConfiguration configuration ) throws NexusException {

        
        List<TRPPojo> trps = configuration.getTrps();
        List<ComponentPojo> components = configuration.getComponents();
        List<PipelinePojo> backendPipelineTemplates = configuration.getBackendPipelineTemplates();
        List<PipelinePojo> frontendPipelineTemplates = configuration.getFrontendPipelineTemplates();
        List<PartnerPojo> partners = configuration.getPartners();
        List<CertificatePojo> certificates = configuration.getCertificates();
        List<ChoreographyPojo> choreographies = configuration.getChoreographies();
        List<ServicePojo> services = configuration.getServices();
        List<LoggerPojo> loggers = configuration.getLoggers();
        List<RolePojo> roles = configuration.getRoles();
        List<UserPojo> users = configuration.getUsers();
        Map<String, List<GenericParamPojo>> genericParameters = configuration.getGenericParameters();
        List<MappingPojo> mappings = configuration.getMappings();

        if ( trps != null && trps.size() > 0 ) {

            Iterator<TRPPojo> i = trps.iterator();
            while ( i.hasNext() ) {
                TRPPojo pojo = i.next();
                LOG.debug( "TRP: " + pojo.getNxTRPId() + " - " + pojo.getProtocol() + " - " + pojo.getVersion() + " - "
                        + pojo.getTransport() );
                if ( pojo.getNxTRPId() != 0 ) {
                    updateTRP( pojo );
                } else {
                    saveTRP( pojo );
                }
            }
        }

        if ( components != null && components.size() > 0 ) {

            for ( ComponentPojo pojo : components ) {
                LOG.debug( "Component: " + pojo.getNxComponentId() + " - " + pojo.getType() + " - " + pojo.getName() );
                if ( pojo.getNxComponentId() != 0 ) {
                    pojo.setModifiedDate( new Date() );
                    updateComponent( pojo );
                } else {
                    pojo.setCreatedDate( new Date() );
                    pojo.setModifiedDate( new Date() );
                    saveComponent( pojo );
                }
            }
        }

        if ( backendPipelineTemplates != null && backendPipelineTemplates.size() > 0 ) {

            for ( PipelinePojo pojo : backendPipelineTemplates ) {
                LOG.debug( "BackendPipeline: " + pojo.getNxPipelineId() + " - " + pojo.getName() );
                if ( pojo.getNxPipelineId() != 0 ) {
                    pojo.setModifiedDate( new Date() );
                    updatePipeline( pojo );
                } else {
                    pojo.setCreatedDate( new Date() );
                    pojo.setModifiedDate( new Date() );
                    savePipeline( pojo );
                }
            }
        }

        if ( frontendPipelineTemplates != null && frontendPipelineTemplates.size() > 0 ) {

            for ( PipelinePojo pojo : frontendPipelineTemplates ) {
                LOG.debug( "FrontendPipeline: " + pojo.getNxPipelineId() + " - " + pojo.getName() );
                if ( pojo.getNxPipelineId() != 0 ) {
                    pojo.setModifiedDate( new Date() );
                    updatePipeline( pojo );
                } else {
                    pojo.setCreatedDate( new Date() );
                    pojo.setModifiedDate( new Date() );
                    savePipeline( pojo );
                }
            }
        }

        if ( partners != null && partners.size() > 0 ) {

            Iterator<PartnerPojo> i = partners.iterator();
            while ( i.hasNext() ) {
                PartnerPojo pojo = i.next();
                LOG.debug( "Partner: " + pojo.getNxPartnerId() + " - " + pojo.getPartnerId() + " - " + pojo.getName() );

                if ( pojo.getNxPartnerId() != 0 ) {
                    pojo.setModifiedDate( new Date() );
                    updatePartner( pojo );
                } else {
                    pojo.setCreatedDate( new Date() );
                    pojo.setModifiedDate( new Date() );
                    savePartner( pojo );
                }
            }
        }

        if ( certificates != null && certificates.size() > 0 ) {

            for ( CertificatePojo certificate : certificates ) {
                LOG.debug( "Certificate: " + certificate.getNxCertificateId() + " - " + certificate.getName() );
                if ( certificate.getNxCertificateId() != 0 ) {
                    certificate.setModifiedDate( new Date() );
                    updateCertificate( certificate );
                } else {
                    certificate.setCreatedDate( new Date() );
                    certificate.setModifiedDate( new Date() );
                    saveCertificate( certificate );
                }
            }
        }

        if ( choreographies != null && choreographies.size() > 0 ) {

            Iterator<ChoreographyPojo> i = choreographies.iterator();
            while ( i.hasNext() ) {

                ChoreographyPojo pojo = i.next();
                LOG.debug( "Choreography: " + pojo.getNxChoreographyId() + " - " + pojo.getName() );

                if ( pojo.getNxChoreographyId() != 0 ) {
                    pojo.setModifiedDate( new Date() );
                    updateChoreography( pojo );
                } else {
                    pojo.setCreatedDate( new Date() );
                    pojo.setModifiedDate( new Date() );
                    saveChoreography( pojo );
                }
            }
        }

        if ( services != null && services.size() > 0 ) {
            for ( ServicePojo pojo : services ) {
                LOG.debug( "Service: " + pojo.getNxServiceId() + " - " + pojo.getName() );
                if ( pojo.getNxServiceId() != 0 ) {
                    pojo.setModifiedDate( new Date() );
                    updateService( pojo );
                } else {
                    pojo.setCreatedDate( new Date() );
                    pojo.setModifiedDate( new Date() );
                    saveService( pojo );
                }
            }
        }

        if ( loggers != null && loggers.size() > 0 ) {

            for ( LoggerPojo logger : loggers ) {
                LOG.debug( "Logger: " + logger.getNxLoggerId() + " - " + logger.getName() );
                if ( logger.getNxLoggerId() != 0 ) {
                    logger.setModifiedDate( new Date() );
                    updateLogger( logger );
                } else {
                    logger.setCreatedDate( new Date() );
                    logger.setModifiedDate( new Date() );
                    saveLogger( logger );
                }
            }
        }

        // save roles first to ensure referential integrity
        if ( roles != null && roles.size() > 0 ) {

            for ( RolePojo role : roles ) {
                LOG.debug( "Role: " + role.getNxRoleId() + " - " + role.getName() );
                if ( role.getNxRoleId() != 0 ) {
                    role.setModifiedDate( new Date() );
                    updateRole( role );
                } else {
                    role.setCreatedDate( new Date() );
                    role.setModifiedDate( new Date() );
                    saveRole( role );
                }
            }
        }

        if ( users != null && users.size() > 0 ) {

            for ( UserPojo user : users ) {
                LOG.debug( "User: " + user.getNxUserId() + " - " + user.getLoginName() );
                if ( user.getNxUserId() != 0 ) {
                    user.setModifiedDate( new Date() );
                    updateUser( user );
                } else {
                    user.setCreatedDate( new Date() );
                    user.setModifiedDate( new Date() );
                    saveUser( user );
                }
            }
        }

        List<GenericParamPojo> tempList = new ArrayList<GenericParamPojo>();
        for ( String name : genericParameters.keySet() ) {
            List<GenericParamPojo> values = genericParameters.get( name );
            tempList.addAll( values );
        }

        if ( tempList != null && tempList.size() > 0 ) {

            for ( GenericParamPojo param : tempList ) {
                LOG.debug( "Parameter: " + param.getNxGenericParamId() + " - (" + param.getCategory() + "/"
                        + param.getTag() + "):" + param.getParamName() + "=" + param.getValue() );
                if ( param.getNxGenericParamId() != 0 ) {
                    param.setModifiedDate( new Date() );
                    updateGenericParameter( param );
                } else {
                    param.setCreatedDate( new Date() );
                    param.setModifiedDate( new Date() );
                    saveGenericParameter( param );
                }
            }
        }

        if ( mappings != null && mappings.size() > 0 ) {

            for ( MappingPojo pojo : mappings ) {
                LOG.debug( "Mapping: " + pojo.getNxMappingId() + " - " + pojo.getCategory() + " - "
                        + pojo.getLeftValue() + " - " + pojo.getRightValue() );
                if ( pojo.getNxMappingId() != 0 ) {
                    pojo.setModifiedDate( new Date() );
                    updateMapping( pojo );
                } else {
                    pojo.setCreatedDate( new Date() );
                    pojo.setModifiedDate( new Date() );
                    saveMapping( pojo );
                }
            }
        }

        
    } // saveConfigurationToDB
} // CommunicationPartnerDAO
