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
 * This exception shall be raised when a patch failed to execute.
 *
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public class PatchException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a <code>PatchException</code> with no further information.
     */
    public PatchException() {
        super();
    }

    /**
     * Constructs a <code>PatchException</code> with the given error message
     * and cause.
     * @param message The message.
     * @param cause The cause.
     */
    public PatchException( String message, Throwable cause ) {
        super( message, cause );
    }

    /**
     * Constructs a <code>PatchException</code> with the given error message.
     * @param message The message.
     */
    public PatchException( String message ) {
        super( message );
    }

    /**
     * Constructs a <code>PatchException</code> with the given cause.
     * @param cause The cause.
     */
    public PatchException( Throwable cause ) {
        super(cause);
    }
}
