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
package org.nexuse2e.ui.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.nexuse2e.Engine;
import org.nexuse2e.configuration.Constants;
import org.nexuse2e.pojo.PartnerPojo;
import org.nexuse2e.ui.form.CollaborationPartnerForm;

/**
 * @author guido.esch
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CollaborationPartnersAction extends NexusE2EAction {

    /* (non-Javadoc)
     * @see com.tamgroup.nexus.e2e.ui.action.NexusE2EAction#executeNexusE2EAction(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.apache.struts.action.ActionMessages)
     */
    @Override
    public ActionForward executeNexusE2EAction( ActionMapping actionMapping, ActionForm actionForm,
            HttpServletRequest request, HttpServletResponse response, ActionMessages errors, ActionMessages messages )
            throws Exception {

        ActionForward success = actionMapping.findForward( ACTION_FORWARD_SUCCESS );

        String type = request.getParameter( "type" );
        if ( type == null || type.trim().equals( "" ) ) {
            type = (String) request.getAttribute( "type" );
        }

        LOG.debug( "type: " + type );

        List<CollaborationPartnerForm> partners = new ArrayList<CollaborationPartnerForm>();
        List<PartnerPojo> partnerPojos = null;
        if ( ( type != null ) && type.equals( "1" ) ) {
            partnerPojos = Engine.getInstance().getActiveConfigurationAccessService().getPartners( Constants.PARTNER_TYPE_LOCAL,
                    Constants.PARTNERCOMPARATOR );

        } else {
            partnerPojos = Engine.getInstance().getActiveConfigurationAccessService().getPartners( Constants.PARTNER_TYPE_PARTNER,
                    Constants.PARTNERCOMPARATOR );
        }

        Iterator<PartnerPojo> partnerI = partnerPojos.iterator();
        while ( partnerI.hasNext() ) {
            PartnerPojo partner = partnerI.next();
            CollaborationPartnerForm cpf = new CollaborationPartnerForm();
            cpf.setProperties( partner );
            partners.add( cpf );
        }
        request.setAttribute( "TYPE", type );
        if ( ( type != null ) && type.equals( "1" ) ) {
            request.setAttribute( "HEADLINE", "Server Identities" );
            request.setAttribute( "BUTTONTEXT", "Add Server Identity" );

        } else {
            request.setAttribute( "HEADLINE", "Collaboration Partners" );
            request.setAttribute( "BUTTONTEXT", "Add Collaboration Partner" );
        }

        request.setAttribute( ATTRIBUTE_COLLECTION, partners );

        return success;
    }

}
