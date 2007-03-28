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

import java.util.Vector;

import org.apache.struts.action.ActionForm;
import org.nexuse2e.pojo.ActionPojo;
import org.nexuse2e.pojo.ChoreographyPojo;
import org.nexuse2e.pojo.ParticipantPojo;

/**
 * @author guido.esch
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ChoreographyForm extends ActionForm {

    /**
     * 
     */
    private static final long serialVersionUID = 6527987381436278179L;

    int                       nxChoreographyId = 0;
    String                    choreographyName;
    String                    description;
    String                    lastModifiedUserId;
    Vector<ActionPojo>        actions;
    Vector<ParticipantPojo>   participants;

    public void setProperties( ChoreographyPojo choreographyPojo ) {

        setNxChoreographyId( choreographyPojo.getNxChoreographyId() );
        setChoreographyName( choreographyPojo.getName() );
        setDescription( choreographyPojo.getDescription() );
        setLastModifiedUserId( "" + choreographyPojo.getModifiedNxUserId() );
    }

    public ChoreographyPojo getProperties( ChoreographyPojo choreographyPojo ) {

        choreographyPojo.setNxChoreographyId( getNxChoreographyId() );
        choreographyPojo.setName( getChoreographyName() );
        choreographyPojo.setDescription( getDescription() );
        choreographyPojo.setModifiedNxUserId( new Integer( getLastModifiedUserId() ).intValue() );
        return choreographyPojo;
    }

    /**
     * @return the choreographyName
     */
    public String getChoreographyName() {

        return choreographyName;
    }

    /**
     * @param choreographyName the choreographyName to set
     */
    public void setChoreographyName( String choreographyName ) {

        this.choreographyName = choreographyName;
    }

    /**
     * @return the description
     */
    public String getDescription() {

        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription( String description ) {

        this.description = description;
    }

    /**
     * @return the lastModifiedUserId
     */
    public String getLastModifiedUserId() {

        return lastModifiedUserId;
    }

    /**
     * @param lastModifiedUserId the lastModifiedUserId to set
     */
    public void setLastModifiedUserId( String lastModifiedUserId ) {

        this.lastModifiedUserId = lastModifiedUserId;
    }

    /**
     * @return the actions
     */
    public Vector getActions() {

        return actions;
    }

    /**
     * @param actions the actions to set
     */
    public void setActions( Vector actions ) {

        this.actions = actions;
    }

    /**
     * @return the participants
     */
    public Vector getParticipants() {

        return participants;
    }

    /**
     * @param participants the participants to set
     */
    public void setParticipants( Vector participants ) {

        this.participants = participants;
    }

    public int getNxChoreographyId() {

        return nxChoreographyId;
    }

    public void setNxChoreographyId( int nxChoreographyId ) {

        this.nxChoreographyId = nxChoreographyId;
    }

}
