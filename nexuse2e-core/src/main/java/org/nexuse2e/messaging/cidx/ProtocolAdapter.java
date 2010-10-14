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
package org.nexuse2e.messaging.cidx;

import java.util.List;
import java.util.Vector;

import org.nexuse2e.NexusException;
import org.nexuse2e.ProtocolSpecificKey;
import org.nexuse2e.messaging.Constants;
import org.nexuse2e.messaging.ErrorDescriptor;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.messaging.Constants.ErrorMessageReasonCode;
import org.nexuse2e.pojo.ChoreographyPojo;

public class ProtocolAdapter implements org.nexuse2e.messaging.ProtocolAdapter {

//    private static Logger       LOG                    = Logger.getLogger( ProtocolAdapter.class );
//    private static String       receiptAcknowledgement = "    <ReceiptAcknowledgement>\n"
//                                                               + "      <fromRole>\n"
//                                                               + "        <PartnerRoleDescription>\n"
//                                                               + "          <ContactInformation>\n"
//                                                               + "            <contactName xsi:nil=\"true\" />\n"
//                                                               + "            <EmailAddress>string</EmailAddress>\n"
//                                                               + "            <telephoneNumber xsi:nil=\"true\" />\n"
//                                                               + "          </ContactInformation>\n"
//                                                               + "          <GlobalPartnerRoleClassificationCode>string</GlobalPartnerRoleClassificationCode>\n"
//                                                               + "          <PartnerDescription>\n"
//                                                               + "            <BusinessDescription xsi:nil=\"true\" />\n"
//                                                               + "            <GlobalPartnerClassificationCode>string</GlobalPartnerClassificationCode>\n"
//                                                               + "          </PartnerDescription>\n"
//                                                               + "        </PartnerRoleDescription>\n"
//                                                               + "      </fromRole>\n"
//                                                               + "      <NonRepudiationInformation>\n"
//                                                               + "        <GlobalDigestAlgorithmCode>string</GlobalDigestAlgorithmCode>\n"
//                                                               + "        <OriginalMessageDigest>string</OriginalMessageDigest>\n"
//                                                               + "      </NonRepudiationInformation>\n"
//                                                               + "      <receivedDocumentDateTime>\n"
//                                                               + "        <DateTimeStamp>string</DateTimeStamp>\n"
//                                                               + "      </receivedDocumentDateTime>\n"
//                                                               + "      <receivedDocumentIdentifier>\n"
//                                                               + "        <ProprietaryDocumentIdentifier>string</ProprietaryDocumentIdentifier>\n"
//                                                               + "      </receivedDocumentIdentifier>\n"
//                                                               + "      <thisMessageDateTime>\n"
//                                                               + "        <DateTimeStamp>string</DateTimeStamp>\n"
//                                                               + "      </thisMessageDateTime>\n"
//                                                               + "      <thisMessageIdentifier>\n"
//                                                               + "        <ProprietaryMessageIdentifier>string</ProprietaryMessageIdentifier>\n"
//                                                               + "      </thisMessageIdentifier>\n"
//                                                               + "      <toRole>\n"
//                                                               + "        <PartnerRoleDescription>\n"
//                                                               + "          <ContactInformation>\n"
//                                                               + "            <contactName xsi:nil=\"true\" />\n"
//                                                               + "            <EmailAddress>string</EmailAddress>\n"
//                                                               + "            <telephoneNumber xsi:nil=\"true\" />\n"
//                                                               + "          </ContactInformation>\n"
//                                                               + "          <GlobalPartnerRoleClassificationCode>string</GlobalPartnerRoleClassificationCode>\n"
//                                                               + "          <PartnerDescription>\n"
//                                                               + "            <BusinessDescription xsi:nil=\"true\" />\n"
//                                                               + "            <GlobalPartnerClassificationCode>string</GlobalPartnerClassificationCode>\n"
//                                                               + "          </PartnerDescription>\n"
//                                                               + "        </PartnerRoleDescription>\n"
//                                                               + "      </toRole>\n"
//                                                               + "    </ReceiptAcknowledgement>\n";
    private ProtocolSpecificKey key                    = null;

    public void addProtcolSpecificParameters( MessageContext messageContext ) {
    }

    public MessageContext createAcknowledgement( ChoreographyPojo choreography,
            MessageContext messageContext ) {

        return null;
    }

    public MessageContext createErrorAcknowledgement( Constants.ErrorMessageReasonCode reasonCode, ChoreographyPojo choreography,
            MessageContext messageContext, Vector<ErrorDescriptor> errorMessages ) {

        return null;
    }

    public int determineMessageType( MessageContext messageContext ) {

        if ( messageContext.getMessagePojo().getAction().equals(
                org.nexuse2e.messaging.ebxml.Constants.MESSAGE_TYPE_ACK ) ) {
            return org.nexuse2e.messaging.Constants.INT_MESSAGE_TYPE_ACK;
        } else if ( messageContext.getMessagePojo().getAction().equals(
                org.nexuse2e.messaging.ebxml.Constants.MESSAGE_TYPE_ERROR ) ) {
            return org.nexuse2e.messaging.Constants.INT_MESSAGE_TYPE_ERROR;
        }
        return org.nexuse2e.messaging.Constants.INT_MESSAGE_TYPE_NORMAL;
    }

    public ProtocolSpecificKey getKey() {

        return key;
    }

    public void setKey( ProtocolSpecificKey key ) {

        this.key = key;
    }

    public MessageContext createErrorAcknowledgement(
            ErrorMessageReasonCode reasonCode, ChoreographyPojo choreography,
            MessageContext messageContext, List<ErrorDescriptor> errorMessages) {

        return null;
    }

    public MessageContext createResponse(MessageContext messageContext)
            throws NexusException {

        return null;
    }

} // ProtocolAdapter
