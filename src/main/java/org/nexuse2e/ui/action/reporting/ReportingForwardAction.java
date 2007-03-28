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
package org.nexuse2e.ui.action.reporting;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.nexuse2e.Engine;
import org.nexuse2e.configuration.Constants;
import org.nexuse2e.pojo.ChoreographyPojo;
import org.nexuse2e.pojo.PartnerPojo;
import org.nexuse2e.ui.action.NexusE2EAction;
import org.nexuse2e.ui.form.ReportingPropertiesForm;

/**
 * @author guido.esch
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ReportingForwardAction extends NexusE2EAction {

    private static final String VERSIONSTRING = "$Id: ReportingForwardAction.java 1024 2006-02-14 11:27:45Z markus.breilmann $";

    private static String       URL           = "reporting.error.url";
    private static String       TIMEOUT       = "reporting.error.timeout";

    /* (non-Javadoc)
     * @see com.tamgroup.nexus.e2e.ui.action.NexusE2EAction#executeNexusE2EAction(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.apache.struts.action.ActionMessages)
     */
    @Override
    public ActionForward executeNexusE2EAction( ActionMapping actionMapping, ActionForm actionForm,
            HttpServletRequest request, HttpServletResponse response, ActionMessages errors, ActionMessages messages )
            throws Exception {

        ActionForward engine = actionMapping.findForward( "engine" );
        ActionForward conversation = actionMapping.findForward( "conversation" );
        ActionForward save = actionMapping.findForward( "save" );
        ActionForward error = actionMapping.findForward( ACTION_FORWARD_FAILURE );

        ReportingPropertiesForm form = (ReportingPropertiesForm) actionForm;
        String type = request.getParameter( "type" );
        if ( type != null ) {
            LOG.trace( "----- form: " + form );
            form.setCommand( type );
        }

        form.setStartCount( 0 );
        form.setEndCount( 0 );

        if ( form.getCommand() != null && form.getCommand().equals( "transaction" ) ) {
            Vector participants = new Vector();

            List partners = Engine.getInstance().getActiveConfigurationAccessService().getPartners( Constants.PARTNER_TYPE_PARTNER,
                    Constants.PARTNERCOMPARATOR );
            Iterator<PartnerPojo> partnerI = partners.iterator();
            LOG.trace( "PartnerCount: " + partners.size() );
            while ( partnerI.hasNext() ) {
                PartnerPojo partner = partnerI.next();
                participants.addElement( partner.getPartnerId() );

            }
            form.setParticipantIds( participants );

            Vector choreographyIds = new Vector();

            List choreographies = Engine.getInstance().getActiveConfigurationAccessService().getChoreographies();

            Iterator<ChoreographyPojo> choreographyI = choreographies.iterator();
            while ( choreographyI.hasNext() ) {
                ChoreographyPojo choreography = choreographyI.next();
                choreographyIds.addElement( choreography.getName() );
            }
            form.setChoreographyIds( choreographyIds );

            return conversation;
        } else if ( form.getCommand() != null && form.getCommand().equals( "engine" ) ) {
            //            ChoreographyDAO cDao = new ChoreographyDAO();
            //            List choreographies = cDao.getChoreographies();
            //            Vector chorIds = new Vector();
            //            chorIds.addElement( new String( CertificateDAO.ENGINE_ID ) );
            //            Iterator i = choreographies.iterator();
            //            while ( i.hasNext() ) {
            //                ChoreographyPojo cPojo = (ChoreographyPojo) i.next();
            //                chorIds.addElement( cPojo.getChoreographyId() );
            //            }
            //            form.setOriginIds( chorIds );
            //
            return engine;

        } else if ( form.getCommand() != null && form.getCommand().equals( "saveFields" ) ) {
            return save;
        } else {
            LOG.error( "Invalid actionparameter=>" + form.getCommand() + "<" );
            ActionMessage errorMessage = new ActionMessage( "generic.error", "Invalid actionparameter=>"
                    + form.getCommand() + "<" );
            errors.add( ActionMessages.GLOBAL_MESSAGE, errorMessage );
            addRedirect( request, URL, TIMEOUT );
            return error;
        }

    }

}
