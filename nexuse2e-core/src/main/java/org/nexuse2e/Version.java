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
package org.nexuse2e;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.jar.Manifest;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

/**
 * This class can read the following main attributes from the META-INF/MANIFEST.MF file
 * to which this class belongs:
 * <ul>
 * 	<li>Application-Name</li>
 *  <li>Application-Artifact-Id</li>
 *  <li>Application-Group-Id</li>
 *  <li>Implementation-Svn-Revision</li>
 *  <li>Implementation-Version</li>
 *  <li>Implementation-Build-Date</li>
 * </ul> 
 * @author Sebastian Schulze
 * @date 26.02.2009
 */

public class Version {
    
    private static Manifest cachedManifest;
    
    public enum MainAttribute {
    	ApplicationName( "Application-Name" ),
    	ApplicationArtifactId( "Application-Artifact-Id" ),
    	ApplicationGroupId( "Application-Group-Id" ),
    	ImplementationGitRevision("Implementation-Git-Revision"),
		ImplementationVersion("Implementation-Version"),
		ImplementationBuildDate("Implementation-Build-Date"),
		HudsonBuildNumber("Hudson-Build-Number"),
		HudsonProject("Hudson-Project"),
		HudsonVersion("Hudson-Version");

		private String name;

		MainAttribute(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

	}

	/**
	 * Returns the official version string for this product.
	 * @return The official version string for this product,
	 *  	   or an empty string, if no version information
	 *         is present.
	 */
	public static String getVersion() {
		String result = "No version information found";
		try {
			String version = getMainAttribute(MainAttribute.ImplementationVersion);
			String revision = getMainAttribute(MainAttribute.ImplementationGitRevision);
			String buildNo = getMainAttribute(MainAttribute.HudsonBuildNumber);
			String buildDate = getMainAttribute(MainAttribute.ImplementationBuildDate);
			result = (version != null && version.length() > 0 ? version : "unspecified version") + (revision != null && revision.length() > 0 ?
				", revision: " + revision :
				"") + ", build: " + (buildNo != null && buildNo.length() > 0 ? buildNo : "unofficial") + (buildDate != null && buildDate.length() > 0 ?
				" " + buildDate :
				"");
		} catch (IOException e) {
		}

		return result;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		System.out.println("NexusE2E Version: " + getVersion());
	}

	/**
	 * Loads the manifest file of the JAR to which the given class belongs to. 
	 * @param clazz The class of the JAR.
	 * @return An object representation of the manifest file.
	 * @throws IOException, if an error occurs while reading the file.
	 */
	private static Manifest read( Class<?> clazz ) throws IOException {
		String path = clazz.getResource( toResourceName( clazz ) )
				.toString();
		String manifestPath = extractRoot( path, clazz )
				+ "/META-INF/MANIFEST.MF";
		InputStream stream = new URL( manifestPath ).openStream();
		try {
			return new Manifest( stream );
		} finally {
			IOUtils.closeQuietly( stream );
		}
	}

	/*
	 * Helper method for read()
	 */
	private static String extractRoot( String path, Class<?> mainClass ) {
		if ( path.contains( "!" ) ) {
			return path.substring( 0, path.lastIndexOf( "!" ) + 1 );
		} else {
			return StringUtils.substringBefore( path,
												mainClass.getName().replace( '.', '/' ) );
		}
	}

	/*
	 * Helper method for read()
	 */
	private static String toResourceName(Class<?> mainClass) {
		return "/" + mainClass.getName().replace('.', '/') + ".class";
	}
	
	/**
	 * Reads the value of a main attribute of a (cached) version of
	 * the MANIFEST.MF in the JAR file this class belongs to. 
	 * @param att The name of the main attribute
	 * @return The value of the main attribute or <code>null</code>, if no such value exists.
	 * @throws IOException, if an error occurs while reading the MANIFEST.MF file.
	 */
	public static String getMainAttribute( MainAttribute att ) throws IOException {
		if ( cachedManifest == null ) {
			// attempt to read manifest
			cachedManifest = read( Version.class );
		}
		
		String result = null;
		if ( cachedManifest != null ) {
			result = cachedManifest.getMainAttributes().getValue( att.getName() );
		}
		return result;
	}

} // Version