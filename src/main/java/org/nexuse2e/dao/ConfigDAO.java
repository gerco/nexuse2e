package org.nexuse2e.dao;

import java.util.List;

import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.pojo.CertificatePojo;
import org.nexuse2e.pojo.ChoreographyPojo;
import org.nexuse2e.pojo.ComponentPojo;
import org.nexuse2e.pojo.ConnectionPojo;
import org.nexuse2e.pojo.GenericParamPojo;
import org.nexuse2e.pojo.LoggerPojo;
import org.nexuse2e.pojo.MappingPojo;
import org.nexuse2e.pojo.PartnerPojo;
import org.nexuse2e.pojo.PipelinePojo;
import org.nexuse2e.pojo.RolePojo;
import org.nexuse2e.pojo.ServicePojo;
import org.nexuse2e.pojo.TRPPojo;
import org.nexuse2e.pojo.UserPojo;

public interface ConfigDAO {

    /**
     * @param trp
     */
    public abstract void saveTRP( TRPPojo trp );

    /**
     * @param trp
     */
    public abstract void updateTRP( TRPPojo trp );

    /**
     * @param partner
     */
    public abstract void savePartner( PartnerPojo partner );

    /**
     * @param partner
     */
    public abstract void updatePartner( PartnerPojo partner );

    /**
     * @param partner
     */
    public abstract void deletePartner( PartnerPojo partner );

    /**
     * @param connection
     */
    public abstract void deleteConnection( ConnectionPojo connection );

    /**
     * @return
     */
    @SuppressWarnings("unchecked")
    public abstract List<PartnerPojo> getPartners();

    /**
     * @param choreography
     */
    public abstract void saveChoreography( ChoreographyPojo choreography );

    /**
     * @param choreography
     */
    public abstract void updateChoreography( ChoreographyPojo choreography );

    /**
     * @param choreography
     */
    public abstract void deleteChoreography( ChoreographyPojo choreography );

    /**
     * @return
     */
    @SuppressWarnings("unchecked")
    public abstract List<ChoreographyPojo> getChoreographies();

    /**
     * @param choreography
     */
    public abstract void savePipeline( PipelinePojo pipeline );

    /**
     * @param choreography
     */
    public abstract void updatePipeline( PipelinePojo pipeline );

    /**
     * @param choreography
     */
    public abstract void deletePipeline( PipelinePojo pipeline );

    /**
     * @return
     */
    @SuppressWarnings("unchecked")
    public abstract List<PipelinePojo> getFrontendPipelines();

    /**
     * @return
     */
    @SuppressWarnings("unchecked")
    public abstract List<PipelinePojo> getBackendPipelines();

    /**
     * @return
     */
    @SuppressWarnings("unchecked")
    public abstract List<PipelinePojo> getPipelines();

    /**
     * @param trp
     */
    public abstract void saveTrp( TRPPojo trp );

    /**
     * @param trp
     */
    public abstract void updateTrp( TRPPojo trp );

    /**
     * @param trp
     */
    public abstract void deleteTrp( TRPPojo trp );

    /**
     * @return
     */
    @SuppressWarnings("unchecked")
    public abstract List<TRPPojo> getTrps();

    @SuppressWarnings("unchecked")
    public abstract List<ComponentPojo> getComponents() throws NexusException;

    /**
     * @param trp
     */
    public abstract void saveComponent( ComponentPojo component );

    /**
     * @param trp
     */
    public abstract void updateComponent( ComponentPojo component );

    /**
     * @param trp
     */
    public abstract void deleteComponent( ComponentPojo component ) throws NexusException;

    /**
     * @return
     */
    @SuppressWarnings("unchecked")
    public abstract List<LoggerPojo> getLoggers();

    /**
     * @param logger
     */
    public abstract void deleteLogger( LoggerPojo logger );

    /**
     * @param logger
     */
    public abstract void updateLogger( LoggerPojo logger );

    /**
     * @param logger
     */
    public abstract void saveLogger( LoggerPojo logger );

    /**
     * Gets a list ofall services.
     * @return A list of all services, ordered by their positions.
     */
    @SuppressWarnings("unchecked")
    public abstract List<ServicePojo> getServices();

    /**
     * Deletes a service.
     * @param service The service to be deleted.
     */
    public abstract void deleteService( ServicePojo service );

    /**
     * Updates a service.
     * @param service The service to update.
     */
    public abstract void updateService( ServicePojo service );

    /**
     * Persists a service.
     * @param service The service to be persisted.
     */
    public abstract void saveService( ServicePojo service );

    /**
     * @return
     */
    @SuppressWarnings("unchecked")
    public abstract List<CertificatePojo> getCertificates();

    /**
     * @param certificate
     */
    public abstract void deleteCertificate( CertificatePojo certificate );

    /**
     * @param certificate
     */
    public abstract void updateCertificate( CertificatePojo certificate );

    /**
     * @param certificate
     */
    public abstract void saveCertificate( CertificatePojo certificate );

    /*
     * USER SECTION
     */
    @SuppressWarnings("unchecked")
    public abstract List<UserPojo> getUsers();

    public abstract void updateUser( UserPojo user );

    public abstract void saveUser( UserPojo user );

    /**
     * Deletes a user.
     * @param user The user to delete.
     */
    public abstract void deleteUser( UserPojo user );

    /*
     * ROLE SECTION
     */
    @SuppressWarnings("unchecked")
    public abstract List<RolePojo> getRoles();

    /**
     * @param role
     */
    public abstract void updateRole( RolePojo role );

    /**
     * @param role
     */
    public abstract void saveRole( RolePojo role ) throws NexusException;

    /**
     * Deletes a role.
     * @param role The role to delete.
     */
    public abstract void deleteRole( RolePojo role ) throws NexusException;

    /**
     * @return
     */
    @SuppressWarnings("unchecked")
    public abstract List<GenericParamPojo> getGenericParameters();

    /**
     * @param param
     */
    public abstract void updateGenericParameter( GenericParamPojo param );

    /**
     * @param param
     */
    public abstract void saveGenericParameter( GenericParamPojo param );

    /**
     * Deletes a parameter.
     * @param param The parameter to delete.
     */
    public abstract void deleteGemericParameter( GenericParamPojo param );

    /**
     * @return
     */
    @SuppressWarnings("unchecked")
    public abstract List<MappingPojo> getMappings();

    /**
     * @param param
     */
    public abstract void updateMapping( MappingPojo mapping );

    /**
     * @param param
     */
    public abstract void saveMapping( MappingPojo mapping );

    /**
     * Deletes a mapping.
     * @param param The parameter to delete.
     */
    public abstract void deleteMapping( MappingPojo mapping );

    /**
     * Deletes the whole database. Don't call this method unless you really know what you're doing!
     */
    public abstract void deleteAll() throws NexusException;

    /**
     * Determines if the configuration database has been created or not.
     * @return <code>true</code> if and only if the database has been created and contains
     * a configuration.
     * @throws NexusException if the database could not be accessed for some reason.
     */
    public abstract boolean isDatabasePopulated() throws NexusException;

    /**
     * Fills the given <code>EngineConfiguration</code> object with the configuration
     * in the database.
     * @param configuration The <code>EngineConfiguration</code> object to be filled
     * with the configuration in the database.
     * @throws NexusException If something went wrong (e.g., no DB connection).
     */
    public abstract void loadDatafromDB( EngineConfiguration configuration ) throws NexusException; // loadDataFromDB

    /**
     * Saves the difference between the current configuration and the given configuration.
     * @param configuration The configuration containing the changes that shall apply.
     * @throws NexusException If the delta could not be saved.
     */
    public abstract void saveDelta( EngineConfiguration configuration );

    /**
     * @throws NexusException
     */
    public abstract void saveConfigurationToDB( EngineConfiguration configuration ) throws NexusException; // saveConfigurationToDB

}