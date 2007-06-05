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
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.Constants.ParameterType;
import org.nexuse2e.logging.LogAppender;
import org.nexuse2e.pojo.ComponentPojo;
import org.nexuse2e.pojo.LoggerParamPojo;
import org.nexuse2e.pojo.LoggerPojo;

/**
 * @author gesch
 *
 */
public class LoggerForm extends ActionForm {

    private static final long              serialVersionUID         = 1L;

    private static org.apache.log4j.Logger LOG                      = org.apache.log4j.Logger
                                                                            .getLogger( LoggerForm.class );

    private int                            nxLoggerId               = 0;
    private int                            nxComponentId            = 0;
    private int                            nxChoreographyId         = 0;
    private int                            paramsNxComponentId      = 0;
    private String                         name                     = null;
    private String                         choreographyId           = null;
    private String                         componentId              = null;
    private int                            threshold                = 0;

    private boolean                        autoStart                = false;
    private boolean                        running                  = false;

    private String                         submitted                = "false";
    private HashMap<String, String>        logFilterValues          = null;
    private HashMap<String, String>        pipeletParamValues       = new HashMap<String, String>();
    private List<LoggerParamPojo>          parameters               = new Vector<LoggerParamPojo>();
    private List<String>                   groupNames               = new Vector<String>();
    private List<ComponentPojo>            availableTemplates       = new ArrayList<ComponentPojo>();

    private String                         filterJavaPackagePattern = "";
    private String                         componentName;

    private LoggerPojo                     logger                   = null;

    private LogAppender                    loggerInstance           = null;

    /* (non-Javadoc)
     * @see org.apache.struts.action.ActionForm#reset(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest)
     */
    @Override
    public void reset( ActionMapping arg0, HttpServletRequest arg1 ) {

        submitted = "false";
        componentId = null;
        nxLoggerId = 0;
        if ( logFilterValues != null ) {
            logFilterValues.clear();
        }
        if ( pipeletParamValues != null ) {
            pipeletParamValues.clear();
        }
        filterJavaPackagePattern = "";
    } // reset

    /**
     * Set the properties of this form based on a POJO
     * @param notifier The POJO used to fill in the fields
     */
    public void setProperties( LoggerPojo logger ) {

        nxLoggerId = logger.getNxLoggerId();
        name = logger.getName();
        running = logger.isRunning();
        autoStart = logger.isAutostart();
    }

    /**
     * Set the properties of this form based on a POJO
     * @return The updated POJO
     */
    public LoggerPojo getProperties( LoggerPojo logger ) {

        logger.setName( name );
        logger.setRunning( running );
        logger.setAutostart( autoStart );

        return logger;
    }

    public void createParameterMapFromPojos() {

        pipeletParamValues = new HashMap<String, String>();
        for ( LoggerParamPojo param : getParameters() ) {
            pipeletParamValues.put( param.getParamName(), param.getValue() );
        }

    }

    public void fillPojosFromParameterMap() {

        if ( pipeletParamValues == null ) {
            return;
        }
        for ( LoggerParamPojo param : getParameters() ) {
            ParameterDescriptor pd = loggerInstance.getParameterMap().get( param.getParamName() );
            if ( pd != null ) {
                String value = pipeletParamValues.get( param.getParamName() );
                if ( pd.getParameterType() == ParameterType.BOOLEAN ) {
                    if ( "on".equalsIgnoreCase( value ) ) {
                        value = Boolean.TRUE.toString();
                    }
                }
                if ( value == null ) {
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

        LOG.trace( "key: " + key );
        LOG.trace( "value: " + value.toString() );
        pipeletParamValues.put( key, (String) value );
    }

    /**
     * @return Returns the autoStart.
     */
    public boolean isAutoStart() {

        return autoStart;
    }

    /**
     * @return Returns the autoStart.
     */
    public String getAutoStartString() {

        return autoStart ? "Yes" : "No";
    }

    /**
     * @param autoStart The autoStart to set.
     */
    public void setAutoStart( boolean autoStart ) {

        this.autoStart = autoStart;
    }

    /**
     * @return Returns the started.
     */
    public String getRunningString() {

        return running ? "Running" : "Stopped";
    }

    /**
     * @return the componentName
     */
    public String getComponentId() {

        return componentId;
    }

    /**
     * @param componentName the componentName to set
     */
    public void setComponentId( String componentName ) {

        this.componentId = componentName;
    }

    /**
     * @return the filterJavaPackagePattern
     */
    public String getFilterJavaPackagePattern() {

        return filterJavaPackagePattern;
    }

    /**
     * @param filterJavaPackagePattern the filterJavaPackagePattern to set
     */
    public void setFilterJavaPackagePattern( String filterJavaPackagePattern ) {

        this.filterJavaPackagePattern = filterJavaPackagePattern;
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
     * @return the running
     */
    public boolean isRunning() {

        return running;
    }

    /**
     * @param running the running to set
     */
    public void setRunning( boolean running ) {

        this.running = running;
    }

    /**
     * @return the choreographyId
     */
    public String getChoreographyId() {

        return choreographyId;
    }

    /**
     * @param choreographyId the choreographyId to set
     */
    public void setChoreographyId( String choreographyId ) {

        this.choreographyId = choreographyId;
    }

    /**
     * @return the nxLoggerId
     */
    public int getNxLoggerId() {

        return nxLoggerId;
    }

    /**
     * @param nxLoggerId the nxLoggerId to set
     */
    public void setNxLoggerId( int nxLoggerId ) {

        this.nxLoggerId = nxLoggerId;
    }

    public String getFilterString() {

        return "dummyFilterString";
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
    public List<LoggerParamPojo> getParameters() {

        return parameters;
    }

    /**
     * @param parameters the parameters to set
     */
    public void setParameters( List<LoggerParamPojo> parameters ) {

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
     * @return the groupNames
     */
    public List<String> getGroupNames() {

        if ( groupNames == null ) {
            groupNames = new Vector<String>();
        }
        return groupNames;
    }

    /**
     * @param groupNames the groupNames to set
     */
    public void setGroupNames( List<String> groupNames ) {

        this.groupNames = groupNames;
    }

    /**
     * @return the threshold
     */
    public int getThreshold() {

        return threshold;
    }

    /**
     * @param threshold the threshold to set
     */
    public void setThreshold( int threshold ) {

        this.threshold = threshold;
    }

    /**
     * @return the logger
     */
    public LoggerPojo getLogger() {

        return logger;
    }

    /**
     * @param logger the logger to set
     */
    public void setLogger( LoggerPojo logger ) {

        this.logger = logger;
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

    public int getNxChoreographyId() {

        return nxChoreographyId;
    }

    public void setNxChoreographyId( int nxChoreographyId ) {

        this.nxChoreographyId = nxChoreographyId;
    }

    public String getComponentName() {

        return componentName;
    }

    public void setComponentName( String componentName ) {

        this.componentName = componentName;
    }

    public LogAppender getLoggerInstance() {

        return loggerInstance;
    }

    public void setLoggerInstance( LogAppender loggerInstance ) {

        this.loggerInstance = loggerInstance;
    }

    
    public List<ComponentPojo> getAvailableTemplates() {
    
        return availableTemplates;
    }

    
    public void setAvailableTemplates( List<ComponentPojo> availableTemplates ) {
    
        this.availableTemplates = availableTemplates;
    }

} // NotifierForm
