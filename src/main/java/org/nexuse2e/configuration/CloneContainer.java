package org.nexuse2e.configuration;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

import org.nexuse2e.pojo.CertificatePojo;
import org.nexuse2e.pojo.ChoreographyPojo;
import org.nexuse2e.pojo.ComponentPojo;
import org.nexuse2e.pojo.LoggerPojo;
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
public class CloneContainer implements Serializable {

    /**
     * 
     */
    private static final long      serialVersionUID = 464928496062976971L;
    private List<ChoreographyPojo> choreographies;
    private List<CertificatePojo>  certificates;
    private List<PartnerPojo>      partners;
    private List<PipelinePojo>     frontendPipelineTemplates;
    private List<PipelinePojo>     backendPipelineTemplates;
    private List<TRPPojo>          trps;
    private List<ComponentPojo>    components;
    private List<LoggerPojo>       loggers;
    private List<ServicePojo>      services;
    private List<UserPojo>         users;
    private List<RolePojo>         roles;

    public CloneContainer( EngineConfiguration config ) {

        trps = config.getTrps();
        choreographies = config.getChoreographies();
        certificates = config.getCertificates();
        partners = config.getPartners();
        frontendPipelineTemplates = config.getFrontendPipelineTemplates();
        backendPipelineTemplates = config.getBackendPipelineTemplates();
        components = config.getComponents();
        loggers = config.getLoggers();
        services = config.getServices();
        users = config.getUsers();
        roles = config.getRoles();

    }

    /**
     * CloneContainer instance is cloned and piped into the EngineConfiguration configuration
     * @param configuration
     * @return 
     */
    public EngineConfiguration cloneContainer(EngineConfiguration configuration) {
        if(configuration == null ) {
            return null;
        }
        try {
            for ( LoggerPojo logger : loggers ) {
                System.out.println("logger: "+logger.getName());
                
            }
//            trps.clear();
//            choreographies.clear();

//            certificates.clear();
//            partners.clear();
            
//            frontendPipelineTemplates.clear();
//            backendPipelineTemplates.clear();
//            components.clear();
//            loggers.clear();
//            services.clear();
//            users.clear();
//            roles.clear();
            
            
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream( bos );
            out.writeObject( this );
            out.flush();
            out.close();

            ObjectInputStream in = new ObjectInputStream( new ByteArrayInputStream( bos.toByteArray() ) );
            CloneContainer newContainer = (CloneContainer) in.readObject();

            configuration.setTrps( newContainer.getTrps() );
            configuration.setPartners( newContainer.getPartners() );
            configuration.setChoreographies( newContainer.getChoreographies() );
            configuration.setComponents( newContainer.getComponents() );
            configuration.setFrontendPipelineTemplates( newContainer.getFrontendPipelineTemplates() );
            configuration.setBackendPipelineTemplates( newContainer.getBackendPipelineTemplates() );
            configuration.setServices( newContainer.getServices() );
            configuration.setLoggers( newContainer.getLoggers() );
            configuration.setCertificates( newContainer.getCertificates() );
            configuration.setUsers( newContainer.getUsers() );
            configuration.setRoles( newContainer.getRoles() );

        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return configuration;
    }
    
    
    /**
     * @return the backendPipelineTemplates
     */
    public List<PipelinePojo> getBackendPipelineTemplates() {

        return backendPipelineTemplates;
    }

    /**
     * @param backendPipelineTemplates the backendPipelineTemplates to set
     */
    public void setBackendPipelineTemplates( List<PipelinePojo> backendPipelineTemplates ) {

        this.backendPipelineTemplates = backendPipelineTemplates;
    }

    /**
     * @return the certificates
     */
    public List<CertificatePojo> getCertificates() {

        return certificates;
    }

    /**
     * @param certificates the certificates to set
     */
    public void setCertificates( List<CertificatePojo> certificates ) {

        this.certificates = certificates;
    }

    /**
     * @return the choreographies
     */
    public List<ChoreographyPojo> getChoreographies() {

        return choreographies;
    }

    /**
     * @param choreographies the choreographies to set
     */
    public void setChoreographies( List<ChoreographyPojo> choreographies ) {

        this.choreographies = choreographies;
    }

    /**
     * @return the components
     */
    public List<ComponentPojo> getComponents() {

        return components;
    }

    /**
     * @param components the components to set
     */
    public void setComponents( List<ComponentPojo> components ) {

        this.components = components;
    }

    /**
     * @return the frontendPipelineTemplates
     */
    public List<PipelinePojo> getFrontendPipelineTemplates() {

        return frontendPipelineTemplates;
    }

    /**
     * @param frontendPipelineTemplates the frontendPipelineTemplates to set
     */
    public void setFrontendPipelineTemplates( List<PipelinePojo> frontendPipelineTemplates ) {

        this.frontendPipelineTemplates = frontendPipelineTemplates;
    }

    /**
     * @return the loggers
     */
    public List<LoggerPojo> getLoggers() {

        return loggers;
    }

    /**
     * @param loggers the loggers to set
     */
    public void setLoggers( List<LoggerPojo> loggers ) {

        this.loggers = loggers;
    }

    /**
     * @return the partners
     */
    public List<PartnerPojo> getPartners() {

        return partners;
    }

    /**
     * @param partners the partners to set
     */
    public void setPartners( List<PartnerPojo> partners ) {

        this.partners = partners;
    }

    /**
     * @return the roles
     */
    public List<RolePojo> getRoles() {

        return roles;
    }

    /**
     * @param roles the roles to set
     */
    public void setRoles( List<RolePojo> roles ) {

        this.roles = roles;
    }

    /**
     * @return the services
     */
    public List<ServicePojo> getServices() {

        return services;
    }

    /**
     * @param services the services to set
     */
    public void setServices( List<ServicePojo> services ) {

        this.services = services;
    }

    /**
     * @return the trps
     */
    public List<TRPPojo> getTrps() {

        return trps;
    }

    /**
     * @param trps the trps to set
     */
    public void setTrps( List<TRPPojo> trps ) {

        this.trps = trps;
    }

    /**
     * @return the users
     */
    public List<UserPojo> getUsers() {

        return users;
    }

    /**
     * @param users the users to set
     */
    public void setUsers( List<UserPojo> users ) {

        this.users = users;
    }

};