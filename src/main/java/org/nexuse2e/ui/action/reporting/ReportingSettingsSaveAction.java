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

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.nexuse2e.Engine;
import org.nexuse2e.ui.action.NexusE2EAction;
import org.nexuse2e.ui.form.ReportingSettingsForm;

/**
 * Saves the reporting settings to the persistence layer.
 *
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public class ReportingSettingsSaveAction extends NexusE2EAction {

    @Override
    public ActionForward executeNexusE2EAction( ActionMapping actionMapping, ActionForm actionForm,
            HttpServletRequest request, HttpServletResponse response, ActionMessages errors, ActionMessages messages )
            throws Exception {

        ReportingSettingsForm form = (ReportingSettingsForm) actionForm;

        Map<String, Object> values = new HashMap<String, Object>();

        values.put( ReportingSettingsForm.PARAM_NAME_ENGCOL_SEVERITY, Boolean
                .valueOf( form.isEngineColSeverity() ) );
        values.put( ReportingSettingsForm.PARAM_NAME_ENGCOL_CLASSNAME, Boolean.valueOf( form
                .isEngineColClassName() ) );
        values.put( ReportingSettingsForm.PARAM_NAME_ENGCOL_DESCRIPTION, Boolean.valueOf( form
                .isEngineColDescription() ) );
        values.put( ReportingSettingsForm.PARAM_NAME_ENGCOL_ISSUEDDATE, Boolean
                .valueOf( form.isEngineColIssued() ) );
        values.put( ReportingSettingsForm.PARAM_NAME_ENGCOL_METHODNAME, Boolean.valueOf( form
                .isEngineColmethodName() ) );
        values.put( ReportingSettingsForm.PARAM_NAME_ENGCOL_ORIGIN, Boolean.valueOf( form.isEngineColOrigin() ) );

        values.put( ReportingSettingsForm.PARAM_NAME_CONVCOL_ACTION, Boolean.valueOf( form.isConvColAction() ) );
        values.put( ReportingSettingsForm.PARAM_NAME_CONVCOL_CHOREOGRAPHYID, Boolean.valueOf( form
                .isConvColChorId() ) );
        values.put( ReportingSettingsForm.PARAM_NAME_CONVCOL_CONVID, Boolean.valueOf( form.isConvColConId() ) );
        values.put( ReportingSettingsForm.PARAM_NAME_CONVCOL_CREATED, Boolean.valueOf( form.isConvColCreated() ) );
        values.put( ReportingSettingsForm.PARAM_NAME_CONVCOL_PARTICIPANTID, Boolean.valueOf( form
                .isConvColPartId() ) );
        values.put( ReportingSettingsForm.PARAM_NAME_CONVCOL_SELECT, Boolean.valueOf( form.isConvColSelect() ) );
        values.put( ReportingSettingsForm.PARAM_NAME_CONVCOL_STATUS, Boolean.valueOf( form.isConvColStatus() ) );
        values.put( ReportingSettingsForm.PARAM_NAME_CONVCOL_TURNAROUND, Boolean.valueOf( form
                .isConvColTurnaround() ) );

        values.put( ReportingSettingsForm.PARAM_NAME_MSGCOL_ACTION, Boolean.valueOf( form.isMessColAction() ) );
        values.put( ReportingSettingsForm.PARAM_NAME_MSGCOL_CREATED, Boolean.valueOf( form.isMessColCreated() ) );
        values.put( ReportingSettingsForm.PARAM_NAME_MSGCOL_MSGID, Boolean.valueOf( form.isMessColMessageId() ) );
        values.put( ReportingSettingsForm.PARAM_NAME_MSGCOL_PARTICIPANTID, Boolean.valueOf( form
                .isMessColParticipantId() ) );
        values.put( ReportingSettingsForm.PARAM_NAME_MSGCOL_SELECT, Boolean.valueOf( form.isMessColSelect() ) );
        values.put( ReportingSettingsForm.PARAM_NAME_MSGCOL_STATUS, Boolean.valueOf( form.isMessColStatus() ) );
        values.put( ReportingSettingsForm.PARAM_NAME_MSGCOL_TURNAROUND, Boolean.valueOf( form
                .isMessColTurnaround() ) );
        values.put( ReportingSettingsForm.PARAM_NAME_MSGCOL_TYPE, Boolean.valueOf( form.isMessColType() ) );

        values.put( ReportingSettingsForm.PARAM_NAME_TIMEZONE, form.getTimezone() );
        values.put( ReportingSettingsForm.PARAM_NAME_ROWCOUNT, "" + form.getPageSize() );

        Engine.getInstance().getActiveConfigurationAccessService().setGenericParameters(
                "log_display_configuration", null, values, form.getParameterMap(), true );
        form.setCommand( "" );
        return actionMapping.findForward( ACTION_FORWARD_SUCCESS );
    }

}
