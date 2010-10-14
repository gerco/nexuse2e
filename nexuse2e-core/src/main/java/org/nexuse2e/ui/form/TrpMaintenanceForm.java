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

import org.apache.struts.action.ActionForm;

/**
 * Web form for TRP maintenance.
 *
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public class TrpMaintenanceForm extends ActionForm {

    private static final long serialVersionUID = -4659594397866558364L;

    private int               nxTRPId;
    private String            submitaction;
    private String            protocol;
    private String            transport;
    private String            version;
    private String            adapterClassName;

    public String getProtocol() {

        return protocol;
    }

    public void setProtocol( String protocol ) {

        this.protocol = protocol;
    }

    public String getTransport() {

        return transport;
    }

    public void setTransport( String transport ) {

        this.transport = transport;
    }

    public String getVersion() {

        return version;
    }

    public void setVersion( String version ) {

        this.version = version;
    }

    public String getSubmitaction() {

        return submitaction;
    }

    public void setSubmitaction( String submitaction ) {

        this.submitaction = submitaction;
    }

    public int getNxTRPId() {

        return nxTRPId;
    }

    public void setNxTRPId( int nxTRPId ) {

        this.nxTRPId = nxTRPId;
    }

    public String getAdapterClassName() {

        return adapterClassName;
    }

    public void setAdapterClassName( String adapterClassName ) {

        this.adapterClassName = adapterClassName;
    }

}
