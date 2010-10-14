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
 * Objects of this class receive human-readable messages during the execution of a
 * <code>Patch</code>.
 *
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public interface PatchReporter {

    
    /**
     * Adds a <code>DETAIL</code> type message to this report.
     * @param message The human-readable message.
     */
    public void detail( String message );

    /**
     * Adds a <code>DETAIL</code> type message to this report.
     * @param message The human-readable message.
     */
    public void info( String message );

    /**
     * Adds a <code>WARNING</code> type message to this report.
     * @param message The human-readable message.
     */
    public void warning( String message );

    /**
     * Adds an <code>ERROR</code> type message to this report.
     * @param message The human-readable message.
     */
    public void error( String message );

    /**
     * Adds a <code>FATAL</code> type message to this report.
     * Fatal messages shall only be added to a report if the patch execution failed.
     * @param message The human-readable message.
     */
    public void fatal( String message );
}
