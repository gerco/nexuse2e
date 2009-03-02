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

import org.nexuse2e.Configurable;
import org.nexuse2e.Manageable;

/**
 * A <code>Pipelet</code> is a processing unit in the message workflow. Each Pipelet provides 
 * a distinct function on the message, e.g. encoding or decoding header or payload structures, 
 * data compression/decompression etc.
 * 
 * @author mbreilmann
 *
 */
public interface Pipelet extends Configurable, Manageable, MessageProcessor {

    /**
     * Determines if this <code>Pipelet</code> is a forward pipelet.
     * @return The <code>forward</code> status. Defaults to <code>true</code>.
     */
    public boolean isForwardPipelet();
    
    /**
     * Sets the <code>forward</code> status.
     * @param isForwardPipelet <code>true</code> if this shall be a forward pipelet,
     * <code>false</code> otherwise.
     */
    public void setForwardPipelet( boolean isForwardPipelet );
    
    /**
     * Gets the <code>frontendPipelet</code> state.
     * @return <code>true</code> if this is a frontend pipelet, <code>false</code> 
     * if this is a backend pipelet.
     */
    public boolean isFrontendPipelet();
    
    /**
     * Sets the <code>frontendPipelet</code> state.
     * @param isFrontendPipelet <code>true</code> if this pipelet shall be a frontend
     * pipelet, <code>false</code> if it shall be a backend pipelet.
     */
    public void setFrontendPipelet( boolean isFrontendPipelet );
    
    /**
     * Gets the parent <code>Pipeline</code>.
     * @return The parent pipeline.
     */
    public Pipeline getPipeline();
    
    /**
     * Sets the parent <code>Pipeline</code>.
     * @param pipeline The parent pipeline to set.
     */
    public void setPipeline( Pipeline pipeline );
 
} // Pipelet
