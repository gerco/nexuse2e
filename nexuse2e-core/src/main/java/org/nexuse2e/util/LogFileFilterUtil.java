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

import java.io.File;
import java.io.IOException;

/**
 * Command-line util for log file filtering.
 * 
 * @author Jonas Reese
 */
public class LogFileFilterUtil {

    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            System.err.println("Error: not enough arguments. Usage: java " + LogFileFilterUtil.class.getName() + " <input_file> <output_file> <text>");
            return;
        }
        
        File input = new File(args[0]);
        File output = new File(args[1]);
        String pattern = args[2];
        FileUtil.lineFilterContains(input, pattern, output, true);
    }
}
