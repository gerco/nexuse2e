/**
 * NEXUSe2e Business Messaging Open Source
 * Copyright 2000-2009, Tamgroup and X-ioma GmbH
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
package org.nexuse2e.configuration;

import org.nexuse2e.pojo.CertificatePojo;
import org.nexuse2e.pojo.ComponentPojo;
import org.nexuse2e.pojo.ConversationPojo;
import org.nexuse2e.pojo.PartnerPojo;
import org.nexuse2e.pojo.PipeletPojo;
import org.nexuse2e.pojo.PipelinePojo;
import org.nexuse2e.pojo.RolePojo;
import org.nexuse2e.pojo.UserPojo;

/**
 * @author gesch
 * 
 */
public class Constants extends org.nexuse2e.Constants {

    public static int          CERTIFICATE_FORMAT_PEM              = 1;
    public static int          CERTIFICATE_FORMAT_DER              = 2;

    public static int          PARTNER_TYPE_ALL                    = 0;
    public static int          PARTNER_TYPE_LOCAL                  = 1;
    public static int          PARTNER_TYPE_PARTNER                = 2;

    // Security settings

    public static final int    DEFAULT_RSA_KEY_LENGTH              = 1024;
    public static final String DEFAULT_DIGITAL_SIGNATURE_ALGORITHM = "SHA1withRSA";
    public static final String DEFAULT_KEY_ALGORITHM               = "RSA";
    public static final String DEFAULT_CERT_TYPE                   = "X.509";
    public static final String DEFAULT_KEY_STORE                   = "PKCS12";
    public static final String DEFAULT_JCE_PROVIDER                = "BC";

    public static GenericComparator<PipelinePojo>     PIPELINECOMPARATOR        = new GenericComparator<PipelinePojo>("nxPipelineId", true);
    public static GenericComparator<PartnerPojo>      PARTNERCOMPARATOR         = new GenericComparator<PartnerPojo>("partnerId", true);
    public static GenericComparator<ComponentPojo>    COMPONENTCOMPARATOR       = new GenericComparator<ComponentPojo>("type;name", true);
    public static GenericComparator<ComponentPojo>    COMPONENT_NAME_COMPARATOR = new GenericComparator<ComponentPojo>("name", true);
    public static GenericComparator<PipeletPojo>      PIPELETCOMPARATOR         = new GenericComparator<PipeletPojo>("position", true);
    public static GenericComparator<CertificatePojo>  CERTIFICATECOMPARATOR     = new GenericComparator<CertificatePojo>("name", true);
    public static GenericComparator<ConversationPojo> CONVERTSATIONCOMPARATOR   = new GenericComparator<ConversationPojo>("createdDate", false);
    public static GenericComparator<UserPojo>         COMPARATOR_USER_BY_NAME   = new GenericComparator<UserPojo>("lastName;firstName;middleName", true);
    public static GenericComparator<RolePojo>         COMPARATOR_ROLE_BY_NAME   = new GenericComparator<RolePojo>("name", true);

}
