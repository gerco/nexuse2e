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
package org.nexuse2e;

public class Version {

    private static final String  SUBVERSION_REVISION = "36";
    private static final String  SUBVERSION_DATE     = "2007/04/23 17:27:49";
    private static final boolean BETA                = true;
    private static final int     BETA_VERSION        = 5;
    private static final String  VERSION             = "4.0.0 " + ( BETA ? "BETA-" + BETA_VERSION : "" ) + ", Build "
                                                             + SUBVERSION_REVISION + " (" + SUBVERSION_DATE + ")";

    public static String getVersion() {

        return VERSION;
    }

    public static void main( String[] args ) {

        System.out.println( "NexusE2E Version: " + VERSION );
    }

} // Version