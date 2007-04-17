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
package org.nexuse2e.ui.action.serverconfig;

import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.nexuse2e.Engine;
import org.nexuse2e.configuration.ConfigurationUtil;
import org.nexuse2e.logging.LogAppender;
import org.nexuse2e.pojo.LoggerParamPojo;
import org.nexuse2e.pojo.LoggerPojo;
import org.nexuse2e.ui.action.NexusE2EAction;
import org.nexuse2e.ui.form.LoggerForm;

/**
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 *
 * @author markus.breilmann
 */
public class NotifierViewAction extends NexusE2EAction {

    //private static final String VERSIONSTRING = "$Id: NotifierViewAction.java 879 2005-07-21 14:17:36Z markus.breilmann $";

    /* (non-Javadoc)
     * @see com.tamgroup.nexus.e2e.ui.action.NexusE2EAction#executeNexusE2EAction(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.apache.struts.action.ActionMessages)
     */
    @Override
    public ActionForward executeNexusE2EAction( ActionMapping actionMapping, ActionForm actionForm,
            HttpServletRequest request, HttpServletResponse response, ActionMessages errors, ActionMessages messages )
            throws Exception {

        ActionForward success = actionMapping.findForward( ACTION_FORWARD_SUCCESS );

        LoggerForm loggerForm = (LoggerForm) actionForm;

        LoggerPojo loggerPojo = Engine.getInstance().getActiveConfigurationAccessService().getLoggerByNxLoggerId(
                loggerForm.getNxLoggerId() );

        loggerForm.setProperties( loggerPojo );
        LogAppender logger = Engine.getInstance().getActiveConfigurationAccessService().getLogger( loggerPojo.getName() );
        for ( LoggerParamPojo loggerParam : loggerPojo.getLoggerParams() ) {
            loggerParam.setParameterDescriptor( logger.getParameterMap().get( loggerParam.getParamName() ) );
        }
        loggerForm.setParameters( ConfigurationUtil.getConfiguration( logger, loggerPojo ) );
        loggerForm.createParameterMapFromPojos();
        loggerForm.setLoggerInstance( logger );
        loggerForm.setComponentName( loggerPojo.getComponent().getName() );

        // Filtergroup Fake

//        String[] groups = new String[] { "core", "inbound", "outbound", "mail", "http"};

        HashMap<String, List<String>> logCategories = Engine.getInstance().getCurrentConfiguration().getLogCategories();
        
        loggerForm.getGroupNames().clear();
        HashMap<String, String> tempFilterValues = new HashMap<String, String>();
        HashMap<String, String> enabledFilterValues = new HashMap<String, String>();
        StringTokenizer st = new StringTokenizer( loggerPojo.getFilter(), "," );
        while ( st.hasMoreTokens() ) {
            enabledFilterValues.put( st.nextToken(), "true" );
        }

        loggerForm.setThreshold( loggerPojo.getThreshold() );
        
        for ( String group : logCategories.keySet() ) {

            String value = enabledFilterValues.get( "group_" + group );
            if ( value != null ) {
                LOG.trace( "value:" + value );
                tempFilterValues.put( "group_" + group, value );
                enabledFilterValues.remove( "group_" + group );
            } else {
                tempFilterValues.put( "group_" + group, "false" );
            }
            loggerForm.getGroupNames().add( group );
        }
        if ( enabledFilterValues.size() > 0 ) {
            StringBuffer filterBuffer = new StringBuffer();
            for ( String pattern : enabledFilterValues.keySet() ) {
                if ( pattern.trim().equals( "" ) ) {
                    continue;
                }
                filterBuffer.append( pattern.trim() );
                filterBuffer.append( ", " );
            }
            loggerForm.setFilterJavaPackagePattern( filterBuffer.toString() );
        }

        loggerForm.getLogFilterValues().clear();
        loggerForm.setLogFilterValues( tempFilterValues );

        return success;
    } // executeNexusE2EAction

} // NotifierViewAction
