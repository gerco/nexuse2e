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
package org.nexuse2e.messaging.ebxml;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.nexuse2e.NexusException;
import org.nexuse2e.messaging.TimestampFormatter;

public class EBXMLTimestampFormatter implements TimestampFormatter {

    public String getTimestamp( Date time ) {

        SimpleDateFormat ebXMLDateFormat = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss'Z'" );
        return ebXMLDateFormat.format( time );
    }

    public Date getTimestamp( String time ) throws NexusException {

        try {
            SimpleDateFormat ebXMLDateFormat = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss'Z'" );
            return ebXMLDateFormat.parse( time );
        } catch ( ParseException e ) {
            try {
                
                SimpleDateFormat ebXMLDateFormat = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" );
                return ebXMLDateFormat.parse( time );
                
            } catch ( ParseException pe ) {
                   throw new NexusException( "Error while parsing timestamp:", pe );
            }
        }
    }

}

/*
    if ( timestamp.endsWith( "Z" ) || timestamp.endsWith( "z" ) ) {
        LOG
                .info( "timestamp ends with Z. UTC is expected and Z is replaced with '-0000'" );
        timestamp = timestamp.substring( 0, timestamp.length() - 1 ) + "+0000";
        SimpleDateFormat targetFormat = new SimpleDateFormat(
                stripParameter( params[1] ) );
        Date dateValue = newDate;
    }
*/