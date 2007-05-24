/*
 * Created on 03.06.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.nexuse2e.ui.action.reporting;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.Constants.ParameterType;
import org.nexuse2e.ui.action.NexusE2EAction;
import org.nexuse2e.ui.form.ReportingPropertiesForm;


/**
 * @author guido.esch
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SaveConfigFieldsAction extends NexusE2EAction {

    private static final String VERSIONSTRING = "$Id: SaveConfigFieldsAction.java 1024 2006-02-14 11:27:45Z markus.breilmann $";

    private static Logger LOG = Logger.getLogger( SaveConfigFieldsAction.class );
    
    private static String URL     = "reporting.error.url";
    private static String TIMEOUT = "reporting.error.timeout";

    
    
    /* (non-Javadoc)
     * @see com.tamgroup.nexus.e2e.ui.action.NexusE2EAction#executeNexusE2EAction(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.apache.struts.action.ActionMessages)
     */
    public ActionForward executeNexusE2EAction( ActionMapping actionMapping, ActionForm actionForm,
            HttpServletRequest request, HttpServletResponse response, ActionMessages errors, ActionMessages messages ) throws Exception {

        ActionForward success = actionMapping.findForward( ACTION_FORWARD_SUCCESS );
        ActionForward error = actionMapping.findForward( ACTION_FORWARD_FAILURE );

        ReportingPropertiesForm form = (ReportingPropertiesForm) actionForm;


        saveField( "pageSize",""+form.getPageSize());
        saveField("timezone",form.getTimezone());

        saveField( "convColSelect", "" + form.isConvColSelect() );
        saveField( "convColChorId", "" + form.isConvColChorId() );
        saveField( "convColConId", "" + form.isConvColConId() );
        saveField( "convColPartId", "" + form.isConvColPartId() );
        saveField( "convColStatus", "" + form.isConvColStatus() );
        saveField( "convColAction", "" + form.isConvColAction() );
        saveField( "convColCreated", "" + form.isConvColCreated() );
        saveField( "convColTurnaround", "" + form.isConvColTurnaround() );

        saveField( "messColSelect", "" + form.isMessColSelect() );
        saveField( "messColMessageId", "" + form.isMessColMessageId() );
        saveField( "messColParticipantId", "" + form.isMessColParticipantId() );
        saveField( "messColStatus", "" + form.isMessColStatus() );
        saveField( "messColType", "" + form.isMessColType() );
        saveField( "messColAction", "" + form.isMessColAction() );
        saveField( "messColCreated", "" + form.isMessColCreated() );
        saveField( "messColTurnaround", "" + form.isMessColTurnaround() );

        saveField( "engineColSeverity", "" + form.isEngineColSeverity() );
        saveField( "engineColIssued", "" + form.isEngineColIssued() );
        saveField( "engineColDescription", "" + form.isEngineColDescription() );
        saveField( "engineColOrigin", "" + form.isEngineColOrigin() );
        saveField( "engineColClassName", "" + form.isEngineColClassName() );
        saveField( "engineColmethodName", "" + form.isEngineColmethodName() );

        Map<String, ParameterDescriptor> parameterMap = new HashMap<String, ParameterDescriptor>(); 
        parameterMap.put( "pagesize", new ParameterDescriptor( ParameterType.STRING, "itemcount",
                "items per page", "20" ) );
        parameterMap.put( "timezone", new ParameterDescriptor( ParameterType.STRING, "itemcount",
                "items per page", "20" ) );
        
        
        
        
        LOG.debug( "Convselect:" + form.isConvColSelect() );
        return success;
    }

    private void saveField( String name, String value ) {

//        try {
//            ConfigFieldDAO cfDao = new ConfigFieldDAO();
//            ConfigFieldPojo cfp = cfDao
//                    .getConfigFieldByChoreographyIdComponentIdAndName( "#ENGINE#", "Reporting", name );
//            boolean exists = false;
//            if ( cfp == null ) {
//                ConfigFieldKey cfk = new ConfigFieldKey( "#ENGINE#", "Reporting", name );
//                cfp = new ConfigFieldPojo( cfk );
//                cfp.setCreatedDate( DateWrapper.getNOWdatabaseString() );
//                cfp.setRequired( false );
//                cfp.setHeading("Displayed Column Configuration");
//            } else {
//                exists = true;
//            }
//            cfp.setLastModifiedDate( DateWrapper.getNOWdatabaseString() );
//            cfp.setValue( value );
//            if ( exists ) {
//                cfDao.updateConfigField( cfp );
//            } else {
//                cfDao.saveConfigField( cfp );
//            }
//
//        } catch ( PersistenceException e ) {
//            e.printStackTrace();
//        }
    }
}
