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
package org.nexuse2e.ui.form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.Constants.ParameterType;
import org.nexuse2e.pojo.ServiceParamPojo;
import org.nexuse2e.pojo.ServicePojo;
import org.nexuse2e.service.Service;

/**
 * The <code>ActionForm</code> used to retrieve and store information
 * for a <code>Service</code>
 *
 * @author jonas.reese
 */
public class ServiceForm extends ActionForm {

    private static final long       serialVersionUID    = 1L;

    private int                     nxServiceId         = 0;
    private int                     nxComponentId       = 0;
    private int                     paramsNxComponentId = 0;
    private String                  name                = null;
    private String                  componentName       = null;
    private int                     position            = 0;

    private String                  submitted           = "false";
    private HashMap<String, String> logFilterValues     = null;
    private HashMap<String, String> pipeletParamValues  = new HashMap<String, String>();
    private List<ServiceParamPojo>  parameters          = new ArrayList<ServiceParamPojo>();

    private Service                 serviceInstance;

    /* (non-Javadoc)
     * @see org.apache.struts.action.ActionForm#reset(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest)
     */
    @Override
    public void reset( ActionMapping arg0, HttpServletRequest arg1 ) {

        submitted = "false";
        componentName = null;
        nxServiceId = 0;
        if ( logFilterValues != null ) {
            logFilterValues.clear();
        }
        if ( pipeletParamValues != null ) {
            pipeletParamValues.clear();
        }
    } // reset

    /**
     * Set the properties of this form based on a POJO
     * @param notifier The POJO used to fill in the fields
     */
    public void setProperties( ServicePojo service ) {

        nxServiceId = service.getNxServiceId();
        nxComponentId = service.getComponent() == null ? 0 : service.getComponent().getNxComponentId();
        name = service.getName();
        if ( service.getComponent() != null ) {
            nxComponentId = service.getComponent().getNxComponentId();
            componentName = service.getComponent().getName();
        }
    }

    /**
     * Set the properties of this form based on a POJO
     * @return The updated POJO
     */
    public ServicePojo getProperties( ServicePojo service ) {

        service.setName( name );
        return service;
    }

    public void createParameterMapFromPojos() {

        pipeletParamValues = new HashMap<String, String>();
        for (ServiceParamPojo param : getParameters()) {
            pipeletParamValues.put( param.getParamName(), param.getValue() );
        }

    }

    public void fillPojosFromParameterMap() {

        if ( pipeletParamValues == null ) {
            return;
        }
        for ( ServiceParamPojo param : getParameters() ) {
            ParameterDescriptor pd = serviceInstance.getParameterMap().get( param.getParamName() );
            if (pd != null) {
                String value = pipeletParamValues.get( param.getParamName() );
                if (pd.getParameterType() == ParameterType.BOOLEAN) {
                    if ("on".equalsIgnoreCase( value )) {
                        value = Boolean.TRUE.toString();
                    }
                }
                if (value == null) {
                    value = Boolean.FALSE.toString();
                }
                param.setValue( value );
            }
        }
    }

    /**
     * @return the pipeletParamValues
     */
    public HashMap<String, String> getPipeletParamValues() {

        return pipeletParamValues;
    }

    /**
     * @param pipeletParamValues the pipeletParamValues to set
     */
    public void setPipeletParamValues( HashMap<String, String> pipeletParamValues ) {

        this.pipeletParamValues = pipeletParamValues;
    }

    public Object getParamValue( String key ) {

        return pipeletParamValues.get( key );
    }

    public void setParamValue( String key, Object value ) {

        // LOG.trace("key: "+key); 
        // LOG.trace("value: "+value.toString());
        pipeletParamValues.put( key, (String) value );
    }

    /**
     * @return the componentName
     */
    public String getComponentName() {

        return componentName;
    }

    /**
     * @param componentName the componentName to set
     */
    public void setComponentName( String componentName ) {

        this.componentName = componentName;
    }

    /**
     * @return the name
     */
    public String getName() {

        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName( String name ) {

        this.name = name;
    }

    /**
     * @return the nxServiceId
     */
    public int getNxServiceId() {

        return nxServiceId;
    }

    /**
     * @param nxServiceId the nxServiceId to set
     */
    public void setNxServiceId( int nxServiceId ) {

        this.nxServiceId = nxServiceId;
    }

    /**
     * @return the nxComponentId
     */
    public int getNxComponentId() {

        return nxComponentId;
    }

    /**
     * @param nxComponentId the nxComponentId to set
     */
    public void setNxComponentId( int nxComponentId ) {

        this.nxComponentId = nxComponentId;
    }

    /**
     * @return the parameters
     */
    public List<ServiceParamPojo> getParameters() {

        return parameters;
    }

    /**
     * @param parameters the parameters to set
     */
    public void setParameters( List<ServiceParamPojo> parameters ) {

        this.parameters = parameters;
    }

    /**
     * @return the submitted
     */
    public String getSubmitted() {

        return submitted;
    }

    /**
     * @param submitted the submitted to set
     */
    public void setSubmitted( String submitted ) {

        this.submitted = submitted;
    }

    /**
     * @return the logFilterValues
     */
    public HashMap<String, String> getLogFilterValues() {

        if ( logFilterValues == null ) {
            logFilterValues = new HashMap<String, String>();
        }
        return logFilterValues;
    }

    /**
     * @param logFilterValues the logFilterValues to set
     */
    public void setLogFilterValues( HashMap<String, String> logFilterValues ) {

        this.logFilterValues = logFilterValues;
    }

    /**
     * @param key
     * @return
     */
    public Object getLogFilterValue( String key ) {

        if ( logFilterValues == null ) {
            logFilterValues = new HashMap<String, String>();
        }
        return logFilterValues.get( key );
    }

    /**
     * @param key
     * @param value
     */
    public void setLogFilterValue( String key, Object value ) {

        logFilterValues.put( key, (String) value );
    }

    /**
     * @return the threshold
     */
    public int getPosition() {

        return position;
    }

    /**
     * @param threshold the threshold to set
     */
    public void setPosition( int threshold ) {

        this.position = threshold;
    }

    /**
     * @return the paramsNxComponentId
     */
    public int getParamsNxComponentId() {

        return paramsNxComponentId;
    }

    /**
     * @param paramsNxComponentId the paramsNxComponentId to set
     */
    public void setParamsNxComponentId( int paramsNxComponentId ) {

        this.paramsNxComponentId = paramsNxComponentId;
    }

    public Service getServiceInstance() {

        return serviceInstance;
    }

    public void setServiceInstance( Service serviceInstance ) {

        this.serviceInstance = serviceInstance;
    }

} // NotifierForm
