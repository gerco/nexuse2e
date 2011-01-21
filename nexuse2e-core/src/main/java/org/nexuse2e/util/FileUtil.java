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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for file-related operations.
 *
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public class FileUtil {

    /**
     * Private constructor (this is a static class).
     */
    private FileUtil() {
    }
    
    /**
     * Converts a DOS-style file pattern ("*.xml", "ab?.txt", ...) into a regular
     * expression.
     * @param pattern The DOS-style file name pattern.
     * @return The converted regular expression can be used to match DOS style file names
     * with the Java regexp API.
     */
    public static String dosStyleToRegEx( String pattern ) {

        StringBuilder sb = new StringBuilder();
        for ( int i = 0; i < pattern.length(); i++ ) {
            char c = pattern.charAt( i );
            if ( '?' != c && '*' != c ) {
                sb.append( "\\Q" );
                sb.append( c );
                sb.append( "\\E" );
            } else {
                sb.append( c );
            }
        }
        pattern = sb.toString().replaceAll( "\\*", "\\.\\*" );
        pattern = pattern.replaceAll( "\\?", "\\." );

        return pattern;
    }

    /**
     * Returns a <code>FilenameFilter</code> for the given DOS-style file name pattern.
     * @param dosStylePattern The DOS-style file name pattern (e.g. *.xml, *.?ml, ???.bin).
     * @return A <code>FilenameFilter</code> implementation.
     */
    public static FilenameFilter getFilenameFilterForPattern( String dosStylePattern ) {
        final Pattern p = Pattern.compile( dosStyleToRegEx( dosStylePattern ) );
        return new FilenameFilter() {
            public boolean accept( File dir, String name ) {
                Matcher m = p.matcher( name );
                return m.matches();
            }
        };
    }
    
    /**
     * Returns <code>true</code> if and only if the given path contains a DOS-style filename
     * pattern.
     * @param path The path to check.
     * @return <code>true</code> if file pattern is contained, <code>false</code> otherwise.
     */
    public static boolean containsDosStylePattern( String path ) {
        if (path == null) {
            return false;
        }
        return path.indexOf( '*' ) >= 0 || path.indexOf( '?' ) >= 0;
    }

    /**
     * Gets the leaf (the deepest element from the given path). This also works for
     * paths that contain DOS-style patterns, e.g. "c:/temp/*.xml".
     * @param path The path.
     * @return The leaf. In our example, "*.xml".
     */
    public static String getLeaf( String path ) {
        String p = path.replace( File.separatorChar, '/' );
        int index = p.lastIndexOf( '/' );
        if (index >= 0) {
            return path.substring( index + 1 );
        }
        return path;
    }
    
    /**
     * Filters an input file by line and "contains" check and writes the resulting lines into the given output file.
     * @param input The input file. Must not be <code>null</code>.
     * @param pattern The pattern (non-regex). Shall not be <code>null</code>.
     * @param output The output file.
     * @param ignoreCase If this is <code>true</code>, a case-insensitive compare will be made.
     * @throws IOException If something went wrong.
     */
    public static void lineFilterContains(File input, String pattern, File output, boolean ignoreCase) throws IOException {
        if (pattern == null) {
            return;
        }
        BufferedReader reader = new BufferedReader(new FileReader(input));
        PrintWriter writer = new PrintWriter(output);
        String line;
        if (ignoreCase) {
            pattern = pattern.toLowerCase();
            while ((line = reader.readLine()) != null) {
                if (line.toLowerCase().contains(pattern)) {
                    writer.println(line);
                }
            }
        } else {
            while ((line = reader.readLine()) != null) {
                if (line.contains(pattern)) {
                    writer.println(line);
                }
            }
        }
        
    }
}
