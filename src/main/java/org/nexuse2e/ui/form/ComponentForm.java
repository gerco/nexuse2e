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

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.nexuse2e.configuration.Constants.ComponentType;
import org.nexuse2e.pojo.ComponentPojo;

/**
 * @author gesch
 *
 */
public class ComponentForm extends ActionForm {

    /**
     * 
     */
    private static final long serialVersionUID = -217755396015512335L;
    private int               nxComponentId    = 0;
    private int               type             = 0;
    private String            name             = null;
    private String            className        = null;
    private String            Description      = null;

    /**
     * @param component
     */
    public void setProperties( ComponentPojo component ) {

        setNxComponentId( component.getNxComponentId() );
        setType( component.getType() );
        setName( component.getName() );
        setClassName( component.getClassName() );
        setDescription( component.getDescription() );
    }

    /**
     * @param component
     * @return
     */
    public ComponentPojo getProperties( ComponentPojo component ) {

        component.setNxComponentId( getNxComponentId() );
        component.setName( getName() );
        component.setClassName( getClassName() );
        component.setDescription( getDescription() );
        return component;
    }

    /**
     * @param mapping
     * @param request
     */
    @Override
    public void reset( ActionMapping mapping, HttpServletRequest request ) {

    }

    /**
     * 
     */
    public void cleanSettings() {

        setNxComponentId( 0 );
        setType( 0 );
        setName( null );
        setClassName( null );
        setDescription( null );

    }

    /**
     * @return the className
     */
    public String getClassName() {

        return className;
    }

    /**
     * @param className the className to set
     */
    public void setClassName( String className ) {

        this.className = className;
    }

    /**
     * @return the description
     */
    public String getDescription() {

        return Description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription( String description ) {

        Description = description;
    }

    /**
     * @return the name
     */
    public String getName() {

        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName( String name ) {

        this.name = name;
    }

    /**
     * @return the nxComponentId
     */
    public int getNxComponentId() {

        return nxComponentId;
    }

    /**
     * @param nxComponentId the nxComponentId to set
     */
    public void setNxComponentId( int nxComponentId ) {

        this.nxComponentId = nxComponentId;
    }

    /**
     * @return
     */
    public String getTypeString() {

        if ( type == ComponentType.LOGGER.getValue() ) {
            return "Logger";
        } else if ( type == ComponentType.PIPELET.getValue() ) {
            return "Pipelet";
        } else if ( type == ComponentType.SERVICE.getValue() ) {
            return "Service";
        }
        return "unknown Component";
    }

    /**
     * @return the type
     */
    public int getType() {

        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType( int type ) {

        this.type = type;
    }

}
