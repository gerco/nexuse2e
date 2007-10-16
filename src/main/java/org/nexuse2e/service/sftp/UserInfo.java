/**
 * NEXUSe2e Business Messaging Open Source  
 * Copyright 2007, Tamgroup and X-ioma GmbH   
 *  
 * This is free software; you can redistribute it and/or modify it  
 * under the terms of the GNU Lesser General Public License as  
 * published by the Free Software Foundation version 2.1 of  
 * the License.  
 *  
 * This software is distributed in the hope that it will be useful,  
 * but WITHOUT ANY WARRANTY; without even the implied warranty of  
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU  
 * Lesser General Public License for more details.  
 *  
 * You should have received a copy of the GNU Lesser General Public  
 * License along with this software; if not, write to the Free  
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.nexuse2e.service.sftp;

/**
 * @author mbreilmann
 *
 */
public class UserInfo implements com.jcraft.jsch.UserInfo {

    private String password = null;

    public UserInfo( String password ) {

        this.password = password;
    }

    /* (non-Javadoc)
     * @see com.jcraft.jsch.UserInfo#getPassphrase()
     */
    public String getPassphrase() {
        return null;
    }

    /* (non-Javadoc)
     * @see com.jcraft.jsch.UserInfo#getPassword()
     */
    public String getPassword() {

       return password;
    }

    /* (non-Javadoc)
     * @see com.jcraft.jsch.UserInfo#promptPassphrase(java.lang.String)
     */
    public boolean promptPassphrase( String arg0 ) {

        return true;
    }

    /* (non-Javadoc)
     * @see com.jcraft.jsch.UserInfo#promptPassword(java.lang.String)
     */
    public boolean promptPassword( String arg0 ) {

        return true;
    }

    /* (non-Javadoc)
     * @see com.jcraft.jsch.UserInfo#promptYesNo(java.lang.String)
     */
    public boolean promptYesNo( String arg0 ) {

        return true;
    }

    /* (non-Javadoc)
     * @see com.jcraft.jsch.UserInfo#showMessage(java.lang.String)
     */
    public void showMessage( String arg0 ) {

    }

}
