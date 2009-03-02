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
package org.nexuse2e.service.ws;

import javax.jws.Oneway;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

/**
 * Interface for XML document receiver web service.
 *
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
@WebService(name="XmlDocumentService", targetNamespace = "http://integration.nexuse2e.org")
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface XmlDocumentService {

    /**
     * Process an XML document for the given choreography, action and partner.
     * @param choreography The choreography. Must not be <code>null</code>.
     * @param action The action. Must not be <code>null</code>.
     * @param partner The partner. Must not be <code>null</code>.
     * @param conversationId The conversation ID. Must not be <code>null</code>.
     * @param messageId The message ID. Must not be <code>null</code>.
     * @param xmlPayload The payload (XML document).
     */
    @WebMethod(operationName = "processXmlDocument", action = "http://integration.nexuse2e.org/XmlDocumentService/processXmlDocument")
    @Oneway
    public abstract void processXmlDocument(
            @WebParam(name = "choreography", targetNamespace = "")
            String choreography,
            @WebParam(name = "action", targetNamespace = "")
            String action,
            @WebParam(name = "partner", targetNamespace = "")
            String partner,
            @WebParam(name = "conversationId", targetNamespace = "")
            String conversationId,
            @WebParam(name = "messageId", targetNamespace = "")
            String messageId,
            @WebParam(name = "xmlPayload", targetNamespace = "")
            String xmlPayload );
}
