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

import java.security.KeyStore;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.nexuse2e.NexusException;
import org.nexuse2e.backend.BackendPipelineDispatcher;
import org.nexuse2e.configuration.Constants.ComponentType;
import org.nexuse2e.configuration.Constants.ParameterType;
import org.nexuse2e.pojo.ActionPojo;
import org.nexuse2e.pojo.CertificatePojo;
import org.nexuse2e.pojo.ChoreographyPojo;
import org.nexuse2e.pojo.ComponentPojo;
import org.nexuse2e.pojo.ConnectionPojo;
import org.nexuse2e.pojo.LoggerPojo;
import org.nexuse2e.pojo.MappingPojo;
import org.nexuse2e.pojo.ParticipantPojo;
import org.nexuse2e.pojo.PartnerPojo;
import org.nexuse2e.pojo.PipelinePojo;
import org.nexuse2e.pojo.RolePojo;
import org.nexuse2e.pojo.ServicePojo;
import org.nexuse2e.pojo.TRPPojo;
import org.nexuse2e.pojo.UserPojo;
import org.nexuse2e.service.Service;

/**
 * @author gesch, sschulze
 *
 */
public interface ConfigurationAccessService {

    /**
     * @param nxPipelineId
     * @return
     */
    public abstract PipelinePojo getPipelinePojoByNxPipelineId(int nxPipelineId);

    /**
     * @param name
     * @return
     */
    public abstract PipelinePojo getPipelineByName(String name);

    /**
     * @param nxServiceId
     * @return
     */
    public abstract ServicePojo getServicePojoByNxServiceId(int nxServiceId);

    /**
     * @param name
     * @return
     */
    public abstract ServicePojo getServicePojoName(String name);

    /**
     * Return the BackendPipelineDispatcher instance
     * @return The BackendPipelineDispatcher instance
     */
    public abstract BackendPipelineDispatcher getBackendPipelineDispatcher(); // getBackendPipelineDispatcher

    /**
     * @return
     */
    public abstract List<PipelinePojo> getFrontendPipelinePojos(int type,
            Comparator<PipelinePojo> comparator);

    /**
     * @return
     */
    public abstract List<PipelinePojo> getBackendPipelinePojos(int type,
            Comparator<PipelinePojo> comparator);

    /**
     * @return
     */
    public abstract List<TRPPojo> getTrps();

    /**
     * @param nxTrpId
     * @return
     */
    public abstract TRPPojo getTrpByNxTrpId(int nxTrpId);

    /**
     * @param protocol
     * @param version
     * @param transport
     * @return
     */
    public abstract TRPPojo getTrpByProtocolVersionAndTransport(
            String protocol, String version, String transport);

    /**
     * @param nxPartnerId
     * @return
     * @throws NexusException
     */
    public abstract PartnerPojo getPartnerByNxPartnerId(int nxPartnerId)
            throws NexusException;

    /**
     * @param nxComponentId
     * @return
     * @throws NexusException
     */
    public abstract ComponentPojo getComponentByNxComponentId(int nxComponentId)
            throws NexusException;

    /**
     * @param partnerId
     * @return
     * @throws NexusException
     */
    public abstract PartnerPojo getPartnerByPartnerId(String partnerId)
            throws NexusException;

    /**
     * @param partner
     * @param nxCertificateId
     * @return
     */
    public abstract CertificatePojo getCertificateFromPartnerByNxCertificateId(
            PartnerPojo partner, int nxCertificateId);

    /**
     * @param partner
     * @param nxConnectionId
     * @return
     */
    public abstract ConnectionPojo getConnectionFromPartnerByNxConnectionId(
            PartnerPojo partner, int nxConnectionId);

    /**
     * @return
     * @throws NexusException
     */
    public abstract List<ChoreographyPojo> getChoreographies()
            throws NexusException;

    /**
     * @param type
     * @param comparator
     * @return
     * @throws NexusException
     */
    public abstract List<ComponentPojo> getComponents(ComponentType type,
            Comparator<ComponentPojo> comparator) throws NexusException;

    public abstract List<ComponentPojo> getPipelets(boolean frontend)
            throws NexusException;

    /**
     * @param type 0=all, 1=local, 2=partner
     * @param comparator
     * @return
     * @throws NexusException
     */
    public abstract List<PartnerPojo> getPartners(int type,
            Comparator<PartnerPojo> comparator) throws NexusException;

    /**
     * @param type 0=all, 1=local, 2=partner
     * @param comparator
     * @return
     * @throws NexusException
     */
    public abstract List<CertificatePojo> getCertificates(int type,
            Comparator<CertificatePojo> comparator) throws NexusException;

    /**
     * @param type
     * @param name
     * @return
     * @throws NexusException
     */
    public abstract CertificatePojo getCertificateByName(int type, String name)
            throws NexusException;

    /**
     * @param type
     * @param nxCertificateId
     * @return
     * @throws NexusException
     */
    public abstract CertificatePojo getCertificateByNxCertificateId(int type,
            int nxCertificateId) throws NexusException;

    /**
     * @param type certificate type, defined in configuration.constants. 
     * @param isUnique if true, method throws exception if there is more than one element of selected type in database
     * @return
     * @throws NexusException
     */
    public abstract CertificatePojo getFirstCertificateByType(int type,
            boolean isUnique) throws NexusException;

    /**
     * @param choreographyId
     * @return
     * @throws NexusException
     */
    public abstract ChoreographyPojo getChoreographyByChoreographyId(
            String choreographyId) throws NexusException;

    /**
     * @param nxChoreographyId
     * @return
     * @throws NexusException
     */
    public abstract ChoreographyPojo getChoreographyByNxChoreographyId(
            int nxChoreographyId) throws NexusException;

    /**
     * @param choreography
     * @param participantId
     * @return
     */
    public abstract ParticipantPojo getParticipantFromChoreographyByNxPartnerId(
            ChoreographyPojo choreography, int nxPartnerId);

    /**
     * @param choreography
     * @param nxActionId
     * @return
     */
    public abstract ActionPojo getActionFromChoreographyByNxActionId(
            ChoreographyPojo choreography, int nxActionId);

    /**
     * @param choreography
     * @param actionId
     * @return
     */
    public abstract ActionPojo getActionFromChoreographyByActionId(
            ChoreographyPojo choreography, String actionId);

    /**
     * @param choreography
     * @param partner
     * @return
     */
    public abstract ParticipantPojo getParticipantFromChoreographyByPartner(
            ChoreographyPojo choreography, PartnerPojo partner);

    /**
     * @return the engineConfig
     */
    public abstract EngineConfiguration getEngineConfig();

    public abstract void updatePartner(PartnerPojo partner) throws NexusException ;

    /**
     * @param choreography
     */
    public abstract void updateChoreography(ChoreographyPojo choreography) throws NexusException;

    /**
     * @param choreography
     * @throws ReferencedChoreographyException if the choreography is being referenced by
     * one or more conversations.
     * @throws NexusException if something else went wrong.
     */
    public abstract void deleteChoreography(ChoreographyPojo choreography)
            throws ReferencedChoreographyException, NexusException;

    /**
     * Removes a partner from the configuration.
     * @param partner The partner to be removed
     * @throws ReferencedPartnerException if the partner is being referenced by
     * one or more participants.
     * @throws NexusException if something else went wrong.
     */
    public abstract void deletePartner(PartnerPojo partner)
            throws ReferencedPartnerException, NexusException;

    /**
     * Removes a connection from the configuration.
     * @param connection The connection to be removed.
     * @throws ReferencedConnectionException if the connection is being referenced
     * by one or more participants.
     * @throws NexusException if something else went wrong.
     */
    public abstract void deleteConnection(ConnectionPojo connection)
            throws ReferencedConnectionException, NexusException;

    public abstract void updateTrp(TRPPojo trp) throws NexusException;

    public abstract void deleteTrp(TRPPojo trp) throws NexusException;

    /**
     * @param component
     */
    public abstract void updateComponent(ComponentPojo component) throws NexusException;

    /**
     * @param component
     */
    public abstract void deleteComponent(ComponentPojo component) throws NexusException;

    /**
     * @param pipeline
     */
    public abstract void updatePipeline(PipelinePojo pipeline)
            throws NexusException;

    /**
     * @param pipeline
     * @throws ReferencedPipelineException if the pipeline is being referenced by
     * one or more actions.
     * @throws NexusException if something else went wrong.
     */
    public abstract void deletePipeline(PipelinePojo pipeline)
            throws ReferencedPipelineException, NexusException;

    /**
     * Convenience method that allows to  update a <code>Service</code>
     * without having to retrieve it's <code>ServicePojo</code> object
     * before. The update is performed through the service's unique name.
     * @param name The name for the service. If the name could not be
     * resolved, this method does nothing.
     */
    public abstract void updateService(String name) throws NexusException;

    /**
     * Updates or saves a service.
     * @param servicePojo The service to be updated or saved.
     */
    public abstract void updateService(ServicePojo servicePojo) throws NexusException;

    /**
     * Deletes a service.
     * @param servicePojo The service to be deleted.
     */
    public abstract void deleteService(ServicePojo servicePojo) throws NexusException;

    /**
     * @param loggerPojo
     */
    public abstract void updateLogger(LoggerPojo loggerPojo) throws NexusException;

    /**
     * @param logger
     */
    public abstract void deleteLogger(LoggerPojo logger) throws NexusException;

    /**
     * @return
     */
    public abstract List<LoggerPojo> getLoggers();

    /**
     * @param nxLoggerId
     * @return
     */
    public abstract LoggerPojo getLoggerByNxLoggerId(int nxLoggerId);

    /**
     * Gets a logger by it's unique name.
     * @param name the logger name.
     * @return A <code>Logger</code> instance, or <code>null</code>
     * if no logger with the given name exists.
     */
    public abstract org.nexuse2e.logging.LogAppender getLogger(String name);

    /**
     * Renames a logger.
     * @param oldName The old logger name.
     * @param newName The new logger name.
     * @return The renamed logger. Never <code>null</code>.
     * @throws NexusException if <code>oldName</code> was not found or <code>newName</code>
     * already exists.
     */
    public abstract org.nexuse2e.logging.LogAppender renameLogger(
            String oldName, String newName) throws NexusException;

    /**
     * Gets a service by it's unique name.
     * @param name the service name.
     * @return A <code>Service</code> instance, or <code>null</code>
     * if no service with the given name exists.
     */
    public abstract Service getService(String name);

    /**
     * Renames a service.
     * @param oldName The old service name.
     * @param newName The new service name.
     * @return The renamed service. Never <code>null</code>.
     * @throws NexusException if <code>oldName</code> was not found or <code>newName</code>
     * already exists.
     */
    public abstract Service renameService(String oldName, String newName)
            throws NexusException;

    /**
     * Gets a list of all services that are registered.
     * @return A copied list of all services. Can be empty, but not <code>null</code>.
     */
    public abstract List<Service> getServiceInstances();

    /**
     * Gets a list of all services, represented as <code>ServicePojo</code>s.
     * @return A list of all services, as <code>ServicePojo</code> objects.
     */
    public abstract List<ServicePojo> getServices();

    /**
     * Removes the given certificate from the configuration.
     * @param certificate The certificate to be removed.
     * @throws ReferencedCertificateException if the certificate is being referenced by
     * one or more connections.
     * @throws NexusException if something else went wrong.
     */
    public abstract void deleteCertificate(CertificatePojo certificate)
            throws ReferencedCertificateException, NexusException;

    /**
     * Removes a list of certificates from the configuration.
     * @param certificates The certificates to be removed.
     * @throws ReferencedCertificateException if the certificate is being referenced by
     * one or more connections.
     * @throws NexusException if something else went wrong.
     */
    public abstract void deleteCertificates(
            Collection<CertificatePojo> certificates) throws NexusException,
            ReferencedCertificateException;

    /**
     * Updates a certificate.
     * @param certificate The certificate to be updated.
     * @throws NexusException if something went wrong.
     */
    public abstract void updateCertificate(CertificatePojo certificate)
            throws NexusException;

    /**
     * @return
     * @throws NexusException
     */
    public abstract KeyStore getCacertsKeyStore() throws NexusException;

    /**
     * Returns a sorted list of all users.
     * @param comparator The comperator to sort with.
     *          If <code>comparator</code> is <code>null</code> an unsorted list will be returned. 
     * @return a sorted list of all users.
     */
    public abstract List<UserPojo> getUsers(Comparator<UserPojo> comparator);

    /**
     * Returns the user with the given login name.
     * @param loginName login name of the desired user.
     * @return the user with the given <code>loginName</code> or <code>null</code>
     *          if no such user exists.
     */
    public abstract UserPojo getUserByLoginName(String loginName);

    /**
     * Returns the user with the given id.
     * @param nxUserId id of the desired user.
     * @return the user with the given <code>nxUserId</code> or <code>null</code>
     *          if no such user exists.
     */
    public abstract UserPojo getUserByNxUserId(int nxUserId);

    /**
     * Updates the given user or adds her to the list if she is new.
     * @param user User to add or update.
     */
    public abstract void updateUser(UserPojo user) throws NexusException;

    /**
     * Deletes the given user.
     * @param user User to delete.
     */
    public abstract void deleteUser(UserPojo user) throws NexusException;

    /**
     * Returns a sorted list of all roles.
     * @param comparator The comperator to sort with.
     * @return a sorted list of all roles.
     */
    public abstract List<RolePojo> getRoles(Comparator<RolePojo> comparator);

    /**
     * Returns the role with the given id.
     * @param nxRoleId id of the desired role.
     * @return the role with the given <code>nxRoleId</code> or <code>null</code>
     *          if no such role exists.
     */
    public abstract RolePojo getRoleByNxRoleId(int nxRoleId);

    /**
     * Returns the role with the given <code>name</code>.
     * @param name name of the desired role.
     * @return the role with the given <code>name</code> or <code>null</code>
     *          if no such role exists.
     */
    public abstract RolePojo getRoleByName(String name);

    /**
     * Updates the given role or adds it to the list if it is new.
     * @param role Role to add or update.
     */
    public abstract void updateRole(RolePojo role) throws NexusException;

    /**
     * Deletes the given role.
     * @param role Role to delete.
     */
    public abstract void deleteRole(RolePojo role) throws NexusException;

    /**
     * @param comparator
     * @return
     */
    public abstract List<MappingPojo> getMappings(
            Comparator<MappingPojo> comparator);

    /**
     * @param nxMappingId
     * @return
     */
    public abstract MappingPojo getMappingByNxMappingId(int nxMappingId);

    /**
     * @param category
     * @param left
     * @param key
     * @return
     */
    public abstract MappingPojo getMappingByCategoryDirectionAndKey(
            String category, boolean left, String key);

    /**
     * @param mapping
     * @throws NexusException 
     */
    public abstract void updateMapping(MappingPojo mapping)
            throws NexusException;

    /**
     * @param mapping
     */
    public abstract void updateMappings(List<MappingPojo> addMappings,
            List<MappingPojo> removeMappings) throws NexusException;

    /**
     * @param mapping
     * @throws NexusException 
     */
    public abstract void deleteMapping(MappingPojo mapping)
            throws NexusException;

    /**
     * @param category any subsystem selects its own category id to avoid parameter naming conflicts. 
     * @param tag optional ability to separate different sets of parameters for the same category, e.g. for different instances. 
     * @param descriptors 
     * @return
     */
    public abstract Map<String, Object> getGenericParameters(String category,
            String tag, Map<String, ParameterDescriptor> descriptors);

    /**
     * Gets the generic parameter for the given category and tag, or the default value
     * if not found.
     * @param category The category string. Not <code>null</code>.
     * @param tag The tag (parameter name). Not <code>null</code>.
     * @param type The parameter type. Not <code>null</code>.
     * @param defaultValue The default value. Can be <code>null</code>.
     * @return The value object, or <code>defaultValue</code> if no such parameter was found.
     */
    public abstract Object getGenericParameter(String category, String tag,
            ParameterType type, Object defaultValue);

    /**
     * @param category
     * @param tag
     * @param values Name, Value pairs you want to set. Values for missing descriptors are ingnored while changing configuration.
     * @param descriptors Map containing the matching ParameterDescriptors and Names, If there is no matching value for one
     * if the descriptors, the default value(specified in descriptor) is used.  
     * @throws NexusException Thrown on various database problems.
     */
    public abstract void setGenericParameters(String category, String tag,
            Map<String, Object> values,
            Map<String, ParameterDescriptor> descriptors )
            throws NexusException;

    /**
     * Might be used to determine whether parameters are available for this category and tag or even not.
     * Returns true, if there is one or more matching parameter available in your configuration.
     * 
     * @param category 
     * @param tag optional identifier for seprating different values for one category.
     * @return
     */
    public abstract boolean containsParameters(String category, String tag);

    /**
     * @param certs
     * @throws NexusException 
     */
    public abstract void updateCertificates(List<CertificatePojo> certs)
            throws NexusException;

}
