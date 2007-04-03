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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author gesch
 *
 */
public class DateUtil {

    public static final String  dbFormat      = "yyyyMMddHHmmssSSS";
    
    /**
     * Converts local Date to displayable String using the given Timezone and a optional pattern 
     * 
     * @param time
     * @param timeZone java TimeZone Ids (e.g. GMT+8)
     * @param pattern format pattern (SimpleDateFormat) (null = default)
     * @return display formated String
     */
    public static String localTimeToTimezone( Date time, String timeZone, String pattern ) {

        try {
            if ( pattern == null ) {
                pattern = "yyyy-MM-dd HH:mm:ss.SSS z";
            }

            SimpleDateFormat sdf = new SimpleDateFormat( pattern );
            if ( timeZone != null && !timeZone.equals( "" ) ) {
                sdf.setTimeZone( TimeZone.getTimeZone( timeZone ) );
            }
            return sdf.format( time );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return "#" + time + "#";
    }

    /**
     *  Static method returns the current date in String format used by Nexus 3.x.
     *  @returns java.lang.String Representings Local Date String
     */
    public static String getFormatedNowString() {

        Date date = new Date();
        SimpleDateFormat databaseDateFormat = new SimpleDateFormat( dbFormat );
        String stringDate = databaseDateFormat.format( date );
        return stringDate;
    }
}
