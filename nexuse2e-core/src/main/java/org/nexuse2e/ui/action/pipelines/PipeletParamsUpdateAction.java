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
package org.nexuse2e.ui.action.pipelines;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.configuration.EnumerationParameter;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.pojo.PipeletParamPojo;
import org.nexuse2e.pojo.PipeletPojo;
import org.nexuse2e.pojo.PipelinePojo;
import org.nexuse2e.ui.action.NexusE2EAction;
import org.nexuse2e.ui.form.PipelineForm;

/**
 * @author guido.esch
 */
public class PipeletParamsUpdateAction extends NexusE2EAction {

    /* (non-Javadoc)
     * @see com.tamgroup.nexus.e2e.ui.action.NexusE2EAction#executeNexusE2EAction(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.apache.struts.action.ActionMessages)
     */
    @Override
    public ActionForward executeNexusE2EAction( ActionMapping actionMapping, ActionForm actionForm,
            HttpServletRequest request, HttpServletResponse response, EngineConfiguration engineConfiguration, ActionMessages errors, ActionMessages messages )
            throws Exception {

        ActionForward update = actionMapping.findForward( "update" );
        ActionForward view = actionMapping.findForward( "view" );
        ActionForward error = actionMapping.findForward( ACTION_FORWARD_FAILURE );

        PipelineForm form = (PipelineForm) actionForm;

        
        String action = form.getSubmitaction();

        LOG.trace( "submitaction: " + action );

        if ( action.equals( "add" ) ) {
            String paramName = form.getParamName();
            LOG.trace( "paramName: " + paramName );
            int maxsqn = 0;
            PipeletParamPojo headerLine = null;
            for (PipeletParamPojo param : form.getParameters()) {
                if ( param.getParamName().equals( paramName ) ) {
                    if ( param.getLabel() == null ) {
                        headerLine = param;
                    } else {
                        maxsqn = param.getSequenceNumber();
                    }
                }
            }

            boolean alreadyIn;
            if (form.getKey() != null) {
                alreadyIn = false;
                for (PipeletParamPojo param : form.getParameters()) {
                    if (form.getKey().equals( param.getLabel() )) {
                        alreadyIn = true;
                        break;
                    }
                }
            } else {
                alreadyIn = true;
            }
            if ( headerLine != null && !alreadyIn ) {
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
                form.getParameters().add( form.getParameters().size() - 1, newParam );
            }
            form.setKey( "" );
            form.setValue( "" );

            return view;
        }
        if ( action.equals( "delete" ) ) {
            form.fillPojosFromParameterMap();
            String paramName = form.getParamName();
            LOG.trace( "paramName: " + paramName );
            int sqn = form.getActionNxId();
            LOG.trace( "sqn: " + sqn );

            PipeletParamPojo obsoleteParam = null;
            for (PipeletParamPojo param : form.getParameters()) {
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
                if (obsoleteParam.getNxPipeletParamId() != 0) {
                    form.getObsoleteParameters().add( obsoleteParam );
                }
            }
            return view;
        }

        if ( action.equals( "update" ) ) {
            PipelinePojo pipelinePojo =
                engineConfiguration.getPipelinePojoByNxPipelineId(
                            form.getNxPipelineId() );
            pipelinePojo.setNxPipelineId( form.getNxPipelineId() );
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
                for ( PipeletParamPojo param : form.getParameters() ) {
                    if (param.getLabel() != null && param.getValue() != null && param.getNxPipeletParamId() == 0) {
                        param.setPipelet( pipeletPojo );
                        pipeletPojo.getPipeletParams().add( param );
                    }
                }
                for ( PipeletParamPojo param : form.getObsoleteParameters() ) {
                    pipeletPojo.getPipeletParams().remove( param );
                    param.setPipelet( null );
                }

                if ( pipeletPojo.getNxPipeletId() != 0 ) {
                    engineConfiguration.updatePipeline( pipelinePojo );

                    // update form
                    form.setProperties( engineConfiguration
                            .getPipelinePojoByNxPipelineId( form.getNxPipelineId() ) );
                }
            }
        }

        return update;
    }

}
