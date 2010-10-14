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
package org.nexuse2e.messaging;

/**
 * Constants used throughout the messaging related parts of the NEXUSe2e system.
 *
 * @author mbreilmann
 */
public class Constants extends org.nexuse2e.Constants {

    public static final int     INT_MESSAGE_TYPE_UNKNOWN       = 0;
    public static final int     INT_MESSAGE_TYPE_NORMAL        = 1;
    public static final int     INT_MESSAGE_TYPE_ACK           = 2;
    public static final int     INT_MESSAGE_TYPE_ERROR         = 3;

    public static final boolean INBOUND                        = true;
    public static final boolean OUTBOUND                       = false;

    public static final int     DEFAULT_MESSAGE_INTERVAL       = 30;

    public static final String PARAMETER_PREFIX_HTTP               = "http_";
    public static final String PARAMETER_PREFIX_HTTP_REQUEST_PARAM = "http_request_";

    /**
     * Choreography was not found in Configuration. e.g. message informations are invalid, or configuration is incomplete
     */
    public static int           ACK_ERROR_CHOREOGRPHAYNOTFOUND = 0;
    /**
     * no participant for the specified choreographyId found in configuration. 
     */
    public static int           ACK_ERROR_PARTICIPANTNOTFOUND  = 1;
    /**
     * the action defined in message header is (currently) not permitted.
     */
    public static int           ACK_ERROR_STEPNOTPERMITTED     = 2;

    /**
     * Possible reason codes when error messages are create in response to an erroneous inbound message.
     */
    public static enum ErrorMessageReasonCode {
        UNKNOWN(0), CHOREOGRAPHY_NOT_FOUND(1), PARTICIPANT_NOT_FOUND(2), ACTION_NOT_PERMITTED(3);

        private int value;

        ErrorMessageReasonCode( int value ) {

            this.value = value;
        }

        public int getValue() {

            return value;
        }
    };
} // Constants
