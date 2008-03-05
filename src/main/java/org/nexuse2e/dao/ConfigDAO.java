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
package org.nexuse2e.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
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

/**
 * @author gesch
 *
 */
public class ConfigDAO extends BasicDAO {

    private static Logger LOG = Logger.getLogger( ConfigDAO.class );

    
    /**
     * @param trp
     * @param session
     * @param transaction
     * @throws NexusException
     */
    public void saveTRP( TRPPojo trp, Session session, Transaction transaction ) throws NexusException {

        saveRecord( trp, session, transaction );

    }

    /**
     * @param trp
     * @param session
     * @param transaction
     * @throws NexusException
     */
    public void updateTRP( TRPPojo trp, Session session, Transaction transaction ) throws NexusException {

        updateRecord( trp, session, transaction );

    }

    /**
     * @param partner
     * @param session
     * @param transaction
     * @throws NexusException
     */
    public void savePartner( PartnerPojo partner, Session session, Transaction transaction ) throws NexusException {

        partner.setCreatedDate( new Date() );
        partner.setModifiedDate( new Date() );
        saveRecord( partner, session, transaction );

    }

    /**
     * @param partner
     * @param session
     * @param transaction
     * @throws NexusException
     */
    public void updatePartner( PartnerPojo partner, Session session, Transaction transaction ) throws NexusException {

        partner.setModifiedDate( new Date() );
        updateRecord( partner, session, transaction );
    }

    /**
     * @param partner
     * @param session
     * @param transaction
     * @throws NexusException
     */
    public void deletePartner( PartnerPojo partner, Session session, Transaction transaction ) throws NexusException {

        if ( partner != null ) {
            ArrayList<Object> list = new ArrayList<Object>();
            list.addAll( partner.getConnections() );
            list.addAll( partner.getCertificates() );
            list.add( partner );
            deleteRecords( list, session, transaction );
        }
    }

    /**
     * @param connection
     * @param session
     * @param transaction
     * @throws NexusException
     */
    public void deleteConnection(
            ConnectionPojo connection, Session session, Transaction transaction ) throws NexusException {

        if ( connection != null ) {
            deleteRecord( connection, session, transaction );
        }
    }

    /**
     * @param session
     * @param transaction
     * @return
     * @throws NexusException
     */
    @SuppressWarnings("unchecked")
    public List<PartnerPojo> getPartners( Session session, Transaction transaction ) throws NexusException {

        // "select partner from PartnerPojo as partner"
        StringBuffer query = new StringBuffer( "from PartnerPojo" );

        return getListThroughSessionFind( query.toString(), session, transaction );
    }

    /**
     * @param choreography
     * @param session
     * @param transaction
     * @throws NexusException
     */
    public void saveChoreography( ChoreographyPojo choreography, Session session, Transaction transaction )
            throws NexusException {

        choreography.setCreatedDate( new Date() );
        choreography.setModifiedDate( new Date() );
        saveOrUpdateRecord( choreography, session, transaction );

    }

    /**
     * @param choreography
     * @param session
     * @param transaction
     * @throws NexusException
     */
    public void updateChoreography( ChoreographyPojo choreography, Session session, Transaction transaction )
            throws NexusException {

        choreography.setModifiedDate( new Date() );
        updateRecord( choreography, session, transaction );
    }

    /**
     * @param choreography
     * @param session
     * @param transaction
     * @throws NexusException
     */
    public void deleteChoreography( ChoreographyPojo choreography, Session session, Transaction transaction )
            throws NexusException {

        if ( choreography != null ) {
            deleteRecord( choreography, session, transaction );
        }
    }

    /**
     * @param session
     * @param transaction
     * @return
     * @throws NexusException
     */
    @SuppressWarnings("unchecked")
    public List<ChoreographyPojo> getChoreographies( Session session, Transaction transaction ) throws NexusException {

        StringBuffer query = new StringBuffer( "from ChoreographyPojo" );

        return getListThroughSessionFind( query.toString(), session, transaction );
    }

    /**
     * @param choreography
     * @param session
     * @param transaction
     * @throws NexusException
     */
    public void savePipeline( PipelinePojo pipeline, Session session, Transaction transaction ) throws NexusException {

        pipeline.setCreatedDate( new Date() );
        pipeline.setModifiedDate( new Date() );
        saveRecord( pipeline, session, transaction );

    }

    /**
     * @param choreography
     * @param session
     * @param transaction
     * @throws NexusException
     */
    public void updatePipeline( PipelinePojo pipeline, Session session, Transaction transaction ) throws NexusException {

        pipeline.setModifiedDate( new Date() );
        updateRecord( pipeline, session, transaction );
    }

    /**
     * @param choreography
     * @param session
     * @param transaction
     * @throws NexusException
     */
    public void deletePipeline( PipelinePojo pipeline, Session session, Transaction transaction ) throws NexusException {

        if ( pipeline != null ) {
            deleteRecord( pipeline, session, transaction );
        }
    }

    /**
     * @param session
     * @param transaction
     * @return
     * @throws NexusException
     */
    @SuppressWarnings("unchecked")
    public List<PipelinePojo> getFrontendPipelines( Session session, Transaction transaction ) throws NexusException {

        StringBuffer query = new StringBuffer( "from PipelinePojo as pipeline where pipeline.frontend=true" );

        return getListThroughSessionFind( query.toString(), session, transaction );
    }

    /**
     * @param session
     * @param transaction
     * @return
     * @throws NexusException
     */
    @SuppressWarnings("unchecked")
    public List<PipelinePojo> getBackendPipelines( Session session, Transaction transaction ) throws NexusException {

        StringBuffer query = new StringBuffer( "from PipelinePojo as pipeline where pipeline.frontend=false" );

        return getListThroughSessionFind( query.toString(), session, transaction );
    }

    /**
     * @param session
     * @param transaction
     * @return
     * @throws NexusException
     */
    @SuppressWarnings("unchecked")
    public List<PipelinePojo> getPipelines( Session session, Transaction transaction ) throws NexusException {

        StringBuffer query = new StringBuffer( "from PipelinePojo" );

        return getListThroughSessionFind( query.toString(), session, transaction );
    }

    /**
     * @param trp
     * @param session
     * @param transaction
     * @throws NexusException
     */
    public void saveTrp( TRPPojo trp, Session session, Transaction transaction ) throws NexusException {

        saveRecord( trp, session, transaction );

    }

    /**
     * @param trp
     * @param session
     * @param transaction
     * @throws NexusException
     */
    public void updateTrp( TRPPojo trp, Session session, Transaction transaction ) throws NexusException {

        updateRecord( trp, session, transaction );
    }

    /**
     * @param trp
     * @param session
     * @param transaction
     * @throws NexusException
     */
    public void deleteTrp( TRPPojo trp, Session session, Transaction transaction ) throws NexusException {

        if ( trp != null ) {
            deleteRecord( trp, session, transaction );
        }
    }

    /**
     * @param session
     * @param transaction
     * @return
     * @throws NexusException
     */
    @SuppressWarnings("unchecked")
    public List<TRPPojo> getTrps( Session session, Transaction transaction ) throws NexusException {

        StringBuffer query = new StringBuffer( "from TRPPojo" );

        return getListThroughSessionFind( query.toString(), session, transaction );
    }

    @SuppressWarnings("unchecked")
    public List<ComponentPojo> getComponents( Session session, Transaction transaction ) throws NexusException {

        StringBuffer query = new StringBuffer( "from ComponentPojo" );

        return getListThroughSessionFind( query.toString(), session, transaction );
    }

    /**
     * @param trp
     * @param session
     * @param transaction
     * @throws NexusException
     */
    public void saveComponent( ComponentPojo component, Session session, Transaction transaction )
            throws NexusException {

        saveRecord( component, session, transaction );

    }

    /**
     * @param trp
     * @param session
     * @param transaction
     * @throws NexusException
     */
    public void updateComponent( ComponentPojo component, Session session, Transaction transaction )
            throws NexusException {

        updateRecord( component, session, transaction );
    }

    /**
     * @param trp
     * @param session
     * @param transaction
     * @throws NexusException
     */
    public void deleteComponent( ComponentPojo component, Session session, Transaction transaction )
            throws NexusException {

        if ( component != null ) {
            deleteRecord( component, session, transaction );
        }
    }

    /**
     * @param session
     * @param transaction
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<LoggerPojo> getLoggers( Session session, Transaction transaction ) throws NexusException {

        StringBuffer query = new StringBuffer( "from LoggerPojo" );

        return getListThroughSessionFind( query.toString(), session, transaction );
    }

    /**
     * @param logger
     * @param session
     * @param transaction
     * @throws NexusException
     */
    public void deleteLogger( LoggerPojo logger, Session session, Transaction transaction ) throws NexusException {

        if ( logger != null ) {
            deleteRecord( logger, session, transaction );
        }
    }

    /**
     * @param logger
     * @param session
     * @param transaction
     * @throws NexusException
     */
    public void updateLogger( LoggerPojo logger, Session session, Transaction transaction ) throws NexusException {

        updateRecord( logger, session, transaction );

    }

    /**
     * @param logger
     * @param session
     * @param transaction
     * @throws NexusException
     */
    public void saveLogger( LoggerPojo logger, Session session, Transaction transaction ) throws NexusException {

        saveRecord( logger, session, transaction );

    }

    /**
     * Gets a list ofall services.
     * @param session The hibernate session.
     * @param transaction The transaction, or <code>null</code>.
     * @return A list of all services, ordered by their positions.
     * @throws NexusException
     */
    @SuppressWarnings("unchecked")
    public List<ServicePojo> getServices( Session session, Transaction transaction ) throws NexusException {

        return getListThroughSessionFind( "from ServicePojo service order by service.position", session, transaction );
    }

    /**
     * Deletes a service.
     * @param service The service to be deleted.
     * @param session The hibernate session.
     * @param transaction The transaction, or <code>null</code>.
     * @throws NexusException
     */
    public void deleteService( ServicePojo service, Session session, Transaction transaction ) throws NexusException {

        if ( service != null ) {
            deleteRecord( service, session, transaction );
        }
    }

    /**
     * Updates a service.
     * @param service The service to update.
     * @param session The hibernate session.
     * @param transaction The transaction, or <code>null</code>.
     * @throws NexusException
     */
    public void updateService( ServicePojo service, Session session, Transaction transaction ) throws NexusException {

        updateRecord( service, session, transaction );
    }

    /**
     * Persists a service.
     * @param service The service to be persisted.
     * @param session The hibernate session.
     * @param transaction The transaction, or <code>null</code>.
     * @throws NexusException
     */
    public void saveService( ServicePojo service, Session session, Transaction transaction ) throws NexusException {

        saveRecord( service, session, transaction );
    }

    /**
     * @param session
     * @param transaction
     * @return
     * @throws NexusException
     */
    @SuppressWarnings("unchecked")
    public List<CertificatePojo> getCertificates( Session session, Transaction transaction ) throws NexusException {

        StringBuffer query = new StringBuffer( "from CertificatePojo" );

        return getListThroughSessionFind( query.toString(), session, transaction );
    }

    /**
     * @param certificate
     * @param session
     * @param transaction
     * @throws NexusException
     */
    public void deleteCertificate( CertificatePojo certificate, Session session, Transaction transaction )
            throws NexusException {

        if ( certificate != null ) {
            deleteRecord( certificate, session, transaction );
        }
    }

    /**
     * @param certificate
     * @param session
     * @param transaction
     * @throws NexusException
     */
    public void updateCertificate( CertificatePojo certificate, Session session, Transaction transaction )
            throws NexusException {

        updateRecord( certificate, session, transaction );

    }

    /**
     * @param certificate
     * @param session
     * @param transaction
     * @throws NexusException
     */
    public void saveCertificate( CertificatePojo certificate, Session session, Transaction transaction )
            throws NexusException {

        saveRecord( certificate, session, transaction );

    }

    /*
     * USER SECTION
     */
    @SuppressWarnings("unchecked")
    public List<UserPojo> getUsers( Session session, Transaction transaction ) throws NexusException {

        StringBuffer query = new StringBuffer( "from UserPojo" );

        return getListThroughSessionFind( query.toString(), session, transaction );
    }

    public void updateUser( UserPojo user, Session session, Transaction transaction ) throws NexusException {

        updateRecord( user, session, transaction );

    }

    public void saveUser( UserPojo user, Session session, Transaction transaction ) throws NexusException {

        saveRecord( user, session, transaction );

    }

    /**
     * Deletes a user.
     * @param user The user to delete.
     * @param session The hibernate session.
     * @param transaction The transaction, or <code>null</code>.
     * @throws NexusException
     */
    public void deleteUser( UserPojo user, Session session, Transaction transaction ) throws NexusException {

        if ( user != null ) {
            deleteRecord( user, session, transaction );
        }
    }

    /*
     * ROLE SECTION
     */
    @SuppressWarnings("unchecked")
    public List<RolePojo> getRoles( Session session, Transaction transaction ) throws NexusException {

        StringBuffer query = new StringBuffer( "from RolePojo" );

        return getListThroughSessionFind( query.toString(), session, transaction );
    }

    /**
     * @param role
     * @param session
     * @param transaction
     * @throws NexusException
     */
    public void updateRole( RolePojo role, Session session, Transaction transaction ) throws NexusException {

        updateRecord( role, session, transaction );

    }

    /**
     * @param role
     * @param session
     * @param transaction
     * @throws NexusException
     */
    public void saveRole( RolePojo role, Session session, Transaction transaction ) throws NexusException {

        saveRecord( role, session, transaction );

    }

    /**
     * Deletes a role.
     * @param role The role to delete.
     * @param session The hibernate session.
     * @param transaction The transaction, or <code>null</code>.
     * @throws NexusException
     */
    public void deleteRole( RolePojo role, Session session, Transaction transaction ) throws NexusException {

        if ( role != null ) {
            deleteRecord( role, session, transaction );
        }
    }

    /**
     * @param session
     * @param transaction
     * @return
     * @throws NexusException
     */
    @SuppressWarnings("unchecked")
    public List<GenericParamPojo> getGenericParameters( Session session, Transaction transaction )
            throws NexusException {

        StringBuffer query = new StringBuffer( "from GenericParamPojo" );

        return getListThroughSessionFind( query.toString(), session, transaction );
    }

    /**
     * @param param
     * @param session
     * @param transaction
     * @throws NexusException
     */
    public void updateGenericParameter( GenericParamPojo param, Session session, Transaction transaction )
            throws NexusException {

        updateRecord( param, session, transaction );

    }

    /**
     * @param param
     * @param session
     * @param transaction
     * @throws NexusException
     */
    public void saveGenericParameter( GenericParamPojo param, Session session, Transaction transaction )
            throws NexusException {

        saveRecord( param, session, transaction );

    }

    /**
     * Deletes a parameter.
     * @param param The parameter to delete.
     * @param session The hibernate session.
     * @param transaction The transaction, or <code>null</code>.
     * @throws NexusException
     */
    public void deleteGemericParameter( GenericParamPojo param, Session session, Transaction transaction )
            throws NexusException {

        if ( param != null ) {
            deleteRecord( param, session, transaction );
        }
    }

    /**
     * @param session
     * @param transaction
     * @return
     * @throws NexusException
     */
    @SuppressWarnings("unchecked")
    public List<MappingPojo> getMappings( Session session, Transaction transaction ) throws NexusException {

        StringBuffer query = new StringBuffer( "from MappingPojo" );

        return getListThroughSessionFind( query.toString(), session, transaction );
    }

    /**
     * @param param
     * @param session
     * @param transaction
     * @throws NexusException
     */
    public void updateMapping( MappingPojo mapping, Session session, Transaction transaction ) throws NexusException {

        updateRecord( mapping, session, transaction );

    }

    /**
     * @param param
     * @param session
     * @param transaction
     * @throws NexusException
     */
    public void saveMapping( MappingPojo mapping, Session session, Transaction transaction ) throws NexusException {

        saveRecord( mapping, session, transaction );

    }

    /**
     * Deletes a mapping.
     * @param param The parameter to delete.
     * @param session The hibernate session.
     * @param transaction The transaction, or <code>null</code>.
     * @throws NexusException
     */
    public void deleteMapping( MappingPojo mapping, Session session, Transaction transaction ) throws NexusException {

        if ( mapping != null ) {
            deleteRecord( mapping, session, transaction );
        }
    }
    
    /**
     * Deletes the whole database. Don't call this method unless you really know what you're doing!
     */
    public void deleteAll() throws NexusException {
        Session session = super.getDBSession();
        Transaction t = session.beginTransaction();
        
        String[] typeNames = new String[] {
                "MessageLabelPojo",
                "MessagePayloadPojo",
                "MessagePojo",
                "ConversationPojo",
                "FollowUpActionPojo",
                "ActionPojo",
                "ParticipantPojo",
                "ConnectionPojo",
                "CertificatePojo",
                "PartnerPojo",
                "ChoreographyPojo",
                "ServiceParamPojo",
                "ServicePojo",
                "LoggerParamPojo",
                "LoggerPojo",
                "PipeletParamPojo",
                "PipeletPojo",
                "ComponentPojo",
                "PipelinePojo",
                "TRPPojo",
                "UserPojo",
                "GrantPojo",
                "RolePojo",
                "GenericParamPojo",
                "MappingPojo",
        };
        
        try {
            for (String typeName : typeNames) {
                Query query = session.createQuery( "delete from " + typeName );
                query.executeUpdate();
            }
            t.commit();
        } catch (Exception ex) {
            t.rollback();
            throw new NexusException( ex );
        }
    }

    /**
     * Determines if the configuration database has been created or not.
     * @return <code>true</code> if and only if the database has been created and contains
     * a configuration.
     * @throws NexusException if the database could not be accessed for some reason.
     */
    public boolean isDatabasePopulated() throws NexusException {

        List<TRPPojo> tempTRPs = getTrps( null, null );
        return ( tempTRPs != null ) && ( tempTRPs.size() != 0 );
    }

    /**
     * Fills the given <code>EngineConfiguration</code> object with the configuration
     * in the database.
     * @param configuration The <code>EngineConfiguration</code> object to be filled
     * with the configuration in the database.
     * @throws NexusException If something went wrong (e.g., no DB connection).
     */
    public void loadDatafromDB( EngineConfiguration configuration ) throws NexusException {

        Session session = getDBSession();
        List<ChoreographyPojo> tempChoreographies = getChoreographies( session, null );
        if ( tempChoreographies == null ) {
            LOG.debug( "No choreographies available in database!" );
        } else {
            LOG.trace( "ChoreographyCount:" + tempChoreographies.size() );
        }
        configuration.setChoreographies( tempChoreographies );

        List<PartnerPojo> tempPartners = getPartners( session, null );
        if ( tempPartners == null ) {
            LOG.debug( "No partners available in database!" );
        } else {
            LOG.trace( "PartnerCount:" + tempPartners.size() );
        }
        configuration.setPartners( tempPartners );

        List<CertificatePojo> allCertificates = getCertificates( session, null );
        if ( allCertificates == null || allCertificates.size() == 0 ) {
            LOG.debug( "No certificates available in database!" );
        }
        configuration.setCertificates( allCertificates );

        List<PipelinePojo> pipelines = getFrontendPipelines( session, null );
        if ( pipelines == null || pipelines.size() == 0 ) {
            LOG.debug( "No frontend pipelines available in database!" );
        }
        configuration.setFrontendPipelineTemplates( pipelines );

        pipelines = getBackendPipelines( session, null );
        if ( pipelines == null || pipelines.size() == 0 ) {
            LOG.debug( "No backend pipelines available in database!" );
        }
        configuration.setBackendPipelineTemplates( pipelines );

        List<TRPPojo> tempTRPs = getTrps( session, null );
        configuration.setTrps( tempTRPs );

        List<ComponentPojo> tempComponents = getComponents( session, null );
        configuration.setComponents( tempComponents );

        List<LoggerPojo> loggers = getLoggers( session, null );
        configuration.setLoggers( loggers );

        List<ServicePojo> services = getServices( session, null );
        configuration.setServices( services );

        List<UserPojo> users = getUsers( session, null );
        configuration.setUsers( users );

        List<RolePojo> roles = getRoles( session, null );
        configuration.setRoles( roles );

        List<MappingPojo> mappings = getMappings( session, null );
        configuration.setMappings( mappings );

        configuration.setGenericParameters( new HashMap<String, List<GenericParamPojo>>() );
        List<GenericParamPojo> tempParams = getGenericParameters( session, null );
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
        
        releaseDBSession( session );
    } // loadDataFromDB
    
    /**
     * Saves the difference between the current configuration and the given configuration.
     * @param configuration The configuration containing the changes that shall apply.
     * @throws NexusException If the delta could not be saved.
     */
    public void saveDelta( EngineConfiguration configuration ) throws NexusException {

        Session session = getDBSession();
        Transaction transaction = session.beginTransaction();

        try {
            // patch records that got a temporary (negative) ID
            for (NEXUSe2ePojo record : configuration.getUpdateList()) {
                if (record.getNxId() < 0) {
                    record.setNxId( 0 );
                }
            }
            for (NEXUSe2ePojo record : configuration.getImplicitUpdateList()) {
                if (record.getNxId() < 0) {
                    record.setNxId( 0 );
                }
            }
            // save records
            for (NEXUSe2ePojo record : configuration.getUpdateList()) {
                saveOrUpdateRecord( record, session, transaction );
            }
            // delete records
            for (NEXUSe2ePojo record : configuration.getDeleteList()) {
                deleteRecord( record, session, transaction );
            }
            
            transaction.commit();
        } catch (Exception ex) {
            transaction.rollback();
            
            if (ex instanceof NexusException) {
                throw (NexusException) ex;
            }
            throw new NexusException( ex );
        }
    }
    
    /**
     * @throws NexusException
     */
    public void saveConfigurationToDB( EngineConfiguration configuration ) throws NexusException {

        Session session = getDBSession();

        Transaction transaction = session.beginTransaction();

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
                    updateTRP( pojo, session, transaction );
                } else {
                    saveTRP( pojo, session, transaction );
                }
            }
        }

        if ( components != null && components.size() > 0 ) {

            for ( ComponentPojo pojo : components ) {
                LOG.debug( "Component: " + pojo.getNxComponentId() + " - " + pojo.getType() + " - " + pojo.getName() );
                if ( pojo.getNxComponentId() != 0 ) {
                    pojo.setModifiedDate( new Date() );
                    updateComponent( pojo, session, transaction );
                } else {
                    pojo.setCreatedDate( new Date() );
                    pojo.setModifiedDate( new Date() );
                    saveComponent( pojo, session, transaction );
                }
            }
        }

        if ( backendPipelineTemplates != null && backendPipelineTemplates.size() > 0 ) {

            for ( PipelinePojo pojo : backendPipelineTemplates ) {
                LOG.debug( "BackendPipeline: " + pojo.getNxPipelineId() + " - " + pojo.getName() );
                if ( pojo.getNxPipelineId() != 0 ) {
                    pojo.setModifiedDate( new Date() );
                    updatePipeline( pojo, session, transaction );
                } else {
                    pojo.setCreatedDate( new Date() );
                    pojo.setModifiedDate( new Date() );
                    savePipeline( pojo, session, transaction );
                }
            }
        }

        if ( frontendPipelineTemplates != null && frontendPipelineTemplates.size() > 0 ) {

            for ( PipelinePojo pojo : frontendPipelineTemplates ) {
                LOG.debug( "FrontendPipeline: " + pojo.getNxPipelineId() + " - " + pojo.getName() );
                if ( pojo.getNxPipelineId() != 0 ) {
                    pojo.setModifiedDate( new Date() );
                    updatePipeline( pojo, session, transaction );
                } else {
                    pojo.setCreatedDate( new Date() );
                    pojo.setModifiedDate( new Date() );
                    savePipeline( pojo, session, transaction );
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
                    updatePartner( pojo, session, transaction );
                } else {
                    pojo.setCreatedDate( new Date() );
                    pojo.setModifiedDate( new Date() );
                    savePartner( pojo, session, transaction );
                }
            }
        }

        if ( certificates != null && certificates.size() > 0 ) {

            for ( CertificatePojo certificate : certificates ) {
                LOG.debug( "Certificate: " + certificate.getNxCertificateId() + " - " + certificate.getName() );
                if ( certificate.getNxCertificateId() != 0 ) {
                    certificate.setModifiedDate( new Date() );
                    updateCertificate( certificate, session, transaction );
                } else {
                    certificate.setCreatedDate( new Date() );
                    certificate.setModifiedDate( new Date() );
                    saveCertificate( certificate, session, transaction );
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
                    updateChoreography( pojo, session, transaction );
                } else {
                    pojo.setCreatedDate( new Date() );
                    pojo.setModifiedDate( new Date() );
                    saveChoreography( pojo, session, transaction );
                }
            }
        }

        if ( services != null && services.size() > 0 ) {
            for ( ServicePojo pojo : services ) {
                LOG.debug( "Service: " + pojo.getNxServiceId() + " - " + pojo.getName() );
                if ( pojo.getNxServiceId() != 0 ) {
                    pojo.setModifiedDate( new Date() );
                    updateService( pojo, session, transaction );
                } else {
                    pojo.setCreatedDate( new Date() );
                    pojo.setModifiedDate( new Date() );
                    saveService( pojo, session, transaction );
                }
            }
        }

        if ( loggers != null && loggers.size() > 0 ) {

            for ( LoggerPojo logger : loggers ) {
                LOG.debug( "Logger: " + logger.getNxLoggerId() + " - " + logger.getName() );
                if ( logger.getNxLoggerId() != 0 ) {
                    logger.setModifiedDate( new Date() );
                    updateLogger( logger, session, transaction );
                } else {
                    logger.setCreatedDate( new Date() );
                    logger.setModifiedDate( new Date() );
                    saveLogger( logger, session, transaction );
                }
            }
        }

        // save roles first to ensure referential integrity
        if ( roles != null && roles.size() > 0 ) {

            for ( RolePojo role : roles ) {
                LOG.debug( "Role: " + role.getNxRoleId() + " - " + role.getName() );
                if ( role.getNxRoleId() != 0 ) {
                    role.setModifiedDate( new Date() );
                    updateRole( role, session, transaction );
                } else {
                    role.setCreatedDate( new Date() );
                    role.setModifiedDate( new Date() );
                    saveRole( role, session, transaction );
                }
            }
        }

        if ( users != null && users.size() > 0 ) {

            for ( UserPojo user : users ) {
                LOG.debug( "User: " + user.getNxUserId() + " - " + user.getLoginName() );
                if ( user.getNxUserId() != 0 ) {
                    user.setModifiedDate( new Date() );
                    updateUser( user, session, transaction );
                } else {
                    user.setCreatedDate( new Date() );
                    user.setModifiedDate( new Date() );
                    saveUser( user, session, transaction );
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
                    updateGenericParameter( param, session, transaction );
                } else {
                    param.setCreatedDate( new Date() );
                    param.setModifiedDate( new Date() );
                    saveGenericParameter( param, session, transaction );
                }
            }
        }

        if ( mappings != null && mappings.size() > 0 ) {

            for ( MappingPojo pojo : mappings ) {
                LOG.debug( "Mapping: " + pojo.getNxMappingId() + " - " + pojo.getCategory() + " - "
                        + pojo.getLeftValue() + " - " + pojo.getRightValue() );
                if ( pojo.getNxMappingId() != 0 ) {
                    pojo.setModifiedDate( new Date() );
                    updateMapping( pojo, session, transaction );
                } else {
                    pojo.setCreatedDate( new Date() );
                    pojo.setModifiedDate( new Date() );
                    saveMapping( pojo, session, transaction );
                }
            }
        }

        transaction.commit();
        releaseDBSession( session );
    } // saveConfigurationToDB
} // CommunicationPartnerDAO
