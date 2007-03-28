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
package org.nexuse2e.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;


/**
 * @author Sebastian Schulze
 * @date 02.02.2007
 */
public class PasswordUtil {
    /**
     * For password encryption.
     */
    private static MessageDigest shaDigest;
    
    /**
     * Returns the message digest to hash the password with.
     * @return The MessageDigest to use for password encryption.
     * @throws NoSuchAlgorithmException
     */
    private static MessageDigest getMessageDigest() throws NoSuchAlgorithmException {

        if ( shaDigest == null ) {
            shaDigest = MessageDigest.getInstance( "SHA-1" );
        }
        
        return shaDigest;        
    }

    /**
     * Returns a BASE64 encoded hash of the given
     * plain-text password.
     * @param password The password to be hashed and encoded
     * @return A BASE64 encoded hash value.
     */
    public static String hashPassword( String password ) throws NoSuchAlgorithmException {
        byte[] hash = null;
        try {
            hash = getMessageDigest().digest( password.getBytes( "ISO8859-1" ) );

        } catch ( UnsupportedEncodingException e ) {
            e.printStackTrace();

        }

        if ( hash == null ) {
            return null;
        }

        String encoded = new String( Base64.encodeBase64( hash ) );
        return encoded;
    }
}
