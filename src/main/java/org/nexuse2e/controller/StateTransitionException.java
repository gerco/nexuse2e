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
package org.nexuse2e.controller;

/**
 * This exception signals invalid state transitions for stateful NEXUSe2e POJO types,
 * e.g. conversations or messages.
 *
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public class StateTransitionException extends Exception {

    private static final long serialVersionUID = 1L;

    public StateTransitionException() {
        super();
    }

    public StateTransitionException(String message, Throwable cause) {
        super(message, cause);
    }

    public StateTransitionException(String message) {
        super(message);
    }

    public StateTransitionException(Throwable cause) {
        super(cause);
    }
}
