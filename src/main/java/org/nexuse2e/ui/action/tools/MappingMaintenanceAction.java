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
package org.nexuse2e.ui.action.tools;


import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.nexuse2e.Constants.MappingType;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.pojo.MappingPojo;
import org.nexuse2e.ui.action.NexusE2EAction;
import org.nexuse2e.ui.form.MappingMaintenanceForm;


public class MappingMaintenanceAction extends NexusE2EAction {

    @Override
    public ActionForward executeNexusE2EAction( ActionMapping actionMapping, ActionForm actionForm,
            HttpServletRequest request, HttpServletResponse response, EngineConfiguration engineConfiguration, ActionMessages errors, ActionMessages messages )
            throws Exception {

        ActionForward success = actionMapping.findForward( ACTION_FORWARD_SUCCESS );
        MappingMaintenanceForm form = (MappingMaintenanceForm) actionForm;

        System.out.println( "form.submitaction: " + form.getSubmitaction() );
        System.out.println( "form.nxMappingId: " + form.getNxMappingId() );
        System.out.println( "form.category: " + form.getCategory() );
        System.out.println( "form.leftType: " + form.getLeftType() );
        System.out.println( "form.leftValue: " + form.getLeftValue() );
        System.out.println( "form.rightType: " + form.getRightType() );
        System.out.println( "form.rightValue: " + form.getRightValue() );

        String action = form.getSubmitaction();

        System.out.println("values:"+MappingType.values());
        
        if ( !StringUtils.isEmpty( action ) && action.equals( "add" ) ) {
            if ( !StringUtils.isEmpty( form.getCategory() ) ) {
                MappingPojo mapping = new MappingPojo();

                mapping.setCategory( form.getCategory() );
                mapping.setLeftType( form.getLeftType() );
                mapping.setRightType( form.getRightType() );
                
                mapping.setLeftValue( form.getLeftValue() );
                mapping.setRightValue( form.getRightValue() );

                engineConfiguration.updateMapping( mapping );
            }

        } else if ( !StringUtils.isEmpty( action ) && action.equals( "update" ) ) {
            if ( !StringUtils.isEmpty( form.getCategory() ) ) {
                int nxId = 0;
                try {
                    nxId = Integer.parseInt( form.getNxMappingId() );
                } catch ( Exception e ) {
                    e.printStackTrace();
                }
                if ( nxId != 0 ) {
                    MappingPojo mapping = engineConfiguration
                            .getMappingByNxMappingId( nxId );
                    
                    if ( mapping != null ) {
                        mapping.setCategory( form.getCategory() );
                        mapping.setLeftType( form.getLeftType() );
                        mapping.setRightType( form.getRightType() );

                        mapping.setLeftValue( form.getLeftValue() );
                        mapping.setRightValue( form.getRightValue() );

                        engineConfiguration.updateMapping( mapping );
                    }
                }
            }
        } else if ( !StringUtils.isEmpty( action ) && action.equals( "delete" ) ) {
            if ( !StringUtils.isEmpty( form.getCategory() ) ) {
                int nxId = 0;
                try {
                    nxId = Integer.parseInt( form.getNxMappingId() );
                } catch ( Exception e ) {
                    e.printStackTrace();
                }
                if ( nxId != 0 ) {
                    MappingPojo mapping = engineConfiguration
                            .getMappingByNxMappingId( nxId );
                    if ( mapping != null ) { 
                        engineConfiguration.deleteMapping( mapping );
                    }
                }
            }
        }
        List<MappingPojo> mappings = engineConfiguration.getMappings( null );

        if (mappings == null) {
            request.setAttribute( ATTRIBUTE_COLLECTION, Collections.EMPTY_LIST );
        } else {
            request.setAttribute( ATTRIBUTE_COLLECTION, mappings );
        }
        


        List<String> typenames = new Vector<String>();
        for ( MappingType type : MappingType.values() ) {
            typenames.add( ""+type );
        }
        

        form.setTypenames( typenames );

        return success;
    }

}
