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
package org.nexuse2e.messaging.ebxml.legacy;

public class Constants extends org.nexuse2e.messaging.Constants {

    public static final String MESSAGE_TYPE_NORMAL = "Normal";
    public static final String MESSAGE_TYPE_ACK    = "Acknowledgment";
    public static final String MESSAGE_TYPE_ERROR  = "MessageError";

    // HTTPTransport Packaging
    public static String       MIMEPARTBOUNDARY    = "--MIME_boundary";
    public static String       MIMEPACKBOUNDARY    = "--MIME_boundary--";
    public static String       HDRCONTENTTYPE      = "Content-Type: text/xml";

    public static final int    MIME_SMTP           = 1;
    public static final int    MIME_EBXML          = 2;
    public static final int    MIME_PAYLOAD        = 3;
    public static final int    MIME_NONE_PAYLOAD   = 4;
    public static final int    MIME_NONE           = 999;

}
