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
package org.nexuse2e.ui.action.pipelines;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.nexuse2e.Engine;
import org.nexuse2e.configuration.EnumerationParameter;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.pojo.PipeletParamPojo;
import org.nexuse2e.pojo.PipeletPojo;
import org.nexuse2e.pojo.PipelinePojo;
import org.nexuse2e.ui.action.NexusE2EAction;
import org.nexuse2e.ui.form.PipelineForm;

/**
 * @author guido.esch
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PipeletParamsUpdateAction extends NexusE2EAction {

    /* (non-Javadoc)
     * @see com.tamgroup.nexus.e2e.ui.action.NexusE2EAction#executeNexusE2EAction(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.apache.struts.action.ActionMessages)
     */
    @Override
    public ActionForward executeNexusE2EAction( ActionMapping actionMapping, ActionForm actionForm,
            HttpServletRequest request, HttpServletResponse response, ActionMessages errors, ActionMessages messages )
            throws Exception {

        ActionForward update = actionMapping.findForward( "update" );
        ActionForward view = actionMapping.findForward( "view" );
        ActionForward error = actionMapping.findForward( ACTION_FORWARD_FAILURE );

        PipelineForm form = (PipelineForm) actionForm;

//        request.setAttribute( "keepData", "true" );
        //request.getSession().setAttribute( Crumbs.CURRENT_LOCATION, Crumbs.PIPELINE_VIEW + "_" + form.getNxPipelineId() );

        String action = form.getSubmitaction();

        LOG.trace( "submitaction: " + action );

        if ( action.equals( "add" ) ) {
            form.fillPojosFromParameterMap();
            String paramName = form.getParamName();
            LOG.trace( "paramName: " + paramName );
            int maxsqn = 0;
            PipeletParamPojo headerLine = null;
            for (PipeletParamPojo param : form.getParameters()) {
                if ( param.getParamName().equals( paramName ) ) {
                    if ( param.getSequenceNumber() == 0 ) {
                        headerLine = param;
                    }
                    maxsqn = param.getSequenceNumber();
                }
            }

            if ( headerLine != null ) {
                PipeletParamPojo newParam = new PipeletParamPojo();
                newParam.setParamName( headerLine.getParamName() );
                newParam.setModifiedDate( new Date() );
                newParam.setCreatedDate( new Date() );
                newParam.setPipelet( headerLine.getPipelet() );
                newParam.setLabel( form.getKey() );
                newParam.setValue( form.getValue() );
                ParameterDescriptor pd = headerLine.getParameterDescriptor();
                EnumerationParameter enumeration = form.getConfigurable().getParameter( headerLine.getParamName() );
                if (enumeration == null) {
                    enumeration = pd.getDefaultValue();
                    form.getConfigurable().setParameter( headerLine.getParamName(), enumeration );
                }
                enumeration.putElement( form.getKey(), form.getValue() );
                newParam.setSequenceNumber( maxsqn + 1 );
                newParam.setParameterDescriptor( pd );
                form.getParameters().add( newParam );
            }

            return view;
        }
        if ( action.equals( "delete" ) ) {
            form.fillPojosFromParameterMap();
            String paramName = form.getParamName();
            LOG.trace( "paramName: " + paramName );
            int sqn = form.getActionNxId();
            LOG.trace( "sqn: " + sqn );

            PipeletParamPojo obsoleteParam = null;
            for (PipeletParamPojo param : form.getCurrentPipelet().getPipeletParams()) {
                if ( param.getParamName().equals( paramName ) && param.getSequenceNumber() == sqn ) {
                    obsoleteParam = param;
                }
                if ( param.getParamName().equals( paramName ) && param.getSequenceNumber() > sqn ) {
                    param.setSequenceNumber( param.getSequenceNumber() - 1 );
                }
            }
            if ( obsoleteParam != null ) {
                form.getParameters().remove( obsoleteParam );
                EnumerationParameter enumeration = form.getConfigurable().getParameter( obsoleteParam.getParamName() );
                if (enumeration != null) {
                    enumeration.removeElement( obsoleteParam.getLabel() );
                }
            }
            return view;
        }

        if ( action.equals( "update" ) ) {
            PipelinePojo pipelinePojo = Engine.getInstance().getActiveConfigurationAccessService()
                    .getPipelinePojoByNxPipelineId( form.getNxPipelineId() );
            PipeletPojo pipeletPojo = null;
            if ( form.getCurrentPipelet().getNxPipeletId() == 0 ) {
                pipeletPojo = form.getCurrentPipelet();
            } else {
                if ( pipelinePojo != null ) {
                    if ( pipelinePojo.getPipelets() != null ) {
                        for ( PipeletPojo pp : pipelinePojo.getPipelets() ) {
                            if ( pp.getNxPipeletId() == form.getCurrentPipelet().getNxPipeletId() ) {
                                pipeletPojo = pp;
                                break;
                            }
                        }
                    }
                }
            }
            if ( pipeletPojo != null ) {
                if ( form.getConfigurable() == null ) {
                    return error;
                }
                form.fillPojosFromParameterMap();
                List<PipeletParamPojo> paramPojos = form.getParameters();
                List<PipeletParamPojo> copiedParamPojos = new ArrayList<PipeletParamPojo>();
                for ( PipeletParamPojo param : paramPojos ) {
                    if (param.getLabel() != null && param.getValue() != null) {
                        param.setPipelet( pipeletPojo );
                        copiedParamPojos.add( param );
                    }
                }
                pipeletPojo.setPipeletParams( copiedParamPojos );

                if ( pipeletPojo.getNxPipeletId() != 0 ) {
                    Engine.getInstance().getActiveConfigurationAccessService().updatePipeline( pipelinePojo );

                    // update form
                    form.setProperties( Engine.getInstance().getActiveConfigurationAccessService()
                            .getPipelinePojoByNxPipelineId( form.getNxPipelineId() ) );
                }
            }
        }

        return update;
    }

}
