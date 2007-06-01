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

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.nexuse2e.pojo.MappingPojo;
import org.nexuse2e.ui.action.NexusE2EAction;
import org.nexuse2e.ui.form.MappingMaintenanceForm;
import org.nexuse2e.ui.form.ProtectedFileAccessForm;


public class MappingMaintenanceAction extends NexusE2EAction {

    @Override
    public ActionForward executeNexusE2EAction( ActionMapping actionMapping, ActionForm actionForm,
            HttpServletRequest request, HttpServletResponse response, ActionMessages errors, ActionMessages messages )
            throws Exception {

        ActionForward success = actionMapping.findForward( ACTION_FORWARD_SUCCESS );
        MappingMaintenanceForm form = (MappingMaintenanceForm) actionForm;
        
        System.out.println("form.submitaction: "+form.getSubmitaction());
        System.out.println("form.nxMappingId: "+form.getNxMappingId());
        System.out.println("form.category: "+form.getCategory());
        System.out.println("form.leftType: "+form.getLeftType());
        System.out.println("form.leftValue: "+form.getLeftValue());
        System.out.println("form.rightType: "+form.getRightType());
        System.out.println("form.rightValue: "+form.getRightValue());
        
        List<MappingPojo> mappings = new ArrayList<MappingPojo>();
        
        MappingPojo mapping = new MappingPojo();
        mapping.setNxMappingId( 1 );
        mapping.setCategory( "abcd" );
        mapping.setLeftType( 1 );
        mapping.setLeftValue( "aaaaa" );
        mapping.setRightType( 1 );
        mapping.setRightValue( "bbbb" );
        mappings.add( mapping );
        
        mapping = new MappingPojo();
        mapping.setNxMappingId( 2 );
        mapping.setCategory( "abcd" );
        mapping.setLeftType( 1 );
        mapping.setLeftValue( "bbbb" );
        mapping.setRightType( 1 );
        mapping.setRightValue( "cccc" );
        mappings.add( mapping );
        
        request.setAttribute( ATTRIBUTE_COLLECTION, mappings );
        
        List<String> typenames = new Vector<String>();
        typenames.add( "Integer" );
        typenames.add( "String" );
        typenames.add( "Boolean" );
        
        
        List<String> typeids = new Vector<String>();
        typeids.add( "1" );
        typeids.add( "2" );
        typeids.add( "3" );
        
        form.setTypeids( typeids );
        form.setTypenames( typenames );
        
        return success;
    }

}
