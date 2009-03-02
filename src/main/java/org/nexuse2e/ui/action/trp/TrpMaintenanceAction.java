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
package org.nexuse2e.ui.action.trp;

import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.nexuse2e.configuration.EngineConfiguration;
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
            HttpServletRequest request, HttpServletResponse response, EngineConfiguration engineConfiguration, ActionMessages errors, ActionMessages messages )
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
                trp.setAdapterClassName( form.getAdapterClassName() );

                engineConfiguration.updateTrp( trp );
            }
        } else if ( !StringUtils.isEmpty( action ) && action.equals( "update" ) ) {
            int nxId = form.getNxTRPId();
            if ( nxId != 0 ) {
                TRPPojo trp = engineConfiguration
                        .getTrpByNxTrpId( nxId );
                
                if ( trp != null ) {
                    trp.setProtocol( form.getProtocol() );
                    trp.setTransport( form.getTransport() );
                    trp.setVersion( form.getVersion() );
                    trp.setAdapterClassName( form.getAdapterClassName() );

                    engineConfiguration.updateTrp( trp );
                }
            }
        } else if ( !StringUtils.isEmpty( action ) && action.equals( "delete" ) ) {
            int nxId = form.getNxTRPId();
            if ( nxId != 0 ) {
                TRPPojo trp = engineConfiguration.getTrpByNxTrpId( nxId );
                if ( trp != null ) { 
                    engineConfiguration.deleteTrp( trp );
                }
            }
        }
        Comparator<TRPPojo> comparator = new Comparator<TRPPojo>() {
            public int compare( TRPPojo trp1, TRPPojo trp2 ) {
                if (trp1 == null) {
                    if (trp2 != null) {
                        return -1;
                    }
                    return 0;
                }
                if (trp2 == null) {
                    if (trp1 == null) {
                        return 1;
                    }
                    return 0;
                }
                String t1 = trp1.getTransport();
                String t2 = trp2.getTransport();
                if (t1 != null && t2 != null) {
                    int result = t1.compareTo( t2 );
                    if (result == 0) {
                        String p1 = trp1.getProtocol();
                        String p2 = trp2.getProtocol();
                        if (p1 != null && p2 != null) {
                            result = p1.compareTo( p2 );
                            if (result == 0) {
                                String v1 = trp1.getVersion();
                                String v2 = trp2.getVersion();
                                if (v1 != null && v2 != null) {
                                    result = v1.compareTo( v2 );
                                }
                            }
                        }
                    }
                    return result;
                }
                return 0;
            }
        };

        SortedSet<TRPPojo> set = new TreeSet<TRPPojo>( comparator );
        set.addAll( engineConfiguration.getTrps() );

        request.setAttribute( ATTRIBUTE_COLLECTION, set );

        return success;
    }

}
