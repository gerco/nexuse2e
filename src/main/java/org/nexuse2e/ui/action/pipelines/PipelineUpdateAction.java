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

import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.nexuse2e.Configurable;
import org.nexuse2e.Engine;
import org.nexuse2e.configuration.ConfigurationUtil;
import org.nexuse2e.configuration.Constants;
import org.nexuse2e.messaging.Pipelet;
import org.nexuse2e.pojo.ComponentPojo;
import org.nexuse2e.pojo.PipeletParamPojo;
import org.nexuse2e.pojo.PipeletPojo;
import org.nexuse2e.pojo.PipelinePojo;
import org.nexuse2e.transport.TransportReceiver;
import org.nexuse2e.ui.action.NexusE2EAction;
import org.nexuse2e.ui.form.PipelineForm;

/**
 * @author gesch
 *
 */
public class PipelineUpdateAction extends NexusE2EAction {

    private static String URL     = "partner.error.url";
    private static String TIMEOUT = "partner.error.timeout";

    /* (non-Javadoc)
     * @see com.tamgroup.nexus.e2e.ui.action.NexusE2EAction#executeNexusE2EAction(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.apache.struts.action.ActionMessages)
     */
    @SuppressWarnings("unchecked")
    public ActionForward executeNexusE2EAction( ActionMapping actionMapping, ActionForm actionForm,
            HttpServletRequest request, HttpServletResponse response, ActionMessages errors, ActionMessages messages )
            throws Exception {

        ActionForward success = actionMapping.findForward( ACTION_FORWARD_SUCCESS );
        ActionForward config = actionMapping.findForward( "config" );
        ActionForward error = actionMapping.findForward( ACTION_FORWARD_FAILURE );

        PipelineForm form = (PipelineForm) actionForm;
        if ( form.isFrontend() ) {
            error = actionMapping.findForward( "frontendError" );
        } else {
            error = actionMapping.findForward( "backendError" );
        }

        String action = form.getSubmitaction();
        LOG.trace( "action: " + action );
        int actionNxId = form.getActionNxId();
        LOG.trace( "actionNxId: " + actionNxId );

        PipelinePojo pipeline = Engine.getInstance().getActiveConfigurationAccessService()
                .getPipelinePojoByNxPipelineId( form.getNxPipelineId() );

        if ( pipeline == null ) {
            ActionMessage errorMessage = new ActionMessage( "generic.error", "No pipeline found for id: "
                    + form.getNxPipelineId() );
            errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
            addRedirect( request, URL, TIMEOUT );
            return error;
        }

        if ( action.equals( "add" ) ) {
            ComponentPojo component = Engine.getInstance().getActiveConfigurationAccessService()
                    .getComponentByNxComponentId( actionNxId );
            if ( component != null ) {
                PipeletPojo pipelet = new PipeletPojo();
                pipelet.setComponent( component );
                pipelet.setCreatedDate( new Date() );
                pipelet.setModifiedDate( new Date() );
                pipelet.setName( component.getName() );
                pipelet.setDescription( component.getDescription() );
                pipelet.setPipeline( pipeline );
                pipelet.setPosition( form.getPipelets().size() + 1 );

                try {
                    Object newComponent = Class.forName( component.getClassName() ).newInstance();
                    LOG.trace( "object:" + newComponent.getClass().getName() );
                    if ( ( newComponent instanceof Pipelet ) || ( newComponent instanceof TransportReceiver ) ) {
                        pipelet.getPipeletParams().addAll(
                                ConfigurationUtil.getConfiguration( (Configurable) newComponent, pipelet ) );
                    } else {
                        ActionMessage errorMessage = new ActionMessage( "generic.error",
                                "Referenced Component is no pipelet: " + component.getClassName() );
                        errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
                        addRedirect( request, URL, TIMEOUT );
                        return error;
                    }
                } catch ( Exception e ) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                form.getPipelets().add( pipelet );
                //pipeline.getPipelets().add( pipelet );
                LOG.trace( "size: " + form.getPipelets().size() );
            }
            request.setAttribute( "keepData", "true" );
        }
        if ( action.equals( "delete" ) ) {
            int deletePosition = form.getSortaction();
            List<PipeletPojo> pipelets = form.getPipelets();
            if ( pipelets != null && deletePosition > 0 && deletePosition <= pipelets.size() ) {
                pipeline.getPipelets().remove( pipelets.get( deletePosition - 1 ) );

                pipelets.remove( deletePosition - 1 );
                Iterator<PipeletPojo> pipeletI = pipelets.iterator();
                int counter = 1;
                while ( pipeletI.hasNext() ) {
                    PipeletPojo pipelet = pipeletI.next();
                    pipelet.setPosition( counter );
                    counter++;
                }
            }
            request.setAttribute( "keepData", "true" );
        }

        if ( action.equals( "sort" ) ) {
            int direction = form.getSortingDirection();
            int sortaction = form.getSortaction();
            List<PipeletPojo> pipelets = form.getPipelets();

            LOG.trace( "direction: " + direction );
            LOG.trace( "sortaction: " + form.getSortaction() );

            if ( pipelets != null && pipelets.size() > 0 ) {
                if ( ( sortaction == 1 && direction == 1 ) || ( sortaction == pipelets.size() && direction == 2 ) ) {
                    LOG.trace( "nothing to sort..." );
                } else {
                    // up
                    if ( direction == 1 ) {
                        pipelets.get( sortaction - 2 ).setPosition( sortaction );
                        pipelets.get( sortaction - 1 ).setPosition( sortaction - 1 );
                    }
                    // down
                    else if ( direction == 2 ) {
                        pipelets.get( sortaction ).setPosition( sortaction );
                        pipelets.get( sortaction - 1 ).setPosition( sortaction + 1 );
                    }
                    Collections.sort( pipelets, Constants.PIPELETCOMPARATOR );

                }

            }
            request.setAttribute( "keepData", "true" );

        }
        if ( action.equals( "config" ) ) {
            LOG.trace( "config..." + config.getName() );

            return config;
        }

        if ( action.equals( "update" ) ) {
            form.getProperties( pipeline );
            // debug
            
//                PipeletPojo p = pipeline.getPipelets().iterator().next();
//                
//                for ( PipeletParamPojo param : p.getPipeletParams() ) {
//                    System.out.println("param:"+param.getLabel()+" - "+param.getValue());
//                }
                
            
            //
            Engine.getInstance().getActiveConfigurationAccessService().updatePipeline( pipeline );
        }

        form.setSubmitaction( "" );

        return success;
    }

}
