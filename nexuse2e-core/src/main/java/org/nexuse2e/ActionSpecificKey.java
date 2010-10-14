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
package org.nexuse2e;

import java.io.Serializable;

/**
 * Key identifying an entity that is specific to an action within a choreogrpahy.
 * 
 * @author gesch
 *
 */
public class ActionSpecificKey  implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = 6647137553991695400L;
    /**
     * The unique identifier of the action.
     */
    private String actionId       = null;
    /**
     * The unique identifier of the choreography.
     */
    private String choreographyId = null;

    /**
     * The default constructor.
     */
    public ActionSpecificKey() {

    } // ActionSpecificKey

    /**
     * @param actionId The unique identifier of the action.
     * @param choreographyId The unique identifier of the choreography.
     */
    public ActionSpecificKey( String actionId, String choreographyId ) {

        this.actionId = actionId;
        this.choreographyId = choreographyId;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals( Object obj ) {

        if ( this == obj ) {
            return true;
        } else {
            if ( obj instanceof ActionSpecificKey ) {
                ActionSpecificKey keyObj = (ActionSpecificKey) obj;
                if ( this.actionId.equals( keyObj.actionId ) && this.choreographyId.equals( keyObj.choreographyId ) ) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @param obj
     * @return
     */
    public boolean equalsIgnoreAction( Object obj ) {

        if ( this == obj ) {
            return true;
        } else {
            if ( obj instanceof ActionSpecificKey ) {
                ActionSpecificKey keyObj = (ActionSpecificKey) obj;
                if ( this.choreographyId.equals( keyObj.choreographyId ) ) {
                    return true;
                }
            }
        }
        return false;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {

        return new String( choreographyId + "/" + actionId ).hashCode();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {

        return new StringBuilder().append( "ActionKey: " ).append( choreographyId ).append( "/" ).append( actionId )
                .toString();
    }

    /**
     * @return The unique identifier of the action.
     */
    public String getActionId() {

        return actionId;
    }

    /**
     * @param actionId The unique identifier of the action to set
     */
    public void setActionId( String actionId ) {

        this.actionId = actionId;
    }

    /**
     * @return The unique identifier of the choreography.
     */
    public String getChoreographyId() {

        return choreographyId;
    }

    /**
     * @param choreographyId The unique identifier of the choreography to set
     */
    public void setChoreographyId( String choreographyId ) {

        this.choreographyId = choreographyId;
    }

}
