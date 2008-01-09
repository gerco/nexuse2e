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
package org.nexuse2e.configuration;

import java.util.List;

import org.nexuse2e.pojo.CertificatePojo;
import org.nexuse2e.pojo.ChoreographyPojo;
import org.nexuse2e.pojo.ComponentPojo;
import org.nexuse2e.pojo.LoggerPojo;
import org.nexuse2e.pojo.MappingPojo;
import org.nexuse2e.pojo.PartnerPojo;
import org.nexuse2e.pojo.PipelinePojo;
import org.nexuse2e.pojo.RolePojo;
import org.nexuse2e.pojo.ServicePojo;
import org.nexuse2e.pojo.TRPPojo;
import org.nexuse2e.pojo.UserPojo;

/**
 * Interface to provide a base configuration for the NEXUSe2e engine. 
 * Implementations are used e.g. on first start-up when a new database is created. 
 * Following the schema an implementation of <code>BaseConfigurationProvider</code> will
 * be called to provide a minimal system configuration. The can be implementations e.g. for a 
 * server or client deployment.
 * 
 * @author mbreilmann
 *
 */
public interface BaseConfigurationProvider {

    /**
     * Determines if a configuration be made available by this <code>BaseConfigurationProvider</code>.
     * @return <code>true</code> if a configuration is available, <code>false</code> otherwise.
     */
    public boolean isConfigurationAvailable();
    
    /**
     * Method that create the base system configuration. Receives all (empty) lists that need to be pupulated.
     * @param components The list of components (i.e. Pipelets or Loggers) that are known to the system
     * @param choreographies The list of choreographies
     * @param partners The list of commmunication/trading partners
     * @param backendPipelineTemplates The backend pipelines
     * @param frontendPipelineTemplates The fronend pipelines
     * @param services The list of services.
     * @param caCertificates The CA certificates used by the system
     * @param trps List of all supported Transport/Routing/Packaging combinations supported by the systesm
     * @param users List of all initial defined user accounts.
     * @param roles List of all initial defined user roles.
     */
    public void createBaseConfiguration( List<ComponentPojo> components, List<ChoreographyPojo> choreographies,
            List<PartnerPojo> partners, List<PipelinePojo> backendPipelineTemplates,
            List<PipelinePojo> frontendPipelineTemplates, List<ServicePojo> services,
            List<CertificatePojo> caCertificates, List<TRPPojo> trps, List<UserPojo> users, List<RolePojo> roles,
            List<LoggerPojo> loggers, List<MappingPojo> mappings ) throws InstantiationException;

} // BaseConfigurationProvider
