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

import org.apache.struts.action.ActionForm;

/**
 * @author mbreilmann
 *
 */
public class ProvisioningSubmissionForm extends ActionForm {

    /**
     * 
     */
    private static final long serialVersionUID = -8630672244641905149L;

    private String            choreographyId   = null;

    private String            receiver         = null;

    private List              choreographies   = null;

    private List              receivers        = null;

    /**

     * @return Returns the choreographies.

     */

    public List getChoreographies() {

        return choreographies;

    }

    /**

     * @param choreographies The choreographies to set.

     */

    public void setChoreographies( List choreographies ) {

        this.choreographies = choreographies;

    }

    /**

     * @return Returns the choreographyId.

     */

    public String getChoreographyId() {

        return choreographyId;

    }

    /**

     * @param choreographyId The choreographyId to set.

     */

    public void setChoreographyId( String choreographyId ) {

        this.choreographyId = choreographyId;

    }

    /**

     * @return Returns the receiver.

     */

    public String getReceiver() {

        return receiver;

    }

    /**

     * @param receiver The receiver to set.

     */

    public void setReceiver( String receiver ) {

        this.receiver = receiver;

    }

    /**

     * @return Returns the receivers.

     */

    public List getReceivers() {

        return receivers;

    }

    /**

     * @param receivers The receivers to set.

     */

    public void setReceivers( List receivers ) {

        this.receivers = receivers;

    }

} // ProvisioningSubmissionForm
