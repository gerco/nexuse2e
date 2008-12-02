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
import org.nexuse2e.Constants;
import org.nexuse2e.Engine;
import org.nexuse2e.Constants.MappingType;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.dao.PersistentPropertyDAO;
import org.nexuse2e.pojo.PersistentPropertyPojo;
import org.nexuse2e.ui.action.NexusE2EAction;
import org.nexuse2e.ui.form.PersistentPropertiesForm;


public class PersistentPropertiesAction extends NexusE2EAction {

    @Override
    public ActionForward executeNexusE2EAction( ActionMapping actionMapping, ActionForm actionForm,
            HttpServletRequest request, HttpServletResponse response, EngineConfiguration engineConfiguration, ActionMessages errors, ActionMessages messages )
            throws Exception {

        ActionForward success = actionMapping.findForward( ACTION_FORWARD_SUCCESS );
        PersistentPropertiesForm form = (PersistentPropertiesForm) actionForm;

        String action = form.getSubmitaction();
        form.setSubmitaction( null );

        PersistentPropertyDAO dao = (PersistentPropertyDAO) Engine.getInstance().getDao( Constants.PERSISTENT_PROPERTY_DAO );
        
        if ( !StringUtils.isEmpty( action ) && action.equals( "add" ) ) {
            if ( !StringUtils.isEmpty( form.getNamespace() ) && !StringUtils.isEmpty( form.getName() ) && !StringUtils.isEmpty( form.getVersion() ) ) {
                PersistentPropertyPojo property = new PersistentPropertyPojo();

                property.setNamespace( form.getNamespace() );
                property.setName( form.getName() );
                property.setValue( form.getValue() );
                property.setVersion( form.getVersion() );
                dao.savePersistentProperty( property );
            }

        } else if ( !StringUtils.isEmpty( action ) && action.equals( "update" ) ) {
            if ( !StringUtils.isEmpty( form.getValue() ) ) {
                int nxId = 0;
                try {
                    nxId = Integer.parseInt( form.getNxPersistentPropertyId() );
                } catch ( Exception e ) {
                    e.printStackTrace();
                }
                if ( nxId != 0 ) {
                    PersistentPropertyPojo property = dao.getPersistentPropertyById(  nxId );
                    
                    if ( property != null ) {
                        property.setNamespace( form.getNamespace() );
                        property.setName( form.getName() );
                        property.setValue( form.getValue() );
                        property.setVersion( form.getVersion() );

                        dao.savePersistentProperty( property );
                    }
                }
            }
        } else if ( !StringUtils.isEmpty( action ) && action.equals( "delete" ) ) {
            if ( !StringUtils.isEmpty( form.getValue() ) ) {
                int nxId = 0;
                try {
                    nxId = Integer.parseInt( form.getNxPersistentPropertyId() );
                } catch ( Exception e ) {
                    e.printStackTrace();
                }
                if ( nxId != 0 ) {
                    PersistentPropertyPojo property = dao.getPersistentPropertyById( nxId );
                    if ( property != null ) { 
                        dao.deletePersistentProperty( property );
                    }
                }
            }
        }
        List<PersistentPropertyPojo> properties = dao.getPersistentProperties( null, null );

        if (properties == null) {
            request.setAttribute( ATTRIBUTE_COLLECTION, Collections.EMPTY_LIST );
        } else {
            request.setAttribute( ATTRIBUTE_COLLECTION, properties );
        }
        


        List<String> typenames = new Vector<String>();
        for ( MappingType type : MappingType.values() ) {
            typenames.add( ""+type );
        }
        
        return success;
    }

}