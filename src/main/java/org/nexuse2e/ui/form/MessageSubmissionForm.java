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
package org.nexuse2e.ui.form;

import java.util.Collection;
import java.util.List;

import org.apache.struts.action.ActionForm;
import org.apache.struts.upload.FormFile;
import org.nexuse2e.pojo.PartnerPojo;

/**
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 *
 * @author markus.breilmann
 */
public class MessageSubmissionForm extends ActionForm {

    /**
     * 
     */
    private static final long serialVersionUID = 9066165869803142603L;

    private String            choreographyId   = null;
    private String            actionId         = null;
    private int               receiver         = 0;
    private String            primaryKey       = null;
    private String            conversationId   = null;
    private String            encoding         = null;
    private int               repeat           = 1;
    private FormFile          payloadFile1     = null;
    private FormFile          payloadFile2     = null;
    private FormFile          payloadFile3     = null;

    private Collection<String>       choreographies   = null;
    private Collection<String>       actions          = null;
    private Collection<PartnerPojo>  receivers        = null;
    private List<String>             encodings        = null;

    /**
     * @return Returns the actionId.
     */
    public String getActionId() {

        return actionId;
    }

    /**
     * @param actionId The actionId to set.
     */
    public void setActionId( String action ) {

        this.actionId = action;
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
     * @return Returns the conversationId.
     */
    public String getConversationId() {

        return conversationId;
    }

    /**
     * @param conversationId The conversationId to set.
     */
    public void setConversationId( String conversationId ) {

        this.conversationId = conversationId;
    }

    /**
     * @return Returns the primaryKey.
     */
    public String getPrimaryKey() {

        return primaryKey;
    }

    /**
     * @param primaryKey The primaryKey to set.
     */
    public void setPrimaryKey( String primaryKey ) {

        this.primaryKey = primaryKey;
    }

    /**
     * @return Returns the receiver.
     */
    public int getReceiver() {

        return receiver;
    }

    /**
     * @param receiver The receiver to set.
     */
    public void setReceiver( int receiver ) {

        this.receiver = receiver;
    }

    /**
     * @return Returns the repeat.
     */
    public int getRepeat() {

        return repeat;
    }

    /**
     * @param repeat The repeat to set.
     */
    public void setRepeat( int repeat ) {

        this.repeat = repeat;
    }

    /**
     * @return Returns the actions.
     */
    public Collection<String> getActions() {

        return actions;
    }

    /**
     * @param actions The actions to set.
     */
    public void setActions( Collection<String> actions ) {

        this.actions = actions;
    }

    /**
     * @return Returns the choreographies.
     */
    public Collection<String> getChoreographies() {

        return choreographies;
    }

    /**
     * @param choreographies The choreographies to set.
     */
    public void setChoreographies( Collection<String> choreographies ) {

        this.choreographies = choreographies;
    }

    /**
     * @return Returns the receivers.
     */
    public Collection<PartnerPojo> getReceivers() {

        return receivers;
    }

    /**
     * @param receivers The receivers to set.
     */
    public void setReceivers( Collection<PartnerPojo> receivers ) {

        this.receivers = receivers;
    }

    /**
     * @return Returns the payloadFile1.
     */
    public FormFile getPayloadFile1() {

        return payloadFile1;
    }

    /**
     * @param payloadFile1 The payloadFile1 to set.
     */
    public void setPayloadFile1( FormFile payloadFile1 ) {

        this.payloadFile1 = payloadFile1;
    }

    /**
     * @return Returns the payloadFile2.
     */
    public FormFile getPayloadFile2() {

        return payloadFile2;
    }

    /**
     * @param payloadFile2 The payloadFile2 to set.
     */
    public void setPayloadFile2( FormFile payloadFile2 ) {

        this.payloadFile2 = payloadFile2;
    }

    /**
     * @return Returns the payloadFile3.
     */
    public FormFile getPayloadFile3() {

        return payloadFile3;
    }

    /**
     * @param payloadFile3 The payloadFile3 to set.
     */
    public void setPayloadFile3( FormFile payloadFile3 ) {

        this.payloadFile3 = payloadFile3;
    }

    public List<String> getEncodings() {

        return encodings;
    }

    public void setEncodings( List<String> encodings ) {

        this.encodings = encodings;
    }

    
    public String getEncoding() {
    
        return encoding;
    }

    
    public void setEncoding( String encoding ) {
    
        this.encoding = encoding;
    }
} // MessageSubmissionForm
