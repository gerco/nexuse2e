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

import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.nexuse2e.NexusException;
import org.nexuse2e.pojo.CertificatePojo;
import org.nexuse2e.pojo.ChoreographyPojo;
import org.nexuse2e.pojo.ComponentPojo;
import org.nexuse2e.pojo.GenericParamPojo;
import org.nexuse2e.pojo.LoggerPojo;
import org.nexuse2e.pojo.MappingPojo;
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
            if (!partner.getConnections().isEmpty()) {
                partner.getConnections().clear();
            }
            if (!partner.getCertificates().isEmpty()) {
                partner.getCertificates().clear();
            }
            deleteRecord( partner, session, transaction );
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

} // CommunicationPartnerDAO
