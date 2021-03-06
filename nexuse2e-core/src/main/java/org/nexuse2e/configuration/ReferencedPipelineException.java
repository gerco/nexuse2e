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
package org.nexuse2e.configuration;

import java.util.Collection;

import org.nexuse2e.pojo.ActionPojo;

/**
 * This exception shall be thrown if an attempt is made to remove a pipeline from a configuration
 * that is still referenced by one or more actions.
 *
 * @author Jonas Reese
 */
public class ReferencedPipelineException extends ReferencedObjectException {

    private static final long serialVersionUID = 1L;

    private Collection<ActionPojo> referringActions;
    
    public ReferencedPipelineException( Collection<ActionPojo> referringActions ) {
        this.referringActions = referringActions;
    }

    @Override
    public Collection<ActionPojo> getReferringObjects() {
        return referringActions;
    }
}
