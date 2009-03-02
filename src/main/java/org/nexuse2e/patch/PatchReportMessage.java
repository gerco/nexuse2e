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
package org.nexuse2e.patch;

/**
 * A single human-readable message that was created by a <code>Patch</code>.
 *
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public class PatchReportMessage {
    /**
     * This enumeration lists all types of messages that can be added to a
     * <code>PatchReport</code>.
     */
    public enum Type {
        DETAIL,
        INFO,
        WARNING,
        ERROR,
        FATAL
    }
    
    private String message;
    private Type type;

    /**
     * Constructs a new <code>PatchReportMessage</code>.
     * @param type The message type, must not be <code>null</code>.
     * @param message The message. Shall not be <code>null</code>.
     */
    public PatchReportMessage( Type type, String message ) {
        this.type = type;
        this.message = message;
    }
    
    /**
     * Gets the message type.
     * @return The type.
     */
    public Type getType() {
        return type;
    }
    
    /**
     * The message.
     * @return The message.
     */
    public String getMessage() {
        return message;
    }
}
