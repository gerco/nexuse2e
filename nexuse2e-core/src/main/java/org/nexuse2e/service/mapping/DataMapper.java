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
package org.nexuse2e.service.mapping;

import java.util.List;

/**
 * Basic interface required for all DataMapping services.
 * 
 * A DataMapping service accesses any external (or in some cases, internal) data source to translate data tokens from one type into another, 
 * e.g. global product ID numbers into local product ID numbers for a target business application.
 * 
 * Note that service implementations are supposed to extend from AbstractService, and then implement additional support interfaces like this one.
 * 
 * @author JJerke
 */
public interface DataMapper {

	/**
	 * Takes a given input token and a descriptor of it's source format, and transparently translates it into the corresponding
	 * token for the target format.
	 * 
	 * @param input The input token, as a String.
	 * @param source The type of the input token.
	 * @param target The desired target type for the token.
	 * @return A String, the input token translated into the target type.
	 */
	public String processConversion(String input, String source, String target);
	
	/**
	 * Fetches a list of supported types from the backend.
	 * 
	 * @return A list of Strings
	 */
	public List<String> getPossibleTypes();
	
}
