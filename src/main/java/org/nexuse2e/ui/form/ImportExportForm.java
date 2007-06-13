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
package org.nexuse2e.ui.form;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

/**
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 *
 * @author markus.breilmann
 */
public class ImportExportForm extends ActionForm {

    /**
     * 
     */
    private static final long serialVersionUID         = -7760013405323559900L;

    private List              choreographyList         = null;
    private String[]          choreographies           = null;
    private String            choreographyImportPath   = null;
    private FormFile          choreographyImportFile   = null;
    private List              partnerList              = null;
    private String[]          partners                 = null;
    private String            partnerImportPath        = null;
    private FormFile          partnerImportFile        = null;
    private boolean           includeParticipants      = false;
    private boolean           includeComponents        = false;
    private boolean           exportSettingsOnShutdown = false;

    /* (non-Javadoc)
     * @see org.apache.struts.action.ActionForm#reset(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest)
     */
    @Override
    public void reset( ActionMapping arg0, HttpServletRequest arg1 ) {

        includeParticipants = false;
        includeComponents = false;
        exportSettingsOnShutdown = false;
    } // reset

    /**
     * @return Returns the choreographies.
     */
    public String[] getChoreographies() {

        return choreographies;
    }

    /**
     * @param choreographies The choreographies to set.
     */
    public void setChoreographies( String[] choreographies ) {

        this.choreographies = choreographies;
    }

    /**
     * @return Returns the choreographyList.
     */
    public List getChoreographyList() {

        return choreographyList;
    }

    /**
     * @param choreographyList The choreographyList to set.
     */
    public void setChoreographyList( List choreographyList ) {

        this.choreographyList = choreographyList;
    }

    /**
     * @return Returns the exportSettingsOnShutdown.
     */
    public boolean isExportSettingsOnShutdown() {

        return exportSettingsOnShutdown;
    }

    /**
     * @param exportSettingsOnShutdown The exportSettingsOnShutdown to set.
     */
    public void setExportSettingsOnShutdown( boolean exportSettingsOnShutdown ) {

        this.exportSettingsOnShutdown = exportSettingsOnShutdown;
    }

    /**
     * @return Returns the includeComponents.
     */
    public boolean isIncludeComponents() {

        return includeComponents;
    }

    /**
     * @param includeComponents The includeComponents to set.
     */
    public void setIncludeComponents( boolean includeComponents ) {

        this.includeComponents = includeComponents;
    }

    /**
     * @return Returns the includeParticipants.
     */
    public boolean isIncludeParticipants() {

        return includeParticipants;
    }

    /**
     * @param includeParticipants The includeParticipants to set.
     */
    public void setIncludeParticipants( boolean includeParticipants ) {

        this.includeParticipants = includeParticipants;
    }

    /**
     * @return Returns the partnerList.
     */
    public List getPartnerList() {

        return partnerList;
    }

    /**
     * @param partnerList The partnerList to set.
     */
    public void setPartnerList( List partnerList ) {

        this.partnerList = partnerList;
    }

    /**
     * @return Returns the partners.
     */
    public String[] getPartners() {

        return partners;
    }

    /**
     * @param partners The partners to set.
     */
    public void setPartners( String[] partners ) {

        this.partners = partners;
    }

    /**
     * @return Returns the choreographyImportPath.
     */
    public String getChoreographyImportPath() {

        return choreographyImportPath;
    }

    /**
     * @param choreographyImportPath The choreographyImportPath to set.
     */
    public void setChoreographyImportPath( String choreographyImportPath ) {

        this.choreographyImportPath = choreographyImportPath;
    }

    /**
     * @return Returns the partnerImportPath.
     */
    public String getPartnerImportPath() {

        return partnerImportPath;
    }

    /**
     * @param partnerImportPath The partnerImportPath to set.
     */
    public void setPartnerImportPath( String partnerImportPath ) {

        this.partnerImportPath = partnerImportPath;
    }

    /**
     * @return Returns the choreographyImportFile.
     */
    public FormFile getChoreographyImportFile() {

        return choreographyImportFile;
    }

    /**
     * @param choreographyImportFile The choreographyImportFile to set.
     */
    public void setChoreographyImportFile( FormFile choreographyImportFile ) {

        this.choreographyImportFile = choreographyImportFile;
    }

    /**
     * @return Returns the partnerImportFile.
     */
    public FormFile getPartnerImportFile() {

        return partnerImportFile;
    }

    /**
     * @param partnerImportFile The partnerImportFile to set.
     */
    public void setPartnerImportFile( FormFile partnerImportFile ) {

        this.partnerImportFile = partnerImportFile;
    }
} // ImportExportForm
