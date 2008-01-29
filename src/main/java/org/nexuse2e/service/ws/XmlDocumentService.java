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
     * @param choreography The choreography.
     * @param action The action.
     * @param partner The partner.
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
            @WebParam(name = "xmlPayload", targetNamespace = "")
            String xmlPayload );
}
