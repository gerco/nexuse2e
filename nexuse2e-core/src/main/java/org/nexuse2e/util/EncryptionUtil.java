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
package org.nexuse2e.util;

import org.apache.commons.codec.binary.Base64;

/**
 * @author gesch
 *
 */
public class EncryptionUtil {

    /**
     * Scramble a String using Base64 encoding
     * @param plainText The text to be obfuscated
     * @return The Base64 encoded String
     */
    public static String encryptString( String plainText ) {

        if ( plainText != null ) {
            return new String( Base64.encodeBase64( plainText.getBytes() ) );
        }

        return null;
    } // encryptString

    /**
     * Unscramble a String using Base64 encoding
     * @param secureText The Base64 encoded String
     * @return The plain text
     */
    public static String decryptString( String secureText ) {

        if ( secureText != null ) {
            return new String( Base64.decodeBase64( secureText.getBytes() ) );
        }
        return null;
    } // encryptString

}
