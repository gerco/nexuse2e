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
package org.nexuse2e.patch;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;


/**
 * A <code>PatchBundle</code> is a .jar or .zip file that contains one
 * or more patches.
 *
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public class PatchBundle {
    
    private Map<String, Object> entries;
    private List<Patch> patches;
    private ClassLoaderImpl loader;
    
    /**
     * Constructs a new <code>PatchBundle</code> from the given JAR or ZIP data.
     * @param jarData The binary .jar representation as an InputStream.
     * @throws IOException If the jar data is invalid.
     */
    public PatchBundle( InputStream jarData, ClassLoader parentClassLoader ) throws IOException {
        JarInputStream jarIs = new JarInputStream( jarData );
        loader = new ClassLoaderImpl( parentClassLoader );
        entries = new HashMap<String, Object>();
        patches = new ArrayList<Patch>();
        for (ZipEntry entry = jarIs.getNextEntry(); entry != null; entry = jarIs.getNextEntry()) {
            if (!entry.isDirectory()) {
                String name = entry.getName();
                boolean eof = false;
                int size = 0;
                List<byte[]> data = new ArrayList<byte[]>();
                while (!eof) {
                    int offset = 0;
                    byte[] b = new byte[1024];
                    while (offset < b.length && !eof) {
                        if (jarIs.available() > 0) {
                            int read = jarIs.read( b, offset, b.length - offset );
                            if (read >= 0) {
                                offset += read;
                            } else {
                                eof = true;
                            }
                        } else {
                            eof = true;
                        }
                    }
                    size += offset;
                    data.add( b );
                }
                byte[] b = new byte[size];
                int offset = 0;
                for (byte[] part : data) {
                    System.arraycopy( part, 0, b, offset, Math.min( size - offset, part.length ) );
                    offset += part.length;
                }
                
                boolean c = name.endsWith( ".class" );
                name = name.replace( "/", "." ).substring( 0, name.length() - 6 );
                if (c) {
                    Class<?> clazz = loader.define( name, b );
                    System.out.println( "ClassLoader: " + clazz.getClassLoader() );
                    if (Patch.class.isAssignableFrom( clazz )) {
                        try {
                            patches.add( (Patch) clazz.newInstance() );
                        } catch (InstantiationException e) {
                            e.printStackTrace();
                            throw new IOException( e.getMessage() );
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                            throw new IOException( e.getMessage() );
                        }
                    }
                    entries.put( name, clazz );
                } else {
                    entries.put( name, b );
                }
            }
            jarIs.closeEntry();
        }
    }
    
    public ClassLoader getPatchClassLoader( ClassLoader parent ) {
        return loader;
    }
    
    private class ClassLoaderImpl extends ClassLoader {
        
        ClassLoaderImpl( ClassLoader parent ) {
            super( parent );
        }
        
        Class<?> define( String name, byte[] b ) {
            return super.defineClass( name, b, 0, b.length );
        }

        @Override
        protected Class<?> loadClass( String name, boolean resolve )
                throws ClassNotFoundException {
            // first, we try to find the class in the jar file
            Object o = entries.get( name );
            if (o != null && o instanceof Class<?>) {
                return (Class<?>) o;
            }
            
            // if not found, use default lookup
            return super.loadClass( name, resolve );
        }
    }
    
    /**
     * Gets a list of patches that are contained in this <code>PatchBundle</code>.
     * @return A list of patches.
     */
    public List<Patch> getPatches() {
        return patches;
    }
}
