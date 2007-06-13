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
package org.nexuse2e.tools.mapping.input;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


import org.nexuse2e.tools.mapping.conversation.ProcessCSV;
import org.nexuse2e.tools.mapping.csv.RecordContainer;


/**
 * @author guido.esch
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CSVReader {

    /**
     * @param parent
     * @param source
     * @param separator
     */
    public void readCSV( ProcessCSV parent, InputStream source, RecordContainer records ) {

     
        InputStreamReader isr = new InputStreamReader( source );
        BufferedReader br = new BufferedReader( isr );
        String line = ""; //$NON-NLS-1$
        while ( line != null ) {
            try {
                line = br.readLine();
                if ( line != null && !line.trim().equals( "" ) ) { //$NON-NLS-1$
                    CSVLine newLine = new CSVLine( line, records );
                    parent.processLine( newLine );
                }
            } catch ( IOException e1 ) {

//                Plugin
//                        .getDefault()
//                        .log(
//                                new LogMessage(
//                                        LogMessage.ERROR,
//                                        "Processing", e1.getClass().getName(), this, "readCSV", 169, e1.getLocalizedMessage(), e1 ) ); //$NON-NLS-1$
                return;
            }
        }
        try {
            br.close();
            isr.close();
        } catch ( IOException e1 ) {
//            Plugin
//                    .getDefault()
//                    .log(
//                            new LogMessage(
//                                    LogMessage.ERROR,
//                                    "Processing", e1.getClass().getName(), this, "readCSV", 170, e1.getLocalizedMessage(), e1 ) ); //$NON-NLS-1$
        }
    }
}