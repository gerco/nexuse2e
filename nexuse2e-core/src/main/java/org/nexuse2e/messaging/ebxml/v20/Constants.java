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
package org.nexuse2e.messaging.ebxml.v20;

/**
 * @author mbreilmann
 *
 */
public class Constants extends org.nexuse2e.messaging.ebxml.Constants {

    public static final String EBXML_NAMESPACE                     = "http://www.oasis-open.org/committees/ebxml-msg/schema/msg-header-2_0.xsd";
    public static final String EBXML_NAMESPACE_PREFIX              = "eb";
    public static final String EBXMLVERSION                        = "2.0";
    public static final String VERSION                             = "version";
    public static final String MUSTUNDERSTAND                      = "mustUnderstand";
    public static final String MUSTUNDERSTAND_VALUE                = "1";

    public static final String SOAPACTOR                           = "urn:oasis:names:tc:ebxml-msg:actor:toPartyMSH";
    public static final String ACTOR                               = "SOAP-ENV:actor";
    public static final String PARTY_ID                            = "PartyId";
    public static final String TIMESTAMP_ID                        = "Timestamp";
    public static final String REFTOMESSAGE_ID                     = "RefToMessageId";
    public static final String URI_ID                              = "uri:";
    public static final String CID_ID                              = "cid:";
    public static final String LOCATION_ID                         = "Location";

    public static final String ACKREQUESTED_SIGNED                 = "true";
    public static final String ACKREQUESTED_UNSIGNED               = "false";

    public static final String PARAMETER_PREFIX_EBXML20            = "ebxml20_";
    public static final String PROTOCOLSPECIFIC_FROM               = "from";
    public static final String PROTOCOLSPECIFIC_TO                 = "to";
    public static final String PROTOCOLSPECIFIC_FROMIDTYPE         = "fromIDType";
    public static final String PROTOCOLSPECIFIC_TOIDTYPE           = "toIDType";
    public static final String PROTOCOLSPECIFIC_SERVICE            = "service";
    public static final String PROTOCOLSPECIFIC_SERVICE_TYPE	   = "serviceType";	
    public static final String PROTOCOLSPECIFIC_ROLE_FROM		   = "role_from";
    public static final String PROTOCOLSPECIFIC_ROLE_TO            = "role_to";
    public static final String TRIM_CONTENT_ID					   = "trimContId";
    
    
    public static final String CPAID_SCHEME_PARAM_NAME   = "cpaIdScheme";
    public static final CPAIdScheme DEFAULT_CPAID_SCHEME = CPAIdScheme.CHOREOGRAPHYID;
}
