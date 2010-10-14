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
package org.nexuse2e.ui.form;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.nexuse2e.Configurable;
import org.nexuse2e.Engine;
import org.nexuse2e.configuration.ConfigurationUtil;
import org.nexuse2e.configuration.Constants;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.Constants.ParameterType;
import org.nexuse2e.pojo.ComponentPojo;
import org.nexuse2e.pojo.PipeletParamPojo;
import org.nexuse2e.pojo.PipeletPojo;
import org.nexuse2e.pojo.PipelinePojo;
import org.nexuse2e.pojo.TRPPojo;

/**
 * @author gesch
 *
 */
public class PipelineForm extends ActionForm {

    /**
     * 
     */
    private static final long       serialVersionUID   = -2860731115832301190L;

    private static Logger           LOG                = Logger.getLogger( PipelineForm.class );

    private int                     nxPipelineId       = 0;
    private int                     nxTrpId            = 0;
    private String                  name               = null;
    private String                  Description        = null;
    private int                     direction          = -1;
    private List<PipeletPojo>       pipelets           = new ArrayList<PipeletPojo>();
    private List<ComponentPojo>     availableTemplates = null;

    private List<PipeletParamPojo>  parameters         = new ArrayList<PipeletParamPojo>();
    private List<PipeletParamPojo>  obsoleteParameters = new ArrayList<PipeletParamPojo>();
    private List<TRPPojo>           trps               = null;

    private boolean                 frontend           = false;
    private PipeletPojo             currentPipelet     = null;
    private Configurable            configurable       = null;

    private HashMap<String, String> pipeletParamValues = null;
    /**
     * may contain different nexusIds, depends on submitaction 
     */
    private int                     actionNxId         = 0;
    private int                     actionNxIdReturn   = 0;

    private String                  paramName          = null;

    /**
     * 1=up, 2=down
     */
    private int                     sortingDirection   = 0;

    /**
     * sort = used to specify pipelet position in pipeline (actionNxId contains the pipelet which is inteded to be moved and sortingDirections defines
     * the direction. (1=up, 2=down)
     * add = adds the the Pipelet whis is found by actionNxId = nxComponentId
     * update = updates the whole pipeline in config and database
     */
    private String                  submitaction       = null;

    private int                     sortaction         = 0;

    private String                  key                = null;
    private String                  value              = null;
    
    private boolean                 bidirectional      = false;
    
    /**
     * @param component
     */
    public void setProperties( PipelinePojo pipeline ) {

        if ( pipeline == null ) {
            return;
        }
        setNxPipelineId( pipeline.getNxPipelineId() );
        if ( pipeline.getTrp() != null ) {
            setNxTrpId( pipeline.getTrp().getNxTRPId() );
        }
        setName( pipeline.getName() );
        setDescription( pipeline.getDescription() );
        if ( !pipeline.isOutbound() ) {
            setDirection( 0 );
        } else {
            setDirection( 1 );
        }

        setFrontend( pipeline.isFrontend() );

        List<PipeletPojo> pipeletList = null;

        setTrps( Engine.getInstance().getActiveConfigurationAccessService().getTrps() );

        pipeletList = new ArrayList<PipeletPojo>( pipeline.getPipelets() );
        if ( pipeline.getPipelets() != null ) {
            Collections.sort( pipeletList, Constants.PIPELETCOMPARATOR );
            if ((pipeline.isBackendInbound()) && pipeletList.size() > 1) {
                pipeletList.add( pipeletList.remove( 0 ) );
            }
        }
        setPipelets( pipeletList );

        // TODO: timer settings
    }

    /**
     * @param component
     * @return
     */
    public PipelinePojo getProperties( PipelinePojo pipeline ) {

        pipeline.setNxPipelineId( getNxPipelineId() );
        pipeline.setName( getName() );
        pipeline.setDescription( getDescription() );
        if ( getDirection() == 0 ) {
            pipeline.setOutbound( false );
        } else {
            pipeline.setOutbound( true );
        }
        if ( pipeline.getPipelets() == null ) {
            pipeline.setPipelets( new ArrayList<PipeletPojo>() );
        } else {
            pipeline.getPipelets().clear();
        }

        if ( getPipelets() != null ) {
            List<PipeletPojo> pipeletList = new ArrayList<PipeletPojo>( getPipelets() );
            if ((pipeline.isBackendInbound()) && pipeletList.size() > 1) {
                pipeletList.add( 0, pipeletList.remove( pipeletList.size() - 1 ) );
            }
            for (int i = 0; i < pipeletList.size(); i++) {
                PipeletPojo pipelet = pipeletList.get( i );
                pipelet.setPosition( i );
                pipeline.getPipelets().add( pipelet );
            }
        }

        TRPPojo trpPojo = Engine.getInstance().getActiveConfigurationAccessService().getTrpByNxTrpId( nxTrpId );
        if ( trpPojo != null ) {
            pipeline.setTrp( trpPojo );
        } else if ( pipeline.isFrontend() ) {
            LOG.error( "No valid TRP found!" );
        }
        
        //TODO: timer settings

        return pipeline;
    }

    public void createParameterMapFromPojos() {

        pipeletParamValues = new HashMap<String, String>();
        for ( PipeletParamPojo param : getParameters() ) {
            pipeletParamValues.put( param.getParamName(), ConfigurationUtil.getParameterStringValue( param ) );
        }
    }

    public void fillPojosFromParameterMap() {

        if ( pipeletParamValues == null ) {
            return;
        }
        if ( getParameters() != null ) {
            for ( PipeletParamPojo param : getParameters() ) {
                ParameterDescriptor pd = configurable.getParameterMap().get( param.getParamName() );
                if ( pd != null ) {
                    String value;
                    if (pd.getParameterType() == ParameterType.ENUMERATION) {
                        value = param.getValue();
                    } else {
                        value = pipeletParamValues.get( param.getParamName() );
                    }
                    if ( pd.getParameterType() == ParameterType.BOOLEAN ) {
                        if ( "on".equalsIgnoreCase( value ) ) {
                            value = Boolean.TRUE.toString();
                        }
                    }
                    if ( value == null ) {
                        value = Boolean.FALSE.toString();
                    }
                    ConfigurationUtil.setParameterStringValue( param, value );
                }
            }
        }
    }

    /**
     * @param mapping
     * @param request
     */
    @Override
    public void reset( ActionMapping mapping, HttpServletRequest request ) {

        System.out.println("mapping.name: "+mapping.getName());
        System.out.println("request: "+request.getParameterNames());
//        Enumeration e = request.getParameterNames();
//        while(e.hasMoreElements() ) {
//            System.out.println("param:"+e.nextElement());
//        }
        String action = request.getParameter( "submitaction" );
        
        if(mapping.getPath().indexOf( "PipelineView" ) != -1 &&(action == null)) {
            this.pipelets = new ArrayList<PipeletPojo>();
            this.parameters = new ArrayList<PipeletParamPojo>();
            this.currentPipelet = null;
        } else if(mapping.getPath().indexOf( "PipeletParamsUpdate" ) != -1) {
            for ( PipeletParamPojo pipeletParamPojo : parameters ) {
                if ( pipeletParamPojo.getParameterDescriptor().getParameterType() == ParameterType.BOOLEAN ) {
                    pipeletParamPojo.setValue( null );
                }
            }
        } else {
        }
        pipeletParamValues = new HashMap<String, String>();
        bidirectional = false;
    }

    /**
     * 
     */
    public void cleanSettings() {

        setNxPipelineId( 0 );
        setName( null );
        setDescription( null );
        setDirection( -1 );
        setPipelets( null );
        bidirectional = false;

    }

    /**
     * @return the description
     */
    public String getDescription() {

        return Description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription( String description ) {

        Description = description;
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
     * @return the nxPipelineId
     */
    public int getNxPipelineId() {

        return nxPipelineId;
    }

    /**
     * @param nxPipelineId the nxPipelineId to set
     */
    public void setNxPipelineId( int nxPipelineId ) {

        this.nxPipelineId = nxPipelineId;
    }

    /**
     * @return the direction
     */
    public int getDirection() {

        return direction;
    }

    /**
     * @param direction the direction to set
     */
    public void setDirection( int direction ) {

        this.direction = direction;
    }

    /**
     * @return the pipelets
     */
    public List<PipeletPojo> getPipelets() {

        return pipelets;
    }
    
    /**
     * Returns all pipelets that have the <code>forward</code> flag set to <code>true</code>.
     * @return A copied collection containing all forward pipelets.
     */
    public List<PipeletPojo> getForwardPipelets() {

        List<PipeletPojo> forwardPipelets = new ArrayList<PipeletPojo>();
        if (pipelets != null) {
            for (PipeletPojo pipelet : pipelets) {
                if (pipelet.isForward()) {
                    forwardPipelets.add( pipelet );
                }
            }
        }
        return forwardPipelets;
    }
    
    /**
     * Returns all pipelets that have the <code>forward</code> flag set to <code>false</code>.
     * @return A copied collection containing all backward pipelets.
     */
    public List<PipeletPojo> getReturnPipelets() {

        List<PipeletPojo> returnPipelets = new ArrayList<PipeletPojo>();
        if (pipelets != null) {
            for (PipeletPojo pipelet : pipelets) {
                if (!pipelet.isForward()) {
                    returnPipelets.add( pipelet );
                }
            }
        }
        return returnPipelets;
    }
    
    /**
     * @param pipelets the pipelets to set
     */
    public void setPipelets( List<PipeletPojo> pipelets ) {

        this.pipelets = pipelets;
    }
    
    public int getForwardPipeletCount() {
        if (pipelets == null) {
            return 0;
        }
        int c = 0;
        for (PipeletPojo pipelet : pipelets) {
            if (pipelet.isForward()) {
                c++;
            }
        }
        return c;
    }

    public int getReturnPipeletCount() {
        if (pipelets == null) {
            return 0;
        }
        int c = 0;
        for (PipeletPojo pipelet : pipelets) {
            if (!pipelet.isForward()) {
                c++;
            }
        }
        return c;
    }
    
    /**
     * @return the availableTemplates
     */
    public List<ComponentPojo> getAvailableTemplates() {

        return availableTemplates;
    }

    /**
     * @param availableTemplates the availableTemplates to set
     */
    public void setAvailableTemplates( List<ComponentPojo> availableTemplates ) {

        this.availableTemplates = availableTemplates;
    }

    /**
     * @return the actionNxId
     */
    public int getActionNxId() {

        return actionNxId;
    }

    /**
     * @param actionNxId the actionNxId to set
     */
    public void setActionNxId( int actionNxId ) {

        this.actionNxId = actionNxId;
    }

    public int getActionNxIdReturn() {

        return actionNxIdReturn;
    }

    public void setActionNxIdReturn( int actionNxIdReturn ) {

        this.actionNxIdReturn = actionNxIdReturn;
    }

    /**
     * @return the sortingDirection
     */
    public int getSortingDirection() {

        return sortingDirection;
    }

    /**
     * @param sortingDirection the sortingDirection to set
     */
    public void setSortingDirection( int sortingDirection ) {

        this.sortingDirection = sortingDirection;
    }

    /**
     * @return the submitaction
     */
    public String getSubmitaction() {

        return submitaction;
    }

    /**
     * @param submitaction the submitaction to set
     */
    public void setSubmitaction( String submitaction ) {

        this.submitaction = submitaction;
    }

    /**
     * @return the sortaction
     */
    public int getSortaction() {

        return sortaction;
    }

    /**
     * @param sortaction the sortaction to set
     */
    public void setSortaction( int sortaction ) {

        this.sortaction = sortaction;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    /**
     * @return the currentPipelet
     */
    public PipeletPojo getCurrentPipelet() {

        return currentPipelet;
    }

    /**
     * @param currentPipelet the currentPipelet to set
     */
    public void setCurrentPipelet( PipeletPojo currentPipelet ) {

        this.currentPipelet = currentPipelet;
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

        pipeletParamValues.put( key, (String) value );
    }

    /**
     * @return the paramName
     */
    public String getParamName() {

        return paramName;
    }

    /**
     * @param paramName the paramName to set
     */
    public void setParamName( String paramName ) {

        this.paramName = paramName;
    }

    /**
     * @return the parameters
     */
    public List<PipeletParamPojo> getParameters() {

        return parameters;
    }

    /**
     * @param parameters the parameters to set
     */
    public void setParameters( List<PipeletParamPojo> parameters ) {

        this.parameters = parameters;
    }

    public List<PipeletParamPojo> getObsoleteParameters() {
        return obsoleteParameters;
    }
    
    public void setObsoleteParameters( List<PipeletParamPojo> obsoleteParameters ) {
        this.obsoleteParameters = obsoleteParameters;
    }

    
    public boolean isFrontend() {

        return frontend;
    }

    public void setFrontend( boolean frontend ) {

        this.frontend = frontend;
    }

    public int getNxTrpId() {

        return nxTrpId;
    }

    public void setNxTrpId( int nxTrpId ) {

        this.nxTrpId = nxTrpId;
    }

    public List<TRPPojo> getTrps() {

        return trps;
    }

    public void setTrps( List<TRPPojo> trps ) {

        this.trps = trps;
    }

    public Configurable getConfigurable() {

        return configurable;
    }

    public void setConfigurable( Configurable configurable ) {

        this.configurable = configurable;
    }

    public boolean isBidirectional() {

        return bidirectional || getReturnPipelets().size() > 0;
    }

    public void setBidirectional( boolean bidirectional ) {

        this.bidirectional = bidirectional;
    }

}
