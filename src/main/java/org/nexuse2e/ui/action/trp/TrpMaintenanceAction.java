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
package org.nexuse2e.ui.action.trp;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.nexuse2e.Engine;
import org.nexuse2e.pojo.TRPPojo;
import org.nexuse2e.ui.action.NexusE2EAction;
import org.nexuse2e.ui.form.TrpMaintenanceForm;

/**
 * Provides a list of existing TRPs along with the model for adding/removing/updating
 * TRPs.
 *
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public class TrpMaintenanceAction extends NexusE2EAction {

    @Override
    public ActionForward executeNexusE2EAction( ActionMapping actionMapping, ActionForm actionForm,
            HttpServletRequest request, HttpServletResponse response, ActionMessages errors, ActionMessages messages )
            throws Exception {

        ActionForward success = actionMapping.findForward( ACTION_FORWARD_SUCCESS );
        TrpMaintenanceForm form = (TrpMaintenanceForm) actionForm;

        String action = form.getSubmitaction();
        form.setSubmitaction( "" );

        if ( !StringUtils.isEmpty( action ) && action.equals( "add" ) ) {
            if ( !StringUtils.isEmpty( form.getProtocol() )
                    && !StringUtils.isEmpty( form.getTransport() )
                    && !StringUtils.isEmpty( form.getVersion() ) ) {
                TRPPojo trp = new TRPPojo();

                trp.setProtocol( form.getProtocol() );
                trp.setTransport( form.getTransport() );
                trp.setVersion( form.getVersion() );

                Engine.getInstance().getActiveConfigurationAccessService().updateTrp( trp );
            }
        } else if ( !StringUtils.isEmpty( action ) && action.equals( "update" ) ) {
            int nxId = form.getNxTRPId();
            if ( nxId != 0 ) {
                TRPPojo trp = Engine.getInstance().getActiveConfigurationAccessService()
                        .getTrpByNxTrpId( nxId );
                
                if ( trp != null ) {
                    trp.setProtocol( form.getProtocol() );
                    trp.setTransport( form.getTransport() );
                    trp.setVersion( form.getVersion() );

                    Engine.getInstance().getActiveConfigurationAccessService().updateTrp( trp );
                }
            }
        } else if ( !StringUtils.isEmpty( action ) && action.equals( "delete" ) ) {
            int nxId = form.getNxTRPId();
            if ( nxId != 0 ) {
                TRPPojo trp = Engine.getInstance().getActiveConfigurationAccessService().getTrpByNxTrpId( nxId );
                if ( trp != null ) { 
                    Engine.getInstance().getActiveConfigurationAccessService().deleteTrp( trp );
                }
            }
        }
        List<TRPPojo> trps = Engine.getInstance().getActiveConfigurationAccessService().getTrps();

        

        request.setAttribute( ATTRIBUTE_COLLECTION, trps );

        return success;
    }

}
