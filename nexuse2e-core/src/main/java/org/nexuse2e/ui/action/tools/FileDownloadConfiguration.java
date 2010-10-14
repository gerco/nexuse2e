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
package org.nexuse2e.ui.action.tools;

import java.util.Collections;
import java.util.Map;

/**
 * Configuration bean for file download feature.
 *
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public class FileDownloadConfiguration {

    private static FileDownloadConfiguration instance = null;
    
    private Map<String, String> configuration;
    
    /**
     * Constructs a new empty <code>FileDownloadConfiguration</code>.
     */
    @SuppressWarnings("unchecked")
    public FileDownloadConfiguration() {
        instance = this;
        configuration = Collections.EMPTY_MAP;
    }
    
    /**
     * Retrieves the most recently created file download configuration instance.
     * @return The configuration. Never <code>null</code>.
     */
    public static FileDownloadConfiguration getInstance() {
        if (instance == null) {
            return new FileDownloadConfiguration();
        }
        
        return instance;
    }
    
    /**
     * Sets the configuration <code>Map</code>.
     * @param configuration The configuration. Shall not be <code>null</code>. The key is
     * a shared file (or directory) or DOS-style path pattern, the value is a display name.
     * If value is <code>null</code>, the full path will be used as display name
     */
    public void setConfiguration( Map<String, String> configuration ) {
        this.configuration = configuration;
    }
    
    /**
     * Gets the configuration.
     * @return The configuration map.
     */
    public Map<String, String> getConfiguration() {
        return configuration;
    }
}
