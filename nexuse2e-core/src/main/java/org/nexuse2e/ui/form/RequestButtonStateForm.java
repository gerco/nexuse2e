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

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * @author guido.esch
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class RequestButtonStateForm extends ActionForm {

    /**
     * 
     */
    private static final long serialVersionUID = -4233120333469992016L;
    boolean                   createRequest    = false;
    boolean                   importCert       = false;
    boolean                   importBackup     = false;
    boolean                   exportPKCS12     = false;
    boolean                   exportRequest    = false;
    boolean                   deleteRequest    = false;
    boolean                   showRequest      = false;

    @Override
    public void reset( ActionMapping arg0, HttpServletRequest arg1 ) {

        setCreateRequest( false );
        setImportCert( false );
        setExportRequest( false );
        setExportPKCS12( false );
        setDeleteRequest( false );
        setShowRequest( false );
    }

    public boolean isCreateRequest() {

        return createRequest;
    }

    public void setCreateRequest( boolean createRequest ) {

        this.createRequest = createRequest;
    }

    public boolean isDeleteRequest() {

        return deleteRequest;
    }

    public void setDeleteRequest( boolean deleteRequest ) {

        this.deleteRequest = deleteRequest;
    }

    public boolean isExportRequest() {

        return exportRequest;
    }

    public void setExportRequest( boolean exportCert ) {

        this.exportRequest = exportCert;
    }

    public boolean isExportPKCS12() {

        return exportPKCS12;
    }

    public void setExportPKCS12( boolean exportPKCS12 ) {

        this.exportPKCS12 = exportPKCS12;
    }

    public boolean isImportCert() {

        return importCert;
    }

    public void setImportCert( boolean importCert ) {

        this.importCert = importCert;
    }

    public boolean isShowRequest() {

        return showRequest;
    }

    public void setShowRequest( boolean showRequest ) {

        this.showRequest = showRequest;
    }

    public boolean isImportBackup() {

        return importBackup;
    }

    public void setImportBackup( boolean importBackup ) {

        this.importBackup = importBackup;
    }
}
